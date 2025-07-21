package org.cyprus.wms.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MLocator;
//import org.cyprusbrs.model.X_M_Zone;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSZone extends X_WMS_Zone {
  private static final CLogger log = CLogger.getCLogger(MWMSZone.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MWMSZone.class);
  
  public MWMSZone(Properties ctx, int M_Zone_ID, String trx) {
    super(ctx, M_Zone_ID, trx);
  }
  
  public MWMSZone(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public static MWMSZone getOfLocator(Properties ctx, int M_Warehouse_ID, int M_Locator_ID) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    MWMSZone zone = null;
    String sql = "SELECT * FROM WMS_Zone z WHERE IsStatic='Y' AND z.M_Warehouse_ID=? AND EXISTS (SELECT 1 FROM WMS_ZoneLocator zl WHERE z.WMS_Zone_ID = zl.M_Zone_ID AND zl.M_Locator_ID=?) ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, M_Warehouse_ID);
      cPreparedStatement.setInt(2, M_Locator_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        zone = new MWMSZone(ctx, rs, null); 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return zone;
  }
  
  public static boolean isLocatorInZone(Properties ctx, int M_Zone_ID, int M_Locator_ID) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    boolean isLocatorInZone = false;
    String sql = "SELECT 1 FROM WMS_ZoneLocator zl WHERE zl.WMS_Zone_ID = ? AND zl.M_Locator_ID=? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, M_Zone_ID);
      cPreparedStatement.setInt(2, M_Locator_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        isLocatorInZone = true; 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return isLocatorInZone;
  }
  
  public MLocator[] getLocators() throws Exception {
    CPreparedStatement cPreparedStatement=null;
    ArrayList<MLocator> list = new ArrayList<MLocator>();
    String sql = "SELECT M_Locator_ID FROM WMS_ZoneLocator WHERE WMS_Zone_ID=? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
      cPreparedStatement.setInt(1, getWMS_Zone_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        int M_Locator_ID = rs.getInt("M_Locator_ID");
        MLocator loc = new MLocator(getCtx(), M_Locator_ID, get_TrxName());
        list.add(loc);
      } 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    MLocator[] lines = new MLocator[list.size()];
    list.toArray(lines);
    return lines;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (newRecord || is_ValueChanged("IsStatic")) {
      boolean isStatic = get_ValueAsString("IsStatic").equals("Y");
      if (!isStatic) {
        int ii = DB.getSQLValue(null, "SELECT count(*) FROM WMS_ZoneRelationship zr WHERE zr.WMS_Zone_ID=? ", new Object[] { Integer.valueOf(getWMS_Zone_ID()) });
        if (ii > 0) {
          log.saveError("Error", Msg.getMsg(getCtx(), "ZoneRelationshipDefined"));
          return false;
        } 
      } 
      if (isStatic) {
        int ii = DB.getSQLValue(null, "SELECT count(*) FROM WMS_ZoneLocator zl WHERE zl.WMS_Zone_ID=?  AND EXISTS (SELECT 1 FROM WMS_Zone z INNER JOIN WMS_ZoneLocator zl2 ON (z.WMS_Zone_ID = zl2.WMS_Zone_ID) WHERE z.IsStatic = 'Y' AND zl2.M_Locator_ID=zl.M_Locator_ID)", new Object[] { Integer.valueOf(getWMS_Zone_ID()) });
        if (ii > 0) {
          log.saveError("Error", Msg.getMsg(getCtx(), "OverlappingStaticZone"));
          return false;
        } 
      } 
    } 
    if (newRecord || is_ValueChanged("IsAvailableToPromise") || is_ValueChanged("IsAvailableForAllocation"))
      if (isAvailableForAllocation() && !isAvailableToPromise()) {
        log.saveError("Error", Msg.getMsg(getCtx(), "InvalidCombination"));
        return false;
      }  
    return super.beforeSave(newRecord);
  }
}

