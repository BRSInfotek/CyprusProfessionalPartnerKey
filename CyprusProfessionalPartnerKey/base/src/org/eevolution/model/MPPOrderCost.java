package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.MCost;
import org.cyprusbrs.model.PO;

public class MPPOrderCost extends X_PP_Order_Cost {
  private static final long serialVersionUID = 1L;
  
  public MPPOrderCost(Properties ctx, int PP_Order_Cost_ID, String trxName) {
    super(ctx, PP_Order_Cost_ID, trxName);
  }
  
  public MPPOrderCost(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }
  
  public MPPOrderCost(MCost cost, int PP_Order_ID, String trxName) {
    this(cost.getCtx(), 0, trxName);
    setClientOrg((PO)cost);
    setPP_Order_ID(PP_Order_ID);
    setC_AcctSchema_ID(cost.getC_AcctSchema_ID());
    setM_CostType_ID(cost.getM_CostType_ID());
    setCumulatedAmt(cost.getCumulatedAmt());
    setCumulatedQty(cost.getCumulatedQty());
    setCurrentCostPrice(cost.getCurrentCostPrice());
    setCurrentCostPriceLL(cost.getCurrentCostPriceLL());
    setM_Product_ID(cost.getM_Product_ID());
    setM_AttributeSetInstance_ID(cost.getM_AttributeSetInstance_ID());
    setM_CostElement_ID(cost.getM_CostElement_ID());
  }
}
