package org.cyprus.webui.apps.form;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cyprus.crm.model.MLead;
import org.cyprus.crm.model.MLeadInfo;
import org.cyprus.crm.model.MOpportunityLine;
import org.cyprus.crm.model.MSalesOpportunity;
import org.cyprus.util.UtilTax;
import org.cyprus.webui.apps.form.WCRMSalesManagement.SendMailData;
import org.cyprusbrs.framework.I_AD_User;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.framework.MMailText;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MQuery;
import org.cyprusbrs.framework.MTax;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Trx;

public class WCRMSalesManagementUtility {

	
	// Create a new unique name for the transaction
	//private static String trxName = Trx.createTrxName("CreateWCRMSalesManagementUtility");
	// Start the transaction
	// private static Trx trx = Trx.get(trxName, true); // `true` = create if doesn't exist
	
	private static Logger log = Logger.getLogger(WCRMSalesManagementUtility.class.getName());

	public static StringBuilder createInvoice(Trx trx, ArrayList<KeyNamePair> keyNamePairVal) throws Exception {
		StringBuilder sb = new StringBuilder("Sales Invoice Status: ");
		boolean overallSuccess = true; // Track success of entire batch

		try {
			for (KeyNamePair data : keyNamePairVal) {
				MInvoice m_Invoice = null;
				Integer v_C_SalesOrder_ID = data.getKey();
				log.info("v_C_SalesOrder_ID=" + v_C_SalesOrder_ID);

				if (v_C_SalesOrder_ID == 0) {
					sb.append(" | Error: Sales Order not found");
					overallSuccess = false;
					continue;
				}

				MOrder order = new MOrder(Env.getCtx(), v_C_SalesOrder_ID, trx.getTrxName());

				// Check if invoice already exists for this order
				List<MInvoice> existingInvoices = MInvoice.getOfOrder(Env.getCtx(), v_C_SalesOrder_ID, trx.getTrxName());
				if (existingInvoices != null && !existingInvoices.isEmpty()) {
					sb.append(" | Invoice already exists for Order " + order.getDocumentNo() + 
							": " + existingInvoices.get(0).getDocumentNo());
					continue;
				}

				MOrderLine[] oLines = order.getLines(true, MOrderLine.COLUMNNAME_Line);
				log.info("No of order lines: " + oLines.length);

				if (oLines.length > 0) {
					try {
						MDocType docType = MDocType.get(Env.getCtx(), order.getC_DocTypeTarget_ID());
						Integer docTypeId = docType.getC_DocTypeInvoice_ID();
						log.info("Doc Type ID: " + docTypeId);

						m_Invoice = new MInvoice(order, docTypeId, new Timestamp(System.currentTimeMillis()));
						m_Invoice.setM_PriceList_ID(order.getM_PriceList_ID());
						m_Invoice.setC_BPartner_ID(order.getC_BPartner_ID());
						m_Invoice.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());

						if (!m_Invoice.save(trx.getTrxName())) {
							sb.append(" | Error creating Invoice for Order: " + order.getDocumentNo());
							overallSuccess = false;
							continue;
						}

						// Create invoice lines
						createLine(order, oLines, m_Invoice, trx);

						// Update order status
						if(order.processIt(MOrder.DOCACTION_Complete))
						{
							order.setDocStatus(MOrder.DOCACTION_Complete);
							order.setDocAction(MOrder.DOCACTION_Close);
							order.setProcessed(true);
						}

						if (!order.save(trx.getTrxName())) {
							sb.append(" | Error updating Sales Order: " + order.getDocumentNo());
							overallSuccess = false;
							continue;
						}

						sb.append(" | Created : " + m_Invoice.getDocumentNo());

					} catch (Exception e) {
						sb.append(" | Error processing Order " + order.getDocumentNo() + ": " + e.getMessage());
						overallSuccess = false;
						log.log(Level.SEVERE, "Error processing order", e);
						continue;
					}
				} else {
					sb.append(" | No lines found for Order: " + order.getDocumentNo());
				}
			}

			// Commit or rollback based on overall success
			if (overallSuccess) {
				trx.commit();
			} else {
				trx.rollback();
				sb.insert(0, "Transaction rolled back due to errors. ");
			}
		} catch (Exception e) {
			trx.rollback();
			sb.insert(0, "Error: Transaction rolled back. "+e.getMessage());
			log.log(Level.SEVERE, "Error processing invoices", e);
			throw e;
		}

