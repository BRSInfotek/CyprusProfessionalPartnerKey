package org.eevolution.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cyprusbrs.framework.MJobOrder;
import org.cyprusbrs.framework.MJobOrderLine;
import org.cyprusbrs.framework.MMFGRequisition;
import org.cyprusbrs.framework.MTax;
import org.cyprusbrs.framework.MUOMConversion;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

public class GenerateJobWorkOrderProcess extends SvrProcess { 
    private int requisitionID = 0;

    private List<String> jobWorkOrderNo=new ArrayList<String>();
    // Map to track existing Job Orders for same BPartner & Location
    private Map<String, Integer> jobOrderMap = new HashMap<>();

    @Override
    protected void prepare() {
        requisitionID = getRecord_ID();
    }

    @Override
    protected String doIt() throws Exception {
        if (requisitionID <= 0) {
            return "Invalid Requisition ID";
        }
        
        MMFGRequisition outsourcingRequisition=new MMFGRequisition(getCtx(), requisitionID, get_TrxName());
        if (!"CO".equals(outsourcingRequisition.getDocStatus())) {
        	return "Document must be in Complete status to generate Job Work Order.";
        }
        
        // ✅ Check if Job Work Order already exists for this requisition
        if (isJobOrderAlreadyCreated(requisitionID)) {
            return "Error: Job Work Order is already created for this Requisition.";
        }

        // Fetch outsourcing requisition details
        String requisitionQuery = "SELECT AD_User_ID, M_Warehouse_ID, PriorityRule, JobOrderType, DateDoc, DateRequired,M_PriceList_ID FROM M_MFGRequisition WHERE M_MFGRequisition_ID=?";
        PreparedStatement pstmtReq = DB.prepareStatement(requisitionQuery, get_TrxName());
        pstmtReq.setInt(1, requisitionID);
        ResultSet rsReq = pstmtReq.executeQuery();

        int adUserId = 0;
        int warehouseId = 0;
        String priorityRule = "";
        String jobOrderType = "";
        Timestamp dateDoc = null;
        Timestamp dateRequired = null;
        int M_PriceList_ID=0;

        if (rsReq.next()) {
            adUserId = rsReq.getInt("AD_User_ID");
            warehouseId = rsReq.getInt("M_Warehouse_ID");
            priorityRule = rsReq.getString("PriorityRule");
            jobOrderType = rsReq.getString("JobOrderType");
            dateDoc = rsReq.getTimestamp("DateDoc");
            dateRequired = rsReq.getTimestamp("DateRequired");
            M_PriceList_ID=rsReq.getInt("M_PriceList_ID");
        }
        DB.close(rsReq, pstmtReq);

        
     // For Default Tax /// It should be on Save of Opportunity line window ... Required to implement it 
 		MTax tax = new Query(getCtx(), MTax.Table_Name, "IsDefault=?", null)
 			.setClient_ID()	
 			.setOnlyActiveRecords(true)
 			.setParameters("Y")
 			.firstOnly();

        
        // Fetch requisition lines and create job orders
        String requisitionLineQuery = "SELECT * FROM M_MFGRequisitionLine WHERE M_MFGRequisition_ID=?";
        PreparedStatement pstmtLine = DB.prepareStatement(requisitionLineQuery, get_TrxName());
        pstmtLine.setInt(1, requisitionID);
        ResultSet rsLine = pstmtLine.executeQuery();

        while (rsLine.next()) {
            int bpartnerID = rsLine.getInt("C_BPartner_ID");
            int locationID = rsLine.getInt("C_BPartner_Location_ID");
            String key = bpartnerID + "_" + locationID; // Unique key for BPartner & Location
            int jobOrderID;
            if (jobOrderMap.containsKey(key)) {
                // ✅ Use existing Job Order if found in the map
                jobOrderID = jobOrderMap.get(key);
            } else {
                // ✅ Create new Job Order if not found
                jobOrderID = createJobWorkOrder(rsLine, adUserId, warehouseId, priorityRule, dateDoc, jobOrderType, dateRequired, M_PriceList_ID);
                jobOrderMap.put(key, jobOrderID); // Store in map for future lines
            }

            // ✅ Create Job Order Line under the same Job Order
            createJobWorkOrderLine(jobOrderID, rsLine, jobOrderType,tax.getC_Tax_ID());
        }

        DB.close(rsLine, pstmtLine);
        return "Job Work Orders "+jobWorkOrderNo +" Created Successfully!";
    }

