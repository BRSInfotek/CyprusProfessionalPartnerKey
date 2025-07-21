/**
 * 
 */
package org.cyprus.crm.process;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import org.cyprus.crm.model.MOpportunityLine;
import org.cyprus.crm.model.MSalesOpportunity;
import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

/**
 * @author Mukesh
 * org.cyprus.crm.process.GenerateSalesQuotation
 */
public class GenerateSalesQuotation extends SvrProcess {

	/**	Sales Opportunity ID			*/
	private int v_C_SalesOpportunity_ID = 0;
	
	private MOrder order=null; 
	
	/** Document Type of new Order	*/
	private int p_C_DocType_ID = 0;
	
	/** Sales Rep of new Order	*/
	private int p_SalesRep_ID = 0;
	
	/** Current Date and Time **/
	Timestamp currentTime=new Timestamp(System.currentTimeMillis());
	@Override
	protected void prepare() {
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_DocType_ID"))
				p_C_DocType_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("SalesRep_ID"))
				p_SalesRep_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		v_C_SalesOpportunity_ID=getRecord_ID();
				
	}

	@Override
	protected String doIt() throws Exception {
		
		StringBuffer returnVal=new StringBuffer("Sales Quotation No : ");
		log.info("v_C_SalesOpportunity_ID=" + v_C_SalesOpportunity_ID );
		if (v_C_SalesOpportunity_ID == 0)
			throw new Exception ("Sales Opportunity not found -  v_C_SalesOpportunity_ID=" +  v_C_SalesOpportunity_ID);
		
		MSalesOpportunity saleOpp=new MSalesOpportunity(getCtx(), v_C_SalesOpportunity_ID, get_TrxName());
		
		final String whereClause = MOpportunityLine.COLUMNNAME_C_SalesOpportunity_ID+"=? ";
		List<MOpportunityLine> lines = new Query(getCtx(),MOpportunityLine.Table_Name,whereClause,get_TrxName())
		.setParameters(saleOpp.getC_SalesOpportunity_ID())
		.list();
		
		if (lines == null || lines.isEmpty()) {
		    throw new CyprusException("Sales Quotation cannot be created: No Opportunity Lines found for this Sales Opportunity.");
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
			
			MBPartner bp=new MBPartner(getCtx(), C_BPartner_ID, get_TrxName()); /// Here we need also check Prospect customer
			
			Integer paymentTerm=0;
			if(saleOpp.getC_PaymentTerm_ID()>0)
				paymentTerm=saleOpp.getC_PaymentTerm_ID();
			else if (bp.getC_PaymentTerm_ID()>0)
				paymentTerm=bp.getC_PaymentTerm_ID();
			else
				return "Payment Term is not defined";
			
			order=new MOrder(getCtx(), 0, get_TrxName());
			
			order.setIsSOTrx(true);
			if (p_C_DocType_ID != 0)
				order.setC_DocTypeTarget_ID(p_C_DocType_ID);
			else
				order.setC_DocTypeTarget_ID();
			order.setBPartner(bp);
			order.setC_BPartner_Location_ID(C_BPartnerLocation_ID);
			order.setAD_User_ID(AD_UserID);
			order.setSalesRep_ID(p_SalesRep_ID);
			order.setC_PaymentTerm_ID(paymentTerm);
			order.setPaymentRule(saleOpp.getPaymentRule());
			order.setDatePromised(saleOpp.getConversionDate());  /// Need to check all dates from Video Meeting
			order.setC_SalesOpportunity_ID(saleOpp.getC_SalesOpportunity_ID()); /// LInked to the Sales Quotation
			order.setM_Warehouse_ID(saleOpp.getM_Warehouse_ID());
			order.setC_Currency_ID(saleOpp.getC_Currency_ID());
			if(!order.save(get_TrxName()))
			{
				DB.rollback(true, get_TrxName());
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
//				ol.setQty(line.getPlannedQty());
				BigDecimal price = line.getPlannedPrice();
				ol.setPriceEntered(line.getPlannedPrice());
				ol.setPriceActual(line.getBasePrice());
//				ol.setPrice(price);
//				ol.setQty(line.getBaseQty());
				ol.setPriceList(line.getPriceList());
				ol.setDiscount(line.getDiscount());
				ol.setC_Tax_ID(line.getC_Tax_ID());
				if(!ol.save(get_TrxName()))
				{
					DB.rollback(true, get_TrxName());
				}
			}
		}
		
		if(order!=null)
		{	
			saleOpp.setC_Order_ID(order.getC_Order_ID());
			saleOpp.setProposalDate(currentTime);
			saleOpp.setStatus(MSalesOpportunity.STATUS_ConvertedToQuotation);
			saleOpp.setProcessed(true);
			if(saleOpp.save(get_TrxName()))
			{	
				int sqlNo=DB.executeUpdate(	"Update C_OpportunityLine set Processed='Y' where C_SalesOpportunity_ID="+saleOpp.getC_SalesOpportunity_ID(), get_TrxName());
				log.info("UPdated processed field in OpportunityLine "+sqlNo);
				try {
					DB.commit(true, get_TrxName());
				} catch (IllegalStateException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return returnVal.append(order.getDocumentNo()).toString();
		}
		else
		return null;
	}
	
	
	private MOrder createOrderFromOpportunity(MSalesOpportunity saleOpp, List<MOpportunityLine> lines) 
	{
		// Create Order and Order line
		
				lines.stream().forEach(line->
				{
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
						MBPartner bp=new MBPartner(getCtx(), C_BPartner_ID, get_TrxName()); /// Here we need also check Prospect customer
						order=new MOrder(getCtx(), 0, get_TrxName());
						
						order.setIsSOTrx(true);
						if (p_C_DocType_ID != 0)
							order.setC_DocTypeTarget_ID(p_C_DocType_ID);
						else
							order.setC_DocTypeTarget_ID();
						order.setBPartner(bp);
						order.setC_BPartner_Location_ID(C_BPartnerLocation_ID);
						order.setAD_User_ID(AD_UserID);
						order.setSalesRep_ID(p_SalesRep_ID);
						order.setC_PaymentTerm_ID(saleOpp.getC_PaymentTerm_ID());
						order.setPaymentRule(saleOpp.getPaymentRule());
						order.setDatePromised(saleOpp.getConversionDate());  /// Need to check all dates from Video Meeting
						order.setC_SalesOpportunity_ID(saleOpp.getC_SalesOpportunity_ID()); /// LInked to the Sales Quotation
						order.saveEx();
						
					}
					
					/// Below need to check information again from Sales Opportunity window
					MOrderLine ol = new MOrderLine (order);
					
					if(line.getC_Charge_ID()>0)
						ol.setC_Charge_ID(line.getC_Charge_ID());
					else
					ol.setM_Product_ID(line.getM_Product_ID(),line.getC_UOM_ID());
					
					ol.setDescription(line.getDescription());
					ol.setQty(line.getPlannedQty());
					BigDecimal price = line.getPlannedPrice();
					ol.setPrice(price);
					ol.setQty(line.getBaseQty());
					ol.setC_Tax_ID(line.getC_Tax_ID());
					ol.saveEx();
					
				});	
				
				return order;
			}


}
