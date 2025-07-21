package org.cyprus.crm.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.util.UtilTax;
import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MCharge;
import org.cyprusbrs.framework.MPriceList;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductPrice;
import org.cyprusbrs.framework.MProductPricing;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.framework.MTax;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MUOMConversion;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class CalloutSalesOpportunity extends CalloutEngine {

	/**
	 * 
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	// Callout path : org.cyprus.crm.model.CalloutSalesOpportunity.leadDetails
	public String leadDetails (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer c_Lead_ID = (Integer) mField.getValue();
		if (c_Lead_ID == null || c_Lead_ID.intValue() == 0)
			return "";
//		List<MLead> listLead = new Query(ctx, MLead.Table_Name, "C_Lead_ID=?", null)
//				.setParameters(c_Lead_ID)
//				.list();
//		MLead lead = listLead.stream().findFirst().get();
		
		MLead lead = new Query(ctx, MLead.Table_Name, "C_Lead_ID=?", null)
				.setParameters(c_Lead_ID)
				.firstOnly();
		
		if(lead!=null)
		{
			mTab.setValue(MSalesOpportunity.COLUMNNAME_Status, lead.getStatus());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_Description, lead.getDescription());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_EnquiryDate, lead.getEnquiryDate());
			if(lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_Customer))
			{	
				mTab.setValue(MSalesOpportunity.COLUMNNAME_C_BPartner_ID, lead.getC_BPartner_ID());
				mTab.setValue(MSalesOpportunity.COLUMNNAME_C_BPartner_Location_ID, lead.getC_BPartner_Location_ID());
				mTab.setValue(MSalesOpportunity.COLUMNNAME_AD_User_ID, lead.getAD_User_ID());
			}
			else
			{
				mTab.setValue(MSalesOpportunity.COLUMNNAME_Ref_BPartner_ID, lead.getRef_BPartner_ID());
				mTab.setValue(MSalesOpportunity.COLUMNNAME_Ref_BPartner_Location_ID, lead.getRef_BPartner_Location_ID());
				mTab.setValue(MSalesOpportunity.COLUMNNAME_Ref_User_ID, lead.getRef_User_ID());
			}
			mTab.setValue(MSalesOpportunity.COLUMNNAME_M_Warehouse_ID, lead.getM_Warehouse_ID());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_M_PriceList_ID, lead.getM_PriceList_ID());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_C_Currency_ID, lead.getC_Currency_ID());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_C_ConversionType_ID, lead.getC_ConversionType_ID());
		}
		return "";
	}
	
	// Callout path : org.cyprus.crm.model.CalloutSalesOpportunity.leadInfoDetails
	public String leadInfoDetails (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer c_LeadInfo_ID = (Integer) mField.getValue();
		if (c_LeadInfo_ID == null || c_LeadInfo_ID.intValue() == 0)
			return "";
//		List<MLeadInfo> listLeadInfo = new Query(ctx, MLeadInfo.Table_Name, "C_LeadInfo_ID=?", null)
//				.setParameters(c_LeadInfo_ID)
//				.list();
//		MLeadInfo leadInfo = listLeadInfo.stream().findFirst().get();
		
		MLeadInfo leadInfo = new Query(ctx, MLeadInfo.Table_Name, "C_LeadInfo_ID=?", null)
				.setParameters(c_LeadInfo_ID)
				.firstOnly();
		
		MTax tax = new Query(ctx, MTax.Table_Name, "IsDefault=?", null)
			.setClient_ID()	
			.setOnlyActiveRecords(true)
			.setParameters("Y")
			.firstOnly();
		
		if(leadInfo!=null)
		{
			mTab.setValue(MOpportunityLine.COLUMNNAME_Description, leadInfo.getDescription());
			if(leadInfo.getM_Product_ID()>0)	
				mTab.setValue(MOpportunityLine.COLUMNNAME_M_Product_ID, leadInfo.getM_Product_ID());
			else	
				mTab.setValue(MOpportunityLine.COLUMNNAME_C_Charge_ID, leadInfo.getC_Charge_ID());
			mTab.setValue(MOpportunityLine.COLUMNNAME_M_AttributeSetInstance_ID, leadInfo.getM_AttributeSetInstance_ID());
			mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedQty, leadInfo.getPlannedQty());

			mTab.setValue(MOpportunityLine.COLUMNNAME_C_UOM_ID, leadInfo.getC_UOM_ID());
			mTab.setValue(MOpportunityLine.COLUMNNAME_BaseQty, leadInfo.getBaseQty());
			mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedPrice, leadInfo.getPlannedPrice());
			mTab.setValue(MOpportunityLine.COLUMNNAME_PriceList, leadInfo.getPriceList());
			mTab.setValue(MOpportunityLine.COLUMNNAME_BasePrice, leadInfo.getBasePrice());
			mTab.setValue(MOpportunityLine.COLUMNNAME_Discount, leadInfo.getDiscount());
			mTab.setValue(MOpportunityLine.COLUMNNAME_LineNetAmt, leadInfo.getLineNetAmt());
			mTab.setValue(MOpportunityLine.COLUMNNAME_LineTotalAmount, leadInfo.getLineNetAmt());

			if(tax!=null)
			{	
				int C_SalesOpportunity_ID = Env.getContextAsInt(ctx, WindowNo, WindowNo, MOpportunityLine.COLUMNNAME_C_SalesOpportunity_ID);
				int p_C_Currency_ID=DB.getSQLValue(null, "Select C_Currency_ID from C_SalesOpportunity Where C_SalesOpportunity_ID=?", C_SalesOpportunity_ID);
				BigDecimal taxAmount=UtilTax.getTaxAmountFromStdPrecision(leadInfo.getLineNetAmt(), tax.getC_Tax_ID(), p_C_Currency_ID);
				mTab.setValue(MOpportunityLine.COLUMNNAME_C_Tax_ID, tax.getC_Tax_ID());
				mTab.setValue(MOpportunityLine.COLUMNNAME_TaxAmt, taxAmount);
				mTab.setValue(MOpportunityLine.COLUMNNAME_LineTotalAmount, leadInfo.getLineNetAmt().add(taxAmount));
			}
			else
			mTab.setValue(MOpportunityLine.COLUMNNAME_C_Tax_ID, null);
		}
		return "";
	}
	
	// Callout path : org.cyprus.crm.model.CalloutSalesOpportunity.bpartnerDetails
	public String bpartnerDetails (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer bpartner_ID = (Integer) mField.getValue();
		if (bpartner_ID == null || bpartner_ID.intValue() == 0)
			return "";
		
		MBPartner partner=new MBPartner(ctx, bpartner_ID, null);
		
		// User
		MUser[] userAll=MUser.getOfBPartner(ctx, bpartner_ID, null);
		List<MUser> listUser=Arrays.asList(userAll);
		MUser user = listUser.stream().findFirst().get();
		
		/// Location
		MBPartnerLocation[] locationAll=MBPartnerLocation.getForBPartner(ctx, bpartner_ID,null); 
		List<MBPartnerLocation> listLocation=Arrays.asList(locationAll);
		MBPartnerLocation location = listLocation.stream().findFirst().get();
		
		if(partner.isProspect())
		{
			mTab.setValue(MSalesOpportunity.COLUMNNAME_Ref_User_ID, user.getAD_User_ID());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_Ref_BPartner_Location_ID, location.getC_BPartner_Location_ID());
		}
		else
		{
			mTab.setValue(MSalesOpportunity.COLUMNNAME_AD_User_ID, user.getAD_User_ID());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_C_BPartner_Location_ID, location.getC_BPartner_Location_ID());
			mTab.setValue(MSalesOpportunity.COLUMNNAME_M_PriceList_ID, partner.getM_PriceList_ID());
		}
		return ""; 	
	}
	
	// Callout path : org.cyprus.crm.model.CalloutSalesOpportunity.taxAmount
	public String taxAmount (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer tax_ID = (Integer) mField.getValue();
		if (tax_ID == null || tax_ID.intValue() == 0)
			return "";
			BigDecimal lineAmount=(BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_LineNetAmt);
		if(tax_ID>0)
		{				
			int M_PriceList_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_ID");
			int StdPrecision = MPriceList.getStandardPrecision(ctx, M_PriceList_ID);				
					    
		    BigDecimal taxAmount =calculateTaxAmount(tax_ID, StdPrecision, lineAmount);
		    
			mTab.setValue(MOpportunityLine.COLUMNNAME_TaxAmt, taxAmount);
			mTab.setValue(MOpportunityLine.COLUMNNAME_LineTotalAmount, lineAmount.add(taxAmount));
		}
		return "";
	}
		
//		org.cyprus.crm.model.CalloutSalesOpportunity.amt
	public String amt (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		if (isCalloutActive() || value == null)
			return "";

		//		if (steps) log.warning("init");
		int C_Tax_ID = Env.getContextAsInt(ctx, WindowNo, MOpportunityLine.COLUMNNAME_C_Tax_ID);
		//		int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, MOpportunityLine.COLUMNNAME_C_UOM_ID);
		int C_UOM_To_ID =(Integer)mTab.getValue(MOpportunityLine.COLUMNNAME_C_UOM_ID); 
		int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, MOpportunityLine.COLUMNNAME_M_Product_ID);
		int M_PriceList_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_ID");
		//		M_PriceList_ID=DB.getSQLValue(null, "SELECT M_PriceList_ID from C_LEAD where C_LEAD_ID="+c_lead_id);
		int StdPrecision = MPriceList.getStandardPrecision(ctx, M_PriceList_ID);
		BigDecimal QtyPlanned, QtyBase, PricePlanned, PriceBase, PriceLimit, Discount, PriceList;
		//	get values
		QtyPlanned = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_PlannedQty);//"QtyEntered");
		QtyBase = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_BaseQty);//"QtyOrdered");
		log.fine("QtyPlanned=" + QtyPlanned + ", QtyBase=" + QtyBase + ", UOM=" + C_UOM_To_ID);
		//
		PricePlanned = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_PlannedPrice);//"PricePlanned");
		PriceBase = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_BasePrice);//"PriceActual");
		Discount = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_Discount);//"Discount");
		PriceLimit =Env.ZERO;// (BigDecimal)mTab.getValue("PriceLimit");
		PriceList = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_PriceList);//"PriceList");
		//		log.fine("PriceList=" + PriceList + ", Limit=" + PriceLimit + ", Precision=" + StdPrecision);
		log.fine("PricePlanned=" + PricePlanned + ", PriceBase=" + PriceBase + ", Discount=" + Discount);

		//		No Product
		if (M_Product_ID == 0)
		{
			if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_PlannedPrice))//"PriceEntered"))
			{
				mTab.setValue(MOpportunityLine.COLUMNNAME_BasePrice, value);
				if (PriceBase.compareTo(Env.ZERO)==0)
					PriceBase = PricePlanned;	
			}
		}
		//	Product Qty changed - recalc price
		else if ( //(mField.getColumnName().equals(MLeadInfo.COLUMNNAME_BaseQty) || //"QtyOrdered") 
				mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_PlannedQty)//"QtyEntered")
				|| mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_M_Product_ID)) 
			//			&& !"N".equals(Env.getContext(ctx, WindowNo, "DiscountSchema")))
		{
			int C_BPartner_ID = Env.getContextAsInt(ctx, WindowNo, "C_BPartner_ID");
			//			if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_PlannedQty))//"QtyEntered"))
			//				QtyBase = MUOMConversion.convertProductTo (ctx, M_Product_ID, 
			//					C_UOM_To_ID, QtyPlanned);
			QtyBase = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
					C_UOM_To_ID, QtyPlanned);
			if (QtyBase == null)
				QtyBase = QtyPlanned;
			boolean IsSOTrx = Env.getContext(ctx, WindowNo, "IsSOTrx").equals("Y");
			MProductPricing pp = new MProductPricing (M_Product_ID, C_BPartner_ID, QtyBase, IsSOTrx);
			//			pp.setM_PriceList_ID(M_PriceList_ID);
			//			int M_PriceList_Version_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_Version_ID");
			int M_PriceList_Version_ID=DB.getSQLValue(null, "select MAX(M_PriceList_Version_ID) from M_PriceList_Version where M_PriceList_ID="+M_PriceList_ID);

			pp.setM_PriceList_Version_ID(M_PriceList_Version_ID);
			Timestamp date = (Timestamp)mTab.getValue("DateOrdered");
			pp.setPriceDate(date);
			//
			PricePlanned = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
					C_UOM_To_ID, pp.getPriceStd());
			if (PricePlanned == null)
				PricePlanned = pp.getPriceStd();
			//
			log.fine("QtyChanged -> PriceActual=" + pp.getPriceStd() 
			+ ", PriceEntered=" + PricePlanned + ", Discount=" + pp.getDiscount());
			//	PriceBase=pp.getPriceStd().multiply(QtyBase).setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);	
			PriceBase=pp.getPriceStd().setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);		

			mTab.setValue(MOpportunityLine.COLUMNNAME_BasePrice, PriceBase);
			mTab.setValue("Discount", pp.getDiscount());
			mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedPrice, PriceBase.multiply(QtyBase.divide(QtyPlanned)).setScale(StdPrecision, BigDecimal.ROUND_HALF_UP));
			Env.setContext(ctx, WindowNo, "DiscountSchema", pp.isDiscountSchema() ? "Y" : "N");
		}
		else if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_BasePrice))
		{
			PriceBase = (BigDecimal)value;
			PricePlanned = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
					C_UOM_To_ID, PricePlanned);
			if (PricePlanned == null)
				PricePlanned = PriceBase;
			//
			log.fine("PriceActual=" + PriceBase 
					+ " -> PriceEntered=" + PricePlanned);
			mTab.setValue("PriceEntered", PricePlanned);
		}
		else if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_PlannedPrice))//"PriceEntered"))
		{
			PricePlanned = (BigDecimal)value;
			PriceBase = MUOMConversion.convertProductTo (ctx, M_Product_ID, 
					C_UOM_To_ID, PricePlanned);
			if (PriceBase == null)
				PriceBase = PricePlanned;
			//
			log.fine("PricePlanned=" + PricePlanned 
					+ " -> PriceBase=" + PriceBase);
			mTab.setValue(MOpportunityLine.COLUMNNAME_BasePrice, PriceBase);
		}

		//  Discount entered - Calculate Actual/Entered
		if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_Discount))
		{
			if ( PriceList.doubleValue() != 0 )
				PriceBase = new BigDecimal ((100.0 - Discount.doubleValue()) / 100.0 * PriceList.doubleValue());
			//			if (PriceActual.scale() > StdPrecision)
			//				PriceActual = PriceActual.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
			PricePlanned = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
					C_UOM_To_ID, PriceBase);
			if (PricePlanned == null)
				PricePlanned = PriceBase;
			mTab.setValue(MOpportunityLine.COLUMNNAME_BasePrice, PriceBase);
			mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedPrice, PricePlanned);
		}
		//	calculate Discount
		else
		{
			if (PriceList.intValue() == 0)
				Discount = Env.ZERO;
			else
				Discount = new BigDecimal ((PriceList.doubleValue() - PriceBase.doubleValue()) / PriceList.doubleValue() * 100.0);
			if (Discount.scale() > 2)
				Discount = Discount.setScale(2, BigDecimal.ROUND_HALF_UP);
			mTab.setValue("Discount", Discount);
		}
		log.fine("PriceEntered=" + PricePlanned + ", Actual=" + PriceBase + ", Discount=" + Discount);

		//	Check PriceLimit
		String epl = Env.getContext(ctx, WindowNo, "EnforcePriceLimit");
		boolean enforce = Env.isSOTrx(ctx, WindowNo) && epl != null && epl.equals("Y");
		if (enforce && MRole.getDefault().isOverwritePriceLimit())
			enforce = false;
		//	Check Price Limit?
		if (enforce && PriceLimit.doubleValue() != 0.0
				&& PriceBase.compareTo(PriceLimit) < 0)
		{
			PriceBase = PriceLimit;
			PricePlanned = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
					C_UOM_To_ID, PriceLimit);
			if (PricePlanned == null)
				PricePlanned = PriceLimit;
			log.fine("(under) PriceEntered=" + PricePlanned + ", Actual" + PriceLimit);
			mTab.setValue (MOpportunityLine.COLUMNNAME_BasePrice, PriceLimit);
			mTab.setValue (MOpportunityLine.COLUMNNAME_PlannedPrice, PricePlanned);
			mTab.fireDataStatusEEvent ("UnderLimitPrice", "", false);
			//	Repeat Discount calc
			if (PriceList.intValue() != 0)
			{
				Discount = new BigDecimal ((PriceList.doubleValue () - PriceBase.doubleValue ()) / PriceList.doubleValue () * 100.0);
				if (Discount.scale () > 2)
					Discount = Discount.setScale (2, BigDecimal.ROUND_HALF_UP);
				mTab.setValue (MOpportunityLine.COLUMNNAME_Discount, Discount);
			}
		}

		//	Line Net Amt
		BigDecimal LineNetAmt = QtyPlanned.multiply(PricePlanned);
		//if (LineNetAmt.scale() > StdPrecision)
		LineNetAmt = LineNetAmt.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
		log.info("LineNetAmt=" + LineNetAmt);
		mTab.setValue("LineNetAmt", LineNetAmt);

		// Update Tax by Condition 

		Integer taxId = 0;

		int C_SalesOpportunity_ID = Env.getContextAsInt(ctx, WindowNo, "C_SalesOpportunity_ID");
		if(C_SalesOpportunity_ID>0 && M_Product_ID>0) // for Order
		{
			BigDecimal lineNetAmt=(BigDecimal)mTab.getValue("LineNetAmt");
			taxId=UtilTax.getTaxIDBasedOnState(MSalesOpportunity.Table_ID,C_SalesOpportunity_ID,M_Product_ID, lineNetAmt, PricePlanned);

			if(taxId!=null)
			{
				mTab.setValue("C_Tax_ID", taxId);
			}
		}
		//

		//
		System.out.println("C_Tax_ID :: "+C_Tax_ID);
		if(taxId!=null && taxId>0) 
		{		
			BigDecimal taxAmount =calculateTaxAmount(taxId, StdPrecision, LineNetAmt);
			mTab.setValue(MOpportunityLine.COLUMNNAME_TaxAmt, taxAmount);
			mTab.setValue(MOpportunityLine.COLUMNNAME_LineTotalAmount, LineNetAmt.add(taxAmount));
		}

		return "";
	}	//	amt
		
		/**
		 * Get All details from Product window for Lead Info window
		 * @param ctx
		 * @param WindowNo
		 * @param mTab
		 * @param mField
		 * @param value
		 * @return
		 */
	//org.cyprus.crm.model.CalloutSalesOpportunity.product;
	public String product (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer M_Product_ID = (Integer)value;
		if (M_Product_ID == null || M_Product_ID.intValue() == 0)
			return "";
		//
		mTab.setValue("C_Charge_ID", null);
		//	Set Attribute
		if (Env.getContextAsInt(ctx, WindowNo, Env.TAB_INFO, "M_Product_ID") == M_Product_ID.intValue()
			&& Env.getContextAsInt(ctx, WindowNo, Env.TAB_INFO, "M_AttributeSetInstance_ID") != 0)
			mTab.setValue("M_AttributeSetInstance_ID", Env.getContextAsInt(ctx, WindowNo, Env.TAB_INFO, "M_AttributeSetInstance_ID"));
		else
			mTab.setValue("M_AttributeSetInstance_ID", null);
			
		/*****	Price Calculation see also qty	****/
		MProduct prod=new MProduct(ctx, M_Product_ID, null);
		int M_PriceList_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_ID");
		int M_PriceList_Version_ID=DB.getSQLValue(null, "select MAX(M_PriceList_Version_ID) from M_PriceList_Version where M_PriceList_ID="+M_PriceList_ID);
		mTab.setValue(MOpportunityLine.COLUMNNAME_BaseQty, Env.ONE);
		mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedQty, Env.ONE);
		mTab.setValue(MOpportunityLine.COLUMNNAME_C_UOM_ID, prod.getC_UOM_ID());
		if(M_PriceList_Version_ID>0)
		{	
			MProductPrice mpp=MProductPrice.get(ctx, M_PriceList_Version_ID, M_Product_ID, null);
			if(mpp!=null)
			{		
				BigDecimal Discount=Env.ZERO;
				BigDecimal PriceStd=mpp.getPriceStd()!=null?mpp.getPriceStd():Env.ZERO;
				mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedPrice, PriceStd);
				mTab.setValue(MOpportunityLine.COLUMNNAME_PriceList, mpp.getPriceList()!=null?mpp.getPriceList():Env.ZERO);
				mTab.setValue(MOpportunityLine.COLUMNNAME_BasePrice, PriceStd);
				mTab.setValue(MOpportunityLine.COLUMNNAME_LineNetAmt, PriceStd);
				mTab.setValue(MOpportunityLine.COLUMNNAME_LineTotalAmount, PriceStd);
				if(mpp.getPriceList().compareTo(Env.ZERO)>0)
				{				
//					Discount = new BigDecimal ((PriceList.doubleValue() - PriceBase.doubleValue()) / PriceList.doubleValue() * 100.0);
					Discount = new BigDecimal ((mpp.getPriceList().doubleValue() - mpp.getPriceStd().doubleValue()) / mpp.getPriceList().doubleValue() * 100.0);
					if (Discount.scale() > 2)
						Discount = Discount.setScale(2, BigDecimal.ROUND_HALF_UP);
					mTab.setValue(MOpportunityLine.COLUMNNAME_Discount, Discount);
				}
			}
		}
		return "";
	}	//	product
	
	// org.cyprus.crm.model.CalloutSalesOpportunity.qty;
	public String qty (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		if (isCalloutActive() || value == null)
			return "";
		int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, MOpportunityLine.COLUMNNAME_M_Product_ID);
