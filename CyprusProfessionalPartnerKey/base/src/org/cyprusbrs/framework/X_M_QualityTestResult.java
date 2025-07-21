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

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for M_QualityTestResult
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_M_QualityTestResult extends PO implements I_M_QualityTestResult, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240731L;

    /** Standard Constructor */
    public X_M_QualityTestResult (Properties ctx, int M_QualityTestResult_ID, String trxName)
    {
      super (ctx, M_QualityTestResult_ID, trxName);
      /** if (M_QualityTestResult_ID == 0)
        {
			setIsQCPass (false);
// N
			setM_QualityTest_ID (0);
			setM_QualityTestResult_ID (0);
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_M_QualityTestResult (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_QualityTestResult[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	@Override
	public void setDescription (String Description)
	{
		throw new IllegalArgumentException ("Description is virtual column");	}

	/** Get Description.
		@return Optional short description of the record
	  */
	@Override
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Expected Result.
		@param ExpectedResult Expected Result	  */
	@Override
	public void setExpectedResult (String ExpectedResult)
	{
		throw new IllegalArgumentException ("ExpectedResult is virtual column");	}

	/** Get Expected Result.
		@return Expected Result	  */
	@Override
	public String getExpectedResult () 
	{
		return (String)get_Value(COLUMNNAME_ExpectedResult);
	}

	/** Set QC Pass.
		@param IsQCPass QC Pass	  */
	@Override
	public void setIsQCPass (boolean IsQCPass)
	{
		set_Value (COLUMNNAME_IsQCPass, Boolean.valueOf(IsQCPass));
	}

	/** Get QC Pass.
		@return QC Pass	  */
	@Override
	public boolean isQCPass () 
	{
		Object oo = get_Value(COLUMNNAME_IsQCPass);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	@Override
	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}

	/** Set Attribute Set Instance.
		@param M_AttributeSetInstance_ID 
		Product Attribute Set Instance
	  */
	@Override
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0) 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Set Instance.
		@return Product Attribute Set Instance
	  */
	@Override
	public int getM_AttributeSetInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Override
	public I_M_QualityTest getM_QualityTest() throws RuntimeException
    {
		return (I_M_QualityTest)MTable.get(getCtx(), I_M_QualityTest.Table_Name)
			.getPO(getM_QualityTest_ID(), get_TrxName());	}

	/** Set Quality Test.
		@param M_QualityTest_ID Quality Test	  */
	@Override
	public void setM_QualityTest_ID (int M_QualityTest_ID)
	{
		if (M_QualityTest_ID < 1) 
			set_Value (COLUMNNAME_M_QualityTest_ID, null);
		else 
			set_Value (COLUMNNAME_M_QualityTest_ID, Integer.valueOf(M_QualityTest_ID));
	}

	/** Get Quality Test.
		@return Quality Test	  */
	@Override
	public int getM_QualityTest_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_QualityTest_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set QC Test ID.
		@param M_QualityTestResult_ID QC Test ID	  */
	@Override
	public void setM_QualityTestResult_ID (int M_QualityTestResult_ID)
	{
		if (M_QualityTestResult_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_QualityTestResult_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_QualityTestResult_ID, Integer.valueOf(M_QualityTestResult_ID));
	}

	/** Get QC Test ID.
		@return QC Test ID	  */
	@Override
	public int getM_QualityTestResult_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_QualityTestResult_ID);
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

	/** Set Result.
		@param Result 
		Result of the action taken
	  */
	@Override
	public void setResult (String Result)
	{
		set_Value (COLUMNNAME_Result, Result);
	}

	/** Get Result.
		@return Result of the action taken
	  */
	@Override
	public String getResult () 
	{
		return (String)get_Value(COLUMNNAME_Result);
	}
}