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

/** Generated Model for WMS_Zone
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_Zone extends PO implements I_WMS_Zone, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_Zone (Properties ctx, int WMS_Zone_ID, String trxName)
    {
      super (ctx, WMS_Zone_ID, trxName);
      /** if (WMS_Zone_ID == 0)
        {
			setIsAvailableForAllocation (true);
// Y
			setIsAvailableToPromise (true);
// Y
			setIsStatic (true);
// Y
			setM_Warehouse_ID (0);
			setName (null);
			setPickingSeqNo (0);
			setPutawaySeqNo (0);
			setReplenishmentSeqNo (0);
			setWMS_Zone_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_Zone (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_Zone[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Add Locators.
		@param AddLocator 
		Add Locators to Zone
	  */
	public void setAddLocator (String AddLocator)
	{
		set_Value (COLUMNNAME_AddLocator, AddLocator);
	}

	/** Get Add Locators.
		@return Add Locators to Zone
	  */
	public String getAddLocator () 
	{
		return (String)get_Value(COLUMNNAME_AddLocator);
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

	/** Set Available For Allocation.
		@param IsAvailableForAllocation 
		Stock in this locator is available for allocation
	  */
	public void setIsAvailableForAllocation (boolean IsAvailableForAllocation)
	{
		set_Value (COLUMNNAME_IsAvailableForAllocation, Boolean.valueOf(IsAvailableForAllocation));
	}

	/** Get Available For Allocation.
		@return Stock in this locator is available for allocation
	  */
	public boolean isAvailableForAllocation () 
	{
		Object oo = get_Value(COLUMNNAME_IsAvailableForAllocation);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Available To Promise.
		@param IsAvailableToPromise 
		Stock in this locator is available to promise
	  */
	public void setIsAvailableToPromise (boolean IsAvailableToPromise)
	{
		set_Value (COLUMNNAME_IsAvailableToPromise, Boolean.valueOf(IsAvailableToPromise));
	}

	/** Get Available To Promise.
		@return Stock in this locator is available to promise
	  */
	public boolean isAvailableToPromise () 
	{
		Object oo = get_Value(COLUMNNAME_IsAvailableToPromise);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Static Zone.
		@param IsStatic 
		If checked, this zone cannot overlap other static zones
	  */
	public void setIsStatic (boolean IsStatic)
	{
		set_Value (COLUMNNAME_IsStatic, Boolean.valueOf(IsStatic));
	}

	/** Get Static Zone.
		@return If checked, this zone cannot overlap other static zones
	  */
	public boolean isStatic () 
	{
		Object oo = get_Value(COLUMNNAME_IsStatic);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Picking Sequence No.
		@param PickingSeqNo 
		Picking Sequence Number of locator/zone
	  */
	public void setPickingSeqNo (int PickingSeqNo)
	{
		set_Value (COLUMNNAME_PickingSeqNo, Integer.valueOf(PickingSeqNo));
	}

	/** Get Picking Sequence No.
		@return Picking Sequence Number of locator/zone
	  */
	public int getPickingSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PickingSeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Putaway Sequence No.
		@param PutawaySeqNo 
		Putaway Sequence Number of locator/zone
	  */
	public void setPutawaySeqNo (int PutawaySeqNo)
	{
		set_Value (COLUMNNAME_PutawaySeqNo, Integer.valueOf(PutawaySeqNo));
	}

	/** Get Putaway Sequence No.
		@return Putaway Sequence Number of locator/zone
	  */
	public int getPutawaySeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PutawaySeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Replenishment Sequence No.
		@param ReplenishmentSeqNo 
		Replenishment Sequence No of zone
	  */
	public void setReplenishmentSeqNo (int ReplenishmentSeqNo)
	{
		set_Value (COLUMNNAME_ReplenishmentSeqNo, Integer.valueOf(ReplenishmentSeqNo));
	}

	/** Get Replenishment Sequence No.
		@return Replenishment Sequence No of zone
	  */
	public int getReplenishmentSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ReplenishmentSeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Synchronize Defaults.
		@param SynchronizeDefaults 
		Copy the values of Available to Promise and Available for Allocation flags to all locators in this zone.
	  */
	public void setSynchronizeDefaults (String SynchronizeDefaults)
	{
		set_Value (COLUMNNAME_SynchronizeDefaults, SynchronizeDefaults);
	}

	/** Get Synchronize Defaults.
		@return Copy the values of Available to Promise and Available for Allocation flags to all locators in this zone.
	  */
	public String getSynchronizeDefaults () 
	{
		return (String)get_Value(COLUMNNAME_SynchronizeDefaults);
	}

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