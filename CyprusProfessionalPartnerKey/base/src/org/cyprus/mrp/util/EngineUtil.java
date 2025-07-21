package org.cyprus.mrp.util;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mrp.model.MMRPPlannedDemand;
import org.cyprus.mrp.model.MMRPPlannedOrder;
import org.cyprus.mrp.model.X_MRP_PlannedAvailability;
import org.cyprus.mrp.model.X_MRP_PlannedOrder;
import org.cyprusbrs.framework.MBOMProduct;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.X_M_BOM;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.TimeUtil;
import org.cyprusbrs.util.Trx;

public class EngineUtil {
  protected static CLogger s_log = CLogger.getCLogger(EngineUtil.class);
  
  public static boolean updateExplodedDemand(MMRPPlannedDemand plannedTreeDemand, BigDecimal qtyToBeOrdered, int planRunID, String trx, Properties ctx) {
    MMRPPlannedDemand[] PlannedDemandList = MMRPPlannedDemand.getTreeChildrenPlannedDemand(plannedTreeDemand, planRunID, trx, ctx);
    MProduct product = MProduct.get(ctx, plannedTreeDemand.getM_Product_ID());
    MBOMProduct[] BOMProducts = MBOMProduct.getBOMLines(product, X_M_BOM.BOMTYPE_CurrentActive, X_M_BOM.BOMUSE_Manufacturing);
    if (BOMProducts.length == 0)
      return true; 
    for (MMRPPlannedDemand plannedDemand : PlannedDemandList) {
      BigDecimal unitQtyRequired = Env.ZERO;
      for (MBOMProduct BOMProduct : BOMProducts) {
        if (BOMProduct.getM_ProductBOM_ID() == plannedDemand.getM_Product_ID()) {
          unitQtyRequired = BOMProduct.getBOMQty();
          break;
        } 
      } 
      plannedDemand.setQtyRequired(qtyToBeOrdered.multiply(unitQtyRequired));
      if (!plannedDemand.save(trx)) {
        s_log.saveError("Error", "Planned Demand Not Saved");
        return false;
      } 
    } 
    return true;
  }
  
  public static MMRPPlannedOrder createPlannedOrder(MMRPPlannedDemand plannedDemand, BigDecimal qtyToBeOrdered, boolean isWorkOrder, int planRunID, String trx, Properties ctx) {
    MMRPPlannedOrder plannedOrder = new MMRPPlannedOrder(ctx, 0, trx);
    plannedOrder.setAD_Client_ID(plannedDemand.getAD_Client_ID());
    plannedOrder.setAD_Org_ID(plannedDemand.getAD_Org_ID());
    if (isWorkOrder) {
      plannedOrder.setOrderType(X_MRP_PlannedOrder.ORDERTYPE_WorkOrder);
    } else {
      plannedOrder.setOrderType(X_MRP_PlannedOrder.ORDERTYPE_PurchaseOrder);
    } 
    plannedOrder.setM_Product_ID(plannedDemand.getM_Product_ID());
    plannedOrder.setMRP_PlannedDemand_ID(plannedDemand.getMRP_PlannedDemand_ID());
    plannedOrder.setMRP_PlanRun_ID(planRunID);
    BigDecimal cumulativeLeadTimeInDays = plannedDemand.getLeadTime();
    MPeriod period = new MPeriod(ctx, plannedDemand.getC_Period_ID(), trx);
    Timestamp dateOrder = null;
    if (plannedDemand.getLevelNo() == 0) {
      MPeriod orderPeriod = MPeriod.getOfCalendar(ctx, period.getC_Calendar_ID(), TimeUtil.addDays(period.getEndDate(), -cumulativeLeadTimeInDays.intValue()));
      dateOrder = period.getStartDate();
      if (orderPeriod == null) {
        s_log.warning("Cumulative Lead time leads to a non existant period.");
      } else {
        dateOrder = orderPeriod.getStartDate();
      } 
    } else if (plannedDemand.getMRP_PlannedDemand_Root_ID() != 0) {
      MMRPPlannedDemand rootDemand = new MMRPPlannedDemand(ctx, plannedDemand.getMRP_PlannedDemand_Root_ID(), trx);
      cumulativeLeadTimeInDays = cumulativeLeadTimeInDays.subtract(rootDemand.getLeadTime());
      MPeriod orderPeriod = MPeriod.getOfCalendar(ctx, period.getC_Calendar_ID(), TimeUtil.addDays(period.getEndDate(), -rootDemand.getLeadTime().intValue()));
      if (orderPeriod == null) {
        s_log.warning("Cumulative Lead time leads to a non existant period.");
        dateOrder = period.getStartDate();
      } else {
        dateOrder = orderPeriod.getStartDate();
      } 
      dateOrder = TimeUtil.addDays(dateOrder, -cumulativeLeadTimeInDays.intValue());
    } else {
      dateOrder = period.getStartDate();
    } 
    plannedOrder.setDateOrdered(dateOrder);
    plannedOrder.setParentMRPPlannedOrder(plannedDemand, planRunID, trx, ctx);
    plannedOrder.setQtyOrdered(qtyToBeOrdered);
    plannedOrder.setLevelNo(plannedDemand.getLevelNo());
    plannedOrder.setPlannedOrderStatus(X_MRP_PlannedOrder.PLANNEDORDERSTATUS_NotImplemented);
    if (!plannedOrder.save(trx)) {
      s_log.saveError("Error", "Planned Order Not Saved");
      return null;
    } 
    return plannedOrder;
  }
  
