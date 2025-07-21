package org.cyprus.mrp.process;

//import com.cyprusbrs.client.SysEnv;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.mfg.model.X_MFG_WorkOrder;
import org.cyprus.mrp.model.MMRPPlan;
import org.cyprus.mrp.model.MMRPPlanRun;
import org.cyprus.mrp.model.MMRPPlannedDemand;
import org.cyprus.mrp.model.MMRPPlannedOrder;
import org.cyprus.mrp.model.MMRPProductAudit;
import org.cyprus.mrp.model.X_MRP_PlannedOrder;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductPO;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_MRP_PlannedOrder;
//import org.cyprusbrs.model.X_M_WorkOrder;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsSystemException;
//import org.cyprusbrs.util.cyprusbrsUserException;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.TimeUtil;
import org.cyprusbrs.util.Trx;
import org.cyprusbrs.util.ValueNamePair;

public class ImplementEngine extends SvrProcess {
	
	Trx trxname;
	
	PO PO;
  private int p_T_MRPPlanImplement_ID = 0;
  
  private int p_MRP_PlannedOrder_ID = 0;
  
  private int m_ordersCreated = 0;
  
  private int m_ordersPrepared = 0;
  
  private int m_componentOrdersPrepared = 0;
  
  private int m_componentOrdersCreated = 0;
  
  private boolean m_isConsolidate = false;
  
  StringBuffer retErrorValue = new StringBuffer();
  
  protected String doIt() throws Exception {
    String retValue = "";
//    SysEnv se = SysEnv.get("CMRP");
//    if (se == null || !se.checkLicense())
//      throw new cyprusbrsUserException(CLogger.retrieveError().getName()); 
    Timestamp dateStamp = new Timestamp(System.currentTimeMillis());
    this.log.info("Started at :" + dateStamp);
    int noInserted = prepareTable();
    this.log.fine("Prepare table : Inserted # " + noInserted);
  
    //@Anil21122021
    //Trx newTrx = Trx.get("CMRPImplement");
    Trx newTrx = Trx.get("CMRPImplement");
    
    String SQL = "SELECT m.MRP_PlanRun_ID FROM T_MRPPlanImplement t join MRP_PlannedOrder m ON (t.MRP_PlannedOrder_ID= m.MRP_PlannedOrder_ID )WHERE t.T_MRPPlanImplement_ID=? ";
  Object[][] result = DB.executeQuery(newTrx, SQL, new Object[] { Integer.valueOf(this.p_T_MRPPlanImplement_ID) });
  
    BigDecimal mrpPlanRun_ID = Env.ZERO;
    if (result.length != 0)
      mrpPlanRun_ID = (BigDecimal)result[0][0]; 
    MMRPPlanRun mrpPlanRun = new MMRPPlanRun(getCtx(), mrpPlanRun_ID.intValue(), get_TrxName());
    MMRPPlan mrpPlan = new MMRPPlan(getCtx(), mrpPlanRun.getMRP_Plan_ID(), get_TrxName());
    this.m_isConsolidate = mrpPlan.isConsolidatePO();
    boolean retFlag = false;
    if (!this.m_isConsolidate) {
      retFlag = createOrders(newTrx);
    } else {
      retFlag = createConsolidatedOrders(newTrx);
    } 
    deleteTable();
    dateStamp = new Timestamp(System.currentTimeMillis());
    this.log.info("Completed at :" + dateStamp);
    retValue = Msg.getMsg(getCtx(), "OrdersSelected") + " : " + noInserted + " \n" + Msg.getMsg(getCtx(), "OrdersCreated") + " : " + this.m_ordersCreated + " \n" + Msg.getMsg(getCtx(), "OrdersPrepared") + " : " + this.m_ordersPrepared + " \n";
    if (!this.m_isConsolidate)
      retValue = retValue + Msg.getMsg(getCtx(), "ComponentOrdersCreated") + " : " + this.m_componentOrdersCreated + " \n" + Msg.getMsg(getCtx(), "ComponentOrdersPrepared") + " : " + this.m_componentOrdersPrepared + " \n "; 
    if (!retFlag) {
      ValueNamePair pp = CLogger.retrieveError();
      if (pp != null)
        return retValue.concat("\n").concat(pp.getName()); 
    } 
    return retValue.concat("\n").concat(this.retErrorValue.toString());
  }
  
