package org.eevolution.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CCache;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.wf.MWorkflow;

public class MPPOrderWorkflow extends X_PP_Order_Workflow {
  private static final long serialVersionUID = 1L;
  
  public static MPPOrderWorkflow get(Properties ctx, int PP_Order_Workflow_ID) {
    if (PP_Order_Workflow_ID <= 0)
      return null; 
    MPPOrderWorkflow retValue = (MPPOrderWorkflow)s_cache.get(Integer.valueOf(PP_Order_Workflow_ID));
    if (retValue != null)
      return retValue; 
    retValue = new MPPOrderWorkflow(ctx, PP_Order_Workflow_ID, null);
    if (retValue.get_ID() != 0)
      s_cache.put(Integer.valueOf(PP_Order_Workflow_ID), retValue); 
    return retValue;
  }
  
  private static CCache<Integer, MPPOrderWorkflow> s_cache = new CCache("PP_Order_Workflow", 20);
  
  private List<MPPOrderNode> m_nodes;
  
  private MPPOrder m_order;
  
  public MPPOrderWorkflow(Properties ctx, int PP_Order_Workflow_ID, String trxName) {
    super(ctx, PP_Order_Workflow_ID, trxName);
    this.m_nodes = null;
    this.m_order = null;
    if (PP_Order_Workflow_ID == 0) {
      setAccessLevel("1");
      setAuthor(MClient.get(ctx).getName());
      setDurationUnit("D");
      setDuration(1);
      setEntityType("U");
      setIsDefault(false);
      setPublishStatus("U");
      setVersion(0);
      setCost(Env.ZERO);
      setWaitingTime(0);
      setWorkingTime(0);
    } 
  }
  
  public MPPOrderWorkflow(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_nodes = null;
    this.m_order = null;
  }
  
  public MPPOrderWorkflow(MWorkflow workflow, int PP_Order_ID, String trxName) {
    this(workflow.getCtx(), 0, trxName);
    setPP_Order_ID(PP_Order_ID);
    setValue(workflow.getValue());
    setWorkflowType(workflow.getWorkflowType());
    setQtyBatchSize(workflow.getQtyBatchSize());
    setName(workflow.getName());
    setAccessLevel(workflow.getAccessLevel());
    setAuthor(workflow.getAuthor());
    setDurationUnit(workflow.getDurationUnit());
    setDuration(workflow.getDuration());
    setEntityType(workflow.getEntityType());
    setIsDefault(workflow.isDefault());
    setPublishStatus(workflow.getPublishStatus());
    setVersion(workflow.getVersion());
    setCost(workflow.getCost());
    setWaitingTime(workflow.getWaitingTime());
    setWorkingTime(workflow.getWorkingTime());
    setAD_WF_Responsible_ID(workflow.getAD_WF_Responsible_ID());
    setAD_Workflow_ID(workflow.getAD_Workflow_ID());
    setLimit(workflow.getLimit());
    setPriority(workflow.getPriority());
    setS_Resource_ID(workflow.getS_Resource_ID());
    setQueuingTime(workflow.getQueuingTime());
    setSetupTime(workflow.getSetupTime());
    setMovingTime(workflow.getMovingTime());
    setProcessType(workflow.getProcessType());
    setAD_Table_ID(workflow.getAD_Table_ID());
    setAD_WF_Node_ID(workflow.getAD_WF_Node_ID());
    setAD_WorkflowProcessor_ID(workflow.getAD_WorkflowProcessor_ID());
    setDescription(workflow.getDescription());
    setValidFrom(workflow.getValidFrom());
    setValidTo(workflow.getValidTo());
  }
  
  protected List<MPPOrderNode> getNodes(boolean requery) {
    if (this.m_nodes == null || requery) {
      String whereClause = "PP_Order_Workflow_ID=?";
      this.m_nodes = (new Query(getCtx(), "PP_Order_Node", "PP_Order_Workflow_ID=?", get_TrxName()))
        .setParameters(new Object[] { Integer.valueOf(get_ID()) }).setOnlyActiveRecords(true)
        .list();
      this.log.fine("#" + this.m_nodes.size());
    } 
    return this.m_nodes;
  }
  
