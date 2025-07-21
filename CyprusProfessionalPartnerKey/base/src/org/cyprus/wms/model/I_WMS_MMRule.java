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
package org.cyprus.wms.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for WMS_MMRule
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_WMS_MMRule 
{

    /** TableName=WMS_MMRule */
    public static final String Table_Name = "WMS_MMRule";

    /** AD_Table_ID=1000108 */
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

    /** Column name IsMaintainUOMIntegrity */
    public static final String COLUMNNAME_IsMaintainUOMIntegrity = "IsMaintainUOMIntegrity";

	/** Set Maintain UOM Integrity.
	  * This checkbox can be used to restrict the locator selection based on the stocking UOM and picking UOM of the locator
	  */
	public void setIsMaintainUOMIntegrity (boolean IsMaintainUOMIntegrity);

	/** Get Maintain UOM Integrity.
	  * This checkbox can be used to restrict the locator selection based on the stocking UOM and picking UOM of the locator
	  */
	public boolean isMaintainUOMIntegrity();

    /** Column name MMType */
    public static final String COLUMNNAME_MMType = "MMType";

	/** Set Type.
	  * Warehouse Management Rule Type
	  */
	public void setMMType (String MMType);

	/** Get Type.
	  * Warehouse Management Rule Type
	  */
	public String getMMType();

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

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Rule */
    public static final String COLUMNNAME_Rule = "Rule";

	/** Set Rule.
	  * Indicates which locators will qualify 
	  */
	public void setRule (String Rule);

	/** Get Rule.
	  * Indicates which locators will qualify 
	  */
	public String getRule();

    /** Column name RuleClass */
    public static final String COLUMNNAME_RuleClass = "RuleClass";

	/** Set Custom Class.
	  * Custom java class for Warehouse Management Rule
	  */
	public void setRuleClass (String RuleClass);

	/** Get Custom Class.
	  * Custom java class for Warehouse Management Rule
	  */
	public String getRuleClass();

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

    /** Column name WMS_MMRule_ID */
    public static final String COLUMNNAME_WMS_MMRule_ID = "WMS_MMRule_ID";

	/** Set Warehouse Management Rule ID	  */
	public void setWMS_MMRule_ID (int WMS_MMRule_ID);

	/** Get Warehouse Management Rule ID	  */
	public int getWMS_MMRule_ID();

    /** Column name WMS_Zone_ID */
    public static final String COLUMNNAME_WMS_Zone_ID = "WMS_Zone_ID";

	/** Set Zone ID	  */
	public void setWMS_Zone_ID (int WMS_Zone_ID);

	/** Get Zone ID	  */
	public int getWMS_Zone_ID();

	public I_WMS_Zone getWMS_Zone() throws RuntimeException;
}
