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
package org.cyprus.wms.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for WMS_MMStrategyLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_MMStrategyLine extends PO implements I_WMS_MMStrategyLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_MMStrategyLine (Properties ctx, int WMS_MMStrategyLine_ID, String trxName)
    {
      super (ctx, WMS_MMStrategyLine_ID, trxName);
      /** if (WMS_MMStrategyLine_ID == 0)
        {
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM WMS_MMStrategyLine WHERE WMS_MMStrategy_ID=@WMS_MMStrategy_ID@
			setWMS_MMRule_ID (0);
			setWMS_MMStrategyLine_ID (0);
			setWMS_MMStrategy_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_MMStrategyLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_MMStrategyLine[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	public I_WMS_MMRule getWMS_MMRule() throws RuntimeException
    {
		return (I_WMS_MMRule)MTable.get(getCtx(), I_WMS_MMRule.Table_Name)
			.getPO(getWMS_MMRule_ID(), get_TrxName());	}

	/** Set Warehouse Management Rule .
		@param WMS_MMRule_ID 
		Rule to determine the putaway or pick location for goods stocked in the warehouse
	  */
	public void setWMS_MMRule_ID (int WMS_MMRule_ID)
	{
		if (WMS_MMRule_ID < 1) 
			set_Value (COLUMNNAME_WMS_MMRule_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_MMRule_ID, Integer.valueOf(WMS_MMRule_ID));
	}

	/** Get Warehouse Management Rule .
		@return Rule to determine the putaway or pick location for goods stocked in the warehouse
	  */
	public int getWMS_MMRule_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMRule_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Material Management Strategy Line ID.
		@param WMS_MMStrategyLine_ID Material Management Strategy Line ID	  */
	public void setWMS_MMStrategyLine_ID (int WMS_MMStrategyLine_ID)
	{
		if (WMS_MMStrategyLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategyLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategyLine_ID, Integer.valueOf(WMS_MMStrategyLine_ID));
	}

	/** Get Material Management Strategy Line ID.
		@return Material Management Strategy Line ID	  */
	public int getWMS_MMStrategyLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMStrategyLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_MMStrategy getWMS_MMStrategy() throws RuntimeException
    {
		return (I_WMS_MMStrategy)MTable.get(getCtx(), I_WMS_MMStrategy.Table_Name)
			.getPO(getWMS_MMStrategy_ID(), get_TrxName());	}

	/** Set Warehouse Management Strategy ID.
		@param WMS_MMStrategy_ID Warehouse Management Strategy ID	  */
	public void setWMS_MMStrategy_ID (int WMS_MMStrategy_ID)
	{
		if (WMS_MMStrategy_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategy_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_MMStrategy_ID, Integer.valueOf(WMS_MMStrategy_ID));
	}

	/** Get Warehouse Management Strategy ID.
		@return Warehouse Management Strategy ID	  */
	public int getWMS_MMStrategy_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_MMStrategy_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}