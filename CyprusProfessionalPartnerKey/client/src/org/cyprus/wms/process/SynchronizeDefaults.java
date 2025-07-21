package org.cyprus.wms.process;


import org.cyprus.wms.model.MWMSZone;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.process.SvrProcess;

public class SynchronizeDefaults extends SvrProcess {
  private int p_M_Zone_ID = 0;
  
  protected void prepare() {
    this.p_M_Zone_ID = getRecord_ID();
  }
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    MWMSZone zone = new MWMSZone(getCtx(), this.p_M_Zone_ID, get_TrxName());
    MLocator[] locators = zone.getLocators();
    for (MLocator element : locators) {
      element.setIsAvailableForAllocation(zone.isAvailableForAllocation());
      element.setIsAvailableToPromise(zone.isAvailableToPromise());
      element.save();
    } 
    return "Synchronized defaults for : " + locators.length + " locators";
  }
}

