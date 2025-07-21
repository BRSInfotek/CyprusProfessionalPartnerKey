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

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MFG_WorkCenterResource
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WorkCenterResource extends PO implements I_MFG_WorkCenterResource, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WorkCenterResource (Properties ctx, int MFG_WorkCenterResource_ID, String trxName)
    {
      super (ctx, MFG_WorkCenterResource_ID, trxName);
      /** if (MFG_WorkCenterResource_ID == 0)
        {
			setMFG_WorkCenterResource_ID (0);
			setMFG_WorkCenter_ID (0);
			setM_Product_ID (0);
        } */
    }

    /** Load Constructor */
    public X_MFG_WorkCenterResource (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WorkCenterResource[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Work Center Resource ID.
		@param MFG_WorkCenterResource_ID Work Center Resource ID	  */
	public void setMFG_WorkCenterResource_ID (int MFG_WorkCenterResource_ID)
	{
		if (MFG_WorkCenterResource_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenterResource_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenterResource_ID, Integer.valueOf(MFG_WorkCenterResource_ID));
	}

	/** Get Work Center Resource ID.
		@return Work Center Resource ID	  */
	public int getMFG_WorkCenterResource_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkCenterResource_ID);
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
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenter_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WorkCenter_ID, Integer.valueOf(MFG_WorkCenter_ID));
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
}