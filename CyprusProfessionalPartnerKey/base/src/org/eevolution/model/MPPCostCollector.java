package org.eevolution.model;

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.exceptions.DocTypeNotFoundException;
import org.cyprus.exceptions.NoVendorForProductException;
import org.cyprus.model.engines.IDocumentLine;
import org.cyprusbrs.framework.I_C_UOM;
import org.cyprusbrs.framework.MAttributeSetInstance;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MCost;
import org.cyprusbrs.framework.MCostDetail;
import org.cyprusbrs.framework.MCostElement;
import org.cyprusbrs.framework.MCostQueue;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductCategoryAcct;
import org.cyprusbrs.framework.MProductPO;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MTransaction;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.model.ModelValidationEngine;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.TimeUtil;

public class MPPCostCollector extends X_PP_Cost_Collector implements DocAction, IDocumentLine {
  private static final long serialVersionUID = 1L;
  
  private String m_processMsg;
  
  private boolean m_justPrepared;
  
  private MPPOrder m_order;
  
  private MPPOrderNode m_orderNode;
  
  private MPPOrderBOMLine m_bomLine;
  
  public static MPPCostCollector createCollector(MPPOrder order, int M_Product_ID, int M_Locator_ID, int M_AttributeSetInstance_ID, int S_Resource_ID, int PP_Order_BOMLine_ID, int PP_Order_Node_ID, int C_DocType_ID, String CostCollectorType, Timestamp movementdate, BigDecimal qty, BigDecimal scrap, BigDecimal reject, int durationSetup, BigDecimal duration) {
    MPPCostCollector cc = new MPPCostCollector(order);
    cc.setPP_Order_BOMLine_ID(PP_Order_BOMLine_ID);
    cc.setPP_Order_Node_ID(PP_Order_Node_ID);
    cc.setC_DocType_ID(C_DocType_ID);
    cc.setC_DocTypeTarget_ID(C_DocType_ID);
    cc.setCostCollectorType(CostCollectorType);
    cc.setDocAction("CO");
    cc.setDocStatus("DR");
    cc.setIsActive(true);
    cc.setM_Locator_ID(M_Locator_ID);
    cc.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
    cc.setS_Resource_ID(S_Resource_ID);
    cc.setMovementDate(movementdate);
    cc.setDateAcct(movementdate);
    cc.setMovementQty(qty);
    cc.setScrappedQty(scrap);
    cc.setQtyReject(reject);
    cc.setSetupTimeReal(new BigDecimal(durationSetup));
    cc.setDurationReal(duration);
    cc.setPosted(false);
    cc.setProcessed(false);
    cc.setProcessing(false);
    cc.setUser1_ID(order.getUser1_ID());
    cc.setUser2_ID(order.getUser2_ID());
    cc.setM_Product_ID(M_Product_ID);
    if (PP_Order_Node_ID > 0)
      cc.setIsSubcontracting(PP_Order_Node_ID); 
    if (PP_Order_BOMLine_ID > 0)
      cc.setC_UOM_ID(0); 
    cc.saveEx();
    if (!cc.processIt("CO"))
      throw new CyprusException(cc.getProcessMsg()); 
    cc.saveEx();
    return cc;
  }
  
  public static void setPP_Order(I_PP_Cost_Collector cc, MPPOrder order) {
    cc.setPP_Order_ID(order.getPP_Order_ID());
    cc.setPP_Order_Workflow_ID(order.getMPPOrderWorkflow().get_ID());
    cc.setAD_Org_ID(order.getAD_Org_ID());
    cc.setM_Warehouse_ID(order.getM_Warehouse_ID());
    cc.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
    cc.setC_Activity_ID(order.getC_Activity_ID());
    cc.setC_Campaign_ID(order.getC_Campaign_ID());
    cc.setC_Project_ID(order.getC_Project_ID());
    cc.setDescription(order.getDescription());
    cc.setS_Resource_ID(order.getS_Resource_ID());
    cc.setM_Product_ID(order.getM_Product_ID());
    cc.setC_UOM_ID(order.getC_UOM_ID());
    cc.setM_AttributeSetInstance_ID(order.getM_AttributeSetInstance_ID());
    cc.setMovementQty(order.getQtyOrdered());
  }
  
  public MPPCostCollector(Properties ctx, int PP_Cost_Collector_ID, String trxName) {
    super(ctx, PP_Cost_Collector_ID, trxName);
    this.m_processMsg = null;
    this.m_justPrepared = false;
    this.m_order = null;
    this.m_orderNode = null;
    this.m_bomLine = null;
    if (PP_Cost_Collector_ID == 0) {
      setDocStatus("DR");
      setDocAction("CO");
      setMovementDate(new Timestamp(System.currentTimeMillis()));
      setIsActive(true);
      setPosted(false);
      setProcessing(false);
      setProcessed(false);
    } 
  }
  
  public MPPCostCollector(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_processMsg = null;
    this.m_justPrepared = false;
    this.m_order = null;
    this.m_orderNode = null;
    this.m_bomLine = null;
  }
  
