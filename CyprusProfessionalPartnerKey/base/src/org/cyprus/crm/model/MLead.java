/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 cyprusbrs, Inc. All Rights Reserved.                *
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
 * cyprusbrs, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@cyprusbrs.org or http://www.cyprusbrs.org/license.html           *
 *****************************************************************************/
package org.cyprus.crm.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;

import org.cyprus.exceptions.AdempiereException;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MLocation;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Util;

/**
 * 	Bank Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MBank.java,v 1.2 2006/07/30 00:51:05 jjanke Exp $
 */
public class MLead extends X_C_Lead
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3459010882027283811L;
	
	int c_BPartnerLocation_ID = 0;
	
	private MLeadInfo[] 	m_lines = null;
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param C_Bank_ID bank
	 *	@param trxName trx
	 */
	public MLead (Properties ctx, int C_Lead_ID, String trxName)
	{
		super (ctx, C_Lead_ID, trxName);
	}	//	MBank

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MLead (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLead

	
	/**
	 * 	Called before Save for Pre-Save Operation
	 * 	@param newRecord new record
	 *	@return true if record can be saved
	 */
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
	    System.out.println("Before Save " + toString());

	    if (!newRecord) {
	        List<MLead> list = new Query(getCtx(), I_C_Lead.Table_Name, "C_Lead_ID=?", get_TrxName())
	                .setParameters(get_ID())
	                .list();

	        if (list.isEmpty())
	            return false;

	        MLead lead = list.get(0);
	        MLeadHistory history = MLeadHistory.createLeadHistory(lead);
	        boolean hasChanges = false;

	        // Helper to compare and assign values
	        BiFunction<Object, Object, Boolean> isChanged = (oldVal, newVal) -> !Objects.equals(oldVal, newVal);

	        // Compare and update fields
	        if (lead.getDocumentNo()!=null)// && isChanged.apply(lead.getDocumentNo(), getDocumentNo())) 
	        	{
	            history.setDocumentNo(lead.getDocumentNo());
	            hasChanges = true;
	        }

	        if (lead.getC_LeadSource_ID() > 0) // && lead.getC_LeadSource_ID() != getC_LeadSource_ID()) 
	        	{
	            history.setC_LeadSource_ID(lead.getC_LeadSource_ID());
	            hasChanges = true;
	        }

	        if (lead.getC_SalesRegion_ID() > 0 ){ // && lead.getC_SalesRegion_ID() != getC_SalesRegion_ID()) {
	            history.setC_SalesRegion_ID(lead.getC_SalesRegion_ID());
	            hasChanges = true;
	        }

	        if (lead.getName()!=null ) { //&& isChanged.apply(lead.getName(), getName())) {
	            history.setName(lead.getName());
	            hasChanges = true;
	        }

	        if (lead.getDescription()!=null ){// && isChanged.apply(lead.getDescription(), getDescription())) {
	            history.setDescription(lead.getDescription());
	            hasChanges = true;
	        }

	        if (lead.getEnquiryDate()!=null) {// && isChanged.apply(lead.getEnquiryDate(), getEnquiryDate())) {
	            history.setEnquiryDate(lead.getEnquiryDate());
	            hasChanges = true;
	        }

	        if (lead.getFollowupDate()!=null) {// && isChanged.apply(lead.getFollowupDate(), getFollowupDate())) {
	            history.setFollowupDate(lead.getFollowupDate());
	            hasChanges = true;
	        }

	        if (lead.getLeadRating()!=null) {// && isChanged.apply(lead.getLeadRating(), getLeadRating())) {
	            history.setLeadRating(getLeadRating());
	            hasChanges = true;
	        }

	        if (lead.getC_LeadQualification_ID() > 0 ) {//&& lead.getC_LeadQualification_ID() != getC_LeadQualification_ID()) {
	            history.setC_LeadQualification_ID(lead.getC_LeadQualification_ID());
	            hasChanges = true;
	        }

	        if (lead.getStatus()!=null ) {// && isChanged.apply(lead.getStatus(), getStatus())) {
	            history.setStatus(getStatus());
	            hasChanges = true;
	        }

	        if (lead.getC_Campaign_ID() > 0 ) {// && lead.getC_Campaign_ID() != getC_Campaign_ID()) {
	            history.setC_Campaign_ID(lead.getC_Campaign_ID());
	            hasChanges = true;
	        }

	        if (lead.getSummary()!=null ) {// && isChanged.apply(lead.getSummary(), getSummary())) {
	            history.setSummary(lead.getSummary());
	            hasChanges = true;
	        }

	        if (lead.getHelp()!=null ) {// && isChanged.apply(lead.getHelp(), getHelp())) {
	            history.setHelp(lead.getHelp());
	            hasChanges = true;
	        }

	        if (lead.getBPType()!=null) {// && isChanged.apply(lead.getBPType(), getBPType())) {
	            history.setBPType(lead.getBPType());
	            hasChanges = true;
	        }

	        if (lead.getURL()!=null) {// && isChanged.apply(lead.getURL(), getURL())) {
	            history.setURL(lead.getURL());
	            hasChanges = true;
	        }

	        if (lead.getC_BP_Group_ID()> 0) {//  && lead.getC_BP_Group_ID() != getC_BP_Group_ID()) {
	            history.setC_BP_Group_ID(lead.getC_BP_Group_ID());
	            hasChanges = true;
	        }

	        if (lead.getCompanyName()!=null) {// && isChanged.apply(lead.getCompanyName(), getCompanyName())) {
	            history.setCompanyName(lead.getCompanyName());
	            hasChanges = true;
	        }

	        if (lead.getC_BPartner_ID() > 0) {// && lead.getC_BPartner_ID() != getC_BPartner_ID()) {
	            history.setC_BPartner_ID(lead.getC_BPartner_ID());
	            hasChanges = true;
	        }

	        if (lead.getC_BPartner_Location_ID() > 0) {// && lead.getC_BPartner_Location_ID() != getC_BPartner_Location_ID()) {
	            history.setC_BPartner_Location_ID(lead.getC_BPartner_Location_ID());
	            hasChanges = true;
	        }

	        if (lead.getAD_User_ID() > 0) {// && lead.getAD_User_ID() != getAD_User_ID()) {
	            history.setAD_User_ID(lead.getAD_User_ID());
	            hasChanges = true;
	        }

	        if (lead.getRef_BPartner_ID() > 0) {// && lead.getRef_BPartner_ID() != getRef_BPartner_ID()) {
	            history.setRef_BPartner_ID(lead.getRef_BPartner_ID());
	            hasChanges = true;
	        }

	        if (lead.getRef_BPartner_Location_ID() > 0 ) {// && lead.getRef_BPartner_Location_ID() != getRef_BPartner_Location_ID()) {
	            history.setRef_BPartner_Location_ID(lead.getRef_BPartner_Location_ID());
	            hasChanges = true;
	        }

	        if (lead.getRef_User_ID() > 0 ) {// && lead.getRef_User_ID() != getRef_User_ID()) {
	            history.setRef_User_ID(lead.getRef_User_ID());
	            hasChanges = true;
	        }

	        if (lead.getAddress1()!=null) {// && isChanged.apply(lead.getAddress1(), getAddress1())) {
	            history.setAddress1(lead.getAddress1());
	            hasChanges = true;
	        }

	        if (lead.getAddress2()!=null) {// && isChanged.apply(lead.getAddress2(), getAddress2())) {
	            history.setAddress2(lead.getAddress2());
	            hasChanges = true;
	        }

	        if (lead.getC_City_ID() > 0) {// && lead.getC_City_ID() != getC_City_ID()) {
	            history.setC_City_ID(lead.getC_City_ID());
	            hasChanges = true;
	        }

	        if (lead.getCity()!=null) {// && isChanged.apply(lead.getCity(), getCity())) {
	            history.setCity(lead.getCity());
	            hasChanges = true;
	        }

	        if (lead.getC_Region_ID() > 0) {// && lead.getC_Region_ID() != getC_Region_ID()) {
	            history.setC_Region_ID(lead.getC_Region_ID());
	            hasChanges = true;
	        }

	        if (lead.getRegionName()!=null) {// && isChanged.apply(lead.getRegionName(), getRegionName())) {
	            history.setRegionName(lead.getRegionName());
	            hasChanges = true;
	        }

	        if (lead.getC_Country_ID() > 0) {// && lead.getC_Country_ID() != getC_Country_ID()) {
	            history.setC_Country_ID(lead.getC_Country_ID());
	            hasChanges = true;
	        }

	        if (lead.getPostal()!=null) {// && isChanged.apply(lead.getPostal(), getPostal())) {
	            history.setPostal(lead.getPostal());
	            hasChanges = true;
	        }

	        if (lead.getC_Greeting_ID() > 0) {// && lead.getC_Greeting_ID() != getC_Greeting_ID()) {
	            history.setC_Greeting_ID(lead.getC_Greeting_ID());
	            hasChanges = true;
	        }

	        if (lead.getContactName()!=null) {// && isChanged.apply(lead.getContactName(), getContactName())) {
	            history.setContactName(lead.getContactName());
	            hasChanges = true;
	        }

	        if (lead.getMobile()!=null) {// && isChanged.apply(lead.getMobile(), getMobile())) {
	            history.setMobile(lead.getMobile());
	            hasChanges = true;
	        }

	        if (lead.getPhone()!=null) {// && isChanged.apply(lead.getPhone(), getPhone())) {
	            history.setPhone(lead.getPhone());
	            hasChanges = true;
	        }

	        if (lead.getPhone2()!=null) {// && isChanged.apply(lead.getPhone2(), getPhone2())) {
	            history.setPhone2(lead.getPhone2());
	            hasChanges = true;
	        }

	        if (lead.getFax()!=null) {// && isChanged.apply(lead.getFax(), getFax())) {
	            history.setFax(lead.getFax());
	            hasChanges = true;
	        }

	        if (lead.getEMail()!=null) {// && isChanged.apply(lead.getEMail(), getEMail())) {
	            history.setEMail(lead.getEMail());
	            hasChanges = true;
	        }

	        if (lead.getC_Job_ID() > 0) {// && lead.getC_Job_ID() != getC_Job_ID()) {
	            history.setC_Job_ID(lead.getC_Job_ID());
	            hasChanges = true;
	        }

	        if (lead.getTitle()!=null) {// && isChanged.apply(lead.getTitle(), getTitle())) {
	            history.setTitle(lead.getTitle());
	            hasChanges = true;
	        }

	        if (lead.getC_LeadResponseMaster_ID() > 0) {// && lead.getC_LeadResponseMaster_ID() != getC_LeadResponseMaster_ID()) {
	            history.setC_LeadResponseMaster_ID(lead.getC_LeadResponseMaster_ID());
	            hasChanges = true;
	        }

	        if (lead.getLeadResponseDetails()!=null) {// && isChanged.apply(lead.getLeadResponseDetails(), getLeadResponseDetails())) {
	            history.setLeadResponseDetails(lead.getLeadResponseDetails());
	            hasChanges = true;
	        }

	        if (lead.getM_Warehouse_ID() > 0) {// && lead.getM_Warehouse_ID() != getM_Warehouse_ID()) {
	            history.setM_Warehouse_ID(lead.getM_Warehouse_ID());
	            hasChanges = true;
	        }

	        if (lead.getM_PriceList_ID() > 0) {// && lead.getM_PriceList_ID() != getM_PriceList_ID()) {
	            history.setM_PriceList_ID(lead.getM_PriceList_ID());
	            hasChanges = true;
	        }

	        if (lead.getC_Currency_ID() > 0) {// && lead.getC_Currency_ID() != getC_Currency_ID()) {
	            history.setC_Currency_ID(lead.getC_Currency_ID());
	            hasChanges = true;
	        }

	        if (lead.getC_ConversionType_ID() > 0) {// && lead.getC_ConversionType_ID() != getC_ConversionType_ID()) {
	            history.setC_ConversionType_ID(lead.getC_ConversionType_ID());
	            hasChanges = true;
	        }

	        if (lead.getTotalLines()!=null) {// && isChanged.apply(lead.getTotalLines(), getTotalLines())) {
	            history.setTotalLines(lead.getTotalLines());
	            hasChanges = true;
	        }

//	        if (!hasChanges) {
//	            System.out.println("No changes detected — record will not be saved.");
//	            return false;
//	        }

	        if (hasChanges && !history.save(lead.get_TrxName())) {
	            throw new AdempiereException("Lead history is not saved");
	        }
	    }

	    return true;
	}

	
	
	/**
	 * @param lead
	 * @return
	 * Create Prospect Customer
	 */
	public MBPartner createProspectBPartner() {
			
		MBPartner partner=new MBPartner(getCtx(), 0, get_TrxName());
//		partner.setValue(10101+""); /// Need to ask question to Surya
		partner.setName((getCompanyName()!=null && getCompanyName().length()>0)?getCompanyName():getContactName());
		partner.setURL(getURL());
		partner.setC_BP_Group_ID(getC_BP_Group_ID());
		//partner.setC_Greeting_ID(getC_Greeting_ID());
		partner.setIsProspect(true); /// Prospect Customer
		if(partner.save(get_TrxName()))
			return partner;	
		return null;
	}
	
	/**
	 * @param lead
	 * @return
	 */
	public MBPartnerLocation createBPartnerLocation(MBPartner bPartner) {
		// TODO Auto-generated method stub
		MBPartnerLocation bPlocation=null;
		if(bPartner!=null)
		{	
			/// Create Location of BpLocation
			MLocation location=new MLocation(getCtx(), getC_Country_ID(), getC_Region_ID(), getCity(), get_TrxName());
			location.setAddress1(getAddress1());
			location.setAddress2(getAddress2());
			location.setPostal(getPostal());
			location.setRegionName(getRegionName()!=null?getRegionName():"");
			location.setCity(getCity()!=null?getCity():"");

			if(location.save(get_TrxName()))
			{	
				bPlocation=new MBPartnerLocation(bPartner);
				bPlocation.setC_Location_ID(location.getC_Location_ID());
//				bPlocation.setPhone(getPhone());
//				bPlocation.setPhone2(getPhone2());
//				bPlocation.setFax(getFax());
//				bPlocation.setName(getAddress1()!=null?getAddress1():getC_Country_ID()+"");
				String region = DB.getSQLValueString(get_TrxName(), " SELECT Name FROM C_Region  WHERE C_Region_ID = ? ", new Object[] { Integer.valueOf(getC_Region_ID()) });
//				bPlocation.setName(getAddress1()!=null?getAddress1():getC_Country_ID()+"");
				bPlocation.setName(getAddress1()!=null?getAddress1():region+"");
				//bPlocation.setName(getCity()!=null?getCity():".");
				if(bPlocation.save(get_TrxName()))
				{
					c_BPartnerLocation_ID = bPlocation.getC_BPartner_Location_ID();
					return bPlocation;
				}					
			}
		}
		return bPlocation;
	}
	
	/**
	 * @param lead
	 * @return
	 */
	public MUser createProspectUser(MBPartner prospectBPartner) {

		MUser user=null;
		if(prospectBPartner!=null && getContactName()!=null)
		{	
			user=new MUser(prospectBPartner);
			user.setTitle(getTitle());
			user.setEMail(getEMail());
			user.setPhone(getPhone());
			user.setPhone2(getPhone2());
			user.setName(getContactName());
			user.setFax(getFax());
			user.setC_Job_ID(getC_Job_ID());
			user.setC_BPartner_Location_ID(c_BPartnerLocation_ID);
			user.setC_Greeting_ID(getC_Greeting_ID());
			if(user.save(get_TrxName()))
				return user;
		}
		return user;
	}
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MLead[");
		sb.append (get_ID ()).append ("-").append(getName ()).append ("]");
		return sb.toString ();
	}	//	toString

	/**
	 * @param saleOpp
	 */
	public void setBPType(MSalesOpportunity saleOpp) {
		
		if(saleOpp!=null)
		{
			setBPType(BPTYPE_Prospect);
			setRef_BPartner_ID(saleOpp.getRef_BPartner_ID());
			setRef_BPartner_Location_ID(saleOpp.getRef_BPartner_Location_ID());
			setRef_User_ID(saleOpp.getRef_User_ID());
		}
	}
	
	public MLeadInfo[] getLines()
	{
		return getLines(false, null);
	}	//	getLines
	
	public MLeadInfo[] getLines (boolean requery, String orderBy)
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
	
	public MLeadInfo[] getLines (String whereClause, String leadClause)
	{
		StringBuffer whereClauseFinal = new StringBuffer(MLeadInfo.COLUMNNAME_C_Lead_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (leadClause.length() == 0)
			leadClause = MOrderLine.COLUMNNAME_Line;
		//
		List<MLeadInfo> list = new Query(getCtx(), I_C_LeadInfo.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(leadClause)
										.list();
		for (MLeadInfo li : list) {
			li.setHeaderInfo(this);
		}
		//
		return list.toArray(new MLeadInfo[list.size()]);		
	}	//	getLines
	
}	//	MBank
