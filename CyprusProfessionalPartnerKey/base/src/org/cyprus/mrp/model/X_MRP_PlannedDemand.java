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

import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MRP_PlannedDemand
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_PlannedDemand extends PO implements I_MRP_PlannedDemand, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_PlannedDemand (Properties ctx, int MRP_PlannedDemand_ID, String trxName)
    {
      super (ctx, MRP_PlannedDemand_ID, trxName);
      /** if (MRP_PlannedDemand_ID == 0)
        {
			setC_Period_ID (0);
			setC_UOM_ID (0);
			setDateRequired (new Timestamp( System.currentTimeMillis() ));
			setLeadTime (Env.ZERO);
// 0
			setLevelNo (0);
// 0
			setMRP_PlanRun_ID (0);
			setMRP_PlannedDemand_ID (0);
			setM_Product_ID (0);
			setQtyRequired (Env.ZERO);
// 0
			setRunStatus (null);
// N
        } */
    }

    /** Load Constructor */
    public X_MRP_PlannedDemand (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_PlannedDemand[")
        .append(get_ID()).append("]");
      return sb.toString();
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getC_Period_ID()));
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

	/** Set Date Required.
		@param DateRequired 
		Date when required
	  */
	public void setDateRequired (Timestamp DateRequired)
	{
		set_ValueNoCheck (COLUMNNAME_DateRequired, DateRequired);
	}

	/** Get Date Required.
		@return Date when required
	  */
	public Timestamp getDateRequired () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateRequired);
	}

	/** Set Lead Time.
		@param LeadTime 
		Lead Time
	  */
	public void setLeadTime (BigDecimal LeadTime)
	{
		set_Value (COLUMNNAME_LeadTime, LeadTime);
	}

	/** Get Lead Time.
		@return Lead Time
	  */
	public BigDecimal getLeadTime () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LeadTime);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	public I_MRP_PlannedDemand getMRP_PlannedDemand_Parent() throws RuntimeException
    {
		return (I_MRP_PlannedDemand)MTable.get(getCtx(), I_MRP_PlannedDemand.Table_Name)
			.getPO(getMRP_PlannedDemand_Parent_ID(), get_TrxName());	}

	/** Set Planned Demand Parent.
		@param MRP_PlannedDemand_Parent_ID 
		Planned Demand of the Immediate Parent Product
	  */
	public void setMRP_PlannedDemand_Parent_ID (int MRP_PlannedDemand_Parent_ID)
	{
		if (MRP_PlannedDemand_Parent_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedDemand_Parent_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedDemand_Parent_ID, Integer.valueOf(MRP_PlannedDemand_Parent_ID));
	}

	/** Get Planned Demand Parent.
		@return Planned Demand of the Immediate Parent Product
	  */
	public int getMRP_PlannedDemand_Parent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlannedDemand_Parent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MRP_PlannedDemand getMRP_PlannedDemand_Root() throws RuntimeException
    {
		return (I_MRP_PlannedDemand)MTable.get(getCtx(), I_MRP_PlannedDemand.Table_Name)
			.getPO(getMRP_PlannedDemand_Root_ID(), get_TrxName());	}

	/** Set Planned Demand Root.
		@param MRP_PlannedDemand_Root_ID 
		Planned Demand of the Root Product (Top Most in the Tree)
	  */
	public void setMRP_PlannedDemand_Root_ID (int MRP_PlannedDemand_Root_ID)
	{
		if (MRP_PlannedDemand_Root_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedDemand_Root_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MRP_PlannedDemand_Root_ID, Integer.valueOf(MRP_PlannedDemand_Root_ID));
	}

	/** Get Planned Demand Root.
		@return Planned Demand of the Root Product (Top Most in the Tree)
	  */
	public int getMRP_PlannedDemand_Root_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MRP_PlannedDemand_Root_ID);
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

	/** Set Required Quantity.
		@param QtyRequired 
		Quantity required for an activity
	  */
	public void setQtyRequired (BigDecimal QtyRequired)
	{
		set_Value (COLUMNNAME_QtyRequired, QtyRequired);
	}

	/** Get Required Quantity.
		@return Quantity required for an activity
	  */
	public BigDecimal getQtyRequired () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyRequired);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** RunStatus AD_Reference_ID=1000042 */
	public static final int RUNSTATUS_AD_Reference_ID=1000042;
	/** Error During Run = E */
	public static final String RUNSTATUS_ErrorDuringRun = "E";
	/** In Progress = I */
	public static final String RUNSTATUS_InProgress = "I";
	/** Not Running = N */
	public static final String RUNSTATUS_NotRunning = "N";
	/** Setup Error = X */
	public static final String RUNSTATUS_SetupError = "X";
	/** Completed Running = Y */
	public static final String RUNSTATUS_CompletedRunning = "Y";
	/** Set Run Status.
		@param RunStatus 
		Plan Run Status
	  */
	public void setRunStatus (String RunStatus)
	{

		set_Value (COLUMNNAME_RunStatus, RunStatus);
	}

	/** Get Run Status.
		@return Plan Run Status
	  */
	public String getRunStatus () 
	{
		return (String)get_Value(COLUMNNAME_RunStatus);
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (BigDecimal SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, SeqNo);
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public BigDecimal getSeqNo () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_SeqNo);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}