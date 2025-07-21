package org.cyprusbrs.framework;

import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

public class MQCProductionQualityResult extends PO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 128443123641206610L;

	public MQCProductionQualityResult(Properties ctx, int ID, String trxName) {
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
