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
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for A_Asset_Group
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS - $Id$ */
public class X_A_Asset_Group extends PO implements I_A_Asset_Group, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20100614L;

    /** Standard Constructor */
    public X_A_Asset_Group (Properties ctx, int A_Asset_Group_ID, String trxName)
    {
      super (ctx, A_Asset_Group_ID, trxName);
      /** if (A_Asset_Group_ID == 0)
        {
			setA_Asset_Group_ID (0);
			setIsCreateAsActive (true);
// Y
			setIsDepreciated (false);
			setIsOneAssetPerUOM (false);
			setIsOwned (false);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_A_Asset_Group (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    @Override
	protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    @Override
	protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    @Override
	public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_A_Asset_Group[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Asset Group.
		@param A_Asset_Group_ID 
		Group of Assets
	  */
	@Override
	public void setA_Asset_Group_ID (int A_Asset_Group_ID)
	{
		if (A_Asset_Group_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_A_Asset_Group_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_A_Asset_Group_ID, Integer.valueOf(A_Asset_Group_ID));
	}

	/** Get Asset Group.
		@return Group of Assets
	  */
	@Override
	public int getA_Asset_Group_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_Group_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getA_Asset_Group_ID()));
    }

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	@Override
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	@Override
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	@Override
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	@Override
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Create As Active.
		@param IsCreateAsActive 
		Create Asset and activate it
	  */
	@Override
	public void setIsCreateAsActive (boolean IsCreateAsActive)
	{
		set_Value (COLUMNNAME_IsCreateAsActive, Boolean.valueOf(IsCreateAsActive));
	}

	/** Get Create As Active.
		@return Create Asset and activate it
	  */
	@Override
	public boolean isCreateAsActive () 
	{
		Object oo = get_Value(COLUMNNAME_IsCreateAsActive);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Depreciate.
		@param IsDepreciated 
		The asset will be depreciated
	  */
	@Override
	public void setIsDepreciated (boolean IsDepreciated)
	{
		set_Value (COLUMNNAME_IsDepreciated, Boolean.valueOf(IsDepreciated));
	}

	/** Get Depreciate.
		@return The asset will be depreciated
	  */
	@Override
	public boolean isDepreciated () 
	{
		Object oo = get_Value(COLUMNNAME_IsDepreciated);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set One Asset Per UOM.
		@param IsOneAssetPerUOM 
		Create one asset per UOM
	  */
	@Override
	public void setIsOneAssetPerUOM (boolean IsOneAssetPerUOM)
	{
		set_Value (COLUMNNAME_IsOneAssetPerUOM, Boolean.valueOf(IsOneAssetPerUOM));
	}

	/** Get One Asset Per UOM.
		@return Create one asset per UOM
	  */
	@Override
	public boolean isOneAssetPerUOM () 
	{
		Object oo = get_Value(COLUMNNAME_IsOneAssetPerUOM);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Owned.
		@param IsOwned 
		The asset is owned by the organization
	  */
	@Override
	public void setIsOwned (boolean IsOwned)
	{
		set_Value (COLUMNNAME_IsOwned, Boolean.valueOf(IsOwned));
	}

	/** Get Owned.
		@return The asset is owned by the organization
	  */
	@Override
	public boolean isOwned () 
	{
		Object oo = get_Value(COLUMNNAME_IsOwned);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Track Issues.
		@param IsTrackIssues 
		Enable tracking issues for this asset
	  */
	@Override
	public void setIsTrackIssues (boolean IsTrackIssues)
	{
		set_Value (COLUMNNAME_IsTrackIssues, Boolean.valueOf(IsTrackIssues));
	}

	/** Get Track Issues.
		@return Enable tracking issues for this asset
	  */
	@Override
	public boolean isTrackIssues () 
	{
		Object oo = get_Value(COLUMNNAME_IsTrackIssues);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	@Override
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	@Override
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}
	
		/** Set Scrap Value.
		@param ScrapValue Scrap Value	  */
	@Override
	public void setScrapValue (BigDecimal ScrapValue)
	{
		set_Value (COLUMNNAME_ScrapValue, ScrapValue);
	}
	
	/** Get Scrap Value.
		@return Scrap Value	  */
	@Override
	public BigDecimal getScrapValue () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ScrapValue);
		if (bd == null)
			 return new BigDecimal(0);
		return bd;
	}
	
	/** Set Usable Life - Months.
		@param UseLifeMonths 
		Months of the usable life of the asset
	  */
	@Override
	public void setUseLifeMonths (int UseLifeMonths)
	{
		set_Value (COLUMNNAME_UseLifeMonths, Integer.valueOf(UseLifeMonths));
	}
	
	/** Get Usable Life - Months.
		@return Months of the usable life of the asset
	  */
	@Override
	public int getUseLifeMonths () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_UseLifeMonths);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	/** Set Usable Life - Years.
		@param UseLifeYears 
		Years of the usable life of the asset
	  */
	@Override
	public void setUseLifeYears (int UseLifeYears)
	{
		set_Value (COLUMNNAME_UseLifeYears, Integer.valueOf(UseLifeYears));
	}
	
	/** Get Usable Life - Years.
		@return Years of the usable life of the asset
	  */
	@Override
	public int getUseLifeYears () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_UseLifeYears);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	/** UseableLifeType AD_Reference_ID=1000217 */
	public static final int USEABLELIFETYPE_AD_Reference_ID=1000217;
	/** Period = PR */
	public static final String USEABLELIFETYPE_Period = "PR";
	/** Year = YR */
	public static final String USEABLELIFETYPE_Year = "YR";
	/** Set Useable Life Type.
		@param UseableLifeType Useable Life Type	  */
	@Override
	public void setUseableLifeType (String UseableLifeType)
	{
	
		set_Value (COLUMNNAME_UseableLifeType, UseableLifeType);
	}
	
	/** Get Useable Life Type.
		@return Useable Life Type	  */
	@Override
	public String getUseableLifeType () 
	{
		return (String)get_Value(COLUMNNAME_UseableLifeType);
	}
	
}