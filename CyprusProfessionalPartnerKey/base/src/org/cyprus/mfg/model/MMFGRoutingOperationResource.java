package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class MMFGRoutingOperationResource extends X_MFG_RoutingOptResource {
  private static final CLogger log = CLogger.getCLogger(MMFGRoutingOperationResource.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static final CLogger s_log = CLogger.getCLogger(MMFGRoutingOperationResource.class);
  
//  public MMFGRoutingOperationResource(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGRoutingOperationResource(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
//  public MRoutingOperationResource(Ctx ctx, int M_RoutingOperationResource_ID, Trx trx) {
//    super(ctx, M_RoutingOperationResource_ID, trx);
//  }
  
  public MMFGRoutingOperationResource(Properties ctx, int MFG_RoutingOperationResource_ID, String trxName) {
	    super(ctx, MFG_RoutingOperationResource_ID, trxName);
	  }
  
  public MMFGRoutingOperationResource(MMFGRoutingOperation operation, int M_Product_ID, int C_UOM_ID, BigDecimal QtyRequired, String BasisType, String ChargeType, int SeqNo, boolean isActive) {
//    this(operation.getCtx(), 0, operation.get_Trx());
	  this(operation.getCtx(), 0, operation.get_TrxName());
    if (operation.getMFG_RoutingOperation_ID() == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_RoutingOperation_ID(operation.getMFG_RoutingOperation_ID());
    setM_Product_ID(M_Product_ID);
    setClientOrg(operation.getAD_Client_ID(), operation.getAD_Org_ID());
    setC_UOM_ID(C_UOM_ID);
    setQtyRequired(QtyRequired);
    setBasisType(BasisType);
    setChargeType(ChargeType);
    setSeqNo(SeqNo);
    setIsActive(isActive);
  }
  
 // @UICallout
  public void setM_Product_ID(String oldM_Product_ID, String newM_Product_ID, int windowNo) throws Exception {
    if (newM_Product_ID == null || newM_Product_ID.length() == 0) {
      set_ValueNoCheck("C_UOM_ID", null);
      return;
    } 
    int M_Product_ID = Integer.parseInt(newM_Product_ID);
    if (0 == M_Product_ID) {
      set_ValueNoCheck("C_UOM_ID", null);
      return;
    } 
    MProduct product = new MProduct(getCtx(), M_Product_ID, get_TrxName());
    setC_UOM_ID(product.getC_UOM_ID());
//    setBasisType(product.getBasisType());
//    setChargeType(product.getChargeType());
  }
  
  public static MMFGRoutingOperationResource[] getResourceLines(MMFGRoutingOperation operation) {
    String sql = "SELECT * FROM MFG_RoutingOptResource WHERE MFG_RoutingOperation_ID=? AND IsActive='Y' ORDER BY SeqNo";
    ArrayList<MMFGRoutingOperationResource> list = new ArrayList<MMFGRoutingOperationResource>();
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, operation.get_Trx());
    CPreparedStatement cPreparedStatement =null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, operation.get_TrxName());
      cPreparedStatement.setInt(1, operation.getMFG_RoutingOperation_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMFGRoutingOperationResource(operation.getCtx(), rs, operation.get_TrxName())); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	 DB.close(rs);
         DB.close((Statement)cPreparedStatement);
         rs = null;
         cPreparedStatement = null;
    	
    } 
    MMFGRoutingOperationResource[] retValue = new MMFGRoutingOperationResource[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  public static MMFGRoutingOperationResource[] getOperationResource(MMFGRoutingOperation op) {
    ArrayList<MMFGRoutingOperationResource> list = new ArrayList<MMFGRoutingOperationResource>();
    String sql = "SELECT * FROM MFG_RoutingOptResource  WHERE MFG_RoutingOperation_ID = ?  AND AD_Org_ID = ?  AND AD_Client_ID = ? ";
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, op.get_Trx());
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, op.get_TrxName());
      cPreparedStatement.setInt(1, op.getMFG_RoutingOperation_ID());
      cPreparedStatement.setInt(2, op.getAD_Org_ID());
      cPreparedStatement.setInt(3, op.getAD_Client_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MMFGRoutingOperationResource res = new MMFGRoutingOperationResource(op.getCtx(), rs, null);
        list.add(res);
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql.toString(), e);
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	
    	  DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null;
          cPreparedStatement = null;
    } 
    MMFGRoutingOperationResource[] retVal = new MMFGRoutingOperationResource[list.size()];
    list.toArray(retVal);
    return retVal;
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getSeqNo() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SeqNo@ < 0"));
      return false;
    } 
    boolean isDuplicate = false;
    if (newRecord) {
      String str = "SELECT * FROM MFG_RoutingOptResource WHERE M_Product_ID=? AND MFG_RoutingOperation_ID=?";
      CPreparedStatement cPreparedStatement = null;
      ResultSet rs = null;
      try {
    	  cPreparedStatement = DB.prepareStatement(str, null);
        cPreparedStatement.setInt(1, getM_Product_ID());
        cPreparedStatement.setInt(2, getMFG_RoutingOperation_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next())
          isDuplicate = true; 
      } catch (SQLException e) {
        log.log(Level.SEVERE, str, e);
      } finally {
//        DB.closeResultSet(rs);
//        DB.closeStatement((Statement)cPreparedStatement);
    	  DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null;
          cPreparedStatement= null;
      } 
    } 
    if (isDuplicate) {
      log.saveError("Error", Msg.getMsg(getCtx(), "DuplicateResource"));
      return false;
    } 
    String sql = "SELECT MFG_RoutingOptResource_ID FROM MFG_RoutingOptResource WHERE SeqNo = ? AND MFG_RoutingOperation_ID = ?";
    int MFG_RoutingOperationResource_ID = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getSeqNo()), Integer.valueOf(getMFG_RoutingOperation_ID()) });
    if (MFG_RoutingOperationResource_ID != -1 && MFG_RoutingOperationResource_ID != getMFG_RoutingOptResource_ID()) {
      log.saveError("DuplicateSequence", Msg.translate(getCtx(), Integer.toString(getSeqNo())));
      return false;
    } 
    if (getQtyRequired().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@QtyRequired@ < 0"));
      return false;
    } 
    if (is_ValueChanged("QtyRequired")) {
      BigDecimal qtyRequired = getQtyRequired().setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
      if (qtyRequired.compareTo(getQtyRequired()) != 0) {
        log.fine("Corrected QtyRequired Scale UOM=" + getC_UOM_ID() + "; QtyRequired =" + getQtyRequired() + "->" + qtyRequired);
        setQtyRequired(qtyRequired);
      } 
    } 
    return true;
  }
  
  //@UICallout
  public void setQtyRequired(String oldQtyRequired, String newQtyRequired, int windowNo) throws Exception {
    if (newQtyRequired == null || newQtyRequired.trim().length() == 0)
      return; 
    if (getC_UOM_ID() == 0)
      return; 
    BigDecimal QtyRequired = new BigDecimal(newQtyRequired);
    BigDecimal QtyRequired1 = QtyRequired.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (QtyRequired.compareTo(QtyRequired1) != 0) {
      log.fine("Corrected QtyRequired Scale UOM=" + getC_UOM_ID() + "; QtyRequired =" + QtyRequired + "->" + QtyRequired1);
      QtyRequired = QtyRequired1;
      setQtyRequired(QtyRequired);
    } 
  }
}

