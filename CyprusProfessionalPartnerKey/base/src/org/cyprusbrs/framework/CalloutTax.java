package org.cyprusbrs.framework;

import java.util.Properties;

import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.DB;

	public class CalloutTax extends CalloutEngine {

		/*
		 *  @param ctx context
		 *  @param WindowNo current Window No
		 *  @param mTab Grid Tab
		 *  @param mField Grid Field
		 *  @param value New Value
		 *  @return "" or error message
		 */
		public  String setTaxRuleDefined (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
		{
			if (isCalloutActive() || value == null)
				return "";

			Integer count= DB.getSQLValueEx(null,"SELECT COUNT(*) FROM C_TaxRule WHERE C_TaxCategory_ID = ?", (Integer) mTab.getValue(I_M_Product.COLUMNNAME_C_TaxCategory_ID));
			if (count > 0)
			{
			   mTab.setValue("IsTaxRule", true);
			}
			else 
			{
				mTab.setValue("IsTaxRule", false);
			}
			return "";
		}	
		
		
		public  String setTaxRate(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
		{
			if (isCalloutActive() || value == null)
				return "";
			MProduct pr = new MProduct(ctx, (Integer) mTab.getValue("M_Product_ID"), null);
			mTab.setValue("C_Tax_ID", pr.getC_Tax_ID());
			return "";
		}	
		
		
		public  String setTaxRateByRule(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
		{
			if (isCalloutActive() || value == null)
				return "";
			Integer M_Product_ID = (Integer) mTab.getValue("M_Product_ID");		
			if (M_Product_ID == null || M_Product_ID.intValue() == 0)
				return "";
			MProduct pr = new MProduct(ctx, (Integer) mTab.getValue("M_Product_ID"), null);
			if(pr != null) {
				if(pr.IsTaxRule())
				{
					int C_Tax_ID = DB.getSQLValueEx(null,"SELECT C_Tax_ID FROM C_TaxRule WHERE C_TaxCategory_ID = ? AND ? BETWEEN AmountFrom AND AmountTo", pr.getC_TaxCategory_ID(), mTab.getValue("PriceActual"));
					if(C_Tax_ID > 0)
					{
						mTab.setValue("C_Tax_ID", C_Tax_ID);
					}
					else
					{
						mTab.setValue("C_Tax_ID", pr.getC_Tax_ID());
					}
				}
				else
				{
					mTab.setValue("C_Tax_ID", pr.getC_Tax_ID());
				}
			}
			return "";
		}
		
	}// Callout Product

