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

import org.cyprus.mfg.model.I_MFG_WorkOrder;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_Order;
import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MRP_PlanResult_v
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MRP_PlanResult_v 
{

    /** TableName=MRP_PlanResult_v */
    public static final String Table_Name = "MRP_PlanResult_v";

    /** AD_Table_ID=1000086 */
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

    /** Column name C_Order_ID */
    public static final String COLUMNNAME_C_Order_ID = "C_Order_ID";

	/** Set Order.
	  * Order
	  */
	public void setC_Order_ID (int C_Order_ID);

	/** Get Order.
	  * Order
	  */
	public int getC_Order_ID();

	public I_C_Order getC_Order() throws RuntimeException;

    /** Column name C_Period_ID */
    public static final String COLUMNNAME_C_Period_ID = "C_Period_ID";

	/** Set Period.
	  * Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID);

	/** Get Period.
	  * Period of the Calendar
	  */
	public int getC_Period_ID();

	public I_C_Period getC_Period() throws RuntimeException;

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

    /** Column name DateOrdered */
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";

	/** Set Date Ordered.
	  * Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered);

	/** Get Date Ordered.
	  * Date of Order
	  */
	public Timestamp getDateOrdered();

    /** Column name DemandDateRequired */
    public static final String COLUMNNAME_DemandDateRequired = "DemandDateRequired";

	/** Set Demand Date Required.
	  * Indicates by when a product is required to satisfy demand.
	  */
	public void setDemandDateRequired (Timestamp DemandDateRequired);

	/** Get Demand Date Required.
	  * Indicates by when a product is required to satisfy demand.
	  */
	public Timestamp getDemandDateRequired();

    /** Column name ExpectedReceipt */
    public static final String COLUMNNAME_ExpectedReceipt = "ExpectedReceipt";

	/** Set Expected Receipt.
	  * Product quantity expected to be received into inventory
	  */
	public void setExpectedReceipt (BigDecimal ExpectedReceipt);

	/** Get Expected Receipt.
	  * Product quantity expected to be received into inventory
	  */
	public BigDecimal getExpectedReceipt();

    /** Column name GrossRequirement */
    public static final String COLUMNNAME_GrossRequirement = "GrossRequirement";

	/** Set Gross Requirement.
	  * Product quantity required after exploding the master demand
	  */
	public void setGrossRequirement (BigDecimal GrossRequirement);

	/** Get Gross Requirement.
	  * Product quantity required after exploding the master demand
	  */
	public BigDecimal getGrossRequirement();

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

    /** Column name IsImplemented */
    public static final String COLUMNNAME_IsImplemented = "IsImplemented";

	/** Set Implemented.
	  * Indicates if the order has been released
	  */
	public void setIsImplemented (boolean IsImplemented);

	/** Get Implemented.
	  * Indicates if the order has been released
	  */
	public boolean isImplemented();

    /** Column name LevelNo */
    public static final String COLUMNNAME_LevelNo = "LevelNo";

	/** Set Level no	  */
	public void setLevelNo (int LevelNo);

	/** Get Level no	  */
	public int getLevelNo();

    /** Column name MFG_WorkOrder_ID */
    public static final String COLUMNNAME_MFG_WorkOrder_ID = "MFG_WorkOrder_ID";

	/** Set Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID);

	/** Get Work Order	  */
	public int getMFG_WorkOrder_ID();

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException;

    /** Column name MRP_PlanRun_ID */
    public static final String COLUMNNAME_MRP_PlanRun_ID = "MRP_PlanRun_ID";

	/** Set Plan Run	  */
	public void setMRP_PlanRun_ID (int MRP_PlanRun_ID);

	/** Get Plan Run	  */
	public int getMRP_PlanRun_ID();

	public I_MRP_PlanRun getMRP_PlanRun() throws RuntimeException;

    /** Column name MRP_Plan_ID */
    public static final String COLUMNNAME_MRP_Plan_ID = "MRP_Plan_ID";

	/** Set Plan ID	  */
	public void setMRP_Plan_ID (int MRP_Plan_ID);

	/** Get Plan ID	  */
	public int getMRP_Plan_ID();

	public I_MRP_Plan getMRP_Plan() throws RuntimeException;

    /** Column name MRP_PlannedDemand_ID */
    public static final String COLUMNNAME_MRP_PlannedDemand_ID = "MRP_PlannedDemand_ID";

	/** Set Planned Demand ID	  */
	public void setMRP_PlannedDemand_ID (int MRP_PlannedDemand_ID);

	/** Get Planned Demand ID	  */
	public int getMRP_PlannedDemand_ID();

	public I_MRP_PlannedDemand getMRP_PlannedDemand() throws RuntimeException;

    /** Column name MRP_PlannedOrder_ID */
    public static final String COLUMNNAME_MRP_PlannedOrder_ID = "MRP_PlannedOrder_ID";

	/** Set Planned Order.
	  * Recommended orders calculated by the plan engine
	  */
	public void setMRP_PlannedOrder_ID (int MRP_PlannedOrder_ID);

	/** Get Planned Order.
	  * Recommended orders calculated by the plan engine
	  */
	public int getMRP_PlannedOrder_ID();

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

    /** Column name NetRequirement */
    public static final String COLUMNNAME_NetRequirement = "NetRequirement";

	/** Set Net Requirement.
	  * Product quantity calculated by the plan engine required to satisfy demand
	  */
	public void setNetRequirement (BigDecimal NetRequirement);

	/** Get Net Requirement.
	  * Product quantity calculated by the plan engine required to satisfy demand
	  */
	public BigDecimal getNetRequirement();

    /** Column name OrderType */
    public static final String COLUMNNAME_OrderType = "OrderType";

	/** Set Order Type.
	  * Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public void setOrderType (String OrderType);

	/** Get Order Type.
	  * Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public String getOrderType();

    /** Column name Order_Min */
    public static final String COLUMNNAME_Order_Min = "Order_Min";

	/** Set Minimum Order Qty.
	  * Minimum order quantity in UOM
	  */
	public void setOrder_Min (BigDecimal Order_Min);

	/** Get Minimum Order Qty.
	  * Minimum order quantity in UOM
	  */
	public BigDecimal getOrder_Min();

    /** Column name ParentProductID */
    public static final String COLUMNNAME_ParentProductID = "ParentProductID";

	/** Set Parent Product.
	  * Immediate parent product of the component
	  */
	public void setParentProductID (int ParentProductID);

	/** Get Parent Product.
	  * Immediate parent product of the component
	  */
	public int getParentProductID();

	public I_M_Product getParentProduc() throws RuntimeException;

    /** Column name PlannedAvailability */
    public static final String COLUMNNAME_PlannedAvailability = "PlannedAvailability";

	/** Set Planned Availability.
	  * Product quantity expected to be available as calculated by the plan engine
	  */
	public void setPlannedAvailability (BigDecimal PlannedAvailability);

	/** Get Planned Availability.
	  * Product quantity expected to be available as calculated by the plan engine
	  */
	public BigDecimal getPlannedAvailability();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name ProductLevel */
    public static final String COLUMNNAME_ProductLevel = "ProductLevel";

	/** Set Product Level.
	  * Product Level Indented
	  */
	public void setProductLevel (String ProductLevel);

	/** Get Product Level.
	  * Product Level Indented
	  */
	public String getProductLevel();

    /** Column name QtyOrdered */
    public static final String COLUMNNAME_QtyOrdered = "QtyOrdered";

	/** Set Ordered Quantity.
	  * Ordered Quantity
	  */
	public void setQtyOrdered (BigDecimal QtyOrdered);

	/** Get Ordered Quantity.
	  * Ordered Quantity
	  */
	public BigDecimal getQtyOrdered();

    /** Column name RootProductID */
    public static final String COLUMNNAME_RootProductID = "RootProductID";

	/** Set Root Product.
	  * Product Assembly at the Root (Level 0)
	  */
	public void setRootProductID (int RootProductID);

	/** Get Root Product.
	  * Product Assembly at the Root (Level 0)
	  */
	public int getRootProductID();

	public I_M_Product getRootProduc() throws RuntimeException;

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
