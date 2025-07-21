package org.cyprusbrs.process;

import java.math.BigDecimal;

import org.cyprusbrs.util.Env;

public class ProcessTestByDeveloper extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		
		// Get all Amount of Direct Exepense
		int PP_Order_ID=1000017;
		BigDecimal activity=GetActivityExp(PP_Order_ID);
		
		return null;
	}

	private BigDecimal GetActivityExp(int pP_Order_ID) {
		BigDecimal totalAmt=Env.ZERO; 
		
		
		
		
		return totalAmt;
	}

}
