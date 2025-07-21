package org.cyprus.mrp.model;

import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;

public class CalloutPlan extends CalloutEngine {
  public String warehouse(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    Integer M_Warehouse_ID = (Integer)value;
    if (M_Warehouse_ID == null || M_Warehouse_ID.intValue() == 0) {
      mTab.setValue("M_Locator_ID", null);
      return "";
    } 
    MWarehouse warehouse = MWarehouse.get(Env.getCtx(), M_Warehouse_ID.intValue());
//    mTab.setValue("M_Locator_ID", Integer.valueOf(warehouse.getDefaultM_Locator_ID()));
    mTab.setValue("M_Locator_ID", Integer.valueOf(warehouse.getDefaultLocator().getM_Locator_ID()));

    return "";
  }
}

