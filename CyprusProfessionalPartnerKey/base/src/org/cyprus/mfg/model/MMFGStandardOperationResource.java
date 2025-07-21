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

public class MMFGStandardOperationResource extends X_MFG_StandardOptResource {
  private static final CLogger log = CLogger.getCLogger(MMFGStandardOperationResource.class);
  
  private static final long serialVersionUID = 1L;
  
  protected static CLogger s_log = CLogger.getCLogger(MMFGStandardOperationResource.class);
  
//  public MMFGStandardOperationResource(Properties ctx, int MFG_StandardOperationResource_ID, Trx trx) {
//    super(ctx, M_StandardOperationResource_ID, trx);
//  }
//  
//  public MMFGStandardOperationResource(Properties ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGStandardOperationResource(Properties ctx, int MFG_StandardOperationResource_ID, String trxName) {
	    super(ctx, MFG_StandardOperationResource_ID, trxName);
	  }
	  
	  public MMFGStandardOperationResource(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  public static MMFGStandardOperationResource[] getResourceLines(MMFGStandardOperation operation) {
    String sql = "SELECT * FROM MFG_StandardOperationResource WHERE MFG_StandardOperation_ID=? AND IsActive='Y' ORDER BY SeqNo";
    ArrayList<MMFGStandardOperationResource> list = new ArrayList<MMFGStandardOperationResource>();
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, operation.get_Trx());
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, operation.get_TrxName());
      cPreparedStatement.setInt(1, operation.getMFG_StandardOperation_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMFGStandardOperationResource(operation.getCtx(), rs, operation.get_TrxName())); 
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
    MMFGStandardOperationResource[] retValue = new MMFGStandardOperationResource[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  //@UICallout
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
  
  protected boolean beforeSave(boolean newRecord) {
    if (getSeqNo() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SeqNo@ < 0"));
      return false;
    } 
    if (newRecord) {
      String str = "SELECT * from MFG_StandardOperationResource WHERE M_Product_ID=? and MFG_StandardOperation_ID = ?";
//      CPreparedStatement cPreparedStatement = DB.prepareStatement(str, (Trx)null);
      CPreparedStatement cPreparedStatement = null;
      ResultSet rs = null;
      boolean success = true;
      try {
    	  cPreparedStatement = DB.prepareStatement(str, null);
        cPreparedStatement.setInt(1, getM_Product_ID());
        cPreparedStatement.setInt(2, getMFG_StandardOperation_ID());
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          log.saveError("Error", Msg.getMsg(getCtx(), "DuplicateResource"));
          success = false;
        } 
        rs.close();
        cPreparedStatement.close();
      } catch (SQLException e) {
        log.log(Level.SEVERE, str, e);
        return false;
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
    String sql = "SELECT MFG_StandardOperationResource_ID FROM MFG_StandardOperationResource WHERE SeqNo = ? AND MFG_StandardOperation_ID = ?";
    int MFG_StandardOperationResource_ID = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getSeqNo()), Integer.valueOf(getMFG_StandardOperation_ID()) });
    if (MFG_StandardOperationResource_ID != -1 && MFG_StandardOperationResource_ID != getMFG_StandardOptResource_ID()) {
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
        log.fine("Corrected QtyRequired Scale UOM=" + getC_UOM_ID() + "; QtyRequired=" + getQtyRequired() + "->" + qtyRequired);
        setQtyRequired(qtyRequired);
      } 
    } 
    return true;
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
