package org.cyprus.mfg.process;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprus.mfg.util.MMFGWorkOrderSchedulerUtil;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.Msg;

public class MMFGWorkOrderScheduler extends SvrProcess {
  private Timestamp p_M_DateScheduledFrom = null;
  
  private Timestamp p_M_DateScheduledTo = null;
  
  private int pWorkOrderID;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null || element.getParameter_To() != null)
        if (name.equals("MFG_WorkOrder_ID")) {
          BigDecimal workOrderID = (BigDecimal)element.getParameter();
          this.pWorkOrderID = workOrderID.intValue();
        } else if (name.equals("DateScheduleFrom")) {
          String dateScheduledFrom = (String)element.getParameter();
          this.p_M_DateScheduledFrom = Timestamp.valueOf(dateScheduledFrom);
        } else if (name.equals("DateScheduleTo")) {
          String dateScheduledTo = (String)element.getParameter();
          this.p_M_DateScheduledTo = Timestamp.valueOf(dateScheduledTo);
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
  
  protected String doIt() throws Exception {
    if (this.pWorkOrderID == 0) {
      this.log.warning(Msg.parseTranslation(getCtx(), "@FillMandatory@ @MMFG_WorkOrder_ID@"));
//      throw new cyprusbrsUserException(Msg.parseTranslation(getCtx(), "@FillMandatory@ @MMFG_WorkOrder_ID@"));
      throw new CyprusException(Msg.parseTranslation(getCtx(), "@FillMandatory@ @MMFG_WorkOrder_ID@"));

    } 
    if (this.p_M_DateScheduledFrom == null && this.p_M_DateScheduledTo == null) {
      this.log.warning(Msg.parseTranslation(getCtx(), "@FillMandatory@ - Enter either @DateScheduleTo@ or @DateScheduleFrom@"));
      //throw new cyprusbrsUserException(Msg.parseTranslation(getCtx(), "@FillMandatory@ - Enter either @DateScheduleTo@ or @DateScheduleFrom@"));
      throw new CyprusException(Msg.parseTranslation(getCtx(), "@FillMandatory@ - Enter either @DateScheduleTo@ or @DateScheduleFrom@"));
    } 
    if (!SchedulerSetDate(this.pWorkOrderID, this.p_M_DateScheduledFrom, this.p_M_DateScheduledTo)) {
      this.log.warning(Msg.parseTranslation(getCtx(), "@RunWOScheduler@"));
    //  throw new cyprusbrsUserException(Msg.parseTranslation(getCtx(), "@RunWOScheduler@"));
      throw new CyprusException(Msg.parseTranslation(getCtx(), "@RunWOScheduler@")); 
    } 
    return null;
  }
  
  private boolean SchedulerSetDate(int workOrderID, Timestamp DateScheduledFrom, Timestamp DateScheduleTo) {
    Timestamp StartDate = null;
    Timestamp EndDate = null;
    boolean includeSetupTime = true;
    BigDecimal quantity = null;
    boolean includeOptionalOperationTime = false;
    boolean isForwardScheduling = true;
    MMFGWorkOrder workOrder = new MMFGWorkOrder(getCtx(), workOrderID, get_TrxName());
    workOrder.setIsPrepared(true);
    quantity = workOrder.getQtyEntered();
    if (DateScheduledFrom != null) {
      StartDate = DateScheduledFrom;
      isForwardScheduling = true;
      MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(workOrder, null, "SeqNo");
      for (MMFGWorkOrderOperation woo : woos) {
        woo.setIsProcessCalled(true);
        MUOM uom = new MUOM(getCtx(), woo.getC_UOM_ID(), null);
        if (!uom.isDay() && !uom.isHour()) {
          this.log.warning("UOM should either be a Day or Hour");
          return false;
        } 
        woo.setDateScheduleFrom(StartDate);
        EndDate = MMFGWorkOrderSchedulerUtil.ScheduleDate(getCtx(), woo, quantity, StartDate, includeOptionalOperationTime, includeSetupTime, isForwardScheduling);
        woo.setDateScheduleTo(EndDate);
        woo.save();
        if (EndDate != null)
          StartDate = EndDate; 
      } 
      workOrder.setDateScheduleTo(StartDate);
      workOrder.save();
    } else if (DateScheduleTo != null) {
      EndDate = DateScheduleTo;
      isForwardScheduling = false;
      MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(workOrder, null, "SeqNo DESC");
      for (MMFGWorkOrderOperation woo : woos) {
        woo.setIsProcessCalled(true);
        MUOM uom = new MUOM(getCtx(), woo.getC_UOM_ID(), null);
        if (!uom.isDay() && !uom.isHour()) {
          this.log.warning("UOM should either be a Day or Hour");
          return false;
        } 
        woo.setDateScheduleTo(EndDate);
        StartDate = MMFGWorkOrderSchedulerUtil.ScheduleDate(getCtx(), woo, quantity, EndDate, includeOptionalOperationTime, includeSetupTime, isForwardScheduling);
        woo.setDateScheduleFrom(StartDate);
        woo.save();
        if (StartDate != null)
          EndDate = StartDate; 
      } 
      workOrder.setDateScheduleFrom(EndDate);
      workOrder.save();
    } 
    return true;
  }
}

