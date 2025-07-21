package org.cyprus.wms.process;


//import com.cyprusbrs.client.SysEnv;
import org.cyprusbrs.process.SvrProcess;

public class PrintTaskList extends SvrProcess {
  private int p_M_TaskList_ID = 0;
  
  protected void prepare() {
    this.p_M_TaskList_ID = getRecord_ID();
  }
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.log.info("p_M_TaskList_ID=" + this.p_M_TaskList_ID);
    return "Success";
  }
}