  public static BigDecimal getQtyToBeOrdered(MMRPPlannedDemand plannedDemand, int planID, Trx trx, Properties ctx) {
    BigDecimal qtyDemand = plannedDemand.getQtyRequired();
    if (qtyDemand.equals(Env.ZERO))
      return Env.ZERO; 
    Timestamp dateRequired = plannedDemand.getDateRequired();
    int periodID = plannedDemand.getC_Period_ID();
    int productID = plannedDemand.getM_Product_ID();
    int runID = plannedDemand.getMRP_PlanRun_ID();
    BigDecimal qtyToBeOrdered = updateLatestPlannedAvailability(dateRequired, productID, periodID, runID, qtyDemand, planID, trx, ctx);
    return qtyToBeOrdered;
  }
  
  private static BigDecimal updateLatestPlannedAvailability(Timestamp dateRequired, int productID, int periodID, int runID, BigDecimal qtyDemand, int planID, Trx trx, Properties ctx) {
    String periodLiterals = getPeriodIdsLessThanEqualTo(periodID, planID, trx.getTrxName());
    String selectSQL = "SELECT QtyCalculated,QtyExpected, AvailabilityStatus FROM MRP_PlannedAvailability WHERE MRP_PlanRun_ID = ? AND C_Period_ID IN ";
    selectSQL = selectSQL.concat(" ( ").concat(periodLiterals).concat(" ) ").concat(" AND M_Product_ID = ? ").concat("ORDER BY TRUNC(DateExpected, 'DD') DESC FOR UPDATE");
    String updateSQL = "UPDATE MRP_PlannedAvailability SET QtyCalculated = ? , QtyExpected = ? , AvailabilityStatus = ? WHERE MRP_PlanRun_ID = ?  AND C_Period_ID = ? AND M_Product_ID = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    BigDecimal qtyCalculated = Env.ZERO;
    BigDecimal qtyExpected = Env.ZERO;
    String availabilityStatus = null;
    BigDecimal qtyToBeOrdered = Env.ZERO;
    try {
      pstmt = trx.getConnection().prepareStatement(selectSQL, 1003, 1008);
      pstmt.setInt(1, runID);
      pstmt.setInt(2, productID);
      rs = pstmt.executeQuery();
      MPeriod prevPeriod = MPeriod.get(ctx, periodID);
      if (rs.next()) {
        availabilityStatus = rs.getString(3);
        qtyCalculated = rs.getBigDecimal(1);
        qtyExpected = rs.getBigDecimal(2);
        if (availabilityStatus.equals(X_MRP_PlannedAvailability.AVAILABILITYSTATUS_NotUpdated)) {
          BigDecimal qtyPlannedReceipt = getQtyPlannedReceipt(dateRequired, productID, runID, trx.getTrxName());
          qtyCalculated = qtyCalculated.add(qtyPlannedReceipt);
          while (rs.next()) {
            if (rs.getString(3).equals(X_MRP_PlannedAvailability.AVAILABILITYSTATUS_Updated)) {
              qtyCalculated = qtyCalculated.add(rs.getBigDecimal(1));
              break;
            } 
            if (prevPeriod != null)
              prevPeriod = MPeriod.getPreviousPeriod(prevPeriod, ctx, trx); 
            if (prevPeriod != null)
              qtyCalculated = qtyCalculated.add(getQtyPlannedReceipt(prevPeriod.getStartDate(), productID, runID, trx.getTrxName())); 
          } 
          qtyExpected = qtyCalculated;
        } 
        qtyCalculated = qtyCalculated.subtract(qtyDemand);
        if (qtyCalculated.compareTo(Env.ZERO) == -1) {
          qtyToBeOrdered = qtyCalculated.negate();
          qtyCalculated = Env.ZERO;
        } 
        ArrayList<Object> params1 = new ArrayList();
        params1.add(qtyCalculated);
        params1.add(qtyExpected);
        params1.add(X_MRP_PlannedAvailability.AVAILABILITYSTATUS_Updated);
        params1.add(Integer.valueOf(runID));
        params1.add(Integer.valueOf(periodID));
        params1.add(Integer.valueOf(productID));
       // int linesUpdated = DB.executeUpdate(trx, updateSQL, params1.toArray());
        int linesUpdated = DB.executeUpdateEx(updateSQL, params1.toArray(), trx.getTrxName());
        s_log.finest("Planned Availability updated = " + linesUpdated);
      } else {
        s_log.severe("No Planned Availability Found");
      } 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, "MRP_PlannedAvailability - " + e.getMessage(), e);
    } 
    finally
	{
		DB.close(rs, pstmt);
		rs = null; pstmt = null;
	}
    trx.commit();
    return qtyToBeOrdered;
  }
  
  private static String getPeriodIdsLessThanEqualTo(int periodID, int planID, String trx) {
    CPreparedStatement cPreparedStatement = null;
    StringBuilder str = new StringBuilder();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String selectSql = "SELECT C_Period_ID FROM C_Period WHERE C_Period.C_Year_ID IN  (SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ? ) AND (C_Period.C_Year_ID * 1000) + C_Period.PeriodNo <= (  SELECT (per1.C_Year_ID * 1000) + per1.PeriodNo FROM C_Period per1 WHERE per1.C_Period_ID = ? ) ";
    try {
      cPreparedStatement = DB.prepareStatement(selectSql, trx);
      cPreparedStatement.setInt(1, planID);
      cPreparedStatement.setInt(2, periodID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        str.append(rs.getInt(1));
        str.append(" , ");
      } 
      str.deleteCharAt(str.lastIndexOf(","));
    } catch (Exception e) {
      s_log.log(Level.SEVERE, "Period Ids - " + e.getMessage(), e);
    }
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
    return str.toString();
  }
  
  private static BigDecimal getQtyPlannedReceipt(Timestamp dateRequired, int productID, int runID, String trx) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT QtyExpected FROM MRP_PlannedReceipt WHERE MRP_PlanRun_ID = ? AND M_Product_ID = ? AND TRUNC(DateExpected,'DD') = ? ";
    BigDecimal qtyPlannedReceipt = Env.ZERO;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, runID);
      cPreparedStatement.setInt(2, productID);
      cPreparedStatement.setTimestamp(3, TimeUtil.getDay(dateRequired));
       rs = cPreparedStatement.executeQuery();
      if (rs.next())
        qtyPlannedReceipt = rs.getBigDecimal(1); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
