package org.eevolution.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CCache;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.wf.MWFNode;

public class MPPOrderNode extends X_PP_Order_Node {
  private static final long serialVersionUID = 1L;
  
  public static MPPOrderNode get(Properties ctx, int PP_Order_Node_ID) {
    return get(ctx, PP_Order_Node_ID, (String)null);
  }
  
  public static MPPOrderNode get(Properties ctx, int PP_Order_Node_ID, String trxName) {
    if (PP_Order_Node_ID <= 0)
      return null; 
    MPPOrderNode retValue = null;
    if (trxName == null) {
      retValue = (MPPOrderNode)s_cache.get(Integer.valueOf(PP_Order_Node_ID));
      if (retValue != null)
        return retValue; 
    } 
    retValue = new MPPOrderNode(ctx, PP_Order_Node_ID, trxName);
    if (retValue.getPP_Order_Node_ID() <= 0)
      retValue = null; 
    if (retValue != null && trxName == null)
      s_cache.put(Integer.valueOf(PP_Order_Node_ID), retValue); 
    return retValue;
  }
  
  public static boolean isLastNode(Properties ctx, int PP_Order_Node_ID, String trxName) {
    String whereClause = "PP_Order_Node_ID=?";
    return 
      
      !(new Query(ctx, "PP_Order_NodeNext", whereClause, trxName)).setOnlyActiveRecords(true).setParameters(new Object[] { Integer.valueOf(PP_Order_Node_ID) }).match();
  }
  
  private static CCache<Integer, MPPOrderNode> s_cache = new CCache("PP_Order_Node", 50);
  
  MPPOrderWorkflow m_order_wf = null;
  
  private List<MPPOrderNodeNext> m_next;
  
  private long m_durationBaseMS;
  
  public MPPOrderNode(Properties ctx, int PP_Order_Node_ID, String trxName) {
    super(ctx, PP_Order_Node_ID, trxName);
    this.m_next = null;
    this.m_durationBaseMS = -1L;
    if (PP_Order_Node_ID == 0)
      setDefault(); 
    if (get_ID() != 0 && trxName == null)
      s_cache.put(Integer.valueOf(getPP_Order_Node_ID()), this); 
  }
  
  public MPPOrderNode(MPPOrderWorkflow wf, String Value, String Name) {
    this(wf.getCtx(), 0, wf.get_TrxName());
    setClientOrg((PO)wf);
    setPP_Order_Workflow_ID(wf.getPP_Order_Workflow_ID());
    setValue(Value);
    setName(Name);
    this.m_durationBaseMS = wf.getDurationBaseSec() * 1000L;
  }
  
  public MPPOrderNode(MWFNode wfNode, MPPOrderWorkflow PP_Order_Workflow, BigDecimal qtyOrdered, String trxName) {
    this(wfNode.getCtx(), 0, trxName);
    
    set_ValueNoCheck("M_Operation_ID", wfNode.get_Value("M_Operation_ID"));
    setDescription(wfNode.getDescription());
    setHelp(wfNode.getHelp());
    setPP_Order_ID(PP_Order_Workflow.getPP_Order_ID());
    setPP_Order_Workflow_ID(PP_Order_Workflow.getPP_Order_Workflow_ID());
    setAction(wfNode.getAction());
    setAD_WF_Node_ID(wfNode.getAD_WF_Node_ID());
    setAD_WF_Responsible_ID(wfNode.getAD_WF_Responsible_ID());
    setAD_Workflow_ID(wfNode.getAD_Workflow_ID());
    setIsSubcontracting(wfNode.isSubcontracting());
    setIsMilestone(wfNode.isMilestone());
    setC_BPartner_ID(wfNode.getC_BPartner_ID());
    setCost(wfNode.getCost());
    setDuration(wfNode.getDuration());
    setUnitsCycles(wfNode.getUnitsCycles().intValueExact());
    setOverlapUnits(wfNode.getOverlapUnits());
    setEntityType(wfNode.getEntityType());
    setIsCentrallyMaintained(wfNode.isCentrallyMaintained());
    setJoinElement(wfNode.getJoinElement());
    setLimit(wfNode.getLimit());
    setName(wfNode.getName());
    setPriority(wfNode.getPriority());
    setSplitElement(wfNode.getSplitElement());
    setSubflowExecution(wfNode.getSubflowExecution());
    setValue(wfNode.getValue());
    setS_Resource_ID(wfNode.getS_Resource_ID());
    setSetupTime(wfNode.getSetupTime());
    setSetupTimeRequiered(wfNode.getSetupTime());
    setMovingTime(wfNode.getMovingTime());
    setWaitingTime(wfNode.getWaitingTime());
    setWorkingTime(wfNode.getWorkingTime());
    setQueuingTime(wfNode.getQueuingTime());
    setXPosition(wfNode.getXPosition());
    setYPosition(wfNode.getYPosition());
    setDocAction(wfNode.getDocAction());
    setAD_Column_ID(wfNode.getAD_Column_ID());
    setAD_Form_ID(wfNode.getAD_Form_ID());
    setAD_Image_ID(wfNode.getAD_Image_ID());
    setAD_Window_ID(wfNode.getAD_Window_ID());
    setAD_Process_ID(wfNode.getAD_Process_ID());
    setAttributeName(wfNode.getAttributeName());
    setAttributeValue(wfNode.getAttributeValue());
    setC_BPartner_ID(wfNode.getC_BPartner_ID());
    setStartMode(wfNode.getStartMode());
    setFinishMode(wfNode.getFinishMode());
    setValidFrom(wfNode.getValidFrom());
    setValidTo(wfNode.getValidTo());
    setQtyOrdered(qtyOrdered);
    setDocStatus("DR");
  }
  
