package org.cyprusbrs.framework;

import java.util.Properties;

import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;

//import org.compiere.util.Ctx;

public class CalloutProduct extends CalloutEngine {
  public String productCategory(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Product_Category_ID = (Integer)value;
    if (M_Product_Category_ID == null || M_Product_Category_ID.intValue() == 0 || M_Product_Category_ID.intValue() == 0)
      return ""; 
    MProductCategory pc = new MProductCategory(ctx, M_Product_Category_ID.intValue(), null);
    mTab.setValue("IsPurchasedToOrder", Boolean.valueOf(pc.isPurchasedToOrder()));
    return "";
  }
  
  public String productCategoryAssetGroup(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
	  Integer M_Product_Category_ID = (Integer)value;
	  if (M_Product_Category_ID == null || M_Product_Category_ID.intValue() == 0 || M_Product_Category_ID.intValue() == 0)
		  return ""; 
	  MProductCategory pc = new MProductCategory(ctx, M_Product_Category_ID.intValue(), null);
	  if(pc.getProductType().equalsIgnoreCase(X_M_Product_Category.PRODUCTTYPE_Asset))
	  {	
		  mTab.setValue("IsAssets", true);
		  mTab.setValue("A_Asset_Group_ID", pc.getA_Asset_Group_ID());
	  }
	  else
	  {
		  mTab.setValue("IsAssets", false);
	  }
	  return "";
  }
  
  public String resourceGroup(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    String resgrp = (String)value;
    if (resgrp == null || resgrp.length() == 0)
      return ""; 
    if ("O".equals(resgrp)) {
      mTab.setValue("BasisType", (Object)null);
    } else {
      mTab.setValue("BasisType", "I");
    } 
    return "";
  }
  
  public String Organization(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer AD_Org_ID = (Integer)value;
    if (AD_Org_ID == null)
      return ""; 
    MLocator defaultLocator = MLocator.getDefaultLocatorOfOrg(ctx, AD_Org_ID.intValue());
    if (defaultLocator != null)
      mTab.setValue(I_M_Locator.COLUMNNAME_M_Locator_ID, Integer.valueOf(defaultLocator.get_ID())); 
    return "";
  }
}

