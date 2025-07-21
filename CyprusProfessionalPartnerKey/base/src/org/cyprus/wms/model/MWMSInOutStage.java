package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Msg;

public class MWMSInOutStage extends X_WMS_InOutStage {
  private static final CLogger log = CLogger.getCLogger(MWMSInOutStage.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSInOutStage(Properties ctx, int M_InOutStage_ID, String trx) {
    super(ctx, M_InOutStage_ID, trx);
  }
  
  public MWMSInOutStage(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
 // @UICallout
  public void setM_Product_ID(String oldM_Product_ID, String newM_Product_ID, int windowNo) throws Exception {
    if (newM_Product_ID == null || newM_Product_ID.length() == 0)
      return; 
    int M_Product_ID = Integer.parseInt(newM_Product_ID);
    if (M_Product_ID == 0)
      return; 
    setM_Product_ID(M_Product_ID);
    int C_Order_ID = getC_Order_ID();
    if (C_Order_ID == 0)
      return; 
    MOrder order = new MOrder(getCtx(), C_Order_ID, get_TrxName());
    MOrderLine[] lines = order.getLines(M_Product_ID, "QtyOrdered > QtyDelivered", "QtyOrdered - QtyDelivered");
    if (lines == null || lines.length == 0) {
//      this.p_changeVO.addError(Msg.getMsg(getCtx(), "ProductNotOnOrder"));
    	log.saveError(Msg.getMsg(getCtx(), "ProductNotOnOrder"),"");

    	return;
    } 
    setC_UOM_ID(lines[0].getC_UOM_ID());
  }
  
 // @UICallout
  public void setM_Warehouse_ID(String oldM_Warehouse_ID, String newM_Warehouse_ID, int windowNo) throws Exception {
    if (newM_Warehouse_ID == null || newM_Warehouse_ID.length() == 0)
      return; 
    int M_Warehouse_ID = Integer.parseInt(newM_Warehouse_ID);
    if (M_Warehouse_ID == 0)
      return; 
    setM_Warehouse_ID(M_Warehouse_ID);
    MWarehouse wh = MWarehouse.get(getCtx(), M_Warehouse_ID);
//    setM_Locator_ID(wh.getM_RcvLocator_ID());
    setM_Locator_ID(wh.getDefaultLocator().getM_Locator_ID());
  }
  
 // @UICallout
  public void setC_Order_ID(String oldC_Order_ID, String newC_Order_ID, int windowNo) throws Exception {
    if (newC_Order_ID == null || newC_Order_ID.length() == 0)
      return; 
    int C_Order_ID = Integer.parseInt(newC_Order_ID);
    if (C_Order_ID == 0)
      return; 
    setC_Order_ID(C_Order_ID);
    int M_Product_ID = getM_Product_ID();
    if (M_Product_ID == 0)
      return; 
    MOrder order = new MOrder(getCtx(), C_Order_ID, get_TrxName());
    MOrderLine[] lines = order.getLines(M_Product_ID, "QtyOrdered > QtyDelivered", "QtyOrdered - QtyDelivered");
    if (lines == null || lines.length == 0) {
    //  this.p_changeVO.addError(Msg.getMsg(getCtx(), "ProductNotOnOrder"));
    	log.saveError(Msg.getMsg(getCtx(), "ProductNotOnOrder"),"");
      return;
    } 
    setC_UOM_ID(lines[0].getC_UOM_ID());
  }
  
//  @UICallout
  public void setC_DocType_ID(String oldC_DocType_ID, String newC_DocType_ID, int windowNo) throws Exception {
    if (newC_DocType_ID == null || newC_DocType_ID.length() == 0)
      return; 
    int C_DocType_ID = Integer.parseInt(newC_DocType_ID);
    if (C_DocType_ID == 0)
      return; 
    MDocType docType = MDocType.get(getCtx(), C_DocType_ID);
    setIsSOTrx(docType.isSOTrx());
    setIsReturnTrx(docType.isReturnTrx());
  }
  
  private String createFromOrder() {
    MOrder order = new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
    MInOut inout = new MInOut(order, 0, getMovementDate());
    inout.setC_DocType_ID(getC_DocType_ID());
    if (!inout.save())
      return "Could not create receipt"; 
    //have to be check @anil16122021
    MOrderLine[] oLines = order.getLines(null," QtyOrdered - QtyDelivered, Line ");
    for (MOrderLine element : oLines) {
      boolean partiallyReceived = (element.getQtyDelivered().compareTo(BigDecimal.ZERO) != 0);
      BigDecimal MovementQty = element.getQtyOrdered().subtract(element.getQtyDelivered());
      if (MovementQty.signum() != 0 && element.getQtyEntered().compareTo(BigDecimal.ZERO) != 0) {
        MInOutLine iol = null;
        iol = new MInOutLine(inout);
        iol.setM_Product_ID(element.getM_Product_ID(), element.getC_UOM_ID());
        iol.setM_Locator_ID(getM_Locator_ID());
        if (!partiallyReceived && element.getQtyEntered().compareTo(element.getQtyOrdered()) != 0) {
          iol.setQtyEntered(element.getQtyEntered().multiply(MovementQty).divide(element.getQtyOrdered(), 12, 4));
          iol.setC_UOM_ID(element.getC_UOM_ID());
        } else {
          iol.setQtyEntered(MovementQty);
          MProduct product = MProduct.get(getCtx(), element.getM_Product_ID());
          iol.setC_UOM_ID(product.getC_UOM_ID());
        } 
        iol.setMovementQty(MovementQty);
        element.setQtyDelivered(element.getQtyDelivered().add(MovementQty));
        iol.setC_OrderLine_ID(element.getC_OrderLine_ID());
        iol.setM_AttributeSetInstance_ID(element.getM_AttributeSetInstance_ID());
        iol.setDescription(element.getDescription());
        iol.setC_Project_ID(element.getC_Project_ID());
        iol.setC_ProjectPhase_ID(element.getC_ProjectPhase_ID());
        iol.setC_ProjectTask_ID(element.getC_ProjectTask_ID());
        iol.setC_Activity_ID(element.getC_Activity_ID());
        iol.setC_Campaign_ID(element.getC_Campaign_ID());
        iol.setAD_OrgTrx_ID(element.getAD_OrgTrx_ID());
        iol.setUser1_ID(element.getUser1_ID());
        iol.setUser2_ID(element.getUser2_ID());
        if (!iol.save())
          return "Could not create line"; 
      } 
    } 
    String docAction = getCreateInOutDocAction();
    if (docAction != null && docAction.length() > 0) {
      inout.setDocAction(docAction);
      DocumentEngine engine = new DocumentEngine ((DocAction)inout, docAction);
	  engine.processIt (docAction);
     // DocumentEngine.processIt((DocAction)inout, docAction);
    } 
    if (!inout.save())
      return "Could not process Receipt"; 
    setM_InOut_ID(inout.getM_InOut_ID());
    setIsInOutCreated(X_WMS_InOutStage.ISINOUTCREATED_Yes);
    setProcessed(true);
    return "";
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
	
    if (newRecord || is_ValueChanged("M_Warehouse_ID") || is_ValueChanged("M_Locator_ID")) {
      MLocator loc = new MLocator(getCtx(), getM_Locator_ID(), get_TrxName());
      if (loc.getM_Warehouse_ID() != getM_Warehouse_ID()) {
        log.saveError("WarehouseLocatorMismatch", "");
        return false;
      } 
    } 
    if (newRecord || is_ValueChanged("C_Order_ID") || is_ValueChanged("M_Product_ID")) {
      int C_Order_ID = getC_Order_ID();
      if (C_Order_ID == 0) {
        log.saveError("OrderMandatory", "");
        return false;
      } 
      int M_Product_ID = getM_Product_ID();
      if (M_Product_ID != 0) {
        setIsCreateOnSave(false);
        MOrder order = new MOrder(getCtx(), C_Order_ID, get_TrxName());
        MOrderLine[] lines = order.getLines(M_Product_ID, "QtyOrdered > QtyDelivered", "QtyOrdered - QtyDelivered");
        if (lines == null || lines.length == 0) {
          log.saveError("ProductNotOnOrder", "");
          return false;
        } 
      } 
    } 
    if (isCreateOnSave() && !isProcessed() && getM_Product_ID() == 0) {
      String errorMsg = createFromOrder();
      if (errorMsg != null && errorMsg.length() != 0) {
        log.saveError("CreateReceiptFailed", errorMsg);
        setErrorMsg(errorMsg);
        return false;
      } 
    } 
    return super.beforeSave(newRecord);
  }
}

