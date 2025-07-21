package org.cyprusbrs.framework;

import java.sql.ResultSet;
import java.util.Properties;

public class MTaxCollectedAtSource extends X_M_TaxCollectedAtSource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MTaxCollectedAtSource (Properties ctx, int M_TaxCollectedAtSource_ID, String trxName)
	{
		super (ctx, M_TaxCollectedAtSource_ID, trxName);
	}	//	MTaxCollectedAtSource

	public MTaxCollectedAtSource (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MTaxCollectedAtSource
	
	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return success
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (newRecord && success)
			insert_Accounting("M_TCS_Acct", "C_AcctSchema_Default", null);

		return success;
	}	//	afterSave
	
}
