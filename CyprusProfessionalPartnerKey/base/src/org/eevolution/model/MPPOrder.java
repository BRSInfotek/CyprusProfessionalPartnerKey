package org.eevolution.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.exceptions.DocTypeNotFoundException;
import org.cyprus.model.engines.CostDimension;
import org.cyprusbrs.framework.MCost;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProject;
import org.cyprusbrs.framework.MResource;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.ModelValidationEngine;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POResultSet;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.wf.MWFNode;
import org.cyprusbrs.wf.MWFNodeNext;
import org.cyprusbrs.wf.MWorkflow;
import org.eevolution.exceptions.BOMExpiredException;
import org.eevolution.exceptions.RoutingExpiredException;

public class MPPOrder extends X_PP_Order implements DocAction {
  private static final long serialVersionUID = 1L;
  
  private MPPOrderBOMLine[] m_lines;
  
  private String m_processMsg;
  
  private boolean m_justPrepared;
  
  private MPPOrderWorkflow m_PP_Order_Workflow;
  
  public static MPPOrder forC_OrderLine_ID(Properties ctx, int C_OrderLine_ID, String trxName) {
    MOrderLine line = new MOrderLine(ctx, C_OrderLine_ID, trxName);
    return (MPPOrder)(new Query(ctx, "PP_Order", "C_OrderLine_ID=? AND M_Product_ID=?", trxName))
      .setParameters(new Object[] { Integer.valueOf(C_OrderLine_ID), Integer.valueOf(line.getM_Product_ID()) }).firstOnly();
  }
  
  public static void updateQtyBatchs(Properties ctx, I_PP_Order order, boolean override) {
    BigDecimal QtyBatchs, qtyBatchSize = order.getQtyBatchSize();
    if (qtyBatchSize.signum() == 0 || override) {
      int AD_Workflow_ID = order.getAD_Workflow_ID();
      if (AD_Workflow_ID <= 0)
        return; 
      MWorkflow wf = MWorkflow.get(ctx, AD_Workflow_ID);
      qtyBatchSize = wf.getQtyBatchSize().setScale(0, RoundingMode.UP);
      order.setQtyBatchSize(qtyBatchSize);
    } 
    if (qtyBatchSize.signum() == 0) {
      QtyBatchs = Env.ONE;
    } else {
      QtyBatchs = order.getQtyOrdered().divide(qtyBatchSize, 0, 0);
    } 
    order.setQtyBatchs(QtyBatchs);
  }
  
  public static boolean isQtyAvailable(MPPOrder order, ArrayList[][] issue, Timestamp minGuaranteeDate) {
    boolean isCompleteQtyDeliver = false;
    for (int i = 0; i < issue.length; i++) {
      KeyNamePair key = (KeyNamePair) issue[i][0].get(0);
      boolean isSelected = key.getName().equals("Y");
      if (key != null && isSelected) {
        String value = (String) issue[i][0].get(2);
        KeyNamePair productkey = (KeyNamePair) issue[i][0].get(3);
        int M_Product_ID = productkey.getKey();
        BigDecimal qtyToDeliver = (BigDecimal) issue[i][0].get(4);
        BigDecimal qtyScrapComponent = (BigDecimal) issue[i][0].get(5);
        MProduct product = MProduct.get(order.getCtx(), M_Product_ID);
        if (product != null && product.isStocked()) {
          int M_AttributeSetInstance_ID = 0;
          if (value == null && isSelected) {
            M_AttributeSetInstance_ID = Integer.valueOf(key.getKey()).intValue();
          } else if (value != null && isSelected) {
            int PP_Order_BOMLine_ID = Integer.valueOf(key.getKey()).intValue();
            if (PP_Order_BOMLine_ID > 0) {
              MPPOrderBOMLine orderBOMLine = new MPPOrderBOMLine(order.getCtx(), PP_Order_BOMLine_ID, order.get_TrxName());
              M_AttributeSetInstance_ID = orderBOMLine.getM_AttributeSetInstance_ID();
            } 
          } 
          MStorage[] storages = getStorages(order.getCtx(), 
              M_Product_ID, 
              order.getM_Warehouse_ID(), 
              M_AttributeSetInstance_ID, 
              minGuaranteeDate, order.get_TrxName());
          if (M_AttributeSetInstance_ID == 0) {
            BigDecimal toIssue = qtyToDeliver.add(qtyScrapComponent);
            byte b1;
            int k;
            MStorage[] arrayOfMStorage;
            for (k = (arrayOfMStorage = storages).length, b1 = 0; b1 < k; ) {
              MStorage storage = arrayOfMStorage[b1];
              if (storage.getQtyOnHand().signum() != 0) {
                BigDecimal issueActual = toIssue.min(storage.getQtyOnHand());
                toIssue = toIssue.subtract(issueActual);
                if (toIssue.signum() <= 0)
                  break; 
              } 
              b1++;
            } 
          } else {
            BigDecimal qtydelivered = qtyToDeliver;
            qtydelivered.setScale(4, 4);
            qtydelivered = Env.ZERO;
          } 
          BigDecimal onHand = Env.ZERO;
          byte b;
          int j;
          MStorage[] arrayOfMStorage1;
          for (j = (arrayOfMStorage1 = storages).length, b = 0; b < j; ) {
            MStorage storage = arrayOfMStorage1[b];
            onHand = onHand.add(storage.getQtyOnHand());
            b++;
          } 
          isCompleteQtyDeliver = (onHand.compareTo(qtyToDeliver.add(qtyScrapComponent)) >= 0);
          if (!isCompleteQtyDeliver)
            break; 
        } 
      } 
    } 
    return isCompleteQtyDeliver;
  }
  
  public static MStorage[] getStorages(Properties ctx, int M_Product_ID, int M_Warehouse_ID, int M_ASI_ID, Timestamp minGuaranteeDate, String trxName) {
    MProduct product = MProduct.get(ctx, M_Product_ID);
    if (product != null && product.isStocked()) {
      if (product.getM_AttributeSetInstance_ID() == 0) {
        String str = product.getMMPolicy();
        return MStorage.getWarehouse(ctx, 
            M_Warehouse_ID, 
            M_Product_ID, 
            M_ASI_ID, 
            minGuaranteeDate, 
            "F".equals(str), 
            true, 
            0, 
            trxName);
      } 
      String MMPolicy = product.getMMPolicy();
      return MStorage.getWarehouse(ctx, 
          M_Warehouse_ID, 
          M_Product_ID, 
          0, 
          minGuaranteeDate, 
          "F".equals(MMPolicy), 
          true, 
          0, 
          trxName);
    } 
    return new MStorage[0];
  }
  
