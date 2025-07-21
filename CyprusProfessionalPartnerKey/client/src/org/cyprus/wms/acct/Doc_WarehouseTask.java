package org.cyprus.wms.acct;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.cyprus.wms.model.MWMSWarehouseTask;
import org.cyprusbrs.acct.Doc;
import org.cyprusbrs.acct.Fact;
import org.cyprusbrs.acct.FactLine;
//import org.cyprus.wms.model.MWarehouseTask;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.util.Env;

public class Doc_WarehouseTask extends Doc {
  private MWMSWarehouseTask m_task;
  
  private ProductCost m_productCost;
  
  public Doc_WarehouseTask(MAcctSchema[] ass, ResultSet rs, String trx) {
    super(ass, MWMSWarehouseTask.class, rs, null, trx);
    this.m_task = null;
    this.m_productCost = null;
  }
  
  public String loadDocumentDetails() {
    setC_Currency_ID(-2);
    this.m_task = (MWMSWarehouseTask)getPO();
    this.m_productCost = new ProductCost(this.m_task.getCtx(), this.m_task.getM_Product_ID(), this.m_task.getM_ActualASI_ID(), this.m_task.get_TrxName());
    this.m_productCost.setQty(this.m_task.getMovementQty());
    setDateDoc(this.m_task.getMovementDate());
    setDateAcct(this.m_task.getMovementDate());
    return null;
  }
  
  public BigDecimal getBalance() {
    BigDecimal retValue = Env.ZERO;
    return retValue;
  }
  
  public ArrayList<Fact> createFacts(MAcctSchema as) {
    Fact fact = new Fact(this, as, Fact.POST_Actual);
    setC_Currency_ID(as.getC_Currency_ID());
    FactLine dr = null;
    FactLine cr = null;
    BigDecimal costs = this.m_productCost.getProductCosts(as, this.m_task.getAD_Org_ID(), null, 0, false);
    if (costs == null)
      costs = Env.ZERO; 
    dr = fact.createLine(null, this.m_productCost.getAccount(3, as), as.getC_Currency_ID(), costs.negate());
    if (dr == null) {
      this.p_Error = "No Product Costs";
      return null;
    } 
//    dr.setM_Locator_ID(this.m_task.getM_ActualLocator_ID());
    dr.setM_Locator_ID(this.m_task.getM_Locator_ID());
    dr.setQty(getQty().negate());
    cr = fact.createLine(null, this.m_productCost.getAccount(3, as), as.getC_Currency_ID(), costs);
    if (cr == null) {
      this.p_Error = "No Product Costs";
      return null;
    } 
//    cr.setM_Locator_ID(this.m_task.getM_ActualLocatorTo_ID());
    cr.setM_Locator_ID(this.m_task.getM_LocatorTo_ID());
    cr.setQty(getQty());
    ArrayList<Fact> facts = new ArrayList<Fact>();
    facts.add(fact);
    return facts;
  }
}
