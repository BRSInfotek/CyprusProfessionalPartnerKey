package org.cyprus.mrp.process;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import org.cyprus.mrp.model.MMRPMasterDemand;
import org.cyprus.mrp.model.MMRPMasterDemandLine;
import org.cyprus.mrp.model.MMRPPlan;
import org.cyprus.mrp.model.MMRPPlanRun;
import org.cyprus.mrp.model.MMRPPlannedDemand;
import org.cyprus.mrp.model.MMRPProductAudit;
import org.cyprus.mrp.model.X_MRP_Plan;
import org.cyprus.mrp.model.X_MRP_PlannedAvailability;
import org.cyprus.mrp.model.X_MRP_PlannedDemand;
import org.cyprus.mrp.util.MRPFreezeUtil;
import org.cyprus.mrp.util.PlanUtil;
import org.cyprusbrs.framework.MBOMProduct;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductPO;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.framework.X_M_BOM;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.TimeUtil;
import org.cyprusbrs.util.Trx;

public class RunMRP extends SvrProcess {
	
	Trx trxname;
	
	org.cyprusbrs.model.PO PO;
	
  private Timestamp mBackOrderDate;
  
  private int pMasterDemandID = 0;
  
  private Timestamp mPlanRunDate = null;
  
  private int pPlanID = 0;
  
  private int pNumberOfThreads = 0;
  
  private MMRPPlanRun mMRPPlanRun = null;
  
  private MMRPPlan mMRPPlan = null;
  
  private Timestamp mStartDate = null;
  
  private Timestamp mPlanEndDate = null;
  
  private String mPriorityImplementation = X_MRP_Plan.PRIORITYIMPLEMENTATION_Manufacture;
  
  private int pPeriodFromID = 0;
  
  private int pPeriodBackOrderID = 0;
  
  private MPeriod mStartPeriod = null;
  
  //@Anil21122021
  //Trx mTrx = null;
  Trx mTrx=null ;
  
  private Map<Integer, String[]> mProductMap = null;
  