  protected List<MPPOrderNode> getNodes() {
    return getNodes(false);
  }
  
  public int getNodeCount() {
    return getNodes().size();
  }
  
  public MPPOrderNode[] getNodes(boolean ordered, int AD_Client_ID) {
    if (ordered)
      return getNodesInOrder(AD_Client_ID); 
    ArrayList<MPPOrderNode> list = new ArrayList<MPPOrderNode>();
    for (MPPOrderNode node : getNodes()) {
      if (node.getAD_Client_ID() == 0 || node.getAD_Client_ID() == AD_Client_ID)
        list.add(node); 
    } 
    return list.<MPPOrderNode>toArray(new MPPOrderNode[list.size()]);
  }
  
  public MPPOrderNode getFirstNode() {
    return getNode(getPP_Order_Node_ID());
  }
  
  private MPPOrderNode getNode(int PP_Order_Node_ID, int AD_Client_ID) {
    if (PP_Order_Node_ID <= 0)
      return null; 
    for (MPPOrderNode node : getNodes()) {
      if (node.getPP_Order_Node_ID() == PP_Order_Node_ID) {
        if (AD_Client_ID >= 0) {
          if (node.getAD_Client_ID() == 0 || node.getAD_Client_ID() == AD_Client_ID)
            return node; 
          return null;
        } 
        return node;
      } 
    } 
    return null;
  }
  
  public MPPOrderNode getNode(int PP_Order_Node_ID) {
    return getNode(PP_Order_Node_ID, -1);
  }
  
  public MPPOrderNode[] getNextNodes(int PP_Order_Node_ID, int AD_Client_ID) {
    MPPOrderNode node = getNode(PP_Order_Node_ID);
    if (node == null || node.getNextNodeCount() == 0)
      return null; 
    ArrayList<MPPOrderNode> list = new ArrayList<MPPOrderNode>();
    byte b;
    int i;
    MPPOrderNodeNext[] arrayOfMPPOrderNodeNext;
    for (i = (arrayOfMPPOrderNodeNext = node.getTransitions(AD_Client_ID)).length, b = 0; b < i; ) {
      MPPOrderNodeNext nextTr = arrayOfMPPOrderNodeNext[b];
      MPPOrderNode next = getNode(nextTr.getPP_Order_Next_ID(), AD_Client_ID);
      if (next != null)
        list.add(next); 
      b++;
    } 
    return list.<MPPOrderNode>toArray(new MPPOrderNode[list.size()]);
  }
  
  private MPPOrderNode[] getNodesInOrder(int AD_Client_ID) {
    ArrayList<MPPOrderNode> list = new ArrayList<MPPOrderNode>();
    addNodesSF(list, getPP_Order_Node_ID(), AD_Client_ID);
    if (getNodeCount() != list.size())
      for (MPPOrderNode node : getNodes()) {
        if (node.getAD_Client_ID() == 0 || node.getAD_Client_ID() == AD_Client_ID) {
          boolean found = false;
          for (MPPOrderNode existing : list) {
            if (existing.getPP_Order_Node_ID() == node.getPP_Order_Node_ID()) {
              found = true;
              break;
            } 
          } 
          if (!found) {
            this.log.log(Level.WARNING, "Added Node w/o transition: " + node);
            list.add(node);
          } 
        } 
      }  
    MPPOrderNode[] nodeArray = new MPPOrderNode[list.size()];
    list.toArray(nodeArray);
    return nodeArray;
  }
  
