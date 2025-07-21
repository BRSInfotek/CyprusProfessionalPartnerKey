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

import org.cyprus.mfg.model.I_MFG_WorkOrderComponent;
import org.cyprusbrs.framework.I_C_OrderLine;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for WMS_WaveLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_WaveLine extends PO implements I_WMS_WaveLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_WaveLine (Properties ctx, int WMS_WaveLine_ID, String trxName)
    {
      super (ctx, WMS_WaveLine_ID, trxName);
      /** if (WMS_WaveLine_ID == 0)
        {
			setMovementQty (Env.ZERO);
			setWMS_WaveLine_ID (0);
			setWMS_Wave_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_WaveLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_WaveLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_OrderLine getC_OrderLine() throws RuntimeException
    {
		return (I_C_OrderLine)MTable.get(getCtx(), I_C_OrderLine.Table_Name)
			.getPO(getC_OrderLine_ID(), get_TrxName());	}

	/** Set Sales Order Line.
		@param C_OrderLine_ID 
		Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID)
	{
		if (C_OrderLine_ID < 1) 
			set_Value (COLUMNNAME_C_OrderLine_ID, null);
		else 
			set_Value (COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
	}

	/** Get Sales Order Line.
		@return Sales Order Line
	  */
	public int getC_OrderLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderComponent getMFG_WorkOrderComponent() throws RuntimeException
    {
		return (I_MFG_WorkOrderComponent)MTable.get(getCtx(), I_MFG_WorkOrderComponent.Table_Name)
			.getPO(getMFG_WorkOrderComponent_ID(), get_TrxName());	}

	/** Set Work Order Component ID.
		@param MFG_WorkOrderComponent_ID Work Order Component ID	  */
	public void setMFG_WorkOrderComponent_ID (int MFG_WorkOrderComponent_ID)
	{
		if (MFG_WorkOrderComponent_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderComponent_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderComponent_ID, Integer.valueOf(MFG_WorkOrderComponent_ID));
	}

	/** Get Work Order Component ID.
		@return Work Order Component ID	  */
	public int getMFG_WorkOrderComponent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderComponent_ID);
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

	/** Set Wave Line.
		@param WMS_WaveLine_ID 
		Selected order lines for which there is sufficient onhand quantity in the warehouse
	  */
	public void setWMS_WaveLine_ID (int WMS_WaveLine_ID)
	{
		if (WMS_WaveLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_WaveLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_WaveLine_ID, Integer.valueOf(WMS_WaveLine_ID));
	}

	/** Get Wave Line.
		@return Selected order lines for which there is sufficient onhand quantity in the warehouse
	  */
	public int getWMS_WaveLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_WaveLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_Wave getWMS_Wave() throws RuntimeException
    {
		return (I_WMS_Wave)MTable.get(getCtx(), I_WMS_Wave.Table_Name)
			.getPO(getWMS_Wave_ID(), get_TrxName());	}

	/** Set Wave ID.
		@param WMS_Wave_ID Wave ID	  */
	public void setWMS_Wave_ID (int WMS_Wave_ID)
	{
		if (WMS_Wave_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_Wave_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_Wave_ID, Integer.valueOf(WMS_Wave_ID));
	}

	/** Get Wave ID.
		@return Wave ID	  */
	public int getWMS_Wave_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_Wave_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}