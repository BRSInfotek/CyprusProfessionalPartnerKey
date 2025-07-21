package org.cyprus.mrp.model;

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

public class CalloutMasterDemandLine extends CalloutEngine {
  private final CLogger log = CLogger.getCLogger(getClass());
  
  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Product_ID = (Integer)value;
    if (M_Product_ID == null || M_Product_ID.intValue() == 0) {
      mTab.setValue("C_UOM_ID", null);
      return "";
    } 
    MProduct product = new MProduct(Env.getCtx(), M_Product_ID.intValue(), null);
    mTab.setValue("C_UOM_ID", Integer.valueOf(product.getC_UOM_ID()));
    BigDecimal QtyEntered = (BigDecimal)mTab.getValue("Qty");
    if (QtyEntered == null)
      return ""; 
    BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(ctx, product.getC_UOM_ID()), 4);
    if (QtyEntered.compareTo(QtyEntered1) != 0) {
      this.log.fine("Corrected QtyEntered Scale UOM=" + product.getC_UOM_ID() + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
      QtyEntered = QtyEntered1;
      mTab.setValue("Qty", QtyEntered);
    } 
    return "";
  }
  
  public String setQty(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
//    int C_UOM_ID = ctx.getContextAsInt(WindowNo, "C_UOM_ID");
    int C_UOM_ID = Env.getContextAsInt(ctx,WindowNo, "C_UOM_ID");

    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal QtyEntered = (BigDecimal)value;
    BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (QtyEntered.compareTo(QtyEntered1) != 0) {
      this.log.fine("Corrected QtyEntered Scale UOM=" + C_UOM_ID + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
      QtyEntered = QtyEntered1;
      mTab.setValue("Qty", QtyEntered);
    } 
    return "";
  }
}

