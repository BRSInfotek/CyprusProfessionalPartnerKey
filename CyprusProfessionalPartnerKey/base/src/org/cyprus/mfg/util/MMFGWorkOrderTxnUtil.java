package org.cyprus.mfg.util;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderClass;
import org.cyprus.mfg.model.MMFGWorkOrderComponent;
import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprus.mfg.model.MMFGWorkOrderResource;
import org.cyprus.mfg.model.MMFGWorkOrderResourceTxnLine;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.model.MMFGWorkOrderTransactionLine;
//import org.cyprusbrs.model.Storage;
import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mfg.model.X_MFG_WorkOrderComponent;
import org.cyprus.mfg.model.X_MFG_WorkOrderResource;
import org.cyprus.mfg.model.X_MFG_WorkOrderTransaction;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;

public class MMFGWorkOrderTxnUtil {
  protected static CLogger log = CLogger.getCLogger(MMFGWorkOrderTxnUtil.class);
  PO po ;
  Trx trx;
  private boolean save = false;
  
  public MMFGWorkOrderTxnUtil(boolean save) {
    this.save = save;
  }
  
  public MMFGWorkOrderResourceTxnLine[] generateResourceTxnLine(Properties ctx, int MFG_WorkOrderTransaction_ID, BigDecimal Qty, String trx, boolean automatic) {
    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, MFG_WorkOrderTransaction_ID, trx);
    if (wot != null && (!wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage) || (!wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_Drafted) && !wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_InProgress)))) {
      log.severe("Work Order transaction type not correct.");
      return null; 
    } 
    
    int OperationFrom = DB.getSQLValue(trx, "SELECT MIN(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional<> 'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    int OperationTo = DB.getSQLValue(trx, "SELECT MAX(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional <> 'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    return generateResourceTxnLine(ctx, MFG_WorkOrderTransaction_ID, Qty, new BigDecimal(OperationFrom), new BigDecimal(OperationTo), trx, automatic);
  }
  
  public MMFGWorkOrderResourceTxnLine[] generateResourceTxnLine(Properties ctx, int MFG_WorkOrderTransaction_ID, BigDecimal Qty, BigDecimal OperationFrom, BigDecimal OperationTo, String trx, boolean automatic) {
    if (0 >= MFG_WorkOrderTransaction_ID) {
      log.severe("No Work Order Transaction ID specified");
      return null;
    } 
    if (OperationFrom.compareTo(OperationTo) > 0) {
      log.severe("Operation Numbers not correct.");
      return null;
    } 
    if (Qty != null && Qty.compareTo(BigDecimal.ZERO) <= 0) {
      log.severe("Number of product assemblies must be positive");
      return null;
    } 
    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, MFG_WorkOrderTransaction_ID, trx);
    if (wot != null && (!wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage) || (!wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_Drafted) && !wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_InProgress)))) {
      log.severe("Work Order transaction type not correct.");
      return null;
    } 
    ArrayList<MMFGWorkOrderResourceTxnLine> wortLines = new ArrayList<MMFGWorkOrderResourceTxnLine>();
    MMFGWorkOrder wo = new MMFGWorkOrder(ctx, wot.getMFG_WorkOrder_ID(), trx);
    int resTxnLineSeqNo = DB.getSQLValue(trx, "SELECT Max(SeqNo) FROM MFG_WorkOrderResTxnLine WHERE MFG_WORKORDERTRANSACTION_ID = ?", new Object[] { Integer.valueOf(MFG_WorkOrderTransaction_ID) });
    StringBuffer wc = new StringBuffer();
    if (OperationFrom.compareTo(BigDecimal.ZERO) > 0) {
      wc.append(" SeqNo >= ").append(OperationFrom);
      if (OperationTo.compareTo(BigDecimal.ZERO) > 0)
        wc.append(" AND SeqNo <= ").append(OperationTo); 
    } else if (OperationTo.compareTo(BigDecimal.ZERO) > 0) {
      wc.append(" SeqNo <= ").append(OperationTo);
    } 
    wc.append(" AND (IsOptional <> 'Y' ");
    if (OperationFrom.compareTo(BigDecimal.ZERO) > 0)
      wc.append(" OR SeqNo = ").append(OperationFrom); 
    if (OperationTo.compareTo(BigDecimal.ZERO) > 0)
      wc.append(" OR SeqNo = ").append(OperationTo); 
    wc.append(" )");
    String whereClause = (wc.length() > 0) ? wc.toString() : null;
    MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(wo, whereClause, "SeqNo");
    StringBuffer response = new StringBuffer();
    for (MMFGWorkOrderOperation woo : woos) {
      MMFGWorkOrderResource[] wors = MMFGWorkOrderResource.getofWorkOrderOperation(woo, null, null);
      for (MMFGWorkOrderResource wor : wors) {
        String chargeType = wor.getChargeType();
        if ((!chargeType.equals(X_MFG_WorkOrderResource.CHARGETYPE_Automatic) || !automatic) && (!chargeType.equals(X_MFG_WorkOrderResource.CHARGETYPE_Manual) || automatic))
          continue; 
        BigDecimal resAmt = BigDecimal.ZERO;
        if (Qty == null) {
          BigDecimal resCharged = wor.getQtySpent();
          BigDecimal resReq = wo.getQtyEntered().multiply(wor.getQtyRequired());
          resAmt = resReq.subtract(resCharged);
          if (resAmt.compareTo(BigDecimal.ZERO) <= 0) {
            log.warning("Estimated Resource usage has been already charged.");
            continue;
          } 
        } else {
          resAmt = Qty.multiply(wor.getQtyRequired());
        } 
        MMFGWorkOrderResourceTxnLine wortl = new MMFGWorkOrderResourceTxnLine(ctx, 0, trx);
        wortl.setQtyEntered(resAmt.setScale(MUOM.getPrecision(ctx, wor.getC_UOM_ID()), 4));
        wortl.setMFG_WORKORDERTRANSACTION_ID(MFG_WorkOrderTransaction_ID);
        wortl.setClientOrg((PO)wot);
        wortl.setresourceinfo(wor);
        resTxnLineSeqNo += 10;
        wortl.setSeqNo(resTxnLineSeqNo);
        wortl.setIsActive(true);
        wortLines.add(wortl);
        MProduct product = MProduct.get(ctx, wortl.getM_Product_ID());
        response.append(product.getName() + ": ").append(wortl.getQtyEntered());
        continue;
      } 
    } 
   
  /// java before 8
    if (this.save && wortLines.size()>0) {
    	
    	for(MMFGWorkOrderResourceTxnLine line: wortLines)
    	{
    		if(!line.save(trx))
        	{
    			log.severe("Could not save Resource transaction lines. "+line.getDisplayValue()+" product "+line.getM_Product_ID());
    		    return null;
        	}
    	}
    }
    
    /// Compiere based...
    
