package org.cyprus.mfg.model;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.X_C_AcctSchema;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MAcctSchemaDefault;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMFGWorkCenterCost extends X_MFG_WorkCenterCost {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkCenterCost.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static final CLogger s_log = CLogger.getCLogger(MMFGRoutingOperationResource.class);
  
  public MMFGWorkCenterCost(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MMFGWorkCenterCost(Properties ctx, int ignored, String trx) {
    super(ctx, ignored, trx);
  }
  
  public static MMFGWorkCenterCost[] getWorkCenterCosts(MMFGWorkCenter wc, int M_CostType_ID, MAcctSchema as, int AD_Org_ID) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<X_MFG_WorkCenterCost> list = new ArrayList<X_MFG_WorkCenterCost>();
    StringBuffer sql = new StringBuffer("SELECT * FROM MFG_WorkCenterCost WHERE MFG_WorkCenter_ID=? ");
    sql.append(" AND IsActive ='Y'");
    sql.append(" AND M_CostType_ID = ? ");
    sql.append(" AND C_AcctSchema_ID = ? ");
    sql.append(" AND AD_Org_ID = ? ");
    sql.append(" AND AD_Client_ID = ? ");
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), wc.get_TrxName());
      cPreparedStatement.setInt(1, wc.getMFG_WorkCenter_ID());
      cPreparedStatement.setInt(2, M_CostType_ID);
      cPreparedStatement.setInt(3, as.getC_AcctSchema_ID());
      if (as.getCostingLevel().equals(X_C_AcctSchema.COSTINGLEVEL_Client)) {
        cPreparedStatement.setInt(4, 0);
      } else {
        cPreparedStatement.setInt(4, AD_Org_ID);
      } 
      cPreparedStatement.setInt(5, wc.getAD_Client_ID());
      ResultSet rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MMFGWorkCenterCost cost = new MMFGWorkCenterCost(wc.getCtx(), rs, wc.get_TrxName());
        list.add(cost);
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql.toString(), e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
      } catch (Exception e) {}
      cPreparedStatement = null;
    } 
    MMFGWorkCenterCost[] costs = new MMFGWorkCenterCost[list.size()];
    list.toArray(costs);
    return costs;
  }
  
  private MMFGWorkCenterCost getWorkCenterCosts(int M_CostType_ID, MAcctSchema as, int AD_Org_ID, int M_CostElement_ID) {
    MMFGWorkCenter wc = new MMFGWorkCenter(getCtx(), getMFG_WorkCenter_ID(), get_TrxName());
    MMFGWorkCenterCost[] costs = getWorkCenterCosts(wc, M_CostType_ID, as, AD_Org_ID);
    for (MMFGWorkCenterCost cost : costs) {
      if (cost.getM_CostElement_ID() == M_CostElement_ID)
        return cost; 
    } 
    return null;
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (newRecord) {
      MAcctSchema as = new MAcctSchema(getCtx(), getC_AcctSchema_ID(), null);
      String CostingLevel = as.getCostingLevel();
      if (X_C_AcctSchema.COSTINGLEVEL_Client.equals(CostingLevel))
        if (getAD_Org_ID() != 0) {
          log.saveError("CostingLevelClient", "");
          return false;
        }  
      MMFGWorkCenterCost duplicate = getWorkCenterCosts(getM_CostType_ID(), as, getAD_Org_ID(), getM_CostElement_ID());
      if (duplicate != null) {
        log.saveError("DuplicateWorkCenterCost", "");
        return false;
      } 
    } 
    if (getOverhead_Absorption_Acct() == 0) {
      MAcctSchemaDefault def = MAcctSchemaDefault.get(getCtx(), getC_AcctSchema_ID());
      setOverhead_Absorption_Acct(def.getOverhead_Absorption_Acct());
    } 
    return true;
  }
}

