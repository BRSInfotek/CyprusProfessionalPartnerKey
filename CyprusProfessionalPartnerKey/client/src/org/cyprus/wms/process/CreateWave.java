package org.cyprus.wms.process;

//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mfg.model.X_MFG_WorkCenter;
import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mfg.model.X_MFG_WorkOrderComponent;
import org.cyprus.mfg.model.X_MFG_WorkOrderOperation;
import org.cyprus.wms.model.MWMSMMStrategySet;
import org.cyprus.wms.model.MWMSTaskList;
import org.cyprus.wms.model.MWMSWave;
import org.cyprus.wms.model.MWMSWaveLine;
import org.cyprus.wms.model.MWMSWaveSortCriteria;
import org.cyprus.wms.model.X_WMS_WarehouseTask;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProcess;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MStorage.VO;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.framework.X_C_Order;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.Storage;
//import org.cyprusbrs.model.X_C_Order;
//import org.cyprusbrs.model.X_M_WarehouseTask;
//import org.cyprusbrs.model.X_M_WorkCenter;
//import org.cyprusbrs.model.X_M_WorkOrder;
//import org.cyprusbrs.model.X_M_WorkOrderComponent;
//import org.cyprusbrs.model.X_M_WorkOrderOperation;
import org.cyprusbrs.print.ReportCtl;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.process.ProcessInfo;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;
import org.cyprusbrs.util.ValueNamePair;

public class CreateWave extends SvrProcess {
  private String p_selectClause = null;
  PO po;
  Trx trx;
  private int p_M_Warehouse_ID = 0;
  
  private int p_C_DocTypeGroup_ID = 0;
  
  private int p_C_DocType_ID = 0;
  
  private String p_PriorityRule = null;
  
  private Timestamp p_DatePromised_From = null;
  
  private Timestamp p_DatePromised_To = null;
  
  private Timestamp p_DateOrdered_From = null;
  
  private Timestamp p_DateOrdered_To = null;
  
  private int p_C_BPartner_ID = 0;
  
  private int p_C_Order_ID = 0;
  
  private int p_M_Product_Category_ID = 0;
  
  private int p_M_Product_ID = 0;
  
  private int p_C_Country_ID = 0;
  
  private int p_M_Shipper_ID = 0;
  
  private int p_M_SourceZone_ID = 0;
  
  private int p_C_WaveSortCriteria_ID = 0;
  
  private String p_DeliveryRuleOverride = "";
  
  private int p_NoOfRows = 0;
  
  private boolean p_IsGeneratePickList = false;
  
  private String p_PickMethod = null;
  
  private int p_ClusterSize = 0;
  
  private int p_C_DocTypeTask_ID = 0;
  
  private String p_DocAction = X_WMS_WarehouseTask.DOCACTION_Prepare;
  
  private int p_M_Locator_ID = 0;
  
  private int p_T_Wave_ID = 0;
  
  private String p_WaveDocBaseType = "WOO";
  
  private boolean p_IsPrintPickList = false;
  
//  private final Map<SParameter, List<Storage.VO>> m_map = new HashMap<SParameter, List<Storage.VO>>();
  private final Map<SParameter, List<MStorage.VO>> m_map = new HashMap<SParameter, List<MStorage.VO>>();

  
  private SParameter m_lastPP = null;
  
//  private List<Storage.VO> m_lastStorages = null;
  private List<VO> m_lastStorages = null;

  ArrayList<Integer> m_completeOrders = new ArrayList<Integer>();
  
  private MWMSWave m_wave;
  
  private ArrayList<MWMSWaveLine> m_waveLines = new ArrayList<MWMSWaveLine>();
  
  private MWMSTaskList[] m_taskLists;
  
  public MWMSTaskList[] getM_taskLists() {
    return this.m_taskLists;
  }
  
  private String m_insertSQL = null;
  
  private String m_selectSQL = null;
  
  private int m_linesSucceeded = 0;
  
  private int p_SeqNo_From = 0;
  
  private int p_SeqNo_To = 0;
  
  private int p_M_WorkCenter_ID = 0;
  
  private boolean p_IsSupervisedOnly = false;
  
  private int p_M_WorkOrder_ID;
  
  private Timestamp p_DateScheduleFrom_From = null;
  
  private Timestamp p_DateScheduleFrom_To = null;
  
  private Timestamp p_DateScheduleTo_From = null;
  
  private Timestamp p_DateScheduleTo_To = null;
  
  private String p_WOType = null;
  
  private String p_WorkOrderFulfillmentRule;
  
  private ArrayList<Integer> m_completeOperations = new ArrayList<Integer>();
  
  private ArrayList<Integer> m_completeWorkOrders = new ArrayList<Integer>();
  
