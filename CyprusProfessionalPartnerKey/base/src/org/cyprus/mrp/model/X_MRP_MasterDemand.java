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

import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for MRP_MasterDemand
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MRP_MasterDemand extends PO implements I_MRP_MasterDemand, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MRP_MasterDemand (Properties ctx, int MRP_MasterDemand_ID, String trxName)
    {
      super (ctx, MRP_MasterDemand_ID, trxName);
      /** if (MRP_MasterDemand_ID == 0)
        {
			setIsFrozen (false);
// N
			setIsPreviouslyFrozen (false);
// N
			setMRP_MasterDemand_ID (0);
			setMRP_Plan_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_MRP_MasterDemand (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MRP_MasterDemand[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Create lines from.
		@param CreateFrom 
		Process which will generate a new document lines based on an existing document
	  */
	public void setCreateFrom (String CreateFrom)
	{
		set_Value (COLUMNNAME_CreateFrom, CreateFrom);
	}

	/** Get Create lines from.
		@return Process which will generate a new document lines based on an existing document
	  */
	public String getCreateFrom () 
	{
		return (String)get_Value(COLUMNNAME_CreateFrom);
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

	/** Set Freeze.
		@param FreezeAction 
		Freeze the data
	  */
	public void setFreezeAction (String FreezeAction)
	{
		set_Value (COLUMNNAME_FreezeAction, FreezeAction);
	}

	/** Get Freeze.
		@return Freeze the data
	  */
	public String getFreezeAction () 
	{
		return (String)get_Value(COLUMNNAME_FreezeAction);
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

	/** Set Frozen.
		@param IsFrozen 
		Indicates if the data record is frozen
	  */
	public void setIsFrozen (boolean IsFrozen)
	{
		set_Value (COLUMNNAME_IsFrozen, Boolean.valueOf(IsFrozen));
	}

	/** Get Frozen.
		@return Indicates if the data record is frozen
	  */
	public boolean isFrozen () 
	{
		Object oo = get_Value(COLUMNNAME_IsFrozen);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Previously Frozen.
		@param IsPreviouslyFrozen 
		Indicates that the data record was frozen and then unfrozen
	  */
	public void setIsPreviouslyFrozen (boolean IsPreviouslyFrozen)
	{
		set_Value (COLUMNNAME_IsPreviouslyFrozen, Boolean.valueOf(IsPreviouslyFrozen));
	}

	/** Get Previously Frozen.
		@return Indicates that the data record was frozen and then unfrozen
	  */
	public boolean isPreviouslyFrozen () 
	{
		Object oo = get_Value(COLUMNNAME_IsPreviouslyFrozen);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getMRP_Plan_ID()));
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

	/** Set Unfreeze.
		@param Unfreeze 
		Unfreeze the data record to allow changes
	  */
	public void setUnfreeze (String Unfreeze)
	{
		set_Value (COLUMNNAME_Unfreeze, Unfreeze);
	}

	/** Get Unfreeze.
		@return Unfreeze the data record to allow changes
	  */
	public String getUnfreeze () 
	{
		return (String)get_Value(COLUMNNAME_Unfreeze);
	}
	
	// updated by anshul
	
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