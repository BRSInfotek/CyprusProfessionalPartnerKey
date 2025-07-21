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

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for M_Grafana
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_M_Grafana extends PO implements I_M_Grafana, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250612L;

    /** Standard Constructor */
    public X_M_Grafana (Properties ctx, int M_Grafana_ID, String trxName)
    {
      super (ctx, M_Grafana_ID, trxName);
      /** if (M_Grafana_ID == 0)
        {
			setDashboardType (null);
// A
			setM_Grafana_ID (0);
			setName (null);
			setURL (null);
        } */
    }

    /** Load Constructor */
    public X_M_Grafana (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_Grafana[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** DashboardType AD_Reference_ID=1000075 */
	public static final int DASHBOARDTYPE_AD_Reference_ID=1000075;
	/** Business Insight First = A */
	public static final String DASHBOARDTYPE_BusinessInsightFirst = "A";
	/** Business Insight Second = B */
	public static final String DASHBOARDTYPE_BusinessInsightSecond = "B";
	/** Business Insight Third = C */
	public static final String DASHBOARDTYPE_BusinessInsightThird = "C";
	/** Set I-Frame Sequence.
		@param DashboardType I-Frame Sequence	  */
	public void setDashboardType (String DashboardType)
	{

		set_Value (COLUMNNAME_DashboardType, DashboardType);
	}

	/** Get I-Frame Sequence.
		@return I-Frame Sequence	  */
	public String getDashboardType () 
	{
		return (String)get_Value(COLUMNNAME_DashboardType);
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

	/** Set Display Sequence.
		@param DisplaySequence 
		Format for Display Sequence
	  */
	public void setDisplaySequence (int DisplaySequence)
	{
		set_Value (COLUMNNAME_DisplaySequence, Integer.valueOf(DisplaySequence));
	}

	/** Get Display Sequence.
		@return Format for Display Sequence
	  */
	public int getDisplaySequence () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DisplaySequence);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Height.
		@param Height Height	  */
	public void setHeight (int Height)
	{
		set_Value (COLUMNNAME_Height, Integer.valueOf(Height));
	}

	/** Get Height.
		@return Height	  */
	public int getHeight () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Height);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Grafana ID.
		@param M_Grafana_ID Grafana ID	  */
	public void setM_Grafana_ID (int M_Grafana_ID)
	{
		if (M_Grafana_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Grafana_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Grafana_ID, Integer.valueOf(M_Grafana_ID));
	}

	/** Get Grafana ID.
		@return Grafana ID	  */
	public int getM_Grafana_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Grafana_ID);
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

	/** Set Refresh Time (In seconds).
		@param RefreshTimeInSec Refresh Time (In seconds)	  */
	public void setRefreshTimeInSec (int RefreshTimeInSec)
	{
		set_Value (COLUMNNAME_RefreshTimeInSec, Integer.valueOf(RefreshTimeInSec));
	}

	/** Get Refresh Time (In seconds).
		@return Refresh Time (In seconds)	  */
	public int getRefreshTimeInSec () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RefreshTimeInSec);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Grafana Report URL.
		@param URL 
		Full URL address - e.g. http://www.cypruserp.com
	  */
	public void setURL (String URL)
	{
		set_Value (COLUMNNAME_URL, URL);
	}

	/** Get Grafana Report URL.
		@return Full URL address - e.g. http://www.cypruserp.com
	  */
	public String getURL () 
	{
		return (String)get_Value(COLUMNNAME_URL);
	}

	/** Set Width.
		@param Width Width	  */
	public void setWidth (int Width)
	{
		set_Value (COLUMNNAME_Width, Integer.valueOf(Width));
	}

	/** Get Width.
		@return Width	  */
	public int getWidth () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Width);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}