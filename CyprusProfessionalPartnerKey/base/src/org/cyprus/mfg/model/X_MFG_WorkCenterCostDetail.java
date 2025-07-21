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
import org.cyprusbrs.framework.I_M_CostElement;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MFG_WorkCenterCostDetail
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkCenterCostDetail extends PO implements I_MFG_WorkCenterCostDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211221L;

    /** Standard Constructor */
    public X_MFG_WorkCenterCostDetail (Properties ctx, int MFG_WorkCenterCostDetail_ID, String trxName)
    {
      super (ctx, MFG_WorkCenterCostDetail_ID, trxName);
      /** if (MFG_WorkCenterCostDetail_ID == 0)
        {
			setMFG_WorkCenterCostDetail_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkCenterCostDetail (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkCenterCostDetail[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Amount.
		@param Amt 
		Amount
	  */
	public void setAmt (BigDecimal Amt)
	{
		set_Value (COLUMNNAME_Amt, Amt);
	}

	/** Get Amount.
		@return Amount
	  */
	public BigDecimal getAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Amt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set MFG_WorkCenterCostDetail ID.
		@param MFG_WorkCenterCostDetail_ID MFG_WorkCenterCostDetail ID	  */
	public void setMFG_WorkCenterCostDetail_ID (int MFG_WorkCenterCostDetail_ID)
	{
		if (MFG_WorkCenterCostDetail_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenterCostDetail_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenterCostDetail_ID, Integer.valueOf(MFG_WorkCenterCostDetail_ID));
	}

	/** Get MFG_WorkCenterCostDetail ID.
		@return MFG_WorkCenterCostDetail ID	  */
	public int getMFG_WorkCenterCostDetail_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkCenterCostDetail_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkCenter getMFG_WorkCenter() throws RuntimeException
    {
		return (I_MFG_WorkCenter)MTable.get(getCtx(), I_MFG_WorkCenter.Table_Name)
			.getPO(getMFG_WorkCenter_ID(), get_TrxName());	}

	/** Set Work Center.
		@param MFG_WorkCenter_ID 
		Identifies a production area within a warehouse consisting of people and equipment
	  */
	public void setMFG_WorkCenter_ID (int MFG_WorkCenter_ID)
	{
		if (MFG_WorkCenter_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkCenter_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkCenter_ID, Integer.valueOf(MFG_WorkCenter_ID));
	}

	/** Get Work Center.
		@return Identifies a production area within a warehouse consisting of people and equipment
	  */
	public int getMFG_WorkCenter_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkCenter_ID);
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

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}