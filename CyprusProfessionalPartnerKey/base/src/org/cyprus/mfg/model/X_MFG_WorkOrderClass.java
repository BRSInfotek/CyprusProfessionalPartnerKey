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

import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MFG_WorkOrderClass
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderClass extends PO implements I_MFG_WorkOrderClass, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkOrderClass (Properties ctx, int MFG_WorkOrderClass_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderClass_ID, trxName);
      /** if (MFG_WorkOrderClass_ID == 0)
        {
			setIsDefault (false);
// N
			setMFG_WorkOrderClass_ID (0);
			setName (null);
			setWOT_DocType_ID (0);
			setWO_DocType_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderClass (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderClass[")
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

	/** Set Default.
		@param IsDefault 
		Default value
	  */
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault () 
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	public I_C_DocType getWOT_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getWOT_DocType_ID(), get_TrxName());	}

	/** Set Transaction Document Type.
		@param WOT_DocType_ID Transaction Document Type	  */
	public void setWOT_DocType_ID (int WOT_DocType_ID)
	{
		if (WOT_DocType_ID < 1) 
			set_Value (COLUMNNAME_WOT_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_WOT_DocType_ID, Integer.valueOf(WOT_DocType_ID));
	}

	/** Get Transaction Document Type.
		@return Transaction Document Type	  */
	public int getWOT_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WOT_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** WOType AD_Reference_ID=1000031 */
	public static final int WOTYPE_AD_Reference_ID=1000031;
	/** Refurbish = F */
	public static final String WOTYPE_Refurbish = "F";
	/** Repair = R */
	public static final String WOTYPE_Repair = "R";
	/** Standard = S */
	public static final String WOTYPE_Standard = "S";
	/** Set Work Order Type.
		@param WOType 
		Work Order Type
	  */
	public void setWOType (String WOType)
	{

		set_Value (COLUMNNAME_WOType, WOType);
	}

	/** Get Work Order Type.
		@return Work Order Type
	  */
	public String getWOType () 
	{
		return (String)get_Value(COLUMNNAME_WOType);
	}

	public I_C_DocType getWO_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getWO_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param WO_DocType_ID Document Type	  */
	public void setWO_DocType_ID (int WO_DocType_ID)
	{
		if (WO_DocType_ID < 1) 
			set_Value (COLUMNNAME_WO_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_WO_DocType_ID, Integer.valueOf(WO_DocType_ID));
	}

	/** Get Document Type.
		@return Document Type	  */
	public int getWO_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WO_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}