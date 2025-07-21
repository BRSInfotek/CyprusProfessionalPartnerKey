package org.cyprus.mfg.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.mfg.model.MMFGRouting;
import org.cyprus.mfg.model.MMFGRoutingOperation;
import org.cyprus.mfg.model.MMFGRoutingOperationResource;
import org.cyprus.mfg.model.MMFGWorkCenter;
import org.cyprus.mfg.model.MMFGWorkCenterCost;
import org.cyprus.mfg.model.X_MFG_WorkCenterCost;
import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MBOMProduct;
import org.cyprusbrs.framework.MCost;
import org.cyprusbrs.framework.MCostElement;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductCategory;
import org.cyprusbrs.framework.MProductCategoryAcct;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.framework.X_C_AcctSchema;
import org.cyprusbrs.framework.X_M_BOM;
import org.cyprusbrs.framework.X_M_Cost;
import org.cyprusbrs.framework.X_M_CostElement;
import org.cyprusbrs.framework.X_M_Product;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;

public class CostRollup extends SvrProcess { 

private int p_AD_Org_ID = 0;

private int p_M_Product_Category_ID = 0;

private int p_M_Product_ID = 0;

private String p_RollupLevel = "A";

private String p_BOMType = X_M_BOM.BOMTYPE_CurrentActive;

private String p_BOMUse = X_M_BOM.BOMUSE_Manufacturing;

private int p_To_M_CostType_ID = 0;

private int p_From_M_CostType_ID = 0;

private MAcctSchema p_C_AcctSchema = null;

private int p_M_Routing_ID = 0;

private int materialCostElement = 0;

private MCostElement m_ce = null;

private int precision = 6;

private class explosionData {
  public MProduct m_Top_Assembly;
  
  public MProduct m_Assembly;
  
  public MProduct m_Component;
  
  public int Level;
  
  public BigDecimal componentQty;
  
  explosionData(MProduct top_assembly, MProduct assembly, MProduct component, int level, BigDecimal qty) {
    this.m_Top_Assembly = top_assembly;
    this.m_Assembly = assembly;
    this.m_Component = component;
    this.Level = level;
    this.componentQty = qty;
  }
  
