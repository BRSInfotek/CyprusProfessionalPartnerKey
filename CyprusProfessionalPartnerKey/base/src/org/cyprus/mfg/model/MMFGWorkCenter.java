package org.cyprus.mfg.model;



//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MMFGWorkCenter extends X_MFG_WorkCenter {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkCenter.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMFGWorkCenter.class);
  
  
  public MMFGWorkCenter(Properties ctx, int MFG_WorkCenter_ID,String trxName) {
	    super(ctx, MFG_WorkCenter_ID, trxName);
	  }
  
  public MMFGWorkCenter(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  public static MMFGWorkCenter getDefaultWorkCenter(Properties ctx, int M_Warehouse_ID) {
    CPreparedStatement cPreparedStatement = null;
    MMFGWorkCenter retValue = null;
    ArrayList<Integer> defaultWorkCenters = new ArrayList<Integer>();
    ArrayList<Integer> WorkCenters = new ArrayList<Integer>();
    String sql = "SELECT MFG_WorkCenter_ID, IsDefault FROM MFG_WorkCenter WHERE M_Warehouse_ID = ? AND IsActive = 'Y'";
    PreparedStatement pstmt = null;
    ResultSet rs= null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, M_Warehouse_ID);
       rs = cPreparedStatement.executeQuery();
      while (rs != null && rs.next()) {
        if ("Y".equals(rs.getString(2))) {
          defaultWorkCenters.add(Integer.valueOf(rs.getInt(1)));
          break;
        } 
        WorkCenters.add(Integer.valueOf(rs.getInt(1)));
      } 
      if (defaultWorkCenters.size() > 0) {
        retValue = new MMFGWorkCenter(ctx, ((Integer)defaultWorkCenters.get(0)).intValue(), null);

      } else if (WorkCenters.size() > 0) {
          retValue = new MMFGWorkCenter(ctx, ((Integer)defaultWorkCenters.get(0)).intValue(), null);

      } 
      rs.close();
      cPreparedStatement.close();
      return retValue;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        if(rs != null)
        	rs.close();
      } catch (Exception e) {}
      cPreparedStatement = null;
      rs = null;
    } 
    return retValue;
  }
  
  protected boolean beforeSave(boolean newRecord) {

    boolean retvalue = true;
    if (isDefault()) {
      CPreparedStatement cPreparedStatement = null;
      ResultSet rs = null;
      String sql = "SELECT MFG_WorkCenter_ID from MFG_WorkCenter WHERE M_Warehouse_ID = ? AND IsDefault='Y' AND MFG_WorkCenter_ID <> ? ";
      try {
        cPreparedStatement = DB.prepareStatement(sql, null);
        cPreparedStatement.setInt(1, getM_Warehouse_ID());
        cPreparedStatement.setInt(2, getMFG_WorkCenter_ID());
         rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.getMsg(getCtx(), "OneDefaultWorkCenter"));
          retvalue = false;
        } 
        rs.close();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
      } finally {
        try {
          if (cPreparedStatement != null)
            cPreparedStatement.close(); 
          if(rs != null)
        	  rs.close();
        } catch (Exception e) {}
        cPreparedStatement = null;
        rs = null;
      } 
    } 
    if (is_ValueChanged("M_Locator_ID") && 
      !MWarehouse.IsLocatorInWarehouse(getM_Warehouse_ID(), getM_Locator_ID())) {
      log.saveError("Error", Msg.translate(getCtx(), "WarehouseLocatorMismatch"));
      return false;
    } 
   
    // Not Useful Commented by Mukesh @20220719  
//    if (is_ValueChanged("M_Locator_ID")) {
//    	      log.saveError("Error", Msg.translate(getCtx(), "WarehouseLocatorMismatch"));
//    	      return false;
//    	    } 
    return retvalue;
  }
  
 
  /**
   * Not required now... It will be usefull when we implemented default accounts in Accounting Schema window.
   * @author Mukesh @20220719
   * @param oldM_Warehouse_ID
   * @param newM_Warehouse_ID
   * @param windowNo
   * @throws Exception
   */
  
  protected boolean afterSave(boolean newRecord, boolean success) {
    if (newRecord & success)
      success = insert_Accounting("MFG_WorkCenter_Acct", "C_AcctSchema_Default", null); 
    return success;
  }
  
  //@UICallout
  public void setM_Warehouse_ID(String oldM_Warehouse_ID, String newM_Warehouse_ID, int windowNo) throws Exception {
    if (newM_Warehouse_ID == null || 0 == newM_Warehouse_ID.trim().length()) {
      set_ValueNoCheck("M_Locator_ID", null);
      return;
    } 
    int M_Warehouse_ID = Integer.parseInt(newM_Warehouse_ID);
    if (0 == M_Warehouse_ID) {
      set_ValueNoCheck("M_Locator_ID", null);
    } else {
//      setM_Locator_ID(MWarehouse.get(getCtx(), getM_Warehouse_ID()).getDefaultM_Locator_ID());
    	
    } 
  }
}

