package org.cyprus.mrp.model;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

//import org.cyprusbrs.model.X_MRP_PlannedOrder;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMRPPlannedOrder extends X_MRP_PlannedOrder {
  private static final CLogger log = CLogger.getCLogger(MMRPPlannedOrder.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPPlannedOrder.class);
  
  public MMRPPlannedOrder(Properties ctx, int MRP_PlannedOrder_ID, String trx) {
    super(ctx, MRP_PlannedOrder_ID, trx);
  }
  
  public MMRPPlannedOrder(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public void setParentMRPPlannedOrder(MMRPPlannedDemand plannedDemand, int planRunID, String trx, Properties ctx) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT MRP_PlannedOrder_ID, MRP_PlannedOrder_Root_ID FROM MRP_PlannedOrder WHERE MRP_PlannedDemand_ID = ?";
    PreparedStatement pstmt = null;
    ResultSet rs  = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, plannedDemand.getMRP_PlannedDemand_Parent_ID());
       rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        setMRP_PlannedOrder_Parent_ID(rs.getInt(1));
        if (plannedDemand.getLevelNo() == 1) {
          setMRP_PlannedOrder_Root_ID(rs.getInt(1));
        } else {
          setMRP_PlannedOrder_Root_ID(rs.getInt(2));
        } 
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } 
    finally
	{
		DB.close(rs, pstmt);
		rs = null; pstmt = null;
	}
//    try {
//      if (cPreparedStatement != null)
//        cPreparedStatement.close(); 
//      cPreparedStatement = null;
//    } catch (Exception e) {
//      cPreparedStatement = null;
//    } 
  }
}

