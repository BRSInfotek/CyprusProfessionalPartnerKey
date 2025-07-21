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
package org.cyprus.mrp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MRP_PlanRun
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MRP_PlanRun 
{

    /** TableName=MRP_PlanRun */
    public static final String Table_Name = "MRP_PlanRun";

    /** AD_Table_ID=1000069 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name C_Period_BackOrder_ID */
    public static final String COLUMNNAME_C_Period_BackOrder_ID = "C_Period_BackOrder_ID";

	/** Set Back Order Period From.
	  * Period from which back orders should be considered for the plan run
	  */
	public void setC_Period_BackOrder_ID (int C_Period_BackOrder_ID);

	/** Get Back Order Period From.
	  * Period from which back orders should be considered for the plan run
	  */
	public int getC_Period_BackOrder_ID();

	public I_C_Period getC_Period_BackOrder() throws RuntimeException;

    /** Column name C_Period_From_ID */
    public static final String COLUMNNAME_C_Period_From_ID = "C_Period_From_ID";

	/** Set Period From.
	  * Starting period of a range of periods
	  */
	public void setC_Period_From_ID (int C_Period_From_ID);

	/** Get Period From.
	  * Starting period of a range of periods
	  */
	public int getC_Period_From_ID();

	public I_C_Period getC_Period_From() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created On.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name MRP_MasterDemand_ID */
    public static final String COLUMNNAME_MRP_MasterDemand_ID = "MRP_MasterDemand_ID";

	/** Set Master Demand.
	  * Master Demand for material requirements
	  */
	public void setMRP_MasterDemand_ID (int MRP_MasterDemand_ID);

	/** Get Master Demand.
	  * Master Demand for material requirements
	  */
	public int getMRP_MasterDemand_ID();

    /** Column name MRP_PlanRun_ID */
    public static final String COLUMNNAME_MRP_PlanRun_ID = "MRP_PlanRun_ID";

	/** Set Plan Run	  */
	public void setMRP_PlanRun_ID (int MRP_PlanRun_ID);

	/** Get Plan Run	  */
	public int getMRP_PlanRun_ID();

    /** Column name MRP_Plan_ID */
    public static final String COLUMNNAME_MRP_Plan_ID = "MRP_Plan_ID";

	/** Set Plan 	  */
	public void setMRP_Plan_ID (int MRP_Plan_ID);

	/** Get Plan 	  */
	public int getMRP_Plan_ID();

	public I_MRP_Plan getMRP_Plan() throws RuntimeException;

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
