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

import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for WMS_TaskListLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_TaskListLine extends PO implements I_WMS_TaskListLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_TaskListLine (Properties ctx, int WMS_TaskListLine_ID, String trxName)
    {
      super (ctx, WMS_TaskListLine_ID, trxName);
      /** if (WMS_TaskListLine_ID == 0)
        {
			setWMS_TaskListLine_ID (0);
			setWMS_TaskList_ID (0);
			setWMS_WarehouseTask_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_TaskListLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_TaskListLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_UOM getC_UOM() throws RuntimeException
    {
		return (I_C_UOM)MTable.get(getCtx(), I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_LocatorTo() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_LocatorTo_ID(), get_TrxName());	}

	/** Set Locator To.
		@param M_LocatorTo_ID 
		Location inventory is moved to
	  */
	public void setM_LocatorTo_ID (int M_LocatorTo_ID)
	{
		if (M_LocatorTo_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_LocatorTo_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_LocatorTo_ID, Integer.valueOf(M_LocatorTo_ID));
	}

	/** Get Locator To.
		@return Location inventory is moved to
	  */
	public int getM_LocatorTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_LocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public I_M_Product getM_Product() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Slot No.
		@param SlotNo 
		Slot Number of trolley where to product must be placed in upon picking
	  */
	public void setSlotNo (int SlotNo)
	{
		set_Value (COLUMNNAME_SlotNo, Integer.valueOf(SlotNo));
	}

	/** Get Slot No.
		@return Slot Number of trolley where to product must be placed in upon picking
	  */
	public int getSlotNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SlotNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Stop No.
		@param StopNo Stop No	  */
	public void setStopNo (int StopNo)
	{
		set_Value (COLUMNNAME_StopNo, Integer.valueOf(StopNo));
	}

	/** Get Stop No.
		@return Stop No	  */
	public int getStopNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_StopNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Target Quantity.
		@param TargetQty 
		Target Movement Quantity
	  */
	public void setTargetQty (int TargetQty)
	{
		set_Value (COLUMNNAME_TargetQty, Integer.valueOf(TargetQty));
	}

	/** Get Target Quantity.
		@return Target Movement Quantity
	  */
	public int getTargetQty () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_TargetQty);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Task Line ID.
		@param WMS_TaskListLine_ID Task Line ID	  */
	public void setWMS_TaskListLine_ID (int WMS_TaskListLine_ID)
	{
		if (WMS_TaskListLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_TaskListLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_TaskListLine_ID, Integer.valueOf(WMS_TaskListLine_ID));
	}

	/** Get Task Line ID.
		@return Task Line ID	  */
	public int getWMS_TaskListLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_TaskListLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_TaskList getWMS_TaskList() throws RuntimeException
    {
		return (I_WMS_TaskList)MTable.get(getCtx(), I_WMS_TaskList.Table_Name)
			.getPO(getWMS_TaskList_ID(), get_TrxName());	}

	/** Set Task List ID.
		@param WMS_TaskList_ID Task List ID	  */
	public void setWMS_TaskList_ID (int WMS_TaskList_ID)
	{
		if (WMS_TaskList_ID < 1) 
			set_Value (COLUMNNAME_WMS_TaskList_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_TaskList_ID, Integer.valueOf(WMS_TaskList_ID));
	}

	/** Get Task List ID.
		@return Task List ID	  */
	public int getWMS_TaskList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_TaskList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_WarehouseTask getWMS_WarehouseTask() throws RuntimeException
    {
		return (I_WMS_WarehouseTask)MTable.get(getCtx(), I_WMS_WarehouseTask.Table_Name)
			.getPO(getWMS_WarehouseTask_ID(), get_TrxName());	}

	/** Set Warehouse Task.
		@param WMS_WarehouseTask_ID 
		A Warehouse Task represents a basic warehouse operation such as putaway, picking or replenishment.
	  */
	public void setWMS_WarehouseTask_ID (int WMS_WarehouseTask_ID)
	{
		if (WMS_WarehouseTask_ID < 1) 
			set_Value (COLUMNNAME_WMS_WarehouseTask_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_WarehouseTask_ID, Integer.valueOf(WMS_WarehouseTask_ID));
	}

	/** Get Warehouse Task.
		@return A Warehouse Task represents a basic warehouse operation such as putaway, picking or replenishment.
	  */
	public int getWMS_WarehouseTask_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_WarehouseTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}