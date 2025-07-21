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

import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for MFG_WorkOrderResource
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkOrderResource extends PO implements I_MFG_WorkOrderResource, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkOrderResource (Properties ctx, int MFG_WorkOrderResource_ID, String trxName)
    {
      super (ctx, MFG_WorkOrderResource_ID, trxName);
      /** if (MFG_WorkOrderResource_ID == 0)
        {
			setBasisType (null);
// I
			setC_UOM_ID (0);
			setChargeType (null);
// A
			setMFG_WorkOrderOperation_ID (0);
			setMFG_WorkOrderResource_ID (0);
			setM_Product_ID (0);
			setProcessed (false);
// N
			setQtyRequired (Env.ZERO);
			setQtySpent (Env.ZERO);
// 0
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM MFG_WorkOrderResource WHERE MFG_WorkOrderOperation_ID=@MFG_WorkOrderOperation_ID@
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkOrderResource (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkOrderResource[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** BasisType AD_Reference_ID=1000034 */
	public static final int BASISTYPE_AD_Reference_ID=1000034;
	/** Per Batch = B */
	public static final String BASISTYPE_PerBatch = "B";
	/** Per Item = I */
	public static final String BASISTYPE_PerItem = "I";
	/** Set Cost Basis Type.
		@param BasisType 
		Indicates the option to consume and charge materials and resources
	  */
	public void setBasisType (String BasisType)
	{

		set_Value (COLUMNNAME_BasisType, BasisType);
	}

	/** Get Cost Basis Type.
		@return Indicates the option to consume and charge materials and resources
	  */
	public String getBasisType () 
	{
		return (String)get_Value(COLUMNNAME_BasisType);
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

	/** ChargeType AD_Reference_ID=1000035 */
	public static final int CHARGETYPE_AD_Reference_ID=1000035;
	/** Automatic = A */
	public static final String CHARGETYPE_Automatic = "A";
	/** Manual = M */
	public static final String CHARGETYPE_Manual = "M";
	/** Set Cost Charge Type.
		@param ChargeType 
		Indicates how the production resource will be charged - automatically or manually
	  */
	public void setChargeType (String ChargeType)
	{

		set_Value (COLUMNNAME_ChargeType, ChargeType);
	}

	/** Get Cost Charge Type.
		@return Indicates how the production resource will be charged - automatically or manually
	  */
	public String getChargeType () 
	{
		return (String)get_Value(COLUMNNAME_ChargeType);
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

	public I_MFG_WorkOrderOperation getMFG_WorkOrderOperation() throws RuntimeException
    {
		return (I_MFG_WorkOrderOperation)MTable.get(getCtx(), I_MFG_WorkOrderOperation.Table_Name)
			.getPO(getMFG_WorkOrderOperation_ID(), get_TrxName());	}

	/** Set Operation.
		@param MFG_WorkOrderOperation_ID Operation	  */
	public void setMFG_WorkOrderOperation_ID (int MFG_WorkOrderOperation_ID)
	{
		if (MFG_WorkOrderOperation_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderOperation_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderOperation_ID, Integer.valueOf(MFG_WorkOrderOperation_ID));
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

	/** Set Work Order Resource ID.
		@param MFG_WorkOrderResource_ID Work Order Resource ID	  */
	public void setMFG_WorkOrderResource_ID (int MFG_WorkOrderResource_ID)
	{
		if (MFG_WorkOrderResource_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderResource_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkOrderResource_ID, Integer.valueOf(MFG_WorkOrderResource_ID));
	}

	/** Get Work Order Resource ID.
		@return Work Order Resource ID	  */
	public int getMFG_WorkOrderResource_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderResource_ID);
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

	/** Set Quantity Used.
		@param QtySpent 
		Quantity used for this event
	  */
	public void setQtySpent (BigDecimal QtySpent)
	{
		set_Value (COLUMNNAME_QtySpent, QtySpent);
	}

	/** Get Quantity Used.
		@return Quantity used for this event
	  */
	public BigDecimal getQtySpent () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtySpent);
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
}