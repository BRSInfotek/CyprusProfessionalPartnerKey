package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.PO;
//import org.cyprusbrs.framework.PO;
//import org.cyprusbrs.model.X_M_ZoneLocator;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSZoneLocator extends X_WMS_ZoneLocator {
  private static final CLogger log = CLogger.getCLogger(MWMSZoneLocator.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSZoneLocator(Properties ctx, int M_ZoneLocator_ID, String trx) {
    super(ctx, M_ZoneLocator_ID, trx);
  }
  
  public MWMSZoneLocator(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSZoneLocator(MWMSZone zone, int M_Locator_ID) {
    this(zone.getCtx(), 0, zone.get_TrxName());
    setClientOrg((PO)zone);
    setWMS_Zone_ID(zone.getWMS_Zone_ID());
    setM_Locator_ID(M_Locator_ID);
  }
  
  protected boolean beforeSave(boolean newRecord) {
 //   SysEnv se = SysEnv.get("CWMS");
  //  if (se == null || !se.checkLicense())
  //    return false; 
    if (newRecord || is_ValueChanged("M_Locator_ID")) {
      MWMSZone zone = new MWMSZone(getCtx(), getWMS_Zone_ID(), get_TrxName());
      if (zone.isStatic()) {
        CPreparedStatement cPreparedStatement=null;
        int retValue = 0;
        String sql = "SELECT z.M_Zone_ID FROM M_ZoneLocator zl INNER JOIN M_Zone z ON (zl.M_Zone_ID=z.M_Zone_ID) WHERE z.M_Warehouse_ID=? AND z.IsStatic='Y' AND zl.M_Locator_ID=? AND z.M_Zone_ID<>? ";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
          cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
          cPreparedStatement.setInt(1, zone.getM_Warehouse_ID());
          cPreparedStatement.setInt(2, getM_Locator_ID());
          cPreparedStatement.setInt(3, getWMS_Zone_ID());
          rs = cPreparedStatement.executeQuery();
          if (rs.next())
            retValue = rs.getInt(1); 
        } catch (SQLException ex) {
          log.log(Level.SEVERE, sql, ex);
        } finally {
          DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null; cPreparedStatement = null;
        } 
        if (retValue > 0) {
          MWMSZone fixedZone = new MWMSZone(getCtx(), retValue, get_TrxName());
          log.saveError("Error", Msg.getMsg(getCtx(), "StaticZoneLocator") + " : " + fixedZone.getName());
          return false;
        } 
      } 
      int ii = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM M_ZoneLocator zl WHERE zl.M_Zone_ID=? AND zl.M_Locator_ID=? ", new Object[] { Integer.valueOf(getWMS_Zone_ID()), Integer.valueOf(getM_Locator_ID()) });
      if (ii > 0) {
        log.saveError("Error", Msg.getMsg(getCtx(), "LocatorAlreadyInZone"));
        return false;
      } 
    } 
    return true;
  }
}

