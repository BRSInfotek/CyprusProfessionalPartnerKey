package org.cyprus.wms.process;


//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.util.logging.Level;

import org.cyprus.wms.model.MWMSWarehouseTask;
//import org.cyprusbrs.cwms.model.MWarehouseTask;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.Env;

public class SplitTask extends SvrProcess {
  private BigDecimal p_splitQuantity = Env.ZERO;
  
  private int p_M_WarehouseTask_ID = 0;
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense()) 
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.info("p_M_WarehouseTask_ID=" + this.p_M_WarehouseTask_ID + "p_splitQuantity=" + this.p_splitQuantity);
    MWMSWarehouseTask task = new MWMSWarehouseTask(getCtx(), this.p_M_WarehouseTask_ID, get_TrxName());
    if (task == null)
      throw new IllegalArgumentException("Missing/Invalid task"); 
    if (this.p_splitQuantity.signum() <= 0 || this.p_splitQuantity.compareTo(task.getTargetQty()) > 0)
      throw new IllegalArgumentException("@SplitQuantityInvalid@"); 
    MWMSWarehouseTask splitTask = task.splitTask(this.p_splitQuantity);
    if (splitTask == null)
      throw new IllegalStateException("Could not create split task"); 
    return splitTask.getDocumentNo();
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("SplitQuantity")) {
          this.p_splitQuantity = (BigDecimal)element.getParameter();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    this.p_M_WarehouseTask_ID = getRecord_ID();
  }
}
