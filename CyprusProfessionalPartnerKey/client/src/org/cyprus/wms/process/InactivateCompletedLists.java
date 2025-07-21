package org.cyprus.wms.process;


//import com.cyprusbrs.client.SysEnv;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;

public class InactivateCompletedLists extends SvrProcess {
  private int p_M_Warehouse_ID = 0;
  
  private int p_CreatedBy = 0;
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.fine("M_Warehouse_ID = " + this.p_M_Warehouse_ID);
    this.log.fine("CreatedBy = " + this.p_CreatedBy);
    ArrayList<Integer> params = new ArrayList<Integer>();
    params.add(Integer.valueOf(getAD_Client_ID()));
    params.add(Integer.valueOf(this.p_M_Warehouse_ID));
    if (this.p_CreatedBy > 0)
      params.add(Integer.valueOf(this.p_CreatedBy)); 
    String SQL = "UPDATE M_TaskListLine tll SET IsActive = 'N' WHERE AD_Client_ID=? AND IsActive = 'Y' AND EXISTS (SELECT 1 FROM M_TaskList tl WHERE tll.M_TaskList_ID=tl.M_TaskList_ID AND tl.M_Warehouse_ID=? ) AND M_WarehouseTask_ID NOT IN  (SELECT M_WarehouseTask_ID FROM M_WarehouseTask wt  WHERE tll.M_WarehouseTask_ID=wt.M_WarehouseTask_ID  AND wt.DocStatus NOT IN ('CO','CL','VO','RE') )";
    if (this.p_CreatedBy > 0)
      SQL = SQL + " AND tll.CreatedBy = ? "; 
  //  int noTLL = DB.executeUpdate(get_TrxName(), SQL, params.toArray());
    int noTLL =DB.executeUpdateEx(SQL, params.toArray(), get_TrxName());
    this.log.fine("Updated task list lines # =" + noTLL);
    SQL = "UPDATE M_TaskList tl SET IsActive = 'N' WHERE AD_Client_ID=? AND M_Warehouse_ID=? AND IsActive = 'Y' AND NOT EXISTS  (SELECT 1 FROM M_TaskListLine tll  WHERE tl.M_TaskList_ID=tll.M_TaskList_ID  AND tll.IsActive='Y') ";
    if (this.p_CreatedBy > 0)
      SQL = SQL + " AND tl.CreatedBy = ? "; 
    //int noTL = DB.executeUpdate(get_TrxName(), SQL, params.toArray());
    int noTL =DB.executeUpdateEx(SQL, params.toArray(), get_TrxName());
    this.log.fine("Updated task lists # =" + noTL);
    SQL = "UPDATE C_Wave w SET IsActive = 'N' WHERE AD_Client_ID=? AND IsActive = 'Y' AND M_Warehouse_ID=? ";
    if (this.p_CreatedBy > 0)
      SQL = SQL + " AND w.CreatedBy = ? "; 
  //  int noW = DB.executeUpdate(get_TrxName(), SQL, params.toArray());
    int noW =DB.executeUpdateEx(SQL, params.toArray(), get_TrxName());
    this.log.fine("Updated waves # =" + noW);
    return "Cleaned up " + noTL + " Task Lists, and " + noW + " Waves ";
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("CreatedBy")) {
          this.p_CreatedBy = element.getParameterAsInt();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
}

