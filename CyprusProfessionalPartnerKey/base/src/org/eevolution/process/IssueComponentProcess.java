package org.eevolution.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MJobOrder;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

public class IssueComponentProcess extends SvrProcess {private int jobOrderId = 0;

@Override
protected void prepare() {
    jobOrderId = getRecord_ID(); // Get the selected Job Work Order ID
}

@Override
protected String doIt() throws Exception {
	if (jobOrderId <= 0) {
		return "Invalid Job Work Order";
	}

	MJobOrder jobOrder=new MJobOrder(getCtx(), jobOrderId, get_TrxName());
    if (!"CO".equals(jobOrder.getDocStatus())) {
    	return "Document must be in Complete status to generate Job Work(-).";
    }
	
	// Check if a Job Work Issue already exists
    if (jobWorkIssueExists(jobOrderId)) {
        throw new CyprusException("Job Work (-) already exists for this Vendor & Location.");
    }
	
	
	// Create Job Work (-) (M_InOut)
	MInOut jobWorkIssue = new MInOut(getCtx(), 0, get_TrxName());
	int docTypeID = getJobWorkIssueDocType();
	if (docTypeID <= 0) {
	    throw new CyprusException("No valid Job Work Issue Document Type found.");
	}
	jobWorkIssue.setC_DocType_ID(docTypeID);
	jobWorkIssue.setDocStatus("DR"); // Draft
	jobWorkIssue.setDocAction("CO"); // Complete
	jobWorkIssue.setMovementType("J-");

	// Fetch details from Job Work Order
	jobWorkIssue.setC_BPartner_ID(jobOrder.getC_BPartner_ID()); // Vendor
	jobWorkIssue.setC_BPartner_Location_ID(jobOrder.getC_BPartner_Location_ID());
	jobWorkIssue.setAD_User_ID(jobOrder.getAD_User_ID()); // Vendor Contact
	jobWorkIssue.setM_Warehouse_ID(jobOrder.getM_Warehouse_ID()); // Warehouse
	jobWorkIssue.setPriorityRule(jobOrder.getPriority()); // Priority
	jobWorkIssue.setMovementDate(jobOrder.getDateOrdered());
	jobWorkIssue.set_ValueNoCheck("IsJobWork", true);
	jobWorkIssue.set_ValueNoCheck("C_JobOrder_ID", jobOrderId);
	jobWorkIssue.saveEx();
	int jobWorkIssueID = jobWorkIssue.get_ID();

	// Create Job Work Lines (M_InOutLine)
	createJobWorkLines(jobWorkIssueID);
	
	// **UPDATE M_JobOrder WITH M_InOut_ID**
    updateJobOrderWithInOutID(jobOrderId, jobWorkIssueID);

	return "Job Work (-) Created: " + jobWorkIssue.getDocumentNo();
}

private void createJobWorkLines(int jobWorkIssueID) {
    
	
	// Fetch Warehouse ID from the Job Work (-) document
    MInOut jobWorkIssue = new MInOut(getCtx(), jobWorkIssueID, get_TrxName());
    int warehouseID = jobWorkIssue.getM_Warehouse_ID();

    // Get default Locator for this Warehouse
    int locatorID = getDefaultLocator(warehouseID);
    if (locatorID <= 0) {
        throw new CyprusException("No default Locator found for Warehouse ID: " + warehouseID);
    }
	
    // **Step 1: Validate if there are any Component records before proceeding**
    if (!hasValidJobLineComponents(jobOrderId)) {
        throw new CyprusException("No valid Job Line Components found");
    }

    boolean hasCreatedLines = false; // Flag to check if any line is created

    // **Step 2: Process and create MInOutLine records**
	String sql = "SELECT C_JobOrderLine_ID,C_JobLineComponents_ID, M_Product_ID, C_UOM_ID, LineNo, Description, QtyEntered " +
                 "FROM C_JobLineComponents WHERE C_JobOrderLine_ID IN " +
                 "(SELECT C_JobOrderLine_ID FROM C_JobOrderLine WHERE C_JobOrder_ID=?)";

    try (PreparedStatement pstmt = DB.prepareStatement(sql, get_TrxName())) {
    	
    	
        pstmt.setInt(1, jobOrderId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int componentID = rs.getInt("C_JobLineComponents_ID");
            int C_JobOrderLine_ID = rs.getInt("C_JobOrderLine_ID");

            int productId = rs.getInt("M_Product_ID");
            int uomId = rs.getInt("C_UOM_ID");
            int lineNo = rs.getInt("LineNo");
            String description = rs.getString("Description");
            BigDecimal qty = rs.getBigDecimal("QtyEntered");
            if (qty == null) {
                qty = BigDecimal.ZERO;
            }

            MInOutLine jobWorkLine = new MInOutLine(getCtx(), 0, get_TrxName());
            jobWorkLine.setM_InOut_ID(jobWorkIssueID);
            jobWorkLine.setM_Product_ID(productId);
            jobWorkLine.setC_UOM_ID(uomId);
            jobWorkLine.setLine(lineNo);
            jobWorkLine.setDescription(description);
            jobWorkLine.setMovementQty(qty);
            jobWorkLine.setQtyEntered(qty);
            jobWorkLine.setM_Locator_ID(locatorID); // Set the Locator
            jobWorkLine.set_ValueNoCheck("C_JobLineComponents_ID", componentID); // Reference to Component Table
            jobWorkLine.set_ValueNoCheck("C_JobOrderLine_ID", C_JobOrderLine_ID);         
            jobWorkLine.saveEx();
            
            hasCreatedLines = true; // At least one line was created

        }
    } catch (Exception e) {
        log.log(Level.SEVERE, "Error creating Job Work Lines", e);
        throw new CyprusException("Failed to create Job Work Lines: " + e.getMessage());
    }
    
 // **Step 3: Update C_JobOrder.IsIssuedComponent if at least one line was created**
    if (hasCreatedLines) {
        updateIsIssuedComponent(jobOrderId);
    }
    
}

