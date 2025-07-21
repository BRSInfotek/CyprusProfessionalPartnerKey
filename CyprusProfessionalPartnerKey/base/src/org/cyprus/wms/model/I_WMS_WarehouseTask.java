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

import org.cyprus.mfg.model.I_MFG_WorkOrder;
import org.cyprus.mfg.model.I_MFG_WorkOrderComponent;
import org.cyprus.mfg.model.I_MFG_WorkOrderTrxLine;
import org.cyprusbrs.framework.I_AD_User;
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_OrderLine;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_AttributeSetInstance;
import org.cyprusbrs.framework.I_M_InOutLine;
import org.cyprusbrs.framework.I_M_Locator;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for WMS_WarehouseTask
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_WMS_WarehouseTask 
{

    /** TableName=WMS_WarehouseTask */
    public static final String Table_Name = "WMS_WarehouseTask";

    /** AD_Table_ID=1000115 */
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

	 /** Column name M_ActualLocatorTo_ID */
    public static final String COLUMNNAME_M_ActualLocatorTo_ID = "M_ActualLocatorTo_ID";

	/** Set Actual Destination Locator.
	  * Actual locator where the stock was moved to
	  */
	public void setM_ActualLocatorTo_ID (int M_ActualLocatorTo_ID);

	/** Get Actual Destination Locator.
	  * Actual locator where the stock was moved to
	  */
	public int getM_ActualLocatorTo_ID();

	public I_M_Locator getM_ActualLocatorTo() throws RuntimeException;

    /** Column name M_ActualLocator_ID */
    public static final String COLUMNNAME_M_ActualLocator_ID = "M_ActualLocator_ID";

	/** Set Actual Source Locator.
	  * Actual locator from where the stock was moved
	  */
	public void setM_ActualLocator_ID (int M_ActualLocator_ID);

	/** Get Actual Source Locator.
	  * Actual locator from where the stock was moved
	  */
	public int getM_ActualLocator_ID();

	public I_M_Locator getM_ActualLocator() throws RuntimeException;

	
	  public static final String COLUMNNAME_M_ActualASI_ID = "M_ActualASI_ID";

		/** Set Actual Attribute Set Instance.
		  * Product Attribute Set Instance actually used for the warehouse task
		  */
		public void setM_ActualASI_ID (int M_ActualASI_ID);

		/** Get Actual Attribute Set Instance.
		  * Product Attribute Set Instance actually used for the warehouse task
		  */
		public int getM_ActualASI_ID();

		public I_M_AttributeSetInstance getM_ActualASI() throws RuntimeException;

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

    /** Column name ApprovalAmt */
    public static final String COLUMNNAME_ApprovalAmt = "ApprovalAmt";

	/** Set Approval Amount.
	  * Document Approval Amount
	  */
	public void setApprovalAmt (BigDecimal ApprovalAmt);

	/** Get Approval Amount.
	  * Document Approval Amount
	  */
	public BigDecimal getApprovalAmt();

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

    /** Column name C_OrderLine_ID */
    public static final String COLUMNNAME_C_OrderLine_ID = "C_OrderLine_ID";

	/** Set Sales Order Line.
	  * Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID);

	/** Get Sales Order Line.
	  * Sales Order Line
	  */
	public int getC_OrderLine_ID();

	public I_C_OrderLine getC_OrderLine() throws RuntimeException;

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

    /** Column name MFG_WorkOrderComponent_ID */
    public static final String COLUMNNAME_MFG_WorkOrderComponent_ID = "MFG_WorkOrderComponent_ID";

	/** Set Work Order Component ID	  */
	public void setMFG_WorkOrderComponent_ID (int MFG_WorkOrderComponent_ID);

	/** Get Work Order Component ID	  */
	public int getMFG_WorkOrderComponent_ID();

	public I_MFG_WorkOrderComponent getMFG_WorkOrderComponent() throws RuntimeException;

    /** Column name MFG_WorkOrderTrxLine_ID */
    public static final String COLUMNNAME_MFG_WorkOrderTrxLine_ID = "MFG_WorkOrderTrxLine_ID";

	/** Set Work Order Transaction Line ID	  */
	public void setMFG_WorkOrderTrxLine_ID (int MFG_WorkOrderTrxLine_ID);

	/** Get Work Order Transaction Line ID	  */
	public int getMFG_WorkOrderTrxLine_ID();

	public I_MFG_WorkOrderTrxLine getMFG_WorkOrderTrxLine() throws RuntimeException;

    /** Column name MFG_WorkOrder_ID */
    public static final String COLUMNNAME_MFG_WorkOrder_ID = "MFG_WorkOrder_ID";

	/** Set Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID);

	/** Get Work Order	  */
	public int getMFG_WorkOrder_ID();

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException;

    /** Column name M_AttributeSetInstance_ID */
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";

	/** Set Attribute Set Instance.
	  * Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID);

	/** Get Attribute Set Instance.
	  * Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID();

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;

    /** Column name M_InOutLine_ID */
    public static final String COLUMNNAME_M_InOutLine_ID = "M_InOutLine_ID";

	/** Set Shipment/Receipt Line.
	  * Line on Shipment or Receipt document
	  */
	public void setM_InOutLine_ID (int M_InOutLine_ID);

	/** Get Shipment/Receipt Line.
	  * Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID();

	public I_M_InOutLine getM_InOutLine() throws RuntimeException;

    /** Column name M_LocatorTo_ID */
    public static final String COLUMNNAME_M_LocatorTo_ID = "M_LocatorTo_ID";

	/** Set Locator To.
	  * Location inventory is moved to
	  */
	public void setM_LocatorTo_ID (int M_LocatorTo_ID);

	/** Get Locator To.
	  * Location inventory is moved to
	  */
	public int getM_LocatorTo_ID();

	public I_M_Locator getM_LocatorTo() throws RuntimeException;

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

    /** Column name MovementDate */
    public static final String COLUMNNAME_MovementDate = "MovementDate";

	/** Set Movement Date.
	  * Date a product was moved in or out of inventory
	  */
	public void setMovementDate (Timestamp MovementDate);

	/** Get Movement Date.
	  * Date a product was moved in or out of inventory
	  */
	public Timestamp getMovementDate();

    /** Column name MovementQty */
    public static final String COLUMNNAME_MovementQty = "MovementQty";

	/** Set Movement Quantity.
	  * Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty);

	/** Get Movement Quantity.
	  * Quantity of a product moved.
	  */
	public BigDecimal getMovementQty();

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

    /** Column name QtyDedicated */
    public static final String COLUMNNAME_QtyDedicated = "QtyDedicated";

	/** Set Quantity Dedicated.
	  * Quantity for which there is a pending Warehouse Task
	  */
	public void setQtyDedicated (BigDecimal QtyDedicated);

	/** Get Quantity Dedicated.
	  * Quantity for which there is a pending Warehouse Task
	  */
	public BigDecimal getQtyDedicated();

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

    /** Column name QtySuggested */
    public static final String COLUMNNAME_QtySuggested = "QtySuggested";

	/** Set Suggested Quantity.
	  * Quantity suggested for Pick or Putaway by the Putaway or Pick process
	  */
	public void setQtySuggested (BigDecimal QtySuggested);

	/** Get Suggested Quantity.
	  * Quantity suggested for Pick or Putaway by the Putaway or Pick process
	  */
	public BigDecimal getQtySuggested();

    /** Column name SplitTask */
    public static final String COLUMNNAME_SplitTask = "SplitTask";

	/** Set Split Task.
	  * Split Warehouse Task into two tasks
	  */
	public void setSplitTask (String SplitTask);

	/** Get Split Task.
	  * Split Warehouse Task into two tasks
	  */
	public String getSplitTask();

    /** Column name TargetQty */
    public static final String COLUMNNAME_TargetQty = "TargetQty";

	/** Set Target Quantity.
	  * Target Movement Quantity
	  */
	public void setTargetQty (BigDecimal TargetQty);

	/** Get Target Quantity.
	  * Target Movement Quantity
	  */
	public BigDecimal getTargetQty();

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

    /** Column name WMS_WarehouseTask_ID */
    public static final String COLUMNNAME_WMS_WarehouseTask_ID = "WMS_WarehouseTask_ID";

	/** Set Warehouse Task.
	  * A Warehouse Task represents a basic warehouse operation such as putaway, picking or replenishment.
	  */
	public void setWMS_WarehouseTask_ID (int WMS_WarehouseTask_ID);

	/** Get Warehouse Task.
	  * A Warehouse Task represents a basic warehouse operation such as putaway, picking or replenishment.
	  */
	public int getWMS_WarehouseTask_ID();

    /** Column name WMS_WaveLine_ID */
    public static final String COLUMNNAME_WMS_WaveLine_ID = "WMS_WaveLine_ID";

	/** Set Wave Line.
	  * Selected order lines for which there is sufficient onhand quantity in the warehouse
	  */
	public void setWMS_WaveLine_ID (int WMS_WaveLine_ID);

	/** Get Wave Line.
	  * Selected order lines for which there is sufficient onhand quantity in the warehouse
	  */
	public int getWMS_WaveLine_ID();

	public I_WMS_WaveLine getWMS_WaveLine() throws RuntimeException;
	
	 /** Column name WMS_SplitWarehouseTask_ID */
    public static final String COLUMNNAME_WMS_SplitWarehouseTask_ID = "WMS_SplitWarehouseTask_ID";

	/** Set Split Warehouse Task.
	  * Warehouse Task that this task was split from
	  */
	public void setWMS_SplitWarehouseTask_ID (int WMS_SplitWarehouseTask_ID);

	/** Get Split Warehouse Task.
	  * Warehouse Task that this task was split from
	  */
	public int getWMS_SplitWarehouseTask_ID();

	public I_WMS_WarehouseTask getWMS_SplitWarehouseTask() throws RuntimeException;
}
