package org.cyprus.mfg.acct;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.model.MMFGWorkOrderValue;
import org.cyprus.mfg.model.MMFGWorkOrderValueDetail;
import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mfg.model.X_MFG_WorkOrderTransaction;
import org.cyprusbrs.acct.Doc;
import org.cyprusbrs.acct.Fact;
import org.cyprusbrs.acct.FactLine;
import org.cyprusbrs.framework.MAccount;
import org.cyprusbrs.framework.MCostElement;
import org.cyprusbrs.framework.X_M_Cost;
import org.cyprusbrs.framework.X_M_CostElement;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MAcctSchemaDefault;
import org.cyprusbrs.util.Env;

public class Doc_WorkOrder extends Doc {
  private MMFGWorkOrder m_workorder;
  
  public Doc_WorkOrder(MAcctSchema[] ass, ResultSet rs, String trx) {
    super(ass, MMFGWorkOrder.class, rs, null, trx);
    this.m_workorder = null;
  }
  
  public String loadDocumentDetails() {
    this.m_workorder = (MMFGWorkOrder)getPO();
    MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
    MMFGWorkOrderTransaction[] unProcessed = unprocessedWoTxn();
    for (MMFGWorkOrderTransaction txn : unProcessed) {
      String retVal = Doc.postImmediate(ass, txn.get_Table_ID(), txn.getMFG_WORKORDERTRANSACTION_ID(), false, getTrxName());
      if (retVal != null && retVal.length() != 0)
        return "Unable to post unprocessed work order transaction for the work order"; 
    } 
    setDateDoc(this.m_workorder.getDateAcct());
    setDateAcct(this.m_workorder.getDateAcct());
    setC_Currency_ID(-2);
    return null;
  }
  
  public BigDecimal getBalance() {
    BigDecimal retValue = Env.ZERO;
    return retValue;
  }
  
