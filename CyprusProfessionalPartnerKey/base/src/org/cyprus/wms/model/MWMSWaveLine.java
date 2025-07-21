package org.cyprus.wms.model;


//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

//import org.cyprusbrs.model.X_C_WaveLine;
import org.cyprusbrs.util.CLogger;

public class MWMSWaveLine extends X_WMS_WaveLine {
  private static final CLogger log = CLogger.getCLogger(MWMSWaveLine.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSWaveLine(Properties ctx, int M_WaveLine_ID, String trx) {
    super(ctx, M_WaveLine_ID, trx);
  }
  
  public MWMSWaveLine(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSWaveLine(Properties ctx, MWMSWave wave, int C_OrderLine_ID, int M_WorkOrderComponent_ID, BigDecimal MovementQty, String trx) {
    this(ctx, 0, trx);
    setWMS_Wave_ID(wave.getWMS_Wave_ID());
    setC_OrderLine_ID(C_OrderLine_ID);
    if (M_WorkOrderComponent_ID != 0)
      setMFG_WorkOrderComponent_ID(M_WorkOrderComponent_ID); 
    setMovementQty(MovementQty);
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (getC_OrderLine_ID() != 0 && getMFG_WorkOrderComponent_ID() != 0) {
      log.saveError("Error", "Enter either Order Line or Work Order Component, not both.");
      return false;
    } 
    if (is_ValueChanged("M_WorkOrderComponent_ID")) {
//      SysEnv se2 = SysEnv.get("CMFG");
//      if (se2 == null || !se2.checkLicense())
        return false; 
    } 
    return true;
  }
}

