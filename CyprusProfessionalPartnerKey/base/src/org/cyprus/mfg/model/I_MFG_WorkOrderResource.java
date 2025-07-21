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
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_WorkOrderResource
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MFG_WorkOrderResource 
{

    /** TableName=MFG_WorkOrderResource */
    public static final String Table_Name = "MFG_WorkOrderResource";

    /** AD_Table_ID=1000096 */
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

    /** Column name BasisType */
    public static final String COLUMNNAME_BasisType = "BasisType";

	/** Set Cost Basis Type.
	  * Indicates the option to consume and charge materials and resources
	  */
	public void setBasisType (String BasisType);

	/** Get Cost Basis Type.
	  * Indicates the option to consume and charge materials and resources
	  */
	public String getBasisType();

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

    /** Column name ChargeType */
    public static final String COLUMNNAME_ChargeType = "ChargeType";

	/** Set Cost Charge Type.
	  * Indicates how the production resource will be charged - automatically or manually
	  */
	public void setChargeType (String ChargeType);

	/** Get Cost Charge Type.
	  * Indicates how the production resource will be charged - automatically or manually
	  */
	public String getChargeType();

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

    /** Column name DateActualFrom */
    public static final String COLUMNNAME_DateActualFrom = "DateActualFrom";

	/** Set Actual Date From.
	  * Actual date an activity started
	  */
	public void setDateActualFrom (Timestamp DateActualFrom);

	/** Get Actual Date From.
	  * Actual date an activity started
	  */
	public Timestamp getDateActualFrom();

    /** Column name DateActualTo */
    public static final String COLUMNNAME_DateActualTo = "DateActualTo";

	/** Set Actual Date To.
	  * Actual date an activity ended
	  */
	public void setDateActualTo (Timestamp DateActualTo);

	/** Get Actual Date To.
	  * Actual date an activity ended
	  */
	public Timestamp getDateActualTo();

    /** Column name DateScheduleFrom */
    public static final String COLUMNNAME_DateScheduleFrom = "DateScheduleFrom";

	/** Set Scheduled Date From.
	  * Date an activity is scheduled to start
	  */
	public void setDateScheduleFrom (Timestamp DateScheduleFrom);

	/** Get Scheduled Date From.
	  * Date an activity is scheduled to start
	  */
	public Timestamp getDateScheduleFrom();

    /** Column name DateScheduleTo */
    public static final String COLUMNNAME_DateScheduleTo = "DateScheduleTo";

	/** Set Scheduled Date To.
	  * Date an activity is scheduled to end
	  */
	public void setDateScheduleTo (Timestamp DateScheduleTo);

	/** Get Scheduled Date To.
	  * Date an activity is scheduled to end
	  */
	public Timestamp getDateScheduleTo();

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

    /** Column name MFG_WorkOrderOperation_ID */
    public static final String COLUMNNAME_MFG_WorkOrderOperation_ID = "MFG_WorkOrderOperation_ID";

	/** Set Operation	  */
	public void setMFG_WorkOrderOperation_ID (int MFG_WorkOrderOperation_ID);

	/** Get Operation	  */
	public int getMFG_WorkOrderOperation_ID();

	public I_MFG_WorkOrderOperation getMFG_WorkOrderOperation() throws RuntimeException;

    /** Column name MFG_WorkOrderResource_ID */
    public static final String COLUMNNAME_MFG_WorkOrderResource_ID = "MFG_WorkOrderResource_ID";

	/** Set Work Order Resource ID	  */
	public void setMFG_WorkOrderResource_ID (int MFG_WorkOrderResource_ID);

	/** Get Work Order Resource ID	  */
	public int getMFG_WorkOrderResource_ID();

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

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name QtyRequired */
    public static final String COLUMNNAME_QtyRequired = "QtyRequired";

	/** Set Required Quantity.
	  * Quantity required for an activity
	  */
	public void setQtyRequired (BigDecimal QtyRequired);

	/** Get Required Quantity.
	  * Quantity required for an activity
	  */
	public BigDecimal getQtyRequired();

    /** Column name QtySpent */
    public static final String COLUMNNAME_QtySpent = "QtySpent";

	/** Set Quantity Used.
	  * Quantity used for this event
	  */
	public void setQtySpent (BigDecimal QtySpent);

	/** Get Quantity Used.
	  * Quantity used for this event
	  */
	public BigDecimal getQtySpent();

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
}
