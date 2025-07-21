/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2007 ComPiere, Inc. All Rights Reserved.                *
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
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for M_TaxCollectedAtSource
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_M_TaxCollectedAtSource extends PO implements I_M_TaxCollectedAtSource, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250710L;

    /** Standard Constructor */
    public X_M_TaxCollectedAtSource (Properties ctx, int M_TaxCollectedAtSource_ID, String trxName)
    {
      super (ctx, M_TaxCollectedAtSource_ID, trxName);
      /** if (M_TaxCollectedAtSource_ID == 0)
        {
			setM_TaxCollectedAtSource_ID (0);
        } */
    }

    /** Load Constructor */
    public X_M_TaxCollectedAtSource (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_TaxCollectedAtSource[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Tax Collected At Source ID.
		@param M_TaxCollectedAtSource_ID Tax Collected At Source ID	  */
	public void setM_TaxCollectedAtSource_ID (int M_TaxCollectedAtSource_ID)
	{
		if (M_TaxCollectedAtSource_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_TaxCollectedAtSource_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_TaxCollectedAtSource_ID, Integer.valueOf(M_TaxCollectedAtSource_ID));
	}

	/** Get Tax Collected At Source ID.
		@return Tax Collected At Source ID	  */
	public int getM_TaxCollectedAtSource_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_TaxCollectedAtSource_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Percent.
		@param Percent 
		Percentage
	  */
	public void setPercent (BigDecimal Percent)
	{
		set_Value (COLUMNNAME_Percent, Percent);
	}

	/** Get Percent.
		@return Percentage
	  */
	public BigDecimal getPercent () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Percent);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}