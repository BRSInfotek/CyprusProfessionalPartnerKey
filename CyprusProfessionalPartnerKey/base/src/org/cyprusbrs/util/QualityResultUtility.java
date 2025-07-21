package org.cyprusbrs.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.cyprusbrs.framework.MQCCollectorQualityResult;
import org.cyprusbrs.framework.MQCMRQualityResult;
import org.cyprusbrs.framework.MQCProductionQualityResult;

/**
 * Utility class for handling quality result operations.
 * Provides methods to save quality results for different transaction types.
 */
public class QualityResultUtility {
    
    private static final CLogger log = CLogger.getCLogger(QualityResultUtility.class);
    
    // Transaction type constants
    private static final String TRANSACTION_TYPE_MR = "MR";
    private static final String TRANSACTION_TYPE_CC = "CC";
    // Note: Original code uses 'MF' in SQL but 'CC' in method parameter
    private static final String TRANSACTION_TYPE_PR = "PR";
    
    // Quality type constants
    private static final String QUALITY_TYPE_RANGE = "R";
    private static final String QUALITY_TYPE_VALUE = "V";
    
    // Default values
    private static final int INITIAL_LINE_NO = 10;
    private static final int LINE_NO_INCREMENT = 10;

    /**
     * Saves quality results for the specified transaction type and ID.
     * 
     * @param type The transaction type (MR, CC, PR)
     * @param id The transaction ID
     * @param productId The product ID
     */
    public static void saveQualityResult(String type, int id, int productId) {
        if (type == null || type.isEmpty()) {
            log.warning("Type parameter cannot be null or empty");
            return;
        }

        String trxName = Trx.createTrxName("QualityResult");
        Trx trx = Trx.get(trxName, true);
        
        try {
            int qualityPlanId = getQualityPlanId(type, productId);
            
            if (qualityPlanId > 0) {
                saveQualityResultsForPlan(type, id, qualityPlanId, trx);
            } else {
                log.warning("No quality plan found for type: " + type + " and product ID: " + productId);
            }
        } catch (Exception e) {
            log.severe("Error saving quality result: " + e.getMessage());
            if (trx != null) {
                trx.rollback();
            }
        } finally {
            if (trx != null) {
                trx.close();
            }
        }
    }

    /**
     * Retrieves the quality plan ID for the given transaction type and product.
     */
    private static int getQualityPlanId(String type, int productId) {
        String sql = "SELECT QC_QualityPlan_ID FROM QC_QualityPlan WHERE M_Product_ID=? AND QC_TrxType=?";
        
        // Handle the special case where CC type maps to MF in the database
        String dbType = TRANSACTION_TYPE_CC.equalsIgnoreCase(type) ? "MF" : type;
        
        return DB.getSQLValueEx(null, sql, productId, dbType);
    }

    /**
     * Saves all quality results for a given quality plan.
     */
    private static void saveQualityResultsForPlan(String type, int id, int qualityPlanId, Trx trx) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int lineNo = INITIAL_LINE_NO;
        
