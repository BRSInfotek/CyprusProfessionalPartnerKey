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

import org.cyprusbrs.framework.I_C_BP_Group;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_M_Product_Category;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for WMS_MMStrategySetLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_MMStrategySetLine extends PO implements I_WMS_MMStrategySetLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_MMStrategySetLine (Properties ctx, int WMS_MMStrategySetLine_ID, String trxName)
    {
      super (ctx, WMS_MMStrategySetLine_ID, trxName);
      /** if (WMS_MMStrategySetLine_ID == 0)
        {
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM WMS_MMStrategySetLine WHERE WMS_MMStrategySet_ID=@WMS_MMStrategySet_ID@
			setWMS_MMStrategySetLine_ID (0);
			setWMS_MMStrategySet_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_MMStrategySetLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_MMStrategySetLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_BP_Group getC_BP_Group() throws RuntimeException
    {
		return (I_C_BP_Group)MTable.get(getCtx(), I_C_BP_Group.Table_Name)
			.getPO(getC_BP_Group_ID(), get_TrxName());	}

	/** Set Business Partner Group.
		@param C_BP_Group_ID 
		Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID)
	{
		if (C_BP_Group_ID < 1) 
			set_Value (COLUMNNAME_C_BP_Group_ID, null);
		else 
			set_Value (COLUMNNAME_C_BP_Group_ID, Integer.valueOf(C_BP_Group_ID));
	}

	/** Get Business Partner Group.
		@return Business Partner Group
	  */
	public int getC_BP_Group_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_Group_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (I_C_BPartner)MTable.get(getCtx(), I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
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

	public I_M_Product_Category getM_Product_Category() throws RuntimeException
    {
		return (I_M_Product_Category)MTable.get(getCtx(), I_M_Product_Category.Table_Name)
			.getPO(getM_Product_Category_ID(), get_TrxName());	}

	/** Set Product Category.
		@param M_Product_Category_ID 
		Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		if (M_Product_Category_ID < 1) 
			set_Value (COLUMNNAME_M_Product_Category_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_Category_ID, Integer.valueOf(M_Product_Category_ID));
	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
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

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_DocTypeGroup getWMS_DocTypeGroup() throws RuntimeException
    {
		return (I_WMS_DocTypeGroup)MTable.get(getCtx(), I_WMS_DocTypeGroup.Table_Name)
			.getPO(getWMS_DocTypeGroup_ID(), get_TrxName());	}

	/** Set Order Type Group ID.
		@param WMS_DocTypeGroup_ID Order Type Group ID	  */
	public void setWMS_DocTypeGroup_ID (int WMS_DocTypeGroup_ID)
	{
		if (WMS_DocTypeGroup_ID < 1) 
			set_Value (COLUMNNAME_WMS_DocTypeGroup_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_DocTypeGroup_ID, Integer.valueOf(WMS_DocTypeGroup_ID));
	}

	/** Get Order Type Group ID.
		@return Order Type Group ID	  */
	public int getWMS_DocTypeGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_DocTypeGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Material Management Strategy Set Line ID.
		@param WMS_MMStrategySetLine_ID Material Management Strategy Set Line ID	  */
	public void setWMS_MMStrategySetLine_ID (int WMS_MMStrategySetLine_ID)
	{
		if (WMS_MMStrategySetLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategySetLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategySetLine_ID, Integer.valueOf(WMS_MMStrategySetLine_ID));
	}

	/** Get Material Management Strategy Set Line ID.
		@return Material Management Strategy Set Line ID	  */
	public int getWMS_MMStrategySetLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMStrategySetLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_MMStrategySet getWMS_MMStrategySet() throws RuntimeException
    {
		return (I_WMS_MMStrategySet)MTable.get(getCtx(), I_WMS_MMStrategySet.Table_Name)
			.getPO(getWMS_MMStrategySet_ID(), get_TrxName());	}

	/** Set Material Management Strategy Set ID.
		@param WMS_MMStrategySet_ID Material Management Strategy Set ID	  */
	public void setWMS_MMStrategySet_ID (int WMS_MMStrategySet_ID)
	{
		if (WMS_MMStrategySet_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategySet_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategySet_ID, Integer.valueOf(WMS_MMStrategySet_ID));
	}

	/** Get Material Management Strategy Set ID.
		@return Material Management Strategy Set ID	  */
	public int getWMS_MMStrategySet_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMStrategySet_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_MMStrategy getWMS_MMStrategy() throws RuntimeException
    {
		return (I_WMS_MMStrategy)MTable.get(getCtx(), I_WMS_MMStrategy.Table_Name)
			.getPO(getWMS_MMStrategy_ID(), get_TrxName());	}

	/** Set Warehouse Management Strategy ID.
		@param WMS_MMStrategy_ID Warehouse Management Strategy ID	  */
	public void setWMS_MMStrategy_ID (int WMS_MMStrategy_ID)
	{
		if (WMS_MMStrategy_ID < 1) 
			set_Value (COLUMNNAME_WMS_MMStrategy_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_MMStrategy_ID, Integer.valueOf(WMS_MMStrategy_ID));
	}

	/** Get Warehouse Management Strategy ID.
		@return Warehouse Management Strategy ID	  */
	public int getWMS_MMStrategy_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMStrategy_ID);
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