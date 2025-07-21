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

import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for WMS_ZoneLocator
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_ZoneLocator extends PO implements I_WMS_ZoneLocator, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_ZoneLocator (Properties ctx, int WMS_ZoneLocator_ID, String trxName)
    {
      super (ctx, WMS_ZoneLocator_ID, trxName);
      /** if (WMS_ZoneLocator_ID == 0)
        {
			setM_Locator_ID (0);
// -1
			setWMS_ZoneLocator_ID (0);
			setWMS_Zone_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_ZoneLocator (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_ZoneLocator[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Zone Locator ID.
		@param WMS_ZoneLocator_ID Zone Locator ID	  */
	public void setWMS_ZoneLocator_ID (int WMS_ZoneLocator_ID)
	{
		if (WMS_ZoneLocator_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_ZoneLocator_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_ZoneLocator_ID, Integer.valueOf(WMS_ZoneLocator_ID));
	}

	/** Get Zone Locator ID.
		@return Zone Locator ID	  */
	public int getWMS_ZoneLocator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_ZoneLocator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_Zone getWMS_Zone() throws RuntimeException
    {
		return (I_WMS_Zone)MTable.get(getCtx(), I_WMS_Zone.Table_Name)
			.getPO(getWMS_Zone_ID(), get_TrxName());	}

	/** Set Zone ID.
		@param WMS_Zone_ID Zone ID	  */
	public void setWMS_Zone_ID (int WMS_Zone_ID)
	{
		if (WMS_Zone_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_Zone_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_Zone_ID, Integer.valueOf(WMS_Zone_ID));
	}

	/** Get Zone ID.
		@return Zone ID	  */
	public int getWMS_Zone_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_Zone_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}