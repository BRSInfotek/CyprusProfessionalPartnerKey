package org.cyprus.mrp.model;


import java.sql.ResultSet;
import java.util.Properties;

//import org.cyprusbrs.model.X_MRP_PlannedReceipt;
import org.cyprusbrs.util.CLogger;

public class MMRPPlannedReceipt extends X_MRP_PlannedReceipt {
  private static final CLogger log = CLogger.getCLogger(MMRPPlannedReceipt.class);
  
  private static final long serialVersionUID = 1L;
  
  public MMRPPlannedReceipt(Properties ctx, int ignored, String trx) {
    super(ctx, ignored, trx);
    if (ignored != 0)
      throw new IllegalArgumentException("Multi-Key"); 
  }
  
  public MMRPPlannedReceipt(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
}
