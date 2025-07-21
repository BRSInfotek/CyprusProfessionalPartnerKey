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

import org.cyprusbrs.framework.I_AD_User;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_WorkOrderOperation
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MFG_WorkOrderOperation 
{

    /** TableName=MFG_WorkOrderOperation */
    public static final String Table_Name = "MFG_WorkOrderOperation";

    /** AD_Table_ID=1000094 */
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

    /** Column name DateProcessed */
    public static final String COLUMNNAME_DateProcessed = "DateProcessed";

	/** Set DateProcessed	  */
	public void setDateProcessed (Timestamp DateProcessed);

	/** Get DateProcessed	  */
	public Timestamp getDateProcessed();

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

    /** Column name IsOptional */
    public static final String COLUMNNAME_IsOptional = "IsOptional";

	/** Set Optional	  */
	public void setIsOptional (boolean IsOptional);

	/** Get Optional	  */
	public boolean isOptional();

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

	public I_MFG_StandardOperation getMFG_StandardOperation() throws RuntimeException;

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

    /** Column name MFG_WorkOrderOperation_ID */
    public static final String COLUMNNAME_MFG_WorkOrderOperation_ID = "MFG_WorkOrderOperation_ID";

	/** Set Operation	  */
	public void setMFG_WorkOrderOperation_ID (int MFG_WorkOrderOperation_ID);

	/** Get Operation	  */
	public int getMFG_WorkOrderOperation_ID();

    /** Column name MFG_WorkOrder_ID */
    public static final String COLUMNNAME_MFG_WorkOrder_ID = "MFG_WorkOrder_ID";

	/** Set Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID);

	/** Get Work Order	  */
	public int getMFG_WorkOrder_ID();

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException;

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

    /** Column name QtyAssembled */
    public static final String COLUMNNAME_QtyAssembled = "QtyAssembled";

	/** Set Quantity Assembled.
	  * Quantity finished at a production routing step
	  */
	public void setQtyAssembled (BigDecimal QtyAssembled);

	/** Get Quantity Assembled.
	  * Quantity finished at a production routing step
	  */
	public BigDecimal getQtyAssembled();

    /** Column name QtyQueued */
    public static final String COLUMNNAME_QtyQueued = "QtyQueued";

	/** Set Quantity Queued.
	  * Number of sub-assemblies in the Queue step of a work order operation
	  */
	public void setQtyQueued (BigDecimal QtyQueued);

	/** Get Quantity Queued.
	  * Number of sub-assemblies in the Queue step of a work order operation
	  */
	public BigDecimal getQtyQueued();

    /** Column name QtyRun */
    public static final String COLUMNNAME_QtyRun = "QtyRun";

	/** Set Quantity Run.
	  * Number of sub-assemblies in the Run step of a work order operation
	  */
	public void setQtyRun (BigDecimal QtyRun);

	/** Get Quantity Run.
	  * Number of sub-assemblies in the Run step of a work order operation
	  */
	public BigDecimal getQtyRun();

    /** Column name QtyScrapped */
    public static final String COLUMNNAME_QtyScrapped = "QtyScrapped";

	/** Set Quantity Scrapped.
	  * This is the number of sub-assemblies in the Scrap step of an operation in Work Order.
	  */
	public void setQtyScrapped (BigDecimal QtyScrapped);

	/** Get Quantity Scrapped.
	  * This is the number of sub-assemblies in the Scrap step of an operation in Work Order.
	  */
	public BigDecimal getQtyScrapped();

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

    /** Column name Supervisor_ID */
    public static final String COLUMNNAME_Supervisor_ID = "Supervisor_ID";

	/** Set Supervisor.
	  * Supervisor for this user/organization - used for escalation and approval
	  */
	public void setSupervisor_ID (int Supervisor_ID);

	/** Get Supervisor.
	  * Supervisor for this user/organization - used for escalation and approval
	  */
	public int getSupervisor_ID();

	public I_AD_User getSupervisor() throws RuntimeException;

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
