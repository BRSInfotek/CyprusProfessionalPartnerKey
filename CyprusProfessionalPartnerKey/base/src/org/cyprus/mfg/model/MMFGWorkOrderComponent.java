package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class MMFGWorkOrderComponent extends X_MFG_WorkOrderComponent {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderComponent.class);
  
  private static final long serialVersionUID = 1L;
  
  private MMFGWorkOrderOperation headerInfo;
  
//  public MMFGWorkOrderComponent(Ctx ctx, int MFG_WorkOrderComponent_ID, Trx trx) {
//    super(ctx, MFG_WorkOrderComponent_ID, trx);
//    this.headerInfo = null;
//    if (MFG_WorkOrderComponent_ID == 0) {
//      setLine(0);
//      setM_AttributeSetInstance_ID(0);
//      setProcessed(false);
//      setQtyRequired(Env.ZERO);
//      setQtyAvailable(Env.ZERO);
//      setQtySpent(Env.ZERO);
//      setQtyAllocated(Env.ZERO);
//      setQtyDedicated(Env.ZERO);
//    } 
//  }
  
  public MMFGWorkOrderComponent(Properties ctx, int MFG_WorkOrderComponent_ID, String trxName) {
	    super(ctx, MFG_WorkOrderComponent_ID, trxName);
	    this.headerInfo = null;
	    if (MFG_WorkOrderComponent_ID == 0) {
	      setLine(0);
	      setM_AttributeSetInstance_ID(0);
	      setProcessed(false);
	      setQtyRequired(Env.ZERO);
	      setQtyAvailable(Env.ZERO);
	      setQtySpent(Env.ZERO);
	      setQtyAllocated(Env.ZERO);
	      setQtyDedicated(Env.ZERO);
	    } 
	  }
  
  public MMFGWorkOrderComponent(MMFGWorkOrderOperation workorderoperation, MMFGWorkOrder workorder) {
    this(workorderoperation.getCtx(), 0, workorderoperation.get_TrxName());
    if (workorderoperation.get_ID() == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_WorkOrderOperation_ID(workorderoperation.getMFG_WorkOrderOperation_ID());
    setWorkOrder(workorder);
  }
  
  public MMFGWorkOrderComponent(MMFGWorkOrder workorder, MMFGWorkOrderOperation workorderoperation, MProduct product, BigDecimal QtyRequired, String SupplyType) {
    this(workorder, workorderoperation, product, QtyRequired, SupplyType, (MLocator)null);
  }
  
  public MMFGWorkOrderComponent(MMFGWorkOrder workorder, MMFGWorkOrderOperation workorderoperation, MProduct product, BigDecimal QtyRequired, String SupplyType, MLocator locator) {
    this(workorderoperation.getCtx(), 0, workorderoperation.get_TrxName());
    if (workorderoperation.get_ID() == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_WorkOrderOperation_ID(workorderoperation.getMFG_WorkOrderOperation_ID());
    setM_Product_ID(product.getM_Product_ID());
    setC_UOM_ID(product.getC_UOM_ID());
    setQtyRequired(QtyRequired);
    setSupplyType(SupplyType);
    if (locator != null)
      setM_Locator_ID(locator.getM_Locator_ID()); 
    setWorkOrder(workorder);
  }
  
//  public MMFGWorkOrderComponent(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//    this.headerInfo = null;
//  }
  
  public MMFGWorkOrderComponent(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	    this.headerInfo = null;
	  }
  
  private static CLogger s_log = CLogger.getCLogger(MMFGWorkOrderComponent.class);
  
  public static MMFGWorkOrderComponent[] getOfWorkOrder(MMFGWorkOrder workorder, String whereClause, String orderClause) {
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderComponent WHERE MFG_WorkOrder_ID=? ");
    if (whereClause != null)
      sqlstmt.append("AND ").append(whereClause); 
    if (orderClause != null)
      sqlstmt.append(" ORDER BY ").append(orderClause); 
    String sql = sqlstmt.toString();
    ArrayList<MMFGWorkOrderComponent> list = new ArrayList<MMFGWorkOrderComponent>();
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, workorder.get_Trx());
    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, workorder.get_TrxName());
    ResultSet rs = null;
    try {
      cPreparedStatement.setInt(1, workorder.getMFG_WorkOrder_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMFGWorkOrderComponent(workorder.getCtx(), rs, workorder.get_TrxName())); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null;
        cPreparedStatement = null;
    } 
    MMFGWorkOrderComponent[] retValue = new MMFGWorkOrderComponent[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  public static MMFGWorkOrderComponent[] getOfWorkOrderOperation(MMFGWorkOrderOperation workorderoperation, String whereClause, String orderClause) {
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderComponent WHERE MFG_WorkOrderOperation_ID=? ");
    if (whereClause != null)
      sqlstmt.append("AND ").append(whereClause); 
    if (orderClause != null)
      sqlstmt.append(" ORDER BY ").append(orderClause); 
    String sql = sqlstmt.toString();
    ArrayList<MMFGWorkOrderComponent> list = new ArrayList<MMFGWorkOrderComponent>();
    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, workorderoperation.get_TrxName());
    ResultSet rs = null;
    try {
      cPreparedStatement.setInt(1, workorderoperation.getMFG_WorkOrderOperation_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMFGWorkOrderComponent(workorderoperation.getCtx(), rs, workorderoperation.get_TrxName())); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null;
        cPreparedStatement = null;
    } 
    MMFGWorkOrderComponent[] retValue = new MMFGWorkOrderComponent[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  private void setWorkOrder(MMFGWorkOrder workorder) {
    setClientOrg((PO)workorder);
    setC_BPartner_ID(workorder.getC_BPartner_ID());
    setC_BPartner_Location_ID(workorder.getC_BPartner_Location_ID());
    if (!getSupplyType().equals(SUPPLYTYPE_Push) && getM_Locator_ID() == 0)
    //  setM_Locator_ID(MWarehouse.get(getCtx(), workorder.getM_Warehouse_ID()).getDefaultM_Locator_ID());
        setM_Locator_ID(MWarehouse.get(getCtx(), workorder.getM_Warehouse_ID()).getDefaultLocator().getM_Locator_ID()); 

  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getLine() == 0) {
      String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM MFG_WorkOrderComponent WHERE MFG_WorkOrderOperation_ID=?";
      int ii = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getMFG_WorkOrderOperation_ID()) });
      setLine(ii);
    } 
    if (newRecord) {
      String sql = "SELECT * from MFG_WorkOrderComponent WHERE M_Product_ID=? and MFG_WorkOrderOperation_ID = ?";
      CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, null);
      ResultSet rs = null;
      boolean success = true;
      try {
        cPreparedStatement.setInt(1, getM_Product_ID());
        cPreparedStatement.setInt(2, getMFG_WorkOrderOperation_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.getMsg(getCtx(), "DuplicateComponent"));
          success = false;
        } 
        rs.close();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
      } finally {
//        DB.closeResultSet(rs);
//        DB.closeStatement((Statement)cPreparedStatement);
    	  DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null;
          cPreparedStatement = null;
      } 
      if (!success)
        return false; 
    } 
    if (getQtyRequired().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@QtyRequired@ < 0"));
      return false;
    } 
    if (getLine() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@Line@ < 0"));
      return false;
    } 
    if (is_ValueChanged("QtyRequired")) {
      BigDecimal qtyRequired = getQtyRequired().setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
      if (qtyRequired.compareTo(getQtyRequired()) != 0) {
        log.fine("Corrected QtyRequired Scale UOM=" + getC_UOM_ID() + "; QtyRequired=" + getQtyRequired() + "->" + qtyRequired);
        setQtyRequired(qtyRequired);
      } 
    } 
    return true;
  }
  
 // @UICallout
  public void setM_Product_ID(String oldM_Product_ID, String newM_Product_ID, int windowNo) throws Exception {
    if (newM_Product_ID == null || newM_Product_ID.length() == 0) {
      set_ValueNoCheck("C_UOM_ID", null);
      return;
    } 
    int M_Product_ID = Integer.parseInt(newM_Product_ID);
    if (M_Product_ID == 0) {
      set_ValueNoCheck("C_UOM_ID", null);
      return;
    } 
    MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);
    setC_UOM_ID(product.getC_UOM_ID());
  }
  
  public void setHeaderInfo(MMFGWorkOrderOperation headerInfo) {
    this.headerInfo = headerInfo;
  }
  
  public MMFGWorkOrderOperation getHeaderInfo() {
    return this.headerInfo;
  }
  
 // @UICallout
  public void setQtyRequired(String oldQtyRequired, String newQtyRequired, int windowNo) throws Exception {
    if (newQtyRequired == null || newQtyRequired.trim().length() == 0)
      return; 
    if (getC_UOM_ID() == 0)
      return; 
    BigDecimal QtyRequired = new BigDecimal(newQtyRequired);
    BigDecimal QtyRequired1 = QtyRequired.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (QtyRequired.compareTo(QtyRequired1) != 0) {
      log.fine("Corrected QtyRequired Scale UOM=" + getC_UOM_ID() + "; QtyRequired=" + QtyRequired + "->" + QtyRequired1);
      QtyRequired = QtyRequired1;
      setQtyRequired(QtyRequired);
    } 
  }
}

