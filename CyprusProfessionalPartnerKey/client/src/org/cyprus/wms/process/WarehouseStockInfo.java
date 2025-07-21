package org.cyprus.wms.process;

//import com.cyprusbrs.client.SysEnv;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

public class WarehouseStockInfo extends SvrProcess {
	
	Trx trxname;
  private int p_M_Warehouse_ID = 0;
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.info("p_M_Warehouse_ID=" + this.p_M_Warehouse_ID);
    ArrayList<Object> list = new ArrayList();
    String insertSQL = "INSERT INTO T_WarehouseStock (AD_PInstance_ID, AD_Client_ID, AD_Org_ID, M_Warehouse_ID,  IsActive, M_Product_ID, C_Uom_ID, QtyOnhand, QtyAllocated, QtyAvailable,  ConsumedPastSevenDays, ConsumedPastThirtyDays, DaysCover)  SELECT ?, ms.AD_Client_ID, ms.AD_Org_ID, l.M_Warehouse_ID,  'Y', ms.M_Product_Id, p.C_Uom_ID, sum(ms.QtyOnhand), sum(ms.QtyAllocated), sum(ms.QtyOnhand-ms.QtyReserved), (SELECT abs(nvl(sum(rs.MOVEMENTQTY),0)) FROM RV_M_Transaction_Sum rs  WHERE rs.M_Product_ID = ms.M_Product_ID AND rs.MovementQty < 0 AND rs.M_Warehouse_ID=l.M_Warehouse_ID  AND rs.MovementType <> 'M-' and TRUNC(rs.MovementDate,'DD') > sysdate -7),  (SELECT abs(nvl(sum(rs.MOVEMENTQTY),0)) FROM RV_M_Transaction_Sum rs  WHERE rs.M_Product_ID = ms.M_Product_ID AND rs.MovementQty < 0 AND rs.M_Warehouse_ID=l.M_Warehouse_ID  AND rs.MovementType <> 'M-' and TRUNC(rs.MovementDate,'DD') > sysdate -30), null  FROM  M_Storage_V ms  INNER JOIN M_Product p on (ms.M_Product_ID = p.M_Product_ID)  INNER JOIN M_Locator l ON (ms.M_Locator_ID=l.M_Locator_ID)  WHERE  l.M_Warehouse_ID = ?  GROUP BY ms.AD_Client_ID, ms.AD_Org_ID, l.M_Warehouse_ID, ms.M_Product_Id, p.C_Uom_ID ";
    list.add(Integer.valueOf(getAD_PInstance_ID()));
    list.add(Integer.valueOf(this.p_M_Warehouse_ID));
    int no = DB.executeUpdate(trxname, insertSQL, list);
    this.log.finest(insertSQL);
    this.log.fine("Inserted #" + no);
    String updateSQL = " UPDATE T_WarehouseStock set DaysCover =  (CASE WHEN ConsumedPastThirtydays=0 THEN 99999 ELSE abs(QtyOnhand*30/ConsumedPastThirtydays) END)  WHERE AD_PInstance_ID = ? ";
    list.clear();
    list.add(Integer.valueOf(getAD_PInstance_ID()));
    int updateno = DB.executeUpdate(trxname,updateSQL, list);
    this.log.finest(insertSQL);
    this.log.fine("Updated #" + updateno);
    if (no <= 0 || updateno <= 0)
      return "No records created "; 
    return "Succesfully generated Warehouse Stock Report";
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (element.getParameter() != null || element.getParameter_To() != null)
          if (name.equals("M_Warehouse_ID")) {
            this.p_M_Warehouse_ID = element.getParameterAsInt();
          } else if (!name.equals("#AD_PrintFormat_ID")) {
            this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
          }   
    } 
  }
}

