package org.cyprusbrs.framework;

import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

public class MQCMRQualityResult extends PO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -928263861050234308L;

	public MQCMRQualityResult(Properties ctx, int ID, String trxName) {
		super(ctx, ID, trxName);
	}

	@Override
	protected POInfo initPO(Properties ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int get_AccessLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
