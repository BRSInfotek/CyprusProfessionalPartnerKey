package org.cyprus.mfg.process;



//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.util.BOMDropLines;
import org.cyprus.mfg.util.MMFGCopyOperations;
import org.cyprusbrs.process.SvrProcess;
//import org.cyprusbrs.util.cyprusbrsSystemException;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Trx;

public class CopyBOMComponents extends SvrProcess {
  private int p_MFG_WorkOrder_ID = 0;
  
  Trx trxname;
  private MMFGWorkOrder mfg_workorder = null;
  
  private boolean m_isprocesscalled = false;
  
  private MMFGCopyOperations mfg_CopyOperations = null;
  
  private BOMDropLines bomDropLines = null;
  
  protected void prepare() {
    this.p_MFG_WorkOrder_ID = getRecord_ID();
  }
  
  protected String doIt() throws Exception {
    if (this.p_MFG_WorkOrder_ID != 0) {
      this.mfg_workorder = new MMFGWorkOrder(getCtx(), this.p_MFG_WorkOrder_ID, get_TrxName());
    } else {
      this.log.log(Level.SEVERE, "Not found - MFG_WorkOrder_ID=" + this.p_MFG_WorkOrder_ID);
      return "Not found - MFG_WorkOrder_ID=" + this.p_MFG_WorkOrder_ID;
    } 
//    SysEnv se = SysEnv.get("CMFG");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    this.m_isprocesscalled = true;
    BigDecimal qty = Env.ONE;
    this.mfg_CopyOperations = new MMFGCopyOperations();
    if (this.mfg_workorder.getMFG_Routing_ID() != 0)
      if (!this.mfg_CopyOperations.addOperationLines(getCtx(), get_TrxName(), this.mfg_workorder.getMFG_Routing_ID(), this.mfg_workorder.getMFG_WorkOrder_ID(), this.m_isprocesscalled))
//        throw new cyprusbrsSystemException("Operations/Resources not copied.");  
          throw new Exception("Operations/Resources not copied.");  

    	  this.bomDropLines = new BOMDropLines();
    if (this.mfg_workorder.getM_BOM_ID() != 0 && 
      !this.bomDropLines.addBOMLines(getCtx(), trxname, this.mfg_workorder.getM_Product_ID(), qty, this.mfg_workorder.getM_BOM_ID(), null, null, this.mfg_workorder.getMFG_WorkOrder_ID(), this.m_isprocesscalled))
//      throw new cyprusbrsSystemException(this.bomDropLines.getM_processMsg()); 
        throw new Exception(this.bomDropLines.getM_processMsg()); 

    	if (this.mfg_workorder.getM_BOM_ID() != 0) {
      ArrayList<Integer> OperationsProcessed = new ArrayList<Integer>();
      OperationsProcessed.addAll(this.mfg_CopyOperations.operationsProcessed());
      for (Integer i : this.bomDropLines.operationsProcessed()) {
        if (!OperationsProcessed.contains(i))
          OperationsProcessed.add(i); 
      } 
      return "@OperationsProcessed@ = #" + OperationsProcessed.size();
    } 
    return "@OperationsProcessed@ = #" + this.mfg_CopyOperations.operationsProcessed();
  }
}

