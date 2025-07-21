package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mfg.model.X_MFG_WorkOrderComponent;
import org.cyprus.mfg.model.X_MFG_WorkOrderTransaction;
import org.cyprus.mfg.model.X_MFG_WorkOrderTrxLine;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MInOutLineMA;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MStorageDetail;
import org.cyprusbrs.framework.MTransaction;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MUOMConversion;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.framework.X_AD_Client;
import org.cyprusbrs.framework.X_M_InOut;
import org.cyprusbrs.framework.X_M_Transaction;
import org.cyprusbrs.framework.X_Ref_Quantity_Type;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_Ref_Quantity_Type;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;
import org.cyprusbrs.util.ValueNamePair;

public class MWMSWarehouseTask extends X_WMS_WarehouseTask implements DocAction {
  private static final CLogger log = CLogger.getCLogger(MWMSWarehouseTask.class);
  
  static PO po;
  private static final long serialVersionUID = 1L;
  
  private boolean skipDedicating = false;
  
  private int C_Order_ID;
  
  private boolean m_reversal;
  
  private String m_processMsg;
  
  public MWMSWarehouseTask(Properties ctx, int M_WarehouseTask_ID, String trx) {
    super(ctx, M_WarehouseTask_ID, trx);
    this.C_Order_ID = 0;
    this.m_reversal = false;
    this.m_processMsg = null;
    if (M_WarehouseTask_ID == 0) {
      setDocAction(DOCACTION_Complete);
      setDocStatus(DOCSTATUS_Drafted);
      setMovementDate(new Timestamp(System.currentTimeMillis()));
      setProcessed(false);
    } 
  }
  
  public MWMSWarehouseTask(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.C_Order_ID = 0;
    this.m_reversal = false;
    this.m_processMsg = null;
  }
  
  public MWMSWarehouseTask(Properties ctx, int M_Warehouse_ID, int C_DocType_ID, int M_InOutLine_ID, int C_OrderLine_ID, int M_WorkOrderComponent_ID, int M_Locator_ID, int M_LocatorTo_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int C_UOM_ID, BigDecimal TargetQty, int C_WaveLine_ID, String trx) {
    this(ctx, M_Warehouse_ID, C_DocType_ID, M_InOutLine_ID, C_OrderLine_ID, M_WorkOrderComponent_ID, M_Locator_ID, M_LocatorTo_ID, M_Product_ID, M_AttributeSetInstance_ID, C_UOM_ID, TargetQty, C_WaveLine_ID, 0, trx);
  }
  
  public MWMSWarehouseTask(Properties ctx, int M_Warehouse_ID, int C_DocType_ID, int M_InOutLine_ID, int C_OrderLine_ID, int M_WorkOrderComponent_ID, int M_Locator_ID, int M_LocatorTo_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int C_UOM_ID, BigDecimal TargetQty, int C_WaveLine_ID, int C_Order_ID, String trx) {
    this(ctx, 0, trx);
    MWarehouse wh = MWarehouse.get(ctx, M_Warehouse_ID);
    setAD_Org_ID(wh.getAD_Org_ID());
    setM_Warehouse_ID(M_Warehouse_ID);
    setC_DocType_ID(C_DocType_ID);
    setM_InOutLine_ID(M_InOutLine_ID);
    setC_OrderLine_ID(C_OrderLine_ID);
    if (M_WorkOrderComponent_ID != 0)
      setMFG_WorkOrderComponent_ID(M_WorkOrderComponent_ID); 
    setM_Locator_ID(M_Locator_ID);
    setM_LocatorTo_ID(M_LocatorTo_ID);
    setM_Product_ID(M_Product_ID);
    setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
    setC_UOM_ID(C_UOM_ID);
    setTargetQty(TargetQty);
    if (C_WaveLine_ID != 0)
      setWMS_WaveLine_ID(C_WaveLine_ID); 
    setC_Order_ID(C_Order_ID);
  }
  
  public void setC_Order_ID(int p_C_Order_ID) {
    this.C_Order_ID = p_C_Order_ID;
  }
  
  public int getC_Order_ID() {
    if (this.C_Order_ID > 0)
      return this.C_Order_ID; 
    this.C_Order_ID = DB.getSQLValue(get_TrxName(), "SELECT C_Order_ID FROM C_OrderLine WHERE C_OrderLine_ID = ? ", new Object[] { Integer.valueOf(getC_OrderLine_ID()) });
    return this.C_Order_ID;
  }
  
  public String getDocumentInfo() {
    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
    return dt.getName() + " " + getDocumentNo();
  }
  
  private void setReversal(boolean reversal) {
    this.m_reversal = reversal;
  }
  
  private boolean isReversal() {
    return this.m_reversal;
  }
  
  public String getDocBaseType() {
    MDocType docType = MDocType.get(getCtx(), getC_DocType_ID());
    if (docType != null)
      return docType.getDocBaseType(); 
    return "";
  }
  
  protected boolean beforeSave(boolean newRecord) {
	  
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (is_ValueChanged("M_WorkOrderComponent_ID")) {
    	
    	//check at run time @Anil16122021
//      SysEnv se2 = SysEnv.get("CMFG");
//      if (se2 == null || !se2.checkLicense())
        return false; 
    } 
    if (getC_DocType_ID() == 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@ @C_DocType_ID@"));
      return false;
    } 
    if (newRecord || is_ValueChanged("TargetQty") || is_ValueChanged("C_UOM_ID"))
      setQtySuggested(); 
    if (newRecord || is_ValueChanged("QtyEntered") || is_ValueChanged("C_UOM_ID"))
      setMovementQty(); 
    if (newRecord || is_ValueChanged("TargetQty") || is_ValueChanged("QtyEntered"))
      if (getTargetQty().compareTo(getQtyEntered()) < 0) {
        log.saveError("Error", Msg.parseTranslation(getCtx(), "@SuggestedQtyLessThanActual@"));
        return false;
      }  
    if ((newRecord || is_ValueChanged("M_Locator_ID")) && !MWarehouse.IsLocatorInWarehouse(getM_Warehouse_ID(), getM_Locator_ID())) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_Locator_ID@ : @WarehouseLocatorMismatch@"));
      return false;
    } 
    if ((newRecord || is_ValueChanged("M_LocatorTo_ID")) && !MWarehouse.IsLocatorInWarehouse(getM_Warehouse_ID(), getM_LocatorTo_ID())) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_LocatorTo_ID@ : @WarehouseLocatorMismatch@"));
      return false;
    } 
    if (getM_ActualLocator_ID() == 0) {
      setM_ActualLocator_ID(getM_Locator_ID());
    } else if ((newRecord || is_ValueChanged("M_ActualLocator_ID")) && !MWarehouse.IsLocatorInWarehouse(getM_Warehouse_ID(), getM_ActualLocator_ID())) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_ActualLocator_ID@ : @WarehouseLocatorMismatch@"));
      return false;
    } 
    if (getM_LocatorTo_ID() == 0) {
      setM_ActualLocatorTo_ID(getM_LocatorTo_ID());
    } else if ((newRecord || is_ValueChanged("M_ActualLocatorTo_ID")) && !MWarehouse.IsLocatorInWarehouse(getM_Warehouse_ID(), getM_LocatorTo_ID())) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@M_ActualLocatorTo_ID@ : @WarehouseLocatorMismatch@"));
      return false;
    } 
    if (getM_Locator_ID() == getM_LocatorTo_ID() || getM_ActualLocator_ID() == getM_LocatorTo_ID()) {
      log.saveError("Error", Msg.getMsg(getCtx(), "SameFromAndToLocators"));
      return false;
    } 
    return true;
  }

  


