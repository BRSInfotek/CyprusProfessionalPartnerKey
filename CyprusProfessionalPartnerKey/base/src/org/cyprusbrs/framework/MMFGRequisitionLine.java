package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class MMFGRequisitionLine extends X_M_MFGRequisitionLine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MMFGRequisitionLine(Properties ctx, int M_MFGRequisitionLine_ID, String trxName) {
		super(ctx, M_MFGRequisitionLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	public MMFGRequisitionLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MMessage
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
	    if (!success) return false;

	    updateHeaderTotalLines();
	    return true;
	}

	private void updateHeaderTotalLines() {
	    String sql = "SELECT COALESCE(ROUND(SUM(Qty*PriceEntered),2), 0) FROM M_MFGRequisitionLine WHERE M_MFGRequisition_ID = ?";
	    BigDecimal totalLines = DB.getSQLValueBD(get_TrxName(), sql, getM_MFGRequisition_ID());

	    if (totalLines == null)
	        totalLines = Env.ZERO;

	    MMFGRequisition header = new MMFGRequisition(getCtx(), getM_MFGRequisition_ID(), get_TrxName());
	    header.setTotalLines(totalLines);
	    header.saveEx();
	}


}
