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
import java.sql.Timestamp;
import java.util.Properties;

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for M_StorageDetail
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_M_StorageDetail extends PO implements I_M_StorageDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211222L;

    /** Standard Constructor */
    public X_M_StorageDetail (Properties ctx, int M_StorageDetail_ID, String trxName)
    {
      super (ctx, M_StorageDetail_ID, trxName);
      /** if (M_StorageDetail_ID == 0)
        {
        } */
    }

    /** Load Constructor */
    public X_M_StorageDetail (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_StorageDetail[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Date last inventory count.
		@param DateLastInventory 
		Date of Last Inventory Count
	  */
	@Override
	public void setDateLastInventory (Timestamp DateLastInventory)
	{
		set_Value (COLUMNNAME_DateLastInventory, DateLastInventory);
	}

	/** Get Date last inventory count.
		@return Date of Last Inventory Count
	  */
	@Override
	public Timestamp getDateLastInventory () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateLastInventory);
	}

	@Override
	public I_M_Locator getM_Locator() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_Locator_ID(), get_TrxName());	}

	/** Set Locator.
		@param M_Locator_ID 
		Warehouse Locator
	  */
	@Override
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
	@Override
	public int getM_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
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

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	@Override
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	@Override
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** QtyType AD_Reference_ID=1000060 */
	public static final int QTYTYPE_AD_Reference_ID=1000060;
	/** Allocated = A */
	public static final String QTYTYPE_Allocated = "A";
	/** Dedicated = D */
	public static final String QTYTYPE_Dedicated = "D";
	/** Expected = E */
	public static final String QTYTYPE_Expected = "E";
	/** On Hand = H */
	public static final String QTYTYPE_OnHand = "H";
	/** Ordered = O */
	public static final String QTYTYPE_Ordered = "O";
	/** Reserved = R */
	public static final String QTYTYPE_Reserved = "R";
	/** Set Quantity Type.
		@param QtyType Quantity Type	  */
	@Override
	public void setQtyType (String QtyType)
	{

		set_Value (COLUMNNAME_QtyType, QtyType);
	}

	/** Get Quantity Type.
		@return Quantity Type	  */
	@Override
	public String getQtyType () 
	{
		return (String)get_Value(COLUMNNAME_QtyType);
	}
	
	/** Set Attribute Set Instance.
	@param M_AttributeSetInstance_ID 
	Product Attribute Set Instance
  */
@Override
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
@Override
public int getM_AttributeSetInstance_ID () 
{
	Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
	if (ii == null)
		 return 0;
	return ii.intValue();
}

@Override
public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
{
	return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
		.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}
	
}