  public MPPCostCollector(MPPOrder order) {
    this(order.getCtx(), 0, order.get_TrxName());
    setPP_Order((I_PP_Cost_Collector)this, order);
    this.m_order = order;
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(String.valueOf(desc) + " | " + description);
    } 
  }
  
  public void setC_DocTypeTarget_ID(String docBaseType) {
    MDocType[] doc = MDocType.getOfDocBaseType(getCtx(), docBaseType);
    if (doc == null)
      throw new DocTypeNotFoundException(docBaseType, ""); 
    setC_DocTypeTarget_ID(doc[0].get_ID());
  }
  
  public void setProcessed(boolean processed) {
	  super.setProcessed(processed);
	  if (get_ID() == 0)
		  return; 
	  String process=processed?"Y":"N";
	  String sql = "UPDATE PP_Cost_Collector SET Processed='"+process+"' WHERE PP_Cost_Collector_ID="+get_ID();
	  int no= DB.executeUpdate(sql, get_TrxName());
	  
	  String sqlLine = "UPDATE PP_Cost_CollectorLine SET Processed='"+process+"' WHERE PP_Cost_Collector_ID="+get_ID();
	  int noLine= DB.executeUpdate(sqlLine, get_TrxName());
	  
	  this.log.fine("setProcessed - " + processed +" Header - "+no+ " - Lines=" + noLine);
  }
  
  public boolean processIt(String processAction) {
    this.m_processMsg = null;
    DocumentEngine engine = new DocumentEngine(this, getDocStatus());
    return engine.processIt(processAction, getDocAction());
  }
  
  public boolean unlockIt() {
    this.log.info("unlockIt - " + toString());
    setProcessing(false);
    return true;
  }
  
  public boolean invalidateIt() {
    this.log.info("invalidateIt - " + toString());
    setDocAction("PR");
    return true;
  }
  
  public String prepareIt() {
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 1);
    if (this.m_processMsg != null)
      return "IN"; 
    MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocTypeTarget_ID(), getAD_Org_ID());
    setC_DocType_ID(getC_DocTypeTarget_ID());
    if (isActivityControl()) {
    	
    } else if (isIssue()) {
    	
    } else if (isReceipt()) {
    	
    } 
    this.m_justPrepared = true;
    setDocAction("CO");
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 8);
    if (this.m_processMsg != null)
      return "IN"; 
    return "IP";
  }
  
  public boolean approveIt() {
    this.log.info("approveIt - " + toString());
    return true;
  }
  
  public boolean rejectIt() {
    this.log.info("rejectIt - " + toString());
    return true;
  }
  
  public String completeIt() {
	  
    if (!this.m_justPrepared) {
      String status = prepareIt();
      if (!"IP".equals(status))
        return status; 
    } 
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 7);
    if (this.m_processMsg != null)
      return "IN"; 
    if (isOnlyIssue()) /// For Issue and Receipt collector type
    {
    	int reservationAttributeSetInstance_ID=0;
//    	For all lines
    	//MPPCostCollectorLine[] lines=getLines(false);
    	
    	// Updated the Qty at Order Activity window
    	
    	 // Update Queue Qty in Order Activity.. Mukesh @20250715
        Object[] params = {getMovementQty() , getPP_Order_ID()};
        String sql="update PP_Order_Node set QtyQueue=? Where PP_Order_ID=?"; 
        int noOfUp= DB.executeUpdateEx(sql, params, get_TrxName());
        log.info("Qty Queue updated in Order Activity "+noOfUp);
        // End of the code
    	
    	
    	List<MPPCostCollectorLine> lines = getLines(this.getPP_Cost_Collector_ID());
    	for(MPPCostCollectorLine line: lines)
    	{
    		MTransaction trxFrom = null; 
    		if(line.getM_Product_ID()>0)
    		{

    			MLocator locator = new MLocator (getCtx(), line.getM_Locator_ID(), get_TrxName()); //As discussion with Anshul updated by Mukesh @20240829

    			//Ignore the Material Policy when is Reverse Correction
    			if(!isReversal())
    				checkMaterialPolicy(line);

    			System.out.println("Line Product "+line.getM_Product_ID());
    			
    			if (line.get_ValueAsInt("M_AttributeSetInstance_ID")== 0)
				{
    				MPPCostCollectorMA mas[] = MPPCostCollectorMA.get(getCtx(),
							line.getPP_Cost_CollectorLine_ID(), get_TrxName());
    				
    				for (int j = 0; j < mas.length; j++)
					{
    					MPPCostCollectorMA ma = mas[j];
    					if (!MStorage.add(getCtx(), locator.getM_Warehouse_ID(),
    	    					line.getM_Locator_ID(),
    	    					line.getM_Product_ID(), 
    	    					ma.getM_AttributeSetInstance_ID(), reservationAttributeSetInstance_ID, 
    	    					ma.getMovementQty().negate(), Env.ZERO, Env.ZERO, get_TrxName()))
    	    			{
    	    				m_processMsg = "Cannot correct Cost Collector Line";
    	    				return DocAction.STATUS_Invalid;
    	    			}    					
    					trxFrom = new MTransaction (getCtx(), line.getAD_Org_ID(), 
								"A-",
								line.getM_Locator_ID(), line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
								ma.getMovementQty().negate(), getMovementDate(), get_TrxName());
    					trxFrom.set_ValueNoCheck("PP_Cost_CollectorLine_ID", line.getPP_Cost_CollectorLine_ID());
						//trxFrom.setM_MovementLine_ID(line.getM_MovementLine_ID());
						if (!trxFrom.save())
						{
							m_processMsg = "Transaction From not inserted (MA)";
							return DocAction.STATUS_Invalid;
						}
    					
						// Updated by Mukesh @20250709 regarding costing while complete button
						if(ma.getMovementQty().signum() != 0)
						{	
							String err = createCostDetail(line,ma.getM_AttributeSetInstance_ID(), ma.getMovementQty());
							if (err != null && err.length() > 0) {
								m_processMsg = err;
								return DocAction.STATUS_Invalid;
							}
						}
											
					}				
				}
    			
    			// Code updated by Mukesh @20250715
    			BigDecimal totalQty=DB.getSQLValueBD(get_TrxName(), "SELECT TotalQty FROM PP_Component_BOMLine WHERE PP_Order_ID=? AND M_Product_ID=?", new Object[] {getPP_Order_ID(), line.getM_Product_ID()});
    			// Update Present and Total Qty in Component Manufacturing BOM Quantity Mukesh @20250715
    			if(totalQty!=null && totalQty.compareTo(Env.ZERO)>0)
    			totalQty=totalQty.add(line.getQtyEntered());
    			else
    			totalQty=line.getQtyEntered();
    			
    			Object[] param = {line.getQtyEntered() ,totalQty, getPP_Order_ID(), line.getM_Product_ID()};
			    sql="UPDATE PP_Component_BOMLine SET PresentQty=?, TotalQty=? WHERE PP_Order_ID=? AND M_Product_ID=?"; 
			    noOfUp= DB.executeUpdateEx(sql, param, get_TrxName());
			    log.info("Qty Required updated in Component BOM Line "+noOfUp);
			    // End of the code
    						
    		}
    	}
    	
    }
    else if (isOnlyAssembly()) // Assembly Control Type 
    {
    	    	
    	// To Update Storage and MTransaction Table------------------------------------------------
		List<MPPCostCollectorLine> lines = getLines(this.getPP_Cost_Collector_ID());
    	for(MPPCostCollectorLine line: lines)
    	{
    		MTransaction trxFrom = null; 
    		if(line.getM_Product_ID()>0)
    		{
    			MProduct product=new MProduct(getCtx(), line.getM_Product_ID(), get_TrxName());
    			int M_AttributeSetInstance_ID=line.get_ValueAsInt("M_AttributeSetInstance_ID");
    			
    			if(M_AttributeSetInstance_ID==0)
    			{
    				//deliver using new asi
    				MAttributeSetInstance asi = MAttributeSetInstance.create(getCtx(), product, get_TrxName());
    				M_AttributeSetInstance_ID= asi.getM_AttributeSetInstance_ID();
    			}
    						
    			if (!MStorage.add(getCtx(), getM_Warehouse_ID(),
    					line.getM_Locator_ID(),
    					line.getM_Product_ID(), 
    					M_AttributeSetInstance_ID, 0, 
    					line.getQtyEntered(), Env.ZERO, Env.ZERO, get_TrxName()))
    			{
    				m_processMsg = "Cannot correct Cost Collector Line";
    				return DocAction.STATUS_Invalid;
    			}    					
    			trxFrom = new MTransaction (getCtx(), line.getAD_Org_ID(), 
    			"A+",
    			line.getM_Locator_ID(), line.getM_Product_ID(), M_AttributeSetInstance_ID,
    			line.getQtyEntered(), getMovementDate(), get_TrxName());
    			trxFrom.set_ValueNoCheck("PP_Cost_Collector_ID", line.getPP_Cost_Collector_ID());
    			//trxFrom.setM_MovementLine_ID(line.getM_MovementLine_ID());
    			if (!trxFrom.save())
    			{
    				m_processMsg = "Transaction From not inserted (MA)";
    				return DocAction.STATUS_Invalid;
    			}
    			
    			DB.executeUpdate("UPDATE PP_Cost_CollectorLine SET M_AttributeSetInstance_ID="+M_AttributeSetInstance_ID+" WHERE PP_Cost_CollectorLine_ID="+line.getPP_Cost_CollectorLine_ID(), 
    					get_TrxName());
    			
    			
    			// Updated by Mukesh @20250709 regarding costing while complete button
				if(line.getQtyEntered().signum() != 0)
				{	
					String err = createAssemblyCost(line,M_AttributeSetInstance_ID, line.getQtyEntered());
					if (err != null && err.length() > 0) {
						m_processMsg = err;
						return DocAction.STATUS_Invalid;
					}
				}
    		}
    	}
    	
    	
    	// When Assembly window will complete it will update the QtyAvailable and QtyAssembled at Header window Manu.. Order    	
    	// Code updated by Mukesh @20250716
    	BigDecimal qtyAvailable=get_ValueAsBigDecimal("QtyAvailable");
		BigDecimal qtyAssembled=DB.getSQLValueBD(get_TrxName(), "SELECT QtyAssembled FROM PP_Order WHERE PP_Order_ID=? ", new Object[] {getPP_Order_ID()});
		// Update Present and Total Qty in Component Manufacturing BOM Quantity Mukesh @20250715
		if(qtyAssembled!=null && qtyAssembled.compareTo(Env.ZERO)>0)
			qtyAssembled=qtyAssembled.add(qtyAvailable);
		else
			qtyAssembled=qtyAvailable;
		
		Object[] param = {qtyAvailable ,qtyAssembled, getPP_Order_ID()};
	    String sql="UPDATE PP_Order SET QtyAvailable=?, QtyAssembled=? WHERE PP_Order_ID=?"; 
	    int noOfUp= DB.executeUpdateEx(sql, param, get_TrxName());
	    log.info("QtyAvailable and QtyAssembled updated in PP_Order "+noOfUp);
	    // End of the code	
    }
    
    else if(isOnlyOperationExecution())
    {
    	if(MPPCostCollector.STEPFROM_Queue.equalsIgnoreCase(getStepFrom()) && MPPCostCollector.STEPFROM_In_Progress.equalsIgnoreCase(getStepTo()))
    	{	
    	 // Update Required Qty in Component BOM Line.. Mukesh @20250715
        Object[] params = {getMovementQty() , getPP_Order_ID(), getM_OperationRef_ID()};
        String sql="update PP_Order_Node set QtyQueue=0,QtyInprogress=? Where PP_Order_ID=? AND M_Operation_ID=? "; 
        int noOfUp= DB.executeUpdateEx(sql, params, get_TrxName());
        log.info("Qty Queue & Inprogress updated in Order Activity "+noOfUp);
    	}
    	else if(MPPCostCollector.STEPFROM_In_Progress.equalsIgnoreCase(getStepFrom()) && MPPCostCollector.STEPFROM_Finish.equalsIgnoreCase(getStepTo()))
    	{	
       	 // Update Required Qty in Component BOM Line.. Mukesh @20250715
           Object[] params = {getMovementQty() , getPP_Order_ID(), getM_OperationRef_ID()};
           String sql="update PP_Order_Node set QtyInprogress=0, QtyFinished=? Where PP_Order_ID=? AND M_Operation_ID=? "; 
           int noOfUp= DB.executeUpdateEx(sql, params, get_TrxName());
           log.info("Qty Inprogress & Finished  updated in Order Activity "+noOfUp);
       	}
    	// If Operation Change from Operation One to Operstion Two
    	else if(MPPCostCollector.STEPFROM_Finish.equalsIgnoreCase(getStepFrom()) && MPPCostCollector.STEPFROM_Queue.equalsIgnoreCase(getStepTo()))
    	{
    		// Update Required Qty in Component BOM Line.. Mukesh @20250715
            Object[] params = {getMovementQty() , getPP_Order_ID(), getM_OperationRef_ID()};
            String sql="update PP_Order_Node set QtyQueue=?, QtyFinished=0 Where PP_Order_ID=? AND M_Operation_ID=? "; 
            int noOfUp= DB.executeUpdateEx(sql, params, get_TrxName());
            log.info("Qty Inprogress & Finished  updated in Order Activity "+noOfUp);
    	}
    	if(MPPCostCollector.STEPFROM_Finish.equalsIgnoreCase(getStepTo()))
    	{
    		// Update Required Qty in Component BOM Line.. Mukesh @20250715
            Object[] params = {getMovementQty() , getPP_Order_ID()};
            String sql="update PP_Order set QtyAvailable=? Where PP_Order_ID=? "; 
            int noOfUp= DB.executeUpdateEx(sql, params, get_TrxName());
            log.info("QtyAvailable updated in Order Activity "+noOfUp);
    	}
        // End of the code
    	
    }
    
    else if (isActivityControl()) // Activity Control Type 
    {
      
    } 
    else if (isCostCollectorType(new String[] { "120" }) && getPP_Order_BOMLine_ID() > 0) // For Mix Varience & bom line ID
    {
    	
    }
  
    else if (isCostCollectorType(new String[] { "120" }) && getPP_Order_Node_ID() > 0) // For Mix Varience & Order Node    
    {
    }    
    this.m_processMsg = ModelValidationEngine.get().fireDocValidate((PO)this, 9);
    
    if (this.m_processMsg != null)
      return "IN";
    setProcessed(true);
    setDocAction("CL");
    setDocStatus("CO");
    return "CO";
  }
  
  /** Reversal Flag		*/
	private boolean m_reversal = false;
	
	/**
	 * 	Set Reversal
	 *	@param reversal reversal
	 */
	private void setReversal(boolean reversal)
	{
		m_reversal = reversal;
	}	//	setReversal
	/**
	 * 	Is Reversal
	 *	@return reversal
	 */
	private boolean isReversal()
	{
		return m_reversal;
	}	//	isReversal
  
  
  public boolean voidIt() {
    return false;
  }
  
  public boolean closeIt() {
    this.log.info("closeIt - " + toString());
    setDocAction("--");
    return true;
  }
  
  public boolean reverseCorrectIt() {
    return false;
  }
  
  public boolean reverseAccrualIt() {
    return false;
  }
  
  public boolean reActivateIt() {
    return false;
  }
  
  public String getSummary() {
    StringBuffer sb = new StringBuffer();
    sb.append(getDescription());
    return sb.toString();
  }
  
  public String getProcessMsg() {
    return this.m_processMsg;
  }
  
  public int getDoc_User_ID() {
    return getCreatedBy();
  }
  
  public int getC_Currency_ID() {
    return 0;
  }
  
  public BigDecimal getApprovalAmt() {
    return Env.ZERO;
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
    ReportEngine re = ReportEngine.get(getCtx(), 0, getPP_Order_ID());
    if (re == null)
      return null; 
    return re.getPDF(file);
  }
  
  public String getDocumentInfo() {
    MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
    return String.valueOf(dt.getName()) + " " + getDocumentNo();
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getM_Locator_ID() <= 0 && getM_Warehouse_ID() > 0) {
      MWarehouse wh = MWarehouse.get(getCtx(), getM_Warehouse_ID());
      MLocator loc = wh.getDefaultLocator();
      if (loc != null)
        setM_Locator_ID(loc.get_ID()); 
    } 
