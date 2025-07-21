/**
 * 
 */
package org.cyprus.crm.process;

import java.util.logging.Logger;

import org.cyprus.crm.model.MLead;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

/**
 * @author Mukesh
 * process name : org.cyprus.crm.process.GenerateCloseLead
 */
public class GenerateCloseLead extends SvrProcess {

	
	private static Logger log = Logger.getLogger(GenerateCloseLead.class.getName());
	
	/**	Sales Opportunity ID			*/
	int v_C_Lead_ID = 0;
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		v_C_Lead_ID=getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		
		String returnVal="Sales Lead Customer Name : ";
		log.info("v_C_Lead_ID=" + v_C_Lead_ID );
		if (v_C_Lead_ID == 0)
			throw new Exception ("Sales Lead not found -  v_C_Lead_ID=" +  v_C_Lead_ID);
		
		
		if(v_C_Lead_ID>0)
		{
			MLead lead=new MLead(getCtx(), v_C_Lead_ID, get_TrxName());
			
			/// update process of Line 
			String sql="update C_LeadInfo set processed='Y' where C_Lead_ID="+v_C_Lead_ID;
			int no = DB.executeUpdate(sql, get_TrxName());
			if (no <= 0)
				log.warning("(1) #" + no);
			
			lead.setStatus(MLead.STATUS_Closed);
			lead.setProcessed(true);
			if(lead.save(get_TrxName()))
				DB.commit(true, get_TrxName());			
		}	
		return returnVal;
	}

}
