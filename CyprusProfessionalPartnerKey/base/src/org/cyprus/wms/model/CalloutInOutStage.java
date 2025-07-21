package org.cyprus.wms.model;

import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;

public class CalloutInOutStage extends CalloutEngine {
  private static CLogger log = CLogger.getCLogger(CalloutInOutStage.class);
  
  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (isCalloutActive())
      return ""; 
    Integer C_Order_ID = Integer.valueOf(0);
    Integer M_Product_ID = Integer.valueOf(0);
    setCalloutActive(true);
    if (mField.getColumnName().equals("M_Product_ID")) {
      M_Product_ID = (Integer)value;
      if (M_Product_ID == null || M_Product_ID.intValue() == 0) {
        setCalloutActive(false);
        return "";
      } 
      mTab.setValue("M_ProductOrder_ID", M_Product_ID);
      C_Order_ID = (Integer)mTab.getValue("C_Order_ID");
    } else if (mField.getColumnName().equals("C_Order_ID")) {
      C_Order_ID = (Integer)value;
      if (C_Order_ID == null || C_Order_ID.intValue() == 0) {
        setCalloutActive(false);
        return "";
      } 
      mTab.setValue("C_ProductOrder_ID", C_Order_ID);
      M_Product_ID = (Integer)mTab.getValue("M_Product_ID");
    } 
    if (mField.getColumnName().equals("M_OrderProduct_ID")) {
      M_Product_ID = (Integer)value;
      if (M_Product_ID == null || M_Product_ID.intValue() == 0) {
        setCalloutActive(false);
        return "";
      } 
      mTab.setValue("M_Product_ID", M_Product_ID);
      C_Order_ID = (Integer)mTab.getValue("C_Order_ID");
    } else if (mField.getColumnName().equals("C_ProductOrder_ID")) {
      C_Order_ID = (Integer)value;
      if (C_Order_ID == null || C_Order_ID.intValue() == 0) {
        setCalloutActive(false);
        return "";
      } 
      mTab.setValue("C_Order_ID", C_Order_ID);
      M_Product_ID = (Integer)mTab.getValue("M_Product_ID");
    } 
    if (M_Product_ID == null || M_Product_ID.intValue() == 0 || C_Order_ID == null || C_Order_ID.intValue() == 0) {
      setCalloutActive(false);
      return "";
    } 
    MOrder order = new MOrder(ctx, C_Order_ID.intValue(), null);
    MOrderLine[] lines = order.getLines(M_Product_ID.intValue(), "QtyOrdered > QtyDelivered", "QtyOrdered - QtyDelivered, Line");
    if (lines == null || lines.length == 0) {
      log.fine("Product not on order");
      setCalloutActive(false);
      return "ProductNotOnOrder";
    } 
    mTab.setValue("C_UOM_ID", Integer.valueOf(lines[0].getC_UOM_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String warehouse(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Warehouse_ID = (Integer)value;
    if (M_Warehouse_ID == null || M_Warehouse_ID.intValue() == 0)
      return ""; 
    MWarehouse wh = MWarehouse.get(ctx, M_Warehouse_ID.intValue());
    mTab.setValue("M_Locator_ID", Integer.valueOf(wh.getM_RcvLocator_ID()));
    return "";
  }
  
  public String docType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (isCalloutActive())
      return ""; 
    Integer C_DocType_ID = (Integer)value;
    if (C_DocType_ID == null || C_DocType_ID.intValue() == 0)
      return ""; 
    setCalloutActive(true);
    MDocType docType = MDocType.get(ctx, C_DocType_ID.intValue());
    mTab.setValue("IsSOTrx", Boolean.valueOf(docType.isSOTrx()));
    mTab.setValue("IsReturnTrx", Boolean.valueOf(docType.isReturnTrx()));
    setCalloutActive(false);
    return "";
  }
}

