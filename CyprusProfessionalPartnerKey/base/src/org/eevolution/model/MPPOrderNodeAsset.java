package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.PO;

public class MPPOrderNodeAsset extends X_PP_Order_Node_Asset {
  private static final long serialVersionUID = 1L;
  
  public MPPOrderNodeAsset(Properties ctx, int PP_Order_Node_Asset_ID, String trxName) {
    super(ctx, PP_Order_Node_Asset_ID, trxName);
  }
  
  public MPPOrderNodeAsset(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }
  
  public MPPOrderNodeAsset(MPPWFNodeAsset na, MPPOrderNode PP_Order_Node) {
    this(PP_Order_Node.getCtx(), 0, PP_Order_Node.get_TrxName());
    setClientOrg((PO)PP_Order_Node);
    
    if(na.getA_Asset_ID()>0)
    setA_Asset_ID(na.getA_Asset_ID());
    else
    setA_Asset_ID(0);
    
    if(na.get_ValueAsInt("AD_User_ID")>0)
    set_ValueNoCheck("AD_User_ID", na.get_ValueAsInt("AD_User_ID"));
    else
    set_ValueNoCheck("AD_User_ID", null);
    
    set_ValueNoCheck("Description", na.get_Value("Description"));
    set_ValueNoCheck("M_Product_ID", na.get_ValueAsInt("M_Product_ID"));
    set_ValueNoCheck("C_UOM_ID", na.get_ValueAsInt("C_UOM_ID"));
    set_ValueNoCheck("QtyRequiered", na.get_ValueAsBigDecimal("QtyRequiered"));
    setPP_Order_ID(PP_Order_Node.getPP_Order_ID());
    setPP_Order_Workflow_ID(PP_Order_Node.getPP_Order_Workflow_ID());
    setPP_Order_Node_ID(PP_Order_Node.get_ID());
  }
}