  public MPPOrderNode(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_next = null;
    this.m_durationBaseMS = -1L;
    if (trxName == null)
      s_cache.put(Integer.valueOf(getPP_Order_Node_ID()), this); 
  }
  
  private List<MPPOrderNodeNext> getNodeNexts() {
    if (this.m_next != null)
      return this.m_next; 
    boolean splitAnd = "A".equals(getSplitElement());
    String whereClause = "PP_Order_Node_ID=?";
    this.m_next = (new Query(getCtx(), "PP_Order_NodeNext", whereClause, get_TrxName()))
      .setParameters(new Object[] { Integer.valueOf(get_ID()) }).setOnlyActiveRecords(true)
      .setOrderBy("SeqNo,PP_Order_Node_ID")
      .list();
    for (MPPOrderNodeNext next : this.m_next)
      next.setFromSplitAnd(splitAnd); 
    this.log.fine("#" + this.m_next.size());
    return this.m_next;
  }
  
  public void setQtyOrdered(BigDecimal qtyOrdered) {
    setQtyRequiered(qtyOrdered);
    RoutingService routingService = RoutingServiceFactory.get().getRoutingService(getAD_Client_ID());
    BigDecimal workingTime = routingService.estimateWorkingTime((I_PP_Order_Node)this, qtyOrdered);
    setDurationRequiered(workingTime.intValueExact());
  }
  
  public BigDecimal getQtyToDeliver() {
    return getQtyRequiered().subtract(getQtyDelivered());
  }
  
  public int getNextNodeCount() {
    return getNodeNexts().size();
  }
  
  public MPPOrderNodeNext[] getTransitions(int AD_Client_ID) {
    ArrayList<MPPOrderNodeNext> list = new ArrayList<MPPOrderNodeNext>();
    for (MPPOrderNodeNext next : getNodeNexts()) {
      if (next.getAD_Client_ID() == 0 || next.getAD_Client_ID() == AD_Client_ID)
        list.add(next); 
    } 
    return list.<MPPOrderNodeNext>toArray(new MPPOrderNodeNext[list.size()]);
  }
  
  public long getDurationMS() {
    long duration = getDuration();
    if (duration == 0L)
      return 0L; 
    if (this.m_durationBaseMS == -1L)
      this.m_durationBaseMS = getMPPOrderWorkflow().getDurationBaseSec() * 1000L; 
    return duration * this.m_durationBaseMS;
  }
  
  public long getLimitMS() {
    long limit = getLimit();
    if (limit == 0L)
      return 0L; 
    if (this.m_durationBaseMS == -1L)
      this.m_durationBaseMS = getMPPOrderWorkflow().getDurationBaseSec() * 1000L; 
    return limit * this.m_durationBaseMS;
  }
  
  public int getDurationCalendarField() {
    return getMPPOrderWorkflow().getDurationCalendarField();
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("MPPOrderNode[");
    sb.append(get_ID())
      .append("-").append(getName())
      .append("]");
    return sb.toString();
  }
  
  public MPPOrderWorkflow getMPPOrderWorkflow() {
    if (this.m_order_wf == null)
      this.m_order_wf = new MPPOrderWorkflow(getCtx(), getPP_Order_Workflow_ID(), get_TrxName()); 
    return this.m_order_wf;
  }
  