//		if (steps) log.warning("init - M_Product_ID=" + M_Product_ID + " - " );
		BigDecimal BaseQty = Env.ZERO;
//		BigDecimal QtyEntered, PriceActual, PriceEntered;
		
		BigDecimal PlannedQty, BasePrice, PricePlanned;
		
		//	No Product
		if (M_Product_ID == 0)
		{
			PlannedQty = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_PlannedQty);
			BaseQty = PlannedQty;
			mTab.setValue(MOpportunityLine.COLUMNNAME_BaseQty, BaseQty);
		}
		//	UOM Changed - convert from Entered -> Product
		else if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_C_UOM_ID))
		{
			int C_UOM_To_ID = ((Integer)value).intValue();
			PlannedQty = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_PlannedQty);
			BigDecimal PlannedQty1 = PlannedQty.setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), BigDecimal.ROUND_HALF_UP);
			if (PlannedQty.compareTo(PlannedQty1) != 0)
			{
				log.fine("Corrected PlannedQty Scale UOM=" + C_UOM_To_ID 
					+ "; PlannedQty=" + PlannedQty + "->" + PlannedQty1);  
				PlannedQty = PlannedQty1;
				mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedQty, PlannedQty);
			}
			BaseQty = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
				C_UOM_To_ID, PlannedQty);
			if (BaseQty == null)
				BaseQty = PlannedQty;
			boolean conversion = PlannedQty.compareTo(BaseQty) != 0;
			BasePrice = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_BasePrice);
			PricePlanned = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
				C_UOM_To_ID, BasePrice);
			if (PricePlanned == null)
				PricePlanned = BasePrice; 
			log.fine("UOM=" + C_UOM_To_ID 
				+ ", PlannedQty/BasePrice=" + PlannedQty + "/" + BasePrice
				+ " -> " + conversion 
				+ " BaseQty/PricePlanned=" + BaseQty + "/" + PricePlanned);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			BigDecimal NetAmount=BasePrice.multiply(BaseQty).setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), BigDecimal.ROUND_HALF_UP);
			mTab.setValue(MOpportunityLine.COLUMNNAME_BaseQty, BaseQty);
			mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedPrice, PricePlanned);
			mTab.setValue(MOpportunityLine.COLUMNNAME_LineNetAmt, NetAmount);
			//int C_Tax_ID = Env.getContextAsInt(ctx, WindowNo, MOpportunityLine.COLUMNNAME_C_Tax_ID);
			int C_Tax_ID = (Integer)mTab.getValue(MOpportunityLine.COLUMNNAME_C_Tax_ID);				
			if(C_Tax_ID>0)
			{				
				int M_PriceList_ID = Env.getContextAsInt(ctx, WindowNo, "M_PriceList_ID");
				int StdPrecision = MPriceList.getStandardPrecision(ctx, M_PriceList_ID);	
				BigDecimal taxAmount=calculateTaxAmount(C_Tax_ID, StdPrecision, NetAmount);				
				mTab.setValue(MOpportunityLine.COLUMNNAME_TaxAmt, taxAmount);
				mTab.setValue(MOpportunityLine.COLUMNNAME_LineTotalAmount, NetAmount.add(taxAmount));
			}								
		}
		//	PlannedQty changed - calculate BaseQty
		else if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_PlannedQty))
		{
			int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
			PlannedQty = (BigDecimal)value;
			BigDecimal PlannedQty1 = PlannedQty.setScale(MUOM.getPrecision(ctx, C_UOM_To_ID), BigDecimal.ROUND_HALF_UP);
			if (PlannedQty.compareTo(PlannedQty1) != 0)
			{
				log.fine("Corrected PlannedQty Scale UOM=" + C_UOM_To_ID 
					+ "; PlannedQty=" + PlannedQty + "->" + PlannedQty1);  
				PlannedQty = PlannedQty1;
				mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedQty, PlannedQty);
			}
			BaseQty = MUOMConversion.convertProductFrom (ctx, M_Product_ID, 
				C_UOM_To_ID, PlannedQty);
			if (BaseQty == null)
				BaseQty = PlannedQty;
			boolean conversion = PlannedQty.compareTo(BaseQty) != 0;
			log.fine("UOM=" + C_UOM_To_ID 
				+ ", PlannedQty=" + PlannedQty
				+ " -> " + conversion 
				+ " BaseQty=" + BaseQty);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			mTab.setValue(MOpportunityLine.COLUMNNAME_BaseQty, BaseQty);
		}
		//	BaseQty changed - calculate PlannedQty (should not happen)
		else if (mField.getColumnName().equals(MOpportunityLine.COLUMNNAME_BaseQty))
		{
			int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, MOpportunityLine.COLUMNNAME_C_UOM_ID);
			BaseQty = (BigDecimal)value;
			int precision = MProduct.get(ctx, M_Product_ID).getUOMPrecision(); 
			BigDecimal BaseQty1 = BaseQty.setScale(precision, BigDecimal.ROUND_HALF_UP);
			if (BaseQty.compareTo(BaseQty1) != 0)
			{
				log.fine("Corrected BaseQty Scale " 
					+ BaseQty + "->" + BaseQty1);  
				BaseQty = BaseQty1;
				mTab.setValue(MOpportunityLine.COLUMNNAME_BaseQty, BaseQty);
			}
			PlannedQty = MUOMConversion.convertProductTo (ctx, M_Product_ID, 
				C_UOM_To_ID, BaseQty);
			if (PlannedQty == null)
				PlannedQty = BaseQty;
			boolean conversion = BaseQty.compareTo(PlannedQty) != 0;
			log.fine("UOM=" + C_UOM_To_ID 
				+ ", BaseQty=" + BaseQty
				+ " -> " + conversion 
				+ " PlannedQty=" + PlannedQty);
			Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
			mTab.setValue(MOpportunityLine.COLUMNNAME_PlannedQty, PlannedQty);
		}
		else
		{
			PlannedQty = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_PlannedQty);
			BaseQty = (BigDecimal)mTab.getValue(MOpportunityLine.COLUMNNAME_BaseQty);
		}
		
		//
		return "";
	}	//	qty		
	
	/**
	 * Calculate Tax Amount
	 * @param C_Tax_ID
	 * @param StdPrecision
	 * @param NetAmount
	 * @return
	 */
	private BigDecimal calculateTaxAmount(Integer C_Tax_ID, Integer StdPrecision, BigDecimal NetAmount)
	{
		//BigDecimal taxAmt=null;			
		// 1. Fetch Tax Rate via SQL (instead of loading entire MTax)
		String sql = "SELECT Rate FROM C_Tax WHERE C_Tax_ID = ?";
		BigDecimal taxRate = DB.getSQLValueBD(null, sql, C_Tax_ID);	    
		if (taxRate == null) {
			taxRate=Env.ONE;
		}					
		// Get the tax rate (e.g., 15% → 0.15)
		taxRate = taxRate.divide(Env.ONEHUNDRED);//, StdPrecision, BigDecimal.ROUND_HALF_UP);  
		// Calculate tax amount: lineAmount * taxRate
		BigDecimal taxAmount = NetAmount.multiply(taxRate).setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
		
		return taxAmount;
	}	
	
	//path: org.cyprus.crm.model.CalloutSalesOpportunity.setTaxBasedOnState
	public String setTaxBasedOnState(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) 
	{
		Integer taxId=null;

		if (value == null)
			return "";

		Integer M_Product_ID = (Integer) value;

		// Get Lead ID
		Integer C_SalesOpportunity_ID = (Integer) mTab.getValue("C_SalesOpportunity_ID");
		if (C_SalesOpportunity_ID == null)
			return "";

		BigDecimal lineNetAmt = (BigDecimal) mTab.getValue("LineNetAmt");
		if (lineNetAmt == null)
			lineNetAmt = Env.ZERO;

		BigDecimal price = (BigDecimal) mTab.getValue("PlannedPrice");
		if (price == null)
			price = Env.ZERO;

		taxId=UtilTax.getTaxIDBasedOnState(MSalesOpportunity.Table_ID,C_SalesOpportunity_ID,M_Product_ID, lineNetAmt, price);

		if(taxId!=null)
		{
			mTab.setValue("C_Tax_ID", taxId);
		}	  
		return "";
}
	
