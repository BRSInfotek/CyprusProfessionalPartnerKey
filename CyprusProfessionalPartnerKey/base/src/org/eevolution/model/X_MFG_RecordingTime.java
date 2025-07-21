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

import org.cyprusbrs.framework.I_C_Activity;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MFG_RecordingTime
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_MFG_RecordingTime extends PO implements I_MFG_RecordingTime, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250415L;

    /** Standard Constructor */
    public X_MFG_RecordingTime (Properties ctx, int MFG_RecordingTime_ID, String trxName)
    {
      super (ctx, MFG_RecordingTime_ID, trxName);
      /** if (MFG_RecordingTime_ID == 0)
        {
			setMFG_RecordingTime_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_RecordingTime (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_RecordingTime[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_Activity getC_Activity() throws RuntimeException
    {
		return (I_C_Activity)MTable.get(getCtx(), I_C_Activity.Table_Name)
			.getPO(getC_Activity_ID(), get_TrxName());	}

	/** Set Activity.
		@param C_Activity_ID 
		Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID)
	{
		if (C_Activity_ID < 1) 
			set_Value (COLUMNNAME_C_Activity_ID, null);
		else 
			set_Value (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
	}

	/** Get Activity.
		@return Business Activity
	  */
	public int getC_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public I_MFG_RecordingDate getMFG_RecordingDate() throws RuntimeException
    {
		return (I_MFG_RecordingDate)MTable.get(getCtx(), I_MFG_RecordingDate.Table_Name)
			.getPO(getMFG_RecordingDate_ID(), get_TrxName());	}

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

	/** Set Recording Time.
		@param MFG_RecordingTime Recording Time	  */
	public void setMFG_RecordingTime (Timestamp MFG_RecordingTime)
	{
		set_Value (COLUMNNAME_MFG_RecordingTime, MFG_RecordingTime);
	}

	/** Get Recording Time.
		@return Recording Time	  */
	public Timestamp getMFG_RecordingTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_MFG_RecordingTime);
	}

	/** Set Recording Time ID.
		@param MFG_RecordingTime_ID Recording Time ID	  */
	public void setMFG_RecordingTime_ID (int MFG_RecordingTime_ID)
	{
		if (MFG_RecordingTime_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_RecordingTime_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_RecordingTime_ID, Integer.valueOf(MFG_RecordingTime_ID));
	}

	/** Get Recording Time ID.
		@return Recording Time ID	  */
	public int getMFG_RecordingTime_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_RecordingTime_ID);
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
}