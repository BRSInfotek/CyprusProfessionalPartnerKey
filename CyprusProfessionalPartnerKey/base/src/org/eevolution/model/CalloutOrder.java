package org.eevolution.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.cyprus.model.GridTabWrapper;
import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOMConversion;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.wf.MWorkflow;

public class CalloutOrder extends CalloutEngine{

	private boolean steps = false;
	  
	  public String qty(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
	    if (value == null)
	      return ""; 
	    int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");
	    if (this.steps)
	      this.log.warning("qty - init - M_Product_ID=" + M_Product_ID + " - "); 
	    BigDecimal QtyOrdered = Env.ZERO;
	    BigDecimal QtyEntered = Env.ZERO;
	    if (M_Product_ID == 0) {
	      QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
	      mTab.setValue("QtyOrdered", QtyEntered);
	    } else if (mField.getColumnName().equals("C_UOM_ID")) {
	      int C_UOM_To_ID = ((Integer)value).intValue();
	      QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
	      QtyOrdered = MUOMConversion.convertProductFrom(ctx, M_Product_ID, 
	          C_UOM_To_ID, QtyEntered);
	      if (QtyOrdered == null)
	        QtyOrdered = QtyEntered; 
	      boolean conversion = (QtyEntered.compareTo(QtyOrdered) != 0);
	      Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
	      mTab.setValue("QtyOrdered", QtyOrdered);
	    } else if (mField.getColumnName().equals("QtyEntered")) {
	      int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
	      QtyEntered = (BigDecimal)value;
	      QtyOrdered = MUOMConversion.convertProductFrom(ctx, M_Product_ID, 
	          C_UOM_To_ID, QtyEntered);
	      if (QtyOrdered == null)
	        QtyOrdered = QtyEntered; 
	      boolean conversion = (QtyEntered.compareTo(QtyOrdered) != 0);
	      this.log.fine("qty - UOM=" + C_UOM_To_ID + 
	          ", QtyEntered=" + QtyEntered + 
	          " -> " + conversion + 
	          " QtyOrdered=" + QtyOrdered);
	      Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
	      mTab.setValue("QtyOrdered", QtyOrdered);
	    } else if (mField.getColumnName().equals("QtyOrdered")) {
	      int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");
	      QtyOrdered = (BigDecimal)value;
	      QtyEntered = MUOMConversion.convertProductTo(ctx, M_Product_ID, 
	          C_UOM_To_ID, QtyOrdered);
	      if (QtyEntered == null)
	        QtyEntered = QtyOrdered; 
	      boolean conversion = (QtyOrdered.compareTo(QtyEntered) != 0);
	      this.log.fine("qty - UOM=" + C_UOM_To_ID + 
	          ", QtyOrdered=" + QtyOrdered + 
	          " -> " + conversion + 
	          " QtyEntered=" + QtyEntered);
	      Env.setContext(ctx, WindowNo, "UOMConversion", conversion ? "Y" : "N");
	      mTab.setValue("QtyEntered", QtyEntered);
	    } 
	    return qtyBatch(ctx, WindowNo, mTab, mField, value);
	  }
	  
	  public String qtyBatch(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
	    I_PP_Order order = (I_PP_Order)GridTabWrapper.create(mTab, I_PP_Order.class);
	    MPPOrder.updateQtyBatchs(ctx, order, true);
	    return "";
	  }
	  
	  public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
	    if (isCalloutActive())
	      return ""; 
	    I_PP_Order order = (I_PP_Order)GridTabWrapper.create(mTab, I_PP_Order.class);
	    MProduct product = MProduct.get(ctx, order.getM_Product_ID());
	    if (product == null)
	      return ""; 
	    order.setC_UOM_ID(product.getC_UOM_ID());
	    I_PP_Product_Planning pp = getPP_Product_Planning(ctx, order);
	    order.setAD_Workflow_ID(pp.getAD_Workflow_ID());
	    order.setPP_Product_BOM_ID(pp.getPP_Product_BOM_ID());
	    if (pp.getPP_Product_BOM_ID() > 0) {
	      I_PP_Product_BOM bom = pp.getPP_Product_BOM();
	      order.setC_UOM_ID(bom.getC_UOM_ID());
	    } 
	    MPPOrder.updateQtyBatchs(ctx, order, true);
	    return "";
	  }
	  
	  protected static I_PP_Product_Planning getPP_Product_Planning(Properties ctx, I_PP_Order order) {
	    MPPProductPlanning mPPProductPlanning = MPPProductPlanning.find(ctx, 
	        order.getAD_Org_ID(), order.getM_Warehouse_ID(), 
	        order.getS_Resource_ID(), order.getM_Product_ID(), 
	        null);
	    if (mPPProductPlanning == null) {
	      mPPProductPlanning = new MPPProductPlanning(ctx, 0, null);
	      mPPProductPlanning.setAD_Org_ID(order.getAD_Org_ID());
	      mPPProductPlanning.setM_Warehouse_ID(order.getM_Warehouse_ID());
	      mPPProductPlanning.setS_Resource_ID(order.getS_Resource_ID());
	      mPPProductPlanning.setM_Product_ID(order.getM_Product_ID());
	    } 
	    MProduct product = MProduct.get(ctx, mPPProductPlanning.getM_Product_ID());
	    if (mPPProductPlanning.getAD_Workflow_ID() <= 0)
	      mPPProductPlanning.setAD_Workflow_ID(MWorkflow.getWorkflowSearchKey(product)); 
	    if (mPPProductPlanning.getPP_Product_BOM_ID() <= 0) {
	      MPPProductBOM mPPProductBOM = MPPProductBOM.getDefault(product, null);
	      if (mPPProductBOM != null)
	        mPPProductPlanning.setPP_Product_BOM_ID(mPPProductBOM.getPP_Product_BOM_ID()); 
	    } 
	    return (I_PP_Product_Planning)mPPProductPlanning;
	  }
	
}
