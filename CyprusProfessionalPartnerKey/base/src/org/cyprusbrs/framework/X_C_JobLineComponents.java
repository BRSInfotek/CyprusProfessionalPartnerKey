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

/** Generated Model for C_JobLineComponents
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_C_JobLineComponents extends PO implements I_C_JobLineComponents, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250214L;

    /** Standard Constructor */
    public X_C_JobLineComponents (Properties ctx, int C_JobLineComponents_ID, String trxName)
    {
      super (ctx, C_JobLineComponents_ID, trxName);
      /** if (C_JobLineComponents_ID == 0)
        {
			setC_JobLineComponents_ID (0);
        } */
    }

    /** Load Constructor */
    public X_C_JobLineComponents (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_C_JobLineComponents[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Job Components ID.
		@param C_JobLineComponents_ID Job Components ID	  */
	@Override
	public void setC_JobLineComponents_ID (int C_JobLineComponents_ID)
	{
		if (C_JobLineComponents_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_JobLineComponents_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_JobLineComponents_ID, Integer.valueOf(C_JobLineComponents_ID));
	}

	/** Get Job Components ID.
		@return Job Components ID	  */
	@Override
	public int getC_JobLineComponents_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_JobLineComponents_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_C_JobOrderLine getC_JobOrderLine() throws RuntimeException
    {
		return (I_C_JobOrderLine)MTable.get(getCtx(), I_C_JobOrderLine.Table_Name)
			.getPO(getC_JobOrderLine_ID(), get_TrxName());	}

	/** Set Order Line.
		@param C_JobOrderLine_ID Order Line	  */
	@Override
	public void setC_JobOrderLine_ID (int C_JobOrderLine_ID)
	{
		if (C_JobOrderLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_JobOrderLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_JobOrderLine_ID, Integer.valueOf(C_JobOrderLine_ID));
	}

	/** Get Order Line.
		@return Order Line	  */
	@Override
	public int getC_JobOrderLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_JobOrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_C_UOM getC_UOM() throws RuntimeException
    {
		return (I_C_UOM)MTable.get(getCtx(), I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	@Override
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	@Override
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Current Cost Price.
		@param CurrentCostPrice 
		The currently used cost price
	  */
	@Override
	public void setCurrentCostPrice (BigDecimal CurrentCostPrice)
	{
		set_Value (COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
	}

	/** Get Current Cost Price.
		@return The currently used cost price
	  */
	@Override
	public BigDecimal getCurrentCostPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrentCostPrice);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Line.
		@param LineNo 
		Line No
	  */
	@Override
	public void setLineNo (int LineNo)
	{
		set_Value (COLUMNNAME_LineNo, Integer.valueOf(LineNo));
	}

	/** Get Line.
		@return Line No
	  */
	@Override
	public int getLineNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LineNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_M_Product getM_Product() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	@Override
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
	@Override
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Converted Quantity.
		@param QtyConverted Converted Quantity	  */
	@Override
	public void setQtyConverted (BigDecimal QtyConverted)
	{
		set_Value (COLUMNNAME_QtyConverted, QtyConverted);
	}

	/** Get Converted Quantity.
		@return Converted Quantity	  */
	@Override
	public BigDecimal getQtyConverted () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyConverted);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity.
		@param QtyEntered 
		The Quantity Entered is based on the selected UoM
	  */
	@Override
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_Value (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	@Override
	public BigDecimal getQtyEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}