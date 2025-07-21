package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MPPCostCollectorLine extends X_PP_Cost_CollectorLine{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**	Logger	*/

	public MPPCostCollectorLine(Properties ctx, int PP_Cost_CollectorLine_ID, String trxName) {
		super(ctx, PP_Cost_CollectorLine_ID, trxName);
	}	
	
	  /** Load Constructor */
    public MPPCostCollectorLine (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }
}
