package org.cyprus.mfg.acct;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkCenter;
import org.cyprus.mfg.model.MMFGWorkCenterCost;
import org.cyprus.mfg.model.MMFGWorkCenterCostDetail;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderComponent;
import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprus.mfg.model.MMFGWorkOrderResource;
import org.cyprus.mfg.model.MMFGWorkOrderResourceTxnLine;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.model.MMFGWorkOrderTransactionLine;
import org.cyprus.mfg.model.MMFGWorkOrderValue;
import org.cyprus.mfg.model.MMFGWorkOrderValueDetail;
import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mfg.model.X_MFG_WorkOrderTransaction;
import org.cyprusbrs.acct.Doc;
import org.cyprusbrs.acct.DocLine;
import org.cyprusbrs.acct.Fact;
import org.cyprusbrs.acct.FactLine;
import org.cyprusbrs.framework.MAccount;
import org.cyprusbrs.framework.MCost;
import org.cyprusbrs.framework.MCostDetail;
import org.cyprusbrs.framework.MCostElement;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.X_M_Cost;
import org.cyprusbrs.framework.X_M_CostElement;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MAcctSchemaDefault;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Trx;

public class Doc_WorkOrderTransaction extends Doc {
  MMFGWorkOrderTransaction WoTrx;
   int mfg_workcenter_id;
   int m_costelement_id;
  
   Trx trxname;
   
  MMFGWorkOrder Wo;
  
  int BatchSize;
  
  private String DocTxnType;
  
  BigDecimal materialIn = BigDecimal.ZERO;
  
  BigDecimal materialOut = BigDecimal.ZERO;
  
  BigDecimal materialOverhdIn = BigDecimal.ZERO;
  
  BigDecimal materialOverhdOut = BigDecimal.ZERO;
  
  BigDecimal resourceIn = BigDecimal.ZERO;
  
  BigDecimal resourceOut = BigDecimal.ZERO;
  
  BigDecimal overhdIn = BigDecimal.ZERO;
  
  BigDecimal overhdOut = BigDecimal.ZERO;
  
  int precision = 6;
  
  public Doc_WorkOrderTransaction(MAcctSchema[] ass, ResultSet rs, String trx) {
    super(ass, MMFGWorkOrderTransaction.class, rs, "WOT", trx);
  }
  
  
  public String loadDocumentDetails() {
    this.WoTrx = (MMFGWorkOrderTransaction)getPO();
    this.DocTxnType = this.WoTrx.getWorkOrderTxnType();
    this.Wo = new MMFGWorkOrder(getCtx(), this.WoTrx.getMFG_WorkOrder_ID(), getTrxName());
    this.p_lines = loadLines();
    MProduct Assembly = new MProduct(getCtx(), this.Wo.getM_Product_ID(), getTrxName());
    this.BatchSize = Assembly.getBatchSize();
    setDateAcct(this.WoTrx.getDateAcct());
    setDateDoc(this.WoTrx.getDateTrx());
    return null;
  }
  
