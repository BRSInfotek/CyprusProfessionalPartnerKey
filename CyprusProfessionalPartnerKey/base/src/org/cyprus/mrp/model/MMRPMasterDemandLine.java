package org.cyprus.mrp.model;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_MRP_MasterDemandLine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class MMRPMasterDemandLine extends X_MRP_MasterDemandLine {
  private static final CLogger log = CLogger.getCLogger(MMRPMasterDemandLine.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPMasterDemandLine.class);
  
  private MMRPMasterDemand m_parent;
  
  public MMRPMasterDemandLine(Properties ctx, int MRP_MasterDemandLine_ID, String trx) {
    super(ctx, MRP_MasterDemandLine_ID, trx);
    this.m_parent = null;
    if (MRP_MasterDemandLine_ID == 0)
      setIsLineFrozen(false); 
  }
  
  public MMRPMasterDemandLine(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.m_parent = null;
  }
  
  public MMRPMasterDemandLine(MMRPMasterDemand parent) {
    this(parent.getCtx(), 0, parent.get_TrxName());
    setClientOrg((PO)parent);
    setMRP_MasterDemand_ID(parent.getMRP_MasterDemand_ID());
    this.m_parent = parent;
    set_TrxName(parent.get_TrxName());
  }
  
  public MMRPMasterDemand getParent() {
    if (this.m_parent == null)
      this.m_parent = new MMRPMasterDemand(getCtx(), getMRP_MasterDemand_ID(), get_TrxName()); 
    return this.m_parent;
  }
  
  protected void setParent(MMRPMasterDemand parent) {
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
      String sql = "SELECT * FROM MRP_MasterDemandLine WHERE MRP_MasterDemand_ID = ?  AND M_Product_ID = ? AND C_Period_ID = ? ";
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
        cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
        cPreparedStatement.setInt(1, getMRP_MasterDemand_ID());
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
    	  rs = null; cPreparedStatement = null;
      }
      if (lineExists) {
        log.saveError("Error", Msg.translate(getCtx(), "MasterDemandLineNotUnique"));
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean beforeDelete() {
    if (isLineFrozen())
      return false; 
    if (getParent().isPreviouslyFrozen()) {
      log.saveError("Error", Msg.translate(getCtx(), "MasterDemandPreviouslyFrozen"));
      return false;
    } 
    return true;
  }
  
//  @UICallout
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
  
  public static MMRPMasterDemandLine[] getOfMRPMasterDemand(MMRPMasterDemand masterDemand, int startPeriodID, String trx) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<MMRPMasterDemandLine> list = new ArrayList<MMRPMasterDemandLine>();
 //   String sql = "SELECT * FROM MRP_MasterDemandLine WHERE MRP_MasterDemand_ID = ? AND IsActive = 'Y' AND C_Period_ID IN ( \tSELECT C_Period_ID FROM C_Period WHERE \tC_Period.C_Year_ID IN  (SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ? ) AND (C_Period.C_Year_ID * 1000) + C_Period.PeriodNo BETWEEN(  SELECT (per1.C_Year_ID * 1000) + per1.PeriodNo FROM C_Period per1 WHERE per1.C_Period_ID = ? AND per1.C_Year_ID IN (SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ? ))\tAND (SELECT (per2.C_Year_ID * 1000) + per2.PeriodNo FROM C_Period per2 WHERE per2.C_Period_ID = (SELECT MRP_Plan.C_Period_To_ID FROM MRP_Plan WHERE MRP_Plan_ID = ?))) ORDER BY getProductName(M_Product_ID), C_Period_ID";
    String sql = "SELECT * FROM MRP_MasterDemandLine WHERE MRP_MasterDemand_ID = ? AND IsActive = 'Y' AND C_Period_ID IN ( \tSELECT C_Period_ID FROM C_Period WHERE \tC_Period.C_Year_ID IN  (SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ? ) AND (C_Period.C_Year_ID * 1000) + C_Period.PeriodNo BETWEEN(  SELECT (per1.C_Year_ID * 1000) + per1.PeriodNo FROM C_Period per1 WHERE per1.C_Period_ID = ? AND per1.C_Year_ID IN (SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ? ))\tAND (SELECT (per2.C_Year_ID * 1000) + per2.PeriodNo FROM C_Period per2 WHERE per2.C_Period_ID = (SELECT MRP_Plan.C_Period_To_ID FROM MRP_Plan WHERE MRP_Plan_ID = ?))) ORDER BY  C_Period_ID";
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, masterDemand.getMRP_MasterDemand_ID());
      cPreparedStatement.setInt(2, masterDemand.getMRP_Plan_ID());
      cPreparedStatement.setInt(3, startPeriodID);
      cPreparedStatement.setInt(4, masterDemand.getMRP_Plan_ID());
      cPreparedStatement.setInt(5, masterDemand.getMRP_Plan_ID());
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMRPMasterDemandLine(masterDemand.getCtx(), rs, trx)); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } 
    finally
    {
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    }
//    try {
//      if (cPreparedStatement != null)
//        cPreparedStatement.close(); 
//      cPreparedStatement = null;
//    } catch (Exception e) {
//      cPreparedStatement = null;
//    } 
    MMRPMasterDemandLine[] retValue = new MMRPMasterDemandLine[list.size()];
    list.toArray(retValue);
    return retValue;
  }
}