  public String toString() {
    String retVal = "";
    if (this.m_Top_Assembly != null)
      retVal = retVal + this.m_Top_Assembly.getName() + "   "; 
    if (this.m_Assembly != null)
      retVal = retVal + this.m_Assembly.getName() + "     "; 
    if (this.m_Component != null)
      retVal = retVal + this.m_Component.getName() + "    "; 
    retVal = retVal + this.Level;
    return retVal;
  }
}

private final ArrayList<explosionData> BOMStructure = new ArrayList<explosionData>();

private final ArrayList<MProduct> rolledUpAssemblies = new ArrayList<MProduct>();

private int org_id = 0;

private final Trx newtrx = Trx.get("Rollup",true);

protected void prepare() {
  ProcessInfoParameter[] para = getParameter();
  for (ProcessInfoParameter element : para) {
    String name = element.getParameterName();
    if (element.getParameter() != null)
      if (name.equals("AD_Org_ID")) {
        this.p_AD_Org_ID = element.getParameterAsInt();
      } else if (name.equals("M_Product_Category_ID")) {
        this.p_M_Product_Category_ID = element.getParameterAsInt();
      } else if (name.equals("M_Product_ID")) {
        this.p_M_Product_ID = element.getParameterAsInt();
      } else if (name.equals("RollupLevel")) {
        this.p_RollupLevel = (String)element.getParameter();
      } else if (name.equals("BOMType")) {
        this.p_BOMType = (String)element.getParameter();
      } else if (name.equals("BOMUse")) {
        this.p_BOMUse = (String)element.getParameter();
      } else if (name.equals("To_M_CostType_ID")) {
        this.p_To_M_CostType_ID = element.getParameterAsInt();
      } else if (name.equals("From_M_CostType_ID")) {
        this.p_From_M_CostType_ID = element.getParameterAsInt();
      } else if (name.equals("C_AcctSchema_ID")) {
        this.p_C_AcctSchema = MAcctSchema.get(getCtx(), element.getParameterAsInt());
      } else if (name.equals("M_Routing_ID")) {
        this.p_M_Routing_ID = element.getParameterAsInt();
      } else {
        this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
      }  
  } 
}

protected String doIt() throws Exception {
//  SysEnv se = SysEnv.get("CMFG");
//  if (se == null || !se.checkLicense())
//    throw new CompiereUserException(CLogger.retrieveError().getName()); 
  if (this.p_To_M_CostType_ID == 0 || this.p_From_M_CostType_ID == 0)
//    throw new CompiereUserException("@FillMandatory@ @M_CostType_ID@"); 
	  throw new CyprusException("@FillMandatory@ @M_CostType_ID@"); 
  if (this.p_C_AcctSchema == null)
//    throw new CompiereUserException("@FillMandatory@  @C_AcctSchema_ID@");
  throw new CyprusException("@FillMandatory@  @C_AcctSchema_ID@");
  if (this.p_To_M_CostType_ID == this.p_From_M_CostType_ID)
//    throw new CompiereSystemException("@SameCostType@"); 
  throw new CyprusException("@SameCostType@"); 

  this.materialCostElement = MCostElement.getMfgCostElement(getAD_Client_ID());
  if (this.materialCostElement == 0)
//    throw new CompiereUserException("Manufacturing Cost Element is missing"); 
	  throw new CyprusException("Manufacturing Cost Element is missing"); 
  this.precision = this.p_C_AcctSchema.getCostingPrecision();
  MClient client = MClient.get(getCtx());
  this.m_ce = MCostElement.getMaterialCostElement((PO)client, X_C_AcctSchema.COSTINGMETHOD_StandardCosting);
  if (this.m_ce.get_ID() == 0)
//    throw new CompiereUserException("@NotFound@ @M_CostElement_ID@ (StdCost)"); 
  throw new CyprusException("@NotFound@ @M_CostElement_ID@ (StdCost)"); 

  MProduct[] assemblies = getAssemblies();
  if (assemblies == null || assemblies.length == 0) {
    this.log.log(Level.FINE, "No product found");
    return Msg.getMsg(getCtx(), "NoRollupProduct");
  } 
  for (MProduct assembly : assemblies) {
    this.log.log(Level.FINE, assembly.getName() + " :- Started rollup");
    addComponents(assembly, assembly, 0, this.p_BOMType, this.p_BOMUse);
    explosionData[] Structure = new explosionData[this.BOMStructure.size()];
    this.BOMStructure.toArray(Structure);
    this.log.log(Level.FINE, assembly.getName() + " :- " + (Structure.length - 1) + " components found for product ");
    this.BOMStructure.clear();
    int MaxLevel = getMaxLevel(Structure);
    if (MaxLevel != 0)
      for (int i = MaxLevel; i > 0; i--) {
        explosionData[] comps = getOfLevel(i, Structure);
        for (explosionData comp : comps) {
          if (!rolledup(comp.m_Assembly))
            if (comp.m_Assembly.isBOM() && comp.m_Assembly.isVerified() && comp.m_Assembly.isBasedOnRollup()) {
              this.log.log(Level.FINE, comp.m_Assembly.getName() + " :- " + " Started cost calculation ");
              boolean result = rollupCost(comp, Structure, i);
              if (!result) {
                this.newtrx.rollback();
                this.newtrx.close();
//                throw new CompiereSystemException("Rollup process encountered an error");
                throw new CyprusException("Rollup process encountered an error");

              } 
              addLog(comp.m_Assembly.getName() + " :- " + Msg.getMsg(getCtx(), "RollupComplete"));
            }  
        } 
      }  
  } 
  this.newtrx.commit();
  this.newtrx.close();
  return "";
}

private MProduct[] getAssemblies() {
  MRole role = MRole.getDefault(getCtx(), false);
  ArrayList<MProduct> assemblies = new ArrayList<MProduct>();
  if (this.p_M_Product_ID != 0) {
    CPreparedStatement cPreparedStatement=null;
    this.log.log(Level.FINE, "Specific product");
    StringBuffer sql = new StringBuffer("SELECT * FROM M_Product ");
    sql.append(" WHERE IsActive = 'Y' ");
    sql.append(" AND M_Product_ID = ? ");
    sql.append(" AND IsBom = 'Y' ");
    sql.append(" AND IsVerified = 'Y' ");
    sql.append(" AND IsBasedOnRollup = 'Y' ");
    sql.append(" AND IsManufactured = 'Y'");
    if (this.p_M_Product_Category_ID != 0)
      sql.append(" AND M_Product_Category_ID = ? "); 
    String sql1 = role.addAccessSQL(sql.toString(), "M_Product", false, false);
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql1, get_TrxName());
      cPreparedStatement.setInt(1, this.p_M_Product_ID);
      if (this.p_M_Product_Category_ID != 0)
        cPreparedStatement.setInt(2, this.p_M_Product_Category_ID); 
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MProduct product = new MProduct(getCtx(), rs, get_TrxName());
        assemblies.add(product);
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql1);
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    } 
  } else if (this.p_M_Product_Category_ID != 0) {
    this.log.log(Level.FINE, "Specific product category");
    MProductCategory pc = new MProductCategory(getCtx(), this.p_M_Product_Category_ID, get_TrxName());
    StringBuffer whereClause = new StringBuffer("");
    whereClause.append(" AND IsActive = 'Y' ");
    whereClause.append(" AND IsBom = 'Y' ");
    whereClause.append(" AND IsVerified = 'Y' ");
    whereClause.append(" AND IsBasedOnRollup = 'Y' ");
    whereClause.append(" AND IsManufactured = 'Y'");
    MProduct[] products = pc.getProductsofCategory(whereClause.toString(), get_TrxName());
    for (MProduct product : products)
      assemblies.add(product); 
  } else {
    CPreparedStatement cPreparedStatement=null;
    this.log.log(Level.FINE, "Specific organization");
    StringBuffer sql = new StringBuffer("SELECT * FROM M_Product WHERE IsActive = 'Y' ");
    sql.append(" AND IsBom = 'Y' ");
    sql.append(" AND IsVerified = 'Y' ");
    sql.append(" AND IsBasedOnRollup = 'Y' ");
    sql.append(" AND IsManufactured = 'Y'");
    if (this.p_AD_Org_ID != 0)
      sql.append(" AND (AD_Org_ID = ? OR AD_Org_ID = 0) "); 
    String sql1 = role.addAccessSQL(sql.toString(), "M_Product", false, false);
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql1, get_TrxName());
      if (this.p_AD_Org_ID != 0)
        cPreparedStatement.setInt(1, this.p_AD_Org_ID); 
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MProduct product = new MProduct(getCtx(), rs, get_TrxName());
        assemblies.add(product);
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql1);
    } finally {
//      DB.closeResultSet(rs);
//      DB.closeStatement((Statement)cPreparedStatement);
    	DB.close(rs, cPreparedStatement);
    	rs = null; cPreparedStatement = null;
    } 
  } 
  MProduct[] retVal = new MProduct[assemblies.size()];
  assemblies.toArray(retVal);
  this.log.log(Level.FINE, retVal.length + " product(s) found for rollup");
  return retVal;
}

