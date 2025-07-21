package org.cyprus.mrp.model;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
//import org.cyprusbrs.model.X_MRP_PlannedDemand;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.TimeUtil;

public class MMRPPlannedDemand extends X_MRP_PlannedDemand {
  private static final CLogger log = CLogger.getCLogger(MMRPPlannedDemand.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPPlannedDemand.class);
  
  public MMRPPlannedDemand(Properties ctx, int MRP_PlannedDemand_ID, String trx) {
    super(ctx, MRP_PlannedDemand_ID, trx);
  }
  
  public MMRPPlannedDemand(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public void setDateRequired(MMRPPlannedDemand treeMRPPlannedDemand, MProduct compProduct) {
    setDateRequired(treeMRPPlannedDemand.getDateRequired());
  }
  
  public static MMRPPlannedDemand[] getTreeChildrenPlannedDemand(MMRPPlannedDemand plannedTreeDemand, int planRunID, String trx, Properties ctx) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT * FROM MRP_PlannedDemand WHERE MRP_PlanRun_ID =?AND MRP_PlannedDemand_Parent_ID = ?";
    ArrayList<MMRPPlannedDemand> list = new ArrayList<MMRPPlannedDemand>();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, planRunID);
      cPreparedStatement.setInt(2, plannedTreeDemand.getMRP_PlannedDemand_ID());
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMRPPlannedDemand(ctx, rs, trx)); 
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
    MMRPPlannedDemand[] retValue = new MMRPPlannedDemand[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  public static ArrayList<Integer> getAllExplodeChildrenPlannedDemand(int parentDemandID, int planRunID, Properties ctx, String trx) {
    CPreparedStatement cPreparedStatement = null;
    String selectSql = "SELECT MRP_PlannedDemand_ID FROM MRP_PlannedDemand WHERE MRP_PlanRun_ID = ? AND MRP_PlannedDemand_Parent_ID = ?";
    ArrayList<Integer> list = new ArrayList<Integer>();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(selectSql, trx);
      cPreparedStatement.setInt(1, planRunID);
      cPreparedStatement.setInt(2, parentDemandID);
       rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        list.addAll(getAllExplodeChildrenPlannedDemand(rs.getInt(1), planRunID, ctx, trx));
        list.add(Integer.valueOf(rs.getInt(1)));
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, selectSql, e);
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
    return list;
  }
  
  public static int setAllExplodeChildrenPlannedDemand(ArrayList<Integer> list, String trx, Properties ctx, BigDecimal qty) {
    if (list.size() == 0)
      return 0; 
    StringBuilder updateSQL = new StringBuilder("UPDATE MRP_PlannedDemand SET QtyRequired = ? ,  RunStatus = ? WHERE MRP_PlannedDemand_ID IN ( ");
    for (Integer integer : list) {
      updateSQL.append(integer);
      updateSQL.append(" , ");
    } 
    updateSQL.deleteCharAt(updateSQL.lastIndexOf(","));
    updateSQL.append(")");
    ArrayList<Object> params = new ArrayList();
    params.add(qty);
    params.add(X_MRP_PlannedDemand.RUNSTATUS_CompletedRunning);
    System.out.println(updateSQL.toString());
  //  int linesUpdated = DB.executeUpdate(trx, updateSQL.toString(), params.toArray());
    int linesUpdated =  DB.executeUpdateEx(updateSQL.toString(), params.toArray(), trx);
    return linesUpdated;
  }
  
  public static int getTotalRecords(MMRPPlanRun planRun, int level, Timestamp dateRequired) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT COUNT(*) FROM MRP_PlannedDemand WHERE LevelNo = ? AND MRP_PlanRun_ID = ? AND DateRequired = ? ";
    int count = 0;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, planRun.get_TrxName());
      cPreparedStatement.setInt(1, level);
      cPreparedStatement.setInt(2, planRun.getMRP_PlanRun_ID());
      cPreparedStatement.setTimestamp(3, TimeUtil.getDay(dateRequired));
       rs = cPreparedStatement.executeQuery();
      if (rs.next())
        count = rs.getInt(1); 
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
   
    return count;
  }
  
  public static MMRPPlannedDemand[] getUnProcessedPlannedDemand(int planRunID, int level, String trx, Properties ctx, Timestamp dateRequired) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT * FROM MRP_PlannedDemand WHERE LevelNo = ? AND MRP_PlanRun_ID =? AND RunStatus = ? AND DateRequired = ? ";
    ArrayList<MMRPPlannedDemand> list = new ArrayList<MMRPPlannedDemand>();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, level);
      cPreparedStatement.setInt(2, planRunID);
      cPreparedStatement.setString(3, X_MRP_PlannedDemand.RUNSTATUS_NotRunning);
      cPreparedStatement.setTimestamp(4, TimeUtil.getDay(dateRequired));
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMRPPlannedDemand(ctx, rs, trx)); 
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
    MMRPPlannedDemand[] retValue = new MMRPPlannedDemand[list.size()];
    list.toArray(retValue);
    return retValue;
  }
}

