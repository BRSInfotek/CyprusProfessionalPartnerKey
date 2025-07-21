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

import org.cyprusbrs.framework.I_C_AcctSchema;
import org.cyprusbrs.framework.I_C_Activity;
import org.cyprusbrs.framework.I_C_BPartner;
import org.cyprusbrs.framework.I_C_BPartner_Location;
import org.cyprusbrs.framework.I_C_Campaign;
import org.cyprusbrs.framework.I_C_Currency;
import org.cyprusbrs.framework.I_C_ElementValue;
import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_Project;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_BOM;
import org.cyprusbrs.framework.I_M_CostElement;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.framework.I_M_Warehouse;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MFG_WorkOrderValueDetail
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MFG_WorkOrderValueDetail 
{

    /** TableName=MFG_WorkOrderValueDetail */
    public static final String Table_Name = "MFG_WorkOrderValueDetail";

    /** AD_Table_ID=1000133 */
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

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

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

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public I_C_Currency getC_Currency() throws RuntimeException;

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

    /** Column name MATERIALIN */
    public static final String COLUMNNAME_MATERIALIN = "MATERIALIN";

	/** Set MATERIALIN	  */
	public void setMATERIALIN (BigDecimal MATERIALIN);

	/** Get MATERIALIN	  */
	public BigDecimal getMATERIALIN();

    /** Column name MATERIALOUT */
    public static final String COLUMNNAME_MATERIALOUT = "MATERIALOUT";

	/** Set MATERIALOUT	  */
	public void setMATERIALOUT (BigDecimal MATERIALOUT);

	/** Get MATERIALOUT	  */
	public BigDecimal getMATERIALOUT();

    /** Column name MATERIALOVERHDIN */
    public static final String COLUMNNAME_MATERIALOVERHDIN = "MATERIALOVERHDIN";

	/** Set MATERIALOVERHDIN	  */
	public void setMATERIALOVERHDIN (BigDecimal MATERIALOVERHDIN);

	/** Get MATERIALOVERHDIN	  */
	public BigDecimal getMATERIALOVERHDIN();

    /** Column name MATERIALOVERHDOUT */
    public static final String COLUMNNAME_MATERIALOVERHDOUT = "MATERIALOVERHDOUT";

	/** Set MATERIALOVERHDOUT	  */
	public void setMATERIALOVERHDOUT (BigDecimal MATERIALOVERHDOUT);

	/** Get MATERIALOVERHDOUT	  */
	public BigDecimal getMATERIALOVERHDOUT();

    /** Column name MATERIALOVERHDVARIANCE */
    public static final String COLUMNNAME_MATERIALOVERHDVARIANCE = "MATERIALOVERHDVARIANCE";

	/** Set MATERIALOVERHDVARIANCE	  */
	public void setMATERIALOVERHDVARIANCE (BigDecimal MATERIALOVERHDVARIANCE);

	/** Get MATERIALOVERHDVARIANCE	  */
	public BigDecimal getMATERIALOVERHDVARIANCE();

    /** Column name MATERIALVARIANCE */
    public static final String COLUMNNAME_MATERIALVARIANCE = "MATERIALVARIANCE";

	/** Set MATERIALVARIANCE	  */
	public void setMATERIALVARIANCE (BigDecimal MATERIALVARIANCE);

	/** Get MATERIALVARIANCE	  */
	public BigDecimal getMATERIALVARIANCE();

    /** Column name MFG_Routing_ID */
    public static final String COLUMNNAME_MFG_Routing_ID = "MFG_Routing_ID";

	/** Set Routing.
	  * Routing for an assembly
	  */
	public void setMFG_Routing_ID (int MFG_Routing_ID);

	/** Get Routing.
	  * Routing for an assembly
	  */
	public int getMFG_Routing_ID();

	public I_MFG_Routing getMFG_Routing() throws RuntimeException;

    /** Column name MFG_WORKORDERVALUEDETAIL_ID */
    public static final String COLUMNNAME_MFG_WORKORDERVALUEDETAIL_ID = "MFG_WORKORDERVALUEDETAIL_ID";

	/** Set MFG_WORKORDERVALUEDETAIL_ID	  */
	public void setMFG_WORKORDERVALUEDETAIL_ID (int MFG_WORKORDERVALUEDETAIL_ID);

	/** Get MFG_WORKORDERVALUEDETAIL_ID	  */
	public int getMFG_WORKORDERVALUEDETAIL_ID();

    /** Column name MFG_WorkOrderClass_ID */
    public static final String COLUMNNAME_MFG_WorkOrderClass_ID = "MFG_WorkOrderClass_ID";

	/** Set Work Order Class ID	  */
	public void setMFG_WorkOrderClass_ID (int MFG_WorkOrderClass_ID);

	/** Get Work Order Class ID	  */
	public int getMFG_WorkOrderClass_ID();

	public I_MFG_WorkOrderClass getMFG_WorkOrderClass() throws RuntimeException;

    /** Column name MFG_WorkOrderOperation_ID */
    public static final String COLUMNNAME_MFG_WorkOrderOperation_ID = "MFG_WorkOrderOperation_ID";

	/** Set Operation	  */
	public void setMFG_WorkOrderOperation_ID (int MFG_WorkOrderOperation_ID);

	/** Get Operation	  */
	public int getMFG_WorkOrderOperation_ID();

	public I_MFG_WorkOrderOperation getMFG_WorkOrderOperation() throws RuntimeException;

    /** Column name MFG_WorkOrderTransaction_ID */
    public static final String COLUMNNAME_MFG_WorkOrderTransaction_ID = "MFG_WorkOrderTransaction_ID";

	/** Set MFG_WorkOrderTransaction_ID	  */
	public void setMFG_WorkOrderTransaction_ID (int MFG_WorkOrderTransaction_ID);

	/** Get MFG_WorkOrderTransaction_ID	  */
	public int getMFG_WorkOrderTransaction_ID();

	public I_MFG_WorkOrderTransaction getMFG_WorkOrderTransaction() throws RuntimeException;

    /** Column name MFG_WorkOrderValue_ID */
    public static final String COLUMNNAME_MFG_WorkOrderValue_ID = "MFG_WorkOrderValue_ID";

	/** Set MFG_WorkOrderValue_ID	  */
	public void setMFG_WorkOrderValue_ID (int MFG_WorkOrderValue_ID);

	/** Get MFG_WorkOrderValue_ID	  */
	public int getMFG_WorkOrderValue_ID();

	public I_MFG_WorkOrderValue getMFG_WorkOrderValue() throws RuntimeException;

    /** Column name MFG_WorkOrder_ID */
    public static final String COLUMNNAME_MFG_WorkOrder_ID = "MFG_WorkOrder_ID";

	/** Set Work Order	  */
	public void setMFG_WorkOrder_ID (int MFG_WorkOrder_ID);

	/** Get Work Order	  */
	public int getMFG_WorkOrder_ID();

	public I_MFG_WorkOrder getMFG_WorkOrder() throws RuntimeException;

    /** Column name M_BOM_ID */
    public static final String COLUMNNAME_M_BOM_ID = "M_BOM_ID";

	/** Set BOM.
	  * Bill of Material
	  */
	public void setM_BOM_ID (int M_BOM_ID);

	/** Get BOM.
	  * Bill of Material
	  */
	public int getM_BOM_ID();

	public I_M_BOM getM_BOM() throws RuntimeException;

    /** Column name M_CostElement_ID */
    public static final String COLUMNNAME_M_CostElement_ID = "M_CostElement_ID";

	/** Set Cost Element.
	  * Product Cost Element
	  */
	public void setM_CostElement_ID (int M_CostElement_ID);

	/** Get Cost Element.
	  * Product Cost Element
	  */
	public int getM_CostElement_ID();

	public I_M_CostElement getM_CostElement() throws RuntimeException;

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

    /** Column name OVERHDIN */
    public static final String COLUMNNAME_OVERHDIN = "OVERHDIN";

	/** Set OVERHDIN	  */
	public void setOVERHDIN (BigDecimal OVERHDIN);

	/** Get OVERHDIN	  */
	public BigDecimal getOVERHDIN();

    /** Column name OVERHDOUT */
    public static final String COLUMNNAME_OVERHDOUT = "OVERHDOUT";

	/** Set OVERHDOUT	  */
	public void setOVERHDOUT (BigDecimal OVERHDOUT);

	/** Get OVERHDOUT	  */
	public BigDecimal getOVERHDOUT();

    /** Column name OVERHDVARIANCE */
    public static final String COLUMNNAME_OVERHDVARIANCE = "OVERHDVARIANCE";

	/** Set OVERHDVARIANCE	  */
	public void setOVERHDVARIANCE (BigDecimal OVERHDVARIANCE);

	/** Get OVERHDVARIANCE	  */
	public BigDecimal getOVERHDVARIANCE();

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

    /** Column name QtyAvailable */
    public static final String COLUMNNAME_QtyAvailable = "QtyAvailable";

	/** Set Available Quantity.
	  * Available Quantity (On Hand - Reserved)
	  */
	public void setQtyAvailable (BigDecimal QtyAvailable);

	/** Get Available Quantity.
	  * Available Quantity (On Hand - Reserved)
	  */
	public BigDecimal getQtyAvailable();

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

    /** Column name RESOURCEIN */
    public static final String COLUMNNAME_RESOURCEIN = "RESOURCEIN";

	/** Set RESOURCEIN	  */
	public void setRESOURCEIN (BigDecimal RESOURCEIN);

	/** Get RESOURCEIN	  */
	public BigDecimal getRESOURCEIN();

    /** Column name RESOURCEOUT */
    public static final String COLUMNNAME_RESOURCEOUT = "RESOURCEOUT";

	/** Set RESOURCEOUT	  */
	public void setRESOURCEOUT (BigDecimal RESOURCEOUT);

	/** Get RESOURCEOUT	  */
	public BigDecimal getRESOURCEOUT();

    /** Column name RESOURCEVARIANCE */
    public static final String COLUMNNAME_RESOURCEVARIANCE = "RESOURCEVARIANCE";

	/** Set RESOURCEVARIANCE	  */
	public void setRESOURCEVARIANCE (BigDecimal RESOURCEVARIANCE);

	/** Get RESOURCEVARIANCE	  */
	public BigDecimal getRESOURCEVARIANCE();

    /** Column name SCRAPVALUE */
    public static final String COLUMNNAME_SCRAPVALUE = "SCRAPVALUE";

	/** Set SCRAPVALUE	  */
	public void setSCRAPVALUE (BigDecimal SCRAPVALUE);

	/** Get SCRAPVALUE	  */
	public BigDecimal getSCRAPVALUE();

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
}
