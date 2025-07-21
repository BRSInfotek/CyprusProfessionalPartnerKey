package org.cyprus.mfg.util;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import org.cyprus.mfg.model.MMFGRouting;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderClass;
import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
//import org.cyprusbrs.util.BOMDropLines;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;
import org.cyprusbrs.util.Util;
import org.cyprusbrs.util.ValueNamePair;

public class CreateWorkOrder {
  protected static CLogger log = CLogger.getCLogger(CreateWorkOrder.class);
  
  static int errorProcess = 0;
  
  static int errorWorkOrder = 0;
  
  static int errorCopyingOpRequirements = 0;
  
  public static MMFGWorkOrder newWorkOrder(int workOrderClassID, int warehouseID, int locatorID, int planRunID, int productID, int uomID, int routingID, int bomID, Timestamp dateAcc, Timestamp dateScheduleFrom, String p_DocAction, BigDecimal quantity, int clientID, int orgID, int mrpPlannedOrderID, Properties ctx, Trx trx) {
    MMFGWorkOrder order = newWorkOrder(workOrderClassID, warehouseID, locatorID, planRunID, productID, uomID, routingID, bomID, dateAcc, dateScheduleFrom, p_DocAction, quantity, clientID, orgID, ctx, trx);
    if (mrpPlannedOrderID != 0)
      order.setVarMRP_PlannedOrder_ID(mrpPlannedOrderID); 
    return order;
  }
  
