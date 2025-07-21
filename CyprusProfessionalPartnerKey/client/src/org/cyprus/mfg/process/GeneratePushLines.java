package org.cyprus.mfg.process;



import java.math.BigDecimal;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.model.MMFGWorkOrderTransactionLine;
import org.cyprus.mfg.model.X_MFG_WorkOrderComponent;
import org.cyprus.mfg.util.MMFGWorkOrderTxnUtil;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;

public class GeneratePushLines extends SvrProcess {
  private int p_M_WorkOrderTransaction_ID = 0;
  
  private BigDecimal p_Qty = BigDecimal.ZERO;
  
  private BigDecimal MFG_WorkOrderOperation_ID=BigDecimal.ZERO;
  
  protected void prepare() {
    this.p_M_WorkOrderTransaction_ID = getRecord_ID();
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("Quantity")) {
          this.p_Qty = (BigDecimal)element.getParameter();
        } 
        else if (name.equals("MFG_WorkOrderOperation_ID")) {
            this.MFG_WorkOrderOperation_ID = (BigDecimal)element.getParameter();
          } 
        else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
  
  protected String doIt() throws Exception {
	  
    	if (0 == this.p_M_WorkOrderTransaction_ID)
    	      throw new CyprusException("@FillMandatory@ @M_WorkOrderTransaction_ID@"); 

    		MMFGWorkOrderTransaction woTxn = new MMFGWorkOrderTransaction(getCtx(), this.p_M_WorkOrderTransaction_ID, get_TrxName());
    if (this.p_Qty == null) {
      MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), woTxn.getMFG_WorkOrder_ID(), get_TrxName());
      this.p_Qty = wo.getQtyEntered().subtract(wo.getQtyAssembled());
      this.log.info("@Quantity@ = " + wo.getQtyEntered().subtract(wo.getQtyAssembled().add(wo.getQtyScrapped())));
    } 
    woTxn.setQtyEntered(this.p_Qty.setScale(MUOM.getPrecision(getCtx(), woTxn.getC_UOM_ID()), 4));
    woTxn.save();
    MMFGWorkOrderTxnUtil prodTxnLines = new MMFGWorkOrderTxnUtil(true);
    MMFGWorkOrderTransactionLine[] wotlines = prodTxnLines.generateComponentTxnLine(getCtx(), this.p_M_WorkOrderTransaction_ID, this.p_Qty, X_MFG_WorkOrderComponent.SUPPLYTYPE_Push, get_TrxName(),MFG_WorkOrderOperation_ID);
    if (wotlines != null && wotlines.length > 0)
      return "Generated " + wotlines.length + " line(s) for component(s): " + CLogger.retrieveInfo().getName(); 
    return "Generated 0 lines for components.";
  }
}

