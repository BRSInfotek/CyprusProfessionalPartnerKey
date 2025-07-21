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
import org.cyprusbrs.framework.I_C_DocType;
import org.cyprusbrs.framework.I_C_Order;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MRP_PlannedOrder
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MRP_PlannedOrder 
{

    /** TableName=MRP_PlannedOrder */
    public static final String Table_Name = "MRP_PlannedOrder";

    /** AD_Table_ID=1000132 */
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

    /** Column name MRP_PlannedOrder_Parent_ID */
    public static final String COLUMNNAME_MRP_PlannedOrder_Parent_ID = "MRP_PlannedOrder_Parent_ID";

	/** Set Planned Order Parent.
	  * Parent Order of a Planned Order
	  */
	public void setMRP_PlannedOrder_Parent_ID (int MRP_PlannedOrder_Parent_ID);

	/** Get Planned Order Parent.
	  * Parent Order of a Planned Order
	  */
	public int getMRP_PlannedOrder_Parent_ID();

	public I_MRP_PlannedOrder getMRP_PlannedOrder_Parent() throws RuntimeException;

    /** Column name MRP_PlannedOrder_Root_ID */
    public static final String COLUMNNAME_MRP_PlannedOrder_Root_ID = "MRP_PlannedOrder_Root_ID";

	/** Set Planned Order Root.
	  * Root Order of a Planned Order
	  */
	public void setMRP_PlannedOrder_Root_ID (int MRP_PlannedOrder_Root_ID);

	/** Get Planned Order Root.
	  * Root Order of a Planned Order
	  */
	public int getMRP_PlannedOrder_Root_ID();

	public I_MRP_PlannedOrder getMRP_PlannedOrder_Root() throws RuntimeException;

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

    /** Column name PlannedOrderStatus */
    public static final String COLUMNNAME_PlannedOrderStatus = "PlannedOrderStatus";

	/** Set Planned Order Status.
	  * Indicates if an order has been submitted or not
	  */
	public void setPlannedOrderStatus (String PlannedOrderStatus);

	/** Get Planned Order Status.
	  * Indicates if an order has been submitted or not
	  */
	public String getPlannedOrderStatus();

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
