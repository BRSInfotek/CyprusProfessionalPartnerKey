package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Ini;

public class CalloutWorkOrderTransaction extends CalloutEngine {
  protected CLogger log = CLogger.getCLogger(getClass());
  
  public String workOrderID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int MFG_WorkOrder_ID = ((Integer)value).intValue();
    if (MFG_WorkOrder_ID == 0)
      return ""; 
    setCalloutActive(true);
    MMFGWorkOrder workOrder = new MMFGWorkOrder(Env.getCtx(), MFG_WorkOrder_ID, null);
    mTab.setValue("M_Product_ID", Integer.valueOf(workOrder.getM_Product_ID()));
    mTab.setValue("C_UOM_ID", Integer.valueOf(workOrder.getC_UOM_ID()));
    mTab.setValue("M_Locator_ID", Integer.valueOf(workOrder.getM_Locator_ID()));
    mTab.setValue("DocumentNo",workOrder.getDocumentNo());	/// Added Default Transaction No @Mukesh 20220714
    int BPartnerID = workOrder.getC_BPartner_ID();
    if (BPartnerID != 0)
      mTab.setValue("C_BPartner_ID", Integer.valueOf(BPartnerID)); 
    mTab.setValue("C_BPartner_Location_ID", Integer.valueOf(workOrder.getC_BPartner_Location_ID()));
    mTab.setValue("AD_User_ID", Integer.valueOf(workOrder.getAD_User_ID()));
    mTab.setValue("C_Project_ID", Integer.valueOf(workOrder.getC_Project_ID()));
    mTab.setValue("C_Campaign_ID", Integer.valueOf(workOrder.getC_Campaign_ID()));
    mTab.setValue("C_Activity_ID", Integer.valueOf(workOrder.getC_Activity_ID()));
    mTab.setValue("User1_ID", Integer.valueOf(workOrder.getUser1_ID()));
    mTab.setValue("User2_ID", Integer.valueOf(workOrder.getUser2_ID()));
    MMFGWorkOrderClass woc = new MMFGWorkOrderClass(Env.getCtx(), workOrder.getMFG_WorkOrderClass_ID(), null);
    docType(ctx, WindowNo, mTab, mField, Integer.valueOf(woc.getWOT_DocType_ID()), Integer.valueOf(workOrder.getC_DocType_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String setOperationFrom_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int opFromID = ((Integer)value).intValue();
    if (opFromID < 0)
      return ""; 
    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(Env.getCtx(), opFromID, null);
    if (woo.isOptional()) {
      mTab.setValue("IsOptionalFrom", Boolean.valueOf(true));
    } else {
      mTab.setValue("IsOptionalFrom", Boolean.valueOf(false));
    } 
    return "";
  }
  
  public String setOperationTo_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int opToID = ((Integer)value).intValue();
    if (opToID < 0)
      return ""; 
    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(Env.getCtx(), opToID, null);
    if (woo.isOptional()) {
      mTab.setValue("IsOptionalTo", Boolean.valueOf(true));
    } else {
      mTab.setValue("IsOptionalTo", Boolean.valueOf(false));
    } 
    return "";
  }
  
  public String setStepFrom(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int opFromID = ((Integer)mTab.getValue("OperationFrom_ID")).intValue();
    if (opFromID <= 0)
      return ""; 
    String stepFrom = value.toString();
    String woTxnType = mTab.getValue("WorkOrderTxnType").toString();
    if (woTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_WorkOrderMove)) {
      MMFGWorkOrderOperation opFrom = new MMFGWorkOrderOperation(ctx, opFromID, null);
      if (stepFrom.equals(X_MFG_WorkOrderTransaction.STEPFROM_Queue)) {
        mTab.setValue("QtyEntered", opFrom.getQtyQueued());
        return "";
      } 
      if (stepFrom.equals(X_MFG_WorkOrderTransaction.STEPFROM_Run)) {
        mTab.setValue("QtyEntered", opFrom.getQtyRun());
        return "";
      } 
      if (stepFrom.equals(X_MFG_WorkOrderTransaction.STEPTO_ToMove)) {
        mTab.setValue("QtyEntered", opFrom.getQtyAssembled());
        return "";
      } 
    } 
    return "";
  }
  
  private String docType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
    Integer C_DocType_ID = (Integer)value;
    if (C_DocType_ID == null || C_DocType_ID.intValue() == 0)
      return ""; 
    String oldDocNo = (String)mTab.getValue("DocumentNo");
    Integer ad_Client_Id=(Integer)mTab.getValue("AD_Client_ID");
    boolean newDocNo = (oldDocNo == null);
    if (!newDocNo && oldDocNo.startsWith("<") && oldDocNo.endsWith(">"))
      newDocNo = true; 
    Integer oldC_DocType_ID = (Integer)mTab.getValue("C_DocType_ID");
    if ((oldC_DocType_ID == null || oldC_DocType_ID.intValue() == 0) && oldValue != null)
      oldC_DocType_ID = (Integer)oldValue; 
    String sql = "SELECT 'N',d.IsDocNoControlled,s.CurrentNext, d.DocBaseType, s.CurrentNextSys, s.AD_Sequence_ID FROM C_DocType d LEFT OUTER JOIN AD_Sequence s ON (d.DocNoSequence_ID=s.AD_Sequence_ID) WHERE C_DocType_ID=?";
    CPreparedStatement cPreparedStatement1 = null;
    CPreparedStatement cPreparedStatement = null;
    ResultSet resultSet = null;
    ResultSet rs = null;
    try {
      int AD_Sequence_ID = 0;
      if (oldC_DocType_ID.intValue() != 0) {
         cPreparedStatement1 = DB.prepareStatement(sql, null);
        cPreparedStatement1.setInt(1, oldC_DocType_ID.intValue());
         resultSet = cPreparedStatement1.executeQuery();
        if (resultSet.next())
          AD_Sequence_ID = resultSet.getInt(6); 
        resultSet.close();
        cPreparedStatement1.close();
      } 
       cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, C_DocType_ID.intValue());
       rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        mTab.setValue("C_DocType_ID", C_DocType_ID);
        mTab.setValue("C_DocTypeTarget_ID", C_DocType_ID);
        if (rs.getString(2).equals("Y")) {
          if (AD_Sequence_ID != rs.getInt(6))
            newDocNo = true; 
          if (newDocNo)
        	  // have to be check @anshul16122021
            if (Ini.isPropertyBool("cyprusbrsSys") && ad_Client_Id < 1000000) {
              mTab.setValue("DocumentNo", "<" + rs.getString(5) + ">");
            } else {
              mTab.setValue("DocumentNo", "<" + rs.getString(3) + ">");
            }  
        } 
        String s = rs.getString(4);
