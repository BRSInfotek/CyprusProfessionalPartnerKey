package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.MBPGroup;
import org.cyprusbrs.framework.MProductCategory;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Trx;

public class MWMSMMStrategySetLine extends X_WMS_MMStrategySetLine {
  private static final CLogger log = CLogger.getCLogger(MWMSMMStrategySetLine.class);
  
  private static final long serialVersionUID = 1L;
  
  private MWMSMMStrategy m_strategy;
  
  Trx trxname;
  
  public MWMSMMStrategySetLine(Properties ctx, int M_MMStrategySetLine_ID, String trx) {
    super(ctx, M_MMStrategySetLine_ID, trx);
    this.m_strategy = null;
  }
  
  public MWMSMMStrategySetLine(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.m_strategy = null;
  }
  
  public boolean evaluateCriteria(int p_C_BPartner_ID, int p_M_Locator_ID, int p_M_Product_ID, int p_M_Zone_ID, int p_C_DocType_ID) {
    int C_BPartner_ID = getC_BPartner_ID();
    if (C_BPartner_ID != 0 && C_BPartner_ID != p_C_BPartner_ID)
      return false; 
    int C_BP_Group_ID = getC_BP_Group_ID();
    if (C_BP_Group_ID != 0 && C_BP_Group_ID != MBPGroup.getOfBPartner(getCtx(), p_C_BPartner_ID).getC_BP_Group_ID())
      return false; 
    int M_Locator_ID = getM_Locator_ID();
    if (M_Locator_ID != 0 && M_Locator_ID != p_M_Locator_ID)
      return false; 
    int M_Product_ID = getM_Product_ID();
    if (M_Product_ID != 0 && M_Product_ID != p_M_Product_ID)
      return false; 
    int M_Product_Category_ID = getM_Product_Category_ID();
    if (M_Product_Category_ID != 0 && M_Product_Category_ID != MProductCategory.getOfProduct(getCtx(), p_M_Product_ID).getM_Product_Category_ID())
      return false; 
    int M_Zone_ID = getWMS_Zone_ID();
    if (M_Zone_ID != 0 && M_Zone_ID != p_M_Zone_ID)
      return false; 
    int C_DocTypeGroup_ID = getWMS_DocTypeGroup_ID();
    if (C_DocTypeGroup_ID != 0 && p_C_DocType_ID != 0)
      if (!MWMSDocTypeGroup.includesDocType(C_DocTypeGroup_ID, p_C_DocType_ID, trxname))
        return false;  
    return true;
  }
  
  public MWMSMMStrategy getStrategy() {
    if (this.m_strategy == null)
      this.m_strategy = new MWMSMMStrategy(getCtx(), getWMS_MMStrategy_ID(), get_TrxName()); 
    return this.m_strategy;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
}