//    if (this.save && 
////      !PO.saveAll(trx, wortLines)) {
//    		
//    	      !po.save(trx)) {
//
//        
//
//      log.severe("Could not save resource transaction line.");
//      return null;
//    }
    
    log.saveInfo("Info", response.toString());
    return wortLines.<MMFGWorkOrderResourceTxnLine>toArray(new MMFGWorkOrderResourceTxnLine[0]);
  }
  
  public MMFGWorkOrderTransaction createWOTxn(Properties ctx, int M_WorkOrder_ID, String TxnType, String trx) {
    MMFGWorkOrder wo = new MMFGWorkOrder(ctx, M_WorkOrder_ID, trx);
    if (wo == null || !wo.getDocStatus().equals(MMFGWorkOrder.DOCSTATUS_InProgress)) {
      log.severe("Work Order number not valid for transactions.");
      return null;
    } 
    log.info("Getting Default Locator of Work Order Warehouse.");
    MLocator loc = MWarehouse.get(ctx, wo.getM_Warehouse_ID()).getDefaultLocator();
    return createWOTxn(ctx, M_WorkOrder_ID, TxnType, 0, loc.getM_Locator_ID(), BigDecimal.ZERO, trx);
  }
  
  public MMFGWorkOrderTransaction createWOTxn(Properties ctx, int MFG_WorkOrder_ID, String TxnType, int parent_MFG_WorkOrderTransaction_ID, String trx) {
    return createWOTxn(ctx, MFG_WorkOrder_ID, TxnType, parent_MFG_WorkOrderTransaction_ID, 0, BigDecimal.ZERO, trx);
  }
  
  public MMFGWorkOrderTransaction createWOTxn(Properties ctx, int M_WorkOrder_ID, String TxnType, int parent_M_WorkOrderTransaction_ID, int M_Locator_ID, BigDecimal Qty, String trx) {
    if (!TxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage) && !TxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) && !TxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder)) {
      log.severe("Not correct transaction type to generate WO Transaction");
      return null;
    } 
    MMFGWorkOrder wo = new MMFGWorkOrder(ctx, M_WorkOrder_ID, trx);
    if (wo == null || !wo.getDocStatus().equals(X_MFG_WorkOrder.DOCSTATUS_InProgress)) {
      log.severe("Work Order number not valid for transactions.");
      return null;
    } 
    if (parent_M_WorkOrderTransaction_ID == 0 && (
      Qty == null || Qty.intValue() == 0)) {
      log.info("Deriving Quantity from Work Order");
      Qty = wo.getQtyEntered();
    } 
    if (Qty.compareTo(BigDecimal.ZERO) <= 0) {
      MMFGWorkOrderTransaction parentWOT = new MMFGWorkOrderTransaction(ctx, parent_M_WorkOrderTransaction_ID, trx);
      Qty = parentWOT.getQtyEntered();
    } 
    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, 0, trx);
    wot.setRequiredColumns(M_WorkOrder_ID, M_Locator_ID, X_MFG_WorkOrderTransaction.WOTXNSOURCE_Generated, TxnType);
    if (parent_M_WorkOrderTransaction_ID > 0) {
      wot.setParentWorkOrderTxn_ID(parent_M_WorkOrderTransaction_ID);
      MMFGWorkOrderTransaction parentWOT = new MMFGWorkOrderTransaction(ctx, parent_M_WorkOrderTransaction_ID, trx);
      wot.setC_DocType_ID(parentWOT.getC_DocType_ID());
      wot.setClientOrg((PO)parentWOT);
    } else {
      MMFGWorkOrderClass woclass = new MMFGWorkOrderClass(ctx, wo.getMFG_WorkOrderClass_ID(), trx);
      wot.setC_DocType_ID(woclass.getWOT_DocType_ID());
      wot.setClientOrg((PO)wo);
    } 
    wot.setQtyEntered(Qty.setScale(MUOM.getPrecision(ctx, wot.getC_UOM_ID()), 4));
    if (this.save && 
      !wot.save(trx)) {
      log.severe("Could not save WO Txn.");
      return null;
    } 
    return wot;
  }
  
  public MMFGWorkOrderTransaction retrieveWOTxn(Properties ctx, int MFG_WorkOrder_ID, String TxnType, String trx) {
    MMFGWorkOrderTransaction wot = null;
    if (wot == null)
//      wot = createWOTxn(ctx, MFG_WorkOrder_ID, TxnType, trx); 
        wot = createWOTxn(ctx, MFG_WorkOrder_ID, TxnType, trx); 

    return wot;
  }
  
  public MMFGWorkOrderTransactionLine[] generateComponentTxnLine(Properties ctx, int MFG_WorkOrderTransaction_ID, BigDecimal Qty, String SupplyType, String trx, BigDecimal mFG_WorkOrderOperation_ID) {
    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, MFG_WorkOrderTransaction_ID, trx);
    if ((!wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_Drafted) && !wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_InProgress)) || (!wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) && !wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder))) {
      log.severe("Invalid Work Order Txn DocStatus.");
      return null;
    } 
    // Added by Mukesh @20221109 
    int OperationFrom=0;
    int OperationTo=0;
    if(mFG_WorkOrderOperation_ID!=null && mFG_WorkOrderOperation_ID.compareTo(Env.ZERO)>0)
    {
    	OperationFrom = DB.getSQLValue(trx, "SELECT MIN(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ? AND IsOptional <> 'Y'", new Object[] { mFG_WorkOrderOperation_ID.intValue() });
    	OperationTo = DB.getSQLValue(trx, "SELECT MAX(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ? AND IsOptional <> 'Y'", new Object[] { mFG_WorkOrderOperation_ID.intValue() });
    }
    else
    // End by Mukesh
    {
    	// int OperationFrom = DB.getSQLValue(trx, "SELECT MIN(SeqNo) FROM M_WorkOrderOperation WHERE M_WorkOrder_ID = ? AND IsOptional <> 'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    	OperationFrom = DB.getSQLValue(trx, "SELECT MIN(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional <> 'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    	// int OperationTo = DB.getSQLValue(trx, "SELECT MAX(SeqNo) FROM M_WorkOrderOperation WHERE M_WorkOrder_ID = ? AND IsOptional <> 'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    	OperationTo = DB.getSQLValue(trx, "SELECT MAX(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional <> 'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    }
    return generateComponentTxnLine(ctx, MFG_WorkOrderTransaction_ID, Qty, new BigDecimal(OperationFrom), new BigDecimal(OperationTo), SupplyType, trx,mFG_WorkOrderOperation_ID);
  }
  
  public MMFGWorkOrderTransactionLine[] generateComponentTxnLine(Properties ctx, int MFG_WorkOrderTransaction_ID, BigDecimal Qty, BigDecimal OperationFrom, BigDecimal OperationTo, String SupplyType, String trx, BigDecimal mFG_WorkOrderOperation_ID) {
    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, MFG_WorkOrderTransaction_ID, trx);
    if ((!wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_Drafted) && !wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_InProgress)) || (!wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) && !wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder))) {
      log.severe("Invalid Work Order Txn DocStatus.");
      return null;
    } 
    int locatorID = wot.getM_Locator_ID();
    if (0 == locatorID) {
      MMFGWorkOrder wo = new MMFGWorkOrder(ctx, wot.getMFG_WorkOrder_ID(), trx);
      if (!wo.getDocStatus().equals(X_MFG_WorkOrder.DOCSTATUS_InProgress)) {
        log.severe("Invalid Work Order DocStatus.");
        return null;
      } 
      locatorID = MWarehouse.get(ctx, wo.getM_Warehouse_ID()).getDefaultLocator().getM_Locator_ID();
    } 
    return generateComponentTxnLine(ctx, MFG_WorkOrderTransaction_ID, Qty, OperationFrom, OperationTo, SupplyType, locatorID, trx,mFG_WorkOrderOperation_ID);
  }
  
  public MMFGWorkOrderTransactionLine[] generateComponentTxnLine(Properties ctx, int MFG_WorkOrderTransaction_ID, BigDecimal Qty, BigDecimal OperationFrom, BigDecimal OperationTo, String SupplyType, int M_Locator_ID, String trx, BigDecimal mFG_WorkOrderOperation_ID) {
    if (OperationFrom != null && OperationFrom.compareTo(OperationTo) > 0) {
      log.severe("Operation Numbers not correct.");
      return null;
    } 
    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, MFG_WorkOrderTransaction_ID, trx);
    if (0 == MFG_WorkOrderTransaction_ID || (!wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_Drafted) && !wot.getDocStatus().equals(X_MFG_WorkOrderTransaction.DOCSTATUS_InProgress)) || (!wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) && !wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder))) {
      log.severe("Cannot create component lines against give WO Txn.");
      return null;
    } 
    if (Qty != null && Qty.compareTo(BigDecimal.ZERO) <= 0) {
      log.severe("Number of product assemblies must be positive");
      return null;
    } 
    ArrayList<MMFGWorkOrderTransactionLine> wotLines = new ArrayList<MMFGWorkOrderTransactionLine>();
    // Added by Mukesh @20221109 
    int lastMandatoryWOO=0;
    if(mFG_WorkOrderOperation_ID!=null && mFG_WorkOrderOperation_ID.compareTo(Env.ZERO)>0)
    lastMandatoryWOO = DB.getSQLValue(trx, "SELECT MAX(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ? AND IsOptional<>'Y'", new Object[] { mFG_WorkOrderOperation_ID.intValue() });
    else
    // End by Mukesh
    lastMandatoryWOO = DB.getSQLValue(trx, "SELECT MAX(SeqNo) FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional<>'Y'", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    boolean assemblyPull = (lastMandatoryWOO == OperationTo.intValue());
    StringBuffer response = new StringBuffer("");
    BigDecimal woQty = DB.getSQLValueBD(trx, "SELECT QtyEntered FROM MFG_WorkOrder WHERE MFG_WorkOrder_ID = ?", new Object[] { Integer.valueOf(wot.getMFG_WorkOrder_ID()) });
    StringBuffer sqlBuf = new StringBuffer("SELECT woc.M_Product_ID, woc.C_UOM_ID, woc.QtyRequired,  woc.SupplyType, woc.M_AttributeSetInstance_ID, woc.M_Locator_ID, woc.BasisType, woo.MFG_WorkOrderOperation_ID, woc.QtyAvailable, woc.QtySpent, woc.QtyAllocated, woc.QtyDedicated FROM MFG_WorkOrderOperation woo INNER JOIN MFG_WorkOrderComponent woc ON woo.MFG_WorkOrderOperation_ID = woc.MFG_WorkOrderOperation_ID WHERE woo.MFG_WorkOrder_ID = ? AND woc.QtyRequired != 0 AND (woo.IsOptional <> 'Y' OR woo.SeqNo = ? OR woo.SeqNo = ?) AND ((woo.SeqNo BETWEEN ? AND ?)");
    if (assemblyPull)
      sqlBuf.append(" OR woc.SupplyType = 'A'"); 
    sqlBuf.append(" ) ORDER BY woo.SeqNo, woc.M_Product_ID, woc.QtyRequired ");
    CPreparedStatement cPreparedStatement = DB.prepareStatement(sqlBuf.toString(), null);
    ResultSet rs = null;
    try {
      cPreparedStatement.setInt(1, wot.getMFG_WorkOrder_ID());
      if (OperationFrom != null && OperationFrom.compareTo(BigDecimal.ZERO) > 0) {
        cPreparedStatement.setInt(2, OperationFrom.intValue());
        cPreparedStatement.setInt(4, OperationFrom.intValue());
      } else {
        cPreparedStatement.setInt(2, 0);
        cPreparedStatement.setInt(4, 0);
      } 
      if (OperationTo != null && OperationTo.compareTo(BigDecimal.ZERO) > 0) {
        cPreparedStatement.setInt(3, OperationTo.intValue());
        cPreparedStatement.setInt(5, OperationTo.intValue());
      } else {
        cPreparedStatement.setInt(3, lastMandatoryWOO);
        cPreparedStatement.setInt(5, 0);
      } 
      rs = cPreparedStatement.executeQuery();
      int productID = 0;
      int uomID = 0;
      BigDecimal qtyEntered = null;
      int asiID = 0;
      int locatorID = 0;
      int compLineNo = DB.getSQLValue(trx, "SELECT COALESCE(MAX(Line),0)+10 FROM MFG_WorkOrderTrxLine WHERE MFG_WorkOrderTransaction_ID = ?", new Object[] { Integer.valueOf(MFG_WorkOrderTransaction_ID) });
      boolean checkInventory = wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder);
      while (rs.next()) {
        productID = rs.getInt(1);
        uomID = rs.getInt(2);
        BigDecimal qtyRequired = rs.getBigDecimal(3);
        String wocSupplyType = rs.getString(4);
        asiID = rs.getInt(5);
        locatorID = rs.getInt(6);
        String basisType = rs.getString(7);
        int wooID = rs.getInt(8);
        BigDecimal qtyIssued = rs.getBigDecimal(9);
        BigDecimal qtySpent = rs.getBigDecimal(10);
        BigDecimal qtyAllocated = rs.getBigDecimal(11); 
        BigDecimal qtyDedicated = rs.getBigDecimal(12);
        if (Qty == null) {
          if (wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder)) {
            qtyEntered = qtyRequired.multiply(woQty).subtract(qtyIssued).subtract(qtyAllocated).subtract(qtyDedicated);
          } else {
            qtyEntered = qtyIssued.subtract(qtySpent);
          } 
        } else {
          qtyEntered = qtyRequired.multiply(Qty);
          if (wot.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) && qtyEntered.setScale(MUOM.getPrecision(ctx, uomID), 4).compareTo(qtyIssued.subtract(qtySpent)) > 0) {
            log.warning("Not enough quantities to return from Work Order");
            continue;
          }  
        } 
        if (qtyEntered.setScale(MUOM.getPrecision(ctx, uomID), 4).compareTo(BigDecimal.ZERO) <= 0)
          continue; 
        if (SupplyType.equals(wocSupplyType) || (wocSupplyType.equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_AssemblyPull) && assemblyPull && !SupplyType.equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_Push))) {
          MMFGWorkOrderTransactionLine compIssueLine = new MMFGWorkOrderTransactionLine(ctx, 0, trx);
          compIssueLine.setClientOrg((PO)wot);
          compIssueLine.setRequiredColumns(MFG_WorkOrderTransaction_ID, productID, uomID, qtyEntered, wooID, basisType);
          if (locatorID != 0) {
            compIssueLine.setM_Locator_ID(locatorID);
          } else {
            MMFGWorkOrder wo = new MMFGWorkOrder(ctx, wot.getMFG_WorkOrder_ID(), trx);
            MLocator loc = new MLocator(ctx, M_Locator_ID, trx);
            if (loc.getM_Warehouse_ID() != wo.getM_Warehouse_ID()) {
              log.warning("Locator passed is not under the Warehouse of the WorkOrder");
              continue;
            } 
            compIssueLine.setM_Locator_ID(M_Locator_ID);
          } 
          if (asiID > 0)
            compIssueLine.setM_AttributeSetInstance_ID(asiID); 
          compIssueLine.setLine(compLineNo);
          compLineNo += 10;
          wotLines.add(compIssueLine);
          MProduct product = new MProduct(ctx, productID, trx);
          if (checkInventory)
            response.append(product.getName() + ": ").append(verifyQuantity(product, wot, qtyEntered, asiID)).append(" "); 
        } 
      } 
    } catch (SQLException e) {
      log.log(Level.SEVERE, sqlBuf.toString(), e);
      log.severe("SQL failure in checking component requirements");
      return null;
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
  /// Compiere based
//	if (this.save && 
//		      !PO.saveAll(trx, wotLines)) {
//		      log.severe("Could not save component transaction lines.");
//		      return null;
//		    } 
    
    /// java before 8
    if (this.save && wotLines.size()>0) {
    	
    	for(MMFGWorkOrderTransactionLine line: wotLines)
    	{
    		if(!line.save(trx))
        	{
    			log.severe("Could not save component transaction lines. "+line.getLine()+" product "+line.getM_Product_ID());
    		    return null;
        	}
    	}
    }
    	// Java 8
//    	wotLines.forEach(lines->{
//    		if(!lines.save(trx))
//    		{
//    			log.severe("Could not save component transaction lines. "+lines.getLine()+" product "+lines.getM_Product_ID());
//    		    return;
//    		}
//    	});
    log.saveInfo("Info", response.toString());
    return wotLines.<MMFGWorkOrderTransactionLine>toArray(new MMFGWorkOrderTransactionLine[0]);
  }
  
  public int addComponentTxnLine(Properties ctx, int MFG_WorkOrderComponent_ID, BigDecimal Qty, int M_Locator_ID, String trx) {
    MMFGWorkOrderComponent woc = new MMFGWorkOrderComponent(ctx, MFG_WorkOrderComponent_ID, trx);
    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(ctx, woc.getMFG_WorkOrderOperation_ID(), trx);
    MMFGWorkOrderTransaction wot = retrieveWOTxn(ctx, woo.getMFG_WorkOrder_ID(), X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder, trx);
    if (wot == null) {
      log.severe("Cannot create or retrieve WO Txn.");
      return 0;
    } 
    MMFGWorkOrderTransactionLine wotLine = new MMFGWorkOrderTransactionLine(ctx, 0, trx);
    wotLine.setRequiredColumns(wot.getMFG_WORKORDERTRANSACTION_ID(), woc.getM_Product_ID(), woc.getC_UOM_ID(), Qty, woc.getMFG_WorkOrderOperation_ID(), woc.getBasisType());
    wotLine.setM_Locator_ID(M_Locator_ID);
    if (this.save)
      if (!wotLine.save(trx)) {
        log.severe("Could not save component transaction line.");
        return 0;
      }  
    return wotLine.getMFG_WORKORDERTRANSACTION_ID();
  }
  
  private String verifyQuantity(MProduct product, MMFGWorkOrderTransaction wot, BigDecimal qty, int asiID) {
    if (product.isStocked()) {
      MMFGWorkOrder wo = new MMFGWorkOrder(wot.getCtx(), wot.getMFG_WorkOrder_ID(), wot.get_TrxName());
      int M_Warehouse_ID = wo.getM_Warehouse_ID();
      BigDecimal available = MStorage.getQtyAvailable(M_Warehouse_ID, product.getM_Product_ID(), asiID, trx);
    
      if (available == null)
        available = Env.ZERO; 
      if (available.signum() == 0)
      //  return Msg.getMsg(wot.getCtx(), "NoQtyAvailable", "0"); 
          return Msg.getMsg( "NoQtyAvailable", "0"); 

    	  if (available.compareTo(qty) < 0)
       // return Msg.getMsg(wot.getCtx(), "InsufficientQtyAvailable", available.toString()); 
    	        return Msg.getMsg("InsufficientQtyAvailable", available.toString()); 

    	  return Msg.getMsg(wot.getCtx(), "QtyAvailable")+" : "+available;
    }else { 
    return Msg.getMsg(wot.getCtx(), "QtyAvailable");
    }
  }
}

