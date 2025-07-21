package org.cyprus.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cyprus.crm.model.MLead;
import org.cyprus.crm.model.MSalesOpportunity;
import org.cyprusbrs.acct.DocTax;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MLocation;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrgInfo;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
public class UtilTax {

	
	/**	Logger			*/
	private static Logger log = Logger.getLogger(UtilTax.class.getName());
	
	public static final String BANKSTATEMENT = "BANKSTATEMENT";
	public static final String CASHJOURNAL = "CASHJOURNAL";
	public static final String INVOICE = "INVOICE";
	public static final String PAYMENT = "PAYMENT";
	
	
	private UtilTax() {// nothing
	}
	
/**
 * "The system will calculate the tax amount automatically. Assume that Bank Charge is selected with a value of 100 and select tax having the 5% as tax then the following formula to be executed:
	Formula1 = 100/1.05 = 95.2381
	Formul2 = 100 - 9502381 = 4.761905 (Tax Amount)"
 * @param amount
 * @param p_C_Tax_ID
 * @param p_C_Currency_ID
 * @return BigDecimal
 * @author Anshul : Method to calculate TaxAmount by Anshul @23062021 as per excel file given by Mukesh 
 */
	public static BigDecimal getTaxAmountFromStdPrecision(BigDecimal p_Amount, Integer p_C_Tax_ID, Integer p_C_Currency_ID)
	{	
		final String sql="SELECT FN_GET_TAX_AMOUNT(?, ?, ?) FROM DUAL";
		BigDecimal value = DB.getSQLValueBD(null, sql, new Object[]{p_Amount,p_C_Tax_ID, p_C_Currency_ID});
		log.info("Parameters : P_AMOUNT : "+p_Amount+" : p_C_Tax_ID "+p_C_Tax_ID+" : p_C_Currency_ID "+p_C_Currency_ID+" : Return Data "+value);
		if(value!=null && value.compareTo(Env.ZERO)!=0)
		return value;
		else
		return Env.ZERO;
		
//		MTax tax=MTax.get(Env.getCtx(), p_C_Tax_ID); 
//		BigDecimal rate = tax.getRate().divide(Env.ONEHUNDRED,2,RoundingMode.HALF_UP);
//		BigDecimal amt = Env.ONE.add(rate);			
//		amt = amount.divide(amt,MCurrency.getStdPrecision(Env.getCtx(), p_C_Currency_ID),RoundingMode.HALF_UP);
//		BigDecimal actualamt = amount.subtract(amt);
//		log.info("Parameters : ChargeAmt : "+amount+" : p_C_Tax_ID "+p_C_Tax_ID+" : p_C_Currency_ID "+p_C_Currency_ID+" : Return Data "+amt);
//		if(actualamt!=null && actualamt.compareTo(Env.ZERO)!=0)
//			return actualamt;
//		else
//			return Env.ZERO;
	}
	
