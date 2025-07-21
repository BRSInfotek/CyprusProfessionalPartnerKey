package org.cyprus.mfg.model;



import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.util.CLogger;

public class MMFGOperation extends X_MFG_Operation {
  private static final CLogger log = CLogger.getCLogger(MMFGOperation.class);
  
  private static final long serialVersionUID = 1L;
  
//  public MMFGOperation(Properties ctx, int MFG_Operation_ID, Trx trx) {
//    super(ctx, MFG_Operation_ID, trx);
//  }
//  
  
  public MMFGOperation(Properties ctx, int MFG_WorkCenter_ID,String trxName) {
	    super(ctx, MFG_WorkCenter_ID, trxName);
	  }
//  public MMFGOperation(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGOperation(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
}

