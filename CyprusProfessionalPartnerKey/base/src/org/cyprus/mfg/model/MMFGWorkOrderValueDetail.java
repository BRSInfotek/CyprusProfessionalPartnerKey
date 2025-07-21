package org.cyprus.mfg.model;




import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMFGWorkOrderValueDetail extends X_MFG_WorkOrderValueDetail {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderValueDetail.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static final CLogger s_log = CLogger.getCLogger(MMFGWorkOrderValueDetail.class);
  
  public MMFGWorkOrderValueDetail(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MMFGWorkOrderValueDetail(Properties ctx, int ignore, String trx) {
    super(ctx, ignore, trx);
  }
  
  public static MMFGWorkOrderValueDetail[] getofWOTxn(Properties ctx, MMFGWorkOrderTransaction WoTxn, int C_AcctSchema_ID, String txn) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<MMFGWorkOrderValueDetail> vals = new ArrayList<MMFGWorkOrderValueDetail>();
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderValueDetail WHERE MFG_WorkOrderTransaction_ID = ? ");
    sqlstmt.append(" AND C_AcctSchema_ID = ? ");
    sqlstmt.append(" AND AD_Client_ID = ? ");
    sqlstmt.append(" AND AD_Org_ID = ? ");
    String sql = sqlstmt.toString();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, txn);
      cPreparedStatement.setInt(1, WoTxn.getMFG_WORKORDERTRANSACTION_ID());
      cPreparedStatement.setInt(2, C_AcctSchema_ID);
      cPreparedStatement.setInt(3, WoTxn.getAD_Client_ID());
      cPreparedStatement.setInt(4, WoTxn.getAD_Org_ID());
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
        vals.add(new MMFGWorkOrderValueDetail(ctx, rs, txn)); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
    MMFGWorkOrderValueDetail[] retVal = new MMFGWorkOrderValueDetail[vals.size()];
    vals.toArray(retVal);
    return retVal;
  }
  
  public static MMFGWorkOrderValueDetail[] getofWO(Properties ctx, MMFGWorkOrderValue Wo, int C_AcctSchema_ID, String txn) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<MMFGWorkOrderValueDetail> vals = new ArrayList<MMFGWorkOrderValueDetail>();
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderValueDetail WHERE MFG_WorkOrderValue_ID = ? ");
    sqlstmt.append(" AND C_AcctSchema_ID = ? ");
    sqlstmt.append(" AND AD_Client_ID = ? ");
    sqlstmt.append(" AND AD_Org_ID = ? ");
    String sql = sqlstmt.toString();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, txn);
      cPreparedStatement.setInt(1, Wo.getMFG_WorkOrderValue_ID());
      cPreparedStatement.setInt(2, C_AcctSchema_ID);
      cPreparedStatement.setInt(3, Wo.getAD_Client_ID());
      cPreparedStatement.setInt(4, Wo.getAD_Org_ID());
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
        vals.add(new MMFGWorkOrderValueDetail(ctx, rs, txn)); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
    
    MMFGWorkOrderValueDetail[] retVal = new MMFGWorkOrderValueDetail[vals.size()];
    vals.toArray(retVal);
    return retVal;
  }
  
  public static MMFGWorkOrderValueDetail[] getofWorkOrderValue(Properties ctx, int MFG_WorkOrderValue_ID, String trx) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<MMFGWorkOrderValueDetail> vals = new ArrayList<MMFGWorkOrderValueDetail>();
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderValueDetail WHERE MFG_WorkOrderValue_ID = ? ");
    String sql = sqlstmt.toString();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, MFG_WorkOrderValue_ID);
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
        vals.add(new MMFGWorkOrderValueDetail(ctx, rs, trx)); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
    MMFGWorkOrderValueDetail[] retVal = new MMFGWorkOrderValueDetail[vals.size()];
    vals.toArray(retVal);
    return retVal;
  }
}
