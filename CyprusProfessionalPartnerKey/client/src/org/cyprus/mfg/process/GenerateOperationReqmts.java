package org.cyprus.mfg.process;



//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrderResourceTxnLine;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.model.MMFGWorkOrderTransactionLine;
import org.cyprus.mfg.model.X_MFG_WorkOrderComponent;
import org.cyprus.mfg.model.X_MFG_WorkOrderTransaction;
import org.cyprus.mfg.util.MMFGWorkOrderTxnUtil;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;

public class GenerateOperationReqmts extends SvrProcess {
  private int p_T_WorkOrderOperation_ID = 0;
  
  private String p_txnType = null;
  
  private int m_wooProcessed = 0;
  
  private int m_txnsCreated = 0;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("WorkOrderTxnType")) {
          this.p_txnType = element.getParameter().toString();
        } else if (name.equals("T_WorkOrderOperation_ID")) {
          this.p_T_WorkOrderOperation_ID = element.getParameterAsInt();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
  
  protected String doIt() throws Exception {
//  SysEnv se = SysEnv.get("CMFG");
//	    
//
//	  if (se == null || !se.checkLicense())
////      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
//	      throw new Exception(CLogger.retrieveError().getName()); 

		  if (!this.p_txnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) && !this.p_txnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) && !this.p_txnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage))
      return Msg.getMsg(getCtx(), "Invalid"); 
    Timestamp dateStamp = new Timestamp(System.currentTimeMillis());
    this.log.info("Started at :" + dateStamp);
    this.log.info("T_WorkOrderOperation_ID=" + this.p_T_WorkOrderOperation_ID + "WorkOrderTxnType=" + this.p_txnType);
    StringBuffer retValue = new StringBuffer();
    int noInserted = prepareTable();
    this.log.fine("Prepare table : Inserted # " + noInserted);
    boolean retFlag = generateTxn();
    deleteTable();
    dateStamp = new Timestamp(System.currentTimeMillis());
    this.log.info("Completed at :" + dateStamp);
    retValue.append(Msg.getMsg(getCtx(), "OperationsSelected")).append(" : ").append(noInserted).append(", ").append(Msg.getMsg(getCtx(), "OperationsProcessed")).append(" : ").append(this.m_wooProcessed).append(", ").append(Msg.getMsg(getCtx(), "TxnsCreated")).append(" : ").append(this.m_txnsCreated).append(" ");
    if (!retFlag)
      retValue = retValue.append("\n").append(Msg.getMsg(getCtx(), "ErrorsInProcess")); 
    return retValue.toString();
  }
  
  protected int prepareTable() {
    if (this.p_T_WorkOrderOperation_ID != 0) {
      int noInserted = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM T_WorkOrderOperation WHERE T_WorkOrderOperation_ID=?", new Object[] { Integer.valueOf(this.p_T_WorkOrderOperation_ID) });
      return noInserted;
    } 
    return 0;
  }
  
  private void deleteTable() {
    String deleteSQL = "DELETE FROM T_WorkOrderOperation WHERE T_WorkOrderOperation_ID=?";
    int no = DB.executeUpdateEx( deleteSQL, new Object[] { Integer.valueOf(this.p_T_WorkOrderOperation_ID) },get_TrxName());
    this.log.fine("Deleted #" + no);
  }
  
  protected boolean generateTxn() throws Exception {
    CPreparedStatement cPreparedStatement = null;
    boolean retFlag = true;
    MMFGWorkOrderTxnUtil txnUtil = new MMFGWorkOrderTxnUtil(false);
    Trx newTrx = Trx.get("CMFGOperationalReqmts");
    String selectSQL = "SELECT woo.MFG_WorkOrder_ID, woo.SeqNo FROM T_WorkOrderOperation t  INNER JOIN MFG_WorkOrderOperation woo ON (t.MFG_WorkOrderOperation_ID = woo.M_WorkOrderOperation_ID)WHERE T_WorkOrderOperation_ID=? ORDER BY Line";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement.setInt(1, this.p_T_WorkOrderOperation_ID);
       rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        int workorderID = rs.getInt(1);
        BigDecimal wooSeqNo = rs.getBigDecimal(2);
        this.log.fine("M_WorkOrder_ID = " + workorderID + ", Operation Seq = " + wooSeqNo);
        // have to be check @anshul16122021
        MMFGWorkOrderTransaction woTxn = txnUtil.createWOTxn(getCtx(), workorderID, this.p_txnType, get_TrxName());
        woTxn.save(get_TrxName());
        MMFGWorkOrderTransactionLine[] componentTxnLines = null;
        MMFGWorkOrderResourceTxnLine[] resourceTxnLines = null;
        if (this.p_txnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || this.p_txnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder)) {
          componentTxnLines = txnUtil.generateComponentTxnLine(getCtx(), woTxn.getMFG_WORKORDERTRANSACTION_ID(), null, wooSeqNo, wooSeqNo, X_MFG_WorkOrderComponent.SUPPLYTYPE_Push, get_TrxName(),null);
          this.m_wooProcessed++;
          if (componentTxnLines != null && componentTxnLines.length != 0) {
            for (MMFGWorkOrderTransactionLine line : componentTxnLines)
              line.save(get_TrxName()); 
          } else {
            this.log.fine("Work Order Id = " + workorderID + ", Operation Seq = " + wooSeqNo + ", No component reqmts.");
            newTrx.rollback();
            continue;
          } 
        } else if (this.p_txnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage)) {
          this.m_wooProcessed++;
          resourceTxnLines = txnUtil.generateResourceTxnLine(getCtx(), woTxn.getMFG_WORKORDERTRANSACTION_ID(), null, wooSeqNo, wooSeqNo, get_TrxName(), false);
          if (resourceTxnLines != null && resourceTxnLines.length != 0) {
            for (MMFGWorkOrderResourceTxnLine line : resourceTxnLines)
              line.save(get_TrxName()); 
          } else {
            this.log.fine("Work Order Id = " + workorderID + ", Operation Seq = " + wooSeqNo + ", No resource reqmts.");
            newTrx.rollback();
            continue;
          } 
        } 
      DocumentEngine engine = new DocumentEngine((DocAction)woTxn,"CO");
       if( engine.processIt("CO")) {
       // if (DocumentEngine.processIt((DocAction)woTxn, "CO")) {
          woTxn.save();
          newTrx.commit();
          this.m_txnsCreated++;
          continue;
        } 
        retFlag = false;
        this.log.severe("M_WorkOrder_ID = " + workorderID + ", Operation Seq = " + wooSeqNo + ": Could not Complete transaction.");
        newTrx.rollback();
      } 
      newTrx.close();
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      newTrx.rollback();
      newTrx.close();
      this.log.log(Level.SEVERE, selectSQL + " - Param1=" + this.p_T_WorkOrderOperation_ID + " [" + get_TrxName() + "]", e);
    } finally {
      try {
        if (cPreparedStatement != null)
          cPreparedStatement.close(); 
        if(rs != null)
        	rs.close();
      } catch (Exception e) {}
      cPreparedStatement = null;
      rs = null;
    } 
    return retFlag;
  }
}

