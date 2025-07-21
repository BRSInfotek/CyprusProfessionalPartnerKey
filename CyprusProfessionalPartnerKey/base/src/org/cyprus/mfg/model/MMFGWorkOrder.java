package org.cyprus.mfg.model;

//import com.cyprusbrs.client.SysEnv;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.vos.ChangeVO;
import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProcess;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.framework.X_M_BOM;
import org.cyprusbrs.framework.X_M_Cost;
//import org.cyprusbrs.apps.ProcessCtl;
//import org.cyprusbrs.api.UICallout;
//import org.cyprusbrs.apps.ProcessCtl;
//import org.cyprusbrs.framework.PO;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.process.ProcessInfo;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Ini;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Util;

public class MMFGWorkOrder extends X_MFG_WorkOrder implements DocAction {
	

	  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrder.class);
	  
	  private static final long serialVersionUID = 1L;
	  
	  public static final String REVERSE_INDICATOR = "^";
	  
	  private int m_MRP_PlannedOrder_ID;
	  
	  private String m_processMsg;
	  
	  private boolean m_callPrepared;
	  
	  protected ChangeVO p_changeVO;
	  
	  public MMFGWorkOrder(Properties ctx, int MFG_WorkOrder_ID, String trxName) {
			super(ctx, MFG_WorkOrder_ID, trxName);
	    this.m_MRP_PlannedOrder_ID = 0;
	    this.m_processMsg = null;
	    this.m_callPrepared = false;
	    if (MFG_WorkOrder_ID == 0) {
	      setDocStatus(DOCSTATUS_Drafted);
	      setDocAction(DOCACTION_Prepare);
	      setIsApproved(false);
	      setIsSOTrx(true);
	      super.setProcessed(false);
	      setProcessing(false);
//	      setPosted(false);
	    } 
	  }
	  
	  public MMFGWorkOrder(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	    this.m_MRP_PlannedOrder_ID = 0;
	    this.m_processMsg = null;
	    this.m_callPrepared = false;
	  }
	  
	  public int getVarMRP_PlannedOrder_ID() {
	    return this.m_MRP_PlannedOrder_ID;
	  }
	  
	  public void setVarMRP_PlannedOrder_ID(int plannedID) {
	    this.m_MRP_PlannedOrder_ID = plannedID;
	  }
	  
	  public static MMFGWorkOrder getWorkOrder(Properties ctx, String documentNo, String trx) {
		  MMFGWorkOrder workorder = null;
	    String sql = "SELECT * FROM MFG_WorkOrder WHERE DocumentNo = ?";
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql, trx);
	      cPreparedStatement.setString(1, documentNo);
	      rs = cPreparedStatement.executeQuery();
	      while (rs.next())
	        workorder = new MMFGWorkOrder(ctx, rs, trx); 
	      rs.close();
	      cPreparedStatement.close();
	      cPreparedStatement = null;
	    } catch (Exception e) {
	      log.log(Level.SEVERE, sql, e);
	    } finally {
		   DB.close(rs, cPreparedStatement);
		   rs = null; cPreparedStatement = null;
//	      DB.closeResultSet(rs);
//	      DB.closeStatement((Statement)cPreparedStatement);
	    } 
	    return workorder;
	  }
	  
	  public int copyLinesFrom(MMFGWorkOrder otherWorkOrder) {
	    if (isProcessed() || isPosted() || otherWorkOrder == null)
	      return 0; 
	    int count = 0;
	    MMFGWorkOrderOperation[] fromOperations = MMFGWorkOrderOperation.getOfWorkOrder(otherWorkOrder, (String)null, (String)null);
	    for (MMFGWorkOrderOperation fromOperation : fromOperations) {
	    	MMFGWorkOrderOperation operation = new MMFGWorkOrderOperation(getCtx(), 0, get_TrxName());
	      copyValues((PO)fromOperation, (PO)operation, fromOperation.getAD_Client_ID(), fromOperation.getAD_Org_ID());
	      operation.setMFG_WorkOrder_ID(getMFG_WorkOrder_ID());
	      operation.setHeaderInfo(this);
	      operation.set_ValueNoCheck("M_WorkOrderOperation_ID", I_ZERO);
	      if (operation.save())
	        count++; 
	      MMFGWorkOrderComponent[] fromComponents = MMFGWorkOrderComponent.getOfWorkOrderOperation(fromOperation, (String)null, (String)null);
	      int linecount = 0;
	      for (MMFGWorkOrderComponent fromComponent : fromComponents) {
	    	  MMFGWorkOrderComponent component = new MMFGWorkOrderComponent(getCtx(), 0, get_TrxName());
	        copyValues((PO)fromComponent, (PO)component, fromComponent.getAD_Client_ID(), fromComponent.getAD_Org_ID());
	        component.setMFG_WorkOrderOperation_ID(operation.getMFG_WorkOrderOperation_ID());
	        component.setHeaderInfo(operation);
	        component.set_ValueNoCheck("M_WorkOrderComponent_ID", I_ZERO);
	        if (component.save())
	          linecount++; 
	      } 
	      if (fromComponents.length != linecount)
	        log.log(Level.SEVERE, "Component Line difference - From=" + fromComponents.length + " <> Saved=" + linecount); 
	      MMFGWorkOrderResource[] fromResources = MMFGWorkOrderResource.getofWorkOrderOperation(fromOperation, (String)null, (String)null);
	      linecount = 0;
	      for (MMFGWorkOrderResource fromResource : fromResources) {
	    	  MMFGWorkOrderResource resource = new MMFGWorkOrderResource(getCtx(), 0, get_TrxName());
	        copyValues((PO)fromResource, (PO)resource, fromResource.getAD_Client_ID(), fromResource.getAD_Org_ID());
	        resource.setMFG_WorkOrderOperation_ID(operation.getMFG_WorkOrderOperation_ID());
	        resource.setHeaderInfo(operation);
	        resource.set_ValueNoCheck("M_WorkOrderResource_ID", I_ZERO);
	        if (resource.save())
	          linecount++; 
	      } 
	      if (fromResources.length != linecount)
	        log.severe("Resource Line difference - From = " + fromResources.length + " <> Saved = " + linecount); 
	    } 
	    if (fromOperations.length != count)
	      log.severe("Operation Line difference - From = " + fromOperations.length + " <> Saved = " + count); 
	    return count;
	  }
	  
	  public boolean approveIt() {
	    log.info("approveIt - " + toString());
	    setIsApproved(true);
	    return true;
	  }
	  
	  protected boolean beforeSave(boolean newRecord) {
	    
		  
	    if (getAD_Org_ID() == 0) {
	      int context_AD_Org_ID = Env.getAD_Org_ID(getCtx());
	      if (context_AD_Org_ID != 0) {
	        setAD_Org_ID(context_AD_Org_ID);
	        log.warning("Changed Org to Context=" + context_AD_Org_ID);
	      } else {
	        log.saveError("Error", Msg.translate(getCtx(), "Org0NotAllowed"));
	        return false;
	      } 
	    } 
	    if (getAD_Client_ID() == 0)
	      return false; 
	    if (newRecord) {
	      MClient client = MClient.get(getCtx(), getAD_Client_ID());
	      MAcctSchema acctSchema = client.getAcctSchema();
	      if (!acctSchema.getCostingMethod().equals(X_M_Cost.COSTINGMETHOD_StandardCosting))
	        log.saveWarning(Msg.getMsg(getCtx(), "NoStandardCosting"), ""); 
	    } 
	    if (getM_Product_ID() == 0)
	      return false; 
	    if (getDocStatus().equals(X_MFG_WorkOrder.DOCSTATUS_Drafted) && !this.m_callPrepared && 
	      getDateScheduleFrom() != null && getDateScheduleTo() != null) {
	      log.saveError("Error", Msg.translate(getCtx(), "Enter either ScheduledDateFrom or ScheduledDateTo(Not Both, Scheduler will calculate the other date)"));
	      return false;
	    } 
	    if (getDateActualFrom() != null && getDateActualTo() != null)
	      if (getDateActualTo().before(getDateActualFrom())) {
	        log.saveError("Error", Msg.parseTranslation(getCtx(), "@DateActualFrom@ > @DateActualTo@"));
	        return false;
	      }  
	    if (getQtyEntered().compareTo(Env.ZERO) < 0) {
	      log.saveError("Error", Msg.parseTranslation(getCtx(), "@QtyEntered@ < 0"));
	      return false;
	    } 
	    if (MLocator.get(getCtx(), getM_Locator_ID()).getM_Warehouse_ID() != getM_Warehouse_ID()) {
	      log.saveError("Error", Msg.getMsg(getCtx(), "WarehouseLocatorMismatch"));
	      return false;
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
	  
	  protected boolean beforeDelete() {
	    if (isProcessed())
	      return false; 
	    if ("IP".equals(getDocStatus())) {
	      log.saveError("Prepared", "Prepared", false);
	      return false;
	    } 
	    return true;
	  }
	  
	  public MMFGWorkOrder getReversal() {
	    String description = getDescription();
	    if (description == null || description.length() == 0)
	      return null; 
	    String s = description;
	    int pos1 = 0;
	    pos1 = s.indexOf("<-)");
	    if (pos1 == -1)
	      return null; 
	    int pos2 = s.lastIndexOf("(", pos1);
	    if (pos2 == -1)
	      return null; 
	    String workorderDocNo = s.substring(pos2 + 1, pos1);
	    MMFGWorkOrder reversal = getWorkOrder(getCtx(), workorderDocNo, get_TrxName());
	    return reversal;
	  }
	  
	  public void setProcessed(boolean processed) {
	    super.setProcessed(processed);
	    if (get_ID() == 0)
	      return; 
	    MMFGWorkOrderOperation[] operations = MMFGWorkOrderOperation.getOfWorkOrder(this, (String)null, "SeqNo");
	    int numComponent = 0, numResource = 0;
	    for (MMFGWorkOrderOperation operation : operations) {
	      operation.setProcessed(true);
	      String sql = "SET Processed='Y' WHERE MFG_WorkOrderOperation_ID =" + operation.get_ID();	      
	      
	      try {
//			numComponent += DB.executeUpdate(get_Trx(), "UPDATE MFG_WorkOrderComponent " + sql, new Object[0]); /// Old Format in Cmp
//			numResource += DB.executeUpdate(get_Trx(), "UPDATE MFG_WorkOrderResource " + sql, new Object[0]); /// Old Format cmp
			numComponent += DB.executeUpdate( "UPDATE MFG_WorkOrderComponent " + sql,true,get_TrxName());
			numResource += DB.executeUpdate("UPDATE MFG_WorkOrderResource " + sql, true, get_TrxName());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	      operation.save();
	    } 
	    log.fine(processed + " - Components = " + numComponent + ", Operations = " + operations.length + ", Resources = " + numResource);
	  }
	  
	  public boolean closeIt() {
	    if (!reverseTasks(true)) {
	      this.m_processMsg = Msg.getMsg(getCtx(), "CannotReverseTasks");
	      return false;
	    } 
	    log.info(toString());
	    setProcessed(true);
	    setDocAction(DOCACTION_None);
	    return true;
	  }
	  
	  public String completeIt() {
	    MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(this, (String)null, " SeqNo DESC ");
	    for (MMFGWorkOrderOperation woo : woos) {
	    	MMFGWorkOrderComponent[] wocs = MMFGWorkOrderComponent.getOfWorkOrderOperation(woo, (String)null, (String)null);
	      for (MMFGWorkOrderComponent woc : wocs) {
	        if (!woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_AssemblyPull) && !woc.getSupplyType().equals(X_MFG_WorkOrderComponent.SUPPLYTYPE_OperationPull))
	          if (woc.getQtySpent().compareTo(woc.getQtyAvailable()) > 0) {
	            this.m_processMsg = "@NotEnoughQty@";
	            return "IN";
	          }  
	      } 
	    } 
	    if (!getWOType().equals(WOTYPE_Standard)) {
	      String sql = "SELECT Sum(QtyAvailable) FROM M_WorkOrderComponent WHERE M_WorkOrderOperation_ID IN (SELECT M_WorkOrderOperation_ID FROM M_WorkOrder WHERE M_WorkOrder_ID = ?) AND M_Product_ID = ?";
	      BigDecimal Qty = BigDecimal.ZERO;
	      CPreparedStatement cPreparedStatement = null;
	      ResultSet rs = null;
	      try {
	    	  cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	        cPreparedStatement.setInt(1, getMFG_WorkOrder_ID());
	        cPreparedStatement.setInt(2, getM_Product_ID());
	        rs = cPreparedStatement.executeQuery();
	        if (rs.next())
	          Qty = rs.getBigDecimal(1); 
	        if (rs.wasNull())
	          Qty = BigDecimal.ZERO; 
	        rs.close();
	        cPreparedStatement.close();
	        cPreparedStatement = null;
	      } catch (SQLException e) {
	        cPreparedStatement = null;
	        e.printStackTrace();
	      } finally {
	    	  
	    	DB.close(rs, cPreparedStatement);  
	    	rs = null; cPreparedStatement = null;
//	        DB.closeResultSet(rs);
//	        DB.closeStatement((Statement)cPreparedStatement);
	      } 
	      if (Qty.compareTo(getQtyAssembled().subtract(getQtyAvailable())) < 0) {
	        this.m_processMsg = "@NotEnoughQty@";
	        log.severe("Enough Product Assemblies have not been issued to complete Work Order - " + getMFG_WorkOrder_ID() + "; Required - " + Qty + ", Issued - " + getQtyAssembled().subtract(getQtyAvailable()));
	        return "IN";
	      } 
	    } 
	    if (getDocumentNo().endsWith("^")) {
	    	MMFGWorkOrderTransaction[] WOTxns = MMFGWorkOrderTransaction.getOfWorkOrder(this, (String)null, (String)null);
	      if (WOTxns == null || 0 == WOTxns.length) {
	        this.m_processMsg = "@NoWODetails@";
	        return "IN";
	      } 
	      if (WOTxns != null) {
	        log.fine("WOTxns #" + WOTxns.length);
	        MMFGWorkOrderTransaction[] arr$;
	        int len$, i$;
	        for (arr$ = WOTxns, len$ = arr$.length, i$ = 0; i$ < len$; ) {
	        	MMFGWorkOrderTransaction element = arr$[i$];
	          String status = element.getDocStatus();
	          if (DOCSTATUS_Reversed.equals(status) || DOCSTATUS_Voided.equals(status) || DOCSTATUS_Closed.equals(status) || DOCSTATUS_Completed.equals(status)) {
	            i$++;
	            continue;
	          } 
	          this.m_processMsg = "@WOTxnsNotCompleted@";
	          return "IN";
	        } 
	      } 
	    } 
	    return "CO";
	  }
	  
	  public File createPDF() {
	    try {
	      File temp = File.createTempFile(get_TableName() + get_ID() + "_", ".pdf");
	      return createPDF(temp);
	    } catch (Exception e) {
	      log.severe("Could not create PDF - " + e.getMessage() + ", for Work Order - " + getMFG_WorkOrder_ID());
	      return null;
	    } 
	  }
	  
	  public File createPDF(File file) {
	    return null;
	  }
	  
	  public BigDecimal getApprovalAmt() {
	    return null;
	  }
	  
	  public int getC_Currency_ID() {
	    return 0;
	  }
	  
	  public int getDoc_User_ID() {
	    if (getSalesRep_ID() != 0)
	      return getSalesRep_ID(); 
	    return getCreatedBy();
	  }
	  
	  public String getDocumentInfo() {
	    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
	    return dt.getName() + " " + getDocumentNo();
	  }
	  
	  public String getProcessMsg() {
	    return this.m_processMsg;
	  }
	  
	  public String getSummary() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(getDocumentNo());
	    sb.append(": ");
	    MProduct product = new MProduct(getCtx(), getM_Product_ID(), null);
	    sb.append(product.getName());
	    if (getDescription() != null && getDescription().length() > 0)
	      sb.append(" - ").append(getDescription()); 
	    return sb.toString();
	  }
	  
	  public boolean invalidateIt() {
	    log.info(toString());
	    setDocAction(DOCACTION_Prepare);
	    return true;
	  }
	  
	  public String prepareIt() {
	    String docStatus = getDocStatus();
	    MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(this, (String)null, "SeqNo");
	    if (woos == null || 0 == woos.length) {
	      this.m_processMsg = "@NoWOOperations@";
	      return docStatus;
	    } 
	    if (woos[woos.length - 1].isOptional() || woos[0].isOptional()) {
	      this.m_processMsg = "@LastOperation@";
	      return docStatus;
	    } 
	    setIsSOTrx(false);
	    setProcessed(true);
	    if (woos.length > 0 && (getDocStatus().equals("DR") || getDocStatus().equals("IN"))) {
	      BigDecimal qty = BigDecimal.ZERO;
	      for (MMFGWorkOrderOperation woo : woos)
	        qty = qty.add(woo.getQtyAssembled().add(woo.getQtyScrapped()).add(woo.getQtyQueued()).add(woo.getQtyRun())); 
	      qty = qty.add(getQtyAssembled());
	      if (qty.compareTo(BigDecimal.ZERO) == 0) {
	        woos[0].setQtyQueued(getQtyEntered());
	        woos[0].save();
	      } 
	    } 
	    if ((getDateScheduleFrom() != null || getDateScheduleTo() != null) && (getDocStatus().equals("DR") || getDocStatus().equals("IN"))) {
	      boolean getparameters = true;
	      int AD_Process_ID = MProcess.getIDByValue(getCtx(), "MFG_WorkOrderScheduler");
	      MPInstance instance = new MPInstance(getCtx(), AD_Process_ID, 0);
	      if (!instance.save()) {
	        this.m_processMsg = "@RunWOScheduler@";
	        log.saveError("Error", Msg.parseTranslation(getCtx(), "Error while retrieving the WorkOrderScheduler instance"));
	      } else {
	        ProcessInfo pi = new ProcessInfo("", AD_Process_ID);
	        pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
	        pi.setAD_Client_ID(getAD_Client_ID());
	        MPInstancePara para1 = new MPInstancePara(instance, 10);
	        para1.setParameter("MFG_WorkOrder_ID", getMFG_WorkOrder_ID());
	        if (!para1.save())
	          getparameters = false; 
	        MPInstancePara para2 = new MPInstancePara(instance, 20);
	        String dateScheduledFrom = null;
	        if (getDateScheduleFrom() != null)
	          dateScheduledFrom = getDateScheduleFrom().toString(); 
	        para2.setParameter("DateScheduleFrom", dateScheduledFrom);
	        if (!para2.save())
	          getparameters = false; 
	        MPInstancePara para3 = new MPInstancePara(instance, 30);
	        String dateScheduledTo = null;
	        if (getDateScheduleTo() != null)
	          dateScheduledTo = getDateScheduleTo().toString(); 
	        para3.setParameter("DateScheduleTo", dateScheduledTo);
	        if (!para3.save())
	          getparameters = false; 
	        if (!getparameters) {
	          this.m_processMsg = "@RunWOScheduler@";
	          log.saveError("Error", Msg.parseTranslation(getCtx(), "While saving the parameters"));
	        } else {
	        	
	         /// Commented by Mukesh as this class exist in client/src package... so build is not creating 
	        /// Need to discuss then able to uncomment this.... @20220725	
//	          org.cyprusbrs.apps.ProcessCtl woSchedule = new org.cyprusbrs.apps.ProcessCtl(null, pi, null);
//	          woSchedule.start();
	          if (pi.isError())
	            this.m_processMsg = pi.getSummary(); 
	        } 
	      } 
	    } 
	    if (!DOCACTION_Complete.equals(getDocAction()))
	      setDocAction(DOCACTION_Complete); 
	    return "IP";
	  }
	  
	  public boolean processIt (String processAction)
		{
			m_processMsg = null;
			DocumentEngine engine = new DocumentEngine (this, getDocStatus());
			return engine.processIt (processAction, getDocAction());
		}	//	process
	  
	  public boolean reActivateIt() {
	    log.info(toString());
	    
//	    this.m_processMsg = DocumentEngine.isPeriodOpen(this);
//	    if (this.m_processMsg != null)
//	      return false;
	    /// Below Cyprus technique is used check Period is open
	    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
	    if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
	    {
	    	m_processMsg = "@PeriodClosed@";
	    	return false;
	    }
	    setDocAction(DOCACTION_Complete);
	    return true;
	  }
	  
	  public boolean rejectIt() {
	    log.info("rejectIt - " + toString());
	    setIsApproved(false);
	    return true;
	  }
	  
	  public boolean reverseAccrualIt() {
	    log.info(toString());
	    return false;
	  }
	  
	  public boolean reverseCorrectIt() {
	    log.info(toString());
	    return false;
	  }
	  
	  public String toString() {
	    StringBuffer sb = (new StringBuffer("MMFGWorkOrder[")).append(get_ID()).append("-").append(getDocumentNo()).append(",C_DocType_ID=").append(getC_DocType_ID()).append(", M_Product_ID=").append(getM_Product_ID()).append(", QtyEntered=").append(getQtyEntered()).append("]");
	    return sb.toString();
	  }
	  
	  public boolean unlockIt() {
	    log.info(toString());
	    setProcessing(false);
	    return true;
	  }
	  
	  public boolean voidIt() {
	    log.info(toString());
	    if (DOCSTATUS_Closed.equals(getDocStatus()) || DOCSTATUS_Voided.equals(getDocStatus())) {
	      this.m_processMsg = "Document Closed: " + getDocStatus();
	      setDocAction(DOCACTION_None);
	      return false;
	    } 
	    if (!reverseTasks(true)) {
	      this.m_processMsg = Msg.getMsg(getCtx(), "CannotReverseTasks");
	      return false;
	    } 
	    if (DOCSTATUS_Drafted.equals(getDocStatus()) || DOCSTATUS_Invalid.equals(getDocStatus()) || DOCSTATUS_Approved.equals(getDocStatus()) || DOCSTATUS_NotApproved.equals(getDocStatus())) {
	      addDescription(Msg.getMsg(getCtx(), "Voided") + " (" + getQtyEntered() + ")");
	      setQtyEntered(Env.ZERO);
	      setQtyAvailable(Env.ZERO);
	      setQtyAssembled(Env.ZERO);
	      MMFGWorkOrderTransaction[] WOTxns = MMFGWorkOrderTransaction.getOfWorkOrder(this, " ParentWorkOrderTxn_ID IS NULL ", " M_WorkOrderTransaction_ID DESC ");
	      if (WOTxns != null) {
	        log.fine("WOTxns #" + WOTxns.length);
	        for (int i = 0; i < WOTxns.length; i++) {
	          if (!DOCSTATUS_Reversed.equals(WOTxns[i].getDocStatus()) && !DOCSTATUS_Voided.equals(WOTxns[i].getDocStatus())) {
	            WOTxns[i].set_Trx(get_Trx());
	            WOTxns[i].setDocAction("VO");
	            if (!DocumentEngine.processIt(WOTxns[i], "VO")) {
	              this.m_processMsg = "WOTxn Void erorr: " + WOTxns[i].getProcessMsg();
	              return false;
	            } 
	            if (!WOTxns[i].save(get_TrxName())) {
	              this.m_processMsg = "Could not save work order transaction void";
	              return false;
	            } 
	          } 
	        } 
	      } 
	    } else {
	      Timestamp dateAcct = getDateAcct();
	      
	      /// 
//	      this.m_processMsg = DocumentEngine.isPeriodOpen(this);
//	      if (this.m_processMsg != null) {
//	        log.log(Level.SEVERE, this.m_processMsg);
//	        return false;
//	      }
	      /// Below Cyprus technique is used check Period is open
	      MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
	      if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
	      {
			m_processMsg = "@PeriodClosed@";
			return false;
	      }
	      
	      MMFGWorkOrder reversal = new MMFGWorkOrder(getCtx(), 0, get_TrxName());
	      copyValues((PO)this, (PO)reversal);
	      reversal.setClientOrg((PO)this);
	      reversal.setC_Order_ID(0);
	      reversal.setDateAcct(dateAcct);
	      reversal.setDocumentNo(getDocumentNo() + "^");
	      reversal.setDocStatus(DOCSTATUS_InProgress);
	      reversal.setDocAction(DOCACTION_Complete);
	      reversal.setIsApproved(true);
	      reversal.setProcessing(false);
	      reversal.setProcessed(false);
	      reversal.setPosted(false);
	      reversal.setDescription(getDescription());
	      reversal.addDescription("(->" + getDocumentNo() + ")");
	      if (!reversal.save(get_TrxName())) {
	        this.m_processMsg = "Could not save work order reversal";
	        return false;
	      } 
	      reversal.copyLinesFrom(this);
	      if (!reversal.save(get_TrxName())) {
	        this.m_processMsg = "Could not save work order detail reversal ";
	        return false;
	      } 
	      setDocStatus(DOCSTATUS_Reversed);
	      addDescription("(" + reversal.getDocumentNo() + "<-)");
	      save(get_TrxName());
	      MMFGWorkOrderTransaction[] WOTxns = MMFGWorkOrderTransaction.getOfWorkOrder(this, " ParentWorkOrderTxn_ID IS NULL ", " M_WorkOrderTransaction_ID DESC ");
	      if (WOTxns != null) {
	        log.fine("WOTxns #" + WOTxns.length);
	        for (int i = 0; i < WOTxns.length; i++) {
	          if (!DOCSTATUS_Reversed.equals(WOTxns[i].getDocStatus()) && !DOCSTATUS_Voided.equals(WOTxns[i].getDocStatus())) {
	            WOTxns[i].set_Trx(get_Trx());
	            WOTxns[i].setDocAction("VO");
	            if (!DocumentEngine.processIt(WOTxns[i], "VO")) {
	              this.m_processMsg = "WOTxn Reversal error: " + WOTxns[i].getProcessMsg();
	              return false;
	            } 
	            if (!WOTxns[i].save(get_TrxName())) {
	              this.m_processMsg = "Could not save work order transaction reversal";
	              return false;
	            } 
	          } 
	        } 
	      } 
	      if (!DocumentEngine.processIt(reversal, "CO")) {
	        this.m_processMsg = "WO Reversal error: " + reversal.getProcessMsg();
	        return false;
	      } 
	      DocumentEngine.processIt(reversal, "CL");
	      reversal.setDocStatus(DOCSTATUS_Reversed);
	      reversal.setDocAction(DOCACTION_None);
	      reversal.save(get_TrxName());
	    } 
	    setProcessed(true);
	    setDocAction(DOCACTION_None);
	    return true;
	  }
	  
	  public void addDescription(String description) {
	    String desc = getDescription();
	    if (desc == null) {
	      setDescription(description);
	    } else {
	      setDescription(desc + " | " + description);
	    } 
	  }
	  
	  public void setM_Warehouse_ID(String oldM_Warehouse_ID, String newM_Warehouse_ID, int windowNo) throws Exception {
	    if (newM_Warehouse_ID == null || 0 == newM_Warehouse_ID.trim().length()) {
	      set_ValueNoCheck("M_Locator_ID", null);
	      return;
	    } 
	    int M_Warehouse_ID = Integer.parseInt(newM_Warehouse_ID);
	    if (0 == M_Warehouse_ID) {
	      set_ValueNoCheck("M_Locator_ID", null);
	    } else {
	      setM_Locator_ID(MWarehouse.get(getCtx(), getM_Warehouse_ID()).getDefaultM_Locator_ID());
	    } 
	  }
	  
	  public void setAD_Org_ID(String oldAD_Org_ID, String newAD_Org_ID, int windowNo) throws Exception {
	    if (newAD_Org_ID == null || newAD_Org_ID.length() == 0) {
	      set_ValueNoCheck("M_Warehouse_ID", null);
	      set_ValueNoCheck("MFG_WorkOrderClass_ID", null);
	      return;
	    } 
	    int AD_Org_ID = Integer.parseInt(newAD_Org_ID);
	    if (getM_Warehouse_ID() != 0) {
	      MWarehouse warehouse = MWarehouse.get(getCtx(), getM_Warehouse_ID());
	      if (warehouse.getAD_Org_ID() != AD_Org_ID)
	        set_ValueNoCheck("M_Warehouse_ID", null); 
	    } 
	    set_ValueNoCheck("MFG_WorkOrderClass_ID", null);
	    defaultWorkOrderClass(getAD_Client_ID(), AD_Org_ID, getWOType(), windowNo);
	  }
	  
	  public void setWOSource(String oldWOSource, String newWOSource, int windowNo) throws Exception {
	    if (newWOSource == null || newWOSource.length() == 0) {
	      setC_Order_ID(0);
	      setC_OrderLine_ID(0);
	    } 
	  }
	  
	  public void setWOType(String oldWOType, String newWOType, int windowNo) throws Exception {
	    if (newWOType == null || newWOType.length() == 0) {
	      set_ValueNoCheck("MFG_WorkOrderClass_ID", null);
	      return;
	    } 
	    set_ValueNoCheck("MFG_WorkOrderClass_ID", null);
	    defaultWorkOrderClass(getAD_Client_ID(), getAD_Org_ID(), newWOType, windowNo);
	  }
	  
	  private void defaultWorkOrderClass(int AD_Client_ID, int AD_Org_ID, String woType, int windowNo) {
	    String sql = "SELECT MFG_WorkOrderClass_ID FROM MFG_WorkOrderClass WHERE AD_Org_ID IN (0, ?) AND AD_Client_ID = ? AND WOType = ? AND IsDefault = 'Y' AND IsActive = 'Y' ORDER BY AD_Org_ID DESC";
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	      cPreparedStatement.setInt(1, AD_Org_ID);
	      cPreparedStatement.setInt(2, AD_Client_ID);
	      cPreparedStatement.setString(3, woType);
	      rs = cPreparedStatement.executeQuery();
	      if (rs.next()) {
	        setMFG_WorkOrderClass_ID(rs.getInt(1));
	        setM_WorkOrderClass_ID((String)null, String.valueOf(rs.getInt(1)), windowNo);
	      } 
	      rs.close();
	      cPreparedStatement.close();
	      cPreparedStatement = null;
	    } catch (Exception e) {
	      log.log(Level.SEVERE, sql, e);
	    } finally {
	    
	      DB.close(rs, cPreparedStatement);
	      rs = null; cPreparedStatement = null;
//	      DB.closeResultSet(rs);
//	      DB.closeStatement((Statement)cPreparedStatement);
	    } 
	  }
	  
	  public void setM_WorkOrderClass_ID(String oldM_WorkOrderClass_ID, String newM_WorkOrderClass_ID, int windowNo) throws Exception {
	    if (newM_WorkOrderClass_ID == null || newM_WorkOrderClass_ID.length() == 0) {
	      set_ValueNoCheck("C_DocType_ID", null);
	      return;
	    } 
	    int M_WorkOrderClass_ID = Integer.parseInt(newM_WorkOrderClass_ID);
	    if (M_WorkOrderClass_ID == 0) {
	      set_ValueNoCheck("C_DocType_ID", null);
	      return;
	    } 
	    MMFGWorkOrderClass woc = new MMFGWorkOrderClass(getCtx(), M_WorkOrderClass_ID, get_TrxName());
	    setC_DocType_ID(String.valueOf(getC_DocType_ID()), String.valueOf(woc.getWO_DocType_ID()), windowNo);
	  }
	  
	  public void setC_Order_ID(String oldC_Order_ID, String newC_Order_ID, int windowNo) throws Exception {
	    setC_OrderLine_ID(0);
	    set_ValueNoCheck("M_Product_ID", null);
	    set_ValueNoCheck("M_BOM_ID", null);
	    if (newC_Order_ID == null || newC_Order_ID.length() == 0)
	      return; 
	    int C_Order_ID = Integer.parseInt(newC_Order_ID);
	    if (C_Order_ID == 0)
	      return; 
	    MOrder order = new MOrder(getCtx(), C_Order_ID, null);
	    setC_BPartner_ID(order.getC_BPartner_ID());
	    setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
	    setAD_User_ID(order.getAD_User_ID());
	    setPriorityRule(order.getPriorityRule());
	    setC_Project_ID(order.getC_Project_ID());
	    setC_Campaign_ID(order.getC_Campaign_ID());
	    setC_Activity_ID(order.getC_Activity_ID());
	    setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
	    setUser1_ID(order.getUser1_ID());
	    setUser2_ID(order.getUser2_ID());
	  }
	  
	  public void setC_OrderLine_ID(String oldC_OrderLine_ID, String newC_OrderLine_ID, int windowNo) throws Exception {
	    set_ValueNoCheck("M_Product_ID", null);
	    set_ValueNoCheck("M_BOM_ID", null);
	    if (newC_OrderLine_ID == null || newC_OrderLine_ID.length() == 0)
	      return; 
	    int C_OrderLine_ID = Integer.parseInt(newC_OrderLine_ID);
	    if (C_OrderLine_ID == 0)
	      return; 
	    MOrderLine line = new MOrderLine(getCtx(), C_OrderLine_ID, null);
	    setM_Product_ID(line.getM_Product_ID());
	    setM_Product_ID((String)null, String.valueOf(line.getM_Product_ID()), windowNo);
	    setQtyEntered(line.getQtyEntered());
	  }
	  
	  public void setM_Product_ID(String oldM_Product_ID, String newM_Product_ID, int windowNo) throws Exception {
	    if (newM_Product_ID == null || newM_Product_ID.length() == 0) {
	      set_ValueNoCheck("M_BOM_ID", null);
	      set_ValueNoCheck("C_UOM_ID", null);
	      set_ValueNoCheck("MFG_Routing_ID", null);
	      return;
	    } 
	    int M_Product_ID = Integer.parseInt(newM_Product_ID);
	    if (M_Product_ID == 0) {
	      set_ValueNoCheck("M_BOM_ID", null);
	      set_ValueNoCheck("C_UOM_ID", null);
	      set_ValueNoCheck("MFG_Routing_ID", null);
	      return;
	    } 
	    String restriction = "BOMType='" + X_M_BOM.BOMTYPE_CurrentActive + "' AND BOMUse='" + X_M_BOM.BOMUSE_Manufacturing + "' AND IsActive = 'Y'";
	    MBOM[] boms = MBOM.getOfProduct(getCtx(), M_Product_ID, null, restriction);
	    if (boms.length != 0) {
	      MBOM bom = boms[0];
	      setM_BOM_ID(bom.getM_BOM_ID());
	    } else {
	      set_ValueNoCheck("M_BOM_ID", null);
	    } 
	    MMFGRouting routing = null;
	    if (getM_Warehouse_ID() != 0)
	      routing = MMFGRouting.getDefaultRouting(getCtx(), M_Product_ID, getM_Warehouse_ID()); 
	    if (routing != null) {
	      setMFG_Routing_ID(routing.getMFG_Routing_ID());
	    } else {
	      set_ValueNoCheck("MFG_Routing_ID", null);
	    } 
	    MProduct product = new MProduct(Env.getCtx(), M_Product_ID, null);
	    setC_UOM_ID(product.getC_UOM_ID());
	  }
	  
	  public void setC_DocType_ID(String oldC_DocType_ID, String newC_DocType_ID, int windowNo) throws Exception {
	    if (Util.isEmpty(newC_DocType_ID))
	      return; 
	    int C_DocType_ID = convertToInt(newC_DocType_ID);
	    if (C_DocType_ID == 0)
	      return; 
	    String oldDocNo = getDocumentNo();
	    boolean newDocNo = (oldDocNo == null);
	    if (!newDocNo && oldDocNo.startsWith("<") && oldDocNo.endsWith(">"))
	      newDocNo = true; 
	    int oldDocType_ID = getC_DocType_ID();
	    if (oldDocType_ID == 0 && !Util.isEmpty(oldC_DocType_ID))
	      oldDocType_ID = convertToInt(oldC_DocType_ID); 
	    String sql = "SELECT d.DocBaseType, d.IsDocNoControlled, s.CurrentNext, s.CurrentNextSys, s.AD_Sequence_ID FROM C_DocType d LEFT OUTER JOIN AD_Sequence s ON (d.DocNoSequence_ID=s.AD_Sequence_ID)WHERE C_DocType_ID=?";
	    CPreparedStatement cPreparedStatement1 = null;
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet resultSet = null;
	    ResultSet rs = null;
	    try {
	      int AD_Sequence_ID = 0;
	      if (!newDocNo && oldDocType_ID != 0) {
	         cPreparedStatement1 = DB.prepareStatement(sql, get_TrxName());
	        cPreparedStatement1.setInt(1, oldDocType_ID);
	         resultSet = cPreparedStatement1.executeQuery();
	        if (resultSet.next())
	          AD_Sequence_ID = resultSet.getInt(5); 
	        resultSet.close();
	        cPreparedStatement1.close();
	      } 
	       cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	      cPreparedStatement.setInt(1, C_DocType_ID);
	       rs = cPreparedStatement.executeQuery();
	      if (rs.next()) {
	        setC_DocType_ID(C_DocType_ID);
	        this.p_changeVO.setContext(getCtx(), windowNo, "C_DocTypeTarget_ID", C_DocType_ID);
	        if (rs.getString(2).equals("Y")) {
	          if (!newDocNo && AD_Sequence_ID != rs.getInt(6))
	            newDocNo = true; 
	          if (newDocNo)
	            if (Ini.isPropertyBool("CompiereSys") && Env.getAD_Client_ID(getCtx()) < 1000000) {
	              setDocumentNo("<" + rs.getString(4) + ">");
	            } else if (rs.getString(3) != null) {
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
	    	DB.close(rs, cPreparedStatement);
	    	DB.close(resultSet, cPreparedStatement1);
	    	rs = null; cPreparedStatement = null;
	    	resultSet = null; cPreparedStatement1 = null;
	    }
	  }
	  
	  public void setC_BPartner_ID(String oldC_BPartner_ID, String newC_BPartner_ID, int windowNo) throws Exception {
	    if (newC_BPartner_ID == null || newC_BPartner_ID.length() == 0) {
	      set_ValueNoCheck("C_BPartner_Location_ID", null);
	      set_ValueNoCheck("AD_User_ID", null);
	      setC_Project_ID(0);
	      return;
	    } 
	    int BPartner_ID = Integer.parseInt(newC_BPartner_ID);
	    if (BPartner_ID == 0) {
	      set_ValueNoCheck("C_BPartner_Location_ID", null);
	      set_ValueNoCheck("AD_User_ID", null);
	      setC_Project_ID(0);
	      return;
	    } 
	    int Location_ID = 0;
	    int User_ID = 0;
	    String sql = "SELECT c.AD_User_ID, loc.C_BPartner_Location_ID AS Location_ID FROM C_BPartner p LEFT OUTER JOIN C_BPartner_Location loc ON (p.C_BPartner_ID=loc.C_BPartner_ID AND loc.IsActive='Y') LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) WHERE p.C_BPartner_ID=? AND p.IsActive='Y' ORDER BY loc.Name ASC ";
	    CPreparedStatement cPreparedStatement = null;
	    ResultSet rs = null;
	    try {
	    	cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
	      cPreparedStatement.setInt(1, BPartner_ID);
	      rs = cPreparedStatement.executeQuery();
	      if (rs.next()) {
	        Location_ID = rs.getInt("Location_ID");
	        User_ID = rs.getInt("AD_User_ID");
	      } 
	      rs.close();
	      cPreparedStatement.close();
	    } catch (SQLException e) {
	      log.log(Level.SEVERE, "bPartnerBill", e);
	    } finally {
	    	
	      DB.close(rs, cPreparedStatement);
	      rs = null; cPreparedStatement = null;
//	      DB.closeResultSet(rs);
//	      DB.closeStatement((Statement)cPreparedStatement);
	    } 
	    Integer orderID = Integer.valueOf(getC_Order_ID());
	    if (orderID != null && orderID.intValue() != 0) {
	      MOrder order = new MOrder(Env.getCtx(), orderID.intValue(), null);
	      int bpartnerID = order.getC_BPartner_ID();
	      if (BPartner_ID == bpartnerID) {
	        Location_ID = order.getC_BPartner_Location_ID();
	        User_ID = order.getAD_User_ID();
	      } 
	    }
	    
	    
	    		
	    		
	    if (Env.getContextAsInt(getCtx(),1113,1113,"C_BPartner_ID") == BPartner_ID) {
		      String loc = Env.getContext(getCtx(),1113,1113,"C_BPartner_Location_ID");
//	    if (getCtx().getContextAsInt(1113, 1113, "C_BPartner_ID") == BPartner_ID) {
//	      String loc = getCtx().getContext(1113, 1113, "C_BPartner_Location_ID");
	      if (loc.length() > 0)
	        Location_ID = Integer.parseInt(loc); 
	      String cont = Env.getContext(getCtx(),1113,1113, "AD_User_ID");
	      if (cont.length() > 0)
	        User_ID = Integer.parseInt(cont); 
	    } 
	    setC_BPartner_Location_ID(Location_ID);
	    setAD_User_ID(User_ID);
	    setC_Project_ID(0);
	  }
	  
	  public void setSupervisor_ID(String oldSupervisor_ID, String newSupervisor_ID, int windowNo) throws Exception {
	    if (newSupervisor_ID == null || newSupervisor_ID.trim().length() == 0)
	      return; 
	    if (0 == Integer.parseInt(newSupervisor_ID))
	      return; 
	    if (0 == getSalesRep_ID())
	      setSalesRep_ID(Integer.parseInt(newSupervisor_ID)); 
	  }
	  
	  public void setIsPrepared(boolean prepare) {
	    this.m_callPrepared = prepare;
	  }
	  
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
	  
	  public void setProcessMsg(String processMsg) {
	    this.m_processMsg = processMsg;
	  }
	  
	  public String getDocBaseType() {
	    return "WOO";
	  }
	  
	  public Timestamp getDocumentDate() {
	    return getDateAcct();
	  }
	  
	  public Env.QueryParams getLineOrgsQueryInfo() {
	    return new Env.QueryParams("SELECT DISTINCT AD_Org_ID FROM M_WorkOrderOperation WHERE M_WorkOrder_ID = ?", new Object[] { Integer.valueOf(getMFG_WorkOrder_ID()) });
	  }
	  
	  private boolean reverseTasks(boolean onlyIncomplete) { 
	    Class<?>[] parameterTypes = new Class[] { X_MFG_WorkOrder.class, boolean.class };
	    Object[] args = { this, Boolean.valueOf(onlyIncomplete) };
	    try {
	      Class<?> c = Class.forName("org.cyprus.wms.util.MWarehouseTaskUtil");
	      if (c == null)
	        return false; 
	      Method m = c.getMethod("reverseWorkOrderTasks", parameterTypes);
	      return ((Boolean)m.invoke(null, args)).booleanValue();
	    } catch (Exception e) {
	      log.warning("Error reversing Warehouse Tasks:" + e.toString());
	      return false;
	    } 
	  }

}
