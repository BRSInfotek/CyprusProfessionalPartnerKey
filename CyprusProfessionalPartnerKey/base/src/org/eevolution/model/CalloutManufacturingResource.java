/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2007 e-Evolution,SC. All Rights Reserved.               *
 * Contributor(s): Victor Perez www.e-evolution.com                           *
 *****************************************************************************/
//package org.compiere.mfg.model;
package org.eevolution.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MOperation;
import org.cyprusbrs.framework.MResource;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.wf.MWorkflow;


/**
 * BOM Callouts
 *	
 * @author Victor Perez www.e-evolution.com
 * @author Teo Sarca, www.arhipac.ro
 * 			<li>BF [ 2820743 ] CalloutBOM - apply ABP
 * 				https://sourceforge.net/tracker/?func=detail&aid=2820743&group_id=176962&atid=934929  
 */
public class CalloutManufacturingResource extends CalloutEngine
{
	/**
	 *	Parent cycle check and BOM Line defaults.
	 *  @param ctx      Context
	 *  @param WindowNo current Window No
	 *  @param mTab     Model Tab
	 *  @param mField   Model Field
	 *  @param value    The new value
	 */
	
	// callout path : org.eevolution.model.CalloutManufacturingResource.setPlannedCost
	
	/**
     * Set Planned Cost from selected Activity
     */
    public String setPlannedCost(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) {
            return "";
        }
        Integer activityID = (Integer) value;
        if (activityID <= 0) {
            return "";
        }
        // Fetch the Planned Cost from the C_Activity
        BigDecimal plannedCost = DB.getSQLValueBD(
            null, 
            "SELECT PlannedCost FROM C_Activity WHERE C_Activity_ID = ?", 
            activityID
        );

        if (plannedCost == null) {
            plannedCost = Env.ZERO;
        }
        // Set Planned Cost on M_PlannedActivity
        mTab.setValue("PlannedCost", plannedCost);

        return "";
    }
    
    /**
     * Set Name and Description from selected Operation
     * // callout path : org.eevolution.model.CalloutManufacturingResource.setOperation
     */
    public String setOperation(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) {
            return "";
        }
        Integer operationID = (Integer) value;
        if (operationID <= 0) {
            return "";
        }
        // Fetch Name and Description from M_Operation
        MOperation op=new MOperation(Env.getCtx(),operationID, null);
        if(op!=null)
        {
        	mTab.setValue("Name", op.getName());
        	mTab.setValue("Value", op.getValue());
            mTab.setValue("Description", op.getDescription());            
        }
        return "";
    }
    
    /**
     * Set Name and Description from selected Operation
     * // callout path : org.eevolution.model.CalloutManufacturingResource.setMFGResource
     */
    public String setMFGResource(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) {
            return "";
        }
        Integer sResourceID  = (Integer) value;
        if (sResourceID <= 0) {
            return "";
        }
        // Fetch Name and Description from M_Operation
        MResource mr=new MResource(Env.getCtx(),sResourceID, null);
        if(mr!=null)
        {
            mTab.setValue("Description", mr.getDescription());
            if(mr.get_ValueAsInt("AD_User_ID")>0)
            mTab.setValue("AD_User_ID", mr.get_ValueAsInt("AD_User_ID"));
            else
            mTab.setValue("AD_User_ID", null);

            if(mr.get_ValueAsInt("A_Asset_ID")>0)
            mTab.setValue("A_Asset_ID", mr.get_ValueAsInt("A_Asset_ID"));
            else
            mTab.setValue("A_Asset_ID", null);

        }
        return "";
    }
    
    /**
     * Set Name and Description from selected Operation
     * // callout path : org.eevolution.model.CalloutManufacturingResource.setMWorkflow
     */
    public String setMWorkflow(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) {
            return "";
        }
        Integer AD_Workflow_ID  = (Integer) value;
        if (AD_Workflow_ID <= 0) {
            return "";
        }
        // Fetch Name and Description from M_Operation
        MWorkflow mw=new MWorkflow(Env.getCtx(),AD_Workflow_ID, null);
        if(mw!=null)
        {
            mTab.setValue("Description", mw.getDescription());
            if(mw.get_ValueAsInt("M_Product_ID")>0)
            mTab.setValue("M_Product_ID", mw.get_ValueAsInt("M_Product_ID"));
            else
            mTab.setValue("M_Product_ID", null);

            if(mw.get_ValueAsInt("PP_Product_BOM_ID")>0)
            mTab.setValue("PP_Product_BOM_ID", mw.get_ValueAsInt("PP_Product_BOM_ID"));
            else
            mTab.setValue("PP_Product_BOM_ID", null);
            
            if(mw.get_ValueAsInt("M_AttributeSetInstance_ID")>0)
            mTab.setValue("M_AttributeSetInstance_ID", mw.get_ValueAsInt("M_AttributeSetInstance_ID"));
            else
            mTab.setValue("M_AttributeSetInstance_ID", null);

        }
        return "";
    }

}	//	CalloutManufacturingResource

