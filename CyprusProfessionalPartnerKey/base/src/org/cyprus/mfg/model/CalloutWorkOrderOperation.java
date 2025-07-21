package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

//@Deprecated
public class CalloutWorkOrderOperation extends CalloutEngine {
  private final CLogger log = CLogger.getCLogger(getClass());
  
  public String standardOperation(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    CPreparedStatement cPreparedStatement = null;
    Integer MFG_StandardOperation_ID = (Integer)value;
    if (MFG_StandardOperation_ID == null || MFG_StandardOperation_ID.intValue() == 0)
      return ""; 
    String sql = "SELECT MFG_Operation_ID, MFG_WorkCenter_ID, Description, SetupTime, UnitRunTime, C_UOM_ID, IsPermitRequired, IsHazardous FROM MFG_StandardOperation WHERE MFG_StandardOperation_ID = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, MFG_StandardOperation_ID.intValue());
       rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        mTab.setValue("MFG_Operation_ID", Integer.valueOf(rs.getInt(1)));
        mTab.setValue("MFG_WorkCenter_ID", Integer.valueOf(rs.getInt(2)));
        mTab.setValue("Description", rs.getString(3));
        mTab.setValue("SetupTime", rs.getBigDecimal(4));
        mTab.setValue("UnitRuntime", rs.getBigDecimal(5));
        mTab.setValue("C_UOM_ID", Integer.valueOf(rs.getInt(6)));
        mTab.setValue("IsPermitRequired", rs.getString(7));
        mTab.setValue("IsHazardous", rs.getString(8));
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
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
    return "";
  }
  
  public String setC_UOM_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int C_UOM_ID = ((Integer)value).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal SetupTime = (BigDecimal)mTab.getValue("SetupTime");
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      this.log.fine("Corrected Setup Time Scale UOM=" + C_UOM_ID + "; Setup Time = " + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      mTab.setValue("SetupTime", SetupTime);
    } 
    BigDecimal UnitRuntime = (BigDecimal)mTab.getValue("SetupTime");
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      this.log.fine("Corrected Runtime Per Unit Scale UOM=" + C_UOM_ID + "; Runtime Per Unit = " + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      mTab.setValue("UnitRuntime", UnitRuntime);
    } 
    return "";
  }
  
  public String setSetupTime(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal SetupTime = (BigDecimal)value;
    BigDecimal SetupTime1 = SetupTime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (SetupTime.compareTo(SetupTime1) != 0) {
      this.log.fine("Corrected Setup Time Scale UOM=" + C_UOM_ID + "; Setup Time=" + SetupTime + "->" + SetupTime1);
      SetupTime = SetupTime1;
      mTab.setValue("SetupTime", SetupTime);
    } 
    return "";
  }
  
  public String setUnitRuntime(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value) {
    if (value == null)
      return ""; 
    int C_UOM_ID = ((Integer)mTab.getValue("C_UOM_ID")).intValue();
    if (0 == C_UOM_ID)
      return ""; 
    BigDecimal UnitRuntime = (BigDecimal)value;
    BigDecimal UnitRuntime1 = UnitRuntime.setScale(MUOM.getPrecision(ctx, C_UOM_ID), 4);
    if (UnitRuntime.compareTo(UnitRuntime1) != 0) {
      this.log.fine("Corrected Runtime Per Unit UOM = " + C_UOM_ID + "; Runtime Per Unit = " + UnitRuntime + "->" + UnitRuntime1);
      UnitRuntime = UnitRuntime1;
      mTab.setValue("UnitRuntime", UnitRuntime);
    } 
    return "";
  }
}

