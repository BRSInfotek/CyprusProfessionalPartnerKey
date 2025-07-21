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

/** Generated Model for M_Product_QualityTest
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_M_Product_QualityTest extends PO implements I_M_Product_QualityTest, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240731L;

    /** Standard Constructor */
    public X_M_Product_QualityTest (Properties ctx, int M_Product_QualityTest_ID, String trxName)
    {
      super (ctx, M_Product_QualityTest_ID, trxName);
      /** if (M_Product_QualityTest_ID == 0)
        {
			setExpectedResult (null);
			setM_Product_QualityTest_ID (0);
        } */
    }

    /** Load Constructor */
    public X_M_Product_QualityTest (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_M_Product_QualityTest[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Expected Result.
		@param ExpectedResult Expected Result	  */
	@Override
	public void setExpectedResult (String ExpectedResult)
	{
		set_Value (COLUMNNAME_ExpectedResult, ExpectedResult);
	}

	/** Get Expected Result.
		@return Expected Result	  */
	@Override
	public String getExpectedResult () 
	{
		return (String)get_Value(COLUMNNAME_ExpectedResult);
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

	/** Set Product Quality Test ID.
		@param M_Product_QualityTest_ID Product Quality Test ID	  */
	@Override
	public void setM_Product_QualityTest_ID (int M_Product_QualityTest_ID)
	{
		if (M_Product_QualityTest_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Product_QualityTest_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Product_QualityTest_ID, Integer.valueOf(M_Product_QualityTest_ID));
	}

	/** Get Product Quality Test ID.
		@return Product Quality Test ID	  */
	@Override
	public int getM_Product_QualityTest_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_QualityTest_ID);
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
			set_ValueNoCheck (COLUMNNAME_M_QualityTest_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_QualityTest_ID, Integer.valueOf(M_QualityTest_ID));
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
}