private void addComponents(MProduct topAssembly, MProduct assembly, int Level, String BOMType, String BOMUse) {
  if (Level == 0) {
    explosionData comp = new explosionData(topAssembly, assembly, null, Level, BigDecimal.ONE);
    this.BOMStructure.add(comp);
  } 
  if (this.p_RollupLevel.equals("S") && Level >= 1)
    return; 
  if (assembly.isBOM() && assembly.isVerified() && assembly.isBasedOnRollup()) {
    MBOMProduct[] BOMProducts = MBOMProduct.getBOMLines(assembly, BOMType, BOMUse);
    if (BOMProducts.length == 0)
      return; 
    Level++;
    for (MBOMProduct BOMProduct : BOMProducts) {
      MProduct compProduct = new MProduct(getCtx(), BOMProduct.getM_ProductBOM_ID(), get_TrxName());
      if (compProduct.getProductType().equals(X_M_Product.PRODUCTTYPE_Item)) {
        explosionData comp = new explosionData(topAssembly, assembly, compProduct, Level, BOMProduct.getBOMQty());
        this.BOMStructure.add(comp);
        if (compProduct.isBOM() && compProduct.isVerified() && compProduct.isBasedOnRollup()) {
          MBOM bom = BOMProduct.getComponentBOM();
          if (bom != null) {
            BOMType = bom.getBOMType();
            BOMUse = bom.getBOMUse();
          } 
          addComponents(topAssembly, compProduct, Level, BOMType, BOMUse);
        } 
      } 
    } 
  } 
}

private int getMaxLevel(explosionData[] Strucutre) {
  int retVal = -1;
  for (explosionData element : Strucutre) {
    if (element.Level > retVal)
      retVal = element.Level; 
  } 
  return retVal;
}

