package org.eevolution.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.cyprus.exceptions.AdempiereException;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class InOutCreateJobWork extends SvrProcess {
    private String documentNo = null;
    private Integer p_M_Inout_ID = 0;

    @Override
    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();
        for (ProcessInfoParameter param : para) {
            String name = param.getParameterName();
            if (param.getParameter() == null)
                continue;
            else if (name.equals("DocumentNo"))
                documentNo = (String) param.getParameter();
            else
                log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_M_Inout_ID = getRecord_ID();
    }

    @Override
    protected String doIt() throws Exception {
        if (p_M_Inout_ID <= 0) {
            throw new AdempiereException("Invalid Job Work Order");
        }

        MInOut jobWorkIssue = new MInOut(getCtx(), p_M_Inout_ID, get_TrxName());
        if (jobWorkIssue == null || jobWorkIssue.get_ID() <= 0) {
            throw new AdempiereException("No Job Work (+) document found for ID: " + p_M_Inout_ID);
        }
        
        // Check if any charge exists
        if (!hasCharges(p_M_Inout_ID)) {
            throw new AdempiereException("No Charges found in Job Work Receipt lines. Invoice will not be created.");
        }

        // Check if invoice already created
        if (jobWorkIssue.getC_Invoice_ID() > 0) {
            MInvoice existingInvoice = new MInvoice(getCtx(), jobWorkIssue.getC_Invoice_ID(), get_TrxName());
            throw new AdempiereException("Invoice already created: " + existingInvoice.getDocumentNo());
        }

        // Get linked Job Order ID
        int jobOrderId = jobWorkIssue.get_ValueAsInt("C_JobOrder_ID");
        if (jobOrderId <= 0) {
            throw new AdempiereException("No Job Order linked with this InOut document.");
        }

        // Get Price List from Job Order
        int priceListId = DB.getSQLValue(get_TrxName(),
                "SELECT M_PriceList_ID FROM C_JobOrder WHERE C_JobOrder_ID = ?", jobOrderId);
        if (priceListId <= 0) {
            throw new AdempiereException("No valid Price List found in Job Order.");
        }

        int salesRepId = Env.getAD_User_ID(getCtx());
        if (salesRepId <= 0) {
            throw new AdempiereException("No valid Sales Representative found in context.");
        }

        int vendorInvoiceDocType = getVendorInvoiceDocType();
        if (vendorInvoiceDocType <= 0) {
            throw new AdempiereException("No valid Vendor Invoice Document Type found.");
        }

        // Create Invoice Header
        MInvoice vendorInvoice = new MInvoice(getCtx(), 0, get_TrxName());
        vendorInvoice.setC_DocTypeTarget_ID(vendorInvoiceDocType);
        vendorInvoice.setC_BPartner_ID(jobWorkIssue.getC_BPartner_ID());
        vendorInvoice.setC_BPartner_Location_ID(jobWorkIssue.getC_BPartner_Location_ID());
        vendorInvoice.setDateInvoiced(jobWorkIssue.getMovementDate());
        vendorInvoice.setIsSOTrx(false);
        vendorInvoice.setSalesRep_ID(salesRepId);
        vendorInvoice.setM_PriceList_ID(priceListId);
        vendorInvoice.set_ValueNoCheck("C_JobOrder_ID", jobOrderId);
        vendorInvoice.set_ValueNoCheck("M_Inout_ID", p_M_Inout_ID);
        if (documentNo != null && !documentNo.isEmpty()) {
        	vendorInvoice.set_ValueNoCheck("VendorInvoiceNo", documentNo);
//            vendorInvoice.setDocumentNo(documentNo);
//            vendorInvoice.setPOReference(documentNo);
        }

        vendorInvoice.setDocStatus("DR");
        vendorInvoice.setDocAction("CO");
        vendorInvoice.saveEx();

        int vendorInvoiceID = vendorInvoice.get_ID();

        createInvoiceLines(vendorInvoiceID, jobWorkIssue.get_ID());

        // Link invoice to M_InOut
        String updateSQL = "UPDATE M_InOut SET C_Invoice_ID=? WHERE M_InOut_ID=?";
        DB.executeUpdateEx(updateSQL, new Object[]{vendorInvoiceID, p_M_Inout_ID}, get_TrxName());

        return "Vendor Invoice Created: " + vendorInvoice.getDocumentNo();
    }

    private void createInvoiceLines(int vendorInvoiceID, int jobWorkIssueID) {
        String sql = "SELECT M_InOutLine_ID, M_Product_ID, C_UOM_ID, Line, MovementQty, C_Charge_ID, C_JobOrderLine_ID " +
                     "FROM M_InOutLine WHERE M_InOut_ID=?";
        try (PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName())) {
            pstmt.setInt(1, jobWorkIssueID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int chargeID = rs.getInt("C_Charge_ID");
                if (chargeID <= 0) {
                    continue; // Skip lines without charges
                }

                int uomId = rs.getInt("C_UOM_ID");
                int lineNo = rs.getInt("Line");
                BigDecimal qty = rs.getBigDecimal("MovementQty");
                int jobOrderLineId = rs.getInt("C_JobOrderLine_ID");
                int mInOutLine_ID=rs.getInt("M_InOutLine_ID");	
                BigDecimal chargeAmt = getChargeAmountFromJobOrderLine(jobOrderLineId);
                if (chargeAmt == null || chargeAmt.signum() == 0) {
                    throw new AdempiereException("No Charge Amount found for Job Order Line ID: " + jobOrderLineId);
                }

                MInvoiceLine invoiceLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
                invoiceLine.setC_Invoice_ID(vendorInvoiceID);
                invoiceLine.setC_UOM_ID(uomId);
                invoiceLine.setM_InOutLine_ID(mInOutLine_ID);
                invoiceLine.setLine(lineNo);
                invoiceLine.setQty(qty);
                invoiceLine.setC_Charge_ID(chargeID);
                invoiceLine.setPriceActual(chargeAmt);
                invoiceLine.setPriceEntered(chargeAmt);
                invoiceLine.set_ValueOfColumn("C_JobOrderLine_ID", jobOrderLineId);

                invoiceLine.saveEx();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error creating Vendor Invoice Lines", e);
            throw new AdempiereException("Error while creating invoice lines: " + e.getMessage());
        }
    }

    private BigDecimal getChargeAmountFromJobOrderLine(int jobOrderLineId) {
        String sql = "SELECT PriceEntered FROM C_JobOrderLine WHERE C_JobOrderLine_ID=?";
        return DB.getSQLValueBD(get_TrxName(), sql, jobOrderLineId);
    }

    private int getVendorInvoiceDocType() {
        String sql = "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='API' AND AD_Client_ID=?";
        return DB.getSQLValue(get_TrxName(), sql, getAD_Client_ID());
    }
    private boolean hasCharges(int inOutId) {
        String sql = "SELECT COUNT(*) FROM M_InOutLine WHERE M_InOut_ID = ? AND C_Charge_ID IS NOT NULL AND C_Charge_ID > 0";
        int count = DB.getSQLValue(get_TrxName(), sql, inOutId);
        return count > 0;
    }

}