//    try {
//      if (cPreparedStatement != null)
//        cPreparedStatement.close(); 
//      cPreparedStatement = null;
//    } catch (Exception e) {
//      cPreparedStatement = null;
//    } 
    return qtyPlannedReceipt;
  }
  
  public static void setHigherOfSalesVsDemand(MMRPPlannedDemand plannedDemand, Properties ctx, String trx) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT Sum(NVL(QtyExpected,0)) FROM  MRP_Order_Audit  WHERE isSOTrx='Y' AND M_Product_ID = ? AND MRP_PlanRun_ID = ? AND TRUNC(DateExpected, 'DD') BETWEEN ? AND ? ";
    MPeriod period = MPeriod.get(ctx, plannedDemand.getC_Period_ID());
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, plannedDemand.getM_Product_ID());
      cPreparedStatement.setInt(2, plannedDemand.getMRP_PlanRun_ID());
      cPreparedStatement.setTimestamp(3, TimeUtil.getDay(period.getStartDate()));
      cPreparedStatement.setTimestamp(4, TimeUtil.getDay(period.getEndDate()));
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        if (plannedDemand.getQtyRequired().compareTo(BigDecimal.valueOf(rs.getDouble(1))) == -1)
          plannedDemand.setQtyRequired(rs.getBigDecimal(1)); 
      } 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, "Period Ids - " + e.getMessage(), e);
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
  }
}

