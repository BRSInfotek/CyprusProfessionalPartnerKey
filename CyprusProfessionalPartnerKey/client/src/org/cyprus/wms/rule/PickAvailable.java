package org.cyprus.wms.rule;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.intf.WMSRuleIntf;
import org.cyprus.wms.model.MWMSMMRule;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class PickAvailable implements WMSRuleIntf {
  private static final CLogger s_log = CLogger.getCLogger(PickAvailable.class);
  
  public MLocator[] getValidLocators(Properties ctx, int M_Warehouse_ID, int M_Zone_ID, int M_Product_ID, int C_OrderLine_ID, String trx) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    if (M_Warehouse_ID == 0 || M_Product_ID == 0)
      return null; 
    String sql = "SELECT * ";
    sql = sql + "FROM M_Locator l ";
    sql = sql + "LEFT OUTER JOIN WMS_ZoneLocator zl ON (zl.M_Locator_ID=l.M_Locator_ID) LEFT OUTER JOIN WMS_Zone z ON (z.WMS_Zone_ID=zl.WMS_Zone_ID AND z.IsStatic='Y') ";
    sql = sql + "WHERE l.M_Warehouse_ID=? AND l.IsAvailableForAllocation='Y' AND l.IsActive='Y' ";
    sql = sql + "AND EXISTS (SELECT 1 FROM M_STORAGE_V s WHERE s.M_Locator_ID = l.M_Locator_ID AND s.M_Product_ID = ? AND s.QtyOnHand> s.QtyDedicated + s.QtyAllocated) ";
    if (M_Zone_ID != 0)
      sql = sql + "AND l.M_Locator_ID IN (SELECT M_Locator_ID FROM WMS_ZoneLocator zl  WHERE zl.WMS_Zone_ID=? ) "; 
    sql = sql + " ORDER BY ";
    if (M_Zone_ID == 0)
      sql = sql + "z.PickingSeqNo, "; 
    sql = sql + " l.PickingSeqNo ";
    PreparedStatement pstmt = null;
    MLocator[] locators = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      int index = 1;
      cPreparedStatement.setInt(index++, M_Warehouse_ID);
      cPreparedStatement.setInt(index++, M_Product_ID);
      if (M_Zone_ID != 0)
        cPreparedStatement.setInt(index++, M_Zone_ID); 
      locators = MWMSMMRule.getLocators(ctx, (PreparedStatement)cPreparedStatement, trx);
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close((Statement)cPreparedStatement);
      cPreparedStatement = null;
    } 
    return locators;
  }
//Anil20122021
  //Have to check at run time
//@Override
public MLocator[] getValidLocators(Properties ctx, int M_Warehouse_ID, int M_Zone_ID, int M_Product_ID, String trx) {
	// TODO Auto-generated method stub
	return null;
}
}
