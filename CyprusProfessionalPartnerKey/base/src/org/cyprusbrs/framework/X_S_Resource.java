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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Model for S_Resource
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP - $Id$ */
public class X_S_Resource extends PO implements I_S_Resource, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20250702L;

    /** Standard Constructor */
    public X_S_Resource (Properties ctx, int S_Resource_ID, String trxName)
    {
      super (ctx, S_Resource_ID, trxName);
      /** if (S_Resource_ID == 0)
        {
			setIsAvailable (true);
// Y
			setM_Warehouse_ID (0);
			setName (null);
			setPercentUtilization (Env.ZERO);
// 100
			setS_Resource_ID (0);
			setS_ResourceType_ID (0);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_S_Resource (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_S_Resource[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_A_Asset getA_Asset() throws RuntimeException
    {
		return (I_A_Asset)MTable.get(getCtx(), I_A_Asset.Table_Name)
			.getPO(getA_Asset_ID(), get_TrxName());	}

	/** Set Asset.
		@param A_Asset_ID 
		Asset used internally or by customers
	  */
	public void setA_Asset_ID (int A_Asset_ID)
	{
		if (A_Asset_ID < 1) 
			set_Value (COLUMNNAME_A_Asset_ID, null);
		else 
			set_Value (COLUMNNAME_A_Asset_ID, Integer.valueOf(A_Asset_ID));
	}

	/** Get Asset.
		@return Asset used internally or by customers
	  */
	public int getA_Asset_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_AD_User getAD_User() throws RuntimeException
    {
		return (I_AD_User)MTable.get(getCtx(), I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Tax getC_Tax() throws RuntimeException
    {
		return (I_C_Tax)MTable.get(getCtx(), I_C_Tax.Table_Name)
			.getPO(getC_Tax_ID(), get_TrxName());	}

	/** Set Tax1.
		@param C_Tax_ID 
		Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID)
	{
		if (C_Tax_ID < 1) 
			set_Value (COLUMNNAME_C_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
	}

	/** Get Tax1.
		@return Tax identifier
	  */
	public int getC_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_TaxCategory getC_TaxCategory() throws RuntimeException
    {
		return (I_C_TaxCategory)MTable.get(getCtx(), I_C_TaxCategory.Table_Name)
			.getPO(getC_TaxCategory_ID(), get_TrxName());	}

	/** Set Tax Category.
		@param C_TaxCategory_ID 
		Tax Category
	  */
	public void setC_TaxCategory_ID (int C_TaxCategory_ID)
	{
		if (C_TaxCategory_ID < 1) 
			set_Value (COLUMNNAME_C_TaxCategory_ID, null);
		else 
			set_Value (COLUMNNAME_C_TaxCategory_ID, Integer.valueOf(C_TaxCategory_ID));
	}

	/** Get Tax Category.
		@return Tax Category
	  */
	public int getC_TaxCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_TaxCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Capacity.
		@param Capacity Capacity	  */
	public void setCapacity (BigDecimal Capacity)
	{
		set_Value (COLUMNNAME_Capacity, Capacity);
	}

	/** Get Capacity.
		@return Capacity	  */
	public BigDecimal getCapacity () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Capacity);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Chargeable Quantity.
		@param ChargeableQty Chargeable Quantity	  */
	public void setChargeableQty (BigDecimal ChargeableQty)
	{
		set_Value (COLUMNNAME_ChargeableQty, ChargeableQty);
	}

	/** Get Chargeable Quantity.
		@return Chargeable Quantity	  */
	public BigDecimal getChargeableQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ChargeableQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Daily Capacity.
		@param DailyCapacity Daily Capacity	  */
	public void setDailyCapacity (BigDecimal DailyCapacity)
	{
		set_Value (COLUMNNAME_DailyCapacity, DailyCapacity);
	}

	/** Get Daily Capacity.
		@return Daily Capacity	  */
	public BigDecimal getDailyCapacity () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DailyCapacity);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Available.
		@param IsAvailable 
		Resource is available
	  */
	public void setIsAvailable (boolean IsAvailable)
	{
		set_Value (COLUMNNAME_IsAvailable, Boolean.valueOf(IsAvailable));
	}

	/** Get Available.
		@return Resource is available
	  */
	public boolean isAvailable () 
	{
		Object oo = get_Value(COLUMNNAME_IsAvailable);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Manufacturing Resource.
		@param IsManufacturingResource Manufacturing Resource	  */
	public void setIsManufacturingResource (boolean IsManufacturingResource)
	{
		set_Value (COLUMNNAME_IsManufacturingResource, Boolean.valueOf(IsManufacturingResource));
	}

	/** Get Manufacturing Resource.
		@return Manufacturing Resource	  */
	public boolean isManufacturingResource () 
	{
		Object oo = get_Value(COLUMNNAME_IsManufacturingResource);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (I_M_Warehouse)MTable.get(getCtx(), I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1) 
			set_Value (COLUMNNAME_M_Warehouse_ID, null);
		else 
			set_Value (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** ManufacturingResourceType AD_Reference_ID=53223 */
	public static final int MANUFACTURINGRESOURCETYPE_AD_Reference_ID=53223;
	/** Production Line = PL */
	public static final String MANUFACTURINGRESOURCETYPE_ProductionLine = "PL";
	/** Plant = PT */
	public static final String MANUFACTURINGRESOURCETYPE_Plant = "PT";
	/** Work Center = WC */
	public static final String MANUFACTURINGRESOURCETYPE_WorkCenter = "WC";
	/** Work Station = WS */
	public static final String MANUFACTURINGRESOURCETYPE_WorkStation = "WS";
	/** Person = PN */
	public static final String MANUFACTURINGRESOURCETYPE_Person = "PN";
	/** Tools = TL */
	public static final String MANUFACTURINGRESOURCETYPE_Tools = "TL";
	/** Set Manufacturing Resource Type.
		@param ManufacturingResourceType Manufacturing Resource Type	  */
	public void setManufacturingResourceType (String ManufacturingResourceType)
	{

		set_Value (COLUMNNAME_ManufacturingResourceType, ManufacturingResourceType);
	}

	/** Get Manufacturing Resource Type.
		@return Manufacturing Resource Type	  */
	public String getManufacturingResourceType () 
	{
		return (String)get_Value(COLUMNNAME_ManufacturingResourceType);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	/** Set % Utilization.
		@param PercentUtilization % Utilization	  */
	public void setPercentUtilization (BigDecimal PercentUtilization)
	{
		set_Value (COLUMNNAME_PercentUtilization, PercentUtilization);
	}

	/** Get % Utilization.
		@return % Utilization	  */
	public BigDecimal getPercentUtilization () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PercentUtilization);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Planning Horizon.
		@param PlanningHorizon 
		The planning horizon is the amount of time (Days) an organisation will look into the future when preparing a strategic plan.
	  */
	public void setPlanningHorizon (int PlanningHorizon)
	{
		set_Value (COLUMNNAME_PlanningHorizon, Integer.valueOf(PlanningHorizon));
	}

	/** Get Planning Horizon.
		@return The planning horizon is the amount of time (Days) an organisation will look into the future when preparing a strategic plan.
	  */
	public int getPlanningHorizon () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PlanningHorizon);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Queuing Time.
		@param QueuingTime 
		Queue time is the time a job waits at a work center before begin handled.
	  */
	public void setQueuingTime (BigDecimal QueuingTime)
	{
		set_Value (COLUMNNAME_QueuingTime, QueuingTime);
	}

	/** Get Queuing Time.
		@return Queue time is the time a job waits at a work center before begin handled.
	  */
	public BigDecimal getQueuingTime () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QueuingTime);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_C_Tax getRef_C_Tax() throws RuntimeException
    {
		return (I_C_Tax)MTable.get(getCtx(), I_C_Tax.Table_Name)
			.getPO(getRef_C_Tax_ID(), get_TrxName());	}

	/** Set Tax2.
		@param Ref_C_Tax_ID 
		Tax2 identifier
	  */
	public void setRef_C_Tax_ID (int Ref_C_Tax_ID)
	{
		if (Ref_C_Tax_ID < 1) 
			set_Value (COLUMNNAME_Ref_C_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_Ref_C_Tax_ID, Integer.valueOf(Ref_C_Tax_ID));
	}

	/** Get Tax2.
		@return Tax2 identifier
	  */
	public int getRef_C_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Ref_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Resource.
		@param S_Resource_ID 
		Resource
	  */
	public void setS_Resource_ID (int S_Resource_ID)
	{
		if (S_Resource_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_S_Resource_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_S_Resource_ID, Integer.valueOf(S_Resource_ID));
	}

	/** Get Resource.
		@return Resource
	  */
	public int getS_Resource_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_S_Resource_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_S_ResourceType getS_ResourceType() throws RuntimeException
    {
		return (I_S_ResourceType)MTable.get(getCtx(), I_S_ResourceType.Table_Name)
			.getPO(getS_ResourceType_ID(), get_TrxName());	}

	/** Set Resource Type.
		@param S_ResourceType_ID Resource Type	  */
	public void setS_ResourceType_ID (int S_ResourceType_ID)
	{
		if (S_ResourceType_ID < 1) 
			set_Value (COLUMNNAME_S_ResourceType_ID, null);
		else 
			set_Value (COLUMNNAME_S_ResourceType_ID, Integer.valueOf(S_ResourceType_ID));
	}

	/** Get Resource Type.
		@return Resource Type	  */
	public int getS_ResourceType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_S_ResourceType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

	/** Set Waiting Time.
		@param WaitingTime 
		Workflow Simulation Waiting time
	  */
	public void setWaitingTime (BigDecimal WaitingTime)
	{
		set_Value (COLUMNNAME_WaitingTime, WaitingTime);
	}

	/** Get Waiting Time.
		@return Workflow Simulation Waiting time
	  */
	public BigDecimal getWaitingTime () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_WaitingTime);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}