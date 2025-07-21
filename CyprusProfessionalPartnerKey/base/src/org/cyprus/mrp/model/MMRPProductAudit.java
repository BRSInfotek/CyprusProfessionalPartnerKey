package org.cyprus.mrp.model;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
//import org.cyprusbrs.model.X_MRP_Product_Audit;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMRPProductAudit extends X_MRP_Product_Audit {
  private static final CLogger log = CLogger.getCLogger(MMRPProductAudit.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPProductAudit.class);
  
  public MMRPProductAudit(Properties ctx, int MRP_Product_Audit_ID, String trx) {
    super(ctx, MRP_Product_Audit_ID, trx);
  }
  
  public MMRPProductAudit(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public static boolean isProductExistsInRun(MProduct product, MMRPPlanRun run) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT M_Product_ID from MRP_Product_Audit WHERE M_Product_ID = ? and MRP_PlanRun_ID=?";
    int productID = product.getM_Product_ID();
    int runID = run.getMRP_PlanRun_ID();
    boolean isProductExist = false;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, run.get_TrxName());
      cPreparedStatement.setInt(1, productID);
      cPreparedStatement.setInt(2, runID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        isProductExist = true; 
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null) {
          cPreparedStatement.close();
          cPreparedStatement = null;
        } 
        if (rs != null) {
          rs.close();
          rs = null;
        } 
      } catch (Exception e) {
        cPreparedStatement = null;
        rs = null;
      } 
    } 
    return isProductExist;
  }
  
  public static int getBPartner(int planRunID, int productID, String trx) {
    CPreparedStatement cPreparedStatement = null;
    int bPartnerID = 0;
    String sql = "SELECT C_BPartner_ID FROM MRP_Product_Audit WHERE MRP_PlanRun_ID =? AND  M_Product_ID = ? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, planRunID);
      cPreparedStatement.setInt(2, productID);
       rs = cPreparedStatement.executeQuery();
      if (rs.next())
        bPartnerID = rs.getInt(1); 
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
      DB.close(rs, cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return bPartnerID;
  }
}

