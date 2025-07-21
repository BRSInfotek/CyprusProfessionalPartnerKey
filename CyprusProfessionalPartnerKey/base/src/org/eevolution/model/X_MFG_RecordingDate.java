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
package org.eevolution.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.cyprusbrs.framework.I_AD_Workflow;
import org.cyprusbrs.framework.I_M_Operation;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MFG_RecordingDate
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_MFG_RecordingDate extends PO implements I_MFG_RecordingDate, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250415L;

    /** Standard Constructor */
    public X_MFG_RecordingDate (Properties ctx, int MFG_RecordingDate_ID, String trxName)
    {
      super (ctx, MFG_RecordingDate_ID, trxName);
      /** if (MFG_RecordingDate_ID == 0)
        {
			setMFG_RecordingDate_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_RecordingDate (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_RecordingDate[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_AD_Workflow getAD_Workflow() throws RuntimeException
    {
		return (I_AD_Workflow)MTable.get(getCtx(), I_AD_Workflow.Table_Name)
			.getPO(getAD_Workflow_ID(), get_TrxName());	}

	/** Set Workflow.
		@param AD_Workflow_ID 
		Workflow or combination of tasks
	  */
	public void setAD_Workflow_ID (int AD_Workflow_ID)
	{
		if (AD_Workflow_ID < 1) 
			set_Value (COLUMNNAME_AD_Workflow_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Workflow_ID, Integer.valueOf(AD_Workflow_ID));
	}

	/** Get Workflow.
		@return Workflow or combination of tasks
	  */
	public int getAD_Workflow_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Workflow_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Recording Date.
		@param DateRecording Recording Date	  */
	public void setDateRecording (Timestamp DateRecording)
	{
		set_Value (COLUMNNAME_DateRecording, DateRecording);
	}

	/** Get Recording Date.
		@return Recording Date	  */
	public Timestamp getDateRecording () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateRecording);
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

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Operation getM_Operation() throws RuntimeException
    {
		return (I_M_Operation)MTable.get(getCtx(), I_M_Operation.Table_Name)
			.getPO(getM_Operation_ID(), get_TrxName());	}

	/** Set Operation.
		@param M_Operation_ID Operation	  */
	public void setM_Operation_ID (int M_Operation_ID)
	{
		if (M_Operation_ID < 1) 
			set_Value (COLUMNNAME_M_Operation_ID, null);
		else 
			set_Value (COLUMNNAME_M_Operation_ID, Integer.valueOf(M_Operation_ID));
	}

	/** Get Operation.
		@return Operation	  */
	public int getM_Operation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Operation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Recording Date ID.
		@param MFG_RecordingDate_ID Recording Date ID	  */
	public void setMFG_RecordingDate_ID (int MFG_RecordingDate_ID)
	{
		if (MFG_RecordingDate_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_RecordingDate_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_RecordingDate_ID, Integer.valueOf(MFG_RecordingDate_ID));
	}

	/** Get Recording Date ID.
		@return Recording Date ID	  */
	public int getMFG_RecordingDate_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_RecordingDate_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** MFG_Shift AD_Reference_ID=1000055 */
	public static final int MFG_SHIFT_AD_Reference_ID=1000055;
	/** Shift 1 = S1 */
	public static final String MFG_SHIFT_Shift1 = "S1";
	/** Shift 2 = S2 */
	public static final String MFG_SHIFT_Shift2 = "S2";
	/** Set Shift.
		@param MFG_Shift Shift	  */
	public void setMFG_Shift (String MFG_Shift)
	{

		set_Value (COLUMNNAME_MFG_Shift, MFG_Shift);
	}

	/** Get Shift.
		@return Shift	  */
	public String getMFG_Shift () 
	{
		return (String)get_Value(COLUMNNAME_MFG_Shift);
	}

	public I_MFG_TimeRecording getMFG_TimeRecording() throws RuntimeException
    {
		return (I_MFG_TimeRecording)MTable.get(getCtx(), I_MFG_TimeRecording.Table_Name)
			.getPO(getMFG_TimeRecording_ID(), get_TrxName());	}

	/** Set Time Recording ID.
		@param MFG_TimeRecording_ID Time Recording ID	  */
	public void setMFG_TimeRecording_ID (int MFG_TimeRecording_ID)
	{
		if (MFG_TimeRecording_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_TimeRecording_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_TimeRecording_ID, Integer.valueOf(MFG_TimeRecording_ID));
	}

	/** Get Time Recording ID.
		@return Time Recording ID	  */
	public int getMFG_TimeRecording_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_TimeRecording_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.eevolution.model.I_PP_Order_Node getPP_Order_Node() throws RuntimeException
    {
		return (org.eevolution.model.I_PP_Order_Node)MTable.get(getCtx(), org.eevolution.model.I_PP_Order_Node.Table_Name)
			.getPO(getPP_Order_Node_ID(), get_TrxName());	}

	/** Set Manufacturing Order Activity.
		@param PP_Order_Node_ID 
		Workflow Node (activity), step or process
	  */
	public void setPP_Order_Node_ID (int PP_Order_Node_ID)
	{
		if (PP_Order_Node_ID < 1) 
			set_Value (COLUMNNAME_PP_Order_Node_ID, null);
		else 
			set_Value (COLUMNNAME_PP_Order_Node_ID, Integer.valueOf(PP_Order_Node_ID));
	}

	/** Get Manufacturing Order Activity.
		@return Workflow Node (activity), step or process
	  */
	public int getPP_Order_Node_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PP_Order_Node_ID);
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
		set_ValueNoCheck (COLUMNNAME_Processed, Boolean.valueOf(Processed));
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

	/** Set Process Now.
		@param ProcessNow Process Now	  */
	public void setProcessNow (String ProcessNow)
	{
		set_Value (COLUMNNAME_ProcessNow, ProcessNow);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getProcessNow () 
	{
		return (String)get_Value(COLUMNNAME_ProcessNow);
	}
}