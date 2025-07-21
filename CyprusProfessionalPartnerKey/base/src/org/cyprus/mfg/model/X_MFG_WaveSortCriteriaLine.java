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

import org.cyprusbrs.framework.I_AD_InfoColumn;
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;

/** Generated Model for MFG_WaveSortCriteriaLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_MFG_WaveSortCriteriaLine extends PO implements I_MFG_WaveSortCriteriaLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_MFG_WaveSortCriteriaLine (Properties ctx, int MFG_WaveSortCriteriaLine_ID, String trxName)
    {
      super (ctx, MFG_WaveSortCriteriaLine_ID, trxName);
      /** if (MFG_WaveSortCriteriaLine_ID == 0)
        {
			setMFG_WaveSortCriteriaLine_ID (0);
			setMFG_WaveSortCriteria_ID (0);
			setOrderByType (null);
			setSeqNo (0);
// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM MFG_WaveSortCriteriaLine WHERE MFG_WaveSortCriteria_ID=@MFG_WaveSortCriteria_ID@
        } */
    }

    /** Load Constructor */
    public X_MFG_WaveSortCriteriaLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_MFG_WaveSortCriteriaLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_AD_InfoColumn getAD_InfoColumn() throws RuntimeException
    {
		return (I_AD_InfoColumn)MTable.get(getCtx(), I_AD_InfoColumn.Table_Name)
			.getPO(getAD_InfoColumn_ID(), get_TrxName());	}

	/** Set Info Column.
		@param AD_InfoColumn_ID 
		Info Window Column
	  */
	public void setAD_InfoColumn_ID (int AD_InfoColumn_ID)
	{
		if (AD_InfoColumn_ID < 1) 
			set_Value (COLUMNNAME_AD_InfoColumn_ID, null);
		else 
			set_Value (COLUMNNAME_AD_InfoColumn_ID, Integer.valueOf(AD_InfoColumn_ID));
	}

	/** Get Info Column.
		@return Info Window Column
	  */
	public int getAD_InfoColumn_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_InfoColumn_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Wave Sort Criteria Line ID.
		@param MFG_WaveSortCriteriaLine_ID Wave Sort Criteria Line ID	  */
	public void setMFG_WaveSortCriteriaLine_ID (int MFG_WaveSortCriteriaLine_ID)
	{
		if (MFG_WaveSortCriteriaLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WaveSortCriteriaLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WaveSortCriteriaLine_ID, Integer.valueOf(MFG_WaveSortCriteriaLine_ID));
	}

	/** Get Wave Sort Criteria Line ID.
		@return Wave Sort Criteria Line ID	  */
	public int getMFG_WaveSortCriteriaLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WaveSortCriteriaLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WaveSortCriteria getMFG_WaveSortCriteria() throws RuntimeException
    {
		return (I_MFG_WaveSortCriteria)MTable.get(getCtx(), I_MFG_WaveSortCriteria.Table_Name)
			.getPO(getMFG_WaveSortCriteria_ID(), get_TrxName());	}

	/** Set Wave Sort Criteria.
		@param MFG_WaveSortCriteria_ID Wave Sort Criteria	  */
	public void setMFG_WaveSortCriteria_ID (int MFG_WaveSortCriteria_ID)
	{
		if (MFG_WaveSortCriteria_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_MFG_WaveSortCriteria_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_MFG_WaveSortCriteria_ID, Integer.valueOf(MFG_WaveSortCriteria_ID));
	}

	/** Get Wave Sort Criteria.
		@return Wave Sort Criteria	  */
	public int getMFG_WaveSortCriteria_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WaveSortCriteria_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** OrderByType AD_Reference_ID=1000040 */
	public static final int ORDERBYTYPE_AD_Reference_ID=1000040;
	/** Ascending = A */
	public static final String ORDERBYTYPE_Ascending = "A";
	/** Descending = D */
	public static final String ORDERBYTYPE_Descending = "D";
	/** Set Order By Type.
		@param OrderByType 
		Type of Order By - Ascending or Descending
	  */
	public void setOrderByType (String OrderByType)
	{

		set_Value (COLUMNNAME_OrderByType, OrderByType);
	}

	/** Get Order By Type.
		@return Type of Order By - Ascending or Descending
	  */
	public String getOrderByType () 
	{
		return (String)get_Value(COLUMNNAME_OrderByType);
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