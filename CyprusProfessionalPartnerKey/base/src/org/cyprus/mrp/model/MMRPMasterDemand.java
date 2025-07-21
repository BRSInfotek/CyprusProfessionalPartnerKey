package org.cyprus.mrp.model;


//import com.cyprusbrs.client.SysEnv;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.Query;
//import org.cyprusbrs.model.X_MRP_MasterDemand;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MMRPMasterDemand extends X_MRP_MasterDemand {
  private static final CLogger log = CLogger.getCLogger(MMRPMasterDemand.class);
  
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MMRPMasterDemand.class);
  
  public MMRPMasterDemand(Properties ctx, int MRP_MasterDemand_ID, String trx) {
    super(ctx, MRP_MasterDemand_ID, trx);
  }
  
  public MMRPMasterDemand(Properties ctx, ResultSet rs, String trx) {
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
      String sql = "SELECT * FROM MRP_MasterDemand WHERE Name = ?  AND AD_Client_ID = ? ";
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
      finally
      {
    	  DB.close(rs, cPreparedStatement);
    	  rs = null; cPreparedStatement = null;
      }
//      try {
//        if (cPreparedStatement != null)
//          cPreparedStatement.close(); 
//        cPreparedStatement = null;
//      } catch (Exception e) {
//        cPreparedStatement = null;
//      } 
      if (nameExists) {
        log.saveError("Error", Msg.translate(getCtx(), "MasterDemandNameNotUnique"));
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean beforeDelete() {
    if (isPreviouslyFrozen()) {
      log.saveError(Msg.translate(getCtx(), "MasterDemandPreviouslyFrozen"), "");
      return false;
    } 
    return true;
  }
  
//updated by anshul
	
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
			+ "' WHERE MRP_MasterDemand_ID=" + getMRP_MasterDemand_ID();
		int noLine = DB.executeUpdateEx("UPDATE MRP_MasterDemandLine " + set, get_TrxName());
		log.fine("setProcessed - " + processed + " - Lines=" + noLine);
	}	//	setProcessed
	
	//updated by anshul
	  					
		private MMRPMasterDemandLine[]		m_lines = null;
	  /**
		 * 	Get Lines
		 *	@param requery requery
		 *	@return lines
		 */
		public MMRPMasterDemandLine[] getLines (boolean requery)
		{
			if (m_lines != null && !requery)
			{
				set_TrxName(m_lines, get_TrxName());
				return m_lines;
			}
			List<MMRPMasterDemandLine> list = new Query(getCtx(), I_MRP_MasterDemandLine.Table_Name, "MRP_MasterDemand_ID=?", get_TrxName())
					.setParameters(getMRP_MasterDemand_ID())
					//			.setOrderBy(MMRPMasterDemandLine.COLUMNNAME_Line)
					.list();

			m_lines = new MMRPMasterDemandLine[list.size ()];
			list.toArray (m_lines);
			return m_lines;
		}	//	getLines
}

