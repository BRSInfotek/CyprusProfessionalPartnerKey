package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.Env;

public class MPPOrderNodeProduct extends X_PP_Order_Node_Product {
  private static final long serialVersionUID = 1L;
  
  public MPPOrderNodeProduct(Properties ctx, int PP_WF_Order_Product_ID, String trxName) {
    super(ctx, PP_WF_Order_Product_ID, trxName);
  }
  
  public MPPOrderNodeProduct(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }
  
  public MPPOrderNodeProduct(MPPWFNodeProduct np, MPPOrderNode PP_Order_Node) {
    this(PP_Order_Node.getCtx(), 0, PP_Order_Node.get_TrxName());
    setClientOrg((PO)PP_Order_Node);
    setSeqNo(np.getSeqNo());
    setIsActive(np.isActive());
    setM_Product_ID(np.getM_Product_ID());
    setQty(np.getQty());
    setIsSubcontracting(np.isSubcontracting());
    setPP_Order_ID(PP_Order_Node.getPP_Order_ID());
    setPP_Order_Workflow_ID(PP_Order_Node.getPP_Order_Workflow_ID());
    setPP_Order_Node_ID(PP_Order_Node.get_ID());
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getQty().signum() == 0 && isSubcontracting())
      setQty(Env.ONE); 
    return true;
  }
}
