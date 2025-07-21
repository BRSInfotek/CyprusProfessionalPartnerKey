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
package org.cyprus.mfg.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.cyprusbrs.framework.I_AD_User;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MFG_WorkOrderOperation
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderOperation extends PO implements I_MFG_WorkOrderOperation, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkOrderOperation (Properties ctx, int MFG_WorkOrderOperation_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderOperation_ID, trxName);
      /** if (MFG_WorkOrderOperation_ID == 0)
        {
			setC_UOM_ID (0);
			setIsHazardous (false);
// N
			setIsOptional (false);
// N
			setIsPermitRequired (false);
// N
			setMFG_WorkCenter_ID (0);
			setMFG_WorkOrderOperation_ID (0);
			setMFG_WorkOrder_ID (0);
			setProcessed (false);
// N
			setQtyAssembled (Env.ZERO);
// 0
			setQtyQueued (Env.ZERO);
// 0
			setQtyRun (Env.ZERO);
// 0
			setQtyScrapped (Env.ZERO);
// 0
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID=@MFG_WorkOrder_ID@
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderOperation (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderOperation[")
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
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
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

	/** Set Actual Date From.
		@param DateActualFrom 
		Actual date an activity started
	  */
	public void setDateActualFrom (Timestamp DateActualFrom)
	{
		set_Value (COLUMNNAME_DateActualFrom, DateActualFrom);
	}

	/** Get Actual Date From.
		@return Actual date an activity started
	  */
	public Timestamp getDateActualFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateActualFrom);
	}

	/** Set Actual Date To.
		@param DateActualTo 
		Actual date an activity ended
	  */
	public void setDateActualTo (Timestamp DateActualTo)
	{
		set_Value (COLUMNNAME_DateActualTo, DateActualTo);
	}

	/** Get Actual Date To.
		@return Actual date an activity ended
	  */
	public Timestamp getDateActualTo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateActualTo);
	}

	/** Set DateProcessed.
		@param DateProcessed DateProcessed	  */
	public void setDateProcessed (Timestamp DateProcessed)
	{
		set_Value (COLUMNNAME_DateProcessed, DateProcessed);
	}

	/** Get DateProcessed.
		@return DateProcessed	  */
	public Timestamp getDateProcessed () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateProcessed);
	}

	/** Set Scheduled Date From.
		@param DateScheduleFrom 
		Date an activity is scheduled to start
	  */
	public void setDateScheduleFrom (Timestamp DateScheduleFrom)
	{
		set_Value (COLUMNNAME_DateScheduleFrom, DateScheduleFrom);
	}

	/** Get Scheduled Date From.
		@return Date an activity is scheduled to start
	  */
	public Timestamp getDateScheduleFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateScheduleFrom);
	}

	/** Set Scheduled Date To.
		@param DateScheduleTo 
		Date an activity is scheduled to end
	  */
	public void setDateScheduleTo (Timestamp DateScheduleTo)
	{
		set_Value (COLUMNNAME_DateScheduleTo, DateScheduleTo);
	}

	/** Get Scheduled Date To.
		@return Date an activity is scheduled to end
	  */
	public Timestamp getDateScheduleTo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateScheduleTo);
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

	/** Set Hazardous.
		@param IsHazardous 
		Involves hazardous materials
	  */
	public void setIsHazardous (boolean IsHazardous)
	{
		set_Value (COLUMNNAME_IsHazardous, Boolean.valueOf(IsHazardous));
	}

	/** Get Hazardous.
		@return Involves hazardous materials
	  */
	public boolean isHazardous () 
	{
		Object oo = get_Value(COLUMNNAME_IsHazardous);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Optional.
		@param IsOptional Optional	  */
	public void setIsOptional (boolean IsOptional)
	{
		set_Value (COLUMNNAME_IsOptional, Boolean.valueOf(IsOptional));
	}

	/** Get Optional.
		@return Optional	  */
	public boolean isOptional () 
	{
		Object oo = get_Value(COLUMNNAME_IsOptional);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Permit Required.
		@param IsPermitRequired 
		Indicates if a permit or similar authorization is required for use or execution of a product, resource or work order operation.
	  */
	public void setIsPermitRequired (boolean IsPermitRequired)
	{
		set_Value (COLUMNNAME_IsPermitRequired, Boolean.valueOf(IsPermitRequired));
	}

	/** Get Permit Required.
		@return Indicates if a permit or similar authorization is required for use or execution of a product, resource or work order operation.
	  */
	public boolean isPermitRequired () 
	{
		Object oo = get_Value(COLUMNNAME_IsPermitRequired);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_MFG_Operation getMFG_Operation() throws RuntimeException
    {
		return (I_MFG_Operation)MTable.get(getCtx(), I_MFG_Operation.Table_Name)
			.getPO(getMFG_Operation_ID(), get_TrxName());	}

	/** Set Operation.
		@param MFG_Operation_ID Operation	  */
	public void setMFG_Operation_ID (int MFG_Operation_ID)
	{
		if (MFG_Operation_ID < 1) 
			set_Value (COLUMNNAME_MFG_Operation_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_Operation_ID, Integer.valueOf(MFG_Operation_ID));
	}

	/** Get Operation.
		@return Operation	  */
	public int getMFG_Operation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_Operation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_StandardOperation getMFG_StandardOperation() throws RuntimeException
    {
		return (I_MFG_StandardOperation)MTable.get(getCtx(), I_MFG_StandardOperation.Table_Name)
			.getPO(getMFG_StandardOperation_ID(), get_TrxName());	}

	/** Set Standard Operation.
		@param MFG_StandardOperation_ID 
		Identifies a standard operation template
	  */
	public void setMFG_StandardOperation_ID (int MFG_StandardOperation_ID)
	{
		if (MFG_StandardOperation_ID < 1) 
			set_Value (COLUMNNAME_MFG_StandardOperation_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_StandardOperation_ID, Integer.valueOf(MFG_StandardOperation_ID));
	}

	/** Get Standard Operation.
		@return Identifies a standard operation template
	  */
	public int getMFG_StandardOperation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_StandardOperation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkCenter getMFG_WorkCenter() throws RuntimeException
    {
		return (I_MFG_WorkCenter)MTable.get(getCtx(), I_MFG_WorkCenter.Table_Name)
			.getPO(getMFG_WorkCenter_ID(), get_TrxName());	}

	/** Set Work Center.
		@param MFG_WorkCenter_ID 
		Identifies a production area within a warehouse consisting of people and equipment
	  */
	public void setMFG_WorkCenter_ID (int MFG_WorkCenter_ID)
	{
		if (MFG_WorkCenter_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkCenter_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkCenter_ID, Integer.valueOf(MFG_WorkCenter_ID));
	}

	/** Get Work Center.
		@return Identifies a production area within a warehouse consisting of people and equipment
	  */
	public int getMFG_WorkCenter_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkCenter_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Operation.
		@param MFG_WorkOrderOperation_ID Operation	  */
	public void setMFG_WorkOrderOperation_ID (int MFG_WorkOrderOperation_ID)
	{
		if (MFG_WorkOrderOperation_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderOperation_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderOperation_ID, Integer.valueOf(MFG_WorkOrderOperation_ID));
	}

	/** Get Operation.
		@return Operation	  */
	public int getMFG_WorkOrderOperation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderOperation_ID);
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

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Quantity Assembled.
		@param QtyAssembled 
		Quantity finished at a production routing step
	  */
	public void setQtyAssembled (BigDecimal QtyAssembled)
	{
		set_Value (COLUMNNAME_QtyAssembled, QtyAssembled);
	}

	/** Get Quantity Assembled.
		@return Quantity finished at a production routing step
	  */
	public BigDecimal getQtyAssembled () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyAssembled);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Queued.
		@param QtyQueued 
		Number of sub-assemblies in the Queue step of a work order operation
	  */
	public void setQtyQueued (BigDecimal QtyQueued)
	{
		set_Value (COLUMNNAME_QtyQueued, QtyQueued);
	}

	/** Get Quantity Queued.
		@return Number of sub-assemblies in the Queue step of a work order operation
	  */
	public BigDecimal getQtyQueued () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyQueued);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Run.
		@param QtyRun 
		Number of sub-assemblies in the Run step of a work order operation
	  */
	public void setQtyRun (BigDecimal QtyRun)
	{
		set_Value (COLUMNNAME_QtyRun, QtyRun);
	}

	/** Get Quantity Run.
		@return Number of sub-assemblies in the Run step of a work order operation
	  */
	public BigDecimal getQtyRun () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyRun);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity Scrapped.
		@param QtyScrapped 
		This is the number of sub-assemblies in the Scrap step of an operation in Work Order.
	  */
	public void setQtyScrapped (BigDecimal QtyScrapped)
	{
		set_Value (COLUMNNAME_QtyScrapped, QtyScrapped);
	}

	/** Get Quantity Scrapped.
		@return This is the number of sub-assemblies in the Scrap step of an operation in Work Order.
	  */
	public BigDecimal getQtyScrapped () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyScrapped);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Setup Time.
		@param SetupTime 
		Setup time before starting Production
	  */
	public void setSetupTime (BigDecimal SetupTime)
	{
		set_Value (COLUMNNAME_SetupTime, SetupTime);
	}

	/** Get Setup Time.
		@return Setup time before starting Production
	  */
	public BigDecimal getSetupTime () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_SetupTime);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_AD_User getSupervisor() throws RuntimeException
    {
		return (I_AD_User)MTable.get(getCtx(), I_AD_User.Table_Name)
			.getPO(getSupervisor_ID(), get_TrxName());	}

	/** Set Supervisor.
		@param Supervisor_ID 
		Supervisor for this user/organization - used for escalation and approval
	  */
	public void setSupervisor_ID (int Supervisor_ID)
	{
		if (Supervisor_ID < 1) 
			set_Value (COLUMNNAME_Supervisor_ID, null);
		else 
			set_Value (COLUMNNAME_Supervisor_ID, Integer.valueOf(Supervisor_ID));
	}

	/** Get Supervisor.
		@return Supervisor for this user/organization - used for escalation and approval
	  */
	public int getSupervisor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Supervisor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Runtime per Unit.
		@param UnitRuntime 
		Time to produce one unit
	  */
	public void setUnitRuntime (BigDecimal UnitRuntime)
	{
		set_Value (COLUMNNAME_UnitRuntime, UnitRuntime);
	}

	/** Get Runtime per Unit.
		@return Time to produce one unit
	  */
	public BigDecimal getUnitRuntime () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_UnitRuntime);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}