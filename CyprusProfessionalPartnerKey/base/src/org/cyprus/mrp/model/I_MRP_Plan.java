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

import org.cyprus.mfg.model.I_MFG_WorkOrderClass;
import org.cyprusbrs.framework.I_C_Calendar;
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_PriceList;
import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MRP_Plan
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MRP_Plan 
{

    /** TableName=MRP_Plan */
    public static final String Table_Name = "MRP_Plan";

    /** AD_Table_ID=1000065 */
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

    /** Column name C_Calendar_ID */
    public static final String COLUMNNAME_C_Calendar_ID = "C_Calendar_ID";

	/** Set Calendar.
	  * Accounting Calendar Name
	  */
	public void setC_Calendar_ID (int C_Calendar_ID);

	/** Get Calendar.
	  * Accounting Calendar Name
	  */
	public int getC_Calendar_ID();

	public I_C_Calendar getC_Calendar() throws RuntimeException;

    /** Column name C_DocTypeTarget_ID */
    public static final String COLUMNNAME_C_DocTypeTarget_ID = "C_DocTypeTarget_ID";

	/** Set Target Document Type.
	  * Target document type for conversing documents
	  */
	public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID);

	/** Get Target Document Type.
	  * Target document type for conversing documents
	  */
	public int getC_DocTypeTarget_ID();

	public I_C_DocType getC_DocTypeTarget() throws RuntimeException;

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

    /** Column name C_Period_To_ID */
    public static final String COLUMNNAME_C_Period_To_ID = "C_Period_To_ID";

	/** Set Period To.
	  * Ending period of a range of periods
	  */
	public void setC_Period_To_ID (int C_Period_To_ID);

	/** Get Period To.
	  * Ending period of a range of periods
	  */
	public int getC_Period_To_ID();

	public I_C_Period getC_Period_To() throws RuntimeException;

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

    /** Column name DateLastRun */
    public static final String COLUMNNAME_DateLastRun = "DateLastRun";

	/** Set Date last run.
	  * Date the process was last run.
	  */
	public void setDateLastRun (Timestamp DateLastRun);

	/** Get Date last run.
	  * Date the process was last run.
	  */
	public Timestamp getDateLastRun();

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

    /** Column name IsConsolidatePO */
    public static final String COLUMNNAME_IsConsolidatePO = "IsConsolidatePO";

	/** Set Consolidate PO	  */
	public void setIsConsolidatePO (boolean IsConsolidatePO);

	/** Get Consolidate PO	  */
	public boolean isConsolidatePO();

    /** Column name MFG_WorkOrderClass_ID */
    public static final String COLUMNNAME_MFG_WorkOrderClass_ID = "MFG_WorkOrderClass_ID";

	/** Set Work Order Class ID	  */
	public void setMFG_WorkOrderClass_ID (int MFG_WorkOrderClass_ID);

	/** Get Work Order Class ID	  */
	public int getMFG_WorkOrderClass_ID();

	public I_MFG_WorkOrderClass getMFG_WorkOrderClass() throws RuntimeException;

    /** Column name MRP_Plan_ID */
    public static final String COLUMNNAME_MRP_Plan_ID = "MRP_Plan_ID";

	/** Set Plan 	  */
	public void setMRP_Plan_ID (int MRP_Plan_ID);

	/** Get Plan 	  */
	public int getMRP_Plan_ID();

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

    /** Column name M_PriceList_ID */
    public static final String COLUMNNAME_M_PriceList_ID = "M_PriceList_ID";

	/** Set Price List.
	  * Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID);

	/** Get Price List.
	  * Unique identifier of a Price List
	  */
	public int getM_PriceList_ID();

	public I_M_PriceList getM_PriceList() throws RuntimeException;

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

    /** Column name PrioritizeOrderOverDemand */
    public static final String COLUMNNAME_PrioritizeOrderOverDemand = "PrioritizeOrderOverDemand";

	/** Set Prioritize Orders Over Demand.
	  * Firm orders are considered over demand if orders exceed demand
	  */
	public void setPrioritizeOrderOverDemand (boolean PrioritizeOrderOverDemand);

	/** Get Prioritize Orders Over Demand.
	  * Firm orders are considered over demand if orders exceed demand
	  */
	public boolean isPrioritizeOrderOverDemand();

    /** Column name PriorityImplementation */
    public static final String COLUMNNAME_PriorityImplementation = "PriorityImplementation";

	/** Set Implementation Priority.
	  * Indicates preference to either procure or manufacture a product
	  */
	public void setPriorityImplementation (String PriorityImplementation);

	/** Get Implementation Priority.
	  * Indicates preference to either procure or manufacture a product
	  */
	public String getPriorityImplementation();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

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
