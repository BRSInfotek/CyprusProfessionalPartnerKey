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
import java.sql.Timestamp;
import java.util.Properties;

import org.cyprus.mfg.model.I_MFG_WorkOrder;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_BOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MRP_WorkOrder_Audit
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_WorkOrder_Audit extends PO implements I_MRP_WorkOrder_Audit, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_WorkOrder_Audit (Properties ctx, int MRP_WorkOrder_Audit_ID, String trxName)
    {
      super (ctx, MRP_WorkOrder_Audit_ID, trxName);
      /** if (MRP_WorkOrder_Audit_ID == 0)
        {
			setC_UOM_ID (0);
			setDateExpected (new Timestamp( System.currentTimeMillis() ));
			setMFG_WorkOrder_ID (0);
			setMRP_PlanRun_ID (0);
			setMRP_WorkOrder_Audit_ID (0);
			setM_BOM_ID (0);
			setM_Product_ID (0);
			setQtyDemand (Env.ZERO);
// 0
			setQtyExpected (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_MRP_WorkOrder_Audit (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_WorkOrder_Audit[")
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

	/** Set Date Expected.
		@param DateExpected 
		Date on which the order is expected to be fulfilled
	  */
	public void setDateExpected (Timestamp DateExpected)
	{
		set_ValueNoCheck (COLUMNNAME_DateExpected, DateExpected);
	}

	/** Get Date Expected.
		@return Date on which the order is expected to be fulfilled
	  */
	public Timestamp getDateExpected () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateExpected);
	}

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException
    {
		return (I_MFG_WorkOrder)MTable.get(getCtx(), I_MFG_WorkOrder.Table_Name)
			.getPO(getMFG_WorkOrder_ID(), get_TrxName());	}

	/** Set Work Order.
		@param MFG_WorkOrder_ID Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID)
	{
		if (MFG_WorkOrder_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrder_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrder_ID, Integer.valueOf(MFG_WorkOrder_ID));
	}

	/** Get Work Order.
		@return Work Order	  */
	public int getMFG_WorkOrder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrder_ID);
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
			set_ValueNoCheck (COLUMNNAME_MRP_PlanRun_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_PlanRun_ID, Integer.valueOf(MRP_PlanRun_ID));
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

	/** Set MRP_WorkOrder_Audit_ID ID.
		@param MRP_WorkOrder_Audit_ID MRP_WorkOrder_Audit_ID ID	  */
	public void setMRP_WorkOrder_Audit_ID (int MRP_WorkOrder_Audit_ID)
	{
		if (MRP_WorkOrder_Audit_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_WorkOrder_Audit_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_WorkOrder_Audit_ID, Integer.valueOf(MRP_WorkOrder_Audit_ID));
	}

	/** Get MRP_WorkOrder_Audit_ID ID.
		@return MRP_WorkOrder_Audit_ID ID	  */
	public int getMRP_WorkOrder_Audit_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_WorkOrder_Audit_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_BOM getM_BOM() throws RuntimeException
    {
		return (I_M_BOM)MTable.get(getCtx(), I_M_BOM.Table_Name)
			.getPO(getM_BOM_ID(), get_TrxName());	}

	/** Set BOM.
		@param M_BOM_ID 
		Bill of Material
	  */
	public void setM_BOM_ID (int M_BOM_ID)
	{
		if (M_BOM_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_BOM_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_BOM_ID, Integer.valueOf(M_BOM_ID));
	}

	/** Get BOM.
		@return Bill of Material
	  */
	public int getM_BOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_BOM_ID);
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

	/** Set Quantity Demand.
		@param QtyDemand 
		Product quantity considered as demand
	  */
	public void setQtyDemand (BigDecimal QtyDemand)
	{
		set_ValueNoCheck (COLUMNNAME_QtyDemand, QtyDemand);
	}

	/** Get Quantity Demand.
		@return Product quantity considered as demand
	  */
	public BigDecimal getQtyDemand () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyDemand);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Expected Quantity.
		@param QtyExpected 
		Quantity expected to be received into a locator
	  */
	public void setQtyExpected (BigDecimal QtyExpected)
	{
		set_ValueNoCheck (COLUMNNAME_QtyExpected, QtyExpected);
	}

	/** Get Expected Quantity.
		@return Quantity expected to be received into a locator
	  */
	public BigDecimal getQtyExpected () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyExpected);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}