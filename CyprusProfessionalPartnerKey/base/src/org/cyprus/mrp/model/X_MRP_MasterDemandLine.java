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
package org.cyprus.mrp.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MRP_MasterDemandLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_MasterDemandLine extends PO implements I_MRP_MasterDemandLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_MasterDemandLine (Properties ctx, int MRP_MasterDemandLine_ID, String trxName)
    {
      super (ctx, MRP_MasterDemandLine_ID, trxName);
      /** if (MRP_MasterDemandLine_ID == 0)
        {
			setC_Period_ID (0);
			setC_UOM_ID (0);
			setIsLineFrozen (false);
// N
			setMRP_MasterDemandLine_ID (0);
			setMRP_MasterDemand_ID (0);
			setM_Product_ID (0);
			setQty (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_MRP_MasterDemandLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_MasterDemandLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_Period getC_Period() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_ID(), get_TrxName());	}

	/** Set Period.
		@param C_Period_ID 
		Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Period_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	public int getC_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set LineFrozen.
		@param IsLineFrozen 
		Indicates if the record line is frozen
	  */
	public void setIsLineFrozen (boolean IsLineFrozen)
	{
		set_Value (COLUMNNAME_IsLineFrozen, Boolean.valueOf(IsLineFrozen));
	}

	/** Get LineFrozen.
		@return Indicates if the record line is frozen
	  */
	public boolean isLineFrozen () 
	{
		Object oo = get_Value(COLUMNNAME_IsLineFrozen);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Master Demand Line ID.
		@param MRP_MasterDemandLine_ID Master Demand Line ID	  */
	public void setMRP_MasterDemandLine_ID (int MRP_MasterDemandLine_ID)
	{
		if (MRP_MasterDemandLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_MasterDemandLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_MasterDemandLine_ID, Integer.valueOf(MRP_MasterDemandLine_ID));
	}

	/** Get Master Demand Line ID.
		@return Master Demand Line ID	  */
	public int getMRP_MasterDemandLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_MasterDemandLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MRP_MasterDemand getMRP_MasterDemand() throws RuntimeException
    {
		return (I_MRP_MasterDemand)MTable.get(getCtx(), I_MRP_MasterDemand.Table_Name)
			.getPO(getMRP_MasterDemand_ID(), get_TrxName());	}

	/** Set Master Demand.
		@param MRP_MasterDemand_ID 
		Master Demand for material requirements
	  */
	public void setMRP_MasterDemand_ID (int MRP_MasterDemand_ID)
	{
		if (MRP_MasterDemand_ID < 1) 
			set_Value (COLUMNNAME_MRP_MasterDemand_ID, null);
		else 
			set_Value (COLUMNNAME_MRP_MasterDemand_ID, Integer.valueOf(MRP_MasterDemand_ID));
	}

	/** Get Master Demand.
		@return Master Demand for material requirements
	  */
	public int getMRP_MasterDemand_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_MasterDemand_ID);
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
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getM_Product_ID()));
    }

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}