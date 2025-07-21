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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_M_AttributeSetInstance;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for WMS_WarehouseTaskMA
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_WarehouseTaskMA extends PO implements I_WMS_WarehouseTaskMA, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_WarehouseTaskMA (Properties ctx, int WMS_WarehouseTaskMA_ID, String trxName)
    {
      super (ctx, WMS_WarehouseTaskMA_ID, trxName);
      /** if (WMS_WarehouseTaskMA_ID == 0)
        {
			setWMS_WarehouseTaskMA_ID (0);
			setWMS_WarehouseTask_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_WarehouseTaskMA (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuffer sb = new StringBuffer ("X_WMS_WarehouseTaskMA[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}

	/** Set Attribute Set Instance.
		@param M_AttributeSetInstance_ID 
		Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0) 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Set Instance.
		@return Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Dedicated.
		@param QtyDedicated 
		Quantity for which there is a pending Warehouse Task
	  */
	public void setQtyDedicated (BigDecimal QtyDedicated)
	{
		set_Value (COLUMNNAME_QtyDedicated, QtyDedicated);
	}

	/** Get Quantity Dedicated.
		@return Quantity for which there is a pending Warehouse Task
	  */
	public BigDecimal getQtyDedicated () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyDedicated);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Warehouse Task Material Allocation ID.
		@param WMS_WarehouseTaskMA_ID Warehouse Task Material Allocation ID	  */
	public void setWMS_WarehouseTaskMA_ID (int WMS_WarehouseTaskMA_ID)
	{
		if (WMS_WarehouseTaskMA_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_WarehouseTaskMA_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_WarehouseTaskMA_ID, Integer.valueOf(WMS_WarehouseTaskMA_ID));
	}

	/** Get Warehouse Task Material Allocation ID.
		@return Warehouse Task Material Allocation ID	  */
	public int getWMS_WarehouseTaskMA_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_WarehouseTaskMA_ID);
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
			set_ValueNoCheck (COLUMNNAME_WMS_WarehouseTask_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_WarehouseTask_ID, Integer.valueOf(WMS_WarehouseTask_ID));
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