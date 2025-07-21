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
import java.util.Properties;

import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MRP_PlanRun
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_PlanRun extends PO implements I_MRP_PlanRun, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_PlanRun (Properties ctx, int MRP_PlanRun_ID, String trxName)
    {
      super (ctx, MRP_PlanRun_ID, trxName);
      /** if (MRP_PlanRun_ID == 0)
        {
			setC_Period_From_ID (0);
			setMRP_MasterDemand_ID (0);
			setMRP_PlanRun_ID (0);
			setMRP_Plan_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MRP_PlanRun (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_PlanRun[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_Period getC_Period_BackOrder() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_BackOrder_ID(), get_TrxName());	}

	/** Set Back Order Period From.
		@param C_Period_BackOrder_ID 
		Period from which back orders should be considered for the plan run
	  */
	public void setC_Period_BackOrder_ID (int C_Period_BackOrder_ID)
	{
		if (C_Period_BackOrder_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Period_BackOrder_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Period_BackOrder_ID, Integer.valueOf(C_Period_BackOrder_ID));
	}

	/** Get Back Order Period From.
		@return Period from which back orders should be considered for the plan run
	  */
	public int getC_Period_BackOrder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_BackOrder_ID);
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

	/** Set Master Demand.
		@param MRP_MasterDemand_ID 
		Master Demand for material requirements
	  */
	public void setMRP_MasterDemand_ID (int MRP_MasterDemand_ID)
	{
		if (MRP_MasterDemand_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_MasterDemand_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_MasterDemand_ID, Integer.valueOf(MRP_MasterDemand_ID));
	}

	/** Get Master Demand.
		@return Master Demand for material requirements
	  */
	public int getMRP_MasterDemand_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_MasterDemand_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

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

	/** Set Plan .
		@param MRP_Plan_ID Plan 	  */
	public void setMRP_Plan_ID (int MRP_Plan_ID)
	{
		if (MRP_Plan_ID < 1) 
			set_Value (COLUMNNAME_MRP_Plan_ID, null);
		else 
			set_Value (COLUMNNAME_MRP_Plan_ID, Integer.valueOf(MRP_Plan_ID));
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
}