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

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for M_Resources
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_M_Resources extends PO implements I_M_Resources, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250116L;

    /** Standard Constructor */
    public X_M_Resources (Properties ctx, int M_Resources_ID, String trxName)
    {
      super (ctx, M_Resources_ID, trxName);
      /** if (M_Resources_ID == 0)
        {
			setM_Resources_ID (0);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM M_Resources WHERE M_Operation_ID=@M_Operation_ID@
        } */
    }

    /** Load Constructor */
    public X_M_Resources (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_Resources[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	@Override
	public I_A_Asset getA_Asset() throws RuntimeException
    {
		return (I_A_Asset)MTable.get(getCtx(), I_A_Asset.Table_Name)
			.getPO(getA_Asset_ID(), get_TrxName());	}

	/** Set Asset.
		@param A_Asset_ID 
		Asset used internally or by customers
	  */
	@Override
	public void setA_Asset_ID (int A_Asset_ID)
	{
		if (A_Asset_ID < 1) 
			set_Value (COLUMNNAME_A_Asset_ID, null);
		else 
			set_Value (COLUMNNAME_A_Asset_ID, Integer.valueOf(A_Asset_ID));
	}

	/** Get Asset.
		@return Asset used internally or by customers
	  */
	@Override
	public int getA_Asset_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_ID);
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

	@Override
	public I_M_Operation getM_Operation() throws RuntimeException
    {
		return (I_M_Operation)MTable.get(getCtx(), I_M_Operation.Table_Name)
			.getPO(getM_Operation_ID(), get_TrxName());	}

	/** Set Operation.
		@param M_Operation_ID Operation	  */
	@Override
	public void setM_Operation_ID (int M_Operation_ID)
	{
		if (M_Operation_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Operation_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Operation_ID, Integer.valueOf(M_Operation_ID));
	}

	/** Get Operation.
		@return Operation	  */
	@Override
	public int getM_Operation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Operation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_M_Product getM_Product() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Production Resource.
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

	/** Get Production Resource.
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

	/** Set Resources ID.
		@param M_Resources_ID Resources ID	  */
	@Override
	public void setM_Resources_ID (int M_Resources_ID)
	{
		if (M_Resources_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Resources_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Resources_ID, Integer.valueOf(M_Resources_ID));
	}

	/** Get Resources ID.
		@return Resources ID	  */
	@Override
	public int getM_Resources_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Resources_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Qty Requiered.
		@param QtyRequiered Qty Requiered	  */
	@Override
	public void setQtyRequiered (int QtyRequiered)
	{
		set_Value (COLUMNNAME_QtyRequiered, Integer.valueOf(QtyRequiered));
	}

	/** Get Qty Requiered.
		@return Qty Requiered	  */
	@Override
	public int getQtyRequiered () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_QtyRequiered);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	@Override
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	@Override
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}