  public MWMSWave getWave() {
    return this.m_wave;
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null || element.getParameter_To() != null)
        if (name.equals("SelectClause")) {
          this.p_selectClause = (String)element.getParameter();
        } else if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("C_DocTypeGroup_ID")) {
          this.p_C_DocTypeGroup_ID = element.getParameterAsInt();
        } else if (name.equals("C_DocType_ID")) {
          this.p_C_DocType_ID = element.getParameterAsInt();
        } else if (name.equals("PriorityRule")) {
          this.p_PriorityRule = (String)element.getParameter();
        } else if (name.equals("DatePromised")) {
          this.p_DatePromised_From = (Timestamp)element.getParameter();
          this.p_DatePromised_To = (Timestamp)element.getParameter_To();
        } else if (name.equals("DateOrdered")) {
          this.p_DateOrdered_From = (Timestamp)element.getParameter();
          this.p_DateOrdered_To = (Timestamp)element.getParameter_To();
        } else if (name.equals("DateScheduleFrom")) {
          try {
            this.p_DateScheduleFrom_From = new Timestamp(Long.parseLong(element.getInfo()));
            this.p_DateScheduleFrom_To = new Timestamp(Long.parseLong(element.getInfo_To()));
          } catch (NumberFormatException nfe) {
            this.p_DateScheduleFrom_From = (Timestamp)element.getParameter();
            this.p_DateScheduleFrom_To = (Timestamp)element.getParameter_To();
          } 
        } else if (name.equals("DateScheduleTo")) {
          try {
            this.p_DateScheduleTo_From = new Timestamp(Long.parseLong(element.getInfo()));
            this.p_DateScheduleTo_To = new Timestamp(Long.parseLong(element.getInfo_To()));
          } catch (NumberFormatException nfe) {
            this.p_DateScheduleTo_From = (Timestamp)element.getParameter();
            this.p_DateScheduleTo_To = (Timestamp)element.getParameter_To();
          } 
        } else if (name.equals("C_BPartner_ID")) {
          this.p_C_BPartner_ID = element.getParameterAsInt();
        } else if (name.equals("C_Order_ID")) {
          this.p_C_Order_ID = element.getParameterAsInt();
        } else if (name.equals("M_Product_Category_ID")) {
          this.p_M_Product_Category_ID = element.getParameterAsInt();
        } else if (name.equals("M_Product_ID")) {
          this.p_M_Product_ID = element.getParameterAsInt();
        } else if (name.equals("C_Country_ID")) {
          this.p_C_Country_ID = element.getParameterAsInt();
        } else if (name.equals("M_Shipper_ID")) {
          this.p_M_Shipper_ID = element.getParameterAsInt();
        } else if (name.equals("M_SourceZone_ID")) {
          this.p_M_SourceZone_ID = element.getParameterAsInt();
        } else if (name.equals("C_WaveSortCriteria_ID")) {
          this.p_C_WaveSortCriteria_ID = element.getParameterAsInt();
        } else if (name.equals("DeliveryRuleOverride")) {
          this.p_DeliveryRuleOverride = (String)element.getParameter();
        } else if (name.equals("NoOfRows")) {
          this.p_NoOfRows = element.getParameterAsInt();
        } else if (name.equals("IsGeneratePickList")) {
          this.p_IsGeneratePickList = "Y".equals(element.getParameter());
        } else if (name.equals("PickMethod")) {
          this.p_PickMethod = (String)element.getParameter();
        } else if (name.equals("ClusterSize")) {
          this.p_ClusterSize = element.getParameterAsInt();
        } else if (name.equals("C_DocTypeTask_ID")) {
          this.p_C_DocTypeTask_ID = element.getParameterAsInt();
        } else if (name.equals("DocAction")) {
          this.p_DocAction = (String)element.getParameter();
        } else if (name.equals("M_Locator_ID")) {
          this.p_M_Locator_ID = element.getParameterAsInt();
        } else if (name.equals("T_Wave_ID")) {
          this.p_T_Wave_ID = element.getParameterAsInt();
        } else if (name.equals("IsPrintPickList")) {
          this.p_IsPrintPickList = element.getParameter().equals("Y");
        } else if (name.equals("WaveDocBaseType")) {
          this.p_WaveDocBaseType = (String)element.getParameter();
        } else if (name.equals("SeqNo")) {
          this.p_SeqNo_From = element.getParameterAsInt();
          this.p_SeqNo_To = element.getParameter_ToAsInt();
        } else if (name.equals("M_WorkCenter_ID")) {
          this.p_M_WorkCenter_ID = element.getParameterAsInt();
        } else if (name.equals("IsSupervisedOnly")) {
          this.p_IsSupervisedOnly = "Y".equals(element.getParameter());
        } else if (name.equals("WOFulfillRule")) {
          this.p_WorkOrderFulfillmentRule = (String)element.getParameter();
        } else if (name.equals("M_WorkOrder_ID")) {
          this.p_M_WorkOrder_ID = element.getParameterAsInt();
        } else if (name.equals("WOType")) {
          this.p_WOType = (String)element.getParameter();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    if (this.p_T_Wave_ID == 0 && this.p_DateScheduleFrom_To != null)
      this.p_DateScheduleFrom_To = new Timestamp(this.p_DateScheduleFrom_To.getTime() + 86400000L - 1L); 
    if (this.p_T_Wave_ID == 0 && this.p_DateScheduleTo_To != null)
      this.p_DateScheduleTo_To = new Timestamp(this.p_DateScheduleTo_To.getTime() + 86400000L - 1L); 
  }
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    Date startedAt = new Date();
    this.log.info("Started at :" + startedAt);
    this.log.info("M_Warehouse_ID=" + this.p_M_Warehouse_ID + "WaveDocBaseType=" + this.p_WaveDocBaseType + "C_DocTypeGroup_ID=" + this.p_C_DocTypeGroup_ID + "C_DocType_ID=" + this.p_C_DocType_ID + "PriorityRule=" + this.p_PriorityRule + "DatePromisedFrom=" + this.p_DatePromised_From + "DatePromisedTo=" + this.p_DatePromised_To + "DateOrderedFrom=" + this.p_DateOrdered_From + "DateOrderedTo=" + this.p_DateOrdered_To + "C_BPartner_ID=" + this.p_C_BPartner_ID + "C_Order_ID=" + this.p_C_Order_ID + "M_Product_Category_ID=" + this.p_M_Product_Category_ID + "M_Product_ID=" + this.p_M_Product_ID + "C_Country_ID=" + this.p_C_Country_ID + "M_Shipper_ID=" + this.p_M_Shipper_ID + "M_SourceZone_ID=" + this.p_M_SourceZone_ID + "C_WaveSortCriteria_ID=" + this.p_C_WaveSortCriteria_ID + "T_Wave_ID=" + this.p_T_Wave_ID + "p_IsPrintPickList=" + this.p_IsPrintPickList);
    if (this.p_M_Warehouse_ID == 0)
      throw new Exception("@NotFound@ @M_Warehouse_ID@"); 
    MWarehouse wh = MWarehouse.get(getCtx(), this.p_M_Warehouse_ID);
    if (wh == null || !wh.isWMSEnabled())
      throw new Exception("@WarehouseNotWMSEnabled@"); 
    String retVal = "";
    if ("SOO".equals(this.p_WaveDocBaseType)) {
      retVal = doItForSOO();
    } else if ("WOO".equals(this.p_WaveDocBaseType)) {
//      SysEnv se2 = SysEnv.get("CMFG");
//      if (se2 == null || !se2.checkLicense())
//        throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
      retVal = doItForWOO();
    } 
    return retVal;
  }
  
  private String doItForSOO() throws Exception {
    int noInserted = prepareTableForSOO();
    this.log.fine("Prepare table : Inserted # " + noInserted);
    String retVal = "";
    if (this.p_IsGeneratePickList) {
      String mandatoryColumn = "";
      if (this.p_M_Locator_ID == 0)
        mandatoryColumn = mandatoryColumn + "Destination Locator"; 
      if (this.p_C_DocTypeTask_ID == 0) {
        if (mandatoryColumn.length() != 0)
          mandatoryColumn = mandatoryColumn + ","; 
        mandatoryColumn = mandatoryColumn + "Pick Document Type";
      } 
      if (this.p_DocAction == null || this.p_DocAction.length() == 0) {
        if (mandatoryColumn.length() != 0)
          mandatoryColumn = mandatoryColumn + ","; 
        mandatoryColumn = mandatoryColumn + "Document Action";
      } 
      if (this.p_PickMethod == null || this.p_PickMethod.length() == 0) {
        if (mandatoryColumn.length() != 0)
          mandatoryColumn = mandatoryColumn + ","; 
        mandatoryColumn = mandatoryColumn + "Pick Method";
      } 
      if (mandatoryColumn.length() != 0)
        return "The following parameters are mandatory when generating Pick Lists : " + mandatoryColumn; 
      MLocator loc = new MLocator(getCtx(), this.p_M_Locator_ID, get_TrxName());
      if (loc == null || loc.getM_Warehouse_ID() != this.p_M_Warehouse_ID)
        throw new Exception("@WarehouseDestLocatorMismatch@"); 
      this.m_taskLists = createPickTasksForSOO(getCtx(), this.p_M_Warehouse_ID, this.p_T_Wave_ID, this.p_M_SourceZone_ID, this.p_M_Locator_ID, this.p_C_DocTypeTask_ID, this.p_DocAction, this.p_PickMethod.equals("C") ? 0 : 1, this.p_ClusterSize, this.p_DeliveryRuleOverride, trx);
      deleteTable();
      if (this.m_taskLists == null) {
        ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          return pp.getName(); 
      } 
      Date date = new Date();
      this.log.info("Completed at :" + date);
      retVal = Msg.getMsg(getCtx(), "OrderLinesProcessed") + " : " + noInserted + "," + Msg.getMsg(getCtx(), "OrderLinesSucceeded") + " : " + this.m_linesSucceeded + " ";
      if (this.m_taskLists == null || this.m_taskLists.length == 0)
        return retVal + ", No warehouse tasks created"; 
      boolean IsPrintFormatDef = true;
      for (MWMSTaskList element : this.m_taskLists) {
        addLog(element.getWMS_TaskList_ID(), null, null, element.getDocumentNo());
        if (this.p_IsPrintPickList && IsPrintFormatDef) {
//          ReportEngine re = ReportCtl.startDocumentPrint(getCtx(), 11, element.getWMS_TaskList_ID(), true);
            ReportEngine re = ReportCtl.startDocumentPrint(getCtx(), 11, element.getWMS_TaskList_ID(), true);

        	if (re == null) {
            IsPrintFormatDef = false;
          } else {
            int AD_Process_ID = MProcess.getIDByValue(getCtx(), "Print Address Labels");
            MPInstance instance = new MPInstance(getCtx(), AD_Process_ID, element.getWMS_TaskList_ID());
            if (!instance.save()) {
              IsPrintFormatDef = false;
            } else {
              ProcessInfo pi = new ProcessInfo("", AD_Process_ID);
              pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
              pi.setAD_Client_ID(getAD_Client_ID());
              MPInstancePara para = new MPInstancePara(instance, 10);
              para.setParameter("M_TaskList_ID", element.getWMS_TaskList_ID());
              if (!para.save()) {
                IsPrintFormatDef = false;
              } else {
                re = ReportCtl.startStandardReport(getCtx(), pi, true);
                if (re == null)
                  IsPrintFormatDef = false; 
              } 
            } 
          } 
        } 
      } 
      if (!IsPrintFormatDef)
        return retVal + "," + Msg.getMsg(getCtx(), "NoDocPrintFormat"); 
      return retVal + "," + " Created Task Lists : " + this.m_taskLists.length;
    } 
    checkAvailabilityForSOO();
    deleteTable();
    Date completedAt = new Date();
    this.log.info("Completed at :" + completedAt);
    retVal = Msg.getMsg(getCtx(), "OrderLinesProcessed") + " : " + noInserted + "," + Msg.getMsg(getCtx(), "OrderLinesSucceeded") + " : " + this.m_linesSucceeded + " ";
    if (this.m_wave == null)
      return retVal + ", No qualifying order lines can be fulfilled. Wave not created"; 
    return retVal + ",Created Wave : " + this.m_wave.getDocumentNo();
  }
  
  protected int prepareTableForSOO() throws Exception {
    if (this.p_T_Wave_ID != 0) {
      int noInserted = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM T_Wave WHERE T_Wave_ID=?", new Object[] { Integer.valueOf(this.p_T_Wave_ID) });
      return noInserted;
    } 
    this.p_T_Wave_ID = DB.getSQLValue(get_TrxName(), "SELECT T_WaveID_Seq.NEXTVAL FROM DUAL", new Object[0]);
    ArrayList<Object> list = new ArrayList();
    this.m_insertSQL = "INSERT INTO T_Wave (T_Wave_ID, SeqNo, C_OrderLine_ID) SELECT ?, T_WaveSeqNo_Seq.NEXTVAL, t.C_OrderLine_ID FROM ";
    list.add(Integer.valueOf(this.p_T_Wave_ID));
    if (this.p_selectClause != null) {
      this.m_insertSQL += "(" + this.p_selectClause + ") t";
    } else {
      this.m_insertSQL += " (SELECT lin.C_OrderLine_ID FROM C_OrderLine lin  INNER JOIN C_Order hdr ON (hdr.C_Order_ID=lin.C_Order_ID)  INNER JOIN M_Product p ON (lin.M_Product_ID=p.M_Product_ID AND p.IsExcludeAutoDelivery='N') ";
      if (this.p_C_DocTypeGroup_ID != 0)
        this.m_insertSQL += " INNER JOIN C_DocTypeGroupLine dtgl ON (dtgl.C_DocType_ID = hdr.C_DocType_ID) INNER JOIN C_DocTypeGroup dtg ON (dtg.C_DocTypeGroup_ID=dtgl.C_DocTypeGroup_ID)"; 
      if (this.p_C_Country_ID != 0)
        this.m_insertSQL += " INNER JOIN C_BPartner_Location bpl ON (hdr.C_BPartner_Location_ID = bpl.C_BPartner_Location_ID) INNER JOIN C_Location l ON (bpl.C_Location_ID = l.C_Location_ID)  INNER JOIN C_Country c ON (c.C_Country_ID = l.C_Country_ID) "; 
      this.m_insertSQL += " WHERE hdr.DocStatus='CO' AND hdr.IsSOTrx='Y' AND p.ProductType='I'  AND hdr.C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='SOO' AND DocSubTypeSO=('SO'))\tAND hdr.IsDropShip='N' AND hdr.DeliveryRule NOT IN ('M','F','R') AND lin.M_Warehouse_ID=? AND lin.QtyOrdered<>lin.QtyDelivered AND (lin.QtyOrdered - lin.QtyDelivered - lin.QtyDedicated -lin.QtyAllocated > 0)";
      list.add(Integer.valueOf(this.p_M_Warehouse_ID));
      if (this.p_C_DocTypeGroup_ID != 0) {
        this.m_insertSQL += " AND dtg.C_DocTypeGroup_ID = ? ";
        list.add(Integer.valueOf(this.p_C_DocTypeGroup_ID));
      } 
      if (this.p_C_DocType_ID != 0) {
        this.m_insertSQL += " AND hdr.C_DocType_ID = ? ";
        list.add(Integer.valueOf(this.p_C_DocType_ID));
      } 
      if (this.p_PriorityRule != null) {
        this.m_insertSQL += " AND hdr.PriorityRule = ? ";
        list.add(this.p_PriorityRule);
      } 
      if (this.p_DatePromised_From != null) {
        this.m_insertSQL += " AND TRUNC(lin.DatePromised,'DD')>=?";
        list.add(this.p_DatePromised_From);
      } 
      if (this.p_DatePromised_To != null) {
        this.m_insertSQL += " AND TRUNC(lin.DatePromised,'DD')<=?";
        list.add(this.p_DatePromised_To);
      } 
      if (this.p_DateOrdered_From != null) {
        this.m_insertSQL += " AND TRUNC(lin.DateOrdered,'DD')>=?";
        list.add(this.p_DateOrdered_From);
      } 
      if (this.p_DateOrdered_To != null) {
        this.m_insertSQL += " AND TRUNC(lin.DateOrdered,'DD')<=?";
        list.add(this.p_DateOrdered_To);
      } 
      if (this.p_C_BPartner_ID != 0) {
        this.m_insertSQL += " AND lin.C_BPartner_ID=?";
        list.add(Integer.valueOf(this.p_C_BPartner_ID));
      } 
      if (this.p_C_Order_ID != 0) {
        this.m_insertSQL += " AND lin.C_Order_ID=?";
        list.add(Integer.valueOf(this.p_C_Order_ID));
      } 
      if (this.p_M_Product_Category_ID != 0) {
        this.m_insertSQL += " AND p.M_Product_Category_ID=? ";
        list.add(Integer.valueOf(this.p_M_Product_Category_ID));
      } 
      if (this.p_M_Product_ID != 0) {
        this.m_insertSQL += " AND p.M_Product_ID=? ";
        list.add(Integer.valueOf(this.p_M_Product_ID));
      } 
      if (this.p_C_Country_ID != 0) {
        this.m_insertSQL += " AND c.C_Country_ID = ? ";
        list.add(Integer.valueOf(this.p_C_Country_ID));
      } 
      if (this.p_M_Shipper_ID != 0) {
        this.m_insertSQL += " AND hdr.M_Shipper_ID=? ";
        list.add(Integer.valueOf(this.p_M_Shipper_ID));
      } 
      if (this.p_NoOfRows > 0) {
        this.m_insertSQL += " AND ROWNUM <= ? ";
        list.add(Integer.valueOf(this.p_NoOfRows));
      } 
      String orderBy = " ORDER BY hdr.PriorityRule, lin.DatePromised, hdr.M_Shipper_ID, hdr.C_BPartner_ID, hdr.C_BPartner_Location_ID, hdr.C_Order_ID";
      if (this.p_C_WaveSortCriteria_ID != 0)
        orderBy = MWMSWaveSortCriteria.getOrderBy(getCtx(), this.p_C_WaveSortCriteria_ID); 
      this.m_insertSQL += orderBy;
      this.m_insertSQL += ") t ";
    } 
    Object[] params = new Object[list.size()];
    list.toArray(params);
    int no = DB.executeUpdateEx( this.m_insertSQL, params,get_TrxName());
    this.log.finest(this.m_insertSQL);
    this.log.fine("Inserted #" + no);
    return no;
  }
  
  private String doItForWOO() throws Exception {
    int noInserted = prepareTableForWOO();
    this.log.fine("Prepare table : Inserted # " + noInserted);
    if (this.p_IsGeneratePickList) {
      StringBuffer mandatoryColumn = new StringBuffer();
      if (this.p_M_Locator_ID == 0)
        mandatoryColumn.append("Destination Locator, "); 
      if (this.p_C_DocTypeTask_ID == 0)
        mandatoryColumn.append("Pick Document Type, "); 
      if (this.p_DocAction == null || this.p_DocAction.length() == 0)
        mandatoryColumn.append("Document Action, "); 
      if (this.p_PickMethod == null || this.p_PickMethod.length() == 0)
        mandatoryColumn.append("Pick Method, "); 
      int lastIndex = mandatoryColumn.lastIndexOf(",");
      if (lastIndex != -1)
        mandatoryColumn.deleteCharAt(lastIndex); 
      if (mandatoryColumn.length() != 0)
        return "The following parameters are mandatory when generating pick lists : " + mandatoryColumn.toString(); 
      MLocator loc = new MLocator(getCtx(), this.p_M_Locator_ID, get_TrxName());
      if (loc == null || loc.getM_Warehouse_ID() != this.p_M_Warehouse_ID)
        throw new Exception("@WarehouseDestLocatorMismatch@"); 
      MWMSTaskList[] taskList = createPickTasksForWOO(getCtx(), this.p_M_Warehouse_ID, this.p_T_Wave_ID, this.p_M_SourceZone_ID, this.p_M_Locator_ID, this.p_C_DocTypeTask_ID, this.p_DocAction, this.p_PickMethod.equals("C") ? 0 : 1, this.p_ClusterSize, this.p_WorkOrderFulfillmentRule, trx);
      deleteTable();
      if (taskList == null) {
        ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          return pp.getName(); 
      } 
      Date date = new Date();
      this.log.info("Completed at: " + date);
      String str = Msg.getMsg(getCtx(), "@OperationsProcessed@ : ") + noInserted + ", " + Msg.getMsg(getCtx(), "@ComponentsSucceeded@ : ") + this.m_linesSucceeded + " ";
      if (taskList == null || taskList.length == 0)
        return str + ", No warehouse tasks created"; 
      boolean IsPrintFormatDef = true;
      for (MWMSTaskList element : taskList) {
        addLog(element.getWMS_TaskList_ID(), null, null, element.getDocumentNo());
        if (!this.p_IsPrintPickList || IsPrintFormatDef);
      } 
      if (!IsPrintFormatDef)
        return str + ", " + Msg.getMsg(getCtx(), "NoDocPrintFormat"); 
      return str + ", Created Task Lists : " + taskList.length;
    } 
    checkAvailabilityForWOO();
    deleteTable();
    Date completedAt = new Date();
    this.log.info("Completed at :" + completedAt);
    String retVal = Msg.getMsg(getCtx(), "OperationsProcessed") + " : " + noInserted + "," + Msg.getMsg(getCtx(), "ComponentsSucceeded") + " : " + this.m_linesSucceeded + " ";
    if (this.m_wave == null)
      return retVal + ", No qualifying work order operations can be fulfilled. Wave not created"; 
    return retVal + ",Created Wave : " + this.m_wave.getDocumentNo();
  }
  
  protected int prepareTableForWOO() throws Exception {
    if (this.p_T_Wave_ID != 0) {
      int noInserted = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM T_Wave WHERE T_Wave_ID=?", new Object[] { Integer.valueOf(this.p_T_Wave_ID) });
      return noInserted;
    } 
    this.p_T_Wave_ID = DB.getSQLValue(get_TrxName(), "SELECT T_WaveID_Seq.NEXTVAL FROM DUAL", new Object[0]);
    ArrayList<Object> list = new ArrayList();
    this.m_insertSQL = "INSERT INTO T_Wave (T_Wave_ID, SeqNo, M_WorkOrderOperation_ID) SELECT ?, T_WaveSeqNo_Seq.NEXTVAL, t.M_WorkOrderOperation_ID FROM ";
    list.add(Integer.valueOf(this.p_T_Wave_ID));
    if (this.p_selectClause != null) {
      this.m_insertSQL += "(" + this.p_selectClause + ") t";
    } else {
      this.m_insertSQL += " ( SELECT woo.M_WorkOrderOperation_ID ";
      this.m_insertSQL += getFromClauseforWOO(list);
      this.m_insertSQL += ") t ";
    } 
    Object[] params = new Object[list.size()];
    list.toArray(params);
    int no = DB.executeUpdateEx( this.m_insertSQL, params,get_TrxName());
    this.log.finest(this.m_insertSQL);
    this.log.fine("Inserted #" + no);
    return no;
  }
  
  private String getFromClauseforWOO(List<Object> list) throws Exception {
    StringBuffer returnStr = new StringBuffer("FROM M_WorkOrder wo INNER JOIN M_Product p ON (p.M_Product_ID = wo.M_Product_ID AND p.IsActive='Y') INNER JOIN M_WorkOrderOperation woo ON (woo.M_WorkOrder_ID=wo.M_WorkOrder_ID AND woo.IsActive='Y') INNER JOIN C_DocType dt ON (dt.C_DocType_ID=wo.C_DocType_ID AND dt.DocBaseType='WOO') INNER JOIN C_DocTypeGroupLine dtgl ON (dtgl.C_DocType_ID=wo.C_DocType_ID) INNER JOIN C_DocTypeGroup dtg ON (dtg.C_DocTypeGroup_ID=dtgl.C_DocTypeGroup_ID) WHERE wo.DocStatus='IP' AND wo.M_Warehouse_ID=? AND dtg.C_DocTypeGroup_ID=? AND EXISTS (SELECT 1 FROM M_WorkOrderComponent woc WHERE woc.M_WorkOrderOperation_ID=woo.M_WorkOrderOperation_ID AND woc.QtyDedicated+woc.QtyAllocated+woc.QtyAvailable<woc.QtyRequired*wo.QtyEntered AND SupplyType='P') ");
    list.add(Integer.valueOf(this.p_M_Warehouse_ID));
    list.add(Integer.valueOf(this.p_C_DocTypeGroup_ID));
    if (this.p_PriorityRule != null) {
      returnStr.append(" AND wo.PriorityRule = ? ");
      list.add(this.p_PriorityRule);
    } 
    if (this.p_C_BPartner_ID != 0) {
      returnStr.append(" AND wo.C_BPartner_ID = ? ");
      list.add(Integer.valueOf(this.p_C_BPartner_ID));
    } 
    if (this.p_M_WorkOrder_ID != 0) {
      returnStr.append(" AND wo.M_WorkOrder_ID = ? ");
      list.add(Integer.valueOf(this.p_M_WorkOrder_ID));
    } 
    if (this.p_M_Product_ID != 0) {
      returnStr.append(" AND p.M_Product_ID = ? ");
      list.add(Integer.valueOf(this.p_M_Product_ID));
    } 
    if (this.p_SeqNo_From != 0) {
      returnStr.append(" AND woo.SeqNo >= ? ");
      list.add(Integer.valueOf(this.p_SeqNo_From));
    } 
    if (this.p_SeqNo_To != 0) {
      returnStr.append(" AND woo.SeqNo <= ? ");
      list.add(Integer.valueOf(this.p_SeqNo_To));
    } 
    if (this.p_M_WorkCenter_ID != 0) {
      returnStr.append(" AND woo.M_WorkCenter_ID = ? ");
      list.add(Integer.valueOf(this.p_M_WorkCenter_ID));
    } 
    if (this.p_DateScheduleFrom_From != null) {
      returnStr.append(" AND woo.DateScheduleFrom >= ? ");
      list.add(this.p_DateScheduleFrom_From);
    } 
    if (this.p_DateScheduleFrom_To != null) {
      returnStr.append(" AND woo.DateScheduleFrom <= ? ");
      list.add(this.p_DateScheduleFrom_To);
    } 
    if (this.p_DateScheduleTo_From != null) {
      returnStr.append(" AND woo.DateScheduleTo >= ? ");
      list.add(this.p_DateScheduleTo_From);
    } 
    if (this.p_DateScheduleTo_To != null) {
      returnStr.append(" AND woo.DateScheduleTo <= ? ");
      list.add(this.p_DateScheduleTo_To);
    } 
    if (this.p_WOType != null) {
      returnStr.append(" AND wo.WOType = ? ");
      list.add(this.p_WOType);
    } 
    if (this.p_NoOfRows != 0) {
      returnStr.append(" AND ROWNUM <= ? ");
      list.add(Integer.valueOf(this.p_NoOfRows));
    } 
    if (this.p_IsSupervisedOnly) {
      returnStr.append(" AND woo.Supervisor_ID = ? ");
//      list.add(Integer.valueOf(getCtx().getAD_User_ID()));
      list.add(Integer.valueOf(Env.getAD_User_ID(getCtx())));

    } 
    String orderBy = " ORDER BY wo.DocumentNo, woo.SeqNo ";
    if (this.p_C_WaveSortCriteria_ID != 0)
      orderBy = MWMSWaveSortCriteria.getOrderBy(getCtx(), this.p_C_WaveSortCriteria_ID); 
    returnStr.append(orderBy);
    return returnStr.toString();
  }
  
  private void deleteTable() {
    String deleteSQL = "DELETE FROM T_Wave WHERE T_Wave_ID=?";
    int no = DB.executeUpdateEx( deleteSQL, new Object[] { Integer.valueOf(this.p_T_Wave_ID) },get_TrxName());
    this.log.fine("Deleted #" + no);
  }
  
  private boolean checkCompleteOrder(MWMSMMStrategySet strategySet, int T_Wave_ID, MOrder order, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID, Trx trx) throws Exception {
    Integer C_Order_ID = Integer.valueOf(order.getC_Order_ID());
    String sqlCheck = "SELECT COUNT(*) FROM C_OrderLine ol  INNER JOIN M_Product p ON (ol.M_Product_ID=p.M_Product_ID)WHERE C_Order_ID = ?  AND p.ProductType='I' AND NOT EXISTS  (SELECT 1 FROM T_Wave wave  WHERE wave.T_Wave_ID=?  AND ol.C_OrderLine_ID = wave.C_OrderLine_ID)";
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, new Object[] { C_Order_ID, Integer.valueOf(T_Wave_ID) });
    
    if (no != 0)
      return true; 
    MOrderLine[] lines = order.getLines();
    boolean availabilityCheck = true;
    int lineCount = 0;
    for (MOrderLine line : lines) {
      BigDecimal qtyOpen = strategySet.executePickStrategySetForSOO(line, order, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
      if (qtyOpen != null && qtyOpen.signum() > 0) {
        this.log.fine("Could not allocate C_OrderLine_ID : " + line.getC_OrderLine_ID());
        availabilityCheck = false;
        break;
      } 
      if (qtyOpen != null)
        lineCount++; 
    } 
    if (!availabilityCheck) {
      strategySet.removeUnconfirmedTasks();
    } else if (!strategySet.confirmTasks(trx,get_TrxName(), docAction)) {
      return false;
    } 
    this.m_linesSucceeded += lineCount;
    return true;
  }
  
  public MWMSTaskList[] createPickTasksForSOO(Properties ctx, int M_Warehouse_ID, int T_Wave_ID, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int pickMethod, int clusterSize, String deliveryRuleOverride, Trx trx) throws Exception {
    CPreparedStatement cPreparedStatement1 = null, cPreparedStatement2 = null;
    HashMap<Integer, MOrder> m_orders = new HashMap<Integer, MOrder>();
    MWMSMMStrategySet strategySet = MWMSMMStrategySet.getPickStrategySet(ctx, M_Warehouse_ID, get_TrxName());
    if (strategySet == null) {
      this.log.saveError("Error", "PickStrategySetNotDefined");
      return null;
    } 
    MDocType dt = MDocType.get(getCtx(), C_DocType_ID);
    Timestamp today = new Timestamp(Env.getContextAsTime("#Date"));
    ArrayList<Integer> orgs = new ArrayList<Integer>();
    orgs.add(Integer.valueOf(strategySet.getAD_Org_ID()));
//    String periodCheck = MPeriod.isOpen(strategySet.getCtx(), strategySet.getAD_Client_ID(), orgs, today, dt.getDocBaseType());
    Boolean periodCheck = MPeriod.isOpen(strategySet.getCtx(), today, dt.getDocBaseType(),strategySet.getAD_Client_ID());

    if (periodCheck) {
     // this.log.saveError("Error", periodCheck);
      this.log.saveError("Error","");

      return null;
    } 
    strategySet.setGroupingParameters(pickMethod, clusterSize);
    String selectOrders = "SELECT * FROM C_Order o WHERE C_Order_ID IN (SELECT ol.C_Order_ID FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) AND t.T_Wave_ID=?)";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement1 = DB.prepareStatement(selectOrders, get_TrxName());
      cPreparedStatement1.setInt(1, this.p_T_Wave_ID);
      rs = cPreparedStatement1.executeQuery();
      while (rs.next()) {
        MOrder order = new MOrder(getCtx(), rs, get_TrxName());
        m_orders.put(Integer.valueOf(order.getC_Order_ID()), order);
      } 
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, selectOrders, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement1);
      rs = null; cPreparedStatement1 = null;
    } 
    String selectSQL = "SELECT * FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) WHERE T_Wave_ID = ? ORDER BY SeqNo";
    PreparedStatement pstmt2 = null;
    rs = null;
    try {
      cPreparedStatement2 = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement2.setInt(1, T_Wave_ID);
      rs = cPreparedStatement2.executeQuery();
      while (rs.next()) {
        MOrderLine line = new MOrderLine(getCtx(), rs, get_TrxName());
        MOrder order = m_orders.get(Integer.valueOf(line.getC_Order_ID()));
        int C_WaveLine_ID = rs.getInt("C_WaveLine_ID");
        String deliveryRule = "";
        if (deliveryRuleOverride != null && deliveryRuleOverride.length() != 0) {
          deliveryRule = deliveryRuleOverride;
        } else {
          deliveryRule = order.getDeliveryRule();
        } 
        if (X_C_Order.DELIVERYRULE_CompleteOrder.equals(deliveryRule)) {
          if (!this.m_completeOrders.contains(Integer.valueOf(order.getC_Order_ID()))) {
            checkCompleteOrder(strategySet, T_Wave_ID, order, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID, trx);
            this.m_completeOrders.add(Integer.valueOf(order.getC_Order_ID()));
          } 
          continue;
        } 
        BigDecimal qtyOpen = strategySet.executePickStrategySetForSOO(line, order, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        if (qtyOpen != null) {
          if (qtyOpen.signum() > 0) {
            this.log.fine("Could not fully allocate C_OrderLine_ID : " + line.getC_OrderLine_ID());
            if (X_C_Order.DELIVERYRULE_Availability.equals(deliveryRule) && strategySet.getUnconfirmedCount() > 0) {
              if (!strategySet.confirmTasks(trx,get_TrxName(), docAction))
                throw new IllegalStateException("Could not create task"); 
              this.m_linesSucceeded++;
              continue;
            } 
            strategySet.removeUnconfirmedTasks();
            continue;
          } 
          if (strategySet.getUnconfirmedCount() > 0) {
            strategySet.confirmTasks(trx,get_TrxName(), docAction);
            this.m_linesSucceeded++;
          } 
        } 
      } 
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, selectSQL, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement2);
      rs = null; cPreparedStatement2 = null;
    } 
    strategySet.confirmTasks(trx,get_TrxName(), docAction);
    MWMSTaskList[] taskList = strategySet.getTaskLists();
    Date completedAt = new Date();
    this.log.info("Completed at :" + completedAt);
    return taskList;
  }
  
  public MWMSTaskList[] createPickTasksForWOO(Properties ctx, int M_Warehouse_ID, int T_Wave_ID, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int pickMethod, int clusterSize, String deliveryRuleOverride, Trx trx ) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    MWMSMMStrategySet strategySet = MWMSMMStrategySet.getPickStrategySet(ctx, M_Warehouse_ID, get_TrxName());
    if (strategySet == null) {
      this.log.saveError("Error", "PickStrategySetNotDefined");
      return null;
    } 
    MDocType dt = MDocType.get(getCtx(), C_DocType_ID);
    Timestamp today = new Timestamp(Env.getContextAsTime("#Date"));
    ArrayList<Integer> orgs = new ArrayList<Integer>();
    orgs.add(Integer.valueOf(strategySet.getAD_Org_ID()));
