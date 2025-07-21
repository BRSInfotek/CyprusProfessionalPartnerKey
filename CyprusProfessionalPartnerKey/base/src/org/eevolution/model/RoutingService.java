package org.eevolution.model;

import java.math.BigDecimal;

import org.cyprusbrs.framework.I_AD_WF_Node;
import org.cyprusbrs.framework.I_AD_Workflow;
import org.cyprusbrs.framework.I_S_Resource;

public interface RoutingService {
  BigDecimal estimateWorkingTime(I_AD_WF_Node paramI_AD_WF_Node);
  
  BigDecimal estimateWorkingTime(I_PP_Order_Node paramI_PP_Order_Node, BigDecimal paramBigDecimal);
  
  BigDecimal estimateWorkingTime(I_PP_Cost_Collector paramI_PP_Cost_Collector);
  
  BigDecimal calculateDuration(I_AD_WF_Node paramI_AD_WF_Node);
  
  BigDecimal calculateDuration(I_AD_Workflow paramI_AD_Workflow, I_S_Resource paramI_S_Resource, BigDecimal paramBigDecimal);
  
  BigDecimal calculateDuration(I_PP_Cost_Collector paramI_PP_Cost_Collector);
  
  BigDecimal getResourceBaseValue(int paramInt, I_PP_Cost_Collector paramI_PP_Cost_Collector);
  
  BigDecimal getResourceBaseValue(int paramInt, I_AD_WF_Node paramI_AD_WF_Node);
}
