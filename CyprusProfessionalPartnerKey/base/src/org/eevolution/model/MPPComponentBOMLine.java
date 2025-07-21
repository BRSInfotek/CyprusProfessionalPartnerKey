package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MPPComponentBOMLine extends X_PP_Component_BOMLine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MPPComponentBOMLine(Properties ctx, int PP_Component_BOMLine_id, String trxname)
	{
		super(ctx, PP_Component_BOMLine_id, trxname);
	}	//	MPPComponentBOMLine

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName Transaction Name
	 *	@return MPPComponentBOMLine Data Product Planning 
	 */
	public MPPComponentBOMLine(Properties ctx, ResultSet rs, String trxname)
	{
		super(ctx, rs,trxname);
	}
}
