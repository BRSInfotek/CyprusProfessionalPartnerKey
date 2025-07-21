package org.cyprus.mrp.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

//import org.cyprusbrs.model.X_MRP_Forecast;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MMRPForecast extends X_MRP_Forecast {
  private static final CLogger log = CLogger.getCLogger(MMRPForecast.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPForecast.class);
  
  public MMRPForecast(Properties ctx, int MRP_Forecast_ID, String trx) {
    super(ctx, MRP_Forecast_ID, trx);
  }
  
  public MMRPForecast(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CMRP");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (getAD_Org_ID() == 0) {
      log.saveError("Error", Msg.translate(getCtx(), "Org0NotAllowed"));
      return false;
    } 
    if (getAD_Client_ID() == 0)
      return false; 
    if (newRecord == true) {
      CPreparedStatement cPreparedStatement = null;
      boolean nameExists = false;
      String sql = "SELECT * FROM MRP_Forecast WHERE Name = ?  AND AD_Client_ID = ? ";
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
        cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
        cPreparedStatement.setString(1, getName());
        cPreparedStatement.setInt(2, getAD_Client_ID());
         rs = cPreparedStatement.executeQuery();
        if (rs.next())
          nameExists = true; 
        rs.close();
        cPreparedStatement.close();
        cPreparedStatement = null;
      } catch (Exception e) {
        s_log.log(Level.SEVERE, sql, e);
      } 
      try {
        if (cPreparedStatement != null)
        {
          cPreparedStatement.close(); 
        cPreparedStatement = null;
        }
        if(rs != null)
        {
        	rs.close();
        	rs = null;
        }
      } catch (Exception e) {
        cPreparedStatement = null;
        rs = null;
      } 
      if (nameExists) {
        log.saveError("Error", Msg.translate(getCtx(), "ForecastNameNotUnique"));
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean beforeDelete() {
    if (isPreviouslyFrozen()) {
      log.saveError(Msg.translate(getCtx(), "ForecastPreviouslyFrozen"), "");
      return false;
    } 
    if (isFrozen())
      return false; 
    return true;
  }
  // Updated by Anshul @30-111-2022
  private MMRPForecastLine[] 	m_lines = null;
  
  /**
	 * 	Set Processed.
	 * 	Propagate to Lines/Taxes
	 *	@param processed processed
	 */
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		String set = "SET Processed='"
			+ (processed ? "Y" : "N")
			+ "' WHERE MRP_Forecast_ID=" + getMRP_Forecast_ID();
		int noLine = DB.executeUpdateEx("UPDATE MRP_ForecastLine " + set, get_TrxName());
		m_lines = null;
		log.fine("setProcessed - " + processed + " - Lines=" + noLine);
	}	//	setProcessed
}

