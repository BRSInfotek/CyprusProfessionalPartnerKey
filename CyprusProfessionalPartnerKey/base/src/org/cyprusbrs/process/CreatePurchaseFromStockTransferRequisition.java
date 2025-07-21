package org.cyprusbrs.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprusbrs.framework.MRequisition;
import org.cyprusbrs.framework.MRequisitionLine;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

/**
 * Process: CreatePurchaseFromStockTransferRequisition
 * This process consolidates Stock Transfer Requisitions (STR) into a single Purchase Requisition (POR)
 */
public class CreatePurchaseFromStockTransferRequisition extends SvrProcess {
	
    // Process Parameters
    private int p_AD_Org_ID = 0;
    private int p_M_Warehouse_ID = 0;
    private int p_M_WarehouseTo_ID = 0;
    private int p_M_Requisition_ID = 0;
    private String p_PriorityRule = null;
    private Timestamp p_DateOrderedFrom = null;
    private Timestamp p_DateOrderedTo = null;

    @Override
    protected void prepare() {
        ProcessInfoParameter[] params = getParameter();
        for (ProcessInfoParameter param : params) {
            String name = param.getParameterName();

            switch (name) {
                case "AD_Org_ID":
                    p_AD_Org_ID = param.getParameterAsInt(); // No default from Env
                    break;
                case "M_Warehouse_ID":
                    p_M_Warehouse_ID = param.getParameterAsInt(); // No default from Env
                    break;
                case "M_WarehouseTo_ID":
                    p_M_WarehouseTo_ID = param.getParameterAsInt();
                    break;
                case "M_Requisition_ID":
                    p_M_Requisition_ID = param.getParameterAsInt();
                    break;
                case "PriorityRule":
                    p_PriorityRule = param.getParameterAsString();
                    break;
                case "DateOrderedFrom":
                    p_DateOrderedFrom = param.getParameterAsTimestamp();
                    break;
                case "DateOrderedTo":
                    p_DateOrderedTo = param.getParameterAsTimestamp();
                    break;
                default:
                    log.log(Level.WARNING, "Unknown Parameter: " + name);
                    break;
            }
        }
    }

