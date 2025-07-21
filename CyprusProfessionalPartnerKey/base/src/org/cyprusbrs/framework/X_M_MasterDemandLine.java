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
package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for M_MasterDemandLine
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_M_MasterDemandLine extends PO implements I_M_MasterDemandLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241008L;

    /** Standard Constructor */
    public X_M_MasterDemandLine (Properties ctx, int M_MasterDemandLine_ID, String trxName)
    {
      super (ctx, M_MasterDemandLine_ID, trxName);
      /** if (M_MasterDemandLine_ID == 0)
        {
			setC_Period_ID (0);
			setIsLineFrozen (false);
// N
			setM_MasterDemand_ID (0);
			setM_MasterDemandLine_ID (0);
			setM_Product_ID (0);
        } */
    }

    /** Load Constructor */
    public X_M_MasterDemandLine (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    @Override
	protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    @Override
	protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    @Override
	public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_M_MasterDemandLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	@Override
	public I_C_Period getC_Period() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_ID(), get_TrxName());	}

	/** Set Period.
		@param C_Period_ID 
		Period of the Calendar
	  */
	@Override
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1) 
			set_Value (COLUMNNAME_C_Period_ID, null);
		else 
			set_Value (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	@Override
	public int getC_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LineFrozen.
		@param IsLineFrozen LineFrozen	  */
	@Override
	public void setIsLineFrozen (boolean IsLineFrozen)
	{
		set_Value (COLUMNNAME_IsLineFrozen, Boolean.valueOf(IsLineFrozen));
	}

	/** Get LineFrozen.
		@return LineFrozen	  */
	@Override
	public boolean isLineFrozen () 
	{
		Object oo = get_Value(COLUMNNAME_IsLineFrozen);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	@Override
	public I_M_MasterDemand getM_MasterDemand() throws RuntimeException
    {
		return (I_M_MasterDemand)MTable.get(getCtx(), I_M_MasterDemand.Table_Name)
			.getPO(getM_MasterDemand_ID(), get_TrxName());	}

	/** Set Master Demand ID.
		@param M_MasterDemand_ID Master Demand ID	  */
	@Override
	public void setM_MasterDemand_ID (int M_MasterDemand_ID)
	{
		if (M_MasterDemand_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_MasterDemand_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_MasterDemand_ID, Integer.valueOf(M_MasterDemand_ID));
	}

	/** Get Master Demand ID.
		@return Master Demand ID	  */
	@Override
	public int getM_MasterDemand_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_MasterDemand_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Master Demand Line ID.
		@param M_MasterDemandLine_ID Master Demand Line ID	  */
	@Override
	public void setM_MasterDemandLine_ID (int M_MasterDemandLine_ID)
	{
		if (M_MasterDemandLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_MasterDemandLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_MasterDemandLine_ID, Integer.valueOf(M_MasterDemandLine_ID));
	}

	/** Get Master Demand Line ID.
		@return Master Demand Line ID	  */
	@Override
	public int getM_MasterDemandLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_MasterDemandLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_M_Product getM_Product() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	@Override
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
	@Override
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	@Override
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	@Override
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

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	@Override
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	@Override
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Sales Representative.
		@param SalesRep_ID 
		Sales Representative or Company Agent
	  */
	@Override
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1) 
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else 
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Representative.
		@return Sales Representative or Company Agent
	  */
	@Override
	public int getSalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}