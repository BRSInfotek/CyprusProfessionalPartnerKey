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

/** Generated Model for MFG_WorkCenter_Acct
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkCenter_Acct extends PO implements I_MFG_WorkCenter_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkCenter_Acct (Properties ctx, int MFG_WorkCenter_Acct_ID, String trxName)
    {
      super (ctx, MFG_WorkCenter_Acct_ID, trxName);
      /** if (MFG_WorkCenter_Acct_ID == 0)
        {
			setMFG_WorkCenter_Acct_ID (0);
			setMFG_WorkCenter_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkCenter_Acct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkCenter_Acct[")
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

	/** Set Work Center Account ID.
		@param MFG_WorkCenter_Acct_ID Work Center Account ID	  */
	public void setMFG_WorkCenter_Acct_ID (int MFG_WorkCenter_Acct_ID)
	{
		if (MFG_WorkCenter_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenter_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenter_Acct_ID, Integer.valueOf(MFG_WorkCenter_Acct_ID));
	}

	/** Get Work Center Account ID.
		@return Work Center Account ID	  */
	public int getMFG_WorkCenter_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkCenter_Acct_ID);
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

	public I_C_ValidCombination getWC_Overhead_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWC_Overhead_Acct(), get_TrxName());	}

	/** Set Work Center Overhead.
		@param WC_Overhead_Acct 
		Work Center Overhead Account
	  */
	public void setWC_Overhead_Acct (int WC_Overhead_Acct)
	{
		set_Value (COLUMNNAME_WC_Overhead_Acct, Integer.valueOf(WC_Overhead_Acct));
	}

	/** Get Work Center Overhead.
		@return Work Center Overhead Account
	  */
	public int getWC_Overhead_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WC_Overhead_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}