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

/** Generated Model for M_ProductLocator
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_M_ProductLocator extends PO implements I_M_ProductLocator, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211221L;

    /** Standard Constructor */
    public X_M_ProductLocator (Properties ctx, int M_ProductLocator_ID, String trxName)
    {
      super (ctx, M_ProductLocator_ID, trxName);
      /** if (M_ProductLocator_ID == 0)
        {
			setM_ProductLocator_ID (0);
        } */
    }

    /** Load Constructor */
    public X_M_ProductLocator (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_ProductLocator[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Product Locator ID.
		@param M_ProductLocator_ID Product Locator ID	  */
	@Override
	public void setM_ProductLocator_ID (int M_ProductLocator_ID)
	{
		if (M_ProductLocator_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_ProductLocator_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_ProductLocator_ID, Integer.valueOf(M_ProductLocator_ID));
	}

	/** Get Product Locator ID.
		@return Product Locator ID	  */
	@Override
	public int getM_ProductLocator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ProductLocator_ID);
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

	/** Set Picking Sequence No.
		@param PickingSeqNo 
		Picking Sequence Number of locator/zone
	  */
	@Override
	public void setPickingSeqNo (int PickingSeqNo)
	{
		set_Value (COLUMNNAME_PickingSeqNo, Integer.valueOf(PickingSeqNo));
	}

	/** Get Picking Sequence No.
		@return Picking Sequence Number of locator/zone
	  */
	@Override
	public int getPickingSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PickingSeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Putaway Sequence No.
		@param PutawaySeqNo 
		Putaway Sequence Number of locator/zone
	  */
	@Override
	public void setPutawaySeqNo (int PutawaySeqNo)
	{
		set_Value (COLUMNNAME_PutawaySeqNo, Integer.valueOf(PutawaySeqNo));
	}

	/** Get Putaway Sequence No.
		@return Putaway Sequence Number of locator/zone
	  */
	@Override
	public int getPutawaySeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PutawaySeqNo);
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

	/** Set Volume Limit.
		@param VolumeLimit 
		Volume Limit of a Locator
	  */
	@Override
	public void setVolumeLimit (BigDecimal VolumeLimit)
	{
		set_Value (COLUMNNAME_VolumeLimit, VolumeLimit);
	}

	/** Get Volume Limit.
		@return Volume Limit of a Locator
	  */
	@Override
	public BigDecimal getVolumeLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_VolumeLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Weight Limit.
		@param WeightLimit 
		Weight Limit of a Locator
	  */
	@Override
	public void setWeightLimit (BigDecimal WeightLimit)
	{
		set_Value (COLUMNNAME_WeightLimit, WeightLimit);
	}

	/** Get Weight Limit.
		@return Weight Limit of a Locator
	  */
	@Override
	public BigDecimal getWeightLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_WeightLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}