package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.Env;
//import org.cyprusbrs.util.Ctx;

//@Deprecated
public class CalloutWorkOrderResourceTxnLine extends CalloutEngine {
  protected CLogger log = CLogger.getCLogger(getClass());
  
  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Product_ID = (Integer)value;
    if (M_Product_ID == null || 0 == M_Product_ID.intValue()) {
      mTab.setValue("C_UOM_ID", null);
      return "";
    } 
    MProduct product = new MProduct(ctx, M_Product_ID.intValue(), null);
    mTab.setValue("C_UOM_ID", Integer.valueOf(product.getC_UOM_ID()));
    return "";
  }
  
  public String setQtyEntered(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
   // int C_UOM_ID = ctx.getContextAsInt(WindowNo, "C_UOM_ID");
    int C_UOM_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");

    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal QtyEntered = (BigDecimal)value;
    BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (QtyEntered.compareTo(QtyEntered1) != 0) {
      this.log.fine("Corrected QtyEntered Scale UOM=" + C_UOM_ID + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
      QtyEntered = QtyEntered1;
      mTab.setValue("QtyEntered", QtyEntered);
    } 
    return "";
  }
}

