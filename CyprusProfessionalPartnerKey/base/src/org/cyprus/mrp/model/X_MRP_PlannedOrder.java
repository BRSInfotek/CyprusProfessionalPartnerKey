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
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_Order;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MRP_PlannedOrder
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_PlannedOrder extends PO implements I_MRP_PlannedOrder, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211222L;

    /** Standard Constructor */
    public X_MRP_PlannedOrder (Properties ctx, int MRP_PlannedOrder_ID, String trxName)
    {
      super (ctx, MRP_PlannedOrder_ID, trxName);
      /** if (MRP_PlannedOrder_ID == 0)
        {
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
			setLevelNo (0);
// 0
			setMRP_PlanRun_ID (0);
			setMRP_PlannedDemand_ID (0);
			setMRP_PlannedOrder_ID (0);
			setM_Product_ID (0);
			setPlannedOrderStatus (null);
// N
			setQtyOrdered (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_MRP_PlannedOrder (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_PlannedOrder[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_DocType getC_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Order getC_Order() throws RuntimeException
    {
		return (I_C_Order)MTable.get(getCtx(), I_C_Order.Table_Name)
			.getPO(getC_Order_ID(), get_TrxName());	}

	/** Set Order.
		@param C_Order_ID 
		Order
	  */
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1) 
			set_Value (COLUMNNAME_C_Order_ID, null);
		else 
			set_Value (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date Ordered.
		@param DateOrdered 
		Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_Value (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Level no.
		@param LevelNo Level no	  */
	public void setLevelNo (int LevelNo)
	{
		set_Value (COLUMNNAME_LevelNo, Integer.valueOf(LevelNo));
	}

	/** Get Level no.
		@return Level no	  */
	public int getLevelNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LevelNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
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
			set_Value (COLUMNNAME_MFG_WorkOrder_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrder_ID, Integer.valueOf(MFG_WorkOrder_ID));
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

	public I_MRP_PlannedDemand getMRP_PlannedDemand() throws RuntimeException
    {
		return (I_MRP_PlannedDemand)MTable.get(getCtx(), I_MRP_PlannedDemand.Table_Name)
			.getPO(getMRP_PlannedDemand_ID(), get_TrxName());	}

	/** Set Planned Demand ID.
		@param MRP_PlannedDemand_ID Planned Demand ID	  */
	public void setMRP_PlannedDemand_ID (int MRP_PlannedDemand_ID)
	{
		if (MRP_PlannedDemand_ID < 1) 
			set_Value (COLUMNNAME_MRP_PlannedDemand_ID, null);
		else 
			set_Value (COLUMNNAME_MRP_PlannedDemand_ID, Integer.valueOf(MRP_PlannedDemand_ID));
	}

	/** Get Planned Demand ID.
		@return Planned Demand ID	  */
	public int getMRP_PlannedDemand_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlannedDemand_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Planned Order.
		@param MRP_PlannedOrder_ID 
		Recommended orders calculated by the plan engine
	  */
	public void setMRP_PlannedOrder_ID (int MRP_PlannedOrder_ID)
	{
		if (MRP_PlannedOrder_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedOrder_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedOrder_ID, Integer.valueOf(MRP_PlannedOrder_ID));
	}

	/** Get Planned Order.
		@return Recommended orders calculated by the plan engine
	  */
	public int getMRP_PlannedOrder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlannedOrder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MRP_PlannedOrder getMRP_PlannedOrder_Parent() throws RuntimeException
    {
		return (I_MRP_PlannedOrder)MTable.get(getCtx(), I_MRP_PlannedOrder.Table_Name)
			.getPO(getMRP_PlannedOrder_Parent_ID(), get_TrxName());	}

	/** Set Planned Order Parent.
		@param MRP_PlannedOrder_Parent_ID 
		Parent Order of a Planned Order
	  */
	public void setMRP_PlannedOrder_Parent_ID (int MRP_PlannedOrder_Parent_ID)
	{
		if (MRP_PlannedOrder_Parent_ID < 1) 
			set_Value (COLUMNNAME_MRP_PlannedOrder_Parent_ID, null);
		else 
			set_Value (COLUMNNAME_MRP_PlannedOrder_Parent_ID, Integer.valueOf(MRP_PlannedOrder_Parent_ID));
	}

	/** Get Planned Order Parent.
		@return Parent Order of a Planned Order
	  */
	public int getMRP_PlannedOrder_Parent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlannedOrder_Parent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MRP_PlannedOrder getMRP_PlannedOrder_Root() throws RuntimeException
    {
		return (I_MRP_PlannedOrder)MTable.get(getCtx(), I_MRP_PlannedOrder.Table_Name)
			.getPO(getMRP_PlannedOrder_Root_ID(), get_TrxName());	}

	/** Set Planned Order Root.
		@param MRP_PlannedOrder_Root_ID 
		Root Order of a Planned Order
	  */
	public void setMRP_PlannedOrder_Root_ID (int MRP_PlannedOrder_Root_ID)
	{
		if (MRP_PlannedOrder_Root_ID < 1) 
			set_Value (COLUMNNAME_MRP_PlannedOrder_Root_ID, null);
		else 
			set_Value (COLUMNNAME_MRP_PlannedOrder_Root_ID, Integer.valueOf(MRP_PlannedOrder_Root_ID));
	}

	/** Get Planned Order Root.
		@return Root Order of a Planned Order
	  */
	public int getMRP_PlannedOrder_Root_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlannedOrder_Root_ID);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getM_Product_ID()));
    }

	/** OrderType AD_Reference_ID=1000041 */
	public static final int ORDERTYPE_AD_Reference_ID=1000041;
	/** Purchase Order = P */
	public static final String ORDERTYPE_PurchaseOrder = "P";
	/** Work Order = W */
	public static final String ORDERTYPE_WorkOrder = "W";
	/** Set Order Type.
		@param OrderType 
		Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public void setOrderType (String OrderType)
	{

		set_Value (COLUMNNAME_OrderType, OrderType);
	}

	/** Get Order Type.
		@return Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public String getOrderType () 
	{
		return (String)get_Value(COLUMNNAME_OrderType);
	}

	/** PlannedOrderStatus AD_Reference_ID=1000062 */
	public static final int PLANNEDORDERSTATUS_AD_Reference_ID=1000062;
	/** Error  = E */
	public static final String PLANNEDORDERSTATUS_Error = "E";
	/** Implemented = I */
	public static final String PLANNEDORDERSTATUS_Implemented = "I";
	/** Not Implemented = N */
	public static final String PLANNEDORDERSTATUS_NotImplemented = "N";
	/** Set Planned Order Status.
		@param PlannedOrderStatus 
		Indicates if an order has been submitted or not
	  */
	public void setPlannedOrderStatus (String PlannedOrderStatus)
	{

		set_Value (COLUMNNAME_PlannedOrderStatus, PlannedOrderStatus);
	}

	/** Get Planned Order Status.
		@return Indicates if an order has been submitted or not
	  */
	public String getPlannedOrderStatus () 
	{
		return (String)get_Value(COLUMNNAME_PlannedOrderStatus);
	}

	/** Set Ordered Quantity.
		@param QtyOrdered 
		Ordered Quantity
	  */
	public void setQtyOrdered (BigDecimal QtyOrdered)
	{
		set_Value (COLUMNNAME_QtyOrdered, QtyOrdered);
	}

	/** Get Ordered Quantity.
		@return Ordered Quantity
	  */
	public BigDecimal getQtyOrdered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyOrdered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}