        try {
            String sql = "SELECT c_uom_id, qc_maximumvalue, qc_minimumvalue, qc_qualityparameter_id, "
                       + "qc_qualityplan_id, qc_qualityplanline_id, qc_qualityresult, qc_qualitytype "
                       + "FROM QC_QualityPlanLine WHERE QC_QualityPlan_ID=?";
            
            pstmt = DB.prepareStatement(sql, trx.getTrxName());
            pstmt.setInt(1, qualityPlanId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                boolean saved = false;
                
                switch (type.toUpperCase()) {
                    case TRANSACTION_TYPE_MR:
                        saved = saveMRQualityResult(id, lineNo, rs, trx);
                        break;
                    case TRANSACTION_TYPE_CC:
                        saved = saveCCQualityResult(id, lineNo, rs, trx);
                        break;
                    case TRANSACTION_TYPE_PR:
                        saved = savePRQualityResult(id, lineNo, rs, trx);
                        break;
                    default:
                        log.warning("Unknown transaction type: " + type);
                }
                
                if (saved) {
                    lineNo += LINE_NO_INCREMENT;
                }
            }
        } catch (SQLException e) {
            log.severe("Database error while saving quality results: " + e.getMessage());
        } finally {
            DB.close(rs, pstmt);
        }
    }

    /**
     * Saves a Manufacturing Return quality result.
     */
    private static boolean saveMRQualityResult(int id, int lineNo, ResultSet rs, Trx trx) throws SQLException {
        MQCMRQualityResult qr = new MQCMRQualityResult(Env.getCtx(), 0, trx.getTrxName());
        qr.set_ValueOfColumn("M_InOutLine_ID", id);
        qr.set_ValueOfColumn("Line", lineNo);
        setCommonQualityResultFields(qr, rs);
        return saveQualityResult(qr, trx);
    }

    /**
     * Saves a Cost Collector quality result.
     */
    private static boolean saveCCQualityResult(int id, int lineNo, ResultSet rs, Trx trx) throws SQLException {
        MQCCollectorQualityResult ccqr = new MQCCollectorQualityResult(Env.getCtx(), 0, trx.getTrxName());
        ccqr.set_ValueOfColumn("PP_Cost_CollectorLine_ID", id);
        ccqr.set_ValueOfColumn("Line", lineNo);
        setCommonQualityResultFields(ccqr, rs);
        return saveQualityResult(ccqr, trx);
    }

    /**
     * Saves a Production quality result.
     */
    private static boolean savePRQualityResult(int id, int lineNo, ResultSet rs, Trx trx) throws SQLException {
        MQCProductionQualityResult cpqr = new MQCProductionQualityResult(Env.getCtx(), 0, trx.getTrxName());
        cpqr.set_ValueOfColumn("M_Production_ID", id);
        cpqr.set_ValueOfColumn("Line", lineNo);
        setCommonQualityResultFields(cpqr, rs);
        return saveQualityResult(cpqr, trx);
    }

    /**
     * Sets common fields for all quality result types.
     */
    private static void setCommonQualityResultFields(Object qualityResult, ResultSet rs) throws SQLException {
        int qualityParameterId = rs.getInt("qc_qualityparameter_id");
        String qualityType = rs.getString("qc_qualitytype");
        
        if (qualityResult instanceof MQCMRQualityResult) {
            MQCMRQualityResult qr = (MQCMRQualityResult) qualityResult;
            setQualityResultFields(qr, rs, qualityParameterId, qualityType);
        } else if (qualityResult instanceof MQCCollectorQualityResult) {
            MQCCollectorQualityResult qr = (MQCCollectorQualityResult) qualityResult;
            setQualityResultFields(qr, rs, qualityParameterId, qualityType);
        } else if (qualityResult instanceof MQCProductionQualityResult) {
            MQCProductionQualityResult qr = (MQCProductionQualityResult) qualityResult;
            setQualityResultFields(qr, rs, qualityParameterId, qualityType);
        }
    }

    /**
     * Sets the specific fields for a quality result based on its type.
     */
    private static void setQualityResultFields(Object qualityResult, ResultSet rs, 
                                             int qualityParameterId, String qualityType) throws SQLException {
        if (qualityResult instanceof MQCMRQualityResult) {
            MQCMRQualityResult qr = (MQCMRQualityResult) qualityResult;
            setFields(qr, rs, qualityParameterId, qualityType);
        } else if (qualityResult instanceof MQCCollectorQualityResult) {
            MQCCollectorQualityResult qr = (MQCCollectorQualityResult) qualityResult;
            setFields(qr, rs, qualityParameterId, qualityType);
        } else if (qualityResult instanceof MQCProductionQualityResult) {
            MQCProductionQualityResult qr = (MQCProductionQualityResult) qualityResult;
            setFields(qr, rs, qualityParameterId, qualityType);
        }
    }

    /**
     * Helper method to set the actual fields on the quality result object.
     */
    private static void setFields(Object qr, ResultSet rs, int qualityParameterId, 
                                String qualityType) throws SQLException {
        if (qr instanceof MQCMRQualityResult) {
            MQCMRQualityResult result = (MQCMRQualityResult) qr;
            result.set_ValueOfColumn("QC_QualityParameter_ID", qualityParameterId);
            result.set_ValueOfColumn("QC_QualityType", qualityType);
            setTypeSpecificFields(result, rs, qualityType);
        } else if (qr instanceof MQCCollectorQualityResult) {
            MQCCollectorQualityResult result = (MQCCollectorQualityResult) qr;
            result.set_ValueOfColumn("QC_QualityParameter_ID", qualityParameterId);
            result.set_ValueOfColumn("QC_QualityType", qualityType);
            setTypeSpecificFields(result, rs, qualityType);
        } else if (qr instanceof MQCProductionQualityResult) {
            MQCProductionQualityResult result = (MQCProductionQualityResult) qr;
            result.set_ValueOfColumn("QC_QualityParameter_ID", qualityParameterId);
            result.set_ValueOfColumn("QC_QualityType", qualityType);
            setTypeSpecificFields(result, rs, qualityType);
        }
    }

    /**
     * Sets type-specific fields for the quality result.
     */
    private static void setTypeSpecificFields(Object qr, ResultSet rs, String qualityType) throws SQLException {
        if (QUALITY_TYPE_RANGE.equalsIgnoreCase(qualityType)) {
            if (qr instanceof MQCMRQualityResult) {
                MQCMRQualityResult result = (MQCMRQualityResult) qr;
                result.set_ValueOfColumn("C_UOM_ID", rs.getInt("c_uom_id"));
                result.set_ValueOfColumn("QC_MaximumValue", rs.getBigDecimal("qc_maximumvalue"));
                result.set_ValueOfColumn("QC_MinimumValue", rs.getBigDecimal("qc_minimumvalue"));
            } else if (qr instanceof MQCCollectorQualityResult) {
                MQCCollectorQualityResult result = (MQCCollectorQualityResult) qr;
                result.set_ValueOfColumn("C_UOM_ID", rs.getInt("c_uom_id"));
                result.set_ValueOfColumn("QC_MaximumValue", rs.getBigDecimal("qc_maximumvalue"));
                result.set_ValueOfColumn("QC_MinimumValue", rs.getBigDecimal("qc_minimumvalue"));
            } else if (qr instanceof MQCProductionQualityResult) {
                MQCProductionQualityResult result = (MQCProductionQualityResult) qr;
                result.set_ValueOfColumn("C_UOM_ID", rs.getInt("c_uom_id"));
                result.set_ValueOfColumn("QC_MaximumValue", rs.getBigDecimal("qc_maximumvalue"));
                result.set_ValueOfColumn("QC_MinimumValue", rs.getBigDecimal("qc_minimumvalue"));
            }
        } else if (QUALITY_TYPE_VALUE.equalsIgnoreCase(qualityType)) {
            if (qr instanceof MQCMRQualityResult) {
                ((MQCMRQualityResult) qr).set_ValueOfColumn("QC_QualityResult", rs.getString("qc_qualityresult"));
            } else if (qr instanceof MQCCollectorQualityResult) {
                ((MQCCollectorQualityResult) qr).set_ValueOfColumn("QC_QualityResult", rs.getString("qc_qualityresult"));
            } else if (qr instanceof MQCProductionQualityResult) {
                ((MQCProductionQualityResult) qr).set_ValueOfColumn("QC_QualityResult", rs.getString("qc_qualityresult"));
            }
        }
        
        // Set common default values
        if (qr instanceof MQCMRQualityResult) {
            MQCMRQualityResult result = (MQCMRQualityResult) qr;
            result.set_ValueOfColumn("QC_ActualResult", "R");
            result.set_ValueOfColumn("QC_ObservedMinVal", Env.ZERO);
            result.set_ValueOfColumn("QC_ObservedMaxVal", Env.ZERO);
        } else if (qr instanceof MQCCollectorQualityResult) {
            MQCCollectorQualityResult result = (MQCCollectorQualityResult) qr;
            result.set_ValueOfColumn("QC_ActualResult", "R");
            result.set_ValueOfColumn("QC_ObservedMinVal", Env.ZERO);
            result.set_ValueOfColumn("QC_ObservedMaxVal", Env.ZERO);
        } else if (qr instanceof MQCProductionQualityResult) {
            MQCProductionQualityResult result = (MQCProductionQualityResult) qr;
            result.set_ValueOfColumn("QC_ActualResult", "R");
            result.set_ValueOfColumn("QC_ObservedMinVal", Env.ZERO);
            result.set_ValueOfColumn("QC_ObservedMaxVal", Env.ZERO);
        }
    }

    /**
     * Saves the quality result and commits the transaction if successful.
     */
    private static boolean saveQualityResult(Object qualityResult, Trx trx) {
        boolean saved = false;
        
        if (qualityResult instanceof MQCMRQualityResult) {
            saved = ((MQCMRQualityResult) qualityResult).save();
        } else if (qualityResult instanceof MQCCollectorQualityResult) {
            saved = ((MQCCollectorQualityResult) qualityResult).save();
        } else if (qualityResult instanceof MQCProductionQualityResult) {
            saved = ((MQCProductionQualityResult) qualityResult).save();
        }
        
        if (saved) {
            trx.commit();
        }
        
        return saved;
    }
}