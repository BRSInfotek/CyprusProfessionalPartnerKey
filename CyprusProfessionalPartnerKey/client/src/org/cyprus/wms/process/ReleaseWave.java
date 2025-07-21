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
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mfg.model.X_MFG_WorkCenter;
import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mfg.model.X_MFG_WorkOrderComponent;
import org.cyprus.mfg.model.X_MFG_WorkOrderOperation;
import org.cyprus.wms.model.MWMSMMStrategySet;
import org.cyprus.wms.model.MWMSTaskList;
import org.cyprus.wms.model.X_WMS_WarehouseTask;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProcess;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.framework.X_C_Order;
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

public class ReleaseWave extends SvrProcess {
  private String p_selectClause = null;
  
  Trx trx;
  private int p_M_Warehouse_ID = 0;
  
  private int p_C_Wave_ID = 0;
  
  private String p_DeliveryRuleOverride = "";
  
  private int p_NoOfRows = 0;
  
  private String p_PickMethod = null;
  
  private int p_ClusterSize = 0;
  
  private int p_C_DocTypeTask_ID = 0;
  
  private String p_DocAction = X_WMS_WarehouseTask.DOCACTION_Prepare;
  
  private int p_M_SourceZone_ID = 0;
  
  private int p_M_Locator_ID = 0;
  
  private String m_insertSQL = null;
  
  private int p_T_Wave_ID = 0;
  
  private boolean p_IsPrintPickList = false;
  
  private int m_linesSucceeded = 0;
  
  private boolean m_CMFGLicensed = false;
  
  private String p_WorkOrderFulfillmentRule = "";
  
