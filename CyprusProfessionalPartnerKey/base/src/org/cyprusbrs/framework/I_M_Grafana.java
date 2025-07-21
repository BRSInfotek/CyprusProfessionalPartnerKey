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
package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cyprusbrs.model.MTable;
import org.cyprusbrs.util.KeyNamePair;

/** Generated Interface for M_Grafana
 *  @author Adempiere (generated) 
 *  @version Release 1.0 Supported By Cyprus ERP
 */
public interface I_M_Grafana 
{

    /** TableName=M_Grafana */
    public static final String Table_Name = "M_Grafana";

    /** AD_Table_ID=1000105 */
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

    /** Column name DashboardType */
    public static final String COLUMNNAME_DashboardType = "DashboardType";

	/** Set I-Frame Sequence	  */
	public void setDashboardType (String DashboardType);

	/** Get I-Frame Sequence	  */
	public String getDashboardType();

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

    /** Column name DisplaySequence */
    public static final String COLUMNNAME_DisplaySequence = "DisplaySequence";

	/** Set Display Sequence.
	  * Format for Display Sequence
	  */
	public void setDisplaySequence (int DisplaySequence);

	/** Get Display Sequence.
	  * Format for Display Sequence
	  */
	public int getDisplaySequence();

    /** Column name Height */
    public static final String COLUMNNAME_Height = "Height";

	/** Set Height	  */
	public void setHeight (int Height);

	/** Get Height	  */
	public int getHeight();

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

    /** Column name M_Grafana_ID */
    public static final String COLUMNNAME_M_Grafana_ID = "M_Grafana_ID";

	/** Set Grafana ID	  */
	public void setM_Grafana_ID (int M_Grafana_ID);

	/** Get Grafana ID	  */
	public int getM_Grafana_ID();

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

    /** Column name RefreshTimeInSec */
    public static final String COLUMNNAME_RefreshTimeInSec = "RefreshTimeInSec";

	/** Set Refresh Time (In seconds)	  */
	public void setRefreshTimeInSec (int RefreshTimeInSec);

	/** Get Refresh Time (In seconds)	  */
	public int getRefreshTimeInSec();

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

    /** Column name URL */
    public static final String COLUMNNAME_URL = "URL";

	/** Set Grafana Report URL.
	  * Full URL address - e.g. http://www.cypruserp.com
	  */
	public void setURL (String URL);

	/** Get Grafana Report URL.
	  * Full URL address - e.g. http://www.cypruserp.com
	  */
	public String getURL();

    /** Column name Width */
    public static final String COLUMNNAME_Width = "Width";

	/** Set Width	  */
	public void setWidth (int Width);

	/** Get Width	  */
	public int getWidth();
}
