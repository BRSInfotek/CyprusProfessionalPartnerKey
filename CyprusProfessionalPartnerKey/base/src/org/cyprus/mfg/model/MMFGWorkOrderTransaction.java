package org.cyprus.mfg.model;



import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

//import org.cyprusbrs.api.UICallout;
import org.cyprus.mfg.util.MMFGWorkOrderTxnUtil;
import org.cyprusbrs.framework.MAttributeSetInstance;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductCategory;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MTransaction;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.X_AD_Client;
import org.cyprusbrs.framework.X_M_Transaction;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Ini;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;
import org.cyprusbrs.util.Util;
import org.cyprusbrs.util.ValueNamePair;

public class MMFGWorkOrderTransaction extends X_MFG_WorkOrderTransaction implements DocAction {
	  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderTransaction.class);
	  
	  private static final long serialVersionUID = 1L;
	  
	  public static final String REVERSE_INDICATOR = "^";
	  
	  private MMFGWorkOrderTransactionLine[] m_lines = null;
	  
	  private StringBuffer warningLog = new StringBuffer();
	  
	  private StringBuffer infoLog = new StringBuffer();
	  
	  private int m_nextLineNo;
	  
	  private String m_processMsg;
	  
	  private boolean m_reversal;
	  
	  private int m_parentReversal;
	  
	  private boolean m_forceReverse;
	  
	  private boolean m_forceVoid;
	  
	//  public MMFGWorkOrderTransaction(Ctx ctx, int MFG_WorkOrderTransaction_ID, Trx trx) {
//	    super(ctx, MFG_WorkOrderTransaction_ID, trx);
//	    this.m_nextLineNo = 10;
//	    this.m_processMsg = null;
//	    this.m_reversal = false;
//	    this.m_parentReversal = 0;
//	    this.m_forceReverse = false;
//	    this.m_forceVoid = false;
//	    if (MFG_WorkOrderTransaction_ID == 0) {
//	      setDocStatus(DOCSTATUS_Drafted);
//	      setDocAction(DOCACTION_Prepare);
//	      setC_DocType_ID(0);
//	      setDateAcct(new Timestamp(System.currentTimeMillis()));
//	      setIsApproved(false);
//	      super.setProcessed(false);
//	      setProcessing(false);
//	      setPosted(false);
//	    } 
	//  }
	//  
	//  public MMFGWorkOrderTransaction(Ctx ctx, ResultSet rs, Trx trx) {
