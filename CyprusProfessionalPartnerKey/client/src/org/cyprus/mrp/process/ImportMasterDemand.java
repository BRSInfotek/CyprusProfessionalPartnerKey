package org.cyprus.mrp.process;


//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprus.mrp.model.MMRPMasterDemand;
import org.cyprus.mrp.model.MMRPMasterDemandLine;
import org.cyprus.mrp.model.X_I_MasterDemand;
import org.cyprusbrs.framework.MPeriod;
//import org.cyprusbrs.model.X_I_MasterDemand;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.TimeUtil;
import org.cyprusbrs.util.ValueNamePair;

public class ImportMasterDemand extends SvrProcess {
  private int m_AD_Client_ID = 0;
  
  private int m_AD_Org_ID = 0;
  
  private boolean m_deleteOldImported = false;
  
  private Timestamp m_DateValue = null;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("AD_Client_ID")) {
          this.m_AD_Client_ID = ((BigDecimal)element.getParameter()).intValue();
        } else if (name.equals("AD_Org_ID")) {
          this.m_AD_Org_ID = ((BigDecimal)element.getParameter()).intValue();
        } else if (name.equals("DeleteOldImported")) {
          this.m_deleteOldImported = "Y".equals(element.getParameter());
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    if (this.m_DateValue == null)
      this.m_DateValue = new Timestamp(System.currentTimeMillis()); 
  }
  
  protected String doIt() throws Exception {
//    SysEnv se = SysEnv.get("CMRP");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    StringBuffer sql = null;
    int no = 0;
    String clientCheck = " AND AD_Client_ID=" + this.m_AD_Client_ID;
    if (this.m_deleteOldImported) {
      sql = (new StringBuffer("DELETE FROM I_MasterDemand WHERE I_IsImported='Y'")).append(clientCheck);
     // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
      no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
      this.log.fine("Delete Old Imported =" + no);
    } 
    sql = (new StringBuffer("UPDATE I_MasterDemand SET AD_Client_ID = COALESCE (AD_Client_ID,")).append(this.m_AD_Client_ID).append("), AD_Org_ID = COALESCE (AD_Org_ID,").append(this.m_AD_Org_ID).append("), IsActive = COALESCE (IsActive, 'Y'), Created = COALESCE (Created, SysDate), CreatedBy = COALESCE (CreatedBy, 0), Updated = COALESCE (Updated, SysDate), UpdatedBy = COALESCE (UpdatedBy, 0), I_ErrorMsg = NULL, I_IsImported = 'N' WHERE I_IsImported<>'Y' OR I_IsImported IS NULL");
  //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.info("Reset=" + no);
    String ts = DB.isPostgreSQL() ? "COALESCE(I_ErrorMsg,'')" : "I_ErrorMsg";
    sql = (new StringBuffer("UPDATE I_MasterDemand md SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Invalid Org, '" + "WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0" + " OR EXISTS (SELECT * FROM AD_Org oo WHERE md.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))" + " AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Invalid Org=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET MRP_Plan_ID=(SELECT MRP_Plan_ID FROM MRP_Plan P WHERE P.Name = i.Name2 AND P.IsActive='Y')WHERE MRP_Plan_ID IS NULL AND i.Name2 IS NOT NULL AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Set Plan from Plan Name=" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Fill Mandatory - Plan, ' " + "WHERE MRP_Plan_ID IS NULL AND Name2 IS NULL" + " AND I_IsImported<>'Y'")).append(clientCheck);
    //no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Fill Mandatory - Plan=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET Name2=(SELECT Name FROM MRP_Plan WHERE MRP_Plan_ID=i.MRP_Plan_ID) WHERE MRP_Plan_ID IS NOT NULL  AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Update PlanName from Plan =" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Enter a valid and active Plan Name (or) Fill Mandatory - Plan, ' " + "WHERE MRP_Plan_ID IS NULL AND Name2 IS NOT NULL AND NOT EXISTS(SELECT * FROM MRP_Plan WHERE MRP_Plan.Name = i.Name2 AND MRP_Plan.IsActive = 'Y') " + " AND I_IsImported<>'Y'")).append(clientCheck);
  //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Enter a valid Plan Name=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Fill Mandatory - Name, ' " + "WHERE Name IS NULL" + " AND I_IsImported<>'Y'")).append(clientCheck);
  //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Fill Mandatory - Name=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p WHERE i.ProductValue=p.Value AND i.AD_Client_ID=p.AD_Client_ID) WHERE M_Product_ID IS NULL AND ProductValue IS NOT NULL AND I_IsImported<>'Y'")).append(clientCheck);
    //no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Set Product from ProductKey=" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Fill Mandatory - Product, ' " + "WHERE M_Product_ID IS NULL AND ProductValue IS NULL" + " AND I_IsImported<>'Y'")).append(clientCheck);
  //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Fill Mandatory - Product=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET ProductValue = (SELECT Value FROM M_Product WHERE M_Product_ID=i.M_Product_ID)WHERE M_Product_ID IS NOT NULL AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Update ProductKey from Product=" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Enter a Valid ProductKey (or) Fill Mandatory - Product, ' " + "WHERE M_Product_ID IS NULL AND (ProductValue IS NOT NULL)" + " AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Invalid Product=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand md SET C_UOM_ID=(SELECT p.C_UOM_ID FROM M_Product p WHERE p.M_Product_ID=md.M_Product_ID) WHERE md.M_Product_ID IS NOT NULL AND (md.C_UOM_ID <> (SELECT p.C_UOM_ID FROM M_Product p WHERE p.M_Product_ID=md.M_Product_ID) OR C_UOM_ID IS NULL)  AND md.I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Set UOM from Product=" + no);
    if (no != 0)
      this.log.warning("Invalid UOM, Updated UOM to product primary UOM=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand md SET Qty=(SELECT ROUND(md.Qty,u.StdPrecision) FROM C_UOM u WHERE ( md.C_UOM_ID = u.C_UOM_ID))WHERE md.I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Set Qty Precision =" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET C_Period_ID=(SELECT MAX(C_Period_ID) FROM C_Period p WHERE  p.Name = i.PeriodName AND p.C_Year_ID IN (SELECT C_Year_ID FROM C_Year WHERE C_Year.C_Calendar_ID = (SELECT C_Calendar_ID FROM MRP_Plan WHERE  MRP_Plan.MRP_Plan_ID = i.MRP_Plan_ID))) WHERE C_Period_ID IS NULL AND MRP_Plan_ID IS NOT NULL AND i.PeriodName IS NOT NULL AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Set Period from PeriodName=" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Fill Mandatory - Period, ' " + "WHERE C_Period_ID IS NULL AND PeriodName IS NULL " + " AND I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Fill Mandatory - Period=" + no); 
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET PeriodName=(SELECT p.Name  FROM C_Period p WHERE p.C_Period_ID =  i.C_Period_ID AND p.C_Year_ID IN (SELECT C_Year_ID FROM C_Year WHERE C_Year.C_Calendar_ID = (SELECT C_Calendar_ID FROM MRP_Plan WHERE  MRP_Plan.MRP_Plan_ID = i.MRP_Plan_ID))) WHERE C_Period_ID IS NOT NULL AND MRP_Plan_ID IS NOT NULL  AND I_IsImported<>'Y'")).append(clientCheck);
  //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    this.log.fine("Update PeriodName from Period=" + no);
    sql = (new StringBuffer("UPDATE I_MasterDemand i SET I_IsImported='E', I_ErrorMsg=" + ts + "||'ERR=Enter a valid PeriodName (or) Fill Mandatory - Period, ' " + "WHERE i.C_Period_ID IS NULL AND i.PeriodName IS NOT NULL AND i.PeriodName NOT IN" + "(SELECT p.Name  FROM C_Period p WHERE p.C_Period_ID =  i.C_Period_ID AND p.C_Year_ID IN (Select C_Year_ID FROM C_Year WHERE " + "C_Year.C_Calendar_ID = (SELECT C_Calendar_ID FROM MRP_Plan WHERE  MRP_Plan.MRP_Plan_ID = i.MRP_Plan_ID)))" + " AND I_IsImported<>'Y'")).append(clientCheck);
 //   no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    if (no != 0)
      this.log.warning("Enter a valid PeriodName=" + no); 
    commit();
    int noInsert = 0;
    int noInsertLine = 0;
    int noUpdate = 0;
    int noUpdateLine = 0;
    sql = (new StringBuffer("SELECT * FROM I_MasterDemand WHERE I_IsImported='N'")).append(clientCheck).append(" ORDER BY MRP_Plan_ID, Name , M_Product_ID, C_Period_ID");
    CPreparedStatement cPreparedStatement = null;
    ResultSet rs = null;
    try {
       cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
       rs = cPreparedStatement.executeQuery();
      boolean MasterDemandExists = false;
      boolean MasterDemandLineExists = false;
      String oldName = "";
      MMRPMasterDemand MasterDemand = null;
      MMRPMasterDemandLine line = null;
      while (rs.next()) {
        X_I_MasterDemand imp = new X_I_MasterDemand(getCtx(), rs, get_TrxName());
        sql = (new StringBuffer("SELECT * FROM MRP_MasterDemand WHERE MRP_Plan_ID = " + imp.getMRP_Plan_ID() + " AND Name = '" + imp.getName() + "' ")).append(clientCheck).append(" ORDER BY MRP_Plan_ID, Name");
        CPreparedStatement cPreparedStatement1 = DB.prepareStatement(sql.toString(), get_TrxName());
        ResultSet rs1 = cPreparedStatement1.executeQuery();
        if (rs1.next()) {
          MasterDemandExists = true;
          MasterDemand = new MMRPMasterDemand(getCtx(), rs1, get_TrxName());
          if (MasterDemand.isFrozen()) {
            sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg='ERR=Cannot update MasterDemand " + MasterDemand.getName() + " which is frozen. ' " + "WHERE I_MasterDemand_ID =  " + imp.getI_MasterDemand_ID())).append(clientCheck);
           // DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
           DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
            commit();
            this.log.warning("Cannot update MasterDemand " + MasterDemand.getName() + " which is frozen.");
            continue;
          } 
        } else {
          MasterDemandExists = false;
          MasterDemand = new MMRPMasterDemand(getCtx(), 0, get_TrxName());
        } 
        imp.setAD_Client_ID(this.m_AD_Client_ID);
        imp.setAD_Org_ID(this.m_AD_Org_ID);
        if (!validatePlan(imp.getMRP_Plan_ID(), imp.getAD_Org_ID())) {
          String msg = "Plan is restricted to Org - Select a Plan which is defined under the Organization Provided";
          ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            msg = msg + " - " + pp.toStringX(); 
          imp.setI_ErrorMsg(msg);
          imp.save();
          continue;
        } 
        if (!validateProduct(imp.getAD_Org_ID(), imp.getM_Product_ID())) {
          String msg = "Invalid Product - Select a Product which is active and planned";
          ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            msg = msg + " - " + pp.toStringX(); 
          imp.setI_ErrorMsg(msg);
          imp.save();
          continue;
        } 
        if (!validatePeriod(imp.getMRP_Plan_ID(), imp.getC_Period_ID())) {
          String msg = "Invalid Period - Period  is either frozen or not included in the plan or not Active";
          ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            msg = msg + " - " + pp.toStringX(); 
          imp.setI_ErrorMsg(msg);
          imp.save();
          continue;
        } 
        oldName = imp.getName();
        if (oldName == null)
          oldName = ""; 
        MasterDemand.setClientOrg(imp.getAD_Client_ID(), imp.getAD_Org_ID());
        if (imp.getMRP_Plan_ID() != 0)
          MasterDemand.setMRP_Plan_ID(imp.getMRP_Plan_ID()); 
        if (imp.getName() != null)
          MasterDemand.setName(imp.getName()); 
        if (imp.getDescription() != null)
          MasterDemand.setDescription(imp.getDescription()); 
        if (imp.getHelp() != null)
          MasterDemand.setHelp(imp.getHelp()); 
        if (!MasterDemandExists) {
          MasterDemand.setIsFrozen(false);
          MasterDemand.setIsPreviouslyFrozen(false);
        } 
        if (!MasterDemand.save()) {
          String msg = "Could not save MasterDemand";
          ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            msg = msg + " - " + pp.toStringX(); 
          imp.setI_ErrorMsg(msg);
          imp.save();
          continue;
        } 
        if (MasterDemandExists == true) {
          noUpdate++;
        } else {
          noInsert++;
        } 
        imp.setMRP_MasterDemand_ID(MasterDemand.getMRP_MasterDemand_ID());
        if (MasterDemand.isPreviouslyFrozen()) {
          MPeriod period = new MPeriod(getCtx(), imp.getC_Period_ID(), get_TrxName());
          if (TimeUtil.getDay(System.currentTimeMillis()).after(period.getEndDate())) {
            sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='E', I_ErrorMsg='ERR=Cannot update/insert demand line for a previous period , plan is run. ' WHERE I_MasterDemand_ID =  " + imp.getI_MasterDemand_ID())).append(clientCheck);
           // DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
            DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
            commit();
            this.log.warning("Cannot update MasterDemand " + MasterDemand.getName() + " which is frozen.");
            continue;
          } 
        } 
        if (MasterDemandExists == true) {
          sql = (new StringBuffer("SELECT * FROM MRP_MasterDemandLine WHERE M_Product_ID = " + imp.getM_Product_ID() + " AND C_Period_ID = " + imp.getC_Period_ID() + " AND MRP_MasterDemand_ID = " + imp.getMRP_MasterDemand_ID())).append(clientCheck).append(" ORDER BY M_Product_ID, C_Period_ID");
          CPreparedStatement cPreparedStatement2 = DB.prepareStatement(sql.toString(), get_TrxName());
          ResultSet rsLine = cPreparedStatement2.executeQuery();
          if (rsLine.next()) {
            MasterDemandLineExists = true;
            line = new MMRPMasterDemandLine(getCtx(), rsLine, get_TrxName());
            if (line.isLineFrozen()) {
              sql = (new StringBuffer("UPDATE I_Forecast SET I_IsImported='E', I_ErrorMsg='ERR=MasterDemandLine which is Frozen cannot be updated. ' WHERE I_MasterDemand_ID =  " + imp.getI_MasterDemand_ID())).append(clientCheck);
             // DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
             DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
              commit();
              this.log.warning(MasterDemand.getName() + ": MasterDemandLine which is Frozen cannot be updated.");
              continue;
            } 
          } else {
            MasterDemandLineExists = false;
            line = new MMRPMasterDemandLine(MasterDemand);
          } 
          rsLine.close();
          cPreparedStatement2.close();
        } else {
          MasterDemandLineExists = false;
          line = new MMRPMasterDemandLine(MasterDemand);
        } 
        if (imp.getC_Period_ID() != 0)
          line.setC_Period_ID(imp.getC_Period_ID()); 
        if (imp.getM_Product_ID() != 0)
          line.setM_Product_ID(imp.getM_Product_ID()); 
        if (imp.getC_UOM_ID() != 0)
          line.setC_UOM_ID(imp.getC_UOM_ID()); 
        line.setQty(imp.getQty());
        if (!line.save()) {
          String msg = "Could not save MasterDemandLine";
          ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            msg = msg + " - " + pp.toStringX(); 
          imp.setI_ErrorMsg(msg);
          imp.save();
          continue;
        } 
        imp.setMRP_MasterDemandLine_ID(line.getMRP_MasterDemandLine_ID());
        imp.setI_IsImported(X_I_MasterDemand.I_ISIMPORTED_Yes);
        imp.setProcessed(true);
        if (imp.save())
          if (MasterDemandLineExists == true) {
            noUpdateLine++;
          } else {
            noInsertLine++;
          }  
        cPreparedStatement1.close();
        rs1.close();
      } 
      rs.close();
      cPreparedStatement.close();
    } catch (Exception e) {
      this.log.log(Level.SEVERE, "MasterDemand - " + sql.toString(), e);
    } 
    finally
    {
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    }
    sql = (new StringBuffer("UPDATE I_MasterDemand SET I_IsImported='N', Updated=SysDate WHERE I_IsImported<>'Y'")).append(clientCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
    addLog(0, null, new BigDecimal(no), "@Errors@");
    addLog(0, null, new BigDecimal(noInsert), "@MRP_MasterDemand_ID@: @Inserted@");
    addLog(0, null, new BigDecimal(noInsertLine), "@MRP_MasterDemandLine_ID@: @Inserted@");
    addLog(0, null, new BigDecimal(noUpdate), "@MRP_MasterDemand_ID@: @Updated@");
    addLog(0, null, new BigDecimal(noUpdateLine), "@MRP_MasterDemandLine_ID@: @Updated@");
    return "#" + noInsert + "/" + noInsertLine + "/" + noUpdate + "/" + noUpdateLine;
  }
  
  private boolean validatePlan(int PlanID, int OrgID) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT MRP_Plan_ID FROM MRP_Plan WHERE MRP_Plan_ID = ? AND AD_Org_ID=? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, PlanID);
      cPreparedStatement.setInt(2, OrgID);
      rs = cPreparedStatement.executeQuery();
      if (!rs.next()) {
        this.log.warning("Plan - Invalid, Plan is restricted to Org.Select a Plan which is defined under the Organization Provided");
        return false;
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        cPreparedStatement = null;
      } catch (Exception e) {
        cPreparedStatement = null;
      } 
      try {
        if (rs != null)
          rs.close(); 
        rs = null;
      } catch (Exception e) {
        rs = null;
      } 
    } 
    return true;
  }
  
  private boolean validatePeriod(int PlanID, int PeriodID) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT * FROM C_Period WHERE C_Period.IsActive='Y' AND PeriodType='S' AND C_Period.C_Year_ID IN  ( SELECT C_Year_ID FROM C_Year INNER JOIN MRP_Plan ON MRP_Plan.C_Calendar_ID = C_Year.C_Calendar_ID AND MRP_Plan.MRP_Plan_ID = ?  ) AND (? * 1000) + ?  BETWEEN ( SELECT (per1.C_Year_ID * 1000) + per1.PeriodNo FROM C_Period per1 WHERE per1.C_Period_ID = (SELECT MRP_Plan.C_Period_From_ID FROM MRP_Plan WHERE MRP_Plan_ID = ? ) ) AND (SELECT (per4.C_Year_ID * 1000) + per4.PeriodNo FROM C_Period per4 WHERE per4.C_Period_ID = (SELECT MRP_Plan.C_Period_To_ID FROM MRP_Plan WHERE MRP_Plan_ID = ? ) )";
    MPeriod period = new MPeriod(getCtx(), PeriodID, get_TrxName());
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, PlanID);
      cPreparedStatement.setInt(2, period.getC_Year_ID());
      cPreparedStatement.setInt(3, period.getPeriodNo());
      cPreparedStatement.setInt(4, PlanID);
      cPreparedStatement.setInt(5, PlanID);
      rs = cPreparedStatement.executeQuery();
      int PeriodExists = 0;
      if (rs.next())
        if (period.isActive()) {
          PeriodExists++;
        } else {
          this.log.warning("Period - Invalid, Period is not Active");
          return false;
        }  
      if (PeriodExists != 1) {
        this.log.warning("Period - Invalid, Period  is either frozen or not included in the plan");
        return false;
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        cPreparedStatement = null;
      } catch (Exception e) {
        cPreparedStatement = null;
      } 
      try {
        if (rs != null)
          rs.close(); 
        rs = null;
      } catch (Exception e) {
        rs = null;
      } 
    } 
    return true;
  }
  
  private boolean validateProduct(int OrgID, int ProductID) {
    CPreparedStatement cPreparedStatement = null;
    String sql = "SELECT M_Product_ID FROM M_Product WHERE  M_Product.AD_Org_ID IN (0, ?) AND M_Product.IsActive='Y' AND M_Product.IsStocked='Y'  AND (M_Product.IsBOM = 'N' OR (M_Product.IsBOM = 'Y' AND M_Product.IsVerified='Y')) AND M_Product.ProductType = 'I' AND (M_Product.IsPurchased = 'Y' OR M_Product.IsManufactured = 'Y') AND M_Product.IsPlannedItem = 'Y' ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, OrgID);
      rs = cPreparedStatement.executeQuery();
      int ProductExists = 0;
      while (rs.next()) {
        if (ProductID == rs.getInt(1))
          ProductExists++; 
      } 
      if (ProductExists != 1) {
        this.log.warning("Product - Invalid, Select a Product which is active and planned ");
        return false;
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        cPreparedStatement = null;
      } catch (Exception e) {
        cPreparedStatement = null;
      } 
      try {
        if (rs != null)
          rs.close(); 
        rs = null;
      } catch (Exception e) {
        rs = null;
      } 
    } 
    return true;
  }
}

