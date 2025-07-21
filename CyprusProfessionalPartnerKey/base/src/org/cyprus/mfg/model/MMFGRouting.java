package org.cyprus.mfg.model;



//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MMFGRouting extends X_MFG_Routing {
  private static final CLogger log = CLogger.getCLogger(MMFGRouting.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMFGRouting.class);
  
//  public MMFGRouting(Ctx ctx, int M_Routing_ID, Trx trx) {
//    super(ctx, M_Routing_ID, trx);
//  }
//  
//  public MMFGRouting(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  
  public MMFGRouting(Properties ctx, int M_Routing_ID, String trxName) {
	    super(ctx, M_Routing_ID, trxName);
	  }
	  
	  public MMFGRouting(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  public MMFGRoutingOperation[] getOperations(String whereClause, String orderClause) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<MMFGRoutingOperation> list = new ArrayList<MMFGRoutingOperation>();
    StringBuffer sql = new StringBuffer("SELECT * FROM MFG_RoutingOperation WHERE MFG_Routing_ID=? ");
    if (whereClause != null)
      sql.append(whereClause); 
    if (orderClause != null)
      sql.append(" ").append(orderClause); 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
//      cPreparedStatement = DB.prepareStatement(sql.toString(), get_Trx());
    	cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
      cPreparedStatement.setInt(1, getMFG_Routing_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MMFGRoutingOperation op = new MMFGRoutingOperation(getCtx(), rs, get_TrxName());
        list.add(op);
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      log.log(Level.SEVERE, sql.toString(), e);
    } finally {
      try {
        if (cPreparedStatement != null)
        {
          cPreparedStatement.close();
          cPreparedStatement = null;
        }
        if(rs != null)
        {
        	rs.close();
        	rs = null;
        }
      } catch (Exception e) {}
      cPreparedStatement = null;
      rs = null;
    } 
    MMFGRoutingOperation[] operations = new MMFGRoutingOperation[list.size()];
    list.toArray(operations);
    return operations;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CMFG");
//    if (se == null || !se.checkLicense())
//      return false; 
    boolean success = true;
    if (isDefault()) {
      String sql = "SELECT MFG_Routing_ID from MFG_Routing WHERE M_Product_ID = ? and M_Warehouse_ID = ? AND IsDefault='Y' AND MFG_Routing_ID<>?";
      CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, null);
      ResultSet rs = null;
      try {
        cPreparedStatement.setInt(1, getM_Product_ID());
        cPreparedStatement.setInt(2, getM_Warehouse_ID());
        cPreparedStatement.setInt(3, getMFG_Routing_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.getMsg(getCtx(), "OneDefaultRouting"));
          success = false;
        } 
        rs.close();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
        return false;
      } finally {
        try {
          if (cPreparedStatement != null)
          {
            cPreparedStatement.close(); 
          cPreparedStatement = null;
          }
          if (rs != null)
          {
            rs.close(); 
          rs = null;
          }
        } catch (SQLException e) {
          log.log(Level.SEVERE, sql, e);
        } 
      } 
      if (!success)
        return false; 
    } 
    if (isCostRollup()) {
      String sql = "SELECT MFG_Routing_ID from MFG_Routing WHERE M_Product_ID = ? and AD_Org_ID = ? AND IsCostRollup='Y' AND MFG_Routing_ID<>?";
      CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, null);
      ResultSet rs = null;
      try {
        cPreparedStatement.setInt(1, getM_Product_ID());
        cPreparedStatement.setInt(2, getAD_Org_ID());
        cPreparedStatement.setInt(3, getMFG_Routing_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.translate(getCtx(), "OneCostRollupRouting"));
          success = false;
        } 
        rs.close();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
        return false;
      } finally {
        try {
          if (cPreparedStatement != null)
          {
            cPreparedStatement.close(); 
          cPreparedStatement = null;
          }
          if (rs != null) {
            rs.close(); 
          rs = null;
          }
        } catch (SQLException e) {
          log.log(Level.SEVERE, sql, e);
        } 
      } 
      if (!success)
        return false; 
    } 
    return true;
  }
  
  public static MMFGRouting getDefaultRouting(Properties ctx, int M_Product_ID, int M_Warehouse_ID) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT * FROM MFG_Routing WHERE M_Product_ID=? AND M_Warehouse_ID=? AND IsDefault='Y' AND IsActive = 'Y'";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    MMFGRouting routing = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, M_Product_ID);
      cPreparedStatement.setInt(2, M_Warehouse_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        routing = new MMFGRouting(ctx, rs, null); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
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
    return routing;
  }
  
  public static MMFGRouting getCostRollupRouting(MProduct product) {
    CPreparedStatement cPreparedStatement = null;
    MMFGRouting rt = null;
    String sql = "SELECT * FROM MFG_Routing WHERE M_Product_ID=?  AND AD_Client_ID = ?  AND IsCostRollup = ? ";
    PreparedStatement pstmt = null;
    ResultSet rs= null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), product.get_TrxName());
      cPreparedStatement.setInt(1, product.getM_Product_ID());
      cPreparedStatement.setInt(2, product.getAD_Client_ID());
      cPreparedStatement.setString(3, "Y");
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        rt = new MMFGRouting(product.getCtx(), rs, null); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql.toString(), e);
    } finally {
      try {
        if (cPreparedStatement != null)
        {
          cPreparedStatement.close(); 
          cPreparedStatement = null;
        }
        if(rs != null)
        {
        	rs.close();
        	rs = null;
        }
      } catch (Exception e) {}
      cPreparedStatement = null;
      rs = null;
    } 
    return rt;
  }
  
  public static MMFGRouting getRouting(Properties ctx, int productID, int warehouseID) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT * FROM MFG_Routing WHERE M_Product_ID=?  AND M_Warehouse_ID=? AND IsActive = 'Y'";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    MMFGRouting routing = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, productID);
      cPreparedStatement.setInt(2, warehouseID);
       rs = cPreparedStatement.executeQuery();
      if (rs.next())
        routing = new MMFGRouting(ctx, rs, null); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null)
        {
          cPreparedStatement.close(); 
          cPreparedStatement = null;
        }
        if(rs != null)
        {
        	rs.close();
        	rs = null;
        }
      } catch (Exception e) {
        cPreparedStatement = null;
      } 
    } 
    return routing;
  }
}

