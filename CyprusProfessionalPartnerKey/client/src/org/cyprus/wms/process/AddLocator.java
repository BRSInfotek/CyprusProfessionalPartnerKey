package org.cyprus.wms.process;

//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.cyprus.wms.model.MWMSZone;
import org.cyprus.wms.model.MWMSZoneLocator;
//import org.cyprusbrs.common.cyprusbrsStateException;
//import org.cyprusbrs.cwms.model.MZone;
//import org.cyprusbrs.cwms.model.MZoneLocator;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class AddLocator extends SvrProcess {
  private int p_M_Zone_ID = 0;
  
  private MWMSZone p_Zone; 
  
  private int p_M_SourceZone_ID = 0;
  
  private String p_xFrom = null;
  
  private String p_xTo = null;
  
  private String p_yFrom = null;
  
  private String p_yTo = null;
  
  private String p_zFrom = null;
  
  private String p_zTo = null;
  
  private String p_positionFrom = null;
  
  private String p_positionTo = null;
  
  private String p_binFrom = null;
  
  private String p_binTo = null;
  
  private String m_sql;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("WMS_SourceZone_ID")) {
          this.p_M_SourceZone_ID = element.getParameterAsInt();
        } else if (name.equals("X")) {
          this.p_xFrom = element.getInfo();
          this.p_xTo = element.getInfo_To();
        } else if (name.equals("Y")) {
          this.p_yFrom = element.getInfo();
          this.p_yTo = element.getInfo_To();
        } else if (name.equals("Z")) {
          this.p_zFrom = element.getInfo();
          this.p_zTo = element.getInfo_To();
        } else if (name.equals("Position")) {
          this.p_positionFrom = element.getInfo();
          this.p_positionTo = element.getInfo_To();
        } else if (name.equals("Bin")) {
          this.p_binFrom = element.getInfo();
          this.p_binTo = element.getInfo_To();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    this.p_M_Zone_ID = getRecord_ID();
    this.p_Zone = new MWMSZone(getCtx(), this.p_M_Zone_ID, null);
  }
  
  protected String doIt() throws Exception {
    CPreparedStatement cPreparedStatement = null;
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new Exception(CLogger.retrieveError().getName()); 
    this.log.info("p_M_SourceZone_ID=" + this.p_M_SourceZone_ID + "p_xFrom=" + this.p_xFrom + "p_xTo=" + this.p_xTo + "p_yFrom=" + this.p_yFrom + "p_yTo=" + this.p_yTo + "p_zFrom=" + this.p_zFrom + "p_zTo=" + this.p_zTo + "p_PositionFrom=" + this.p_positionFrom + "p_PositionTo=" + this.p_positionTo + "p_BinFrom=" + this.p_binFrom + "p_BinTo=" + this.p_binTo);
    if (this.p_M_SourceZone_ID != 0) {
      this.m_sql = "SELECT locator.M_Locator_ID FROM WMS_ZoneLocator zLocator  INNER JOIN WMS_Zone zone ON (zLocator.WMS_Zone_ID=zone.WMS_Zone_ID) INNER JOIN M_Locator locator ON (locator.M_Locator_ID=zLocator.M_Locator_ID) WHERE zone.WMS_Zone_ID=? AND locator.M_Warehouse_ID=?";
    } else {
      this.m_sql = "SELECT locator.M_Locator_ID FROM M_Locator locator  WHERE locator.M_Warehouse_ID=?";
    } 
    if (this.p_xFrom != null && this.p_xFrom.length() != 0)
      this.m_sql += " AND locator.X >= ?"; 
    if (this.p_xTo != null && this.p_xTo.length() != 0)
      this.m_sql += " AND locator.X <= ?"; 
    if (this.p_yFrom != null && this.p_yFrom.length() != 0)
      this.m_sql += " AND locator.Y >= ?"; 
    if (this.p_yTo != null && this.p_yTo.length() != 0)
      this.m_sql += " AND locator.Y <= ?"; 
    if (this.p_zFrom != null && this.p_zFrom.length() != 0)
      this.m_sql += " AND locator.Z >= ?"; 
    if (this.p_zTo != null && this.p_zTo.length() != 0)
      this.m_sql += " AND locator.Z <= ?"; 
    if (this.p_positionFrom != null && this.p_positionFrom.length() != 0)
      this.m_sql += " AND locator.Position >= ?"; 
    if (this.p_positionTo != null && this.p_positionTo.length() != 0)
      this.m_sql += " AND locator.Position <= ?"; 
    if (this.p_binFrom != null && this.p_binFrom.length() != 0)
      this.m_sql += " AND locator.Bin >= ?"; 
    if (this.p_binTo != null && this.p_binTo.length() != 0)
      this.m_sql += " AND locator.Bin <= ?"; 
    this.m_sql += " AND NOT EXISTS ( SELECT 1 FROM WMS_ZoneLocator zl WHERE zl.WMS_Zone_ID = ? AND  zl.M_Locator_ID = locator.M_Locator_ID)";
    this.m_sql += " AND (NOT EXISTS (SELECT 1 FROM WMS_Zone z WHERE z.WMS_Zone_ID = ? AND z.IsStatic='Y')  OR NOT EXISTS (SELECT 1 FROM WMS_Zone z2 INNER JOIN WMS_ZoneLocator zl ON z2.WMS_Zone_ID=zl.WMS_Zone_ID WHERE z2.IsStatic='Y' AND zl.M_Locator_ID=locator.M_Locator_ID)) ";
    this.m_sql += " ORDER BY X, Y, Z, Position, Bin";
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(this.m_sql, get_TrxName());
      int index = 1;
      if (this.p_M_SourceZone_ID != 0)
        cPreparedStatement.setInt(index++, this.p_M_SourceZone_ID); 
      cPreparedStatement.setInt(index++, this.p_Zone.getM_Warehouse_ID());
      if (this.p_xFrom != null && this.p_xFrom.length() != 0)
        cPreparedStatement.setString(index++, this.p_xFrom); 
      if (this.p_xTo != null && this.p_xTo.length() != 0)
        cPreparedStatement.setString(index++, this.p_xTo); 
      if (this.p_yFrom != null && this.p_yFrom.length() != 0)
        cPreparedStatement.setString(index++, this.p_yFrom); 
      if (this.p_yTo != null && this.p_yTo.length() != 0)
        cPreparedStatement.setString(index++, this.p_yTo); 
      if (this.p_zFrom != null && this.p_zFrom.length() != 0)
        cPreparedStatement.setString(index++, this.p_zFrom); 
      if (this.p_zTo != null && this.p_zTo.length() != 0)
        cPreparedStatement.setString(index++, this.p_zTo); 
      if (this.p_positionFrom != null && this.p_positionFrom.length() != 0)
        cPreparedStatement.setString(index++, this.p_positionFrom); 
      if (this.p_positionTo != null && this.p_positionTo.length() != 0)
        cPreparedStatement.setString(index++, this.p_positionTo); 
      if (this.p_binFrom != null && this.p_binFrom.length() != 0)
        cPreparedStatement.setString(index++, this.p_binFrom); 
      if (this.p_binTo != null && this.p_binTo.length() != 0)
        cPreparedStatement.setString(index++, this.p_binTo); 
      cPreparedStatement.setInt(index++, this.p_M_Zone_ID);
      cPreparedStatement.setInt(index++, this.p_M_Zone_ID);
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, this.m_sql, e);
      cPreparedStatement.close();
      cPreparedStatement = null;
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } 
    return addLocators((PreparedStatement)cPreparedStatement);
  }
  
  private String addLocators(PreparedStatement pstmt) throws Exception {
    int count = 0;
    ResultSet rs = null;
    try {
      rs = pstmt.executeQuery();
      while (rs.next()) {
        MWMSZoneLocator zLocator = new MWMSZoneLocator(this.p_Zone, rs.getInt("M_Locator_ID"));
        zLocator.save();
        count++;
      } 
    } catch (SQLException e) {
      this.log.log(Level.SEVERE, "", e);
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } finally {
      DB.close(rs);
      DB.close(pstmt);
      rs = null; pstmt = null;
    } 
    addLog(0, null, new BigDecimal(count), "@M_Locator_ID@: @Inserted@");
    return "Inserted : " + count;
  }
}

