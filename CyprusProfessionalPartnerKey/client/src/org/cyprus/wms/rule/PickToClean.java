package org.cyprus.wms.rule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.intf.WMSRuleIntf;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class PickToClean implements WMSRuleIntf {
  private static final CLogger s_log = CLogger.getCLogger(PickToClean.class);
  
  public MLocator[] getValidLocators(Properties ctx, int M_Warehouse_ID, int M_Zone_ID, int M_Product_ID, int C_OrderLine_ID, String trx) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    if (M_Warehouse_ID == 0 || M_Product_ID == 0)
      return null; 
    ArrayList<MLocator> list = new ArrayList<MLocator>();
    String sql = "SELECT l.M_Locator_ID FROM M_STORAGE_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) INNER JOIN M_Warehouse w ON (l.M_Warehouse_ID=w.M_Warehouse_ID) ";
    sql = sql + "LEFT OUTER JOIN WMS_ZoneLocator zl ON (zl.M_Locator_ID=l.M_Locator_ID) LEFT OUTER JOIN WMS_Zone z ON (z.WMS_Zone_ID=zl.WMS_Zone_ID AND z.IsStatic='Y') ";
    sql = sql + "WHERE w.M_Warehouse_ID=? AND s.M_Product_ID=? AND l.IsAvailableForAllocation='Y' AND l.IsActive='Y' AND (s.QtyOnHand>0 OR s.QtyDedicated>0 OR s.QtyAllocated>0) ";
    if (M_Zone_ID != 0)
      sql = sql + "AND l.M_Locator_ID IN (SELECT M_Locator_ID FROM WMS_ZoneLocator zl  WHERE zl.WMS_Zone_ID=? ) "; 
    sql = sql + "GROUP BY l.M_Locator_ID,z.PickingSeqNo,l.PickingSeqNo  HAVING SUM(s.QtyOnHand) > SUM(s.QtyDedicated)+SUM(s.QtyAllocated) ";
    sql = sql + " ORDER BY ";
    sql = sql + " SUM(s.QtyOnHand)-SUM(s.QtyDedicated)-SUM(s.QtyAllocated) ASC, ";
    if (M_Zone_ID == 0)
      sql = sql + "z.PickingSeqNo, "; 
    sql = sql + " l.PickingSeqNo ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      int index = 1;
      cPreparedStatement.setInt(index++, M_Warehouse_ID);
      cPreparedStatement.setInt(index++, M_Product_ID);
      if (M_Zone_ID != 0)
        cPreparedStatement.setInt(index++, M_Zone_ID); 
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MLocator locator = new MLocator(ctx, rs.getInt("M_Locator_ID"), trx);
        list.add(locator);
      } 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);

		rs = null; cPreparedStatement = null;
    } 
    MLocator[] retValue = new MLocator[list.size()];
    list.toArray(retValue);
    return retValue;
  }
//@Anil20122021
  //Have to check At run time
//@Override
public MLocator[] getValidLocators(Properties ctx, int M_Warehouse_ID, int M_Zone_ID, int M_Product_ID, String trx) {
	// TODO Auto-generated method stub
	return null;
}
}
