package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.util.CLogger;

public class MWMSMMStrategyLine extends X_WMS_MMStrategyLine {
  private static final CLogger log = CLogger.getCLogger(MWMSMMStrategyLine.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSMMStrategyLine(Properties ctx, int M_MMStrategyLine_ID, String trx) {
    super(ctx, M_MMStrategyLine_ID, trx);
  }
  
  public MWMSMMStrategyLine(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSMMRule getRule() {
	  MWMSMMRule rule = new MWMSMMRule(getCtx(), getWMS_MMRule_ID(), get_TrxName());
    return rule;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
}

