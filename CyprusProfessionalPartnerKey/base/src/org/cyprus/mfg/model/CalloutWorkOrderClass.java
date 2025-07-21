package org.cyprus.mfg.model;



import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class CalloutWorkOrderClass extends CalloutEngine {
  private final CLogger log = CLogger.getCLogger(getClass());
  
  public String isDefault(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    String isDefault = ((Boolean)value).booleanValue() ? "Y" : "N";
    if (isDefault == null || isDefault.length() == 0)
      return ""; 
    if (isDefault.equals("Y")) {
      String updateSql = "UPDATE MFG_WorkOrderClass SET IsDefault = 'N' WHERE WOType = ? AND AD_Org_ID = ? AND AD_Client_ID = ? AND MFG_WorkOrderClass_ID != ?";
      String WOType = mTab.get_ValueAsString("WOType");
      int orgID = ((Integer)mTab.getValue("AD_Org_ID")).intValue();
      int clientID = ((Integer)mTab.getValue("AD_Client_ID")).intValue();
      Object objClassID = mTab.getValue("MFG_WorkOrderClass_ID");
      int classID = 0;
      if (objClassID != null)
        classID = ((Integer)objClassID).intValue(); 
      CPreparedStatement cPreparedStatement = null;
      try {
     //   CPreparedStatement cPreparedStatement = DB.prepareStatement(updateSql, (Trx)null);
           cPreparedStatement = DB.prepareStatement(updateSql, null);

        cPreparedStatement.setString(1, WOType);
        cPreparedStatement.setInt(2, orgID);
        cPreparedStatement.setInt(3, clientID);
        cPreparedStatement.setInt(4, classID);
        cPreparedStatement.executeUpdate();
        cPreparedStatement.close();
      } catch (SQLException e) {
        this.log.log(Level.SEVERE, updateSql, e);
        return "";
      } 
      finally
		{
			DB.close(cPreparedStatement);
			cPreparedStatement = null; 
		}
    } 
    return "";
  }
}