    @Override
    protected String doIt() throws Exception {
        log.info("Starting CreatePurchaseFromStockTransferRequisition");
        MRequisition purchaseRequisition=null;
     // Build SQL Query
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM M_RequisitionLine WHERE M_Requisition_ID IN (" +
            "SELECT M_Requisition_ID FROM M_Requisition WHERE C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='STR')");

        if (p_AD_Org_ID > 0) sql.append(" AND AD_Org_ID = ?");
        if (p_M_Warehouse_ID > 0) sql.append(" AND M_Warehouse_ID = ?");
        if (p_M_WarehouseTo_ID > 0) sql.append(" AND M_WarehouseTo_ID = ?");
        if (p_M_Requisition_ID > 0) sql.append(" AND M_Requisition_ID = ?");
        if (p_PriorityRule != null && !p_PriorityRule.isEmpty()) sql.append(" AND PriorityRule = ?");
        if (p_DateOrderedFrom != null) sql.append(" AND DateOrdered >= ?");
        if (p_DateOrderedTo != null) sql.append(" AND DateOrdered <= ?");

        sql.append(" AND DocStatus IN ('CO', 'DR', 'IP'))"); // Only completed and draft requisitions
        log.info("sql :"+sql);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int lineCount = 0;

        try {
            pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
            int index = 1;

            if (p_AD_Org_ID > 0) pstmt.setInt(index++, p_AD_Org_ID);
            if (p_M_Warehouse_ID > 0) pstmt.setInt(index++, p_M_Warehouse_ID);
            if (p_M_WarehouseTo_ID > 0) pstmt.setInt(index++, p_M_WarehouseTo_ID);
            if (p_M_Requisition_ID > 0) pstmt.setInt(index++, p_M_Requisition_ID);
            if (p_PriorityRule != null && !p_PriorityRule.isEmpty()) pstmt.setString(index++, p_PriorityRule);
            if (p_DateOrderedFrom != null) pstmt.setTimestamp(index++, p_DateOrderedFrom);
            if (p_DateOrderedTo != null) pstmt.setTimestamp(index++, p_DateOrderedTo);

            rs = pstmt.executeQuery();

            // Create Purchase Requisition
            purchaseRequisition = new MRequisition(getCtx(), 0, get_TrxName());
            
            
         // Default values
            int priceListID = 0;
            Timestamp dateDoc = new Timestamp(System.currentTimeMillis());
            Timestamp dateRequired = new Timestamp(System.currentTimeMillis());

            // If p_M_Requisition_ID is provided, fetch values from the requisition
            if (p_M_Requisition_ID > 0) {
                MRequisition sourceRequisition = new MRequisition(getCtx(), p_M_Requisition_ID, get_TrxName());
                if (sourceRequisition != null && sourceRequisition.get_ID() > 0) {
                    priceListID = sourceRequisition.getM_PriceList_ID();
                    dateDoc = sourceRequisition.getDateDoc();
                    dateRequired = sourceRequisition.getDateRequired();
                    purchaseRequisition.setPriorityRule(sourceRequisition.getPriorityRule()); // Preserve Priority
                    purchaseRequisition.setAD_User_ID(sourceRequisition.getAD_User_ID());
                }
                else
                {
                    purchaseRequisition.setPriorityRule("5");
                 // Set User/Contact
                    purchaseRequisition.setAD_User_ID(Env.getAD_User_ID(getCtx()));

                }
            }
            
            purchaseRequisition.setAD_Org_ID(Env.getAD_Org_ID(getCtx()));
            purchaseRequisition.setM_Warehouse_ID(Env.getContextAsInt(getCtx(), "#M_Warehouse_ID"));
            int docTypeTarget_ID = DB.getSQLValueEx(
                get_TrxName(),
                "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType = 'POR' AND AD_Client_ID = ?",
                Env.getAD_Client_ID(getCtx())
            );

            if (docTypeTarget_ID <= 0) {
                throw new IllegalStateException("No Document Type found for DocBaseType = POR");
            }

            purchaseRequisition.setC_DocType_ID(docTypeTarget_ID);
            purchaseRequisition.setDateDoc(dateDoc);
            purchaseRequisition.setDateRequired(dateRequired);
            purchaseRequisition.setDocStatus(DocAction.STATUS_Drafted);
           
            // If no valid M_PriceList_ID is found, fallback to the default price list
            if (priceListID <= 0) {
                priceListID = DB.getSQLValueEx(get_TrxName(),
                    "SELECT M_PriceList_ID FROM M_PriceList WHERE IsDefault='Y' AND AD_Client_ID=?",
                    Env.getAD_Client_ID(getCtx()));
            }

            if (priceListID <= 0) {
                throw new IllegalStateException("No valid Price List found for the requisition.");
            }
            purchaseRequisition.setM_PriceList_ID(priceListID);
            purchaseRequisition.saveEx();

            // Create Purchase Requisition Lines
            while (rs.next()) {
                MRequisitionLine stockTransferLine = new MRequisitionLine(getCtx(), rs, get_TrxName());

                MRequisitionLine purchaseLine = new MRequisitionLine(purchaseRequisition);
                purchaseLine.setM_Product_ID(stockTransferLine.getM_Product_ID());
                purchaseLine.setQty(stockTransferLine.getQty());
                purchaseLine.setDescription(stockTransferLine.getDescription());
                purchaseLine.setC_Charge_ID(stockTransferLine.getC_Charge_ID());
                purchaseLine.setPriceActual(stockTransferLine.getPriceActual());
                purchaseLine.saveEx();

                lineCount++;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error creating purchase requisition", e);
            throw e;
        } finally {
            DB.close(rs, pstmt);
        }
        return "Purchase Requisition : "+purchaseRequisition.getDocumentNo()+" created with " + lineCount + " lines.";
    }
}
