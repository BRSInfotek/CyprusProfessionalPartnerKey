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

import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for WMS_DocTypeGroup
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_DocTypeGroup extends PO implements I_WMS_DocTypeGroup, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_DocTypeGroup (Properties ctx, int WMS_DocTypeGroup_ID, String trxName)
    {
      super (ctx, WMS_DocTypeGroup_ID, trxName);
      /** if (WMS_DocTypeGroup_ID == 0)
        {
			setName (null);
			setWMS_DocTypeGroup_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_DocTypeGroup (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_DocTypeGroup[")
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

	/** Set Order Type Group ID.
		@param WMS_DocTypeGroup_ID Order Type Group ID	  */
	public void setWMS_DocTypeGroup_ID (int WMS_DocTypeGroup_ID)
	{
		if (WMS_DocTypeGroup_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_DocTypeGroup_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_DocTypeGroup_ID, Integer.valueOf(WMS_DocTypeGroup_ID));
	}

	/** Get Order Type Group ID.
		@return Order Type Group ID	  */
	public int getWMS_DocTypeGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_DocTypeGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}