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
import java.sql.Timestamp;
import java.util.Properties;

import org.cyprusbrs.framework.I_AD_User;
import org.cyprusbrs.framework.I_C_Activity;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_BPartner_Location;
import org.cyprusbrs.framework.I_C_Campaign;
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_ElementValue;
import org.cyprusbrs.framework.I_C_Project;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MFG_WorkOrderTransaction
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderTransaction extends PO implements I_MFG_WorkOrderTransaction, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkOrderTransaction (Properties ctx, int MFG_WorkOrderTransaction_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderTransaction_ID, trxName);
      /** if (MFG_WorkOrderTransaction_ID == 0)
        {
			setC_DocType_ID (0);
			setC_UOM_ID (0);
// @#C_UOM_ID@
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setIsApproved (false);
// @IsApproved@
			setIsOptionalFrom (false);
// N
			setIsOptionalTo (false);
// N
			setMFG_WORKORDERTRANSACTION_ID (0);
			setMFG_WorkOrder_ID (0);
			setM_Product_ID (0);
			setProcessed (false);
// N
			setQtyEntered (Env.ZERO);
// 1
			setWOComplete (false);
// N
			setWOTxnSource (null);
// M
			setWorkOrderTxnType (null);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderTransaction (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderTransaction[")
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

	public I_AD_User getAD_User() throws RuntimeException
    {
		return (I_AD_User)MTable.get(getCtx(), I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
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

	public I_C_DocType getC_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_ValueNoCheck (COLUMNNAME_C_DocType_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
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

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Transaction Date.
		@param DateTrx 
		Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx)
	{
		set_Value (COLUMNNAME_DateTrx, DateTrx);
	}

	/** Get Transaction Date.
		@return Transaction Date
	  */
	public Timestamp getDateTrx () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTrx);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getDocumentNo());
    }

	/** Set GenerateLines.
		@param GenerateLines GenerateLines	  */
	public void setGenerateLines (String GenerateLines)
	{
		set_Value (COLUMNNAME_GenerateLines, GenerateLines);
	}

	/** Get GenerateLines.
		@return GenerateLines	  */
	public String getGenerateLines () 
	{
		return (String)get_Value(COLUMNNAME_GenerateLines);
	}

	/** Set Generate Resource Usage Lines.
		@param GenerateResourceLines 
		Generate resource usage lines for manually charged resources
	  */
	public void setGenerateResourceLines (String GenerateResourceLines)
	{
		set_Value (COLUMNNAME_GenerateResourceLines, GenerateResourceLines);
	}

	/** Get Generate Resource Usage Lines.
		@return Generate resource usage lines for manually charged resources
	  */
	public String getGenerateResourceLines () 
	{
		return (String)get_Value(COLUMNNAME_GenerateResourceLines);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Approved.
		@param IsApproved 
		Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved)
	{
		set_Value (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved () 
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Optional.
		@param IsOptionalFrom 
		Indicates if the Operation From in the Work Order Move Transaction is an optional operation
	  */
	public void setIsOptionalFrom (boolean IsOptionalFrom)
	{
		set_Value (COLUMNNAME_IsOptionalFrom, Boolean.valueOf(IsOptionalFrom));
	}

	/** Get Optional.
		@return Indicates if the Operation From in the Work Order Move Transaction is an optional operation
	  */
	public boolean isOptionalFrom () 
	{
		Object oo = get_Value(COLUMNNAME_IsOptionalFrom);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Optional.
		@param IsOptionalTo 
		Indicates if the Operation To in Work Order Move Transaction is an optional operation
	  */
	public void setIsOptionalTo (boolean IsOptionalTo)
	{
		set_Value (COLUMNNAME_IsOptionalTo, Boolean.valueOf(IsOptionalTo));
	}

	/** Get Optional.
		@return Indicates if the Operation To in Work Order Move Transaction is an optional operation
	  */
	public boolean isOptionalTo () 
	{
		Object oo = get_Value(COLUMNNAME_IsOptionalTo);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set MFG_WORKORDERTRANSACTION_ID.
		@param MFG_WORKORDERTRANSACTION_ID MFG_WORKORDERTRANSACTION_ID	  */
	public void setMFG_WORKORDERTRANSACTION_ID (int MFG_WORKORDERTRANSACTION_ID)
	{
		if (MFG_WORKORDERTRANSACTION_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WORKORDERTRANSACTION_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WORKORDERTRANSACTION_ID, Integer.valueOf(MFG_WORKORDERTRANSACTION_ID));
	}

	/** Get MFG_WORKORDERTRANSACTION_ID.
		@return MFG_WORKORDERTRANSACTION_ID	  */
	public int getMFG_WORKORDERTRANSACTION_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WORKORDERTRANSACTION_ID);
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

	public I_M_Locator getM_Locator() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_Locator_ID(), get_TrxName());	}

	/** Set Locator.
		@param M_Locator_ID 
		Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1) 
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else 
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
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

	public I_MFG_WorkOrderOperation getOperationFrom() throws RuntimeException
    {
		return (I_MFG_WorkOrderOperation)MTable.get(getCtx(), I_MFG_WorkOrderOperation.Table_Name)
			.getPO(getOperationFrom_ID(), get_TrxName());	}

	/** Set Operation From.
		@param OperationFrom_ID 
		Process the operations in a work order transaction starting at this one.
	  */
	public void setOperationFrom_ID (int OperationFrom_ID)
	{
		if (OperationFrom_ID < 1) 
			set_Value (COLUMNNAME_OperationFrom_ID, null);
		else 
			set_Value (COLUMNNAME_OperationFrom_ID, Integer.valueOf(OperationFrom_ID));
	}

	/** Get Operation From.
		@return Process the operations in a work order transaction starting at this one.
	  */
	public int getOperationFrom_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_OperationFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderOperation getOperationTo() throws RuntimeException
    {
		return (I_MFG_WorkOrderOperation)MTable.get(getCtx(), I_MFG_WorkOrderOperation.Table_Name)
			.getPO(getOperationTo_ID(), get_TrxName());	}

	/** Set Operation To.
		@param OperationTo_ID 
		Process the operations in a work order transaction ending at this one (inclusive).
	  */
	public void setOperationTo_ID (int OperationTo_ID)
	{
		if (OperationTo_ID < 1) 
			set_Value (COLUMNNAME_OperationTo_ID, null);
		else 
			set_Value (COLUMNNAME_OperationTo_ID, Integer.valueOf(OperationTo_ID));
	}

	/** Get Operation To.
		@return Process the operations in a work order transaction ending at this one (inclusive).
	  */
	public int getOperationTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_OperationTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderTransaction getParentWorkOrderTxn() throws RuntimeException
    {
		return (I_MFG_WorkOrderTransaction)MTable.get(getCtx(), I_MFG_WorkOrderTransaction.Table_Name)
			.getPO(getParentWorkOrderTxn_ID(), get_TrxName());	}

	/** Set Parent Work Order Transaction.
		@param ParentWorkOrderTxn_ID 
		Work Order Transaction that created this Work Order Transaction
	  */
	public void setParentWorkOrderTxn_ID (int ParentWorkOrderTxn_ID)
	{
		if (ParentWorkOrderTxn_ID < 1) 
			set_Value (COLUMNNAME_ParentWorkOrderTxn_ID, null);
		else 
			set_Value (COLUMNNAME_ParentWorkOrderTxn_ID, Integer.valueOf(ParentWorkOrderTxn_ID));
	}

	/** Get Parent Work Order Transaction.
		@return Work Order Transaction that created this Work Order Transaction
	  */
	public int getParentWorkOrderTxn_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ParentWorkOrderTxn_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** StepFrom AD_Reference_ID=1000047 */
	public static final int STEPFROM_AD_Reference_ID=1000047;
	/** Queue = Q */
	public static final String STEPFROM_Queue = "Q";
	/** Run = R */
	public static final String STEPFROM_Run = "R";
	/** To Move = T */
	public static final String STEPFROM_ToMove = "T";
	/** Scrap = X */
	public static final String STEPFROM_Scrap = "X";
	/** Set Step From.
		@param StepFrom 
		The source intra-operation step from which the work order movement is being made.
	  */
	public void setStepFrom (String StepFrom)
	{

		set_Value (COLUMNNAME_StepFrom, StepFrom);
	}

	/** Get Step From.
		@return The source intra-operation step from which the work order movement is being made.
	  */
	public String getStepFrom () 
	{
		return (String)get_Value(COLUMNNAME_StepFrom);
	}

	/** StepTo AD_Reference_ID=1000047 */
	public static final int STEPTO_AD_Reference_ID=1000047;
	/** Queue = Q */
	public static final String STEPTO_Queue = "Q";
	/** Run = R */
	public static final String STEPTO_Run = "R";
	/** To Move = T */
	public static final String STEPTO_ToMove = "T";
	/** Scrap = X */
	public static final String STEPTO_Scrap = "X";
	/** Set Step To.
		@param StepTo 
		The destination intra-operation step to which the work order movement is being done.
	  */
	public void setStepTo (String StepTo)
	{

		set_Value (COLUMNNAME_StepTo, StepTo);
	}

	/** Get Step To.
		@return The destination intra-operation step to which the work order movement is being done.
	  */
	public String getStepTo () 
	{
		return (String)get_Value(COLUMNNAME_StepTo);
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

	/** Set Complete this Assembly.
		@param WOComplete 
		Indicates that a move transaction should include a completion for the assembly.
	  */
	public void setWOComplete (boolean WOComplete)
	{
		set_Value (COLUMNNAME_WOComplete, Boolean.valueOf(WOComplete));
	}

	/** Get Complete this Assembly.
		@return Indicates that a move transaction should include a completion for the assembly.
	  */
	public boolean isWOComplete () 
	{
		Object oo = get_Value(COLUMNNAME_WOComplete);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** WOTxnSource AD_Reference_ID=1000048 */
	public static final int WOTXNSOURCE_AD_Reference_ID=1000048;
	/** Generated = G */
	public static final String WOTXNSOURCE_Generated = "G";
	/** Manually Entered = M */
	public static final String WOTXNSOURCE_ManuallyEntered = "M";
	/** Set Transaction Source.
		@param WOTxnSource 
		Indicates where the work order transaction originated.
	  */
	public void setWOTxnSource (String WOTxnSource)
	{

		set_Value (COLUMNNAME_WOTxnSource, WOTxnSource);
	}

	/** Get Transaction Source.
		@return Indicates where the work order transaction originated.
	  */
	public String getWOTxnSource () 
	{
		return (String)get_Value(COLUMNNAME_WOTxnSource);
	}

	/** WorkOrderTxnType AD_Reference_ID=1000049 */
	public static final int WORKORDERTXNTYPE_AD_Reference_ID=1000049;
	/** Assembly Completion to Inventory = AI */
	public static final String WORKORDERTXNTYPE_AssemblyCompletionToInventory = "AI";
	/** Assembly Return from Inventory = AR */
	public static final String WORKORDERTXNTYPE_AssemblyReturnFromInventory = "AR";
	/** Component Issue to Work Order = CI */
	public static final String WORKORDERTXNTYPE_ComponentIssueToWorkOrder = "CI";
	/** Component Return from Work Order = CR */
	public static final String WORKORDERTXNTYPE_ComponentReturnFromWorkOrder = "CR";
	/** Resource Usage = RU */
	public static final String WORKORDERTXNTYPE_ResourceUsage = "RU";
	/** Work Order Move = WM */
	public static final String WORKORDERTXNTYPE_WorkOrderMove = "WM";
	/** Set Transaction Type.
		@param WorkOrderTxnType 
		Transaction Type
	  */
	public void setWorkOrderTxnType (String WorkOrderTxnType)
	{

		set_Value (COLUMNNAME_WorkOrderTxnType, WorkOrderTxnType);
	}

	/** Get Transaction Type.
		@return Transaction Type 
	  */
	public String getWorkOrderTxnType () 
	{
		return (String)get_Value(COLUMNNAME_WorkOrderTxnType);
	}
}