		return sb;
	}
	
	
	/**
	 * Created Header and lines of invoice
	 * @param order
	 * @param line
	 */
	private static void createLine(MOrder order, MOrderLine[] orderLines, MInvoice m_Invoice, Trx trx) {
	    for (MOrderLine orderLine : orderLines) {
	        MInvoiceLine line = new MInvoiceLine(m_Invoice);
	        BigDecimal toDeliver = orderLine.getQtyEntered().subtract(orderLine.getQtyInvoiced());
	        log.info("Delivery Qty: " + toDeliver);
	        line.setOrderLine(orderLine);
	        line.setQty(toDeliver);	        
			line.setQtyInvoiced(orderLine.getQtyOrdered());
			line.setPriceList(orderLine.getPriceList());	        
	        if (!line.save(trx.getTrxName())) {
	            throw new IllegalStateException("Could not create Invoice Line for Order: " + order.getDocumentNo());
	        }
	    }
	}
	
	public static StringBuilder createSalesOrder(Trx trx, ArrayList<KeyNamePair> keyNamePairVal) throws Exception
	{
		StringBuilder sb = new StringBuilder("Sales Order Status : ");
		Integer v_C_SalesQuotation_ID = 0;
		boolean overallSuccess = true; // Track success of entire batch

		try {
			for(KeyNamePair data : keyNamePairVal) {
				v_C_SalesQuotation_ID = data.getKey();
				log.info("v_C_SalesQuotation_ID=" + v_C_SalesQuotation_ID);

				if (v_C_SalesQuotation_ID == 0) {
					sb.append(" | Error: Sales Quotation not found");
					overallSuccess = false;
					continue;
				}

				MOrder saleQuotation = new MOrder(Env.getCtx(), v_C_SalesQuotation_ID, trx.getTrxName());

				// Check if order already exists
				if (saleQuotation.getRef_Order_ID() > 0) {
					MOrder existingOrder = new MOrder(Env.getCtx(), saleQuotation.getRef_Order_ID(), trx.getTrxName());
					sb.append(" | Sales Order already exists: " + existingOrder.getDocumentNo());
					continue;
				}

				// Create new order
				Timestamp p_DateDoc = new Timestamp(System.currentTimeMillis());
				Integer p_C_DocType_ID = new Query(Env.getCtx(), MDocType.Table_Name,
						"DocBaseType = ? AND DocSubTypeSO = ? AND AD_Client_ID = ?",
						trx.getTrxName())
						.setParameters("SOO", "MO", saleQuotation.getAD_Client_ID())
						.setOrderBy("C_DocType_ID")
						.firstId();

				MDocType dt = MDocType.get(Env.getCtx(), p_C_DocType_ID);
				if (dt.get_ID() == 0) {
					sb.append(" | Error: No DocType found");
					overallSuccess = false;
					continue;
				}

				MOrder newOrder = MOrder.copyFrom(saleQuotation, p_DateDoc, 
						dt.getC_DocType_ID(), dt.isSOTrx(), false, true, trx.getTrxName());
				newOrder.setC_DocTypeTarget_ID(p_C_DocType_ID);
				newOrder.setRef_Order_ID(saleQuotation.getC_Order_ID());
				newOrder.setSalesRep_ID(saleQuotation.getSalesRep_ID());

				if (!newOrder.save(trx.getTrxName())) {
					sb.append(" | Error creating Order from: " + saleQuotation.getDocumentNo());
					overallSuccess = false;
					continue;
				}

				// Process original quotation
				if(saleQuotation.processIt(MOrder.DOCACTION_Complete))
				{
					saleQuotation.setDocStatus(MOrder.DOCACTION_Complete);
					saleQuotation.setDocAction(MOrder.DOCACTION_Close);
					saleQuotation.setProcessed(true);
				}
				if (!saleQuotation.save(trx.getTrxName())) {
					sb.append(" | Error updating Sales Quotation: " + saleQuotation.getDocumentNo());
					overallSuccess = false;
					continue;
				}

				sb.append(" | Created : " + newOrder.getDocumentNo());
			}

			// Commit or rollback based on overall success
			if (overallSuccess) {
				trx.commit();
			} else {
				trx.rollback();
				sb.insert(0, "Transaction rolled back due to errors. ");
			}
		} catch (Exception e) {
			trx.rollback();
			sb.insert(0, "Error: Transaction rolled back. "+e.getMessage());
			log.log(Level.SEVERE, "Error processing sales orders", e);
			throw e;
		}

		return sb;}
	
	
	public static StringBuilder createSalesQuotation(Trx trx, ArrayList<KeyNamePair> keyNamePairVal) throws Exception
	{
		StringBuilder sb=new StringBuilder(" Sales Quotation Status : ");
		Integer v_C_SalesOpportunity_ID=0;
		for(KeyNamePair data:keyNamePairVal)
		{
			MOrder order=null;
			v_C_SalesOpportunity_ID=data.getKey();
			log.info("v_C_SalesOpportunity_ID=" + v_C_SalesOpportunity_ID );

			if (v_C_SalesOpportunity_ID == 0)
			{
				//throw new Exception ("Sales Opportunity not found -  v_C_SalesOpportunity_ID=" +  v_C_SalesOpportunity_ID);
				sb.append(" | Sales Opportunity not found -  v_C_SalesOpportunity_ID=" +  v_C_SalesOpportunity_ID);
		        continue; // Skip to next opportunity
			}

			MSalesOpportunity saleOpp=new MSalesOpportunity(Env.getCtx(), v_C_SalesOpportunity_ID, trx.getTrxName());

			// Check if a quotation already exists for this opportunity
		    if (saleOpp.getC_Order_ID() > 0) {
		        MOrder existingOrder = new MOrder(Env.getCtx(), saleOpp.getC_Order_ID(), trx.getTrxName());
		        sb.append(" | Sales Quotation already exists: " + existingOrder.getDocumentNo());
		        continue; // Skip to next opportunity
		    }
			
			final String whereClause = MOpportunityLine.COLUMNNAME_C_SalesOpportunity_ID+"=? ";
			List<MOpportunityLine> lines = new Query(Env.getCtx(),MOpportunityLine.Table_Name,whereClause,trx.getTrxName())
					.setParameters(saleOpp.getC_SalesOpportunity_ID())
					.list();

			if (lines == null || lines.isEmpty()) {
				{
					//throw new CyprusException("Sales Quotation cannot be created: No Opportunity Lines found for this Sales Opportunity.");
					//throw new Exception ("Sales Opportunity not found -  v_C_SalesOpportunity_ID=" +  v_C_SalesOpportunity_ID);
					sb.append(" | Sales Quotation cannot be created: No Opportunity Lines found for this Sales Opportunity.");
			        continue; // Skip to next opportunity
				}
			}

			if(order==null)
			{
				Integer C_BPartner_ID=0;
				Integer C_BPartnerLocation_ID=0;
				Integer AD_UserID=0;
				if(saleOpp.getC_BPartner_ID()>0)
				{
					C_BPartner_ID=saleOpp.getC_BPartner_ID();
					C_BPartnerLocation_ID=saleOpp.getC_BPartner_Location_ID();
					AD_UserID=saleOpp.getAD_User_ID();
				}
				else if(saleOpp.getRef_BPartner_ID()>0)
				{
					C_BPartner_ID=saleOpp.getRef_BPartner_ID();
					C_BPartnerLocation_ID=saleOpp.getRef_BPartner_Location_ID();
					AD_UserID=saleOpp.getRef_User_ID();
				}

				MBPartner bp=new MBPartner(Env.getCtx(), C_BPartner_ID, trx.getTrxName()); /// Here we need also check Prospect customer

				Integer paymentTerm=0;
				if(saleOpp.getC_PaymentTerm_ID()>0)
					paymentTerm=saleOpp.getC_PaymentTerm_ID();
				else if (bp.getC_PaymentTerm_ID()>0)
					paymentTerm=bp.getC_PaymentTerm_ID();
				else
				{
					sb.append("Payment Term is not defined");
					continue;
				}

				Integer p_C_DocType_ID = new Query(Env.getCtx(), MDocType.Table_Name,
						"DocBaseType = ? AND DocSubTypeSO = ? AND AD_Client_ID = ?",
						trx.getTrxName())
						.setParameters("SOO", "OB", Env.getAD_Client_ID(Env.getCtx()))
						.setOrderBy("C_DocType_ID")
						.firstId(); // Gets the first result


				order=new MOrder(Env.getCtx(), 0, trx.getTrxName());

				order.setIsSOTrx(true);

				if (p_C_DocType_ID != 0)
					order.setC_DocTypeTarget_ID(p_C_DocType_ID);
				else
					order.setC_DocTypeTarget_ID();
				order.setBPartner(bp);
				order.setC_BPartner_Location_ID(C_BPartnerLocation_ID);
				order.setAD_User_ID(AD_UserID);
				order.setSalesRep_ID(Env.getAD_User_ID(Env.getCtx()));
				order.setC_PaymentTerm_ID(paymentTerm);
				order.setC_Currency_ID(saleOpp.getC_Currency_ID());
				order.setPaymentRule(saleOpp.getPaymentRule());
				order.setDatePromised(saleOpp.getConversionDate());  /// Need to check all dates from Video Meeting
				order.setC_SalesOpportunity_ID(saleOpp.getC_SalesOpportunity_ID()); /// LInked to the Sales Quotation
				order.setSalesRep_ID(saleOpp.get_ValueAsInt("SalesRep_ID"));
				if(!order.save(trx.getTrxName()))
				{
					DB.rollback(true, trx.getTrxName());
				}

			}

			if(order!=null && lines.size()>0)
			{
				for(MOpportunityLine line:lines)
				{
					/// Below need to check information again from Sales Opportunity window
					MOrderLine ol = new MOrderLine (order);

					if(line.getC_Charge_ID()>0)
						ol.setC_Charge_ID(line.getC_Charge_ID());
					else
						ol.setM_Product_ID(line.getM_Product_ID(),line.getC_UOM_ID());

					ol.setDescription(line.getDescription());
					ol.setQtyEntered(line.getPlannedQty());
					ol.setQtyOrdered(line.getBaseQty()); 
					ol.setPriceEntered(line.getPlannedPrice());
					ol.setPriceActual(line.getBasePrice());
					ol.setC_UOM_ID(line.getC_UOM_ID());  // Updated by Mukesh @20250620
					ol.setPriceList(line.getPriceList()); // Updated by Mukesh @20250619
					ol.setC_Tax_ID(line.getC_Tax_ID());
					ol.setDiscount(line.getDiscount()); // Updated by Mukesh @20250616
					if(!ol.save(trx.getTrxName()))
					{
						DB.rollback(true, trx.getTrxName());
					}
				}
			}

			if(order!=null)
			{
				saleOpp.setC_Order_ID(order.getC_Order_ID());
				saleOpp.setProposalDate(new Timestamp(System.currentTimeMillis()));
				saleOpp.setStatus(MSalesOpportunity.STATUS_ConvertedToQuotation);
				saleOpp.setProcessed(true);
				if(saleOpp.save(trx.getTrxName()))
				{	
					int sqlNo=DB.executeUpdate(	"Update C_OpportunityLine set Processed='Y' where C_SalesOpportunity_ID="+saleOpp.getC_SalesOpportunity_ID(), trx.getTrxName());
					log.info("UPdated processed field in OpportunityLine "+sqlNo);
					try {
						DB.commit(true, trx.getTrxName());
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
			if(order!=null)
				sb.append(" | Created : "+order.getDocumentNo());
		}
		return sb;

	}
	public static StringBuilder createSalesOpportunity(Trx trx, ArrayList<KeyNamePair> keyNamePairVal) throws Exception
	{
		

		/**	Lead ID			*/
		int v_C_Lead_ID = 0;
		
		int v_C_BPartner_ID=0;
		
		int v_C_BPartnerLocation_ID=0;
		
		int v_Ad_User_ID=0;
		StringBuilder sb=new StringBuilder(" Sales Opportunity Status : ");
		for(KeyNamePair data:keyNamePairVal)
		{
			MSalesOpportunity saleOpp=null;
			v_C_Lead_ID=data.getKey();
			
			log.info("p_C_Lead_ID=" + v_C_Lead_ID);
			if (v_C_Lead_ID == 0)
				throw new Exception ("Lead not found -  p_C_Lead_ID=" +  v_C_Lead_ID);
			
			MLead lead=new MLead(Env.getCtx(), v_C_Lead_ID, trx.getTrxName());
					
			// Check if Company and Contact Name is empty
			// Check if Company Name or Contact Name is empty
			String company = lead.getCompanyName();
			String contact = lead.getContactName();
			if ((company == null || company.trim().isEmpty()) && (contact == null || contact.trim().isEmpty())) {
			    sb.append(" | Missing Company or Contact Name for Lead: ").append(lead.getDocumentNo());
			    continue;
			}
			
			// Check for existing opportunity
		    if (lead.getC_SalesOpportunity_ID() > 0) {
		        MSalesOpportunity existingOpp = new MSalesOpportunity(Env.getCtx(), lead.getC_SalesOpportunity_ID(), trx.getTrxName());
		        sb.append(" | Opportunity exists: " + existingOpp.getDocumentNo());
		        continue;
		    }
				
			/// Create or select Customer details
			if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Prospect))
			{
				v_C_BPartner_ID=lead.getRef_BPartner_ID();
				v_C_BPartnerLocation_ID=lead.getRef_BPartner_Location_ID();
				v_Ad_User_ID=lead.getRef_User_ID();
			}
			else if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Customer))
			{
				v_C_BPartner_ID=lead.getC_BPartner_ID();
				v_C_BPartnerLocation_ID=lead.getC_BPartner_Location_ID();
				v_Ad_User_ID=lead.getAD_User_ID();
			}
			else // For New Prospect
			{
				MUser existingUser = null;

				if (lead.getEMail() != null && !lead.getEMail().trim().isEmpty()) {
					existingUser = new Query(Env.getCtx(), MUser.Table_Name, "Upper(Email) = ? AND AD_Client_ID = ?", trx.getTrxName())
						.setParameters(lead.getEMail().trim().toUpperCase(), Env.getAD_Client_ID(Env.getCtx()))
						.first();
				} else if (lead.getCompanyName() != null && !lead.getCompanyName().trim().isEmpty()) {
					Integer mBpartnerID = new Query(Env.getCtx(), MBPartner.Table_Name, " Upper(Name) = ? AND AD_Client_ID = ?", trx.getTrxName())
						.setParameters(lead.getCompanyName().trim().toUpperCase(), Env.getAD_Client_ID(Env.getCtx()))
						.firstId();

					if (mBpartnerID != null && mBpartnerID > 0) {
						existingUser = new Query(Env.getCtx(), I_AD_User.Table_Name, "C_BPartner_ID=?", trx.getTrxName())
							.setParameters(mBpartnerID)
							.setOnlyActiveRecords(true)
							.first();

						if (existingUser == null)
						{
							//throw new CyprusException("User/ Contact not created for " + lead.getCompanyName());
							sb.append(" | User/ Contact not created for " + lead.getCompanyName());
					        continue;
						}
							
					}
				}

				if (existingUser != null) {
					v_Ad_User_ID = existingUser.getAD_User_ID();
					v_C_BPartner_ID = existingUser.getC_BPartner_ID();

					MBPartnerLocation loc = new Query(Env.getCtx(), MBPartnerLocation.Table_Name, "C_BPartner_ID=? AND IsActive='Y'", trx.getTrxName())
							.setParameters(v_C_BPartner_ID)
							.setOrderBy("Created DESC")
							.first();
					if (loc != null)
						v_C_BPartnerLocation_ID = loc.getC_BPartner_Location_ID();

					lead.setRef_User_ID(v_Ad_User_ID);
					lead.setRef_BPartner_ID(v_C_BPartner_ID);
					lead.setRef_BPartner_Location_ID(v_C_BPartnerLocation_ID);
					lead.saveEx(trx.getTrxName());
				}
				else {

					MBPartner prospectCust=lead.createProspectBPartner();
					if(prospectCust!=null)
					{
						v_C_BPartner_ID=prospectCust.getC_BPartner_ID();
						/// Location
						MBPartnerLocation prospectLocation=lead.createBPartnerLocation(prospectCust);
						if(prospectLocation!=null)
							v_C_BPartnerLocation_ID=prospectLocation.getC_BPartner_Location_ID();
						// user
						MUser prospectuser=lead.createProspectUser(prospectCust);
						if(prospectuser!=null)
							v_Ad_User_ID=prospectuser.getAD_User_ID();
					}

				}
			}
				
			MQuery where=new MQuery(MLeadInfo.Table_Name);
			where.addRestriction(MLeadInfo.COLUMNNAME_C_Lead_ID, MQuery.EQUAL, v_C_Lead_ID);
			where.addRestriction(MLeadInfo.COLUMNNAME_AD_Client_ID, MQuery.EQUAL, Env.getAD_Client_ID(Env.getCtx()));
			List<MLeadInfo> leadInfo = new Query(Env.getCtx(), MLeadInfo.Table_Name, where.getWhereClause(), trx.getTrxName())
			.setOnlyActiveRecords(true).setClient_ID()
			.list();
					
			/// Create Header of Sales Opportunity
			if(saleOpp==null)
			{
				saleOpp=new MSalesOpportunity(lead);
				if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Customer))
				{
					saleOpp.setC_BPartner_ID(v_C_BPartner_ID);
					saleOpp.setC_BPartner_Location_ID(v_C_BPartnerLocation_ID);
					saleOpp.setAD_User_ID(v_Ad_User_ID);
				}
				else
				{
					saleOpp.setRef_BPartner_ID(v_C_BPartner_ID);
					saleOpp.setRef_BPartner_Location_ID(v_C_BPartnerLocation_ID);
					saleOpp.setRef_User_ID(v_Ad_User_ID);
				}
				
				// Updated Payment Term and TenderType according to Business Partner
				MBPartner bp=new MBPartner(Env.getCtx(), v_C_BPartner_ID, trx.getTrxName());
				if(bp.getC_PaymentTerm_ID()>0)
					saleOpp.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
				else
				{
					// Set default Payment Term if not defined in Business Partner
				    int defaultPaymentTermID = DB.getSQLValueEx(trx.getTrxName(),
				        "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE IsDefault='Y' AND AD_Client_ID = ?", lead.getAD_Client_ID());
				    if (defaultPaymentTermID > 0) {
				        saleOpp.setC_PaymentTerm_ID(defaultPaymentTermID);
				    }
				}
				if(bp.getPaymentRule()!=null && bp.getPaymentRule().length()>0)
					saleOpp.setPaymentRule(bp.getPaymentRule());
				
				// Update Sales Rep..
				saleOpp.set_ValueNoCheck("SalesRep_ID", lead.getSalesRep_ID());
				saleOpp.setC_Currency_ID(lead.getC_Currency_ID()); // Updated by Mukesh @20250624
				/// save Sales Opportunity
				if(saleOpp.save(trx.getTrxName()))
				{
					int intNo=DB.executeUpdateEx("Update C_Lead set Status=?, C_SalesOpportunity_ID=? Where C_Lead_ID=?", new Object[] {"19",saleOpp.getC_SalesOpportunity_ID(),lead.getC_Lead_ID()}, trx.getTrxName());
					log.info("Sales Opportunity ID has updated at Lead "+intNo);
					try {
						DB.commit(true, trx.getTrxName());
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					DB.rollback(true, trx.getTrxName());
				}
			}
			
			/// Create Line of Sales Opportunity
			if(saleOpp!=null && leadInfo.size()>0)
			{
				for(MLeadInfo leadLine : leadInfo)
				{
					MOpportunityLine line=new MOpportunityLine(saleOpp);
					line.setC_LeadInfo_ID(leadLine.getC_LeadInfo_ID());
					if(leadLine.getM_Product_ID()>0)
					{
						line.setM_Product_ID(leadLine.getM_Product_ID());
						line.setM_AttributeSetInstance_ID(leadLine.getM_AttributeSetInstance_ID());
					}
					else
						line.setC_Charge_ID(leadLine.getC_Charge_ID());
					line.setDescription(leadLine.getDescription());
					line.setPlannedQty(leadLine.getPlannedQty());
					line.setC_UOM_ID(leadLine.getC_UOM_ID());
					line.setBaseQty(leadLine.getBaseQty());
					line.setPlannedPrice(leadLine.getPlannedPrice());
					line.setPriceList(leadLine.getPriceList());
					line.setBasePrice(leadLine.getBasePrice());
					line.setC_UOM_ID(leadLine.getC_UOM_ID());  // Updated by Mukesh @20250624
					line.setDiscount(leadLine.getDiscount());
					line.setLineNetAmt(leadLine.getLineNetAmt());

					// below code should be updated while on Save method of Sales Opportunity line window
					// Below code updated for for Tax update from Lead Info to Sales Opportunity line window
					Integer taxID=leadLine.get_ValueAsInt("C_Tax_ID");
					if(taxID>0)
					{						
						BigDecimal taxAmt=leadLine.get_ValueAsBigDecimal("TaxAmt");
						line.setC_Tax_ID(taxID);
						line.setTaxAmt(taxAmt);
						line.setLineTotalAmount(leadLine.getLineNetAmt().add(taxAmt));
					}					
					else
					{
						// For Default Tax /// It should be on Save of Opportunity line window ... Required to implement it 
						MTax tax = new Query(Env.getCtx(), MTax.Table_Name, "IsDefault=?", null)
							.setClient_ID()	
							.setOnlyActiveRecords(true)
							.setParameters("Y")
							.firstOnly();
						BigDecimal taxAmount=UtilTax.getTaxAmountFromStdPrecision(leadLine.getLineNetAmt(), tax.getC_Tax_ID(), lead.getC_Currency_ID());
						line.setC_Tax_ID(tax.getC_Tax_ID());
						line.setTaxAmt(taxAmount);
						line.setLineTotalAmount(leadLine.getLineNetAmt().add(taxAmount));
					}
					if(line.save(trx.getTrxName()))
					{
						try {
							DB.commit(true, trx.getTrxName());
						} catch (IllegalStateException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			if(saleOpp!=null)
			{
				/// Also update New Customer to Prospect Customer at Lead window 
				if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_New) )
				lead.setBPType(saleOpp);
				lead.setStatus(MLead.STATUS_ConvertedToLead);
				lead.setProcessed(true);
				String sql="update C_LeadInfo set processed='Y' where C_Lead_ID="+saleOpp.getC_Lead_ID();
				int no = DB.executeUpdate(sql, trx.getTrxName());
				if(lead.save(trx.getTrxName()))
				{	
					try {
						DB.commit(true, trx.getTrxName());
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				sb.append(" | Created : "+saleOpp.getDocumentNo());
			}
			
		}		
		return sb;
		
	}



	public static StringBuilder completeInvoice(Trx trx, ArrayList<KeyNamePair> keyNamePairVal) 
	{
		StringBuilder sb = new StringBuilder("Sales Invoice Status : ");
		boolean overallSuccess = true; // Track success of entire batch
		try {
			for (KeyNamePair data : keyNamePairVal) {
				
				Integer v_C_Invoice_ID = data.getKey();
				log.info("v_C_SalesOrder_ID=" + v_C_Invoice_ID);

				if (v_C_Invoice_ID == 0) {
					sb.append(" | Error: Sales Invoice not found");
					overallSuccess = false;
					continue;
				}

				MInvoice invoice = new MInvoice(Env.getCtx(), v_C_Invoice_ID, trx.getTrxName());
				if(invoice!=null)
				{
					// Update order status
					if(invoice.processIt(MInvoice.DOCACTION_Complete))
					{
						invoice.setDocStatus(MInvoice.DOCACTION_Complete);
						invoice.setDocAction(MInvoice.DOCACTION_Close);
						invoice.setProcessed(true);

					}					
					if (!invoice.save(trx.getTrxName())) {
						sb.append(" | Error updating Invoice Order: " + invoice.getDocumentNo());
						overallSuccess = false;
						continue;
					}
					sb.append(" | Completed Invoice : " + invoice.getDocumentNo());
				}
			}

		}
		catch (Exception e) {
			sb.append(" | Error processing invoice : " + e.getMessage());
			overallSuccess = false;
			log.log(Level.SEVERE, "Error processing order", e);
		}
				
		// Commit or rollback based on overall success
		if (overallSuccess) {
			trx.commit();
		} else {
			trx.rollback();
			sb.insert(0, "Transaction rolled back due to errors. ");
		}

		return sb;
	}



	public static List<SendMailData> createLeadEmailData(Trx trx, ArrayList<KeyNamePair> keyNamePairVal,
			int selectedMailTempId) throws Exception {
		
			List<SendMailData> sendMailDatas = new LinkedList<>();
			MMailText mailText = new MMailText(Env.getCtx(), selectedMailTempId, null);

			for (KeyNamePair data : keyNamePairVal) {
			    Integer v_C_Lead_ID = data.getKey();
			    log.info("p_C_Lead_ID=" + v_C_Lead_ID);
			    
			    if (v_C_Lead_ID == 0) {
			        throw new IllegalArgumentException("Lead not found - p_C_Lead_ID=" + v_C_Lead_ID);
			    }

			    MLead lead = new MLead(Env.getCtx(), v_C_Lead_ID, trx.getTrxName());
			    SendMailData sendMailData = new SendMailData();
			    StringBuilder toEmails = new StringBuilder();

			    // 1. Get lead email (primary recipient)
			    String leadEmail = lead.getEMail();
			    if (leadEmail != null && !leadEmail.trim().isEmpty()) {
			        toEmails.append(leadEmail).append(",");
			    } else {
			        // 2. Fallback logic if lead email is empty
			        if (MLead.BPTYPE_Prospect.equalsIgnoreCase(lead.getBPType()) && lead.getRef_User_ID() > 0) {
			            MUser prospectUser = MUser.get(Env.getCtx(), lead.getRef_User_ID());
			            String prospectEmail = prospectUser.getEMail();
			            if (prospectEmail != null && !prospectEmail.trim().isEmpty()) {
			                toEmails.append(prospectEmail).append(",");
			            }
			        } else if (MLead.BPTYPE_Customer.equalsIgnoreCase(lead.getBPType())) {
			            if (lead.getAD_User_ID() > 0) {
			                MUser customerUser = MUser.get(Env.getCtx(), lead.getAD_User_ID());
			                String customerEmail = customerUser.getEMail();
			                if (customerEmail != null && !customerEmail.trim().isEmpty()) {
			                    toEmails.append(customerEmail).append(",");
			                }
			            }
			        }
			    }

			    // 3. Add sales rep email
			    int salesRep_ID = lead.getSalesRep_ID();
			    if (salesRep_ID > 0) {
			        MUser salesRep = MUser.get(Env.getCtx(), salesRep_ID);
			        String salesRepEmail = salesRep.getEMail();
			        if (salesRepEmail != null && !salesRepEmail.trim().isEmpty()) {
			            toEmails.append(salesRepEmail).append(",");
			        }
			    }

			    // Remove trailing comma (if any)
			    if (toEmails.length() > 0) {
			        toEmails.setLength(toEmails.length() - 1);
			        sendMailData.setEmailsTo(toEmails.toString());
			    }

			    // ===== DYNAMIC FIELD REPLACEMENT =====
	    	
	    	    // Example: Replace @DocumentNo@ with lead.getDocumentNo()
	    	    String mailHeader = replacePlaceholdersDynamic(mailText.getMailHeader(), lead.get_ID(), MLead.Table_ID);
	    	    String mailBody = replacePlaceholdersDynamic(mailText.getMailText(), lead.get_ID(), MLead.Table_ID);
			    sendMailData.setEmailsBcc("apitestbymukesh@gmail.com"); // TODO: Replace with configurable value
			    sendMailData.setEmailsBody(mailBody);
			    sendMailData.setEmailsHeader(mailHeader);
			    sendMailData.setEmailIsHtml(mailText.isHtml());
			    sendMailDatas.add(sendMailData);
			}

			return sendMailDatas;
	}
		
	private static String replacePlaceholdersDynamic(String template, int recordID, int tableID) {
	    if (template == null || template.isEmpty() || recordID <= 0 || tableID <= 0) {
	        return template;
	    }

	    // Load the record
	    PO po = MTable.get(Env.getCtx(), tableID).getPO(recordID, null);
	    if (po == null) {
	        return template;
	    }

	    // Pattern to match placeholders like @ColumnName@
	    Pattern pattern = Pattern.compile("@(\\w+)@");
	    Matcher matcher = pattern.matcher(template);
	    StringBuffer sb = new StringBuffer();

	    while (matcher.find()) {
	        String columnName = matcher.group(1);
	        String replacement = getDisplayValue(po, tableID, columnName);
	        matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
	    }
	    matcher.appendTail(sb);
	    return sb.toString();
	}

	private static String getDisplayValue(PO po, int tableID, String columnName) {
	    try {
	        // Get column metadata
	    	
	        MColumn column = MColumn.get(Env.getCtx(), MColumn.getColumn_ID(MTable.getTableName(Env.getCtx(), tableID), columnName));
	        if (column == null) {
	            return "@" + columnName + "@"; // Return original if column not found
	        }

	        Object value = po.get_Value(columnName);
	        if (value == null) {
	            return "";
	        }

	        // Handle special reference types
	        switch (column.getAD_Reference_ID()) {
	            case DisplayType.TableDir:
	            case DisplayType.Table:
	            case DisplayType.Search:
	                return getReferenceDisplayValue(column, value);
	                
	            case DisplayType.List:
	                return getListDisplayValue(column, value);
	                
	            case DisplayType.Date:
	                return DisplayType.getDateFormat(DisplayType.Date).format(value);
	                
	            case DisplayType.DateTime:
	                return DisplayType.getDateFormat(DisplayType.DateTime).format(value);
	                
	            case DisplayType.YesNo:
	                return "Y".equals(value) ? "Yes" : "No";
	                
	            default:
	                return value.toString();
	        }
	    } catch (Exception e) {
	        return "@" + columnName + "@";
	    }
	}

	private static String getReferenceDisplayValue(MColumn column, Object value) {
	    if (!(value instanceof Integer)) {
	        return value.toString();
	    }
	   String sql="SELECT t.TableName FROM AD_Ref_Table r " +
		        "JOIN AD_Table t ON (r.AD_Table_ID=t.AD_Table_ID) " +
		        "WHERE r.AD_Reference_ID=?";
	    // Get referenced table name
	    String refTableName = DB.getSQLValueString(null,
	        sql, 
	        column.getAD_Reference_Value_ID());
	    
	    if (refTableName != null) {
	        PO refPO = MTable.get(Env.getCtx(), refTableName).getPO((Integer)value, null);
	        if (refPO != null) {
	            return refPO.getDisplayValue();
	        }
	    }
	    else
	    {
	    	String tableName=column.getColumnName().replace("_ID", "");	
	    	PO refPO = MTable.get(Env.getCtx(), tableName).getPO((Integer)value, null);
	        if (refPO != null) {
	            return refPO.getDisplayValue();
	        }
	    }
	    return value.toString();
	}

	private static String getListDisplayValue(MColumn column, Object value) {
	    if (value == null) {
	        return "";
	    }
	    // Get the reference value ID (AD_Reference_Value_ID)
	    int referenceValueId = column.getAD_Reference_Value_ID();
	    if (referenceValueId <= 0) {
	        return value.toString();
	    }

	    // Query to get the display value
	    String sql = "SELECT r.Name FROM AD_Ref_List r " +
	                "WHERE r.AD_Reference_ID = ? " +
	                "AND r.Value = ? " +
	                "AND r.IsActive = 'Y'";

	    String displayValue = DB.getSQLValueStringEx(null, sql, referenceValueId, value.toString());
	    
	    return displayValue != null ? displayValue : value.toString();
	}

	
	
}