	private void updateIsIssuedComponent(int jobOrderId) {
	    String sql = "UPDATE C_JobOrder SET IsIssuedComponent='Y' WHERE C_JobOrder_ID=?";
	    int updatedRows = DB.executeUpdate(sql, new Object[]{jobOrderId}, false, get_TrxName());
	    
	    if (updatedRows > 0) {
	        log.info("Updated C_JobOrder: IsIssuedComponent set to 'Y' for Job Order ID: " + jobOrderId);
	    } else {
	        log.warning("Failed to update C_JobOrder: No records updated for Job Order ID: " + jobOrderId);
	    }
	}

	private int getJobWorkIssueDocType() {
		String sql = "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='JWI' AND AD_Client_ID=?";
		return DB.getSQLValue(get_TrxName(), sql, getAD_Client_ID());
	}
	
	private int getDefaultLocator(int warehouseID) {
	    String sql = "SELECT M_Locator_ID FROM M_Locator WHERE M_Warehouse_ID=? AND IsDefault='Y' AND IsActive='Y'";
	    return DB.getSQLValue(get_TrxName(), sql, warehouseID);
	}

	private boolean hasValidJobLineComponents(int jobOrderId) {
	    String sql = "SELECT COUNT(*) FROM C_JobLineComponents " +
	                 "WHERE C_JobOrderLine_ID IN (SELECT C_JobOrderLine_ID FROM C_JobOrderLine WHERE C_JobOrder_ID=?) " +
	                 "AND IsActive='Y'";
	    int count = DB.getSQLValue(get_TrxName(), sql, jobOrderId);
	    return count > 0; // Returns true if at least one valid component exists
	}
	
	private boolean jobWorkIssueExists(int jobOrderId) {
	    String sql = "SELECT COUNT(*) FROM M_InOut WHERE C_JobOrder_ID = ? AND DocStatus NOT IN ('VO', 'RE')";
	    int count = DB.getSQLValue(get_TrxName(), sql, jobOrderId);
	    return count > 0; // Returns true if a record already exists
	}
	
	private void updateJobOrderWithInOutID(int jobOrderId, int inOutId) {
	    String sql = "UPDATE C_JobOrder SET M_InOut_ID=? WHERE C_JobOrder_ID=?";
	    int updatedRows = DB.executeUpdate(sql, new Object[]{inOutId, jobOrderId}, false, get_TrxName());

	    if (updatedRows > 0) {
	        log.info("Updated C_JobOrder: M_InOut_ID set to " + inOutId + " for Job Order ID: " + jobOrderId);
	    } else {
	        log.warning("Failed to update C_JobOrder: No records updated for Job Order ID: " + jobOrderId);
	    }
	}

}
