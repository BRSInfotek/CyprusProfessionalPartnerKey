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

/** Generated Model for AD_Grafana_Access
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_AD_Grafana_Access extends PO implements I_AD_Grafana_Access, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250616L;

    /** Standard Constructor */
    public X_AD_Grafana_Access (Properties ctx, int AD_Grafana_Access_ID, String trxName)
    {
      super (ctx, AD_Grafana_Access_ID, trxName);
      /** if (AD_Grafana_Access_ID == 0)
        {
			setAD_Role_ID (0);
			setIsReadWrite (false);
			setM_Grafana_ID (0);
        } */
    }

    /** Load Constructor */
    public X_AD_Grafana_Access (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client 
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
      StringBuffer sb = new StringBuffer ("X_AD_Grafana_Access[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_AD_Role getAD_Role() throws RuntimeException
    {
		return (I_AD_Role)MTable.get(getCtx(), I_AD_Role.Table_Name)
			.getPO(getAD_Role_ID(), get_TrxName());	}

	/** Set Role.
		@param AD_Role_ID 
		Responsibility Role
	  */
	public void setAD_Role_ID (int AD_Role_ID)
	{
		if (AD_Role_ID < 0) 
			set_ValueNoCheck (COLUMNNAME_AD_Role_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_Role_ID, Integer.valueOf(AD_Role_ID));
	}

	/** Get Role.
		@return Responsibility Role
	  */
	public int getAD_Role_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Role_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Read Write.
		@param IsReadWrite 
		Field is read / write
	  */
	public void setIsReadWrite (boolean IsReadWrite)
	{
		set_Value (COLUMNNAME_IsReadWrite, Boolean.valueOf(IsReadWrite));
	}

	/** Get Read Write.
		@return Field is read / write
	  */
	public boolean isReadWrite () 
	{
		Object oo = get_Value(COLUMNNAME_IsReadWrite);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_M_Grafana getM_Grafana() throws RuntimeException
    {
		return (I_M_Grafana)MTable.get(getCtx(), I_M_Grafana.Table_Name)
			.getPO(getM_Grafana_ID(), get_TrxName());	}

	/** Set Grafana.
		@param M_Grafana_ID Grafana	  */
	public void setM_Grafana_ID (int M_Grafana_ID)
	{
		if (M_Grafana_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Grafana_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Grafana_ID, Integer.valueOf(M_Grafana_ID));
	}

	/** Get Grafana.
		@return Grafana	  */
	public int getM_Grafana_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Grafana_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}