package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMFGWorkCenterCostDetail extends X_MFG_WorkCenterCostDetail {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkCenterCostDetail.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static final CLogger s_log = CLogger.getCLogger(MMFGWorkCenterCostDetail.class);
  
  public MMFGWorkCenterCostDetail(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MMFGWorkCenterCostDetail(Properties ctx, int ignore, String trx) {
    super(ctx, ignore, trx);
  }
  
  public static boolean createWorkCenterCostDetail(MMFGWorkCenterCost cost, int MFG_WorkOrderTransaction_ID, BigDecimal Amt, BigDecimal Qty) {
    boolean retVal = false;
    MMFGWorkCenterCostDetail cd = getWorkCenterCostDetail(MFG_WorkOrderTransaction_ID, cost);
    if (cd == null) {
      cd = new MMFGWorkCenterCostDetail(cost.getCtx(), 0, cost.get_TrxName());
      cd.setAD_Client_ID(cost.getAD_Client_ID());
      cd.setAD_Org_ID(cost.getAD_Org_ID());
      cd.setC_AcctSchema_ID(cost.getC_AcctSchema_ID());
      cd.setAmt(Amt);
      cd.setDescription(cost.getDescription());
      cd.setM_CostElement_ID(cost.getM_CostElement_ID());
      cd.setMFG_WorkCenter_ID(cost.getMFG_WorkCenter_ID());
      cd.setMFG_WorkOrderTransaction_ID(MFG_WorkOrderTransaction_ID);
      cd.setQty(Qty);
      cd.setProcessed(true);
      cd.setIsActive(true);
    } else {
      cd.setAmt(Amt);
      cd.setQty(Qty);
    } 
    retVal = cd.save();
    return retVal;
  }
  
  private static MMFGWorkCenterCostDetail getWorkCenterCostDetail(int MFG_WorkOrderTransaction_ID, MMFGWorkCenterCost cost) {
    CPreparedStatement cPreparedStatement = null;
    String sql = " SELECT * FROM MFG_WorkCenterCostDetail  WHERE AD_Org_ID = ?  AND AD_Client_ID = ?  AND C_AcctSchema_ID = ?  AND MFG_WorkCenter_ID = ?  AND MFG_CostElement_ID = ?  AND MFG_WorkOrderTransaction_ID = ? ";
    MMFGWorkCenterCostDetail retValue = null;
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, cost.getAD_Org_ID());
      cPreparedStatement.setInt(2, cost.getAD_Client_ID());
      cPreparedStatement.setInt(3, cost.getC_AcctSchema_ID());
      cPreparedStatement.setInt(4, cost.getMFG_WorkCenter_ID());
      cPreparedStatement.setInt(5, cost.getM_CostElement_ID());
      cPreparedStatement.setInt(6, MFG_WorkOrderTransaction_ID);
      ResultSet rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = new MMFGWorkCenterCostDetail(cost.getCtx(), rs, cost.get_TrxName()); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql + e);
    } 
    try {
      if (cPreparedStatement != null)
        cPreparedStatement.close(); 
      cPreparedStatement = null;
    } catch (Exception e) {
      cPreparedStatement = null;
    } 
    return retValue;
  }
}