//path: org.cyprus.crm.model.CalloutSalesOpportunity.updateTaxBasedOnCharge
	public String updateTaxBasedOnCharge(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {

        Integer C_Charge_ID = (Integer) mTab.getValue("C_Charge_ID");
        if (C_Charge_ID == null || C_Charge_ID <= 0)
            return "";

        MCharge charge = new MCharge(ctx, C_Charge_ID, null);
        int selectedTaxID = Env.getContextAsInt(ctx, WindowNo, "C_Tax_ID");

        // Case 1: SameTax = false
        if (!charge.isSameTax()) {
            // Use tax selected on header
        	Integer taxOnCharge=charge.get_ValueAsInt("C_Tax_ID");
        	if(taxOnCharge>0)
        		mTab.setValue("C_Tax_ID", taxOnCharge);
        	else if (selectedTaxID > 0) {
                mTab.setValue("C_Tax_ID", selectedTaxID);
            }
            return "";
        }

        // Case 2: SameTax = true
        int C_SalesOpportunity_ID = (Integer) mTab.getValue("C_SalesOpportunity_ID");
        MSalesOpportunity so = new MSalesOpportunity(ctx, C_SalesOpportunity_ID, null);

        // Retrieve lead info
        MOpportunityLine[] lines = so.getLines();

        if (lines.length == 0) {
            // Case 4: No lines, use default tax from header
            if (selectedTaxID > 0)
                mTab.setValue("C_Tax_ID", selectedTaxID);
            return "";
        }

        // Count frequency and rate of each tax ID
        Map<Integer, Integer> taxCountMap = new HashMap<Integer, Integer>();
        Map<Integer, BigDecimal> taxRateMap = new HashMap<>();

        for (MOpportunityLine line : lines) {
            int taxID = line.getC_Tax_ID();
            taxCountMap.put(taxID, taxCountMap.getOrDefault(taxID, 0) + 1);

            MTax tax = new MTax(ctx, taxID, null);
            taxRateMap.put(taxID, tax.getRate());
        }

        // Find tax with max count, break ties with max rate
        int selectedTax = 0;
        int maxCount = 0;
        BigDecimal maxRate = Env.ZERO;

        for (Map.Entry<Integer, Integer> entry : taxCountMap.entrySet()) {
            int taxID = entry.getKey();
            int count = entry.getValue();
            BigDecimal rate = taxRateMap.get(taxID);

            if (count > maxCount || (count == maxCount && rate.compareTo(maxRate) > 0)) {
                maxCount = count;
                maxRate = rate;
                selectedTax = taxID;
            }
        }

        if (selectedTax > 0) {
            mTab.setValue("C_Tax_ID", selectedTax);
        }

        return "";
    }
	
	// Callout path : org.cyprus.crm.model.CalloutSalesOpportunity.charge
		public String charge (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
		{
			Integer C_Charge_ID = (Integer)value;
			if (C_Charge_ID == null || C_Charge_ID.intValue() == 0)
				return "";
			//	No Product defined
			if (mTab.getValue(MOpportunityLine.COLUMNNAME_M_Product_ID) != null)
			{
				mTab.setValue(MOpportunityLine.COLUMNNAME_C_Charge_ID, null);
				return "ChargeExclusively";
			}
			mTab.setValue(MOpportunityLine.COLUMNNAME_M_AttributeSetInstance_ID, null);
			mTab.setValue(MOpportunityLine.COLUMNNAME_C_UOM_ID, new Integer(100));	//	EA
			
			Env.setContext(ctx, WindowNo, "DiscountSchema", "N");
			String sql = "SELECT ChargeAmt, Description FROM C_Charge WHERE C_Charge_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, C_Charge_ID.intValue());
				rs = pstmt.executeQuery();
				if (rs.next()) 
				{
					BigDecimal chargeAmt=rs.getBigDecimal (MCharge.COLUMNNAME_ChargeAmt);
					String description=rs.getString (MCharge.COLUMNNAME_Description);
					mTab.setValue (MOpportunityLine.COLUMNNAME_Description, description);
					mTab.setValue (MOpportunityLine.COLUMNNAME_PlannedPrice, chargeAmt);
					mTab.setValue (MOpportunityLine.COLUMNNAME_BasePrice, chargeAmt);
					mTab.setValue (MOpportunityLine.COLUMNNAME_PriceList, chargeAmt);
					mTab.setValue (MOpportunityLine.COLUMNNAME_LineNetAmt, chargeAmt);
					mTab.setValue ("LineTotalAmount", chargeAmt);
					mTab.setValue (MOpportunityLine.COLUMNNAME_PlannedQty, Env.ONE);
					mTab.setValue (MOpportunityLine.COLUMNNAME_BaseQty, Env.ONE);
					mTab.setValue (MOpportunityLine.COLUMNNAME_Discount, Env.ZERO);
				}
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
				return e.getLocalizedMessage();
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return "";
		}	//	charge
}