  private DocLine[] loadLines() {
    ArrayList<DocLine> list = new ArrayList<DocLine>();
    if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage)) {
      MMFGWorkOrderResourceTxnLine[] arrayOfMMFGWorkOrderResourceTxnLine = this.WoTrx.getResourceTxnLines(null, null);
      for (MMFGWorkOrderResourceTxnLine MMFGWorkOrderResourceTxnLine : arrayOfMMFGWorkOrderResourceTxnLine) {
      //  DocLine docLine = new DocLine((PO)MMFGWorkOrderResourceTxnLine, this);
          DocLine docLine = new DocLine(MMFGWorkOrderResourceTxnLine, this);

    	  BigDecimal Qty = MMFGWorkOrderResourceTxnLine.getQtyEntered();
        docLine.setQty(Qty, false);
        list.add(docLine);
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyCompletionToInventory) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
      MMFGWorkOrderTransactionLine[] lines = this.WoTrx.getLines(null, null);
      for (MMFGWorkOrderTransactionLine line : lines) {
    //    DocLine docLine = new DocLine((PO)line, this);
          DocLine docLine = new DocLine(line, this);

    	  BigDecimal Qty = line.getQtyEntered();
        docLine.setQty(Qty, false);
        list.add(docLine);
      } 
    } 
    DocLine[] dl = new DocLine[list.size()];
    list.toArray(dl);
    return dl;
  }
  
  public BigDecimal getBalance() {
    return Env.ZERO;
  }
  
  public ArrayList<Fact> createFacts(MAcctSchema as) throws Exception {
    BigDecimal Value = BigDecimal.ZERO;
    MMFGWorkCenter Wc = null;
    MMFGWorkCenterCost[] WorkCenterCosts = null;
    this.mfg_workcenter_id = -1;
    this.precision = as.getCostingPrecision();
    ArrayList<Fact> facts = new ArrayList<Fact>();
    MProduct assembly = new MProduct(getCtx(), this.WoTrx.getM_Product_ID(), getTrxName());
    if (!as.getCostingMethod().equals(X_M_CostElement.COSTINGMETHOD_StandardCosting))
      return facts; 
   // if (this.m_repost)
     // deleteWorkOrderValue(as); 
    boolean createWOValue = this.Wo.getWOType().equals(X_MFG_WorkOrder.WOTYPE_Standard);
    Fact fact = new Fact(this, as, Fact.POST_Actual);
    setC_Currency_ID(as.getC_Currency_ID());
    MAccount crAcct = null;
    MAccount drAcct = null;
    BigDecimal quantity = BigDecimal.ZERO;
    if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_WorkOrderMove)) {
      int fromOperation = this.WoTrx.getOperationFrom_ID();
      int fromSeqNo = (new MMFGWorkOrderOperation(getCtx(), fromOperation, getTrxName())).getSeqNo();
      int toOperation = this.WoTrx.getOperationTo_ID();
      int toSeqNo = (new MMFGWorkOrderOperation(getCtx(), toOperation, getTrxName())).getSeqNo();
      String fromStep = this.WoTrx.getStepFrom();
      String toStep = this.WoTrx.getStepTo();
      quantity = this.WoTrx.getQtyEntered();
      boolean isScrap = toStep.equals(X_MFG_WorkOrderTransaction.STEPTO_Scrap);
      if (!isScrap) {
        StringBuffer whereClause = new StringBuffer("");
        if (fromStep.equals(X_MFG_WorkOrderTransaction.STEPFROM_ToMove)) {
          whereClause.append(" SeqNo >  " + fromSeqNo);
        } else {
          whereClause.append(" SeqNo >= " + fromSeqNo);
        } 
        if (toStep.equals(X_MFG_WorkOrderTransaction.STEPTO_ToMove) || toStep.equals(X_MFG_WorkOrderTransaction.STEPTO_Scrap)) {
          whereClause.append(" AND SeqNo <= " + toSeqNo);
        } else {
          whereClause.append(" AND SeqNo < " + toSeqNo);
        } 
        String orderClause = " SeqNo ";
        MMFGWorkOrderOperation[] operations = MMFGWorkOrderOperation.getOfWorkOrder(this.Wo, whereClause.toString(), orderClause);
        if (operations.length != 0)
          for (MMFGWorkOrderOperation operation : operations) {
            if (!operation.isOptional() || operation.getMFG_Operation_ID() == fromOperation || operation.getMFG_Operation_ID() == toOperation) {
              this.mfg_workcenter_id = operation.getMFG_WorkCenter_ID();
              Wc = new MMFGWorkCenter(getCtx(), this.mfg_workcenter_id, getTrxName());
              if (Wc != null)
                WorkCenterCosts = MMFGWorkCenterCost.getWorkCenterCosts(Wc, as.getM_CostType_ID(), as, this.Wo.getAD_Org_ID()); 
              for (MMFGWorkCenterCost WorkCenterCost : WorkCenterCosts) {
                this.m_costelement_id = WorkCenterCost.getM_CostElement_ID();
                this.materialIn = BigDecimal.ZERO;
                this.materialOut = BigDecimal.ZERO;
                this.materialOverhdIn = BigDecimal.ZERO;
                this.materialOverhdOut = BigDecimal.ZERO;
                this.resourceIn = BigDecimal.ZERO;
                this.resourceOut = BigDecimal.ZERO;
                this.overhdIn = BigDecimal.ONE;
                this.overhdOut = BigDecimal.ZERO;
                crAcct = getAccount(7678, as);
                drAcct = getAccount(1777, as);
                Value = WorkCenterCost.getCurrentCostPrice().multiply(quantity);
                String WorkCenterBasisType = WorkCenterCost.getBasisType();
                if (WorkCenterBasisType.equals(X_M_Cost.BASISTYPE_PerBatch))
                  Value = Value.divide(new BigDecimal(this.BatchSize), this.precision, 4); 
                if (!createAccountLines(crAcct, drAcct, (DocLine)null, Value, quantity, fact, as, this.Wo.getM_Product_ID()))
                  return null; 
                if (createWOValue)
                  if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), operation.getMFG_WorkOrderOperation_ID(), this.m_costelement_id, this.Wo.getM_Product_ID(), operation.getC_UOM_ID(), Value, this.materialIn, this.materialOut, this.materialOverhdIn, this.materialOverhdOut, this.resourceIn, this.resourceOut, this.overhdIn, this.overhdOut))
                    return null;  
                if (!MMFGWorkCenterCostDetail.createWorkCenterCostDetail(WorkCenterCost, this.WoTrx.getMFG_WORKORDERTRANSACTION_ID(), Value, quantity))
                  return null; 
              } 
            } 
          }  
      } else {
        ArrayList<MMFGWorkOrderComponent> scrapComponents = new ArrayList<MMFGWorkOrderComponent>();
        ArrayList<MMFGWorkOrderResource> scrapResources = new ArrayList<MMFGWorkOrderResource>();
        StringBuffer whereClause = new StringBuffer("");
        if (fromSeqNo == toSeqNo) {
          if (fromStep.equals(X_MFG_WorkOrderTransaction.STEPFROM_Queue)) {
            whereClause.append(" SeqNo < " + toSeqNo);
          } else {
            whereClause.append(" SeqNo <= " + toSeqNo);
          } 
        } else if (fromSeqNo < toSeqNo) {
          whereClause.append(" SeqNo < " + fromSeqNo);
        } 
        whereClause.append(" AND IsOptional = 'N'");
        String orderClause = " SeqNo ";
        MMFGWorkOrderOperation[] operations = MMFGWorkOrderOperation.getOfWorkOrder(this.Wo, whereClause.toString(), orderClause);
        if (operations.length != 0)
          for (MMFGWorkOrderOperation operation : operations) {
            if (!operation.isOptional() || operation.getMFG_Operation_ID() == fromOperation || operation.getMFG_Operation_ID() == toOperation) {
              this.mfg_workcenter_id = operation.getMFG_WorkCenter_ID();
              Wc = new MMFGWorkCenter(getCtx(), this.mfg_workcenter_id, getTrxName());
              if (Wc != null)
                WorkCenterCosts = MMFGWorkCenterCost.getWorkCenterCosts(Wc, as.getM_CostType_ID(), as, this.Wo.getAD_Org_ID()); 
              for (MMFGWorkCenterCost WorkCenterCost : WorkCenterCosts) {
                this.m_costelement_id = WorkCenterCost.getM_CostElement_ID();
                this.materialIn = BigDecimal.ZERO;
                this.materialOut = BigDecimal.ZERO;
                this.materialOverhdIn = BigDecimal.ZERO;
                this.materialOverhdOut = BigDecimal.ZERO;
                this.resourceIn = BigDecimal.ZERO;
                this.resourceOut = BigDecimal.ZERO;
                this.overhdIn = BigDecimal.ZERO;
                this.overhdOut = BigDecimal.ONE;
                crAcct = getAccount(1777, as);
                drAcct = getAccount(7677, as);
                Value = WorkCenterCost.getCurrentCostPrice().multiply(quantity);
                String WorkCenterBasisType = WorkCenterCost.getBasisType();
                if (WorkCenterBasisType.equals(X_M_Cost.BASISTYPE_PerBatch))
                  Value = Value.divide(new BigDecimal(this.BatchSize), this.precision, 4); 
                if (!createAccountLines(crAcct, drAcct, (DocLine)null, Value, quantity, fact, as, this.Wo.getM_Product_ID()))
                  return null; 
                if (createWOValue)
                  if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), operation.getMFG_WorkOrderOperation_ID(), this.m_costelement_id, this.Wo.getM_Product_ID(), operation.getC_UOM_ID(), Value, this.materialIn, this.materialOut, this.materialOverhdIn, this.materialOverhdOut, this.resourceIn, this.resourceOut, this.overhdIn, this.overhdOut))
                    return null;  
                if (!MMFGWorkCenterCostDetail.createWorkCenterCostDetail(WorkCenterCost, this.WoTrx.getMFG_WORKORDERTRANSACTION_ID(), Value, quantity))
                  return null; 
              } 
              MMFGWorkOrderComponent[] components = MMFGWorkOrderComponent.getOfWorkOrderOperation(operation, null, null);
              for (MMFGWorkOrderComponent component : components)
                scrapComponents.add(component); 
              MMFGWorkOrderResource[] resources = MMFGWorkOrderResource.getofWorkOrderOperation(operation, null, null);
              for (MMFGWorkOrderResource resource : resources)
                scrapResources.add(resource); 
            } 
          }  
        int i;
        for (i = 0; i < scrapComponents.size(); i++) {
          MMFGWorkOrderComponent scrapComponent = scrapComponents.get(i);
          MProduct product = new MProduct(getCtx(), scrapComponent.getM_Product_ID(), getTrxName());
//          MCost[] costLines = MCost.get(scrapComponent.getM_AttributeSetInstance_ID(), as, 0, this.Wo.getAD_Org_ID(), product);
          MCost[] costLines = MCost.get(scrapComponent.getM_AttributeSetInstance_ID(),as,0,this.Wo.getAD_Org_ID(),product);
          BigDecimal nonPercentageCost = getNonPercentCost(costLines);
          for (MCost cost : costLines) {
            MCostElement ce = new MCostElement(getCtx(), cost.getM_CostElement_ID(), getTrxName());
            this.materialIn = BigDecimal.ZERO;
            this.materialOut = BigDecimal.ZERO;
            this.materialOverhdIn = BigDecimal.ZERO;
            this.materialOverhdOut = BigDecimal.ZERO;
            this.resourceIn = BigDecimal.ZERO;
            this.resourceOut = BigDecimal.ZERO;
            this.overhdIn = BigDecimal.ZERO;
            this.overhdOut = BigDecimal.ZERO;
            MAccount[] accts = getAccounts(ce, as, (DocLine)null, true);
            crAcct = accts[0];
            drAcct = accts[1];
            String CostBasisType = cost.getBasisType();
            Value = BigDecimal.ZERO;
            if (cost.getPercentCost() == null || cost.getPercentCost().signum() == 0) {
              Value = cost.getCurrentCostPrice().multiply(scrapComponent.getQtyRequired()).multiply(quantity);
            } else {
              Value = nonPercentageCost.multiply(cost.getPercentCost()).multiply(scrapComponent.getQtyRequired()).multiply(quantity).divide(Env.ONEHUNDRED);
            } 
            if (CostBasisType.equals(X_M_Cost.BASISTYPE_PerBatch))
              Value = Value.divide(new BigDecimal(this.BatchSize), this.precision, 4); 
            if (!createAccountLines(crAcct, drAcct, (DocLine)null, Value, scrapComponent.getQtyRequired().multiply(quantity), fact, as, cost.getM_Product_ID()))
              return null; 
            if (createWOValue)
              if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), scrapComponent.getMFG_WorkOrderOperation_ID(), ce.getM_CostElement_ID(), cost.getM_Product_ID(), scrapComponent.getC_UOM_ID(), Value, this.materialIn, this.materialOut, this.materialOverhdIn, this.materialOverhdOut, this.resourceIn, this.resourceOut, this.overhdIn, this.overhdOut))
                return null;  
          } 
        } 
        for (i = 0; i < scrapResources.size(); i++) {
          MMFGWorkOrderResource scrapResource = scrapResources.get(i);
          MProduct product = new MProduct(getCtx(), scrapResource.getM_Product_ID(), getTrxName());
          MCost[] costLines = MCost.get(0,as,0,this.Wo.getAD_Org_ID(),product);
          BigDecimal nonPercentageCost = getNonPercentCost(costLines);
          for (MCost cost : costLines) {
            MCostElement ce = new MCostElement(getCtx(), cost.getM_CostElement_ID(), getTrxName());
            this.materialIn = BigDecimal.ZERO;
            this.materialOut = BigDecimal.ZERO;
            this.materialOverhdIn = BigDecimal.ZERO;
            this.materialOverhdOut = BigDecimal.ZERO;
            this.resourceIn = BigDecimal.ZERO;
            this.resourceOut = BigDecimal.ZERO;
            this.overhdIn = BigDecimal.ZERO;
            this.overhdOut = BigDecimal.ZERO;
            MAccount[] accts = getAccounts(ce, as, (DocLine)null, true);
            crAcct = accts[0];
            drAcct = accts[1];
            String CostBasisType = cost.getBasisType();
            Value = BigDecimal.ZERO;
            if (cost.getPercentCost() == null || cost.getPercentCost().signum() == 0) {
              Value = cost.getCurrentCostPrice().multiply(scrapResource.getQtyRequired()).multiply(quantity);
            } else {
              Value = nonPercentageCost.multiply(cost.getPercentCost()).multiply(scrapResource.getQtyRequired()).multiply(quantity).divide(Env.ONEHUNDRED);
            } 
            if (CostBasisType.equals(X_M_Cost.BASISTYPE_PerBatch))
              Value = Value.divide(new BigDecimal(this.BatchSize), this.precision, 4); 
            if (!createAccountLines(crAcct, drAcct, (DocLine)null, Value, scrapResource.getQtyRequired().multiply(quantity), fact, as, cost.getM_Product_ID()))
              return null; 
            if (createWOValue)
              if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), scrapResource.getMFG_WorkOrderOperation_ID(), ce.getM_CostElement_ID(), cost.getM_Product_ID(), scrapResource.getC_UOM_ID(), Value, this.materialIn, this.materialOut, this.materialOverhdIn, this.materialOverhdOut, this.resourceIn, this.resourceOut, this.overhdIn, this.overhdOut))
                return null;  
          } 
        } 
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyCompletionToInventory) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
      for (DocLine line : this.p_lines) {
        MCost[] costLines = MCost.get(assembly.getM_AttributeSetInstance_ID(),as,0, this.Wo.getAD_Org_ID(), assembly);
        BigDecimal nonPercentageCost = getNonPercentCost(costLines);
        for (MCost cost : costLines) {
          MCostElement ce = new MCostElement(getCtx(), cost.getM_CostElement_ID(), getTrxName());
          if (!ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material) || ce.getCostingMethod() == null) {
            this.materialIn = BigDecimal.ZERO;
            this.materialOut = BigDecimal.ZERO;
            this.materialOverhdIn = BigDecimal.ZERO;
            this.materialOverhdOut = BigDecimal.ZERO;
            this.resourceIn = BigDecimal.ZERO;
            this.resourceOut = BigDecimal.ZERO;
            this.overhdIn = BigDecimal.ZERO;
            this.overhdOut = BigDecimal.ZERO;
            MAccount[] accts = getAccounts(ce, as, (DocLine)null, false);
            crAcct = accts[0];
            drAcct = accts[1];
            quantity = this.WoTrx.getQtyEntered();
            String CostBasisType = cost.getBasisType();
            Value = BigDecimal.ZERO;
            if (cost.getPercentCost() == null || cost.getPercentCost().signum() == 0) {
              Value = cost.getCurrentCostPrice().multiply(quantity);
            } else {
              Value = nonPercentageCost.multiply(cost.getPercentCost()).multiply(quantity).divide(Env.ONEHUNDRED);
            } 
            if (CostBasisType.equals(X_M_Cost.BASISTYPE_PerBatch))
              Value = Value.divide(new BigDecimal(this.BatchSize), this.precision, 4); 
            if (!createAccountLines(crAcct, drAcct, (DocLine)null, Value, quantity, fact, as, cost.getM_Product_ID()))
              return null; 
            if (createWOValue && cost.getIsThisLevel().equals("N"))
              if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), this.WoTrx.getOperationFrom_ID(), ce.getM_CostElement_ID(), cost.getM_Product_ID(), assembly.getC_UOM_ID(), Value, this.materialIn, this.materialOut, this.materialOverhdIn, this.materialOverhdOut, this.resourceIn, this.resourceOut, this.overhdIn, this.overhdOut))
                return null;  
            if (this.Wo.getM_Product_ID() != 0)
              MCostDetail.createWorkOrderTransaction(as, this.WoTrx.getAD_Org_ID(), this.WoTrx.getM_Product_ID(), this.Wo.getM_AttributeSetInstance_ID(), line.get_ID(), ce.getM_CostElement_ID(), Value, quantity, this.WoTrx.getDescription(), false, getTrxName()); 
          } 
        } 
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage)) {
      for (DocLine line : this.p_lines) {
        MCost[] costLines = MCost.get(line.getM_AttributeSetInstance_ID(),as,0, line.getAD_Org_ID(),line.getProduct());
        String BasisType = line.getBasisType(); 
        BigDecimal nonPercentageCost = getNonPercentCost(costLines);
        for (MCost cost : costLines) {
          MCostElement ce = new MCostElement(getCtx(), cost.getM_CostElement_ID(), getTrxName());
          MProduct p = new MProduct(getCtx(), line.getM_Product_ID(), getTrxName());
          if (!p.isManufactured() || !ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material) || ce.getCostingMethod() == null || !p.isBOM()) {
            this.materialIn = BigDecimal.ZERO;
            this.materialOut = BigDecimal.ZERO;
            this.materialOverhdIn = BigDecimal.ZERO;
            this.materialOverhdOut = BigDecimal.ZERO;
            this.resourceIn = BigDecimal.ZERO;
            this.resourceOut = BigDecimal.ZERO;
            this.overhdIn = BigDecimal.ZERO;
            this.overhdOut = BigDecimal.ZERO;
            MAccount[] accts = getAccounts(ce, as, line, false);
            crAcct = accts[0];
            drAcct = accts[1];
            quantity = line.getQty();
            if (BasisType.equals(X_M_Cost.BASISTYPE_PerBatch))
              quantity = quantity.divide(new BigDecimal(this.BatchSize), this.precision, 4); 
            String CostBasisType = cost.getBasisType();
            Value = BigDecimal.ZERO;
            if (cost.getPercentCost() == null || cost.getPercentCost().signum() == 0) {
              Value = cost.getCurrentCostPrice().multiply(quantity);
            } else {
              Value = nonPercentageCost.multiply(cost.getPercentCost()).multiply(quantity).divide(Env.ONEHUNDRED);
            } 
            if (CostBasisType.equals(X_M_Cost.BASISTYPE_PerBatch)) {
              MProduct pr = new MProduct(getCtx(), line.getM_Product_ID(), null);
              Value = Value.divide(new BigDecimal(pr.getBatchSize()), this.precision, 4);
            } 
            if (!createAccountLines(crAcct, drAcct, line, Value, quantity, fact, as, line.getM_Product_ID()))
              return null; 
            if (createWOValue)
              if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), line.getMFG_WorkOrderOperation_ID(), ce.getM_CostElement_ID(), line.getM_Product_ID(), line.getC_UOM_ID(), Value, this.materialIn, this.materialOut, this.materialOverhdIn, this.materialOverhdOut, this.resourceIn, this.resourceOut, this.overhdIn, this.overhdOut))
                return null;  
            if (line.getM_Product_ID() != 0 && !this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage)) {
              MCostDetail.createWorkOrderTransaction(as, line.getAD_Org_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), line.get_ID(), ce.getM_CostElement_ID(), Value, quantity, line.getDescription(), false, getTrxName());
            } else if (line.getM_Product_ID() != 0 && this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage)) {
              MCostDetail.createWorkOrderResourceTransaction(as, line.getAD_Org_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), line.get_ID(), ce.getM_CostElement_ID(), Value, quantity, line.getDescription(), false, getTrxName());
            } 
          } 
        } 
      } 
    } 
    facts.add(fact);
    return facts;
  }
  
  private boolean populateWorkOrderValue(int C_AcctSchema_ID, int MFG_WorkOrderOperation_ID, int M_CostElement_ID, int M_Product_ID, int C_UOM_ID, BigDecimal Value, BigDecimal MaterialIn, BigDecimal MaterialOut, BigDecimal MaterialOverhdIn, BigDecimal MaterialOverhdOut, BigDecimal ResourceIn, BigDecimal ResourceOut, BigDecimal OverhdIn, BigDecimal OverhdOut) {
    MMFGWorkOrderValue wov = MMFGWorkOrderValue.getWorkOrderValue(getCtx(), this.Wo.getMFG_WorkOrder_ID(), C_AcctSchema_ID, getTrxName());
    if (wov == null) {
      wov = new MMFGWorkOrderValue(getCtx(), 0, getTrxName());
      wov.setAD_Client_ID(getAD_Client_ID());
      wov.setAD_Org_ID(getAD_Org_ID());
      wov.setAD_OrgTrx_ID(getAD_OrgTrx_ID());
      wov.setC_Activity_ID(getC_Activity_ID());
      wov.setC_BPartner_ID(getC_BPartner_ID());
      wov.setC_Campaign_ID(getC_Campaign_ID());
      wov.setC_Project_ID(getC_Activity_ID());
      wov.setC_BPartner_Location_ID(getC_BPartner_Location_ID());
      wov.setC_AcctSchema_ID(C_AcctSchema_ID);
      wov.setMFG_WorkOrder_ID(this.Wo.getMFG_WorkOrder_ID());
      wov.setM_BOM_ID(this.Wo.getM_BOM_ID());
      wov.setMFG_Routing_ID(this.Wo.getMFG_Routing_ID());
      wov.setM_Product_ID(this.Wo.getM_Product_ID());
      wov.setC_Currency_ID(getC_Currency_ID());
      wov.setMATERIALIN(BigDecimal.ZERO);
      wov.setMATERIALOUT(BigDecimal.ZERO);
      wov.setMATERIALOVERHDIN(BigDecimal.ZERO);
      wov.setMATERIALOVERHDOUT(BigDecimal.ZERO);
      wov.setRESOURCEIN(BigDecimal.ZERO);
      wov.setRESOURCEOUT(BigDecimal.ZERO);
      wov.setOVERHDIN(BigDecimal.ZERO);
      wov.setOVERHDOUT(BigDecimal.ZERO);
      wov.setIsActive(true);
      wov.setMATERIALVARIANCE(BigDecimal.ZERO);
      wov.setMATERIALOVERHDVARIANCE(BigDecimal.ZERO);
      wov.setOVERHDVARIANCE(BigDecimal.ZERO);
      wov.setRESOURCEVARIANCE(BigDecimal.ZERO);
      if (!wov.save(getTrxName())) {
        this.p_Error = "Error in creating work order value: ";
        this.log.log(Level.WARNING, this.p_Error);
        return false;
      } 
    } 
    MMFGWorkOrderValueDetail wovd = new MMFGWorkOrderValueDetail(getCtx(), 0, getTrxName());
    BigDecimal ScrapValue = BigDecimal.ZERO;
    MaterialIn = MaterialIn.multiply(Value);
    MaterialOut = MaterialOut.multiply(Value);
    MaterialOverhdIn = MaterialOverhdIn.multiply(Value);
    MaterialOverhdOut = MaterialOverhdOut.multiply(Value);
    ResourceIn = ResourceIn.multiply(Value);
    ResourceOut = ResourceOut.multiply(Value);
    OverhdIn = OverhdIn.multiply(Value);
    OverhdOut = OverhdOut.multiply(Value);
    wovd.setAD_Client_ID(getAD_Client_ID());
    wovd.setAD_Org_ID(getAD_Org_ID());
    wovd.setAD_OrgTrx_ID(getAD_OrgTrx_ID());
    wovd.setC_AcctSchema_ID(C_AcctSchema_ID);
    wovd.setMFG_WorkOrderTransaction_ID(this.WoTrx.getMFG_WORKORDERTRANSACTION_ID());
    wovd.setMFG_WorkOrderOperation_ID(MFG_WorkOrderOperation_ID);
    wovd.setC_Period_ID(getC_Period_ID());
    wovd.setM_CostElement_ID(M_CostElement_ID);
    wovd.setC_UOM_ID(C_UOM_ID);
    wovd.setM_Product_ID(M_Product_ID);
    if (this.WoTrx.getWorkOrderTxnType().equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_WorkOrderMove) && this.WoTrx.getStepTo().equals(X_MFG_WorkOrderTransaction.STEPTO_Scrap)) {
      ScrapValue = MaterialOut.add(MaterialOverhdOut).add(ResourceOut).add(OverhdOut);
      wovd.setSCRAPVALUE(ScrapValue);
    } 
    wovd.setMATERIALIN(MaterialIn);
    wovd.setMATERIALOUT(MaterialOut);
    wovd.setMATERIALOVERHDIN(MaterialOverhdIn);
    wovd.setMATERIALOVERHDOUT(MaterialOverhdOut);
    wovd.setRESOURCEIN(ResourceIn);
    wovd.setRESOURCEOUT(ResourceOut);
    wovd.setOVERHDIN(OverhdIn);
    wovd.setOVERHDOUT(OverhdOut);
    wovd.setIsActive(true);
    wovd.setMFG_WorkOrderValue_ID(wov.getMFG_WorkOrderValue_ID());
    if (!wovd.save(getTrxName())) {
      this.p_Error = "Error in creating work order value Detail: ";
      this.log.log(Level.WARNING, this.p_Error);
      return false;
    } 
    wov.setMATERIALIN(wov.getMATERIALIN().add(MaterialIn));
    wov.setMATERIALOUT(wov.getMATERIALOUT().add(MaterialOut));
    wov.setMATERIALOVERHDIN(wov.getMATERIALOVERHDIN().add(MaterialOverhdIn));
    wov.setMATERIALOVERHDOUT(wov.getMATERIALOVERHDOUT().add(MaterialOverhdOut));
    wov.setRESOURCEIN(wov.getRESOURCEIN().add(ResourceIn));
    wov.setRESOURCEOUT(wov.getRESOURCEOUT().add(ResourceOut));
    wov.setOVERHDIN(wov.getOVERHDIN().add(OverhdIn));
    wov.setOVERHDOUT(wov.getOVERHDOUT().add(OverhdOut));
    wov.setSCRAPVALUE(wov.getSCRAPVALUE().add(ScrapValue));
    if (!wov.save(getTrxName())) {
      this.p_Error = "Error in creating work order value: ";
      this.log.log(Level.WARNING, this.p_Error);
      return false;
    } 
    return true;
  }
  
  private void deleteWorkOrderValue(MAcctSchema as) {
    MMFGWorkOrderValueDetail[] valss = MMFGWorkOrderValueDetail.getofWOTxn(getCtx(), this.WoTrx, as.getC_AcctSchema_ID(), getTrxName());
    MMFGWorkOrderValue wov = MMFGWorkOrderValue.getWorkOrderValue(getCtx(), this.Wo.getMFG_WorkOrder_ID(), as.getC_AcctSchema_ID(), getTrxName());
    if (valss == null)
      return; 
    for (MMFGWorkOrderValueDetail vals : valss) {
      if (wov != null) {
        wov.setMATERIALIN(wov.getMATERIALIN().subtract(vals.getMATERIALIN()));
        wov.setMATERIALOUT(wov.getMATERIALOUT().subtract(vals.getMATERIALOUT()));
        wov.setMATERIALOVERHDIN(wov.getMATERIALOVERHDIN().subtract(vals.getMATERIALOVERHDIN()));
        wov.setMATERIALOVERHDOUT(wov.getMATERIALOVERHDOUT().subtract(vals.getMATERIALOVERHDOUT()));
        wov.setRESOURCEIN(wov.getRESOURCEIN().subtract(vals.getRESOURCEIN()));
        wov.setRESOURCEOUT(wov.getRESOURCEOUT().subtract(vals.getRESOURCEOUT()));
        wov.setOVERHDIN(wov.getOVERHDIN().subtract(vals.getOVERHDIN()));
        wov.setOVERHDOUT(wov.getOVERHDOUT().subtract(vals.getOVERHDOUT()));
        wov.setSCRAPVALUE(wov.getSCRAPVALUE().subtract(vals.getSCRAPVALUE()));
        wov.save(getTrxName());
      } 
      vals.delete(true, getTrxName());
    } 
  }
  
  private MAccount[] getAccounts(MCostElement ce, MAcctSchema as, DocLine line, boolean isScrap) {
    MAccount crAcct = null;
    MAccount drAcct = null;
    MProduct assembly = new MProduct(getCtx(), this.WoTrx.getM_Product_ID(), null);
    ProductCost pc = new ProductCost(getCtx(), assembly.getM_Product_ID(), assembly.getM_AttributeSetInstance_ID(), getTrxName());
    MAcctSchemaDefault schema = MAcctSchemaDefault.get(getCtx(), as.getC_AcctSchema_ID());
    if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentIssueToWorkOrder) || this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage)) {
      if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
        crAcct = line.getAccount(3, as);
        drAcct = getAccount(1773, as);
        this.materialIn = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
        crAcct = line.getAccount(12, as);
        drAcct = getAccount(1774, as);
        this.materialOverhdIn = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
        crAcct = line.getAccount(11, as);
        drAcct = getAccount(1775, as);
        this.resourceIn = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
        if (this.mfg_workcenter_id != -1) {
          crAcct = getAccount(7678, as);
          drAcct = getAccount(1777, as);
        } 
        if (crAcct == null)
          crAcct = MAccount.get(getCtx(), schema.getOverhead_Absorption_Acct()); 
        if (drAcct == null)
          drAcct = MAccount.get(getCtx(), schema.getP_Overhead_Acct()); 
        this.overhdIn = BigDecimal.ONE;
      } else {
        this.p_Error = "Not a Valid Cost Element " + line;
        this.log.log(Level.WARNING, this.p_Error);
        return null;
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ComponentReturnFromWorkOrder)) {
      if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
        drAcct = line.getAccount(3, as);
        crAcct = getAccount(1773, as);
        this.materialOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
        drAcct = line.getAccount(12, as);
        crAcct = getAccount(1774, as);
        this.materialOverhdOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
        drAcct = line.getAccount(11, as);
        crAcct = getAccount(1775, as);
        this.resourceOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
        if (this.mfg_workcenter_id != -1) {
          drAcct = getAccount(7678, as);
          crAcct = getAccount(1777, as);
        } 
        if (drAcct == null)
          drAcct = MAccount.get(getCtx(), schema.getOverhead_Absorption_Acct()); 
        if (crAcct == null)
          crAcct = MAccount.get(getCtx(), schema.getP_Overhead_Acct()); 
        this.overhdOut = BigDecimal.ONE;
      } else {
        this.p_Error = "Not a Valid Cost Element " + line;
        this.log.log(Level.WARNING, this.p_Error);
        return null;
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyCompletionToInventory)) {
      if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
        drAcct = pc.getAccount(3, as);
        crAcct = getAccount(1773, as);
        this.materialOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
        drAcct = pc.getAccount(12, as);
        crAcct = getAccount(1774, as);
        this.materialOverhdOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
        drAcct = pc.getAccount(11, as);
        crAcct = getAccount(1775, as);
        this.resourceOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
        if (this.mfg_workcenter_id != -1) {
          drAcct = getAccount(7678, as);
          crAcct = getAccount(1777, as);
        } 
        if (drAcct == null)
          drAcct = MAccount.get(getCtx(), schema.getOverhead_Absorption_Acct()); 
        if (crAcct == null)
          crAcct = MAccount.get(getCtx(), schema.getP_Overhead_Acct()); 
        this.overhdOut = BigDecimal.ONE;
      } else {
        this.p_Error = "Not a Valid Cost Element ";
        this.log.log(Level.WARNING, this.p_Error);
        return null;
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_AssemblyReturnFromInventory)) {
      if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
        crAcct = pc.getAccount(3, as);
        drAcct = getAccount(1773, as);
        this.materialIn = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
        crAcct = pc.getAccount(12, as);
        drAcct = getAccount(1774, as);
        this.materialOverhdIn = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
        crAcct = pc.getAccount(11, as);
        drAcct = getAccount(1775, as);
        this.resourceIn = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
        if (this.mfg_workcenter_id != -1) {
          crAcct = getAccount(7678, as);
          drAcct = getAccount(1777, as);
        } 
        if (crAcct == null)
          crAcct = MAccount.get(getCtx(), schema.getOverhead_Absorption_Acct()); 
        if (drAcct == null)
          drAcct = MAccount.get(getCtx(), schema.getP_Overhead_Acct()); 
        this.overhdIn = BigDecimal.ONE;
      } else {
        this.p_Error = "Not a Valid Cost Element ";
        this.log.log(Level.WARNING, this.p_Error);
        return null;
      } 
    } else if (this.DocTxnType.equals(X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_WorkOrderMove) && isScrap) {
      if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
        drAcct = getAccount(7677, as);
        crAcct = getAccount(1773, as);
        this.materialOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
        drAcct = getAccount(7677, as);
        crAcct = getAccount(1774, as);
        this.materialOverhdOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
        drAcct = getAccount(7677, as);
        crAcct = getAccount(1775, as);
        this.resourceOut = BigDecimal.ONE;
      } else if (ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
        drAcct = getAccount(7677, as);
        crAcct = getAccount(1777, as);
        this.overhdOut = BigDecimal.ONE;
      } else {
        this.p_Error = "Not a Valid Cost Element ";
        this.log.log(Level.WARNING, this.p_Error);
        return null;
      } 
    } 
    MAccount[] retVal = new MAccount[2];
    retVal[0] = crAcct;
    retVal[1] = drAcct;
    return retVal;
  }
  
  private BigDecimal getNonPercentCost(MCost[] costLines) {
    if (costLines.length == 0)
      return BigDecimal.ZERO; 
    BigDecimal nonPercentageCost = BigDecimal.ZERO;
    MProduct prod = new MProduct(getCtx(), costLines[0].getM_Product_ID(), getTrxName());
    for (MCost cost : costLines) {
      MCostElement ce = new MCostElement(getCtx(), cost.getM_CostElement_ID(), getTrxName());
      if (!prod.isManufactured() || !ce.getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material) || ce.getCostingMethod() == null)
        if (cost.getPercentCost() == null || cost.getPercentCost().signum() == 0)
          if (cost.getBasisType().equals(X_M_Cost.BASISTYPE_PerBatch)) {
            nonPercentageCost = nonPercentageCost.add(cost.getCurrentCostPrice().divide(new BigDecimal(prod.getBatchSize()), this.precision, 4));
          } else {
            nonPercentageCost = nonPercentageCost.add(cost.getCurrentCostPrice());
          }   
    } 
    return nonPercentageCost;
  }
  
  private boolean createAccountLines(MAccount crAcct, MAccount drAcct, DocLine line, BigDecimal Value, BigDecimal quantity, Fact fact, MAcctSchema as, int M_Product_ID) {
    if (Value.compareTo(BigDecimal.ZERO) == 0)
      return true; 
    FactLine dr = null;
    FactLine cr = null;
    dr = fact.createLine(line, drAcct, as.getC_Currency_ID(), Value, null);
    if (dr == null) {
      this.p_Error = "FactLine DR not created: ";
      this.log.log(Level.WARNING, this.p_Error);
      return false;
    } 
    dr.setAD_Org_ID(this.Wo.getAD_Org_ID());
    dr.setQty(quantity.negate());
    cr = fact.createLine(line, crAcct, as.getC_Currency_ID(), null, Value);
    if (cr == null) {
      this.p_Error = "FactLine CR not created: ";
      this.log.log(Level.WARNING, this.p_Error);
      return false;
    } 
    if (line == null) {
      dr.setM_Product_ID(M_Product_ID);
      cr.setM_Product_ID(M_Product_ID);
    } 
    return true;
  }
}

