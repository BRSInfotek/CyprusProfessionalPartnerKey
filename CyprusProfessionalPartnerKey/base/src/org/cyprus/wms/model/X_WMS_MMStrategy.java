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
package org.cyprus.wms.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for WMS_MMStrategy
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_MMStrategy extends PO implements I_WMS_MMStrategy, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_MMStrategy (Properties ctx, int WMS_MMStrategy_ID, String trxName)
    {
      super (ctx, WMS_MMStrategy_ID, trxName);
      /** if (WMS_MMStrategy_ID == 0)
        {
			setIsAllowSplit (false);
// N
			setMMType (null);
			setName (null);
			setWMS_MMStrategy_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_MMStrategy (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_MMStrategy[")
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

	/** Set Allow Splitting.
		@param IsAllowSplit 
		Indicates if multiple pick or putaway tasks can be created for a single order line
	  */
	public void setIsAllowSplit (boolean IsAllowSplit)
	{
		set_Value (COLUMNNAME_IsAllowSplit, Boolean.valueOf(IsAllowSplit));
	}

	/** Get Allow Splitting.
		@return Indicates if multiple pick or putaway tasks can be created for a single order line
	  */
	public boolean isAllowSplit () 
	{
		Object oo = get_Value(COLUMNNAME_IsAllowSplit);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** MMType AD_Reference_ID=1000054 */
	public static final int MMTYPE_AD_Reference_ID=1000054;
	/** Material Picking = PCK */
	public static final String MMTYPE_MaterialPicking = "PCK";
	/** Material Putaway = PUT */
	public static final String MMTYPE_MaterialPutaway = "PUT";
	/** Set Type.
		@param MMType 
		Warehouse Management Rule Type
	  */
	public void setMMType (String MMType)
	{

		set_Value (COLUMNNAME_MMType, MMType);
	}

	/** Get Type.
		@return Warehouse Management Rule Type
	  */
	public String getMMType () 
	{
		return (String)get_Value(COLUMNNAME_MMType);
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

	/** Set Warehouse Management Strategy ID.
		@param WMS_MMStrategy_ID Warehouse Management Strategy ID	  */
	public void setWMS_MMStrategy_ID (int WMS_MMStrategy_ID)
	{
		if (WMS_MMStrategy_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategy_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategy_ID, Integer.valueOf(WMS_MMStrategy_ID));
	}

	/** Get Warehouse Management Strategy ID.
		@return Warehouse Management Strategy ID	  */
	public int getWMS_MMStrategy_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMStrategy_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}