package org.cyprus.mrp.process;


//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.util.logging.Level;

import org.cyprusbrs.framework.MForecast;
import org.cyprusbrs.framework.MForecastLine;
import org.cyprusbrs.framework.MMasterDemandLine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class MasterDemandCreateFrom extends SvrProcess {
	
  private int p_M_MasterDemand_ID = 0;
  
  private int p_M_Forecast_ID;
  
  private BigDecimal p_percent = BigDecimal.ZERO;
  
  private static CLogger s_log = CLogger.getCLogger(MasterDemandCreateFrom.class);
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("M_Forecast_ID")) {
          p_M_Forecast_ID = element.getParameterAsInt();
        } else if (name.equals("Percentage")) {
          p_percent = (BigDecimal)element.getParameter();
        } else {
          log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    p_M_MasterDemand_ID = getRecord_ID();
  }
  
  protected String doIt() throws Exception {
	  
	  MForecast forecast=null;
	  if(p_M_Forecast_ID>0)
	  {
		  forecast = new MForecast(getCtx(), p_M_Forecast_ID, get_TrxName());
		  MForecastLine[] lines=forecast.getLines(null, null);
		  
		  if (p_percent == null || p_percent.signum() == 0) {
		      p_percent = BigDecimal.ONE;
		    } else {
		      p_percent = this.p_percent.divide(Env.ONEHUNDRED).add(BigDecimal.ONE);
		    } 
		  
		// Check and Delete if Lines already Created for other forecast		  
		  String sql="SELECT COUNT(*) FROM M_MasterDemandLine WHERE M_MasterDemand_ID="+p_M_MasterDemand_ID;
		  int count=DB.getSQLValue(get_TrxName(), sql);
		  if(count>0)
		  {
			int NoOfDelete=DB.executeUpdate("DELETE FROM M_MasterDemandLine WHERE M_MasterDemand_ID=? ", p_M_MasterDemand_ID,get_TrxName());
			log.info("No of Demand Lines deleted : "+NoOfDelete);
		  }
		  
		  for (MForecastLine mForecastLine : lines) {
			MMasterDemandLine demandLine=new MMasterDemandLine(getCtx(), 0,get_TrxName());
			demandLine.setM_MasterDemand_ID(p_M_MasterDemand_ID);
			demandLine.setM_Product_ID(mForecastLine.getM_Product_ID());
			demandLine.set_ValueNoCheck("M_Warehouse_ID", mForecastLine.getM_Warehouse_ID());
			demandLine.set_ValueNoCheck("M_ForecastLine_ID", mForecastLine.getM_ForecastLine_ID());
			demandLine.set_ValueNoCheck("PP_Product_BOM_ID", mForecastLine.get_Value("PP_Product_BOM_ID"));
			demandLine.setQty((mForecastLine.getQty().multiply(p_percent)).setScale(2, BigDecimal.ROUND_HALF_UP));
			demandLine.setC_Period_ID(mForecastLine.getC_Period_ID());
			demandLine.setSalesRep_ID(mForecastLine.getSalesRep_ID());
			if(!demandLine.save(get_TrxName()))
				rollback();
		}
	  }
	  
	  
	  /**
//    SysEnv se = SysEnv.get("CMRP");
//    if (se == null || !se.checkLicense())
//      throw new CompiereUserException(CLogger.retrieveError().getName()); 
    if (this.p_MRP_Forecast_ID == 0)
      throw new Exception("@FillMandatory@ @MRP_Forecast_ID@"); 
    if (this.p_percent.compareTo(BigDecimal.valueOf(-100L)) == -1)
      throw new Exception("@Percentage@ < -100 "); 
    MForecast forecast = new MForecast(getCtx(), this.p_MRP_Forecast_ID, get_TrxName());
    //forecast.setIsPreviouslyFrozen(true);
    forecast.set_ValueNoCheck("IsPreviouslyFrozen", "Y");
    int lines = 0;
    if (!forecast.isFrozen()) {
      MRPFreezeUtil freezeUtil = new MRPFreezeUtil();
      lines = freezeUtil.freeze(forecast, get_TrxName());
    } 
    forecast.save();
//    get_TrxName().commit();
//    trxname.commit();
    this.log.fine("Frozen - Forecast Lines = " + lines);
    MMRPMasterDemand masterDemand = new MMRPMasterDemand(getCtx(), this.p_MRP_MasterDemand_ID, get_TrxName());
    MPeriod currentPeriod = MPeriod.getOfCalendar(getCtx(), forecast.getC_Calendar_ID(), new Timestamp(System.currentTimeMillis()));
    int nextDemandSeq = 0;
    int incrementNo = 0;
    int endDemandSeq = 0;
    if (this.p_percent == null || this.p_percent.signum() == 0) {
      this.p_percent = BigDecimal.ONE;
    } else {
      this.p_percent = this.p_percent.divide(Env.ONEHUNDRED).add(BigDecimal.ONE);
    } 
    String selectSQL = "SELECT CurrentNext, IncrementNo , AD_Sequence_ID FROM AD_Sequence WHERE Name=? AND IsActive='Y' AND IsTableID='Y' AND IsAutoSequence='Y'  FOR UPDATE";
    MRole role = MRole.getDefault(getCtx(), false);
    StringBuilder updateSQL = new StringBuilder("UPDATE MRP_MasterDemandLine mdl  SET Qty = Qty + (" + role.addAccessSQL("SELECT ROUND(fl.Qty * ?, u.StdPrecision) FROM MRP_ForecastLine fl INNER JOIN C_UOM u ON ( fl.C_UOM_ID = u.C_UOM_ID) ", "fl", true, false) + " AND fl.MRP_Forecast_ID = ? AND fl.IsActive = 'Y' " + " AND mdl.M_Product_ID = fl.M_Product_ID " + " AND mdl.C_Period_ID = fl.C_Period_ID) " + " WHERE mdl.MRP_MasterDemand_ID = ? " + " AND mdl.IsLineFrozen = 'N' " + " AND EXISTS (SELECT * FROM MRP_ForecastLine fl2 " + " WHERE fl2.MRP_Forecast_ID = ? AND fl2.IsActive = 'Y' " + " AND fl2.M_Product_ID = mdl.M_Product_ID " + " AND fl2.C_Period_ID = mdl.C_Period_ID) ");
    if (masterDemand.isPreviouslyFrozen())
      updateSQL.append(" AND mdl.C_PERIOD_ID IN  (SELECT per.C_PERIOD_ID  FROM C_Period per  WHERE ((per.C_Year_ID * 1000) + per.PeriodNo) >= ((? * 1000) + ?))"); 
    StringBuilder insertSQL = new StringBuilder("INSERT INTO MRP_MasterDemandLine(AD_Client_ID, AD_Org_ID,  Created, CreatedBy, IsActive, MRP_MasterDemand_ID, MRP_MasterDemandLine_ID,  M_Product_ID, C_Period_ID, Qty, C_UOM_ID, IsLineFrozen, Updated, UpdatedBy) " + role.addAccessSQL("SELECT fl.AD_Client_ID, fl.AD_Org_ID, sysdate, 0, 'Y', ?,  ? + (?  * ROWNUM), fl.M_Product_ID, fl.C_Period_ID,  ROUND(fl.Qty * ?, u.StdPrecision), fl.C_UOM_ID, 'N', sysdate, 0  FROM MRP_ForecastLine fl INNER JOIN C_UOM u ON ( fl.C_UOM_ID = u.C_UOM_ID)", "fl", true, false) + " AND fl.MRP_Forecast_ID = ? AND fl.IsActive = 'Y'" + " AND NOT EXISTS (SELECT * FROM MRP_MasterDemandLine mdl " + " WHERE mdl.MRP_MasterDemand_ID = ? " + " AND mdl.M_Product_ID = fl.M_Product_ID " + " AND mdl.C_Period_ID = fl.C_Period_ID)");
    if (masterDemand.isPreviouslyFrozen())
      insertSQL.append(" AND fl.C_PERIOD_ID IN  (SELECT per.C_PERIOD_ID  FROM C_Period per  WHERE ((per.C_Year_ID * 1000) + per.PeriodNo) >= ((? * 1000) + ?))"); 
   // Trx trx = Trx.get("MasterDemandCreateFrom");
    Trx trx = Trx.get("MasterDemandCreateFrom");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = trx.getConnection().prepareStatement(selectSQL, 1003, 1008);
      pstmt.setString(1, "MRP_MasterDemandLine");
      rs = pstmt.executeQuery();
      if (rs.next()) {
        incrementNo = rs.getInt(2);
        nextDemandSeq = rs.getInt(1) + incrementNo;
        ArrayList<Object> params1 = new ArrayList();
        params1.add(this.p_percent);
        params1.add(Integer.valueOf(this.p_MRP_Forecast_ID));
        params1.add(Integer.valueOf(this.p_MRP_MasterDemand_ID));
        params1.add(Integer.valueOf(this.p_MRP_Forecast_ID));
        if (masterDemand.isPreviouslyFrozen()) {
          params1.add(Integer.valueOf(currentPeriod.getC_Year_ID()));
          params1.add(Integer.valueOf(currentPeriod.getPeriodNo()));
        } 
      //  int linesUpdated = DB.executeUpdate(trx, updateSQL.toString(), params1.toArray());
        int linesUpdated = DB.executeUpdateEx(updateSQL.toString(), params1.toArray(), get_TrxName());
       
        s_log.finest("MasterDemandLine updated = " + linesUpdated);
        ArrayList<Object> params2 = new ArrayList();
        params2.add(Integer.valueOf(this.p_MRP_MasterDemand_ID));
        params2.add(Integer.valueOf(nextDemandSeq));
        params2.add(Integer.valueOf(incrementNo));
        params2.add(this.p_percent);
        params2.add(Integer.valueOf(this.p_MRP_Forecast_ID));
        params2.add(Integer.valueOf(this.p_MRP_MasterDemand_ID));
        if (masterDemand.isPreviouslyFrozen()) {
          params2.add(Integer.valueOf(currentPeriod.getC_Year_ID()));
          params2.add(Integer.valueOf(currentPeriod.getPeriodNo()));
        } 
        int linesInserted = DB.executeUpdateEx( insertSQL.toString(), params2.toArray(), get_TrxName());
      
      //  DB.executeUpdateEx(insertSQL.toString(), params2.toArray(), get_TrxName());
        s_log.finest("MasterDemandLine inserted = " + linesInserted);
        endDemandSeq = nextDemandSeq + incrementNo * linesInserted + incrementNo * 100;
        rs.updateInt(1, endDemandSeq);
        rs.updateRow();
      } else {
        s_log.severe("No sequence record found - MRP_MasterDemandLine");
      } 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, "MRP_MasterDemandLine - " + e.getMessage(), e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
          s_log.log(Level.SEVERE, "Finish", e);
        }  
      if (pstmt != null)
        try {
          pstmt.close();
        } catch (SQLException e) {
          s_log.log(Level.SEVERE, "Finish", e);
        }  
      pstmt = null;
      if (trx != null) {
        trx.commit();
        trx.close();
      } 
    } **/
	  if(forecast!=null)
    return "@I_IsImported@ " + forecast.getName();
	  else
	return "@I_IsImported@ "+" No forcast" ;	  
		  
  }
}

