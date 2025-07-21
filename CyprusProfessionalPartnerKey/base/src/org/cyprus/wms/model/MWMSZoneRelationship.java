package org.cyprus.wms.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.PO;
//import org.cyprusbrs.common.cyprusbrsStateException;
//import org.cyprusbrs.framework.PO;
//import org.cyprusbrs.model.X_M_ZoneRelationship;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSZoneRelationship extends X_WMS_ZoneRelationship {
  private static final CLogger log = CLogger.getCLogger(MWMSZoneRelationship.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSZoneRelationship(Properties ctx, int M_ZoneRelationship_ID, String trx) {
    super(ctx, M_ZoneRelationship_ID, trx);
  }
  
  public MWMSZoneRelationship(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSZoneRelationship(MWMSZone zone, int M_SourceZone_ID) {
    this(zone.getCtx(), 0, zone.get_TrxName());
    setClientOrg((PO)zone);
    setWMS_Zone_ID(zone.getWMS_Zone_ID());
    setM_SourceZone_ID(M_SourceZone_ID);
  }
  
  private static CLogger s_log = CLogger.getCLogger(MWMSZoneRelationship.class);
  
  public static MWMSZone[] getSourceZones(Properties ctx, int M_Zone_ID, String trx) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    ArrayList<MWMSZone> list = new ArrayList<MWMSZone>();
    String sql = "SELECT M_SourceZone_ID FROM M_ZoneRelationship WHERE M_Zone_ID=? ORDER BY ReplenishmentSeqNo ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, M_Zone_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        int M_SourceZone_ID = rs.getInt(1);
        list.add(new MWMSZone(ctx, M_SourceZone_ID, trx));
      } 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    MWMSZone[] retValue = new MWMSZone[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (newRecord) {
      MWMSZone zone = new MWMSZone(getCtx(), getWMS_Zone_ID(), get_TrxName());
      if (!zone.isStatic()) {
        log.saveError("Error", Msg.getMsg(getCtx(), "NonStaticZone") + " : " + zone.getName());
        return false;
      } 
    } 
    return true;
  }
}
