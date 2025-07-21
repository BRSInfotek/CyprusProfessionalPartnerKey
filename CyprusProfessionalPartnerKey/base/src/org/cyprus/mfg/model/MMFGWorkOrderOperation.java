package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class MMFGWorkOrderOperation extends X_MFG_WorkOrderOperation {
  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderOperation.class);
  
  private static final long serialVersionUID = 1L;
  
  private boolean m_IsProcessCalled = false;
  
//  public MMFGWorkOrderOperation(Ctx ctx, int MFG_WorkOrderOperation_ID, Trx trx) {
//    super(ctx, MFG_WorkOrderOperation_ID, trx);
//    this.workcenter = null;
//    this.headerInfo = null;
//    if (M_WorkOrderOperation_ID == 0) {
//      setProcessed(false);
//      setQtyAssembled(BigDecimal.ZERO);
//      setQtyScrapped(BigDecimal.ZERO);
//      setQtyQueued(BigDecimal.ZERO);
//      setQtyRun(BigDecimal.ZERO);
//    } 
//  }
//  
//  public MMFGWorkOrderOperation(Ctx ctx, ResultSet rs, Trx trx) {
//    super(ctx, rs, trx);
//    this.workcenter = null;
//    this.headerInfo = null;
//  }
  
  public MMFGWorkOrderOperation(Properties ctx, int MFG_WorkOrderOperation_ID, String trxName) {
	    super(ctx, MFG_WorkOrderOperation_ID, trxName);
	    this.workcenter = null;
	    this.headerInfo = null;
	    if (MFG_WorkOrderOperation_ID == 0) {
	      setProcessed(false);
	      setQtyAssembled(BigDecimal.ZERO);
	      setQtyScrapped(BigDecimal.ZERO);
	      setQtyQueued(BigDecimal.ZERO);
	      setQtyRun(BigDecimal.ZERO);
	    } 
	  }
	  
	  public MMFGWorkOrderOperation(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	    this.workcenter = null;
	    this.headerInfo = null;
	  }
  
  private static CLogger s_log = CLogger.getCLogger(MMFGWorkOrderOperation.class);
  
  MMFGWorkCenter workcenter;
  
  private MMFGWorkOrder headerInfo;
  
  public MMFGWorkOrderOperation(Properties ctx, String trxName, int MFG_WorkOrder_ID, int MFG_Operation_ID, int MFG_StandardOperation_ID, int C_UOM_ID, BigDecimal SetupTime, BigDecimal UnitRuntime, String Description, int SeqNo, boolean isActive, boolean IsHazardous, boolean IsPermitRequired, boolean IsOptional, boolean isprocesscalled) throws Exception {
    this(ctx, 0, trxName);
    if (MFG_WorkOrder_ID == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_WorkOrder_ID(MFG_WorkOrder_ID);
   // MMFGWorkOrder workorder = new MMFGWorkOrder(ctx, MFG_WorkOrder_ID, trx);
    MMFGWorkOrder workorder = new MMFGWorkOrder(ctx, MFG_WorkOrder_ID, trxName);
    setClientOrg(workorder.getAD_Client_ID(), workorder.getAD_Org_ID());
    this.workcenter = MMFGWorkCenter.getDefaultWorkCenter(ctx, workorder.getM_Warehouse_ID());
    setMFG_WorkCenter_ID(this.workcenter.getMFG_WorkCenter_ID());
    if (workorder.getSupervisor_ID() != 0)
      setSupervisor_ID(workorder.getSupervisor_ID()); 
    setMFG_Operation_ID(MFG_Operation_ID);
    setMFG_StandardOperation_ID(MFG_StandardOperation_ID);
    setC_UOM_ID(C_UOM_ID);
    setSetupTime(SetupTime);
    setUnitRuntime(UnitRuntime);
    setDescription(Description);
    setSeqNo(SeqNo);
    setIsActive(isActive);
    setIsHazardous(IsHazardous);
    setIsPermitRequired(IsPermitRequired);
    setIsOptional(IsOptional);
    this.m_IsProcessCalled = isprocesscalled;
  }
  
  public MMFGWorkOrderOperation(Properties ctx, String trxName, int MFG_WorkOrder_ID, MMFGRoutingOperation ro, boolean isprocesscalled) {
    this(ctx, 0, trxName);
    if (MFG_WorkOrder_ID == 0)
      throw new IllegalArgumentException("Header not saved"); 
    setMFG_WorkOrder_ID(MFG_WorkOrder_ID);
    MMFGWorkOrder workorder = new MMFGWorkOrder(ctx, MFG_WorkOrder_ID, trxName);
    setClientOrg(workorder.getAD_Client_ID(), workorder.getAD_Org_ID());
    if (workorder.getSupervisor_ID() != 0)
      setSupervisor_ID(workorder.getSupervisor_ID()); 
    setMFG_WorkCenter_ID(ro.getMFG_WorkCenter_ID());
    setMFG_Operation_ID(ro.getMFG_Operation_ID());
    setMFG_StandardOperation_ID(ro.getMFG_StandardOperation_ID());
    setC_UOM_ID(ro.getC_UOM_ID());
    setSetupTime(ro.getSetupTime());
    setUnitRuntime(ro.getUnitRuntime());
    setDescription(ro.getDescription());
    setSeqNo(ro.getSeqNo());
    setIsActive(ro.isActive());
    setIsHazardous(ro.isHazardous());
    setIsPermitRequired(ro.isPermitRequired());
    setIsOptional(ro.isOptional());
    this.m_IsProcessCalled = isprocesscalled;
  }
  
  public static MMFGWorkOrderOperation[] getOfWorkOrder(MMFGWorkOrder workorder, String whereClause, String orderClause) {
    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID=? ");
    if (whereClause != null)
      sqlstmt.append("AND ").append(whereClause); 
    if (orderClause != null)
      sqlstmt.append(" ORDER BY ").append(orderClause); 
    String sql = sqlstmt.toString();
    ArrayList<MMFGWorkOrderOperation> list = new ArrayList<MMFGWorkOrderOperation>();
//    CPreparedStatement cPreparedStatement = DB.prepareStatement(sql, workorder.get_Trx());
    CPreparedStatement cPreparedStatement =null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, workorder.get_TrxName());
      cPreparedStatement.setInt(1, workorder.getMFG_WorkOrder_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MMFGWorkOrderOperation(workorder.getCtx(), rs, workorder.get_TrxName())); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException e) {
      s_log.log(Level.SEVERE, sql, e);
      return null;
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	DB.close(rs);
        DB.close((Statement)cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    } 
    MMFGWorkOrderOperation[] retValue = new MMFGWorkOrderOperation[list.size()];
    list.toArray(retValue);
    return retValue;
  }
  
 // @UICallout
  public void setMFG_StandardOperation_ID(String oldMFG_StandardOperation_ID, String newMFG_StandardOperation_ID, int windowNo) throws Exception {
    if (newMFG_StandardOperation_ID == null || newMFG_StandardOperation_ID.trim().length() == 0)
      return; 
    int MFG_StandardOperation_ID = Integer.parseInt(newMFG_StandardOperation_ID);
    if (0 == MFG_StandardOperation_ID)
      return; 
    String sql = "SELECT MFG_Operation_ID, MFG_WorkCenter_ID, Description, SetupTime, UnitRunTime, C_UOM_ID, IsPermitRequired, IsHazardous FROM MFG_StandardOperation WHERE MFG_StandardOperation_ID = ?";
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
    	cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, MFG_StandardOperation_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        setMFG_Operation_ID(rs.getInt(1));
        setMFG_WorkCenter_ID(rs.getInt(2));
        setDescription(rs.getString(3));
        setSetupTime(rs.getBigDecimal(4));
        setUnitRuntime(rs.getBigDecimal(5));
        setC_UOM_ID(rs.getInt(6));
        setIsPermitRequired(rs.getString(7).equals("Y"));
        setIsHazardous(rs.getString(8).equals("Y"));
      } 
    } catch (Exception e) {
      log.log(Level.SEVERE, sql, e);
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	DB.close(rs);
        DB.close((Statement)cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    } 
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
    String sql = "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE SeqNo = ? AND MFG_WorkOrder_ID = ?";
    int MFG_WorkOrderOperation_ID = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getSeqNo()), Integer.valueOf(getMFG_WorkOrder_ID()) });
    if (MFG_WorkOrderOperation_ID != -1 && MFG_WorkOrderOperation_ID != getMFG_WorkOrderOperation_ID()) {
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
      MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
      int no = DB.getSQLValue(get_TrxName(), "SELECT 1 FROM MFG_WorkCenter wc WHERE wc.MFG_WorkCenter_ID=? AND wc.M_Warehouse_ID=?", new Object[] { Integer.valueOf(getMFG_WorkCenter_ID()), Integer.valueOf(wo.getM_Warehouse_ID()) });
      /**
       *   Need to check logic as below in condition with Surya  @author Mukesh @Date 20220713  
       */
//            if (no == -1) {
//              log.saveError("Error", Msg.parseTranslation(getCtx(), "@WorkCenterWarehouseMismatch@"));
//              return false;
//            }
      
    } 
    return true;
  }
  
  protected boolean afterSave(boolean newRecord, boolean success) {
    if (!this.m_IsProcessCalled && getMFG_StandardOperation_ID() != 0 && newRecord) {
      MMFGStandardOperationResource[] resources = MMFGStandardOperationResource.getResourceLines(new MMFGStandardOperation(getCtx(), getMFG_StandardOperation_ID(), get_TrxName()));
      for (MMFGStandardOperationResource resource : resources) {
        MMFGWorkOrderResource workOrderOpRes = new MMFGWorkOrderResource(getCtx(), get_TrxName(), this, resource.getM_Product_ID(), resource.getC_UOM_ID(), resource.getQtyRequired(), resource.getBasisType(), resource.getChargeType(), resource.getSeqNo(), resource.isActive());
        if (!workOrderOpRes.save()) {
          log.saveError("Error", Msg.translate(getCtx(), "Error while copying the Resources"));
          return false;
        } 
      } 
      log.fine("#" + resources.length);
      if (getMFG_StandardOperation_ID() != 0) {
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
    } 
    return true;
  }
  
  public void setIsProcessCalled(boolean processCalled) {
    this.m_IsProcessCalled = processCalled;
  }
  
  public void setHeaderInfo(MMFGWorkOrder headerInfo) {
    this.headerInfo = headerInfo;
  }
  
  public MMFGWorkOrder getHeaderInfo() {
    return this.headerInfo;
  }
  
  //@UICallout
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
      log.fine("Corrected UnitRuntime Scale UOM=" + getC_UOM_ID() + "; UnitRuntime=" + UnitRuntime + "->" + UnitRuntime1);
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
    BigDecimal SetupTime = getSetupTime();
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      log.fine("Corrected Setup Time Scale UOM=" + getC_UOM_ID() + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      setSetupTime(SetupTime);
    } 
  }
  
 // @UICallout
  public void setUnitRuntime(String oldUnitRuntime, String newUnitRuntime, int windowNo) throws Exception {
    if (newUnitRuntime == null || 0 == newUnitRuntime.trim().length())
      return; 
    if (0 == getC_UOM_ID())
      return; 
    BigDecimal UnitRuntime = getUnitRuntime();
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      log.fine("Corrected UnitRuntime Scale UOM=" + getC_UOM_ID() + "; UnitRuntime=" + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      setUnitRuntime(UnitRuntime);
    } 
  }
}

