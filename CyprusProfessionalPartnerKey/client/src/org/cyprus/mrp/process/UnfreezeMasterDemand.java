package org.cyprus.mrp.process;


import org.cyprus.mrp.util.MRPFreezeUtil;
import org.cyprusbrs.framework.MMasterDemand;
import org.cyprusbrs.process.SvrProcess;

public class UnfreezeMasterDemand extends SvrProcess {
	  private int p_MRP_MasterDemand_ID = 0;
	  
	  private MMasterDemand m_masterdemand = null;
	  
	  private MRPFreezeUtil m_MFreezeUtil = null;
	  
	  protected void prepare() {
		  
	    p_MRP_MasterDemand_ID = getRecord_ID();
	  }
	  
	  protected String doIt() throws Exception {
//	    SysEnv se = SysEnv.get("CMRP");
//	    if (se == null || !se.checkLicense())
//	      throw new CompiereUserException(CLogger.retrieveError().getName()); 
		    m_masterdemand = new MMasterDemand(getCtx(), p_MRP_MasterDemand_ID, get_TrxName());

	    this.m_MFreezeUtil = new MRPFreezeUtil();
	    int lines = 0;
	    if (m_masterdemand.isProcessed()) {
	    	
//	      Timestamp currentDate = new Timestamp(System.currentTimeMillis());
//	      lines = this.m_MFreezeUtil.unfreeze(this.m_masterdemand, currentDate, getCtx(), get_TrxName());
//	    } else {
	      lines = this.m_MFreezeUtil.unfreeze(m_masterdemand, get_TrxName());
	    } 
	    log.fine("Unfrozen - Master Demand Lines = " + lines);
	    m_masterdemand.setProcessed(false);
	    m_masterdemand.save(get_TrxName());
	    return "@IsUnfrozen@";
	  }
}

