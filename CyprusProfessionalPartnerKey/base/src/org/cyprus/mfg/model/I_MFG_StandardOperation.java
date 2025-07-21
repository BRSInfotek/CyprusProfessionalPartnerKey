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
package org.cyprus.mfg.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_StandardOperation
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MFG_StandardOperation 
{

    /** TableName=MFG_StandardOperation */
    public static final String Table_Name = "MFG_StandardOperation";

    /** AD_Table_ID=1000073 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

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

    /** Column name C_UOM_ID */
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";

	/** Set UOM.
	  * Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID);

	/** Get UOM.
	  * Unit of Measure
	  */
	public int getC_UOM_ID();

	public I_C_UOM getC_UOM() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

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

    /** Column name IsHazardous */
    public static final String COLUMNNAME_IsHazardous = "IsHazardous";

	/** Set Hazardous.
	  * Involves hazardous materials
	  */
	public void setIsHazardous (boolean IsHazardous);

	/** Get Hazardous.
	  * Involves hazardous materials
	  */
	public boolean isHazardous();

    /** Column name IsPermitRequired */
    public static final String COLUMNNAME_IsPermitRequired = "IsPermitRequired";

	/** Set Permit Required.
	  * Indicates if a permit or similar authorization is required for use or execution of a product, resource or work order operation.
	  */
	public void setIsPermitRequired (boolean IsPermitRequired);

	/** Get Permit Required.
	  * Indicates if a permit or similar authorization is required for use or execution of a product, resource or work order operation.
	  */
	public boolean isPermitRequired();

    /** Column name MFG_Operation_ID */
    public static final String COLUMNNAME_MFG_Operation_ID = "MFG_Operation_ID";

	/** Set Operation	  */
	public void setMFG_Operation_ID (int MFG_Operation_ID);

	/** Get Operation	  */
	public int getMFG_Operation_ID();

	public I_MFG_Operation getMFG_Operation() throws RuntimeException;

    /** Column name MFG_StandardOperation_ID */
    public static final String COLUMNNAME_MFG_StandardOperation_ID = "MFG_StandardOperation_ID";

	/** Set Standard Operation.
	  * Identifies a standard operation template
	  */
	public void setMFG_StandardOperation_ID (int MFG_StandardOperation_ID);

	/** Get Standard Operation.
	  * Identifies a standard operation template
	  */
	public int getMFG_StandardOperation_ID();

    /** Column name MFG_WorkCenter_ID */
    public static final String COLUMNNAME_MFG_WorkCenter_ID = "MFG_WorkCenter_ID";

	/** Set Work Center.
	  * Identifies a production area within a warehouse consisting of people and equipment
	  */
	public void setMFG_WorkCenter_ID (int MFG_WorkCenter_ID);

	/** Get Work Center.
	  * Identifies a production area within a warehouse consisting of people and equipment
	  */
	public int getMFG_WorkCenter_ID();

	public I_MFG_WorkCenter getMFG_WorkCenter() throws RuntimeException;

    /** Column name M_Warehouse_ID */
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";

	/** Set Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID);

	/** Get Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID();

	public I_M_Warehouse getM_Warehouse() throws RuntimeException;

    /** Column name SetupTime */
    public static final String COLUMNNAME_SetupTime = "SetupTime";

	/** Set Setup Time.
	  * Setup time before starting Production
	  */
	public void setSetupTime (BigDecimal SetupTime);

	/** Get Setup Time.
	  * Setup time before starting Production
	  */
	public BigDecimal getSetupTime();

    /** Column name UnitRuntime */
    public static final String COLUMNNAME_UnitRuntime = "UnitRuntime";

	/** Set Runtime per Unit.
	  * Time to produce one unit
	  */
	public void setUnitRuntime (BigDecimal UnitRuntime);

	/** Get Runtime per Unit.
	  * Time to produce one unit
	  */
	public BigDecimal getUnitRuntime();

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