//    if (isIssue()) { 
//      if (getPP_Order_BOMLine_ID() <= 0)
//        throw new FillMandatoryException(new String[] { "PP_Order_BOMLine_ID" }); 
//      if (getC_UOM_ID() <= 0)
//        setC_UOM_ID(getPP_Order_BOMLine().getC_UOM_ID()); 
//      if (getC_UOM_ID() != getPP_Order_BOMLine().getC_UOM_ID())
//        throw new CyprusException("@PP_Cost_Collector_ID@ @C_UOM_ID@ <> @PP_Order_BOMLine_ID@ @C_UOM_ID@"); 
//    } 
//    if (isActivityControl() && getPP_Order_Node_ID() <= 0)
//      throw new FillMandatoryException(new String[] { "PP_Order_Node_ID" }); 
    return true;
  }
  
  public MPPOrderNode getPP_Order_Node() {
    int node_id = getPP_Order_Node_ID();
    if (node_id <= 0) {
      this.m_orderNode = null;
      return null;
    } 
    if (this.m_orderNode == null || this.m_orderNode.get_ID() != node_id)
      this.m_orderNode = new MPPOrderNode(getCtx(), node_id, get_TrxName()); 
    return this.m_orderNode;
  }
  
  public MPPOrderBOMLine getPP_Order_BOMLine() {
    int id = getPP_Order_BOMLine_ID();
    if (id <= 0) {
      this.m_bomLine = null;
      return null;
    } 
    if (this.m_bomLine == null || this.m_bomLine.get_ID() != id)
      this.m_bomLine = new MPPOrderBOMLine(getCtx(), id, get_TrxName()); 
    this.m_bomLine.set_TrxName(get_TrxName());
    return this.m_bomLine;
  }
  
  public MPPOrder getPP_Order() {
    int id = getPP_Order_ID();
    if (id <= 0) {
      this.m_order = null;
      return null;
    } 
    if (this.m_order == null || this.m_order.get_ID() != id)
      this.m_order = new MPPOrder(getCtx(), id, get_TrxName()); 
    return this.m_order;
  }
  
  public long getDurationBaseSec() {
    return getPP_Order().getMPPOrderWorkflow().getDurationBaseSec();
  }
  
  public Timestamp getDateStart() {
    double duration = getDurationReal().doubleValue();
    if (duration != 0.0D) {
      long durationMillis = (long)(getDurationReal().doubleValue() * getDurationBaseSec() * 1000.0D);
      return new Timestamp(getMovementDate().getTime() - durationMillis);
    } 
    return getMovementDate();
  }
  
  public Timestamp getDateFinish() {
    return getMovementDate();
  }
  
  private String createPO(MPPOrderNode activity) {
    String msg = "";
    HashMap<Integer, MOrder> orders = new HashMap<Integer, MOrder>();
    String whereClause = "PP_Order_Node_ID=? AND IsSubcontracting=?";
    Collection<MPPOrderNodeProduct> subcontracts = (new Query(getCtx(), "PP_Order_Node_Product", whereClause, get_TrxName()))
      .setParameters(new Object[] { Integer.valueOf(activity.get_ID()), Boolean.valueOf(true) }).setOnlyActiveRecords(true)
      .list();
    for (MPPOrderNodeProduct subcontract : subcontracts) {
      MProduct product = MProduct.get(getCtx(), subcontract.getM_Product_ID());
      if (!product.isPurchased() || !"S".equals(product.getProductType()))
        throw new CyprusException("The Product: " + product.getName() + " Do not is Purchase or Service Type"); 
      int C_BPartner_ID = activity.getC_BPartner_ID();
      MProductPO product_po = null;
      byte b;
      int i;
      MProductPO[] arrayOfMProductPO;
      for (i = (arrayOfMProductPO = MProductPO.getOfProduct(getCtx(), product.get_ID(), null)).length, b = 0; b < i; ) {
        MProductPO ppo = arrayOfMProductPO[b];
        if (C_BPartner_ID == ppo.getC_BPartner_ID()) {
          C_BPartner_ID = ppo.getC_BPartner_ID();
          product_po = ppo;
          break;
        } 
        if (ppo.isCurrentVendor() && ppo.getC_BPartner_ID() != 0) {
          C_BPartner_ID = ppo.getC_BPartner_ID();
          product_po = ppo;
          break;
        } 
        b++;
      } 
      if (C_BPartner_ID <= 0 || product_po == null)
        throw new NoVendorForProductException(product.getName()); 
      Timestamp today = new Timestamp(System.currentTimeMillis());
      Timestamp datePromised = TimeUtil.addDays(today, product_po.getDeliveryTime_Promised());
      MOrder order = orders.get(Integer.valueOf(C_BPartner_ID));
      if (order == null) {
        order = new MOrder(getCtx(), 0, get_TrxName());
        MBPartner vendor = MBPartner.get(getCtx(), C_BPartner_ID);
        order.setAD_Org_ID(getAD_Org_ID());
        order.setBPartner(vendor);
        order.setIsSOTrx(false);
        order.setC_DocTypeTarget_ID();
        order.setDatePromised(datePromised);
        order.setDescription(String.valueOf(Msg.translate(getCtx(), "PP_Order_ID")) + ":" + getPP_Order().getDocumentNo());
        order.setDocStatus("DR");
        order.setDocAction("CO");
        order.setAD_User_ID(getAD_User_ID());
        order.setM_Warehouse_ID(getM_Warehouse_ID());
        order.saveEx();
        addDescription(String.valueOf(Msg.translate(getCtx(), "C_Order_ID")) + ": " + order.getDocumentNo());
        orders.put(Integer.valueOf(C_BPartner_ID), order);
        msg = String.valueOf(msg) + Msg.translate(getCtx(), "C_Order_ID") + 
          " : " + order.getDocumentNo() + 
          " - " + 
          Msg.translate(getCtx(), "C_BPartner_ID") + 
          " : " + vendor.getName() + " , ";
      } 
      BigDecimal QtyOrdered = getMovementQty().multiply(subcontract.getQty());
      if (product_po.getOrder_Min().signum() > 0)
        QtyOrdered = QtyOrdered.max(product_po.getOrder_Min()); 
      if (product_po.getOrder_Pack().signum() > 0 && QtyOrdered.signum() > 0)
        QtyOrdered = product_po.getOrder_Pack().multiply(QtyOrdered.divide(product_po.getOrder_Pack(), 0, 0)); 
      MOrderLine oline = new MOrderLine(order);
      oline.setM_Product_ID(product.getM_Product_ID());
      oline.setDescription(activity.getDescription());
      oline.setM_Warehouse_ID(getM_Warehouse_ID());
      oline.setQty(QtyOrdered);
      oline.setPP_Cost_Collector_ID(get_ID());
      oline.setDatePromised(datePromised);
      oline.saveEx();
      setProcessed(true);
    } 
    return msg;
  }
  
  public MProduct getM_Product() {
    return MProduct.get(getCtx(), getM_Product_ID());
  }
  
  public I_C_UOM getC_UOM() {
    return (I_C_UOM)MUOM.get(getCtx(), getC_UOM_ID());
  }
  
  public boolean isIssue() {
    if (!isCostCollectorType(new String[] { "110" }) && (!isCostCollectorType(new String[] { "130" }) || getPP_Order_BOMLine_ID() <= 0) && (
      !isCostCollectorType(new String[] { "150" }) || getPP_Order_BOMLine_ID() <= 0))
      return false; 
    return true;
  }
  
  public boolean isReceipt() {
    return isCostCollectorType(new String[] { "100" });
  }
  
  public boolean isOnlyIssue() {
	    return isCostCollectorType(new String[] { "110" });
	  }
  public boolean isOnlyAssembly() {
	    return isCostCollectorType(new String[] { "180" });
	  }
  public boolean isOnlyOperationExecution() {
	    return isCostCollectorType(new String[] { "170" });
	  } 
  public boolean isActivityControl() {
    return isCostCollectorType(new String[] { "160" });
  } 
  public boolean isVariance() {
    return isCostCollectorType(new String[] { "130", 
          "120", 
          "140", 
          "150" });
  }
  
  public String getMovementType() {
    if (isReceipt())
      return "W+"; 
    if (isIssue())
      return "W-"; 
    return null;
  }
  
  public boolean isCostCollectorType(String... types) {
    String type = getCostCollectorType();
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = types).length, b = 0; b < i; ) {
      String t = arrayOfString[b];
      if (type.equals(t))
        return true; 
      b++;
    } 
    return false;
  }
  
  public boolean isFloorStock() {
    String whereClause = "PP_Order_BOMLine_ID=? AND IssueMethod=?";
    boolean isFloorStock = (new Query(getCtx(), "PP_Order_BOMLine", "PP_Order_BOMLine_ID=? AND IssueMethod=?", get_TrxName()))
      .setOnlyActiveRecords(true)
      .setParameters(new Object[] { Integer.valueOf(getPP_Order_BOMLine_ID()), "2" }).match();
    return isFloorStock;
  }
  
  public void setIsSubcontracting(int PP_Order_Node_ID) {
    setIsSubcontracting(MPPOrderNode.get(getCtx(), PP_Order_Node_ID, get_TrxName()).isSubcontracting());
  }
  
  
  
  //-----------------------Method Added by Mukesh @20250708
  
  
  /**	Lines					*/
	private MPPCostCollectorLine[]	m_lines = null;
  
  
  /**
	 * 	Get Lines of Shipment
	 * 	@param requery refresh from db
	 * 	@return lines
	 */
	public MPPCostCollectorLine[] getLinesOld (boolean requery)
	{
		if (this.m_lines != null && !requery) {
		      set_TrxName((PO[])this.m_lines, get_TrxName());
		      return this.m_lines;
		    } 
		    String whereClause = "PP_Cost_Collector_ID=?";
		    List<MPPCostCollectorLine> list = (new Query(getCtx(), I_PP_Cost_CollectorLine.Table_Name, whereClause, get_TrxName()))
		    		.setParameters(new Object[] { Integer.valueOf(getPP_Cost_Collector_ID()) })
		    		.setOrderBy("Line")
		    		.list();
		    for(MPPCostCollectorLine l : list)
		    	System.out.println(l.getM_Product_ID());
		    
		    this.m_lines = list.<MPPCostCollectorLine>toArray(new MPPCostCollectorLine[list.size()]);
		    return this.m_lines;
		    
	}	//	getMInOutLines
 
	public List<MPPCostCollectorLine> getLines (int mppCostCollector)
	{	
		List<MPPCostCollectorLine> list = new ArrayList<MPPCostCollectorLine> ();
		StringBuffer sql = new StringBuffer("SELECT * FROM PP_Cost_CollectorLine WHERE PP_Cost_Collector_ID=? ORDER BY Line");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, mppCostCollector);	
			 rs = pstmt.executeQuery();
			while (rs.next())
			{
				list.add(new MPPCostCollectorLine(getCtx(), rs, get_TrxName()));
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//
		//MPPCostCollectorLine[] lines = new MPPCostCollectorLine[list.size ()];
		//list.toArray (lines);
//		return lines;
		return list;

	}	//	getLines
	
	/**
	 * 	Check Material Policy
	 * 	Sets line ASI
	 */
	private void checkMaterialPolicy(MPPCostCollectorLine line)
	{
		int no = MPPCostCollectorMA.deleteCostCollectorMA(line.getPP_Cost_CollectorLine_ID(), get_TrxName());
		if (no > 0)
			log.config("Delete old #" + no);
		BigDecimal trxCost=Env.ZERO;
		boolean needSave = false;

		//	Attribute Set Instance
		if (line.get_ValueAsInt("M_AttributeSetInstance_ID")== 0)
		{
			MProduct product = MProduct.get(getCtx(), line.getM_Product_ID());
			String MMPolicy = product.getMMPolicy();
			MStorage[] storages = MStorage.getWarehouse(getCtx(), 0, line.getM_Product_ID(), 0, 
					null, MClient.MMPOLICY_FiFo.equals(MMPolicy), true, line.getM_Locator_ID(), get_TrxName());
			System.out.println(line.getQtyEntered());
			BigDecimal qtyToDeliver = line.getQtyEntered();

			for (MStorage storage: storages)
			{
				MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
				for(int asn = 0; asn < acctschemas.length; asn++)
				{
					MAcctSchema as = acctschemas[asn];
					String costingMethod = null;
					//							MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), get_TrxName());
					MProductCategoryAcct prca = MProductCategoryAcct.get(getCtx(), product.getM_Product_Category_ID(), as.getC_AcctSchema_ID(), get_TrxName());
					if(prca.getCostingMethod() != null)
					{
						costingMethod = prca.getCostingMethod();
					}
					else
					{
						costingMethod = as.getCostingMethod();
					}
					MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
					if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0)
					{
						
						if(MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod))
						{
							trxCost = MCostQueue.getCosts(product, storage.getM_AttributeSetInstance_ID(), as, getAD_Org_ID(), ce, qtyToDeliver, get_TrxName());
						}
						else
						{								
							trxCost = MCost.getCurrentCost(product, storage.getM_AttributeSetInstance_ID(), as, line.getAD_Org_ID(), costingMethod, qtyToDeliver, 0, true, get_TrxName());
						}
						if(trxCost!=null && trxCost.compareTo(Env.ZERO)>0)
						{
							MPPCostCollectorMA ma = new MPPCostCollectorMA (line, 
									storage.getM_AttributeSetInstance_ID(),
									qtyToDeliver);
							ma.set_ValueNoCheck("TrxCost", trxCost.divide(qtyToDeliver));
							ma.saveEx();		
							qtyToDeliver = Env.ZERO;
							log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);
						}
					}
					else
					{	
						
						if(MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod))
						{
							trxCost = MCostQueue.getCosts(product, storage.getM_AttributeSetInstance_ID(), as, getAD_Org_ID(), ce, qtyToDeliver, get_TrxName());
						}
						else
						{								
							trxCost = MCost.getCurrentCost(product, storage.getM_AttributeSetInstance_ID(), as, line.getAD_Org_ID(), costingMethod, qtyToDeliver, 0, true, get_TrxName());
						}
						
						if(trxCost!=null && trxCost.compareTo(Env.ZERO)>0)
						{
							MPPCostCollectorMA ma = new MPPCostCollectorMA (line, 
									storage.getM_AttributeSetInstance_ID(),
									storage.getQtyOnHand());
							ma.set_ValueNoCheck("TrxCost", trxCost.divide(qtyToDeliver));
							//ma.setTrxCost(trxCost.divide(qtyToDeliver));
							ma.saveEx();	
							qtyToDeliver = qtyToDeliver.subtract(storage.getQtyOnHand());
							log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);
						}
					}
					if (qtyToDeliver.signum() == 0)
						break;
				}
			}
							
			//	No AttributeSetInstance found for remainder
			if (qtyToDeliver.signum() != 0)
			{
				//deliver using new asi
				MAttributeSetInstance asi = MAttributeSetInstance.create(getCtx(), product, get_TrxName());
				int M_AttributeSetInstance_ID = asi.getM_AttributeSetInstance_ID();
				MPPCostCollectorMA ma = new MPPCostCollectorMA (line, M_AttributeSetInstance_ID , qtyToDeliver);
				ma.saveEx();
				log.fine("##: " + ma);
			}
		}	//	attributeSetInstance
		
		if (needSave)
		{
			line.saveEx();
		}
	}	//	checkMaterialPolicy
  
	
	//////////////////Costing 
	
	/**
	 * Create Cost Detail
	 * @param line
	 * @param Qty
	 * @return an EMPTY String on success otherwise an ERROR message
	 * @author Mukesh Created similar as Inventory to prepare costing while complete
	 * 
	 */
	private String createCostDetail(MPPCostCollectorLine line, int M_AttributeSetInstance_ID ,BigDecimal qty)
	{
		// Get Account Schemas to create MCostDetail
		MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());

		for(int asn = 0; asn < acctschemas.length; asn++)
		{
			MAcctSchema as = acctschemas[asn];
			
			if (as.isSkipOrg(getAD_Org_ID()) || as.isSkipOrg(line.getAD_Org_ID()))
			{
				continue;
			}
			
			MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), line.get_TrxName());
			String costingMethod = product.getCostingMethod(as);
			
			BigDecimal costs = Env.ZERO;
			BigDecimal unitCost=Env.ZERO;
			BigDecimal additionalCost = Env.ZERO;
			if (isReversal())
			{				
				String sql = "SELECT amt * -1 FROM M_CostDetail WHERE M_MovementLine_ID=?"; // negate costs				
				// MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), line.get_TrxName());
				String CostingLevel = product.getCostingLevel(as);
				if (MAcctSchema.COSTINGLEVEL_Organization.equals(CostingLevel))
					sql = sql + " AND AD_Org_ID=" + getAD_Org_ID(); 
				else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(CostingLevel) && M_AttributeSetInstance_ID != 0)
					sql = sql + " AND M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID;
				costs = DB.getSQLValueBD(line.get_TrxName(), sql, line.get_ValueAsInt("ReversalLine_ID"));
			}
			else 
			{
				if(line.getQtyEntered().compareTo(Env.ZERO)>0 && 
				(MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod)))
				{
					BigDecimal toGetCost=getCost(line,as);
					unitCost=toGetCost;	
					costs=toGetCost.multiply(qty);
					
					String sql = "SELECT " +
							"COALESCE(SUM(c.CurrentCostPrice),0) " + 
							"FROM M_Cost c " + 
							"LEFT OUTER JOIN M_CostElement ce ON (c.M_CostElement_ID=ce.M_CostElement_ID) " + 
							"WHERE c.AD_Client_ID=?" +  
							" AND c.AD_Org_ID=?" +  
							" AND c.M_Product_ID=?" + 
							" AND (c.M_AttributeSetInstance_ID=0)" + 
							" AND c.M_CostType_ID=?" + 
							" AND c.C_AcctSchema_ID=?" +  
							" AND (ce.CostingMethod IS NULL) ";
					additionalCost = DB.getSQLValueBD(get_TrxName(), sql, product.getAD_Client_ID(), line.getAD_Org_ID(), line.getM_Product_ID(), as.getM_CostType_ID(), as.getC_AcctSchema_ID());
					if(additionalCost.signum() != 0)
					{
						costs = costs.add(additionalCost.multiply(qty.abs()));
					}					
				}
				else
				{
					ProductCost pc = new ProductCost (getCtx(), 
							line.getM_Product_ID(), M_AttributeSetInstance_ID, line.get_TrxName());
					pc.setQty(qty);
					costs = pc.getProductCosts(as, line.getAD_Org_ID(), as.getCostingMethod(), 0,true);
					unitCost=costs.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
				}
			}
			if (costs == null)
			{
				return "No Costs for " + line.getM_Product().getName();
			}
			
			
		// update TrxCost @Mukesh 20240716
			if(unitCost.signum()!=0)
			{
				// @ M_MovementLine PP_Cost_CollectorLine_ID
				int retVal=DB.executeUpdate("UPDATE PP_Cost_CollectorLine SET TrxCost="+unitCost+" WHERE PP_Cost_CollectorLine_ID="+line.getPP_Cost_CollectorLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_MovementLine = " +retVal);
				// @ M_MovementLineMA
				/*
				 * retVal=DB.executeUpdate("UPDATE M_MovementLineMA SET TrxCost="
				 * +unitCost+" WHERE M_MovementLine_ID="+line.getM_MovementLine_ID(),
				 * get_TrxName() ); log.fine("Trx Cost updated at M_MovementLineMA = " +retVal);
				 */
				// @ M_Transaction
				retVal=DB.executeUpdate("UPDATE M_Transaction SET TrxCost="+unitCost+" WHERE PP_Cost_CollectorLine_ID="+line.getPP_Cost_CollectorLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_Transaction = " +retVal);
			}
			
			
			// get these details from Doc_Movement.Java
//			Only for between-org movements
			MLocator fromLocator=MLocator.get(getCtx(), line.getM_Locator_ID());
			// Now it is allow to create costing details for any type of Organization transfer
			if(true) // (fromLocator.getAD_Org_ID() != toLocator.getAD_Org_ID())
			{
				String costingLevel = product.getCostingLevel(as);
				if (!MAcctSchema.COSTINGLEVEL_Organization.equals(costingLevel))
					continue;

				String description = line.getDescription();
				if (description == null)
					description = "";
				//	Cost Detail From
				//MCostDetail.createCostCollector
				MCostDetail.createCostCollector(as, fromLocator.getAD_Org_ID(), 	//	from locator org
						line.getM_Product_ID(), M_AttributeSetInstance_ID,
						line.get_ID(), 0,
						costs.negate(), qty.negate(), true,
						description + "Cost Collector", get_TrxName());
				
				// End of these details
			}			
			// Code updated by Mukesh @20240726 regarding to update current cost price at Cost Details
			if(qty.signum()!=0 )// && (MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod)))
			{
				System.out.println(line.getPP_Cost_CollectorLine_ID());
//				MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
//		    	BigDecimal currentCostDT = MCostQueue.getCosts(product, M_AttributeSetInstance_ID, as, getAD_Org_ID(), ce, qty.negate(), null);
			    MCostDetail cd = MCostDetail.get (as.getCtx(), "PP_Cost_CollectorLine_ID=?", 
						line.getPP_Cost_CollectorLine_ID(), M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), get_TrxName());
			    if(cd!=null)
			    {	
			    	cd.setCurrentCostPrice(costs.abs().divide(qty.abs(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
			    	cd.save();
			    }
			}
		}		
		return "";
	}
	/**
	 * Get Cost from Movement Attribute Tab
	 * @param line
	 * @param as
	 * @return
	 */
	public BigDecimal getCost(MPPCostCollectorLine line, MAcctSchema as)
	{
		MPPCostCollectorMA[] m_linesma = MPPCostCollectorMA.get(getCtx(), line.getPP_Cost_CollectorLine_ID(), get_TrxName());
		BigDecimal cost = Env.ZERO;
		BigDecimal qty = Env.ZERO;

		for(int ma = 0; ma < m_linesma.length; ma++)
		{
			MPPCostCollectorMA lineMA = m_linesma[ma];
			cost = cost.add(lineMA.get_ValueAsBigDecimal("TrxCost").multiply(lineMA.getMovementQty()));
			qty=qty.add(lineMA.getMovementQty());
		}
		cost=cost.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);;
		return cost;
	}	//	getCost
	
	
	public BigDecimal getAssemblyCost()
	{
    	BigDecimal trxCost=new BigDecimal(0); 

		String sql="SELECT MAX(pp.PP_Cost_Collector_ID) "
        		+ " FROM PP_Cost_Collector pp "
        		+ " INNER JOIN PP_Cost_Collector line ON (pp.PP_Cost_Collector_ID=line.PP_Cost_Collector_ID) "
        		+ " WHERE pp.PP_Order_ID="+getPP_Order_ID()+" AND pp.AD_Org_ID="+getAD_Org_ID()+" AND pp.M_Product_ID="+getM_Product_ID()
        		+ " AND pp.MovementQty="+getMovementQty()+" AND pp.CostCollectorType='110' AND pp.docStatus='CO'";
        
        Integer PP_Cost_Collector_ID=DB.getSQLValue(get_TrxName(), sql);
        
        if(PP_Cost_Collector_ID>0)
        {

        	List<MPPCostCollectorLine> lines = getLines(PP_Cost_Collector_ID);

        	for(MPPCostCollectorLine line : lines)  
        	{
        		trxCost=trxCost.add(line.getTrxCost().multiply(line.getQtyEntered()));
        	}   	 	
        }
        
        /// divide by Header Movement Qty Need to work
        return trxCost;
        
	}
	
	
	private String createAssemblyCost(MPPCostCollectorLine line, int M_AttributeSetInstance_ID ,BigDecimal qty)
	{
		
		/// Get Trx Cost from respected Component Issue Type
    	
    	MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
		for(int asn = 0; asn < acctschemas.length; asn++)
		{

			MAcctSchema as = acctschemas[asn];
			BigDecimal trxCost=getAssemblyCost();

			if(trxCost.compareTo(Env.ZERO)>0)
			{
				//MCostDetail.createCostCollector
				MCostDetail.createCostCollector(as, getAD_Org_ID(), 	//	from locator org
						getM_Product_ID(), M_AttributeSetInstance_ID,
						line.get_ID(), 0,
						trxCost, qty, true,
						"Assembly Cost Collector", get_TrxName());
				

				if(qty.signum()!=0 )// && (MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod)))
				{
					System.out.println(line.getPP_Cost_CollectorLine_ID());
//					MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
//			    	BigDecimal currentCostDT = MCostQueue.getCosts(product, M_AttributeSetInstance_ID, as, getAD_Org_ID(), ce, qty.negate(), null);
				    MCostDetail cd = MCostDetail.get (as.getCtx(), "PP_Cost_CollectorLine_ID=?", 
							line.getPP_Cost_CollectorLine_ID(), M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), get_TrxName());
				    if(cd!=null)
				    {	
				    	cd.setCurrentCostPrice(trxCost.abs().divide(qty.abs(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
				    	cd.setProcessed(true);
				    	cd.save();
				    }
				}
				trxCost=trxCost.divide(qty);

				int retVal=DB.executeUpdate("UPDATE PP_Cost_Collector SET TrxCost="+trxCost+", M_AttributeSetInstance_ID="+M_AttributeSetInstance_ID+" WHERE PP_Cost_Collector_ID="+getPP_Cost_Collector_ID(), get_TrxName() ); 			
				log.fine("Trx Cost updated at M_MovementLine = " +retVal);
				
				// Updated line as well
				retVal=DB.executeUpdate("UPDATE PP_Cost_CollectorLine SET TrxCost="+trxCost+" WHERE PP_Cost_CollectorLine_ID="+line.getPP_Cost_CollectorLine_ID(), get_TrxName() ); 			
				log.fine("Trx Cost updated at M_MovementLine = " +retVal);
				
				/// MTransaction 
				
				// @ M_Transaction
				retVal=DB.executeUpdate("UPDATE M_Transaction SET TrxCost="+trxCost+" WHERE PP_Cost_CollectorLine_ID="+line.getPP_Cost_CollectorLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_Transaction = " +retVal);
				
			}
		}
		return "";
	}
	
}
