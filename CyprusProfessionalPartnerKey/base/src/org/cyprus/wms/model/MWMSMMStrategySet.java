package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.wms.util.MWMSMContext;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOMConversion;
//import org.cyprus.wms.util.MMContext;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Trx;

public class MWMSMMStrategySet extends X_WMS_MMStrategySet {
  private static final CLogger log = CLogger.getCLogger(MWMSMMStrategySet.class);
  
  private static final long serialVersionUID = 1L;
  
  private MWMSMContext m_mmContext = null;
  
  private static CLogger s_log = CLogger.getCLogger(MWMSMMStrategySet.class);
  
  private MWMSMMStrategySetLine[] m_setLines;
  
  public MWMSMMStrategySet(Properties ctx, int M_MMStrategySet_ID, String trx) {
    super(ctx, M_MMStrategySet_ID, trx);
    this.m_setLines = null;
    if (this.m_mmContext == null)
      this.m_mmContext = new MWMSMContext(); 
  }
  
  public MWMSMMStrategySet(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.m_setLines = null;
    if (this.m_mmContext == null)
      this.m_mmContext = new MWMSMContext(); 
  }
  
  public MWMSMMStrategySetLine[] getLines() throws Exception {
    CPreparedStatement cPreparedStatement=null;
    if (this.m_setLines != null && this.m_setLines.length > 0)
      return this.m_setLines; 
    ArrayList<MWMSMMStrategySetLine> list = new ArrayList<MWMSMMStrategySetLine>();
   // String sql = "SELECT * FROM M_MMStrategySetLine WHERE M_MMStrategySet_ID=? AND IsActive='Y' ORDER BY SeqNo";
    String sql = "SELECT * FROM WMS_MMStrategySetLine WHERE WMS_MMStrategySet_ID=? AND IsActive='Y' ORDER BY SeqNo";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
      cPreparedStatement.setInt(1, getWMS_MMStrategySet_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MWMSMMStrategySetLine sl = new MWMSMMStrategySetLine(getCtx(), rs, get_TrxName());
        list.add(sl);
      } 
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    this.m_setLines = new MWMSMMStrategySetLine[list.size()];
    list.toArray(this.m_setLines);
    return this.m_setLines;
  }
  
