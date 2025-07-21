package org.cyprus.wms.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.MWarehouse;
//import org.cyprusbrs.model.X_C_Wave;
import org.cyprusbrs.util.CLogger;

public class MWMSWave extends X_WMS_Wave {
  private static final CLogger log = CLogger.getCLogger(MWMSWave.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSWave(Properties ctx, int M_Wave_ID, String trx) {
    super(ctx, M_Wave_ID, trx);
  }
  
  public MWMSWave(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSWave(Properties ctx, String trx, int M_Warehouse_ID) {
    this(ctx, 0, trx);
    setM_Warehouse_ID(M_Warehouse_ID);
    MWarehouse wh = MWarehouse.get(ctx, M_Warehouse_ID);
    setAD_Org_ID(wh.getAD_Org_ID());
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
}

