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
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_Order;
import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MRP_PlanResult_v
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_PlanResult_v extends PO implements I_MRP_PlanResult_v, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_PlanResult_v (Properties ctx, int MRP_PlanResult_v_ID, String trxName)
    {
      super (ctx, MRP_PlanResult_v_ID, trxName);
      /** if (MRP_PlanResult_v_ID == 0)
        {
			setC_Period_ID (0);
			setC_UOM_ID (0);
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
			setDemandDateRequired (new Timestamp( System.currentTimeMillis() ));
			setGrossRequirement (Env.ZERO);
			setIsImplemented (false);
// N
			setLevelNo (0);
			setMRP_PlanRun_ID (0);
			setMRP_Plan_ID (0);
			setMRP_PlannedDemand_ID (0);
			setMRP_PlannedOrder_ID (0);
			setM_Product_ID (0);
			setQtyOrdered (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_MRP_PlanResult_v (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_PlanResult_v[")
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
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
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

	/** Set Date Ordered.
		@param DateOrdered 
		Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_ValueNoCheck (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Demand Date Required.
		@param DemandDateRequired 
		Indicates by when a product is required to satisfy demand.
	  */
	public void setDemandDateRequired (Timestamp DemandDateRequired)
	{
		set_ValueNoCheck (COLUMNNAME_DemandDateRequired, DemandDateRequired);
	}

	/** Get Demand Date Required.
		@return Indicates by when a product is required to satisfy demand.
	  */
	public Timestamp getDemandDateRequired () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DemandDateRequired);
	}

	/** Set Expected Receipt.
		@param ExpectedReceipt 
		Product quantity expected to be received into inventory
	  */
	public void setExpectedReceipt (BigDecimal ExpectedReceipt)
	{
		set_ValueNoCheck (COLUMNNAME_ExpectedReceipt, ExpectedReceipt);
	}

	/** Get Expected Receipt.
		@return Product quantity expected to be received into inventory
	  */
	public BigDecimal getExpectedReceipt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ExpectedReceipt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Gross Requirement.
		@param GrossRequirement 
		Product quantity required after exploding the master demand
	  */
	public void setGrossRequirement (BigDecimal GrossRequirement)
	{
		set_Value (COLUMNNAME_GrossRequirement, GrossRequirement);
	}

	/** Get Gross Requirement.
		@return Product quantity required after exploding the master demand
	  */
	public BigDecimal getGrossRequirement () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_GrossRequirement);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Implemented.
		@param IsImplemented 
		Indicates if the order has been released
	  */
	public void setIsImplemented (boolean IsImplemented)
	{
		set_ValueNoCheck (COLUMNNAME_IsImplemented, Boolean.valueOf(IsImplemented));
	}

	/** Get Implemented.
		@return Indicates if the order has been released
	  */
	public boolean isImplemented () 
	{
		Object oo = get_Value(COLUMNNAME_IsImplemented);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Level no.
		@param LevelNo Level no	  */
	public void setLevelNo (int LevelNo)
	{
		set_ValueNoCheck (COLUMNNAME_LevelNo, Integer.valueOf(LevelNo));
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

	public I_MRP_Plan getMRP_Plan() throws RuntimeException
    {
		return (I_MRP_Plan)MTable.get(getCtx(), I_MRP_Plan.Table_Name)
			.getPO(getMRP_Plan_ID(), get_TrxName());	}

	/** Set Plan ID.
		@param MRP_Plan_ID Plan ID	  */
	public void setMRP_Plan_ID (int MRP_Plan_ID)
	{
		if (MRP_Plan_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_Plan_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_Plan_ID, Integer.valueOf(MRP_Plan_ID));
	}

	/** Get Plan ID.
		@return Plan ID	  */
	public int getMRP_Plan_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_Plan_ID);
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
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedDemand_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedDemand_ID, Integer.valueOf(MRP_PlannedDemand_ID));
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

	/** Set Net Requirement.
		@param NetRequirement 
		Product quantity calculated by the plan engine required to satisfy demand
	  */
	public void setNetRequirement (BigDecimal NetRequirement)
	{
		set_Value (COLUMNNAME_NetRequirement, NetRequirement);
	}

	/** Get Net Requirement.
		@return Product quantity calculated by the plan engine required to satisfy demand
	  */
	public BigDecimal getNetRequirement () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_NetRequirement);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	public I_M_Product getParentProduc() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getParentProductID(), get_TrxName());	}

	/** Set Parent Product.
		@param ParentProductID 
		Immediate parent product of the component
	  */
	public void setParentProductID (int ParentProductID)
	{
		set_Value (COLUMNNAME_ParentProductID, Integer.valueOf(ParentProductID));
	}

	/** Get Parent Product.
		@return Immediate parent product of the component
	  */
	public int getParentProductID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ParentProductID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Planned Availability.
		@param PlannedAvailability 
		Product quantity expected to be available as calculated by the plan engine
	  */
	public void setPlannedAvailability (BigDecimal PlannedAvailability)
	{
		set_Value (COLUMNNAME_PlannedAvailability, PlannedAvailability);
	}

	/** Get Planned Availability.
		@return Product quantity expected to be available as calculated by the plan engine
	  */
	public BigDecimal getPlannedAvailability () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PlannedAvailability);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Product Level.
		@param ProductLevel 
		Product Level Indented
	  */
	public void setProductLevel (String ProductLevel)
	{
		set_Value (COLUMNNAME_ProductLevel, ProductLevel);
	}

	/** Get Product Level.
		@return Product Level Indented
	  */
	public String getProductLevel () 
	{
		return (String)get_Value(COLUMNNAME_ProductLevel);
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

	public I_M_Product getRootProduc() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getRootProductID(), get_TrxName());	}

	/** Set Root Product.
		@param RootProductID 
		Product Assembly at the Root (Level 0)
	  */
	public void setRootProductID (int RootProductID)
	{
		set_Value (COLUMNNAME_RootProductID, Integer.valueOf(RootProductID));
	}

	/** Get Root Product.
		@return Product Assembly at the Root (Level 0)
	  */
	public int getRootProductID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RootProductID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}