    private int createJobWorkOrder(ResultSet rs, int adUserId, int warehouseId, String priorityRule, Timestamp dateDoc, String jobOrderType, Timestamp dateRequired, int M_PriceList_ID) throws Exception {
        MJobOrder jobOrder = new MJobOrder(getCtx(), 0, get_TrxName());

        jobOrder.setC_DocType_ID(getJobWorkOrderDocType()); // DocumentNo is auto-set
        jobOrder.setC_BPartner_ID(rs.getInt("C_BPartner_ID"));
        jobOrder.setC_BPartner_Location_ID(rs.getInt("C_BPartner_Location_ID"));
        jobOrder.set_ValueNoCheck("M_MFGRequisitionLine_ID", rs.getInt("M_MFGRequisitionLine_ID"));
        jobOrder.set_ValueNoCheck("M_MFGRequisition_ID", requisitionID);
        jobOrder.setJobOrderType(jobOrderType);
        jobOrder.setAD_User_ID(adUserId);
        jobOrder.set_ValueNoCheck("M_PriceList_ID", M_PriceList_ID);
        jobOrder.setM_Warehouse_ID(warehouseId);
        jobOrder.setPriority(priorityRule);

        if (dateDoc == null)
            dateDoc = new Timestamp(System.currentTimeMillis());
        if (dateRequired == null)
        	dateRequired = new Timestamp(System.currentTimeMillis());
        
        jobOrder.setDateOrdered(dateDoc);
        jobOrder.setDateRequired(dateRequired);

        jobOrder.setDocStatus("DR");
        jobOrder.setDocAction("CO");

        jobOrder.saveEx();
        
        if(jobOrder!=null)
        jobWorkOrderNo.add(jobOrder.getDocumentNo());
        return jobOrder.get_ID();
    }

    private void createJobWorkOrderLine(int jobOrderID, ResultSet rs, String jobOrderType, int C_Tax_ID) throws Exception {
        MJobOrderLine jobOrderLine = new MJobOrderLine(getCtx(), 0, get_TrxName());
        
        Integer M_Product_ID=rs.getInt("M_Product_ID");
        Integer C_UOM_ID=rs.getInt("C_UOM_ID");
        jobOrderLine.setC_JobOrder_ID(jobOrderID);
        jobOrderLine.setLineNo(rs.getInt("Line"));
        jobOrderLine.setM_Product_ID(M_Product_ID);
        int chargeID = rs.getInt("C_Charge_ID"); // Fetching C_Charge_ID from JobOrderLine
        
        // Set BOM & Formula from Requisition Line
        if (MMFGRequisition.JOBORDERTYPE_ComponentIssuedByOrganization.equalsIgnoreCase(jobOrderType))
            jobOrderLine.setPP_Product_BOM_ID(rs.getInt("PP_Product_BOM_ID"));

        jobOrderLine.setC_UOM_ID(C_UOM_ID);
        jobOrderLine.setDescription(rs.getString("Description"));
        
        BigDecimal qty=rs.getBigDecimal("Qty");     
        jobOrderLine.setQtyEntered(qty);
        
        if (M_Product_ID> 0 && qty != null && C_UOM_ID>0)
        {	
        	BigDecimal convertedQty = MUOMConversion.convertProductFrom(getCtx(), M_Product_ID, C_UOM_ID, qty);
        	jobOrderLine.setQtyConverted(convertedQty);
        }
        else
        {
        	BigDecimal priceEnt=rs.getBigDecimal("PriceEntered");
        	jobOrderLine.setQtyConverted(qty);
        	jobOrderLine.setPriceEntered(priceEnt);
        	jobOrderLine.setTotalLineNetAmt(priceEnt);
        	jobOrderLine.setLineNetAmt(priceEnt);
            jobOrderLine.setC_Tax_ID(C_Tax_ID);
        }
            
     // **Set C_Charge_ID in M_MFGRequisitionLine if available**
        if (chargeID > 0) {
        	jobOrderLine.setC_Charge_ID(chargeID);
        } 
        jobOrderLine.saveEx();
    }

    private int getJobWorkOrderDocType() {
        return DB.getSQLValue(get_TrxName(), "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='JWO'");
    }

    // ✅ Function to check if Job Work Order already exists for this requisition
    private boolean isJobOrderAlreadyCreated(int requisitionID) {
        String sql = "SELECT COUNT(*) FROM C_JobOrder WHERE M_MFGRequisition_ID = ?";
        int count = DB.getSQLValue(get_TrxName(), sql, requisitionID);
        return count > 0; // If count > 0, a Job Order already exists
    }
}
