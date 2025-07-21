package org.cyprus.mrp.process;


import org.cyprus.mrp.util.MRPFreezeUtil;
import org.cyprusbrs.framework.MForecast;
import org.cyprusbrs.process.SvrProcess;

public class UnfreezeForecast extends SvrProcess {
	  private int p_MRP_Forecast_ID = 0;
	  
	  private MForecast m_forecast = null;
	  
	  private MRPFreezeUtil m_MFreezeUtil = null;
	  
	  protected void prepare() {
	    p_MRP_Forecast_ID = getRecord_ID();
	    m_forecast = new MForecast(getCtx(), this.p_MRP_Forecast_ID, get_TrxName());
	  }
	  
	  protected String doIt() throws Exception {

	    m_MFreezeUtil = new MRPFreezeUtil();
	    int lines = 0;
	    if (m_forecast.isProcessed()) {
	     /*
	      Timestamp currentDate = new Timestamp(System.currentTimeMillis());
	      lines = this.m_MFreezeUtil.unfreeze(this.m_forecast, currentDate, getCtx(), get_TrxName());
	    } else {**/
	      lines = m_MFreezeUtil.unfreeze(m_forecast, get_TrxName());
	    } 
	    log.fine("Unfrozen - Forecast Lines = " + lines);
	    m_forecast.setProcessed(false);
	    m_forecast.save(get_TrxName());
	    return "@IsUnfrozen@";
	  }
}

