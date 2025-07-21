package org.cyprus.mfg.model;



import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.X_M_BOMAlternative;
import org.cyprusbrs.util.CLogger;

public class MBOMAlternative extends X_M_BOMAlternative {
  private static final CLogger log = CLogger.getCLogger(MBOMAlternative.class);
  
  private static final long serialVersionUID = 1L;
  
  public MBOMAlternative(Properties ctx, int M_BOMAlternative_ID, String trx) {
    super(ctx, M_BOMAlternative_ID, trx);
  }
  
  public MBOMAlternative(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
}

