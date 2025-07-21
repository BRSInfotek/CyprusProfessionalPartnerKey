package org.cyprus.wms.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mfg.model.X_MFG_WaveSortCriteria;
import org.cyprusbrs.framework.MInfoColumn;
import org.cyprusbrs.framework.MProductCategory;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSWaveSortCriteria extends X_MFG_WaveSortCriteria {
  private static final CLogger log = CLogger.getCLogger(MWMSWaveSortCriteria.class);
  
  private static final long serialVersionUID = 1L;
  
  public MWMSWaveSortCriteria(Properties ctx, int M_WaveSortCriteria_ID, String trx) {
    super(ctx, M_WaveSortCriteria_ID, trx);
  }
  
  public MWMSWaveSortCriteria(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  private static final CLogger s_log = CLogger.getCLogger(MProductCategory.class);
  
  public static String getOrderBy(Properties ctx, int C_WaveSortCriteria_ID) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    String orderBy = "";
    String sql = "SELECT AD_InfoColumn_ID, orderByType FROM C_WaveSortCriteriaLine WHERE C_WaveSortCriteria_ID=? ORDER BY SeqNo";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), null);
      cPreparedStatement.setInt(1, C_WaveSortCriteria_ID);
      rs = cPreparedStatement.executeQuery();
      boolean firstColumn = true;
      while (rs.next()) {
        if (firstColumn) {
          orderBy = orderBy + "ORDER BY ";
          firstColumn = false;
        } else {
          orderBy = orderBy + ",";
        } 
        MInfoColumn infoColumn = new MInfoColumn(ctx, rs.getInt(1), null);
        String orderByInfo = infoColumn.getSelectClause();
        int idx = orderByInfo.indexOf("DISTINCT");
        if (idx >= 0)
          orderByInfo = orderByInfo.substring(idx + 8); 
        orderBy = orderBy + orderByInfo;
        String orderCriteria = rs.getString(2);
        if (orderCriteria != null) {
          if (orderCriteria.equals("A")) {
            orderBy = orderBy + " ASC ";
            continue;
          } 
          if (orderCriteria.equals("D"))
            orderBy = orderBy + " DESC "; 
        } 
      } 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return orderBy;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
}
