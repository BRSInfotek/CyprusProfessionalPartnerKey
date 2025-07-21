package org.cyprus.mfg.process;



//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderResourceTxnLine;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.util.MMFGWorkOrderTxnUtil;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.cyprusbrsUserException;

public class GenerateResourceTxnLines extends SvrProcess {
  private int p_MFG_WorkOrderTransaction_ID = 0;
  
  private BigDecimal p_Qty = BigDecimal.ZERO;
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CMFG");
//    if (se == null || !se.checkLicense())
//    //  throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
//        throw new Exception(CLogger.retrieveError().getName()); 

    	if (0 == this.p_MFG_WorkOrderTransaction_ID)
//      throw new cyprusbrsUserException("@FillMandatory@ @M_WorkOrderTransaction_ID@"); 
    	      throw new Exception("@FillMandatory@ @M_WorkOrderTransaction_ID@"); 

    		MMFGWorkOrderTransaction woTxn = new MMFGWorkOrderTransaction(getCtx(), this.p_MFG_WorkOrderTransaction_ID, get_TrxName());
    if (this.p_Qty == null) {
      MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), woTxn.getMFG_WorkOrder_ID(), get_TrxName());
      this.p_Qty = wo.getQtyEntered().subtract(wo.getQtyAssembled());
      this.log.info("@Quantity@ = " + wo.getQtyEntered().subtract(wo.getQtyAssembled().add(wo.getQtyScrapped())));
    } 
    woTxn.setQtyEntered(this.p_Qty.setScale(MUOM.getPrecision(getCtx(), woTxn.getC_UOM_ID()), 4));
    woTxn.save();
    MMFGWorkOrderTxnUtil resTxnLines = new MMFGWorkOrderTxnUtil(true);
    MMFGWorkOrderResourceTxnLine[] wortlines = resTxnLines.generateResourceTxnLine(getCtx(), this.p_MFG_WorkOrderTransaction_ID, this.p_Qty, get_TrxName(), false);
    if (wortlines != null && wortlines.length > 0)
      return "Generated " + wortlines.length + " line(s) for production resource(s): " + CLogger.retrieveInfo().getName(); 
    return "Generated 0 lines for production resources.";
  }
  
  protected void prepare() {
    this.p_MFG_WorkOrderTransaction_ID = getRecord_ID();
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("Quantity")) {
          this.p_Qty = (BigDecimal)element.getParameter();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
}