	public static DocTax[] loadTaxes(Integer p_GetID, String p_Class)
	{
		ArrayList<DocTax> list = new ArrayList<DocTax>();	
		String sql = null;
		if(BANKSTATEMENT.equalsIgnoreCase(p_Class))
		{
		 sql = "select CB.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate,CB.ChargeAmt as ChargeAmt,CB.TaxAmt as TaxAmt,t.IsSummary as IsSummary "
			 + " from C_Tax t,C_BankStatementLine CB" 
			 + " Where t.C_Tax_ID=CB.C_Tax_ID AND t.AD_Client_ID=CB.AD_Client_ID AND t.AD_org_ID=CB.AD_org_ID AND CB.C_BankStatement_ID=?";
		}
		if(CASHJOURNAL.equalsIgnoreCase(p_Class))
		{
		 sql = "select CB.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate,CB.Amount as ChargeAmt,CB.TaxAmt as TaxAmt,t.IsSummary as IsSummary "
			 + " from C_Tax t,C_CashLine CB" 
			 + " Where t.C_Tax_ID=CB.C_Tax_ID AND t.AD_Client_ID=CB.AD_Client_ID AND t.AD_org_ID=CB.AD_org_ID AND CB.C_Cash_ID=?";
		}
		if(PAYMENT.equalsIgnoreCase(p_Class))	
		{
//			boolean  IsMultiCharge = "N".equals("N");
//			sql = "select IsMultiCharge from C_Payment where C_Payment_ID=?";
//			PreparedStatement pstmt1 = null;
//			ResultSet rs1 = null;
//			try
//			{
//				pstmt1 = DB.prepareStatement(sql, null);
//				pstmt1.setInt(1, p_GetID);
//				rs1 = pstmt1.executeQuery();
//				//
//				while (rs1.next())
//				{
//					
//					 IsMultiCharge = "Y".equals(rs1.getString("IsMultiCharge"));
//								
//				}
//			}
//			catch (SQLException e)
//			{
//				log.log(Level.SEVERE, sql, e);
//				return null;
//			}
//			finally {
//				DB.close(rs1, pstmt1);
//				rs1 = null; pstmt1 = null;
//			}
//			
//			if(!IsMultiCharge)
//			{
//				sql = "select paymentallocate.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate,paymentallocate.Amount as ChargeAmt,paymentallocate.TaxAmt as TaxAmt,t.IsSummary as IsSummary "
//						 + " from C_Tax t,C_PaymentAllocate paymentallocate" 
//						 + " Where t.C_Tax_ID=paymentallocate.C_Tax_ID AND paymentallocate.C_Payment_ID=?";
//			}
			
				sql = "select payment.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate,payment.PayAmt as ChargeAmt,payment.TaxAmt as TaxAmt,t.IsSummary as IsSummary "
						 + " from C_Tax t,C_Payment payment" 
						 + " Where t.C_Tax_ID=payment.C_Tax_ID AND t.AD_Client_ID=payment.AD_Client_ID AND t.AD_org_ID=payment.AD_org_ID AND payment.C_Payment_ID=?";
			
		}
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, p_GetID);
			rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				int C_Tax_ID = rs.getInt("C_Tax_ID");
				String name = rs.getString("Name");
				BigDecimal rate = rs.getBigDecimal("Rate");
				BigDecimal ChargeAmt = rs.getBigDecimal("ChargeAmt");
				BigDecimal taxAmt = rs.getBigDecimal("TaxAmt");
				boolean IsSummary = "Y".equals(rs.getString("IsSummary"));
					
