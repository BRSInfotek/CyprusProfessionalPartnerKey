package org.cyprus.wms.process;


//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

import org.cyprus.wms.model.MWMSTaskList;
import org.cyprus.wms.util.MWMSMContext;
//import org.cyprus.wms.util.MMContext;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.print.ReportCtl;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class GenerateReplenishment extends SvrProcess {
  private int p_M_Warehouse_ID = 0;
  
  private int p_M_Zone_ID = 0;
  
  private int p_C_DocTypeTask_ID = 0;
  
  private String p_docAction = "CO";
  
  private boolean p_IsPrintReplenishmentList = false;
  
  private MWMSMContext m_mmContext = null;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("WMS_Zone_ID")) {
          this.p_M_Zone_ID = element.getParameterAsInt();
        } else if (name.equals("C_DocTypeTask_ID")) {
          this.p_C_DocTypeTask_ID = element.getParameterAsInt();
        } else if (name.equals("DocAction")) {
          this.p_docAction = (String)element.getParameter();
        } else if (name.equals("IsPrintReplenishmentList")) {
          this.p_IsPrintReplenishmentList = element.getParameter().equals("Y");
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    if (this.m_mmContext == null)
      this.m_mmContext = new MWMSMContext(); 
  }
  
  protected String doIt() throws Exception {
    CPreparedStatement cPreparedStatement1 = null, cPreparedStatement2 = null, cPreparedStatement3 = null;
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.info("p_M_Warehouse_ID=" + this.p_M_Warehouse_ID + " p_M_Zone_ID=" + this.p_M_Zone_ID + " p_C_DocType_ID=" + this.p_C_DocTypeTask_ID + " p_docAction=" + this.p_docAction + "p_IsPrintReplenishmentList=" + this.p_IsPrintReplenishmentList);
    if (this.p_M_Warehouse_ID == 0)
      throw new Exception("@WarehouseNotSpecified@"); 
    MWarehouse wh = MWarehouse.get(getCtx(), this.p_M_Warehouse_ID);
    if (wh == null || !wh.isWMSEnabled())
      throw new Exception("@WarehouseNotWMSEnabled@"); 
 //   String d_sql = " select wms_zone_id, minquantity, maxquantity, stocking_uom_id, c_uom_id,  M_PRODUCT_ID, m_locator_id, REPLENISHMENTSEQNO  from \t( select mz.wms_zone_id, mpl.minquantity, mpl.maxquantity, l.STOCKING_UOM_ID,  P.C_UOM_ID,  mpl.M_PRODUCT_ID, mpl.m_locator_id, mz.REPLENISHMENTSEQNO, l.PUTAWAYSEQNO  from  wms_zone mz  INNER JOIN wms_zonelocator mzl ON (mz.wms_zone_id = mzl.wms_ZONE_ID)  INNER JOIN m_locator l ON (mzl.M_LOCATOR_ID = l.M_LOCATOR_ID )  INNER JOIN  m_productlocator mpl ON (mpl.m_locator_id = l.m_locator_id)  INNER JOIN m_product p ON (mpl.m_product_id=p.m_product_id)  where  mz.M_WAREHOUSE_ID = ?  and mz.wms_ZONE_ID = nvl(?,mz.wms_zone_id)  and mz.ISSTATIC = 'Y'  and not exists  (select s.m_locator_id from M_STORAGE_V s  where s.m_locator_id = l.m_locator_id and s.m_product_id = mpl.M_PRODUCT_ID)  union all  select mz.wms_zone_id, mpl.minquantity, mpl.maxquantity, l.STOCKING_UOM_ID,  P.C_UOM_ID, mpl.M_PRODUCT_ID, mpl.m_locator_id, mz.REPLENISHMENTSEQNO, l.PUTAWAYSEQNO  from  wms_zone mz  INNER JOIN wms_zonelocator mzl ON (mz.wms_zone_id = mzl.wms_ZONE_ID)  INNER JOIN m_locator l ON (mzl.M_LOCATOR_ID = l.M_LOCATOR_ID )  INNER JOIN  m_productlocator mpl ON (mpl.m_locator_id = l.m_locator_id)  INNER JOIN m_product p ON (mpl.m_product_id=p.m_product_id)  where  mz.M_WAREHOUSE_ID = ?  and mz.wms_ZONE_ID = nvl(?,mz.wms_zone_id)  and mz.ISSTATIC = 'Y'  and convert_uom(nvl(mpl.minquantity,0),l.STOCKING_UOM_ID,p.c_uom_id,p.m_product_id) >  ( select sum( nvl(s.QtyOnHand,0) + nvl(s.QtyExpected,0) + nvl(s.QtyOrdered,0))  from M_STORAGE_V s  where s.m_locator_id = l.m_locator_id and s.m_product_id = mpl.M_PRODUCT_ID)  )  order by REPLENISHMENTSEQNO, PUTAWAYSEQNO ";
    String d_sql = " select wms_zone_id, minquantity, maxquantity, stocking_uom_id, c_uom_id,  M_PRODUCT_ID, m_locator_id, REPLENISHMENTSEQNO  from \t( select mz.wms_zone_id, mpl.minquantity, mpl.maxquantity, l.STOCKING_UOM_ID,  P.C_UOM_ID,  mpl.M_PRODUCT_ID, mpl.m_locator_id, mz.REPLENISHMENTSEQNO, l.PUTAWAYSEQNO  from  wms_zone mz  INNER JOIN wms_zonelocator mzl ON (mz.wms_zone_id = mzl.wms_ZONE_ID)  INNER JOIN m_locator l ON (mzl.M_LOCATOR_ID = l.M_LOCATOR_ID )  INNER JOIN  m_productlocator mpl ON (mpl.m_locator_id = l.m_locator_id)  INNER JOIN m_product p ON (mpl.m_product_id=p.m_product_id)  where  mz.M_WAREHOUSE_ID = ?  and mz.wms_ZONE_ID = nvl(?,mz.wms_zone_id)  and mz.ISSTATIC = 'Y'  and not exists  (select s.m_locator_id from M_STORAGE s  where s.m_locator_id = l.m_locator_id and s.m_product_id = mpl.M_PRODUCT_ID)  union all  select mz.wms_zone_id, mpl.minquantity, mpl.maxquantity, l.STOCKING_UOM_ID,  P.C_UOM_ID, mpl.M_PRODUCT_ID, mpl.m_locator_id, mz.REPLENISHMENTSEQNO, l.PUTAWAYSEQNO  from  wms_zone mz  INNER JOIN wms_zonelocator mzl ON (mz.wms_zone_id = mzl.wms_ZONE_ID)  INNER JOIN m_locator l ON (mzl.M_LOCATOR_ID = l.M_LOCATOR_ID )  INNER JOIN  m_productlocator mpl ON (mpl.m_locator_id = l.m_locator_id)  INNER JOIN m_product p ON (mpl.m_product_id=p.m_product_id)  where  mz.M_WAREHOUSE_ID = ?  and mz.wms_ZONE_ID = nvl(?,mz.wms_zone_id)  and mz.ISSTATIC = 'Y'  and FN_CONVERT_UOM(nvl(mpl.minquantity,0),l.STOCKING_UOM_ID,p.c_uom_id,p.m_product_id) >  ( select sum( nvl(s.QtyOnHand,0) + nvl(s.QtyExpected,0) + nvl(s.QtyOrdered,0))  from M_STORAGE s  where s.m_locator_id = l.m_locator_id and s.m_product_id = mpl.M_PRODUCT_ID)  )  order by REPLENISHMENTSEQNO, PUTAWAYSEQNO ";  
    System.out.println("d_sql"+d_sql);
 
    //   String q_sql = " select convert_uom(sum(nvl(s.QtyOnHand,0) + nvl(s.QtyExpected,0) + nvl(s.QtyOrdered,0)),?,?,?)  from M_STORAGE_V s  where s.m_locator_id = ? and s.m_product_id = ? ";
    String q_sql = " select fn_convert_uom(sum(nvl(s.QtyOnHand,0) + nvl(s.QtyExpected,0) + nvl(s.QtyOrdered,0)),?,?,?)  from M_STORAGE s  where s.m_locator_id = ? and s.m_product_id = ? ";
     System.out.println("q_sql"+q_sql);
   
//     String s_sql = " select mzr.wms_zone_id, mzl.m_locator_id,  convert_uom( sum(nvl(s.QtyOnhand,0)-nvl(s.QtyDedicated,0)-nvl(s.QtyAllocated,0)),?,?,?), mzr.replenishmentseqno, l.pickingseqno  from  wms_zonerelationship mzr  INNER JOIN wms_zonelocator mzl ON (mzr.m_sourcezone_id = mzl.wms_ZONE_ID)  INNER JOIN m_storage_v s ON (s.m_locator_id = mzl.m_locator_id)  INNER JOIN m_locator l on (mzl.m_locator_id = l.M_LOCATOR_ID)  where  mzr.wms_zone_id= ?  and s.m_product_id = ?  and l.picking_uom_id = ?  group by mzr.wms_zone_id, mzl.m_locator_id, mzr.replenishmentseqno,l.pickingseqno  having sum(nvl(s.QtyOnhand,0)-nvl(s.QtyDedicated,0)-nvl(s.QtyAllocated,0)) > 0 order by mzr.replenishmentseqno,l.pickingseqno ";
     String s_sql = " select mzr.wms_zone_id, mzl.m_locator_id,  fn_convert_uom( sum(nvl(s.QtyOnhand,0)-nvl(s.QtyDedicated,0)-nvl(s.QtyAllocated,0)),?,?,?), mzr.replenishmentseqno, l.pickingseqno  from  wms_zonerelationship mzr  INNER JOIN wms_zonelocator mzl ON (mzr.m_sourcezone_id = mzl.wms_ZONE_ID)  INNER JOIN m_storage s ON (s.m_locator_id = mzl.m_locator_id)  INNER JOIN m_locator l on (mzl.m_locator_id = l.M_LOCATOR_ID)  where  mzr.wms_zone_id= ?  and s.m_product_id = ?  and l.picking_uom_id = ?  group by mzr.wms_zone_id, mzl.m_locator_id, mzr.replenishmentseqno,l.pickingseqno  having sum(nvl(s.QtyOnhand,0)-nvl(s.QtyDedicated,0)-nvl(s.QtyAllocated,0)) > 0 order by mzr.replenishmentseqno,l.pickingseqno ";
     System.out.println("s_sql"+s_sql);
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    PreparedStatement pstmt3 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;
    try {
      cPreparedStatement1 = DB.prepareStatement(d_sql, get_TrxName());
      cPreparedStatement2 = DB.prepareStatement(q_sql, get_TrxName());
      cPreparedStatement3 = DB.prepareStatement(s_sql, get_TrxName());
      int index = 1;
      cPreparedStatement1.setInt(index++, this.p_M_Warehouse_ID);
      if (this.p_M_Zone_ID != 0) {
        cPreparedStatement1.setInt(index++, this.p_M_Zone_ID);
      } else {
        cPreparedStatement1.setNull(index++, 4);
      } 
      cPreparedStatement1.setInt(index++, this.p_M_Warehouse_ID);
      if (this.p_M_Zone_ID != 0) {
        cPreparedStatement1.setInt(index++, this.p_M_Zone_ID);
      } else {
        cPreparedStatement1.setNull(index++, 4);
      } 
      rs = cPreparedStatement1.executeQuery();
      while (rs.next()) {
        int zoneId = rs.getInt(1);
        BigDecimal minquantity = rs.getBigDecimal(2);
        if (minquantity == null)
          minquantity = BigDecimal.ZERO; 
        BigDecimal maxquantity = rs.getBigDecimal(3);
        if (maxquantity == null)
          maxquantity = BigDecimal.ZERO; 
        int stockinguomId = rs.getInt(4);
        int puomId = rs.getInt(5);
        int productId = rs.getInt(6);
        int locatorId = rs.getInt(7);
        long replenishqty = 0L;
        cPreparedStatement2.setInt(1, puomId);
        cPreparedStatement2.setInt(2, stockinguomId);
        cPreparedStatement2.setInt(3, productId);
        cPreparedStatement2.setInt(4, locatorId);
        cPreparedStatement2.setInt(5, productId);
        rs2 = cPreparedStatement2.executeQuery();
        if (rs2.next()) {
          BigDecimal currentqty = rs2.getBigDecimal(1);
          if (currentqty == null)
            currentqty = BigDecimal.ZERO; 
          replenishqty = maxquantity.subtract(currentqty).longValue();
          if (replenishqty < 0L)
            replenishqty = 0L; 
        } 
        cPreparedStatement3.setInt(1, puomId);
        cPreparedStatement3.setInt(2, stockinguomId);
        cPreparedStatement3.setInt(3, productId);
        cPreparedStatement3.setInt(4, zoneId);
        cPreparedStatement3.setInt(5, productId);
        cPreparedStatement3.setInt(6, stockinguomId);
        rs3 = cPreparedStatement3.executeQuery();
        while (rs3.next() && replenishqty > 0L) {
          int slocatorId = rs3.getInt(2);
          BigDecimal scurrentqty = rs3.getBigDecimal(3);
          if (scurrentqty == null)
            scurrentqty = BigDecimal.ZERO; 
          long sreplenishqty = scurrentqty.longValue();
          if (sreplenishqty > replenishqty)
            sreplenishqty = replenishqty; 
          if (sreplenishqty <= 0L)
            continue; 
          if (!this.m_mmContext.generateTask(getCtx(), this.p_M_Warehouse_ID, slocatorId, locatorId, productId, 0, 0, 0, 0, 0, new BigDecimal(sreplenishqty), stockinguomId, this.p_C_DocTypeTask_ID, this.p_docAction, get_TrxName()))
            throw new IllegalStateException("Could not create task"); 
          replenishqty -= sreplenishqty;
        } 
        rs3.close();
      } 
      rs.close();
      cPreparedStatement1.close();
      cPreparedStatement1 = null;
      cPreparedStatement2.close();
      cPreparedStatement2 = null;
      cPreparedStatement3.close();
      cPreparedStatement3 = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, d_sql, e);
    } finally {
      DB.close(rs);
      DB.close(rs2);
      DB.close(rs3);
      DB.close((Statement)cPreparedStatement1);
      DB.close((Statement)cPreparedStatement2);
      DB.close((Statement)cPreparedStatement3);
      rs = null; cPreparedStatement1 = null;
      rs2 = null; cPreparedStatement2 = null;
      rs3 = null; cPreparedStatement3 = null;
    } 
    MWMSTaskList[] taskList = new MWMSTaskList[this.m_mmContext.m_TaskLists.size()];
    this.m_mmContext.m_TaskLists.toArray((Object[])taskList);
    if (taskList == null || taskList.length == 0)
      return "No warehouse tasks created"; 
    commit();
    addLog("Created Replenishment Tasklist ");
    boolean IsPrintFormatDef = true;
    for (MWMSTaskList element : taskList) {
      addLog(element.getWMS_TaskList_ID(), null, null, element.getDocumentNo());
      if (this.p_IsPrintReplenishmentList && IsPrintFormatDef) {
        ReportEngine re = ReportCtl.startDocumentPrint(getCtx(), 11, element.getWMS_TaskList_ID(), true);
        if (re == null)
          IsPrintFormatDef = false; 
      } 
    } 
    if (!IsPrintFormatDef)
      return Msg.getMsg(getCtx(), "NoDocPrintFormat"); 
    return "success";
  }
}
