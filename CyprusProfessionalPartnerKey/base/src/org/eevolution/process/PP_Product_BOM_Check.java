package org.eevolution.process;

import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CyprusUserError;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.ValueNamePair;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

public class PP_Product_BOM_Check extends SvrProcess {
  private int p_Record_ID = 0;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (int i = 0; i < para.length; i++) {
      String name = para[i].getParameterName();
      if (para[i].getParameter() != null)
        this.log.log(Level.SEVERE, "Unknown Parameter: " + name); 
    } 
    this.p_Record_ID = getRecord_ID();
  }
  
  protected String doIt() throws Exception {
    this.log.info("Check BOM Structure");
    MProduct xp = new MProduct(Env.getCtx(), this.p_Record_ID, get_TrxName());
    if (!xp.isBOM()) {
      this.log.info("Product is not a BOM");
      return "OK";
    } 
    int lowlevel = MPPProductBOMLine.getLowLevel(getCtx(), this.p_Record_ID, get_TrxName());
    xp.setLowLevel(lowlevel);
    xp.setIsVerified(true);
    xp.saveEx();
    MPPProductBOM tbom = MPPProductBOM.getDefault(xp, get_TrxName());
    if (tbom == null)
      raiseError("No Default BOM found: ", "Check BOM Parent search key"); 
    if (tbom.getM_Product_ID() != 0) {
      MPPProductBOMLine[] tbomlines = tbom.getLines();
      byte b;
      int i;
      MPPProductBOMLine[] arrayOfMPPProductBOMLine1;
      for (i = (arrayOfMPPProductBOMLine1 = tbomlines).length, b = 0; b < i; ) {
        MPPProductBOMLine tbomline = arrayOfMPPProductBOMLine1[b];
        lowlevel = tbomline.getLowLevel();
        MProduct p = new MProduct(getCtx(), tbomline.getM_Product_ID(), get_TrxName());
        p.setLowLevel(lowlevel);
        p.setIsVerified(true);
        p.saveEx();
        b++;
      } 
    } 
    return "OK";
  }
  
  private void raiseError(String string, String hint) throws Exception {
    DB.rollback(false, get_TrxName());
    MProduct xp = new MProduct(getCtx(), this.p_Record_ID, null);
    xp.setIsVerified(false);
    xp.saveEx();
    String msg = string;
    ValueNamePair pp = CLogger.retrieveError();
    if (pp != null)
      msg = String.valueOf(pp.getName()) + " - "; 
    msg = String.valueOf(msg) + hint;
    throw new CyprusUserError(msg);
  }
}
