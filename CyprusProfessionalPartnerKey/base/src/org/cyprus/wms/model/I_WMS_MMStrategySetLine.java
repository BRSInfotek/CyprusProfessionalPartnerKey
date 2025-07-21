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

import org.cyprusbrs.framework.I_C_BP_Group;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_M_Product_Category;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for WMS_MMStrategySetLine
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_WMS_MMStrategySetLine 
{

    /** TableName=WMS_MMStrategySetLine */
    public static final String Table_Name = "WMS_MMStrategySetLine";

    /** AD_Table_ID=1000117 */
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

    /** Column name C_BP_Group_ID */
    public static final String COLUMNNAME_C_BP_Group_ID = "C_BP_Group_ID";

	/** Set Business Partner Group.
	  * Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID);

	/** Get Business Partner Group.
	  * Business Partner Group
	  */
	public int getC_BP_Group_ID();

	public I_C_BP_Group getC_BP_Group() throws RuntimeException;

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public I_C_BPartner getC_BPartner() throws RuntimeException;

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

    /** Column name M_Locator_ID */
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";

	/** Set Locator.
	  * Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID);

	/** Get Locator.
	  * Warehouse Locator
	  */
	public int getM_Locator_ID();

	public I_M_Locator getM_Locator() throws RuntimeException;

    /** Column name M_Product_Category_ID */
    public static final String COLUMNNAME_M_Product_Category_ID = "M_Product_Category_ID";

	/** Set Product Category.
	  * Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID);

	/** Get Product Category.
	  * Category of a Product
	  */
	public int getM_Product_Category_ID();

	public I_M_Product_Category getM_Product_Category() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public I_M_Product getM_Product() throws RuntimeException;

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo (int SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public int getSeqNo();

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

    /** Column name WMS_DocTypeGroup_ID */
    public static final String COLUMNNAME_WMS_DocTypeGroup_ID = "WMS_DocTypeGroup_ID";

	/** Set Order Type Group ID	  */
	public void setWMS_DocTypeGroup_ID (int WMS_DocTypeGroup_ID);

	/** Get Order Type Group ID	  */
	public int getWMS_DocTypeGroup_ID();

	public I_WMS_DocTypeGroup getWMS_DocTypeGroup() throws RuntimeException;

    /** Column name WMS_MMStrategySetLine_ID */
    public static final String COLUMNNAME_WMS_MMStrategySetLine_ID = "WMS_MMStrategySetLine_ID";

	/** Set Material Management Strategy Set Line ID	  */
	public void setWMS_MMStrategySetLine_ID (int WMS_MMStrategySetLine_ID);

	/** Get Material Management Strategy Set Line ID	  */
	public int getWMS_MMStrategySetLine_ID();

    /** Column name WMS_MMStrategySet_ID */
    public static final String COLUMNNAME_WMS_MMStrategySet_ID = "WMS_MMStrategySet_ID";

	/** Set Material Management Strategy Set ID	  */
	public void setWMS_MMStrategySet_ID (int WMS_MMStrategySet_ID);

	/** Get Material Management Strategy Set ID	  */
	public int getWMS_MMStrategySet_ID();

	public I_WMS_MMStrategySet getWMS_MMStrategySet() throws RuntimeException;

    /** Column name WMS_MMStrategy_ID */
    public static final String COLUMNNAME_WMS_MMStrategy_ID = "WMS_MMStrategy_ID";

	/** Set Warehouse Management Strategy ID	  */
	public void setWMS_MMStrategy_ID (int WMS_MMStrategy_ID);

	/** Get Warehouse Management Strategy ID	  */
	public int getWMS_MMStrategy_ID();

	public I_WMS_MMStrategy getWMS_MMStrategy() throws RuntimeException;

    /** Column name WMS_Zone_ID */
    public static final String COLUMNNAME_WMS_Zone_ID = "WMS_Zone_ID";

	/** Set Zone ID	  */
	public void setWMS_Zone_ID (int WMS_Zone_ID);

	/** Get Zone ID	  */
	public int getWMS_Zone_ID();

	public I_WMS_Zone getWMS_Zone() throws RuntimeException;
}
