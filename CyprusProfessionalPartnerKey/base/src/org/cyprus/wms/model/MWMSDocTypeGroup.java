package org.cyprus.wms.model;



import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
//import org.cyprusbrs.util.Ctx;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;

public class MWMSDocTypeGroup extends X_WMS_DocTypeGroup {
  private static final CLogger log = CLogger.getCLogger(MWMSDocTypeGroup.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSDocTypeGroup(Properties ctx, int C_DocTypeGroup_ID, String trx) {
    super(ctx, C_DocTypeGroup_ID, trx);
  }
  
  public MWMSDocTypeGroup(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public static boolean includesDocType(int C_DocTypeGroup_ID, int C_DocType_ID, Trx trx) {
    String sql = "SELECT 1 FROM C_DocTypeGroupLine WHERE C_DocTypeGroup_ID=? AND C_DocType_ID=? AND IsActive='Y'";
    if (DB.getSQLValue(trx.getTrxName(), sql, new Object[] { Integer.valueOf(C_DocTypeGroup_ID), Integer.valueOf(C_DocType_ID) }) == 1)
      return true; 
    return false;
  }
}
