package org.cyprus.mfg.model;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMFGWorkOrderValue extends X_MFG_WorkOrderValue {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderValue.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static final CLogger s_log = CLogger.getCLogger(MMFGWorkOrderValue.class);
  
  public MMFGWorkOrderValue(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MMFGWorkOrderValue(Properties ctx, int ignore, String trx) {
    super(ctx, ignore, trx);
  }
  
  public static MMFGWorkOrderValue getWorkOrderValue(Properties ctx, int M_WorkOrder_ID, int C_AcctSchema_ID, String trx) {
    CPreparedStatement cPreparedStatement = null;
    MMFGWorkOrderValue retval = null;
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderValue WHERE C_AcctSchema_ID = ? ");
    sqlstmt.append("AND MFG_WorkOrder_ID = ? ");
    String sql = sqlstmt.toString();
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, C_AcctSchema_ID);
      cPreparedStatement.setInt(2, M_WorkOrder_ID);
      ResultSet rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retval = new MMFGWorkOrderValue(ctx, rs, trx); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        cPreparedStatement = null;
      } catch (Exception e) {
        cPreparedStatement = null;
      } 
    } 
    return retval;
  }
}

