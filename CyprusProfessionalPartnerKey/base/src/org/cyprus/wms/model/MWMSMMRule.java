package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.intf.WMSRuleIntf;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSMMRule extends X_WMS_MMRule {
  private static final CLogger log = CLogger.getCLogger(MWMSMMRule.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSMMRule(Properties ctx, int M_MMRule_ID, String trx) {
    super(ctx, M_MMRule_ID, trx);
  }
  
  public MWMSMMRule(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  private static final CLogger s_log = CLogger.getCLogger(MWMSMMRule.class);
  
  public static MLocator[] getLocators(Properties ctx, PreparedStatement pstmt, String trx) throws Exception {
    ArrayList<MLocator> list = new ArrayList<MLocator>();
    ResultSet rs = null;
    try {
      rs = pstmt.executeQuery();
      while (rs.next()) {
        MLocator locator = new MLocator(ctx, rs, trx);
        list.add(locator);
      } 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, "", e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close(pstmt);
    } 
    MLocator[] retValue = new MLocator[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  public String getRuleClass() {
    String className = "";
    if (getRule().equals(X_WMS_MMRule.RULE_FindAFixedLocator)) {
      className = "org.cyprusbrs.cwms.rule.PutawayFixed";
    } else if (getRule().equals(X_WMS_MMRule.RULE_FindAFloatingLocatorWithTheSameProduct)) {
      className = "org.cyprusbrs.cwms.rule.PutawayFloatingSameProduct";
    } else if (getRule().equals(X_WMS_MMRule.RULE_FindAnyEmptyFloatingLocator)) {
      className = "org.cyprusbrs.cwms.rule.PutawayFloatingEmpty";
    } else if (getRule().equals(X_WMS_MMRule.RULE_FindAnyLocatorWithAvailableCapacity)) {
      className = "org.cyprusbrs.cwms.rule.PutawayFloatingAvailable";
    } else if (getRule().equals(X_WMS_MMRule.RULE_FindItemInTheWarehouse)) {
      className = "org.cyprusbrs.cwms.rule.PickAvailable";
    } else if (getRule().equals(X_WMS_MMRule.RULE_FindItemInTheWarehousePickToClean)) {
      className = "org.cyprusbrs.cwms.rule.PickToClean";
    } else if (getRule().equals(X_WMS_MMRule.RULE_HardAllocatePurchasedToOrderProducts)) {
      className = "org.cyprusbrs.cwms.rule.PickPTO";
    } else if (getRule().equals(X_WMS_MMRule.RULE_UseCustomClass)) {
      className = getRuleClass();
    } 
    return className;
  }
  
  private WMSRuleIntf getWMSRule() throws Exception {
    String className = getRuleClass();
    if (className == null || className.length() == 0) {
      log.log(Level.SEVERE, Msg.translate(getCtx(), "InvalidRuleClass"));
      throw new Exception(Msg.translate(getCtx(), "InvalidRuleClass"));
    } 
    try {
      Class<?> c = Class.forName(className);
      if (c != null) {
        WMSRuleIntf rule = (WMSRuleIntf)c.newInstance();
        return rule;
      } 
      log.log(Level.SEVERE, Msg.translate(getCtx(), "InvalidRuleClass"));
      throw new Exception(Msg.translate(getCtx(), "InvalidRuleClass"));
    } catch (Exception e) {
      log.log(Level.SEVERE, Msg.translate(getCtx(), "InvalidRuleClass"));
      throw new Exception(Msg.translate(getCtx(), "InvalidRuleClass"));
    } 
  }
  
  public MLocator[] getValidLocators(int M_Product_ID, int C_OrderLine_ID) throws Exception {
    WMSRuleIntf w = getWMSRule();
    if (w != null)
      return w.getValidLocators(getCtx(), getM_Warehouse_ID(), getWMS_Zone_ID(), M_Product_ID, C_OrderLine_ID, get_TrxName()); 
    return null;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
}