  protected int prepareTable() {
    if (this.p_T_MRPPlanImplement_ID != 0) {
      int noInserted = DB.getSQLValue(get_TrxName(), "SELECT count(*) FROM T_MRPPlanImplement WHERE T_MRPPlanImplement_ID=?", new Object[] { Integer.valueOf(this.p_T_MRPPlanImplement_ID) });
      return noInserted;
    } 
    this.p_T_MRPPlanImplement_ID = DB.getSQLValue(get_TrxName(), "SELECT t_mrpplanimplementid_seq.NEXTVAL FROM DUAL", new Object[0]);
    String insertSQL = "INSERT INTO T_MRPPlanImplement (T_MRPPlanImplement_ID, MRP_PlannedOrder_ID, Line) SELECT ?, ? , T_MRPPlanImplementLine_Seq.NEXTVAL FROM DUAL";
    ArrayList<Object> params = new ArrayList();
    if (this.p_MRP_PlannedOrder_ID == 0)
      this.p_MRP_PlannedOrder_ID = getRecord_ID(); 
    params.add(Integer.valueOf(this.p_T_MRPPlanImplement_ID));
    params.add(Integer.valueOf(this.p_MRP_PlannedOrder_ID));
   // int no = DB.executeUpdate(get_TrxName(), insertSQL, params.toArray());
    int no =DB.executeUpdateEx(insertSQL, params.toArray(), get_TrxName());
    this.log.finest(no + " records inserted for SQL: " + insertSQL);
    trxname.commit();
    return no;
  }
  
  private void deleteTable() {
    String deleteSQL = "DELETE FROM T_MRPPlanImplement WHERE T_MRPPlanImplement_ID=?";
  //  int no = DB.executeUpdate(get_TrxName(), deleteSQL, new Object[] { Integer.valueOf(this.p_T_MRPPlanImplement_ID) });
    int no =  DB.executeUpdateEx(deleteSQL, new Object[] { Integer.valueOf(this.p_T_MRPPlanImplement_ID) }, get_TrxName());
    this.log.fine("Deleted #" + no);
  }
  
