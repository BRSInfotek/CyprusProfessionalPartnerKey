package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class CalloutStandardOperation extends CalloutEngine {
  protected CLogger log = CLogger.getCLogger(getClass());
  
  public String warehouse(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null) {
      mTab.setValue("MFG_WorkCenter_ID", null);
      return "";
    } 
    int M_Warehouse_ID = ((Integer)value).intValue();
    if (0 == M_Warehouse_ID) {
      mTab.setValue("MFG_WorkCenter_ID", null);
      return "";
    } 
    MMFGWorkCenter wc = MMFGWorkCenter.getDefaultWorkCenter(Env.getCtx(), M_Warehouse_ID);
    if (wc != null) {
      mTab.setValue("MFG_WorkCenter_ID", Integer.valueOf(wc.getMFG_WorkCenter_ID()));
      return "";
    } 
    mTab.setValue("MFG_WorkCenter_ID", null);
    return Msg.getMsg(Env.getCtx(), "WorkCenterNotDefined");
  }
  
  public String setC_UOM_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int C_UOM_ID = ((Integer)value).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal SetupTime = (BigDecimal)mTab.getValue("SetupTime");
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      this.log.fine("Corrected Setup Time Scale UOM=" + C_UOM_ID + "; Setup Time = " + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      mTab.setValue("SetupTime", SetupTime);
    } 
    BigDecimal UnitRuntime = (BigDecimal)mTab.getValue("SetupTime");
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      this.log.fine("Corrected Runtime Per Unit Scale UOM=" + C_UOM_ID + "; Runtime Per Unit = " + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      mTab.setValue("UnitRuntime", UnitRuntime);
    } 
    return "";
  }
  
  public String setSetupTime(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal SetupTime = (BigDecimal)value;
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      this.log.fine("Corrected Setup Time Scale UOM=" + C_UOM_ID + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      mTab.setValue("SetupTime", SetupTime);
    } 
    return "";
  }
  
  public String setUnitRuntime(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal UnitRuntime = (BigDecimal)value;
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      this.log.fine("Corrected Runtime Per Unit UOM = " + C_UOM_ID + "; Runtime Per Unit = " + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      mTab.setValue("UnitRuntime", UnitRuntime);
    } 
    return "";
  }
}

