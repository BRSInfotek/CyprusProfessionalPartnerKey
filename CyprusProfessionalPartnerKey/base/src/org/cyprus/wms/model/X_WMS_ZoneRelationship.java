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

/** Generated Model for WMS_ZoneRelationship
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_ZoneRelationship extends PO implements I_WMS_ZoneRelationship, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_ZoneRelationship (Properties ctx, int WMS_ZoneRelationship_ID, String trxName)
    {
      super (ctx, WMS_ZoneRelationship_ID, trxName);
      /** if (WMS_ZoneRelationship_ID == 0)
        {
			setM_SourceZone_ID (0);
			setReplenishmentSeqNo (0);
// 0
			setWMS_ZoneRelationship_ID (0);
			setWMS_Zone_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_ZoneRelationship (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_WMS_ZoneRelationship[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_WMS_Zone getM_SourceZone() throws RuntimeException
    {
		return (I_WMS_Zone)MTable.get(getCtx(), I_WMS_Zone.Table_Name)
			.getPO(getM_SourceZone_ID(), get_TrxName());	}

	/** Set Source Zone.
		@param M_SourceZone_ID 
		Source Warehouse zone
	  */
	public void setM_SourceZone_ID (int M_SourceZone_ID)
	{
		if (M_SourceZone_ID < 1) 
			set_Value (COLUMNNAME_M_SourceZone_ID, null);
		else 
			set_Value (COLUMNNAME_M_SourceZone_ID, Integer.valueOf(M_SourceZone_ID));
	}

	/** Get Source Zone.
		@return Source Warehouse zone
	  */
	public int getM_SourceZone_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_SourceZone_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Replenishment Sequence No.
		@param ReplenishmentSeqNo 
		Replenishment Sequence No of zone
	  */
	public void setReplenishmentSeqNo (int ReplenishmentSeqNo)
	{
		set_Value (COLUMNNAME_ReplenishmentSeqNo, Integer.valueOf(ReplenishmentSeqNo));
	}

	/** Get Replenishment Sequence No.
		@return Replenishment Sequence No of zone
	  */
	public int getReplenishmentSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ReplenishmentSeqNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Zone Relationship ID.
		@param WMS_ZoneRelationship_ID Zone Relationship ID	  */
	public void setWMS_ZoneRelationship_ID (int WMS_ZoneRelationship_ID)
	{
		if (WMS_ZoneRelationship_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_ZoneRelationship_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_ZoneRelationship_ID, Integer.valueOf(WMS_ZoneRelationship_ID));
	}

	/** Get Zone Relationship ID.
		@return Zone Relationship ID	  */
	public int getWMS_ZoneRelationship_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_ZoneRelationship_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_Zone getWMS_Zone() throws RuntimeException
    {
		return (I_WMS_Zone)MTable.get(getCtx(), I_WMS_Zone.Table_Name)
			.getPO(getWMS_Zone_ID(), get_TrxName());	}

	/** Set Zone ID.
		@param WMS_Zone_ID Zone ID	  */
	public void setWMS_Zone_ID (int WMS_Zone_ID)
	{
		if (WMS_Zone_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_Zone_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_Zone_ID, Integer.valueOf(WMS_Zone_ID));
	}

	/** Get Zone ID.
		@return Zone ID	  */
	public int getWMS_Zone_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_Zone_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}