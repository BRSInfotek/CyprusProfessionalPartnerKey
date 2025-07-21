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
import org.cyprusbrs.framework.I_C_ValidCombination;
import org.cyprusbrs.framework.I_M_CostElement;
import org.cyprusbrs.framework.I_M_CostType;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MFG_WorkCenterCost
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkCenterCost extends PO implements I_MFG_WorkCenterCost, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211221L;

    /** Standard Constructor */
    public X_MFG_WorkCenterCost (Properties ctx, int MFG_WorkCenterCost_ID, String trxName)
    {
      super (ctx, MFG_WorkCenterCost_ID, trxName);
      /** if (MFG_WorkCenterCost_ID == 0)
        {
			setMFG_WorkCenterCost_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkCenterCost (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkCenterCost[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** BasisType AD_Reference_ID=1000034 */
	public static final int BASISTYPE_AD_Reference_ID=1000034;
	/** Per Batch = B */
	public static final String BASISTYPE_PerBatch = "B";
	/** Per Item = I */
	public static final String BASISTYPE_PerItem = "I";
	/** Set Cost Basis Type.
		@param BasisType 
		Indicates the option to consume and charge materials and resources
	  */
	public void setBasisType (String BasisType)
	{

		set_Value (COLUMNNAME_BasisType, BasisType);
	}

	/** Get Cost Basis Type.
		@return Indicates the option to consume and charge materials and resources
	  */
	public String getBasisType () 
	{
		return (String)get_Value(COLUMNNAME_BasisType);
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

	/** CostingMethod AD_Reference_ID=122 */
	public static final int COSTINGMETHOD_AD_Reference_ID=122;
	/** Standard Costing = S */
	public static final String COSTINGMETHOD_StandardCosting = "S";
	/** Average PO = A */
	public static final String COSTINGMETHOD_AveragePO = "A";
	/** Lifo = L */
	public static final String COSTINGMETHOD_Lifo = "L";
	/** Fifo = F */
	public static final String COSTINGMETHOD_Fifo = "F";
	/** Last PO Price = p */
	public static final String COSTINGMETHOD_LastPOPrice = "p";
	/** Average Invoice = I */
	public static final String COSTINGMETHOD_AverageInvoice = "I";
	/** Last Invoice = i */
	public static final String COSTINGMETHOD_LastInvoice = "i";
	/** User Defined = U */
	public static final String COSTINGMETHOD_UserDefined = "U";
	/** _ = x */
	public static final String COSTINGMETHOD__ = "x";
	/** Set Costing Method.
		@param CostingMethod 
		Indicates how Costs will be calculated
	  */
	public void setCostingMethod (String CostingMethod)
	{

		set_Value (COLUMNNAME_CostingMethod, CostingMethod);
	}

	/** Get Costing Method.
		@return Indicates how Costs will be calculated
	  */
	public String getCostingMethod () 
	{
		return (String)get_Value(COLUMNNAME_CostingMethod);
	}

	/** Set Current Cost Price.
		@param CurrentCostPrice 
		The currently used cost price
	  */
	public void setCurrentCostPrice (BigDecimal CurrentCostPrice)
	{
		set_Value (COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
	}

	/** Get Current Cost Price.
		@return The currently used cost price
	  */
	public BigDecimal getCurrentCostPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrentCostPrice);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Future Cost Price.
		@param FutureCostPrice Future Cost Price	  */
	public void setFutureCostPrice (BigDecimal FutureCostPrice)
	{
		set_Value (COLUMNNAME_FutureCostPrice, FutureCostPrice);
	}

	/** Get Future Cost Price.
		@return Future Cost Price	  */
	public BigDecimal getFutureCostPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FutureCostPrice);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set MFG_WorkCenterCost ID.
		@param MFG_WorkCenterCost_ID MFG_WorkCenterCost ID	  */
	public void setMFG_WorkCenterCost_ID (int MFG_WorkCenterCost_ID)
	{
		if (MFG_WorkCenterCost_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenterCost_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenterCost_ID, Integer.valueOf(MFG_WorkCenterCost_ID));
	}

	/** Get MFG_WorkCenterCost ID.
		@return MFG_WorkCenterCost ID	  */
	public int getMFG_WorkCenterCost_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkCenterCost_ID);
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

	public I_M_CostType getM_CostType() throws RuntimeException
    {
		return (I_M_CostType)MTable.get(getCtx(), I_M_CostType.Table_Name)
			.getPO(getM_CostType_ID(), get_TrxName());	}

	/** Set Cost Type.
		@param M_CostType_ID 
		Type of Cost (e.g. Current, Plan, Future)
	  */
	public void setM_CostType_ID (int M_CostType_ID)
	{
		if (M_CostType_ID < 1) 
			set_Value (COLUMNNAME_M_CostType_ID, null);
		else 
			set_Value (COLUMNNAME_M_CostType_ID, Integer.valueOf(M_CostType_ID));
	}

	/** Get Cost Type.
		@return Type of Cost (e.g. Current, Plan, Future)
	  */
	public int getM_CostType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_CostType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getOverhead_Absorption_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getOverhead_Absorption_Acct(), get_TrxName());	}

	/** Set Overhead Absorption.
		@param Overhead_Absorption_Acct 
		Overhead Absorption Account
	  */
	public void setOverhead_Absorption_Acct (int Overhead_Absorption_Acct)
	{
		set_Value (COLUMNNAME_Overhead_Absorption_Acct, Integer.valueOf(Overhead_Absorption_Acct));
	}

	/** Get Overhead Absorption.
		@return Overhead Absorption Account
	  */
	public int getOverhead_Absorption_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Overhead_Absorption_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}