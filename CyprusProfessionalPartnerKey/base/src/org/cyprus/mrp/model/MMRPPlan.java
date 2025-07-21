package org.cyprus.mrp.model;

//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MWarehouse;
//import org.cyprusbrs.model.X_MRP_Plan;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.TimeUtil;

public class MMRPPlan extends X_MRP_Plan {
  private static final CLogger log = CLogger.getCLogger(MMRPPlan.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static CLogger s_log = CLogger.getCLogger(MMRPPlan.class);
  
  public MMRPPlan(Properties ctx, int MRP_Plan_ID, String trx) {
    super(ctx, MRP_Plan_ID, trx);
  }
  
  public MMRPPlan(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (newRecord) {
      CPreparedStatement cPreparedStatement = null;
      String sql = "SELECT per2.EndDate , per1.StartDate  FROM C_Period per1, C_Period per2 WHERE per1.C_Period_ID = ? AND per2.C_Period_ID = ?";
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      int numDays = 0;
      Timestamp endDate = null;
      Timestamp startDate = null;
      try {
        cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
        cPreparedStatement.setInt(1, getC_Period_From_ID());
        cPreparedStatement.setInt(2, getC_Period_To_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          endDate = rs.getTimestamp(1);
          startDate = rs.getTimestamp(2);
          numDays = TimeUtil.getDaysBetween(startDate, endDate);
        } 
        rs.close();
        cPreparedStatement.close();
        cPreparedStatement = null;
      } catch (Exception e) {
        s_log.log(Level.SEVERE, sql, e);
      } finally {
        if (rs != null)
          try {
            rs.close();
          } catch (SQLException e) {
            s_log.log(Level.SEVERE, "Finish", e);
          }  
        rs = null;
        if (cPreparedStatement != null)
          try {
            cPreparedStatement.close();
          } catch (SQLException e) {
            s_log.log(Level.SEVERE, "Finish", e);
          }  
        cPreparedStatement = null;
      } 
      if (numDays > 366) {
        log.saveError("Error", Msg.parseTranslation(getCtx(), "@C_Period_ID@ @EndDate@ - @StartDate@ > 366"));
        return false;
      } 
    } 
    if (isActive()) {
      String sql = "SELECT MRP_Plan_ID from MRP_Plan WHERE M_Warehouse_ID = ? AND IsActive='Y'";
      int plan = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getM_Warehouse_ID()) });
      if (plan != -1 && plan != getMRP_Plan_ID()) {
        log.saveError("Error", Msg.translate(getCtx(), "PlanExists"));
        return false;
      } 
    } 
//    SysEnv se = SysEnv.get("CMFG");
//    if (se != null && se.checkLicense()) 
//    {
      if (getMFG_WorkOrderClass_ID() == 0) {
        log.saveError("Error", Msg.parseTranslation(getCtx(), "@FillMandatory@ @M_WorkOrderClass_ID@"));
        return false;
      } 
      if (getM_Locator_ID() == 0) {
        log.saveError("Error", Msg.parseTranslation(getCtx(), "@FillMandatory@ @M_Locator_ID@"));
        return false;
      } 
 //   } 
    if (getPriorityImplementation().equalsIgnoreCase(X_MRP_Plan.PRIORITYIMPLEMENTATION_Manufacture)) {
     // se = SysEnv.get("CMFG");
//      if (se == null || !se.checkLicense()) {
        setPriorityImplementation(X_MRP_Plan.PRIORITYIMPLEMENTATION_Purchase);
        log.warning("MFG Not installed, Implementation prioirty is set to Purchase");
    //  } 
    } 
    return true;
  }
  
  protected boolean beforeDelete() {
    int result = 0;
    String sql = "SELECT COUNT(*)FROM MRP_Forecast WHERE MRP_Plan_ID=? ";
    result = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getMRP_Plan_ID()) });
    if (result == 0) {
      sql = "SELECT COUNT(*) FROM MRP_MasterDemand WHERE MRP_Plan_ID=? ";
      result = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getMRP_Plan_ID()) });
    } 
    if (result > 0) {
      log.saveError(Msg.translate(getCtx(), "CannotDeletePlan"), "");
      return false;
    } 
    return true;
  }
  
 // @UICallout
  public void setM_Warehouse_ID(String oldM_Warehouse_ID, String newM_Warehouse_ID, int windowNo) throws Exception {
    if (newM_Warehouse_ID == null || newM_Warehouse_ID.length() == 0) {
      set_ValueNoCheck("M_Locator_ID", null);
      return;
    } 
    int M_Warehouse_ID = Integer.parseInt(newM_Warehouse_ID);
    if (M_Warehouse_ID == 0) {
      set_ValueNoCheck("M_Locator_ID", null);
      return;
    } 
    MWarehouse warehouse = MWarehouse.get(Env.getCtx(), M_Warehouse_ID);
    setM_Locator_ID(warehouse.getDefaultLocator().getM_Locator_ID());
  }
}