  protected static CLogger s_log = CLogger.getCLogger(RunMRP.class);
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CMRP");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    String returnString = "MRP Ran Successfully";
    if (this.pPeriodFromID == 0)
      throw new Exception("@FillMandatory@ @C_Period_From_ID@"); 
    if (this.pMasterDemandID == 0)
      throw new Exception("@FillMandatory@ @MRP_MasterDemand_ID@"); 
    if (this.pPlanID == 0)
      throw new Exception("@FillMandatory@ @MRP_Plan_ID@"); 
    if (this.pPeriodBackOrderID == 0) {
      this.pPeriodBackOrderID = this.pPeriodFromID;
      this.log.info("Period BackOrder ID = " + this.pPeriodBackOrderID);
    } 
    if (!this.mMRPPlan.isActive())
      throw new Exception("@MRP_Plan_ID@ is Not Active"); 
    MMRPMasterDemand demand = new MMRPMasterDemand(getCtx(), this.pMasterDemandID, get_TrxName());
    demand.setIsPreviouslyFrozen(true);
    int lines = 0;
    if (!demand.isFrozen()) {
      MRPFreezeUtil freezeUtil = new MRPFreezeUtil();
      lines = freezeUtil.freeze(demand, get_TrxName());
    } 
    demand.save(get_TrxName());
//    get_TrxName().commit();
  //  trxname.commit();
    this.log.fine("Frozen - Demand Lines = " + lines);
    this.mTrx = Trx.get("RunMRP");
    setDates();
    try {
      MMRPMasterDemand masterDemand = new MMRPMasterDemand(getCtx(), this.pMasterDemandID, this.get_TrxName());
      if (masterDemand == null)
        return "Master Demand not found. MRP did not run"; 
      MMRPMasterDemandLine[] masterDemandLineList = MMRPMasterDemandLine.getOfMRPMasterDemand(masterDemand, this.mStartPeriod.getC_Period_ID(), this.get_TrxName());
      if (masterDemandLineList.length == 0) {
        this.log.warning(masterDemand.getName() + ": " + "Does not have any Products for the periods" + "chosen in the Plan paremeter");
        return "No products found in Master Demand for the periods chosen. MRP did not run";
      } 
      this.mMRPPlanRun = new MMRPPlanRun(getCtx(), 0, this.get_TrxName());
      this.mMRPPlanRun.setC_Period_From_ID(this.pPeriodFromID);
      this.mMRPPlanRun.setC_Period_BackOrder_ID(this.pPeriodBackOrderID);
      this.mMRPPlanRun.setMRP_MasterDemand_ID(this.pMasterDemandID);
      this.mMRPPlanRun.setMRP_Plan_ID(this.pPlanID);
      this.mMRPPlanRun.setClientOrg((org.cyprusbrs.model.PO)this.mMRPPlan);
      if (!this.mMRPPlanRun.save(this.get_TrxName())) {
        this.mTrx.rollback();
        throw new Exception("Plan Run Not Saved");
      } 
      this.mProductMap = (Map)new HashMap<Integer, String>();
      this.log.info("Started populating planned demand");
      if (!populatePlannedDemand(masterDemandLineList)) {
        this.mTrx.rollback();
        throw new Exception("Error Populating Planned Demand");
      } 
      this.log.info("Successfully populated planned demand");
      this.log.info("Started populating Audit tables");
      if (!populateAuditTables()) {
        this.mTrx.rollback();
        throw new Exception("Error Populating Audit Data");
      } 
      this.log.info("Successfully populated Audit tables");
      this.log.info("Started populating Planned Receipt");
      if (!populatePlannedReceipt()) {
        this.mTrx.rollback();
        throw new Exception("Error Populating Planned Receipt");
      } 
      this.log.info("Successfully populated Planned Receipt");
      this.log.info("Started populating Planned Availability");
      if (!populatePlannedAvailability()) {
        this.mTrx.rollback();
        throw new Exception("Error Populating Planned Availability");
      } 
      this.log.info("Successfully populated Planned Availability");
      if (!populateAllPlannedAvailability()) {
        this.mTrx.rollback();
        throw new Exception("Error Populating Planned Availability for all periods");
      } 
      this.mMRPPlan.setDateLastRun(new Timestamp(System.currentTimeMillis()));
//      this.mMRPPlan.save(this.get_TrxName());
      this.mMRPPlanRun.save(mTrx.getTrxName());
      this.mTrx.commit();
      this.log.info("Starting the MRP Engine");
//      if (!runEngine()) {
//        this.mTrx.rollback();
//        throw new Exception("Error in MRP Engine");
//      } 
      this.mTrx.commit();
      int no = setPlanRunsInactive();
      if (no == -1)
        throw new Exception("Error in Upddating Plan Runs"); 
      this.mMRPPlanRun.setIsActive(true);
      if (!this.mMRPPlanRun.save(this.get_TrxName())) {
        this.mTrx.rollback();
        throw new Exception("Plan Run Not Saved");
      } 
      this.mTrx.commit();
    } catch (Exception e) {
      s_log.log(Level.SEVERE, "MRP_Plan" + e.getMessage(), e);
      this.mMRPPlanRun.setIsActive(false);
//      this.mMRPPlanRun.save(this.get_TrxName());
      this.mMRPPlanRun.save(mTrx.getTrxName());
      returnString = e.getMessage().concat(" No further Progress");
    } finally {
      if (this.mTrx != null) {
        this.mTrx.commit();
        this.mTrx.close();
      } 
    } 
    return returnString;
  }
  
  private boolean populateAllPlannedAvailability() {
	  String strPeriods = PlanUtil.getPeriodIdsFromPlan(this.mStartPeriod, this.pPlanID, this.mTrx, getCtx());
	  String sql = "INSERT INTO MRP_PlannedAvailability( AD_Client_ID, AD_Org_ID, DateExpected, C_Period_ID,  MRP_PlanRun_ID, M_Product_ID, QtyExpected , QtyCalculated , C_UOM_ID ,created,createdby,updated,updatedby,MRP_PLANNEDAVAILABILITY_ID)SELECT  pa.AD_Client_ID ,pa.AD_Org_ID , TRUNC(period.StartDate, 'DD') , period.C_Period_ID , pa.MRP_PlanRun_ID, pa.M_Product_ID, 0, 0, p.C_UOM_ID,SYSDATE,pa.createdby,SYSDATE,pa.updatedby,? FROM MRP_Product_Audit pa INNER JOIN M_Product p ON (pa.M_Product_ID = p.M_Product_ID) INNER JOIN C_Period period ON (period.IsActive ='Y') WHERE pa.MRP_PlanRun_ID = ? AND period.C_Period_ID IN ( " + strPeriods + " )";
	  String Query = "SELECT MRP_PLANNEDAVAILABILITY_SEQ.nextval FROM DUAL";
	  int sequence = DB.getSQLValue (null, Query);   
	  int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
	  ArrayList<Object> list = new ArrayList<Object>();
	  list.add(Integer.valueOf(sequence));
	  list.add(Integer.valueOf(runID));
	  MPeriod prevPeriod = MPeriod.getPreviousPeriod(this.mStartPeriod, getCtx(), this.mTrx);
	  if (prevPeriod == null) {
		  MPeriod nextPeriod = MPeriod.getNextPeriod(this.mStartPeriod, getCtx(), this.mTrx);
		  if (nextPeriod == null)
			  return true; 
	  } 
	  Object[] params = new Object[list.size()];
	  list.toArray(params);
	  //   int no = DB.executeUpdate(this.mTrx, sql, params);
	  int no = DB.executeUpdateEx(sql, params, this.get_TrxName());
	  if (no == -1)
		  return false; 
	  return true;
  }
  
  private int setPlanRunsInactive() {
    String sql = "UPDATE MRP_PlanRun SET IsActive = 'N'  WHERE MRP_Plan_ID = ? ";
    ArrayList<Object> list = new ArrayList();
    list.add(Integer.valueOf(this.pPlanID));
    Object[] params = new Object[list.size()];
    list.toArray(params);
   // int no = DB.executeUpdate(this.mTrx, sql, params);
    int no = DB.executeUpdateEx(sql, params, this.get_TrxName());
    return no;
  }
  
  private boolean runEngine() throws InterruptedException {
    ArrayList<Timestamp> dates = new ArrayList<Timestamp>();
    MPeriod endPeriod = MPeriod.get(getCtx(), this.mMRPPlan.getC_Period_To_ID());
    int dateNo = 0;
    MPeriod period = null;
    MPeriod[] allPeriods = MPeriod.getAllPeriodsInRange(this.mStartPeriod, endPeriod, this.mMRPPlan.getC_Calendar_ID(), getCtx(), this.mTrx);
    int totalPeriods = allPeriods.length;
    for (int periodCount = 0; periodCount < totalPeriods; periodCount++) {
      period = allPeriods[periodCount];
      dates.add(dateNo, period.getStartDate());
      dateNo++;
    } 
    for (int periodNo = 0; periodNo < totalPeriods; periodNo++) {
      this.log.fine("getting the demand for the period" + allPeriods[periodNo].getName());
      MMRPPlannedDemand[] plannedDemand = MMRPPlannedDemand.getUnProcessedPlannedDemand(this.mMRPPlanRun.getMRP_PlanRun_ID(), 0, this.get_TrxName(), getCtx(), dates.get(periodNo));
      int totalDemadLinesInPeriod = plannedDemand.length;
      this.log.fine("Total demand lines in Period is " + totalDemadLinesInPeriod);
      if (totalDemadLinesInPeriod != 0) {
        int threadsPerPeriod = 3;
        if (this.pNumberOfThreads > 0)
          threadsPerPeriod = this.pNumberOfThreads; 
        ExecutorService threadpool = Executors.newFixedThreadPool(threadsPerPeriod);
        Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        for (int demandInPeriod = 0; demandInPeriod < totalDemadLinesInPeriod; demandInPeriod++) {
          this.mTrx.commit();
          tasks.add(Executors.callable(new RunMRPEngine(this.mMRPPlanRun.getMRP_PlanRun_ID(), this.mPriorityImplementation, plannedDemand[demandInPeriod].getMRP_PlannedDemand_ID(), getCtx(), this.mProductMap)));
        } 
        this.log.fine("Calling the threads to act upon each Planned Demand");
        List<Future<Object>> futures = threadpool.invokeAll(tasks);
        int nDone = 0;
        int nCancelled = 0;
        for (int i = 0; i < futures.size(); i++) {
          if (((Future)futures.get(i)).isCancelled()) {
            nCancelled++;
          } else {
            try {
              ((Future)futures.get(i)).get();
            } catch (ExecutionException e) {
              break;
            } 
            if (((Future)futures.get(i)).isDone())
              nDone++; 
          } 
        } 
        if (nDone == futures.size()) {
          threadpool.shutdown();
          while (!threadpool.isTerminated())
            threadpool.shutdown(); 
        } else {
          this.log.warning("Error running thread ");
          threadpool.shutdown();
          while (!threadpool.isTerminated())
            threadpool.shutdown(); 
          return false;
        } 
      } 
    } 
    return true;
  }
  
  private boolean populatePlannedAvailability() {
	    Timestamp startDate = this.mStartDate;
	    MPeriod startPeriodOfmStartDate = MPeriod.getOfCalendar(getCtx(), this.mMRPPlan.getC_Calendar_ID(), this.mStartDate);
	    if (startPeriodOfmStartDate != null)
	      startDate = startPeriodOfmStartDate.getStartDate(); 

	    //    String sql = "INSERT INTO MRP_PlannedAvailability( AD_Client_ID, AD_Org_ID, DateExpected, C_Period_ID,  MRP_PlanRun_ID, M_Product_ID, QtyExpected , QtyCalculated , C_UOM_ID , AvailabilityStatus )SELECT  max(run.AD_Client_ID),max(run.AD_Org_ID), ? , ? , max(run.MRP_PlanRun_ID),M_Product_ID, Sum(QtyExpected), Sum(QtyExpected), max(C_UOM_ID) , ? FROM (SELECT -Sum(QtyExpected) QtyExpected, M_Product_ID, MRP_PlanRun_ID , C_UOM_ID FROM  MRP_Order_Audit  WHERE isSOTrx='Y' AND MRP_PlanRun_ID = ? AND TRUNC(DateExpected, 'DD') BETWEEN ? AND ? GROUP BY M_Product_ID, MRP_PlanRun_ID, C_UOM_ID UNION ALL SELECT Sum(QtyExpected) as QtyExpected, M_Product_ID, MRP_PlanRun_ID, C_UOM_ID FROM  MRP_PlannedReceipt WHERE MRP_PlanRun_ID = ? AND TRUNC(DateExpected,'DD') BETWEEN ? AND ? GROUP BY M_Product_ID, MRP_PlanRun_ID, C_UOM_ID UNION ALL SELECT Sum(QtyOnHand) as QtyExpected, invaudit.M_Product_ID, MRP_PlanRun_ID, C_UOM_ID FROM MRP_Inventory_Audit invaudit INNER JOIN M_Product product ON ( invaudit.M_Product_ID = product.M_Product_ID )  WHERE invaudit.MRP_PlanRun_ID = ? GROUP by invaudit.M_Product_ID, MRP_PlanRun_ID, C_UOM_ID UNION ALL SELECT 0 as QtyExpected, pa.M_Product_ID, MRP_PlanRun_ID, C_UOM_ID FROM MRP_Product_Audit pa INNER JOIN M_Product product ON ( pa.M_Product_ID = product.M_Product_ID )WHERE pa.MRP_PlanRun_ID = ? GROUP BY pa.M_Product_ID, MRP_PlanRun_ID, C_UOM_ID) tab INNER JOIN MRP_PlanRun run ON tab.MRP_PlanRun_ID = run.MRP_PlanRun_ID GROUP BY  M_Product_ID, run.MRP_PlanRun_ID ";
	    String sql = "INSERT INTO MRP_PlannedAvailability( AD_Client_ID, AD_Org_ID, DateExpected, C_Period_ID,  MRP_PlanRun_ID, M_Product_ID, QtyExpected , QtyCalculated , C_UOM_ID , AvailabilityStatus,created,createdby,updated,updatedby,MRP_PLANNEDAVAILABILITY_ID )SELECT  max(run.AD_Client_ID),max(run.AD_Org_ID), ? , ? , max(run.MRP_PlanRun_ID),M_Product_ID, Sum(QtyExpected), Sum(QtyExpected), max(C_UOM_ID) , ?,SYSDATE,run.createdby,SYSDATE,run.updatedby,? FROM (SELECT -Sum(QtyExpected) QtyExpected, M_Product_ID, MRP_PlanRun_ID , C_UOM_ID FROM  MRP_Order_Audit  WHERE isSOTrx='Y' AND MRP_PlanRun_ID = ? AND TRUNC(DateExpected, 'DD') BETWEEN ? AND ? GROUP BY M_Product_ID, MRP_PlanRun_ID, C_UOM_ID UNION ALL SELECT Sum(QtyExpected) as QtyExpected, M_Product_ID, MRP_PlanRun_ID, C_UOM_ID FROM  MRP_PlannedReceipt WHERE MRP_PlanRun_ID = ? AND TRUNC(DateExpected,'DD') BETWEEN ? AND ? GROUP BY M_Product_ID, MRP_PlanRun_ID, C_UOM_ID UNION ALL SELECT Sum(invaudit.QtyOnHand) as QtyExpected, invaudit.M_Product_ID, MRP_PlanRun_ID, invaudit.C_UOM_ID FROM MRP_Inventory_Audit invaudit INNER JOIN M_Product product ON ( invaudit.M_Product_ID = product.M_Product_ID )  WHERE invaudit.MRP_PlanRun_ID = ? GROUP by invaudit.M_Product_ID, invaudit.MRP_PlanRun_ID, invaudit.C_UOM_ID UNION ALL SELECT 0 as QtyExpected, pa.M_Product_ID, MRP_PlanRun_ID, C_UOM_ID FROM MRP_Product_Audit pa INNER JOIN M_Product product ON ( pa.M_Product_ID = product.M_Product_ID )WHERE pa.MRP_PlanRun_ID = ? GROUP BY pa.M_Product_ID, MRP_PlanRun_ID, C_UOM_ID) tab INNER JOIN MRP_PlanRun run ON tab.MRP_PlanRun_ID = run.MRP_PlanRun_ID GROUP BY  M_Product_ID, run.MRP_PlanRun_ID,run.createdby,run.updatedby ";
	    
	    String Query = "SELECT MRP_PLANNEDAVAILABILITY_SEQ.nextval FROM DUAL";
		 int sequence = DB.getSQLValue (null, Query);
	    int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
	    ArrayList<Object> list = new ArrayList<Object>();
	    list.add(TimeUtil.getDay(TimeUtil.addDays(TimeUtil.getDay(this.mStartPeriod.getStartDate()), -1)));
	    MPeriod prevPeriod = MPeriod.getPreviousPeriod(this.mStartPeriod, getCtx(), this.mTrx);
	    if (prevPeriod == null) {
	      list.add(Integer.valueOf(this.mStartPeriod.getC_Period_ID()));
	      list.add(X_MRP_PlannedAvailability.AVAILABILITYSTATUS_NotUpdated);
	    } else {
	      list.add(Integer.valueOf(prevPeriod.getC_Period_ID()));
	      list.add(X_MRP_PlannedAvailability.AVAILABILITYSTATUS_Updated);
	    } 
	    list.add(Integer.valueOf(sequence));
	    list.add(Integer.valueOf(runID));
	    list.add(TimeUtil.getDay(startDate));
	    list.add(TimeUtil.getDay(TimeUtil.addDays(TimeUtil.getDay(this.mStartPeriod.getStartDate()), -1)));
	    list.add(Integer.valueOf(runID));
	    list.add(TimeUtil.getDay(startDate));
	    if (prevPeriod == null) {
	      list.add(TimeUtil.getDay(this.mStartPeriod.getStartDate()));
	    } else {
	      list.add(TimeUtil.getDay(TimeUtil.addDays(TimeUtil.getDay(this.mStartPeriod.getStartDate()), -1)));
	    } 
	    list.add(Integer.valueOf(runID));
	    list.add(Integer.valueOf(runID));
	    Object[] params = new Object[list.size()];
	    list.toArray(params);
	   // int no = DB.executeUpdate(this.mTrx, sql, params);
	    int no = DB.executeUpdateEx(sql, params, this.get_TrxName());
	    if (no == -1)
	      return false; 
	    return true;
	   }
  
  private boolean populatePlannedReceipt() {
	  String sql = "INSERT INTO MRP_PlannedReceipt( AD_Client_ID, AD_Org_ID, DateExpected, MRP_PlanRun_ID, M_Product_ID, QtyExpected, C_Period_ID, C_UOM_ID,created,createdby,updated,updatedby,MRP_PLANNEDRECEIPT_ID )SELECT MAX(run.AD_Client_ID), MAX(run.AD_Org_ID), TRUNC(period.startdate, 'DD') AS DateExpected,MAX(run.MRP_PlanRun_ID), M_Product_ID, SUM(QtyExpected), MAX(period.C_Period_ID), MAX(C_UOM_ID),SYSDATE,run.createdby, SYSDATE,run.updatedby,? FROM (SELECT SUM(QtyExpected) QtyExpected, M_Product_ID,  TRUNC(DateExpected,'DD') DateExpected, MAX(MRP_PlanRun_ID) MRP_PlanRun_ID, MAX(C_UOM_ID) C_UOM_ID FROM  MRP_Order_Audit ordAudit WHERE MRP_PlanRun_ID = ? AND IsSOTrx='N' GROUP BY M_Product_ID, TRUNC(DateExpected,'DD') UNION ALL SELECT SUM(QtyExpected) QtyExpected, M_Product_ID, TRUNC(DateExpected,'DD') DateExpected, MAX(MRP_PlanRun_ID) MRP_PlanRun_ID, MAX(C_UOM_ID) C_UOM_ID FROM  MRP_WorkOrder_Audit workAudit WHERE MRP_PlanRun_ID = ? GROUP BY M_Product_ID, TRUNC(DateExpected,'DD')) tab INNER JOIN MRP_PlanRun run ON tab.MRP_PlanRun_ID = run.MRP_PlanRun_ID INNER JOIN C_Period period ON period.C_Year_ID IN (SELECT C_Year_ID FROM C_Year WHERE C_Year.C_Calendar_ID = ? ) WHERE TRUNC(DateExpected,'DD') BETWEEN TRUNC(period.startDate,'DD') AND TRUNC(period.endDate,'DD') GROUP BY M_Product_ID, TRUNC(period.startdate,'DD'),run.createdby,run.updatedby";
	  String Query = "SELECT MRP_PLANNEDRECEIPT_SEQ.nextval FROM DUAL";
	  int sequence = DB.getSQLValue (null, Query);
	  int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
	  ArrayList<Object> list = new ArrayList<Object>();
	  list.add(Integer.valueOf(sequence));
	  list.add(Integer.valueOf(runID));
	  list.add(Integer.valueOf(runID));
	  MPeriod period = MPeriod.get(getCtx(), this.mMRPPlan.getC_Period_To_ID());
	  list.add(Integer.valueOf(period.getC_Calendar_ID()));
	  Object[] params = new Object[list.size()];
	  list.toArray(params);
	  //   int no = DB.executeUpdate(this.mTrx, sql, params);
	  int no = DB.executeUpdateEx(sql, params, this.get_TrxName());
	  if (no == -1)
		  return false; 
	  return true;
  }
  
  private boolean populateAuditTables() {
    int no = populateOrderAudit();
    if (no == -1) {
      s_log.severe("Error populating Order Audit");
      return false;
    } 
    this.log.fine("No. of records populated in MRP_Order_Audit is " + no);
    this.mTrx.commit();
    no = populateWorkOrderAudit();
    if (no == -1) {
      s_log.severe("Error populating WorkOrder Audit");
      return false;
    } 
    this.log.fine("No. of records populated in MRP_WorkOrder_Audit is " + no);
    this.mTrx.commit();
//   int no = populateInventoryAudit();
     no = populateInventoryAudit();
    if (no == -1) {
      s_log.severe("Error populating Inventory Audit");
      return false;
    } 
    this.log.fine("No. of records populated in MRP_Inventory_Audit is " + no);
    this.mTrx.commit();
    return true;
  }
  
  private int populateInventoryAudit() {
    MRole role = MRole.getDefault(getCtx(), false);
    CPreparedStatement cPreparedStatement = null;
    String sql = null;
    String sql3 = null;
    String PRIORITYIMPLEMENTATION = null;
    int no = 0;
  String sql1 = "select PRIORITYIMPLEMENTATION from MRP_Plan plan left join MRP_PlanRun planrun on (plan.MRP_Plan_ID= planrun.MRP_Plan_ID) where planrun.MRP_PlanRun_ID = ?";
  
  PreparedStatement pstmt = null;
  ResultSet rs = null;
  try {
    cPreparedStatement = DB.prepareStatement(sql1, get_TrxName());
    cPreparedStatement.setInt(1, this.mMRPPlanRun.getMRP_PlanRun_ID());
    rs = cPreparedStatement.executeQuery();
    if (rs.next()) {
       PRIORITYIMPLEMENTATION = rs.getString("PRIORITYIMPLEMENTATION");
    } 
    rs.close();
    cPreparedStatement.close();
    cPreparedStatement = null;
  } catch (Exception e) {
    s_log.log(Level.SEVERE, sql1, e);
  }
  finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
  
  if(PRIORITYIMPLEMENTATION.equals("M"))
  {
//	   sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
//	    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDWO,DATEEXPECTEDWO,PRIORITYIMPLEMENTATION )" + role.addAccessSQL
//	    		("SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(nvl(stg.QtyOnHand,0)"
//	    				+ " - nvl(stg.QtyAllocated,0) - nvl(stg.QtyDedicated,0)), pr.isbom,pro.C_BPartner_ID,sum(nvl(stg.QTYRESERVED,0))"
//	    				+ ",sum(nvl(stg.qtyordered,0)), nvl(mdl.qty,0)- sum(nvl(stg.QtyOnHand,0) +nvl(stg.QTYRESERVED,0)-"
//	    				+ "nvl(stg.QtyOrdered,0)) ,sysdate + nvl(pro.DELIVERYTIME_PROMISED,0) ,plan.PRIORITYIMPLEMENTATION  FROM MRP_PlanRun run "
//	    				+ "INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID) Inner Join "
//	    				+ " M_Product pr on (pr.M_Product_ID = mrpproduct.M_Product_ID)left join MRP_MASTERDEMAND md on"
//	    				+ " (md.MRP_MASTERDEMAND_ID =run.MRP_MASTERDEMAND_ID )left join MRP_MASTERDEMANDLINE mdl on "
//	    				+ "(mdl.MRP_MASTERDEMAND_ID = md.MRP_MASTERDEMAND_ID)Left Join M_Product_PO pro on"
//	    				+ " (pr.M_Product_ID = pro.M_Product_ID)INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? )"
//	    				+ " left join MRP_Plan plan on (plan.MRP_Plan_ID = run.MRP_Plan_ID)"
//	    				+ " INNER JOIN M_Storage stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND "
//	    				+ "stg.M_Product_ID = mrpproduct.M_Product_ID )", "run", true, false) + " AND run.MRP_PlanRun_ID = ? "
//	    				+ "GROUP BY stg.M_Product_ID , pr.isbom,pro.C_BPartner_ID,mdl.qty,pro.DELIVERYTIME_PROMISED,plan.PRIORITYIMPLEMENTATION ";
	  
	
//	  sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
//	    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDWO,DATEEXPECTEDWO,PRIORITYIMPLEMENTATION,CREATED,CREATEDBY,UPDATED,UPDATEDBY )" + role.addAccessSQL
//	    		("SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(nvl(stg.QtyOnHand,0)"
//	    				+ " - nvl(stg.QtyAllocated,0) - nvl(stg.QtyDedicated,0)), pr.isbom,pro.C_BPartner_ID,sum(nvl(stg.QTYRESERVED,0))"
//	    				+ ",sum(nvl(stg.qtyordered,0)), nvl(mdl.qty,0)- sum(nvl(stg.QtyOnHand,0) +nvl(stg.QTYRESERVED,0)-"
//	    				+ "nvl(stg.QtyOrdered,0)) ,sysdate + nvl(pro.DELIVERYTIME_PROMISED,0) ,plan.PRIORITYIMPLEMENTATION,SYSDATE,plan.CREATEDBY,SYSDATE,plan.UPDATEDBY   FROM MRP_PlanRun run "
//	    				+ " left join MRP_MASTERDEMAND md on"
//	    				+ " (md.MRP_MASTERDEMAND_ID =run.MRP_MASTERDEMAND_ID )left join MRP_MASTERDEMANDLINE mdl on "
//	    				+ "(mdl.MRP_MASTERDEMAND_ID = md.MRP_MASTERDEMAND_ID) "
//	    				+ " Inner Join M_Product pr on (pr.M_Product_ID = mdl.M_Product_ID)"
//	    				+ "Left Join M_Product_PO pro on"
//	    				+ " (pr.M_Product_ID = pro.M_Product_ID)INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? )"
//	    				+ " left join MRP_Plan plan on (plan.MRP_Plan_ID = run.MRP_Plan_ID)"
//	    				+ " INNER JOIN M_Storage stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND "
//	    				+ "stg.M_Product_ID = mdl.M_Product_ID )", "run", true, false) + " AND run.MRP_PlanRun_ID = ? "
//	    				+ "GROUP BY stg.M_Product_ID , pr.isbom,pro.C_BPartner_ID,mdl.qty,pro.DELIVERYTIME_PROMISED,plan.PRIORITYIMPLEMENTATION,plan.CREATEDBY,plan.UPDATEDBY  ";
// 

	  String sql2 = "select mp.M_Product_ID,mp.isbom from M_Product mp,MRP_MASTERDEMANDLINE mdl,MRP_MASTERDEMAND md,MRP_PlanRun run where mp.M_Product_ID=mdl.M_Product_ID and mdl.MRP_MASTERDEMAND_ID=md.MRP_MASTERDEMAND_ID and md.MRP_MASTERDEMAND_ID=run.MRP_MASTERDEMAND_ID and run.MRP_PlanRun_ID=?";
	 
	  String ISBOM=null;
	  int M_Product_ID=0;
	  try {
		    cPreparedStatement = DB.prepareStatement(sql2, get_TrxName());
		    cPreparedStatement.setInt(1, this.mMRPPlanRun.getMRP_PlanRun_ID());
		    rs = cPreparedStatement.executeQuery();
		    if (rs.next()) {
		    	M_Product_ID = rs.getInt("M_Product_ID");
		    	System.out.println("M_Product_ID ="+M_Product_ID);
		    	ISBOM = rs.getString("ISBOM");
		    	System.out.println("ISBOM ="+ISBOM);
		    	
		    	if(ISBOM.equals("Y"))
		    	{
//		    		 sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
//		    		    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDWO,DATEEXPECTEDWO,PRIORITYIMPLEMENTATION )" + role.addAccessSQL
//		    		    		("select distinct md.AD_Client_ID,md.AD_Org_ID,max(run.MRP_PlanRun_ID),mpb.M_PRODUCTBOM_ID,mpb.BOMQTY,mpr.name product,ms.QTYONHAND,ms.QTYRESERVED,"
//		    	                     + "   ms.QTYORDERED,(mdl.qty*mpb.BOMQTY)-ms.QTYONHAND+ms.QTYRESERVED-ms.QTYORDERED expectedwoqty, "
//		    	+ "cb.C_BPartner_ID from M_Product_BOM mpb,M_Product mp,M_Product mpr,m_storage ms,mrp_plan pln "
//		    	+ ",mrp_planrun run,mrp_masterdemand md ,mrp_masterdemandline mdl, m_locator lc,C_BPartner_Product cbp,C_BPartner cb "
//		    	+ "where mpb.M_Product_ID=mp.M_Product_ID and mpr.M_Product_ID=mpb.M_PRODUCTBOM_ID and "
//		    	+ "run.mrp_plan_id = pln.mrp_plan_id and pln.m_locator_id = ms.m_locator_id and "
//		    	+ "run.mrp_masterdemand_id = md.mrp_masterdemand_id and mdl.mrp_masterdemand_id = md.mrp_masterdemand_id and "
//		    	+ "mdl.m_product_id = mp.m_product_id and cbp.m_product_id = mp.m_product_id and "
//		    	+ "cb.C_BPartner_ID = cbp.C_BPartner_ID ", "run", true, false) + " and mdl.M_Product_id=?"
//		    	+ "GROUP BY ms.M_Product_ID, md.AD_Client_ID,md.AD_Org_ID,mpb.M_PRODUCTBOM_ID,mpb.BOMQTY,ms.QTYONHAND,ms.QTYRESERVED,ms.QTYORDERED,mpr.name,mdl.qty,cb.C_BPartner_ID ";	
		    	
		    		try {
		    		 sql3 = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDWO, " + 
		    				"DATEEXPECTEDWO,PRIORITYIMPLEMENTATION,CREATED,CREATEDBY,UPDATED,UPDATEDBY,MRP_INVENTORY_AUDIT_ID ) " + 
		    				"select distinct md.AD_Client_ID,md.AD_Org_ID,max(run.MRP_PlanRun_ID),mpr.M_Product_ID product,ms.QTYONHAND,mpr.isbom , " + 
		    				"cb.C_BPartner_ID ,ms.QTYRESERVED, ms.QTYORDERED,(mdl.qty*mpb.BOMQTY)-ms.QTYONHAND+ms.QTYRESERVED-ms.QTYORDERED QTYEXPECTEDWO,sysdate + nvl(prpo.DELIVERYTIME_PROMISED,0) " + 
		    				"DATEEXPECTEDWO,pln.PRIORITYIMPLEMENTATION,SYSDATE,pln.CREATEDBY,SYSDATE,pln.UPDATEDBY,?   from M_Product mp, M_Product_BOM mpb,M_Product mpr, m_storage ms,mrp_plan pln ,mrp_planrun run,mrp_masterdemand md , " + 
		    				"mrp_masterdemandline mdl, m_locator lc,C_BPartner_Product cbp,C_BPartner cb,M_Product_PO prpo where mpb.M_Product_ID=mp.M_Product_ID " + 
		    				"and mpr.M_Product_ID=mpb.M_PRODUCTBOM_ID and run.mrp_plan_id = pln.mrp_plan_id and pln.m_locator_id = ms.m_locator_id and run.mrp_masterdemand_id = md.mrp_masterdemand_id " + 
		    				"and mdl.mrp_masterdemand_id = md.mrp_masterdemand_id and mdl.m_product_id = mp.m_product_id  and cbp.m_product_id = mp.m_product_id and cb.C_BPartner_ID = cbp.C_BPartner_ID  " + 
		    				"and prpo.M_Product_ID=mp.M_Product_ID  AND mp.M_Product_id =" + M_Product_ID
		    				+ " GROUP BY ms.M_Product_ID, md.AD_Client_ID,md.AD_Org_ID,mpb.M_PRODUCTBOM_ID,mpb.BOMQTY, " + 
		    				"ms.QTYONHAND,ms.QTYRESERVED,ms.QTYORDERED,mpr.name,mdl.qty,cb.C_BPartner_ID,mpr.M_Product_ID, mpr.isbom,pln.PRIORITYIMPLEMENTATION,prpo.DELIVERYTIME_PROMISED,pln.CREATEDBY,pln.UPDATEDBY";
		    		
//		    		sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
//		    	    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDWO,DATEEXPECTEDWO,PRIORITYIMPLEMENTATION )"
//		    				//+ role.addAccessSQL
//		    	    		
//		    	    	+	" select distinct md.AD_Client_ID,md.AD_Org_ID,max(run.MRP_PlanRun_ID),mpr.M_Product_ID product,ms.QTYONHAND,mpr.isbom ,cb.C_BPartner_ID ,ms.QTYRESERVED,"
//		    + " ms.QTYORDERED,(mdl.qty*mpb.BOMQTY)-ms.QTYONHAND+ms.QTYRESERVED-ms.QTYORDERED expectedwoqty,sysdate + nvl(prpo.DELIVERYTIME_PROMISED,0) expectedwodate,pln.PRIORITYIMPLEMENTATION  "
//		    + "from M_Product mp, M_Product_BOM mpb,M_Product mpr,"
//		    + " m_storage ms,mrp_plan pln ,mrp_planrun run,mrp_masterdemand md ,mrp_masterdemandline mdl, m_locator lc,C_BPartner_Product cbp,C_BPartner cb,M_Product_PO prpo "
//		     + "where mpb.M_Product_ID=mp.M_Product_ID and mpr.M_Product_ID=mpb.M_PRODUCTBOM_ID and run.mrp_plan_id = pln.mrp_plan_id and pln.m_locator_id = ms.m_locator_id "
//		     + "and run.mrp_masterdemand_id = md.mrp_masterdemand_id and mdl.mrp_masterdemand_id = md.mrp_masterdemand_id and mdl.m_product_id = mp.m_product_id "
//		    + " and cbp.m_product_id = mp.m_product_id and cb.C_BPartner_ID = cbp.C_BPartner_ID  and "
//		     + "prpo.M_Product_ID=mp.M_Product_ID  AND mp.M_Product_id =  " + M_Product_ID
//		        + " GROUP BY ms.M_Product_ID, md.AD_Client_ID,md.AD_Org_ID,mpb.M_PRODUCTBOM_ID,mpb.BOMQTY,ms.QTYONHAND,ms.QTYRESERVED,ms.QTYORDERED,mpr.name,mdl.qty,"
//		       + "cb.C_BPartner_ID,mpr.M_Product_ID, mpr.isbom,pln.PRIORITYIMPLEMENTATION,prpo.DELIVERYTIME_PROMISED ";
		    		
		    		 System.out.println("sql ="+sql3);
		    		 String Query = "SELECT MRP_INVENTORY_AUDIT_SEQ.nextval FROM DUAL";
		    		 int sequence = DB.getSQLValue (null, Query);
		    		 int warehouseID = this.mMRPPlan.getM_Warehouse_ID();
		    		   int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
		    		    ArrayList<Object> list = new ArrayList<Object>();
		    		    list.add(Integer.valueOf(sequence));
		    		  //  list.add(Integer.valueOf(warehouseID));
		    		 //   list.add(Integer.valueOf(runID));
		    		  //  list.add(Integer.valueOf(M_Product_ID));
		    		   // list.add(Integer.valueOf(ISBOM));
		    		 Object[] params = new Object[list.size()];
		    		    list.toArray(params);
					no = DB.executeUpdateEx(sql3,params, this.get_TrxName());
		    		}
		    		catch (Exception e) {
		    		    s_log.log(Level.SEVERE, sql3, e);
		    		  } finally {
//		    		    if (rs != null)
		    		      
		    		        try {
		    					rs.close();
		    				} catch (SQLException e) {
		    					
		    					e.printStackTrace();
		    				}
		    		  }
		    		 
		    	}
		    	else
		    	{
		    		try
		    		{
		    		  sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
		    		    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDWO,DATEEXPECTEDWO,PRIORITYIMPLEMENTATION,CREATED,CREATEDBY,UPDATED,UPDATEDBY,MRP_INVENTORY_AUDIT_ID)" + role.addAccessSQL
		    		    		("SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(nvl(stg.QtyOnHand,0)"
		    		    				+ " - nvl(stg.QtyAllocated,0) - nvl(stg.QtyDedicated,0)), pr.isbom,pro.C_BPartner_ID,sum(nvl(stg.QTYRESERVED,0))"
		    		    				+ ",sum(nvl(stg.qtyordered,0)), nvl(mdl.qty,0)- sum(nvl(stg.QtyOnHand,0) +nvl(stg.QTYRESERVED,0)-"
		    		    				+ "nvl(stg.QtyOrdered,0)) ,sysdate + nvl(pro.DELIVERYTIME_PROMISED,0) ,plan.PRIORITYIMPLEMENTATION,SYSDATE,plan.CREATEDBY,SYSDATE,plan.UPDATEDBY,?   FROM MRP_PlanRun run "
		    		    				+ " left join MRP_MASTERDEMAND md on"
		    		    				+ " (md.MRP_MASTERDEMAND_ID =run.MRP_MASTERDEMAND_ID )left join MRP_MASTERDEMANDLINE mdl on "
		    		    				+ "(mdl.MRP_MASTERDEMAND_ID = md.MRP_MASTERDEMAND_ID) "
		    		    				+ " Inner Join M_Product pr on (pr.M_Product_ID = mdl.M_Product_ID)"
		    		    				+ "Left Join M_Product_PO pro on"
		    		    				+ " (pr.M_Product_ID = pro.M_Product_ID)INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? )"
		    		    				+ " left join MRP_Plan plan on (plan.MRP_Plan_ID = run.MRP_Plan_ID)"
		    		    				+ " INNER JOIN M_Storage stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND "
		    		    				+ "stg.M_Product_ID = mdl.M_Product_ID )", "run", true, false) + " AND run.MRP_PlanRun_ID = ? "
		    		    				+ "GROUP BY stg.M_Product_ID , pr.isbom,pro.C_BPartner_ID,mdl.qty,pro.DELIVERYTIME_PROMISED,plan.PRIORITYIMPLEMENTATION,plan.CREATEDBY,plan.UPDATEDBY  ";
		    	 
		    		  String Query = "SELECT MRP_INVENTORY_AUDIT_SEQ.nextval FROM DUAL";
			    		 int sequence = DB.getSQLValue (null, Query);
			    		 int warehouseID = this.mMRPPlan.getM_Warehouse_ID();
			    		   int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
			    		    ArrayList<Object> list = new ArrayList<Object>();
			    		    list.add(Integer.valueOf(sequence));
			    		  //  list.add(Integer.valueOf(warehouseID));
			    		 //   list.add(Integer.valueOf(runID));
			    		  //  list.add(Integer.valueOf(M_Product_ID));
			    		   // list.add(Integer.valueOf(ISBOM));
			    		 Object[] params = new Object[list.size()];
			    		    list.toArray(params);
						no = DB.executeUpdateEx(sql3,params, this.get_TrxName());
			    		}
			    		catch (Exception e) {
			    		    s_log.log(Level.SEVERE, sql3, e);
			    		  } finally {
//			    		    if (rs != null)
			    		      
			    		        try {
			    					rs.close();
			    				} catch (SQLException e) {
			    					
			    					e.printStackTrace();
			    				}
			    		  }
			    	
		    		  
		    	}
		    	
		    } 
		    rs.close();
		    cPreparedStatement.close();
		    cPreparedStatement = null;
		  } catch (Exception e) {
		    s_log.log(Level.SEVERE, sql2, e);
		  } 
	  finally
		{
			DB.close(rs, cPreparedStatement);
			rs = null; cPreparedStatement = null;
		}
  
  }
	 
  else 
  {
//    String sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand )" + role.addAccessSQL(" SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(stg.QtyOnHand - stg.QtyAllocated - stg.QtyDedicated) FROM MRP_PlanRun run INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID) INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? ) INNER JOIN M_Storage_V stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND stg.M_Product_ID = mrpproduct.M_Product_ID )", "run", true, false) + " AND run.MRP_PlanRun_ID = ? " + "GROUP BY stg.M_Product_ID  ";
 //   String sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDPO,DATEEXPECTEDPO )" + role.addAccessSQL("SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(nvl(stg.QtyOnHand,0) - nvl(stg.QtyAllocated,0) - nvl(stg.QtyDedicated,0)), pr.isbom,pro.C_BPartner_ID,sum(nvl(stg.QTYRESERVED,0)),sum(nvl(stg.qtyordered,0)), nvl(mdl.qty,0)- sum(nvl(stg.QtyOnHand,0) +nvl(stg.QTYRESERVED,0)-nvl(stg.QtyOrdered,0)) ,sysdate + nvl(pro.DELIVERYTIME_PROMISED,0)  FROM MRP_PlanRun run INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID)Inner Join  M_Product pr on (pr.M_Product_ID = mrpproduct.M_Product_ID)left join MRP_MASTERDEMAND md on (md.MRP_MASTERDEMAND_ID =run.MRP_MASTERDEMAND_ID )left join MRP_MASTERDEMANDLINE mdl on (mdl.MRP_MASTERDEMAND_ID = md.MRP_MASTERDEMAND_ID)Left Join M_Product_PO pro on (pr.M_Product_ID = pro.M_Product_ID)INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? ) INNER JOIN M_Storage stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND stg.M_Product_ID = mrpproduct.M_Product_ID )", "run", true, false) + " AND run.MRP_PlanRun_ID = ? " + "GROUP BY stg.M_Product_ID , pr.isbom,pro.C_BPartner_ID,mdl.qty,pro.DELIVERYTIME_PROMISED ";

	  //     sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
//    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDPO,DATEEXPECTEDPO,PRIORITYIMPLEMENTATION )" + role.addAccessSQL
//    		("SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(nvl(stg.QtyOnHand,0)"
//    				+ " - nvl(stg.QtyAllocated,0) - nvl(stg.QtyDedicated,0)), pr.isbom,pro.C_BPartner_ID,sum(nvl(stg.QTYRESERVED,0))"
//    				+ ",sum(nvl(stg.qtyordered,0)), nvl(mdl.qty,0)- sum(nvl(stg.QtyOnHand,0) +nvl(stg.QTYRESERVED,0)-"
//    				+ "nvl(stg.QtyOrdered,0)) ,sysdate + nvl(pro.DELIVERYTIME_PROMISED,0) ,plan.PRIORITYIMPLEMENTATION  FROM MRP_PlanRun run "
//    				+ "INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID) Inner Join "
//    				+ " M_Product pr on (pr.M_Product_ID = mrpproduct.M_Product_ID)left join MRP_MASTERDEMAND md on"
//    				+ " (md.MRP_MASTERDEMAND_ID =run.MRP_MASTERDEMAND_ID )left join MRP_MASTERDEMANDLINE mdl on "
//    				+ "(mdl.MRP_MASTERDEMAND_ID = md.MRP_MASTERDEMAND_ID)Left Join M_Product_PO pro on"
//    				+ " (pr.M_Product_ID = pro.M_Product_ID)INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? )"
//    				+ " left join MRP_Plan plan on (plan.MRP_Plan_ID = run.MRP_Plan_ID)"
//    				+ " INNER JOIN M_Storage stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND "
//    				+ "stg.M_Product_ID = mrpproduct.M_Product_ID )", "run", true, false) + " AND run.MRP_PlanRun_ID = ? "
//    				+ "GROUP BY stg.M_Product_ID , pr.isbom,pro.C_BPartner_ID,mdl.qty,pro.DELIVERYTIME_PROMISED,plan.PRIORITYIMPLEMENTATION ";
  

//	  String Query = "SELECT MRP_INVENTORY_AUDIT_SEQ.nextval FROM DUAL";
//	  int sequence = DB.getSQLValue (null, Query);
//	  System.out.println(sequence);
	  sql = "INSERT INTO MRP_Inventory_Audit ( AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,M_Product_ID,QtyOnHand,ISBOM,"
	    		+ "C_BPARTNER_ID,QTYRESERVED,QTYORDERED,QTYEXPECTEDPO,DATEEXPECTEDPO,PRIORITYIMPLEMENTATION,CREATED,CREATEDBY,UPDATED,UPDATEDBY,MRP_INVENTORY_AUDIT_ID )" + role.addAccessSQL
	    		("SELECT max(run.AD_Client_ID),max(run.AD_Org_ID),max(run.MRP_PlanRun_ID),stg.M_Product_ID,sum(nvl(stg.QtyOnHand,0)"
	    				+ " - nvl(stg.QtyAllocated,0) - nvl(stg.QtyDedicated,0)), pr.isbom,pro.C_BPartner_ID,sum(nvl(stg.QTYRESERVED,0))"
	    				+ ",sum(nvl(stg.qtyordered,0)), nvl(mdl.qty,0)- sum(nvl(stg.QtyOnHand,0) +nvl(stg.QTYRESERVED,0)-"
	    				+ "nvl(stg.QtyOrdered,0)) ,sysdate + nvl(pro.DELIVERYTIME_PROMISED,0) ,plan.PRIORITYIMPLEMENTATION,SYSDATE,plan.CREATEDBY,SYSDATE,plan.UPDATEDBY,?  FROM MRP_PlanRun run "
	    				+ " left join MRP_MASTERDEMAND md on"
	    				+ " (md.MRP_MASTERDEMAND_ID =run.MRP_MASTERDEMAND_ID )left join MRP_MASTERDEMANDLINE mdl on "
	    				+ "(mdl.MRP_MASTERDEMAND_ID = md.MRP_MASTERDEMAND_ID)"
	                    + "Inner Join M_Product pr on (pr.M_Product_ID = mdl.M_Product_ID) Left Join M_Product_PO pro on"
	    				+ " (pr.M_Product_ID = pro.M_Product_ID)INNER JOIN M_Locator loc ON (loc.M_Warehouse_ID = ? )"
	    				+ " left join MRP_Plan plan on (plan.MRP_Plan_ID = run.MRP_Plan_ID)"
	    				+ " INNER JOIN M_Storage stg ON (stg.M_Locator_ID = loc.M_Locator_ID  AND "
	    				+ "stg.M_Product_ID = mdl.M_Product_ID )", "run", true, false)
	    		        + " AND run.MRP_PlanRun_ID = ? "
	    				+ "GROUP BY stg.M_Product_ID , pr.isbom,pro.C_BPartner_ID,mdl.qty,pro.DELIVERYTIME_PROMISED,plan.PRIORITYIMPLEMENTATION,plan.CREATEDBY,plan.UPDATEDBY ";
  
	  System.out.println(sql);
	  
  
  String Query = "SELECT MRP_INVENTORY_AUDIT_SEQ.nextval FROM DUAL";
  int sequence = DB.getSQLValue (null, Query);
  System.out.println(sequence);
    int warehouseID = this.mMRPPlan.getM_Warehouse_ID();
    int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
    ArrayList<Object> list = new ArrayList();
    System.out.println("This is testing Code");
    list.add(Integer.valueOf(sequence));
    list.add(Integer.valueOf(warehouseID));
    list.add(Integer.valueOf(runID));
    Object[] params = new Object[list.size()];
    list.toArray(params);
   // int no = DB.executeUpdate(this.mTrx, sql, params);
    System.out.println(warehouseID);
    System.out.println(runID);
    System.out.println(sql);
    
     no = DB.executeUpdateEx(sql, params, get_TrxName());
  }
     
    return no;
  }
  
  private int populateWorkOrderAudit()  {
    MRole role = MRole.getDefault(getCtx(), false);
    
  //  String sql = "INSERT INTO MRP_WorkOrder_Audit (AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID, M_WorkOrder_ID,DateExpected,M_Product_ID,M_BOM_ID,C_UOM_ID,QtyDemand,QtyExpected ) " + role.addAccessSQL("SELECT run.AD_Client_ID,run.AD_Org_ID,run.MRP_PlanRun_ID, workorder.M_WorkOrder_ID, CASE WHEN workorder.DateActualFrom IS NULL THEN COALESCE( TRUNC(workorder.DateScheduleTo,'DD') , TRUNC(SYSDATE,'DD') ) ELSE COALESCE(TRUNC(workorder.DateActualFrom, 'DD') , TRUNC(SYSDATE,'DD') )  + COALESCE( product.ManufactureTime_Expected , 0)  END, workorder.M_Product_ID,workorder.M_BOM_ID,workorder.C_UOM_ID, (workorder.QtyEntered - (workorder.QtyAssembled + workorder.QtyScrapped)), (workorder.QtyEntered - ((workorder.QtyAssembled + workorder.QtyScrapped) - workorder.QtyAvailable)) FROM MRP_PlanRun run INNER JOIN MRP_Product_Audit mrpproduct ON ( mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID) INNER JOIN M_Workorder workorder ON (workorder.M_Warehouse_ID = ? AND workorder.M_Product_ID = mrpproduct.M_Product_ID) INNER JOIN M_Product product ON (product.M_Product_ID = workorder.M_Product_ID)", "run", true, false) + "\tAND run.MRP_PlanRun_ID=? " + "\tAND CASE WHEN workorder.DateActualFrom IS NULL THEN COALESCE( TRUNC(workorder.DateScheduleTo,'DD') , TRUNC(SYSDATE,'DD') ) " + "ELSE COALESCE(TRUNC(workorder.DateActualFrom, 'DD') , TRUNC(SYSDATE,'DD') )  + " + "COALESCE( product.ManufactureTime_Expected , 0)  END " + " BETWEEN ? AND ? " + "\tAND (workorder.QtyEntered - ((workorder.QtyAssembled + workorder.QtyScrapped) - workorder.QtyAvailable)) > 0 " + "\tAND workorder.docstatus = 'IP' " + "   AND workorder.WoType = 'S' ";
    String sql = "INSERT INTO MRP_WorkOrder_Audit (AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID, MFG_WorkOrder_ID,DateExpected,M_Product_ID,M_BOM_ID,C_UOM_ID,QtyDemand,QtyExpected,created,createdby,updated,updatedby,mrp_workorder_audit_id ) " + role.addAccessSQL("SELECT run.AD_Client_ID,run.AD_Org_ID,run.MRP_PlanRun_ID, workorder.MFG_WorkOrder_ID, CASE WHEN workorder.DateActualFrom IS NULL THEN COALESCE( TRUNC(workorder.DateScheduleTo,'DD') , TRUNC(SYSDATE,'DD') ) ELSE COALESCE(TRUNC(workorder.DateActualFrom, 'DD') , TRUNC(SYSDATE,'DD') )  + COALESCE( product.ManufactureTime_Expected , 0)  END, workorder.M_Product_ID,workorder.M_BOM_ID,workorder.C_UOM_ID, (workorder.QtyEntered - (workorder.QtyAssembled + workorder.QtyScrapped)), (workorder.QtyEntered - ((workorder.QtyAssembled + workorder.QtyScrapped) - workorder.QtyAvailable)),SYSDATE,run.createdby,SYSDATE,run.updatedby,? FROM MRP_PlanRun run "
    		+ "INNER JOIN MRP_Product_Audit mrpproduct ON ( mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID) "
    		+ "INNER JOIN MFG_Workorder workorder ON (workorder.M_Warehouse_ID = ? AND workorder.M_Product_ID = mrpproduct.M_Product_ID) "
    		+ "INNER JOIN M_Product product ON (product.M_Product_ID = workorder.M_Product_ID)", "run", true, false) + " AND run.MRP_PlanRun_ID=? " + " AND CASE WHEN workorder.DateActualFrom IS NULL THEN COALESCE( TRUNC(workorder.DateScheduleTo,'DD') , TRUNC(SYSDATE,'DD') ) " + "ELSE COALESCE(TRUNC(workorder.DateActualFrom, 'DD') , TRUNC(SYSDATE,'DD') )  + " + "COALESCE( product.ManufactureTime_Expected , 0)  END " + " BETWEEN ? AND ? " + " AND (workorder.QtyEntered - ((workorder.QtyAssembled + workorder.QtyScrapped) - workorder.QtyAvailable)) > 0 " + " AND workorder.docstatus = 'IP' " + "   AND workorder.WoType = 'S' ";
    String Query = "SELECT MRP_WORKORDER_AUDIT_SEQ.nextval FROM DUAL";
    int sequence = DB.getSQLValue (null, Query);
    int warehouseID = this.mMRPPlan.getM_Warehouse_ID();
    int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
    ArrayList<Object> list = new ArrayList();
    list.add(Integer.valueOf(sequence));
    list.add(Integer.valueOf(warehouseID));
    list.add(Integer.valueOf(runID));
    list.add(TimeUtil.getDay(this.mStartDate));
    list.add(TimeUtil.getDay(this.mPlanEndDate));
    Object[] params = new Object[list.size()];
    list.toArray(params);
   // int no = DB.executeUpdate(this.mTrx, sql, params);
    int no = DB.executeUpdateEx(sql, params, this.get_TrxName());
    return no;
  }
  
  private int populateOrderAudit() {
    MRole role = MRole.getDefault(getCtx(), false);
    System.out.println(role.addAccessSQL("SELECT run.AD_Client_ID,run.AD_Org_ID,run.MRP_PlanRun_ID,ordr.C_Order_ID,ordrline.C_OrderLine_ID,ordrline.C_UOM_ID,TRUNC(ordrline.DatePromised, 'DD'),Ordrline.M_Product_ID, ordr.IsSOTrx,ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated, SYSDATE,run.createdby,SYSDATE,run.updatedby,1000282 FROM MRP_PlanRun run INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID)INNER JOIN C_OrderLine ordrline ON (ordrline.M_Product_ID = mrpproduct.M_Product_ID AND ordrline.M_Warehouse_ID = 103 AND ordrline.IsActive='Y') INNER JOIN C_Order ordr ON (ordrline.C_Order_ID = ordr.C_Order_ID AND ordr.IsActive = 'Y') INNER JOIN C_DocType doctype ON (doctype.C_DocType_ID = ordr.C_DocType_ID AND doctype.IsActive='Y')", "run", true, false) + " AND run.MRP_PlanRun_ID=1000282 AND " + "TRUNC(ordrline.DatePromised, 'DD') " + "AND (ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated) > 0 " + "AND ordr.DocStatus IN ('IP', 'CO', 'CL') " + "AND ((doctype.DocBaseType = 'SOO' " + "AND doctype.DocSubTypeSO = 'SO' " + "AND doctype.IsReturnTrx = 'N') ");
//    String sql = "INSERT INTO MRP_Order_Audit (AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,C_Order_ID,C_OrderLine_ID,C_UOM_ID,DateExpected,M_Product_ID,IsSOTrx,QtyExpected) " + role.addAccessSQL("SELECT run.AD_Client_ID,run.AD_Org_ID,run.MRP_PlanRun_ID,ordr.C_Order_ID,ordrline.C_OrderLine_ID,ordrline.C_UOM_ID,TRUNC(ordrline.DatePromised, 'DD'),Ordrline.M_Product_ID, ordr.IsSOTrx,ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated FROM MRP_PlanRun run INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID)INNER JOIN C_OrderLine ordrline ON (ordrline.M_Product_ID = mrpproduct.M_Product_ID AND ordrline.M_Warehouse_ID = ? AND ordrline.IsActive='Y') INNER JOIN C_Order ordr ON (ordrline.C_Order_ID = ordr.C_Order_ID AND ordr.IsActive = 'Y') INNER JOIN C_DocType doctype ON (doctype.C_DocType_ID = ordr.C_DocType_ID AND doctype.IsActive='Y')", "run", true, false) + " AND run.MRP_PlanRun_ID=? AND " + "TRUNC(ordrline.DatePromised, 'DD') " + "BETWEEN ? AND " + "? " + "AND (ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated) > 0 " + "AND ordr.DocStatus IN ('IP', 'CO', 'CL') " + "AND ((doctype.DocBaseType = 'SOO' " + "AND doctype.DocSubTypeSO = 'SO' " + "AND doctype.IsReturnTrx = 'N') " + "OR (doctype.DOCBASETYPE = 'POO' " + "AND doctype.IsReturnTrx = 'N')) ";
   // String sql = "INSERT INTO MRP_Order_Audit (AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,C_Order_ID,C_OrderLine_ID,C_UOM_ID,DateExpected,M_Product_ID,IsSOTrx,QtyExpected,created,createdby,updated,updatedby,MRP_Order_Audit_ID) " + role.addAccessSQL("SELECT run.AD_Client_ID,run.AD_Org_ID,run.MRP_PlanRun_ID,ordr.C_Order_ID,ordrline.C_OrderLine_ID,ordrline.C_UOM_ID,TRUNC(ordrline.DatePromised, 'DD'),Ordrline.M_Product_ID, ordr.IsSOTrx,ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated, SYSDATE,run.createdby,SYSDATE,run.updatedby,? FROM MRP_PlanRun run INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID)INNER JOIN C_OrderLine ordrline ON (ordrline.M_Product_ID = mrpproduct.M_Product_ID AND ordrline.M_Warehouse_ID = ? AND ordrline.IsActive='Y') INNER JOIN C_Order ordr ON (ordrline.C_Order_ID = ordr.C_Order_ID AND ordr.IsActive = 'Y') INNER JOIN C_DocType doctype ON (doctype.C_DocType_ID = ordr.C_DocType_ID AND doctype.IsActive='Y')", "run", true, false) + " AND run.MRP_PlanRun_ID=? AND " + "TRUNC(ordrline.DatePromised, 'DD') " + "BETWEEN ? AND " + "? " + "AND (ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated) > 0 " + "AND ordr.DocStatus IN ('IP', 'CO', 'CL') " + "AND ((doctype.DocBaseType = 'SOO' " + "AND doctype.DocSubTypeSO = 'SO' " + "AND doctype.IsReturnTrx = 'N') " + "OR (doctype.DOCBASETYPE = 'POO' " + "AND doctype.IsReturnTrx = 'N')) ";
    String sql = "INSERT INTO MRP_Order_Audit (AD_Client_ID,AD_Org_ID,MRP_PlanRun_ID,C_Order_ID,C_OrderLine_ID,C_UOM_ID,DateExpected,M_Product_ID,IsSOTrx,QtyExpected,created,createdby,updated,updatedby,MRP_Order_Audit_ID) " + role.addAccessSQL("SELECT run.AD_Client_ID,run.AD_Org_ID,run.MRP_PlanRun_ID,ordr.C_Order_ID,ordrline.C_OrderLine_ID,ordrline.C_UOM_ID,TRUNC(ordrline.DatePromised, 'DD'),Ordrline.M_Product_ID, ordr.IsSOTrx,ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated, SYSDATE,run.createdby,SYSDATE,run.updatedby,? FROM MRP_PlanRun run "
    		+ "INNER JOIN MRP_Product_Audit mrpproduct ON (mrpproduct.MRP_PlanRun_ID = run.MRP_PlanRun_ID) "
    		+ "INNER JOIN C_OrderLine ordrline ON (ordrline.M_Product_ID = mrpproduct.M_Product_ID AND ordrline.M_Warehouse_ID = ? AND ordrline.IsActive='Y') " 
    		+ "INNER JOIN C_Order ordr ON (ordrline.C_Order_ID = ordr.C_Order_ID AND ordr.IsActive = 'Y') "
    		+ "INNER JOIN C_DocType doctype ON (doctype.C_DocType_ID = ordr.C_DocType_ID AND doctype.IsActive='Y')", "run", true, false) + " AND run.MRP_PlanRun_ID=? AND " + "TRUNC(ordrline.DatePromised, 'DD') " + "BETWEEN ? AND " + "? " + "AND (ordrline.QtyOrdered - ordrline.QtyDelivered - ordrline.QtyAllocated - ordrline.QtyDedicated) > 0 " + "AND ordr.DocStatus IN ('IP', 'CO', 'CL') " + "AND ((doctype.DocBaseType = 'SOO' " + "AND doctype.DocSubTypeSO = 'SO' " + "AND doctype.IsReturnTrx = 'N') " + "OR (doctype.DOCBASETYPE = 'POO' " + "AND doctype.IsReturnTrx = 'N')) ";

    String Query = "SELECT MRP_ORDER_AUDIT_SEQ.nextval FROM DUAL";
    int sequence = DB.getSQLValue (null, Query);
    int warehouseID = this.mMRPPlan.getM_Warehouse_ID();
    int runID = this.mMRPPlanRun.getMRP_PlanRun_ID();
    ArrayList<Object> list = new ArrayList();
    list.add(Integer.valueOf(sequence));
    list.add(Integer.valueOf(warehouseID));
    list.add(Integer.valueOf(runID));
    list.add(TimeUtil.getDay(this.mStartDate));
    list.add(TimeUtil.getDay(this.mPlanEndDate));
    Object[] params = new Object[list.size()];
    list.toArray(params);
    //int no = DB.executeUpdate(this.mTrx, sql, params);
   // int no = DB.executeUpdateEx(sql, params, this.get_TrxName());
//    int no = DB.executeUpdateEx(sql, params,mTrx.getTrxName());
    System.out.println("populateOrderAudit12");
    int no = DB.executeUpdateEx(sql, params,null);

    return no;
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("C_From_Period_ID")) {
          this.pPeriodFromID = element.getParameterAsInt();
        } else if (name.equals("MRP_MasterDemand_ID")) {
          this.pMasterDemandID = element.getParameterAsInt();
        } else if (name.equals("C_Period_BackOrder_ID")) {
          this.pPeriodBackOrderID = element.getParameterAsInt();
        } else if (name.equals("MRP_Plan_ID")) {
          this.pPlanID = element.getParameterAsInt();
        } else if (name.equals("NumberOfThreads")) {
          this.pNumberOfThreads = element.getParameterAsInt();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    if (this.pPlanID == 0)
      this.pPlanID = getRecord_ID(); 
    this.mMRPPlan = new MMRPPlan(getCtx(), this.pPlanID, this.get_TrxName());
    this.mPriorityImplementation = this.mMRPPlan.getPriorityImplementation();
  }
  
  private void setDates() {
    this.mStartDate = null;
    this.mPlanEndDate = null;
    MPeriod backOrderPeriod = MPeriod.get(getCtx(), this.pPeriodBackOrderID);
    this.mBackOrderDate = backOrderPeriod.getStartDate();
    this.mPlanRunDate = new Timestamp(System.currentTimeMillis());
    MPeriod startPeriod = MPeriod.get(getCtx(), this.mMRPPlan.getC_Period_From_ID());
    MPeriod endPeriod = MPeriod.get(getCtx(), this.mMRPPlan.getC_Period_To_ID());
    MPeriod periodFrom = MPeriod.get(getCtx(), this.pPeriodFromID);
    if (startPeriod.getStartDate().after(periodFrom.getStartDate())) {
      this.mStartPeriod = startPeriod;
    } else {
      this.mStartPeriod = periodFrom;
    } 
    if (this.mBackOrderDate.after(this.mStartPeriod.getStartDate())) {
      this.mStartDate = this.mStartPeriod.getStartDate();
    } else {
      this.mStartDate = this.mBackOrderDate;
    } 
    this.mStartDate = TimeUtil.getDay(this.mStartDate);
    this.mPlanEndDate = endPeriod.getEndDate();
  }
  
 // private boolean populatePlannedDemand(MMRPMasterDemandLine[] masterDemandLineList) throws cyprusbrsUserException {
  private boolean populatePlannedDemand(MMRPMasterDemandLine[] masterDemandLineList) throws Exception {
  if (masterDemandLineList.length == 0)
      return true; 
    for (MMRPMasterDemandLine masterDemandLine : masterDemandLineList) {
      MProduct product = MProduct.get(getCtx(), masterDemandLine.getM_Product_ID());
      MPeriod period = MPeriod.get(getCtx(), masterDemandLine.getC_Period_ID());
      this.log.fine("Populating Demand for product " + product.getName() + "for Period " + period.getName());
      if (!product.isPlannedItem()) {
        this.log.warning(product.getName() + ": " + "Is not a Planned Item");
      } else {
        String[] str = addToProductAudit(product);
        if (str.length != 0)
          if (str[0].equals("SetUpError")) {
            addToMRPPlannedDemand(masterDemandLine, 0, false, 0);
          } else {
            MMRPPlannedDemand plannedDemand = null;
            if (str[0].equals("WorkOrder")) {
              plannedDemand = addToMRPPlannedDemand(masterDemandLine, 0, true, Integer.parseInt(str[1]));
              this.log.fine("Populating the demand for components of " + product.getName());
              addBOMComponents(plannedDemand);
              this.log.fine("Successfully populated the demand for components of " + product.getName());
            } 
            if (str[0].equals("PurchaseOrder"))
              plannedDemand = addToMRPPlannedDemand(masterDemandLine, 0, true, Integer.parseInt(str[1])); 
            this.mTrx.commit();
          }  
      } 
    } 
    return true;
  }
  
  private MMRPPlannedDemand addToMRPPlannedDemand(MMRPMasterDemandLine masterDemandLine, int level, boolean isCorrectSetup, int leadTimeInDays) {
    MMRPPlannedDemand plannedDemand = new MMRPPlannedDemand(getCtx(), 0, this.get_TrxName());
    plannedDemand.setM_Product_ID(masterDemandLine.getM_Product_ID());
    plannedDemand.setMRP_PlanRun_ID(this.mMRPPlanRun.getMRP_PlanRun_ID());
    plannedDemand.setClientOrg((org.cyprusbrs.model.PO)this.mMRPPlanRun);
    MPeriod period = MPeriod.get(getCtx(), masterDemandLine.getC_Period_ID());
    plannedDemand.setDateRequired(TimeUtil.getDay(period.getStartDate()));
    plannedDemand.setQtyRequired(masterDemandLine.getQty());
    plannedDemand.setLevelNo(level);
    if (isCorrectSetup) {
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_NotRunning);
    } else {
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_SetupError);
    } 
    plannedDemand.setC_Period_ID(period.getC_Period_ID());
    plannedDemand.setC_UOM_ID(masterDemandLine.getC_UOM_ID());
    plannedDemand.setLeadTime(BigDecimal.valueOf(leadTimeInDays));
    if (!plannedDemand.save(this.get_TrxName()))
      this.log.saveError("Error", "Planned Demand Not Saved"); 
    return plannedDemand;
  }
  
  private String[] addToProductAudit(MProduct product) throws Exception {
    int productID = product.getM_Product_ID();
    if (this.mProductMap.containsKey(Integer.valueOf(productID)))
      return this.mProductMap.get(Integer.valueOf(productID)); 
    String[] str = new String[2];
    int bPartnerID = 0;
    BigDecimal minOrderQty = null;
    if (!product.isItem()) {
      this.log.warning("" + product.getName() + "is not of type Item. " + "MRP Planning will not be done for this product");
      str[0] = "SetUpError";
      this.mProductMap.put(Integer.valueOf(productID), str);
      return str;
    } 
    if (!PlanUtil.checkProductProcurement(product)) {
      this.log.warning("" + product.getName() + "is neither purchased nor manufactured, " + "MRP Planning will not be done for this prduct");
      str[0] = "SetUpError";
      this.mProductMap.put(Integer.valueOf(productID), str);
      return str;
    } 
    if (PlanUtil.isManufacturedInMRP(product, this.mPriorityImplementation)) {
      if (PlanUtil.isCorrectManufacturingSetup(product, getCtx(), this.get_TrxName())) {
        str[0] = "WorkOrder";
        str[1] = Integer.toString(product.getManufactureTime_Expected());
        this.mProductMap.put(Integer.valueOf(productID), str);
        this.log.fine("" + product.getName() + "is Manufactured in this MRP Run with lead time of " + "" + product.getManufactureTime_Expected() + "Days");
      } else {
        this.log.warning("" + product.getName() + "is manufactured in this MRP Run as per Implementation Priority and " + "and the flag 'isManufactured'  in product window. " + "But it does not have a Current Active Manufacturing BOM " + "MRP Planning will not be done on this prduct");
        str[0] = "SetUpError";
        this.mProductMap.put(Integer.valueOf(productID), str);
        return str;
      } 
    } else {
      str[0] = "PurchaseOrder";
      int leadTimeInDays = 0;
      this.log.fine("" + product.getName() + "is Purchased in this MRP Run");
      MProductPO[] ppos = MProductPO.getOfProduct(getCtx(), product.getM_Product_ID(), this.get_TrxName());
      for (MProductPO element : ppos) {
        if (element.isCurrentVendor() && element.getC_BPartner_ID() != 0) {
          leadTimeInDays = element.getDeliveryTime_Promised();
          bPartnerID = element.getC_BPartner_ID();
          minOrderQty = element.getOrder_Min();
          break;
        } 
      } 
      if (bPartnerID == 0 && ppos.length > 0) {
        this.log.info("No Current Active Vendor Found for product " + product.getName());
        bPartnerID = ppos[0].getC_BPartner_ID();
        leadTimeInDays = ppos[0].getDeliveryTime_Promised();
        minOrderQty = ppos[0].getOrder_Min();
      } 
      if (bPartnerID == 0)
        this.log.warning("No Vendor Found for product, purchase lead time will be considered as 0 days" + product.getName()); 
      str[1] = Integer.toString(leadTimeInDays);
      this.mProductMap.put(Integer.valueOf(productID), str);
    } 
    MMRPProductAudit auditProduct = new MMRPProductAudit(getCtx(), 0, this.get_TrxName());
    
    auditProduct.setM_Product_ID(productID);
    auditProduct.setMRP_PlanRun_ID(this.mMRPPlanRun.getMRP_PlanRun_ID());
    auditProduct.setClientOrg((org.cyprusbrs.model.PO)this.mMRPPlanRun);
    if (bPartnerID != 0)
      auditProduct.setC_BPartner_ID(bPartnerID); 
    auditProduct.setOrder_Min(minOrderQty);
    if (!auditProduct.save(this.get_TrxName())) {
      this.log.saveError("Error", "Stg Product Not Copied");
      throw new Exception("Audit Product Not Saved");
    } 
    this.mTrx.commit();
    return str;
  }
  
  private void addBOMComponents(MMRPPlannedDemand parentPlannedDemand) throws Exception {
    MProduct product = MProduct.get(getCtx(), parentPlannedDemand.getM_Product_ID());
    MBOMProduct[] BOMProducts = MBOMProduct.getBOMLinesOrderByProductName(product, X_M_BOM.BOMTYPE_CurrentActive, X_M_BOM.BOMUSE_Manufacturing, true);
    if (BOMProducts.length == 0) {
      this.log.warning(product.getName() + ": " + "Has no components in Current Active Manufacturing BOM");
      return;
    } 
    for (MBOMProduct BOMProduct : BOMProducts) {
      MProduct compProduct = MProduct.get(getCtx(), BOMProduct.getM_ProductBOM_ID());
      this.log.fine("populating the demand for " + compProduct.getName());
      String[] str = addToProductAudit(compProduct);
      if (str.length != 0)
        if (str[0].equals("SetUpError")) {
          addToMRPPlannedDemand(parentPlannedDemand, BOMProduct, false, 0);
        } else {
          MMRPPlannedDemand subParentPlannedDemand = null;
          if (str[0].equals("WorkOrder")) {
            subParentPlannedDemand = addToMRPPlannedDemand(parentPlannedDemand, BOMProduct, true, Integer.parseInt(str[1]));
            this.log.fine("Populating the demand for components of " + compProduct.getName());
            addBOMComponents(subParentPlannedDemand);
            this.log.fine("Successfully populated the demand for components of " + compProduct.getName());
          } 
          if (str[0].equals("PurchaseOrder"))
            addToMRPPlannedDemand(parentPlannedDemand, BOMProduct, true, Integer.parseInt(str[1])); 
        }  
    } 
  }
  
  private MMRPPlannedDemand addToMRPPlannedDemand(MMRPPlannedDemand treeMRPPlannedDemand, MBOMProduct mbomProduct, boolean isCorrectSetup, int leadTimeInDays) throws Exception {
    MMRPPlannedDemand plannedDemand = new MMRPPlannedDemand(getCtx(), 0, this.get_TrxName());
    MProduct compProduct = MProduct.get(getCtx(), mbomProduct.getM_ProductBOM_ID());
    plannedDemand.setM_Product_ID(compProduct.getM_Product_ID());
    plannedDemand.setMRP_PlanRun_ID(this.mMRPPlanRun.getMRP_PlanRun_ID());
    plannedDemand.setClientOrg((org.cyprusbrs.model.PO)this.mMRPPlanRun);
    plannedDemand.setDateRequired(treeMRPPlannedDemand, compProduct);
    plannedDemand.setQtyRequired(treeMRPPlannedDemand.getQtyRequired().multiply(mbomProduct.getBOMQty()));
    if (treeMRPPlannedDemand.getMRP_PlannedDemand_Root_ID() != 0) {
      plannedDemand.setMRP_PlannedDemand_Root_ID(treeMRPPlannedDemand.getMRP_PlannedDemand_Root_ID());
    } else {
      plannedDemand.setMRP_PlannedDemand_Root_ID(treeMRPPlannedDemand.getMRP_PlannedDemand_ID());
    } 
    plannedDemand.setMRP_PlannedDemand_Parent_ID(treeMRPPlannedDemand.getMRP_PlannedDemand_ID());
    plannedDemand.setLevelNo(treeMRPPlannedDemand.getLevelNo() + 1);
    plannedDemand.setLeadTime(BigDecimal.valueOf(leadTimeInDays).add(treeMRPPlannedDemand.getLeadTime()));
    if (isCorrectSetup) {
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_NotRunning);
    } else {
      plannedDemand.setRunStatus(X_MRP_PlannedDemand.RUNSTATUS_SetupError);
    } 
    plannedDemand.setC_Period_ID(treeMRPPlannedDemand.getC_Period_ID());
    plannedDemand.setC_UOM_ID(compProduct.getC_UOM_ID());
    if (!plannedDemand.save(this.get_TrxName()))
      throw new Exception("Planned Demand Not Saved"); 
    return plannedDemand;
  }
}