public boolean approveIt() {
    log.info(toString());
    setIsApproved(true);
    return true;
  }
  
  public boolean closeIt() {
    log.info(toString());
    setDocAction(DOCACTION_None);
    return true;
  }
  
  public String completeIt() {
    MOrder order = null;
    MOrderLine orderLine = null;
    X_MFG_WorkOrderComponent woc = null;
    Class<?> txnUtil = null;
    Object txnUtilInstance = null;
    if (getC_OrderLine_ID() != 0) {
      orderLine = new MOrderLine(getCtx(), getC_OrderLine_ID(), get_TrxName());
      order = orderLine.getParent();
      if (order.isSOTrx() && !isReversal()) {
        String sql = " UPDATE C_OrderLine ol SET QtyAllocated = COALESCE(QtyAllocated, 0) + ? WHERE C_OrderLine_ID = ? ";
    //    DB.executeUpdate(get_TrxName(), sql, new Object[] { getMovementQty(), Integer.valueOf(getC_OrderLine_ID()) });
     //Have to Check at Run time @Anil171220212
     DB.executeUpdateEx(sql, new Object[] { getMovementQty(), get_TrxName(), Integer.valueOf(getC_OrderLine_ID()) }, get_TrxName());
      } 
    } else if (getMFG_WorkOrderComponent_ID() != 0) {
      txnUtil = getWorkOrderTxnUtilClass();
      txnUtilInstance = getWorkOrderTxnUtilInstance();
      woc = new X_MFG_WorkOrderComponent(getCtx(), getMFG_WorkOrderComponent_ID(), get_TrxName());
    } 
    if (isReversal() && order != null && order.isSOTrx() && getM_InOutLine_ID() != 0) {
      MInOutLine line = new MInOutLine(getCtx(), getM_InOutLine_ID(), get_TrxName());
      MInOut inout = line.getParent();
      if (inout.getDocStatus().equals(X_M_InOut.DOCSTATUS_Completed) || inout.getDocStatus().equals(X_M_InOut.DOCSTATUS_Closed)) {
        this.m_processMsg = "Cannot Reverse task : Shipment already completed";
        return "IN";
      } 
      line.setQtyEntered(Env.ZERO);
      line.setQty(Env.ZERO);
      line.setQtyAllocated(Env.ZERO);
      if (!line.save()) {
        this.m_processMsg = "Cannot delete shipment line ";
        return "IN";
      } 
    } else if (isReversal() && woc != null && getMFG_WorkOrderTrxLine_ID() != 0) {
    	X_MFG_WorkOrderTrxLine woTxnLine = new X_MFG_WorkOrderTrxLine(getCtx(), getMFG_WorkOrderTrxLine_ID(), get_TrxName());
      X_MFG_WorkOrderTransaction woTxn = new X_MFG_WorkOrderTransaction(getCtx(), woTxnLine.getMFG_WORKORDERTRANSACTION_ID(), get_TrxName());
      if (woTxn.getDocStatus().equals(X_M_InOut.DOCSTATUS_Completed) || woTxn.getDocStatus().equals(X_M_InOut.DOCSTATUS_Closed)) {
        this.m_processMsg = "Cannot Reverse task : Work Order Transaction already completed";
        return "IN";
      } 
      woTxnLine.setQtyEntered(Env.ZERO);
      if (!woTxnLine.save()) {
        this.m_processMsg = "Cannot delete work order transaction line ";
        return "IN";
      } 
    } 
    MInOutLine shipmentLine = null;
    int M_WorkOrderTransactionLine_ID = 0;
    if (!isReversal() && order != null && order.isSOTrx() && getM_InOutLine_ID() == 0) {
      int C_DocTypeShipment_ID = DB.getSQLValue(null, "SELECT C_DocTypeShipment_ID FROM C_DocType WHERE C_DocType_ID=?", new Object[] { Integer.valueOf(order.getC_DocType_ID()) });
    MInOut inout = order.getOpenInOut(C_DocTypeShipment_ID, orderLine.getM_Warehouse_ID(), orderLine.getC_BPartner_ID(), orderLine.getC_BPartner_Location_ID());
     

      if (inout == null) {
        inout = new MInOut(order, 0, getMovementDate());
        if (inout == null) {
          this.m_processMsg = "Cannout create shipment";
          return "IN";
        } 
        inout.setM_Warehouse_ID(orderLine.getM_Warehouse_ID());
        if (order.getC_BPartner_ID() != orderLine.getC_BPartner_ID())
          inout.setC_BPartner_ID(orderLine.getC_BPartner_ID()); 
        if (order.getC_BPartner_Location_ID() != orderLine.getC_BPartner_Location_ID())
          inout.setC_BPartner_Location_ID(orderLine.getC_BPartner_Location_ID()); 
        if (!inout.save()) {
          this.m_processMsg = "Cannout create shipment";
          return "IN";
        } 
      } 
      shipmentLine = new MInOutLine(inout);
      shipmentLine.setOrderLine(orderLine, getM_LocatorTo_ID(), getMovementQty());
      shipmentLine.setQty(getMovementQty());
      shipmentLine.setQtyEntered(getQtyEntered());
      shipmentLine.setC_UOM_ID(getC_UOM_ID());
      shipmentLine.setM_AttributeSetInstance_ID(getM_ActualASI_ID());
      shipmentLine.setQtyAllocated(getMovementQty());
      if (!shipmentLine.save()) {
        this.m_processMsg = "Could not create Shipment Line";
        return "IN";
      } 
      setM_InOutLine_ID(shipmentLine.getM_InOutLine_ID());
    } else if (!isReversal() && woc != null && getMFG_WorkOrderTransactionLine_ID() == 0) {
      try {
        Method addLine = txnUtil.getMethod("addComponentTxnLine", new Class[] { Properties.class, int.class, BigDecimal.class, int.class, Trx.class });
        Object txnLineID = addLine.invoke(txnUtilInstance, new Object[] { getCtx(), Integer.valueOf(getMFG_WorkOrderComponent_ID()), getMovementQty(), Integer.valueOf(getM_LocatorTo_ID()), get_TrxName() });
        if (txnLineID != null) {
          M_WorkOrderTransactionLine_ID = ((Integer)txnLineID).intValue();
          setMFG_WorkOrderTrxLine_ID(M_WorkOrderTransactionLine_ID);
        } else {
          log.severe("Unable to add work order transaction line");
          return "IN";
        } 
      } catch (Exception e) {
        log.log(Level.SEVERE, "addComponentTxnLine", e);
        return "IN";
      } 
    } 
    ArrayList<MWMSWarehouseTaskMA> mas = null;
	try {
		mas = checkMaterialPolicy();
	} catch (Exception e) {
		
		e.printStackTrace();
	}
    if (mas == null)
      return "IN"; 
    List<MStorageDetail> storagesToSave = new ArrayList<MStorageDetail>();
    List<MInOutLineMA> shipmentMAToSave = new ArrayList<MInOutLineMA>();
    List<MTransaction> transactionsToSave = new ArrayList<MTransaction>();
    if (getM_ActualASI_ID() == 0) {
      for (MWMSWarehouseTaskMA ma : mas) {
        MStorageDetail storageOnHandFrom = MStorageDetail.getForUpdate(getCtx(), getM_ActualLocator_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.ON_HAND, get_Trx());
        if (storageOnHandFrom == null) {
          this.m_processMsg = "Storage From not valid";
          return "IN";
        } 
//        Storage.Record storageRecTo = Storage.getCreateRecord(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), get_TrxName());
       MStorage storageRecTo = MStorage.getCreate(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), get_TrxName());
       
        storageRecTo.setDetailsBulkUpdate(true);
        MStorageDetail storageOnHandTo = storageRecTo.getDetail(X_Ref_Quantity_Type.ON_HAND);
        storageOnHandFrom.setQty(storageOnHandFrom.getQty().subtract(ma.getMovementQty()));
        storageOnHandTo.setQty(storageOnHandTo.getQty().add(ma.getMovementQty()));
        if (!isReversal() && order != null && order.isSOTrx()) {
          BigDecimal qtyToAllocate = ma.getMovementQty();
          MStorageDetail storageAllocTo = storageRecTo.getDetail(X_Ref_Quantity_Type.ALLOCATED);
          storageAllocTo.setQty(storageAllocTo.getQty().add(qtyToAllocate));
          storagesToSave.add(storageAllocTo);
        } 
        storagesToSave.add(storageOnHandTo);
        storagesToSave.add(storageOnHandFrom);
        if (!storageRecTo.validate()) {
          this.m_processMsg = "Could not update storage records";
          return "IN";
        } 
        if (shipmentLine != null) {
          MInOutLineMA shipmentMA = new MInOutLineMA(shipmentLine, storageOnHandTo.getM_AttributeSetInstance_ID(), ma.getMovementQty());
          shipmentMAToSave.add(shipmentMA);
        } else if (M_WorkOrderTransactionLine_ID != 0) {
          PO WOTxnLineMA = getWorkOrderTxnLineMAInstance(getCtx(), get_TrxName(), getMFG_WorkOrderTransactionLine_ID(), storageOnHandTo.getM_AttributeSetInstance_ID(), ma.getMovementQty());
          if (!WOTxnLineMA.save()) {
            this.m_processMsg = "Cannot create allocation for work order transaction";
            return "IN";
          } 
        } 
        MTransaction trxFrom = new MTransaction(getCtx(), getAD_Org_ID(), X_M_Transaction.MOVEMENTTYPE_MovementFrom, getM_ActualLocator_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), ma.getMovementQty().negate(), getMovementDate(), get_TrxName());
        trxFrom.setWMS_WarehouseTask_ID(getWMS_WarehouseTask_ID());
        transactionsToSave.add(trxFrom);
        MTransaction trxTo = new MTransaction(getCtx(), getAD_Org_ID(), X_M_Transaction.MOVEMENTTYPE_MovementTo, getM_LocatorTo_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), ma.getMovementQty(), getMovementDate(), get_TrxName());
        trxTo.setWMS_WarehouseTask_ID(getWMS_WarehouseTask_ID());
        transactionsToSave.add(trxTo);
      } 
    } else {
      MStorageDetail storageOnHandFrom = MStorageDetail.getForUpdate(getCtx(), getM_ActualLocator_ID(), getM_Product_ID(), getM_ActualASI_ID(), X_Ref_Quantity_Type.ON_HAND, get_Trx());
      if (storageOnHandFrom == null) {
        this.m_processMsg = "Storage From not valid";
        return "IN";
      } 
//      Storage.Record storageRecTo = Storage.getCreateRecord(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), getM_ActualASI_ID(), get_TrxName());
      MStorage storageRecTo = MStorage.getCreate(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), getM_ActualASI_ID(), get_TrxName());

      storageRecTo.setDetailsBulkUpdate(true);
      MStorageDetail storageOnHandTo = storageRecTo.getDetail(X_Ref_Quantity_Type.ON_HAND);
      storageOnHandFrom.setQty(storageOnHandFrom.getQty().subtract(getMovementQty()));
      storageOnHandTo.setQty(storageOnHandTo.getQty().add(getMovementQty()));
      if (!isReversal() && ((order != null && order.isSOTrx()) || woc != null)) {
        BigDecimal qtyToAllocate = getMovementQty();
        MStorageDetail storageAllocTo = storageRecTo.getDetail(X_Ref_Quantity_Type.ALLOCATED);
        storageAllocTo.setQty(storageAllocTo.getQty().add(qtyToAllocate));
        storagesToSave.add(storageAllocTo);
      } 
      if (!storageRecTo.validate()) {
        this.m_processMsg = "Could not update storage records";
        return "IN";
      } 
      storagesToSave.add(storageOnHandFrom);
      storagesToSave.add(storageOnHandTo);
      if (shipmentLine != null) {
        MInOutLineMA shipmentMA = new MInOutLineMA(shipmentLine, storageOnHandTo.getM_AttributeSetInstance_ID(), getMovementQty());
        shipmentMAToSave.add(shipmentMA);
      } else if (M_WorkOrderTransactionLine_ID != 0) {
        PO WOTxnLineMA = getWorkOrderTxnLineMAInstance(getCtx(), get_TrxName(), getMFG_WorkOrderTransactionLine_ID(), storageOnHandTo.getM_AttributeSetInstance_ID(), getMovementQty());
        if (!WOTxnLineMA.save()) {
          this.m_processMsg = "Cannot create allocation for work order transaction";
          return "IN";
        } 
      } 
      MTransaction trxFrom = new MTransaction(getCtx(), getAD_Org_ID(), X_M_Transaction.MOVEMENTTYPE_MovementFrom, getM_ActualLocator_ID(), getM_Product_ID(), getM_ActualASI_ID(), getMovementQty().negate(), getMovementDate(), get_TrxName());
      trxFrom.setWMS_WarehouseTask_ID(getWMS_WarehouseTask_ID());
      transactionsToSave.add(trxFrom);
      MTransaction trxTo = new MTransaction(getCtx(), getAD_Org_ID(), X_M_Transaction.MOVEMENTTYPE_MovementTo, getM_LocatorTo_ID(), getM_Product_ID(), getM_ActualASI_ID(), getMovementQty(), getMovementDate(), get_TrxName());
      trxTo.setWMS_WarehouseTask_ID(getWMS_WarehouseTask_ID());
      transactionsToSave.add(trxTo);
    } 