  private void addNodesSF(Collection<MPPOrderNode> list, int PP_Order_Node_ID, int AD_Client_ID) {
    MPPOrderNode node = getNode(PP_Order_Node_ID, AD_Client_ID);
    if (node != null) {
      if (!list.contains(node))
        list.add(node); 
      ArrayList<Integer> nextNodes = new ArrayList<Integer>();
      byte b;
      int i;
      MPPOrderNodeNext[] arrayOfMPPOrderNodeNext;
      for (i = (arrayOfMPPOrderNodeNext = node.getTransitions(AD_Client_ID)).length, b = 0; b < i; ) {
        MPPOrderNodeNext next = arrayOfMPPOrderNodeNext[b];
        MPPOrderNode child = getNode(next.getPP_Order_Next_ID(), AD_Client_ID);
        if (child != null)
          if (!list.contains(child)) {
            list.add(child);
            nextNodes.add(Integer.valueOf(next.getPP_Order_Next_ID()));
          } else {
            this.log.saveError("Error", "Cyclic transition found - " + node + " -> " + child);
          }  
        b++;
      } 
      for (Iterator<Integer> iterator = nextNodes.iterator(); iterator.hasNext(); ) {
        int pp_Order_Next_ID = ((Integer)iterator.next()).intValue();
        addNodesSF(list, pp_Order_Next_ID, AD_Client_ID);
      } 
    } 
  }
  
