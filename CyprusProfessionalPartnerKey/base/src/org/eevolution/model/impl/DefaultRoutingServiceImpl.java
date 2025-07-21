package org.eevolution.model.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

import org.cyprus.exceptions.AdempiereException;
import org.cyprusbrs.framework.I_AD_WF_Node;
import org.cyprusbrs.framework.I_AD_Workflow;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_S_Resource;
import org.cyprusbrs.framework.MResource;
import org.cyprusbrs.framework.MResourceType;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.wf.MWFNode;
import org.cyprusbrs.wf.MWorkflow;
import org.eevolution.model.I_PP_Cost_Collector;
import org.eevolution.model.I_PP_Order_Node;
import org.eevolution.model.MPPOrderNode;
import org.eevolution.model.RoutingService;

public class DefaultRoutingServiceImpl implements RoutingService {
  public BigDecimal estimateWorkingTime(I_AD_WF_Node node) {
    double duration;
    if (node.getUnitsCycles().signum() == 0) {
      duration = node.getDuration();
    } else {
      duration = node.getDuration() / node.getUnitsCycles().doubleValue();
    } 
    return BigDecimal.valueOf(duration);
  }
  
  public BigDecimal estimateWorkingTime(I_PP_Order_Node node, BigDecimal qty) {
    double unitDuration = node.getDuration();
    double cycles = calculateCycles(node.getUnitsCycles(), qty);
    BigDecimal duration = BigDecimal.valueOf(unitDuration * cycles);
    return duration;
  }
  
  public BigDecimal estimateWorkingTime(I_PP_Cost_Collector cc) {
    String trxName = (cc instanceof PO) ? ((PO)cc).get_TrxName() : null;
    BigDecimal qty = cc.getMovementQty();
    MPPOrderNode node = MPPOrderNode.get(Env.getCtx(), cc.getPP_Order_Node_ID(), trxName);
    return estimateWorkingTime((I_PP_Order_Node)node, qty);
  }
  
  protected int calculateCycles(int unitsCycle, BigDecimal qty) {
    BigDecimal cycles = qty;
    BigDecimal unitsCycleBD = BigDecimal.valueOf(unitsCycle);
    if (unitsCycleBD.signum() > 0)
      cycles = qty.divide(unitsCycleBD, 0, RoundingMode.UP); 
    return cycles.intValue();
  }
  
  protected BigDecimal calculateDuration(I_AD_WF_Node node, I_PP_Cost_Collector cc) {
    double setupTime, duration;
    if (node == null)
      node = cc.getPP_Order_Node().getAD_WF_Node(); 
    I_AD_Workflow workflow = node.getAD_Workflow();
    double batchSize = workflow.getQtyBatchSize().doubleValue();
    if (cc != null) {
      setupTime = cc.getSetupTimeReal().doubleValue();
      duration = cc.getDurationReal().doubleValue();
    } else {
      setupTime = node.getSetupTime();
      duration = estimateWorkingTime(node).doubleValue();
    } 
    double totalDuration = setupTime / batchSize + duration;
    return BigDecimal.valueOf(totalDuration);
  }
  
  public BigDecimal calculateDuration(I_AD_WF_Node node) {
    return calculateDuration(node, null);
  }
  
  public BigDecimal calculateDuration(I_PP_Cost_Collector cc) {
    return calculateDuration(getAD_WF_Node(cc), cc);
  }
  
  public BigDecimal calculateDuration(I_AD_Workflow wf, I_S_Resource plant, BigDecimal qty) {
    if (plant == null)
      return Env.ZERO; 
    Properties ctx = ((PO)wf).getCtx();
    MResourceType S_ResourceType = MResourceType.get(ctx, plant.getS_ResourceType_ID());
    BigDecimal AvailableDayTime = BigDecimal.valueOf(S_ResourceType.getTimeSlotHours());
    int AvailableDays = S_ResourceType.getAvailableDaysWeek();
    double durationBaseSec = getDurationBaseSec(wf.getDurationUnit());
    double durationTotal = 0.0D;
    MWFNode[] nodes = ((MWorkflow)wf).getNodes(false, Env.getAD_Client_ID(ctx));
    byte b;
    int i;
    MWFNode[] arrayOfMWFNode1;
    for (i = (arrayOfMWFNode1 = nodes).length, b = 0; b < i; ) {
      MWFNode mWFNode = arrayOfMWFNode1[b];
      durationTotal += mWFNode.getQueuingTime();
      durationTotal += mWFNode.getSetupTime();
      durationTotal += mWFNode.getWaitingTime();
      durationTotal += mWFNode.getMovingTime();
      double overlapUnits = qty.doubleValue();
      if (mWFNode.getOverlapUnits() > 0 && mWFNode.getOverlapUnits() < overlapUnits)
        overlapUnits = mWFNode.getOverlapUnits(); 
      double durationBeforeOverlap = mWFNode.getDuration() * overlapUnits;
      durationTotal += durationBeforeOverlap;
      b++;
    } 
    BigDecimal requiredTime = BigDecimal.valueOf(durationTotal * durationBaseSec / 60.0D / 60.0D);
    BigDecimal WeeklyFactor = BigDecimal.valueOf(7L).divide(BigDecimal.valueOf(AvailableDays), 8, RoundingMode.UP);
    return requiredTime.multiply(WeeklyFactor).divide(AvailableDayTime, 0, RoundingMode.UP);
  }
  