				DocTax taxLine = new DocTax(C_Tax_ID, name, rate, 
		  			ChargeAmt, taxAmt, IsSummary);	
				System.out.println(taxLine.toString());
				log.fine(taxLine.toString());
				list.add(taxLine);				
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return null;
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//	Return Array
		DocTax[] fl = new DocTax[list.size()];
		list.toArray(fl);
		log.info(list+"");
		return fl;
	}
	
	public static DocTax[] loadSummaryTaxes(Integer p_GetID, String p_Class)
	{
		ArrayList<DocTax> list = new ArrayList<DocTax>();	
		String sql = null;
		if(BANKSTATEMENT.equalsIgnoreCase(p_Class))
		{
		 sql =               
				 "select t.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate, cb.chargeamt as chargeAmt," 
				+" FN_GET_TAX_AMOUNT_CHILD((cb.chargeamt-CB.TAXAMT),CB.c_tax_id,CB.ad_client_id,CB.ad_org_id) as TaxAMount ,t.IsSummary as IsSummary"
				+" from C_Tax t, C_BankStatementLine CB"
				+" Where t.parent_Tax_ID=CB.c_tax_id AND t.AD_Client_ID=CB.AD_Client_ID AND t.AD_org_ID=CB.AD_org_ID AND CB.C_BankStatement_ID=?";
		}
		
		if(CASHJOURNAL.equalsIgnoreCase(p_Class))
		{
		 sql =               
				 "select t.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate, cb.chargeamt as chargeAmt," 
				+" FN_GET_TAX_AMOUNT_CHILD((cb.chargeamt-CB.TAXAMT),CB.c_tax_id,CB.ad_client_id,CB.ad_org_id) as TaxAMount ,t.IsSummary as IsSummary"
				+" from C_Tax t, C_CashLine CB"
				+" Where t.parent_Tax_ID=CB.c_tax_id AND t.AD_Client_ID=CB.AD_Client_ID AND t.AD_org_ID=CB.AD_org_ID AND CB.C_Cash_ID=?";
		}
		
		if(PAYMENT.equalsIgnoreCase(p_Class))
		{
//			boolean  IsMultiCharge = "N".equals("N");
//			sql = "select IsMultiCharge from C_Payment where C_Payment_ID=?";
//			PreparedStatement pstmt1 = null;
//			ResultSet rs1 = null;
//			try
//			{
//				pstmt1 = DB.prepareStatement(sql, null);
//				pstmt1.setInt(1, p_GetID);
//				rs1 = pstmt1.executeQuery();
//				//
//				while (rs1.next())
//				{
//					
//					 IsMultiCharge = "Y".equals(rs1.getString("IsMultiCharge"));
//								
//				}
//			}
//			catch (SQLException e)
//			{
//				log.log(Level.SEVERE, sql, e);
//				return null;
//			}
//			finally {
//				DB.close(rs1, pstmt1);
//				rs1 = null; pstmt1 = null;
//			}
//			if(!IsMultiCharge)
//			{
//				 sql =               
//						 "select t.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate, paymentallocate.Amount as chargeAmt," 
//						+" FN_GET_TAX_AMOUNT_CHILD((paymentallocate.Amount-paymentallocate.TAXAMT),t.c_tax_id) as TaxAMount ,t.IsSummary as IsSummary"
//						+" from C_Tax t, C_PaymentAllocate paymentallocate"
//						+" Where t.parent_Tax_ID=paymentallocate.c_tax_id AND paymentallocate.C_Payment_ID=?";
//			}
//			else
				
		//	{
				 sql =               
						 "select t.C_Tax_ID as C_Tax_ID ,t.Name as Name,t.Rate as Rate, payment.payamt as chargeAmt," 
						+" FN_GET_TAX_AMOUNT_CHILD((payment.payamt-payment.TAXAMT),payment.c_tax_id,payment.ad_client_id,payment.ad_org_id) as TaxAMount ,t.IsSummary as IsSummary"
						+" from C_Tax t, C_Payment payment"
						+" Where t.parent_Tax_ID=payment.c_tax_id AND t.AD_Client_ID=payment.AD_Client_ID AND t.AD_org_ID=payment.AD_org_ID AND payment.C_Payment_ID=?";
		//	}
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, p_GetID);
			rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				int C_Tax_ID = rs.getInt("C_Tax_ID");
				String name = rs.getString("Name");
				BigDecimal rate = rs.getBigDecimal("Rate");
				BigDecimal ChargeAmt = rs.getBigDecimal("chargeAmt");
				BigDecimal amount = rs.getBigDecimal("TaxAMount");
				boolean IsSummary = "Y".equals(rs.getString("IsSummary"));
						
					DocTax taxLine = new DocTax(C_Tax_ID, name, rate, 
				  			ChargeAmt, amount, IsSummary);	
					log.fine(taxLine.toString());
					list.add(taxLine);
			}
			
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return null;
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//	Return Array
		DocTax[] fl = new DocTax[list.size()];
		list.toArray(fl);
		log.info(list+"");
		return fl;
	}//loadSummaryLevelTaxes
	
	
	public static Integer getTaxIDBasedOnStateForOrder(Integer C_Order_ID, Integer M_Product_ID,BigDecimal lineNetAmt)
	{
		Integer taxId = null;
		try {
			// Get order and product
			MOrder order = new MOrder(Env.getCtx(), C_Order_ID, null);
			MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);

			// Check if partner is tax exempt
			MBPartner partner = new MBPartner(Env.getCtx(), order.getBill_BPartner_ID(), null);
			if(partner.isTaxExempt() || partner.isPOTaxExempt()) {
				// Get tax exempt tax ID (should be cached)
				taxId = DB.getSQLValueEx(null,
						"SELECT C_Tax_ID FROM C_Tax WHERE isTaxExempt='Y' AND IsActive='Y' AND AD_Client_ID=?",
						order.getAD_Client_ID());
			}
			else {
				// Get organization location info
				MOrgInfo orgInfo = MOrgInfo.get(Env.getCtx(), order.getAD_Org_ID(), null);
				MLocation orgLocation = new MLocation(Env.getCtx(), orgInfo.getC_Location_ID(), null);
				int orgRegionID = orgLocation.getC_Region_ID();

				// Get bill-to location info
				if (order.getBill_Location_ID() == 0) {
					return null;
				}
				MBPartnerLocation bpLoc = new MBPartnerLocation(Env.getCtx(), order.getBill_Location_ID(), null);
				MLocation bpLocation = new MLocation(Env.getCtx(), bpLoc.getC_Location_ID(), null);
				int bpRegionID = bpLocation.getC_Region_ID();

				// Inter-state transaction (different regions)
				if (orgRegionID != 0 && bpRegionID != 0 && orgRegionID != bpRegionID) {
					taxId = product.get_ValueAsInt("Ref_C_Tax_ID"); // IGST tax
				}
				// Intra-state transaction with tax rules enabled
				else if(product.get_ValueAsBoolean("IsTaxRule") && orgRegionID == bpRegionID) {
					// Get tax rule based on amount
					taxId = DB.getSQLValueEx(null,
							"SELECT tr.C_Tax_ID " +
									"FROM C_TaxRule tr " +
									"JOIN C_Tax t ON (t.C_Tax_ID = tr.C_Tax_ID) " +
									"WHERE tr.C_TaxCategory_ID = ? " +
									"AND (tr.AD_Org_ID = ? OR tr.AD_Org_ID = 0) " +
									"AND (? >= COALESCE(tr.AmountFrom,0) AND ? <= COALESCE(tr.AmountTo,999999999)) " +
									"AND tr.IsActive='Y' AND t.IsActive='Y' " +
									"ORDER BY COALESCE(tr.AmountFrom,0) DESC " +
									"LIMIT 1", // Get only the most specific rule
									product.getC_TaxCategory_ID(), 
									order.getAD_Org_ID(), 
									lineNetAmt,
									lineNetAmt);
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error determining tax", e);
		}
		return taxId;
}
	
	/**
	 * 
	 * @param C_Order_ID
	 * @param M_Product_ID
	 * @param lineNetAmt
	 * @param price
	 * @return
	 */
	public static Integer getTaxIDBasedOnStateForOrder(Integer C_Order_ID, Integer M_Product_ID,BigDecimal lineNetAmt, BigDecimal price)
	{
		Integer taxId = null;
		try {
			// Get order and product
			MOrder order = new MOrder(Env.getCtx(), C_Order_ID, null);
			MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);

			// Check if partner is tax exempt
			MBPartner partner = new MBPartner(Env.getCtx(), order.getBill_BPartner_ID(), null);
			if(partner.isTaxExempt() || partner.isPOTaxExempt()) {
				// Get tax exempt tax ID (should be cached)
				taxId = DB.getSQLValueEx(null,
						"SELECT C_Tax_ID FROM C_Tax WHERE isTaxExempt='Y' AND IsActive='Y' AND AD_Client_ID=?",
						order.getAD_Client_ID());
			}
			else {
				// Get organization location info
				MOrgInfo orgInfo = MOrgInfo.get(Env.getCtx(), order.getAD_Org_ID(), null);
				MLocation orgLocation = new MLocation(Env.getCtx(), orgInfo.getC_Location_ID(), null);
				int orgRegionID = orgLocation.getC_Region_ID();

				// Get bill-to location info
				if (order.getBill_Location_ID() == 0) {
					return null;
				}
				MBPartnerLocation bpLoc = new MBPartnerLocation(Env.getCtx(), order.getBill_Location_ID(), null);
				MLocation bpLocation = new MLocation(Env.getCtx(), bpLoc.getC_Location_ID(), null);
				int bpRegionID = bpLocation.getC_Region_ID();

				// Inter-state transaction (different regions)
				if (orgRegionID != 0 && bpRegionID != 0 && orgRegionID != bpRegionID) {
					taxId = product.get_ValueAsInt("Ref_C_Tax_ID"); // IGST tax
				}
				// Intra-state transaction with tax rules enabled
				else if(product.get_ValueAsBoolean("IsTaxRule") && orgRegionID == bpRegionID) {
					// Get tax rule based on amount
					
					String applicableON = "LA";
					if(M_Product_ID > 0)
					{
						int taxCategory_ID = DB.getSQLValue(null, "SELECT C_TaxCategory_ID FROM M_Product WHERE M_Product_ID=" + M_Product_ID);
					    applicableON = DB.getSQLValueString(null, "SELECT Cyprus_ApplicableOn FROM C_TaxRule WHERE C_TaxCategory_ID=?", taxCategory_ID);
					}
					BigDecimal amount = Env.ZERO;
					if(applicableON.equalsIgnoreCase("LA"))
					{
					    amount = lineNetAmt;
					}
					else if(applicableON.equalsIgnoreCase("PP"))
					{
						amount = price;
					}
					
					taxId = DB.getSQLValueEx(null,
							"SELECT tr.C_Tax_ID " +
									"FROM C_TaxRule tr " +
									"JOIN C_Tax t ON (t.C_Tax_ID = tr.C_Tax_ID) " +
									"WHERE tr.C_TaxCategory_ID = ? " +
									"AND (tr.AD_Org_ID = ? OR tr.AD_Org_ID = 0) " +
									"AND (? >= COALESCE(tr.AmountFrom,0) AND ? <= COALESCE(tr.AmountTo,999999999)) " +
									"AND tr.IsActive='Y' AND t.IsActive='Y' " +
									"ORDER BY COALESCE(tr.AmountFrom,0) DESC " +
									"LIMIT 1", // Get only the most specific rule
									product.getC_TaxCategory_ID(), 
									order.getAD_Org_ID(), 
									amount,
									amount);
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error determining tax", e);
		}
		return taxId;
	}
	
	/**
	 * Gets the appropriate tax ID based on state and tax rules
	 * @param AD_Table_ID - The table ID of the document (e.g., C_Order, C_Invoice, or C_Lead)
	 * @param Record_ID - The ID of the document record
	 * @param M_Product_ID - The product ID
	 * @param lineNetAmt - The line net amount for tax rule calculation
	 * @return The determined tax ID or null if not found
	 */
	public static Integer getTaxIDBasedOnState(int AD_Table_ID, int Record_ID, int M_Product_ID, BigDecimal lineNetAmt) 
	{
		Integer taxId = null;
		try {
			// Get product
			MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);

			// Get document (order, invoice, or lead)
			int billBPartner_ID = 0;
			int billLocation_ID = 0;
			int regionID = 0;
			int AD_Org_ID = 0;
			int AD_Client_ID = 0;

			if (AD_Table_ID == MOrder.Table_ID) {
				MOrder order = new MOrder(Env.getCtx(), Record_ID, null);
				billBPartner_ID = order.getBill_BPartner_ID();
				billLocation_ID = order.getBill_Location_ID();
				AD_Org_ID = order.getAD_Org_ID();
				AD_Client_ID = order.getAD_Client_ID();
			} 
			else if (AD_Table_ID == MInvoice.Table_ID) {
				MInvoice invoice = new MInvoice(Env.getCtx(), Record_ID, null);
				billBPartner_ID = invoice.getC_BPartner_ID();
				billLocation_ID = invoice.getC_BPartner_Location_ID();
				AD_Org_ID = invoice.getAD_Org_ID();
				AD_Client_ID = invoice.getAD_Client_ID();
			}
			else if (AD_Table_ID == MLead.Table_ID) {
				MLead lead = new MLead(Env.getCtx(), Record_ID, null);
				if (lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Prospect)) {
					billBPartner_ID = lead.getRef_BPartner_ID();
					billLocation_ID = lead.getRef_BPartner_Location_ID();
				} else if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Customer)) {
					billBPartner_ID = lead.getC_BPartner_ID();
					billLocation_ID = lead.getC_BPartner_Location_ID();
				}
				else
				{
					regionID = lead.getC_Region_ID();
				}
				AD_Org_ID = lead.getAD_Org_ID();
				AD_Client_ID = lead.getAD_Client_ID();
			}
			else if (AD_Table_ID == MSalesOpportunity.Table_ID) {
				MSalesOpportunity so = new MSalesOpportunity(Env.getCtx(), Record_ID, null);
				billBPartner_ID = so.getC_BPartner_ID();
				billLocation_ID = so.getC_BPartner_Location_ID();
				AD_Org_ID = so.getAD_Org_ID();
				AD_Client_ID = so.getAD_Client_ID();
			}
			else {
				throw new IllegalArgumentException("Unsupported document type");
			}

			// Check if partner is tax exempt
			if (billBPartner_ID > 0) {
				MBPartner partner = new MBPartner(Env.getCtx(), billBPartner_ID, null);
				if(partner.isTaxExempt() || partner.isPOTaxExempt()) {
					// Get tax exempt tax ID
					taxId = DB.getSQLValueEx(null,
							"SELECT C_Tax_ID FROM C_Tax WHERE isTaxExempt='Y' AND IsActive='Y' AND AD_Client_ID=?",
							AD_Client_ID);
					return taxId;
				}
			}

			// Get organization location info
			MOrgInfo orgInfo = MOrgInfo.get(Env.getCtx(), AD_Org_ID, null);
			MLocation orgLocation = new MLocation(Env.getCtx(), orgInfo.getC_Location_ID(), null);
			int orgRegionID = orgLocation.getC_Region_ID();
			int bpRegionID = 0;

			// Get bill-to location info
			if (billLocation_ID == 0 && regionID == 0) {
				return null;
			}
			if(billLocation_ID > 0)
			{
				MBPartnerLocation bpLoc = new MBPartnerLocation(Env.getCtx(), billLocation_ID, null);
				MLocation bpLocation = new MLocation(Env.getCtx(), bpLoc.getC_Location_ID(), null);
			    bpRegionID = bpLocation.getC_Region_ID();
			}
			else if(regionID > 0)
			{
				bpRegionID = regionID;
			}
