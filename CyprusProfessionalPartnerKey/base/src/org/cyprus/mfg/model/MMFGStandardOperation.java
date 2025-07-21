package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Util;

public class MMFGStandardOperation extends X_MFG_StandardOperation {
  private static final CLogger log = CLogger.getCLogger(MMFGStandardOperation.class);
  
  private static final long serialVersionUID = 1L;
  
//  public MMFGStandardOperation(Properties ctx, int MFG_StandardOperation_ID, Trx trx) {
//    super(ctx, MFG_StandardOperation_ID, trx);
//  }
//  
//  public MMFGStandardOperation(Properties ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGStandardOperation(Properties ctx, int MFG_StandardOperation_ID, String trxName) {
	    super(ctx, MFG_StandardOperation_ID, trxName);
	  }
	  
	  public MMFGStandardOperation(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getC_UOM_ID() != 0) {
      MUOM uom = MUOM.get(getCtx(), getC_UOM_ID());
      if (!uom.isDay() && !uom.isHour()) {
        log.saveError("Error", Msg.translate(getCtx(), "UOMNotAllowed"));
        return false;
      } 
    } 
    if (getSetupTime().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SetupTime@ < 0"));
      return false;
    } 
    if (getUnitRuntime().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@UnitRuntime@ < 0"));
      return false;
    } 
    if (newRecord || is_ValueChanged("MFG_WorkCenter_ID")) {
//      int no = QueryUtil.getSQLValue(get_Trx(), "SELECT 1 FROM MFG_WorkCenter wc WHERE wc.MFG_WorkCenter_ID=? AND wc.M_Warehouse_ID=?", new Object[] { Integer.valueOf(getMFG_WorkCenter_ID()), Integer.valueOf(getM_Warehouse_ID()) });
        int no = DB.getSQLValue(get_TrxName(), "SELECT 1 FROM MFG_WorkCenter wc WHERE wc.MFG_WorkCenter_ID=? AND wc.M_Warehouse_ID=?", new Object[] { Integer.valueOf(getMFG_WorkCenter_ID()), Integer.valueOf(getM_Warehouse_ID()) });

    	if (no == -1) {
        log.saveError("Error", Msg.parseTranslation(getCtx(), "@WorkCenterWarehouseMismatch@"));
        return false;
      } 
    } 
    return true;
  }
  
  //@UICallout
  public void setM_Warehouse_ID(String oldM_Warehouse_ID, String newM_Warehouse_ID, int windowNo) throws Exception {
    if (Util.isEmpty(newM_Warehouse_ID)) {
      set_ValueNoCheck("MFG_WorkCenter_ID", null);
      return;
    } 
    int M_Warehouse_ID = Integer.parseInt(newM_Warehouse_ID);
    if (0 == M_Warehouse_ID) {
      set_ValueNoCheck("MFG_WorkCenter_ID", null);
      return;
    } 
    MMFGWorkCenter wc = MMFGWorkCenter.getDefaultWorkCenter(getCtx(), M_Warehouse_ID);
    if (wc != null) {
      setMFG_WorkCenter_ID(wc.getMFG_WorkCenter_ID());
    } else {
      set_ValueNoCheck("MFG_WorkCenter_ID", null);
    } 
  }
  
  //@UICallout
  public void setC_UOM_ID(String oldC_UOM_ID, String newC_UOM_ID, int windowNo) throws Exception {
    if (newC_UOM_ID == null || newC_UOM_ID.trim().length() == 0)
      return; 
    int C_UOM_ID = Integer.parseInt(newC_UOM_ID);
    if (0 == C_UOM_ID)
      return; 
    BigDecimal SetupTime = getSetupTime();
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(getCtx(), C_UOM_ID), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      log.fine("Corrected Setup Time Scale UOM=" + getC_UOM_ID() + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      setSetupTime(SetupTime);
    } 
    BigDecimal UnitRuntime = getUnitRuntime();
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(getCtx(), C_UOM_ID), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      log.fine("Corrected UnitRuntime Scale UOM=" + getC_UOM_ID() + "; UnitRuntime=" + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      setUnitRuntime(UnitRuntime);
    } 
  }
  
 // @UICallout
  public void setSetupTime(String oldSetupTime, String newSetupTime, int windowNo) throws Exception {
    if (newSetupTime == null || 0 == newSetupTime.trim().length())
      return; 
    if (0 == getC_UOM_ID())
      return; 
    BigDecimal SetupTime = getSetupTime();
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      log.fine("Corrected Setup Time Scale UOM=" + getC_UOM_ID() + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      setSetupTime(SetupTime);
    } 
  }
  
 // @UICallout
  public void setUnitRuntime(String oldUnitRuntime, String newUnitRuntime, int windowNo) throws Exception {
    if (newUnitRuntime == null || 0 == newUnitRuntime.trim().length())
      return; 
    if (0 == getC_UOM_ID())
      return; 
    BigDecimal UnitRuntime = getUnitRuntime();
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      log.fine("Corrected UnitRuntime Scale UOM=" + getC_UOM_ID() + "; UnitRuntime=" + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      setUnitRuntime(UnitRuntime);
    } 
  }
}