//	    super(ctx, rs, trx);
//	    this.m_nextLineNo = 10;
//	    this.m_processMsg = null;
//	    this.m_reversal = false;
//	    this.m_parentReversal = 0;
//	    this.m_forceReverse = false;
//	    this.m_forceVoid = false;
	//  }
	  
	  
	  public MMFGWorkOrderTransaction(Properties ctx, int MFG_WorkOrderTransaction_ID, String trxName) {
		    super(ctx, MFG_WorkOrderTransaction_ID, trxName);
		    this.m_nextLineNo = 10;
		    this.m_processMsg = null;
		    this.m_reversal = false;
		    this.m_parentReversal = 0;
		    this.m_forceReverse = false;
		    this.m_forceVoid = false;
		    if (MFG_WorkOrderTransaction_ID == 0) {
		      setDocStatus(DOCSTATUS_Drafted);
		      setDocAction(DOCACTION_Prepare);
		      setC_DocType_ID(0);
		      setDateAcct(new Timestamp(System.currentTimeMillis()));
		      setIsApproved(false);
		      super.setProcessed(false);
		      setProcessing(false);
		    //  setPosted(false);
		    } 
		  }
		  
		  public MMFGWorkOrderTransaction(Properties ctx, ResultSet rs, String trxName) {
		    super(ctx, rs, trxName);
		    this.m_nextLineNo = 10;
		    this.m_processMsg = null;
		    this.m_reversal = false;
		    this.m_parentReversal = 0;
		    this.m_forceReverse = false;
		    this.m_forceVoid = false;
		  }
	  
	  public static MMFGWorkOrderTransaction[] getOfWorkOrder(MMFGWorkOrder workorder, String whereClause, String orderClause) {
	    StringBuffer sqlstmt = new StringBuffer("SELECT * FROM MFG_WorkOrderTransaction WHERE MFG_WorkOrder_ID=? ");
	    if (whereClause != null)
	      sqlstmt.append("AND ").append(whereClause); 
	    if (orderClause != null)
	      sqlstmt.append("ORDER BY ").append(orderClause); 
	    String sql = sqlstmt.toString();
	    ArrayList<MMFGWorkOrderTransaction> list = new ArrayList<MMFGWorkOrderTransaction>();
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql.toString(), workorder.get_TrxName());
	      cPreparedStatement.setInt(1, workorder.getMFG_WorkOrder_ID());
	      rs = cPreparedStatement.executeQuery();
	      while (rs.next())
	        list.add(new MMFGWorkOrderTransaction(workorder.getCtx(), rs, workorder.get_TrxName())); 
	      rs.close();
	      cPreparedStatement.close();
	      cPreparedStatement = null;
	    } catch (SQLException e) {
	      log.log(Level.SEVERE, sql, e);
	      return null;
	    } finally {
	      DB.close(rs);
	      DB.close((Statement)cPreparedStatement);
	      rs = null;
	      cPreparedStatement = null;
	    } 
	    MMFGWorkOrderTransaction[] retValue = new MMFGWorkOrderTransaction[list.size()];
	    list.toArray(retValue);
	    return retValue;
	  }
	  
	//  @UICallout
	  public void setMFG_WorkOrder_ID(String oldMFG_WorkOrder_ID, String newMFG_WorkOrder_ID, int windowNo) throws Exception {
	    if (newMFG_WorkOrder_ID == null || newMFG_WorkOrder_ID.length() == 0)
	      return; 
	    int MFG_WorkOrder_ID = Integer.parseInt(newMFG_WorkOrder_ID);
	    if (MFG_WorkOrder_ID == 0)
	      return; 
	    MMFGWorkOrder workOrder = new MMFGWorkOrder(getCtx(), MFG_WorkOrder_ID, get_TrxName());
	    setM_Product_ID(workOrder.getM_Product_ID());
	    setC_UOM_ID(workOrder.getC_UOM_ID());
	    setM_Locator_ID(workOrder.getM_Locator_ID());
	    setC_BPartner_ID(workOrder.getC_BPartner_ID());
	    setC_BPartner_Location_ID(workOrder.getC_BPartner_Location_ID());
	    setAD_User_ID(workOrder.getAD_User_ID());
	    setC_Project_ID(workOrder.getC_Project_ID());
	    setC_Campaign_ID(workOrder.getC_Campaign_ID());
	    setC_Activity_ID(workOrder.getC_Activity_ID());
	    setUser1_ID(workOrder.getUser1_ID());
	    setUser2_ID(workOrder.getUser2_ID());
	    MMFGWorkOrderClass woc = new MMFGWorkOrderClass(getCtx(), workOrder.getMFG_WorkOrderClass_ID(), get_TrxName());
	    setC_DocType_ID(String.valueOf(workOrder.getC_DocType_ID()), String.valueOf(woc.getWOT_DocType_ID()), windowNo);
	    if (getWorkOrderTxnType() != null && getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	      String sql = "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? ORDER BY SeqNo";
	      int M_WorkOrderOperation_ID = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(MFG_WorkOrder_ID) });
	      setOperationFrom_ID(M_WorkOrderOperation_ID);
	    } 
	  }
	  
	  //@UICallout
	  public void setWorkOrderTxnType(String oldWorkOrderTxnType, String newWorkOrderTxnType, int windowNo) throws Exception {
	    if (newWorkOrderTxnType == null || newWorkOrderTxnType.length() == 0)
	      return; 
	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	      int operationID = DB.getSQLValue(get_TrxName(), "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? ORDER BY SeqNo", new Object[] { Integer.valueOf(getMFG_WorkOrder_ID()) });
	      setOperationFrom_ID(operationID);
	    } 
	  }
	  
	 // @UICallout
	  public void setWOComplete(String oldWOComplete, String newWOComplete, int windowNo) throws Exception {
	    if (newWOComplete.isEmpty() || newWOComplete.equals("N"))
	      return; 
	    MMFGWorkOrderOperation lastMandatoryWOO = null;
	    String sql = "SELECT * FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional <> 'Y' ORDER BY SeqNo DESC";
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	      cPreparedStatement.setInt(1, getMFG_WorkOrder_ID());
	      rs = cPreparedStatement.executeQuery();
	      if (rs.next()) {
	        lastMandatoryWOO = new MMFGWorkOrderOperation(getCtx(), rs, get_TrxName());
	      } else {
	        setIsOptionalTo(false);
	        return;
	      } 
	      cPreparedStatement.close();
	      rs.close();
	      cPreparedStatement = null;
	    } catch (SQLException e) {
	      log.log(Level.SEVERE, sql, e);
	    } finally {
	      DB.close(rs);
	      DB.close((Statement)cPreparedStatement);
	      rs = null;
	      cPreparedStatement = null;
	    } 
	    setOperationTo_ID(lastMandatoryWOO.getMFG_WorkOrderOperation_ID());
	    if (lastMandatoryWOO.isOptional()) {
	      setIsOptionalTo(true);
	    } else {
	      setIsOptionalTo(false);
	    } 
	    setStepTo(STEPTO_ToMove);
	  }
	  
	 // @UICallout
	  public void setStepFrom(String oldStepFrom, String newStepFrom, int windowNo) throws Exception {
	    if (getOperationFrom_ID() <= 0)
	      return; 
	    if (newStepFrom.isEmpty())
	      return; 
	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_WorkOrderMove)) {
	      MMFGWorkOrderOperation opFrom = new MMFGWorkOrderOperation(getCtx(), getOperationFrom_ID(), get_TrxName());
	      if (newStepFrom.equals(STEPFROM_Queue)) {
	        setQtyEntered(opFrom.getQtyQueued());
	        return;
	      } 
	      if (newStepFrom.equals(STEPFROM_Run)) {
	        setQtyEntered(opFrom.getQtyRun());
	        return;
	      } 
	      if (newStepFrom.equals(STEPTO_ToMove)) {
	        setQtyEntered(opFrom.getQtyAssembled());
	        return;
	      } 
	      if (newStepFrom.equals(STEPFROM_Scrap)) {
//	        this.p_changeVO.addError(Msg.getMsg(getCtx(), "Error", "Cannot select scrap intra-operation step as starting operation."));
	        //  this.p_changeVO.addError(Msg.getMsg(getCtx(),  "Cannot select scrap intra-operation step as starting operation."));
	          log.saveError(Msg.getMsg(getCtx(),  "Cannot select scrap intra-operation step as starting operation."), "");
				
	    	  return;
	      } 
	    } 
	  }
	  
	 // @UICallout
	  public void setOperationFrom_ID(String oldOperationFrom_ID, String newOperationFrom_ID, int windowNo) throws Exception {
	    if (newOperationFrom_ID == null || newOperationFrom_ID.trim().length() == 0)
	      return; 
	    int OperationFrom_ID = Integer.parseInt(newOperationFrom_ID);
	    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(getCtx(), OperationFrom_ID, get_TrxName());
	    if (woo.isOptional()) {
	      setIsOptionalFrom(true);
	    } else {
	      setIsOptionalFrom(false);
	    } 
	  }
	  
	 // @UICallout
	  public void setQtyEntered(String oldQtyEntered, String newQtyEntered, int windowNo) throws Exception {
	    if (newQtyEntered == null || newQtyEntered.trim().length() == 0)
	      return; 
	    if (getC_UOM_ID() == 0)
	      return; 
	    BigDecimal QtyEntered = new BigDecimal(newQtyEntered);
	    BigDecimal QtyEntered1 = QtyEntered.setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
	    if (QtyEntered.compareTo(QtyEntered1) != 0) {
	      log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered=" + QtyEntered + "->" + QtyEntered1);
	      QtyEntered = QtyEntered1;
	      setQtyEntered(QtyEntered);
	    } 
	  }
	  
	  //@UICallout
	  public void setOperationTo_ID(String oldOperationTo_ID, String newOperationTo_ID, int windowNo) throws Exception {
	    if (newOperationTo_ID == null || newOperationTo_ID.trim().length() == 0)
	      return; 
	    int OperationTo_ID = Integer.parseInt(newOperationTo_ID);
	    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(getCtx(), OperationTo_ID, get_TrxName());
	    if (woo.isOptional()) {
	      setIsOptionalTo(true);
	    } else {
	      setIsOptionalTo(false);
	    } 
	  }
	  
	  public void setC_DocType_ID(String oldC_DocType_ID, String newC_DocType_ID, int windowNo) throws Exception {
	    if (Util.isEmpty(newC_DocType_ID))
	      return; 
	  //  int C_DocType_ID = convertToInt(newC_DocType_ID);
	    int C_DocType_ID = Integer.parseInt(newC_DocType_ID);

	    if (C_DocType_ID == 0)
	      return; 
	    String oldDocNo = getDocumentNo();
	    boolean newDocNo = (oldDocNo == null);
	    if (!newDocNo && oldDocNo.startsWith("<") && oldDocNo.endsWith(">"))
	      newDocNo = true; 
	    int oldDocType_ID = getC_DocType_ID();
	    if (oldDocType_ID == 0 && !Util.isEmpty(oldC_DocType_ID))
//	      oldDocType_ID = convertToInt(oldC_DocType_ID);
	    	 oldDocType_ID = Integer.parseInt(oldC_DocType_ID); 
	    String sql = "SELECT d.DocBaseType, d.IsDocNoControlled, s.CurrentNext, s.CurrentNextSys, s.AD_Sequence_ID FROM C_DocType d LEFT OUTER JOIN AD_Sequence s ON (d.DocNoSequence_ID=s.AD_Sequence_ID)WHERE C_DocType_ID=?";
	    CPreparedStatement cPreparedStatement1 = null;
	    ResultSet resultSet = null;
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs  = null;
	    try {
	      int AD_Sequence_ID = 0;
	      if (!newDocNo && oldDocType_ID != 0) {
	         cPreparedStatement1 = DB.prepareStatement(sql, null);
	        cPreparedStatement1.setInt(1, oldDocType_ID);
	         resultSet = cPreparedStatement1.executeQuery();
	        if (resultSet.next())
	          AD_Sequence_ID = resultSet.getInt(5); 
	        resultSet.close();
	        cPreparedStatement1.close();
	      } 
	       cPreparedStatement = DB.prepareStatement(sql, null);
	      cPreparedStatement.setInt(1, C_DocType_ID);
	       rs = cPreparedStatement.executeQuery();
	      if (rs.next()) {
	        setC_DocType_ID(C_DocType_ID);
	        //have to be check @anshul16122021
	       // this.p_changeVO.setContext(getCtx(), windowNo, "C_DocTypeTarget_ID", C_DocType_ID);
	        if (rs.getString(2).equals("Y")) {
	          if (!newDocNo && AD_Sequence_ID != rs.getInt(6))
	            newDocNo = true; 
	          if (newDocNo)
	            if (Ini.isPropertyBool("CompiereSys") && Env.getAD_Client_ID(getCtx()) < 1000000) {
	              setDocumentNo("<" + rs.getString(4) + ">");
	            } else {
	              setDocumentNo("<" + rs.getString(3) + ">");
	            }  
	        } 
	      } 
	      rs.close();
	      cPreparedStatement.close();
	    } catch (SQLException e) {
	      log.log(Level.SEVERE, sql, e);
	    } 
	    finally
	    {
	    	cPreparedStatement1.close();
	    	 cPreparedStatement.close();
	    	 resultSet.close();
	    	 rs.close();
	    	 cPreparedStatement1 = null;
	    	 cPreparedStatement = null;
	    	 resultSet = null;
	    	 rs = null;
	    }
	  }
	  
	  public void setMFG_WorkOrder_ID(int workOrderID) {
	    super.setMFG_WorkOrder_ID(workOrderID);
	    MMFGWorkOrder workOrder = new MMFGWorkOrder(getCtx(), workOrderID, get_TrxName());
	    setM_Product_ID(workOrder.getM_Product_ID());
	    setC_UOM_ID(workOrder.getC_UOM_ID());
	  }
	  
	  private void setReversalM_WorkOrder_ID(int workOrderID) {
	    super.setMFG_WorkOrder_ID(workOrderID);
	  }
	  
	  public void setRequiredColumns(int workOrderID, int locatorID, String woTxnSource, String woTxnType) {
	    setMFG_WorkOrder_ID(workOrderID);
	    setM_Locator_ID(locatorID);
	    setWOTxnSource(woTxnSource);
	    setWorkOrderTxnType(woTxnType);
	    setDateTrx(new Timestamp((new Date()).getTime()));
	  }
	  
	  public String getDocumentInfo() {
	    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
	    return dt.getName() + " " + getDocumentNo();
	  }
	  
	  private boolean processInvComponentLine(MMFGWorkOrderTransactionLine woTxnLine, String txnType) {
	    BigDecimal newQty = BigDecimal.ZERO;
	    if (txnType.equals(WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || txnType.equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	      newQty = woTxnLine.getQtyEntered();
	    } else if (txnType.equals(WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) || txnType.equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory)) {
	      newQty = woTxnLine.getQtyEntered().negate();
	    } else {
	      this.m_processMsg = getMFG_WORKORDERTRANSACTION_ID() + " - Invalid transaction type " + txnType + " for inventory component processing";
	      return false;
	    } 
	    MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	    MMFGWorkOrderOperation woo = null;
	    if (txnType.equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory) || txnType.equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	      woo = new MMFGWorkOrderOperation(getCtx(), getOperationFrom_ID(), get_TrxName());
	      if (txnType.equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory)) {
	        BigDecimal woAssyQty = wo.getQtyAvailable();
	        if (woAssyQty.compareTo(woTxnLine.getQtyEntered()) < 0) {
	          this.m_processMsg = "No of assemblies - " + woAssyQty + ", not enough to issue from the work order - " + wo.getMFG_WorkOrder_ID();
	          return false;
	        } 
	        woo.setQtyAssembled(newQty.add(woo.getQtyAssembled()).setScale(MUOM.getPrecision(getCtx(), wo.getC_UOM_ID()), 4));
	      } else {
	        woo.setQtyQueued(newQty.add(woo.getQtyQueued()).setScale(MUOM.getPrecision(getCtx(), wo.getC_UOM_ID()), 4));
	      } 
	      if (!woo.save(get_TrxName()))
	        return false; 
	      updateAssemblyQty(newQty, true, false);
	    } else if (txnType.equals(WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || txnType.equals(WORKORDERTXNTYPE_ComponentReturnFromWorkOrder)) {
	      String sql = "SELECT MFG_WorkOrderComponent_ID FROM MFG_WorkOrderComponent WHERE MFG_WorkOrderOperation_ID = ? and M_Product_ID = ?";
	      MRole role = MRole.getDefault(getCtx(), false);
	      sql = role.addAccessSQL(sql, "MFG_WorkOrderComponent", false, true);
	      int woCompID = 0;
	      int componentsUpdated = 0;
	      CPreparedStatement cPreparedStatement = null;
	      ResultSet rs = null;
	      boolean success = true;
	      try {
	    	  cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	        cPreparedStatement.setInt(1, woTxnLine.getMFG_WorkOrderOperation_ID());
	        cPreparedStatement.setInt(2, woTxnLine.getM_Product_ID());
	        rs = cPreparedStatement.executeQuery();
	        while (rs.next()) {
	          woCompID = rs.getInt(1);
	          MMFGWorkOrderComponent woComp = new MMFGWorkOrderComponent(getCtx(), woCompID, get_TrxName());
	          BigDecimal woCompQty = woComp.getQtyAvailable().subtract(woComp.getQtySpent());
	          if (txnType.equals(WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) && woCompQty.compareTo(woTxnLine.getQtyEntered()) < 0) {
	            this.m_processMsg = "No of components - " + woCompQty + ", in work order component line - " + woComp.getMFG_WorkOrderComponent_ID() + ", not enough to allow component return from the work order - " + wo.getMFG_WorkOrder_ID();
	            success = false;
	            break;
	          } 
	          woComp.setQtyAvailable(woComp.getQtyAvailable().add(newQty).setScale(MUOM.getPrecision(getCtx(), woComp.getC_UOM_ID()), 4));
	          if (!woComp.save(get_TrxName())) {
	            success = false;
	            break;
	          } 
	          BigDecimal overissue = woComp.getQtyAvailable().subtract(wo.getQtyEntered().multiply(woComp.getQtyRequired()));
	          if (overissue.compareTo(BigDecimal.ZERO) > 0)
	            this.warningLog.append("\nOverissue of " + overissue + " for component " + MProduct.get(getCtx(), woComp.getM_Product_ID()) + ", M_WorkOrderComponent_ID: " + woComp.getMFG_WorkOrderComponent_ID()); 
	          componentsUpdated++;
	          woo = new MMFGWorkOrderOperation(getCtx(), woComp.getMFG_WorkOrderOperation_ID(), get_TrxName());
	        } 
	      } catch (SQLException e) {
	        log.log(Level.SEVERE, sql, e);
	        return false;
	      } finally {
	        DB.close(rs);
	        DB.close((Statement)cPreparedStatement);
	        rs = null;
	        cPreparedStatement = null;		
	      } 
	      if (!success)
	        return false; 
	      if (componentsUpdated == 0)
	        if (txnType.equals(WORKORDERTXNTYPE_ComponentIssueToWorkOrder)) {
	          woo = new MMFGWorkOrderOperation(getCtx(), woTxnLine.getMFG_WorkOrderOperation_ID(), get_TrxName());
	          MProduct prod = new MProduct(getCtx(), woTxnLine.getM_Product_ID(), get_TrxName());
	          MLocator loc = new MLocator(getCtx(), getM_Locator_ID(), get_TrxName());
	          MMFGWorkOrderComponent woComp = new MMFGWorkOrderComponent(wo, woo, prod, BigDecimal.ZERO, X_MFG_WorkOrderComponent.SUPPLYTYPE_Push, loc);
	          woComp.setQtyAvailable(newQty.setScale(MUOM.getPrecision(getCtx(), woComp.getC_UOM_ID()), 4));
	          woComp.setProcessed(true);
	          if (!woComp.save(get_TrxName()))
	            return false; 
	        } else {
	          this.m_processMsg = "Not enough quantity to return from the work order.";
	          return false;
	        }  
	    } 
	    if (!processInventory(woTxnLine, txnType))
	      return false; 
	    if (woo != null) {
	      Timestamp trxDate = getDateTrx();
	      if (trxDate == null)
	        trxDate = new Timestamp(System.currentTimeMillis()); 
	      if (woo.getDateActualFrom() == null || woo.getDateActualFrom().after(trxDate))
	        woo.setDateActualFrom(trxDate); 
	      if (woo.getDateActualTo() == null || woo.getDateActualTo().before(trxDate))
	        woo.setDateActualTo(trxDate); 
	      woo.save(get_TrxName());
	    } 
	    return true;
	  }
	  
	  private boolean processInventory(MMFGWorkOrderTransactionLine woTxnLine, String txnType) {
	    checkMaterialPolicy(woTxnLine);
	    MProduct product = MProduct.get(getCtx(), getM_Product_ID());
	    if (!product.isStocked()) {
	      log.info("Product " + product + " is not stocked. Not creating MTransaction");
	      setProcessed(true);
	      return save(get_TrxName());
	    } 
	    boolean workOrderIn = false;
	    if (txnType.equals(WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || txnType.equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory))
	      workOrderIn = true;
	    /// Commented by Mukesh as 
//	    if (woTxnLine.getM_AttributeSetInstance_ID() == 0) {
//	      MMFGWorkOrderTransactionLineMA[] mas = MMFGWorkOrderTransactionLineMA.get(getCtx(), woTxnLine.getMFG_WORKORDERTRANSACTION_ID(), get_TrxName());
//	      for (MMFGWorkOrderTransactionLineMA ma : mas) {
////	        BigDecimal qtyMA = workOrderIn ? ma.getMovementQty().negate() : ma.getMovementQty();  // Old pattern by Compiere updated by Mukesh @20220901
//	          BigDecimal qtyMA = workOrderIn ? woTxnLine.getQtyEntered().negate() : woTxnLine.getQtyEntered();
//	        if (!processMTrxAndStorage(woTxnLine, workOrderIn, ma.getM_AttributeSetInstance_ID(), qtyMA)) {
//	          log.severe("Error processing MA");
//	          return false;
//	        } 
//	      } 
//	      return true;
//	    }
    	BigDecimal movementQty = workOrderIn ? woTxnLine.getQtyEntered().negate() : woTxnLine.getQtyEntered();
    	return processMTrxAndStorage(woTxnLine, workOrderIn, woTxnLine.getM_AttributeSetInstance_ID(), movementQty);
	    
	  }
	  
	  private void checkMaterialPolicy(MMFGWorkOrderTransactionLine line) {
	    int no = MMFGWorkOrderTransactionLineMA.deleteWorkOrderTransactionLineMA(line.getMFG_WorkOrderTrxLine_ID(), get_TrxName());
	    if (no > 0)
	      log.config("Delete old #" + no); 
	    String txnType = getWorkOrderTxnType();
	    boolean toInvTrx = (txnType.equals(WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) || txnType.equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory));
	    MClient client = MClient.get(getCtx());
	    boolean needSave = false;
	    MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), get_TrxName());
	    if (product != null && line.getM_Locator_ID() == 0) {
	      line.setM_Locator_ID(toInvTrx ? Env.ZERO : line.getQtyEntered());
	      needSave = true;
	    } 
	    if (product != null && line.getM_AttributeSetInstance_ID() == 0)
	      if (toInvTrx) {
	        MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), 0, get_TrxName());
	        asi.setClientOrg(getAD_Client_ID(), 0);
	        asi.setM_AttributeSet_ID(product.getM_AttributeSet_ID());
	        if (asi.save(get_TrxName())) {
	          line.setM_AttributeSetInstance_ID(asi.getM_AttributeSetInstance_ID());
	          log.config("New ASI=" + line);
	          needSave = true;
	        } 
	      } else {
	        MProductCategory pc = MProductCategory.get(getCtx(), product.getM_Product_Category_ID());
	        String MMPolicy = pc.getMMPolicy();
	        if (MMPolicy == null || MMPolicy.length() == 0)
	          MMPolicy = client.getMMPolicy(); 
	        BigDecimal qtyToDeliver = line.getQtyEntered();
//	        List<Storage.Record> storages = Storage.getAllWithASI(getCtx(), line.getM_Product_ID(), line.getM_Locator_ID(), X_AD_Client.MMPOLICY_FiFo.equals(MMPolicy), qtyToDeliver, get_TrxName());
	        MStorage[] storages = MStorage.getAllWithASI(getCtx(), line.getM_Product_ID(), line.getM_Locator_ID(), X_AD_Client.MMPOLICY_FiFo.equals(MMPolicy), get_TrxName());

//	        for (int ii = 0; ii < storages.size(); ii++) {
	        for (MStorage num : storages) {
	    //   for (int ii = 0; ii < storages.length; ii++) {

//	        Storage.Record storage = storages.get(ii);
	           // MStorage storage = storages;

	        	BigDecimal qtyAvailable = num.getQtyOnHand().subtract(num.getQtyDedicated()).subtract(num.getQtyAllocated());
	          if (qtyAvailable.compareTo(Env.ZERO) > 0) {
	            MMFGWorkOrderTransactionLineMA ma = new MMFGWorkOrderTransactionLineMA(line, num.getM_AttributeSetInstance_ID(), qtyToDeliver);
	            if (qtyAvailable.compareTo(qtyToDeliver) >= 0) {
	              qtyToDeliver = Env.ZERO;
	            } else {
	              ma.setMovementQty(qtyAvailable);
	              qtyToDeliver = qtyToDeliver.subtract(qtyAvailable);
	            } 
	            if (!ma.save(get_TrxName()))
	              log.fine("failed to save"); 
	          //  log.fine("#" + ii + ": " + ma + ", QtyToDeliver=" + qtyToDeliver);
	            if (qtyToDeliver.signum() == 0)
	              break; 
	          } 
	        } 
	        if (qtyToDeliver.signum() != 0) {
	          MMFGWorkOrderTransactionLineMA ma = new MMFGWorkOrderTransactionLineMA(line, 0, qtyToDeliver);
	          if (!ma.save(get_TrxName()))
	            log.fine("failed to save"); 
	          log.fine("##: " + ma);
	        } 
	      }  
	    if (needSave && !line.save(get_TrxName()))
	      log.severe("NOT saved " + line); 
	  }
	  
	  private boolean processMTrxAndStorage(MMFGWorkOrderTransactionLine woTxnLine, boolean workOrderIn, int ASI_ID, BigDecimal movementQty) {
	    MTransaction mTrx = new MTransaction(getCtx(), getAD_Org_ID(), workOrderIn ? X_M_Transaction.MOVEMENTTYPE_WorkOrderPlus : X_M_Transaction.MOVEMENTTYPE_WorkOrder_, woTxnLine.getM_Locator_ID(), woTxnLine.getM_Product_ID(), ASI_ID, movementQty, getDateTrx(), get_TrxName());
	    mTrx.setMFG_WorkOrderTrxLine_ID(woTxnLine.getMFG_WorkOrderTrxLine_ID());
	    MLocator loc = MLocator.get(getCtx(), woTxnLine.getM_Locator_ID());
//	    if (Storage.addQtys(getCtx(), loc.getM_Warehouse_ID(), woTxnLine.getM_Locator_ID(), woTxnLine.getM_Product_ID(), ASI_ID, 0, movementQty, null, null, get_TrxName())) {
	    if (MStorage.add(getCtx(), loc.getM_Warehouse_ID(), woTxnLine.getM_Locator_ID(), woTxnLine.getM_Product_ID(), ASI_ID, 0, movementQty, null, null, get_TrxName())) {

	    if (mTrx.save(get_TrxName())) {
	        setProcessed(true);
	        if (save(get_TrxName()))
	          return true; 
	        log.log(Level.SEVERE, "MFG_WorkOrderTrxLine " + woTxnLine.getMFG_WorkOrderTrxLine_ID() + ": processInventory: Work Order Transaction not saved");
	      } else {
	        log.log(Level.SEVERE, "MFG_WorkOrderTrxLine " + woTxnLine.getMFG_WorkOrderTrxLine_ID() + "processInventory: MTransaction not saved");
	      } 
	    } else {
	      ValueNamePair pp = CLogger.retrieveError();
	      if (pp != null) {
	        this.m_processMsg = pp.getName();
	        log.log(Level.SEVERE, "MFG_WorkOrderTrxLine " + woTxnLine.getMFG_WorkOrderTrxLine_ID() + "processInventory: MStorage not updated" + pp.getName());
	      } else {
	        log.log(Level.SEVERE, "MFG_WorkOrderTrxLine " + woTxnLine.getMFG_WorkOrderTrxLine_ID() + "processInventory: MStorage not updated");
	      } 
	    } 
	    return false;
	  }
	  
	  private boolean generateMoveComponentIssueTxn(int opFrom, int opTo) {
	    int count = 0;
	    MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	    String whereClause = " SeqNo BETWEEN " + opFrom + " AND " + opTo;
	    MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(wo, whereClause, "SeqNo");
	    ArrayList<MMFGWorkOrderComponent> wocs = new ArrayList<MMFGWorkOrderComponent>();
	    for (MMFGWorkOrderOperation woo : woos) {
	      if (!woo.isOptional())
	        for (MMFGWorkOrderComponent woc : MMFGWorkOrderComponent.getOfWorkOrderOperation(woo, (String)null, (String)null)) {
	          wocs.add(woc);
	          if (woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_AssemblyPull) || woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_OperationPull))
	            count++; 
	        }  
	    } 
	    if (woos[woos.length - 1].isOptional())
	      for (MMFGWorkOrderComponent woc : MMFGWorkOrderComponent.getOfWorkOrderOperation(woos[woos.length - 1], " QtyRequired != 0 ", (String)null)) {
	        wocs.add(woc);
	        if (woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_AssemblyPull) || woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_OperationPull))
	          count++; 
	      }  
	    if (woos[0].isOptional())
	      for (MMFGWorkOrderComponent woc : MMFGWorkOrderComponent.getOfWorkOrderOperation(woos[0], " QtyRequired != 0 ", (String)null)) {
	        wocs.add(woc);
	        if (woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_AssemblyPull) || woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_OperationPull))
	          count++; 
	      }  
	    int depLineNo = 10;
	    if (count > 0) {
	      MMFGWorkOrderTxnUtil txnUtil = new MMFGWorkOrderTxnUtil(true);
	      MMFGWorkOrderTransaction woComponentIssue = txnUtil.createWOTxn(getCtx(), getMFG_WorkOrder_ID(), WORKORDERTXNTYPE_ComponentIssueToWorkOrder, getMFG_WORKORDERTRANSACTION_ID(), getM_Locator_ID(), getQtyEntered(), get_TrxName());
	      if (woComponentIssue == null) {
	        ValueNamePair pp = CLogger.retrieveError();
	        this.m_processMsg = pp.getName();
	        log.severe("Error " + this.m_processMsg + ", Could not create component issue transaction for pull components of Work Order - " + getMFG_WorkOrder_ID());
	        return false;
	      } 
	      woComponentIssue.setDateTrx(getDateTrx());
	      woComponentIssue.setDateAcct(getDateAcct());
	      txnUtil.generateComponentTxnLine(getCtx(), woComponentIssue.getMFG_WORKORDERTRANSACTION_ID(), woComponentIssue.getQtyEntered(), new BigDecimal(opFrom), new BigDecimal(opTo), X_MFG_WorkOrderComponent.SUPPLYTYPE_OperationPull, get_TrxName(),null);
	      DocumentEngine engine = new DocumentEngine (woComponentIssue,  "CO");
			if( !engine.processIt ( getDocAction())) {

				 this.m_processMsg = woComponentIssue.getProcessMsg();
			        log.severe("Could not complete component issue : " + this.m_processMsg + ", for Work Order - " + getMFG_WorkOrder_ID());
			        return false;
			}
//	      if (!DocumentEngine.processIt(woComponentIssue, "CO")) {
//	        this.m_processMsg = woComponentIssue.getProcessMsg();
//	        log.severe("Could not complete component issue : " + this.m_processMsg + ", for Work Order - " + getMFG_WorkOrder_ID());
//	        return false;
//	      } 
	      if (!woComponentIssue.save(get_TrxName())) {
	        this.m_processMsg = "Could not save component issue transaction lines for Work Order - " + getMFG_WorkOrder_ID();
	        return false;
	      } 
	    } 
	    for (MMFGWorkOrderComponent woc : wocs) {
	      MMFGWorkOrderTransactionLine compDepleteLine = new MMFGWorkOrderTransactionLine(getCtx(), 0, get_TrxName());
	      BigDecimal reqQty = woc.getQtyRequired().multiply(getQtyEntered());
	      compDepleteLine.setClientOrg((PO)this);
	      if (reqQty.setScale(MUOM.getPrecision(getCtx(), woc.getC_UOM_ID()), 4).compareTo(BigDecimal.ZERO) <= 0)
	        continue; 
	      compDepleteLine.setRequiredColumns(getMFG_WORKORDERTRANSACTION_ID(), woc.getM_Product_ID(), woc.getC_UOM_ID(), reqQty.negate(), woc.getMFG_WorkOrderOperation_ID(), X_MFG_WorkOrderComponent.BASISTYPE_PerItem);
	      if (woc.getM_AttributeSetInstance_ID() > 0)
	        compDepleteLine.setM_AttributeSetInstance_ID(woc.getM_AttributeSetInstance_ID()); 
	      compDepleteLine.setLine(depLineNo);
	      if (X_MFG_WorkOrderComponent.SUPPLYTYPE_Push.equals(woc.getSupplyType()) && woc.getQtyAvailable().compareTo(reqQty) < 0)
	        this.infoLog.append("\nEnough " + MProduct.get(getCtx(), woc.getM_Product_ID()) + " have not been issued to the work order - " + wo.getMFG_WorkOrder_ID() + ". Issued: " + woc.getQtyAvailable() + " Required: " + reqQty); 
	      depLineNo += 10;
	      if (!compDepleteLine.save(get_TrxName())) {
	        this.m_processMsg = "Could not save deplete lines for Work Order Move";
	        return false;
	      } 
	      BigDecimal additionalQty = woc.getQtyRequired().multiply(getQtyEntered());
	      if (additionalQty.compareTo(BigDecimal.ZERO) == 0 && woc.getM_Product_ID() == wo.getM_Product_ID())
	        additionalQty = getQtyEntered(); 
	      woc.setQtySpent(woc.getQtySpent().add(additionalQty).setScale(MUOM.getPrecision(getCtx(), woc.getC_UOM_ID()), 4));
	      if (!woc.save(get_TrxName())) {
	        this.m_processMsg = "Could not update Component Usage";
	        return false;
	      } 
	      MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(getCtx(), woc.getMFG_WorkOrderOperation_ID(), get_TrxName());
	      Timestamp trxDate = getDateTrx();
	      if (trxDate == null)
	        trxDate = new Timestamp(System.currentTimeMillis()); 
	      if (woo.getDateActualFrom() == null || woo.getDateActualFrom().after(trxDate))
	        woo.setDateActualFrom(trxDate); 
	      if (woo.getDateActualTo() == null || woo.getDateActualTo().before(trxDate))
	        woo.setDateActualTo(trxDate); 
	      if (!woo.save(get_TrxName())) {
	        this.m_processMsg = "Could not update dates.";
	        return false;
	      } 
	    } 
	    this.m_nextLineNo = depLineNo;
	    return true;
	  }
	  
	  private boolean updateAssemblyQty(BigDecimal qty, boolean updateAvailable, boolean updateAssembled) {
	    MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	    if (updateAvailable) {
	      wo.setQtyAvailable(wo.getQtyAvailable().add(qty).setScale(MUOM.getPrecision(getCtx(), wo.getC_UOM_ID()), 4));
	      if (wo.getQtyAvailable().compareTo(BigDecimal.ZERO) < 0) {
	        log.saveError("Error", "Could not update Quantity Available on Work Order - " + getMFG_WorkOrder_ID() + "; Quantity Available must be greater than zero.");
	        return false;
	      } 
	    } 
	    if (updateAssembled)
	      wo.setQtyAssembled(wo.getQtyAssembled().add(qty).setScale(MUOM.getPrecision(getCtx(), wo.getC_UOM_ID()), 4)); 
	    if (!wo.save(get_TrxName())) {
	      log.saveError("NotSaved", "Could not save Work Order");
	      return false;
	    } 
	    return true;
	  }
	  
	  private boolean makeAssemblyCreationLine(int assemblyID, int uomID, int operationID) {
	    MMFGWorkOrderTransactionLine mAssyCreationLine = new MMFGWorkOrderTransactionLine(getCtx(), 0, get_TrxName());
	    mAssyCreationLine.setClientOrg((PO)this);
	    mAssyCreationLine.setRequiredColumns(getMFG_WORKORDERTRANSACTION_ID(), assemblyID, uomID, getQtyEntered(), operationID, X_MFG_WorkOrderComponent.BASISTYPE_PerItem);
	    mAssyCreationLine.setLine(this.m_nextLineNo);
	    this.m_nextLineNo += 10;
	    mAssyCreationLine.save(get_TrxName());
	    if (!updateAssemblyQty(getQtyEntered(), true, true)) {
	      log.saveError("NotUpdated", "Could not update assembly quantity");
	      return false;
	    } 
	    return true;
	  }
	  
	  private boolean generateAssemblyIssueTxn(int assemblyID, int uomID, int operationID) {
	    MMFGWorkOrderTransaction woAssyIssue = new MMFGWorkOrderTransaction(getCtx(), 0, get_TrxName());
	    woAssyIssue.setClientOrg((PO)this);
	    woAssyIssue.setC_DocType_ID(getC_DocType_ID());
	    woAssyIssue.setRequiredColumns(getMFG_WorkOrder_ID(), getM_Locator_ID(), WOTXNSOURCE_Generated, WORKORDERTXNTYPE_AssemblyCompletionToInventory);
	    woAssyIssue.setParentWorkOrderTxn_ID(getMFG_WORKORDERTRANSACTION_ID());
	    woAssyIssue.setQtyEntered(getQtyEntered().setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4));
	    woAssyIssue.setDateTrx(getDateTrx());
	    woAssyIssue.setDateAcct(getDateAcct());
	    woAssyIssue.save(get_TrxName());
	    DocumentEngine docengine = new DocumentEngine (woAssyIssue,  "CO");
		docengine.processIt ( getDocAction());
	   // DocumentEngine.processIt(woAssyIssue, "CO");
	    String errMsg = woAssyIssue.getDocStatus();
	    if (errMsg.equals("IN"))
	      return false; 
	    return woAssyIssue.save(get_TrxName());
	  }
	  
	  private void generateAssemblyTxnLine(int woTxnID, int assemblyID, int uomID, BigDecimal qty, int locator) {
	    MMFGWorkOrderTransaction woAssyIssue = new MMFGWorkOrderTransaction(getCtx(), woTxnID, get_TrxName());
	    MMFGWorkOrderTransactionLine assyDeliveryLine = new MMFGWorkOrderTransactionLine(getCtx(), 0, get_TrxName());
	    assyDeliveryLine.setClientOrg((PO)woAssyIssue);
	    assyDeliveryLine.setRequiredColumns(woTxnID, assemblyID, uomID, qty, woAssyIssue.getOperationFrom_ID(), X_MFG_WorkOrderComponent.BASISTYPE_PerItem);
	    assyDeliveryLine.setM_Locator_ID(locator);
	    assyDeliveryLine.setLine(10);
	    assyDeliveryLine.save(get_TrxName());
	  }
	  
	  public MMFGWorkOrderTransactionLine[] getLines(String whereClause, String orderClause) {
	    ArrayList<MMFGWorkOrderTransactionLine> list = new ArrayList<MMFGWorkOrderTransactionLine>();
	    StringBuffer sql = new StringBuffer("SELECT * FROM MFG_WorkOrderTrxLine WHERE MFG_WorkOrderTransaction_ID=? ");
	    if (whereClause != null)
	      sql.append(whereClause); 
	    if (orderClause != null)
	      sql.append(" ").append(orderClause); 
	    CPreparedStatement cPreparedStatement =null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
	      cPreparedStatement.setInt(1, getMFG_WORKORDERTRANSACTION_ID());
	      rs = cPreparedStatement.executeQuery();
	      while (rs.next()) {
	        MMFGWorkOrderTransactionLine ol = new MMFGWorkOrderTransactionLine(getCtx(), rs, get_TrxName());
	        list.add(ol);
	      } 
	      rs.close();
	      cPreparedStatement.close();
	      cPreparedStatement = null;
	    } catch (Exception e) {
	      log.log(Level.SEVERE, sql.toString(), e);
	    } finally {
	      DB.close(rs);
	      DB.close((Statement)cPreparedStatement);
	      rs = null;
	      cPreparedStatement = null;
	    } 
	    MMFGWorkOrderTransactionLine[] lines = new MMFGWorkOrderTransactionLine[list.size()];
	    list.toArray(lines);
	    return lines;
	  }
	  
	  public String completeIt() {
	    save(get_TrxName());
	    String woTxnType = getWorkOrderTxnType();
	    if (this.m_processMsg == null)
	      this.m_processMsg = ""; 
	    MMFGWorkOrder workOrder = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	    int assemblyID = workOrder.getM_Product_ID();
	    int uomID = workOrder.getC_UOM_ID();
	    MRole role = MRole.getDefault(getCtx(), false);
	    if (woTxnType.equals(WORKORDERTXNTYPE_WorkOrderMove)) {
	      if (getQtyEntered().compareTo(BigDecimal.ZERO) == 0) {
	        this.m_processMsg = "you must enter a nonzero quantity for a move transaction.";
	      //  get_TrxName().rollback();
	        Trx trx = Trx.get(get_TrxName(), true);
	        trx.rollback();
	        return "IN";
	      } 
	      ArrayList<Integer> operations = new ArrayList<Integer>();
	      StringBuffer sqlBuf = new StringBuffer("SELECT woo.MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation woo WHERE woo.MFG_WorkOrder_ID = ?");
	      String stepFrom = getStepFrom();
	      if (stepFrom.equals(STEPFROM_Scrap)) {
	        this.m_processMsg = "cannot initiate move transaction from scrap step";
	       // get_TrxName().rollback();
	        Trx trx = Trx.get(get_TrxName(), true);
	        trx.rollback();
	        return "IN";
	      } 
	      String stepTo = getStepTo();
	      MMFGWorkOrderOperation fromOp = new MMFGWorkOrderOperation(getCtx(), getOperationFrom_ID(), get_TrxName());
	      MMFGWorkOrderOperation toOp = new MMFGWorkOrderOperation(getCtx(), getOperationTo_ID(), get_TrxName());
	      if (stepFrom.equals(STEPFROM_ToMove)) {
	        sqlBuf.append(" AND SeqNo > ? ");
	      } else {
	        sqlBuf.append(" AND SeqNo >= ? ");
	      } 
	      if (fromOp.getSeqNo() < toOp.getSeqNo()) {
	        if (stepTo.equals(STEPTO_ToMove)) {
	          sqlBuf.append(" AND SeqNo <= ? ");
	        } else {
	          sqlBuf.append(" AND SeqNo < ? ");
	        } 
	      } else if (fromOp.getSeqNo() == toOp.getSeqNo()) {
	        if ((stepTo.equals(STEPTO_Scrap) && (stepFrom.equals(STEPFROM_Queue) || stepFrom.equals(STEPFROM_ToMove))) || stepTo.equals(STEPTO_Run)) {
	          sqlBuf.append(" AND SeqNo < ? ");
	        } else {
	          sqlBuf.append(" AND SeqNo <= ? ");
	        } 
	      } 
	      sqlBuf.append(" AND (IsOptional <> 'Y' OR SeqNo = ? OR SeqNo = ?) ORDER BY SeqNo ");
	      String sql = sqlBuf.toString();
	      sql = role.addAccessSQL(sql, "MFG_WorkOrderOperation", false, false);
	      CPreparedStatement cPreparedStatement = null;
	      ResultSet rs = null;
	      try {
	    	  cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	        cPreparedStatement.setInt(1, getMFG_WorkOrder_ID());
	        cPreparedStatement.setInt(2, fromOp.getSeqNo());
	        cPreparedStatement.setInt(3, toOp.getSeqNo());
	        cPreparedStatement.setInt(4, fromOp.getSeqNo());
	        cPreparedStatement.setInt(5, toOp.getSeqNo());
	        rs = cPreparedStatement.executeQuery();
	        while (rs.next())
	          operations.add(Integer.valueOf(rs.getInt(1))); 
	        rs.close();
	        cPreparedStatement.close();
	      } catch (SQLException e) {
	        log.log(Level.SEVERE, sql, e);
	      } finally {
	        DB.close(rs);
	        DB.close((Statement)cPreparedStatement);
	        rs = null;
	        cPreparedStatement = null;
	      } 
	      MMFGWorkOrderOperation firstOp = null, lastOp = null;
	      if (operations.size() > 0) {
	        firstOp = new MMFGWorkOrderOperation(getCtx(), ((Integer)operations.get(0)).intValue(), get_TrxName());
	        lastOp = new MMFGWorkOrderOperation(getCtx(), ((Integer)operations.get(operations.size() - 1)).intValue(), get_TrxName());
	      } 
	      int lastMandatoryOp = DB.getSQLValue(get_TrxName(), "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND IsOptional <> 'Y' ORDER BY SeqNo DESC", new Object[] { Integer.valueOf(getMFG_WorkOrder_ID()) });
	      if (stepFrom.equals(STEPFROM_Queue)) {
	        fromOp.setQtyQueued(fromOp.getQtyQueued().subtract(getQtyEntered()));
	        if (fromOp.getQtyQueued().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	         // get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          
	          return "IN";
	        } 
	      } else if (stepFrom.equals(STEPFROM_Run)) {
	        fromOp.setQtyRun(fromOp.getQtyRun().subtract(getQtyEntered()));
	        if (fromOp.getQtyRun().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	          //get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } else if (stepFrom.equals(STEPFROM_ToMove)) {
	        fromOp.setQtyAssembled(fromOp.getQtyAssembled().subtract(getQtyEntered()));
	        if (fromOp.getQtyAssembled().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	         // get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } 
	      fromOp.save(get_TrxName());
	      if (stepTo.equals(STEPTO_Queue)) {
	        toOp.setQtyQueued(toOp.getQtyQueued().add(getQtyEntered()));
	        if (toOp.getQtyQueued().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	         // get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } else if (stepTo.equals(STEPTO_Run)) {
	        toOp.setQtyRun(toOp.getQtyRun().add(getQtyEntered()));
	        if (toOp.getQtyRun().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	         // get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } else if (stepTo.equals(STEPTO_ToMove)) {
	        toOp.setQtyAssembled(toOp.getQtyAssembled().add(getQtyEntered()));
	        if (toOp.getQtyAssembled().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	          //get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } else if (stepTo.equals(STEPTO_Scrap)) {
	        toOp.setQtyScrapped(toOp.getQtyScrapped().add(getQtyEntered()));
	        if (toOp.getQtyScrapped().compareTo(BigDecimal.ZERO) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	          //get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	        workOrder.setQtyScrapped(workOrder.getQtyScrapped().add(getQtyEntered()));
	        if (stepFrom.equals(STEPFROM_ToMove) && toOp.getMFG_WorkOrderOperation_ID() == lastMandatoryOp)
	          updateAssemblyQty(getQtyEntered().negate(), true, true); 
	        workOrder.save(get_TrxName());
	      } 
	      toOp.save(get_TrxName());
	      if (!isReversal()) {
	        if (operations.size() > 0) {
	          if (generateMoveComponentIssueTxn(firstOp.getSeqNo(), lastOp.getSeqNo())) {
	            if (lastMandatoryOp <= getOperationTo_ID() && stepTo.equals(STEPTO_ToMove)) {
	              if (!makeAssemblyCreationLine(assemblyID, uomID, getOperationTo_ID())) {
	                log.severe("Could not generate assembly line for creating final assembly of Work Order - " + getMFG_WorkOrder_ID());
	              //  get_TrxName().rollback();
	                Trx trx = Trx.get(get_TrxName(), true);
	                trx.rollback();
	                return "IN";
	              } 
	              if (isWOComplete() && !generateAssemblyIssueTxn(assemblyID, uomID, getOperationTo_ID())) {
	                this.m_processMsg = "Could not complete Assembly Completion to Inventory Transaction for Work Order - " + getMFG_WorkOrder_ID();
	                log.severe(this.m_processMsg);
	                //get_TrxName().rollback();
	                Trx trx = Trx.get(get_TrxName(), true);
	                trx.rollback();
	                return "IN";
	              } 
	            } 
	          } else {
	           // get_TrxName().rollback();
	        	  Trx trx = Trx.get(get_TrxName(), true);
	              trx.rollback();
	        	  return "IN";
	          } 
	          if (!generateMoveResourceUsageTxn(firstOp.getSeqNo(), lastOp.getSeqNo())) {
	            //get_TrxName().rollback();
	        	  Trx trx = Trx.get(get_TrxName(), true);
	              trx.rollback();
	        	  return "IN";
	          } 
	        } 
	      } else {
	        if (lastMandatoryOp == getOperationTo_ID() && STEPTO_ToMove.equals(stepTo) && workOrder.getQtyAvailable().compareTo(getQtyEntered().abs()) < 0) {
	          this.m_processMsg = "Not enough assembly quantity available in the work order to reverse.";
	          return "IN";
	        } 
	        MMFGWorkOrderTransactionLine[] wotLines = getLines((String)null, "ORDER BY M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID ");
	        for (MMFGWorkOrderTransactionLine wotl : wotLines) {
	          MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(getCtx(), wotl.getMFG_WorkOrderOperation_ID(), get_TrxName());
	          MMFGWorkOrderComponent[] woc = MMFGWorkOrderComponent.getOfWorkOrderOperation(woo, " M_Product_ID = " + wotl.getM_Product_ID(), (String)null);
	          if (woc != null && woc.length > 0) {
	            woc[0].setQtySpent(woc[0].getQtySpent().subtract(wotl.getQtyEntered()).setScale(MUOM.getPrecision(getCtx(), woc[0].getC_UOM_ID()), 4));
	            if (!woc[0].save(get_TrxName())) {
	              this.m_processMsg = "Error in reversing component usage";
	              return "IN";
	            } 
	          } 
	        } 
	        if (lastMandatoryOp == getOperationTo_ID() && STEPTO_ToMove.equals(stepTo))
	          updateAssemblyQty(getQtyEntered(), true, true); 
	        workOrder.setDateActualTo(null);
	        workOrder.save();
	      } 
	      setProcessed(true);
	    } 
	    if ((woTxnType.equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory) || woTxnType.equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory)) && !isReversal()) {
	      this.m_lines = getLines((String)null, "ORDER BY M_Product_ID");
	      if (this.m_lines.length != 0)
	        for (MMFGWorkOrderTransactionLine line : this.m_lines) {
	          if (!line.delete(true, get_TrxName())) {
	            this.m_processMsg = "WorkOrderTransaction " + getMFG_WORKORDERTRANSACTION_ID() + ": problem deleting existing lines (assembly returns and issues generate their own lines)";
	          //  get_TrxName().rollback();
	            Trx trx = Trx.get(get_TrxName(), true);
	            trx.rollback();
	            return "IN";
	          } 
	        }  
	      generateAssemblyTxnLine(getMFG_WORKORDERTRANSACTION_ID(), assemblyID, uomID, getQtyEntered(), getM_Locator_ID());
	    } 
	    if (woTxnType.equals(WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || woTxnType.equals(WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) || woTxnType.equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory) || woTxnType.equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory)) {
	      this.m_lines = getLines((String)null, "ORDER BY M_Product_ID");
	      if (this.m_lines.length == 0) {
	        this.m_processMsg = "WorkOrderTransaction " + getMFG_WORKORDERTRANSACTION_ID() + ": no lines to process";
	       // get_TrxName().rollback();
	        Trx trx = Trx.get(get_TrxName(), true);
	        trx.rollback();
	        return "IN";
	      } 
	      for (int i = 0; i < this.m_lines.length; i++) {
	        if (!processInvComponentLine(this.m_lines[i], woTxnType)) {
	          this.m_processMsg += "- WorkOrderTransaction " + getMFG_WORKORDERTRANSACTION_ID() + ": error processing inv component lines";
	         // get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } 
	    } 
	    if (woTxnType.equals(WORKORDERTXNTYPE_ResourceUsage)) {
	      MMFGWorkOrderResourceTxnLine[] wortLines = getResourceTxnLines((String)null, (String)null);
	      if (wortLines.length == 0) {
	        this.m_processMsg = "WorkOrderTransaction " + getMFG_WORKORDERTRANSACTION_ID() + ": no resource transaction lines to process";
	        //get_TrxName().rollback();
	        Trx trx = Trx.get(get_TrxName(), true);
	        trx.rollback();
	        return "IN";
	      } 
	      for (MMFGWorkOrderResourceTxnLine wortl : wortLines) {
	        if (!processResTxnLine(wortl)) {
	          this.m_processMsg += "- WorkOrderTransaction " + getMFG_WORKORDERTRANSACTION_ID() + ": error processing resource transaction lines";
	          //get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          return "IN";
	        } 
	      } 
	    } 
	    Timestamp trxDate = getDateTrx();
	    if (trxDate == null)
	      trxDate = new Timestamp(System.currentTimeMillis()); 
	    if (workOrder.getDateActualFrom() == null || workOrder.getDateActualFrom().after(trxDate))
	      workOrder.setDateActualFrom(trxDate); 
	    if (workOrder.getDateActualTo() == null || workOrder.getDateActualTo().before(trxDate))
	      workOrder.setDateActualTo(trxDate); 
	    if (!workOrder.save(get_TrxName())) {
	      this.m_processMsg = "Error in saving work order " + CLogger.retrieveError();
	     // get_TrxName().rollback();
	      Trx trx = Trx.get(get_TrxName(), true);
	      trx.rollback();
	      return "IN";
	    } 
	    setProcessed(true);
	    setDocAction(DOCACTION_Close);
	    if (!save(get_TrxName())) {
	      this.m_processMsg = "Error in saving Work Order Transaction - " + getMFG_WORKORDERTRANSACTION_ID();
	      //get_TrxName().rollback();
	      Trx trx = Trx.get(get_TrxName(), true);
	      trx.rollback();
	      return "IN";
	    } 
	    if (this.warningLog.length() > 0)
	      log.warning(this.warningLog.toString()); 
	    if (this.infoLog.length() > 0)
	      log.info(this.infoLog.toString()); 
	    return "CO";
	  }
	  
	  private boolean generateMoveResourceUsageTxn(int opFrom, int opTo) {
	    int count = 0;
	    MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	    String whereClause = " SeqNo BETWEEN " + opFrom + " AND " + opTo;
	    MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(wo, whereClause, "SeqNo");
	    ArrayList<MMFGWorkOrderResource> wors = new ArrayList<MMFGWorkOrderResource>();
	    for (MMFGWorkOrderOperation woo : woos) {
	      if (!woo.isOptional())
	        for (MMFGWorkOrderResource wor : MMFGWorkOrderResource.getofWorkOrderOperation(woo, (String)null, (String)null)) {
	          wors.add(wor);
	          if (wor.getChargeType().equals(X_MFG_WorkOrderResource.CHARGETYPE_Automatic))
	            count++; 
	        }  
	    } 
	    if (woos[woos.length - 1].isOptional())
	      for (MMFGWorkOrderResource wor : MMFGWorkOrderResource.getofWorkOrderOperation(woos[woos.length - 1], " QtyRequired != 0 ", (String)null)) {
	        wors.add(wor);
	        if (wor.getChargeType().equals(X_MFG_WorkOrderResource.CHARGETYPE_Automatic))
	          count++; 
	      }  
	    if (woos[0].isOptional())
	      for (MMFGWorkOrderResource wor : MMFGWorkOrderResource.getofWorkOrderOperation(woos[0], " QtyRequired != 0 ", (String)null)) {
	        wors.add(wor);
	        if (wor.getChargeType().equals(X_MFG_WorkOrderResource.CHARGETYPE_Automatic))
	          count++; 
	      }  
	    if (count > 0) {
	      MMFGWorkOrderTxnUtil wotUtil = new MMFGWorkOrderTxnUtil(true);
	      MMFGWorkOrderTransaction wot = wotUtil.createWOTxn(getCtx(), getMFG_WorkOrder_ID(), WORKORDERTXNTYPE_ResourceUsage, getMFG_WORKORDERTRANSACTION_ID(), get_TrxName());
	      if (wot == null) {
	        ValueNamePair pp = CLogger.retrieveError();
	        this.m_processMsg = pp.getName();
	        log.severe("Could not create Resource Usage Txn during Work Order Move : " + this.m_processMsg + ", for Work Order - " + getMFG_WorkOrder_ID());
	      } 
	      wot.setDateTrx(getDateTrx());
	      wot.setDateAcct(getDateAcct());
	      int no = (wotUtil.generateResourceTxnLine(getCtx(), wot.getMFG_WORKORDERTRANSACTION_ID(), getQtyEntered(), new BigDecimal(opFrom), new BigDecimal(opTo), get_TrxName(), true)).length;
	      if (no > 0) {
	    	  DocumentEngine docengine = new DocumentEngine (wot,  "CO");
				if(!docengine.processIt ( getDocAction())){
	      //  if (!DocumentEngine.processIt(wot, "CO")) {
	          this.m_processMsg = wot.getProcessMsg();
	          log.severe("Could not complete resource charging : " + this.m_processMsg);
	          return false;
	        } 
	        if (!wot.save(get_TrxName())) {
	          ValueNamePair pp = CLogger.retrieveError();
	          this.m_processMsg = pp.getName();
	          log.severe("Could not save Txn during Move Resource Usage.");
	          return false;
	        } 
	      } 
	    } 
	    return true;
	  }
	  
	  private boolean processResTxnLine(MMFGWorkOrderResourceTxnLine wortl) {
	    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(wortl.getCtx(), wortl.getMFG_WorkOrderOperation_ID(), wortl.get_TrxName());
	    String whereClause = " M_Product_ID = " + wortl.getM_Product_ID();
	    MMFGWorkOrderResource[] wor = MMFGWorkOrderResource.getofWorkOrderOperation(woo, whereClause, "SeqNo");
	    Timestamp trxDate = getDateTrx();
	    if (trxDate == null)
	      trxDate = new Timestamp(System.currentTimeMillis()); 
	    if (wor == null || wor.length == 0) {
	      this.warningLog.append("\nProduction Resource - " + MProduct.get(getCtx(), wortl.getM_Product_ID()) + " not in Work Order Operation - " + woo.getMFG_WorkOrderOperation_ID());
	    } else {
	      wor[0].setQtySpent(wor[0].getQtySpent().add(wortl.getQtyEntered()).setScale(MUOM.getPrecision(getCtx(), wor[0].getC_UOM_ID()), 4));
	      if (wor[0].getDateActualFrom() == null || wor[0].getDateActualFrom().after(trxDate))
	        wor[0].setDateActualFrom(trxDate); 
	      if (wor[0].getDateActualTo() == null || wor[0].getDateActualTo().before(trxDate))
	        wor[0].setDateActualTo(trxDate); 
	      wor[0].save(get_TrxName());
	      MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), woo.getMFG_WorkOrder_ID(), get_TrxName());
	      BigDecimal overissue = wor[0].getQtySpent().subtract(wor[0].getQtyRequired().multiply(wo.getQtyEntered()));
	      if (overissue.compareTo(BigDecimal.ZERO) > 0)
	        this.warningLog.append("\nOverissue of " + overissue + " for Production Resource " + MProduct.get(getCtx(), wor[0].getM_Product_ID())); 
	    } 
	    wortl.setProcessed(true);
	    if (!wortl.save(get_TrxName())) {
	      this.m_processMsg = "Could not save Resource Transaction Line - " + wortl.getMFG_WorkOrderResTxnLine_ID();
	      return false;
	    } 
	    if (woo.getDateActualFrom() == null || woo.getDateActualFrom().after(trxDate))
	      woo.setDateActualFrom(trxDate); 
	    if (woo.getDateActualTo() == null || woo.getDateActualTo().before(trxDate))
	      woo.setDateActualTo(trxDate); 
	    if (!woo.save(get_TrxName())) {
	      this.m_processMsg = "Could not update Dates for Work Order Operation - " + woo.getMFG_WorkOrderOperation_ID();
	      return false;
	    } 
	    return true;
	  }
	  
	  public MMFGWorkOrderResourceTxnLine[] getResourceTxnLines(String whereClause, String orderClause) {
	    ArrayList<MMFGWorkOrderResourceTxnLine> list = new ArrayList<MMFGWorkOrderResourceTxnLine>();
	    StringBuffer sql = new StringBuffer("SELECT * FROM MFG_WorkOrderResTxnLine WHERE MFG_WorkOrderTransaction_ID=? ");
	    if (whereClause != null)
	      sql.append(whereClause); 
	    if (orderClause != null)
	      sql.append(" ").append(orderClause); 
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
	      cPreparedStatement.setInt(1, getMFG_WORKORDERTRANSACTION_ID());
	      rs = cPreparedStatement.executeQuery();
	      while (rs.next()) {
	        MMFGWorkOrderResourceTxnLine ol = new MMFGWorkOrderResourceTxnLine(getCtx(), rs, get_TrxName());
	        list.add(ol);
	      } 
	      rs.close();
	      cPreparedStatement.close();
	      cPreparedStatement = null;
	    } catch (Exception e) {
	      log.log(Level.SEVERE, sql.toString(), e);
	    } finally {
	      DB.close(rs);
	      DB.close((Statement)cPreparedStatement);
	      rs = null;
	      cPreparedStatement = null;
	    } 
	    MMFGWorkOrderResourceTxnLine[] lines = new MMFGWorkOrderResourceTxnLine[list.size()];
	    list.toArray(lines);
	    return lines;
	  }
	  
	  public void addDescription(String description) {
	    String desc = getDescription();
	    if (desc == null) {
	      setDescription(description);
	    } else {
	      setDescription(desc + " | " + description);
	    } 
	  }
	  
	  protected boolean beforeDelete() {
	    if (isProcessed())
	      return false; 
	    if ("IP".equals(getDocStatus())) {
	      log.saveError("Prepared", "Prepared", false);
	      return false;
	    } 
	    return true;
	  }
	  
	  public File createPDF() {
	    try {
	      File temp = File.createTempFile(get_TableName() + get_ID() + "_", ".pdf");
	      return createPDF(temp);
	    } catch (Exception e) {
	      log.severe("Could not create PDF - " + e.getMessage());
	      return null;
	    } 
	  }
	  
	  public File createPDF(File file) {
	    return null;
	  }
	  
	  public boolean approveIt() {
	    return true;
	  }
	  
	  public boolean closeIt() {
	    setProcessing(false);
	    setDocAction(DOCACTION_None);
	    return true;
	  }
	  
	  public BigDecimal getApprovalAmt() {
	    return null;
	  }
	  
	  public int getC_Currency_ID() {
	    return 0;
	  }
	  
	  public int getDoc_User_ID() {
	    return 0;
	  }
	  
	  public String getProcessMsg() {
	    return this.m_processMsg;
	  }
	  
	  public String getSummary() {
	    return null;
	  }
	  
	  public boolean invalidateIt() {
	    return false;
	  }
	  
	  public String prepareIt() {
	    MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	    if (!wo.getDocStatus().equals(X_MFG_WorkOrder.DOCSTATUS_InProgress)) {
	      this.m_processMsg = "Work Order - " + wo.getMFG_WorkOrder_ID() + ", not in progress.";
	      return "IN";
	    } 
	    if (!DOCACTION_Complete.equals(getDocAction()))
	      setDocAction(DOCACTION_Complete); 
	    return "IP";
	  }
	  
	  protected boolean beforeSave(boolean newRecord) {
	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_WorkOrderMove) && is_ValueChanged("QtyEntered")) {
	      MMFGWorkOrderOperation opFrom = new MMFGWorkOrderOperation(getCtx(), getOperationFrom_ID(), get_TrxName());
	      String stepFrom = getStepFrom();
	      if (stepFrom.equals(STEPFROM_Queue)) {
	        if (opFrom.getQtyQueued().compareTo(getQtyEntered()) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	          log.saveError("Error", Msg.parseTranslation(getCtx(), "@QtyEntered@ > @QtyQueued@"));
	          return false;
	        } 
	      } else if (stepFrom.equals(STEPFROM_Run)) {
	        if (opFrom.getQtyRun().compareTo(getQtyEntered()) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	          log.saveError("Error", Msg.parseTranslation(getCtx(), "@QtyEntered@ > @QtyRun@"));
	          return false;
	        } 
	      } else if (stepFrom.equals(STEPTO_ToMove)) {
	        if (opFrom.getQtyAssembled().compareTo(getQtyEntered()) < 0) {
	          this.m_processMsg = "@NotEnoughQty@";
	          log.saveError("Error", Msg.parseTranslation(getCtx(), "@QtyEntered@ > @QtyAssembled@"));
	          return false;
	        } 
	      } else if (stepFrom.equals(STEPFROM_Scrap)) {
	        this.m_processMsg = "@NotEnoughQty@";
	        log.saveError("Error", "Quantities scrapped cannot be moved out");
	        return false;
	      } 
	    } 
	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	      if (getOperationTo_ID() <= 0) {
	        log.saveError("Error", "NoOperation");
	        return false;
	      } 
	      MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), getMFG_WorkOrder_ID(), get_TrxName());
	      if (getQtyEntered().compareTo(wo.getQtyAssembled().subtract(wo.getQtyAvailable())) > 0 && is_ValueChanged("QtyEntered")) {
	        log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotEnoughQty@"));
	        return false;
	      } 
	      setOperationFrom_ID(getOperationTo_ID());
	    } 
	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory) && is_ValueChanged("QtyEntered")) {
	      int operationID = 0;
	      BigDecimal QtyAssembled = BigDecimal.ZERO;
	      StringBuffer sql = new StringBuffer("SELECT MFG_WorkOrderOperation_ID, QtyAssembled FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? ORDER BY SeqNo DESC");
	      CPreparedStatement cPreparedStatement = null;
	      ResultSet rs = null;
	      try {
	    	  cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
	        cPreparedStatement.setInt(1, getMFG_WorkOrder_ID());
	        rs = cPreparedStatement.executeQuery();
	        if (rs.next()) {
	          operationID = rs.getInt(1);
	          QtyAssembled = rs.getBigDecimal(2);
	        } 
	        rs.close();
	        cPreparedStatement.close();
	        cPreparedStatement = null;
	      } catch (Exception e) {
	        log.log(Level.SEVERE, sql.toString(), e);
	      } finally {
	        DB.close(rs);
	        DB.close((Statement)cPreparedStatement);
	        rs = null;
	        cPreparedStatement = null; 
	      } 
	      if (QtyAssembled.compareTo(getQtyEntered()) < 0) {
	        log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotEnoughQty@"));
	        return false;
	      } 
	      setOperationFrom_ID(operationID);
	      setOperationTo_ID(operationID);
	    } 
	    if (is_ValueChanged("QtyEntered")) {
	      BigDecimal qtyEntered = getQtyEntered().setScale(MUOM.getPrecision(getCtx(), getC_UOM_ID()), 4);
	      if (qtyEntered.compareTo(getQtyEntered()) != 0) {
	        log.fine("Corrected QtyEntered Scale UOM=" + getC_UOM_ID() + "; QtyEntered =" + getQtyEntered() + "->" + qtyEntered);
	        setQtyEntered(qtyEntered);
	      } 
	    } 
	    return true;
	  }
	  
	  public boolean reActivateIt() {
	    return false;
	  }
	  
	  public boolean rejectIt() {
	    return false;
	  }
	  
	  public boolean reverseAccrualIt() {
	    return false;
	  }
	  
	  public int copyLinesFrom(MMFGWorkOrderTransaction otherWOTxn) {
//	    if (isProcessed() || isPosted() || otherWOTxn == null)
		  if (isProcessed() || otherWOTxn == null)
	      return 0; 
	    MMFGWorkOrderTransactionLine[] fromLines = otherWOTxn.getLines((String)null, "ORDER BY MFG_WorkOrderTrxLine_ID");
	    int count = 0;
	    for (MMFGWorkOrderTransactionLine fromLine : fromLines) {
	      MMFGWorkOrderTransactionLine line = new MMFGWorkOrderTransactionLine(getCtx(), 0, get_TrxName());
	      line.set_TrxName(get_TrxName());
	      PO.copyValues((PO)fromLine, (PO)line, fromLine.getAD_Client_ID(), fromLine.getAD_Org_ID());
	      line.setMFG_WORKORDERTRANSACTION_ID(getMFG_WORKORDERTRANSACTION_ID());
	      line.set_ValueNoCheck("MFG_WorkOrderTrxLine_ID", I_ZERO);
	      line.setProcessed(false);
	      if (line.save(get_TrxName()))
	        count++; 
	    } 
	    if (fromLines.length != count)
	      log.log(Level.SEVERE, "Line difference - From=" + fromLines.length + " <> Saved=" + count); 
	    return count;
	  }
	  
	  public int copyResLinesFrom(MMFGWorkOrderTransaction otherWOTxn) {
//	    if (isProcessed() || isPosted() || otherWOTxn == null)
		  if (isProcessed() || otherWOTxn == null)
	      return 0; 
	    MMFGWorkOrderResourceTxnLine[] fromResLines = otherWOTxn.getResourceTxnLines((String)null, "ORDER BY MFG_WorkOrderResTxnLine_ID");
	    int count = 0;
	    for (MMFGWorkOrderResourceTxnLine fromResLine : fromResLines) {
	      MMFGWorkOrderResourceTxnLine resLine = new MMFGWorkOrderResourceTxnLine(getCtx(), 0, get_TrxName());
	      resLine.set_TrxName(get_TrxName());
	      PO.copyValues((PO)fromResLine, (PO)resLine, fromResLine.getAD_Client_ID(), fromResLine.getAD_Org_ID());
	      resLine.setMFG_WORKORDERTRANSACTION_ID(getMFG_WORKORDERTRANSACTION_ID());
	      resLine.set_ValueNoCheck("M_WorkOrderResourceTxnLine_ID", I_ZERO);
	      resLine.setProcessed(false);
	      if (resLine.save(get_TrxName()))
	        count++; 
	    } 
	    if (fromResLines.length != count)
	      log.severe("Resource Line difference - From = " + fromResLines.length + " <> Saved = " + count); 
	    return count;
	  }
	  
	  private void setReversal(boolean reversal, int woTxnID) {
	    this.m_reversal = reversal;
	  }
	  
	  private void setParentReversal(int parentReversal) {
	    this.m_parentReversal = parentReversal;
	  }
	  
	  private boolean isReversal() {
	    return this.m_reversal;
	  }
	  
	  private void setForceReverse(boolean force) {
	    this.m_forceReverse = force;
	  }
	  
	  private boolean isForceReverse() {
	    return this.m_forceReverse;
	  }
	  
	  public void setProcessed(boolean processed) {
	    super.setProcessed(processed);
	    if (get_ID() == 0)
	      return; 
	    String set = "SET Processed='" + (processed ? "Y" : "N") + "' WHERE MFG_WorkOrderTransaction_ID=" + getMFG_WORKORDERTRANSACTION_ID();
	    int noLine =  DB.executeUpdateEx("UPDATE MFG_WorkOrderTrxLine " + set, new Object[0], get_TrxName());
	   
	    log.fine(processed + " - Lines=" + noLine);
	  }
	  
	  public boolean reverseCorrectIt() {
	    if (getParentWorkOrderTxn_ID() != 0 && !isForceReverse()) {
	      this.m_processMsg = "Cannot reverse a child transaction without reversing the parent transaction - " + getParentWorkOrderTxn_ID();
	      return false;
	    } 
	    log.info(toString());
	    if (!MPeriod.isOpen(getCtx(), getDateAcct(), getDocBaseType(), getAD_Org_ID()))
		{
			this.m_processMsg = "@PeriodClosed@";
			//return DocAction.STATUS_Invalid;
		}
	    //this.m_processMsg = DocumentEngine.isPeriodOpen(this);
	    if (this.m_processMsg != null)
	      return false; 
	    MMFGWorkOrderTransaction reversal = new MMFGWorkOrderTransaction(getCtx(), 0, get_TrxName());
	    copyValues((PO)this, (PO)reversal, getAD_Client_ID(), getAD_Org_ID());
	    reversal.set_ValueNoCheck("DocumentNo", null);
	    reversal.setDocStatus(DOCSTATUS_Drafted);
	    reversal.setDocAction(DOCACTION_Complete);
	    reversal.setIsApproved(false);
	   // reversal.setPosted(false);
	    reversal.setDocumentNo(getDocumentNo() + "^");
	    reversal.addDescription("{->" + getDocumentNo() + ")");
	    reversal.setQtyEntered(getQtyEntered().negate());
	    reversal.setDateTrx(new Timestamp((new Date()).getTime()));
	    reversal.setProcessed(false);
	    if (this.m_parentReversal != 0)
	      reversal.setParentWorkOrderTxn_ID(this.m_parentReversal); 
	    MMFGWorkOrder workorder = new MMFGWorkOrder(getCtx(), reversal.getMFG_WorkOrder_ID(), get_TrxName());
	    if (DOCSTATUS_Reversed.equals(workorder.getDocStatus())) {
	      MMFGWorkOrder WOReversal = workorder.getReversal();
	      if (WOReversal != null) {
	        reversal.setReversalM_WorkOrder_ID(WOReversal.getMFG_WorkOrder_ID());
	        if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_WorkOrderMove) || getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory) || getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	          int fromSeqNo = DB.getSQLValue(get_TrxName(), "SELECT SeqNo FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ?", new Object[] { Integer.valueOf(reversal.getOperationFrom_ID()) });
	          int reversalFromOpID = DB.getSQLValue(get_TrxName(), "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND SeqNo = ?", new Object[] { Integer.valueOf(reversal.getMFG_WorkOrder_ID()), Integer.valueOf(fromSeqNo) });
	          reversal.setOperationFrom_ID(reversalFromOpID);
	          if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory) || getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
	            reversal.setOperationTo_ID(reversalFromOpID);
	          } else {
	            int toSeqNo = DB.getSQLValue(get_TrxName(), "SELECT SeqNo FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ?", new Object[] { Integer.valueOf(reversal.getOperationTo_ID()) });
	            int reversalToOpID = DB.getSQLValue(get_TrxName(), "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND SeqNo = ?", new Object[] { Integer.valueOf(reversal.getMFG_WorkOrder_ID()), Integer.valueOf(toSeqNo) });
	            reversal.setOperationTo_ID(reversalToOpID);
	          } 
	        } 
	      } 
	    } 
	    if (!reversal.save(get_TrxName())) {
	      this.m_processMsg = "Could not create Reversal for Work Order Transaction - " + getMFG_WORKORDERTRANSACTION_ID();
	      return false;
	    } 
	    reversal.setReversal(true, getMFG_WORKORDERTRANSACTION_ID());
	    String sql = "SELECT MFG_WorkOrderTransaction_ID FROM MFG_WorkOrderTransaction WHERE ParentWorkOrderTxn_ID = ?";
	    MRole role = MRole.getDefault(getCtx(), false);
	    sql = role.addAccessSQL(sql, "MFG_WorkOrderTransaction", false, false);
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    boolean success = true;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	      cPreparedStatement.setInt(1, getMFG_WORKORDERTRANSACTION_ID());
	      rs = cPreparedStatement.executeQuery();
	      while (rs.next()) {
	        MMFGWorkOrderTransaction childToReverse = new MMFGWorkOrderTransaction(getCtx(), rs.getInt(1), get_TrxName());
	        childToReverse.setForceReverse(true);
	        childToReverse.setParentReversal(reversal.getMFG_WORKORDERTRANSACTION_ID());
	        DocumentEngine engine = new DocumentEngine(childToReverse,"RC");
	        if(!engine.processIt(getDocAction())) {
	       // if (!DocumentEngine.processIt(childToReverse, "RC")) {
	          this.m_processMsg = "Child Reversal ERROR: " + childToReverse.getProcessMsg();
	          //get_TrxName().rollback();
	          Trx trx = Trx.get(get_TrxName(), true);
	          trx.rollback();
	          success = false;
	          break;
	        } 
	        childToReverse.save(get_TrxName());
	      } 
	      rs.close();
	      cPreparedStatement.close();
	    } catch (SQLException e) {
	      log.log(Level.SEVERE, sql, e);
	      this.m_processMsg = "Could not reverse child transactions of Work Order Transaction " + getMFG_WorkOrder_ID();
	      return false;
	    } finally {
	      DB.close(rs);
	      DB.close((Statement)cPreparedStatement);
	      rs = null;
	      cPreparedStatement = null;
	    } 
	    if (!success)
	      return false; 
	    if (WORKORDERTXNTYPE_ResourceUsage.equals(getWorkOrderTxnType())) {
	      reversal.copyResLinesFrom(this);
	      MMFGWorkOrderResourceTxnLine[] rResLines = reversal.getResourceTxnLines((String)null, "ORDER BY MFG_WorkOrderResTxnLine_ID");
	      for (MMFGWorkOrderResourceTxnLine rResLine : rResLines) {
	        rResLine.setQtyEntered(rResLine.getQtyEntered().negate());
	        int SeqNo = DB.getSQLValue(get_TrxName(), "SELECT SeqNo FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ?", new Object[] { Integer.valueOf(rResLine.getMFG_WorkOrderOperation_ID()) });
	        int reversalOpID = DB.getSQLValue(get_TrxName(), "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND SeqNo = ?", new Object[] { Integer.valueOf(reversal.getMFG_WorkOrder_ID()), Integer.valueOf(SeqNo) });
	        rResLine.setMFG_WorkOrderOperation_ID(reversalOpID);
	        if (!rResLine.save(get_TrxName())) {
	          this.m_processMsg = "Could not save reversal line for resource transaction - " + getMFG_WORKORDERTRANSACTION_ID();
	          return false;
	        } 
	      } 
	    } else {
	      reversal.copyLinesFrom(this);
	      MMFGWorkOrderTransactionLine[] rLines = reversal.getLines((String)null, "ORDER BY MFG_WorkOrderTrxLine_ID");
	      for (int i = 0; i < rLines.length; i++) {
	        MMFGWorkOrderTransactionLine rLine = rLines[i];
	        rLine.setQtyEntered(rLine.getQtyEntered().negate());
	        int SeqNo = DB.getSQLValue(get_TrxName(), "SELECT SeqNo FROM MFG_WorkOrderOperation WHERE MFG_WorkOrderOperation_ID = ?", new Object[] { Integer.valueOf(rLine.getMFG_WorkOrderOperation_ID()) });
	        int reversalOpID = DB.getSQLValue(get_TrxName(), "SELECT MFG_WorkOrderOperation_ID FROM MFG_WorkOrderOperation WHERE MFG_WorkOrder_ID = ? AND SeqNo = ?", new Object[] { Integer.valueOf(reversal.getMFG_WorkOrder_ID()), Integer.valueOf(SeqNo) });
	        rLine.setMFG_WorkOrderOperation_ID(reversalOpID);
	        if (!rLine.save(get_TrxName())) {
	          this.m_processMsg = "Could not save reversal line for component transaction - " + getMFG_WORKORDERTRANSACTION_ID();
	          return false;
	        } 
	        if (rLine.getM_AttributeSetInstance_ID() == 0) {
	          this.m_lines = getLines((String)null, "ORDER BY M_Product_ID");
	          MMFGWorkOrderTransactionLineMA[] mas = MMFGWorkOrderTransactionLineMA.get(getCtx(), this.m_lines[i].getMFG_WorkOrderTrxLine_ID(), get_TrxName());
	          for (MMFGWorkOrderTransactionLineMA element : mas) {
	            MMFGWorkOrderTransactionLineMA ma = new MMFGWorkOrderTransactionLineMA(rLine, element.getM_AttributeSetInstance_ID(), element.getMovementQty().negate());
	            if (!ma.save());
	          } 
	        } 
	      } 
	    } 
	    DocumentEngine engine = new DocumentEngine(reversal,"CO");
	    if(!engine.processIt(getDocAction())) {
	   // if (!DocumentEngine.processIt(reversal, "CO")) {
	      this.m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
	     // get_TrxName().rollback();
	      Trx trx = Trx.get(get_TrxName(), true);
	      trx.rollback();
	      return false;
	    } 
	    DocumentEngine docengine = new DocumentEngine(reversal,"CL");
	    docengine.processIt(getDocAction());
	    //DocumentEngine.processIt(reversal, "CL");
	    reversal.setDocStatus(DOCSTATUS_Reversed);
	    reversal.setDocAction(DOCACTION_None);
	    reversal.save();
	    this.m_processMsg = reversal.getDocumentNo();
	    addDescription("(" + reversal.getDocumentNo() + "<-)");
	    setProcessed(true);
	    setDocStatus(DOCSTATUS_Reversed);
	    setDocAction(DOCACTION_None);
	    return true;
	  }
	  
	  public String toString() {
	    StringBuffer sb = (new StringBuffer("MMFGWorkOrderTransaction[")).append(get_ID()).append("-").append(getDocumentNo()).append(",C_DocType_ID=").append(getC_DocType_ID()).append(", M_Product_ID=").append(getM_Product_ID()).append(", WorkOrderTxnType=").append(getWorkOrderTxnType()).append(", QtyEntered=").append(getQtyEntered()).append("]");
	    return sb.toString();
	  }
	  
	  public boolean unlockIt() {
	    return false;
	  }
	  
	  private void setForceVoid(boolean force) {
	    this.m_forceVoid = force;
	  }
	  
	  private boolean isForceVoid() {
	    return this.m_forceVoid;
	  }
	  
	  public boolean voidIt() {
	    log.info(toString());
	    if (getParentWorkOrderTxn_ID() != 0 && !isForceVoid()) {
	      this.m_processMsg = "Cannot void a child transaction - " + getMFG_WORKORDERTRANSACTION_ID() + ", without voiding the parent transaction - " + getParentWorkOrderTxn_ID();
	      return false;
	    } 
	    if (DOCSTATUS_Closed.equals(getDocStatus()) || DOCSTATUS_Reversed.equals(getDocStatus()) || DOCSTATUS_Voided.equals(getDocStatus())) {
	      this.m_processMsg = "Document Closed: " + getDocStatus();
	      return false;
	    } 
	    if (DOCSTATUS_Drafted.equals(getDocStatus()) || DOCSTATUS_Invalid.equals(getDocStatus()) || DOCSTATUS_InProgress.equals(getDocStatus()) || DOCSTATUS_Approved.equals(getDocStatus()) || DOCSTATUS_NotApproved.equals(getDocStatus())) {
	      String sql = "SELECT MFG_WorkOrderTransaction_ID FROM MFG_WorkOrderTransaction WHERE ParentWorkOrderTxn_ID = ?";
	      MRole role = MRole.getDefault(getCtx(), false);
	      sql = role.addAccessSQL(sql, "MFG_WorkOrderTransaction", false, false);
	      CPreparedStatement cPreparedStatement = null;
	      ResultSet rs = null;
	      try {
	    	  cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	        cPreparedStatement.setInt(1, getMFG_WORKORDERTRANSACTION_ID());
	        rs = cPreparedStatement.executeQuery();
	        while (rs.next()) {
	          MMFGWorkOrderTransaction childToVoid = new MMFGWorkOrderTransaction(getCtx(), rs.getInt(1), get_TrxName());
	          childToVoid.setForceVoid(true);
	          DocumentEngine engine = new DocumentEngine(childToVoid,"VO");
	          engine.processIt(getDocAction());
	          //DocumentEngine.processIt(childToVoid, "VO");
	          childToVoid.save();
	        } 
	        rs.close();
	        cPreparedStatement.close();
	      } catch (SQLException e) {
	        log.log(Level.SEVERE, sql, e);
	        this.m_processMsg = "Could not void child transactions of Work Order Transaction " + getMFG_WorkOrder_ID();
	        return false;
	      } finally {
	        DB.close(rs);
	        DB.close((Statement)cPreparedStatement);
	        rs = null;
	        cPreparedStatement = null;
	      } 
	      this.m_lines = getLines((String)null, (String)null);
	      for (MMFGWorkOrderTransactionLine line : this.m_lines) {
	        BigDecimal old = line.getQtyEntered();
	        if (old.signum() != 0) {
	          line.setQtyEntered(Env.ZERO);
	          line.addDescription("Void (" + old + ")");
	          line.save(get_TrxName());
	        } 
	      } 
	      MMFGWorkOrderResourceTxnLine[] lines = getResourceTxnLines((String)null, (String)null);
	      for (MMFGWorkOrderResourceTxnLine line : lines) {
	        BigDecimal old = line.getQtyEntered();
	        if (old.signum() != 0) {
	          line.setQtyEntered(Env.ZERO);
	          line.addDescription("Void (" + old + ")");
	          line.save(get_TrxName());
	        } 
	      } 
	      if (getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_WorkOrderMove)) {
	        BigDecimal old = getQtyEntered();
	        if (old.signum() != 0) {
	          setQtyEntered(Env.ZERO);
	          addDescription("Void (" + old + ")");
	        } 
	      } 
	    } else {
	    	DocumentEngine engine = new DocumentEngine(this,"RC");
	    	return engine.processIt(getDocAction());
	     // return DocumentEngine.processIt(this, "RC");
	    } 
	    setProcessed(true);
	    setDocAction(DOCACTION_None);
	    return true;
	  }
	  
	  public void setProcessMsg(String processMsg) {
	    this.m_processMsg = processMsg;
	  }
	  
	  public String getDocBaseType() {
	    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
	    return dt.getDocBaseType();
	  }
	  
	  public Timestamp getDocumentDate() {
	    return getDateTrx();
	  }
	  
	//  public Env.QueryParams getLineOrgsQueryInfo() {
//	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_ResourceUsage))
//	      return new Env.QueryParams("SELECT DISTINCT AD_Org_ID FROM MFG_WorkOrderResTxnLine WHERE M_WorkOrderTransaction_ID = ?", new Object[] { Integer.valueOf(getMFG_WORKORDERTRANSACTION_ID()) }); 
//	    if (getWorkOrderTxnType().equals(WORKORDERTXNTYPE_WorkOrderMove) || getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyCompletionToInventory) || getWorkOrderTxnType().equals(WORKORDERTXNTYPE_AssemblyReturnFromInventory))
//	      return null; 
//	    return new Env.QueryParams("SELECT DISTINCT AD_Org_ID FROM MFG_WorkOrderTrxLine WHERE MFG_WorkOrderTransaction_ID=? ", new Object[] { Integer.valueOf(getMFG_WORKORDERTRANSACTION_ID()) });
	//  }

	@Override
	public boolean processIt(String processAction) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}
}

