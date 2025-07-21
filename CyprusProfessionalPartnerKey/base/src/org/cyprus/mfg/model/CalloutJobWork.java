package org.cyprus.mfg.model;

import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MJobOrder;
import org.cyprusbrs.framework.MJobOrderLine;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.Env;

public class CalloutJobWork  extends CalloutEngine{

	//Callout Path : org.cyprus.mfg.model.CalloutJobWork.setFromJobWorkOrder
	 public String setFromJobWorkOrder(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
		    if (value == null)
		      return ""; 
		    int C_JobOrder_ID = ((Integer)mTab.getValue("C_JobOrder_ID")).intValue();
		    if (0 == C_JobOrder_ID)
		      return ""; 
		    MJobOrder jobOrder = new MJobOrder(Env.getCtx(), C_JobOrder_ID, null);
		      mTab.setValue("C_BPartner_ID", jobOrder.getC_BPartner_ID());
		      mTab.setValue("C_BPartner_Location_ID", jobOrder.getC_BPartner_Location_ID());
		      mTab.setValue("AD_User_ID", jobOrder.getAD_User_ID());
		      mTab.setValue("M_Warehouse_ID", jobOrder.getM_Warehouse_ID());
		      mTab.setValue("PriorityRule", jobOrder.getPriority());
		    return "";
		  }
	 
	//Callout Path : org.cyprus.mfg.model.CalloutJobWork.setFromJobWorkOrderLine
		 public String setFromJobWorkOrderLine(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
			    if (value == null)
			      return ""; 
			    int C_JobOrderLine_ID = ((Integer)mTab.getValue("C_JobOrderLine_ID")).intValue();
			    if (0 == C_JobOrderLine_ID)
			      return ""; 
			    MJobOrderLine jobOrderLine = new MJobOrderLine(Env.getCtx(), C_JobOrderLine_ID, null);
			      mTab.setValue("M_Product_ID", jobOrderLine.getM_Product_ID());
			      mTab.setValue("C_Charge_ID", jobOrderLine.getC_Charge_ID());
			      mTab.setValue("M_AttributeSetInstance_ID", jobOrderLine.getM_AttributeSetInstance_ID());
			      mTab.setValue("C_UOM_ID", jobOrderLine.getC_UOM_ID());
			      mTab.setValue("QtyEntered", jobOrderLine.getQtyEntered());
			    return "";
			  }

		//Callout Path : org.cyprus.mfg.model.CalloutJobWork.setFromDocumentType
		 public String setFromDocumentType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
			    if (value == null)
			      return ""; 
			    int C_DocType_ID = ((Integer)mTab.getValue("C_DocType_ID")).intValue();
			    if (0 == C_DocType_ID)
			      return ""; 
			    MDocType dt = new MDocType(Env.getCtx(), C_DocType_ID, null);
			      mTab.setValue("IsJobWork", dt.get_Value("IsJobWork"));
			    return "";
			  }
}