//    if (!PO.saveAll(get_TrxName(), storagesToSave)) {
    if (!po.save(get_TrxName())) {

    this.m_processMsg = "Could not update storage records";
      return "IN";
    } 
//    if (!PO.saveAll(get_TrxName(), shipmentMAToSave)) {
    if (!po.save(get_TrxName())) {

    this.m_processMsg = "Cannot create allocation for shipment";
      return "IN";
    } 
//    if (!PO.saveAll(get_TrxName(), transactionsToSave)) {
    if (!po.save(get_TrxName())) {

    this.m_processMsg = "Transaction From not inserted";
      return "IN";
    } 
    setQtyDedicated(Env.ZERO);
    if (getMFG_WorkOrderTransactionLine_ID() != 0) {
      boolean WOTxnComplete = false;
      X_MFG_WorkOrderTrxLine WOTxnLine = new X_MFG_WorkOrderTrxLine(getCtx(), getMFG_WorkOrderTrxLine_ID(), get_TrxName());
      DocAction WOTxnInstance = getWorkOrderTransactionInstance(getCtx(), WOTxnLine.getMFG_WORKORDERTRANSACTION_ID(), get_TrxName());
     DocumentEngine engine = new DocumentEngine(WOTxnInstance, DOCACTION_Complete);
     WOTxnComplete = engine.processIt(DOCACTION_Complete);
     //WOTxnComplete = DocumentEngine.processIt(WOTxnInstance, DOCACTION_Complete);
      if (WOTxnComplete) {
        WOTxnInstance.save();
      } else {
        log.severe("Unable to complete work order transaction");
        return "IN";
      } 
    } 
    return "CO";
  }
  
  private int getMFG_WorkOrderTransactionLine_ID() {
	// TODO Auto-generated method stub
	return 0;
}

