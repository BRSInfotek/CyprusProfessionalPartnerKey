/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 cyprusbrs, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * cyprusbrs, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@cyprusbrs.org or http://www.cyprusbrs.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.cyprus.mfg.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_C_AcctSchema;
import org.cyprusbrs.framework.I_C_Activity;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_BPartner_Location;
import org.cyprusbrs.framework.I_C_Campaign;
import org.cyprusbrs.framework.I_C_Currency;
import org.cyprusbrs.framework.I_C_ElementValue;
import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_Project;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_BOM;
import org.cyprusbrs.framework.I_M_CostElement;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MFG_WorkOrderValueDetail
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderValueDetail extends PO implements I_MFG_WorkOrderValueDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211222L;

    /** Standard Constructor */
    public X_MFG_WorkOrderValueDetail (Properties ctx, int MFG_WorkOrderValueDetail_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderValueDetail_ID, trxName);
      /** if (MFG_WorkOrderValueDetail_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderValueDetail (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderValueDetail[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_AcctSchema getC_AcctSchema() throws RuntimeException
    {
		return (I_C_AcctSchema)MTable.get(getCtx(), I_C_AcctSchema.Table_Name)
			.getPO(getC_AcctSchema_ID(), get_TrxName());	}

	/** Set Accounting Schema.
		@param C_AcctSchema_ID 
		Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID)
	{
		if (C_AcctSchema_ID < 1) 
			set_Value (COLUMNNAME_C_AcctSchema_ID, null);
		else 
			set_Value (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
	}

	/** Get Accounting Schema.
		@return Rules for accounting
	  */
	public int getC_AcctSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_AcctSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Activity getC_Activity() throws RuntimeException
    {
		return (I_C_Activity)MTable.get(getCtx(), I_C_Activity.Table_Name)
			.getPO(getC_Activity_ID(), get_TrxName());	}

	/** Set Activity.
		@param C_Activity_ID 
		Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID)
	{
		if (C_Activity_ID < 1) 
			set_Value (COLUMNNAME_C_Activity_ID, null);
		else 
			set_Value (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
	}

	/** Get Activity.
		@return Business Activity
	  */
	public int getC_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (I_C_BPartner)MTable.get(getCtx(), I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
    {
		return (I_C_BPartner_Location)MTable.get(getCtx(), I_C_BPartner_Location.Table_Name)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID 
		Identifies the (ship to) address for this Business Partner
	  */
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
	{
		if (C_BPartner_Location_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_Location_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
	}

	/** Get Partner Location.
		@return Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Campaign getC_Campaign() throws RuntimeException
    {
		return (I_C_Campaign)MTable.get(getCtx(), I_C_Campaign.Table_Name)
			.getPO(getC_Campaign_ID(), get_TrxName());	}

	/** Set Campaign.
		@param C_Campaign_ID 
		Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID)
	{
		if (C_Campaign_ID < 1) 
			set_Value (COLUMNNAME_C_Campaign_ID, null);
		else 
			set_Value (COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
	}

	/** Get Campaign.
		@return Marketing Campaign
	  */
	public int getC_Campaign_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Currency getC_Currency() throws RuntimeException
    {
		return (I_C_Currency)MTable.get(getCtx(), I_C_Currency.Table_Name)
			.getPO(getC_Currency_ID(), get_TrxName());	}

	/** Set Currency.
		@param C_Currency_ID 
		The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1) 
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else 
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Period getC_Period() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_ID(), get_TrxName());	}

	/** Set Period.
		@param C_Period_ID 
		Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1) 
			set_Value (COLUMNNAME_C_Period_ID, null);
		else 
			set_Value (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	public int getC_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Project getC_Project() throws RuntimeException
    {
		return (I_C_Project)MTable.get(getCtx(), I_C_Project.Table_Name)
			.getPO(getC_Project_ID(), get_TrxName());	}

	/** Set Project.
		@param C_Project_ID 
		Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID)
	{
		if (C_Project_ID < 1) 
			set_Value (COLUMNNAME_C_Project_ID, null);
		else 
			set_Value (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
	}

	/** Get Project.
		@return Financial Project
	  */
	public int getC_Project_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_UOM getC_UOM() throws RuntimeException
    {
		return (I_C_UOM)MTable.get(getCtx(), I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
//	/** Drafted = D */
//	public static final String DOCSTATUS_Drafted = "D";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set MATERIALIN.
		@param MATERIALIN MATERIALIN	  */
	public void setMATERIALIN (BigDecimal MATERIALIN)
	{
		set_Value (COLUMNNAME_MATERIALIN, MATERIALIN);
	}

	/** Get MATERIALIN.
		@return MATERIALIN	  */
	public BigDecimal getMATERIALIN () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MATERIALIN);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MATERIALOUT.
		@param MATERIALOUT MATERIALOUT	  */
	public void setMATERIALOUT (BigDecimal MATERIALOUT)
	{
		set_Value (COLUMNNAME_MATERIALOUT, MATERIALOUT);
	}

	/** Get MATERIALOUT.
		@return MATERIALOUT	  */
	public BigDecimal getMATERIALOUT () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MATERIALOUT);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MATERIALOVERHDIN.
		@param MATERIALOVERHDIN MATERIALOVERHDIN	  */
	public void setMATERIALOVERHDIN (BigDecimal MATERIALOVERHDIN)
	{
		set_Value (COLUMNNAME_MATERIALOVERHDIN, MATERIALOVERHDIN);
	}

	/** Get MATERIALOVERHDIN.
		@return MATERIALOVERHDIN	  */
	public BigDecimal getMATERIALOVERHDIN () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MATERIALOVERHDIN);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MATERIALOVERHDOUT.
		@param MATERIALOVERHDOUT MATERIALOVERHDOUT	  */
	public void setMATERIALOVERHDOUT (BigDecimal MATERIALOVERHDOUT)
	{
		set_Value (COLUMNNAME_MATERIALOVERHDOUT, MATERIALOVERHDOUT);
	}

	/** Get MATERIALOVERHDOUT.
		@return MATERIALOVERHDOUT	  */
	public BigDecimal getMATERIALOVERHDOUT () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MATERIALOVERHDOUT);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MATERIALOVERHDVARIANCE.
		@param MATERIALOVERHDVARIANCE MATERIALOVERHDVARIANCE	  */
	public void setMATERIALOVERHDVARIANCE (BigDecimal MATERIALOVERHDVARIANCE)
	{
		set_Value (COLUMNNAME_MATERIALOVERHDVARIANCE, MATERIALOVERHDVARIANCE);
	}

	/** Get MATERIALOVERHDVARIANCE.
		@return MATERIALOVERHDVARIANCE	  */
	public BigDecimal getMATERIALOVERHDVARIANCE () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MATERIALOVERHDVARIANCE);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MATERIALVARIANCE.
		@param MATERIALVARIANCE MATERIALVARIANCE	  */
	public void setMATERIALVARIANCE (BigDecimal MATERIALVARIANCE)
	{
		set_Value (COLUMNNAME_MATERIALVARIANCE, MATERIALVARIANCE);
	}

	/** Get MATERIALVARIANCE.
		@return MATERIALVARIANCE	  */
	public BigDecimal getMATERIALVARIANCE () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MATERIALVARIANCE);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_MFG_Routing getMFG_Routing() throws RuntimeException
    {
		return (I_MFG_Routing)MTable.get(getCtx(), I_MFG_Routing.Table_Name)
			.getPO(getMFG_Routing_ID(), get_TrxName());	}

	/** Set Routing.
		@param MFG_Routing_ID 
		Routing for an assembly
	  */
	public void setMFG_Routing_ID (int MFG_Routing_ID)
	{
		if (MFG_Routing_ID < 1) 
			set_Value (COLUMNNAME_MFG_Routing_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_Routing_ID, Integer.valueOf(MFG_Routing_ID));
	}

	/** Get Routing.
		@return Routing for an assembly
	  */
	public int getMFG_Routing_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_Routing_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set MFG_WORKORDERVALUEDETAIL_ID.
		@param MFG_WORKORDERVALUEDETAIL_ID MFG_WORKORDERVALUEDETAIL_ID	  */
	public void setMFG_WORKORDERVALUEDETAIL_ID (int MFG_WORKORDERVALUEDETAIL_ID)
	{
		if (MFG_WORKORDERVALUEDETAIL_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WORKORDERVALUEDETAIL_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WORKORDERVALUEDETAIL_ID, Integer.valueOf(MFG_WORKORDERVALUEDETAIL_ID));
	}

	/** Get MFG_WORKORDERVALUEDETAIL_ID.
		@return MFG_WORKORDERVALUEDETAIL_ID	  */
	public int getMFG_WORKORDERVALUEDETAIL_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WORKORDERVALUEDETAIL_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderClass getMFG_WorkOrderClass() throws RuntimeException
    {
		return (I_MFG_WorkOrderClass)MTable.get(getCtx(), I_MFG_WorkOrderClass.Table_Name)
			.getPO(getMFG_WorkOrderClass_ID(), get_TrxName());	}

	/** Set Work Order Class ID.
		@param MFG_WorkOrderClass_ID Work Order Class ID	  */
	public void setMFG_WorkOrderClass_ID (int MFG_WorkOrderClass_ID)
	{
		if (MFG_WorkOrderClass_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderClass_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderClass_ID, Integer.valueOf(MFG_WorkOrderClass_ID));
	}

	/** Get Work Order Class ID.
		@return Work Order Class ID	  */
	public int getMFG_WorkOrderClass_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderClass_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderOperation getMFG_WorkOrderOperation() throws RuntimeException
    {
		return (I_MFG_WorkOrderOperation)MTable.get(getCtx(), I_MFG_WorkOrderOperation.Table_Name)
			.getPO(getMFG_WorkOrderOperation_ID(), get_TrxName());	}

	/** Set Operation.
		@param MFG_WorkOrderOperation_ID Operation	  */
	public void setMFG_WorkOrderOperation_ID (int MFG_WorkOrderOperation_ID)
	{
		if (MFG_WorkOrderOperation_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderOperation_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderOperation_ID, Integer.valueOf(MFG_WorkOrderOperation_ID));
	}

	/** Get Operation.
		@return Operation	  */
	public int getMFG_WorkOrderOperation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderOperation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderTransaction getMFG_WorkOrderTransaction() throws RuntimeException
    {
		return (I_MFG_WorkOrderTransaction)MTable.get(getCtx(), I_MFG_WorkOrderTransaction.Table_Name)
			.getPO(getMFG_WorkOrderTransaction_ID(), get_TrxName());	}

	/** Set MFG_WorkOrderTransaction_ID.
		@param MFG_WorkOrderTransaction_ID MFG_WorkOrderTransaction_ID	  */
	public void setMFG_WorkOrderTransaction_ID (int MFG_WorkOrderTransaction_ID)
	{
		if (MFG_WorkOrderTransaction_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderTransaction_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderTransaction_ID, Integer.valueOf(MFG_WorkOrderTransaction_ID));
	}

	/** Get MFG_WorkOrderTransaction_ID.
		@return MFG_WorkOrderTransaction_ID	  */
	public int getMFG_WorkOrderTransaction_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderTransaction_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderValue getMFG_WorkOrderValue() throws RuntimeException
    {
		return (I_MFG_WorkOrderValue)MTable.get(getCtx(), I_MFG_WorkOrderValue.Table_Name)
			.getPO(getMFG_WorkOrderValue_ID(), get_TrxName());	}

	/** Set MFG_WorkOrderValue_ID.
		@param MFG_WorkOrderValue_ID MFG_WorkOrderValue_ID	  */
	public void setMFG_WorkOrderValue_ID (int MFG_WorkOrderValue_ID)
	{
		if (MFG_WorkOrderValue_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderValue_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderValue_ID, Integer.valueOf(MFG_WorkOrderValue_ID));
	}

	/** Get MFG_WorkOrderValue_ID.
		@return MFG_WorkOrderValue_ID	  */
	public int getMFG_WorkOrderValue_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderValue_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException
    {
		return (I_MFG_WorkOrder)MTable.get(getCtx(), I_MFG_WorkOrder.Table_Name)
			.getPO(getMFG_WorkOrder_ID(), get_TrxName());	}

	/** Set Work Order.
		@param MFG_WorkOrder_ID Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID)
	{
		if (MFG_WorkOrder_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrder_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrder_ID, Integer.valueOf(MFG_WorkOrder_ID));
	}

	/** Get Work Order.
		@return Work Order	  */
	public int getMFG_WorkOrder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_BOM getM_BOM() throws RuntimeException
    {
		return (I_M_BOM)MTable.get(getCtx(), I_M_BOM.Table_Name)
			.getPO(getM_BOM_ID(), get_TrxName());	}

	/** Set BOM.
		@param M_BOM_ID 
		Bill of Material
	  */
	public void setM_BOM_ID (int M_BOM_ID)
	{
		if (M_BOM_ID < 1) 
			set_Value (COLUMNNAME_M_BOM_ID, null);
		else 
			set_Value (COLUMNNAME_M_BOM_ID, Integer.valueOf(M_BOM_ID));
	}

	/** Get BOM.
		@return Bill of Material
	  */
	public int getM_BOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_BOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_CostElement getM_CostElement() throws RuntimeException
    {
		return (I_M_CostElement)MTable.get(getCtx(), I_M_CostElement.Table_Name)
			.getPO(getM_CostElement_ID(), get_TrxName());	}

	/** Set Cost Element.
		@param M_CostElement_ID 
		Product Cost Element
	  */
	public void setM_CostElement_ID (int M_CostElement_ID)
	{
		if (M_CostElement_ID < 1) 
			set_Value (COLUMNNAME_M_CostElement_ID, null);
		else 
			set_Value (COLUMNNAME_M_CostElement_ID, Integer.valueOf(M_CostElement_ID));
	}

	/** Get Cost Element.
		@return Product Cost Element
	  */
	public int getM_CostElement_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_CostElement_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Product getM_Product() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (I_M_Warehouse)MTable.get(getCtx(), I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1) 
			set_Value (COLUMNNAME_M_Warehouse_ID, null);
		else 
			set_Value (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set OVERHDIN.
		@param OVERHDIN OVERHDIN	  */
	public void setOVERHDIN (BigDecimal OVERHDIN)
	{
		set_Value (COLUMNNAME_OVERHDIN, OVERHDIN);
	}

	/** Get OVERHDIN.
		@return OVERHDIN	  */
	public BigDecimal getOVERHDIN () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_OVERHDIN);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set OVERHDOUT.
		@param OVERHDOUT OVERHDOUT	  */
	public void setOVERHDOUT (BigDecimal OVERHDOUT)
	{
		set_Value (COLUMNNAME_OVERHDOUT, OVERHDOUT);
	}

	/** Get OVERHDOUT.
		@return OVERHDOUT	  */
	public BigDecimal getOVERHDOUT () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_OVERHDOUT);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set OVERHDVARIANCE.
		@param OVERHDVARIANCE OVERHDVARIANCE	  */
	public void setOVERHDVARIANCE (BigDecimal OVERHDVARIANCE)
	{
		set_Value (COLUMNNAME_OVERHDVARIANCE, OVERHDVARIANCE);
	}

	/** Get OVERHDVARIANCE.
		@return OVERHDVARIANCE	  */
	public BigDecimal getOVERHDVARIANCE () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_OVERHDVARIANCE);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Assembled.
		@param QtyAssembled 
		Quantity finished at a production routing step
	  */
	public void setQtyAssembled (BigDecimal QtyAssembled)
	{
		set_Value (COLUMNNAME_QtyAssembled, QtyAssembled);
	}

	/** Get Quantity Assembled.
		@return Quantity finished at a production routing step
	  */
	public BigDecimal getQtyAssembled () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyAssembled);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Available Quantity.
		@param QtyAvailable 
		Available Quantity (On Hand - Reserved)
	  */
	public void setQtyAvailable (BigDecimal QtyAvailable)
	{
		set_Value (COLUMNNAME_QtyAvailable, QtyAvailable);
	}

	/** Get Available Quantity.
		@return Available Quantity (On Hand - Reserved)
	  */
	public BigDecimal getQtyAvailable () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyAvailable);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity.
		@param QtyEntered 
		The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_Value (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Scrapped.
		@param QtyScrapped 
		This is the number of sub-assemblies in the Scrap step of an operation in Work Order.
	  */
	public void setQtyScrapped (BigDecimal QtyScrapped)
	{
		set_Value (COLUMNNAME_QtyScrapped, QtyScrapped);
	}

	/** Get Quantity Scrapped.
		@return This is the number of sub-assemblies in the Scrap step of an operation in Work Order.
	  */
	public BigDecimal getQtyScrapped () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyScrapped);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set RESOURCEIN.
		@param RESOURCEIN RESOURCEIN	  */
	public void setRESOURCEIN (BigDecimal RESOURCEIN)
	{
		set_Value (COLUMNNAME_RESOURCEIN, RESOURCEIN);
	}

	/** Get RESOURCEIN.
		@return RESOURCEIN	  */
	public BigDecimal getRESOURCEIN () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_RESOURCEIN);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set RESOURCEOUT.
		@param RESOURCEOUT RESOURCEOUT	  */
	public void setRESOURCEOUT (BigDecimal RESOURCEOUT)
	{
		set_Value (COLUMNNAME_RESOURCEOUT, RESOURCEOUT);
	}

	/** Get RESOURCEOUT.
		@return RESOURCEOUT	  */
	public BigDecimal getRESOURCEOUT () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_RESOURCEOUT);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set RESOURCEVARIANCE.
		@param RESOURCEVARIANCE RESOURCEVARIANCE	  */
	public void setRESOURCEVARIANCE (BigDecimal RESOURCEVARIANCE)
	{
		set_Value (COLUMNNAME_RESOURCEVARIANCE, RESOURCEVARIANCE);
	}

	/** Get RESOURCEVARIANCE.
		@return RESOURCEVARIANCE	  */
	public BigDecimal getRESOURCEVARIANCE () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_RESOURCEVARIANCE);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set SCRAPVALUE.
		@param SCRAPVALUE SCRAPVALUE	  */
	public void setSCRAPVALUE (BigDecimal SCRAPVALUE)
	{
		set_Value (COLUMNNAME_SCRAPVALUE, SCRAPVALUE);
	}

	/** Get SCRAPVALUE.
		@return SCRAPVALUE	  */
	public BigDecimal getSCRAPVALUE () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_SCRAPVALUE);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_C_ElementValue getUser1() throws RuntimeException
    {
		return (I_C_ElementValue)MTable.get(getCtx(), I_C_ElementValue.Table_Name)
			.getPO(getUser1_ID(), get_TrxName());	}

	/** Set User List 1.
		@param User1_ID 
		User defined list element #1
	  */
	public void setUser1_ID (int User1_ID)
	{
		if (User1_ID < 1) 
			set_Value (COLUMNNAME_User1_ID, null);
		else 
			set_Value (COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
	}

	/** Get User List 1.
		@return User defined list element #1
	  */
	public int getUser1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ElementValue getUser2() throws RuntimeException
    {
		return (I_C_ElementValue)MTable.get(getCtx(), I_C_ElementValue.Table_Name)
			.getPO(getUser2_ID(), get_TrxName());	}

	/** Set User List 2.
		@param User2_ID 
		User defined list element #2
	  */
	public void setUser2_ID (int User2_ID)
	{
		if (User2_ID < 1) 
			set_Value (COLUMNNAME_User2_ID, null);
		else 
			set_Value (COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
	}

	/** Get User List 2.
		@return User defined list element #2
	  */
	public int getUser2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}