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
import org.cyprusbrs.util.Msg;

public class MMFGWorkOrderResource extends X_MFG_WorkOrderResource {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderResource.class);
  
  private static final long serialVersionUID = 1L;
  
  private static final CLogger s_log = CLogger.getCLogger(MMFGWorkOrderResource.class);
  
  private MMFGWorkOrderOperation headerInfo;
  
//  public MMFGWorkOrderResource(Ctx ctx, int MFG_WorkOrderResource_ID, Trx trx) {
//    super(ctx, MFG_WorkOrderResource_ID, trx);
//  }
//  
//  public MMFGWorkOrderResource(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGWorkOrderResource(Properties ctx, int MFG_WorkOrderResource_ID, String trxName) {
	    super(ctx, MFG_WorkOrderResource_ID, trxName);
	  }
	  
	  public MMFGWorkOrderResource(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
  
  public MMFGWorkOrderResource(Properties ctx, String trxName, MMFGWorkOrderOperation workOrderOpLine, int M_Product_ID, int C_UOM_ID, BigDecimal QtyRequired, String BasisType, String ChargeType, int SeqNo, boolean isActive) {
    this(ctx, 0, trxName);
    if (workOrderOpLine.getMFG_WorkOrderOperation_ID() == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_WorkOrderOperation_ID(workOrderOpLine.getMFG_WorkOrderOperation_ID());
    setClientOrg(workOrderOpLine.getAD_Client_ID(), workOrderOpLine.getAD_Org_ID());
    setM_Product_ID(M_Product_ID);
    setC_UOM_ID(C_UOM_ID);
    setQtyRequired(QtyRequired);
    setBasisType(BasisType);
    setChargeType(ChargeType);
    setSeqNo(SeqNo);
    setIsActive(isActive);
  }
  
  public MMFGWorkOrderResource(Properties ctx, String trxName, MMFGWorkOrderOperation workOrderOpLine, MMFGRoutingOperationResource ror) {
    this(ctx, 0, trxName);
    if (workOrderOpLine.getMFG_WorkOrderOperation_ID() == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_WorkOrderOperation_ID(workOrderOpLine.getMFG_WorkOrderOperation_ID());
    setClientOrg(workOrderOpLine.getAD_Client_ID(), workOrderOpLine.getAD_Org_ID());
    setM_Product_ID(ror.getM_Product_ID());
    setC_UOM_ID(ror.getC_UOM_ID());
    setQtyRequired(ror.getQtyRequired());
    setBasisType(ror.getBasisType());
    setChargeType(ror.getChargeType());
    setSeqNo(ror.getSeqNo());
    setIsActive(ror.isActive());
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
//    MProduct product = new MProduct(getCtx(), M_Product_ID, get_Trx());
    MProduct product = new MProduct(getCtx(), M_Product_ID, get_TrxName());
    setC_UOM_ID(product.getC_UOM_ID());
//    setBasisType(product.getBasisType());
//    setChargeType(product.getChargeType());
  }
  
  public static MMFGWorkOrderResource[] getofWorkOrderOperation(MMFGWorkOrderOperation operation, String whereClause, String orderClause) {
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderResource WHERE MFG_WorkOrderOperation_ID = ?");
    if (whereClause != null)
      sqlstmt.append(" AND ").append(whereClause); 
    if (orderClause != null)
      sqlstmt.append(" ORDER BY ").append(orderClause); 
    String sql = sqlstmt.toString();
    ArrayList<MMFGWorkOrderResource> resList = new ArrayList<MMFGWorkOrderResource>();
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, operation.get_Trx());
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, operation.get_TrxName());
      cPreparedStatement.setInt(1, operation.getMFG_WorkOrderOperation_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        resList.add(new MMFGWorkOrderResource(operation.getCtx(), rs, operation.get_TrxName())); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException sqle) {
      s_log.log(Level.SEVERE, sql, sqle);
      return null;
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	  DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null;
          cPreparedStatement = null;
    } 
    MMFGWorkOrderResource[] retVal = new MMFGWorkOrderResource[resList.size()];
    resList.toArray(retVal);
    return retVal;
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getSeqNo() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SeqNo@ < 0"));
      return false;
    } 
    if (newRecord) {
      String str = "SELECT * from MFG_WorkOrderResource WHERE M_Product_ID=? and MFG_WorkOrderOperation_ID = ?";
      CPreparedStatement cPreparedStatement = null;
      ResultSet rs = null;
      boolean success = true;
      try {
    	  cPreparedStatement = DB.prepareStatement(str, null);
        cPreparedStatement.setInt(1, getM_Product_ID());
        cPreparedStatement.setInt(2, getMFG_WorkOrderOperation_ID());
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
    String sql = "SELECT MFG_WorkOrderResource_ID FROM MFG_WorkOrderResource WHERE SeqNo = ? AND MFG_WorkOrderOperation_ID = ?";
    int MFG_WorkOrderResource_ID = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getSeqNo()), Integer.valueOf(getMFG_WorkOrderOperation_ID()) });
    if (MFG_WorkOrderResource_ID != -1 && MFG_WorkOrderResource_ID != getMFG_WorkOrderResource_ID()) {
      log.saveError("DuplicateSequence", Msg.translate(getCtx(), Integer.toString(getSeqNo())));
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
      log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered=" + QtyRequired + "->" + QtyRequired1);
      QtyRequired = QtyRequired1;
      setQtyRequired(QtyRequired);
    } 
  }
}

