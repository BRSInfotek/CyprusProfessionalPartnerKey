package org.cyprusbrs.process;

import java.util.List;

import org.cyprusbrs.framework.MMovement;
import org.cyprusbrs.framework.MMovementLine;
import org.cyprusbrs.framework.MRequisitionLine;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.DB;

public class GenerateMovementFromRequisition extends SvrProcess {

	private int requisitionID;
    private int locatorID;
    private int locatorToID;
    private int movementID;

    @Override
    protected void prepare() {
        // Get Movement ID from context (current record)
        movementID = getRecord_ID();

        // Get parameters
        for (ProcessInfoParameter param : getParameter()) {
            String name = param.getParameterName();
            if ("M_Requisition_ID".equals(name)) {
                requisitionID = param.getParameterAsInt();
            } else if ("M_Locator_ID".equals(name)) {
                locatorID = param.getParameterAsInt();
            } else if ("M_LocatorTo_ID".equals(name)) {
                locatorToID = param.getParameterAsInt();
            }
        }
    }

    @Override
    protected String doIt() throws Exception {
        if (movementID == 0 || requisitionID == 0 || locatorID == 0 || locatorToID == 0) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        // Validate that Movement exists
        MMovement movement = new MMovement(getCtx(), movementID, get_TrxName());
        if (movement.get_ID() == 0) {
            throw new IllegalStateException("Movement not found!");
        }

        // Fetch requisition lines
        List<MRequisitionLine> reqLines = new Query(getCtx(), MRequisitionLine.Table_Name, 
                "M_Requisition_ID=?", get_TrxName())
                .setParameters(requisitionID)
                .list();

        if (reqLines.isEmpty()) {
            return "No Requisition Lines Found";
        }

     // SQL to count existing Movement Lines linked to this Requisition Line
        String countSQL = "SELECT COUNT(*) FROM M_MovementLine WHERE M_Movement_ID=?";
        int count = DB.getSQLValueEx(get_TrxName(), countSQL, new Object[]{movementID});

        if (count > 0) {
        	log.info("Found " + count + " existing Movement Line(s) " + movementID);

        	// Delete the existing Movement Line linked to the Requisition Line using SQL query
        	String deleteSQL = "DELETE FROM M_MovementLine WHERE M_Movement_ID=?";
        	int deletedRows = DB.executeUpdate(deleteSQL, new Object[]{movementID}, false, get_TrxName());
        	if (deletedRows > 0) {
        		log.info("Deleted existing Movement " + movementID);
        	}
        }else {
        	log.info("No existing Movement Line found " + movementID);
        }

        
        // Create Movement Lines from Requisition Lines
        for (MRequisitionLine reqLine : reqLines) {
            MMovementLine moveLine = new MMovementLine(movement);
            moveLine.setM_Product_ID(reqLine.getM_Product_ID());
            moveLine.setM_Locator_ID(locatorID);
            moveLine.setM_LocatorTo_ID(locatorToID);
            moveLine.setMovementQty(reqLine.getQty());
            moveLine.set_ValueNoCheck("M_RequisitionLine_ID", reqLine.getM_RequisitionLine_ID());
            moveLine.saveEx();
            
         // Optimized SQL Update
            String sql = "UPDATE M_RequisitionLine SET M_MovementLine_ID = ? WHERE M_RequisitionLine_ID = ?";
            int no = DB.executeUpdate(sql, new Object[]{moveLine.getM_MovementLine_ID(), reqLine.getM_RequisitionLine_ID()}, false, get_TrxName());
            if (no == 0) {
                throw new IllegalStateException("Failed to update Requisition Line ID: " + reqLine.getM_RequisitionLine_ID());
            }
        }

        return "Movement Lines Created for Movement: " + movement.getDocumentNo();
    }
    

}