  protected boolean createOrders(Trx newTrx) {
    CPreparedStatement cPreparedStatement = null;
    ArrayList<PO> workOrders = new ArrayList<PO>();
    ArrayList<MOrder> purchaseOrders = new ArrayList<MOrder>();
    String selectSQL = "SELECT MRP_PlannedOrder_ID FROM T_MRPPlanImplement  WHERE T_MRPPlanImplement_ID=? ORDER BY Line";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean isPODocTypeChecked = false;
    try {
      cPreparedStatement = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement.setInt(1, this.p_T_MRPPlanImplement_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        workOrders.clear();
        purchaseOrders.clear();
        int plannedOrderRootID = rs.getInt(1);
        MMRPPlannedOrder plannedOrderRoot = new MMRPPlannedOrder(getCtx(), plannedOrderRootID, newTrx.getTrxName());
        if (!isPODocTypeChecked) {
          MMRPPlanRun run = new MMRPPlanRun(getCtx(), plannedOrderRoot.getMRP_PlanRun_ID(), newTrx.getTrxName());
          MMRPPlan plan = new MMRPPlan(getCtx(), run.getMRP_Plan_ID(), newTrx.getTrxName());
          MDocType poDocType = MDocType.get(getCtx(), plan.getC_DocTypeTarget_ID());
          if (poDocType == null) {
            this.log.warning("PO document type is not valid");
            return false;
          } 
          if (!poDocType.isActive()) {
            this.log.warning("PO Document type used in the plan is not active. Update the Plan to use an active PO Document type and re-implement orders");
            this.log.saveError("Error", "PO document type used in the plan is not active");
            return false;
          } 
          isPODocTypeChecked = true;
        } 
        MProduct product = MProduct.get(getCtx(), plannedOrderRoot.getM_Product_ID());
        this.log.fine("Implementing Product " + product.getName() + "Order Date = " + plannedOrderRoot.getDateOrdered());
        if (plannedOrderRoot.getPlannedOrderStatus().equalsIgnoreCase(X_MRP_PlannedOrder.PLANNEDORDERSTATUS_Implemented)) {
          this.log.fine("Product " + product.getName() + "is already implemented");
          continue;
        } 
        boolean isWO = false;
        if (plannedOrderRoot.getOrderType().equalsIgnoreCase(X_MRP_PlannedOrder.ORDERTYPE_WorkOrder))
          isWO = true; 
        boolean isCreated = false;
        if (isWO) {
          isCreated = createWorkOrder(plannedOrderRoot, newTrx.getTrxName(), workOrders);
        } else {
          isCreated = createPO(plannedOrderRoot, newTrx, purchaseOrders);
        } 
        if (!isCreated) {
//          newTrx.rollback();
        	trxname.rollback();

        	ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            this.retErrorValue.append("\n").append("Failed Creation: " + product.getName() + ": ").append(pp.getName()); 
          continue;
        } 
        int componentOrdersCreatedSoFar = this.m_componentOrdersCreated;
        boolean created = createChildOrders(plannedOrderRoot, workOrders, purchaseOrders, newTrx);
        if (!created) {
//          newTrx.rollback();
        	trxname.rollback();

        	this.m_componentOrdersCreated = componentOrdersCreatedSoFar;
          ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            this.retErrorValue.append("\n").append("Failed Creation: " + product.getName() + ": ").append(pp.getName()); 
          continue;
        } 
        if (isWO) {
          plannedOrderRoot.setMFG_WorkOrder_ID(((PO)workOrders.get(0)).get_ID());
        } else {
          plannedOrderRoot.setC_Order_ID(((MOrder)purchaseOrders.get(0)).getC_Order_ID());
        } 
        plannedOrderRoot.setPlannedOrderStatus(X_MRP_PlannedOrder.PLANNEDORDERSTATUS_Implemented);
        plannedOrderRoot.save(newTrx.getTrxName());
//        newTrx.commit();
        trxname.commit();

        this.m_ordersCreated++;
        boolean prepared = prepareOrders(workOrders, purchaseOrders, trxname);
        if (!prepared) {
//          newTrx.rollback();
        	trxname.rollback();

        	ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            this.retErrorValue.append("\n").append(pp.getName()); 
          continue;
        } 
        plannedOrderRoot.save(newTrx.getTrxName());
//        newTrx.commit();
        trxname.commit();

      } 
//      newTrx.close();
      trxname.close();

      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
//      newTrx.rollback();
//      newTrx.close();
    	trxname.rollback();
    	trxname.close();
      this.log.log(Level.SEVERE, selectSQL + " - Param1=" + this.p_T_MRPPlanImplement_ID + " [" + get_TrxName() + "]", e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return true;
  }
  
  protected boolean createConsolidatedOrders(Trx newTrx) {
    CPreparedStatement cPreparedStatement = null;
    int ordersCreated = 0;
    ArrayList<PO> workOrders = new ArrayList<PO>();
    ArrayList<MOrder> orders = new ArrayList<MOrder>();
    Map<MOrder, ArrayList<MOrderLine>> orderMap = new HashMap<MOrder, ArrayList<MOrderLine>>();
    String selectSQL = "SELECT  MIN( m.MRP_PlannedOrder_ID) , m.M_Product_ID, SUM( m.QtyOrdered), m.DateOrdered  FROM MRP_PlannedOrder m JOIN T_MRPPlanImplement t ON ( t.T_MRPPlanImplement_ID= ? AND (m.MRP_PlannedOrder_ID=t.MRP_PlannedOrder_ID OR m.MRP_PlannedOrder_Root_ID=t.MRP_PlannedOrder_ID)) WHERE m.PlannedOrderStatus='N'and m.OrderType='P' GROUP BY m.M_Product_ID, m.DateOrdered ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean isPODocTypeChecked = false;
    try {
      cPreparedStatement = DB.prepareStatement(selectSQL, get_TrxName());
      cPreparedStatement.setInt(1, this.p_T_MRPPlanImplement_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        int plannedOrderID = rs.getInt(1);
        int productID = rs.getInt(2);
        Timestamp dateOrdered = rs.getTimestamp(4);
        MMRPPlannedOrder plannedOrder = new MMRPPlannedOrder(getCtx(), plannedOrderID, newTrx.getTrxName());
        if (!isPODocTypeChecked) {
          MMRPPlanRun run = new MMRPPlanRun(getCtx(), plannedOrder.getMRP_PlanRun_ID(), newTrx.getTrxName());
          MMRPPlan plan = new MMRPPlan(getCtx(), run.getMRP_Plan_ID(), newTrx.getTrxName());
          MDocType poDocType = MDocType.get(getCtx(), plan.getC_DocTypeTarget_ID());
          if (poDocType == null) {
            this.log.warning("PO document type is not valid");
            return false;
          } 
          if (!poDocType.isActive()) {
            this.log.warning("PO Document type used in the plan is not active. Update the Plan to use an active PO Document type and re-implement orders");
            this.log.saveError("Error", "PO document type used in the plan is not active");
            return false;
          } 
          isPODocTypeChecked = true;
        } 
        MProduct product = MProduct.get(getCtx(), plannedOrder.getM_Product_ID());
        this.log.fine("Implementing Product " + product.getName() + "Order Date = " + plannedOrder.getDateOrdered());
        boolean isCreated = false;
        BigDecimal orderedQty = rs.getBigDecimal(3);
        plannedOrder.setQtyOrdered(orderedQty);
        isCreated = createConsolidatedPO(plannedOrder, newTrx, orderMap);
        if (!isCreated) {
//          newTrx.rollback();
        	trxname.rollback();

        	ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            this.retErrorValue.append("\n").append("Failed Creation: " + product.getName() + ": ").append(pp.getName()); 
          return false;
        } 
        ordersCreated++;
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
//      newTrx.rollback();
//      newTrx.close();
    	trxname.rollback();
    	trxname.close();
      this.log.log(Level.SEVERE, selectSQL + " - Param1=" + this.p_T_MRPPlanImplement_ID + " [" + get_TrxName() + "]", e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    try {
      String selectSQL1 = "SELECT m.MRP_PlannedOrder_ID  FROM MRP_PlannedOrder m JOIN T_MRPPlanImplement t ON( t.T_MRPPlanImplement_ID= ? AND (m.MRP_PlannedOrder_ID=t.MRP_PlannedOrder_ID OR m.MRP_PlannedOrder_Root_ID=t.MRP_PlannedOrder_ID))WHERE m.PlannedOrderStatus='N' AND m.OrderType='W' ";
      cPreparedStatement = DB.prepareStatement(selectSQL1, get_TrxName());
      cPreparedStatement.setInt(1, this.p_T_MRPPlanImplement_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        int plannedOrderID = rs.getInt(1);
        MMRPPlannedOrder plannedOrder = new MMRPPlannedOrder(getCtx(), plannedOrderID, newTrx.getTrxName());
        MProduct product = MProduct.get(getCtx(), plannedOrder.getM_Product_ID());
        this.log.fine("Implementing Product " + product.getName() + "Order Date = " + plannedOrder.getDateOrdered());
        boolean isCreated = false;
        isCreated = createWorkOrder(plannedOrder, newTrx.getTrxName(), workOrders);
        if (!isCreated) {
//          newTrx.rollback();
        	trxname.rollback();
        	ValueNamePair pp = CLogger.retrieveError();
          if (pp != null)
            this.retErrorValue.append("\n").append("Failed Creation: " + product.getName() + " :").append(pp.getName()); 
          return false;
        } 
        ordersCreated++;
      } 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
//      newTrx.rollback();
//      newTrx.close();
    	trxname.rollback();
    	trxname.close();
      this.log.log(Level.SEVERE, selectSQL + " - Param1=" + this.p_T_MRPPlanImplement_ID + " [" + get_TrxName() + "]", e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    try {
//      saveOrders(newTrx, orderMap);
//      PO.saveAll(newTrx, workOrders);
    	 saveOrders(trxname, orderMap);
         PO.save(newTrx.getTrxName());
    } catch (Exception e) {
//      newTrx.rollback();
//      newTrx.close();
    	trxname.rollback();
    	trxname.close();
      ValueNamePair pp = CLogger.retrieveError();
      if (pp != null)
        this.retErrorValue.append("\n").append(pp.getName()); 
      return false;
    } 
    orders = new ArrayList<MOrder>(orderMap.keySet());
    for (MOrder order : orders) {
      MOrderLine[] list = order.getLines();
      for (MOrderLine line : list) {
        String updateSQL = "UPDATE MRP_PlannedOrder set PlannedOrderStatus ='I' , C_Order_ID =? WHERE  MRP_PlannedOrder_ID IN (SELECT   m.MRP_PlannedOrder_ID  FROM MRP_PlannedOrder m JOIN T_MRPPlanImplement t ON ( t.T_MRPPlanImplement_ID= ? AND (m.MRP_PlannedOrder_ID=t.MRP_PlannedOrder_ID OR m.MRP_PlannedOrder_Root_ID=t.MRP_PlannedOrder_ID)) WHERE m.PlannedOrderStatus='N'and m.OrderType='P'and m.M_Product_ID=? AND m.DateOrdered= ? ) ";
       // DB.executeUpdate(newTrx, updateSQL, new Object[] { Integer.valueOf(order.getC_Order_ID()), Integer.valueOf(this.p_T_MRPPlanImplement_ID), Integer.valueOf(line.getM_Product_ID()), order.getDateOrdered() });
     DB.executeUpdateEx(updateSQL, new Object[] { Integer.valueOf(order.getC_Order_ID()), Integer.valueOf(this.p_T_MRPPlanImplement_ID), Integer.valueOf(line.getM_Product_ID()), order.getDateOrdered() }, newTrx.getTrxName());
      } 
    } 
    Class<?> mWOClass = getMWorkOrderClass();
    for (PO workorder : workOrders) {
      if (mWOClass == null)
        return false; 
      Integer plannedID = null;
      try {
        Method getMRPPlannedOrderID = mWOClass.getMethod("getVarMRP_PlannedOrder_ID", (Class[])null);
        plannedID = (Integer)getMRPPlannedOrderID.invoke(workorder, new Object[0]);
      } catch (Exception e) {
        this.log.warning("Error instantiating constructor for org.cyprusbrs.cmfg.model.MWorkOrder:" + e.toString());
        return false;
      } 
      String updateSQL = "UPDATE MRP_PlannedOrder set PlannedOrderStatus ='I' , M_WorkOrder_ID =? WHERE  MRP_PlannedOrder_ID = ? ";
 //     DB.executeUpdate(newTrx, updateSQL, new Object[] { Integer.valueOf(workorder.get_ValueAsInt("M_WorkOrder_ID")), Integer.valueOf(plannedID.intValue()) });
    DB.executeUpdateEx(updateSQL, new Object[] { Integer.valueOf(workorder.get_ValueAsInt("M_WorkOrder_ID")), Integer.valueOf(plannedID.intValue()) }, newTrx.getTrxName());
    } 
//    newTrx.commit();
    trxname.commit();
    this.m_ordersCreated = ordersCreated;
    boolean prepared = prepareOrders(workOrders, orders, trxname);
    if (!prepared) {
//      newTrx.rollback();
    	trxname.rollback();
    	ValueNamePair pp = CLogger.retrieveError();
      if (pp != null)
        this.retErrorValue.append("\n").append(pp.getName()); 
      return false;
    } 
//    newTrx.commit();
//    newTrx.close();
    trxname.commit();
    trxname.close();
    return true;
  }
  
  private boolean prepareOrders(ArrayList<PO> workOrders, ArrayList<MOrder> purchaseOrders, Trx newTrx) {
    int count = 0;
    for (PO workorder : workOrders) {
      count++;
      boolean isPrepared = prepareWorkOrder(newTrx, workorder);
      if (!isPrepared) {
        newTrx.rollback();
        ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          this.retErrorValue.append("\n").append(pp.getName()); 
        continue;
      } 
      if (!this.m_isConsolidate) {
        if (count == 1) {
          this.m_ordersPrepared++;
        } else {
          this.m_componentOrdersPrepared++;
        } 
      } else {
        this.m_ordersPrepared++;
      } 
      newTrx.commit();
    } 
    count = 0;
    for (MOrder purchaseOrder : purchaseOrders) {
      count++;
      boolean isPrepared = preparePurchaseOrder(newTrx, purchaseOrder);
      if (!isPrepared) {
        newTrx.rollback();
        ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          this.retErrorValue.append("\n").append(pp.getName()); 
        continue;
      } 
      if (!this.m_isConsolidate) {
        if (count == 1 && workOrders.size() == 0) {
          this.m_ordersPrepared++;
        } else {
          this.m_componentOrdersPrepared++;
        } 
      } else {
        this.m_ordersPrepared++;
      } 
      newTrx.commit();
    } 
    return true;
  }
  
  private boolean preparePurchaseOrder(Trx newTrx, MOrder purchaseOrder) {
	  DocumentEngine engine = new DocumentEngine((DocAction)purchaseOrder, "PR");
	  if (!engine.processIt("PR")) {
   // if (!DocumentEngine.processIt((DocAction)purchaseOrder, "PR")) {
      this.retErrorValue.append("\n").append(purchaseOrder.getProcessMsg());
      newTrx.rollback();
      return false;
    } 
//    purchaseOrder.save(newTrx);
    purchaseOrder.save(get_TrxName());
    newTrx.commit();
    return true;
  }
  
  private boolean prepareWorkOrder(Trx newTrx, PO workorder) {
    Class<?> createWOClass = getCreateWorkOrderClass();
    if (createWOClass == null)
      return false; 
    try {
      Method processIt = createWOClass.getMethod("processIt", new Class[] { int.class, String.class, Trx.class, Properties.class });
      int workOrderID = workorder.get_ID();
      boolean isPrepared = ((Boolean)processIt.invoke(createWOClass, new Object[] { Integer.valueOf(workOrderID), X_MFG_WorkOrder.DOCACTION_Prepare, newTrx, getCtx() })).booleanValue();
      if (!isPrepared) {
        ValueNamePair pp = CLogger.retrieveError();
        if (pp != null)
          this.retErrorValue.append("\n").append(pp.getName()); 
        newTrx.rollback();
        return false;
      } 
      newTrx.commit();
      return true;
    } catch (Exception e) {
      this.log.warning("Error in Preparing");
      return false;
    } 
  }
  
  private boolean createChildOrders(MMRPPlannedOrder plannedOrderRoot, ArrayList<PO> workOrders, ArrayList<MOrder> purchaseOrders, Trx newTrx)  {
    CPreparedStatement cPreparedStatement = null;
    int plannedOrderID = 0;
    String selectSQL = "SELECT MRP_PlannedOrder_ID FROM MRP_PlannedOrder  WHERE MRP_PlannedOrder_Root_ID =? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(selectSQL, newTrx.getTrxName());
      cPreparedStatement.setInt(1, plannedOrderRoot.getMRP_PlannedOrder_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        plannedOrderID = rs.getInt(1);
        MMRPPlannedOrder plannedOrder = new MMRPPlannedOrder(getCtx(), plannedOrderID, newTrx.getTrxName());
        if (plannedOrder.getPlannedOrderStatus().equalsIgnoreCase(X_MRP_PlannedOrder.PLANNEDORDERSTATUS_Implemented))
          continue; 
        boolean isWO = false;
        if (plannedOrder.getOrderType().equalsIgnoreCase(X_MRP_PlannedOrder.ORDERTYPE_WorkOrder))
          isWO = true; 
        boolean isCreated = false;
        if (isWO) {
          isCreated = createWorkOrder(plannedOrder, newTrx.getTrxName(), workOrders);
        } else {
          isCreated = createPO(plannedOrder, newTrx, purchaseOrders);
        } 
        if (!isCreated)
          return false; 
        if (isWO) {
          plannedOrder.setMFG_WorkOrder_ID(((PO)workOrders.get(workOrders.size() - 1)).get_ID());
        } else {
          plannedOrder.setC_Order_ID(((MOrder)purchaseOrders.get(purchaseOrders.size() - 1)).getC_Order_ID());
        } 
        plannedOrder.setPlannedOrderStatus(X_MRP_PlannedOrder.PLANNEDORDERSTATUS_Implemented);
        plannedOrder.save(newTrx.getTrxName());
        this.m_componentOrdersCreated++;
      } 
    } catch (Exception e) {
      this.log.log(Level.SEVERE, selectSQL + " - Param1=" + this.p_T_MRPPlanImplement_ID + " [" + get_TrxName() + "]", e);
      return false;
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return true;
  }
  
  private boolean createConsolidatedPO(MMRPPlannedOrder plannedOrder, Trx trx, Map<MOrder, ArrayList<MOrderLine>> orderMap) {
    MOrder order = new MOrder(getCtx(), 0, trx.getTrxName());
    MMRPPlanRun run = new MMRPPlanRun(getCtx(), plannedOrder.getMRP_PlanRun_ID(), trx.getTrxName());
    MMRPPlan plan = new MMRPPlan(getCtx(), run.getMRP_Plan_ID(), trx.getTrxName());
    int C_BPartner_ID = 0;
    int promisedDeliveryLeadTime = 0;
    int productID = plannedOrder.getM_Product_ID();
    MProduct product = MProduct.get(getCtx(), productID);
    C_BPartner_ID = MMRPProductAudit.getBPartner(run.getMRP_PlanRun_ID(), productID, trx.getTrxName());
    if (C_BPartner_ID == 0) {
      MProductPO[] ppos = MProductPO.getOfProduct(getCtx(), productID, trx.getTrxName());
      for (MProductPO element : ppos) {
        if (element.isCurrentVendor() && element.getC_BPartner_ID() != 0) {
          promisedDeliveryLeadTime = element.getDeliveryTime_Promised();
          C_BPartner_ID = element.getC_BPartner_ID();
          break;
        } 
      } 
      if (C_BPartner_ID == 0 && ppos.length > 0) {
        this.log.info("No Current Active Vendor Found for product " + product.getName());
        C_BPartner_ID = ppos[0].getC_BPartner_ID();
        promisedDeliveryLeadTime = ppos[0].getDeliveryTime_Promised();
      } 
      if (C_BPartner_ID == 0) {
        this.log.saveError("Error", "No Vendor Found for product " + product.getName());
        return false;
      } 
    } 
    MProductPO productPO = MProductPO.getOfVendorProduct(getCtx(), C_BPartner_ID, productID, trx);
    BigDecimal orderMinQty = productPO.getOrder_Min();
    BigDecimal orderPack = productPO.getOrder_Pack();
    if (productPO != null)
      promisedDeliveryLeadTime = productPO.getDeliveryTime_Promised(); 
    MBPartner bpartner = MBPartner.get(getCtx(), C_BPartner_ID);
    if (bpartner == null || !bpartner.isActive()) {
      this.log.saveError("Error", "No Active Vendor Found for product " + product.getName());
      return false;
    } 
    order.setIsSOTrx(false);
    order.setM_Warehouse_ID(plan.getM_Warehouse_ID());
    order.setClientOrg((PO)plannedOrder);
    if (plan.getC_DocTypeTarget_ID() != 0) {
      order.setC_DocTypeTarget_ID(plan.getC_DocTypeTarget_ID());
    } else {
      order.setC_DocTypeTarget_ID();
    } 
    order.setBPartner(bpartner);
    order.setDateOrdered(plannedOrder.getDateOrdered());
    order.setDatePromised(TimeUtil.addDays(plannedOrder.getDateOrdered(), promisedDeliveryLeadTime));
    if (bpartner.getPO_PriceList_ID() == 0)
      order.setM_PriceList_ID(plan.getM_PriceList_ID()); 
    ArrayList<MOrderLine> orderLinesToSave = new ArrayList<MOrderLine>();
//    MOrderLine orderLine = new MOrderLine(order, 1);
    MOrderLine orderLine = new MOrderLine(order);

    orderLine.setProduct(product);
    orderLine.setClientOrg((PO)plannedOrder);
    orderLine.setM_Warehouse_ID(plan.getM_Warehouse_ID());
    BigDecimal qtyToOrder = null;
    if (orderMinQty.compareTo(plannedOrder.getQtyOrdered()) == 1) {
      qtyToOrder = orderMinQty;
    } else {
      qtyToOrder = plannedOrder.getQtyOrdered();
    } 
    if (orderPack.compareTo(Env.ZERO) > 0 && !qtyToOrder.remainder(orderPack).equals(Env.ZERO))
      qtyToOrder = qtyToOrder.subtract(qtyToOrder.remainder(orderPack)).add(orderPack); 
    orderLine.setQty(qtyToOrder);
    orderLine.setDateOrdered(plannedOrder.getDateOrdered());
    orderLine.setDatePromised(TimeUtil.addDays(plannedOrder.getDateOrdered(), promisedDeliveryLeadTime));
    orderLinesToSave.add(orderLine);
    orderMap.put(order, orderLinesToSave);
    return true;
  }
  //@Anil22122021
 // private boolean createPO(MMRPPlannedOrder plannedOrderRoot, String trx, ArrayList<MOrder> purchaseOrders) throws cyprusbrsUserException, cyprusbrsSystemException {
  private boolean createPO(MMRPPlannedOrder plannedOrderRoot, Trx trx, ArrayList<MOrder> purchaseOrders)  {	   
  MOrder order = new MOrder(getCtx(), 0, trx.getTrxName());
    MMRPPlanRun run = new MMRPPlanRun(getCtx(), plannedOrderRoot.getMRP_PlanRun_ID(), trx.getTrxName());
    MMRPPlan plan = new MMRPPlan(getCtx(), run.getMRP_Plan_ID(), trx.getTrxName());
    int C_BPartner_ID = 0;
    int promisedDeliveryLeadTime = 0;
    int productID = plannedOrderRoot.getM_Product_ID();
    MProduct product = MProduct.get(getCtx(), productID);
    C_BPartner_ID = MMRPProductAudit.getBPartner(run.getMRP_PlanRun_ID(), productID, trx.getTrxName());
    if (C_BPartner_ID == 0) {
      MProductPO[] ppos = MProductPO.getOfProduct(getCtx(), productID, trx.getTrxName());
      for (MProductPO element : ppos) {
        if (element.isCurrentVendor() && element.getC_BPartner_ID() != 0) {
          promisedDeliveryLeadTime = element.getDeliveryTime_Promised();
          C_BPartner_ID = element.getC_BPartner_ID();
          break;
        } 
      } 
      if (C_BPartner_ID == 0 && ppos.length > 0) {
        this.log.info("No Current Active Vendor Found for product " + product.getName());
        C_BPartner_ID = ppos[0].getC_BPartner_ID();
        promisedDeliveryLeadTime = ppos[0].getDeliveryTime_Promised();
      } 
      if (C_BPartner_ID == 0) {
        this.log.saveError("Error", "No Vendor Found for product " + product.getName());
        return false;
      } 
    } 
    MProductPO productPO = MProductPO.getOfVendorProduct(getCtx(), C_BPartner_ID, productID, trx);
    BigDecimal orderMinQty = productPO.getOrder_Min();
    BigDecimal orderPack = productPO.getOrder_Pack();
    if (productPO != null)
      promisedDeliveryLeadTime = productPO.getDeliveryTime_Promised(); 
    MBPartner bpartner = MBPartner.get(getCtx(), C_BPartner_ID);
    if (bpartner == null || !bpartner.isActive()) {
      this.log.saveError("Error", "No Active Vendor Found for product " + product.getName());
      return false;
    } 
    order.setIsSOTrx(false);
    order.setM_Warehouse_ID(plan.getM_Warehouse_ID());
    order.setClientOrg((PO)plannedOrderRoot);
    if (plan.getC_DocTypeTarget_ID() != 0) {
      order.setC_DocTypeTarget_ID(plan.getC_DocTypeTarget_ID());
    } else {
      order.setC_DocTypeTarget_ID();
    } 
    order.setBPartner(bpartner);
    order.setDateOrdered(plannedOrderRoot.getDateOrdered());
    order.setDatePromised(TimeUtil.addDays(plannedOrderRoot.getDateOrdered(), promisedDeliveryLeadTime));
    if (bpartner.getPO_PriceList_ID() == 0)
      order.setM_PriceList_ID(plan.getM_PriceList_ID()); 
    if (!order.save(trx.getTrxName())) {
    //	trx.rollback();
    	
      return false;
    } 
    MOrderLine orderLine = new MOrderLine(order);
    orderLine.setProduct(product);
    orderLine.setClientOrg((PO)plannedOrderRoot);
    orderLine.setM_Warehouse_ID(plan.getM_Warehouse_ID());
    BigDecimal qtyToOrder = null;
    if (orderMinQty.compareTo(plannedOrderRoot.getQtyOrdered()) == 1) {
      qtyToOrder = orderMinQty;
    } else {
      qtyToOrder = plannedOrderRoot.getQtyOrdered();
    } 
    if (orderPack.compareTo(Env.ZERO) > 0 && !qtyToOrder.remainder(orderPack).equals(Env.ZERO))
      qtyToOrder = qtyToOrder.subtract(qtyToOrder.remainder(orderPack)).add(orderPack); 
    orderLine.setQty(qtyToOrder);
    orderLine.setDateOrdered(plannedOrderRoot.getDateOrdered());
    orderLine.setDatePromised(TimeUtil.addDays(plannedOrderRoot.getDateOrdered(), promisedDeliveryLeadTime));
    if (!orderLine.save(trx.getTrxName())) {
//      trx.rollback();
    	trxname.rollback();
      return false;
    } 
    order.setIsActive(order.isActive());
    order.save(trx.getTrxName());
    purchaseOrders.add(order);
    return true;
  }
  
  private boolean createWorkOrder(MMRPPlannedOrder plannedOrder, String trx, ArrayList<PO> workOrders) {
    Class<?> createWOClass = getCreateWorkOrderClass();
    if (createWOClass == null)
      return false; 
    try {
      Method newWorkOrder = createWOClass.getMethod("newWorkOrder", new Class[] { 
            int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, Timestamp.class, Timestamp.class, 
            String.class, BigDecimal.class, int.class, int.class, int.class, Properties.class, Trx.class });
      MMRPPlanRun planRun = new MMRPPlanRun(getCtx(), plannedOrder.getMRP_PlanRun_ID(), trx);
      MMRPPlan plan = new MMRPPlan(getCtx(), planRun.getMRP_Plan_ID(), trx);
      int woClassID = plan.getMFG_WorkOrderClass_ID();
      int locatorID = plan.getM_Locator_ID();
      int routingID = 0;
      int bomID = 0;
      int planRunID = planRun.getMRP_PlanRun_ID();
      int wareHouseID = plan.getM_Warehouse_ID();
      int productID = plannedOrder.getM_Product_ID();
      MProduct product = new MProduct(getCtx(), productID, trx);
      MMRPPlannedDemand demand = new MMRPPlannedDemand(getCtx(), plannedOrder.getMRP_PlannedDemand_ID(), trx);
      int uomID = demand.getC_UOM_ID();
      Timestamp dateScheduled = plannedOrder.getDateOrdered();
      BigDecimal qty = plannedOrder.getQtyOrdered();
      this.log.info("Calling the API(CreateWorkOrder) to create WorkOrder for the product " + product.getName());
      PO workOrder = (PO)newWorkOrder.invoke(createWOClass, new Object[] { 
            Integer.valueOf(woClassID), Integer.valueOf(wareHouseID), Integer.valueOf(locatorID), Integer.valueOf(planRunID), Integer.valueOf(productID), Integer.valueOf(uomID), Integer.valueOf(routingID), Integer.valueOf(bomID), dateScheduled, dateScheduled, 
            X_MFG_WorkOrder.DOCACTION_Prepare, qty, Integer.valueOf(plannedOrder.getAD_Client_ID()), Integer.valueOf(plannedOrder.getAD_Org_ID()), Integer.valueOf(plannedOrder.getMRP_PlannedOrder_ID()), getCtx(), trx });
      if (workOrder == null)
        return false; 
      workOrders.add(workOrder);
    } catch (Exception e) {
      this.log.warning("Error instantiating constructor for org.cyprusbrs.cmfg.model.MWorkOrder:" + e.toString());
      return false;
    } 
    return true;
  }
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("T_MRPPlanImplement_ID")) {
          this.p_T_MRPPlanImplement_ID = element.getParameterAsInt();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
  }
  
  private Class<?> getCreateWorkOrderClass() {
    String className = "org.cyprusbrs.cmfg.util.CreateWorkOrder";
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (Exception e) {
      this.log.warning("Error getting class for " + className + ": - " + e.toString());
      return null;
    } 
  }
  
  private Class<?> getMWorkOrderClass() {
    String className = "org.cyprusbrs.cmfg.model.MWorkOrder";
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (Exception e) {
      this.log.warning("Error getting class for " + className + ": - " + e.toString());
      return null;
    } 
  }
  
  public void saveOrders(Trx newTrx, Map<MOrder, ArrayList<MOrderLine>> orderMap) throws Exception {
    if (orderMap.isEmpty())
      return; 
    ArrayList<MOrder> ordersToSave = new ArrayList<MOrder>(orderMap.keySet());
//    if (!PO.saveAll(newTrx, ordersToSave))
    if (!PO.save(get_TrxName()))
    throw new Exception("Could not save Orders"); 
    ArrayList<MOrderLine> orderLinesToSave = new ArrayList<MOrderLine>();
    for (Map.Entry<MOrder, ArrayList<MOrderLine>> entry : orderMap.entrySet()) {
      MOrder order = entry.getKey();
      for (MOrderLine orderLine : entry.getValue()) {
        orderLine.setC_Order_ID(order.getC_Order_ID());
        orderLinesToSave.add(orderLine);
      } 
    } 
//    if (!PO.saveAll(newTrx, orderLinesToSave))
    if (!PO.save(get_TrxName())) 
    throw new Exception("Could not save Orders"); 
    for (MOrder order : ordersToSave)
      order.setIsActive(order.isActive()); 
//    if (!PO.saveAll(newTrx, ordersToSave))
    if (!PO.save(get_TrxName()))
      throw new Exception("Could not save Orders"); 
  }
}
