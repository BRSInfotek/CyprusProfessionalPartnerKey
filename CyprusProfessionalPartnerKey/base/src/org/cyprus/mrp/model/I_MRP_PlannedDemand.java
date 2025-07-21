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

import org.cyprusbrs.framework.I_C_Period;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.I_M_Product;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for MRP_PlannedDemand
 *  @author Adempiere (generated) 
 *  @version Release 1.1 Supported By Cyprus
 */
public interface I_MRP_PlannedDemand 
{

    /** TableName=MRP_PlannedDemand */
    public static final String Table_Name = "MRP_PlannedDemand";

    /** AD_Table_ID=1000091 */
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

    /** Column name DateRequired */
    public static final String COLUMNNAME_DateRequired = "DateRequired";

	/** Set Date Required.
	  * Date when required
	  */
	public void setDateRequired (Timestamp DateRequired);

	/** Get Date Required.
	  * Date when required
	  */
	public Timestamp getDateRequired();

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

    /** Column name LeadTime */
    public static final String COLUMNNAME_LeadTime = "LeadTime";

	/** Set Lead Time.
	  * Lead Time
	  */
	public void setLeadTime (BigDecimal LeadTime);

	/** Get Lead Time.
	  * Lead Time
	  */
	public BigDecimal getLeadTime();

    /** Column name LevelNo */
    public static final String COLUMNNAME_LevelNo = "LevelNo";

	/** Set Level no	  */
	public void setLevelNo (int LevelNo);

	/** Get Level no	  */
	public int getLevelNo();

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

    /** Column name MRP_PlannedDemand_Parent_ID */
    public static final String COLUMNNAME_MRP_PlannedDemand_Parent_ID = "MRP_PlannedDemand_Parent_ID";

	/** Set Planned Demand Parent.
	  * Planned Demand of the Immediate Parent Product
	  */
	public void setMRP_PlannedDemand_Parent_ID (int MRP_PlannedDemand_Parent_ID);

	/** Get Planned Demand Parent.
	  * Planned Demand of the Immediate Parent Product
	  */
	public int getMRP_PlannedDemand_Parent_ID();

	public I_MRP_PlannedDemand getMRP_PlannedDemand_Parent() throws RuntimeException;

    /** Column name MRP_PlannedDemand_Root_ID */
    public static final String COLUMNNAME_MRP_PlannedDemand_Root_ID = "MRP_PlannedDemand_Root_ID";

	/** Set Planned Demand Root.
	  * Planned Demand of the Root Product (Top Most in the Tree)
	  */
	public void setMRP_PlannedDemand_Root_ID (int MRP_PlannedDemand_Root_ID);

	/** Get Planned Demand Root.
	  * Planned Demand of the Root Product (Top Most in the Tree)
	  */
	public int getMRP_PlannedDemand_Root_ID();

	public I_MRP_PlannedDemand getMRP_PlannedDemand_Root() throws RuntimeException;

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

    /** Column name RunStatus */
    public static final String COLUMNNAME_RunStatus = "RunStatus";

	/** Set Run Status.
	  * Plan Run Status
	  */
	public void setRunStatus (String RunStatus);

	/** Get Run Status.
	  * Plan Run Status
	  */
	public String getRunStatus();

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo (BigDecimal SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public BigDecimal getSeqNo();

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