//			MBPartnerLocation bpLoc = new MBPartnerLocation(Env.getCtx(), billLocation_ID, null);
//			MLocation bpLocation = new MLocation(Env.getCtx(), bpLoc.getC_Location_ID(), null);
//			int bpRegionID = bpLocation.getC_Region_ID();

			// Inter-state transaction (different regions)
			if (orgRegionID != 0 && bpRegionID != 0 && orgRegionID != bpRegionID) {
				taxId = product.get_ValueAsInt("Ref_C_Tax_ID"); // IGST tax
			}
			// Intra-state transaction with tax rules enabled
			else if(product.get_ValueAsBoolean("IsTaxRule") && orgRegionID == bpRegionID) {
				// Get tax rule based on amount
				// Let the framework handle the row limiting
				taxId = DB.getSQLValueEx(null,
				    "SELECT MAX(tr.C_Tax_ID) " +
				    "FROM C_TaxRule tr " +
				    "JOIN C_Tax t ON t.C_Tax_ID = tr.C_Tax_ID " +
				    "WHERE tr.C_TaxCategory_ID = ? " +
				    "AND (tr.AD_Org_ID = ? OR tr.AD_Org_ID = 0) " +
				    "AND ? BETWEEN tr.AmountFrom AND tr.AmountTo "+
				    //"AND (? >= NVL(tr.AmountFrom,0) AND ? <= NVL(tr.AmountTo,999999999)) " +
				    "AND tr.IsActive='Y' " +
				    "AND t.IsActive='Y' " +
				    "GROUP BY tr.AmountFrom,tr.AmountTo "+
				    "ORDER BY tr.AmountFrom DESC", 
				    product.getC_TaxCategory_ID(),
				    AD_Org_ID,
				    lineNetAmt);     // max 1 row
			}
			else if(orgRegionID == bpRegionID)
			{
				taxId = product.get_ValueAsInt("C_Tax_ID");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error determining tax", e);
		}
		return taxId;
	}	
	
	public static Integer getTaxIDBasedOnState(int AD_Table_ID, int Record_ID, int M_Product_ID, BigDecimal lineNetAmt, BigDecimal price) 
	{
		Integer taxId = null;
		try {
			// Get product
			MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);

			// Get document (order, invoice, or lead)
			int billBPartner_ID = 0;
			int billLocation_ID = 0;
			int regionID = 0;
			int AD_Org_ID = 0;
			int AD_Client_ID = 0;

			if (AD_Table_ID == MOrder.Table_ID) {
				MOrder order = new MOrder(Env.getCtx(), Record_ID, null);
				billBPartner_ID = order.getBill_BPartner_ID();
				billLocation_ID = order.getBill_Location_ID();
				AD_Org_ID = order.getAD_Org_ID();
				AD_Client_ID = order.getAD_Client_ID();
			} 
			else if (AD_Table_ID == MInvoice.Table_ID) {
				MInvoice invoice = new MInvoice(Env.getCtx(), Record_ID, null);
				billBPartner_ID = invoice.getC_BPartner_ID();
				billLocation_ID = invoice.getC_BPartner_Location_ID();
				AD_Org_ID = invoice.getAD_Org_ID();
				AD_Client_ID = invoice.getAD_Client_ID();
			}
			else if (AD_Table_ID == MLead.Table_ID) {
				MLead lead = new MLead(Env.getCtx(), Record_ID, null);
				if (lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Prospect)) {
					billBPartner_ID = lead.getRef_BPartner_ID();
					billLocation_ID = lead.getRef_BPartner_Location_ID();
				} else if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Customer)) {
					billBPartner_ID = lead.getC_BPartner_ID();
					billLocation_ID = lead.getC_BPartner_Location_ID();
				}
				else
				{
					regionID = lead.getC_Region_ID();
				}
				AD_Org_ID = lead.getAD_Org_ID();
				AD_Client_ID = lead.getAD_Client_ID();
			}
			else if (AD_Table_ID == MSalesOpportunity.Table_ID) {
				MSalesOpportunity so = new MSalesOpportunity(Env.getCtx(), Record_ID, null);
				billBPartner_ID = so.getC_BPartner_ID();
				billLocation_ID = so.getC_BPartner_Location_ID();
				AD_Org_ID = so.getAD_Org_ID();
				AD_Client_ID = so.getAD_Client_ID();
			}
			else {
				throw new IllegalArgumentException("Unsupported document type");
			}

			// Check if partner is tax exempt
			if (billBPartner_ID > 0) {
				MBPartner partner = new MBPartner(Env.getCtx(), billBPartner_ID, null);
				if(partner.isTaxExempt() || partner.isPOTaxExempt()) {
					// Get tax exempt tax ID
					taxId = DB.getSQLValueEx(null,
							"SELECT C_Tax_ID FROM C_Tax WHERE isTaxExempt='Y' AND IsActive='Y' AND AD_Client_ID=?",
							AD_Client_ID);
					return taxId;
				}
			}

			// Get organization location info
			MOrgInfo orgInfo = MOrgInfo.get(Env.getCtx(), AD_Org_ID, null);
			MLocation orgLocation = new MLocation(Env.getCtx(), orgInfo.getC_Location_ID(), null);
			int orgRegionID = orgLocation.getC_Region_ID();
			int bpRegionID = 0;

			// Get bill-to location info
			if (billLocation_ID == 0 && regionID == 0) {
				return null;
			}
			if(billLocation_ID > 0)
			{
				MBPartnerLocation bpLoc = new MBPartnerLocation(Env.getCtx(), billLocation_ID, null);
				MLocation bpLocation = new MLocation(Env.getCtx(), bpLoc.getC_Location_ID(), null);
			    bpRegionID = bpLocation.getC_Region_ID();
			}
			else if(regionID > 0)
			{
				bpRegionID = regionID;
			}
