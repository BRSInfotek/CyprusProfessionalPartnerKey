/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.cyprusbrs.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.cyprusbrs.framework.MAccount;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.eevolution.model.MPPCostCollector;
import org.eevolution.model.MPPCostCollectorLine;

/**
 *  Post Inventory Documents.
 *  <pre>
 *  Table:              M_Inventory (321)
 *  Document Types:     MMI
 *  </pre>
 *  @author Jorg Janke
 *  @author Armen Rizal, Goodwill Consulting
 * 			<li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 * 	@author red1
 * 			<li>BF [ 2982994 ]  Internal Use Inventory does not reverse Accts
 *  @version  $Id: Doc_Inventory.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_PPCostCollector extends Doc
{
	private int				m_Reversal_ID = 0;
	private String			m_DocStatus = "";
	private String 			costCollecterType;
	private MPPCostCollector collector=null;
	
	/**
	 *  Constructor
	 * 	@param ass accounting schemata
	 * 	@param rs record
	 * 	@param trxName trx
	 */
	public Doc_PPCostCollector (MAcctSchema[] ass, ResultSet rs, String trxName)
	{
		super (ass, MPPCostCollector.class, rs, DOCTYPE_ManufacturingCostCollector, trxName);
	}   //  Doc_Inventory

	/**
	 *  Load Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails()
	{
		/// Code commented by Mukesh as per discussion by Surya @20220411
//		String costupdate = setcost();
		setC_Currency_ID (NO_CURRENCY);
		collector = (MPPCostCollector)getPO();
		setDateDoc (collector.getMovementDate());
		setDateAcct(collector.getMovementDate());
		m_Reversal_ID = collector.getReversal_ID();//store original (voided/reversed) document
		m_DocStatus = collector.getDocStatus();
		costCollecterType=collector.getCostCollectorType();
		//	Contained Objects
		p_lines = loadLines(collector);
		log.fine("Lines=" + p_lines.length);
		return null;
	}   //  loadDocumentDetails

	/**
	 *	Load Invoice Line
	 *	@param inventory inventory
	 *  @return DocLine Array
	 */
	private DocLine[] loadLines(MPPCostCollector collector)
	{
		ArrayList<DocLine> list = new ArrayList<DocLine>();
		List<MPPCostCollectorLine> lines = collector.getLines(collector.getPP_Cost_Collector_ID());
//		for (int i = 0; i < lines.length; i++)
		for(MPPCostCollectorLine line:lines)
		{
			//MInventoryLine line = lines[i];
			//	nothing to post
//			if (line.getQtyEntered().signum() == 0)
//				continue;
			//
			DocLine docLine = new DocLine (line, this); 
			docLine.setQty(line.getQtyEntered(), true);
			docLine.setAmount(line.getTrxCost()!=null ? line.getTrxCost().multiply(line.getQtyEntered()):Env.ZERO);
			docLine.setTrxCost(line.getTrxCost()!=null ? line.getTrxCost() : Env.ZERO);
//			BigDecimal Qty = line.getQtyEntered();
//			if (Qty.signum() != 0)
//				Qty = Qty.negate();		//	Internal Use entered positive
//			else
//			{
//				BigDecimal QtyBook = line.getQtyBook();
//				BigDecimal QtyCount = line.getQtyCount();
//				Qty = QtyCount.subtract(QtyBook);
//			}
//			docLine.setQty (Qty, false);		// -5 => -5
//			// code update by Anshul @30072024 to get cost from line's transaction cost
//			BigDecimal trxCost = line.get_ValueAsBigDecimal("TrxCost");
//						docLine.setTrxCost(trxCost);
//
//			docLine.setReversalLine_ID(line.getReversalLine_ID());
//			log.fine(docLine.toString());
			list.add (docLine);
		}

		//	Return Array
		DocLine[] dls = new DocLine[list.size()];
		list.toArray(dls);
		return dls;
	}	//	loadLines

	/**
	 *  Get Balance
	 *  @return Zero (always balanced)
	 */
	public BigDecimal getBalance()
	{
		BigDecimal retValue = Env.ZERO;
		return retValue;
	}   //  getBalance

	/**
	 *  Create Facts (the accounting logic) for
	 *  MMI.
	 *  <pre>
	 *  Inventory
	 *      Inventory       DR      CR
	 *      InventoryDiff   DR      CR   (or Charge)
	 *  </pre>
	 *  @param as account schema
	 *  @return Fact
	 */
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		setC_Currency_ID(as.getC_Currency_ID());

		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			// MZ Goodwill
			// if Physical Inventory CostDetail is exist then get Cost from Cost Detail 
			//BigDecimal costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InventoryLine_ID=?");
			// code update by Anshul @30072024 to get cost from line's transaction cost
			BigDecimal costs = line.getTrxCost();
			// end MZ
			if (costs == null || costs.signum() == 0)
			{
				p_Error = "No Costs for " + line.getProduct().getName();
				return null;
			}
			
			
			
			costs = costs.multiply(line.getQty()).abs();
			
			
			if(MPPCostCollector.COSTCOLLECTORTYPE_ComponentIssue.equalsIgnoreCase(costCollecterType))
			{
				//  Inventory       DR      CR
				dr = fact.createLine(line,
					line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
					as.getC_Currency_ID(), costs);
				//  may be zero difference - no line created.
				if (dr == null)
					continue;
				dr.setM_Locator_ID(line.getM_Locator_ID());
				if (m_DocStatus.equals(MPPCostCollector.DOCSTATUS_Reversed) && m_Reversal_ID !=0 && line.getReversalLine_ID() != 0)
				{
					//	Set AmtAcctDr from Original Phys.Inventory
					if (!dr.updateReverseLine (MPPCostCollector.Table_ID, 
							m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
					{
						p_Error = "Original Cost Collector is not posted yet";
						return null;
					}
				}			
				//  InventoryDiff   DR      CR
				//	or Charge
				MAccount workInProcess = null;
				if (workInProcess == null)
					workInProcess = getAccount(Doc.ACCTTYPE_WorkInProcess, as);
				cr = fact.createLine(line, workInProcess,
					as.getC_Currency_ID(), costs.negate());
				if (cr == null)
					continue;
				cr.setM_Locator_ID(line.getM_Locator_ID());
				cr.setQty(line.getQty().negate());
				

				if (m_DocStatus.equals(MPPCostCollector.DOCSTATUS_Reversed) && m_Reversal_ID !=0 && line.getReversalLine_ID() != 0)
				{
					//	Set AmtAcctCr from Original Phys.Inventory
					if (!cr.updateReverseLine (MPPCostCollector.Table_ID, 
							m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
					{
						p_Error = "Original Cost Collector is not posted yet";
						return null;
					}
					costs = cr.getAcctBalance(); //get original cost
				}
			}
			else if(MPPCostCollector.COSTCOLLECTORTYPE_AssemblyTransfer.equalsIgnoreCase(costCollecterType))
			{
				BigDecimal assetCost = costs != null ? costs : Env.ZERO;

				Map<String, BigDecimal> activityCost = getCostOfTimeActivity();
				if (activityCost != null && !activityCost.isEmpty()) {
				    for (Map.Entry<String, BigDecimal> entry : activityCost.entrySet()) {
				        String accountingCode = entry.getKey();
				        BigDecimal totalCost = entry.getValue() != null ? entry.getValue() : Env.ZERO;

				        assetCost = assetCost.add(totalCost);

				        System.out.println("Accounting Code: " + accountingCode + " | Total Cost: " + totalCost);

				        if (accountingCode == null || accountingCode.trim().isEmpty())
				            continue;

				        MAccount absorptionClearing = MAccount.get(as.getCtx(), Integer.parseInt(accountingCode));
				        if (absorptionClearing == null)
				            continue;

				        dr = fact.createLine(line, absorptionClearing, as.getC_Currency_ID(), totalCost.negate());
				        if (dr == null)
				            continue;

				        if (line.getM_Locator_ID() != 0)
				            dr.setM_Locator_ID(line.getM_Locator_ID());
				    }
				}

				// Work In Process (WIP) debit
				MAccount workInProcess = getAccount(Doc.ACCTTYPE_WorkInProcess, as);
				if (workInProcess != null) {
				    dr = fact.createLine(line, workInProcess, as.getC_Currency_ID(), costs != null ? costs.negate() : Env.ZERO);
				    if (dr != null) {
				        if (line.getM_Locator_ID() != 0)
				            dr.setM_Locator_ID(line.getM_Locator_ID());

				        if (MPPCostCollector.DOCSTATUS_Reversed.equals(m_DocStatus) && m_Reversal_ID != 0 && line.getReversalLine_ID() != 0) {
				            if (!dr.updateReverseLine(MPPCostCollector.Table_ID, m_Reversal_ID, line.getReversalLine_ID(), Env.ONE)) {
				                p_Error = "Original Cost Collector is not posted yet";
				                return null;
				            }
				        }
				    }
				}

				// Credit side - Product Asset
				MAccount assetAcct = getAccount(Doc.ACCTTYPE_P_Asset_Acct, as);
				if (assetAcct != null) {
				    cr = fact.createLine(line, assetAcct, as.getC_Currency_ID(), assetCost);
				    if (cr != null) {
				        if (line.getM_Locator_ID() != 0)
				            cr.setM_Locator_ID(line.getM_Locator_ID());

				        if (line.getQty() != null)
				            cr.setQty(line.getQty().negate());

				        if (MPPCostCollector.DOCSTATUS_Reversed.equals(m_DocStatus) && m_Reversal_ID != 0 && line.getReversalLine_ID() != 0) {
				            if (!cr.updateReverseLine(MPPCostCollector.Table_ID, m_Reversal_ID, line.getReversalLine_ID(), Env.ONE)) {
				                p_Error = "Original Cost Collector is not posted yet";
				                return null;
				            }
				            assetCost = cr.getAcctBalance(); // update asset cost from reverse
				        }
				    }
				}

			}
			else
			{
				p_Error = "No Posting for Cost Collecter Type : "+costCollecterType;
				return null;
			}
			

			//	Cost Detail
			 /* Source move to MInventory.createCostDetail()
			MCostDetail.createInventory(as, line.getAD_Org_ID(), 
				line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), 
				line.get_ID(), 0, 
				costs, line.getQty(), 
				line.getDescription(), getTrxName());*/
		}
		//
		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(fact);
		return facts;
	}   //  createFact

	private Map<String,BigDecimal> getCostOfTimeActivity() {

		Map<String,BigDecimal> mapReturn=new HashMap<String,BigDecimal>();
		if(collector!=null)
		{
			String sql=" SELECT caa.P_AbsorptionClearing_Acct AS AbsorptionClearing, SUM((EXTRACT(HOUR FROM mrt.MFG_RecordingTime) * ca.PlannedCost)) AS Total"
					+ " FROM MFG_TimeRecording mtr"
					+ " INNER JOIN MFG_RecordingDate mrd ON (mrd.MFG_TimeRecording_ID=mtr.MFG_TimeRecording_ID) "
					+ " INNER JOIN MFG_RecordingTime mrt ON (mrt.MFG_RecordingDate_ID=mrd.MFG_RecordingDate_ID) "
					+ " INNER JOIN C_Activity ca ON (ca.C_Activity_ID=mrt.C_Activity_ID) "
					+ " LEFT OUTER JOIN C_Activity_Acct caa ON (caa.C_Activity_ID=ca.C_Activity_ID) "
					+ " WHERE mtr.PP_Order_ID= ? AND mrd.IsRecordConsidered='N' "
					+ " GROUP BY caa.P_AbsorptionClearing_Acct";
			//totalCost = DB.getSQLValueBD(getTrxName(), sql, collector.get_ValueAsInt("PP_Order_ID"));

			PreparedStatement pstmt = null;
			ResultSet        rs    = null;
			try {
				pstmt = DB.prepareStatement(sql, getTrxName());
				pstmt.setInt(1, collector.get_ValueAsInt("PP_Order_ID"));
				rs = pstmt.executeQuery();

				while (rs.next()) {
					String acct  = rs.getString("AbsorptionClearing");           // account code
					BigDecimal total = rs.getBigDecimal("Total");       // aggregated amount
					mapReturn.put(acct, total);
					// merge (in case the same key appears twice – shouldn’t, but safe)
					//result.merge(acct, total, BigDecimal::add);
				}
			}catch (Exception e)
			{
				s_log.log (Level.SEVERE, sql, e);
			}
			finally {
				DB.close(rs, pstmt);
			}
		}
		return mapReturn;
	}

}   //  Doc_Inventory
