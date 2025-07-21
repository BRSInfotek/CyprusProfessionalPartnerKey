package org.cyprus.mfg.util;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.cyprus.mfg.model.MBOMAlternative;
import org.cyprus.vos.ProductInfo;
import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MBOMProduct;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.X_M_BOMProduct;
import org.cyprusbrs.framework.X_M_Product;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;

public class BOMDropLines {
	  private int p_M_WorkOrder_ID = 0;
	  
	  private PO m_workorderoperation;
	  
	  static CLogger log = CLogger.getCLogger(BOMDropLines.class);
	  
	  public ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();
	  
	  private boolean recursive = false;
	  
	  private ArrayList<Integer> OpProcessed = new ArrayList<Integer>();
	  
	  private PO dummyOp;
	  
	  private int BOMOperationSeqNo = 0;
	  
	  private String m_processMsg = "BOM Lines not copied";
	  
	  public String getM_processMsg() {
	    return this.m_processMsg;
	  }
	  
	  public void setM_processMsg(String msg) {
	    this.m_processMsg = msg;
	  }
	  
	  public boolean addBOMLines(Properties ctx, Trx trx, int productID, BigDecimal qty, int bomID, String bomType, String bomUse, int workorderID, boolean isprocesscalled) {
	    return addBOMLines(ctx, trx, productID, qty, bomID, bomType, bomUse, workorderID, isprocesscalled, -1, -1, false);
	  }
	  
