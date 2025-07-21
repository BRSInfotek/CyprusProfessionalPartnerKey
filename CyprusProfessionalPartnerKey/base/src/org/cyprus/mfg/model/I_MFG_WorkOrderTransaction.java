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
import org.cyprusbrs.framework.I_C_Activity;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_BPartner_Location;
import org.cyprusbrs.framework.I_C_Campaign;
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_ElementValue;
import org.cyprusbrs.framework.I_C_Project;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_WorkOrderTransaction
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MFG_WorkOrderTransaction 
{

    /** TableName=MFG_WorkOrderTransaction */
    public static final String Table_Name = "MFG_WorkOrderTransaction";

    /** AD_Table_ID=1000097 */
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

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

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

    /** Column name AD_User_ID */
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Set User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID);

	/** Get User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID();

	public I_AD_User getAD_User() throws RuntimeException;

    /** Column name C_Activity_ID */
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";

	/** Set Activity.
	  * Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID);

	/** Get Activity.
	  * Business Activity
	  */
	public int getC_Activity_ID();

	public I_C_Activity getC_Activity() throws RuntimeException;

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

    /** Column name C_BPartner_Location_ID */
    public static final String COLUMNNAME_C_BPartner_Location_ID = "C_BPartner_Location_ID";

	/** Set Partner Location.
	  * Identifies the (ship to) address for this Business Partner
	  */
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID);

	/** Get Partner Location.
	  * Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID();

	public I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException;

    /** Column name C_Campaign_ID */
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";

	/** Set Campaign.
	  * Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID);

	/** Get Campaign.
	  * Marketing Campaign
	  */
	public int getC_Campaign_ID();

	public I_C_Campaign getC_Campaign() throws RuntimeException;

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public I_C_DocType getC_DocType() throws RuntimeException;

    /** Column name C_Project_ID */
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";

	/** Set Project.
	  * Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID);

	/** Get Project.
	  * Financial Project
	  */
	public int getC_Project_ID();

	public I_C_Project getC_Project() throws RuntimeException;

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

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name DateTrx */
    public static final String COLUMNNAME_DateTrx = "DateTrx";

	/** Set Transaction Date.
	  * Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx);

	/** Get Transaction Date.
	  * Transaction Date
	  */
	public Timestamp getDateTrx();

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

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocStatus */
    public static final String COLUMNNAME_DocStatus = "DocStatus";

	/** Set Document Status.
	  * The current status of the document
	  */
	public void setDocStatus (String DocStatus);

	/** Get Document Status.
	  * The current status of the document
	  */
	public String getDocStatus();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name GenerateLines */
    public static final String COLUMNNAME_GenerateLines = "GenerateLines";

	/** Set GenerateLines	  */
	public void setGenerateLines (String GenerateLines);

	/** Get GenerateLines	  */
	public String getGenerateLines();

    /** Column name GenerateResourceLines */
    public static final String COLUMNNAME_GenerateResourceLines = "GenerateResourceLines";

	/** Set Generate Resource Usage Lines.
	  * Generate resource usage lines for manually charged resources
	  */
	public void setGenerateResourceLines (String GenerateResourceLines);

	/** Get Generate Resource Usage Lines.
	  * Generate resource usage lines for manually charged resources
	  */
	public String getGenerateResourceLines();

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

    /** Column name IsApproved */
    public static final String COLUMNNAME_IsApproved = "IsApproved";

	/** Set Approved.
	  * Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved);

	/** Get Approved.
	  * Indicates if this document requires approval
	  */
	public boolean isApproved();

    /** Column name IsOptionalFrom */
    public static final String COLUMNNAME_IsOptionalFrom = "IsOptionalFrom";

	/** Set Optional.
	  * Indicates if the Operation From in the Work Order Move Transaction is an optional operation
	  */
	public void setIsOptionalFrom (boolean IsOptionalFrom);

	/** Get Optional.
	  * Indicates if the Operation From in the Work Order Move Transaction is an optional operation
	  */
	public boolean isOptionalFrom();

    /** Column name IsOptionalTo */
    public static final String COLUMNNAME_IsOptionalTo = "IsOptionalTo";

	/** Set Optional.
	  * Indicates if the Operation To in Work Order Move Transaction is an optional operation
	  */
	public void setIsOptionalTo (boolean IsOptionalTo);

	/** Get Optional.
	  * Indicates if the Operation To in Work Order Move Transaction is an optional operation
	  */
	public boolean isOptionalTo();

    /** Column name MFG_WORKORDERTRANSACTION_ID */
    public static final String COLUMNNAME_MFG_WORKORDERTRANSACTION_ID = "MFG_WORKORDERTRANSACTION_ID";

	/** Set MFG_WORKORDERTRANSACTION_ID	  */
	public void setMFG_WORKORDERTRANSACTION_ID (int MFG_WORKORDERTRANSACTION_ID);

	/** Get MFG_WORKORDERTRANSACTION_ID	  */
	public int getMFG_WORKORDERTRANSACTION_ID();

    /** Column name MFG_WorkOrder_ID */
    public static final String COLUMNNAME_MFG_WorkOrder_ID = "MFG_WorkOrder_ID";

	/** Set Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID);

	/** Get Work Order	  */
	public int getMFG_WorkOrder_ID();

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException;

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

    /** Column name OperationFrom_ID */
    public static final String COLUMNNAME_OperationFrom_ID = "OperationFrom_ID";

	/** Set Operation From.
	  * Process the operations in a work order transaction starting at this one.
	  */
	public void setOperationFrom_ID (int OperationFrom_ID);

	/** Get Operation From.
	  * Process the operations in a work order transaction starting at this one.
	  */
	public int getOperationFrom_ID();

	public I_MFG_WorkOrderOperation getOperationFrom() throws RuntimeException;

    /** Column name OperationTo_ID */
    public static final String COLUMNNAME_OperationTo_ID = "OperationTo_ID";

	/** Set Operation To.
	  * Process the operations in a work order transaction ending at this one (inclusive).
	  */
	public void setOperationTo_ID (int OperationTo_ID);

	/** Get Operation To.
	  * Process the operations in a work order transaction ending at this one (inclusive).
	  */
	public int getOperationTo_ID();

	public I_MFG_WorkOrderOperation getOperationTo() throws RuntimeException;

    /** Column name ParentWorkOrderTxn_ID */
    public static final String COLUMNNAME_ParentWorkOrderTxn_ID = "ParentWorkOrderTxn_ID";

	/** Set Parent Work Order Transaction.
	  * Work Order Transaction that created this Work Order Transaction
	  */
	public void setParentWorkOrderTxn_ID (int ParentWorkOrderTxn_ID);

	/** Get Parent Work Order Transaction.
	  * Work Order Transaction that created this Work Order Transaction
	  */
	public int getParentWorkOrderTxn_ID();

	public I_MFG_WorkOrderTransaction getParentWorkOrderTxn() throws RuntimeException;

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

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name QtyEntered */
    public static final String COLUMNNAME_QtyEntered = "QtyEntered";

	/** Set Quantity.
	  * The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered);

	/** Get Quantity.
	  * The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered();

    /** Column name StepFrom */
    public static final String COLUMNNAME_StepFrom = "StepFrom";

	/** Set Step From.
	  * The source intra-operation step from which the work order movement is being made.
	  */
	public void setStepFrom (String StepFrom);

	/** Get Step From.
	  * The source intra-operation step from which the work order movement is being made.
	  */
	public String getStepFrom();

    /** Column name StepTo */
    public static final String COLUMNNAME_StepTo = "StepTo";

	/** Set Step To.
	  * The destination intra-operation step to which the work order movement is being done.
	  */
	public void setStepTo (String StepTo);

	/** Get Step To.
	  * The destination intra-operation step to which the work order movement is being done.
	  */
	public String getStepTo();

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

    /** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public I_C_ElementValue getUser1() throws RuntimeException;

    /** Column name User2_ID */
    public static final String COLUMNNAME_User2_ID = "User2_ID";

	/** Set User List 2.
	  * User defined list element #2
	  */
	public void setUser2_ID (int User2_ID);

	/** Get User List 2.
	  * User defined list element #2
	  */
	public int getUser2_ID();

	public I_C_ElementValue getUser2() throws RuntimeException;

    /** Column name WOComplete */
    public static final String COLUMNNAME_WOComplete = "WOComplete";

	/** Set Complete this Assembly.
	  * Indicates that a move transaction should include a completion for the assembly.
	  */
	public void setWOComplete (boolean WOComplete);

	/** Get Complete this Assembly.
	  * Indicates that a move transaction should include a completion for the assembly.
	  */
	public boolean isWOComplete();

    /** Column name WOTxnSource */
    public static final String COLUMNNAME_WOTxnSource = "WOTxnSource";

	/** Set Transaction Source.
	  * Indicates where the work order transaction originated.
	  */
	public void setWOTxnSource (String WOTxnSource);

	/** Get Transaction Source.
	  * Indicates where the work order transaction originated.
	  */
	public String getWOTxnSource();

    /** Column name WorkOrderTxnType */
    public static final String COLUMNNAME_WorkOrderTxnType = "WorkOrderTxnType";

	/** Set Transaction Type.
	  * Transaction Type
	  */
	public void setWorkOrderTxnType (String WorkOrderTxnType);

	/** Get Transaction Type.
	  * Transaction Type
	  */
	public String getWorkOrderTxnType();
}
