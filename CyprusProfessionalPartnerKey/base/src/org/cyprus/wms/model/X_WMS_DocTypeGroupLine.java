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

import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for WMS_DocTypeGroupLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_DocTypeGroupLine extends PO implements I_WMS_DocTypeGroupLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_DocTypeGroupLine (Properties ctx, int WMS_DocTypeGroupLine_ID, String trxName)
    {
      super (ctx, WMS_DocTypeGroupLine_ID, trxName);
      /** if (WMS_DocTypeGroupLine_ID == 0)
        {
			setC_DocType_ID (0);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM WMS_DocTypeGroupLine WHERE WMS_DocTypeGroup_ID=@WMS_DocTypeGroup_ID@
			setWMS_DocTypeGroupLine_ID (0);
			setWMS_DocTypeGroup_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_DocTypeGroupLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_DocTypeGroupLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_DocType getC_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
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

	/** Set Order Type Group Line ID.
		@param WMS_DocTypeGroupLine_ID Order Type Group Line ID	  */
	public void setWMS_DocTypeGroupLine_ID (int WMS_DocTypeGroupLine_ID)
	{
		if (WMS_DocTypeGroupLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_DocTypeGroupLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_DocTypeGroupLine_ID, Integer.valueOf(WMS_DocTypeGroupLine_ID));
	}

	/** Get Order Type Group Line ID.
		@return Order Type Group Line ID	  */
	public int getWMS_DocTypeGroupLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_DocTypeGroupLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_DocTypeGroup getWMS_DocTypeGroup() throws RuntimeException
    {
		return (I_WMS_DocTypeGroup)MTable.get(getCtx(), I_WMS_DocTypeGroup.Table_Name)
			.getPO(getWMS_DocTypeGroup_ID(), get_TrxName());	}

	/** Set Order Type Group ID.
		@param WMS_DocTypeGroup_ID Order Type Group ID	  */
	public void setWMS_DocTypeGroup_ID (int WMS_DocTypeGroup_ID)
	{
		if (WMS_DocTypeGroup_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_DocTypeGroup_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_DocTypeGroup_ID, Integer.valueOf(WMS_DocTypeGroup_ID));
	}

	/** Get Order Type Group ID.
		@return Order Type Group ID	  */
	public int getWMS_DocTypeGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_DocTypeGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}