	  public boolean addBOMLines(Properties ctx, Trx trx, int productID, BigDecimal qty, int bomID, String bomType, String bomUse, int workorderID, boolean isprocesscalled, int level, int lineNo, boolean loadComponents) {
	    MBOMProduct[] bomLines;
	    boolean loadBOMComponents = loadComponents;
	    if (level == 0 && lineNo == 0)
	      loadBOMComponents = true; 
	    this.p_M_WorkOrder_ID = workorderID;
	    MProduct product = new MProduct(ctx, productID, null);
	    MBOM bom = new MBOM(ctx, bomID, null);
	    PO workorder = getWorkOrderInstance(ctx, workorderID, trx);
	    if (bomID == 0) {
	      log.fine(product.toString());
	      bomLines = MBOMProduct.getBOMLines(product, bomType, bomUse);
	    } else {
	      log.fine(bom.toString());
	      bomLines = MBOMProduct.getBOMLines(bom);
	      bomType = bom.getBOMType();
	      bomUse = bom.getBOMUse();
	    } 
	    if (loadComponents == true) {
	      for (MBOMProduct bomLine : bomLines) {
	        if (workorderID != 0) {
	          MProduct componentProduct = new MProduct(ctx, bomLine.getM_ProductBOM_ID(), null);
	          if (!componentProduct.getProductType().equals(X_M_Product.PRODUCTTYPE_Item)) {
	            log.fine(product.getName() + ": " + componentProduct.getName() + "'s ProductType is not Item. Products of type Item are only processed");
	            continue;
	          } 
	        } 
	        addBOMLine(ctx, trx, bomLine, qty, bomType, bomUse, workorder, isprocesscalled, level, lineNo, loadBOMComponents);
	        continue;
	      } 
	    } else {
	      int seqNoMatch = 0;
	      PO woo = getWorkOrderOperationInstance2(ctx, 0, trx);
	      Class<?> wooClass = getWorkOrderOperationClass();
	      try {
	        Method getOfWO = wooClass.getMethod("getOfWorkOrder", new Class[] { getWorkOrderClass(), String.class, String.class });
	        PO[] woos = (PO[])getOfWO.invoke(woo, new Object[] { workorder, null, "SeqNo" });
	        for (int i = 0; i < bomLines.length; i++) {
	          MProduct componentProduct = new MProduct(ctx, bomLines[i].getM_ProductBOM_ID(), null);
	          if (!componentProduct.getProductType().equals(X_M_Product.PRODUCTTYPE_Item)) {
	            log.fine(product.getName() + ": " + componentProduct.getName() + "'s ProductType is not Item. Products of type Item are only processed");
	          } else if (this.recursive == true) {
	            this.m_workorderoperation = this.dummyOp;
	            if (!addBOMLine(ctx, trx, bomLines[i], qty, bomType, bomUse, workorder, isprocesscalled, level, lineNo, loadBOMComponents))
	              return false; 
	          } else {
	            this.recursive = false;
	            MBOMProduct newbomproduct = bomLines[i];
	            seqNoMatch = 0;
	            for (PO woop : woos) {
	              int WOseqNo = 0;
	              Method getSeqNo = wooClass.getMethod("getSeqNo", new Class[0]);
	              WOseqNo = ((Integer)getSeqNo.invoke(woop, new Object[0])).intValue();
	              if (bomLines[i].getOperationSeqNo() == WOseqNo) {
	                seqNoMatch++;
	                this.m_workorderoperation = woop;
	                this.dummyOp = woop;
	                PO woc = getWorkOrderComponentInstance2(ctx, 0, trx);
	                Class<?> wocClass = getWorkOrderComponentClass();
	                Method getOfWOO = wocClass.getMethod("getOfWorkOrderOperation", new Class[] { getWorkOrderOperationClass(), String.class, String.class });
	                PO[] wocs = (PO[])getOfWOO.invoke(woc, new Object[] { woop, " M_Product_ID = " + bomLines[i].getM_ProductBOM_ID(), null });
	                if (wocs.length > 0) {
	                  Method SetQuantity = wocClass.getMethod("setQtyRequired", new Class[] { BigDecimal.class });
	                  Method GetQuantity = wocClass.getMethod("getQtyRequired", new Class[0]);
	                  SetQuantity.invoke(wocs[0], new Object[] { bomLines[i].getBOMQty().add((BigDecimal)GetQuantity.invoke(wocs[0], new Object[0])) });
	                  if (!wocs[0].save()) {
	                    log.saveError("Error", "Work Order Component NOT updated");
	                    return false;
	                  } 
	                  if (!this.OpProcessed.contains(Integer.valueOf(WOseqNo)))
	                    this.OpProcessed.add(Integer.valueOf(WOseqNo)); 
	                  break;
	                } 
	                if (!addBOMLine(ctx, trx, bomLines[i], qty, bomType, bomUse, workorder, isprocesscalled, level, lineNo, loadBOMComponents))
	                  return false; 
	                if (!this.OpProcessed.contains(Integer.valueOf(WOseqNo)))
	                  this.OpProcessed.add(Integer.valueOf(WOseqNo)); 
	                break;
	              } 
	              this.recursive = false;
	            } 
	            if (seqNoMatch <= 0) {
	              String sql = "SELECT C_UOM_ID FROM C_UOM WHERE Name LIKE ?";
	              int uomID = DB.getSQLValue(null, sql, new Object[] { "Day" });
	              PO woop = getWorkOrderOperationInstance(ctx, trx, workorderID, 100, 0, uomID, BigDecimal.ZERO, BigDecimal.ZERO, null, newbomproduct.getOperationSeqNo(), true, false, false, false, isprocesscalled);
	              if (woop == null) {
	                setM_processMsg("There are no WorkCenters defined for the warehouse that is provided.Create a WorkCenter to proceed.");
	                log.saveError("Error", "Work Order Operation NOT created");
	                return false;
	              } 
	              if (!woop.save()) {
	                log.saveError("Error", "Work Order Operation NOT created");
	                return false;
	              } 
	              if (!this.OpProcessed.contains(Integer.valueOf(newbomproduct.getOperationSeqNo())))
	                this.OpProcessed.add(Integer.valueOf(newbomproduct.getOperationSeqNo())); 
	              Method getOfWO2 = wooClass.getMethod("getOfWorkOrder", new Class[] { getWorkOrderClass(), String.class, String.class });
	              woos = (PO[])getOfWO2.invoke(woo, new Object[] { workorder, null, "SeqNo" });
	              this.m_workorderoperation = woop;
	              this.dummyOp = this.m_workorderoperation;
	              addBOMLine(ctx, trx, newbomproduct, qty, bomType, bomUse, workorder, isprocesscalled, level, lineNo, loadBOMComponents);
	            } 
	          } 
	        } 
	      } catch (NoSuchMethodException e) {
	        e.printStackTrace();
	      } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	      } catch (InvocationTargetException e) {
	        e.printStackTrace();
	      } catch (IllegalAccessException e) {
	        e.printStackTrace();
	      } 
	    } 
	    return true;
	  }
	  
	  private boolean addBOMLine(Properties ctx, Trx trx, MBOMProduct line, BigDecimal qty, String bomType, String bomUse, PO workorder, boolean isprocesscalled, int level, int lineNo, boolean loadBOMComponents) {
	    log.fine(line.toString());
	    String bomProductType = line.getBOMProductType();
	    if (bomProductType == null)
	      bomProductType = X_M_BOMProduct.BOMPRODUCTTYPE_StandardProduct; 
	    MProduct componentProduct = line.getComponent();
	    if (componentProduct == null)
	      return false; 
	    BigDecimal lineQty = line.getBOMQty().multiply(qty);
	    if (!componentProduct.isStocked() && componentProduct.isBOM() && componentProduct.isVerified()) {
	      this.BOMOperationSeqNo = line.getOperationSeqNo();
	      this.recursive = true;
	      if (line.getM_ProductBOMVersion_ID() == 0) {
	        if (!addBOMLines(ctx, trx, componentProduct.get_ID(), lineQty, 0, bomType, bomUse, this.p_M_WorkOrder_ID, isprocesscalled, level + 1, lineNo, loadBOMComponents))
	          return false; 
	      } else {
	        MBOM componentBOM = new MBOM(ctx, line.getM_ProductBOMVersion_ID(), trx.getTrxName());
	        if (!addBOMLines(ctx, trx, line.getM_BOMProduct_ID(), lineQty, componentBOM.getM_BOM_ID(), componentBOM.getBOMType(), componentBOM.getBOMUse(), this.p_M_WorkOrder_ID, isprocesscalled, level + 1, lineNo, loadBOMComponents))
	          return false; 
	      } 
	      if (loadBOMComponents)
	        this.recursive = false; 
	    } else if (!loadBOMComponents) {
	      addComponent(this.m_workorderoperation, componentProduct, workorder, lineQty, line.getSupplyType());
	    } else {
	      String fieldIdentifier = null;
	      String groupName = null;
	      String levelStr = String.valueOf(level);
	      if (X_M_BOMProduct.BOMPRODUCTTYPE_Alternative.equals(bomProductType) || X_M_BOMProduct.BOMPRODUCTTYPE_AlternativeDefault.equals(bomProductType)) {
	        MBOMAlternative group = new MBOMAlternative(ctx, line.getM_BOMAlternative_ID(), null);
	        groupName = group.getName();
	        fieldIdentifier = groupName + "_" + lineNo + "_" + levelStr;
	      } else {
	        fieldIdentifier = line.getLine() + "_" + lineNo + "_" + levelStr;
	      } 
	      if (this.recursive) {
	        addDisplay(ctx, line.getBOM().getM_Product_ID(), componentProduct.getM_Product_ID(), bomProductType, componentProduct.getName(), lineQty, groupName, line.getSupplyType(), this.BOMOperationSeqNo, fieldIdentifier);
	      } else {
	        addDisplay(ctx, line.getBOM().getM_Product_ID(), componentProduct.getM_Product_ID(), bomProductType, componentProduct.getName(), lineQty, groupName, line.getSupplyType(), line.getOperationSeqNo(), fieldIdentifier);
	      } 
	    } 
	    return true;
	  }
	  
	  private boolean addComponent(PO workorderoperation, MProduct product, PO workorder, BigDecimal lineQty, String supplyType) {
	    log.fine("addComponent: Product=" + product.toString());
	    PO woc = getWorkOrderComponentInstance(workorder, workorderoperation, product, lineQty, supplyType);
	    if (woc.save())
	      return true; 
	    log.saveError("Error", "Work Order Component NOT created");
	    return false;
	  }
	  
	  private void addDisplay(Properties ctx, int parentM_Product_ID, int M_Product_ID, String bomProductType, String name, BigDecimal lineQty, String groupName, String supplyType, int OperationSeqNo, String fieldIdentifier) {
	    log.fine("M_Product_ID=" + M_Product_ID + ",Type=" + bomProductType + ",Name=" + name + ",Qty=" + lineQty);
	    if (X_M_BOMProduct.BOMPRODUCTTYPE_StandardProduct.equals(bomProductType)) {
	      String title = Msg.getMsg(ctx, "Standard");
	      this.productList.add(new ProductInfo(M_Product_ID, name, title, null, bomProductType, lineQty.toString(), supplyType, OperationSeqNo, fieldIdentifier));
	    } else if (X_M_BOMProduct.BOMPRODUCTTYPE_OptionalProduct.equals(bomProductType)) {
	      String title = Msg.getMsg(ctx, "Optional");
	      this.productList.add(new ProductInfo(M_Product_ID, name, title, null, bomProductType, lineQty.toString(), supplyType, OperationSeqNo, fieldIdentifier));
	    } else {
	      String title = groupName;
	      this.productList.add(new ProductInfo(M_Product_ID, name, title, groupName, bomProductType, lineQty.toString(), supplyType, OperationSeqNo, fieldIdentifier));
	    } 
	  }
	  
	  public ArrayList<ProductInfo> getProductList() {
	    return this.productList;
	  }
	  
	  public ArrayList<Integer> operationsProcessed() {
	    return this.OpProcessed;
	  }
	  
	  private Class<?> getWorkOrderClass() {
	    String className = "org.cyprus.mfg.model.MMFGWorkOrder";
	    try {
	      Class<?> clazz = Class.forName(className);
	      return clazz;
	    } catch (Exception e) {
	      log.warning("Error getting class for " + className + ": - " + e.toString());
	      return null;
	    } 
	  }
	  
	  private Class<?> getWorkOrderComponentClass() {
	    String className = "org.cyprus.mfg.model.MMFGWorkOrderComponent";
	    try {
	      Class<?> clazz = Class.forName(className);
	      return clazz;
	    } catch (Exception e) {
	      log.warning("Error getting class for " + className + ": - " + e.toString());
	      return null;
	    } 
	  }
	  
	  private PO getWorkOrderInstance(Properties ctx, int workOrderID, Trx trx) {
	    Class<?> clazz = getWorkOrderClass();
	    if (clazz == null)
	      return null; 
	    try {
	      Constructor<?> constr = clazz.getConstructor(new Class[] { Properties.class, int.class, String.class });
	      PO retValue = (PO)constr.newInstance(new Object[] { ctx, Integer.valueOf(workOrderID), trx });
	      return retValue;
	    } catch (Exception e) {
	      log.warning("Error instantiating constructor for org.cyprus.mfg.model.MMFGWorkOrder:" + e.toString());
	      return null;
	    } 
	  }
	  
	  private PO getWorkOrderComponentInstance(PO workorder, PO workorderOp, MProduct componentProduct, BigDecimal componentQuantity, String componentSupplyType) {
	    Class<?> clazz = getWorkOrderComponentClass();
	    if (clazz == null)
	      return null; 
	    try {
	      Class<?> MWorkOrderClass = getWorkOrderClass();
	      Class<?> MWorkOrderOperationClass = getWorkOrderOperationClass();
	      Constructor<?> constr = clazz.getConstructor(new Class[] { MWorkOrderClass, MWorkOrderOperationClass, MProduct.class, BigDecimal.class, String.class });
	      PO retValue = (PO)constr.newInstance(new Object[] { MWorkOrderClass.cast(workorder), MWorkOrderOperationClass.cast(workorderOp), componentProduct, componentQuantity, componentSupplyType });
	      return retValue;
	    } catch (Exception e) {
	      log.warning("Error instantiating constructor for org.cyprus.mfg.model.MMFGWorkOrderComponent:" + e.toString());
	      return null;
	    } 
	  }
	  
	  private PO getWorkOrderComponentInstance2(Properties ctx, int M_WorkOrderComponent_ID, Trx trx) {
	    Class<?> clazz = getWorkOrderComponentClass();
	    if (clazz == null)
	      return null; 
	    try {
	      Constructor<?> constr = clazz.getConstructor(new Class[] { Properties.class, int.class, String.class });
	      PO retValue = (PO)constr.newInstance(new Object[] { ctx, Integer.valueOf(M_WorkOrderComponent_ID), trx });
	      return retValue;
	    } catch (Exception e) {
	      log.warning("Error instantiating constructor for org.cyprus.mfg.model.MMFGWorkOrderComponent:" + e.toString());
	      return null;
	    } 
	  }
	  
	  private Class<?> getWorkOrderOperationClass() {
	    String className = "org.cyprus.mfg.model.MMFGWorkOrderOperation";
	    try {
	      Class<?> clazz = Class.forName(className);
	      return clazz;
	    } catch (Exception e) {
	      log.warning("Error getting class for " + className + ": - " + e.toString());
	      return null;
	    } 
	  }
	  
	  private PO getWorkOrderOperationInstance(Properties ctx, Trx trx, int M_WorkOrder_ID, int M_Operation_ID, int M_StandardOperation_ID, int C_UOM_ID, BigDecimal SetupTime, BigDecimal UnitRuntime, String Description, int SeqNo, boolean isActive, boolean IsHazmat, boolean IsPermitRequired, boolean IsOptional, boolean isprocesscalled) {
	    Class<?> clazz = getWorkOrderOperationClass();
	    if (clazz == null)
	      return null; 
	    try {
	      Constructor<?> constr = clazz.getConstructor(new Class[] { 
	    		  Properties.class, String.class, int.class, int.class, int.class, int.class, BigDecimal.class, BigDecimal.class, String.class, int.class, 
	            boolean.class, boolean.class, boolean.class, boolean.class, boolean.class });
	      PO retValue = (PO)constr.newInstance(new Object[] { 
	            ctx, trx, Integer.valueOf(M_WorkOrder_ID), Integer.valueOf(M_Operation_ID), Integer.valueOf(M_StandardOperation_ID), Integer.valueOf(C_UOM_ID), SetupTime, UnitRuntime, Description, Integer.valueOf(SeqNo), 
	            Boolean.valueOf(isActive), Boolean.valueOf(IsHazmat), Boolean.valueOf(IsPermitRequired), Boolean.valueOf(IsOptional), Boolean.valueOf(isprocesscalled) });
	      return retValue;
	    } catch (Exception e) {
	      log.warning("Error instantiating constructor for org.cyprus.mfg.model.MMFGWorkOrderOperation:" + e.toString());
	      return null;
	    } 
	  }
	  
	  private PO getWorkOrderOperationInstance2(Properties ctx, int M_WorkOrderOperation_ID, Trx trx) {
	    Class<?> clazz = getWorkOrderOperationClass();
	    if (clazz == null)
	      return null; 
	    try {
	      Constructor<?> constr = clazz.getConstructor(new Class[] { Properties.class, int.class, String.class });
	      PO retValue = (PO)constr.newInstance(new Object[] { ctx, Integer.valueOf(M_WorkOrderOperation_ID), trx });
	      return retValue;
	    } catch (Exception e) {
	      log.warning("Error instantiating constructor for org.compiere.cmfg.model.MMFGWorkOrderOperation:" + e.toString());
	      return null;
	    } 
	  }
	}

