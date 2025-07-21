package org.cyprusbrs.framework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;

public class CalloutQuality extends CalloutEngine {

    private static CLogger log = CLogger.getCLogger(CalloutQuality.class);

    public String setQualityPlanLine(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (isCalloutActive() || value == null)
            return "";

        int QC_QualityParameter_ID = ((Integer)value).intValue();
        if (QC_QualityParameter_ID == 0)
            return "";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT c_uom_id, qc_maximumvalue, qc_minimumvalue, " +
                         "qc_qualityresult, qc_qualitytype " +
                         "FROM QC_QualityParameter " +
                         "WHERE QC_QualityParameter_ID=? AND IsActive='Y'";
            
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, QC_QualityParameter_ID);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {     
            	mTab.setValue("QC_QualityType", rs.getString("qc_qualitytype"));
            	if("R".equalsIgnoreCase(rs.getString("qc_qualitytype")))
            	{
            		mTab.setValue("C_UOM_ID", rs.getInt("c_uom_id"));
                    mTab.setValue("QC_MaximumValue", rs.getBigDecimal("qc_maximumvalue"));
                    mTab.setValue("QC_MinimumValue", rs.getBigDecimal("qc_minimumvalue"));
            	}
            	else if("V".equalsIgnoreCase(rs.getString("qc_qualitytype")))
            	{
            		mTab.setValue("QC_QualityResult", rs.getString("qc_qualityresult"));                    
            	}              
            }
        } catch (SQLException e) {
            log.severe("Error in Quality Parameter Callout: " + e.getMessage());
            return e.getMessage();
        } finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }
        
        return "";
    }
}