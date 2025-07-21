package org.cyprus.mrp.process;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mrp.model.MMRPPlan;
import org.cyprus.mrp.model.MMRPPlanRun;
import org.cyprus.mrp.model.MMRPPlannedDemand;
import org.cyprus.mrp.model.MMRPPlannedOrder;
import org.cyprus.mrp.model.X_MRP_PlannedDemand;
import org.cyprus.mrp.util.EngineUtil;
//import org.cyprusbrs.cmrp.model.MMRPPlan;
//import org.cyprusbrs.cmrp.model.MMRPPlanRun;
//import org.cyprusbrs.cmrp.model.MMRPPlannedDemand;
//import org.cyprusbrs.cmrp.model.MMRPPlannedOrder;
//import org.cyprusbrs.cmrp.util.EngineUtil;
//import org.cyprusbrs.model.X_MRP_PlannedDemand;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.cyprusbrsUserException;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Trx;

public class RunMRPEngine implements Runnable {
	
	Trx trxname;
	
	
	
	
  int pPlanRunID = 0;
  
  String pPriorityImplementation;
  
  int pPlannedDemandID = 0;
  
//  private Trx mTrx = null;
  private Trx mTrx = null;
 
  //Ctx pCtx = null;
  Properties pCtx = null;
  
  private Map<Integer, String[]> productMap = null;
  
  protected static CLogger s_log = CLogger.getCLogger(RunMRPEngine.class);
  
  public RunMRPEngine(int planRunID, String priorityImplementation, int plannedDemandID, Properties ctx, Map<Integer, String[]> map) {
    this.pPlanRunID = planRunID;
    this.pPriorityImplementation = priorityImplementation;
    this.pPlannedDemandID = plannedDemandID;
    this.pCtx = ctx;
    this.productMap = map;
  }
  
  public void run() {
    boolean successfulConnection = false;
    for (int attempt = 0; attempt < 5; attempt++) {
      try {
        this.mTrx = Trx.get("MRPEngine");
        successfulConnection = true;
        break;
      } catch (Exception e) {
        Env.sleep(300);
      } 
    } 
    if (!successfulConnection) {
      s_log.saveError("Error", "error getting DB connection");
      return;
    } 
    MMRPPlannedDemand plannedDemand = new MMRPPlannedDemand(this.pCtx, this.pPlannedDemandID, this.trxname.getTrxName());
    if (plannedDemand == null);
    try {
      MMRPPlanRun run = new MMRPPlanRun(this.pCtx, this.pPlanRunID, this.trxname.getTrxName());
      MMRPPlan plan = new MMRPPlan(this.pCtx, run.getMRP_Plan_ID(), this.trxname.getTrxName());
      if (plan.isPrioritizeOrderOverDemand())
        EngineUtil.setHigherOfSalesVsDemand(plannedDemand, this.pCtx, this.trxname.getTrxName()); 
      runRequirementPlan(plannedDemand);
    } catch (Exception e) {
      s_log.log(Level.SEVERE, "MRP_Engine" + e.getMessage(), e);
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_ErrorDuringRun);
      plannedDemand.save(this.trxname.getTrxName());
      this.mTrx.commit();
      throw new RuntimeException(e);
    } finally {
      if (this.mTrx != null) {
        this.mTrx.commit();
        this.mTrx.close();
        this.mTrx = null;
      } 
    } 
  }
  
  //@Anil21122021
 // void runRequirementPlan(MMRPPlannedDemand plannedDemand) throws cyprusbrsUserException {
  void runRequirementPlan(MMRPPlannedDemand plannedDemand) throws Exception  {
    plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_InProgress);
    plannedDemand.save(this.trxname.getTrxName());
    this.mTrx.commit();
    if (plannedDemand.getQtyRequired().equals(Env.ZERO)) {
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_CompletedRunning);
      plannedDemand.save(this.trxname.getTrxName());
      this.mTrx.commit();
      return;
    } 
    boolean isWorkOrder = false;
    String[] orderType = getOrderType(plannedDemand.getM_Product_ID());
    if (orderType.length == 0)
      return; 
    if (orderType[0].equals("SetUpError")) {
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_CompletedRunning);
      plannedDemand.save(this.trxname.getTrxName());
      this.mTrx.commit();
      return;
    } 
    if (orderType[0].equals("WorkOrder"))
      isWorkOrder = true; 
    MMRPPlanRun run = new MMRPPlanRun(this.pCtx, this.pPlanRunID, this.trxname.getTrxName());
    MMRPPlan plan = new MMRPPlan(this.pCtx, run.getMRP_Plan_ID(), this.trxname.getTrxName());
    BigDecimal qtyToBeOrdered = EngineUtil.getQtyToBeOrdered(plannedDemand, plan.getMRP_Plan_ID(), this.mTrx, this.pCtx);
    if (qtyToBeOrdered.equals(Env.ZERO)) {
      if (isWorkOrder) {
        ArrayList<Integer> demandIDList = MMRPPlannedDemand.getAllExplodeChildrenPlannedDemand(plannedDemand.getMRP_PlannedDemand_ID(), plannedDemand.getMRP_PlanRun_ID(), this.pCtx, this.trxname.getTrxName());
        int linesUpdated = MMRPPlannedDemand.setAllExplodeChildrenPlannedDemand(demandIDList, this.trxname.getTrxName(), this.pCtx, Env.ZERO);
        s_log.fine("Number of Lines Updated to Qty 0 is " + linesUpdated);
      } 
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_CompletedRunning);
      plannedDemand.save(this.trxname.getTrxName());
      this.mTrx.commit();
      return;
    } 
    MMRPPlannedOrder plannedOrder = EngineUtil.createPlannedOrder(plannedDemand, qtyToBeOrdered, isWorkOrder, this.pPlanRunID, this.trxname.getTrxName(), this.pCtx);
    if (plannedOrder == null) {
      this.mTrx.rollback();
      throw new Exception("Planned Order Not Saved");
    } 
    if (!plannedOrder.save(this.trxname.getTrxName())) {
      this.mTrx.rollback();
      throw new Exception("Planned Order Not Saved");
    } 
    this.mTrx.commit();
    if (isWorkOrder) {
      if (!EngineUtil.updateExplodedDemand(plannedDemand, qtyToBeOrdered, this.pPlanRunID, this.trxname.getTrxName(), this.pCtx)) {
        this.mTrx.rollback();
        throw new Exception("Update Exploded demand failed");
      } 
      this.mTrx.commit();
      MMRPPlannedDemand[] PlannedDemandList = MMRPPlannedDemand.getTreeChildrenPlannedDemand(plannedDemand, this.pPlanRunID, this.trxname.getTrxName(), this.pCtx);
      for (MMRPPlannedDemand plannedChildDemand : PlannedDemandList) {
        runRequirementPlan(plannedChildDemand);
        this.mTrx.commit();
      } 
    } 
    plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_CompletedRunning);
    plannedDemand.save(this.trxname.getTrxName());
    this.mTrx.commit();
  }
  
  private String[] getOrderType(int productID) {
    if (this.productMap.containsKey(Integer.valueOf(productID)))
      return this.productMap.get(Integer.valueOf(productID)); 
    return null;
  }
}
