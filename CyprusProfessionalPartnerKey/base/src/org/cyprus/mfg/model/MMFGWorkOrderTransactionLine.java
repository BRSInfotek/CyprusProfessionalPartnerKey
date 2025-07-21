package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MOrg;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductLocator;
import org.cyprusbrs.framework.MRole;
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

public class MMFGWorkOrderTransactionLine extends X_MFG_WorkOrderTrxLine {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderTransactionLine.class);
  
  private static final long serialVersionUID = 1L;
  
//  public MMFGWorkOrderTransactionLine(Ctx ctx, int M_WorkOrderTransactionLine_ID, Trx trx) {
//    super(ctx, M_WorkOrderTransactionLine_ID, trx);
//    MWorkOrderTransaction wot = new MWorkOrderTransaction(ctx, getM_WorkOrderTransaction_ID(), trx);
//    setClientOrg((PO)wot);
//    if (M_WorkOrderTransactionLine_ID == 0)
//      setProcessed(false); 
//  }
//  
//  public MMFGWorkOrderTransactionLine(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGWorkOrderTransactionLine(Properties ctx, int MFG_WorkOrderTransactionLine_ID, String trxName) {
	    super(ctx, MFG_WorkOrderTransactionLine_ID, trxName);
	    MMFGWorkOrderTransaction wot = new MMFGWorkOrderTransaction(ctx, getMFG_WORKORDERTRANSACTION_ID(), null);
	    setClientOrg((PO)wot);
	    if (MFG_WorkOrderTransactionLine_ID == 0)
	      setProcessed(false); 
	  }
	  
	  public MMFGWorkOrderTransactionLine(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  public void setProduct(MProduct product) {
    if (product != null) {
      setM_Product_ID(product.getM_Product_ID());
      setC_UOM_ID(product.getC_UOM_ID());
    } else {
      setM_Product_ID(0);
      set_ValueNoCheck("C_UOM_ID", null);
    } 
    setM_AttributeSetInstance_ID(0);
  }
  
  public void setM_Product_ID(int M_Product_ID, boolean setUOM) {
    if (setUOM) {
      setProduct(MProduct.get(getCtx(), M_Product_ID));
    } else {
      setM_Product_ID(M_Product_ID);
    } 
    setM_AttributeSetInstance_ID(0);
  }
  
  public void setRequiredColumns(int workOrderTransactionID, int productID, int uomID, BigDecimal qty, int operationID, String basisType) {
    setMFG_WORKORDERTRANSACTION_ID(workOrderTransactionID);
    if (uomID > 0) {
      setM_Product_ID(productID);
      setC_UOM_ID(uomID);
    } else {
      setM_Product_ID(productID, true);
    } 
    setQtyEntered(qty.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4));
    setMFG_WorkOrderOperation_ID(operationID);
    setBasisType(basisType);
    MRole role = MRole.getDefault(getCtx(), false);
    String sql = "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM MFG_WorkOrderTrxLine WHERE MFG_WorkOrderTransaction_ID=?";
    sql = role.addAccessSQL(sql, "MFG_WorkOrderTrxLine", false, false);
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, workOrderTransactionID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        setLine(rs.getInt(1)); 
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        cPreparedStatement = null;
        if (rs != null)
          rs.close(); 
        rs = null;
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
      } 
    } 
  }
  
 // @UICallout
  public void setM_Product_ID(String oldM_Product_ID, String newM_Product_ID, int windowNo) throws Exception {
    if (newM_Product_ID == null || newM_Product_ID.length() == 0) {
      setM_AttributeSetInstance_ID(0);
      return;
    } 
    int M_Product_ID = Integer.parseInt(newM_Product_ID);
    setM_Product_ID(M_Product_ID);
    if (M_Product_ID == 0) {
      setM_AttributeSetInstance_ID(0);
      return;
    } 
//    int M_AttributeSetInstance_ID = getCtx().getContextAsInt(1113, 1113, "M_AttributeSetInstance_ID");
    int M_AttributeSetInstance_ID = Env.getContextAsInt(getCtx(),1113, 1113, "M_AttributeSetInstance_ID");
    if (Env.getContextAsInt(getCtx(),1113, 1113, "M_Product_ID") == M_Product_ID && M_AttributeSetInstance_ID != 0) {
      setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
    } else {
      setM_AttributeSetInstance_ID(0);
    } 
    MProduct product = MProduct.get(getCtx(), M_Product_ID);
    setC_UOM_ID(product.getC_UOM_ID());
  }
  
  public void setM_Locator_ID(BigDecimal Qty) {
    if (getM_Locator_ID() != 0)
      return; 
    if (getM_Product_ID() == 0) {
      set_ValueNoCheck("M_Locator_ID", null);
      return;
    } 
    MOrg org = new MOrg(getCtx(), getAD_Org_ID(), get_TrxName());
//    int M_Locator_ID = MStorage.getLocatorID(org.getM_Warehouse_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), Qty, get_TrxName());
    int M_Locator_ID = MStorage.getM_Locator_ID(org.getM_Warehouse_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), Qty, get_TrxName());

    if (M_Locator_ID == 0) {
      MProduct product = MProduct.get(getCtx(), getM_Product_ID());
      M_Locator_ID = MProductLocator.getFirstM_Locator_ID(product, org.getM_Warehouse_ID());
      if (M_Locator_ID == 0) {
        MWarehouse wh = MWarehouse.get(getCtx(), org.getM_Warehouse_ID());
        M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
      } 
    } 
    setM_Locator_ID(M_Locator_ID);
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(desc + " | " + description);
    } 
  }
  
  protected boolean beforeDelete() {
    MMFGWorkOrderTransaction workOrderTxn = new MMFGWorkOrderTransaction(getCtx(), getMFG_WORKORDERTRANSACTION_ID(), get_TrxName());
    if (workOrderTxn.isProcessed())
      return false; 
    if (isProcessed())
      return false; 
    return true;
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getLine() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@Line@ < 0"));
      return false;
    } 
    if (is_ValueChanged("QtyEntered")) {
      BigDecimal qtyEntered = getQtyEntered().setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
      if (qtyEntered.compareTo(getQtyEntered()) != 0) {
        log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered =" + getQtyEntered() + "->" + qtyEntered);
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

