package org.cyprus.wms.process;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

public class LocatorVisits extends SvrProcess {
	
	Trx trxname;
  private int p_M_Warehouse_ID = 0;
  
  private int p_M_Zone_ID = 0;
  
  private Timestamp p_Date_From = null;
  
  private Timestamp p_Date_To = null;
  
  protected String doIt() throws Exception {
    this.log.info("p_M_Warehouse_ID=" + this.p_M_Warehouse_ID + "p_M_Zone_ID=" + this.p_M_Zone_ID + "p_Date_From=" + this.p_Date_From + "p_Date_To=" + this.p_Date_To);
    ArrayList<Object> list = new ArrayList();
    String insertSQL = "INSERT INTO T_LocatorVisits (AD_PInstance_ID, AD_Client_ID, AD_Org_ID, M_Warehouse_ID,  M_Locator_ID, M_Product_ID, MovementDate,  PickVisits, PutawayVisits, ReplenishmentVisits, TotalVisits)  SELECT ?, AD_Client_ID, AD_Org_ID,  M_Warehouse_ID, M_Locator_ID, M_Product_ID, MIN(MovementDate), SUM(PickVisits) AS PickVisits,  SUM(PutawayVisits) AS PutawayVisits,  SUM(ReplenishmentVisits) AS ReplenishmentVisits,  COUNT(*) AS TotalVisits  FROM RV_LocatorVisits rlv  WHERE M_Warehouse_ID = ? ";
    list.add(Integer.valueOf(getAD_PInstance_ID()));
    list.add(Integer.valueOf(this.p_M_Warehouse_ID));
    if (this.p_M_Zone_ID != 0) {
      insertSQL = insertSQL + " AND rlv.M_Zone_ID=? ";
      list.add(Integer.valueOf(this.p_M_Zone_ID));
    } 
    if (this.p_Date_From != null) {
      insertSQL = insertSQL + " AND TRUNC(rlv.MovementDate,'DD') >= ? ";
      list.add(this.p_Date_From);
    } 
    if (this.p_Date_To != null) {
      insertSQL = insertSQL + " AND TRUNC(rlv.MovementDate,'DD') <= ? ";
      list.add(this.p_Date_To);
    } 
    insertSQL = insertSQL + " GROUP BY AD_Client_ID, AD_Org_ID,  M_Warehouse_ID, M_Locator_ID, M_Product_ID ";
    int no = DB.executeUpdate( trxname,insertSQL, list);
  
    this.log.finest(insertSQL);
    this.log.fine("Inserted #" + no);
    return "@OK@";
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null || element.getParameter_To() != null)
        if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("M_Zone_ID")) {
          this.p_M_Zone_ID = element.getParameterAsInt();
        } else if (name.equals("MovementDate")) {
          this.p_Date_From = (Timestamp)element.getParameter();
          this.p_Date_To = (Timestamp)element.getParameter_To();
        } else if (!name.equals("#AD_PrintFormat_ID")) {
          if (!name.equals("TotalVisits"))
            this.log.log(Level.SEVERE, "Unknown Parameter: " + name); 
        }  
    } 
  }
}
