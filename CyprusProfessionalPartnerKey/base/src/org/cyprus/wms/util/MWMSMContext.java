package org.cyprus.wms.util;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.cyprus.wms.model.MWMSTaskList;
import org.cyprus.wms.model.MWMSTaskListLine;
import org.cyprus.wms.model.MWMSWarehouseTask;
//import org.cyprusbrs.cwms.model.MTaskList;
//import org.cyprusbrs.cwms.model.MTaskListLine;
//import org.cyprusbrs.cwms.model.MWarehouseTask;
//import org.cyprusbrs.framework.PO;
import org.cyprus.wms.model.X_WMS_TaskList;
import org.cyprus.wms.model.X_WMS_WarehouseTask;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_M_WarehouseTask;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.CLogger;

public class MWMSMContext {
	
	PO po;
	
  public MWMSTaskList m_lastTaskList = null;
  
  private int m_groupingMethod = -1;
  
  private int m_groupSize = 0;
  
  private int m_orderCount = 0;
  
  public ArrayList<MWMSTaskList> m_TaskLists = new ArrayList<MWMSTaskList>();
  
  private final ArrayList<WTask> m_unconfirmedTasks = new ArrayList<WTask>();
  
  private final HashMap<Integer, WTaskList> m_orderMap = new HashMap<Integer, WTaskList>();
  
  static CLogger log = CLogger.getCLogger(MWMSMContext.class);
  
  public void setGroupingParameters(int pickMethod, int clusterSize) {
    this.m_groupingMethod = pickMethod;
    this.m_groupSize = clusterSize;
  }
  
  public boolean generateTask(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_LocatorTo_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int M_InOutLine_ID, int C_Order_ID, int C_OrderLine_ID, int M_WorkOrderOpeComponent_ID, BigDecimal qtyEntered, int C_UOM_ID, int C_DocType_ID, String docAction, String trx) {
    return generateTask(ctx, M_Warehouse_ID, M_Locator_ID, M_LocatorTo_ID, M_Product_ID, M_AttributeSetInstance_ID, M_InOutLine_ID, C_Order_ID, C_OrderLine_ID, M_WorkOrderOpeComponent_ID, qtyEntered, C_UOM_ID, C_DocType_ID, 0, docAction, trx);
  }
  
  public boolean generateTask(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_LocatorTo_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int M_InOutLine_ID, int C_Order_ID, int C_OrderLine_ID, int M_WorkOrderOpeComponent_ID, BigDecimal qtyEntered, int C_UOM_ID, int C_DocType_ID, int C_WaveLine_ID, String docAction, String trx) {
    log.fine("Generating task : Locator : " + M_Locator_ID + " Locator To : " + M_LocatorTo_ID + " Product : " + M_Product_ID + " ASI : " + M_AttributeSetInstance_ID + " Order : " + C_Order_ID + " Orderline : " + C_OrderLine_ID + " WorkOrder Operation Component : " + M_WorkOrderOpeComponent_ID + " QtyEntered : " + qtyEntered + " UOM : " + C_UOM_ID + " Document Type : " + C_DocType_ID + " Wave Line : " + C_WaveLine_ID + " Document Action : " + docAction);
    MWMSTaskList taskList = null;
    int slotNo = -1;
    WTaskList tList = this.m_orderMap.get(Integer.valueOf(C_Order_ID));
    if (tList != null) {
      taskList = tList.taskList;
      slotNo = tList.slotNo;
    } 
    if (taskList == null) {
      if (this.m_lastTaskList == null || this.m_groupingMethod == -1 || (this.m_groupingMethod == 0 && this.m_groupSize > 0 && this.m_orderCount >= this.m_groupSize) || this.m_groupingMethod == 1) {
        String pickMethod = null;
        if (this.m_groupingMethod == 0) {
          pickMethod = X_WMS_TaskList.PICKMETHOD_ClusterPicking;
        } else if (this.m_groupingMethod == 1) {
          pickMethod = X_WMS_TaskList.PICKMETHOD_OrderPicking;
        } 
        this.m_lastTaskList = new MWMSTaskList(ctx, M_Warehouse_ID, C_DocType_ID, pickMethod, trx);
        if (!this.m_lastTaskList.save()) {
          log.severe("Could not create task list ");
          return false;
        } 
        this.m_TaskLists.add(this.m_lastTaskList);
        this.m_orderCount = 0;
      } 
      taskList = this.m_lastTaskList;
      slotNo = ++this.m_orderCount;
      this.m_orderMap.put(Integer.valueOf(C_Order_ID), new WTaskList(slotNo, taskList));
    } 
    MWMSWarehouseTask task = new MWMSWarehouseTask(ctx, M_Warehouse_ID, C_DocType_ID, M_InOutLine_ID, C_OrderLine_ID, M_WorkOrderOpeComponent_ID, M_Locator_ID, M_LocatorTo_ID, M_Product_ID, M_AttributeSetInstance_ID, C_UOM_ID, qtyEntered, C_WaveLine_ID, trx);
    if (!task.save()) {
      log.severe("Could not create task");
      return false;
    } 
    if (docAction != null && docAction.length() > 0) {
      if (docAction.compareTo(X_WMS_WarehouseTask.DOCACTION_Complete) == 0)
        task.setSkipDedicating(true); 
      task.setDocAction(docAction);
      DocumentEngine engine = new DocumentEngine((DocAction)task, docAction);
      if(!engine.processIt(docAction)) {
  //    if (!DocumentEngine.processIt((DocAction)task, docAction)) {
        log.severe("Could not process task");
        return false;
      } 
    } 
    if (!task.save()) {
      log.severe("Could not save task");
      return false;
    } 
    MWMSTaskListLine tlLine = new MWMSTaskListLine(ctx, taskList.getWMS_TaskList_ID(), task.getWMS_WarehouseTask_ID(), slotNo, trx);
    if (!tlLine.save()) {
      log.severe("Could not add task to task list");
      return false;
    } 
    log.fine("Generated task : " + task);
    return true;
  }
  
