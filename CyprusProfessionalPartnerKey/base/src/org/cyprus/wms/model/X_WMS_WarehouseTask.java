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
/** Generated Model - DO NOT CHANGE */
package org.cyprus.wms.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

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
import org.cyprusbrs.framework.I_Persistent;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.Env;

/** Generated Model for WMS_WarehouseTask
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus - $Id$ */
public class X_WMS_WarehouseTask extends PO implements I_WMS_WarehouseTask, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211210L;

    /** Standard Constructor */
    public X_WMS_WarehouseTask (Properties ctx, int WMS_WarehouseTask_ID, String trxName)
    {
      super (ctx, WMS_WarehouseTask_ID, trxName);
      /** if (WMS_WarehouseTask_ID == 0)
        {
			setC_UOM_ID (0);
			setDocAction (null);
			setDocumentNo (null);
			setM_Warehouse_ID (0);
			setMovementDate (new Timestamp( System.currentTimeMillis() ));
			setQtyEntered (Env.ZERO);
			setTargetQty (Env.ZERO);
			setWMS_WarehouseTask_ID (0);
        } */
    }

    /** Load Constructor */
    public X_WMS_WarehouseTask (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_WMS_WarehouseTask[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_AD_User getAD_User() throws RuntimeException
    {
		return (I_AD_User)MTable.get(getCtx(), I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Approval Amount.
		@param ApprovalAmt 
		Document Approval Amount
	  */
	public void setApprovalAmt (BigDecimal ApprovalAmt)
	{
		set_Value (COLUMNNAME_ApprovalAmt, ApprovalAmt);
	}

	/** Get Approval Amount.
		@return Document Approval Amount
	  */
	public BigDecimal getApprovalAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ApprovalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_C_DocType getC_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_OrderLine getC_OrderLine() throws RuntimeException
    {
		return (I_C_OrderLine)MTable.get(getCtx(), I_C_OrderLine.Table_Name)
			.getPO(getC_OrderLine_ID(), get_TrxName());	}

	/** Set Sales Order Line.
		@param C_OrderLine_ID 
		Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID)
	{
		if (C_OrderLine_ID < 1) 
			set_Value (COLUMNNAME_C_OrderLine_ID, null);
		else 
			set_Value (COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
	}

	/** Get Sales Order Line.
		@return Sales Order Line
	  */
	public int getC_OrderLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_UOM getC_UOM() throws RuntimeException
    {
		return (I_C_UOM)MTable.get(getCtx(), I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Drafted = D */
//	public static final String DOCSTATUS_Drafted = "D";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set Approved.
		@param IsApproved 
		Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved)
	{
		set_Value (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved () 
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_MFG_WorkOrderComponent getMFG_WorkOrderComponent() throws RuntimeException
    {
		return (I_MFG_WorkOrderComponent)MTable.get(getCtx(), I_MFG_WorkOrderComponent.Table_Name)
			.getPO(getMFG_WorkOrderComponent_ID(), get_TrxName());	}

	/** Set Work Order Component ID.
		@param MFG_WorkOrderComponent_ID Work Order Component ID	  */
	public void setMFG_WorkOrderComponent_ID (int MFG_WorkOrderComponent_ID)
	{
		if (MFG_WorkOrderComponent_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderComponent_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderComponent_ID, Integer.valueOf(MFG_WorkOrderComponent_ID));
	}

	/** Get Work Order Component ID.
		@return Work Order Component ID	  */
	public int getMFG_WorkOrderComponent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderComponent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrderTrxLine getMFG_WorkOrderTrxLine() throws RuntimeException
    {
		return (I_MFG_WorkOrderTrxLine)MTable.get(getCtx(), I_MFG_WorkOrderTrxLine.Table_Name)
			.getPO(getMFG_WorkOrderTrxLine_ID(), get_TrxName());	}

	/** Set Work Order Transaction Line ID.
		@param MFG_WorkOrderTrxLine_ID Work Order Transaction Line ID	  */
	public void setMFG_WorkOrderTrxLine_ID (int MFG_WorkOrderTrxLine_ID)
	{
		if (MFG_WorkOrderTrxLine_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrderTrxLine_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrderTrxLine_ID, Integer.valueOf(MFG_WorkOrderTrxLine_ID));
	}

	/** Get Work Order Transaction Line ID.
		@return Work Order Transaction Line ID	  */
	public int getMFG_WorkOrderTrxLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrderTrxLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException
    {
		return (I_MFG_WorkOrder)MTable.get(getCtx(), I_MFG_WorkOrder.Table_Name)
			.getPO(getMFG_WorkOrder_ID(), get_TrxName());	}

	/** Set Work Order.
		@param MFG_WorkOrder_ID Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID)
	{
		if (MFG_WorkOrder_ID < 1) 
			set_Value (COLUMNNAME_MFG_WorkOrder_ID, null);
		else 
			set_Value (COLUMNNAME_MFG_WorkOrder_ID, Integer.valueOf(MFG_WorkOrder_ID));
	}

	/** Get Work Order.
		@return Work Order	  */
	public int getMFG_WorkOrder_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MFG_WorkOrder_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}

	/** Set Attribute Set Instance.
		@param M_AttributeSetInstance_ID 
		Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0) 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Set Instance.
		@return Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_InOutLine getM_InOutLine() throws RuntimeException
    {
		return (I_M_InOutLine)MTable.get(getCtx(), I_M_InOutLine.Table_Name)
			.getPO(getM_InOutLine_ID(), get_TrxName());	}

	/** Set Shipment/Receipt Line.
		@param M_InOutLine_ID 
		Line on Shipment or Receipt document
	  */
	public void setM_InOutLine_ID (int M_InOutLine_ID)
	{
		if (M_InOutLine_ID < 1) 
			set_Value (COLUMNNAME_M_InOutLine_ID, null);
		else 
			set_Value (COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
	}

	/** Get Shipment/Receipt Line.
		@return Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOutLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_LocatorTo() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_LocatorTo_ID(), get_TrxName());	}

	/** Set Locator To.
		@param M_LocatorTo_ID 
		Location inventory is moved to
	  */
	public void setM_LocatorTo_ID (int M_LocatorTo_ID)
	{
		if (M_LocatorTo_ID < 1) 
			set_Value (COLUMNNAME_M_LocatorTo_ID, null);
		else 
			set_Value (COLUMNNAME_M_LocatorTo_ID, Integer.valueOf(M_LocatorTo_ID));
	}

	/** Get Locator To.
		@return Location inventory is moved to
	  */
	public int getM_LocatorTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_LocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_Locator() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_Locator_ID(), get_TrxName());	}

	/** Set Locator.
		@param M_Locator_ID 
		Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1) 
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else 
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Product getM_Product() throws RuntimeException
    {
		return (I_M_Product)MTable.get(getCtx(), I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (I_M_Warehouse)MTable.get(getCtx(), I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1) 
			set_Value (COLUMNNAME_M_Warehouse_ID, null);
		else 
			set_Value (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Date.
		@param MovementDate 
		Date a product was moved in or out of inventory
	  */
	public void setMovementDate (Timestamp MovementDate)
	{
		set_Value (COLUMNNAME_MovementDate, MovementDate);
	}

	/** Get Movement Date.
		@return Date a product was moved in or out of inventory
	  */
	public Timestamp getMovementDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_MovementDate);
	}

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Quantity Dedicated.
		@param QtyDedicated 
		Quantity for which there is a pending Warehouse Task
	  */
	public void setQtyDedicated (BigDecimal QtyDedicated)
	{
		set_Value (COLUMNNAME_QtyDedicated, QtyDedicated);
	}

	/** Get Quantity Dedicated.
		@return Quantity for which there is a pending Warehouse Task
	  */
	public BigDecimal getQtyDedicated () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyDedicated);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity.
		@param QtyEntered 
		The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_Value (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Suggested Quantity.
		@param QtySuggested 
		Quantity suggested for Pick or Putaway by the Putaway or Pick process
	  */
	public void setQtySuggested (BigDecimal QtySuggested)
	{
		set_Value (COLUMNNAME_QtySuggested, QtySuggested);
	}

	/** Get Suggested Quantity.
		@return Quantity suggested for Pick or Putaway by the Putaway or Pick process
	  */
	public BigDecimal getQtySuggested () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtySuggested);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Split Task.
		@param SplitTask 
		Split Warehouse Task into two tasks
	  */
	public void setSplitTask (String SplitTask)
	{
		set_Value (COLUMNNAME_SplitTask, SplitTask);
	}

	/** Get Split Task.
		@return Split Warehouse Task into two tasks
	  */
	public String getSplitTask () 
	{
		return (String)get_Value(COLUMNNAME_SplitTask);
	}

	/** Set Target Quantity.
		@param TargetQty 
		Target Movement Quantity
	  */
	public void setTargetQty (BigDecimal TargetQty)
	{
		set_Value (COLUMNNAME_TargetQty, TargetQty);
	}

	/** Get Target Quantity.
		@return Target Movement Quantity
	  */
	public BigDecimal getTargetQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TargetQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Warehouse Task.
		@param WMS_WarehouseTask_ID 
		A Warehouse Task represents a basic warehouse operation such as putaway, picking or replenishment.
	  */
	public void setWMS_WarehouseTask_ID (int WMS_WarehouseTask_ID)
	{
		if (WMS_WarehouseTask_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_WMS_WarehouseTask_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_WMS_WarehouseTask_ID, Integer.valueOf(WMS_WarehouseTask_ID));
	}

	/** Get Warehouse Task.
		@return A Warehouse Task represents a basic warehouse operation such as putaway, picking or replenishment.
	  */
	public int getWMS_WarehouseTask_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_WarehouseTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_WaveLine getWMS_WaveLine() throws RuntimeException
    {
		return (I_WMS_WaveLine)MTable.get(getCtx(), I_WMS_WaveLine.Table_Name)
			.getPO(getWMS_WaveLine_ID(), get_TrxName());	}

	/** Set Wave Line.
		@param WMS_WaveLine_ID 
		Selected order lines for which there is sufficient onhand quantity in the warehouse
	  */
	public void setWMS_WaveLine_ID (int WMS_WaveLine_ID)
	{
		if (WMS_WaveLine_ID < 1) 
			set_Value (COLUMNNAME_WMS_WaveLine_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_WaveLine_ID, Integer.valueOf(WMS_WaveLine_ID));
	}

	/** Get Wave Line.
		@return Selected order lines for which there is sufficient onhand quantity in the warehouse
	  */
	public int getWMS_WaveLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_WaveLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	public I_M_AttributeSetInstance getM_ActualASI() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_ActualASI_ID(), get_TrxName());	}

	/** Set Actual Attribute Set Instance.
		@param M_ActualASI_ID 
		Product Attribute Set Instance actually used for the warehouse task
	  */
	public void setM_ActualASI_ID (int M_ActualASI_ID)
	{
		if (M_ActualASI_ID < 1) 
			set_Value (COLUMNNAME_M_ActualASI_ID, null);
		else 
			set_Value (COLUMNNAME_M_ActualASI_ID, Integer.valueOf(M_ActualASI_ID));
	}

	/** Get Actual Attribute Set Instance.
		@return Product Attribute Set Instance actually used for the warehouse task
	  */
	public int getM_ActualASI_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ActualASI_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
	public I_M_Locator getM_ActualLocatorTo() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_ActualLocatorTo_ID(), get_TrxName());	}

	/** Set Actual Destination Locator.
		@param M_ActualLocatorTo_ID 
		Actual locator where the stock was moved to
	  */
	public void setM_ActualLocatorTo_ID (int M_ActualLocatorTo_ID)
	{
		if (M_ActualLocatorTo_ID < 1) 
			set_Value (COLUMNNAME_M_ActualLocatorTo_ID, null);
		else 
			set_Value (COLUMNNAME_M_ActualLocatorTo_ID, Integer.valueOf(M_ActualLocatorTo_ID));
	}

	/** Get Actual Destination Locator.
		@return Actual locator where the stock was moved to
	  */
	public int getM_ActualLocatorTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ActualLocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_ActualLocator() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_ActualLocator_ID(), get_TrxName());	}

	/** Set Actual Source Locator.
		@param M_ActualLocator_ID 
		Actual locator from where the stock was moved
	  */
	public void setM_ActualLocator_ID (int M_ActualLocator_ID)
	{
		if (M_ActualLocator_ID < 1) 
			set_Value (COLUMNNAME_M_ActualLocator_ID, null);
		else 
			set_Value (COLUMNNAME_M_ActualLocator_ID, Integer.valueOf(M_ActualLocator_ID));
	}

	/** Get Actual Source Locator.
		@return Actual locator from where the stock was moved
	  */
	public int getM_ActualLocator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_ActualLocator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_WMS_WarehouseTask getWMS_SplitWarehouseTask() throws RuntimeException
    {
		return (I_WMS_WarehouseTask)MTable.get(getCtx(), I_WMS_WarehouseTask.Table_Name)
			.getPO(getWMS_SplitWarehouseTask_ID(), get_TrxName());	}

	/** Set Split Warehouse Task.
		@param WMS_SplitWarehouseTask_ID 
		Warehouse Task that this task was split from
	  */
	public void setWMS_SplitWarehouseTask_ID (int WMS_SplitWarehouseTask_ID)
	{
		if (WMS_SplitWarehouseTask_ID < 1) 
			set_Value (COLUMNNAME_WMS_SplitWarehouseTask_ID, null);
		else 
			set_Value (COLUMNNAME_WMS_SplitWarehouseTask_ID, Integer.valueOf(WMS_SplitWarehouseTask_ID));
	}

	/** Get Split Warehouse Task.
		@return Warehouse Task that this task was split from
	  */
	public int getWMS_SplitWarehouseTask_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WMS_SplitWarehouseTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
	
}