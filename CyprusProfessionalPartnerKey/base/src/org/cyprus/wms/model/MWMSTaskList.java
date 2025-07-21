package org.cyprus.wms.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

//import org.cyprusbrs.common.cyprusbrsStateException;
//import org.cyprusbrs.model.X_M_TaskList;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSTaskList extends X_WMS_TaskList {
  private static final CLogger log = CLogger.getCLogger(MWMSTaskList.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSTaskList(Properties ctx, int M_TaskList_ID, String trx) {
    super(ctx, M_TaskList_ID, trx);
  }
  
  public MWMSTaskList(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSTaskList(Properties ctx, int M_Warehouse_ID, int C_DocType_ID, String pickMethod, String trx) {
    this(ctx, 0, trx);
    setM_Warehouse_ID(M_Warehouse_ID);
    setC_DocType_ID(C_DocType_ID);
    setPickMethod(pickMethod);
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
  
  public boolean setDocStatus() {
   // int count = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM M_TaskList tl INNER JOIN M_TaskListLine tll ON (tll.M_TaskList_ID=tl.M_TaskList_ID) INNER JOIN M_WarehouseTask t ON (tll.M_WarehouseTask_ID=t.M_WarehouseTask_ID) WHERE tl.M_TaskList_ID=? AND t.DocStatus NOT IN ('VO','CO','RE','CL')", new Object[] { Integer.valueOf(getWMS_TaskList_ID()) });
	  int count =  DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM WMS_TaskList tl INNER JOIN WMS_TaskListLine tll ON (tll.WMS_TaskList_ID=tl.WMS_TaskList_ID) INNER JOIN WMS_WarehouseTask t ON (tll.WMS_WarehouseTask_ID=t.WMS_WarehouseTask_ID) WHERE tl.WMS_TaskList_ID=? AND t.DocStatus NOT IN ('VO','CO','RE','CL')", new Object[] { Integer.valueOf(getWMS_TaskList_ID()) });
	  if (count > 0) {
      setDocStatus(X_WMS_TaskList.DOCSTATUS_InProgress);
      return true;
    } 
   // count = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM M_TaskList tl INNER JOIN M_TaskListLine tll ON (tll.M_TaskList_ID=tl.M_TaskList_ID) INNER JOIN M_WarehouseTask t ON (tll.M_WarehouseTask_ID=t.M_WarehouseTask_ID) WHERE tl.M_TaskList_ID=? AND t.DocStatus NOT IN ('VO','RE')", new Object[] { Integer.valueOf(getWMS_TaskList_ID()) });
	  count = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM WMS_TaskList tl INNER JOIN WMS_TaskListLine tll ON (tll.WMS_TaskList_ID=tl.WMS_TaskList_ID) INNER JOIN WMS_WarehouseTask t ON (tll.WMS_WarehouseTask_ID=t.WMS_WarehouseTask_ID) WHERE tl.WMS_TaskList_ID=? AND t.DocStatus NOT IN ('VO','RE')", new Object[] { Integer.valueOf(getWMS_TaskList_ID()) });
	  if (count <= 0) {
      setDocStatus(X_WMS_TaskList.DOCSTATUS_Voided);
      setDateCompleted(new Timestamp(System.currentTimeMillis()));
      return true;
    } 
    setDocStatus(X_WMS_TaskList.DOCSTATUS_Completed);
    setDateCompleted(new Timestamp(System.currentTimeMillis()));
    return true;
  }
  
  public ArrayList<MWMSWarehouseTask> getTasks() throws Exception {
    CPreparedStatement cPreparedStatement=null;
    ArrayList<MWMSWarehouseTask> tasks = new ArrayList<MWMSWarehouseTask>();
   // String sql = "SELECT * FROM M_WarehouseTask wt WHERE EXISTS  (SELECT 1 FROM M_TaskListLine tll WHERE tll.M_TaskList_ID = ? AND tll.M_WarehouseTask_ID = wt.M_WarehouseTask_ID) ";
    String sql = "SELECT * FROM WMS_WarehouseTask wt WHERE EXISTS  (SELECT 1 FROM WMS_TaskListLine tll WHERE tll.WMS_TaskList_ID = 1000014 AND tll.WMS_WarehouseTask_ID = wt.WMS_WarehouseTask_ID) ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, getWMS_TaskList_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MWMSWarehouseTask wt = new MWMSWarehouseTask(getCtx(), rs, get_TrxName());
        tasks.add(wt);
      } 
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return tasks;
  }
}

