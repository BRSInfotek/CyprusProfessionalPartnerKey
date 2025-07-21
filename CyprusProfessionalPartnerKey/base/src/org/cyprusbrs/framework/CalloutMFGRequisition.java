package org.cyprusbrs.framework;

import java.util.Properties;

import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.DB;

public class CalloutMFGRequisition extends CalloutEngine {
	
	// org.cyprusbrs.model.CalloutMFGRequisition.validateBOMFormula
	public String validateBOMFormula(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) return "";

        // Get M_MFGRequisition_ID from the Line
        Integer requisitionID = (Integer) mTab.getValue("M_MFGRequisition_ID");
        if (requisitionID == null || requisitionID == 0) return "";

        // Fetch JobOrderType from M_MFGRequisition table
        String sql = "SELECT JobOrderType FROM M_MFGRequisition WHERE M_MFGRequisition_ID=?";
        String jobOrderType = DB.getSQLValueString(null, sql, requisitionID);

        // If JobOrderType is 'PV', clear BOM Formula field and show an error message
        if ("PV".equals(jobOrderType)) {
            mTab.setValue("PP_Product_BOM_ID", null);
            return "BOM Formula cannot be selected when JobOrderType is 'Component Provided by Vendor'.";
        }

        return "";
    }

}