  public MPPOrder(Properties ctx, int PP_Order_ID, String trxName) {
    super(ctx, PP_Order_ID, trxName);
    this.m_lines = null;
    this.m_processMsg = null;
    this.m_justPrepared = false;
    this.m_PP_Order_Workflow = null;
    if (PP_Order_ID == 0)
      setDefault(); 
  }
  
  public MPPOrder(MProject project, int PP_Product_BOM_ID, int AD_Workflow_ID) {
    this(project.getCtx(), 0, project.get_TrxName());
    setAD_Client_ID(project.getAD_Client_ID());
    setAD_Org_ID(project.getAD_Org_ID());
    setC_Campaign_ID(project.getC_Campaign_ID());
    setC_Project_ID(project.getC_Project_ID());
    setDescription(project.getName());
    setLine(10);
    setPriorityRule("5");
    if (project.getDateContract() == null)
      throw new IllegalStateException("Date Contract is mandatory for Manufacturing Order."); 
    if (project.getDateFinish() == null)
      throw new IllegalStateException("Date Finish is mandatory for Manufacturing Order."); 
    Timestamp ts = project.getDateContract();
    Timestamp df = project.getDateContract();
    if (ts != null)
      setDateOrdered(ts); 
    if (ts != null)
      setDateStartSchedule(ts); 
    ts = project.getDateFinish();
    if (df != null)
      setDatePromised(df); 
    setM_Warehouse_ID(project.getM_Warehouse_ID());
    setPP_Product_BOM_ID(PP_Product_BOM_ID);
    setAD_Workflow_ID(AD_Workflow_ID);
    setQtyEntered(Env.ONE);
    setQtyOrdered(Env.ONE);
    MPPProductBOM bom = new MPPProductBOM(project.getCtx(), PP_Product_BOM_ID, project.get_TrxName());
    MProduct product = MProduct.get(project.getCtx(), bom.getM_Product_ID());
    setC_UOM_ID(product.getC_UOM_ID());
    setM_Product_ID(bom.getM_Product_ID());
    String where = "IsManufacturingResource = 'Y' AND ManufacturingResourceType = 'PT' AND M_Warehouse_ID = " + project.getM_Warehouse_ID();
    MResource resoruce = (MResource)MTable.get(project.getCtx(), MResource.Table_ID).getPO(where, project.get_TrxName());
    if (resoruce == null)
      throw new IllegalStateException("Resource is mandatory."); 
    setS_Resource_ID(resoruce.getS_Resource_ID());
  }
  
  public MPPOrder(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_lines = null;
    this.m_processMsg = null;
    this.m_justPrepared = false;
    this.m_PP_Order_Workflow = null;
  }
  
  public BigDecimal getQtyOpen() {
    return getQtyOrdered().subtract(getQtyDelivered()).subtract(getQtyScrap());
  }
  
  public MPPOrderBOMLine[] getLines(boolean requery) {
    if (this.m_lines != null && !requery) {
      set_TrxName((PO[])this.m_lines, get_TrxName());
      return this.m_lines;
    } 
    String whereClause = "PP_Order_ID=?";
    List<MPPOrderBOMLine> list = (new Query(getCtx(), "PP_Order_BOMLine", whereClause, get_TrxName())).setParameters(new Object[] { Integer.valueOf(getPP_Order_ID()) }).setOrderBy("Line").list();
    this.m_lines = list.<MPPOrderBOMLine>toArray(new MPPOrderBOMLine[list.size()]);
    return this.m_lines;
  }
  
  public MPPOrderBOMLine[] getLines() {
    return getLines(true);
  }
  
  public void setC_DocTypeTarget_ID(String docBaseType) {
    if (getC_DocTypeTarget_ID() > 0)
      return; 
    MDocType[] doc = MDocType.getOfDocBaseType(getCtx(), docBaseType);
    if (doc == null)
      throw new DocTypeNotFoundException(docBaseType, ""); 
    setC_DocTypeTarget_ID(doc[0].get_ID());
  }
  
