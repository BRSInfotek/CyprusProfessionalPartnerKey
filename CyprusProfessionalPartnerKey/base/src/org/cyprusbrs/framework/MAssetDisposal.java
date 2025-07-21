package org.cyprusbrs.framework;

import java.sql.ResultSet;
import java.util.Properties;

public class MAssetDisposal extends X_A_AssetDisposal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MAssetDisposal(Properties ctx, int A_AssetDisposal_ID, String trxName) {
		super(ctx, A_AssetDisposal_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAssetDisposal(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