//    String periodCheck = MPeriod.isOpen(strategySet.getCtx(), strategySet.getAD_Client_ID(), orgs, today, dt.getDocBaseType());
//    if (periodCheck != null) {
//      this.log.saveError("Error", periodCheck);
//      return null;
//    } 
    Boolean periodCheck = MPeriod.isOpen(strategySet.getCtx(), today, dt.getDocBaseType(), strategySet.getAD_Client_ID());
    if (periodCheck) {
      this.log.saveError("Error", "");
      return null;
    } 
    
    strategySet.setGroupingParameters(pickMethod, clusterSize);
    String selectSQL = "SELECT woo.MFG_WorkOrder_ID OrderRef_ID, t.MFG_WorkOrderOperation_ID LineRef_ID, woc.MFG_WorkOrderComponent_ID LineLineRef_ID, t.C_WaveLine_ID, t.SeqNo, 'WOO' WaveDocBaseType FROM T_Wave t INNER JOIN MFG_WorkOrderOperation woo ON (t.MFG_WorkOrderOperation_ID = woo.MFG_WorkOrderOperation_ID) INNER JOIN MFG_WorkOrder wo ON (wo.MFG_WorkOrder_ID = woo.MFG_WorkOrder_ID) INNER JOIN M_WorkOrderComponent woc ON (woo.MFG_WorkOrderOperation_ID = woc.MFG_WorkOrderOperation_ID AND woc.SupplyType='P') WHERE T_Wave_ID = ? ORDER BY SeqNo";
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement.setInt(1, T_Wave_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        String waveLineType = rs.getString(6);
        int OrderRef_ID = rs.getInt(1);
        int LineRef_ID = rs.getInt(2);
        int LineLineRef_ID = rs.getInt(3);
        X_MFG_WorkOrder xwo = null;
        X_MFG_WorkOrderOperation xwoo = null;
        String workorderFulfillmentRule = "";
        int C_WaveLine_ID = rs.getInt(4);
        xwo = new X_MFG_WorkOrder(ctx, OrderRef_ID, get_TrxName());
        xwoo = new X_MFG_WorkOrderOperation(ctx, LineRef_ID, get_TrxName());
        if (this.p_WorkOrderFulfillmentRule != null && this.p_WorkOrderFulfillmentRule.length() != 0)
          workorderFulfillmentRule = this.p_WorkOrderFulfillmentRule; 
        if ("O".equals(workorderFulfillmentRule)) {
          if (!this.m_completeWorkOrders.contains(Integer.valueOf(xwo.getMFG_WorkOrder_ID()))) {
            checkCompleteWorkOrder(strategySet, T_Wave_ID, xwo, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID, trx);
            this.m_completeWorkOrders.add(Integer.valueOf(xwo.getMFG_WorkOrder_ID()));
          } 
          continue;
        } 
        if ("N".equals(workorderFulfillmentRule)) {
          if (!this.m_completeOperations.contains(Integer.valueOf(xwoo.getMFG_WorkOrderOperation_ID()))) {
            checkCompleteWorkOrderOperation(strategySet, T_Wave_ID, xwoo, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID, trx);
            this.m_completeOperations.add(Integer.valueOf(xwoo.getMFG_WorkOrderOperation_ID()));
          } 
          continue;
        } 
        int tempDestM_Locator_ID = destM_Locator_ID;
        X_MFG_WorkCenter xwc = new X_MFG_WorkCenter(getCtx(), xwoo.getMFG_WorkCenter_ID(), get_TrxName());
        destM_Locator_ID = xwc.getM_Locator_ID();
        if (destM_Locator_ID == 0)
          destM_Locator_ID = tempDestM_Locator_ID; 
        BigDecimal qtyOpen = strategySet.executePickStrategySet(waveLineType, LineLineRef_ID, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        destM_Locator_ID = tempDestM_Locator_ID;
        if (qtyOpen != null) {
          if (qtyOpen.signum() > 0) {
            this.log.fine("Could not fully allocate M_WorkOrderComponent_ID : " + LineLineRef_ID);
            if ("A".equals(waveLineType) && strategySet.getUnconfirmedCount() > 0) {
              if (!strategySet.confirmTasks(trx,get_TrxName(), docAction))
                throw new IllegalStateException("Could not create task"); 
              this.m_linesSucceeded++;
              continue;
            } 
            strategySet.removeUnconfirmedTasks();
            continue;
          } 
          if (strategySet.getUnconfirmedCount() > 0) {
            strategySet.confirmTasks(trx,get_TrxName(), docAction);
            this.m_linesSucceeded++;
          } 
        } 
      } 
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, selectSQL, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    strategySet.confirmTasks(trx,get_TrxName(), docAction);
    MWMSTaskList[] taskList = strategySet.getTaskLists();
    Date completedAt = new Date();
    this.log.info("Completed at :" + completedAt);
    return taskList;
  }
  
  private boolean checkCompleteWorkOrderOperation(MWMSMMStrategySet strategySet, int wave_ID, X_MFG_WorkOrderOperation xwoo, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID, Trx trx) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    boolean availabilityCheck = true;
    int compCount = 0;
    String sql = "SELECT * FROM MFG_WorkOrderComponent woc WHERE MFG_WorkOrderOperation_ID = ? AND SupplyType='P' ORDER BY woc.MFG_WorkOrderOperation_ID, woc.Line";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, xwoo.getMFG_WorkOrderOperation_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_MFG_WorkOrderComponent xwoc = new X_MFG_WorkOrderComponent(getCtx(), rs, get_TrxName());
        int tempDestM_Locator_ID = destM_Locator_ID;
        X_MFG_WorkCenter xwc = new X_MFG_WorkCenter(getCtx(), xwoo.getMFG_WorkCenter_ID(), get_TrxName());
        destM_Locator_ID = xwc.getM_Locator_ID();
        if (destM_Locator_ID == 0)
          destM_Locator_ID = tempDestM_Locator_ID; 
        BigDecimal qtyOpen = strategySet.executePickStrategySet("WOO", xwoc.getMFG_WorkOrderComponent_ID(), M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        destM_Locator_ID = tempDestM_Locator_ID;
        if (qtyOpen != null && qtyOpen.signum() > 0) {
          this.log.fine("Could not allocate M_WorkOrderComponent_ID : " + xwoc.getMFG_WorkOrderComponent_ID());
          availabilityCheck = false;
          break;
        } 
        if (qtyOpen != null)
          compCount++; 
      } 
    } catch (SQLException sqle) {
      this.log.log(Level.SEVERE, sql, sqle);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    if (!availabilityCheck) {
      strategySet.removeUnconfirmedTasks();
    } else if (!strategySet.confirmTasks(trx,get_TrxName(), docAction)) {
      return false;
    } 
    this.m_linesSucceeded += compCount;
    return true;
  }
  
  private boolean checkCompleteWorkOrder(MWMSMMStrategySet strategySet, int T_Wave_ID, X_MFG_WorkOrder xwo, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID, Trx trx) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<Object> list = new ArrayList();
    String str = "SELECT woo.MFG_WorkOrderOperation_ID as MFG_WorkOrderOperation_ID, woo.MFG_WorkOrder_ID as MFG_WorkOrder_ID ";
    str = str + getFromClauseforWOO(list);
    String sqlCheck = "SELECT COUNT(*) FROM (" + str + ") work WHERE work.MFG_WorkOrder_ID=? " + "AND NOT EXISTS (SELECT 1 FROM T_Wave t WHERE t.T_Wave_ID=? AND t.MFG_WorkOrderOperation_ID = work.MFG_WorkOrderOperation_ID) ";
    list.add(Integer.valueOf(xwo.getMFG_WorkOrder_ID()));
    list.add(Integer.valueOf(T_Wave_ID));
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, list.toArray());
    if (no != 0)
      return true; 
    boolean availabilityCheck = true;
    int compCount = 0;
    String sql = "SELECT * FROM MFG_WorkOrderComponent woc WHERE MFG_WorkOrderOperation_ID IN (SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID=?) AND SupplyType='P' ORDER BY woc.MFG_WorkOrderOperation_ID, woc.Line";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, xwo.getMFG_WorkOrder_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_MFG_WorkOrderComponent xwoc = new X_MFG_WorkOrderComponent(getCtx(), rs, null);
        int tempDestM_Locator_ID = destM_Locator_ID;
        X_MFG_WorkOrderOperation xwoo = new X_MFG_WorkOrderOperation(getCtx(), xwoc.getMFG_WorkOrderOperation_ID(), get_TrxName());
        X_MFG_WorkCenter xwc = new X_MFG_WorkCenter(getCtx(), xwoo.getMFG_WorkCenter_ID(), get_TrxName());
        destM_Locator_ID = xwc.getM_Locator_ID();
        if (destM_Locator_ID == 0)
          destM_Locator_ID = tempDestM_Locator_ID; 
        BigDecimal qtyOpen = strategySet.executePickStrategySet("WOO", xwoc.getMFG_WorkOrderComponent_ID(), M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        destM_Locator_ID = tempDestM_Locator_ID;
        if (qtyOpen != null && qtyOpen.signum() > 0) {
          this.log.fine("Could not allocate MFG_WorkOrderComponent_ID : " + xwoc.getMFG_WorkOrderComponent_ID());
          availabilityCheck = false;
          break;
        } 
        if (qtyOpen != null)
          compCount++; 
      } 
    } catch (SQLException sqle) {
      this.log.log(Level.SEVERE, sql, sqle);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    if (!availabilityCheck) {
      strategySet.removeUnconfirmedTasks();
    } else if (!strategySet.confirmTasks(trx,get_TrxName(), docAction)) {
      return false;
    } 
    this.m_linesSucceeded += compCount;
    return true;
  }
  
  protected String checkCompleteOrder(MOrder order) {
    Integer C_Order_ID = Integer.valueOf(order.getC_Order_ID());
    if (this.m_completeOrders.contains(C_Order_ID))
      return ""; 
    this.m_completeOrders.add(C_Order_ID);
    String sqlCheck = "SELECT COUNT(*) FROM C_OrderLine ol  INNER JOIN M_Product p ON (ol.M_Product_ID=p.M_Product_ID AND p.ProductType='I')WHERE C_Order_ID = ?  AND NOT EXISTS  (SELECT 1 FROM T_Wave t  WHERE T_Wave_ID=?  AND ol.C_OrderLine_ID = t.C_OrderLine_ID)";
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, new Object[] { C_Order_ID, Integer.valueOf(this.p_T_Wave_ID) });
    if (no != 0)
      return ""; 
    MOrderLine[] lines = order.getLines();
    boolean availabilityCheck = true;
    for (MOrderLine line : lines) {
      BigDecimal onHand = Env.ZERO;
      BigDecimal toAllocate = line.getQtyOrdered().subtract(line.getQtyDelivered()).subtract(line.getQtyDedicated()).subtract(line.getQtyAllocated());
      MProduct product = line.getProduct();
//      List<Storage.VO> storages = null;
      List<VO> storages = null;

      if (product != null && product.isStocked() && toAllocate.signum() > 0) {
        if (product.isPurchasedToOrder()) {
          MOrderLine receiptLine = (MOrderLine) line.getRef_OrderLine();
          if (receiptLine == null) {
            availabilityCheck = false;
            break;
          } 
          BigDecimal qtyReceived = receiptLine.getQtyDelivered();
          BigDecimal qtyShippedOrAllocated = line.getQtyDelivered().add(line.getQtyAllocated()).add(line.getQtyDedicated());
          BigDecimal qtyAvailableToShip = qtyReceived.subtract(qtyShippedOrAllocated);
          if (qtyAvailableToShip.compareTo(toAllocate) < 0) {
            availabilityCheck = false;
            break;
          } 
        } 
        storages = getStorages(line.getM_Warehouse_ID(), this.p_M_SourceZone_ID, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (line.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true);
//        for (Storage.VO storage : storages)
        for (VO storage : storages)

        onHand = onHand.add(storage.getAvailableQty()); 
        if (onHand.compareTo(toAllocate) < 0) {
          availabilityCheck = false;
          break;
        } 
      } 
    } 
    if (!availabilityCheck)
      return ""; 
    for (MOrderLine line : lines) {
      BigDecimal toAllocate = line.getQtyOrdered().subtract(line.getQtyDelivered()).subtract(line.getQtyDedicated()).subtract(line.getQtyAllocated());
      MProduct product = line.getProduct();
//      List<Storage.VO> storages = null;
      List<VO> storages = null;

      if (product != null && product.isStocked()) {
        storages = getStorages(line.getM_Warehouse_ID(), this.p_M_SourceZone_ID, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (line.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true);
        createWaveLine(line.getC_OrderLine_ID(), 0, storages, toAllocate);
      } 
    } 
    return "";
  }
  
  protected String checkAvailabilityForSOO() {
    CPreparedStatement cPreparedStatement1 = null, cPreparedStatement2 = null;
    HashMap<Integer, MOrder> m_orders = new HashMap<Integer, MOrder>();
    String selectOrders = "SELECT * FROM C_Order o WHERE C_Order_ID IN (SELECT ol.C_Order_ID FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) AND t.T_Wave_ID=?)";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement1 = DB.prepareStatement(selectOrders, get_TrxName());
      cPreparedStatement1.setInt(1, this.p_T_Wave_ID);
      rs = cPreparedStatement1.executeQuery();
      while (rs.next()) {
        MOrder order = new MOrder(getCtx(), rs, get_TrxName());
        m_orders.put(Integer.valueOf(order.getC_Order_ID()), order);
      } 
      cPreparedStatement1.close();
      cPreparedStatement1 = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, selectOrders, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement1);
      rs = null; cPreparedStatement1 = null;
    } 
    this.m_selectSQL = "SELECT * FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) WHERE T_Wave_ID=? ORDER BY SeqNo";
    PreparedStatement pstmt2 = null;
    rs = null;
    try {
      cPreparedStatement2 = DB.prepareStatement(this.m_selectSQL, get_TrxName());
      cPreparedStatement2.setInt(1, this.p_T_Wave_ID);
      rs = cPreparedStatement2.executeQuery();
      while (rs.next()) {
        MOrderLine line = new MOrderLine(getCtx(), rs, get_TrxName());
        MOrder order = m_orders.get(Integer.valueOf(line.getC_Order_ID()));
        String deliveryRule = order.getDeliveryRule();
        if (this.p_DeliveryRuleOverride != null && this.p_DeliveryRuleOverride.length() != 0)
          deliveryRule = this.p_DeliveryRuleOverride; 
        this.log.fine("check: " + line + " - DeliveryRule=" + order.getDeliveryRule());
        if (X_C_Order.DELIVERYRULE_CompleteOrder.equals(deliveryRule)) {
          checkCompleteOrder(order);
          continue;
        } 
        BigDecimal onHand = Env.ZERO;
        BigDecimal toAllocate = line.getQtyOrdered().subtract(line.getQtyDelivered()).subtract(line.getQtyDedicated()).subtract(line.getQtyAllocated());
        MProduct product = line.getProduct();
        if (product == null || !product.isStocked() || toAllocate.signum() <= 0)
          continue; 
        if (product.isPurchasedToOrder()) {
          MOrderLine receiptLine = (MOrderLine) line.getRef_OrderLine();
          if (receiptLine == null)
            continue; 
          BigDecimal qtyReceived = receiptLine.getQtyDelivered();
          BigDecimal qtyShippedOrAllocated = line.getQtyDelivered().add(line.getQtyAllocated()).add(line.getQtyDedicated());
          BigDecimal qtyAvailableToShip = qtyReceived.subtract(qtyShippedOrAllocated);
          if (qtyAvailableToShip.compareTo(BigDecimal.ZERO) <= 0)
            continue; 
          if (qtyAvailableToShip.compareTo(toAllocate) <= 0)
            toAllocate = qtyAvailableToShip; 
        } 
        List<VO> storages = getStorages(line.getM_Warehouse_ID(), this.p_M_SourceZone_ID, line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (line.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true);
        for (VO storage : storages)
          onHand = onHand.add(storage.getAvailableQty()); 
        boolean fullLine = (onHand.compareTo(toAllocate) >= 0);
        if (fullLine && X_C_Order.DELIVERYRULE_CompleteLine.equals(deliveryRule)) {
          this.log.fine("CompleteLine - OnHand=" + onHand + ", ToDeliver=" + toAllocate + " - " + line);
          createWaveLine(line.getC_OrderLine_ID(), 0, storages, toAllocate);
          continue;
        } 
        if (X_C_Order.DELIVERYRULE_Availability.equals(deliveryRule) && onHand.signum() > 0) {
          BigDecimal allocate = toAllocate;
          if (allocate.compareTo(onHand) > 0)
            allocate = onHand; 
          this.log.fine("Available - OnHand=" + onHand + "), ToDeliver=" + toAllocate + ", Delivering=" + allocate + " - " + line);
          createWaveLine(line.getC_OrderLine_ID(), 0, storages, allocate);
          continue;
        } 
        this.log.fine("Failed: " + deliveryRule + " - OnHand=" + onHand + "), ToAllocate=" + toAllocate + " - " + line);
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, this.m_selectSQL, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement2);
      rs = null; cPreparedStatement2 = null;
    } 
//    if (!PO.saveAll(get_TrxName(), this.m_waveLines)) {
    if (!po.save(get_TrxName())) {

    String msg = "Save failed for wave lines";
      this.log.severe(msg);
      return msg;
    } 
    if (this.m_wave != null)
      return "@Created@ = " + this.m_wave.getDocumentNo(); 
    return "No matching order lines found ";
  }
  
  protected String checkCompleteWorkOrder(int M_WorkOrder_ID) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    if (this.m_completeWorkOrders.contains(Integer.valueOf(M_WorkOrder_ID)))
      return ""; 
    this.m_completeWorkOrders.add(Integer.valueOf(M_WorkOrder_ID));
    ArrayList<Object> list = new ArrayList();
    String str = "SELECT woo.MFG_WorkOrderOperation_ID as MFG_WorkOrderOperation_ID, woo.MFG_WorkOrder_ID as MFG_WorkOrder_ID ";
    str = str + getFromClauseforWOO(list);
    String sqlCheck = "SELECT COUNT(*) FROM (" + str + ") work WHERE work.MFG_WorkOrder_ID = ? " + "AND NOT EXISTS (SELECT 1 FROM T_Wave t WHERE t.T_Wave_ID = ? AND t.MFG_WorkOrderOperation_ID = work.MFG_WorkOrderOperation_ID)";
    list.add(Integer.valueOf(M_WorkOrder_ID));
    list.add(Integer.valueOf(this.p_T_Wave_ID));
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, list.toArray());
    if (no != 0)
      return ""; 
    X_MFG_WorkOrder xwo = new X_MFG_WorkOrder(getCtx(), M_WorkOrder_ID, get_TrxName());
    String sql = "SELECT * FROM MFG_WorkOrderComponent woc WHERE woc.MFG_WorkOrderOperation_ID IN (SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation woo WHERE MFG_WorkOrder_ID = ?) AND woc.SupplyType='P' ORDER BY woc.MFG_WorkOrderOperation_ID, woc.Line";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean availabilityCheck = true;
    ArrayList<X_MFG_WorkOrderComponent> xwocs = new ArrayList<X_MFG_WorkOrderComponent>();
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, M_WorkOrder_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_MFG_WorkOrderComponent xwoc = new X_MFG_WorkOrderComponent(getCtx(), rs, get_TrxName());
        xwocs.add(xwoc);
        BigDecimal onHand = Env.ZERO;
        BigDecimal toAllocate = xwoc.getQtyRequired().multiply(xwo.getQtyEntered()).subtract(xwoc.getQtyAvailable()).subtract(xwoc.getQtyAllocated()).subtract(xwoc.getQtyDedicated());
        MProduct product = MProduct.get(getCtx(), xwoc.getM_Product_ID());
        List<VO> storages = null;
        if (product != null && product.isStocked() && toAllocate.signum() > 0)
          storages = getStorages(xwo.getM_Warehouse_ID(), this.p_M_SourceZone_ID, xwoc.getM_Product_ID(), xwoc.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (xwoc.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true); 
        if (storages != null && storages.size() > 0)
          for (VO storage : storages)
            onHand = onHand.add(storage.getQtyOnHand()).subtract(storage.getQtyDedicated()).subtract(storage.getQtyAllocated());  
        if (onHand.compareTo(toAllocate) < 0) {
          availabilityCheck = false;
          break;
        } 
      } 
    } catch (SQLException sqle) {
      this.log.log(Level.SEVERE, sql, sqle);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    if (!availabilityCheck)
      return ""; 
//    for (X_M_WorkOrderComponent xwoc : xwocs) {
    for (X_MFG_WorkOrderComponent xwoc : xwocs) {
      BigDecimal toAllocate = xwoc.getQtyRequired().multiply(xwo.getQtyEntered()).subtract(xwoc.getQtyAvailable()).subtract(xwoc.getQtyDedicated()).subtract(xwoc.getQtyAllocated());
      MProduct product = MProduct.get(getCtx(), xwoc.getM_Product_ID());
      List<VO> storages = null;
      if (product != null && product.isStocked()) {
        storages = getStorages(xwo.getM_Warehouse_ID(), this.p_M_SourceZone_ID, xwoc.getM_Product_ID(), xwoc.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (xwoc.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true);
        createWaveLine(0, xwoc.getMFG_WorkOrderComponent_ID(), storages, toAllocate);
      } 
    } 
    return "";
  }
  
  protected String checkCompleteWorkOrderOperation(int M_WorkOrderOperation_ID) {
    CPreparedStatement cPreparedStatement = null;
    if (this.m_completeOperations.contains(Integer.valueOf(M_WorkOrderOperation_ID)))
      return ""; 
    this.m_completeOperations.add(Integer.valueOf(M_WorkOrderOperation_ID));
    X_MFG_WorkOrderOperation xwoo = new X_MFG_WorkOrderOperation(getCtx(), M_WorkOrderOperation_ID, get_TrxName());
    X_MFG_WorkOrder xwo = new X_MFG_WorkOrder(getCtx(), xwoo.getMFG_WorkOrder_ID(), get_TrxName());
    String sql = "SELECT * FROM MFG_WorkOrderComponent woc WHERE MFG_WorkOrderOperation_ID = ? AND SupplyType='P' ORDER BY woc.MFG_WorkOrderOperation_ID, woc.Line ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean availabilityCheck = true;
    ArrayList<X_MFG_WorkOrderComponent> xwocs = new ArrayList<X_MFG_WorkOrderComponent>();
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, M_WorkOrderOperation_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_MFG_WorkOrderComponent xwoc = new X_MFG_WorkOrderComponent(getCtx(), rs, get_TrxName());
        xwocs.add(xwoc);
        BigDecimal onHand = Env.ZERO;
        BigDecimal toAllocate = xwoc.getQtyRequired().multiply(xwo.getQtyEntered()).subtract(xwoc.getQtyAvailable()).subtract(xwoc.getQtyAllocated()).subtract(xwoc.getQtyDedicated());
        MProduct product = MProduct.get(getCtx(), xwoc.getM_Product_ID());
        List<VO> storages = null;
        if (product != null && product.isStocked() && toAllocate.signum() > 0)
          storages = getStorages(xwo.getM_Warehouse_ID(), this.p_M_SourceZone_ID, xwoc.getM_Product_ID(), xwoc.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (xwoc.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true); 
        for (VO storage : storages)
          onHand = onHand.add(storage.getAvailableQty()); 
        if (onHand.compareTo(toAllocate) < 0) {
          availabilityCheck = false;
          break;
        } 
      } 
    } catch (SQLException sqle) {
      this.log.log(Level.SEVERE, sql, sqle);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    if (!availabilityCheck)
      return ""; 
    for (X_MFG_WorkOrderComponent xwoc : xwocs) {
      BigDecimal toAllocate = xwoc.getQtyRequired().multiply(xwo.getQtyEntered()).subtract(xwoc.getQtyAvailable()).subtract(xwoc.getQtyDedicated()).subtract(xwoc.getQtyAllocated());
      MProduct product = MProduct.get(getCtx(), xwoc.getM_Product_ID());
      List<VO> storages = null;
      if (product != null && product.isStocked()) {
        storages = getStorages(xwo.getM_Warehouse_ID(), this.p_M_SourceZone_ID, xwoc.getM_Product_ID(), xwoc.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), (xwoc.getM_AttributeSetInstance_ID() == 0), (Timestamp)null, true);
        createWaveLine(0, xwoc.getMFG_WorkOrderComponent_ID(), storages, toAllocate);
      } 
    } 
    return "";
  }
  
  protected String checkAvailabilityForWOO() {
    CPreparedStatement cPreparedStatement = null;
    this.m_selectSQL = "SELECT t.MFG_WorkOrderOperation_ID, woo.SeqNo, woc.MFG_WorkOrderComponent_ID, woc.M_Product_ID, (woc.QtyRequired*wo.QtyEntered)-(woc.QtyAvailable+woc.QtyDedicated+woc.QtyAllocated), woc.C_BPartner_ID, woc.M_AttributeSetInstance_ID, wo.MFG_WorkOrder_ID, wo.M_Warehouse_ID, wo.DocStatus FROM T_Wave t INNER JOIN MFG_WorkOrderOperation woo ON (woo.MFG_WorkOrderOperation_ID = t.MFG_WorkOrderOperation_ID) INNER JOIN MFG_WorkOrder wo ON (wo.MFG_WorkOrder_ID = woo.MFG_WorkOrder_ID) INNER JOIN MFG_WorkOrderComponent woc ON (woc.MFG_WorkOrderOperation_ID = woo.MFG_WorkOrderOperation_ID AND woc.SupplyType='P') WHERE T_Wave_ID=? ORDER BY t.SeqNo, woc.MFG_WorkOrderOperation_ID, woc.Line";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(this.m_selectSQL, get_TrxName());
      cPreparedStatement.setInt(1, this.p_T_Wave_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        if (!"IP".equals(rs.getString(10)))
          this.log.fine("Failed: Invalid Work Order DocStatus, MFG_WorkOrderOperation_ID = " + rs.getInt(1)); 
        if ("O".equals(this.p_WorkOrderFulfillmentRule)) {
          checkCompleteWorkOrder(rs.getInt(8));
          continue;
        } 
        if ("N".equals(this.p_WorkOrderFulfillmentRule)) {
          checkCompleteWorkOrderOperation(rs.getInt(1));
          continue;
        } 
        MProduct product = new MProduct(getCtx(), rs.getInt(4), get_TrxName());
        BigDecimal toAllocate = rs.getBigDecimal(5);
        if (product == null || !product.isStocked() || toAllocate.signum() <= 0)
          continue; 
        List<VO> storages = getStorages(rs.getInt(9), 0, rs.getInt(4), rs.getInt(7), product.getM_AttributeSet_ID(), (rs.getInt(7) == 0), (Timestamp)null, true);
        BigDecimal onHand = Env.ZERO;
        for (VO storage : storages)
          onHand = onHand.add(storage.getAvailableQty()); 
        boolean fullLine = (onHand.compareTo(toAllocate) >= 0);
        if (fullLine && "L".equals(this.p_WorkOrderFulfillmentRule)) {
          this.log.fine("Complete Component - OnHand=" + onHand + ", ToIssue=" + toAllocate);
          createWaveLine(0, rs.getInt(3), storages, toAllocate);
          continue;
        } 
        if ("A".equals(this.p_WorkOrderFulfillmentRule) && onHand.signum() > 0) {
          BigDecimal allocate = toAllocate;
          if (allocate.compareTo(onHand) > 0)
            allocate = onHand; 
          this.log.fine("Available - OnHand=" + onHand + "), ToDeliver=" + toAllocate + ", Delivering=" + allocate + " - " + rs.getInt(3));
          createWaveLine(0, rs.getInt(3), storages, allocate);
          continue;
        } 
        this.log.fine("Failed: " + this.p_WorkOrderFulfillmentRule + " - OnHand=" + onHand + "), ToAllocate=" + toAllocate + " - " + rs.getInt(3));
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, this.m_insertSQL, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
//    if (!PO.saveAll(get_TrxName(), this.m_waveLines)) {
    if (!po.save(get_TrxName())) {

    String msg = "Save failed for wave lines";
      this.log.severe(msg);
      return msg;
    } 
    if (this.m_wave != null)
      return "@Created@ = " + this.m_wave.getDocumentNo(); 
    return "No matching work order operations found ";
  }
  
  private boolean createWaveLine(int C_OrderLine_ID, int M_WorkOrderComponent_ID, List<MStorage.VO> storages, BigDecimal movementQty) {
    if (this.m_wave == null) {
      this.m_wave = new MWMSWave(getCtx(), get_TrxName(), this.p_M_Warehouse_ID);
      if (!this.m_wave.save())
        return false; 
    } 
    MWMSWaveLine waveLine = new MWMSWaveLine(getCtx(), this.m_wave, C_OrderLine_ID, M_WorkOrderComponent_ID, movementQty, get_TrxName());
    this.m_waveLines.add(waveLine);
    this.m_linesSucceeded++;
    return consumeStorage(storages, movementQty);
  }
  
  private boolean consumeStorage(List<MStorage.VO> storages, BigDecimal movementQty) {
    BigDecimal toConsume = movementQty;
    for (VO storage : storages) {
      BigDecimal available = storage.getAvailableQty();
      BigDecimal consume = toConsume;
      if (consume.compareTo(available) > 0)
        consume = available; 
      storage.setQtyOnHand(storage.getQtyOnHand().subtract(consume));
      toConsume = toConsume.subtract(consume);
      if (toConsume.signum() <= 0)
        break; 
    } 
    return (toConsume.signum() <= 0);
  }
  
  private List<MStorage.VO> getStorages(int M_Warehouse_ID, int M_SourceZone_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int M_AttributeSet_ID, boolean allAttributeInstances, Timestamp minGuaranteeDate, boolean FiFo) {
    this.m_lastPP = new SParameter(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, M_AttributeSet_ID, allAttributeInstances, minGuaranteeDate, FiFo);
    this.m_lastStorages = this.m_map.get(this.m_lastPP);
    if (this.m_lastStorages == null) {
      this.m_lastStorages = MStorage.getWarehouse(getCtx(), M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, M_AttributeSet_ID, allAttributeInstances, minGuaranteeDate, FiFo, true, M_SourceZone_ID, trx);
 //       this.m_lastStorages = MStorage.getWarehouse(getCtx(), M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, M_AttributeSet_ID, allAttributeInstances, minGuaranteeDate, FiFo , get_TrxName());

    	this.m_map.put(this.m_lastPP, this.m_lastStorages);
    } 
    return this.m_lastStorages;
  }
  
  static class SParameter {
    public int M_Warehouse_ID;
    
    public int M_Product_ID;
    
    public int M_AttributeSetInstance_ID;
    
    public int M_AttributeSet_ID;
    
    public boolean allAttributeInstances;
    
    public Timestamp minGuaranteeDate;
    
    public boolean FiFo;
    
    protected SParameter(int p_Warehouse_ID, int p_Product_ID, int p_AttributeSetInstance_ID, int p_AttributeSet_ID, boolean p_allAttributeInstances, Timestamp p_minGuaranteeDate, boolean p_FiFo) {
      this.M_Warehouse_ID = p_Warehouse_ID;
      this.M_Product_ID = p_Product_ID;
      this.M_AttributeSetInstance_ID = p_AttributeSetInstance_ID;
      this.M_AttributeSet_ID = p_AttributeSet_ID;
      this.allAttributeInstances = p_allAttributeInstances;
      this.minGuaranteeDate = p_minGuaranteeDate;
      this.FiFo = p_FiFo;
    }
    
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof SParameter) {
        SParameter cmp = (SParameter)obj;
        boolean eq = (cmp.M_Warehouse_ID == this.M_Warehouse_ID && cmp.M_Product_ID == this.M_Product_ID && cmp.M_AttributeSetInstance_ID == this.M_AttributeSetInstance_ID && cmp.M_AttributeSet_ID == this.M_AttributeSet_ID && cmp.allAttributeInstances == this.allAttributeInstances && cmp.FiFo == this.FiFo);
        if (eq)
          if (cmp.minGuaranteeDate != null || this.minGuaranteeDate != null)
            if (cmp.minGuaranteeDate == null || this.minGuaranteeDate == null || !cmp.minGuaranteeDate.equals(this.minGuaranteeDate))
              eq = false;   
        return eq;
      } 
      return false;
    }
    
    public int hashCode() {
      long hash = (this.M_Warehouse_ID + this.M_Product_ID * 2 + this.M_AttributeSetInstance_ID * 3 + this.M_AttributeSet_ID * 4);
      if (this.allAttributeInstances)
        hash *= -1L; 
      if (this.FiFo)
        hash *= -2L; 
      if (hash < 0L)
        hash = -hash + 7L; 
      while (hash > 2147483647L)
        hash -= 2147483647L; 
      if (this.minGuaranteeDate != null) {
        hash += this.minGuaranteeDate.hashCode();
        while (hash > 2147483647L)
          hash -= 2147483647L; 
      } 
      return (int)hash;
    }
  }
}