//			MBPartnerLocation bpLoc = new MBPartnerLocation(Env.getCtx(), billLocation_ID, null);
//			MLocation bpLocation = new MLocation(Env.getCtx(), bpLoc.getC_Location_ID(), null);
//			int bpRegionID = bpLocation.getC_Region_ID();

			// Inter-state transaction (different regions)
			if (orgRegionID != 0 && bpRegionID != 0 && orgRegionID != bpRegionID) {
				taxId = product.get_ValueAsInt("Ref_C_Tax_ID"); // IGST tax
			}
			// Intra-state transaction with tax rules enabled
			else if(product.get_ValueAsBoolean("IsTaxRule") && orgRegionID == bpRegionID) {
				// Get tax rule based on amount
				// Let the framework handle the row limiting
				
				String applicableON = "LA";
				if(M_Product_ID > 0)
				{
					int taxCategory_ID = DB.getSQLValue(null, "SELECT C_TaxCategory_ID FROM M_Product WHERE M_Product_ID=" + M_Product_ID);
				    applicableON = DB.getSQLValueString(null, "SELECT Cyprus_ApplicableOn FROM C_TaxRule WHERE C_TaxCategory_ID=?", taxCategory_ID);
				}
				BigDecimal amount = Env.ZERO;
				if(applicableON.equalsIgnoreCase("LA"))
				{
				    amount = lineNetAmt;
				}
				else if(applicableON.equalsIgnoreCase("PP"))
				{
					amount = price;
				}
				
				taxId = DB.getSQLValueEx(null,
				    "SELECT MAX(tr.C_Tax_ID) " +
				    "FROM C_TaxRule tr " +
				    "JOIN C_Tax t ON t.C_Tax_ID = tr.C_Tax_ID " +
				    "WHERE tr.C_TaxCategory_ID = ? " +
				    "AND (tr.AD_Org_ID = ? OR tr.AD_Org_ID = 0) " +
				    "AND ? BETWEEN tr.AmountFrom AND tr.AmountTo "+
				    //"AND (? >= NVL(tr.AmountFrom,0) AND ? <= NVL(tr.AmountTo,999999999)) " +
				    "AND tr.IsActive='Y' " +
				    "AND t.IsActive='Y' " +
				    "GROUP BY tr.AmountFrom,tr.AmountTo "+
				    "ORDER BY tr.AmountFrom DESC", 
				    product.getC_TaxCategory_ID(),
				    AD_Org_ID,
				    amount);     // max 1 row
			}
			else if(orgRegionID == bpRegionID)
			{
				taxId = product.get_ValueAsInt("C_Tax_ID");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error determining tax", e);
		}
		return taxId;
	}
}
