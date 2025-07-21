package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.wf.MWFNodeNext;

public class MPPOrderNodeNext extends X_PP_Order_NodeNext {
  private static final long serialVersionUID = 1L;
  
  public Boolean m_fromSplitAnd;
  
  public Boolean m_toJoinAnd;
  
  public MPPOrderNodeNext(Properties ctx, int PP_OrderNodeNext_ID, String trxName) {
    super(ctx, PP_OrderNodeNext_ID, trxName);
    this.m_fromSplitAnd = null;
    this.m_toJoinAnd = null;
    if (PP_OrderNodeNext_ID == 0) {
      setEntityType("U");
      setIsStdUserWorkflow(false);
      setSeqNo(10);
    } 
  }
  
  public MPPOrderNodeNext(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_fromSplitAnd = null;
    this.m_toJoinAnd = null;
  }
  
  public MPPOrderNodeNext(MPPOrderNode parent, int PP_Order_Next_ID) {
    this(parent.getCtx(), 0, parent.get_TrxName());
    setClientOrg((PO)parent);
    setPP_Order_ID(parent.getPP_Order_ID());
    setPP_Order_Node_ID(parent.get_ID());
    setPP_Order_Next_ID(PP_Order_Next_ID);
  }
  
  public MPPOrderNodeNext(MWFNodeNext wfNodeNext, MPPOrderNode parent) {
    this(parent, 0);
    setAD_WF_Node_ID(wfNodeNext.getAD_WF_Node_ID());
    setAD_WF_Next_ID(wfNodeNext.getAD_WF_Next_ID());
    setDescription(wfNodeNext.getDescription());
    setEntityType(wfNodeNext.getEntityType());
    setIsStdUserWorkflow(wfNodeNext.isStdUserWorkflow());
    setSeqNo(wfNodeNext.getSeqNo());
    setTransitionCode(wfNodeNext.getTransitionCode());
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("MPPOrderNodeNext[");
    sb.append(getSeqNo())
      .append(":Node=").append(getPP_Order_Node_ID()).append("->Next=").append(getPP_Order_Next_ID());
    if (getDescription() != null && getDescription().length() > 0)
      sb.append(",").append(getDescription()); 
    sb.append("]");
    return sb.toString();
  }
  
  public boolean isFromSplitAnd() {
    if (this.m_fromSplitAnd != null)
      return this.m_fromSplitAnd.booleanValue(); 
    return false;
  }
  
  public void setFromSplitAnd(boolean fromSplitAnd) {
    this.m_fromSplitAnd = new Boolean(fromSplitAnd);
  }
  
  public boolean isToJoinAnd() {
    if (this.m_toJoinAnd == null && getPP_Order_Next_ID() != 0) {
      MPPOrderNode next = MPPOrderNode.get(getCtx(), getPP_Order_Next_ID(), get_TrxName());
      setToJoinAnd("A".equals(next.getJoinElement()));
    } 
    if (this.m_toJoinAnd != null)
      return this.m_toJoinAnd.booleanValue(); 
    return false;
  }
  
  private void setToJoinAnd(boolean toJoinAnd) {
    this.m_toJoinAnd = new Boolean(toJoinAnd);
  }
  
  public void setPP_Order_Next_ID() {
    String sql = "SELECT PP_Order_Node_ID FROM PP_Order_Node  WHERE PP_Order_ID=? AND AD_WF_Node_ID=? AND AD_Client_ID=?";
    int id = DB.getSQLValueEx(get_TrxName(), "SELECT PP_Order_Node_ID FROM PP_Order_Node  WHERE PP_Order_ID=? AND AD_WF_Node_ID=? AND AD_Client_ID=?", new Object[] { Integer.valueOf(getPP_Order_ID()), Integer.valueOf(getAD_WF_Next_ID()), Integer.valueOf(getAD_Client_ID()) });
    setPP_Order_Next_ID((id > 0) ? id : 0);
  }
}

