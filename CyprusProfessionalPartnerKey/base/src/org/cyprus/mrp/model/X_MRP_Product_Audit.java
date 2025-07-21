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

import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MRP_Product_Audit
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_Product_Audit extends PO implements I_MRP_Product_Audit, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211222L;

    /** Standard Constructor */
    public X_MRP_Product_Audit (Properties ctx, int MRP_Product_Audit_ID, String trxName)
    {
      super (ctx, MRP_Product_Audit_ID, trxName);
      /** if (MRP_Product_Audit_ID == 0)
        {
			setMRP_PlanRun_ID (0);
			setMRP_Product_Audit_ID (0);
			setM_Product_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MRP_Product_Audit (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_Product_Audit[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	public I_MRP_PlanRun getMRP_PlanRun() throws RuntimeException
    {
		return (I_MRP_PlanRun)MTable.get(getCtx(), I_MRP_PlanRun.Table_Name)
			.getPO(getMRP_PlanRun_ID(), get_TrxName());	}

	/** Set Plan Run.
		@param MRP_PlanRun_ID Plan Run	  */
	public void setMRP_PlanRun_ID (int MRP_PlanRun_ID)
	{
		if (MRP_PlanRun_ID < 1) 
			set_Value (COLUMNNAME_MRP_PlanRun_ID, null);
		else 
			set_Value (COLUMNNAME_MRP_PlanRun_ID, Integer.valueOf(MRP_PlanRun_ID));
	}

	/** Get Plan Run.
		@return Plan Run	  */
	public int getMRP_PlanRun_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlanRun_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getMRP_PlanRun_ID()));
    }

	/** Set Product Audit ID.
		@param MRP_Product_Audit_ID Product Audit ID	  */
	public void setMRP_Product_Audit_ID (int MRP_Product_Audit_ID)
	{
		if (MRP_Product_Audit_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_Product_Audit_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_Product_Audit_ID, Integer.valueOf(MRP_Product_Audit_ID));
	}

	/** Get Product Audit ID.
		@return Product Audit ID	  */
	public int getMRP_Product_Audit_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_Product_Audit_ID);
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

	/** Set Minimum Order Qty.
		@param Order_Min 
		Minimum order quantity in UOM
	  */
	public void setOrder_Min (BigDecimal Order_Min)
	{
		set_Value (COLUMNNAME_Order_Min, Order_Min);
	}

	/** Get Minimum Order Qty.
		@return Minimum order quantity in UOM
	  */
	public BigDecimal getOrder_Min () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Order_Min);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}