  public static MWMSMMStrategySet getPutawayStrategySet(Properties ctx, int M_Warehouse_ID, String trx) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    MWMSMMStrategySet retValue = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
  //  String sql = "SELECT * FROM M_MMStrategySet s WHERE MMType='PUT' AND M_Warehouse_ID=?AND IsActive='Y'";
    String sql = "SELECT * FROM WMS_MMStrategySet s WHERE MMType='PUT' AND M_Warehouse_ID=?AND IsActive='Y'";
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, M_Warehouse_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = new MWMSMMStrategySet(ctx, rs, trx); 
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return retValue;
  }
  
  public static MWMSMMStrategySet getPickStrategySet(Properties ctx, int M_Warehouse_ID, String trx) throws Exception {
    CPreparedStatement cPreparedStatement=null;
    MWMSMMStrategySet retValue = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
   // String sql = "SELECT * FROM M_MMStrategySet s WHERE MMType='PCK' AND M_Warehouse_ID=?AND IsActive='Y'";
    String sql = "SELECT * FROM WMS_MMStrategySet s WHERE MMType='PCK' AND M_Warehouse_ID=?AND IsActive='Y'";
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, M_Warehouse_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = new MWMSMMStrategySet(ctx, rs, trx); 
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql, e);
      throw new Exception(Msg.translate(ctx, "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return retValue;
  }
  
  public MWMSTaskList[] executePutawayStrategySet(Trx p_trx, PreparedStatement pstmt, int M_DestZone_ID, int C_DocType_ID, String docAction) throws Exception {
    if (!getMMType().equals(X_WMS_MMStrategy.MMTYPE_MaterialPutaway))
      throw new IllegalArgumentException("Strategy set is not of type Putaway"); 
    MWMSMMStrategySetLine[] setLines = getLines();
    ResultSet rs = null;
    try {
      rs = pstmt.executeQuery();
      while (rs.next()) {
        int index = 1;
        int M_Product_ID = rs.getInt(index++);
        int M_Locator_ID = rs.getInt(index++);
        int M_AttributeSetInstance_ID = rs.getInt(index++);
        BigDecimal qtyOnhand = rs.getBigDecimal(index++);
        BigDecimal qtyDedicated = rs.getBigDecimal(index++);
        BigDecimal qtyAllocated = rs.getBigDecimal(index++);
        BigDecimal qtyOpen = qtyOnhand.subtract(qtyAllocated).subtract(qtyDedicated);
        if (qtyOpen.compareTo(BigDecimal.ZERO) <= 0) {
          log.finest("Open Qty is zero, skipping...");
          continue;
        } 
        MInOutLine receipt = null;
        if (M_AttributeSetInstance_ID > 0)
          receipt = getM_InOutLineOf(getCtx(), M_AttributeSetInstance_ID, get_TrxName()); 
        MProduct product = new MProduct(getCtx(), M_Product_ID, get_TrxName());
        int C_UOM_ID = product.getC_UOM_ID();
        int M_InOutLine_ID = 0;
        int C_Order_ID = 0;
        int C_OrderLine_ID = 0;
        int C_BPartner_ID = 0;
        if (receipt != null) {
          M_InOutLine_ID = receipt.getM_InOutLine_ID();
          C_OrderLine_ID = receipt.getC_OrderLine_ID();
          C_BPartner_ID = receipt.getParent().getC_BPartner_ID();
          if (C_UOM_ID != receipt.getC_UOM_ID()) {
            C_UOM_ID = receipt.getC_UOM_ID();
            qtyOpen = MUOMConversion.convertProductTo(getCtx(), M_Product_ID, C_UOM_ID, qtyOpen);
          } 
        } else {
          log.fine("Receipt not found");
        } 
        int M_Zone_ID = 0;
        MWMSZone zone = MWMSZone.getOfLocator(getCtx(), getM_Warehouse_ID(), M_Locator_ID);
        if (zone != null)
          M_Zone_ID = zone.getWMS_Zone_ID(); 
        log.fine("Product : " + M_Product_ID + " Locator : " + M_Locator_ID + " UOM : " + C_UOM_ID + " OrderLine : " + C_OrderLine_ID + " Partner : " + C_BPartner_ID + " Zone : " + M_Zone_ID);
        for (MWMSMMStrategySetLine line : setLines) {
          if (line.evaluateCriteria(C_BPartner_ID, M_Locator_ID, M_Product_ID, M_Zone_ID, 0)) {
            MWMSMMStrategy strategy = line.getStrategy();
            qtyOpen = strategy.executePutawayStrategy(this.m_mmContext, M_Product_ID, M_AttributeSetInstance_ID, qtyOpen, C_UOM_ID, M_InOutLine_ID, C_Order_ID, C_OrderLine_ID, M_DestZone_ID, M_Locator_ID, C_DocType_ID, docAction);
            if (qtyOpen.compareTo(BigDecimal.ZERO) <= 0 || !isEvaluateAllStrategies())
              break; 
          } 
        } 
        if (qtyOpen.signum() > 0 && getM_Locator_ID() != 0 && getM_Locator_ID() != M_Locator_ID)
          if (!this.m_mmContext.addTask(getM_Warehouse_ID(), M_Locator_ID, getM_Locator_ID(), M_Product_ID, M_AttributeSetInstance_ID, C_Order_ID, C_OrderLine_ID, 0, qtyOpen, C_UOM_ID, C_DocType_ID, 0, M_InOutLine_ID)) {
            log.saveError("Error", "Could not generate task");
            p_trx.rollback();
          }  
      } 
    } catch (SQLException e) {
      log.log(Level.SEVERE, "", e);
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } finally {
      DB.close(rs);
      DB.close(pstmt);
    } 
    this.m_mmContext.confirmTasks(getCtx(), docAction, get_TrxName());
    MWMSTaskList[] taskList = new MWMSTaskList[this.m_mmContext.m_TaskLists.size()];
    this.m_mmContext.m_TaskLists.toArray((Object[])taskList);
    return taskList;
  }
  
  public BigDecimal executePickStrategySet(String waveLineType, int LineLineRef_ID, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID) throws Exception {
    if (!getMMType().equals(X_WMS_MMStrategy.MMTYPE_MaterialPicking))
      throw new IllegalArgumentException("Strategy set is not of type Pick"); 
    MWMSMMStrategySetLine[] setLines = getLines();
    MWMSZone zone = MWMSZone.getOfLocator(getCtx(), getM_Warehouse_ID(), destM_Locator_ID);
    int M_Zone_ID = 0;
    if (zone != null)
      M_Zone_ID = zone.getWMS_Zone_ID(); 
    MOrderLine oLine = null;
    MProduct product = null;
    MOrder order = null;
    int M_Product_ID = 0;
    int M_AttributeSetInstance_ID = 0;
    BigDecimal qtyOpen = BigDecimal.ZERO;
    int C_BPartner_ID = 0;
    int C_DocTypeEvaluate_ID = 0;
    int OrderRef_ID = 0;
    int C_OrderLine_ID = 0;
    int M_WorkOrderOpeComponent_ID = 0;
    if ("SOO".equals(waveLineType)) {
      oLine = new MOrderLine(getCtx(), LineLineRef_ID, get_TrxName());
      M_Product_ID = oLine.getM_Product_ID();
      M_AttributeSetInstance_ID = oLine.getM_AttributeSetInstance_ID();
      qtyOpen = oLine.getQtyOrdered().subtract(oLine.getQtyDelivered()).subtract(oLine.getQtyDedicated()).subtract(oLine.getQtyAllocated());
      product = oLine.getProduct();
      C_BPartner_ID = oLine.getC_BPartner_ID();
      order = oLine.getParent();
      C_DocTypeEvaluate_ID = order.getC_DocTypeTarget_ID();
      OrderRef_ID = oLine.getC_Order_ID();
      C_OrderLine_ID = LineLineRef_ID;
    } else if ("WOO".equals(waveLineType)) {
      CPreparedStatement cPreparedStatement = null;
     // String tempSQL = "SELECT woc.M_Product_ID, woc.M_AttributeSetInstance_ID, (woc.QtyRequired*wo.QtyEntered) - (woc.QtyAvailable + woc.QtyDedicated +woc.QtyAllocated), woc.C_BPartner_ID, wo.C_DocType_ID, wo.M_WorkOrder_ID, woo.M_WorkOrderOperation_ID FROM M_WorkOrderComponent woc INNER JOIN M_WorkOrderOperation woo ON (woo.M_WorkOrderOperation_ID = woc.M_WorkOrderOperation_ID) INNER JOIN M_WorkOrder wo ON (wo.M_WorkOrder_ID = woo.M_WorkOrder_ID) WHERE woc.M_WorkOrderComponent_ID = ? ";
      String tempSQL = " SELECT woc.M_Product_ID, woc.M_AttributeSetInstance_ID, (woc.QtyRequired*wo.QtyEntered) - (woc.QtyAvailable + woc.QtyDedicated +woc.QtyAllocated), woc.C_BPartner_ID, wo.C_DocType_ID, wo.MFG_WorkOrder_ID, woo.MFG_WorkOrderOperation_ID FROM MFG_WorkOrderComponent woc INNER JOIN MFG_WorkOrderOperation woo ON (woo.MFG_WorkOrderOperation_ID = woc.MFG_WorkOrderOperation_ID) INNER JOIN MFG_WorkOrder wo  ON (wo.MFG_WorkOrder_ID = woo.MFG_WorkOrder_ID) WHERE woc.MFG_WorkOrderComponent_ID = ?"; 
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
        cPreparedStatement = DB.prepareStatement(tempSQL, null);
        cPreparedStatement.setInt(1, LineLineRef_ID);
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          M_Product_ID = rs.getInt(1);
          M_AttributeSetInstance_ID = rs.getInt(2);
          qtyOpen = rs.getBigDecimal(3);
          product = new MProduct(getCtx(), M_Product_ID, get_TrxName());
          C_BPartner_ID = rs.getInt(4);
          C_DocTypeEvaluate_ID = rs.getInt(5);
          OrderRef_ID = rs.getInt(6);
          M_WorkOrderOpeComponent_ID = LineLineRef_ID;
        } 
      } catch (Exception e) {
        log.log(Level.SEVERE, tempSQL, e);
      } finally {
        DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null; cPreparedStatement = null;
      } 
    } else {
      throw new IllegalArgumentException("Wave Lines are neither SO nor WO");
    } 
    if (qtyOpen.compareTo(BigDecimal.ZERO) <= 0)
      return null; 
    if (product == null || !product.isStocked())
      return null; 
    if ("SOO".equals(waveLineType) && product.isPurchasedToOrder()) {
      MOrderLine receiptLine = (MOrderLine) oLine.getRef_OrderLine();
      if (receiptLine == null)
        return qtyOpen; 
      BigDecimal qtyReceived = receiptLine.getQtyDelivered();
      BigDecimal qtyShippedOrAllocated = oLine.getQtyDelivered().add(oLine.getQtyAllocated()).add(oLine.getQtyDedicated());
      BigDecimal qtyAvailable = qtyReceived.subtract(qtyShippedOrAllocated);
      if (qtyAvailable.compareTo(BigDecimal.ZERO) <= 0)
        return qtyOpen; 
      if (qtyAvailable.compareTo(qtyOpen) <= 0)
        qtyOpen = qtyAvailable; 
    } 
    for (MWMSMMStrategySetLine line : setLines) {
      if (line.evaluateCriteria(C_BPartner_ID, destM_Locator_ID, M_Product_ID, M_Zone_ID, C_DocTypeEvaluate_ID)) {
        MWMSMMStrategy strategy = line.getStrategy();
        qtyOpen = strategy.executePickStrategy(this.m_mmContext, M_Product_ID, M_AttributeSetInstance_ID, qtyOpen, product.getC_UOM_ID(), OrderRef_ID, C_OrderLine_ID, M_WorkOrderOpeComponent_ID, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        if (qtyOpen.compareTo(BigDecimal.ZERO) <= 0 || !isEvaluateAllStrategies())
          break; 
      } 
    } 
    return qtyOpen;
  }
  
  public BigDecimal executePickStrategySetForSOO(MOrderLine oLine, MOrder order, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID) throws Exception {
    if (!getMMType().equals(X_WMS_MMStrategy.MMTYPE_MaterialPicking))
      throw new IllegalArgumentException("Strategy set is not of type Pick"); 
    MWMSMMStrategySetLine[] setLines = getLines();
    MWMSZone zone = MWMSZone.getOfLocator(getCtx(), getM_Warehouse_ID(), destM_Locator_ID);
    int M_Zone_ID = 0;
    if (zone != null)
      M_Zone_ID = zone.getWMS_Zone_ID(); 
    int M_Product_ID = oLine.getM_Product_ID();
    int M_AttributeSetInstance_ID = oLine.getM_AttributeSetInstance_ID();
    BigDecimal qtyOpen = oLine.getQtyOrdered().subtract(oLine.getQtyDelivered()).subtract(oLine.getQtyDedicated()).subtract(oLine.getQtyAllocated());
    MProduct product = oLine.getProduct();
    int C_BPartner_ID = oLine.getC_BPartner_ID();
    int C_DocTypeEvaluate_ID = order.getC_DocTypeTarget_ID();
    if (qtyOpen.compareTo(BigDecimal.ZERO) <= 0)
      return null; 
    if (product == null || !product.isStocked())
      return null; 
    if (product.isPurchasedToOrder()) {
      MOrderLine receiptLine = (MOrderLine) oLine.getRef_OrderLine();
      if (receiptLine == null)
        return qtyOpen; 
      BigDecimal qtyReceived = receiptLine.getQtyDelivered();
      BigDecimal qtyShippedOrAllocated = oLine.getQtyDelivered().add(oLine.getQtyAllocated()).add(oLine.getQtyDedicated());
      BigDecimal qtyAvailable = qtyReceived.subtract(qtyShippedOrAllocated);
      if (qtyAvailable.compareTo(BigDecimal.ZERO) <= 0)
        return qtyOpen; 
      if (qtyAvailable.compareTo(qtyOpen) <= 0)
        qtyOpen = qtyAvailable; 
    } 
    for (MWMSMMStrategySetLine line : setLines) {
      if (line.evaluateCriteria(C_BPartner_ID, destM_Locator_ID, M_Product_ID, M_Zone_ID, C_DocTypeEvaluate_ID)) {
        MWMSMMStrategy strategy = line.getStrategy();
        qtyOpen = strategy.executePickStrategy(this.m_mmContext, M_Product_ID, M_AttributeSetInstance_ID, qtyOpen, product.getC_UOM_ID(), order.getC_Order_ID(), oLine.getC_OrderLine_ID(), 0, M_SourceZone_ID, destM_Locator_ID, C_DocType_ID, docAction, C_WaveLine_ID);
        if (qtyOpen.compareTo(BigDecimal.ZERO) <= 0 || !isEvaluateAllStrategies())
          break; 
      } 
    } 
    return qtyOpen;
  }
  
  public void setGroupingParameters(int pickMethod, int clusterSize) {
    this.m_mmContext.setGroupingParameters(pickMethod, clusterSize);
  }
  
  public boolean confirmTasks(Trx p_trx, String docAction, String trx) {
    if (p_trx == null) {
      log.fine("Cannot confirm, transaction is null");
      return false;
    } 
    if (this.m_mmContext.getUnconfirmedCount() > 0) {
      if (!this.m_mmContext.confirmTasks(getCtx(), docAction, trx)) {
        p_trx.rollback();
        return false;
      } 
      p_trx.commit();
    } 
    return true;
  }
  
  public int getUnconfirmedCount() {
    return this.m_mmContext.getUnconfirmedCount();
  }
  
  public boolean removeUnconfirmedTasks() {
    return this.m_mmContext.removeUnconfirmedTasks();
  }
  
  public MWMSTaskList[] getTaskLists() {
    MWMSTaskList[] taskList = new MWMSTaskList[this.m_mmContext.m_TaskLists.size()];
    this.m_mmContext.m_TaskLists.toArray((Object[])taskList);
    return taskList;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    if (isActive() && (newRecord || is_ValueChanged("IsActive"))) {
      String sql = "SELECT count(*) FROM M_MMStrategySet WHERE M_Warehouse_ID = ? AND M_MMSTrategySet_ID != ? AND MMType=? AND IsActive = 'Y'";
      int ii = DB.getSQLValue(get_TrxName(), sql, new Object[] { Integer.valueOf(getM_Warehouse_ID()), Integer.valueOf(getWMS_MMStrategySet_ID()), getMMType() });
      if (ii != 0) {
        log.saveError("Error", Msg.getMsg(getCtx(), "StrategySetExists"));
        return false;
      } 
    } 
    return true;
  }
  
  public static MInOutLine getM_InOutLineOf(Properties ctx, int M_AttributeSetInstance_ID, String trx) {
    CPreparedStatement cPreparedStatement=null;
    MInOutLine retValue = null;
    String sql = "SELECT * FROM M_InOutLine line WHERE M_AttributeSetInstance_ID=? OR EXISTS (SELECT 1 FROM M_InOutLineMA ma WHERE line.M_InOutLine_ID = ma.M_InOutLine_ID AND M_AttributeSetInstance_ID=?)";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx);
      cPreparedStatement.setInt(1, M_AttributeSetInstance_ID);
      cPreparedStatement.setInt(2, M_AttributeSetInstance_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = new MInOutLine(ctx, rs, trx); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return retValue;
  }
}

