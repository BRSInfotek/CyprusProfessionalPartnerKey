package org.cyprus.mrp.model;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

//import org.cyprusbrs.model.X_MRP_PlannedAvailability;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMRPPlannedAvailability extends X_MRP_PlannedAvailability {
  private static final CLogger log = CLogger.getCLogger(MMRPPlannedAvailability.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPPlannedAvailability.class);
  
  public MMRPPlannedAvailability(Properties ctx, int ignored, String trx) {
    super(ctx, ignored, trx);
    if (ignored != 0)
      throw new IllegalArgumentException("Multi-Key"); 
  }
  
  public MMRPPlannedAvailability(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public static MMRPPlannedAvailability getPlannedAvailability(int periodID, int productID, int runID, String trx, Properties ctx) {
    CPreparedStatement cPreparedStatement = null;
    MMRPPlannedAvailability plannedAvailability = null;
    String sql = "SELECT * FROM MRP_PlannedAvailability WHERE MRP_PlanRun_ID = ? AND M_Product_ID = ? AND C_Period_ID  = ? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, runID);
      cPreparedStatement.setInt(2, productID);
      cPreparedStatement.setInt(3, periodID);
       rs = cPreparedStatement.executeQuery();
      if (rs.next())
        plannedAvailability = new MMRPPlannedAvailability(ctx, rs, trx); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
    return plannedAvailability;
  }
}

