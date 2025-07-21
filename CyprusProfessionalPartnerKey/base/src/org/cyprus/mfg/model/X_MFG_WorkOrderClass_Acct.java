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

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_C_AcctSchema;
import org.cyprusbrs.framework.I_C_ValidCombination;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MFG_WorkOrderClass_Acct
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderClass_Acct extends PO implements I_MFG_WorkOrderClass_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkOrderClass_Acct (Properties ctx, int MFG_WorkOrderClass_Acct_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderClass_Acct_ID, trxName);
      /** if (MFG_WorkOrderClass_Acct_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setMFG_WorkOrderClass_Acct_ID (0);
			setMFG_WorkOrderClass_ID (0);
			setWO_MaterialOverhdVariance_Acct (0);
			setWO_MaterialOverhd_Acct (0);
			setWO_MaterialVariance_Acct (0);
			setWO_Material_Acct (0);
			setWO_OverhdVariance_Acct (0);
			setWO_ResourceVariance_Acct (0);
			setWO_Resource_Acct (0);
			setWO_Scrap_Acct (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderClass_Acct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderClass_Acct[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Work Order Class Accounting ID.
		@param MFG_WorkOrderClass_Acct_ID Work Order Class Accounting ID	  */
	public void setMFG_WorkOrderClass_Acct_ID (int MFG_WorkOrderClass_Acct_ID)
	{
		if (MFG_WorkOrderClass_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderClass_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderClass_Acct_ID, Integer.valueOf(MFG_WorkOrderClass_Acct_ID));
	}

	/** Get Work Order Class Accounting ID.
		@return Work Order Class Accounting ID	  */
	public int getMFG_WorkOrderClass_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderClass_Acct_ID);
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
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderClass_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderClass_ID, Integer.valueOf(MFG_WorkOrderClass_ID));
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

	public I_C_ValidCombination getWO_MaterialOverhdVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_MaterialOverhdVariance_Acct(), get_TrxName());	}

	/** Set Work Order Material Overhead Variance.
		@param WO_MaterialOverhdVariance_Acct 
		Work Order Material Overhead Variance Account
	  */
	public void setWO_MaterialOverhdVariance_Acct (int WO_MaterialOverhdVariance_Acct)
	{
		set_Value (COLUMNNAME_WO_MaterialOverhdVariance_Acct, Integer.valueOf(WO_MaterialOverhdVariance_Acct));
	}

	/** Get Work Order Material Overhead Variance.
		@return Work Order Material Overhead Variance Account
	  */
	public int getWO_MaterialOverhdVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_MaterialOverhdVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_MaterialOverhd_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_MaterialOverhd_Acct(), get_TrxName());	}

	/** Set Work Order Material Overhead.
		@param WO_MaterialOverhd_Acct 
		Work Order Material Overhead Account
	  */
	public void setWO_MaterialOverhd_Acct (int WO_MaterialOverhd_Acct)
	{
		set_Value (COLUMNNAME_WO_MaterialOverhd_Acct, Integer.valueOf(WO_MaterialOverhd_Acct));
	}

	/** Get Work Order Material Overhead.
		@return Work Order Material Overhead Account
	  */
	public int getWO_MaterialOverhd_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_MaterialOverhd_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_MaterialVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_MaterialVariance_Acct(), get_TrxName());	}

	/** Set Work Order Material Variance.
		@param WO_MaterialVariance_Acct 
		Work Order Material Variance Account
	  */
	public void setWO_MaterialVariance_Acct (int WO_MaterialVariance_Acct)
	{
		set_Value (COLUMNNAME_WO_MaterialVariance_Acct, Integer.valueOf(WO_MaterialVariance_Acct));
	}

	/** Get Work Order Material Variance.
		@return Work Order Material Variance Account
	  */
	public int getWO_MaterialVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_MaterialVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_Material_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_Material_Acct(), get_TrxName());	}

	/** Set Work Order Material.
		@param WO_Material_Acct 
		Work Order Material Account
	  */
	public void setWO_Material_Acct (int WO_Material_Acct)
	{
		set_Value (COLUMNNAME_WO_Material_Acct, Integer.valueOf(WO_Material_Acct));
	}

	/** Get Work Order Material.
		@return Work Order Material Account
	  */
	public int getWO_Material_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_Material_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_OverhdVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_OverhdVariance_Acct(), get_TrxName());	}

	/** Set Work Order Overhead Variance.
		@param WO_OverhdVariance_Acct 
		Work Order Overhead Variance Account
	  */
	public void setWO_OverhdVariance_Acct (int WO_OverhdVariance_Acct)
	{
		set_Value (COLUMNNAME_WO_OverhdVariance_Acct, Integer.valueOf(WO_OverhdVariance_Acct));
	}

	/** Get Work Order Overhead Variance.
		@return Work Order Overhead Variance Account
	  */
	public int getWO_OverhdVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_OverhdVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_ResourceVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_ResourceVariance_Acct(), get_TrxName());	}

	/** Set Work Order Resource Variance.
		@param WO_ResourceVariance_Acct 
		Work Order Resource Variance Account
	  */
	public void setWO_ResourceVariance_Acct (int WO_ResourceVariance_Acct)
	{
		set_Value (COLUMNNAME_WO_ResourceVariance_Acct, Integer.valueOf(WO_ResourceVariance_Acct));
	}

	/** Get Work Order Resource Variance.
		@return Work Order Resource Variance Account
	  */
	public int getWO_ResourceVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_ResourceVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_Resource_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_Resource_Acct(), get_TrxName());	}

	/** Set Work Order Resource.
		@param WO_Resource_Acct 
		Work Order Resource Account
	  */
	public void setWO_Resource_Acct (int WO_Resource_Acct)
	{
		set_Value (COLUMNNAME_WO_Resource_Acct, Integer.valueOf(WO_Resource_Acct));
	}

	/** Get Work Order Resource.
		@return Work Order Resource Account
	  */
	public int getWO_Resource_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_Resource_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getWO_Scrap_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWO_Scrap_Acct(), get_TrxName());	}

	/** Set Work Order Scrap.
		@param WO_Scrap_Acct 
		Work Order Scrap Account
	  */
	public void setWO_Scrap_Acct (int WO_Scrap_Acct)
	{
		set_Value (COLUMNNAME_WO_Scrap_Acct, Integer.valueOf(WO_Scrap_Acct));
	}

	/** Get Work Order Scrap.
		@return Work Order Scrap Account
	  */
	public int getWO_Scrap_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_Scrap_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}