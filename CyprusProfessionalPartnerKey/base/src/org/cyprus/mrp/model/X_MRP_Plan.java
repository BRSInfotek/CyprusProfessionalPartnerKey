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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.cyprus.mfg.model.I_MFG_WorkOrderClass;
import org.cyprusbrs.framework.I_C_Calendar;
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_PriceList;
import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MRP_Plan
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_Plan extends PO implements I_MRP_Plan, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_Plan (Properties ctx, int MRP_Plan_ID, String trxName)
    {
      super (ctx, MRP_Plan_ID, trxName);
      /** if (MRP_Plan_ID == 0)
        {
			setC_Calendar_ID (0);
			setC_DocTypeTarget_ID (0);
			setC_Period_From_ID (0);
			setC_Period_To_ID (0);
			setIsConsolidatePO (false);
// N
			setMRP_Plan_ID (0);
			setM_PriceList_ID (0);
// @SQL= SELECT M_PriceList_ID FROM M_PriceList WHERE IsSOPriceList = 'N' AND AD_Client_ID = @AD_Client_ID@ AND AD_Org_ID IN (0,@AD_Org_ID@)
			setM_Warehouse_ID (0);
			setName (null);
			setPrioritizeOrderOverDemand (false);
// N
			setPriorityImplementation (null);
// P
        } */
    }

    /** Load Constructor */
    public X_MRP_Plan (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_Plan[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_Calendar getC_Calendar() throws RuntimeException
    {
		return (I_C_Calendar)MTable.get(getCtx(), I_C_Calendar.Table_Name)
			.getPO(getC_Calendar_ID(), get_TrxName());	}

	/** Set Calendar.
		@param C_Calendar_ID 
		Accounting Calendar Name
	  */
	public void setC_Calendar_ID (int C_Calendar_ID)
	{
		if (C_Calendar_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Calendar_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Calendar_ID, Integer.valueOf(C_Calendar_ID));
	}

	/** Get Calendar.
		@return Accounting Calendar Name
	  */
	public int getC_Calendar_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Calendar_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_DocType getC_DocTypeTarget() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocTypeTarget_ID(), get_TrxName());	}

	/** Set Target Document Type.
		@param C_DocTypeTarget_ID 
		Target document type for conversing documents
	  */
	public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID)
	{
		if (C_DocTypeTarget_ID < 1) 
			set_Value (COLUMNNAME_C_DocTypeTarget_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocTypeTarget_ID, Integer.valueOf(C_DocTypeTarget_ID));
	}

	/** Get Target Document Type.
		@return Target document type for conversing documents
	  */
	public int getC_DocTypeTarget_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocTypeTarget_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Period getC_Period_From() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_From_ID(), get_TrxName());	}

	/** Set Period From.
		@param C_Period_From_ID 
		Starting period of a range of periods
	  */
	public void setC_Period_From_ID (int C_Period_From_ID)
	{
		if (C_Period_From_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Period_From_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Period_From_ID, Integer.valueOf(C_Period_From_ID));
	}

	/** Get Period From.
		@return Starting period of a range of periods
	  */
	public int getC_Period_From_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_From_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Period getC_Period_To() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_To_ID(), get_TrxName());	}

	/** Set Period To.
		@param C_Period_To_ID 
		Ending period of a range of periods
	  */
	public void setC_Period_To_ID (int C_Period_To_ID)
	{
		if (C_Period_To_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Period_To_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Period_To_ID, Integer.valueOf(C_Period_To_ID));
	}

	/** Get Period To.
		@return Ending period of a range of periods
	  */
	public int getC_Period_To_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_To_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date last run.
		@param DateLastRun 
		Date the process was last run.
	  */
	public void setDateLastRun (Timestamp DateLastRun)
	{
		set_Value (COLUMNNAME_DateLastRun, DateLastRun);
	}

	/** Get Date last run.
		@return Date the process was last run.
	  */
	public Timestamp getDateLastRun () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateLastRun);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Consolidate PO.
		@param IsConsolidatePO Consolidate PO	  */
	public void setIsConsolidatePO (boolean IsConsolidatePO)
	{
		set_Value (COLUMNNAME_IsConsolidatePO, Boolean.valueOf(IsConsolidatePO));
	}

	/** Get Consolidate PO.
		@return Consolidate PO	  */
	public boolean isConsolidatePO () 
	{
		Object oo = get_Value(COLUMNNAME_IsConsolidatePO);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_MFG_WorkOrderClass getMFG_WorkOrderClass() throws RuntimeException
    {
		return (I_MFG_WorkOrderClass)MTable.get(getCtx(), I_MFG_WorkOrderClass.Table_Name)
			.getPO(getMFG_WorkOrderClass_ID(), get_TrxName());	}

	/** Set Work Order Class ID.
		@param MFG_WorkOrderClass_ID Work Order Class ID	  */
	public void setMFG_WorkOrderClass_ID (int MFG_WorkOrderClass_ID)
	{
		if (MFG_WorkOrderClass_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderClass_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderClass_ID, Integer.valueOf(MFG_WorkOrderClass_ID));
	}

	/** Get Work Order Class ID.
		@return Work Order Class ID	  */
	public int getMFG_WorkOrderClass_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderClass_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Plan .
		@param MRP_Plan_ID Plan 	  */
	public void setMRP_Plan_ID (int MRP_Plan_ID)
	{
		if (MRP_Plan_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_Plan_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_Plan_ID, Integer.valueOf(MRP_Plan_ID));
	}

	/** Get Plan .
		@return Plan 	  */
	public int getMRP_Plan_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_Plan_ID);
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

	public I_M_PriceList getM_PriceList() throws RuntimeException
    {
		return (I_M_PriceList)MTable.get(getCtx(), I_M_PriceList.Table_Name)
			.getPO(getM_PriceList_ID(), get_TrxName());	}

	/** Set Price List.
		@param M_PriceList_ID 
		Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1) 
			set_Value (COLUMNNAME_M_PriceList_ID, null);
		else 
			set_Value (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (I_M_Warehouse)MTable.get(getCtx(), I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Prioritize Orders Over Demand.
		@param PrioritizeOrderOverDemand 
		Firm orders are considered over demand if orders exceed demand
	  */
	public void setPrioritizeOrderOverDemand (boolean PrioritizeOrderOverDemand)
	{
		set_Value (COLUMNNAME_PrioritizeOrderOverDemand, Boolean.valueOf(PrioritizeOrderOverDemand));
	}

	/** Get Prioritize Orders Over Demand.
		@return Firm orders are considered over demand if orders exceed demand
	  */
	public boolean isPrioritizeOrderOverDemand () 
	{
		Object oo = get_Value(COLUMNNAME_PrioritizeOrderOverDemand);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** PriorityImplementation AD_Reference_ID=1000032 */
	public static final int PRIORITYIMPLEMENTATION_AD_Reference_ID=1000032;
	/** Manufacture = M */
	public static final String PRIORITYIMPLEMENTATION_Manufacture = "M";
	/** Purchase = P */
	public static final String PRIORITYIMPLEMENTATION_Purchase = "P";
	/** Set Implementation Priority.
		@param PriorityImplementation 
		Indicates preference to either procure or manufacture a product
	  */
	public void setPriorityImplementation (String PriorityImplementation)
	{

		set_Value (COLUMNNAME_PriorityImplementation, PriorityImplementation);
	}

	/** Get Implementation Priority.
		@return Indicates preference to either procure or manufacture a product
	  */
	public String getPriorityImplementation () 
	{
		return (String)get_Value(COLUMNNAME_PriorityImplementation);
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
}