  public MWMSWarehouseTask generateWarehouseTask(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_LocatorTo_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int M_InOutLine_ID, int C_Order_ID, int C_OrderLine_ID, int M_WorkOrderOpeComponent_ID, BigDecimal qtyEntered, int C_UOM_ID, int C_DocType_ID, int C_WaveLine_ID, String docAction, String trx) {
    log.fine("Generating task : Locator : " + M_Locator_ID + " Locator To : " + M_LocatorTo_ID + " Product : " + M_Product_ID + " ASI : " + M_AttributeSetInstance_ID + " Order : " + C_Order_ID + " Orderline : " + C_OrderLine_ID + " WorkOrder Operation Component : " + M_WorkOrderOpeComponent_ID + " QtyEntered : " + qtyEntered + " UOM : " + C_UOM_ID + " Document Type : " + C_DocType_ID + " Wave Line : " + C_WaveLine_ID + " Document Action : " + docAction);
    MWMSWarehouseTask task = new MWMSWarehouseTask(ctx, M_Warehouse_ID, C_DocType_ID, M_InOutLine_ID, C_OrderLine_ID, M_WorkOrderOpeComponent_ID, M_Locator_ID, M_LocatorTo_ID, M_Product_ID, M_AttributeSetInstance_ID, C_UOM_ID, qtyEntered, C_WaveLine_ID, C_Order_ID, trx);
    return task;
  }
  
  public boolean addTask(int M_Warehouse_ID, int M_Locator_ID, int M_LocatorTo_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int C_Order_ID, int C_OrderLine_ID, int M_WorkOrderOpeComponent_ID, BigDecimal qtyEntered, int C_UOM_ID, int C_DocType_ID, int C_WaveLine_ID, int M_InOutLine_ID) {
    log.fine("Adding task : Locator : " + M_Locator_ID + " Locator To : " + M_LocatorTo_ID + " Product : " + M_Product_ID + " ASI : " + M_AttributeSetInstance_ID + " Order : " + C_Order_ID + " Orderline : " + C_OrderLine_ID + " WorkOrderOpeComponent : " + M_WorkOrderOpeComponent_ID + " QtyEntered : " + qtyEntered + " UOM : " + C_UOM_ID + " WaveLine : " + C_WaveLine_ID + " Document Type : " + C_DocType_ID);
    WTask task = new WTask(M_Warehouse_ID, M_Locator_ID, M_LocatorTo_ID, M_Product_ID, M_AttributeSetInstance_ID, C_Order_ID, C_OrderLine_ID, M_WorkOrderOpeComponent_ID, qtyEntered, C_UOM_ID, C_DocType_ID, C_WaveLine_ID, M_InOutLine_ID);
    this.m_unconfirmedTasks.add(task);
    return true;
  }
  
  public int getUnconfirmedCount() {
    return this.m_unconfirmedTasks.size();
  }
  
