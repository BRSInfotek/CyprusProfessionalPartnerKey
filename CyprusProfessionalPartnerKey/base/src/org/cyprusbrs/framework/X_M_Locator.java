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

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for M_Locator
 *  @author Adempiere (generated) 
 *  @version Release 3.6.0LTS - $Id$ */
public class X_M_Locator extends PO implements I_M_Locator, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20100614L;

    /** Standard Constructor */
    public X_M_Locator (Properties ctx, int M_Locator_ID, String trxName)
    {
      super (ctx, M_Locator_ID, trxName);
      /** if (M_Locator_ID == 0)
        {
			setIsDefault (false);
			setM_Locator_ID (0);
			setM_Warehouse_ID (0);
			setPriorityNo (0);
// 50
			setValue (null);
			setX (null);
			setY (null);
			setZ (null);
        } */
    }

    /** Load Constructor */
    public X_M_Locator (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
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
      StringBuffer sb = new StringBuffer ("X_M_Locator[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Default.
		@param IsDefault 
		Default value
	  */
	@Override
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	@Override
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

	/** Set Locator.
		@param M_Locator_ID 
		Warehouse Locator
	  */
	@Override
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Locator_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	@Override
	public int getM_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (I_M_Warehouse)MTable.get(getCtx(), I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
	  */
	@Override
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	@Override
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Relative Priority.
		@param PriorityNo 
		Where inventory should be picked from first
	  */
	@Override
	public void setPriorityNo (int PriorityNo)
	{
		set_Value (COLUMNNAME_PriorityNo, Integer.valueOf(PriorityNo));
	}

	/** Get Relative Priority.
		@return Where inventory should be picked from first
	  */
	@Override
	public int getPriorityNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PriorityNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	@Override
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	@Override
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getValue());
    }

	/** Set Aisle (X).
		@param X 
		X dimension, e.g., Aisle
	  */
	@Override
	public void setX (String X)
	{
		set_Value (COLUMNNAME_X, X);
	}

	/** Get Aisle (X).
		@return X dimension, e.g., Aisle
	  */
	@Override
	public String getX () 
	{
		return (String)get_Value(COLUMNNAME_X);
	}

	/** Set Bin (Y).
		@param Y 
		Y dimension, e.g., Bin
	  */
	@Override
	public void setY (String Y)
	{
		set_Value (COLUMNNAME_Y, Y);
	}

	/** Get Bin (Y).
		@return Y dimension, e.g., Bin
	  */
	@Override
	public String getY () 
	{
		return (String)get_Value(COLUMNNAME_Y);
	}

	/** Set Level (Z).
		@param Z 
		Z dimension, e.g., Level
	  */
	@Override
	public void setZ (String Z)
	{
		set_Value (COLUMNNAME_Z, Z);
	}

	/** Get Level (Z).
		@return Z dimension, e.g., Level
	  */
	@Override
	public String getZ () 
	{
		return (String)get_Value(COLUMNNAME_Z);
	}
	
	@Override
	public I_C_UOM getPicking_UOM() throws RuntimeException
    {
		return (I_C_UOM)MTable.get(getCtx(), I_C_UOM.Table_Name)
			.getPO(getPicking_UOM_ID(), get_TrxName());	}

	/** Set Picking UOM.
		@param Picking_UOM_ID 
		Picking UOM of locator
	  */
	@Override
	public void setPicking_UOM_ID (int Picking_UOM_ID)
	{
		if (Picking_UOM_ID < 1) 
			set_Value (COLUMNNAME_Picking_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_Picking_UOM_ID, Integer.valueOf(Picking_UOM_ID));
	}

	/** Get Picking UOM.
		@return Picking UOM of locator
	  */
	@Override
	public int getPicking_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Picking_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	@Override
	public I_C_UOM getStocking_UOM() throws RuntimeException
    {
		return (I_C_UOM)MTable.get(getCtx(), I_C_UOM.Table_Name)
			.getPO(getStocking_UOM_ID(), get_TrxName());	}

	/** Set Stocking UOM.
		@param Stocking_UOM_ID 
		Stocking UOM of locator
	  */
	@Override
	public void setStocking_UOM_ID (int Stocking_UOM_ID)
	{
		if (Stocking_UOM_ID < 1) 
			set_Value (COLUMNNAME_Stocking_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_Stocking_UOM_ID, Integer.valueOf(Stocking_UOM_ID));
	}

	/** Get Stocking UOM.
		@return Stocking UOM of locator
	  */
	@Override
	public int getStocking_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Stocking_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	/** Set Max Stocking Quantity.
	@param MaxQuantity 
	Maximum stocking capacity of the locator in units
  */
@Override
public void setMaxQuantity (BigDecimal MaxQuantity)
{
	set_Value (COLUMNNAME_MaxQuantity, MaxQuantity);
}

/** Get Max Stocking Quantity.
	@return Maximum stocking capacity of the locator in units
  */
@Override
public BigDecimal getMaxQuantity () 
{
	BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MaxQuantity);
	if (bd == null)
		 return Env.ZERO;
	return bd;
}

/** Set Min Stocking Quantity.
	@param MinQuantity 
	Minimum stocking quantity of locator in units
  */
@Override
public void setMinQuantity (BigDecimal MinQuantity)
{
	set_Value (COLUMNNAME_MinQuantity, MinQuantity);
}

/** Get Min Stocking Quantity.
	@return Minimum stocking quantity of locator in units
  */
@Override
public BigDecimal getMinQuantity () 
{
	BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MinQuantity);
	if (bd == null)
		 return Env.ZERO;
	return bd;
}

/** Set Available For Allocation.
@param IsAvailableForAllocation 
Stock in this locator is available for allocation
*/
@Override
public void setIsAvailableForAllocation (boolean IsAvailableForAllocation)
{
set_Value (COLUMNNAME_IsAvailableForAllocation, Boolean.valueOf(IsAvailableForAllocation));
}

/** Get Available For Allocation.
@return Stock in this locator is available for allocation
*/
@Override
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
@Override
public void setIsAvailableToPromise (boolean IsAvailableToPromise)
{
set_Value (COLUMNNAME_IsAvailableToPromise, Boolean.valueOf(IsAvailableToPromise));
}

/** Get Available To Promise.
@return Stock in this locator is available to promise
*/
@Override
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
}