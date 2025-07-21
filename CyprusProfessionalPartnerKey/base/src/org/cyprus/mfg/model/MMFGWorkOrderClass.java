package org.cyprus.mfg.model;



//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MMFGWorkOrderClass extends X_MFG_WorkOrderClass {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderClass.class);
  
  private static final long serialVersionUID = 1L;
  
  public MMFGWorkOrderClass(Properties ctx, int M_WorkOrderClass_ID, String trxName) {
	    super(ctx, M_WorkOrderClass_ID, trxName);
	  }
	  
	  public MMFGWorkOrderClass(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  protected boolean beforeSave(boolean newRecord) { 
    if (isDefault()) {
      String str = "SELECT MFG_WorkOrderClass_ID from MFG_WorkOrderClass WHERE IsDefault='Y' AND MFG_WorkOrderClass_ID<>? AND WOType=? AND WOType IS NOT NULL AND IsActive='Y' ";
      if (getAD_Org_ID() != 0)
        str = str + "AND AD_Org_ID = ? "; 
      CPreparedStatement cPreparedStatement1 = DB.prepareStatement(str, null);
      ResultSet rs = null;
      try {
        cPreparedStatement1.setInt(1, getMFG_WorkOrderClass_ID());
        cPreparedStatement1.setString(2, getWOType());
        if (getAD_Org_ID() != 0)
          cPreparedStatement1.setInt(3, getAD_Org_ID()); 
         rs = cPreparedStatement1.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.getMsg(getCtx(), "OneDefaultWorkOrderClass"));
          return false;
        } 
        rs.close();
        cPreparedStatement1.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, str, e);
      } finally {
        try {
          if (cPreparedStatement1 != null)
          {
            cPreparedStatement1.close(); 
          cPreparedStatement1 = null;
          }
          if(rs != null) {
        	  rs.close();
        	  rs = null;
          }
        } catch (Exception e) {
          cPreparedStatement1 = null;
          rs = null;
        } 
      } 
    } 
    String sql = "SELECT MFG_WorkOrderClass_ID from MFG_WorkOrderClass WHERE Name = ? AND AD_Client_ID = ? AND MFG_WorkOrderClass_ID <> ?";
    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, null);
    ResultSet rs = null;
    try {
      cPreparedStatement.setString(1, getName());
      cPreparedStatement.setInt(2, getAD_Client_ID());
      cPreparedStatement.setInt(3, getMFG_WorkOrderClass_ID());
       rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        log.saveError("Error", Msg.getMsg(getCtx(), "WorkOrderClassExists"));
        return false;
      } 
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null) {
          cPreparedStatement.close(); 
        cPreparedStatement = null;
      }
        if(rs != null) {
        	rs.close();
        	rs = null;
        }
      } catch (Exception e) {
        cPreparedStatement = null;
        rs = null;
      } 
    } 
    return true;
  }
  
  //@UICallout
  public void setIsDefault(String oldIsDefault, String newIsDefault, int windowNo) throws Exception {
    if (newIsDefault == null || newIsDefault.length() == 0)
      return; 
    if (newIsDefault.equals("Y")) {
      String updateSql = "UPDATE MFG_WorkOrderClass SET IsDefault = 'N' WHERE WOType = ? AND AD_Org_ID = ? AND AD_Client_ID = ? AND MFG_WorkOrderClass_ID != ?";
      CPreparedStatement cPreparedStatement = null;
      try {
//        CPreparedStatement cPreparedStatement = DB.prepareStatement(updateSql, get_Trx());
    	   cPreparedStatement = DB.prepareStatement(updateSql, get_TrxName());
        cPreparedStatement.setString(1, getWOType());
        cPreparedStatement.setInt(2, getAD_Org_ID());
        cPreparedStatement.setInt(3, getAD_Client_ID());
        cPreparedStatement.setInt(4, getMFG_WorkOrderClass_ID());
        cPreparedStatement.executeUpdate();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, updateSql, e);        
        return;
      } 
      finally
      {
    	  cPreparedStatement.close();
          cPreparedStatement = null;
      }
    } 
    setIsDefault(newIsDefault.equals("Y"));
  }
  
  public static MMFGWorkOrderClass getWorkOrderClass(Properties ctx, String trx, String whereClause) {
    CPreparedStatement cPreparedStatement = null;
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderClass WHERE IsActive='Y'");
    if (whereClause != null)
      sqlstmt.append("AND ").append(whereClause); 
    String sql = sqlstmt.toString();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    MMFGWorkOrderClass woc = null;
    try {
//      cPreparedStatement = DB.prepareStatement(sql, trx);
    	cPreparedStatement = DB.prepareStatement(sql, null);
       rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        log.fine("WorkOrder Class exists");
//        woc = new MMFGWorkOrderClass(ctx, rs, trx);
        woc = new MMFGWorkOrderClass(ctx, rs, null);
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null) {
          cPreparedStatement.close(); 
        cPreparedStatement = null;
        }
        if(rs != null) {
        	rs.close();
        	rs = null;
        }
      } catch (Exception e) {
        cPreparedStatement = null;
        rs = null;
      } 
    } 
    if (woc != null)
      return woc; 
    return null;
  }
  
  /**
   * Commented below after save method due to OM is not able to save Work Order class @author Mukesh 20220715
   */
  protected boolean afterSave(boolean newRecord, boolean success) {
    if (newRecord & success)
      success = insert_Accounting("MFG_WorkOrderClass_Acct", "C_AcctSchema_Default", null); 
    return success;
  }
  
}

