package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprus.exceptions.AdempiereException;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class CalloutJobWorkOrder extends CalloutEngine {

	// org.compiere.model.CalloutJobWorkOrder.convertQuantity
    // Method triggered when C_UOM field changes
    public String convertQuantity(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) return "";

        int C_UOM_To_ID = (Integer) value;
        int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");
        BigDecimal qty = (BigDecimal) mTab.getValue("QtyEntered");
        int C_JobOrderLine_ID = Env.getContextAsInt(ctx, WindowNo, "C_JobOrderLine_ID"); // Get Job Order Line ID

        if (M_Product_ID == 0 || qty == null) return "";

        // Fetch Conversion Rate
        BigDecimal convertedQty = MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, qty);

        if (convertedQty != null) {
            mTab.setValue("QtyConverted", convertedQty);
            if(C_JobOrderLine_ID>0)
            updateComponentQuantities(ctx, C_JobOrderLine_ID, convertedQty);
        }

        return "";
    }

	// org.compiere.model.CalloutJobWorkOrder.quantity

 // This callout triggers when Quantity is changed
    public String quantity(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
        if (value == null) return "";

        int C_UOM_To_ID = Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID"); // Get selected UOM
        int M_Product_ID = Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");
        BigDecimal qty = (BigDecimal) mTab.getValue("QtyEntered"); // Get Quantity
        int C_JobOrderLine_ID = Env.getContextAsInt(ctx, WindowNo, "C_JobOrderLine_ID"); // Get Job Order Line ID

        if (M_Product_ID == 0 || qty == null) return "";

        // Fetch Conversion Rate
        BigDecimal convertedQty = MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, qty);

        if (convertedQty != null) {
            mTab.setValue("QtyConverted", convertedQty);
            if(C_JobOrderLine_ID>0)
            updateComponentQuantities(ctx, C_JobOrderLine_ID, convertedQty);
        }

        return "";
    }
    
    
    // Method to update all related component quantities
    private void updateComponentQuantities(Properties ctx, int C_JobOrderLine_ID, BigDecimal convertedQty) {
        String sql = "SELECT C_JobLineComponents_ID, QtyConverted,QtyComponent FROM C_JobLineComponents WHERE C_JobOrderLine_ID=?";
        
        try (PreparedStatement pstmt = DB.prepareStatement(sql, null)) {
            pstmt.setInt(1, C_JobOrderLine_ID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int componentID = rs.getInt("C_JobLineComponents_ID");
//              BigDecimal oldQty = rs.getBigDecimal("QtyConverted");
                BigDecimal bomQty = rs.getBigDecimal("QtyComponent");

                // Multiply component quantity by converted quantity
                BigDecimal newQty = bomQty.multiply(convertedQty);

                // Update the component quantity in the database
                String updateSQL = "UPDATE C_JobLineComponents SET QtyConverted=? WHERE C_JobLineComponents_ID=?";
                DB.executeUpdate(updateSQL, new Object[]{newQty, componentID}, false, null);
            }

        } catch (Exception e) {
            throw new AdempiereException("Error updating component quantities: " + e.getMessage());
        }
    }
    
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
  			    if (jobOrderLine.getM_Product_ID() > 0)
  			    {
  			    	mTab.setValue("M_Product_ID", jobOrderLine.getM_Product_ID());
  			    }
  			    else if (jobOrderLine.getC_Charge_ID() > 0)
  			    {
  			    	 mTab.setValue("C_Charge_ID", jobOrderLine.getC_Charge_ID());
  			    	 mTab.setValue("Price", jobOrderLine.getPriceEntered());
  			    	 mTab.setValue("M_Product_ID", null);
  			    }			     
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
  		 
   		//Callout Path : org.cyprusbrs.model.CalloutJobWork.calculateAmounts
  		 // org.compiere.model.CalloutJobWorkOrder.calculateAmounts
  		public String calculateAmounts(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
  	        if (value == null) return "";

  	        // Get Quantity and PriceEntered
  	        BigDecimal qty = (BigDecimal) mTab.getValue("QtyEntered");
  	        BigDecimal price = (BigDecimal) mTab.getValue("PriceEntered");
  	        if (qty == null) qty = Env.ZERO;
  	        if (price == null) price = Env.ZERO;

  	        mTab.setValue("Price", price);
  	        // Calculate Line Amount
  	        BigDecimal lineAmount = qty.multiply(price);
  	        mTab.setValue("LineNetAmt", lineAmount);

  	        // Get Tax Amount if exists
  	        
  	   // Get Tax ID
	    Integer taxID = (Integer) mTab.getValue("C_Tax_ID");
	    BigDecimal taxAmt = Env.ZERO;
	    if (taxID != null && taxID > 0) {
	        // Get tax rate from DB
	        String sql = "SELECT Rate FROM C_Tax WHERE C_Tax_ID = ?";
	        BigDecimal taxRate = DB.getSQLValueBD(null, sql, taxID);
	        if (taxRate == null) taxRate = Env.ZERO;

	        // Calculate Tax Amount
	        taxAmt = lineAmount.multiply(taxRate).divide(Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_UP);
	        mTab.setValue("TaxAmt", taxAmt);
	    }
  	        
//  	BigDecimal taxAmt = (BigDecimal) mTab.getValue("TaxAmt");
//  	if (taxAmt == null) taxAmt = Env.ZERO;

        // Line Net Amount = LineAmount + Tax
        BigDecimal total = lineAmount.add(taxAmt);
        mTab.setValue("TotalLineNetAmt", total);
  	        return "";
  	    }
  	//Callout Path : org.cyprusbrs.model.CalloutJobWork.calculateTaxAndTotal
 		 // org.compiere.model.CalloutJobWork.calculateTaxAndTotal
  		public String calculateTaxAndTotal(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
  		    if (value == null) return "";

  		    // Get Price and Quantity
  		    BigDecimal price = (BigDecimal) mTab.getValue("PriceEntered");
  		    BigDecimal qty = (BigDecimal) mTab.getValue("QtyEntered");
  		    if (price == null) price = Env.ZERO;
  		    if (qty == null) qty = Env.ZERO;

  		    // Calculate Line Amount
  		    BigDecimal lineAmount = qty.multiply(price);
  		    mTab.setValue("LineNetAmt", lineAmount);

  		    // Get Tax ID
  		    Integer taxID = (Integer) mTab.getValue("C_Tax_ID");
  		    BigDecimal taxAmt = Env.ZERO;
  		    if (taxID != null && taxID > 0) {
  		        // Get tax rate from DB
  		        String sql = "SELECT Rate FROM C_Tax WHERE C_Tax_ID = ?";
  		        BigDecimal taxRate = DB.getSQLValueBD(null, sql, taxID);
  		        if (taxRate == null) taxRate = Env.ZERO;

  		        // Calculate Tax Amount
  		        taxAmt = lineAmount.multiply(taxRate).divide(Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_UP);
  		        mTab.setValue("TaxAmt", taxAmt);
  		    }

  		    // Final Total
  		    BigDecimal total = lineAmount.add(taxAmt);
  		    mTab.setValue("TotalLineNetAmt", total);

  		    return "";
  		}

  		
  		
}
