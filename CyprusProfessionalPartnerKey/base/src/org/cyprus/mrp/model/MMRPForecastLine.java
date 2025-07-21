package org.cyprus.mrp.model;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_MRP_ForecastLine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class MMRPForecastLine extends X_MRP_ForecastLine {
  private static final CLogger log = CLogger.getCLogger(MMRPForecastLine.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPForecastLine.class);
  
  private MMRPForecast m_parent;
  
  public MMRPForecastLine(Properties ctx, int MRP_ForecastLine_ID, String trx) {
    super(ctx, MRP_ForecastLine_ID, trx);
    this.m_parent = null;
    if (MRP_ForecastLine_ID == 0)
      setIsLineFrozen(false); 
  }
  
  public MMRPForecastLine(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.m_parent = null;
  }
  
  public MMRPForecastLine(MMRPForecast parent) {
    this(parent.getCtx(), 0, parent.get_TrxName());
    setClientOrg((PO)parent);
    setMRP_Forecast_ID(parent.getMRP_Forecast_ID());
    this.m_parent = parent;
    set_TrxName(parent.get_TrxName());
  }
  
  public MMRPForecast getParent() {
    if (this.m_parent == null)
      this.m_parent = new MMRPForecast(getCtx(), getMRP_Forecast_ID(), get_TrxName()); 
    return this.m_parent;
  }
  
  protected void setParent(MMRPForecast parent) {
    this.m_parent = parent;
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getQty().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@Qty@ < 0"));
      return false;
    } 
    if (newRecord == true) {
      CPreparedStatement cPreparedStatement = null;
      boolean lineExists = false;
      String sql = "SELECT * FROM MRP_ForecastLine WHERE MRP_Forecast_ID = ?  AND M_Product_ID = ? AND C_Period_ID = ? ";
      PreparedStatement pstmt = null;
      ResultSet rs= null;
      try {
        cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
        cPreparedStatement.setInt(1, getMRP_Forecast_ID());
        cPreparedStatement.setInt(2, getM_Product_ID());
        cPreparedStatement.setInt(3, getC_Period_ID());
         rs = cPreparedStatement.executeQuery();
        if (rs.next())
          lineExists = true; 
        rs.close();
        cPreparedStatement.close();
        cPreparedStatement = null;
      } catch (Exception e) {
        s_log.log(Level.SEVERE, sql, e);
      } 
      
      finally
      {
    	  DB.close(rs, cPreparedStatement);
    	  rs = null;
    	  cPreparedStatement = null;
      }
//      try {
//        if (cPreparedStatement != null)
//        {
//          cPreparedStatement.close(); 
//        cPreparedStatement = null;
//        }
//        if(rs != null)
//        {
//        	rs.close();
//        	rs = null;
//        }
//      } catch (Exception e) {
//        cPreparedStatement = null;
//        rs = null;
//      } 
      if (lineExists) {
        log.saveError("Error", Msg.translate(getCtx(), "ForecastLineNotUnique"));
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean beforeDelete() {
    if (getParent().isPreviouslyFrozen()) {
      log.saveError("Error", Msg.translate(getCtx(), "ForecastPreviouslyFrozen"));
      return false;
    } 
    if (isLineFrozen())
      return false; 
    return true;
  }
  //have to check at run time
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
    BigDecimal QtyEntered = getQty();
    if (QtyEntered != null) {
      BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(getCtx(), product.getC_UOM_ID()), 4);
      if (QtyEntered.compareTo(QtyEntered1) != 0) {
        log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
        QtyEntered = QtyEntered1;
        setQty(QtyEntered);
      } 
    } 
  }
  //have to check at run time
 // @UICallout
  public void setQty(String oldQtyEntered, String newQtyEntered, int windowNo) throws Exception {
    if (newQtyEntered == null || newQtyEntered.trim().length() == 0)
      return; 
    if (getC_UOM_ID() == 0)
      return; 
    BigDecimal QtyEntered = new BigDecimal(newQtyEntered);
    BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (QtyEntered.compareTo(QtyEntered1) != 0) {
      log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
      QtyEntered = QtyEntered1;
      setQty(QtyEntered);
    } 
  }
}
