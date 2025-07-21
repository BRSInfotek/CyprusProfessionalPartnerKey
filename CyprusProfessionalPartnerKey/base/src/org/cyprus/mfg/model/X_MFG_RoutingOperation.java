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
import java.util.Properties;

import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MFG_RoutingOperation
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_RoutingOperation extends PO implements I_MFG_RoutingOperation, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_RoutingOperation (Properties ctx, int MFG_RoutingOperation_ID, String trxName)
    {
      super (ctx, MFG_RoutingOperation_ID, trxName);
      /** if (MFG_RoutingOperation_ID == 0)
        {
			setC_UOM_ID (0);
			setIsHazardous (false);
// N
			setIsOptional (false);
// N
			setIsPermitRequired (false);
// N
			setMFG_Operation_ID (0);
			setMFG_RoutingOperation_ID (0);
			setMFG_Routing_ID (0);
			setMFG_WorkCenter_ID (0);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM MFG_RoutingOperation WHERE MFG_Routing_ID=@MFG_Routing_ID@
			setSetupTime (Env.ZERO);
// 0
			setUnitRuntime (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_MFG_RoutingOperation (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_RoutingOperation[")
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
		@param MFG_Operation_ID 
		Manufacturing Operation
	  */
	public void setMFG_Operation_ID (int MFG_Operation_ID)
	{
		if (MFG_Operation_ID < 1) 
			set_Value (COLUMNNAME_MFG_Operation_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_Operation_ID, Integer.valueOf(MFG_Operation_ID));
	}

	/** Get Operation.
		@return Manufacturing Operation
	  */
	public int getMFG_Operation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_Operation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Routing Operation.
		@param MFG_RoutingOperation_ID Routing Operation	  */
	public void setMFG_RoutingOperation_ID (int MFG_RoutingOperation_ID)
	{
		if (MFG_RoutingOperation_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_RoutingOperation_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_RoutingOperation_ID, Integer.valueOf(MFG_RoutingOperation_ID));
	}

	/** Get Routing Operation.
		@return Routing Operation	  */
	public int getMFG_RoutingOperation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_RoutingOperation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_Routing getMFG_Routing() throws RuntimeException
    {
		return (I_MFG_Routing)MTable.get(getCtx(), I_MFG_Routing.Table_Name)
			.getPO(getMFG_Routing_ID(), get_TrxName());	}

	/** Set Routing.
		@param MFG_Routing_ID 
		Routing for an assembly
	  */
	public void setMFG_Routing_ID (int MFG_Routing_ID)
	{
		if (MFG_Routing_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_Routing_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_Routing_ID, Integer.valueOf(MFG_Routing_ID));
	}

	/** Get Routing.
		@return Routing for an assembly
	  */
	public int getMFG_Routing_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_Routing_ID);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getSeqNo()));
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