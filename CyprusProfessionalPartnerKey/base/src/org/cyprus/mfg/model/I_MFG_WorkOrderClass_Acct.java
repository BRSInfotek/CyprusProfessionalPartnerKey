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

import org.cyprusbrs.framework.I_C_AcctSchema;
import org.cyprusbrs.framework.I_C_ValidCombination;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_WorkOrderClass_Acct
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MFG_WorkOrderClass_Acct 
{

    /** TableName=MFG_WorkOrderClass_Acct */
    public static final String Table_Name = "MFG_WorkOrderClass_Acct";

    /** AD_Table_ID=1000085 */
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

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

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

    /** Column name MFG_WorkOrderClass_Acct_ID */
    public static final String COLUMNNAME_MFG_WorkOrderClass_Acct_ID = "MFG_WorkOrderClass_Acct_ID";

	/** Set Work Order Class Accounting ID	  */
	public void setMFG_WorkOrderClass_Acct_ID (int MFG_WorkOrderClass_Acct_ID);

	/** Get Work Order Class Accounting ID	  */
	public int getMFG_WorkOrderClass_Acct_ID();

    /** Column name MFG_WorkOrderClass_ID */
    public static final String COLUMNNAME_MFG_WorkOrderClass_ID = "MFG_WorkOrderClass_ID";

	/** Set Work Order Class ID	  */
	public void setMFG_WorkOrderClass_ID (int MFG_WorkOrderClass_ID);

	/** Get Work Order Class ID	  */
	public int getMFG_WorkOrderClass_ID();

	public I_MFG_WorkOrderClass getMFG_WorkOrderClass() throws RuntimeException;

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

    /** Column name WO_MaterialOverhdVariance_Acct */
    public static final String COLUMNNAME_WO_MaterialOverhdVariance_Acct = "WO_MaterialOverhdVariance_Acct";

	/** Set Work Order Material Overhead Variance.
	  * Work Order Material Overhead Variance Account
	  */
	public void setWO_MaterialOverhdVariance_Acct (int WO_MaterialOverhdVariance_Acct);

	/** Get Work Order Material Overhead Variance.
	  * Work Order Material Overhead Variance Account
	  */
	public int getWO_MaterialOverhdVariance_Acct();

	public I_C_ValidCombination getWO_MaterialOverhdVariance_A() throws RuntimeException;

    /** Column name WO_MaterialOverhd_Acct */
    public static final String COLUMNNAME_WO_MaterialOverhd_Acct = "WO_MaterialOverhd_Acct";

	/** Set Work Order Material Overhead.
	  * Work Order Material Overhead Account
	  */
	public void setWO_MaterialOverhd_Acct (int WO_MaterialOverhd_Acct);

	/** Get Work Order Material Overhead.
	  * Work Order Material Overhead Account
	  */
	public int getWO_MaterialOverhd_Acct();

	public I_C_ValidCombination getWO_MaterialOverhd_A() throws RuntimeException;

    /** Column name WO_MaterialVariance_Acct */
    public static final String COLUMNNAME_WO_MaterialVariance_Acct = "WO_MaterialVariance_Acct";

	/** Set Work Order Material Variance.
	  * Work Order Material Variance Account
	  */
	public void setWO_MaterialVariance_Acct (int WO_MaterialVariance_Acct);

	/** Get Work Order Material Variance.
	  * Work Order Material Variance Account
	  */
	public int getWO_MaterialVariance_Acct();

	public I_C_ValidCombination getWO_MaterialVariance_A() throws RuntimeException;

    /** Column name WO_Material_Acct */
    public static final String COLUMNNAME_WO_Material_Acct = "WO_Material_Acct";

	/** Set Work Order Material.
	  * Work Order Material Account
	  */
	public void setWO_Material_Acct (int WO_Material_Acct);

	/** Get Work Order Material.
	  * Work Order Material Account
	  */
	public int getWO_Material_Acct();

	public I_C_ValidCombination getWO_Material_A() throws RuntimeException;

    /** Column name WO_OverhdVariance_Acct */
    public static final String COLUMNNAME_WO_OverhdVariance_Acct = "WO_OverhdVariance_Acct";

	/** Set Work Order Overhead Variance.
	  * Work Order Overhead Variance Account
	  */
	public void setWO_OverhdVariance_Acct (int WO_OverhdVariance_Acct);

	/** Get Work Order Overhead Variance.
	  * Work Order Overhead Variance Account
	  */
	public int getWO_OverhdVariance_Acct();

	public I_C_ValidCombination getWO_OverhdVariance_A() throws RuntimeException;

    /** Column name WO_ResourceVariance_Acct */
    public static final String COLUMNNAME_WO_ResourceVariance_Acct = "WO_ResourceVariance_Acct";

	/** Set Work Order Resource Variance.
	  * Work Order Resource Variance Account
	  */
	public void setWO_ResourceVariance_Acct (int WO_ResourceVariance_Acct);

	/** Get Work Order Resource Variance.
	  * Work Order Resource Variance Account
	  */
	public int getWO_ResourceVariance_Acct();

	public I_C_ValidCombination getWO_ResourceVariance_A() throws RuntimeException;

    /** Column name WO_Resource_Acct */
    public static final String COLUMNNAME_WO_Resource_Acct = "WO_Resource_Acct";

	/** Set Work Order Resource.
	  * Work Order Resource Account
	  */
	public void setWO_Resource_Acct (int WO_Resource_Acct);

	/** Get Work Order Resource.
	  * Work Order Resource Account
	  */
	public int getWO_Resource_Acct();

	public I_C_ValidCombination getWO_Resource_A() throws RuntimeException;

    /** Column name WO_Scrap_Acct */
    public static final String COLUMNNAME_WO_Scrap_Acct = "WO_Scrap_Acct";

	/** Set Work Order Scrap.
	  * Work Order Scrap Account
	  */
	public void setWO_Scrap_Acct (int WO_Scrap_Acct);

	/** Get Work Order Scrap.
	  * Work Order Scrap Account
	  */
	public int getWO_Scrap_Acct();

	public I_C_ValidCombination getWO_Scrap_A() throws RuntimeException;
}
