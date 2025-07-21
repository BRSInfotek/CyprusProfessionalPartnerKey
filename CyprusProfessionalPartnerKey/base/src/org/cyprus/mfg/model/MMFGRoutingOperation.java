package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MAttachment;
import org.cyprusbrs.framework.MAttachmentEntry;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class MMFGRoutingOperation extends X_MFG_RoutingOperation {
  private static final CLogger log = CLogger.getCLogger(MMFGRoutingOperation.class);
  
  private static CLogger s_log = CLogger.getCLogger(MMFGRoutingOperation.class);
  
  private static final long serialVersionUID = 1L;
  
//  public MMFGRoutingOperation(Properties ctx, int MFG_RoutingOperation_ID, Trx trx) {
//    super(ctx, MFG_RoutingOperation_ID, trx);
  
  public MMFGRoutingOperation(Properties ctx, int MFG_RoutingOperation_ID, String trxName) {
	    super(ctx, MFG_RoutingOperation_ID, trxName);
    if (MFG_RoutingOperation_ID == 0) {
      setIsActive(true);
      setIsHazardous(false);
      setIsOptional(false);
      setIsPermitRequired(false);
    } 
  }
  
//  public MRoutingOperation(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//  }
  
  public MMFGRoutingOperation(Properties ctx, ResultSet rs, String trxName ) {
	    super(ctx, rs, trxName);
	  }
  
  public static MMFGRoutingOperation[] getOperationLines(MMFGRouting routing, String whereClause, String orderClause) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT * FROM MFG_RoutingOperation WHERE MFG_Routing_ID=? AND IsActive='Y' ";
    if (whereClause != null && whereClause.length() != 0)
      sql = sql + " AND " + whereClause; 
    if (orderClause != null && orderClause.length() != 0)
      sql = sql + " " + orderClause; 
    ArrayList<MMFGRoutingOperation> list = new ArrayList<MMFGRoutingOperation>();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
     // cPreparedStatement = DB.prepareStatement(sql, routing.get_Trx());
      cPreparedStatement = DB.prepareStatement(sql, routing.get_TrxName());
      cPreparedStatement.setInt(1, routing.getMFG_Routing_ID());
       rs = cPreparedStatement.executeQuery();
      while (rs.next())
//        list.add(new MRoutingOperation(routing.getCtx(), rs, routing.get_Trx())); 
          list.add(new MMFGRoutingOperation(routing.getCtx(), rs, routing.get_TrxName())); 

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
//    try {
//      if (cPreparedStatement != null)
//        cPreparedStatement.close(); 
//      cPreparedStatement = null;
//    } catch (Exception e) {
//      cPreparedStatement = null;
//    } 
    MMFGRoutingOperation[] retValue = new MMFGRoutingOperation[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
 // @UICallout
  public void setMFG_StandardOperation_ID(String oldMFG_StandardOperation_ID, String newMFG_StandardOperation_ID, int windowNo) throws Exception {
    CPreparedStatement cPreparedStatement = null;
    if (newMFG_StandardOperation_ID == null || newMFG_StandardOperation_ID.trim().length() == 0)
      return; 
    int MFG_StandardOperation_ID = Integer.parseInt(newMFG_StandardOperation_ID);
    String sql = "SELECT MFG_Operation_ID, MFG_WorkCenter_ID, Description, Help, SetupTime, UnitRunTime, C_UOM_ID, IsPermitRequired, IsHazardous FROM MFG_StandardOperation WHERE MFG_StandardOperation_ID = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, MFG_StandardOperation_ID);
       rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        setMFG_Operation_ID(rs.getInt(1));
        setMFG_WorkCenter_ID(rs.getInt(2));
        setDescription(rs.getString(3));
        setHelp(rs.getString(4));
        setSetupTime(rs.getBigDecimal(5));
        setUnitRuntime(rs.getBigDecimal(6));
        setC_UOM_ID(rs.getInt(7));
        setIsPermitRequired(rs.getString(8).equals("Y"));
        setIsHazardous(rs.getString(9).equals("Y"));
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      log.log(Level.SEVERE, sql, e);
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
//    try {
//      if (cPreparedStatement != null)
//        cPreparedStatement.close(); 
//      cPreparedStatement = null;
//    } catch (Exception e) {
//      cPreparedStatement = null;
//    } 
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getSeqNo() < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SeqNo@ < 0"));
      return false;
    } 
    if (getC_UOM_ID() != 0) {
      MUOM uom = MUOM.get(getCtx(), getC_UOM_ID());
      if (!uom.isDay() && !uom.isHour()) {
        log.saveError("Error", Msg.translate(getCtx(), "UOMNotAllowed"));
        return false;
      } 
    } 
    String sql = "SELECT MFG_RoutingOperation_ID FROM MFG_RoutingOperation WHERE SeqNo = ? AND MFG_Routing_ID = ?";
    int MFG_RoutingOperation_ID = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getSeqNo()), Integer.valueOf(getMFG_Routing_ID()) });
    if (MFG_RoutingOperation_ID != -1 && MFG_RoutingOperation_ID != getMFG_RoutingOperation_ID()) {
      log.saveError("DuplicateSequence", Msg.translate(getCtx(), Integer.toString(getSeqNo())));
      return false;
    } 
    if (getSetupTime().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@SetupTime@ < 0"));
      return false;
    } 
    if (getUnitRuntime().compareTo(Env.ZERO) < 0) {
      log.saveError("Error", Msg.parseTranslation(getCtx(), "@UnitRuntime@ < 0"));
      return false;
    } 
    if (newRecord || is_ValueChanged("MFG_WorkCenter_ID")) {
      MMFGRouting r = new MMFGRouting(getCtx(), getMFG_Routing_ID(), get_TrxName());
      int no = DB.getSQLValue(get_TrxName(), "SELECT 1 FROM MFG_WorkCenter wc WHERE wc.MFG_WorkCenter_ID=? AND wc.M_Warehouse_ID=?", new Object[] { Integer.valueOf(getMFG_WorkCenter_ID()), Integer.valueOf(r.getM_Warehouse_ID()) });
      if (no == -1) {
        log.saveError("Error", Msg.parseTranslation(getCtx(), "@WorkCenterWarehouseMismatch@"));
        return false;
      } 
    } 
    return true;
  }
  
  protected boolean afterSave(boolean newRecord, boolean success) {
    if (getMFG_StandardOperation_ID() != 0 && newRecord) {
      MMFGStandardOperationResource[] resources = MMFGStandardOperationResource.getResourceLines(new MMFGStandardOperation(getCtx(), getMFG_StandardOperation_ID(), get_TrxName()));
      for (MMFGStandardOperationResource resource : resources) {
        MMFGRoutingOperationResource routingOpRes = new MMFGRoutingOperationResource(this, resource.getM_Product_ID(), resource.getC_UOM_ID(), resource.getQtyRequired(), resource.getBasisType(), resource.getChargeType(), resource.getSeqNo(), resource.isActive());
        if (!routingOpRes.save()) {
          log.saveError("Error", Msg.translate(getCtx(), "Error while copying the Resources"));
          return false;
        } 
      } 
      log.fine("#" + resources.length);
      MMFGStandardOperation op = new MMFGStandardOperation(getCtx(), getMFG_StandardOperation_ID(), get_TrxName());
      MAttachment attachment = op.getAttachment();
      if (attachment != null) {
        MAttachmentEntry[] entries = attachment.getEntries();
        MAttachment thisAttach = createAttachment();
        for (MAttachmentEntry entry : entries)
          thisAttach.addEntry(entry); 
        thisAttach.save(get_TrxName());
      } 
    } 
    return true;
  }
  
 // @UICallout
  public void setC_UOM_ID(String oldC_UOM_ID, String newC_UOM_ID, int windowNo) throws Exception {
    if (newC_UOM_ID == null || newC_UOM_ID.trim().length() == 0)
      return; 
    int C_UOM_ID = Integer.parseInt(newC_UOM_ID);
    if (0 == C_UOM_ID)
      return; 
    BigDecimal SetupTime = getSetupTime();
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(getCtx(), C_UOM_ID), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      log.fine("Corrected Setup Time Scale UOM=" + getC_UOM_ID() + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      setSetupTime(SetupTime);
    } 
    BigDecimal UnitRuntime = getUnitRuntime();
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(getCtx(), C_UOM_ID), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      log.fine("Corrected Runtime Per Unit Scale UOM=" + getC_UOM_ID() + "; Runtime Per Unit=" + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      setUnitRuntime(UnitRuntime);
    } 
  }
  
 // @UICallout
  public void setSetupTime(String oldSetupTime, String newSetupTime, int windowNo) throws Exception {
    if (newSetupTime == null || 0 == newSetupTime.trim().length())
      return; 
    if (0 == getC_UOM_ID())
      return; 
    BigDecimal SetupTime = new BigDecimal(newSetupTime);
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      log.fine("Corrected Setup Time Scale UOM=" + getC_UOM_ID() + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      setSetupTime(SetupTime);
    } 
  }
  
  //@UICallout
  public void setUnitRuntime(String oldUnitRuntime, String newUnitRuntime, int windowNo) throws Exception {
    if (newUnitRuntime == null || 0 == newUnitRuntime.trim().length())
      return; 
    if (0 == getC_UOM_ID())
      return; 
    BigDecimal UnitRuntime = new BigDecimal(newUnitRuntime);
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      log.fine("Corrected Runtime Per Unit Scale UOM=" + getC_UOM_ID() + "; Runtime Per Unit =" + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      setUnitRuntime(UnitRuntime);
    } 
  }
}

