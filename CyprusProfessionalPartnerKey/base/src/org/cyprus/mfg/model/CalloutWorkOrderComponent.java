package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;

public class CalloutWorkOrderComponent extends CalloutEngine {
  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Product_ID = (Integer)value;
    if (M_Product_ID == null || M_Product_ID.intValue() == 0) {
      mTab.setValue("C_UOM_ID", null);
      return "";
    } 
    MProduct product = new MProduct(Env.getCtx(), M_Product_ID.intValue(), null);
    mTab.setValue("C_UOM_ID", Integer.valueOf(product.getC_UOM_ID()));
    return "";
  }
  
  protected CLogger log = CLogger.getCLogger(getClass());
  
  public String setQtyRequired(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
   // int C_UOM_ID = ctx.getContextAsInt(WindowNo, "C_UOM_ID");
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal QtyRequired = (BigDecimal)value;
    BigDecimal QtyRequired1 = QtyRequired.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (QtyRequired.compareTo(QtyRequired1) != 0) {
      this.log.fine("Corrected QtyRequired Scale UOM=" + C_UOM_ID + "; QtyRequired=" + QtyRequired + "->" + QtyRequired1);
      QtyRequired = QtyRequired1;
      mTab.setValue("QtyRequired", QtyRequired);
    } 
    return "";
  }
}