  protected BigDecimal convertDurationToResourceUOM(BigDecimal duration, int S_Resource_ID, I_AD_WF_Node node) {
    MResource resource = MResource.get(Env.getCtx(), S_Resource_ID);
    MWorkflow mWorkflow = MWorkflow.get(Env.getCtx(), node.getAD_Workflow_ID());
    MUOM mUOM = MUOM.get(Env.getCtx(), resource.getC_UOM_ID());
    return convertDuration(duration, mWorkflow.getDurationUnit(), (I_C_UOM)mUOM);
  }
  
  public BigDecimal getResourceBaseValue(int S_Resource_ID, I_PP_Cost_Collector cc) {
    return getResourceBaseValue(S_Resource_ID, null, cc);
  }
  
  public BigDecimal getResourceBaseValue(int S_Resource_ID, I_AD_WF_Node node) {
    return getResourceBaseValue(S_Resource_ID, node, null);
  }
  
  protected BigDecimal getResourceBaseValue(int S_Resource_ID, I_AD_WF_Node node, I_PP_Cost_Collector cc) {
    if (node == null)
      node = cc.getPP_Order_Node().getAD_WF_Node(); 
    Properties ctx = (node instanceof PO) ? ((PO)node).getCtx() : Env.getCtx();
    MResource resource = MResource.get(ctx, S_Resource_ID);
    MUOM resourceUOM = MUOM.get(ctx, resource.getC_UOM_ID());
    if (isTime((I_C_UOM)resourceUOM)) {
      BigDecimal duration = calculateDuration(node, cc);
      MWorkflow mWorkflow = MWorkflow.get(ctx, node.getAD_Workflow_ID());
      BigDecimal convertedDuration = convertDuration(duration, mWorkflow.getDurationUnit(), (I_C_UOM)resourceUOM);
      return convertedDuration;
    } 
    throw new AdempiereException("@NotSupported@ @C_UOM_ID@ - " + resourceUOM);
  }
  
  protected I_AD_WF_Node getAD_WF_Node(I_PP_Cost_Collector cc) {
    I_PP_Order_Node activity = cc.getPP_Order_Node();
    return activity.getAD_WF_Node();
  }
  
  public long getDurationBaseSec(String durationUnit) {
    if (durationUnit == null)
      return 0L; 
    if ("s".equals(durationUnit))
      return 1L; 
    if ("m".equals(durationUnit))
      return 60L; 
    if ("h".equals(durationUnit))
      return 3600L; 
    if ("D".equals(durationUnit))
      return 86400L; 
    if ("M".equals(durationUnit))
      return 2592000L; 
    if ("Y".equals(durationUnit))
      return 31536000L; 
    return 0L;
  }
  
  public long getDurationBaseSec(I_C_UOM uom) {
    MUOM uomImpl = (MUOM)uom;
    if (uomImpl.isWeek())
      return 604800L; 
    if (uomImpl.isDay())
      return 86400L; 
    if (uomImpl.isHour())
      return 3600L; 
    if (uomImpl.isMinute())
      return 60L; 
    if (uomImpl.isSecond())
      return 1L; 
    throw new AdempiereException("@NotSupported@ @C_UOM_ID@=" + uom.getName());
  }
  
  public boolean isTime(I_C_UOM uom) {
    String x12de355 = uom.getX12DE355();
    if (!"03".equals(x12de355) && 
      !"MJ".equals(x12de355) && 
      !"HR".equals(x12de355) && 
      !"DA".equals(x12de355) && 
      !"WD".equals(x12de355) && 
      !"WK".equals(x12de355) && 
      !"MO".equals(x12de355) && 
      !"WM".equals(x12de355) && 
      !"YR".equals(x12de355))
      return false; 
    return true;
  }
  
  public BigDecimal convertDuration(BigDecimal duration, String fromDurationUnit, I_C_UOM toUOM) {
    double fromMult = getDurationBaseSec(fromDurationUnit);
    double toDiv = getDurationBaseSec(toUOM);
    BigDecimal convertedDuration = BigDecimal.valueOf(duration.doubleValue() * fromMult / toDiv);
    int precision = toUOM.getStdPrecision();
    if (convertedDuration.scale() > precision)
      convertedDuration = convertedDuration.setScale(precision, RoundingMode.HALF_UP); 
    return convertedDuration;
  }
}
