package org.cyprus.mrp.process;

import org.cyprus.mrp.util.MRPFreezeUtil;
import org.cyprusbrs.framework.MMasterDemand;
import org.cyprusbrs.process.SvrProcess;

public class FreezeMasterDemand extends SvrProcess {
	  private int p_MRP_MasterDemand_ID = 0;
	  
	  private MMasterDemand m_masterdemand = null;
	  
	  private MRPFreezeUtil m_MFreezeUtil = null;
	  
	  protected void prepare() {
	    p_MRP_MasterDemand_ID = getRecord_ID();
	  }
	  
	  protected String doIt() throws Exception {
	 
	    m_MFreezeUtil = new MRPFreezeUtil();
	    m_masterdemand = new MMasterDemand(getCtx(), this.p_MRP_MasterDemand_ID, get_TrxName());

	    int lines = m_MFreezeUtil.freeze(m_masterdemand, get_TrxName());
	    log.fine("Frozen - Master Demand Lines = " + lines);
	    m_masterdemand.setProcessed(true);
	    m_masterdemand.save(get_TrxName());
	    return "@IsFrozen@";
	  }
}

