/**
 * 
 */
package org.cyprus.crm.process;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import org.cyprus.crm.model.MLead;
import org.cyprus.crm.model.MLeadInfo;
import org.cyprus.crm.model.MOpportunityLine;
import org.cyprus.crm.model.MSalesOpportunity;
import org.cyprus.exceptions.CyprusException;
import org.cyprus.util.UtilTax;
import org.cyprusbrs.framework.I_AD_User;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MLocation;
import org.cyprusbrs.framework.MQuery;
import org.cyprusbrs.framework.MTax;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

/**
 * @author Mukesh
 * process name : org.cyprus.crm.process.GenerateSalesOpprtunity
 */
public class GenerateSalesOpprtunity extends SvrProcess {

	
	private static Logger log = Logger.getLogger(GenerateSalesOpprtunity.class.getName());

	/**	Lead ID			*/
	int v_C_Lead_ID = 0;
	
	int v_C_BPartner_ID=0;
	
	int v_C_BPartnerLocation_ID=0;
	
	int v_Ad_User_ID=0;
	
	MBPartner prospectBPartner=null;
	
	MSalesOpportunity saleOpp=null;
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		v_C_Lead_ID = getRecord_ID();
	}	
	@Override
	protected String doIt() throws Exception {
		
		String returnVal="Sales Opportunity Document No : ";
		log.info("p_C_Lead_ID=" + v_C_Lead_ID );
		if (v_C_Lead_ID == 0)
			throw new Exception ("Lead not found -  p_C_Lead_ID=" +  v_C_Lead_ID);
		
		MLead lead=new MLead(getCtx(), v_C_Lead_ID, get_TrxName());
		
		// Check for existing opportunity in single query
	    String oppCheckSQL = "SELECT o.DocumentNo " +
	                        "FROM C_Lead l " +
	                        "JOIN C_SalesOpportunity o ON (l.C_SalesOpportunity_ID = o.C_SalesOpportunity_ID) " +
	                        "WHERE l.C_Lead_ID=? " +
	                        "AND o.IsActive='Y' " +
	                        "AND l.IsActive='Y'";
	    
	    String existingDocNo = DB.getSQLValueStringEx(get_TrxName(), oppCheckSQL, v_C_Lead_ID);
	    
	    if (existingDocNo != null) {
	        String message = "Opportunity already exists with Document No: " + existingDocNo;
	        log.info(message);
	        return message;
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
				existingUser = new Query(getCtx(), MUser.Table_Name, "Upper(Email) = ? AND AD_Client_ID = ?", get_TrxName())
					.setParameters(lead.getEMail().trim().toUpperCase(), getAD_Client_ID())
					.first();
			} else if (lead.getCompanyName() != null && !lead.getCompanyName().trim().isEmpty()) {
				Integer mBpartnerID = new Query(getCtx(), MBPartner.Table_Name, " Upper(Name) = ? AND AD_Client_ID = ?", get_TrxName())
					.setParameters(lead.getCompanyName().trim().toUpperCase(), getAD_Client_ID())
					.firstId();

				if (mBpartnerID != null && mBpartnerID > 0) {
					existingUser = new Query(getCtx(), I_AD_User.Table_Name, "C_BPartner_ID=?", get_TrxName())
						.setParameters(mBpartnerID)
						.setOnlyActiveRecords(true)
						.first();

					if (existingUser == null)
						throw new CyprusException("User/ Contact not created for " + lead.getCompanyName());
				}
			}

			if (existingUser != null) {
				v_Ad_User_ID = existingUser.getAD_User_ID();
				v_C_BPartner_ID = existingUser.getC_BPartner_ID();

				MBPartnerLocation loc = new Query(getCtx(), MBPartnerLocation.Table_Name, "C_BPartner_ID=? AND IsActive='Y'", get_TrxName())
						.setParameters(v_C_BPartner_ID)
						.setOrderBy("Created DESC")
						.first();
				if (loc != null)
					v_C_BPartnerLocation_ID = loc.getC_BPartner_Location_ID();

				lead.setRef_User_ID(v_Ad_User_ID);
				lead.setRef_BPartner_ID(v_C_BPartner_ID);
				lead.setRef_BPartner_Location_ID(v_C_BPartnerLocation_ID);
				lead.saveEx(get_TrxName());
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
		where.addRestriction(MLeadInfo.COLUMNNAME_AD_Client_ID, MQuery.EQUAL, getAD_Client_ID());
		List<MLeadInfo> leadInfo = new Query(getCtx(), MLeadInfo.Table_Name, where.getWhereClause(), get_TrxName())
		.setOnlyActiveRecords(true).setClient_ID()
		.list();
		
		// For Default Tax /// It should be on Save of Opportunity line window ... Required to implement it 
		MTax tax = new Query(getCtx(), MTax.Table_Name, "IsDefault=?", null)
			.setClient_ID()	
			.setOnlyActiveRecords(true)
			.setParameters("Y")
			.firstOnly();
		
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
			MBPartner bp=new MBPartner(getCtx(), v_C_BPartner_ID, get_TrxName());
			if(bp.getC_PaymentTerm_ID()>0)
				saleOpp.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
			else
			{
				// Set default Payment Term if not defined in Business Partner
			    int defaultPaymentTermID = DB.getSQLValueEx(get_TrxName(),
			        "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE IsDefault='Y' AND AD_Client_ID = ?", getAD_Client_ID());
			    if (defaultPaymentTermID > 0) {
			        saleOpp.setC_PaymentTerm_ID(defaultPaymentTermID);
			    }
			}
			if(bp.getPaymentRule()!=null && bp.getPaymentRule().length()>0)
				saleOpp.setPaymentRule(bp.getPaymentRule());
			
			// update Sales Rep 
			saleOpp.set_ValueNoCheck("SalesRep_ID", lead.getSalesRep_ID());
			
			/// save Sales Opportunity
			if(saleOpp.save(get_TrxName()))
			{
				int intNo=DB.executeUpdateEx("Update C_Lead set C_SalesOpportunity_ID=? Where C_Lead_ID=?", new Object[] {saleOpp.getC_SalesOpportunity_ID(),lead.getC_Lead_ID()}, get_TrxName());
				log.info("Sales Opportunity ID has updated at Lead "+intNo);
				try {
					DB.commit(true, get_TrxName());
				} catch (IllegalStateException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				DB.rollback(true, get_TrxName());
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
				line.setDiscount(leadLine.getDiscount());
				line.setLineNetAmt(leadLine.getLineNetAmt());

				// below code should be updated while on Save method of Sales Opportunity line window
//				BigDecimal taxAmount=UtilTax.getTaxAmountFromStdPrecision(leadLine.getLineNetAmt(), tax.getC_Tax_ID(), lead.getC_Currency_ID());
//				line.setC_Tax_ID(tax.getC_Tax_ID());
//				line.setTaxAmt(taxAmount);
//				line.setLineTotalAmount(leadLine.getLineNetAmt().add(taxAmount));
				BigDecimal taxAmount=UtilTax.getTaxAmountFromStdPrecision(leadLine.getLineNetAmt(), tax.getC_Tax_ID(), lead.getC_Currency_ID());
				line.setC_Tax_ID(leadLine.get_ValueAsInt("C_Tax_ID"));
				line.setTaxAmt(leadLine.get_ValueAsBigDecimal("TaxAmt"));
				line.setLineTotalAmount(leadLine.getLineNetAmt().add(leadLine.get_ValueAsBigDecimal("TaxAmt")));

				if(line.save(get_TrxName()))
				{
					try {
						DB.commit(true, get_TrxName());
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
		///
		log.info("Length of Lead Info details "+leadInfo.size());
		if(false)
		leadInfo.stream().forEach(leadLine->{

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
				MBPartner bp=new MBPartner(getCtx(), v_C_BPartner_ID, get_TrxName());
				if(bp.getC_PaymentTerm_ID()>0)
					saleOpp.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
				if(bp.getPaymentRule()!=null && bp.getPaymentRule().length()>0)
					saleOpp.setPaymentRule(bp.getPaymentRule());
				
				/// save Sales Opportunity
				if(saleOpp.save(get_TrxName()))
				{
					int intNo=DB.executeUpdateEx("Update C_Lead set C_SalesOpportunity_ID=? Where C_Lead_ID=?", new Object[] {saleOpp.getC_SalesOpportunity_ID(),lead.getC_Lead_ID()}, get_TrxName());
					log.info("Sales Opportunity ID has updated at Lead "+intNo);
					try {
						DB.commit(true, get_TrxName());
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			/// Create Line of Sales Opportunity
			if(saleOpp!=null)
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
				line.setDiscount(leadLine.getDiscount());
				line.setLineNetAmt(leadLine.getLineNetAmt());
				
				// below code should be updated while on Save method of Sales Opportunity line window
				BigDecimal taxAmount=UtilTax.getTaxAmountFromStdPrecision(leadLine.getLineNetAmt(), tax.getC_Tax_ID(), lead.getC_Currency_ID());
				line.setC_Tax_ID(tax.getC_Tax_ID());
				line.setTaxAmt(taxAmount);
				line.setLineTotalAmount(leadLine.getLineNetAmt().add(taxAmount));
				
				if(line.save(get_TrxName()))
				{
					try {
						DB.commit(true, get_TrxName());
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		if(saleOpp!=null)
		{
			/// Also update New Customer to Prospect Customer at Lead window 
			if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_New) )
			lead.setBPType(saleOpp);
			lead.setStatus(MLead.STATUS_ConvertedToLead);
			lead.setProcessed(true);
			String sql="update C_LeadInfo set processed='Y' where C_Lead_ID="+saleOpp.getC_Lead_ID();
			int no = DB.executeUpdate(sql, get_TrxName());
			if(lead.save(get_TrxName()))
			{	
				try {
					DB.commit(true, get_TrxName());
				} catch (IllegalStateException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return returnVal.concat(saleOpp.getDocumentNo());
		}
		else
		return returnVal;
	}



	/**
	 * @param lead
	 * @return
	 */
	private int createADUser(MLead lead) {

		if(prospectBPartner!=null)
		{	
			MUser user=new MUser(prospectBPartner);
			user.setTitle(lead.getTitle());
			user.setEMail(lead.getEMail());
			user.setMobile(lead.getMobile());
			user.setName(lead.getContactName());
			user.setC_Greeting_ID(lead.getC_Greeting_ID());
			if(user.save(get_TrxName()))
				return user.getAD_User_ID();
		}
		return 0;
	}



	/**
	 * @param lead
	 * @return
	 */
	private int createBPartnerLocation(MLead lead) {
		// TODO Auto-generated method stub
		
		if(prospectBPartner!=null)
		{	
			MBPartner bPartner=new MBPartner(getCtx(), v_C_BPartner_ID, get_TrxName());		
			/// Create Location of BpLocation

			MLocation location=new MLocation(getCtx(), lead.getC_Country_ID(), lead.getC_Region_ID(), lead.getCity(), get_TrxName());
			location.setAddress1(lead.getAddress1());
			location.setAddress1(lead.getAddress2());
			location.setPostal(lead.getPostal());
			location.setRegionName(lead.getRegionName()!=null?lead.getRegionName():"");
			location.setCity(lead.getCity()!=null?lead.getCity():"");

			if(location.save(get_TrxName()))
			{	
				MBPartnerLocation bPlocation=new MBPartnerLocation(bPartner);
				bPlocation.setC_Location_ID(location.getC_Location_ID());
				bPlocation.setPhone(lead.getPhone());
				bPlocation.setPhone2(lead.getPhone2());
				bPlocation.setFax(lead.getFax());
				bPlocation.setName(lead.getCity()!=null?lead.getCity():".");
				if(bPlocation.save(get_TrxName()))
					return bPlocation.getC_BPartner_Location_ID();
			}
		}
		return 0;
	}



	/**
	 * @param lead
	 * @return
	 * Create Prospect Customer
	 */
	private int createProspectBPartner(MLead lead) {
		
		MBPartner partner=null;
		if(lead!=null)
		{
			partner=new MBPartner(getCtx(), 0, get_TrxName());
			partner.setValue(lead.getContactName()); /// Need to ask question to Surya
			partner.setName(lead.getName());
			partner.setURL(lead.getURL());
			partner.setC_BP_Group_ID(lead.getC_BP_Group_ID());
			partner.setC_Greeting_ID(lead.getC_Greeting_ID());
			partner.setIsProspect(true); /// Prospect Customer
			if(partner.save(get_TrxName()))
				return partner.getC_BPartner_ID();
		}
		return 0;
	}

}