  public ArrayList<Fact> createFacts(MAcctSchema as) {
    MAccount crAcct = null;
    MAccount drAcct = null;
    BigDecimal ValueIn = BigDecimal.ZERO;
    BigDecimal ValueOut = BigDecimal.ZERO;
    BigDecimal Variance = BigDecimal.ZERO;
    BigDecimal materialVariance = BigDecimal.ZERO;
    BigDecimal materialOvhdVariance = BigDecimal.ZERO;
    BigDecimal resourceVariance = BigDecimal.ZERO;
    BigDecimal overhdVariance = BigDecimal.ZERO;
    ArrayList<Fact> facts = new ArrayList<Fact>();
    if (!as.getCostingMethod().equals(X_M_CostElement.COSTINGMETHOD_StandardCosting))
      return facts; 
    boolean createWOValue = this.m_workorder.getWOType().equals(X_MFG_WorkOrder.WOTYPE_Standard);
    MMFGWorkOrderValue wov = MMFGWorkOrderValue.getWorkOrderValue(getCtx(), this.m_workorder.getMFG_WorkOrder_ID(), as.getC_AcctSchema_ID(), getTrxName());
    if (wov == null)
      return facts; 
    MMFGWorkOrderValueDetail[] vals = MMFGWorkOrderValueDetail.getofWO(getCtx(), wov, as.getC_AcctSchema_ID(), getTrxName());
   // if (this.m_repost) {
      if (vals != null)
        for (MMFGWorkOrderValueDetail val : vals) {
          if (val.getMFG_WorkOrderTransaction_ID() == 0)
            val.delete(true, getTrxName()); 
        }  
      wov.setMATERIALVARIANCE(BigDecimal.ZERO);
      wov.setMATERIALOVERHDVARIANCE(BigDecimal.ZERO);
      wov.setOVERHDVARIANCE(BigDecimal.ZERO);
      wov.setRESOURCEVARIANCE(BigDecimal.ZERO);
      if (!wov.save(getTrxName())) {
        this.p_Error = "Error in saving work order value: ";
        this.log.log(Level.WARNING, this.p_Error);
        return facts;
      } 
  //  } 
    int C_UOM_ID = 0;
    Fact fact = new Fact(this, as, Fact.POST_Actual);
    setC_Currency_ID(as.getC_Currency_ID());
    int mfgCostElement = MCostElement.getMfgCostElement(getAD_Client_ID());
    if (mfgCostElement == 0) {
      this.p_Error = "Mfg Material Cost Element not defined";
      this.log.log(Level.WARNING, this.p_Error);
      return facts;
    } 
    MCostElement StandardCostElement = MCostElement.getMaterialCostElement(getCtx(), X_M_Cost.COSTINGMETHOD_StandardCosting);
    if (mfgCostElement != 0 && StandardCostElement.getM_CostElement_ID() != 0) {
      materialVariance = BigDecimal.ZERO;
      materialOvhdVariance = BigDecimal.ZERO;
      resourceVariance = BigDecimal.ZERO;
      overhdVariance = BigDecimal.ZERO;
      Variance = BigDecimal.ZERO;
      ValueIn = BigDecimal.ZERO;
      ValueOut = BigDecimal.ZERO;
      for (MMFGWorkOrderValueDetail val : vals) {
        if (val.getM_CostElement_ID() == mfgCostElement || val.getM_CostElement_ID() == StandardCostElement.getM_CostElement_ID()) {
          ValueIn = ValueIn.add(val.getMATERIALIN());
          ValueOut = ValueOut.add(val.getMATERIALOUT());
          C_UOM_ID = val.getC_UOM_ID();
        } 
      } 
      Variance = ValueIn.subtract(ValueOut);
      if (Variance.compareTo(BigDecimal.ZERO) != 0) {
        crAcct = getAccount(1773, as);
        drAcct = getAccount(7672, as);
        materialVariance = BigDecimal.ONE;
        if (drAcct == null) {
          this.p_Error = "@NotFound@ Variance Account";
          this.log.log(Level.SEVERE, this.p_Error);
          return null;
        } 
        if (crAcct == null) {
          this.p_Error = "@NotFound@ Work Order Account";
          this.log.log(Level.SEVERE, this.p_Error);
          return null;
        } 
        FactLine dr1 = null;
        FactLine cr1 = null;
        cr1 = fact.createLine(null, crAcct, getC_Currency_ID(), null, Variance);
        if (cr1 == null) {
          this.p_Error = "FactLine DR not created: ";
          this.log.log(Level.WARNING, this.p_Error);
          return null;
        } 
        dr1 = fact.createLine(null, drAcct, getC_Currency_ID(), Variance, null);
        if (dr1 == null) {
          this.p_Error = "FactLine DR not created: ";
          this.log.log(Level.WARNING, this.p_Error);
          return null;
        } 
        if (createWOValue)
          if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), 0, mfgCostElement, C_UOM_ID, Variance, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, materialVariance, materialOvhdVariance, resourceVariance, overhdVariance)) {
            this.p_Error = "Error in creating work order value: ";
            this.log.log(Level.WARNING, this.p_Error);
            return null;
          }  
      } 
    } 
    MCostElement[] costElements = getDistinctCostElements(vals);
    for (int i = 0; i < costElements.length; i++) {
      materialVariance = BigDecimal.ZERO;
      materialOvhdVariance = BigDecimal.ZERO;
      resourceVariance = BigDecimal.ZERO;
      overhdVariance = BigDecimal.ZERO;
      Variance = BigDecimal.ZERO;
      ValueIn = BigDecimal.ZERO;
      ValueOut = BigDecimal.ZERO;
      crAcct = null;
      drAcct = null;
      for (MMFGWorkOrderValueDetail val : vals) {
        if (val.getM_CostElement_ID() == costElements[i].getM_CostElement_ID() && val.getM_CostElement_ID() != mfgCostElement && val.getM_CostElement_ID() != StandardCostElement.getM_CostElement_ID()) {
          if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
            ValueIn = ValueIn.add(val.getMATERIALIN());
            ValueOut = ValueOut.add(val.getMATERIALOUT());
            C_UOM_ID = val.getC_UOM_ID();
          } 
          if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
            ValueIn = ValueIn.add(val.getMATERIALOVERHDIN());
            ValueOut = ValueOut.add(val.getMATERIALOVERHDOUT());
            C_UOM_ID = val.getC_UOM_ID();
          } 
          if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
            ValueIn = ValueIn.add(val.getRESOURCEIN());
            ValueOut = ValueOut.add(val.getRESOURCEOUT());
            C_UOM_ID = val.getC_UOM_ID();
          } 
          if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
            ValueIn = ValueIn.add(val.getOVERHDIN());
            ValueOut = ValueOut.add(val.getOVERHDOUT());
            C_UOM_ID = val.getC_UOM_ID();
          } 
        } 
      } 
      Variance = ValueIn.subtract(ValueOut);
      if (Variance.compareTo(BigDecimal.ZERO) != 0) {
        if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Material)) {
          crAcct = getAccount(1773, as);
          drAcct = getAccount(7672, as);
          materialVariance = BigDecimal.ONE;
        } 
        if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_BurdenMOverhead)) {
          crAcct = getAccount(1774, as);
          drAcct = getAccount(7673, as);
          materialOvhdVariance = BigDecimal.ONE;
        } 
        if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
          crAcct = getAccount(1775, as);
          drAcct = getAccount(7674, as);
          resourceVariance = BigDecimal.ONE;
        } 
        if (costElements[i].getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
          if (crAcct == null) {
            MAcctSchemaDefault schema = new MAcctSchemaDefault(getCtx(), as.getC_AcctSchema_ID(), getTrxName());
//            crAcct = MAccount.get(getCtx(), schema.getWC_Overhead_Acct());
            crAcct = MAccount.get(getCtx(), schema.getP_Overhead_Acct());

          } 
          drAcct = getAccount(7675, as);
          overhdVariance = BigDecimal.ONE;
        } 
        if (drAcct == null) {
          this.p_Error = "@NotFound@ Variance Account";
          this.log.log(Level.SEVERE, this.p_Error);
          return null;
        } 
        if (crAcct == null) {
          this.p_Error = "@NotFound@ Work Order Account";
          this.log.log(Level.SEVERE, this.p_Error);
          return null;
        } 
        FactLine dr = null;
        FactLine cr = null;
        cr = fact.createLine(null, crAcct, getC_Currency_ID(), null, Variance);
        if (cr == null) {
          this.p_Error = "FactLine DR not created: ";
          this.log.log(Level.WARNING, this.p_Error);
          return null;
        } 
        dr = fact.createLine(null, drAcct, getC_Currency_ID(), Variance, null);
        if (dr == null) {
          this.p_Error = "FactLine DR not created: ";
          this.log.log(Level.WARNING, this.p_Error);
          return null;
        } 
        if (createWOValue)
          if (!populateWorkOrderValue(as.getC_AcctSchema_ID(), 0, costElements[i].getM_CostElement_ID(), C_UOM_ID, Variance, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, materialVariance, materialOvhdVariance, resourceVariance, overhdVariance)) {
            this.p_Error = "Error in creating work order value: ";
            this.log.log(Level.WARNING, this.p_Error);
            return null;
          }  
      } 
    } 
    facts.add(fact);
    return facts;
  }
  
  private MCostElement[] getDistinctCostElements(MMFGWorkOrderValueDetail[] vals) {
    ArrayList<MCostElement> elements = new ArrayList<MCostElement>();
    boolean exists = false;
    for (MMFGWorkOrderValueDetail val : vals) {
      exists = false;
      for (int j = 0; j < elements.size(); j++) {
        MCostElement ce = elements.get(j);
        if (val.getM_CostElement_ID() == ce.getM_CostElement_ID()) {
          exists = true;
          break;
        } 
      } 
      if (!exists) {
        MCostElement newCostElement = MCostElement.get(getCtx(), val.getM_CostElement_ID());
        elements.add(newCostElement);
      } 
    } 
    MCostElement[] retVal = new MCostElement[elements.size()];
    elements.toArray(retVal);
    return retVal;
  }
  
  private boolean populateWorkOrderValue(int C_AcctSchema_ID, int MFG_WorkOrderOperation_ID, int M_CostElement_ID, int C_UOM_ID, BigDecimal Value, BigDecimal MaterialIn, BigDecimal MaterialOut, BigDecimal MaterialOverhdIn, BigDecimal MaterialOverhdOut, BigDecimal ResourceIn, BigDecimal ResourceOut, BigDecimal OverhdIn, BigDecimal OverhdOut, BigDecimal MaterialVariance, BigDecimal MaterialOverhdVariance, BigDecimal ResourceVariance, BigDecimal OverhdVariance) {
    MMFGWorkOrderValue wov = MMFGWorkOrderValue.getWorkOrderValue(getCtx(), this.m_workorder.getMFG_WorkOrder_ID(), C_AcctSchema_ID, getTrxName());
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
      wov.setMFG_WorkOrder_ID(this.m_workorder.getMFG_WorkOrder_ID());
      wov.setM_BOM_ID(this.m_workorder.getM_BOM_ID());
      wov.setMFG_Routing_ID(this.m_workorder.getMFG_Routing_ID());
      wov.setM_Product_ID(this.m_workorder.getM_Product_ID());
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
    MaterialIn = MaterialIn.multiply(Value);
    MaterialOut = MaterialOut.multiply(Value);
    MaterialOverhdIn = MaterialOverhdIn.multiply(Value);
    MaterialOverhdOut = MaterialOverhdOut.multiply(Value);
    ResourceIn = ResourceIn.multiply(Value);
    ResourceOut = ResourceOut.multiply(Value);
    OverhdIn = OverhdIn.multiply(Value);
    OverhdOut = OverhdOut.multiply(Value);
    MaterialVariance = MaterialVariance.multiply(Value);
    MaterialOverhdVariance = MaterialOverhdVariance.multiply(Value);
    ResourceVariance = ResourceVariance.multiply(Value);
    OverhdVariance = OverhdVariance.multiply(Value);
    wovd.setAD_Client_ID(getAD_Client_ID());
    wovd.setAD_Org_ID(getAD_Org_ID());
    wovd.setAD_OrgTrx_ID(getAD_OrgTrx_ID());
    wovd.setC_AcctSchema_ID(C_AcctSchema_ID);
    wovd.setMFG_WorkOrderOperation_ID(MFG_WorkOrderOperation_ID);
    wovd.setC_Period_ID(getC_Period_ID());
    wovd.setM_CostElement_ID(M_CostElement_ID);
    wovd.setC_UOM_ID(C_UOM_ID);
    wovd.setM_Product_ID(this.m_workorder.getM_Product_ID());
    wovd.setMATERIALIN(MaterialIn);
    wovd.setMATERIALOUT(MaterialOut);
    wovd.setMATERIALOVERHDIN(MaterialOverhdIn);
    wovd.setMATERIALOVERHDOUT(MaterialOverhdOut);
    wovd.setRESOURCEIN(ResourceIn);
    wovd.setRESOURCEOUT(ResourceOut);
    wovd.setOVERHDIN(OverhdIn);
    wovd.setOVERHDOUT(OverhdOut);
    wovd.setMATERIALVARIANCE(MaterialVariance);
    wovd.setMATERIALOVERHDVARIANCE(MaterialOverhdVariance);
    wovd.setRESOURCEVARIANCE(ResourceVariance);
    wovd.setOVERHDVARIANCE(OverhdVariance);
    wovd.setIsActive(true);
    wovd.setMFG_WorkOrderValue_ID(wov.getMFG_WorkOrderValue_ID());
    if (!wovd.save(getTrxName())) {
      this.p_Error = "Error in creating work order value Detail: ";
      this.log.log(Level.WARNING, this.p_Error);
      return false;
    } 
    wov.setMATERIALVARIANCE(wov.getMATERIALVARIANCE().add(MaterialVariance));
    wov.setMATERIALOVERHDVARIANCE(wov.getMATERIALOVERHDVARIANCE().add(MaterialOverhdVariance));
    wov.setRESOURCEVARIANCE(wov.getRESOURCEVARIANCE().add(ResourceVariance));
    wov.setOVERHDVARIANCE(wov.getOVERHDVARIANCE().add(OverhdVariance));
    if (!wov.save(getTrxName())) {
      this.p_Error = "Error in creating work order value: ";
      this.log.log(Level.WARNING, this.p_Error);
      return false;
    } 
    return true;
  }
  
  private MMFGWorkOrderTransaction[] unprocessedWoTxn() {
    ArrayList<MMFGWorkOrderTransaction> txns = new ArrayList<MMFGWorkOrderTransaction>();
    MMFGWorkOrderTransaction[] woTxns = MMFGWorkOrderTransaction.getOfWorkOrder(this.m_workorder, " Posted = 'N' ", null);
    for (MMFGWorkOrderTransaction element : woTxns) {
      String status = element.getDocStatus();
      if (X_MFG_WorkOrderTransaction.DOCSTATUS_Reversed.equals(status) || X_MFG_WorkOrderTransaction.DOCSTATUS_Voided.equals(status) || X_MFG_WorkOrderTransaction.DOCSTATUS_Closed.equals(status) || X_MFG_WorkOrderTransaction.DOCSTATUS_Completed.equals(status))
        txns.add(element); 
    } 
    MMFGWorkOrderTransaction[] unProcessed = new MMFGWorkOrderTransaction[txns.size()];
    txns.toArray(unProcessed);
    return unProcessed;
  }
}