private explosionData[] getOfLevel(int level, explosionData[] Structure) {
  ArrayList<explosionData> comps = new ArrayList<explosionData>();
  for (explosionData element : Structure) {
    boolean exists = false;
    if (element.Level == level) {
      for (int j = 0; j < comps.size(); j++) {
        explosionData a = comps.get(j);
        if (a.m_Assembly.getM_Product_ID() == element.m_Assembly.getM_Product_ID())
          exists = true; 
      } 
      if (!exists)
        comps.add(element); 
    } 
  } 
  explosionData[] retVal = new explosionData[comps.size()];
  comps.toArray(retVal);
  return retVal;
}

private boolean rollupCost(explosionData comp, explosionData[] Structure, int level) {
  String CostingLevel = getCostingLevel(comp.m_Assembly);
  if (CostingLevel.equals(X_C_AcctSchema.COSTINGLEVEL_BatchLot)) {
    this.log.log(Level.WARNING, comp.m_Assembly.getName() + " :- Costing level Batch/Lot not supported for cost rollup");
    return true;
  } 
  if (CostingLevel.equals(X_C_AcctSchema.COSTINGLEVEL_Organization)) {
    this.org_id = comp.m_Assembly.getAD_Org_ID();
  } else if (CostingLevel.equals(X_C_AcctSchema.COSTINGLEVEL_Client)) {
    this.org_id = 0;
  } 
  boolean costNotFound = false;
  ArrayList<MCost> cost = new ArrayList<MCost>();
  MMFGRouting rt = null;
  MMFGRoutingOperation[] operations = null;
  MCost[] existingCosts=null;
try {
	existingCosts = MCost.get(0, this.p_C_AcctSchema, this.p_To_M_CostType_ID, this.org_id, comp.m_Assembly);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
if(existingCosts!=null) {
	for (MCost existingCost : existingCosts) {
		if (existingCost.getAD_Org_ID() == this.org_id) {
			if (existingCost.getIsUserDefined().equals("Y")) {
				MCost newCostrow = new MCost(existingCost.getCtx(), 0, get_TrxName());
				setComponentCost(existingCost, newCostrow, comp, (explosionData)null, cost);
			} 
			boolean result = existingCost.delete(false, this.newtrx.getTrxName());
			if (!result) {
				this.log.log(Level.SEVERE, comp.m_Assembly + " :- Failed to delete existing cost records");
				return false;
			} 
		} 
	} 
}
  this.log.log(Level.INFO, comp.m_Assembly.getName() + " :- Deleted existing cost rows");
  MCost[] assemblyCosts=null;
try {
	assemblyCosts = MCost.get(0, this.p_C_AcctSchema, this.p_From_M_CostType_ID, this.org_id, comp.m_Assembly);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
if(assemblyCosts!=null)
{
  for (MCost assemblyCost : assemblyCosts) {
    if (assemblyCost.getAD_Org_ID() == this.org_id)
      if (assemblyCost.getIsUserDefined().equals("Y")) {
        MCost newCostrow = new MCost(assemblyCost.getCtx(), 0, get_TrxName());
        setComponentCost(assemblyCost, newCostrow, comp, (explosionData)null, cost);
      }  
  }
}
  this.log.log(Level.INFO, comp.m_Assembly.getName() + " :- Inserted existing cost rows for the product");
  if (this.p_M_Routing_ID != 0) {
    rt = new MMFGRouting(getCtx(), this.p_M_Routing_ID, get_TrxName());
    if (rt.getM_Product_ID() != comp.m_Assembly.getM_Product_ID())
      rt = null; 
  } 
  if (rt == null)
    rt = MMFGRouting.getCostRollupRouting(comp.m_Assembly); 
  if (rt == null)
    this.log.log(Level.WARNING, comp.m_Assembly.getName() + " :- " + Msg.getMsg(getCtx(), "RoutingNotFound")); 
  if (rt != null && rt.isActive())
    operations = MMFGRoutingOperation.getOperationLines(rt, "", "ORDER BY SeqNo"); 
  if (operations != null)
    for (MMFGRoutingOperation operation : operations) {
      MMFGRoutingOperationResource[] resources = MMFGRoutingOperationResource.getOperationResource(operation);
      for (MMFGRoutingOperationResource resource : resources) {
        MProduct res = new MProduct(resource.getCtx(), resource.getM_Product_ID(), get_TrxName());
        MCost[] resCosts=null;
		try {
			resCosts = MCost.get(0, this.p_C_AcctSchema, this.p_From_M_CostType_ID, this.org_id, res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (resCosts==null || resCosts.length == 0) {
          this.log.log(Level.WARNING, res.getName() + " :- " + Msg.getMsg(getCtx(), "ResourceCostNotFound"));
          costNotFound = true;
        } else {
          for (MCost resCost : resCosts) {
            if (resCost.getAD_Org_ID() == this.org_id)
              if (MCostElement.get(getCtx(), resCost.getM_CostElement_ID()).getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Resource)) {
                MCost newCostrow = new MCost(resCost.getCtx(), 0, get_TrxName());
                setResCost(resCost, newCostrow, comp, resource, cost);
              }  
          } 
          this.log.log(Level.FINE, comp.m_Assembly.getName() + " :- Inserted cost rows from resource " + res.getName());
        } 
      } 
      int m_WorkCenter_ID = operation.getMFG_WorkCenter_ID();
      MMFGWorkCenter wc = new MMFGWorkCenter(getCtx(), m_WorkCenter_ID, this.newtrx.getTrxName());
      MMFGWorkCenterCost[] wcCosts = MMFGWorkCenterCost.getWorkCenterCosts(wc, this.p_From_M_CostType_ID, this.p_C_AcctSchema, this.org_id);
      if (wcCosts.length == 0) {
        this.log.log(Level.WARNING, wc.getName() + " :- " + Msg.getMsg(getCtx(), "WorkCenterCostNotFound"));
        costNotFound = true;
      } else {
        for (MMFGWorkCenterCost wcCost : wcCosts) {
          if (MCostElement.get(getCtx(), wcCost.getM_CostElement_ID()).getCostElementType().equals(X_M_CostElement.COSTELEMENTTYPE_Overhead)) {
            MCost newCostrow = new MCost(wcCost.getCtx(), 0, this.newtrx.getTrxName());
            setWCCost((X_MFG_WorkCenterCost)wcCost, newCostrow, comp, cost);
          } 
        } 
        this.log.log(Level.FINE, comp.m_Assembly.getName() + " :- Inserted cost rows from work center " + wc.getName());
      } 
    }  
  explosionData[] BOMComponents = getComponents(comp, Structure, level);
  for (explosionData BOMComponent : BOMComponents) {
    MProduct component = BOMComponent.m_Component;
    if (component.getProductType().equals(X_M_Product.PRODUCTTYPE_Item)) {
      int fetchCostTypeID;
      if (component.isBOM() && component.isVerified() && this.p_RollupLevel.equals("A")) {
        fetchCostTypeID = this.p_To_M_CostType_ID;
      } else {
        fetchCostTypeID = this.p_From_M_CostType_ID;
      } 
      MCost[] compCosts=null;
	try {
		compCosts = MCost.get(0, this.p_C_AcctSchema, fetchCostTypeID, this.org_id, component);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      if (compCosts==null || compCosts.length == 0) {
        this.log.log(Level.WARNING, component.getName() + ":- " + Msg.getMsg(getCtx(), "ComponentCostNotFound"));
        costNotFound = true;
      } else {
        calculatePercentCost(compCosts, component);
        for (MCost compCost : compCosts) {
          if (compCost.getAD_Org_ID() == this.org_id) {
            MCost newCostrow = new MCost(compCost.getCtx(), 0, get_TrxName());
            setComponentCost(compCost, newCostrow, comp, BOMComponent, cost);
          } 
        } 
        this.log.log(Level.FINE, comp.m_Assembly.getName() + " :- Inserted cost rows from component " + component.getName());
      } 
    } 
  } 
  MCost[] cc = new MCost[cost.size()];
  cost.toArray(cc);
  for (MCost c : cc) {
    this.log.log(Level.FINE, c.toString());
    boolean result = false;
    if (c.getCurrentCostPrice() != null && c.getCurrentCostPrice().signum() != 0) {
      result = c.save();
    } else {
      result = c.delete(true);
    } 
    if (!result) {
      this.log.log(Level.SEVERE, comp.m_Assembly.getName() + ":- " + "Error in saving cost row " + c.toString());
      this.newtrx.rollback();
      this.newtrx.close();
      return false;
    } 
  } 
  if (costNotFound)
    this.log.log(Level.WARNING, "One or more cost records not found for the assembly"); 
  this.log.log(Level.INFO, comp.m_Assembly.getName() + " :- Rolled up cost saved");
  this.rolledUpAssemblies.add(comp.m_Assembly);
  return true;
}

private void setResCost(MCost oldCost, MCost newCost, explosionData comp, MMFGRoutingOperationResource resource, ArrayList<MCost> costs) {
  newCost.setAD_Client_ID(oldCost.getAD_Client_ID());
  newCost.setAD_Org_ID(oldCost.getAD_Org_ID());
  newCost.setM_AttributeSetInstance_ID(oldCost.getM_AttributeSetInstance_ID());
  newCost.setM_Product_ID(comp.m_Assembly.getM_Product_ID());
  newCost.setM_CostType_ID(this.p_To_M_CostType_ID);
  newCost.setC_AcctSchema_ID(oldCost.getC_AcctSchema_ID());
  newCost.setM_CostElement_ID(oldCost.getM_CostElement_ID());
  newCost.setDescription(oldCost.getDescription());
  newCost.setCurrentCostPrice(oldCost.getCurrentCostPrice().multiply(resource.getQtyRequired()));
  newCost.setPercentCost(oldCost.getPercentCost());
  newCost.setBasisType(oldCost.getBasisType());
  newCost.setIsUserDefined("N");
  newCost.setIsThisLevel("N");
  consolidateCosts(newCost, costs);
}

private void setWCCost(X_MFG_WorkCenterCost wcCost, MCost newCost, explosionData comp, ArrayList<MCost> costs) {
  newCost.setAD_Client_ID(wcCost.getAD_Client_ID());
  newCost.setAD_Org_ID(wcCost.getAD_Org_ID());
  newCost.setM_AttributeSetInstance_ID(0);
  newCost.setM_Product_ID(comp.m_Assembly.getM_Product_ID());
  newCost.setM_CostType_ID(this.p_To_M_CostType_ID);
  newCost.setC_AcctSchema_ID(wcCost.getC_AcctSchema_ID());
  newCost.setM_CostElement_ID(wcCost.getM_CostElement_ID());
  newCost.setDescription(wcCost.getDescription());
  if (wcCost.getBasisType().equals(X_M_Cost.BASISTYPE_PerBatch)) {
    newCost.setCurrentCostPrice(wcCost.getCurrentCostPrice().divide(new BigDecimal(comp.m_Assembly.getBatchSize()), this.precision, 4));
  } else {
    newCost.setCurrentCostPrice(wcCost.getCurrentCostPrice());
  } 
  newCost.setPercentCost(BigDecimal.ZERO);
  newCost.setBasisType(X_M_Cost.BASISTYPE_PerItem);
  newCost.setIsUserDefined("N");
  newCost.setIsThisLevel("N");
  consolidateCosts(newCost, costs);
}

private void setComponentCost(MCost oldCost, MCost newCost, explosionData comp, explosionData prod, ArrayList<MCost> costs) {
  newCost.setAD_Client_ID(oldCost.getAD_Client_ID());
  newCost.setAD_Org_ID(oldCost.getAD_Org_ID());
  newCost.setM_AttributeSetInstance_ID(oldCost.getM_AttributeSetInstance_ID());
  newCost.setM_Product_ID(comp.m_Assembly.getM_Product_ID());
  newCost.setM_CostType_ID(this.p_To_M_CostType_ID);
  newCost.setC_AcctSchema_ID(oldCost.getC_AcctSchema_ID());
  if (oldCost.getM_CostElement_ID() == this.materialCostElement) {
    newCost.setM_CostElement_ID(this.m_ce.getM_CostElement_ID());
  } else {
    newCost.setM_CostElement_ID(oldCost.getM_CostElement_ID());
  } 
  newCost.setDescription(oldCost.getDescription());
  if (prod == null) {
    newCost.setCurrentCostPrice(oldCost.getCurrentCostPrice());
    newCost.setPercentCost(oldCost.getPercentCost());
    newCost.setIsThisLevel("Y");
    newCost.setBasisType(oldCost.getBasisType());
    if (oldCost.getM_CostType_ID() == this.p_From_M_CostType_ID) {
      newCost.setIsUserDefined("N");
    } else {
      newCost.setIsUserDefined(oldCost.getIsUserDefined());
    } 
  } else {
    newCost.setCurrentCostPrice(oldCost.getCurrentCostPrice().multiply(prod.componentQty));
    if (oldCost.getBasisType().equals(X_M_Cost.BASISTYPE_PerBatch))
      newCost.setCurrentCostPrice(newCost.getCurrentCostPrice().divide(new BigDecimal(prod.m_Component.getBatchSize()), this.precision, 4)); 
    newCost.setPercentCost(BigDecimal.ZERO);
    newCost.setIsThisLevel("N");
    newCost.setIsUserDefined("N");
    newCost.setBasisType(X_M_Cost.BASISTYPE_PerItem);
  } 
  consolidateCosts(newCost, costs);
}

private void consolidateCosts(MCost newCost, ArrayList<MCost> costs) {
  boolean exists = false;
  int index = -1;
  if (newCost.getPercentCost() == null || newCost.getPercentCost().signum() == 0)
    for (int i = 0; i < costs.size(); i++) {
      MCost cc = costs.get(i);
      if (cc.getM_CostElement_ID() == newCost.getM_CostElement_ID())
        if (cc.getBasisType().equals(newCost.getBasisType()))
          if (cc.getIsUserDefined().equals(newCost.getIsUserDefined()))
            if (cc.getIsThisLevel().equals(newCost.getIsThisLevel())) {
              index = i;
              exists = true;
            }    
    }  
  if (exists) {
    MCost cc = costs.get(index);
    cc.setCurrentCostPrice(cc.getCurrentCostPrice().add(newCost.getCurrentCostPrice()));
    costs.remove(index);
    costs.add(cc);
  } else {
    costs.add(newCost);
  } 
}

private boolean rolledup(MProduct assembly) {
  for (int i = 0; i < this.rolledUpAssemblies.size(); i++) {
    MProduct a = this.rolledUpAssemblies.get(i);
    if (a.getM_Product_ID() == assembly.getM_Product_ID())
      return true; 
  } 
  return false;
}

private void calculatePercentCost(MCost[] cc, MProduct comp) {
  BigDecimal nonPercentCost = BigDecimal.ZERO;
  for (MCost element : cc) {
    if (element.getAD_Org_ID() == this.org_id)
      if (element.getPercentCost() == null || element.getPercentCost() == BigDecimal.ZERO)
        if (element.getBasisType().equals(X_M_Cost.BASISTYPE_PerBatch)) {
          nonPercentCost = nonPercentCost.add(element.getCurrentCostPrice().divide(new BigDecimal(comp.getBatchSize())));
        } else {
          nonPercentCost = nonPercentCost.add(element.getCurrentCostPrice());
        }   
  } 
  for (MCost element : cc) {
    if (element.getAD_Org_ID() == this.org_id)
      if (element.getPercentCost() != null && element.getPercentCost() != BigDecimal.ZERO)
        element.setCurrentCostPrice(nonPercentCost.multiply(element.getPercentCost()).divide(Env.ONEHUNDRED));  
  } 
}

private explosionData[] getComponents(explosionData comp, explosionData[] Structure, int level) {
  ArrayList<explosionData> components = new ArrayList<explosionData>();
  for (explosionData element : Structure) {
    if (element.Level == level && element.m_Assembly.getM_Product_ID() == comp.m_Assembly.getM_Product_ID())
      components.add(element); 
  } 
  explosionData[] retVal = new explosionData[components.size()];
  components.toArray(retVal);
  return retVal;
}

private String getCostingLevel(MProduct product) {
  this.log.log(Level.FINE, "Get costing level for product " + product.getName() + " in accounting schema " + this.p_C_AcctSchema.getName());
  String CostingLevel = this.p_C_AcctSchema.getCostingLevel();
  MProductCategoryAcct pca = MProductCategoryAcct.get(product.getCtx(), product.getM_Product_Category_ID(), this.p_C_AcctSchema.getC_AcctSchema_ID(), get_TrxName());
  if (pca == null)
    throw new IllegalStateException("Cannot find Acct for M_Product_Category_ID=" + product.getM_Product_Category_ID() + ", C_AcctSchema_ID=" + this.p_C_AcctSchema.getC_AcctSchema_ID()); 
  if (pca.getCostingLevel() != null)
    CostingLevel = pca.getCostingLevel(); 
  return CostingLevel;
}
}