  public static MMFGWorkOrder newWorkOrder(int workOrderClassID, int warehouseID, int locatorID, int planRunID, int productID, int uomID, int routingID, int bomID, Timestamp dateAcc, Timestamp dateScheduleFrom, String p_DocAction, BigDecimal quantity, int clientID, int orgID, Properties ctx, Trx trx) {
    if (trx == null) {
      log.saveError("Error", "No Trx Found");
      return null;
    } 
    if (clientID == 0 || orgID == 0) {
      log.saveError("Error", "Incorrect Client or Org passed");
      return null;
    } 
    MMFGWorkOrder workorder = new MMFGWorkOrder(ctx, 0, trx.getTrxName());
    workorder.setClientOrg(clientID, orgID);
    if (workOrderClassID == 0) {
      log.fine("Work Order Class ID not passed to the API");
      MMFGWorkOrderClass woc = MMFGWorkOrderClass.getWorkOrderClass(ctx, trx.getTrxName(), "AD_Org_ID IN (0, " + orgID + " )AND (WOType = 'S' OR WOType IS NULL)");
      if (woc == null) {
        log.saveError("Error", "No WorkOrder Class Defined");
        return null;
      } 
      workOrderClassID = woc.getMFG_WorkOrderClass_ID();
    } 
    if (warehouseID != 0 && locatorID != 0) {
      MWarehouse warehouse = MWarehouse.get(ctx, warehouseID);
      if (!MWarehouse.IsLocatorInWarehouse(warehouseID, locatorID)) {
        log.saveError("Error", "Locator is not included under" + warehouse.getName());
        return null;
      } 
    } 
    if (warehouseID == 0 && locatorID == 0) {
      log.fine("warehouseID not passed to the API");
      log.fine("locatorID not passed to the API");
      MWarehouse[] warehouses = MWarehouse.getForOrg(ctx, orgID);
      warehouseID = warehouses[0].getM_Warehouse_ID();
      if (warehouses.length < 1) {
        log.saveError("Error", "No Warehouse Defined");
        return null;
      } 
      MLocator loc = warehouses[0].getDefaultLocator();
      locatorID = loc.getM_Locator_ID();
      if (loc == null) {
        log.saveError("Error", "No Locator Defined");
        return null;
      } 
    } else if (warehouseID != 0 && locatorID == 0) {
      log.fine("warehouseID passed to the API");
      log.fine("locatorID not passed to the API");
      MWarehouse warehouse = new MWarehouse(ctx, warehouseID, trx.getTrxName());
      MLocator loc = warehouse.getDefaultLocator();
      locatorID = loc.getM_Locator_ID();
      if (loc == null) {
        log.saveError("Error", "No Locator Defined");
        return null;
      } 
    } 
    if (warehouseID == 0) {
      log.saveError("Error", "No warehouse Defined");
      return null;
    } 
    if (locatorID == 0) {
      log.saveError("Error", "No Locator Defined");
      return null;
    } 
    if (productID == 0) {
      log.fine("productID not passed to the API");
      log.saveError("Error", "No Product Defined");
      return null;
    } 
    MProduct product = MProduct.get(ctx, productID);
    if (uomID == 0) {
      log.fine("uomID not passed to the API");
      uomID = product.getC_UOM_ID();
      if (uomID == 0) {
        log.saveError("Error", "No uomID Defined");
        return null;
      } 
    } 
    if (routingID == 0) {
      log.fine("Routing ID not passed to the API");
      MMFGRouting routing = MMFGRouting.getDefaultRouting(ctx, productID, warehouseID);
      if (routing == null) {
        log.warning("No Default Routing Defined");
        routing = MMFGRouting.getRouting(ctx, productID, warehouseID);
        if (routing == null) {
          log.saveError("Error", "No Routing defined");
          return null;
        } 
      } 
      routingID = routing.getMFG_Routing_ID();
    } 
    if (bomID == 0) {
      log.fine("bomID not passed to the API");
      MBOM[] boms = MBOM.getOfProduct(ctx, productID, trx.getTrxName(), "BomType = 'A' AND BOMUse='M' AND IsActive='Y'");
      if (boms == null || 0 == boms.length) {
        log.saveError("Error", "No Current Active Manufacturing BOM Defined");
        return null;
      } 
      bomID = boms[0].getM_BOM_ID();
    } 
    if (dateAcc == null)
      dateAcc = new Timestamp(System.currentTimeMillis()); 
    if (dateScheduleFrom == null)
      dateScheduleFrom = new Timestamp(System.currentTimeMillis()); 
    if (planRunID != 0)
      workorder.setMRP_PlanRun_ID(planRunID); 
    if ((((quantity != null) ? 1 : 0) & ((quantity.compareTo(BigDecimal.ZERO) > 0) ? 1 : 0)) != 0) {
      workorder.setQtyEntered(quantity);
    } else {
      workorder.setQtyEntered(Env.ONE);
    } 
    workorder.setMFG_WorkOrderClass_ID(workOrderClassID);
    workorder.set_ValueNoCheck("M_Warehouse_ID", Integer.valueOf(warehouseID));
    workorder.setM_Locator_ID(locatorID);
    workorder.setM_Product_ID(productID);
    workorder.setC_UOM_ID(uomID);
    workorder.setMFG_Routing_ID(routingID);
    workorder.setM_BOM_ID(bomID);
    if (workOrderClassID != 0) {
      MMFGWorkOrderClass woc = new MMFGWorkOrderClass(ctx, workOrderClassID, trx.getTrxName());
      if (woc.getWO_DocType_ID() == 0) {
        log.saveError("Error", Msg.parseTranslation(ctx, "@NotFound@ @WOType@"));
        return null;
      } 
      if (!woc.isActive()) {
        log.saveError("Error", Msg.parseTranslation(ctx, "@MFG_WorkOrderClass_ID@ @NotActive@"));
        return null;
      } 
      workorder.setC_DocType_ID(woc.getWO_DocType_ID());
    } 
    workorder.setDateAcct(dateAcc);
    workorder.setDateScheduleFrom(dateScheduleFrom);
    if (!workorder.save(trx.getTrxName())) {
      ValueNamePair pp = CLogger.retrieveError();
      if (pp != null) {
        log.saveError("Error", pp.getName());
      } else {
        log.saveError("Error", "Cannot save WorkOrder");
      } 
      errorWorkOrder++;
      return null;
    } 
    int workorderid = workorder.getMFG_WorkOrder_ID();
    boolean m_isprocesscalled = true;
    MMFGCopyOperations m_mCopyOperations = null;
    BOMDropLines bomDropLines = null;
    BigDecimal qty = Env.ONE;
    m_mCopyOperations = new MMFGCopyOperations();
    if (workorder.getMFG_Routing_ID() != 0)
      if (!m_mCopyOperations.addOperationLines(ctx, trx.getTrxName(), routingID, workorderid, m_isprocesscalled)) {
        log.warning("Work Operation Requirements not copied");
        errorCopyingOpRequirements++;
        return null;
      }  
    bomDropLines = new BOMDropLines();
    if (workorder.getM_BOM_ID() != 0 && 
      !bomDropLines.addBOMLines(ctx, trx, productID, qty, bomID, null, null, workorderid, m_isprocesscalled)) {
      log.warning("Work Operation Requirements not copied");
      errorCopyingOpRequirements++;
      return null;
    } 
    if (workorder.getM_BOM_ID() != 0) {
      ArrayList<Integer> OperationsProcessed = new ArrayList<Integer>();
      OperationsProcessed.addAll(m_mCopyOperations.operationsProcessed());
      for (Integer i : bomDropLines.operationsProcessed()) {
        if (!OperationsProcessed.contains(i))
          OperationsProcessed.add(i); 
      } 
      log.info("@Processed@=" + OperationsProcessed.size() + "  Operations");
    } else {
      log.info("@Processed@= " + m_mCopyOperations.operationsProcessed().size() + " Operations");
    } 
    return workorder;
  }
  
  public static boolean processIt(int workOrderID, String p_DocAction, String trx, Properties ctx) {
    if (workOrderID == 0) {
      log.saveError("Error", "No such WorkOrder exists");
      errorProcess++;
      return false;
    } 
    MMFGWorkOrder workorder = new MMFGWorkOrder(ctx, workOrderID, trx);
    if (!Util.isEmpty(p_DocAction)) {
      workorder.setDocAction(p_DocAction);
      DocumentEngine engine = new DocumentEngine((DocAction)workorder, p_DocAction);
      if(!engine.processIt(p_DocAction)) {
      //if (!DocumentEngine.processIt((DocAction)workorder, p_DocAction)) {
        log.saveError("Error", workorder.getProcessMsg());
        errorProcess++;
        return false;
      } 
      if (!workorder.save(trx)) {
        log.warning("Not saved");
        errorProcess++;
        return false;
      } 
    } 
    return true;
  }
  
  public static int WorkOrderErrors() {
    return errorWorkOrder;
  }
  
  public static int CopyErrors() {
    return errorCopyingOpRequirements;
  }
  
  public static int ProcessErrors() {
    return errorProcess;
  }
}

