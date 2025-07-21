package org.cyprus.wms.model;

import java.math.BigDecimal;
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
//import org.cyprusbrs.model.X_M_WarehouseTaskMA;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSWarehouseTaskMA extends X_WMS_WarehouseTaskMA {
  private static final CLogger log = CLogger.getCLogger(MWMSWarehouseTaskMA.class);
  
  private static final long serialVersionUID = 1L;
  
  public static MWMSWarehouseTaskMA[] get(Properties ctx, int WMS_WarehouseTask_ID, String trx) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    ArrayList<MWMSWarehouseTaskMA> list = new ArrayList<MWMSWarehouseTaskMA>();
    String sql = "SELECT * FROM WMS_WarehouseTaskMA WHERE WMS_WarehouseTask_ID=? ORDER BY M_AttributeSetInstance_ID";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, WMS_WarehouseTask_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MWMSWarehouseTaskMA(ctx, rs, trx)); 
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    MWMSWarehouseTaskMA[] retValue = new MWMSWarehouseTaskMA[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
  public static int deleteWarehouseTaskMA(int WMS_WarehouseTask_ID, String trx) {
    String sql = "DELETE FROM WMS_WarehouseTaskMA ma WHERE  WMS_WarehouseTask_ID=" + WMS_WarehouseTask_ID;
    return DB.executeUpdateEx( sql, new Object[0],trx);
  }
  
  private static CLogger s_log = CLogger.getCLogger(MWMSWarehouseTaskMA.class);
  
  public MWMSWarehouseTaskMA(Properties ctx, int M_WarehouseTaskMA_ID, String trx) {
    super(ctx, M_WarehouseTaskMA_ID, trx);
    if (M_WarehouseTaskMA_ID != 0)
      throw new IllegalArgumentException("Multi-Key"); 
  }
  
  public MWMSWarehouseTaskMA(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public MWMSWarehouseTaskMA(MWMSWarehouseTask parent, int M_AttributeSetInstance_ID, BigDecimal MovementQty, BigDecimal QtyDedicated) {
    this(parent.getCtx(), 0, parent.get_TrxName());
    setClientOrg((PO)parent);
    setWMS_WarehouseTask_ID(parent.getWMS_WarehouseTask_ID());
    setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
    setMovementQty(MovementQty);
    setQtyDedicated(QtyDedicated);
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("MWMSWarehouseTaskMA[");
    sb.append("WMS_WarehouseTask_ID=").append(getWMS_WarehouseTask_ID()).append(",M_AttributeSetInstance_ID=").append(getM_AttributeSetInstance_ID()).append(", MovementQty=").append(getMovementQty()).append(", QtyDedicated=").append(getQtyDedicated()).append("]");
    return sb.toString();
  }
}
