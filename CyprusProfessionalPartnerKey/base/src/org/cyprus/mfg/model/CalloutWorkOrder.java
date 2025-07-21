package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.framework.X_M_BOM;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class CalloutWorkOrder extends CalloutEngine {
  private final CLogger log = CLogger.getCLogger(getClass());
  
  public String org(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer AD_Org_ID = (Integer)value;
    if (AD_Org_ID == null || AD_Org_ID.intValue() == 0) {
      mTab.setValue("M_Warehouse_ID", null);
      mTab.setValue("MFG_WorkOrderClass_ID", null);
      return "";
    } 
    if (AD_Org_ID.intValue() == 0) {
      mTab.setValue("AD_Org_ID", null);
      return "";
    } 
    mTab.setValue("M_Warehouse_ID", null);
    mTab.setValue("MFG_WorkOrderClass_ID", null);
    int clientID = ((Integer)mTab.getValue("AD_Client_ID")).intValue();
    String woType = (String)mTab.getValue("WOType");
    String sql = "SELECT MFG_WorkOrderClass_ID FROM MFG_WorkOrderClass WHERE AD_Org_ID IN (0, ?) AND AD_Client_ID = ? AND WOType = ? AND IsDefault = 'Y' AND IsActive = 'Y' ORDER BY AD_Org_ID DESC";
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
       cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, AD_Org_ID.intValue());
      cPreparedStatement.setInt(2, clientID);
      cPreparedStatement.setString(3, woType);
       rs = cPreparedStatement.executeQuery();
      if (rs.next())
        mTab.setValue("MFG_WorkOrderClass_ID", Integer.valueOf(rs.getInt(1))); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
      return e.getLocalizedMessage();
    } 
    finally
    {
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    }
    return "";
  }
  
  public String warehouse(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Warehouse_ID = (Integer)value;
    setCalloutActive(true);
    if (M_Warehouse_ID == null || M_Warehouse_ID.intValue() == 0) {
      mTab.setValue("M_Locator_ID", null);
      setCalloutActive(false);
      return "";
    } 
    mTab.setValue("M_Locator_ID", Integer.valueOf(MWarehouse.get(ctx, M_Warehouse_ID.intValue()).getDefaultLocator().getM_Locator_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String WOSource(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    String WOSource = (String)value;
    if (WOSource == null || WOSource.length() == 0) {
      mTab.setValue("C_Order_ID", null);
      mTab.setValue("C_OrderLine_ID", null);
    } 
    return "";
  }
  
  public String woType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    String WOType = (String)value;
    if (WOType == null || WOType.length() == 0) {
      mTab.setValue("MFG_WorkOrderClass_ID", null);
      return "";
    } 
    mTab.setValue("MFG_WorkOrderClass_ID", null);
    int orgID = ((Integer)mTab.getValue("AD_Org_ID")).intValue();
    int clientID = ((Integer)mTab.getValue("AD_Client_ID")).intValue();
    String sql = "SELECT MFG_WorkOrderClass_ID FROM MFG_WorkOrderClass WHERE AD_Org_ID IN (0, ?) AND AD_Client_ID = ? AND WOType = ? AND IsDefault = 'Y' AND IsActive = 'Y' ORDER BY AD_Org_ID DESC";
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
       cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, orgID);
      cPreparedStatement.setInt(2, clientID);
      cPreparedStatement.setString(3, WOType);
       rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        mTab.setValue("MFG_WorkOrderClass_ID", Integer.valueOf(rs.getInt(1)));
      } else {
        mTab.setValue("MFG_WorkOrderClass_ID", null);
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
      return e.getLocalizedMessage();
    } 
    finally
    {
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    }
    return "";
  }
  
  public String workorderClass(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    setCalloutActive(true);
    Integer MFG_WorkOrderClass_ID = (Integer)value;
    if (MFG_WorkOrderClass_ID == null || MFG_WorkOrderClass_ID.intValue() == 0) {
      mTab.setValue("C_DocType_ID", null);
      setCalloutActive(false);
      return "";
    } 
    MMFGWorkOrderClass woc = new MMFGWorkOrderClass(Env.getCtx(), MFG_WorkOrderClass_ID.intValue(), null);
    mTab.setValue("C_DocType_ID", Integer.valueOf(woc.getWO_DocType_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String order(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer C_Order_ID = (Integer)value;
    setCalloutActive(true);
    if (C_Order_ID == null || C_Order_ID.intValue() == 0) {
      mTab.setValue("C_OrderLine_ID", null);
      setCalloutActive(false);
      return "";
    } 
    MOrder order = new MOrder(Env.getCtx(), C_Order_ID.intValue(), null);
    mTab.setValue("C_OrderLine_ID", null);
    mTab.setValue("C_BPartner_ID", Integer.valueOf(order.getC_BPartner_ID()));
    mTab.setValue("C_BPartner_Location_ID", Integer.valueOf(order.getC_BPartner_Location_ID()));
    mTab.setValue("AD_User_ID", Integer.valueOf(order.getAD_User_ID()));
    mTab.setValue("PriorityRule", order.getPriorityRule());
    mTab.setValue("C_Project_ID", Integer.valueOf(order.getC_Project_ID()));
    mTab.setValue("C_Campaign_ID", Integer.valueOf(order.getC_Campaign_ID()));
    mTab.setValue("C_Activity_ID", Integer.valueOf(order.getC_Activity_ID()));
    mTab.setValue("AD_OrgTrx_ID", Integer.valueOf(order.getAD_OrgTrx_ID()));
    mTab.setValue("User1_ID", Integer.valueOf(order.getUser1_ID()));
    mTab.setValue("User2_ID", Integer.valueOf(order.getUser2_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String orderLine(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer C_OrderLine_ID = (Integer)value;
    setCalloutActive(true);
    if (C_OrderLine_ID == null || C_OrderLine_ID.intValue() == 0) {
      setCalloutActive(false);
      return "";
    } 
    MOrderLine line = new MOrderLine(Env.getCtx(), C_OrderLine_ID.intValue(), null);
    mTab.setValue("M_Product_ID", Integer.valueOf(line.getM_Product_ID()));
    mTab.setValue("QtyEntered", line.getQtyEntered());
    setCalloutActive(false);
    return "";
  }
  
  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Product_ID = (Integer)value;
    setCalloutActive(true);
    if (M_Product_ID == null || M_Product_ID.intValue() == 0) {
      mTab.setValue("M_BOM_ID", null);
      mTab.setValue("C_UOM_ID", null);
      mTab.setValue("MFG_Routing_ID", null);
      setCalloutActive(false);
      return "";
    } 
    String restriction = "BOMType='" + X_M_BOM.BOMTYPE_CurrentActive + "' AND BOMUse='" + X_M_BOM.BOMUSE_Manufacturing + "' AND IsActive='Y'";
    MBOM[] boms = MBOM.getOfProduct(Env.getCtx(), M_Product_ID.intValue(), null, restriction);
    if (boms.length != 0) {
      MBOM bom = boms[0];
      mTab.setValue("M_BOM_ID", Integer.valueOf(bom.getM_BOM_ID()));
    } else {
      mTab.setValue("M_BOM_ID", null);
    } 
    MMFGRouting routing = null;
    if (mTab.getValue("M_Warehouse_ID") != null)
      routing = MMFGRouting.getDefaultRouting(Env.getCtx(), M_Product_ID.intValue(), ((Integer)mTab.getValue("M_Warehouse_ID")).intValue()); 
    if (routing != null) {
      mTab.setValue("MFG_Routing_ID", Integer.valueOf(routing.getMFG_Routing_ID()));
    } else {
      mTab.setValue("MFG_Routing_ID", null);
    } 
    MProduct product = new MProduct(Env.getCtx(), M_Product_ID.intValue(), null);
    mTab.setValue("C_UOM_ID", Integer.valueOf(product.getC_UOM_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String bPartner(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer C_BPartner_ID = (Integer)value;
    setCalloutActive(true);
    if (C_BPartner_ID == null || C_BPartner_ID.intValue() == 0) {
      mTab.setValue("C_BPartner_Location_ID", null);
      mTab.setValue("AD_User_ID", null);
      mTab.setValue("C_Project_ID", null);
      setCalloutActive(false);
      return "";
    } 
    int Location_ID = 0;
    int User_ID = 0;
    String sql = "SELECT c.AD_User_ID, loc.C_BPartner_Location_ID AS Location_ID FROM C_BPartner p LEFT OUTER JOIN C_BPartner_Location loc ON (p.C_BPartner_ID=loc.C_BPartner_ID AND loc.IsActive='Y') LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) WHERE p.C_BPartner_ID=? AND p.IsActive='Y' ORDER BY loc.Name ASC ";
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
       cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, C_BPartner_ID.intValue());
       rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        Location_ID = rs.getInt("Location_ID");
        User_ID = rs.getInt("AD_User_ID");
      } 
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, sql, e);
      setCalloutActive(false);
      return e.getLocalizedMessage();
    } 
    finally
    {
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    }
    Integer orderID = (Integer)mTab.getValue("C_Order_ID");
    if (orderID != null && orderID.intValue() != 0) {
      MOrder order = new MOrder(Env.getCtx(), orderID.intValue(), null);
      int bpartnerID = order.getC_BPartner_ID();
      if (C_BPartner_ID.intValue() == bpartnerID) {
        Location_ID = order.getC_BPartner_Location_ID();
        User_ID = order.getAD_User_ID();
      } 
    } 
//    if (C_BPartner_ID.toString().equals(ctx.getContext(1113, 1113, "C_BPartner_ID"))) {
    if (C_BPartner_ID.toString().equals(Env.getContext(ctx,1113, 1113, "C_BPartner_ID"))) {

//    String loc = ctx.getContext(1113, 1113, "C_BPartner_Location_ID");
        String loc = Env.getContext(ctx,1113, 1113, "C_BPartner_Location_ID");

    	if (loc.length() > 0)
        Location_ID = Integer.parseInt(loc); 
//      String cont = ctx.getContext(1113, 1113, "AD_User_ID");
        String cont = Env.getContext(ctx,1113, 1113, "AD_User_ID");

    	if (cont.length() > 0)
        User_ID = Integer.parseInt(cont); 
    } 
    if (Location_ID == 0) {
      mTab.setValue("C_BPartner_Location_ID", null);
    } else {
      mTab.setValue("C_BPartner_Location_ID", Integer.valueOf(Location_ID));
    } 
    if (User_ID == 0) {
      mTab.setValue("AD_User_ID", null);
    } else {
      mTab.setValue("AD_User_ID", Integer.valueOf(User_ID));
    } 
    mTab.setValue("C_Project_ID", null);
    setCalloutActive(false);
    return "";
  }
  
  public String supervisor(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    Integer Supervisor_ID = (Integer)value;
    if (0 == Supervisor_ID.intValue())
      return ""; 
    if (mTab.getValue("SalesRep_ID") == null)
      mTab.setValue("SalesRep_ID", Supervisor_ID); 
    return "";
  }
  
  public String setQtyEntered(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    // Updated by Mukesh @20220803
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

