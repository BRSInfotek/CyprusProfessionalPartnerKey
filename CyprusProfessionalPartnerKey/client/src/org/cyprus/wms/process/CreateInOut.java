package org.cyprus.wms.process;


//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.cyprus.wms.model.X_WMS_InOutStage;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOMConversion;
import org.cyprusbrs.framework.X_M_InOut;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_M_InOutStage;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.cyprusbrsUserException;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Ini;
import org.cyprusbrs.util.Msg;

public class CreateInOut extends SvrProcess {
  private boolean m_deleteOldProcessed = false;
  
  PO po;
  private String m_docAction = X_M_InOut.DOCACTION_Prepare;
  
  private int m_M_Warehouse_ID = 0;
  
  private int noInsert = 0;
  
  private int noInsertLine = 0;
  
  private Map<MInOut, List<MInOutLine>> inoutMap = new HashMap<MInOut, List<MInOutLine>>();
  
  private Map<X_WMS_InOutStage, MInOut> inoutStageMap = new HashMap<X_WMS_InOutStage, MInOut>();
  
  private final HashMap<Integer, MOrderLine[]> m_map = (HashMap)new HashMap<Integer, MOrderLine>();
  
  private static final boolean TESTMODE = false;
  
  private static final int COMMITCOUNT = Integer.parseInt(Ini.getProperty("ImportBatchSize"));
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (name.equals("DeleteOldProcessed")) {
        this.m_deleteOldProcessed = "Y".equals(element.getParameter());
      } else if (name.equals("DocAction")) {
        this.m_docAction = (String)element.getParameter();
      } else if (name.equals("M_Warehouse_ID")) {
        this.m_M_Warehouse_ID = ((BigDecimal)element.getParameter()).intValue();
      } else {
        this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
      } 
    } 
  }
  
  private MOrderLine[] getOrderLines(MOrder order) {
    MOrderLine[] oLines = this.m_map.get(Integer.valueOf(order.getC_Order_ID()));
    if (oLines == null) {
    	/// Have to check at run time @Anil17122021
      oLines = order.getLines(" QtyOrdered - QtyDelivered, Line ","");
      this.m_map.put(Integer.valueOf(order.getC_Order_ID()), oLines);
    } 
    return oLines;
  }
  
  private void checkAvailability(MOrder order, int M_Warehouse_ID, int M_Product_ID, BigDecimal QtyEntered, int C_UOM_ID) throws Exception {
    MOrderLine[] oLines = getOrderLines(order);
    if (oLines == null || oLines.length == 0 || M_Product_ID == 0 || QtyEntered.compareTo(BigDecimal.ZERO) == 0)
      throw new Exception(Msg.getMsg(getCtx(), "QtyEnteredZero")); 
    BigDecimal qtyToReceive = MUOMConversion.convertProductFrom(getCtx(), M_Product_ID, C_UOM_ID, QtyEntered);
    if (qtyToReceive == null || qtyToReceive.signum() <= 0)
      throw new Exception(Msg.getMsg(getCtx(), "QtyEnteredZero")); 
    boolean matchFound = false;
    BigDecimal openQty = Env.ZERO;
    for (MOrderLine element : oLines) {
      if (element.getM_Product_ID() == M_Product_ID) {
        if (!matchFound)
          matchFound = true; 
        openQty = openQty.add(element.getQtyOrdered().subtract(element.getQtyDelivered()));
      } 
    } 
    if (!matchFound)
      throw new Exception(Msg.getMsg(getCtx(), "ProductNotOnOrder")); 
    if (qtyToReceive.compareTo(openQty) > 0)
      throw new Exception(Msg.getMsg(getCtx(), "QtyEnteredExceedsQtyOrdered")); 
  }
  
  protected void createLine(MInOut inout, MOrder order, int M_Warehouse_ID, int M_Locator_ID, int M_Product_ID, BigDecimal QtyEntered, int C_UOM_ID) throws Exception {
    MOrderLine[] oLines = getOrderLines(order);
    List<MInOutLine> lines = this.inoutMap.get(inout);
    if (lines == null) {
      lines = new ArrayList<MInOutLine>();
      this.inoutMap.put(inout, lines);
    } 
    if (oLines == null || oLines.length == 0 || M_Product_ID == 0 || QtyEntered.compareTo(BigDecimal.ZERO) == 0)
      throw new Exception(Msg.getMsg(getCtx(), "QtyEnteredZero")); 
    BigDecimal qtyToReceive = MUOMConversion.convertProductFrom(getCtx(), M_Product_ID, C_UOM_ID, QtyEntered);
    if (qtyToReceive == null)
      throw new Exception(Msg.getMsg(getCtx(), "QtyEnteredZero")); 
    MInOutLine iol = null;
    boolean matchFound = false;
    for (MOrderLine element : oLines) {
      if (qtyToReceive.compareTo(BigDecimal.ZERO) <= 0)
        break; 
      if (element.getM_Product_ID() == M_Product_ID) {
        if (!matchFound)
          matchFound = true; 
        BigDecimal openQty = element.getQtyOrdered().subtract(element.getQtyDelivered());
        if (openQty.compareTo(BigDecimal.ZERO) > 0) {
          iol = new MInOutLine(inout);
          iol.setM_Product_ID(M_Product_ID, C_UOM_ID);
          iol.setM_Locator_ID(M_Locator_ID);
          if (qtyToReceive.compareTo(openQty) <= 0) {
            iol.setMovementQty(qtyToReceive);
            iol.setQtyEntered(MUOMConversion.convertProductTo(getCtx(), M_Product_ID, C_UOM_ID, qtyToReceive));
            element.setQtyDelivered(element.getQtyDelivered().add(qtyToReceive));
            qtyToReceive = BigDecimal.ZERO;
          } else {
            iol.setMovementQty(openQty);
            iol.setQtyEntered(MUOMConversion.convertProductTo(getCtx(), M_Product_ID, C_UOM_ID, openQty));
            qtyToReceive = qtyToReceive.subtract(openQty);
            element.setQtyDelivered(element.getQtyDelivered().add(openQty));
          } 
          iol.setC_UOM_ID(C_UOM_ID);
          iol.setC_OrderLine_ID(element.getC_OrderLine_ID());
          iol.setM_AttributeSetInstance_ID(element.getM_AttributeSetInstance_ID());
          iol.setDescription(element.getDescription());
          iol.setC_Project_ID(element.getC_Project_ID());
          iol.setC_ProjectPhase_ID(element.getC_ProjectPhase_ID());
          iol.setC_ProjectTask_ID(element.getC_ProjectTask_ID());
          iol.setC_Activity_ID(element.getC_Activity_ID());
          iol.setC_Campaign_ID(element.getC_Campaign_ID());
          iol.setAD_OrgTrx_ID(element.getAD_OrgTrx_ID());
          iol.setUser1_ID(element.getUser1_ID());
          iol.setUser2_ID(element.getUser2_ID());
          lines.add(iol);
          this.noInsertLine++;
        } 
      } 
    } 
    if (!matchFound)
      throw new Exception(Msg.getMsg(getCtx(), "ProductNotOnOrder")); 
    if (qtyToReceive.compareTo(BigDecimal.ZERO) > 0)
      throw new Exception(Msg.getMsg(getCtx(), "QtyEnteredExceedsQtyOrdered")); 
  }
  
  public MInOut createFromOrder(MOrder order, Timestamp movementDate, int M_Warehouse_ID, int M_Locator_ID, int C_DocType_ID) {
    if (order == null)
      throw new IllegalArgumentException("No Order"); 
    MInOut inout = new MInOut(order, 0, movementDate);
    inout.setC_DocType_ID(C_DocType_ID);
    List<MInOutLine> lines = this.inoutMap.get(inout);
    if (lines == null) {
      lines = new ArrayList<MInOutLine>();
      this.inoutMap.put(inout, lines);
    } 
    this.noInsert++;
    MOrderLine[] oLines = getOrderLines(order);
    for (MOrderLine element : oLines) {
      boolean partiallyReceived = (element.getQtyDelivered().compareTo(BigDecimal.ZERO) != 0);
      BigDecimal MovementQty = element.getQtyOrdered().subtract(element.getQtyDelivered());
      if (MovementQty.signum() != 0 && element.getQtyEntered().compareTo(BigDecimal.ZERO) != 0) {
        MInOutLine iol = null;
        iol = new MInOutLine(inout);
        iol.setM_Product_ID(element.getM_Product_ID(), element.getC_UOM_ID());
        iol.setM_Locator_ID(M_Locator_ID);
        if (!partiallyReceived && element.getQtyEntered().compareTo(element.getQtyOrdered()) != 0) {
          iol.setQtyEntered(element.getQtyEntered().multiply(MovementQty).divide(element.getQtyOrdered(), 12, 4));
          iol.setC_UOM_ID(element.getC_UOM_ID());
        } else {
          iol.setQtyEntered(MovementQty);
          MProduct product = MProduct.get(getCtx(), element.getM_Product_ID());
          iol.setC_UOM_ID(product.getC_UOM_ID());
        } 
        iol.setMovementQty(MovementQty);
        element.setQtyDelivered(element.getQtyDelivered().add(MovementQty));
        iol.setC_OrderLine_ID(element.getC_OrderLine_ID());
        iol.setM_AttributeSetInstance_ID(element.getM_AttributeSetInstance_ID());
        iol.setDescription(element.getDescription());
        iol.setC_Project_ID(element.getC_Project_ID());
        iol.setC_ProjectPhase_ID(element.getC_ProjectPhase_ID());
        iol.setC_ProjectTask_ID(element.getC_ProjectTask_ID());
        iol.setC_Activity_ID(element.getC_Activity_ID());
        iol.setC_Campaign_ID(element.getC_Campaign_ID());
        iol.setAD_OrgTrx_ID(element.getAD_OrgTrx_ID());
        iol.setUser1_ID(element.getUser1_ID());
        iol.setUser2_ID(element.getUser2_ID());
        lines.add(iol);
        this.noInsertLine++;
      } 
    } 
    return inout;
  }
  
  protected String doIt() throws Exception {
    CPreparedStatement cPreparedStatement = null;
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      throw new Exception(CLogger.retrieveError().getName()); 
    StringBuffer sql = null;
    int no = 0;
    String clientOrgWHCheck = " AND AD_Client_ID=" + getAD_Client_ID() + " AND M_Warehouse_ID=" + this.m_M_Warehouse_ID;
    if (this.m_deleteOldProcessed) {
      sql = (new StringBuffer("DELETE FROM WMS_InOutStage WHERE IsInOutCreated='Y'")).append(clientOrgWHCheck);
    //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
      no =DB.executeUpdateEx(sql.toString(), new Object[0], get_TrxName());
      this.log.fine("Delete Old Processed =" + no);
    } 
    String ts = DB.isPostgreSQL() ? "COALESCE(ErrorMsg,'')" : "ErrorMsg";
    sql = (new StringBuffer("UPDATE WMS_InOutStage i SET IsInOutCreated='N', ErrorMsg=NULL WHERE IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no =  DB.executeUpdateEx(sql.toString(), new Object[0],get_TrxName());
    
    sql = (new StringBuffer("UPDATE WMS_InOutStage i SET IsInOutCreated='E', ErrorMsg=" + ts + "||'ERR=Invalid Org, '" + "WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0" + " OR EXISTS (SELECT * FROM AD_Org oo WHERE i.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))" + " AND IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no = DB.executeUpdateEx(sql.toString(),  new Object[0],get_TrxName());
    if (no != 0)
      this.log.warning("Invalid Org=" + no); 
    sql = (new StringBuffer("UPDATE WMS_InOutStage SET IsInOutCreated='E', ErrorMsg=" + ts + "||'ERR=No DocType, ' " + "WHERE C_DocType_ID IS NULL" + " AND IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
 //   no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no = DB.executeUpdateEx(sql.toString(),  new Object[0],get_TrxName());
    if (no != 0)
      this.log.warning("No DocType=" + no); 
    sql = (new StringBuffer("UPDATE WMS_InOutStage i SET IsInOutCreated='E', ErrorMsg=" + ts + "||'ERR=No Warehouse, ' " + "WHERE M_Warehouse_ID IS NULL" + " AND IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
//    no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no = DB.executeUpdateEx(sql.toString(),  new Object[0],get_TrxName());
    if (no != 0)
      this.log.warning("No Warehouse=" + no); 
    sql = (new StringBuffer("UPDATE WMS_InOutStage i SET IsInOutCreated='E', ErrorMsg=" + ts + "||'ERR=No Order, ' " + "WHERE C_Order_ID IS NULL" + " AND IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
  //  no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no = DB.executeUpdateEx(sql.toString(),  new Object[0],get_TrxName());
    
    if (no != 0)
      this.log.warning("No Order=" + no); 
    sql = (new StringBuffer("UPDATE WMS_InOutStage i SET IsInOutCreated='E', ErrorMsg=" + ts + "||'ERR=Document Type mismatch, ' " + "WHERE EXISTS (SELECT 1 FROM C_Order o, C_DocType d " + " WHERE d.C_DocType_ID = i.C_DocType_ID " + " AND o.C_Order_ID = i.C_Order_ID " + " AND (d.IsSOTrx <> o.IsSOTrx OR d.IsReturnTrx <> o.IsReturnTrx))" + " AND IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
   // no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no = DB.executeUpdateEx(sql.toString(),  new Object[0],get_TrxName());
    if (no != 0)
      this.log.warning("Order/ Document Type mismatch =" + no); 
    commit();
    MInOut inout = null;
    sql = (new StringBuffer("SELECT * FROM WMS_InOutStage WHERE IsInOutCreated='N' AND M_Product_ID IS NULL")).append(clientOrgWHCheck).append(" ORDER BY C_Order_ID, M_Product_ID, Created");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_WMS_InOutStage imp = new X_WMS_InOutStage(getCtx(), rs, get_TrxName());
        MOrder order = new MOrder(getCtx(), imp.getC_Order_ID(), get_TrxName());
        inout = createFromOrder(order, imp.getMovementDate(), imp.getM_Warehouse_ID(), imp.getM_Locator_ID(), imp.getC_DocType_ID());
        if (inout == null) {
          this.log.log(Level.SEVERE, imp + ":- Could not create Receipt");
          continue;
        } 
        this.inoutStageMap.put(imp, inout);
        if (this.inoutStageMap.size() >= COMMITCOUNT)
          saveInout(); 
      } 
    } catch (SQLException e) {
      throw new Exception(e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    saveInout();
    sql = (new StringBuffer("SELECT * FROM WMS_InOutStage WHERE IsInOutCreated='N' AND M_Product_ID IS NOT NULL")).append(clientOrgWHCheck).append(" ORDER BY C_Order_ID, M_Product_ID, Created");
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        X_WMS_InOutStage imp = new X_WMS_InOutStage(getCtx(), rs, get_TrxName());
        MOrder order = new MOrder(getCtx(), imp.getC_Order_ID(), get_TrxName());
        try {
          checkAvailability(order, imp.getM_Warehouse_ID(), imp.getM_Product_ID(), imp.getQtyEntered(), imp.getC_UOM_ID());
        } catch (Exception e) {
          imp.setErrorMsg(e.getLocalizedMessage());
          this.inoutStageMap.put(imp, null);
          continue;
        } 
        inout = order.getOpenInOut(imp.getC_DocType_ID(), imp.getM_Warehouse_ID(), order.getC_BPartner_ID(), order.getC_BPartner_Location_ID());
        if (inout == null) {
          if (this.inoutMap.size() >= COMMITCOUNT)
            saveInout(); 
          inout = new MInOut(order, imp.getC_DocType_ID(), imp.getMovementDate());
          this.noInsert++;
        } 
        try {
          createLine(inout, order, imp.getM_Warehouse_ID(), imp.getM_Locator_ID(), imp.getM_Product_ID(), imp.getQtyEntered(), imp.getC_UOM_ID());
        } catch (Exception e) {
          imp.setErrorMsg(e.getLocalizedMessage());
          this.inoutStageMap.put(imp, null);
          continue;
        } 
        this.inoutStageMap.put(imp, inout);
      } 
    } catch (SQLException e) {
      throw new Exception(e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    saveInout();
    sql = (new StringBuffer("UPDATE WMS_InOutStage SET IsInOutCreated='N', Updated=SysDate WHERE IsInOutCreated<>'Y'")).append(clientOrgWHCheck);
 //   no = DB.executeUpdate(get_TrxName(), sql.toString(), new Object[0]);
    no = DB.executeUpdateEx(sql.toString(),  new Object[0],get_TrxName());
    
    addLog(0, null, new BigDecimal(no), "@Errors@");
    addLog(0, null, new BigDecimal(this.noInsert), "@M_InOut_ID@: @Inserted@");
    addLog(0, null, new BigDecimal(this.noInsertLine), "@M_InOutLine_ID@: @Inserted@");
    return "#" + this.noInsert + "/" + this.noInsertLine;
  }
  
  private void saveInout() throws Exception {
    List<MInOut> inoutToSave = new ArrayList<MInOut>(this.inoutMap.keySet());
//    if (!PO.saveAll(get_TrxName(), inoutToSave))
    if (!po.save(get_TrxName()))

    throw new Exception("Could not save Receipts"); 
    List<MInOutLine> linesToSave = new ArrayList<MInOutLine>();
    for (Map.Entry<MInOut, List<MInOutLine>> entry : this.inoutMap.entrySet()) {
      MInOut inout = entry.getKey();
      for (MInOutLine inoutLine : entry.getValue()) {
        inoutLine.setM_InOut_ID(inout.getM_InOut_ID());
        linesToSave.add(inoutLine);
      } 
    } 
//    if (!PO.saveAll(get_TrxName(), linesToSave))
    if (!po.save(get_TrxName()))

    throw new Exception("Could not save Receipt Lines"); 
    List<X_WMS_InOutStage> inoutStageToSave = new ArrayList<X_WMS_InOutStage>();
    for (Map.Entry<X_WMS_InOutStage, MInOut> entry : this.inoutStageMap.entrySet()) {
    	X_WMS_InOutStage inoutStage = entry.getKey();
      MInOut inout = entry.getValue();
      if (inout != null) {
        inoutStage.setM_InOut_ID(inout.getM_InOut_ID());
        inoutStage.setIsInOutCreated(X_WMS_InOutStage.ISINOUTCREATED_Yes);
        inoutStage.setProcessed(true);
      } 
      inoutStageToSave.add(inoutStage);
    } 
    if (this.m_docAction != null && this.m_docAction.length() > 0)
      for (MInOut inout : inoutToSave) {
        inout.setDocAction(this.m_docAction);
        DocumentEngine engine = new DocumentEngine((DocAction)inout, this.m_docAction);
        if(!engine.processIt(this.m_docAction));
       // if (!DocumentEngine.processIt((DocAction)inout, this.m_docAction))
          this.log.warning("Could not process Receipt : " + inout.getDocumentNo()); 
      }  
//    if (!PO.saveAll(get_TrxName(), inoutToSave))
    if (!po.save(get_TrxName()))

    throw new Exception("Could not save Receipts"); 
//    if (!PO.saveAll(get_TrxName(), inoutStageToSave))
    if (!po.save(get_TrxName())) 

    throw new Exception("Could not save Stage record"); 
    this.inoutMap.clear();
    this.inoutStageMap.clear();
    commit();
  }
}