public File createPDF() {
    try {
      File temp = File.createTempFile(get_TableName() + get_ID() + "_", ".pdf");
      return createPDF(temp);
    } catch (Exception e) {
      log.severe("Could not create PDF - " + e.getMessage());
      return null;
    } 
  }
  
  public File createPDF(File file) {
    return null;
  }
  
  public BigDecimal getApprovalAmt() {
    return null;
  }
  
  public int getC_Currency_ID() {
    return 0;
  }
  
  public int getDoc_User_ID() {
    return getCreatedBy();
  }
  
  public String getProcessMsg() {
    return this.m_processMsg;
  }
  
  public String getSummary() {
    StringBuffer sb = new StringBuffer();
    sb.append(getDocumentNo());
    if (getDescription() != null && getDescription().length() > 0)
      sb.append(" - ").append(getDescription()); 
    return sb.toString();
  }
  
  public boolean invalidateIt() {
    log.info(toString());
    setDocAction(DOCACTION_Prepare);
    return true;
  }
  
  public void setQtySuggested() {
    BigDecimal TargetQty = getTargetQty();
    if (TargetQty == null || TargetQty.compareTo(BigDecimal.ZERO) == 0 || getC_UOM_ID() == 0)
      return; 
    int precision = MUOM.getPrecision(getCtx(), getC_UOM_ID());
    TargetQty = TargetQty.setScale(precision, 4);
    setTargetQty(TargetQty);
    BigDecimal QtySuggested = MUOMConversion.convertProductFrom(getCtx(), getM_Product_ID(), getC_UOM_ID(), TargetQty);
    if (QtySuggested == null)
      QtySuggested = TargetQty; 
    log.fine("UOM=" + getC_UOM_ID() + ", TargetQty=" + TargetQty + " -> " + " ExpectedQty=" + QtySuggested);
    setTargetQty(TargetQty);
    setQtyEntered(TargetQty);
    setQtySuggested(QtySuggested);
  }
  
  public void setMovementQty() {
    BigDecimal QtyEntered = getQtyEntered();
    if (QtyEntered == null || QtyEntered.compareTo(BigDecimal.ZERO) == 0 || getC_UOM_ID() == 0)
      return; 
    int precision = MUOM.getPrecision(getCtx(), getC_UOM_ID());
    QtyEntered = QtyEntered.setScale(precision, 4);
    setQtyEntered(QtyEntered);
    BigDecimal MovementQty = MUOMConversion.convertProductFrom(getCtx(), getM_Product_ID(), getC_UOM_ID(), QtyEntered);
    if (MovementQty == null)
      MovementQty = QtyEntered; 
    log.fine("UOM=" + getC_UOM_ID() + ", QtyEntered=" + QtyEntered + " -> " + " MovementQty=" + MovementQty);
    setMovementQty(MovementQty);
    setQtyEntered(QtyEntered);
  }
  
  private String dedicateStorage() throws Exception {
    if (this.skipDedicating || isReversal())
      return ""; 
    BigDecimal qtyToDedicate = getQtySuggested().subtract(getQtyDedicated());
    if (qtyToDedicate.compareTo(BigDecimal.ZERO) == 0)
      return ""; 
    String errorMsg = createMALines();
    if (errorMsg != null && errorMsg.length() != 0)
      return errorMsg; 
    return "";
  }
  
  private String createMALines() throws Exception {
    String errorMsg = reverseMALines();
    if (errorMsg != null && errorMsg.length() != 0)
      return errorMsg; 
    BigDecimal qtyToDedicate = getQtySuggested();
    List<MWMSWarehouseTaskMA> mas = new ArrayList<MWMSWarehouseTaskMA>();
    List<MStorageDetail> storagesToSave = new ArrayList<MStorageDetail>();
    if (getM_AttributeSetInstance_ID() == 0) {
//      List<Storage.Record> storages = Storage.getAll(getCtx(), getM_Product_ID(), getM_Locator_ID(), qtyToDedicate, get_TrxName());
    	MStorage[] stor = MStorage.getAll(getCtx(), getM_Product_ID(), getM_Locator_ID(), get_TrxName());
    	for (MStorage storage: stor)
    	{

         //   Storage.Record storage = storages.get(ii);
            BigDecimal qtyAvailable = storage.getQtyAvailable();
            if (qtyAvailable.compareTo(BigDecimal.ZERO) > 0) {
              BigDecimal qty = qtyAvailable;
              if (qtyToDedicate.compareTo(qty) < 0)
                qty = qtyToDedicate; 
              MWMSWarehouseTaskMA ma = new MWMSWarehouseTaskMA(this, storage.getM_AttributeSetInstance_ID(), Env.ZERO, qty);
              mas.add(ma);
              MStorageDetail dedicated = storage.getDetail(X_Ref_Quantity_Type.DEDICATED);
              dedicated.setQty(storage.getQtyDedicated().add(qty));
              storagesToSave.add(dedicated);
              MStorageDetail storageExpectedTo = MStorageDetail.getCreate(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), storage.getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.EXPECTED, get_Trx());
              storageExpectedTo.setQty(storageExpectedTo.getQty().add(qty));
              storagesToSave.add(storageExpectedTo);
              qtyToDedicate = qtyToDedicate.subtract(qty);
            //  log.fine("#" + ii + ": " + ma + ", QtyToDedicate=" + qtyToDedicate);
              if (qtyToDedicate.signum() == 0)
                break; 
            } 
          
    	}
    	for (MStorage storage: stor) {
    	//for (int ii = 0; ii < storages.size(); ii++) {
      //  Storage.Record storage = storages.get(ii);
        BigDecimal qtyAvailable = storage.getQtyAvailable();
        if (qtyAvailable.compareTo(BigDecimal.ZERO) > 0) {
          BigDecimal qty = qtyAvailable;
          if (qtyToDedicate.compareTo(qty) < 0)
            qty = qtyToDedicate; 
          MWMSWarehouseTaskMA ma = new MWMSWarehouseTaskMA(this, storage.getM_AttributeSetInstance_ID(), Env.ZERO, qty);
          mas.add(ma);
          MStorageDetail dedicated = storage.getDetail(X_Ref_Quantity_Type.DEDICATED);
          dedicated.setQty(storage.getQtyDedicated().add(qty));
          storagesToSave.add(dedicated);
          MStorageDetail storageExpectedTo = MStorageDetail.getCreate(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), storage.getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.EXPECTED, get_Trx());
          storageExpectedTo.setQty(storageExpectedTo.getQty().add(qty));
          storagesToSave.add(storageExpectedTo);
          qtyToDedicate = qtyToDedicate.subtract(qty);
         // log.fine("#" + ii + ": " + ma + ", QtyToDedicate=" + qtyToDedicate);
          if (qtyToDedicate.signum() == 0)
            break; 
        } 
      } 
      if (qtyToDedicate.signum() != 0)
        return Msg.translate(getCtx(), "CannotSetQtyDedicated"); 
    } else {
      MWMSWarehouseTaskMA ma = new MWMSWarehouseTaskMA(this, getM_AttributeSetInstance_ID(), Env.ZERO, qtyToDedicate);
      mas.add(ma);
//      if (!Storage.addQtys(getCtx(), getM_Warehouse_ID(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), Env.ZERO, Env.ZERO, Env.ZERO, qtyToDedicate, Env.ZERO, Env.ZERO, get_TrxName())) {
      if (!MStorage.add(getCtx(), getM_Warehouse_ID(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(),0, Env.ZERO, Env.ZERO, Env.ZERO,  get_TrxName())) {

      ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          return Msg.getMsg(getCtx(), "CannotSetQtyDedicated") + " : " + pp.getName(); 
        return Msg.getMsg(getCtx(), "CannotSetQtyDedicated");
      } 
     // if (!Storage.addQtys(getCtx(), getM_Warehouse_ID(), getM_LocatorTo_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), Env.ZERO, Env.ZERO, Env.ZERO, Env.ZERO, qtyToDedicate, Env.ZERO, get_TrxName())) {
      if (!MStorage.add(getCtx(), getM_Warehouse_ID(), getM_LocatorTo_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(),0, Env.ZERO, Env.ZERO, Env.ZERO, get_TrxName())) {

      ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          return "CannotSetQtyExpected : " + pp.getName(); 
        return Msg.getMsg(getCtx(), "CannotSetQtyExpected");
      } 
    } 
//    if (storagesToSave.size() > 0 && !PO.saveAll(get_TrxName(), storagesToSave))
    if (storagesToSave.size() > 0 && !po.save(get_TrxName()))

    return Msg.getMsg(getCtx(), "CannotSetQtyDedicated"); 
//    if (!PO.saveAll(get_TrxName(), mas))
    if (!po.save(get_TrxName()))

    return Msg.getMsg(getCtx(), "CannotSetQtyDedicated"); 
    setQtyDedicated(getMovementQty());
    if (getC_OrderLine_ID() != 0) {
      String sql = " UPDATE C_OrderLine ol SET QtyDedicated = COALESCE(QtyDedicated, 0) + ? WHERE C_OrderLine_ID = ? ";
    //  if (0 > DB.executeUpdate(get_TrxName(), sql, new Object[] { getMovementQty(), Integer.valueOf(getC_OrderLine_ID()) }))
    	  if (0 >DB.executeUpdateEx(sql, new Object[] { getMovementQty(), Integer.valueOf(getC_OrderLine_ID()) }, get_TrxName()))
    	  return Msg.getMsg(getCtx(), "CannotSetQtyDedicated"); 
    } 
    if (getMFG_WorkOrderComponent_ID() != 0) {
      String updateSQL = "UPDATE M_WorkOrderComponent SET QtyDedicated = COALESCE( QtyDedicated, 0 ) + ? WHERE M_WorkOrderComponent_ID = ? ";
      ArrayList<Object> params1 = new ArrayList();
      params1.add(getMovementQty());
      params1.add(Integer.valueOf(getMFG_WorkOrderComponent_ID()));
      if (0 > DB.executeUpdateEx(updateSQL.toString(), params1.toArray(), get_TrxName()))
    	  
        return Msg.getMsg(getCtx(), "CannotSetQtyDedicated"); 
    } 
    return "";
  }
  
  private String reverseMALines() throws Exception {
    if (isSkipDedicating())
      return ""; 
    MWMSWarehouseTaskMA[] maLines = MWMSWarehouseTaskMA.get(getCtx(), getWMS_WarehouseTask_ID(), get_TrxName());
    if (maLines == null || maLines.length == 0)
      return ""; 
    BigDecimal totalQtyDedicated = Env.ZERO;
    List<MStorageDetail> storagesToSave = new ArrayList<MStorageDetail>();
    for (MWMSWarehouseTaskMA ma : maLines) {
      BigDecimal qtyDedicated = ma.getQtyDedicated();
      totalQtyDedicated = totalQtyDedicated.add(qtyDedicated);
      if (qtyDedicated.compareTo(BigDecimal.ZERO) > 0) {
        MStorageDetail storageDedicatedFrom = MStorageDetail.getForUpdate(getCtx(), getM_Locator_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.DEDICATED, get_Trx());
        storageDedicatedFrom.setQty(storageDedicatedFrom.getQty().subtract(qtyDedicated));
        storagesToSave.add(storageDedicatedFrom);
        MStorageDetail storageExpectedTo = MStorageDetail.getForUpdate(getCtx(), getM_LocatorTo_ID(), getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.EXPECTED, get_Trx());
        storageExpectedTo.setQty(storageExpectedTo.getQty().subtract(qtyDedicated));
        storagesToSave.add(storageExpectedTo);
      } 
    } 
//    if (!PO.saveAll(get_TrxName(), storagesToSave))
    if (!po.save(get_TrxName()))

    return Msg.getMsg(getCtx(), "CannotSetQtyDedicated"); 
    if (getC_OrderLine_ID() != 0) {
      String sql = " UPDATE C_OrderLine ol SET QtyDedicated = COALESCE(QtyDedicated, 0) - ? WHERE C_OrderLine_ID = ? ";
    //  DB.executeUpdate(get_TrxName(), sql, new Object[] { totalQtyDedicated, Integer.valueOf(getC_OrderLine_ID()) });
      DB.executeUpdateEx(sql,  new Object[] { totalQtyDedicated, Integer.valueOf(getC_OrderLine_ID()) }, get_TrxName());
    } 
    if (getMFG_WorkOrderComponent_ID() != 0) {
      String updateSQL = "UPDATE MFG_WorkOrderComponent SET QtyDedicated = COALESCE( QtyDedicated, 0 ) - ? WHERE MFG_WorkOrderComponent_ID = ? ";
      ArrayList<Object> params1 = new ArrayList();
      params1.add(totalQtyDedicated);
      params1.add(Integer.valueOf(getMFG_WorkOrderComponent_ID()));
      DB.executeUpdateEx( updateSQL.toString(), params1.toArray(),get_TrxName());
    } 
   // DB.executeUpdate(get_TrxName(), "DELETE FROM M_WarehouseTaskMA WHERE M_WarehouseTask_ID=?", new Object[] { Integer.valueOf(getWMS_WarehouseTask_ID()) });
    DB.executeUpdateEx( "DELETE FROM WMs_WarehouseTaskMA WHERE WMS_WarehouseTask_ID=?", new Object[] { Integer.valueOf(getWMS_WarehouseTask_ID()) }, get_TrxName());
    setQtyDedicated(Env.ZERO);
    return "";
  }
  
  private ArrayList<MWMSWarehouseTaskMA> checkMaterialPolicy() throws Exception {
    String errorMsg = reverseMALines();
    if (errorMsg != null && errorMsg.length() != 0) {
      this.m_processMsg = errorMsg;
      return null;
    } 
    ArrayList<MWMSWarehouseTaskMA> mas = new ArrayList<MWMSWarehouseTaskMA>();
    if (getM_ActualASI_ID() == 0) {
      String MMPolicy = DB.getSQLValueString(get_TrxName(), " SELECT COALESCE(PC.MMPolicy, CLIENT.MMPolicy)  FROM M_WarehouseTask WT  INNER JOIN M_Product P ON (WT.M_Product_ID = P.M_Product_ID)  INNER JOIN M_Product_Category PC ON (P.M_Product_Category_ID=PC.M_Product_Category_ID)  INNER JOIN AD_Client CLIENT ON (WT.AD_Client_ID=CLIENT.Ad_Client_ID)  WHERE M_WarehouseTask_ID = ? ", new Object[] { Integer.valueOf(getWMS_WarehouseTask_ID()) });
      BigDecimal qtyToDeliver = getMovementQty();
//      List<Storage.Record> storages = Storage.getAllWithASI(getCtx(), getM_Product_ID(), getM_ActualLocator_ID(), X_AD_Client.MMPOLICY_FiFo.equals(MMPolicy), qtyToDeliver, get_TrxName());
      MStorage[] storages = MStorage.getAllWithASI(getCtx(), getM_Product_ID(), getM_ActualLocator_ID(), X_AD_Client.MMPOLICY_FiFo.equals(MMPolicy), get_TrxName());

      for(MStorage storage : storages) {
//      for (int ii = 0; ii < storages.size(); ii++) {
      //  Storage.Record storage = storages.get(ii);
        BigDecimal qtyAvailable = storage.getQtyAvailable();
        if (qtyAvailable.compareTo(BigDecimal.ZERO) > 0) {
          MWMSWarehouseTaskMA ma = new MWMSWarehouseTaskMA(this, storage.getM_AttributeSetInstance_ID(), qtyToDeliver, Env.ZERO);
          if (qtyAvailable.compareTo(qtyToDeliver) >= 0) {
            qtyToDeliver = Env.ZERO;
          } else {
            ma.setMovementQty(qtyAvailable);
            qtyToDeliver = qtyToDeliver.subtract(qtyAvailable);
          } 
          mas.add(ma);
       //   log.fine("#" + ii + ": " + ma + ", QtyToDeliver=" + qtyToDeliver);
          if (qtyToDeliver.signum() == 0)
            break; 
        } 
      } 
      if (qtyToDeliver.signum() != 0) {
//        Storage.Record storageRec = Storage.getCreateRecord(getCtx(), getM_ActualLocator_ID(), getM_Product_ID(), 0, get_TrxName());
          MStorage storageRec = MStorage.getCreate(getCtx(), getM_ActualLocator_ID(), getM_Product_ID(), 0, get_TrxName());

    	  if (storageRec.getQtyAvailable().compareTo(qtyToDeliver) < 0) {
          this.m_processMsg = Msg.translate(getCtx(), "InsufficientQuantity");
          return null;
        } 
        MWMSWarehouseTaskMA ma = new MWMSWarehouseTaskMA(this, storageRec.getDetail(X_Ref_Quantity_Type.ON_HAND).getM_AttributeSetInstance_ID(), qtyToDeliver, Env.ZERO);
        qtyToDeliver = Env.ZERO;
        mas.add(ma);
      } 
    } 
//    if (!PO.saveAll(get_TrxName(), mas)) {
    if (!po.save(get_TrxName())) {

    log.fine("save failed");
      this.m_processMsg = Msg.translate(getCtx(), "InsufficientQuantity");
      return null;
    } 
    return mas;
  }
  
  protected boolean beforeDelete() {
    if (getQtyDedicated().compareTo(BigDecimal.ZERO) != 0) {
      log.saveError("DeleteError", Msg.translate(getCtx(), "QtyDedicated") + "=" + getQtyDedicated());
      return false;
    } 
    return super.beforeDelete();
  }
  
  public String prepareIt() {
    try {
		this.m_processMsg = dedicateStorage();
	} catch (Exception e) {
		
		e.printStackTrace();
	}
    if (this.m_processMsg != null && this.m_processMsg.length() != 0)
      return "IN"; 
    if (!DOCACTION_Complete.equals(getDocAction()))
      setDocAction(DOCACTION_Complete); 
    return "IP";
  }
  
  public boolean reActivateIt() {
    log.info(toString());
    return false;
  }
  
  public boolean rejectIt() {
    log.info(toString());
    setIsApproved(false);
    return true;
  }
  
  public boolean reverseAccrualIt() {
    log.info(toString());
    return false;
  }
  
  public boolean reverseCorrectIt() {
    log.info(toString());
   // this.m_processMsg = DocumentEngine.isPeriodOpen(this);
    if (!MPeriod.isOpen(getCtx(), null, getDocBaseType(), getAD_Org_ID()))
	{
		this.m_processMsg = "@PeriodClosed@";
		
	}
	if (this.m_processMsg != null) {
		log.log(Level.SEVERE, this.m_processMsg);
		return false;
	}
    if (this.m_processMsg != null)
      return false; 
    MWMSWarehouseTask reversal = new MWMSWarehouseTask(getCtx(), 0, get_TrxName());
    copyValues((PO)this, (PO)reversal, getAD_Client_ID(), getAD_Org_ID());
    reversal.set_ValueNoCheck("DocumentNo", null);
    reversal.setDocStatus(DOCSTATUS_Drafted);
    reversal.setDocAction(DOCACTION_Complete);
    reversal.setIsApproved(false);
    reversal.setProcessed(false);
    reversal.addDescription("{->" + getDocumentNo() + ")");
    reversal.setTargetQty(getTargetQty());
    reversal.setQtyEntered(getQtyEntered());
    reversal.setM_Locator_ID(getM_LocatorTo_ID());
    reversal.setM_LocatorTo_ID(getM_Locator_ID());
    reversal.setM_ActualLocator_ID(getM_ActualLocatorTo_ID());
    reversal.setM_ActualLocatorTo_ID(getM_ActualLocator_ID());
    reversal.setReversal(true);
    reversal.setProcessed(false);
    if (!reversal.save()) {
      this.m_processMsg = "Could not create Warehouse Task Reversal";
      return false;
    } 
    reversal.setQtyDedicated(reversal.getQtySuggested());
    DocumentEngine engine = new DocumentEngine(reversal, "CO");
    if(!engine.processIt(getDocAction())) {
    //if (!DocumentEngine.processIt(reversal, "CO")) {
      this.m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
      return false;
    } 
    DocumentEngine dengine = new DocumentEngine(reversal, "CL");
   dengine.processIt(getDocAction());
   // DocumentEngine.processIt(reversal, "CL");
    reversal.setDocStatus(DOCSTATUS_Reversed);
    reversal.setDocAction(DOCACTION_None);
    reversal.save();
    this.m_processMsg = reversal.getDocumentNo();
    addDescription("(" + reversal.getDocumentNo() + "<-)");
    setProcessed(true);
    setDocStatus(DOCSTATUS_Reversed);
    setDocAction(DOCACTION_None);
    return true;
  }
  
  public boolean unlockIt() {
    log.info(toString());
    setProcessing(false);
    return true;
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(desc + " | " + description);
    } 
  }
  
  public boolean voidIt() {
    log.info(toString());
    if (DOCSTATUS_Closed.equals(getDocStatus()) || DOCSTATUS_Reversed.equals(getDocStatus()) || DOCSTATUS_Voided.equals(getDocStatus())) {
      this.m_processMsg = "Document Closed: " + getDocStatus();
      return false;
    } 
    if (DOCSTATUS_Drafted.equals(getDocStatus()) || DOCSTATUS_Invalid.equals(getDocStatus()) || DOCSTATUS_InProgress.equals(getDocStatus()) || DOCSTATUS_Approved.equals(getDocStatus()) || DOCSTATUS_NotApproved.equals(getDocStatus())) {
      BigDecimal old = getQtySuggested();
      if (old.signum() != 0) {
        setTargetQty(Env.ZERO);
        setQtyEntered(Env.ZERO);
        setQtySuggested(Env.ZERO);
        addDescription("Voided (" + old + ")");
        try {
			this.m_processMsg = reverseMALines();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        if (this.m_processMsg != null && this.m_processMsg.length() != 0) {
          this.m_processMsg = "Cannot reverse dedicated Stock (void)";
          return false;
        } 
      } 
    } else {
    	DocumentEngine engine = new DocumentEngine(this, "RC");
    	return engine.processIt(getDocAction());
     // return DocumentEngine.processIt(this, "RC");
    } 
    setProcessed(true);
    setDocAction(DOCACTION_None);
    return true;
  }
  
  public MWMSWarehouseTask splitTask(BigDecimal splitQuantity) {
    BigDecimal targetQty = getTargetQty();
    if (targetQty.compareTo(splitQuantity) <= 0) {
      log.saveError("Error", "SplitQtyExceedsTargetQty");
      return null;
    } 
    if (isProcessed()) {
      log.saveError("Error", "CannotSplitProcessedTasks");
      return null;
    } 
    MWMSWarehouseTask splitTask = new MWMSWarehouseTask(getCtx(), 0, get_TrxName());
    copyValues((PO)this, (PO)splitTask, getAD_Client_ID(), getAD_Org_ID());
    splitTask.set_ValueNoCheck("DocumentNo", null);
    splitTask.setDocStatus(DOCSTATUS_Drafted);
    splitTask.setDocAction(DOCACTION_Prepare);
    splitTask.setIsApproved(false);
    splitTask.setProcessed(false);
    splitTask.addDescription("{Split From ->" + getDocumentNo() + ")");
    splitTask.setTargetQty(splitQuantity);
    splitTask.setQtyDedicated(Env.ZERO);
    splitTask.setM_Locator_ID(getM_Locator_ID());
    splitTask.setM_LocatorTo_ID(getM_LocatorTo_ID());
    splitTask.setWMS_SplitWarehouseTask_ID(getWMS_WarehouseTask_ID());
    splitTask.setReversal(false);
    splitTask.setProcessed(false);
    if (!splitTask.save()) {
      log.saveError("Error", "Could not save split task ");
      return null;
    } 
    setTargetQty(targetQty.subtract(splitQuantity));
    save();
    if (getDocStatus().equals(X_WMS_WarehouseTask.DOCSTATUS_InProgress))
		try {
			dedicateStorage();
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
    if (!save()) {
      log.saveError("Error", "Could not save split task ");
      return null;
    } 
    DocumentEngine engine = new DocumentEngine(splitTask, DOCACTION_Prepare);
    if (getDocStatus().equals(X_WMS_WarehouseTask.DOCSTATUS_InProgress) && !engine.processIt( DOCACTION_Prepare)) {
      log.saveError("Error", "Could not process split task ");
      return null;
    } 
    splitTask.save();
    return splitTask;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("MWMSWarehouseTask[");
    sb.append(get_ID()).append("-").append(getDocumentNo()).append(",Suggested From=").append(getM_Locator_ID()).append(",Suggested To=").append(getM_LocatorTo_ID()).append(",Product=").append(getM_Product_ID()).append(",TargetQuantity=").append(getTargetQty()).append(",ActualQuantity=").append(getQtyEntered()).append(",UOM=").append(getC_UOM_ID()).append(",Quantity Dedicated=").append(getQtyDedicated()).append("]");
    return sb.toString();
  }
  
  private Class<?> getWorkOrderTxnUtilClass() {
    String className = "org.cyprusbrs.cmfg.util.MMFGWorkOrderTxnUtil";
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (Exception e) {
      log.warning("Error getting class for " + className + ": - " + e.toString());
      return null;
    } 
  }
  
  private Object getWorkOrderTxnUtilInstance() {
    Class<?> clazz = getWorkOrderTxnUtilClass();
    if (clazz == null)
      return null; 
    try {
      Constructor<?> constr = clazz.getConstructor(new Class[] { boolean.class });
      Object retValue = constr.newInstance(new Object[] { Boolean.valueOf(true) });
      return retValue;
    } catch (Exception e) {
      log.warning("Error instantiating constructor for org.cyprusbrs.cmfg.util.MMFGWorkOrderTxnUtil:" + e.toString());
      return null;
    } 
  }
  
  private Class<?> getWorkOrderTxnLineMAClass() {
    String className = "org.cyprusbrs.cmfg.model.MMFGWorkOrderTrxLineMA";
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (Exception e) {
      log.warning("Error getting class for " + className + ": - " + e.toString());
      return null;
    } 
  }
  
  private PO getWorkOrderTxnLineMAInstance(Properties ctx, String trx, int M_WorkOrderTransactionLine_ID, int M_AttributeSetInstance_ID, BigDecimal qty) {
    Class<?> clazz = getWorkOrderTxnLineMAClass();
    if (clazz == null)
      return null; 
    try {
      Constructor<?> constr = clazz.getConstructor(new Class[] { Properties.class, Trx.class, int.class, int.class, BigDecimal.class });
      PO retValue = (PO)constr.newInstance(new Object[] { ctx, trx, Integer.valueOf(M_WorkOrderTransactionLine_ID), Integer.valueOf(M_AttributeSetInstance_ID), qty });
      return retValue;
    } catch (Exception e) {
      log.warning("Error instantiating constructor for org.cyprusbrs.cmfg.model.MMFGWorkOrderTxnLineMA:" + e.toString());
      return null;
    } 
  }
  
  private Class<?> getWorkOrderTransactionClass() {
    String className = "org.cyprusbrs.cmfg.model.MWorkOrderTransaction";
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (Exception e) {
      log.warning("Error getting class for " + className + ": - " + e.toString());
      return null;
    } 
  }
  
  private DocAction getWorkOrderTransactionInstance(Properties ctx, int MFG_WorkOrderTransaction_ID, String trx) {
    Class<?> clazz = getWorkOrderTransactionClass();
    if (clazz == null)
      return null; 
    try {
      Constructor<?> constr = clazz.getConstructor(new Class[] { Properties.class, int.class, Trx.class });
      DocAction retValue = (DocAction)constr.newInstance(new Object[] { ctx, Integer.valueOf(MFG_WorkOrderTransaction_ID), trx });
      return retValue;
    } catch (Exception e) {
      log.warning("Error instantiating constructor for org.cyprusbrs.cmfg.model.MMFGWorkOrderTransaction:" + e.toString());
      return null;
    } 
  }
  
  public void setProcessMsg(String processMsg) {
    this.m_processMsg = processMsg;
  }
  
  public Timestamp getDocumentDate() {
    return getMovementDate();
  }
  
  public Env.QueryParams getLineOrgsQueryInfo() {
    return new Env.QueryParams("SELECT L.AD_Org_ID FROM M_Locator L,WMS_WarehouseTask W WHERE W.WMS_WarehouseTask_ID=? AND L.M_Locator_ID = W.M_Locator_ID UNION SELECT L.AD_Org_ID FROM M_Locator L, WMS_WarehouseTask W WHERE W.WMS_WarehouseTask_ID=? AND L.M_Locator_ID = W.M_LocatorTo_ID", new Object[] { Integer.valueOf(getWMS_WarehouseTask_ID()), Integer.valueOf(getWMS_WarehouseTask_ID()) });
  }
  
  public void setSkipDedicating(boolean skipDedicating) {
    this.skipDedicating = skipDedicating;
  }
  
  public boolean isSkipDedicating() {
    return this.skipDedicating;
  }
  
  private static List<MWMSWarehouseTask> getInOutTasks(MInOut shipment, String whereClause) {
    CPreparedStatement cPreparedStatement=null;
    List<MWMSWarehouseTask> list = new ArrayList<MWMSWarehouseTask>();
    String sql = "SELECT * FROM WMS_WarehouseTask w WHERE EXISTS (SELECT 1 FROM M_InOutLine il WHERE il.M_InOut_ID = ? AND il.M_InOutLine_ID = w.M_InOutLine_ID) ";
    if (whereClause != null && whereClause.length() > 0)
      sql = sql + " AND " + whereClause; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, shipment.get_TrxName());
      cPreparedStatement.setInt(1, shipment.getM_InOut_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MWMSWarehouseTask(shipment.getCtx(), rs, shipment.get_TrxName())); 
    } catch (Exception e) {
      log.log(Level.SEVERE, sql, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return list;
  }
  
  private static List<MWMSWarehouseTask> getWorkOrderTasks(X_MFG_WorkOrder xwo, String whereClause) {
    CPreparedStatement cPreparedStatement=null;
    List<MWMSWarehouseTask> list = new ArrayList<MWMSWarehouseTask>();
    String sql = "SELECT * FROM WMS_WarehouseTask w WHERE EXISTS (SELECT 1 FROM MFG_WorkOrderComponent woc WHERE woc.MFG_WorkOrderOperation_ID IN (SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? ) AND woc.MFG_WorkOrderComponent_ID = w.MFG_WorkOrderComponent_ID) ";
    if (whereClause != null && whereClause.length() > 0)
      sql = sql + " AND " + whereClause; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, xwo.get_TrxName());
      cPreparedStatement.setInt(1, xwo.getMFG_WorkOrder_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MWMSWarehouseTask(xwo.getCtx(), rs, xwo.get_TrxName())); 
    } catch (SQLException sqle) {
      log.log(Level.SEVERE, sql, sqle);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return list;
  }
  
  private static List<MWMSWarehouseTask> getOrderTasks(MOrder order, String whereClause) {
    CPreparedStatement cPreparedStatement=null;
    List<MWMSWarehouseTask> list = new ArrayList<MWMSWarehouseTask>();
    String sql = "SELECT * FROM WMS_WarehouseTask w WHERE EXISTS (SELECT 1 FROM C_OrderLine ol WHERE ol.C_Order_ID = ? AND ol.C_OrderLine_ID = w.C_OrderLine_ID) ";
    if (whereClause != null && whereClause.length() > 0)
      sql = sql + " AND " + whereClause; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, order.get_TrxName());
      cPreparedStatement.setInt(1, order.getC_Order_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MWMSWarehouseTask(order.getCtx(), rs, order.get_TrxName())); 
    } catch (Exception e) {
      log.log(Level.SEVERE, sql, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return list;
  }
  
  private static boolean reverseTasks(List<MWMSWarehouseTask> tasks) {
    if (tasks == null || tasks.isEmpty())
      return true; 
    for (MWMSWarehouseTask task : tasks) {
      if (X_WMS_WarehouseTask.DOCSTATUS_Closed.equals(task.getDocStatus()) || X_WMS_WarehouseTask.DOCSTATUS_Reversed.equals(task.getDocStatus()) || X_WMS_WarehouseTask.DOCSTATUS_Voided.equals(task.getDocStatus()))
        continue; 
//      if (!DocumentEngine.processIt(task, "VO"))
      DocumentEngine engine = new DocumentEngine(task, "VO");
      if (!engine.processIt("VO"))

      return false; 
    } 
//    if (!PO.saveAll(((MWMSWarehouseTask)tasks.get(0)).get_TrxName(), tasks))
    if (!po.save(((MWMSWarehouseTask)tasks.get(0)).get_TrxName()))

    return false; 
    return true;
  }
  
  public static boolean reverseInOutTasks(MInOut inout, boolean onlyIncomplete) {
    String whereClause = null;
    if (onlyIncomplete)
      whereClause = " DocStatus IN ('IP','DR') "; 
    List<MWMSWarehouseTask> tasks = getInOutTasks(inout, whereClause);
    return reverseTasks(tasks);
  }
  
  public static boolean reverseOrderTasks(MOrder order, boolean onlyIncomplete) {
    String whereClause = null;
    if (onlyIncomplete)
      whereClause = " DocStatus IN ('IP','DR') "; 
    List<MWMSWarehouseTask> tasks = getOrderTasks(order, whereClause);
    return reverseTasks(tasks);
  }
  
  public static boolean reverseWorkOrderTasks(X_MFG_WorkOrder xwo, boolean onlyIncomplete) {
    String whereClause = null;
    if (onlyIncomplete)
      whereClause = " DocStatus IN ('IP','DR') "; 
    List<MWMSWarehouseTask> tasks = getWorkOrderTasks(xwo, whereClause);
    return reverseTasks(tasks);
  }

@Override
public boolean processIt(String action) throws Exception {
	// TODO Auto-generated method stub
	return false;
}
}