  private MWMSTaskList[] m_taskLists;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null || element.getParameter_To() != null)
        if (name.equals("SelectClause")) {
          this.p_selectClause = (String)element.getParameter();
        } else if (name.equals("M_Warehouse_ID")) {
          this.p_M_Warehouse_ID = element.getParameterAsInt();
        } else if (name.equals("C_Wave_ID")) {
          this.p_C_Wave_ID = element.getParameterAsInt();
        } else if (name.equals("DeliveryRuleOverride")) {
          this.p_DeliveryRuleOverride = (String)element.getParameter();
        } else if (name.equals("WOFulfillRule")) {
          this.p_WorkOrderFulfillmentRule = (String)element.getParameter();
        } else if (name.equals("NoOfRows")) {
          this.p_NoOfRows = element.getParameterAsInt();
        } else if (name.equals("PickMethod")) {
          this.p_PickMethod = (String)element.getParameter();
        } else if (name.equals("ClusterSize")) {
          this.p_ClusterSize = element.getParameterAsInt();
        } else if (name.equals("C_DocTypeTask_ID")) {
          this.p_C_DocTypeTask_ID = element.getParameterAsInt();
        } else if (name.equals("DocAction")) {
          this.p_DocAction = (String)element.getParameter();
        } else if (name.equals("M_SourceZone_ID")) {
          this.p_M_Locator_ID = element.getParameterAsInt();
        } else if (name.equals("M_Locator_ID")) {
          this.p_M_Locator_ID = element.getParameterAsInt();
        } else if (name.equals("T_Wave_ID")) {
          this.p_T_Wave_ID = element.getParameterAsInt();
        } else if (name.equals("IsPrintPickList")) {
          this.p_IsPrintPickList = element.getParameter().equals("Y");
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
  
  private void deleteTable() {
    String deleteSQL = "DELETE FROM T_Wave WHERE T_Wave_ID=?";
 //   int no = DB.executeUpdate(get_TrxName(), deleteSQL, new Object[] { Integer.valueOf(this.p_T_Wave_ID) });
    int no =DB.executeUpdateEx(deleteSQL, new Object[] { Integer.valueOf(this.p_T_Wave_ID) }, get_TrxName());
    this.log.fine("Deleted #" + no);
  }
  
  public MWMSTaskList[] getTaskLists() {
    return this.m_taskLists;
  }
  
  private boolean WOLinesExist(int T_Wave_ID) {
    int count = DB.getSQLValue(get_TrxName(), "SELECT 1 FROM T_Wave w WHERE C_OrderLine_ID IS NULL AND T_Wave_ID=?", new Object[] { Integer.valueOf(T_Wave_ID) });
    return (count > 0);
  }
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    Date startedAt = new Date();
    this.log.info("Started at :" + startedAt);
    this.log.info("M_Warehouse_ID=" + this.p_M_Warehouse_ID + "C_Wave_ID=" + this.p_C_Wave_ID + "PickMethod=" + this.p_PickMethod + "p_ClusterSize=" + this.p_ClusterSize + "p_C_DocType_ID=" + this.p_C_DocTypeTask_ID + "p_DocAction=" + this.p_DocAction + "p_M_SourceZone_ID=" + this.p_M_SourceZone_ID + "p_M_Locator_ID=" + this.p_M_Locator_ID + "T_Wave_ID=" + this.p_T_Wave_ID + "p_IsPrintPickList=" + this.p_IsPrintPickList);
    if (this.p_M_Warehouse_ID == 0 || this.p_C_DocTypeTask_ID == 0 || this.p_M_Locator_ID == 0)
      throw new Exception("@Invalid Arguments@"); 
    MWarehouse wh = MWarehouse.get(getCtx(), this.p_M_Warehouse_ID);
    if (wh == null || !wh.isWMSEnabled())
      throw new Exception("@WarehouseNotWMSEnabled@"); 
    MLocator loc = new MLocator(getCtx(), this.p_M_Locator_ID, get_TrxName());
    if (loc == null || loc.getM_Warehouse_ID() != this.p_M_Warehouse_ID)
      throw new Exception("@WarehouseDestLocatorMismatch@"); 
    int noInserted = prepareTable();
    boolean workorderWave = false;
    if (isCMFGLicensed() && WOLinesExist(this.p_T_Wave_ID)) {
      workorderWave = true;
      this.m_taskLists = createPickTasks(getCtx(), this.p_M_Warehouse_ID, this.p_T_Wave_ID, this.p_M_SourceZone_ID, this.p_M_Locator_ID, this.p_C_DocTypeTask_ID, this.p_DocAction, this.p_PickMethod.equals("C") ? 0 : 1, this.p_ClusterSize, this.p_DeliveryRuleOverride, trx);
    } else {
      this.m_taskLists = createPickTasksForSOO(getCtx(), this.p_M_Warehouse_ID, this.p_T_Wave_ID, this.p_M_SourceZone_ID, this.p_M_Locator_ID, this.p_C_DocTypeTask_ID, this.p_DocAction, this.p_PickMethod.equals("C") ? 0 : 1, this.p_ClusterSize, this.p_DeliveryRuleOverride, trx);
    } 
    deleteTable();
    if (this.m_taskLists == null) {
      ValueNamePair pp = CLogger.retrieveError();
      if (pp != null)
        return pp.getName(); 
    } 
    Date completedAt = new Date();
    this.log.info("Completed at :" + completedAt);
    String retVal = null;
    if (workorderWave) {
      retVal = Msg.getMsg(getCtx(), "@ComponentsProcessed@ : ") + noInserted + ", " + Msg.getMsg(getCtx(), "@ComponentsSucceeded@ : ") + this.m_linesSucceeded;
    } else {
      retVal = Msg.getMsg(getCtx(), "OrderLinesProcessed") + " : " + noInserted + "," + Msg.getMsg(getCtx(), "OrderLinesSucceeded") + " : " + this.m_linesSucceeded + " ";
    } 
    if (this.m_taskLists == null || this.m_taskLists.length == 0)
      return retVal + ",No warehouse tasks created"; 
    boolean IsPrintFormatDef = true;
    for (MWMSTaskList element : this.m_taskLists) {
      addLog(element.getWMS_TaskList_ID(), null, null, element.getDocumentNo());
      if (this.p_IsPrintPickList && IsPrintFormatDef && !workorderWave) {
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
      return retVal + ", " + Msg.getMsg(getCtx(), "NoDocPrintFormat"); 
    return retVal + ",Created TaskLists : " + this.m_taskLists.length;
  }
  
  protected int prepareTable() {
    if (this.p_T_Wave_ID != 0) {
      int noInserted = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM T_Wave WHERE T_Wave_ID=?", new Object[] { Integer.valueOf(this.p_T_Wave_ID) });
      return noInserted;
    } 
    this.p_T_Wave_ID = DB.getSQLValue(get_TrxName(), "SELECT T_WaveID_Seq.NEXTVAL FROM DUAL", new Object[0]);
    ArrayList<Object> list = new ArrayList();
    this.m_insertSQL = "INSERT INTO T_Wave (T_Wave_ID, SeqNo, C_OrderLine_ID, M_WorkOrderComponent_ID, C_WaveLine_ID) SELECT ?, T_WaveSeqNo_Seq.NEXTVAL, t.C_OrderLine_ID, t.M_WorkOrderComponent_ID, t.C_WaveLine_ID FROM ";
    list.add(Integer.valueOf(this.p_T_Wave_ID));
    if (this.p_selectClause != null) {
      this.m_insertSQL += "(" + this.p_selectClause + ") t";
    } else {
      this.m_insertSQL += " (SELECT wlv.C_OrderLine_ID, wlv.C_WaveLine_ID, wlv.M_WorkOrderComponent_ID FROM C_WaveLine_Detail_v wlv ";
      if (this.p_C_Wave_ID != 0) {
        this.m_insertSQL += " WHERE wlv.C_Wave_ID=?";
        list.add(Integer.valueOf(this.p_C_Wave_ID));
      } 
      if (this.p_NoOfRows > 0) {
        this.m_insertSQL += " AND ROWNUM <=? ";
        list.add(Integer.valueOf(this.p_NoOfRows));
      } 
      this.m_insertSQL += ") t";
    } 
    Object[] params = new Object[list.size()];
    list.toArray(params);
    int no = DB.executeUpdateEx( this.m_insertSQL, params,get_TrxName());
    this.log.finest(this.m_insertSQL);
    this.log.fine("Insert (1) #" + no);
    return no;
  }
  
  public MWMSTaskList[] createPickTasksForSOO(Properties ctx, int M_Warehouse_ID, int T_Wave_ID, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int pickMethod, int clusterSize, String deliveryRuleOverride, Trx trx) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<Integer> m_completeOrders = new ArrayList<Integer>();
    MWMSMMStrategySet strategySet = MWMSMMStrategySet.getPickStrategySet(ctx, M_Warehouse_ID, get_TrxName());
    if (strategySet == null) {
      this.log.saveError("Error", "@PickStrategySetNotDefined@");
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
    HashMap<Integer, MOrder> m_orders = new HashMap<Integer, MOrder>();
    String selectOrders = "SELECT * FROM C_Order o WHERE C_Order_ID IN (SELECT ol.C_Order_ID FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) AND t.T_Wave_ID=?)";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(selectOrders, get_TrxName());
      cPreparedStatement.setInt(1, this.p_T_Wave_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MOrder order = new MOrder(getCtx(), rs, get_TrxName());
        m_orders.put(Integer.valueOf(order.getC_Order_ID()), order);
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, selectOrders, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    String selectSQL = "SELECT * FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) WHERE T_Wave_ID=? ORDER BY SeqNo ";
    PreparedStatement pstmt2 = null;
    try {
      CPreparedStatement cPreparedStatement1 = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement1.setInt(1, T_Wave_ID);
      rs = cPreparedStatement1.executeQuery();
      while (rs.next()) {
        String deliveryRule = "";
        MOrderLine orderLine = new MOrderLine(getCtx(), rs, get_TrxName());
        MOrder order = m_orders.get(Integer.valueOf(orderLine.getC_Order_ID()));
        if (deliveryRuleOverride != null && deliveryRuleOverride.length() != 0) {
          deliveryRule = deliveryRuleOverride;
        } else {
          deliveryRule = order.getDeliveryRule();
        } 
        int C_WaveLine_ID = rs.getInt("C_WaveLine_ID");
        if (X_C_Order.DELIVERYRULE_CompleteOrder.equals(deliveryRule)) {
          if (!m_completeOrders.contains(Integer.valueOf(order.getC_Order_ID()))) {
            checkCompleteOrder(strategySet, T_Wave_ID, order, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID,trx);
            m_completeOrders.add(Integer.valueOf(order.getC_Order_ID()));
          } 
          continue;
        } 
        BigDecimal qtyOpen = strategySet.executePickStrategySetForSOO(orderLine, order, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        if (qtyOpen != null) {
          if (qtyOpen.signum() > 0) {
            this.log.fine("Could not fully allocate C_OrderLine_ID : " + orderLine.getC_OrderLine_ID());
            if (X_C_Order.DELIVERYRULE_Availability.equals(deliveryRule) && strategySet.getUnconfirmedCount() > 0) {
              if (!strategySet.confirmTasks(trx,get_TrxName(), docAction))
                throw new IllegalStateException("Could not confirm task"); 
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
    } catch (Exception e) {
      this.log.log(Level.SEVERE, selectSQL, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    strategySet.confirmTasks(trx,get_TrxName(), docAction);
    MWMSTaskList[] taskList = strategySet.getTaskLists();
    return taskList;
  }
  
  public MWMSTaskList[] createPickTasks(Properties ctx, int M_Warehouse_ID, int T_Wave_ID, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int pickMethod, int clusterSize, String deliveryRuleOverride, Trx trx) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<Integer> m_completeOrders = new ArrayList<Integer>();
    ArrayList<Integer> m_completeWorkOrders = new ArrayList<Integer>();
    ArrayList<Integer> m_completeWorkOrderOperations = new ArrayList<Integer>();
    MWMSMMStrategySet strategySet = MWMSMMStrategySet.getPickStrategySet(ctx, M_Warehouse_ID, get_TrxName());
    if (strategySet == null) {
      this.log.saveError("Error", "@PickStrategySetNotDefined@");
      return null;
    } 
    MDocType dt = MDocType.get(getCtx(), C_DocType_ID);
    Timestamp today = new Timestamp(Env.getContextAsTime("#Date"));
    ArrayList<Integer> orgs = new ArrayList<Integer>();
    orgs.add(Integer.valueOf(strategySet.getAD_Org_ID()));
    Boolean periodCheck = MPeriod.isOpen(strategySet.getCtx(), today, dt.getDocBaseType(), strategySet.getAD_Client_ID());
    if (periodCheck ) {
      this.log.saveError("Error", "");
      return null;
    } 
    strategySet.setGroupingParameters(pickMethod, clusterSize);
    String selectSQL = "SELECT ol.C_Order_ID OrderRef_ID, NULL LineRef_ID, t.C_OrderLine_ID LineLineRef_ID, t.C_WaveLine_ID, t.SeqNo, 'SOO' WaveDocBaseType FROM T_Wave t INNER JOIN C_OrderLine ol ON (t.C_OrderLine_ID = ol.C_OrderLine_ID) WHERE T_Wave_ID=? UNION ALL SELECT woo.M_WorkOrder_ID OrderRef_ID, woc.M_WorkOrderOperation_ID LineRef_ID, t.M_WorkOrderComponent_ID LineLineRef_ID, t.C_WaveLine_ID, t.SeqNo, 'WOO' WaveDocBaseType FROM T_Wave t INNER JOIN M_WorkOrderComponent woc ON (woc.M_WorkOrderComponent_ID = t.M_WorkOrderComponent_ID) INNER JOIN M_WorkOrderOperation woo ON (woo.M_WorkOrderOperation_ID = woc.M_WorkOrderOperation_ID) WHERE T_Wave_ID=? ORDER BY SeqNo ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement.setInt(1, T_Wave_ID);
      cPreparedStatement.setInt(2, T_Wave_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        String waveLineType = rs.getString(6);
        int OrderRef_ID = rs.getInt(1);
        int LineRef_ID = rs.getInt(2);
        int LineLineRef_ID = rs.getInt(3);
        X_MFG_WorkOrder xwo = null;
        X_MFG_WorkOrderOperation xwoo = null;
        String workorderFulfillment = "";
        if ("WOO".equals(waveLineType)) {
          if (!isCMFGLicensed())
            return null; 
          xwo = new X_MFG_WorkOrder(ctx, OrderRef_ID, get_TrxName());
          xwoo = new X_MFG_WorkOrderOperation(ctx, LineRef_ID, get_TrxName());
          if (this.p_WorkOrderFulfillmentRule != null && this.p_WorkOrderFulfillmentRule.length() != 0)
            workorderFulfillment = this.p_WorkOrderFulfillmentRule; 
        } 
        MOrder order = null;
        String deliveryRule = "";
        if ("SOO".equals(waveLineType)) {
          order = new MOrder(ctx, OrderRef_ID, get_TrxName());
          if (deliveryRuleOverride != null && deliveryRuleOverride.length() != 0) {
            deliveryRule = deliveryRuleOverride;
          } else {
            deliveryRule = order.getDeliveryRule();
          } 
        } 
        int C_WaveLine_ID = rs.getInt(4);
        if ("SOO".equals(waveLineType) && X_C_Order.DELIVERYRULE_CompleteOrder.equals(deliveryRule)) {
          if (!m_completeOrders.contains(Integer.valueOf(order.getC_Order_ID()))) {
            checkCompleteOrder(strategySet, T_Wave_ID, order, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID, trx);
            m_completeOrders.add(Integer.valueOf(order.getC_Order_ID()));
          } 
          continue;
        } 
        if ("WOO".equals(waveLineType)) {
          if ("O".equals(workorderFulfillment)) {
            if (!m_completeWorkOrders.contains(Integer.valueOf(xwo.getMFG_WorkOrder_ID()))) {
              checkCompleteWorkOrder(strategySet, T_Wave_ID, xwo, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID, trx);
              m_completeWorkOrders.add(Integer.valueOf(xwo.getMFG_WorkOrder_ID()));
            } 
            continue;
          } 
          if ("N".equals(workorderFulfillment)) {
            if (!m_completeWorkOrderOperations.contains(Integer.valueOf(xwoo.getMFG_WorkOrderOperation_ID()))) {
              checkCompleteWorkOrderOperation(strategySet, T_Wave_ID, xwoo, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID, trx);
              m_completeWorkOrderOperations.add(Integer.valueOf(xwoo.getMFG_WorkOrderOperation_ID()));
            } 
            continue;
          } 
        } 
        int tempDestM_Locator_ID = destM_Locator_ID;
        if ("WOO".equals(waveLineType)) {
          int M_WorkCenter_ID = xwoo.getMFG_WorkCenter_ID();
          X_MFG_WorkCenter xwc = new X_MFG_WorkCenter(getCtx(), M_WorkCenter_ID, get_TrxName());
          destM_Locator_ID = xwc.getM_Locator_ID();
          if (destM_Locator_ID == 0)
            destM_Locator_ID = tempDestM_Locator_ID; 
        } 
        BigDecimal qtyOpen = strategySet.executePickStrategySet(waveLineType, LineLineRef_ID, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        destM_Locator_ID = tempDestM_Locator_ID;
        if (qtyOpen != null) {
          if (qtyOpen.signum() > 0) {
            this.log.fine("Could not fully allocate C_OrderLine_ID : " + LineLineRef_ID);
            if ("SOO".equals(waveLineType) && X_C_Order.DELIVERYRULE_Availability.equals(deliveryRule) && strategySet.getUnconfirmedCount() > 0) {
              if (!strategySet.confirmTasks(trx,get_TrxName(), docAction))
                throw new IllegalStateException("Could not confirm task"); 
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
    } catch (Exception e) {
      this.log.log(Level.SEVERE, selectSQL, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    strategySet.confirmTasks(trx,get_TrxName(), docAction);
    MWMSTaskList[] taskList = strategySet.getTaskLists();
    return taskList;
  }
  
  private boolean checkCompleteWorkOrder(MWMSMMStrategySet strategySet, int T_Wave_ID, X_MFG_WorkOrder xwo, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID, Trx trx) throws Exception {
    ArrayList<Object> list = new ArrayList();
    String str = "SELECT wlv.MFG_WorkOrderOperation_ID as MFG_WorkOrderOperation_ID, wlv.MFG_WorkOrder_ID as MFG_WorkOrder_ID, wlv.MFG_WorkOrderComponent_ID as MFG_WorkOrderComponent_ID, wlv.C_WaveLine_ID as C_WaveLine_ID FROM C_WaveLine_Detail_v wlv ";
    if (this.p_C_Wave_ID != 0) {
      str = str + " WHERE wlv.C_Wave_ID=? ";
      list.add(Integer.valueOf(this.p_C_Wave_ID));
    } 
    String sqlCheck = "SELECT Count(*) FROM (" + str + ") work " + "WHERE work.MFG_WorkOrder_ID = ? " + "AND NOT EXISTS " + "(SELECT 1 FROM T_Wave wave WHERE wave.T_Wave_ID = ? AND work.MFG_WorkOrderComponent_ID = wave.MFG_WorkOrderComponent_ID )";
    list.add(Integer.valueOf(xwo.getMFG_WorkOrder_ID()));
    list.add(Integer.valueOf(T_Wave_ID));
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, list.toArray());
    if (no != 0)
      return true; 
    String sql = "SELECT * FROM MFG_WorkOrderComponent woc WHERE MFG_WorkOrderOperation_ID IN (SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ?)AND SupplyType='P' ORDER BY woc.M_Product_ID ";
    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, null);
    ResultSet rs = null;
    boolean availabilityCheck = true;
    int compCount = 0;
    try {
      cPreparedStatement.setInt(1, xwo.getMFG_WorkOrder_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_MFG_WorkOrderComponent xwoc = new X_MFG_WorkOrderComponent(getCtx(), rs, get_TrxName());
        int tempDestM_Locator_ID = destM_Locator_ID;
        X_MFG_WorkOrderOperation xwoo = new X_MFG_WorkOrderOperation(getCtx(), xwoc.getMFG_WorkOrderOperation_ID(), get_TrxName());
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
      sqle.printStackTrace();
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
    this.m_linesSucceeded++;
    commit();
    return true;
  }
  
  private boolean checkCompleteWorkOrderOperation(MWMSMMStrategySet strategySet, int T_Wave_ID, X_MFG_WorkOrderOperation xwoo, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID, Trx trx) throws Exception {
    ArrayList<Object> list = new ArrayList();
    String str = "SELECT wlv.MFG_WorkOrderOperation_ID as MFG_WorkOrderOperation_ID, wlv.MFG_WorkOrder_ID as M_WorkOrder_ID, wlv.M_WorkOrderComponent_ID as MFG_WorkOrderComponent_ID,wlv.C_WaveLine_ID as C_WaveLine_ID FROM C_WaveLine_Detail_v wlv ";
    if (this.p_C_Wave_ID != 0) {
      str = str + " WHERE wlv.C_Wave_ID=? ";
      list.add(Integer.valueOf(this.p_C_Wave_ID));
    } 
    Integer X_M_WorkOrderOperation_ID = Integer.valueOf(xwoo.getMFG_WorkOrderOperation_ID());
    String sqlCheck = "SELECT COUNT(*) FROM (" + str + ") work " + "WHERE work.MFG_WorkOrderOperation_ID = ? " + "AND NOT EXISTS " + "(SELECT 1 FROM T_Wave wave WHERE wave.T_Wave_ID = ? AND work.MFG_WorkOrderComponent_ID = wave.MFG_WorkOrderComponent_ID) ";
    list.add(X_M_WorkOrderOperation_ID);
    list.add(Integer.valueOf(T_Wave_ID));
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, list.toArray());
    if (no != 0)
      return true; 
    String sql = "SELECT * FROM MFG_WorkOrderComponent WHERE MFG_WorkOrderOperation_ID = ? AND SupplyType='P' ORDER BY M_Product_ID ";
    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, null);
    ResultSet rs = null;
    boolean availabilityCheck = true;
    int compCount = 0;
    try {
      cPreparedStatement.setInt(1, X_M_WorkOrderOperation_ID.intValue());
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
          this.log.fine("Could not allocate MFG_WorkOrderComponent_ID : " + xwoc.getMFG_WorkOrderComponent_ID());
          availabilityCheck = false;
          break;
        } 
        if (qtyOpen != null)
          compCount++; 
      } 
    } catch (SQLException e) {
      e.printStackTrace();
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
    this.m_linesSucceeded++;
    return true;
  }
  
  private boolean checkCompleteOrder(MWMSMMStrategySet strategySet, int T_Wave_ID, MOrder order, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID, Trx trx) throws Exception {
    Integer C_Order_ID = Integer.valueOf(order.getC_Order_ID());
    String sqlCheck = "SELECT COUNT(*) FROM C_OrderLine ol  INNER JOIN M_Product p ON (ol.M_Product_ID=p.M_Product_ID AND p.ProductType='I')WHERE C_Order_ID = ?  AND NOT EXISTS  (SELECT 1 FROM T_Wave wave  WHERE wave.T_Wave_ID=?  AND ol.C_OrderLine_ID = wave.C_OrderLine_ID)";
    int no = DB.getSQLValue(get_TrxName(), sqlCheck, new Object[] { C_Order_ID, Integer.valueOf(T_Wave_ID) });
    if (no != 0)
      return true; 
    MOrderLine[] lines = order.getLines(null, "ORDER BY M_Product_ID, M_Warehouse_ID, M_AttributeSetInstance_ID ");
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
  
  private boolean isCMFGLicensed() throws Exception {
    if (this.m_CMFGLicensed)
      return true; 
//    SysEnv se = SysEnv.get("CMFG");
//    if (se == null || !se.checkLicense())
//      throw new Exception(CLogger.retrieveError().getName()); 
    this.m_CMFGLicensed = true;
    return this.m_CMFGLicensed;
  }
}

