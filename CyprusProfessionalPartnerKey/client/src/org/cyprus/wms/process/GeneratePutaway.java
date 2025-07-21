package org.cyprus.wms.process;

//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.wms.model.MWMSMMStrategySet;
import org.cyprus.wms.model.MWMSTaskList;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.print.ReportCtl;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;

public class GeneratePutaway extends SvrProcess {
  private int p_M_Warehouse_ID = 0;
  
  Trx trx;
  private int p_M_Zone_ID = 0;
  
  private int p_M_Locator_ID = 0;
  
  private int p_M_Product_ID = 0;
  
  private int p_C_DocTypeTask_ID = 0;
  
  private String p_docAction = "CO";
  
  private boolean p_IsPrintPutawayList = false;
  
  private MWMSTaskList[] m_taskLists;
  
  private String m_sql;
  
  public MWMSTaskList[] getTaskLists() {
    return this.m_taskLists;
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("M_Zone_ID")) {
          this.p_M_Zone_ID = element.getParameterAsInt();
        } else if (name.equals("M_Locator_ID")) {
          this.p_M_Locator_ID = element.getParameterAsInt();
        } else if (name.equals("M_Product_ID")) {
          this.p_M_Product_ID = element.getParameterAsInt();
        } else if (name.equals("C_DocTypeTask_ID")) {
          this.p_C_DocTypeTask_ID = element.getParameterAsInt();
        } else if (name.equals("DocAction")) {
          this.p_docAction = (String)element.getParameter();
        } else if (name.equals("IsPrintPutawayList")) {
          this.p_IsPrintPutawayList = element.getParameter().equals("Y");
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
  
  protected String doIt() throws Exception {
    CPreparedStatement cPreparedStatement = null;
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.info("p_M_Warehouse_ID=" + this.p_M_Warehouse_ID + "p_M_Zone_ID=" + this.p_M_Zone_ID + "p_M_Locator_ID=" + this.p_M_Locator_ID + "p_M_Product_ID=" + this.p_M_Product_ID + "p_C_DocType_ID=" + this.p_C_DocTypeTask_ID + "p_docAction=" + this.p_docAction + "p_IsPrintPutawayList=" + this.p_IsPrintPutawayList);
    if (this.p_M_Warehouse_ID == 0 || (this.p_M_Zone_ID == 0 && this.p_M_Locator_ID == 0))
      throw new Exception("@ZoneAndLocatorNotSpecified@"); 
    MWarehouse wh = MWarehouse.get(getCtx(), this.p_M_Warehouse_ID);
    if (wh == null || !wh.isWMSEnabled())
      throw new Exception("@WarehouseNotWMSEnabled@"); 
    MWMSMMStrategySet strategySet = MWMSMMStrategySet.getPutawayStrategySet(getCtx(), this.p_M_Warehouse_ID, get_TrxName());
    if (strategySet == null)
      throw new Exception("@PutawayStrategySetNotDefined@"); 
    this.m_sql = "SELECT s.M_Product_ID, s.M_Locator_ID, s.M_AttributeSetInstance_ID, COALESCE(SUM(CASE WHEN QtyType LIKE 'H' THEN Qty ELSE 0 END),0) QtyOnhand,COALESCE(SUM(CASE WHEN QtyType LIKE 'D' THEN Qty ELSE 0 END),0) QtyDedicated,COALESCE(SUM(CASE WHEN QtyType LIKE 'A' THEN Qty ELSE 0 END),0) QtyAllocated FROM M_StorageDetail s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) WHERE l.AD_Client_ID=? AND l.M_Warehouse_ID=? AND EXISTS (SELECT 1 FROM M_StorageDetail h WHERE h.M_Product_ID = s.M_Product_ID AND h.M_AttributeSetInstance_ID = s.M_AttributeSetInstance_ID AND h.M_Locator_ID = s.M_Locator_ID AND h.QtyType='H' AND h.Qty > 0) ";
    if (this.p_M_Zone_ID != 0)
      this.m_sql += "AND EXISTS (SELECT 1 FROM WMS_Zone z INNER JOIN WMS_ZoneLocator zl ON (z.WMS_Zone_ID = zl.WMS_Zone_ID) WHERE z.M_Warehouse_ID = l.M_Warehouse_ID AND z.WMS_Zone_ID = ? AND s.M_Locator_ID=zl.M_Locator_ID) "; 
    MDocType dt = MDocType.get(getCtx(), this.p_C_DocTypeTask_ID);
    Timestamp today = new Timestamp(Env.getContextAsTime("#Date"));
    ArrayList<Integer> orgs = new ArrayList<Integer>();
    orgs.add(Integer.valueOf(wh.getAD_Org_ID()));
//    String periodCheck = MPeriod.isOpen(getCtx(), wh.getAD_Client_ID(), orgs, today, dt.getDocBaseType());
    Boolean periodCheck = MPeriod.isOpen(getCtx(), today, dt.getDocBaseType(), wh.getAD_Client_ID());

//    if (periodCheck != null)
//      throw new Exception(periodCheck); 
    if (periodCheck)
        throw new Exception(); 
    if (this.p_M_Locator_ID != 0)
      this.m_sql += "AND l.M_Locator_ID=? "; 
    if (this.p_M_Product_ID != 0)
      this.m_sql += "AND s.M_Product_ID=? "; 
    this.m_sql += "GROUP BY s.M_Product_ID,s.M_Locator_ID, s.M_AttributeSetInstance_ID ORDER BY s.M_Product_ID,s.M_Locator_ID, s.M_AttributeSetInstance_ID ";
    this.log.finest("Putaway records SQL : " + this.m_sql);
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(this.m_sql, get_TrxName());
      int index = 1;
      cPreparedStatement.setInt(index++, getAD_Client_ID());
      cPreparedStatement.setInt(index++, this.p_M_Warehouse_ID);
      if (this.p_M_Zone_ID != 0)
        cPreparedStatement.setInt(index++, this.p_M_Zone_ID); 
      if (this.p_M_Locator_ID != 0)
        cPreparedStatement.setInt(index++, this.p_M_Locator_ID); 
      if (this.p_M_Product_ID != 0)
        cPreparedStatement.setInt(index++, this.p_M_Product_ID); 
      this.m_taskLists = strategySet.executePutawayStrategySet(trx, (PreparedStatement)cPreparedStatement, 0, this.p_C_DocTypeTask_ID, this.p_docAction);
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, this.m_sql, e);
      throw new Exception(e);
    } finally {
      DB.close((Statement)cPreparedStatement);
      cPreparedStatement = null;
    } 
    if (this.m_taskLists == null || this.m_taskLists.length == 0)
      return "No warehouse tasks created"; 
    boolean IsPrintFormatDef = true;
    for (MWMSTaskList element : this.m_taskLists) {
      addLog(element.getWMS_TaskList_ID(), null, null, element.getDocumentNo());
      if (this.p_IsPrintPutawayList && IsPrintFormatDef) {
        ReportEngine re = ReportCtl.startDocumentPrint(getCtx(), 11, element.getWMS_TaskList_ID(), true);
        if (re == null)
          IsPrintFormatDef = false; 
      } 
    } 
    if (!IsPrintFormatDef)
      return Msg.getMsg(getCtx(), "NoDocPrintFormat"); 
    return "Created TaskLists : " + this.m_taskLists.length;
  }
}

