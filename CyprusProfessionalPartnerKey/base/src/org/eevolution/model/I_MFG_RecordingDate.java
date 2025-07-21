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
package org.eevolution.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cyprusbrs.framework.I_AD_Workflow;
import org.cyprusbrs.framework.I_M_Operation;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_RecordingDate
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP
 */
public interface I_MFG_RecordingDate 
{

    /** TableName=MFG_RecordingDate */
    public static final String Table_Name = "MFG_RecordingDate";

    /** AD_Table_ID=1000079 */
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

    /** Column name AD_Workflow_ID */
    public static final String COLUMNNAME_AD_Workflow_ID = "AD_Workflow_ID";

	/** Set Workflow.
	  * Workflow or combination of tasks
	  */
	public void setAD_Workflow_ID (int AD_Workflow_ID);

	/** Get Workflow.
	  * Workflow or combination of tasks
	  */
	public int getAD_Workflow_ID();

	public I_AD_Workflow getAD_Workflow() throws RuntimeException;

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

    /** Column name DateRecording */
    public static final String COLUMNNAME_DateRecording = "DateRecording";

	/** Set Recording Date	  */
	public void setDateRecording (Timestamp DateRecording);

	/** Get Recording Date	  */
	public Timestamp getDateRecording();

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

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name M_Operation_ID */
    public static final String COLUMNNAME_M_Operation_ID = "M_Operation_ID";

	/** Set Operation	  */
	public void setM_Operation_ID (int M_Operation_ID);

	/** Get Operation	  */
	public int getM_Operation_ID();

	public I_M_Operation getM_Operation() throws RuntimeException;

    /** Column name MFG_RecordingDate_ID */
    public static final String COLUMNNAME_MFG_RecordingDate_ID = "MFG_RecordingDate_ID";

	/** Set Recording Date ID	  */
	public void setMFG_RecordingDate_ID (int MFG_RecordingDate_ID);

	/** Get Recording Date ID	  */
	public int getMFG_RecordingDate_ID();

    /** Column name MFG_Shift */
    public static final String COLUMNNAME_MFG_Shift = "MFG_Shift";

	/** Set Shift	  */
	public void setMFG_Shift (String MFG_Shift);

	/** Get Shift	  */
	public String getMFG_Shift();

    /** Column name MFG_TimeRecording_ID */
    public static final String COLUMNNAME_MFG_TimeRecording_ID = "MFG_TimeRecording_ID";

	/** Set Time Recording ID	  */
	public void setMFG_TimeRecording_ID (int MFG_TimeRecording_ID);

	/** Get Time Recording ID	  */
	public int getMFG_TimeRecording_ID();

	public I_MFG_TimeRecording getMFG_TimeRecording() throws RuntimeException;

    /** Column name PP_Order_Node_ID */
    public static final String COLUMNNAME_PP_Order_Node_ID = "PP_Order_Node_ID";

	/** Set Manufacturing Order Activity.
	  * Workflow Node (activity), step or process
	  */
	public void setPP_Order_Node_ID (int PP_Order_Node_ID);

	/** Get Manufacturing Order Activity.
	  * Workflow Node (activity), step or process
	  */
	public int getPP_Order_Node_ID();

	public org.eevolution.model.I_PP_Order_Node getPP_Order_Node() throws RuntimeException;

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

    /** Column name ProcessNow */
    public static final String COLUMNNAME_ProcessNow = "ProcessNow";

	/** Set Process Now	  */
	public void setProcessNow (String ProcessNow);

	/** Get Process Now	  */
	public String getProcessNow();

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
