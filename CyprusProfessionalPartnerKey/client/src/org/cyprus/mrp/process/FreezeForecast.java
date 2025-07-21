package org.cyprus.mrp.process;


import org.cyprus.mrp.util.MRPFreezeUtil;
import org.cyprusbrs.framework.MForecast;
import org.cyprusbrs.process.SvrProcess;

public class FreezeForecast extends SvrProcess {
	  private int p_MRP_Forecast_ID = 0;
	  
	  private MForecast m_forecast = null;
	  
	  private MRPFreezeUtil m_MFreezeUtil = null;
	  
	  protected void prepare() {
	    p_MRP_Forecast_ID = getRecord_ID();
	    m_forecast = new MForecast(getCtx(), this.p_MRP_Forecast_ID, get_TrxName());
	  }
	  
	  protected String doIt() throws Exception {
	    m_MFreezeUtil = new MRPFreezeUtil();
	    int lines = this.m_MFreezeUtil.freeze(this.m_forecast, get_TrxName());
	    log.fine("Frozen - Forecast Lines = " + lines);
	    m_forecast.setProcessed(true);
	    m_forecast.save(get_TrxName());
	    return "@IsFrozen@";
	  }
}
