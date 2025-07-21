package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.sql.ResultSet;
import java.util.Properties;

//import org.cyprusbrs.model.X_M_TaskListLine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;

public class MWMSTaskListLine extends X_WMS_TaskListLine {
  private static final CLogger log = CLogger.getCLogger(MWMSTaskListLine.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSTaskListLine(Properties ctx, int M_TaskListLine_ID, String trx) {
    super(ctx, M_TaskListLine_ID, trx);
  }
  
  public MWMSTaskListLine(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSTaskListLine(Properties ctx, int M_TaskList_ID, int M_WarehouseTask_ID, int SlotNo, String trx) {
    this(ctx, 0, trx);
    setSlotNo(SlotNo);
    setWMS_TaskList_ID(M_TaskList_ID);
    setWMS_WarehouseTask_ID(M_WarehouseTask_ID);
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (getLine() == 0) {
   //   String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM M_TaskListLine WHERE M_TaskList_ID=?";
      String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM WMS_TaskListLine WHERE WMS_TaskList_ID=?";
      int ii = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getWMS_TaskList_ID()) });
      setLine(ii);
    } 
    return true;
  }
}

