package org.cyprus.mfg.model;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MMFGWorkCenterResource extends X_MFG_WorkCenterResource {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkCenterResource.class);
  
  private static final long serialVersionUID = 1L;
  
//  public MMFGWorkCenterResource(Properties ctx, int MFG_WorkCenterResource_ID, Trx trx) {
//    super(ctx, MFG_WorkCenterResource_ID, trx);
//  }
//  
//  public MMFGWorkCenterResource(Properties ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGWorkCenterResource(Properties ctx, int MFG_WorkCenterResource_ID, String trxName) {
	    super(ctx, MFG_WorkCenterResource_ID, trxName);
	  }
	  
	  public MMFGWorkCenterResource(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  protected boolean beforeSave(boolean newRecord) {
    boolean retvalue = true;
    if (newRecord) {
      String sql = "SELECT 1 FROM MFG_WorkCenterResource WHERE MFG_WorkCenter_ID = ? AND M_Product_ID = ? ";
      CPreparedStatement cPreparedStatement = null;
      ResultSet rs = null;
      try {
    	  cPreparedStatement = DB.prepareStatement(sql, null);
        cPreparedStatement.setInt(1, getMFG_WorkCenter_ID());
        cPreparedStatement.setInt(2, getM_Product_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.getMsg(getCtx(), "WorkCenterResourceExists"));
          retvalue = false;
        } 
        rs.close();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
      } finally {
//        DB.closeResultSet(rs);
//        DB.closeStatement((Statement)cPreparedStatement);
    	  DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null;
          cPreparedStatement = null;
      } 
    } 
    return retvalue;
  }
  
  public static MMFGWorkCenterResource[] getResources(MMFGWorkCenter wc, String whereClause, String orderClause) {
    ArrayList<MMFGWorkCenterResource> list = new ArrayList<MMFGWorkCenterResource>();
    StringBuffer sql = new StringBuffer("SELECT * FROM MFG_WorkCenterResource WHERE MFG_WorkCenter_ID=? ");
    if (whereClause != null)
      sql.append(whereClause); 
    if (orderClause != null)
      sql.append(" ").append(orderClause); 
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql.toString(), wc.get_Trx());
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql.toString(), wc.get_TrxName());
      cPreparedStatement.setInt(1, wc.getMFG_WorkCenter_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
//        MWorkCenterResource op = new MWorkCenterResource(wc.getCtx(), rs, wc.get_Trx());
          MMFGWorkCenterResource op = new MMFGWorkCenterResource(wc.getCtx(), rs, wc.get_TrxName());

    	  list.add(op);
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      log.log(Level.SEVERE, sql.toString(), e);
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	 DB.close(rs);
         DB.close((Statement)cPreparedStatement);
         rs = null;
         cPreparedStatement = null;
    } 
    MMFGWorkCenterResource[] resources = new MMFGWorkCenterResource[list.size()];
    list.toArray(resources);
    return resources;
  }
}

