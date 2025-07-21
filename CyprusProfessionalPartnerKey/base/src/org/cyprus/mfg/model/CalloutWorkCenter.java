package org.cyprus.mfg.model;



import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;

public class CalloutWorkCenter extends CalloutEngine {
  public String warehouse(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Warehouse_ID = (Integer)value;
    setCalloutActive(true);
    if (M_Warehouse_ID == null || M_Warehouse_ID.intValue() == 0) {
      mTab.setValue("M_Locator_ID", null);
      setCalloutActive(false);
      return "";
    } 
   // mTab.setValue("M_Locator_ID", Integer.valueOf(MWarehouse.get(ctx, M_Warehouse_ID.intValue()).getDefaultM_Locator_ID()));
    setCalloutActive(false);
    return "";
  }
}
