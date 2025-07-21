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

import org.cyprusbrs.framework.I_M_AttributeSetInstance;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MFG_WorkOrderTrxLineMA
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderTrxLineMA extends PO implements I_MFG_WorkOrderTrxLineMA, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkOrderTrxLineMA (Properties ctx, int MFG_WorkOrderTrxLineMA_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderTrxLineMA_ID, trxName);
      /** if (MFG_WorkOrderTrxLineMA_ID == 0)
        {
			setMFG_WorkOrderTrxLineMA_ID (0);
			setMFG_WorkOrderTrxLine_ID (0);
			setM_AttributeSetInstance_ID (0);
			setMovementQty (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderTrxLineMA (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderTrxLineMA[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set MFG_WorkOrderTransactionLineMA_ID ID.
		@param MFG_WorkOrderTrxLineMA_ID MFG_WorkOrderTransactionLineMA_ID ID	  */
	public void setMFG_WorkOrderTrxLineMA_ID (int MFG_WorkOrderTrxLineMA_ID)
	{
		if (MFG_WorkOrderTrxLineMA_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderTrxLineMA_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderTrxLineMA_ID, Integer.valueOf(MFG_WorkOrderTrxLineMA_ID));
	}

	/** Get MFG_WorkOrderTransactionLineMA_ID ID.
		@return MFG_WorkOrderTransactionLineMA_ID ID	  */
	public int getMFG_WorkOrderTrxLineMA_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderTrxLineMA_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderTrxLine getMFG_WorkOrderTrxLine() throws RuntimeException
    {
		return (I_MFG_WorkOrderTrxLine)MTable.get(getCtx(), I_MFG_WorkOrderTrxLine.Table_Name)
			.getPO(getMFG_WorkOrderTrxLine_ID(), get_TrxName());	}

	/** Set Work Order Transaction Line ID.
		@param MFG_WorkOrderTrxLine_ID Work Order Transaction Line ID	  */
	public void setMFG_WorkOrderTrxLine_ID (int MFG_WorkOrderTrxLine_ID)
	{
		if (MFG_WorkOrderTrxLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderTrxLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderTrxLine_ID, Integer.valueOf(MFG_WorkOrderTrxLine_ID));
	}

	/** Get Work Order Transaction Line ID.
		@return Work Order Transaction Line ID	  */
	public int getMFG_WorkOrderTrxLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderTrxLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}

	/** Set Attribute Set Instance.
		@param M_AttributeSetInstance_ID 
		Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0) 
			set_ValueNoCheck (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Set Instance.
		@return Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}