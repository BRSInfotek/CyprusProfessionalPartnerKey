package org.cyprus.wms.util;


import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.wms.model.MWMSWarehouseTask;
//import org.cyprusbrs.model.X_M_WorkOrder;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MOrder;

public class MWarehouseTaskUtil {
  public static boolean reverseInOutTasks(MInOut inout, boolean onlyIncomplete) {
    return MWMSWarehouseTask.reverseInOutTasks(inout, onlyIncomplete);
  }
  
  public static boolean reverseOrderTasks(MOrder order, boolean onlyIncomplete) {
    return MWMSWarehouseTask.reverseOrderTasks(order, onlyIncomplete);
  }
  
  public static boolean reverseWorkOrderTasks(X_MFG_WorkOrder xwo, boolean onlyIncomplete) {
    return MWMSWarehouseTask.reverseWorkOrderTasks(xwo, onlyIncomplete);
  }
}
