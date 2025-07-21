package org.cyprusbrs.framework;

import java.sql.ResultSet;
import java.util.Properties;

public class MGrafana extends X_M_Grafana {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MGrafana(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	public MGrafana(Properties ctx, int M_Grafana_ID, String trxName) {
		super(ctx, M_Grafana_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public String getURL() {
        return super.getURL();
    }
    
    public int getWidth() {
        return super.getWidth();
    }
    public int getHeight() {
        return super.getHeight();
    }
    public int getDisplaySequence() {
        return super.getDisplaySequence();
    }
    
    public int getRefreshTimeInSec() {
        return super.getRefreshTimeInSec();
    }

}
