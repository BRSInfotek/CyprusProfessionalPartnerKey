package org.cyprus.mrp.util;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mrp.model.X_MRP_Plan;
import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProduct;
// org.cyprusbrs.model.X_MRP_Plan;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

public class PlanUtil {
  protected static CLogger log = CLogger.getCLogger(PlanUtil.class);
  
  public static boolean isCorrectManufacturingSetup(MProduct product, Properties ctx, String trx) {
    if (product.isBOM() && product.isVerified() && product.isStocked()) {
      MBOM[] BOM = MBOM.getOfProduct(ctx, product.getM_Product_ID(), trx, "BOMTYPE = 'A' AND BOMUSE = 'M' ");
      if (BOM.length == 0) {
        log.warning(product.getName() + ": " + "Has no Current Active Manufacturing BOM");
        return false;
      } 
      return true;
    } 
    return false;
  }
  
  public static boolean checkProductProcurement(MProduct product) {
    if (product == null)
      return false; 
    if (product.isPurchased() || product.isManufactured())
      return true; 
    return false;
  }
  
  public static boolean isManufacturedInMRP(MProduct mproduct, String mPriorityImplementation) {
    if (mPriorityImplementation.trim().equals(X_MRP_Plan.PRIORITYIMPLEMENTATION_Manufacture))
      if (mproduct.isPurchased() && mproduct.isManufactured())
        return true;  
    if (mproduct.isManufactured() && !mproduct.isPurchased())
      return true; 
    return false;
  }
  
  public static String getPeriodIdsFromPlan(MPeriod startPeriod, int planID, Trx trx, Properties ctx) {
    CPreparedStatement cPreparedStatement = null;
    StringBuilder str = new StringBuilder();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String selectSql = "SELECT C_Period_ID FROM C_Period WHERE \tC_Period.C_Year_ID IN  (SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ? ) AND (C_Period.C_Year_ID * 1000) + C_Period.PeriodNo BETWEEN(  SELECT (per1.C_Year_ID * 1000) + per1.PeriodNo FROM C_Period per1 WHERE per1.C_Period_ID = ? ) \tAND (SELECT (per2.C_Year_ID * 1000) + per2.PeriodNo FROM C_Period per2 WHERE per2.C_Period_ID = (SELECT MRP_Plan.C_Period_To_ID FROM MRP_Plan WHERE MRP_Plan_ID = ?)) ";
    try {
      cPreparedStatement = DB.prepareStatement(selectSql, trx.getTrxName());
      cPreparedStatement.setInt(1, planID);
      MPeriod prevPeriod = MPeriod.getPreviousPeriod(startPeriod, ctx, trx);
      if (prevPeriod == null) {
        MPeriod nextPeriod = MPeriod.getNextPeriod(startPeriod, ctx, trx);
        if (nextPeriod == null)
          return ""; 
        cPreparedStatement.setInt(2, nextPeriod.getC_Period_ID());
      } else {
        cPreparedStatement.setInt(2, startPeriod.getC_Period_ID());
      } 
      cPreparedStatement.setInt(3, planID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        str.append(rs.getInt(1));
        str.append(" , ");
      } 
      str.deleteCharAt(str.lastIndexOf(","));
    } catch (Exception e) {
      log.log(Level.SEVERE, "Period Ids - " + e.getMessage(), e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
          rs = null;
        }  
      if (cPreparedStatement != null)
        try {
          cPreparedStatement.close();
          cPreparedStatement = null;
        } catch (SQLException e) {
          cPreparedStatement = null;
        }  
    } 
    return str.toString();
  }
}