//        ctx.setContext(WindowNo, "DocBaseType", s);
        Env.setContext(null,WindowNo, "DocBaseType", s);

      } 
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, sql, e);
      return e.getLocalizedMessage();
    } 
    finally
    {
    	DB.close(resultSet, cPreparedStatement1);
    	DB.close(rs, cPreparedStatement);
    	resultSet = null; cPreparedStatement1 = null;
    	rs = null; cPreparedStatement = null;
    }
    return "";
  }
  
  public String workOrderTxnType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    String MMFGWorkOrderTxnType = (String)value;
    if (MMFGWorkOrderTxnType == null || MMFGWorkOrderTxnType.length() == 0)
      return ""; 
    setCalloutActive(true);
    if (MMFGWorkOrderTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
      String sql = "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? ORDER BY SeqNo";
      int workOrderID = ((Integer)mTab.getValue("MFG_WorkOrder_ID")).intValue();
//      int OperationFrom_ID = QueryUtil.getSQLValue((Trx)null, sql, new Object[] { Integer.valueOf(workOrderID) });
      int OperationFrom_ID = DB.getSQLValue(null, sql, new Object[] { Integer.valueOf(workOrderID) });

      mTab.setValue("OperationFrom_ID", Integer.valueOf(OperationFrom_ID));
    } 
    setCalloutActive(false);
    return "";
  }
  
  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Product_ID = (Integer)value;
    if (M_Product_ID == null || M_Product_ID.intValue() == 0)
      return ""; 
    setCalloutActive(true);
    int M_AttributeSetInstance_ID = 0;
//    if (ctx.getContextAsInt(1113, 1113, "M_Product_ID") == M_Product_ID.intValue() && ctx.getContextAsInt(1113, 1113, "M_AttributeSetInstance_ID") != 0) {
    if (Env.getContextAsInt(null,1113, 1113, "M_Product_ID") == M_Product_ID.intValue() && Env.getContextAsInt(null,1113, 1113, "M_AttributeSetInstance_ID") != 0) {

//    M_AttributeSetInstance_ID = ctx.getContextAsInt(1113, 1113, "M_AttributeSetInstance_ID");
        M_AttributeSetInstance_ID = Env.getContextAsInt(null,1113, 1113, "M_AttributeSetInstance_ID");

    	mTab.setValue("M_AttributeSetInstance_ID", Integer.valueOf(M_AttributeSetInstance_ID));
    } else {
      mTab.setValue("M_AttributeSetInstance_ID", null);
    } 
    MProduct product = MProduct.get(Env.getCtx(), M_Product_ID.intValue());
    mTab.setValue("C_UOM_ID", Integer.valueOf(product.getC_UOM_ID()));
    setCalloutActive(false);
    return "";
  }
  
  public String woComplete(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    if (value.toString().equals("N"))
      return ""; 
    if (mTab.getValue("MFG_WorkOrder_ID") == null)
      return ""; 
    int MFG_WorkOrder_ID = ((Integer)mTab.getValue("MFG_WorkOrder_ID")).intValue();
    MMFGWorkOrderOperation lastMandatoryWOO = null;
    String sql = "SELECT * FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional <> 'Y' ORDER BY SeqNo DESC";
    CPreparedStatement cPreparedStatement =null;
    ResultSet rs = null;
    boolean success = true;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, MFG_WorkOrder_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        lastMandatoryWOO = new MMFGWorkOrderOperation(Env.getCtx(), rs, null);
      } else {
        success = false;
      } 
      cPreparedStatement.close();
      rs.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      this.log.severe(sql);
      e.printStackTrace();
      return "";
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        cPreparedStatement = null;
        if (rs != null)
          rs.close(); 
        rs = null;
      } catch (SQLException e) {
        this.log.log(Level.SEVERE, sql, e);
      } 
    } 
    if (!success)
      return ""; 
    mTab.setValue("OperationTo_ID", Integer.valueOf(lastMandatoryWOO.getMFG_WorkOrderOperation_ID()));
    if (lastMandatoryWOO.isOptional()) {
      mTab.setValue("IsOptionalTo", Boolean.valueOf(true));
    } else {
      mTab.setValue("IsOptionalTo", Boolean.valueOf(false));
    } 
    mTab.setValue("StepTo", X_MFG_WorkOrderTransaction.STEPTO_ToMove);
    return "";
  }
  
  public String setLineQtyEntered(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
   // int C_UOM_ID = ctx.getContextAsInt(WindowNo, "C_UOM_ID");
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();

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
  
  public String setHeaderQtyEntered(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    //int C_UOM_ID = ctx.getContextAsInt(WindowNo, "C_UOM_ID");
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();

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

