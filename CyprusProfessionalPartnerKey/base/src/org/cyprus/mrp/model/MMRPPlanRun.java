package org.cyprus.mrp.model;


import java.sql.ResultSet;
import java.util.Properties;

//import org.cyprusbrs.model.X_MRP_PlanRun;
import org.cyprusbrs.util.CLogger;

public class MMRPPlanRun extends X_MRP_PlanRun {
  private static final CLogger log = CLogger.getCLogger(MMRPPlanRun.class);
  
  private static final long serialVersionUID = 1L;
  
  public MMRPPlanRun(Properties ctx, int MRP_PlanRun_ID, String trx) {
    super(ctx, MRP_PlanRun_ID, trx);
  }
  
  public MMRPPlanRun(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
}

