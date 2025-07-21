package org.cyprusbrs.framework;

import java.sql.ResultSet;
import java.util.Properties;

public class MJobLineComponents extends X_C_JobLineComponents {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MJobLineComponents(Properties ctx, int C_JobLineComponents_ID, String trxName) {
        super(ctx, C_JobLineComponents_ID, trxName);
    }
    
    public MJobLineComponents(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
    
    // Custom business logic for Job Line Components
    public void calculateComponentCost() {
        // Implement cost calculation logic
    }
    
 
    
}