  public I_PP_Order_Workflow getPP_Order_Workflow() {
    return (I_PP_Order_Workflow)getMPPOrderWorkflow();
  }
  
  public void completeIt() {
    setDocStatus("CO");
    setDocAction("--");
    setDateFinish(true);
  }
  
  public void closeIt() {
    setDocStatus("CL");
    setDocAction("--");
    setDateFinish(false);
    int old = getDurationRequiered();
    if (old != getDurationReal()) {
      addDescription(Msg.parseTranslation(getCtx(), "@closed@ ( @Duration@ :" + old + ") ( @QtyRequiered@ :" + getQtyRequiered() + ")"));
      setDurationRequiered(getDurationReal());
      setQtyRequiered(getQtyDelivered());
    } 
  }
  
  public void voidIt() {
    String docStatus = getDocStatus();
    if ("VO".equals(docStatus)) {
      this.log.warning("Activity already voided - " + this);
      return;
    } 
    BigDecimal qtyRequired = getQtyRequiered();
    if (qtyRequired.signum() != 0)
      addDescription(String.valueOf(Msg.getMsg(getCtx(), "Voided")) + " (" + qtyRequired + ")"); 
    setDocStatus("VO");
    setDocAction("--");
    setQtyRequiered(Env.ZERO);
    setSetupTimeRequiered(0);
    setDurationRequiered(0);
  }
  
  public void setInProgress(MPPCostCollector currentActivity) {
    if (isProcessed())
      throw new IllegalStateException("Cannot change status from " + getDocStatus() + " to " + "IP"); 
    setDocStatus("IP");
    setDocAction("CO");
    if (currentActivity != null && getDateStart() == null)
      setDateStart(currentActivity.getDateStart()); 
  }
  
  public boolean isProcessed() {
    String status = getDocStatus();
    return !(!"CO".equals(status) && !"CL".equals(status));
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(String.valueOf(desc) + " | " + description);
    } 
  }
  
  private void setDefault() {
    setAction("Z");
    setCost(Env.ZERO);
    setDuration(0);
    setEntityType("U");
    setIsCentrallyMaintained(true);
    setJoinElement("X");
    setLimit(0);
    setSplitElement("X");
    setWaitingTime(0);
    setXPosition(0);
    setYPosition(0);
    setDocStatus("DR");
  }
  
  private void setDateFinish(boolean override) {
    if (!"CO".equals(getDocStatus()) && !"CL".equals(getDocStatus()))
      throw new IllegalStateException("Calling setDateFinish when the activity is not completed/closed is not allowed"); 
    if (!override && getDateFinish() != null) {
      this.log.fine("DateFinish already set : Date=" + getDateFinish() + ", Override=" + override);
      return;
    } 
    String sql = "SELECT MAX(MovementDate) FROM PP_Cost_Collector WHERE PP_Order_Node_ID=? AND DocStatus IN (?,?,?) AND CostCollectorType=?";
    Timestamp dateFinish = DB.getSQLValueTSEx(get_TrxName(), "SELECT MAX(MovementDate) FROM PP_Cost_Collector WHERE PP_Order_Node_ID=? AND DocStatus IN (?,?,?) AND CostCollectorType=?", new Object[] { Integer.valueOf(get_ID()), 
          "IP", 
          "CO", 
          "CL", 
          "160" });
    if (dateFinish == null) {
      this.log.warning("Activity Completed/Closed but no cost collectors found!");
      return;
    } 
    setDateFinish(dateFinish);
  }
  
  public BigDecimal getVariance(String costCollectorType, String columnName) {
    String whereClause = "PP_Order_Node_ID=? AND PP_Order_ID=? AND DocStatus IN (?,?) AND CostCollectorType=?";
    BigDecimal variance = (new Query(getCtx(), "PP_Cost_Collector", "PP_Order_Node_ID=? AND PP_Order_ID=? AND DocStatus IN (?,?) AND CostCollectorType=?", get_TrxName()))
      .setParameters(new Object[] { Integer.valueOf(getPP_Order_Node_ID()), 
          Integer.valueOf(getPP_Order_ID()), 
          "CO", "CL", 
          costCollectorType }).sum(columnName);
    return variance;
  }
  
  public BigDecimal getSetupTimeUsageVariance() {
    return getVariance("120", 
        "SetupTimeReal");
  }
  
  public BigDecimal getDurationUsageVariance() {
    return getVariance("120", 
        "DurationReal");
  }
}
