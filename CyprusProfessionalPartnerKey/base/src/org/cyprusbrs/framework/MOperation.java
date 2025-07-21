package org.cyprusbrs.framework;

import java.sql.ResultSet;
import java.util.Properties;

public class MOperation extends X_M_Operation {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param S_Resource_ID id
	 */
	public MOperation (Properties ctx, int M_Operation_ID, String trxName)
	{
		super (ctx, M_Operation_ID, trxName);
	}	//	MResource

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public MOperation (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MResource

	

}
