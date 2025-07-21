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

/** Generated Model for WMS_MMRule
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_MMRule extends PO implements I_WMS_MMRule, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_MMRule (Properties ctx, int WMS_MMRule_ID, String trxName)
    {
      super (ctx, WMS_MMRule_ID, trxName);
      /** if (WMS_MMRule_ID == 0)
        {
			setIsMaintainUOMIntegrity (false);
			setMMType (null);
			setM_Warehouse_ID (0);
			setName (null);
			setRule (null);
			setWMS_MMRule_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_MMRule (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_MMRule[")
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

	/** Set Maintain UOM Integrity.
		@param IsMaintainUOMIntegrity 
		This checkbox can be used to restrict the locator selection based on the stocking UOM and picking UOM of the locator
	  */
	public void setIsMaintainUOMIntegrity (boolean IsMaintainUOMIntegrity)
	{
		set_Value (COLUMNNAME_IsMaintainUOMIntegrity, Boolean.valueOf(IsMaintainUOMIntegrity));
	}

	/** Get Maintain UOM Integrity.
		@return This checkbox can be used to restrict the locator selection based on the stocking UOM and picking UOM of the locator
	  */
	public boolean isMaintainUOMIntegrity () 
	{
		Object oo = get_Value(COLUMNNAME_IsMaintainUOMIntegrity);
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

	/** Rule AD_Reference_ID=1000055 */
	public static final int RULE_AD_Reference_ID=1000055;
	/** Find a fixed locator = MMA */
	public static final String RULE_FindAFixedLocator = "MMA";
	/** Find a floating locator with the same product = MMB */
	public static final String RULE_FindAFloatingLocatorWithTheSameProduct = "MMB";
	/** Find any locator with available capacity = MMC */
	public static final String RULE_FindAnyLocatorWithAvailableCapacity = "MMC";
	/** Find item in the warehouse = MMD */
	public static final String RULE_FindItemInTheWarehouse = "MMD";
	/** Find item in the warehouse (Pick to clean) = MME */
	public static final String RULE_FindItemInTheWarehousePickToClean = "MME";
	/** Find any empty floating locator = MMF */
	public static final String RULE_FindAnyEmptyFloatingLocator = "MMF";
	/** Hard allocate purchased to order products = MMH */
	public static final String RULE_HardAllocatePurchasedToOrderProducts = "MMH";
	/** Use custom class = MMZ */
	public static final String RULE_UseCustomClass = "MMZ";
	/** Set Rule.
		@param Rule 
		Indicates which locators will qualify 
	  */
	public void setRule (String Rule)
	{

		set_Value (COLUMNNAME_Rule, Rule);
	}

	/** Get Rule.
		@return Indicates which locators will qualify 
	  */
	public String getRule () 
	{
		return (String)get_Value(COLUMNNAME_Rule);
	}

	/** Set Custom Class.
		@param RuleClass 
		Custom java class for Warehouse Management Rule
	  */
	public void setRuleClass (String RuleClass)
	{
		set_Value (COLUMNNAME_RuleClass, RuleClass);
	}

	/** Get Custom Class.
		@return Custom java class for Warehouse Management Rule
	  */
	public String getRuleClass () 
	{
		return (String)get_Value(COLUMNNAME_RuleClass);
	}

	/** Set Warehouse Management Rule ID.
		@param WMS_MMRule_ID Warehouse Management Rule ID	  */
	public void setWMS_MMRule_ID (int WMS_MMRule_ID)
	{
		if (WMS_MMRule_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_MMRule_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_MMRule_ID, Integer.valueOf(WMS_MMRule_ID));
	}

	/** Get Warehouse Management Rule ID.
		@return Warehouse Management Rule ID	  */
	public int getWMS_MMRule_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMRule_ID);
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
			set_Value (COLUMNNAME_WMS_Zone_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_Zone_ID, Integer.valueOf(WMS_Zone_ID));
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