  public boolean confirmTasks(Properties ctx, String docAction, String trx) {
    ArrayList<MWMSWarehouseTask> tasksToSave = new ArrayList<MWMSWarehouseTask>();
    for (int i = 0; i < this.m_unconfirmedTasks.size(); i++) {
      WTask task = this.m_unconfirmedTasks.get(i);
      MWMSWarehouseTask warehouseTask = generateWarehouseTask(ctx, task.M_Warehouse_ID, task.M_Locator_ID, task.M_LocatorTo_ID, task.M_Product_ID, task.M_AttributeSetInstance_ID, task.M_InOutLine_ID, task.C_Order_ID, task.C_OrderLine_ID, task.M_WorkOrderOpeComponent_ID, task.qtyEntered, task.C_UOM_ID, task.C_DocType_ID, task.C_WaveLine_ID, docAction, trx);
      tasksToSave.add(warehouseTask);
    } 
//    if (!PO.saveAll(trx, tasksToSave)) {
    if (!po.save(trx)) {

    log.severe("Could not create task");
      return false;
    } 
    ArrayList<MWMSTaskListLine> taskListLines = new ArrayList<MWMSTaskListLine>();
    for (MWMSWarehouseTask task : tasksToSave) {
      MWMSTaskList taskList = null;
      int slotNo = -1;
      WTaskList tList = this.m_orderMap.get(Integer.valueOf(task.getC_Order_ID()));
      if (tList != null) {
        taskList = tList.taskList;
        slotNo = tList.slotNo;
      } 
      if (taskList == null) {
        if (this.m_lastTaskList == null || this.m_groupingMethod == -1 || (this.m_groupingMethod == 0 && this.m_groupSize > 0 && this.m_orderCount >= this.m_groupSize) || this.m_groupingMethod == 1) {
          String pickMethod = null;
          if (this.m_groupingMethod == 0) {
            pickMethod = X_WMS_TaskList.PICKMETHOD_ClusterPicking;
          } else if (this.m_groupingMethod == 1) {
            pickMethod = X_WMS_TaskList.PICKMETHOD_OrderPicking;
          } 
          this.m_lastTaskList = new MWMSTaskList(ctx, task.getM_Warehouse_ID(), task.getC_DocType_ID(), pickMethod, trx);
          if (!this.m_lastTaskList.save()) {
            log.severe("Could not create task list ");
            return false;
          } 
          this.m_TaskLists.add(this.m_lastTaskList);
          this.m_orderCount = 0;
        } 
        taskList = this.m_lastTaskList;
        slotNo = ++this.m_orderCount;
        this.m_orderMap.put(Integer.valueOf(task.getC_Order_ID()), new WTaskList(slotNo, taskList));
      } 
      if (docAction != null && docAction.length() > 0) {
        if (docAction.compareTo(X_WMS_WarehouseTask.DOCACTION_Complete) == 0)
          task.setSkipDedicating(true); 
        task.setDocAction(docAction);
        DocumentEngine engine = new DocumentEngine((DocAction)task, docAction);
        if(!engine.processIt(docAction)) {
      //  if (!DocumentEngine.processIt((DocAction)task, docAction)) {
          log.severe("Could not process task");
          return false;
        } 
      } 
      MWMSTaskListLine tlLine = new MWMSTaskListLine(ctx, taskList.getWMS_TaskList_ID(), task.getWMS_WarehouseTask_ID(), slotNo, trx);
      taskListLines.add(tlLine);
      log.fine("Generated task : " + task);
    } 
//    if (!PO.saveAll(trx, tasksToSave)) {
    if (!po.save(trx)) {

    log.severe("Could not create task");
      return false;
    } 
//    if (!PO.saveAll(trx, taskListLines)) {
    if (!po.save(trx)) {

    log.severe("Could not add task to task list");
      return false;
    } 
    removeUnconfirmedTasks();
    return true;
  }
  
  public boolean removeUnconfirmedTasks() {
    this.m_unconfirmedTasks.clear();
    return true;
  }
  
  static class WTask {
    public int M_Warehouse_ID;
    
    public int M_Locator_ID;
    
    public int M_LocatorTo_ID;
    
    public int M_Product_ID;
    
    public int M_AttributeSetInstance_ID;
    
    public int C_Order_ID;
    
    public int C_OrderLine_ID;
    
    public int M_WorkOrderOpeComponent_ID;
    
    public BigDecimal qtyEntered;
    
    public int C_UOM_ID;
    
    public int C_DocType_ID;
    
    public int C_WaveLine_ID;
    
    public int M_InOutLine_ID;
    
    protected WTask(int p_M_Warehouse_ID, int p_M_Locator_ID, int p_M_LocatorTo_ID, int p_M_Product_ID, int p_M_AttributeSetInstance_ID, int p_C_Order_ID, int p_C_OrderLine_ID, int p_M_WorkOrderOpeComponent_ID, BigDecimal p_qtyEntered, int p_C_UOM_ID, int p_C_DocType_ID, int p_C_WaveLine_ID, int p_M_InOutLine_ID) {
      this.M_Warehouse_ID = p_M_Warehouse_ID;
      this.M_Locator_ID = p_M_Locator_ID;
      this.M_LocatorTo_ID = p_M_LocatorTo_ID;
      this.M_Product_ID = p_M_Product_ID;
      this.M_AttributeSetInstance_ID = p_M_AttributeSetInstance_ID;
      this.C_Order_ID = p_C_Order_ID;
      this.C_OrderLine_ID = p_C_OrderLine_ID;
      this.M_WorkOrderOpeComponent_ID = p_M_WorkOrderOpeComponent_ID;
      this.qtyEntered = p_qtyEntered;
      this.C_UOM_ID = p_C_UOM_ID;
      this.C_DocType_ID = p_C_DocType_ID;
      this.C_WaveLine_ID = p_C_WaveLine_ID;
      this.M_InOutLine_ID = p_M_InOutLine_ID;
    }
  }
  
  static class WTaskList {
    private int slotNo = 0;
    
    private MWMSTaskList taskList = null;
    
    public WTaskList(int slotNo, MWMSTaskList taskList) {
      this.slotNo = slotNo;
      this.taskList = taskList;
    }
  }
}

