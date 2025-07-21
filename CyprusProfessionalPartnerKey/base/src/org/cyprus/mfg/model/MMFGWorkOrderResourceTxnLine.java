package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Msg;

public class MMFGWorkOrderResourceTxnLine extends X_MFG_WorkOrderResTxnLine {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderResourceTxnLine.class);
  
  private static final long serialVersionUID = 1L;
  
//  public MMFGWorkOrderResourceTxnLine(Ctx ctx, int MFG_WorkOrderResTxnLine_ID, Trx trx) {
//    super(ctx, MFG_WorkOrderResTxnLine_ID, trx);
//  }
//  
//  public MWorkOrderResourceTxnLine(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGWorkOrderResourceTxnLine(Properties ctx, int MFG_WorkOrderResTxnLine_ID, String trxName) {
	    super(ctx, MFG_WorkOrderResTxnLine_ID, trxName);
	  }
	  
	  public MMFGWorkOrderResourceTxnLine(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  //@UICallout
  public void setM_Product_ID(String oldM_Product_ID, String newM_Product_ID, int windowNo) throws Exception {
    if (newM_Product_ID == null || newM_Product_ID.length() == 0) {
      set_ValueNoCheck("C_UOM_ID", null);
      set_ValueNoCheck("BasisType", null);
      return;
    } 
    int M_Product_ID = Integer.parseInt(newM_Product_ID);
    if (0 == M_Product_ID) {
      set_ValueNoCheck("C_UOM_ID", null);
      set_ValueNoCheck("BasisType", null);
      return;
    } 
//    MProduct product = new MProduct(getCtx(), M_Product_ID, get_Trx());
    MProduct product = new MProduct(getCtx(), M_Product_ID, get_TrxName());
    setC_UOM_ID(product.getC_UOM_ID());
//    setBasisType(product.getBasisType());
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(desc + " | " + description);
    } 
  }
  
  public void setresourceinfo(MMFGWorkOrderResource wor) {
    setM_Product_ID(wor.getM_Product_ID());
    setBasisType(wor.getBasisType());
    setC_UOM_ID(wor.getC_UOM_ID());
    setDescription(wor.getDescription());
    setMFG_WorkOrderOperation_ID(wor.getMFG_WorkOrderOperation_ID());
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getSeqNo() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SeqNo@ < 0"));
      return false;
    } 
    if (getC_UOM_ID() == 0)
      return false; 
    if (is_ValueChanged("QtyEntered")) {
      BigDecimal qtyEntered = getQtyEntered().setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
      if (qtyEntered.compareTo(getQtyEntered()) != 0) {
        log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered=" + getQtyEntered() + "->" + qtyEntered);
        setQtyEntered(qtyEntered);
      } 
    } 
    return true;
  }
  
  //@UICallout
  public void setQtyEntered(String oldQtyEntered, String newQtyEntered, int windowNo) throws Exception {
    if (newQtyEntered == null || newQtyEntered.trim().length() == 0)
      return; 
    if (getC_UOM_ID() == 0)
      return; 
    BigDecimal QtyEntered = new BigDecimal(newQtyEntered);
    BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (QtyEntered.compareTo(QtyEntered1) != 0) {
      log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
      QtyEntered = QtyEntered1;
      setQtyEntered(QtyEntered);
    } 
  }
}

