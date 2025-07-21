package org.cyprus.wms.process;


//import com.cyprusbrs.client.SysEnv;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

public class TaskHandlerPerformance extends SvrProcess {
	
	Trx trxname;
  private int p_M_Warehouse_ID = 0;
  
  private int p_AD_User_ID = 0;
  
  private Timestamp p_Date_From = null;
  
  private Timestamp p_Date_To = null;
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.info("p_M_Warehouse_ID=" + this.p_M_Warehouse_ID + "p_AD_User_ID=" + this.p_AD_User_ID + "p_Date_From=" + this.p_Date_From + "p_Date_To=" + this.p_Date_To);
    ArrayList<Object> list = new ArrayList();
    String insertSQL = "INSERT INTO T_TaskHandlerPerf (AD_PInstance_ID, AD_Client_ID, AD_Org_ID, M_Warehouse_ID,  AD_User_ID, IsActive, MovementDate,  TrolleysPicked, OrderLinesPicked, OrdersPicked,  LinesPutaway, LinesReplenished)  SELECT ?, wt.AD_Client_ID, wt.AD_Org_ID, wt.M_Warehouse_ID,  wt.AD_User_ID, 'Y', wt.MovementDate,  COUNT(distinct(mtll.M_TaskList_ID)),  COUNT(DISTINCT(COALESCE(CASE WHEN wt.C_Orderline_ID IS NOT NULL THEN TO_CHAR(wt.C_OrderLine_ID)||'SOO' ELSE NULL END,  CASE WHEN wt.M_WorkOrderComponent_ID IS NOT NULL THEN TO_CHAR(wt.M_WorkOrderComponent_ID)||'WOO' ELSE NULL END ))),  COUNT(DISTINCT(COALESCE(CASE WHEN wt.C_OrderLine_ID IS NOT NULL THEN TO_CHAR(col.C_Order_ID)||'SOO' ELSE NULL END, CASE WHEN wt.M_WorkOrderComponent_ID IS NOT NULL THEN TO_CHAR(woo.M_WorkOrder_ID)||'WOO' ELSE NULL END))),  SUM(CASE WHEN dt.DocBaseType = 'PUT'  THEN 1 ELSE 0 END), SUM(CASE WHEN dt.DocBaseType = 'RPL'  THEN 1 ELSE 0 END)  FROM  M_WarehouseTask wt  INNER JOIN C_DocType dt ON (wt.C_DocType_ID=dt.C_DocType_ID)  INNER JOIN M_TASKLISTLINE mtll ON (wt.M_WarehouseTask_ID = mtll.M_WarehouseTask_ID)  LEFT OUTER JOIN c_orderline col ON (col.c_orderline_id=wt.c_orderline_id)  LEFT OUTER JOIN M_WorkOrderComponent woc ON (woc.M_WorkOrderComponent_ID=wt.M_WorkOrderComponent_ID)  LEFT OUTER JOIN M_WorkOrderOperation woo ON (woo.M_WorkOrderOperation_ID=woc.M_WorkOrderOperation_ID)  WHERE  wt.M_Warehouse_ID = ? ";
    list.add(Integer.valueOf(getAD_PInstance_ID()));
    list.add(Integer.valueOf(this.p_M_Warehouse_ID));
    if (this.p_Date_From != null) {
      insertSQL = insertSQL + " AND TRUNC(wt.MovementDate,'DD') >= ? ";
      list.add(this.p_Date_From);
    } 
    if (this.p_Date_To != null) {
      insertSQL = insertSQL + " AND TRUNC(wt.MovementDate,'DD') <= ? ";
      list.add(this.p_Date_To);
    } 
    if (this.p_AD_User_ID != 0) {
      insertSQL = insertSQL + " AND wt.AD_User_ID= ? ";
      list.add(Integer.valueOf(this.p_AD_User_ID));
    } 
    insertSQL = insertSQL + " GROUP BY wt.AD_Client_ID, wt.AD_Org_ID, wt.M_Warehouse_ID, wt.AD_User_ID, wt.MovementDate ";
    int no = DB.executeUpdate(trxname, insertSQL, list);
    this.log.finest(insertSQL);
    this.log.fine("Inserted #" + no);
    if (no <= 0)
      return "No records created "; 
    return "Succesfully generated Task Handler Performance Report";
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null || element.getParameter_To() != null)
        if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("AD_User_ID")) {
          this.p_AD_User_ID = element.getParameterAsInt();
        } else if (name.equals("MovementDate")) {
          this.p_Date_From = (Timestamp)element.getParameter();
          this.p_Date_To = (Timestamp)element.getParameter_To();
        } else if (!name.equals("#AD_PrintFormat_ID")) {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
}