  public int getNext(int PP_Order_Node_ID, int AD_Client_ID) {
    MPPOrderNode[] nodes = getNodesInOrder(AD_Client_ID);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].getPP_Order_Node_ID() == PP_Order_Node_ID) {
        MPPOrderNodeNext[] nexts = nodes[i].getTransitions(AD_Client_ID);
        if (nexts.length > 0)
          return nexts[0].getPP_Order_Next_ID(); 
        return 0;
      } 
    } 
    return 0;
  }
  
  public MPPOrderNodeNext[] getNodeNexts(int PP_Order_Node_ID, int AD_Client_ID) {
    MPPOrderNode[] nodes = getNodesInOrder(AD_Client_ID);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].getPP_Order_Node_ID() == PP_Order_Node_ID)
        return nodes[i].getTransitions(AD_Client_ID); 
    } 
    return new MPPOrderNodeNext[0];
  }
  
  public int getPrevious(int PP_Order_Node_ID, int AD_Client_ID) {
    MPPOrderNode[] nodes = getNodesInOrder(AD_Client_ID);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].getPP_Order_Node_ID() == PP_Order_Node_ID) {
        if (i > 0)
          return nodes[i - 1].getPP_Order_Node_ID(); 
        return 0;
      } 
    } 
    return 0;
  }
  
  public int getNodeLastID(int AD_Client_ID) {
    MPPOrderNode[] nodes = getNodesInOrder(AD_Client_ID);
    if (nodes.length > 0)
      return nodes[nodes.length - 1].getPP_Order_Node_ID(); 
    return 0;
  }
  
  public MPPOrderNode getLastNode(int AD_Client_ID) {
    MPPOrderNode[] nodes = getNodesInOrder(AD_Client_ID);
    if (nodes.length > 0)
      return nodes[nodes.length - 1]; 
    return null;
  }
  
  public boolean isFirst(int PP_Order_Node_ID, int AD_Client_ID) {
    return (PP_Order_Node_ID == getPP_Order_Node_ID());
  }
  
  public boolean isLast(int PP_Order_Node_ID, int AD_Client_ID) {
    MPPOrderNode[] nodes = getNodesInOrder(AD_Client_ID);
    return (PP_Order_Node_ID == nodes[nodes.length - 1].getPP_Order_Node_ID());
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("MPPOrderWorkflow[");
    sb.append(get_ID()).append("-").append(getName())
      .append("]");
    return sb.toString();
  }
  
  protected boolean afterSave(boolean newRecord, boolean success) {
    this.log.fine("Success=" + success);
    if (success && newRecord) {
      MPPOrderNode[] nodes = getNodesInOrder(0);
      for (int i = 0; i < nodes.length; i++)
        nodes[i].saveEx(get_TrxName()); 
    } 
    return success;
  }
  
  public long getDurationBaseSec() {
    if (getDurationUnit() == null)
      return 0L; 
    if ("s".equals(getDurationUnit()))
      return 1L; 
    if ("m".equals(getDurationUnit()))
      return 60L; 
    if ("h".equals(getDurationUnit()))
      return 3600L; 
    if ("D".equals(getDurationUnit()))
      return 86400L; 
    if ("M".equals(getDurationUnit()))
      return 2592000L; 
    if ("Y".equals(getDurationUnit()))
      return 31536000L; 
    return 0L;
  }
  
  public int getDurationCalendarField() {
    if (getDurationUnit() == null)
      return 12; 
    if ("s".equals(getDurationUnit()))
      return 13; 
    if ("m".equals(getDurationUnit()))
      return 12; 
    if ("h".equals(getDurationUnit()))
      return 10; 
    if ("D".equals(getDurationUnit()))
      return 6; 
    if ("M".equals(getDurationUnit()))
      return 2; 
    if ("Y".equals(getDurationUnit()))
      return 1; 
    return 12;
  }
  
  public void closeActivities(MPPOrderNode activity, Timestamp movementDate, boolean milestone) {
    if (activity.getPP_Order_Workflow_ID() != get_ID())
      throw new CyprusException("Activity and Order Workflow not matching (" + 
          activity + ", PP_Order_Workflow_ID=" + get_ID() + ")"); 
    MPPOrder order = getMPPOrder();
    for (int nodeId = activity.get_ID(); nodeId != 0; nodeId = getPrevious(nodeId, getAD_Client_ID())) {
      MPPOrderNode node = getNode(nodeId);
      if (milestone && node.isMilestone() && node.get_ID() != activity.get_ID())
        break; 
      if ("DR".equals(node.getDocStatus())) {
        BigDecimal qtyToDeliver = node.getQtyToDeliver();
        if (qtyToDeliver.signum() > 0) {
          int setupTimeReal = node.getSetupTimeRequiered() - node.getSetupTimeReal();
          RoutingService routingService = RoutingServiceFactory.get().getRoutingService(node.getAD_Client_ID());
          BigDecimal durationReal = routingService.estimateWorkingTime((I_PP_Order_Node)node, qtyToDeliver);
          MPPCostCollector.createCollector(
              order, 
              order.getM_Product_ID(), 
              order.getM_Locator_ID(), 
              order.getM_AttributeSetInstance_ID(), 
              node.getS_Resource_ID(), 
              0, 
              node.get_ID(), 
              MDocType.getDocType("MCC"), 
              "160", 
              movementDate, 
              qtyToDeliver, Env.ZERO, Env.ZERO, 
              setupTimeReal, durationReal);
          node.load(order.get_TrxName());
          node.closeIt();
          node.saveEx();
        } 
      } else if ("CO".equals(node.getDocStatus()) || 
        "IP".equals(node.getDocStatus())) {
        node.closeIt();
        node.saveEx();
      } 
    } 
    this.m_nodes = null;
  }
  
  public void voidActivities() {
    byte b;
    int i;
    MPPOrderNode[] arrayOfMPPOrderNode;
    for (i = (arrayOfMPPOrderNode = getNodes(true, getAD_Client_ID())).length, b = 0; b < i; ) {
      MPPOrderNode node = arrayOfMPPOrderNode[b];
      BigDecimal old = node.getQtyRequiered();
      if (old.signum() != 0) {
        node.addDescription(String.valueOf(Msg.getMsg(getCtx(), "Voided")) + " (" + old + ")");
        node.voidIt();
        node.saveEx();
      } 
      b++;
    } 
  }
  
  public MPPOrder getMPPOrder() {
    if (this.m_order == null)
      this.m_order = new MPPOrder(getCtx(), getPP_Order_ID(), get_TrxName()); 
    return this.m_order;
  }
}

