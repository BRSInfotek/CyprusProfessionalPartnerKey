package org.cyprus.crm.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.Util;

public class MSalesOpportunity extends X_C_SalesOpportunity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2346330562725222455L;
	
	private MOpportunityLine[] 	m_lines = null;
	
	public MSalesOpportunity(Properties ctx, int C_SalesOpportunity_ID, String trxName) {
		super(ctx, C_SalesOpportunity_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MSalesOpportunity(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param lead
	 */
	public MSalesOpportunity(MLead lead) {
		
		this (lead.getCtx(), 0, lead.get_TrxName());
		setClientOrg(lead);
		setDescription(lead.getDescription());
		setC_Lead_ID(lead.getC_Lead_ID());
		
		// date
		setOpportunityDate(new Timestamp(System.currentTimeMillis()));
		setConversionDate(new Timestamp(System.currentTimeMillis()));
		setEnquiryDate(lead.getEnquiryDate());
		setProposalDate(null); /// Should be null if we create from Lead window
		
		setM_Warehouse_ID(lead.getM_Warehouse_ID());
		setM_PriceList_ID(lead.getM_PriceList_ID());
		setC_Currency_ID(lead.getC_Currency_ID());
		setC_ConversionType_ID(lead.getC_ConversionType_ID()); 
		setProbability(90); // default set 
//		setStatus(STATUS_ConvertedToOpportunity);
		setStatus("24");  /// Converted From Lead

		
	}
	
	public MOpportunityLine[] getLines()
	{
		return getLines(false, null);
	}	//	getLines
	
	public MOpportunityLine[] getLines (boolean requery, String orderBy)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		String leadClause = "";
		if (orderBy != null && orderBy.length() > 0)
			leadClause += orderBy;
		else
			leadClause += "Line";
		m_lines = getLines(null, leadClause);
		return m_lines;
	}	//	getLines
	
	public MOpportunityLine[] getLines (String whereClause, String leadClause)
	{
		StringBuffer whereClauseFinal = new StringBuffer(MOpportunityLine.COLUMNNAME_C_SalesOpportunity_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (leadClause.length() == 0)
			leadClause = MOrderLine.COLUMNNAME_Line;
		//
		List<MOpportunityLine> list = new Query(getCtx(), I_C_OpportunityLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(leadClause)
										.list();
		for (MOpportunityLine li : list) {
			li.setHeaderInfo(this);
		}
		//
		return list.toArray(new MOpportunityLine[list.size()]);		
	}	//	getLines

}