  public void setProcessed(boolean processed) {
    super.setProcessed(processed);
    if (get_ID() <= 0)
      return; 
    String sql = "UPDATE PP_Order SET Processed='Y' WHERE PP_Order_ID="+get_ID();    
    int retV= DB.executeUpdate(sql, get_TrxName());
    
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getAD_Client_ID() == 0) {
      this.m_processMsg = "AD_Client_ID = 0";
      return false;
    } 
    if (getAD_Org_ID() == 0) {
      int context_AD_Org_ID = Env.getAD_Org_ID(getCtx());
      if (context_AD_Org_ID == 0) {
        this.m_processMsg = "AD_Org_ID = 0";
        return false;
      } 
      setAD_Org_ID(context_AD_Org_ID);
      this.log.warning("beforeSave - Changed Org to Context=" + context_AD_Org_ID);
    } 
    if (getM_Warehouse_ID() == 0) {
      int ii = Env.getContextAsInt(getCtx(), "#M_Warehouse_ID");
      if (ii != 0)
        setM_Warehouse_ID(ii); 
    } 
    if (getC_UOM_ID() <= 0 && getM_Product_ID() > 0)
      setC_UOM_ID(getM_Product().getC_UOM_ID()); 
    if (getDateFinishSchedule() == null)
      setDateFinishSchedule(getDatePromised()); 
    if (is_ValueChanged("QtyDelivered") || is_ValueChanged("QtyOrdered"))
      orderStock(); 
    updateQtyBatchs(getCtx(), (I_PP_Order)this, false);
    return true;
  }
  
  protected boolean afterSave(boolean newRecord, boolean success) {
    if (!success)
      return false; 
    if ("CL".equals(getDocAction()) || "VO".equals(getDocAction()))
      return true; 
    if (is_ValueChanged("QtyEntered") && !isDelivered()) {
      deleteWorkflowAndBOM();
      explotion();
    } 
    if (is_ValueChanged("QtyEntered") && isDelivered())
      throw new CyprusException("Cannot Change Quantity, Only is allow with Draft or In Process Status"); 
    if (!newRecord)
      return success; 
    explotion();
    return true;
  }
  
  protected boolean beforeDelete() {
    if (getDocStatus().equals("DR") || getDocStatus().equals("IP")) {
      String whereClause = "PP_Order_ID=? AND AD_Client_ID=?";
      Object[] params = { Integer.valueOf(get_ID()), Integer.valueOf(getAD_Client_ID()) };
      deletePO("PP_Order_Cost", whereClause, params);
      deleteWorkflowAndBOM();
    } 
    setQtyOrdered(Env.ZERO);
    orderStock();
    return true;
  }
  
  private void deleteWorkflowAndBOM() {
    if (get_ID() <= 0)
      return; 
    String whereClause = "PP_Order_ID=? AND AD_Client_ID=?";
    Object[] params = { Integer.valueOf(get_ID()), Integer.valueOf(getAD_Client_ID()) };
    DB.executeUpdateEx("UPDATE PP_Order_Workflow SET PP_Order_Node_ID=NULL WHERE " + whereClause, params, get_TrxName());
    deletePO("PP_Order_Node_Asset", whereClause, params);
    deletePO("PP_Order_Node_Product", whereClause, params);
    deletePO("PP_Order_NodeNext", whereClause, params);
    deletePO("PP_Order_Node", whereClause, params);
    deletePO("PP_Order_Workflow", whereClause, params);
    deletePO("PP_Order_BOMLine", whereClause, params);
    deletePO("PP_Order_BOM", whereClause, params);
  }
  
  public boolean processIt(String processAction) {
    this.m_processMsg = null;
    DocumentEngine engine = new DocumentEngine(this, getDocStatus());
    return engine.processIt(processAction, getDocAction());
  }
  
  public boolean unlockIt() {
    this.log.info(toString());
    setProcessing(false);
    return true;
  }
  
  public boolean invalidateIt() {
    this.log.info(toString());
    setDocAction("PR");
    return true;
  }
  
  public String prepareIt() {
    this.log.info(toString());
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 1);
    if (this.m_processMsg != null)
      return "IN"; 
    MPPOrderBOMLine[] lines = getLines(true);
    if (lines.length == 0) {
      this.m_processMsg = "@NoLines@";
      return "IN";
    } 
    if (getC_DocType_ID() != 0)
      for (int i = 0; i < lines.length; i++) {
        if (lines[i].getM_Warehouse_ID() != getM_Warehouse_ID()) {
          this.log.warning("different Warehouse " + lines[i]);
          this.m_processMsg = "@CannotChangeDocType@";
          return "IN";
        } 
      }  
    if ("DR".equals(getDocStatus()) || "IP".equals(getDocStatus()) || "IN".equals(getDocStatus()) || getC_DocType_ID() == 0)
      setC_DocType_ID(getC_DocTypeTarget_ID()); 
    String docBaseType = MDocType.get(getCtx(), getC_DocType_ID()).getDocBaseType();
    if (!"MQO".equals(docBaseType)) {
      reserveStock(lines);
      orderStock();
    } 
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 8);
    if (this.m_processMsg != null)
      return "IN"; 
    this.m_justPrepared = true;
    return "IP";
  }
  
  private void orderStock() {
    MProduct product = getM_Product();
    if (!product.isStocked())
      return; 
    BigDecimal target = getQtyOrdered();
    BigDecimal difference = target.subtract(getQtyReserved()).subtract(getQtyDelivered());
    if (difference.signum() == 0)
      return; 
    BigDecimal ordered = difference;
    int M_Locator_ID = getM_Locator_ID(ordered);
    if ("CL".equals(getDocAction())) {
      if (!MStorage.add(getCtx(), getM_Warehouse_ID(), M_Locator_ID, getM_Product_ID(), getM_AttributeSetInstance_ID(), getM_AttributeSetInstance_ID(), Env.ZERO, Env.ZERO, ordered, get_TrxName()))
        throw new CyprusException(); 
    } else if (!MStorage.add(getCtx(), getM_Warehouse_ID(), M_Locator_ID, getM_Product_ID(), getM_AttributeSetInstance_ID(), getM_AttributeSetInstance_ID(), Env.ZERO, Env.ZERO, ordered, get_TrxName())) {
      throw new CyprusException();
    } 
    setQtyReserved(getQtyReserved().add(difference));
  }
  
  private void reserveStock(MPPOrderBOMLine[] lines) {
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = lines).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      line.reserveStock();
      line.saveEx();
      b++;
    } 
  }
  
  public boolean approveIt() {
    this.log.info("approveIt - " + toString());
    MDocType doc = MDocType.get(getCtx(), getC_DocType_ID());
    if (doc.getDocBaseType().equals("MQO")) {
      String whereClause = "PP_Product_BOM_ID=? AND AD_Workflow_ID=?";
      MQMSpecification qms = (MQMSpecification)(new Query(getCtx(), "QM_Specification", whereClause, get_TrxName())).setParameters(new Object[] { Integer.valueOf(getPP_Product_BOM_ID()), Integer.valueOf(getAD_Workflow_ID()) }).firstOnly();
      return (qms != null) ? qms.isValid(getM_AttributeSetInstance_ID()) : true;
    } 
    setIsApproved(true);
    return true;
  }
  
  public boolean rejectIt() {
    this.log.info("rejectIt - " + toString());
    setIsApproved(false);
    return true;
  }
  
  public String completeIt() {
    if ("PR".equals(getDocAction())) {
      setProcessed(false);
      return "IP";
    } 
    if (!this.m_justPrepared) {
      String status = prepareIt();
      if (!"IP".equals(status))
        return status; 
    } 
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 7);
    if (this.m_processMsg != null)
      return "IN"; 
    if (!isApproved())
      approveIt(); 
    createStandardCosts();
    autoReportActivities();
    
    // Update Required Qty in Component BOM Line.. Mukesh @20250715
    Object[] params = {getQtyEntered() , Integer.valueOf(get_ID())};
    String sql="update PP_Component_BOMLine set QtyRequiered=(QtyEntered*?) Where PP_Order_ID=?"; 
    int noOfUp= DB.executeUpdateEx(sql, params, get_TrxName());
    log.info("Qty Required updated in Component BOM Line "+noOfUp);
    // End of the code
    
    setProcessed(true);
    setDocAction("CL");
    String valid = ModelValidationEngine.get().fireDocValidate((PO)this, 9);
    if (valid != null) {
      this.m_processMsg = valid;
      return "IN";
    } 
    return "CO";
  }
  
  public boolean isAvailable() {
    String whereClause = "QtyOnHand >= QtyRequiered AND PP_Order_ID=?";
    boolean available = (new Query(getCtx(), "RV_PP_Order_Storage", whereClause, get_TrxName())).setParameters(new Object[] { Integer.valueOf(get_ID()) }).match();
    return available;
  }
  
  public boolean voidIt() {
    this.log.info(toString());
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 2);
    if (this.m_processMsg != null)
      return false; 
    if (isDelivered())
      throw new CyprusException("Cannot void this document because exist transactions"); 
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = getLines()).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      BigDecimal bigDecimal = line.getQtyRequiered();
      if (bigDecimal.signum() != 0) {
        line.addDescription(Msg.parseTranslation(getCtx(), "@Voided@ @QtyRequiered@ : (" + bigDecimal + ")"));
        line.setQtyRequiered(Env.ZERO);
        line.saveEx();
      } 
      b++;
    } 
    getMPPOrderWorkflow().voidActivities();
    BigDecimal old = getQtyOrdered();
    if (old.signum() != 0) {
      addDescription(Msg.parseTranslation(getCtx(), "@Voided@ @QtyOrdered@ : (" + old + ")"));
      setQtyOrdered(Env.ZERO);
      setQtyEntered(Env.ZERO);
      saveEx();
    } 
    orderStock();
    reserveStock(getLines());
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 10);
    if (this.m_processMsg != null)
      return false; 
    setDocAction("--");
    return true;
  }
  
  public boolean closeIt() {
    this.log.info(toString());
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 3);
    if (this.m_processMsg != null)
      return false; 
    if ("CL".equals(getDocStatus()))
      return true; 
    if (!"CO".equals(getDocStatus())) {
      String DocStatus = completeIt();
      setDocStatus(DocStatus);
      setDocAction("--");
    } 
    if (!isDelivered())
      throw new CyprusException("Cannot close this document because do not exist transactions"); 
    createVariances();
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = getLines()).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      BigDecimal bigDecimal = line.getQtyRequiered();
      if (bigDecimal.compareTo(line.getQtyDelivered()) != 0) {
        line.setQtyRequiered(line.getQtyDelivered());
        line.addDescription(Msg.parseTranslation(getCtx(), "@closed@ @QtyRequiered@ (" + bigDecimal + ")"));
        line.saveEx();
      } 
      b++;
    } 
    MPPOrderWorkflow m_order_wf = getMPPOrderWorkflow();
    m_order_wf.closeActivities(m_order_wf.getLastNode(getAD_Client_ID()), getUpdated(), false);
    BigDecimal old = getQtyOrdered();
    if (old.signum() != 0) {
      addDescription(Msg.parseTranslation(getCtx(), "@closed@ @QtyOrdered@ : (" + old + ")"));
      setQtyOrdered(getQtyDelivered());
      saveEx();
    } 
    orderStock();
    reserveStock(getLines());
    setDocStatus("CL");
    setDocAction("--");
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 11);
    if (this.m_processMsg != null)
      return false; 
    return true;
  }
  
  public boolean reverseCorrectIt() {
    this.log.info("reverseCorrectIt - " + toString());
    return voidIt();
  }
  
  public boolean reverseAccrualIt() {
    this.log.info("reverseAccrualIt - " + toString());
    return false;
  }
  
  public boolean reActivateIt() {
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 12);
    if (this.m_processMsg != null)
      return false; 
    if (isDelivered())
      throw new CyprusException("Cannot re activate this document because exist transactions"); 
    setDocAction("CO");
    setProcessed(false);
    return true;
  }
  
  public int getDoc_User_ID() {
    return getPlanner_ID();
  }
  
  public BigDecimal getApprovalAmt() {
    return Env.ZERO;
  }
  
  public int getC_Currency_ID() {
    return 0;
  }
  
  public String getProcessMsg() {
    return this.m_processMsg;
  }
  
  public String getSummary() {
    return getDocumentNo() + "/" + getDatePromised();
  }
  
  public File createPDF() {
    try {
      File temp = File.createTempFile(String.valueOf(get_TableName()) + get_ID() + "_", ".pdf");
      return createPDF(temp);
    } catch (Exception e) {
      this.log.severe("Could not create PDF - " + e.getMessage());
      return null;
    } 
  }
  
  public File createPDF(File file) {
    ReportEngine re = ReportEngine.get(getCtx(), 8, getPP_Order_ID());
    if (re == null)
      return null; 
    return re.getPDF(file);
  }
  
  public String getDocumentInfo() {
    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
    return String.valueOf(dt.getName()) + " " + getDocumentNo();
  }
  
  private void deletePO(String tableName, String whereClause, Object[] params) {
    POResultSet<PO> rs = (new Query(getCtx(), tableName, whereClause, get_TrxName())).setParameters(params).scroll();
    try {
      while (rs.hasNext())
        rs.next().deleteEx(true); 
    } finally {
      rs.close();
    } 
  }
  
  public void setQty(BigDecimal Qty) {
    super.setQtyEntered(Qty);
    super.setQtyOrdered(getQtyEntered());
  }
  
  public void setQtyEntered(BigDecimal QtyEntered) {
    if (QtyEntered != null && getC_UOM_ID() != 0) {
      int precision = MUOM.getPrecision(getCtx(), getC_UOM_ID());
      QtyEntered = QtyEntered.setScale(precision, 4);
    } 
    super.setQtyEntered(QtyEntered);
  }
  
  public void setQtyOrdered(BigDecimal QtyOrdered) {
    if (QtyOrdered != null) {
      int precision = getM_Product().getUOMPrecision();
      QtyOrdered = QtyOrdered.setScale(precision, 4);
    } 
    super.setQtyOrdered(QtyOrdered);
  }
  
  public MProduct getM_Product() {
    return MProduct.get(getCtx(), getM_Product_ID());
  }
  
  public MPPOrderBOM getMPPOrderBOM() {
    String whereClause = "PP_Order_ID=?";
    return (MPPOrderBOM)(new Query(getCtx(), "PP_Order_BOM", "PP_Order_ID=?", get_TrxName())).setParameters(new Object[] { Integer.valueOf(getPP_Order_ID()) }).firstOnly();
  }
  
  public MPPOrderWorkflow getMPPOrderWorkflow() {
    if (this.m_PP_Order_Workflow != null)
      return this.m_PP_Order_Workflow; 
    String whereClause = "PP_Order_ID=?";
    this.m_PP_Order_Workflow = (MPPOrderWorkflow)(new Query(getCtx(), "PP_Order_Workflow", "PP_Order_ID=?", get_TrxName()))
      .setParameters(new Object[] { Integer.valueOf(getPP_Order_ID()) }).firstOnly();
    return this.m_PP_Order_Workflow;
  }
  
  private void explotion() {
	  
	  // Get Product BOM Line
	  String whereClause = "PP_Product_BOM_ID=?";
	  Query query=new Query(getCtx(), "PP_Product_BOMLine", whereClause, get_TrxName());
	  List<MPPProductBOMLine> listOfBOMLine = query.setParameters(
			  new Object[] { Integer.valueOf(getPP_Product_BOM_ID())}).list();
	  
    MPPProductBOM PP_Product_BOM = MPPProductBOM.get(getCtx(), getPP_Product_BOM_ID());
    if (getM_Product_ID() != PP_Product_BOM.getM_Product_ID())
      throw new CyprusException("@NotMatch@ @PP_Product_BOM_ID@ , @M_Product_ID@"); 
    MProduct product = MProduct.get(getCtx(), PP_Product_BOM.getM_Product_ID());
    if (!product.isVerified())
      throw new CyprusException("Product BOM Configuration not verified. Please verify the product first - " + product.getValue()); 
    if (PP_Product_BOM.isValidFromTo(getDateStartSchedule())) {
      MPPOrderBOM PP_Order_BOM = new MPPOrderBOM(PP_Product_BOM, getPP_Order_ID(), get_TrxName());
      PP_Order_BOM.setAD_Org_ID(getAD_Org_ID());
      PP_Order_BOM.saveEx();
      byte b;
      int i;
      MPPProductBOMLine[] arrayOfMPPProductBOMLine;
      for (i = (arrayOfMPPProductBOMLine = PP_Product_BOM.getLines(true)).length, b = 0; b < i; ) {
        MPPProductBOMLine PP_Product_BOMline = arrayOfMPPProductBOMLine[b];
        if (PP_Product_BOMline.isValidFromTo(getDateStartSchedule())) {
          MPPOrderBOMLine PP_Order_BOMLine = new MPPOrderBOMLine(PP_Product_BOMline, 
              getPP_Order_ID(), PP_Order_BOM.get_ID(), 
              getM_Warehouse_ID(), 
              get_TrxName());
          PP_Order_BOMLine.setAD_Org_ID(getAD_Org_ID());
          PP_Order_BOMLine.setM_Warehouse_ID(getM_Warehouse_ID());
          PP_Order_BOMLine.setM_Locator_ID(getM_Locator_ID());
          PP_Order_BOMLine.setQtyOrdered(getQtyOrdered());
          PP_Order_BOMLine.saveEx();
        } else {
          this.log.fine("BOM Line skiped - " + PP_Product_BOMline);
        } 
        b++;
      } 
    } else {
      throw new BOMExpiredException(PP_Product_BOM, getDateStartSchedule());
    } 
    MWorkflow AD_Workflow = MWorkflow.get(getCtx(), getAD_Workflow_ID());
    if (!AD_Workflow.isValid())
      throw new CyprusException("Routing is not valid. Please validate it first - " + AD_Workflow.getValue()); 
    if (AD_Workflow.isValidFromTo(getDateStartSchedule())) {
      MPPOrderWorkflow PP_Order_Workflow = new MPPOrderWorkflow(AD_Workflow, get_ID(), get_TrxName());
      PP_Order_Workflow.setAD_Org_ID(getAD_Org_ID());
      PP_Order_Workflow.saveEx();
      byte b;
      int i;
      MWFNode[] arrayOfMWFNode;
      for (i = (arrayOfMWFNode = AD_Workflow.getNodes(false, getAD_Client_ID())).length, b = 0; b < i; ) {
        MWFNode AD_WF_Node = arrayOfMWFNode[b];
        if (AD_WF_Node.isValidFromTo(getDateStartSchedule())) {
          MPPOrderNode PP_Order_Node = new MPPOrderNode(AD_WF_Node, PP_Order_Workflow, 
              getQtyOrdered(), 
              get_TrxName());
          PP_Order_Node.setAD_Org_ID(getAD_Org_ID());
          PP_Order_Node.saveEx();
          
       // Add component of Order Activity @20250128 by Mukesh 
          if(PP_Order_Node!=null)
          {
        	  int M_OperationOrderNode_ID=PP_Order_Node.get_ValueAsInt("M_Operation_ID");
        	  for (MPPProductBOMLine bomLine : listOfBOMLine) {
        		  
        		  int OperationFromProductBOMLine_ID=bomLine.get_ValueAsInt("M_Operation_ID");
            	  int orderId=PP_Order_Node.getPP_Order_ID();
        		  if(M_OperationOrderNode_ID==OperationFromProductBOMLine_ID)
        		  {
        			  MPPComponentBOMLine mppComponentBOMLine=new MPPComponentBOMLine(getCtx(), 0, get_TrxName());
        			  mppComponentBOMLine.setPP_Order_ID(orderId);
        			  mppComponentBOMLine.setPP_Order_Node_ID(PP_Order_Node.getPP_Order_Node_ID());
        			  mppComponentBOMLine.setM_Product_ID(bomLine.getM_Product_ID());
        			  mppComponentBOMLine.setC_UOM_ID(bomLine.getC_UOM_ID());
        			  mppComponentBOMLine.setM_Warehouse_ID(getM_Warehouse_ID());
        			  mppComponentBOMLine.setQtyBatch(Env.ZERO);
        			  mppComponentBOMLine.setQtyBOM(Env.ZERO);
        			  mppComponentBOMLine.setQtyDelivered(Env.ZERO);
        			  mppComponentBOMLine.setQtyPost(Env.ZERO);
        			  mppComponentBOMLine.setQtyReject(Env.ZERO);
        			  mppComponentBOMLine.setQtyRequiered(Env.ZERO);
        			  mppComponentBOMLine.setQtyReserved(Env.ZERO);
        			  mppComponentBOMLine.setQtyScrap(Env.ZERO);
        			  mppComponentBOMLine.setValidFrom(new Timestamp(System.currentTimeMillis()));
        			  mppComponentBOMLine.saveEx();      			  
        		  }
        		}
          }
          // End of the code
          
          
          byte b1;
          int j;
          MWFNodeNext[] arrayOfMWFNodeNext;
          for (j = (arrayOfMWFNodeNext = AD_WF_Node.getTransitions(getAD_Client_ID())).length, b1 = 0; b1 < j; ) {
            MWFNodeNext AD_WF_NodeNext = arrayOfMWFNodeNext[b1];
            MPPOrderNodeNext nodenext = new MPPOrderNodeNext(AD_WF_NodeNext, PP_Order_Node);
            nodenext.setAD_Org_ID(getAD_Org_ID());
            nodenext.saveEx();
            b1++;
          } 
          for (MPPWFNodeProduct wfnp : MPPWFNodeProduct.forAD_WF_Node_ID(getCtx(), AD_WF_Node.get_ID())) {
            MPPOrderNodeProduct nodeOrderProduct = new MPPOrderNodeProduct(wfnp, PP_Order_Node);
            nodeOrderProduct.setAD_Org_ID(getAD_Org_ID());
            nodeOrderProduct.saveEx();
          } 
          for (MPPWFNodeAsset wfna : MPPWFNodeAsset.forAD_WF_Node_ID(getCtx(), AD_WF_Node.get_ID())) {
            MPPOrderNodeAsset nodeorderasset = new MPPOrderNodeAsset(wfna, PP_Order_Node);
            nodeorderasset.setAD_Org_ID(getAD_Org_ID());
            nodeorderasset.saveEx();
          } 
        } 
        b++;
      } 
      PP_Order_Workflow.getNodes(true);
      MPPOrderNode[] arrayOfMPPOrderNode;
      for (i = (arrayOfMPPOrderNode = PP_Order_Workflow.getNodes(false, getAD_Client_ID())).length, b = 0; b < i; ) {
        MPPOrderNode orderNode = arrayOfMPPOrderNode[b];
        if (PP_Order_Workflow.getAD_WF_Node_ID() == orderNode.getAD_WF_Node_ID())
          PP_Order_Workflow.setPP_Order_Node_ID(orderNode.getPP_Order_Node_ID()); 
        byte b1;
        int j;
        MPPOrderNodeNext[] arrayOfMPPOrderNodeNext;
        for (j = (arrayOfMPPOrderNodeNext = orderNode.getTransitions(getAD_Client_ID())).length, b1 = 0; b1 < j; ) {
          MPPOrderNodeNext next = arrayOfMPPOrderNodeNext[b1];
          next.setPP_Order_Next_ID();
          next.saveEx();
          b1++;
        } 
        b++;
      } 
      PP_Order_Workflow.saveEx();
    } else {
      throw new RoutingExpiredException(AD_Workflow, getDateStartSchedule());
    } 
  }
  
  public static void createReceipt(MPPOrder order, Timestamp movementDate, BigDecimal qtyDelivered, BigDecimal qtyToDeliver, BigDecimal qtyScrap, BigDecimal qtyReject, int M_Locator_ID, int M_AttributeSetInstance_ID) {
    if (qtyToDeliver.signum() != 0 || qtyScrap.signum() != 0 || qtyReject.signum() != 0)
      MPPCostCollector.createCollector(
          order, 
          order.getM_Product_ID(), 
          M_Locator_ID, 
          M_AttributeSetInstance_ID, 
          order.getS_Resource_ID(), 
          0, 
          0, 
          MDocType.getDocType("MCC"), 
          "100", 
          movementDate, 
          qtyToDeliver, qtyScrap, qtyReject, 
          0, Env.ZERO); 
    order.setDateDelivered(movementDate);
    if (order.getDateStart() == null)
      order.setDateStart(movementDate); 
    BigDecimal DQ = qtyDelivered;
    BigDecimal SQ = qtyScrap;
    BigDecimal OQ = qtyToDeliver;
    if (DQ.add(SQ).compareTo(OQ) >= 0)
      order.setDateFinish(movementDate); 
    order.saveEx();
  }
  
  public static void createIssue(MPPOrder order, int PP_OrderBOMLine_ID, Timestamp movementdate, BigDecimal qty, BigDecimal qtyScrap, BigDecimal qtyReject, MStorage[] storages, boolean forceIssue) {
    if (qty.signum() == 0)
      return; 
    MPPOrderBOMLine PP_orderbomLine = new MPPOrderBOMLine(order.getCtx(), PP_OrderBOMLine_ID, order.get_TrxName());
    BigDecimal toIssue = qty.add(qtyScrap);
    byte b;
    int i;
    MStorage[] arrayOfMStorage;
    for (i = (arrayOfMStorage = storages).length, b = 0; b < i; ) {
      MStorage storage = arrayOfMStorage[b];
      if (storage.getQtyOnHand().signum() != 0) {
        BigDecimal qtyIssue = toIssue.min(storage.getQtyOnHand());
        if (qtyIssue.signum() != 0 || qtyScrap.signum() != 0 || qtyReject.signum() != 0) {
          String CostCollectorType = "110";
          if (PP_orderbomLine.getQtyBatch().signum() == 0 && 
            PP_orderbomLine.getQtyBOM().signum() == 0) {
            CostCollectorType = "130";
          } else if (PP_orderbomLine.isComponentType(new String[] { "CP" })) {
            CostCollectorType = "150";
          } 
          MPPCostCollector.createCollector(
              order, 
              PP_orderbomLine.getM_Product_ID(), 
              storage.getM_Locator_ID(), 
              storage.getM_AttributeSetInstance_ID(), 
              order.getS_Resource_ID(), 
              PP_OrderBOMLine_ID, 
              0, 
              MDocType.getDocType("MCC"), 
              CostCollectorType, 
              movementdate, 
              qtyIssue, qtyScrap, qtyReject, 
              0, Env.ZERO);
        } 
        toIssue = toIssue.subtract(qtyIssue);
        if (toIssue.signum() == 0)
          break; 
      } 
      b++;
    } 
    if (forceIssue && toIssue.signum() != 0) {
      MPPCostCollector.createCollector(
          order, 
          PP_orderbomLine.getM_Product_ID(), 
          PP_orderbomLine.getM_Locator_ID(), 
          PP_orderbomLine.getM_AttributeSetInstance_ID(), 
          order.getS_Resource_ID(), 
          PP_OrderBOMLine_ID, 
          0, 
          MDocType.getDocType("MCC"), 
          "110", 
          movementdate, 
          toIssue, Env.ZERO, Env.ZERO, 
          0, Env.ZERO);
      return;
    } 
    if (toIssue.signum() != 0)
      throw new CyprusException("Should not happen toIssue=" + toIssue); 
  }
  
  public static boolean isQtyAvailable(MPPOrder order, I_PP_Order_BOMLine line) {
    MProduct product = MProduct.get(order.getCtx(), line.getM_Product_ID());
    if (product == null || !product.isStocked())
      return true; 
    BigDecimal qtyToDeliver = line.getQtyRequiered();
    BigDecimal qtyScrap = line.getQtyScrap();
    BigDecimal qtyRequired = qtyToDeliver.add(qtyScrap);
    BigDecimal qtyAvailable = MStorage.getQtyAvailable(order.getM_Warehouse_ID(), 0, 
        line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), 
        order.get_TrxName());
    return (qtyAvailable.compareTo(qtyRequired) >= 0);
  }
  
  public int getM_Locator_ID() {
    MWarehouse wh = MWarehouse.get(getCtx(), getM_Warehouse_ID());
    return wh.getDefaultLocator().getM_Locator_ID();
  }
  
  private int getM_Locator_ID(BigDecimal qty) {
    int M_Locator_ID = 0;
    int M_ASI_ID = getM_AttributeSetInstance_ID();
    if (M_ASI_ID != 0)
      M_Locator_ID = MStorage.getM_Locator_ID(getM_Warehouse_ID(), getM_Product_ID(), M_ASI_ID, qty, get_TrxName()); 
    if (M_Locator_ID == 0)
      M_Locator_ID = getM_Locator_ID(); 
    return M_Locator_ID;
  }
  
  public boolean isDelivered() {
    if (getQtyDelivered().signum() > 0 || getQtyScrap().signum() > 0 || getQtyReject().signum() > 0)
      return true; 
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = getLines()).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      if (line.getQtyDelivered().signum() > 0)
        return true; 
      b++;
    } 
    MPPOrderNode[] arrayOfMPPOrderNode;
    for (i = (arrayOfMPPOrderNode = getMPPOrderWorkflow().getNodes(true, getAD_Client_ID())).length, b = 0; b < i; ) {
      MPPOrderNode node = arrayOfMPPOrderNode[b];
      if (node.getQtyDelivered().signum() > 0)
        return true; 
      if (node.getDurationReal() > 0)
        return true; 
      b++;
    } 
    return false;
  }
  
  public void setDefault() {
    setLine(10);
    setPriorityRule("5");
    setDescription("");
    setQtyDelivered(Env.ZERO);
    setQtyReject(Env.ZERO);
    setQtyScrap(Env.ZERO);
    setIsSelected(false);
    setIsSOTrx(false);
    setIsApproved(false);
    setIsPrinted(false);
    setProcessed(false);
    setProcessing(false);
    setPosted(false);
    setC_DocTypeTarget_ID("MOP");
    setC_DocType_ID(getC_DocTypeTarget_ID());
    setDocStatus("DR");
    setDocAction("PR");
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(String.valueOf(desc) + " | " + description);
    } 
  }
  
  public String toString() {
    StringBuffer sb = (new StringBuffer("MPPOrder[")).append(get_ID())
      .append("-").append(getDocumentNo())
      .append(",IsSOTrx=").append(isSOTrx())
      .append(",C_DocType_ID=").append(getC_DocType_ID())
      .append("]");
    return sb.toString();
  }
  
  public void autoReportActivities() {
    for (MPPOrderNode activity : getMPPOrderWorkflow().getNodes()) {
      if (activity.isMilestone())
        if (activity.isSubcontracting() || activity.get_ID() == getMPPOrderWorkflow().getPP_Order_Node_ID())
          MPPCostCollector.createCollector(
              this, 
              getM_Product_ID(), 
              getM_Locator_ID(), 
              getM_AttributeSetInstance_ID(), 
              getS_Resource_ID(), 
              0, 
              activity.getPP_Order_Node_ID(), 
              MDocType.getDocType("MCC"), 
              "160", 
              getUpdated(), 
              activity.getQtyToDeliver(), 
              Env.ZERO, 
              Env.ZERO, 
              0, 
              Env.ZERO);  
    } 
  }
  
  private final void createStandardCosts() {
    MAcctSchema as = MClient.get(getCtx(), getAD_Client_ID()).getAcctSchema();
    this.log.info("Cost_Group_ID" + as.getM_CostType_ID());
    TreeSet<Integer> productsAdded = new TreeSet<Integer>();
    MProduct product = getM_Product();
    productsAdded.add(Integer.valueOf(product.getM_Product_ID()));
    CostDimension d = new CostDimension(product, as, as.getM_CostType_ID(), 
        getAD_Org_ID(), getM_AttributeSetInstance_ID(), 
        -10);
    Collection<MCost> costs = d.toQuery(MCost.class, get_TrxName()).list();
    for (MCost cost : costs) {
      MPPOrderCost PP_Order_Cost = new MPPOrderCost(cost, get_ID(), get_TrxName());
      PP_Order_Cost.saveEx();
    } 
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = getLines()).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      MProduct mProduct = line.getM_Product();
      if (!productsAdded.contains(Integer.valueOf(mProduct.getM_Product_ID()))) {
        productsAdded.add(Integer.valueOf(mProduct.getM_Product_ID()));
        CostDimension costDimension = new CostDimension(line.getM_Product(), as, as.getM_CostType_ID(), 
            line.getAD_Org_ID(), line.getM_AttributeSetInstance_ID(), 
            -10);
        Collection<MCost> collection = costDimension.toQuery(MCost.class, get_TrxName()).list();
        for (MCost cost : collection) {
          MPPOrderCost PP_Order_Cost = new MPPOrderCost(cost, get_ID(), get_TrxName());
          PP_Order_Cost.saveEx();
        } 
      } 
      b++;
    } 
    for (MPPOrderNode node : getMPPOrderWorkflow().getNodes(true)) {
      int S_Resource_ID = node.getS_Resource_ID();
      if (S_Resource_ID <= 0)
        continue; 
      MProduct resourceProduct = MProduct.forS_Resource_ID(getCtx(), S_Resource_ID, null);
      if (productsAdded.contains(Integer.valueOf(resourceProduct.getM_Product_ID())))
        continue; 
      productsAdded.add(Integer.valueOf(resourceProduct.getM_Product_ID()));
      CostDimension costDimension = new CostDimension(resourceProduct, as, as.getM_CostType_ID(), 
          node.getAD_Org_ID(), 
          0, 
          -10);
      Collection<MCost> collection = costDimension.toQuery(MCost.class, get_TrxName()).list();
      for (MCost cost : collection) {
        MPPOrderCost orderCost = new MPPOrderCost(cost, getPP_Order_ID(), get_TrxName());
        orderCost.saveEx();
      } 
    } 
  }
  
  public void createVariances() {
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = getLines(true)).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      createUsageVariance((I_PP_Order_BOMLine)line);
      b++;
    } 
    this.m_lines = null;
    MPPOrderWorkflow orderWorkflow = getMPPOrderWorkflow();
    if (orderWorkflow != null)
      for (MPPOrderNode node : orderWorkflow.getNodes(true))
        createUsageVariance((I_PP_Order_Node)node);  
  }
  
  private void createUsageVariance(I_PP_Order_BOMLine bomLine) {
    MPPOrder order = this;
    Timestamp movementDate = order.getUpdated();
    MPPOrderBOMLine line = (MPPOrderBOMLine)bomLine;
    if (line.getQtyBatch().signum() == 0 && line.getQtyBOM().signum() == 0)
      return; 
    BigDecimal qtyUsageVariancePrev = line.getQtyVariance();
    BigDecimal qtyOpen = line.getQtyOpen();
    BigDecimal qtyUsageVariance = qtyOpen.subtract(qtyUsageVariancePrev);
    if (qtyUsageVariance.signum() == 0)
      return; 
    int M_Locator_ID = line.getM_Locator_ID();
    if (M_Locator_ID <= 0) {
      MLocator locator = MLocator.getDefault(MWarehouse.get(order.getCtx(), order.getM_Warehouse_ID()));
      if (locator != null)
        M_Locator_ID = locator.getM_Locator_ID(); 
    } 
    MPPCostCollector.createCollector(
        order, 
        line.getM_Product_ID(), 
        M_Locator_ID, 
        line.getM_AttributeSetInstance_ID(), 
        order.getS_Resource_ID(), 
        line.getPP_Order_BOMLine_ID(), 
        0, 
        MDocType.getDocType("MCC"), 
        "120", 
        movementDate, 
        qtyUsageVariance, 
        Env.ZERO, 
        Env.ZERO, 
        0, 
        Env.ZERO);
  }
  
  private void createUsageVariance(I_PP_Order_Node orderNode) {
    MPPOrder order = this;
    Timestamp movementDate = order.getUpdated();
    MPPOrderNode node = (MPPOrderNode)orderNode;
    BigDecimal setupTimeReal = BigDecimal.valueOf(node.getSetupTimeReal());
    BigDecimal durationReal = BigDecimal.valueOf(node.getDurationReal());
    if (setupTimeReal.signum() == 0 && durationReal.signum() == 0)
      return; 
    BigDecimal setupTimeVariancePrev = node.getSetupTimeUsageVariance();
    BigDecimal durationVariancePrev = node.getDurationUsageVariance();
    BigDecimal setupTimeRequired = BigDecimal.valueOf(node.getSetupTimeRequiered());
    BigDecimal durationRequired = BigDecimal.valueOf(node.getDurationRequiered());
    BigDecimal qtyOpen = node.getQtyToDeliver();
    BigDecimal setupTimeVariance = setupTimeRequired.subtract(setupTimeReal).subtract(setupTimeVariancePrev);
    BigDecimal durationVariance = durationRequired.subtract(durationReal).subtract(durationVariancePrev);
    if (qtyOpen.signum() == 0 && setupTimeVariance.signum() == 0 && durationVariance.signum() == 0)
      return; 
    MPPCostCollector.createCollector(
        order, 
        order.getM_Product_ID(), 
        order.getM_Locator_ID(), 
        order.getM_AttributeSetInstance_ID(), 
        node.getS_Resource_ID(), 
        0, 
        node.getPP_Order_Node_ID(), 
        MDocType.getDocType("MCC"), 
        "120", 
        movementDate, 
        qtyOpen, 
        Env.ZERO, 
        Env.ZERO, 
        setupTimeVariance.intValueExact(), 
        durationVariance);
  }
  
  public BigDecimal getQtyToDeliver() {
    return getQtyOrdered().subtract(getQtyDelivered());
  }
  
  public void updateMakeToKit(BigDecimal qtyShipment) {
    MPPOrderBOM obom = getMPPOrderBOM();
    getLines(true);
    if ("K".equals(obom.getBOMType()) && "M".equals(obom.getBOMUse())) {
      Timestamp today = new Timestamp(System.currentTimeMillis());
      ArrayList[][] issue = new ArrayList[this.m_lines.length][1];
      for (int i = 0; i < (getLines()).length; i++) {
        MPPOrderBOMLine line = this.m_lines[i];
        KeyNamePair id = null;
        if ("1".equals(line.getIssueMethod())) {
          id = new KeyNamePair(line.get_ID(), "Y");
        } else {
          id = new KeyNamePair(line.get_ID(), "N");
        } 
        ArrayList<Object> data = new ArrayList();
        BigDecimal qtyToDeliver = qtyShipment.multiply(line.getQtyMultiplier());
        data.add(id);
        data.add(Boolean.valueOf(line.isCritical()));
        MProduct product = line.getM_Product();
        data.add(product.getValue());
        KeyNamePair productKey = new KeyNamePair(product.get_ID(), product.getName());
        data.add(productKey);
        data.add(qtyToDeliver);
        data.add(Env.ZERO);
        issue[i][0] = data;
      } 
      boolean forceIssue = false;
      MOrderLine oline = (MOrderLine)getC_OrderLine();// oline = oline;
      if ("L".equals(oline.getParent().getDeliveryRule()) || 
        "O".equals(oline.getParent().getDeliveryRule())) {
        boolean isCompleteQtyDeliver = isQtyAvailable(this, issue, today);
        if (!isCompleteQtyDeliver)
          throw new CyprusException("@NoQtyAvailable@"); 
      } else {
        if ("A".equals(oline.getParent().getDeliveryRule()) || 
          "R".equals(oline.getParent().getDeliveryRule()) || 
          "M".equals(oline.getParent().getDeliveryRule()))
          throw new CyprusException("@ActionNotSupported@"); 
        if ("F".equals(oline.getParent().getDeliveryRule()))
          forceIssue = true; 
      } 
      for (int j = 0; j < issue.length; j++) {
        int M_AttributeSetInstance_ID = 0;
        KeyNamePair key = (KeyNamePair) issue[j][0].get(0);
        Boolean isCritical = (Boolean) issue[j][0].get(1);
        String value = (String) issue[j][0].get(2);
        KeyNamePair productkey = (KeyNamePair) issue[j][0].get(3);
        int M_Product_ID = productkey.getKey();
        MProduct product = MProduct.get(getCtx(), M_Product_ID);
        BigDecimal qtyToDeliver = (BigDecimal) issue[j][0].get(4);
        BigDecimal qtyScrapComponent = (BigDecimal) issue[j][0].get(5);
        int PP_Order_BOMLine_ID = Integer.valueOf(key.getKey()).intValue();
        if (PP_Order_BOMLine_ID > 0) {
          MPPOrderBOMLine orderBOMLine = new MPPOrderBOMLine(getCtx(), PP_Order_BOMLine_ID, get_TrxName());
          M_AttributeSetInstance_ID = orderBOMLine.getM_AttributeSetInstance_ID();
        } 
        MStorage[] storages = getStorages(getCtx(), 
            M_Product_ID, 
            getM_Warehouse_ID(), 
            M_AttributeSetInstance_ID, 
            today, get_TrxName());
        createIssue(
            this, 
            key.getKey(), 
            today, qtyToDeliver, 
            qtyScrapComponent, 
            Env.ZERO, 
            storages, forceIssue);
      } 
      createReceipt(
          this, 
          today, 
          getQtyDelivered(), 
          qtyShipment, 
          getQtyScrap(), 
          getQtyReject(), 
          getM_Locator_ID(), 
          getM_AttributeSetInstance_ID());
    } 
  }
}
