package org.eevolution.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MJobOrder;
import org.cyprusbrs.framework.MMFGRequisition;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

public class ReceiveFinishedGoodsProcess extends SvrProcess {
	private int jobOrderID;

@Override
protected void prepare() {
    jobOrderID = getRecord_ID();
}

@Override
protected String doIt() throws Exception {
    if (jobOrderID <= 0) {
        throw new CyprusException("Invalid Job Work Order ID");
    }

    // Fetch Job Work Order details
    MJobOrder jobOrder = new MJobOrder(getCtx(), jobOrderID, get_TrxName());
    if (jobOrder == null || jobOrder.get_ID() <= 0) {
        throw new CyprusException("Job Work Order not found");
    }
    
    // **Check if Job Work Order complete status**
    if (!"CO".equals(jobOrder.getDocStatus())) {
    	return "Document must be in Complete status to generate Job Work(+).";
    }
    
 // **Check if Job Work (+) Receipt already exists**
    if (isJobWorkReceiptExists(jobOrderID)) {
        return "Job Work (+) Receipt already created for this Job Work Order";
    }
    
    if(MMFGRequisition.JOBORDERTYPE_ComponentIssuedByOrganization.equalsIgnoreCase(jobOrder.getJobOrderType()))
    {
    	// check issue component 
    	if(!jobOrder.get_ValueAsBoolean("IsIssuedComponent"))
            throw new CyprusException("Issue Component not created");
    	
    }
    
    int doctypeID=getJobWorkReceiptDocType();
    // Create Job Work Receipt (M_InOut)
    MInOut jobWorkReceipt = new MInOut(getCtx(), 0, get_TrxName());
    jobWorkReceipt.setAD_Org_ID(jobOrder.getAD_Org_ID());
    jobWorkReceipt.setC_DocType_ID(doctypeID);
    jobWorkReceipt.setC_DocType_ID(doctypeID);
    jobWorkReceipt.setC_BPartner_ID(jobOrder.getC_BPartner_ID());
    jobWorkReceipt.setC_BPartner_Location_ID(jobOrder.getC_BPartner_Location_ID());
    jobWorkReceipt.setAD_User_ID(jobOrder.getAD_User_ID());
    jobWorkReceipt.setM_Warehouse_ID(jobOrder.getM_Warehouse_ID());
    jobWorkReceipt.setPriorityRule(jobOrder.getPriority());
    jobWorkReceipt.setMovementDate(jobOrder.getDateOrdered());
    jobWorkReceipt.set_ValueNoCheck("IsJobWork", true);
    jobWorkReceipt.set_ValueNoCheck("C_JobOrder_ID", jobOrderID);    
    jobWorkReceipt.setDocStatus("DR"); // Draft
    jobWorkReceipt.setDocAction("CO"); // Complete
    jobWorkReceipt.setMovementType("J+");
    jobWorkReceipt.saveEx();

    // Create Job Work Lines (M_InOutLine) from MJobOrderLine
    createJobWorkLines(jobWorkReceipt);
    return "Job Work (+) Created: " + jobWorkReceipt.getDocumentNo();
}

private boolean isJobWorkReceiptExists(int jobOrderID) {
    String sql = "SELECT COUNT(*) FROM M_InOut WHERE C_JobOrder_ID=? AND MovementType='J+' AND DocStatus NOT IN ('VO', 'RE')";
    int count = DB.getSQLValueEx(get_TrxName(), sql, jobOrderID);
    return count > 0; // If count > 0, Job Work (+) receipt already exists
}

private int getJobWorkReceiptDocType() {
    String sql = "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='JWR' AND AD_Client_ID=?";
    return DB.getSQLValueEx(get_TrxName(), sql, getAD_Client_ID());
}

private void createJobWorkLines(MInOut jobWorkReceipt) {
    String sql = "SELECT PriceEntered, C_JobOrderLine_ID, M_Product_ID, C_UOM_ID, LineNo, Description, QtyEntered, C_Charge_ID " +
                 "FROM C_JobOrderLine WHERE C_JobOrder_ID=?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        pstmt = DB.prepareStatement(sql, get_TrxName());
        pstmt.setInt(1, jobOrderID);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            int jobOrderLineID = rs.getInt("C_JobOrderLine_ID");
            int productID = rs.getInt("M_Product_ID");
            int uomID = rs.getInt("C_UOM_ID");
            int lineNo = rs.getInt("LineNo");
            String description = rs.getString("Description");
            BigDecimal qty = rs.getBigDecimal("QtyEntered");
            int chargeID = rs.getInt("C_Charge_ID"); // Fetching C_Charge_ID from JobOrderLine
            BigDecimal PriceEntered=rs.getBigDecimal("PriceEntered");

            if (qty == null || qty.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            MInOutLine jobWorkLine = new MInOutLine(jobWorkReceipt);
            jobWorkLine.setM_Product_ID(productID);
            jobWorkLine.setC_UOM_ID(uomID);
            jobWorkLine.setLine(lineNo);
            jobWorkLine.setDescription(description);
            jobWorkLine.setQtyEntered(qty);
            jobWorkLine.setMovementQty(qty);
            jobWorkLine.setM_Locator_ID(getDefaultLocator(jobWorkReceipt.getM_Warehouse_ID()));
            jobWorkLine.set_ValueNoCheck("C_JobOrderLine_ID", jobOrderLineID);
            
         // **Set C_Charge_ID in MInOutLine if available**
            if (chargeID > 0) {
                jobWorkLine.set_ValueNoCheck("C_Charge_ID", chargeID);
                jobWorkLine.set_ValueNoCheck("Price", PriceEntered);

            }
            
            jobWorkLine.saveEx();
        }
    } catch (Exception e) {
        log.log(Level.SEVERE, "Error creating Job Work Lines", e);
        throw new CyprusException("Error creating Job Work Lines", e);
    } finally {
        DB.close(rs, pstmt);
    }
}

private int getDefaultLocator(int warehouseID) {
    String sql = "SELECT M_Locator_ID FROM M_Locator WHERE M_Warehouse_ID=? AND IsDefault='Y'";
    int locatorID = DB.getSQLValueEx(get_TrxName(), sql, warehouseID);
    if (locatorID <= 0) {
        throw new CyprusException("Default Locator not found for Warehouse ID: " + warehouseID);
    }
    return locatorID;
}
}
