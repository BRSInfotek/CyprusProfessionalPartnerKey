package org.cyprus.wms.tools;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

import org.cyprus.wms.model.MWMSTaskList;
import org.cyprus.wms.model.MWMSWarehouseTask;
import org.cyprus.wms.model.MWMSWave;
import org.cyprus.wms.model.X_WMS_WarehouseTask;
import org.cyprus.wms.process.CreateWave;
import org.cyprus.wms.process.GeneratePutaway;
import org.cyprus.wms.process.ReleaseWave;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MLocation;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
import org.cyprusbrs.framework.MPriceList;
import org.cyprusbrs.framework.MPriceListVersion;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductPrice;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.framework.MUserRoles;
import org.cyprusbrs.framework.X_C_BPartner;
import org.cyprusbrs.framework.X_C_Order;
import org.cyprusbrs.model.PO;
//import org.cyprusbrs.model.X_M_WarehouseTask;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.process.ProcessInfo;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogMgt;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Ini;
import org.cyprusbrs.util.Login;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;
//import org.cyprusbrs.util.QueryUtil;
import org.cyprusbrs.util.Util;

public class VolumeTestTasks extends SvrProcess {
	
	PO po;
	
	Trx trx;
	
  String p_Prefix = null;
  
  int p_VolumeBPartner = 1;
  
  int p_VolumeUser = 0;
  
  int p_AD_Role_ID = 0;
  
  int p_VolumeProduct = 17;
  
  int p_VolumeOrder = 500;
  
  int p_VolumeLine = 5;
  
  int p_VolumePurchaseOrder = 10;
  
  int p_C_DocTypeTarget_ID = 132;
  
  int p_C_DocTypePO_ID = 126;
  
  int p_C_DocTypeReceipt_ID = 126;
  
  int p_C_DocTypePick_ID = 1000000;
  
  int p_C_DocTypePutaway_ID = 1000000;
  
  int p_PutawayProcess_ID = 1000056;
  
  int p_WavePlanningProcess_ID = 1000055;
  
  int p_ReleaseWaveProcess_ID = 1000055;
  
  int p_M_Warehouse_ID = 105;
  
  int p_M_Locator_ID = 139;
  
  int p_QtyEntered = 10000;
  
  int p_Vendor_ID = 114;
  
  int C_Tax_ID = 105;
  
  boolean isGenerateReleaseSingleStep = false;
  
  String p_PickMethod = "C";
  
  int p_clusterSize = 10;
  
  int p_stagingLocator_ID = 145;
  
  String p_IsPrintPickList = "N";
  
  String p_PutawayDocAction = null;
  
  String p_PickDocAction = null;
  
  private MWMSWave m_wave = null;
  
  private MPriceList m_pl = null;
  
  private MPriceListVersion m_plv = null;
  
  private MPriceList m_plpo = null;
  
  private MPriceListVersion m_plvpo = null;
  
  private ArrayList<MBPartner> m_bpartners = new ArrayList<MBPartner>();
  
  private ArrayList<MProduct> m_products = new ArrayList<MProduct>();
  
  private static int COMMITCOUNT = 100;
  
  protected void prepare() {
    ProcessInfoParameter[] para = getParameter();
    for (ProcessInfoParameter element : para) {
      String name = element.getParameterName();
      if (element.getParameter() != null)
        if (name.equals("Prefix")) {
          this.p_Prefix = (String)element.getParameter();
        } else if (name.equals("VolumeBPartner")) {
          this.p_VolumeBPartner = element.getParameterAsInt();
        } else if (name.equals("VolumeUser")) {
          this.p_VolumeUser = element.getParameterAsInt();
        } else if (name.equals("AD_Role_ID")) {
          this.p_AD_Role_ID = element.getParameterAsInt();
        } else if (name.equals("VolumeProduct")) {
          this.p_VolumeProduct = element.getParameterAsInt();
        } else if (name.equals("VolumeOrder")) {
          this.p_VolumeOrder = element.getParameterAsInt();
        } else if (name.equals("VolumeLine")) {
          this.p_VolumeLine = element.getParameterAsInt();
        } else if (name.equals("DocAction")) {
          this.p_PickDocAction = (String)element.getParameter();
        } else if (name.equals("C_DocTypeTarget_ID")) {
          this.p_C_DocTypeTarget_ID = element.getParameterAsInt();
        } else {
          this.log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }  
    } 
    if (Util.isEmpty(this.p_Prefix))
      this.p_Prefix = String.valueOf(System.currentTimeMillis()) + "_"; 
  }
  
  protected String doIt() throws Exception {
    this.log.info("Prefix=" + this.p_Prefix + ",VolumeBPartner=" + this.p_VolumeBPartner + ",VolumeUser=" + this.p_VolumeUser + ",AD_Role_ID=" + this.p_AD_Role_ID + ",VolumeProduct=" + this.p_VolumeProduct + ",VolumeOrder=" + this.p_VolumeOrder + ",VolumeLine=" + this.p_VolumeLine + ",C_DocTypeTarget_ID=" + this.p_C_DocTypeTarget_ID + ",DocAction" + this.p_PickDocAction);
    if (Env.getAD_Client_ID(getCtx()) == 0)
      throw new IllegalArgumentException("Cannot run in Client=System"); 
    MDocType dt = MDocType.get(getCtx(), this.p_C_DocTypeTarget_ID);
    if (dt.getC_DocType_ID() != this.p_C_DocTypeTarget_ID)
      throw new IllegalArgumentException("@NotFound@ @C_DocTypeTarget_ID@ - ID=" + this.p_C_DocTypeTarget_ID); 
    if (dt.getAD_Client_ID() != Env.getAD_Client_ID(getCtx()))
      throw new IllegalArgumentException("Client conflict: " + dt); 
    this.m_pl = MPriceList.getDefault(getCtx(), true);
    if (this.m_pl == null)
      throw new IllegalArgumentException("@NotFound@ @M_PriceList_ID@"); 
    this.m_plv = this.m_pl.getPriceListVersion(null);
    if (this.m_pl == null)
      throw new IllegalArgumentException("@NotFound@ @M_PriceListVersion_ID@"); 
    this.m_plpo = MPriceList.getDefault(getCtx(), false);
    if (this.m_plpo == null)
      throw new IllegalArgumentException("@NotFound@ @M_PriceList_ID@"); 
    this.m_plvpo = this.m_plpo.getPriceListVersion(null);
    if (this.m_plpo == null)
      throw new IllegalArgumentException("@NotFound@ @M_PriceListVersion_ID@"); 
    this.p_C_DocTypePick_ID = DB.getSQLValue(get_TrxName(), "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='PCK' AND ROWNUM=1", new Object[0]);
    this.p_C_DocTypePutaway_ID = DB.getSQLValue(get_TrxName(), "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='PUT' AND ROWNUM=1", new Object[0]);
    this.p_PutawayProcess_ID = DB.getSQLValue(get_TrxName(), "SELECT AD_Process_ID FROM AD_Process WHERE Name = 'Generate Putaway'", new Object[0]);
    this.p_WavePlanningProcess_ID = DB.getSQLValue(get_TrxName(), "SELECT AD_Process_ID FROM AD_Process WHERE Name = 'Wave Planning Process'", new Object[0]);
    this.p_ReleaseWaveProcess_ID = DB.getSQLValue(get_TrxName(), "SELECT AD_Process_ID FROM AD_Process WHERE Name = 'Release Wave Process'", new Object[0]);
    long start = System.currentTimeMillis();
    createBPartners(dt.isSOTrx());
    createProducts(dt.isSOTrx());
    createReceipts();
    createOrders();
    CLogMgt.setLoggerLevel(Level.WARNING, null);
    CLogMgt.setLevel(Level.WARNING);
    DB.startLoggingUpdates();
    MWMSTaskList[] taskLists = generatePutaway();
    if (taskLists != null && taskLists.length > 0 && this.p_PutawayDocAction.equals(X_WMS_WarehouseTask.DOCACTION_Prepare))
      completeTasks(taskLists); 
    taskLists = generateWave();
    if (!this.isGenerateReleaseSingleStep)
      taskLists = releaseWave(); 
    String logResult = DB.stopLoggingUpdates(0);
    System.out.println(logResult);
    DB.startLoggingUpdates();
    if (taskLists != null && taskLists.length > 0 && this.p_PickDocAction.equals(X_WMS_WarehouseTask.DOCACTION_Prepare))
      completeTasks(taskLists); 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    return "Total: " + duration + "s";
  }
  
  private void createBPartners(boolean isSOTrx) {
    long start = System.currentTimeMillis();
    int AD_Client_ID = Env.getAD_Client_ID(getCtx());
    int count = 0;
    int error = 0;
    for (int i = 0; i < this.p_VolumeBPartner; i++) {
      MBPartner bp = MBPartner.getTemplate(getCtx(), AD_Client_ID);
      bp.setValue(this.p_Prefix + "_BP_" + i);
      bp.setName(this.p_Prefix + "_BP_" + i);
      if (isSOTrx) {
        bp.setIsCustomer(true);
      } else {
        bp.setIsVendor(true);
      } 
      if (!bp.save(get_TrxName())) {
        error++;
      } else {
        MLocation addr = new MLocation(getCtx(), 0, get_TrxName());
        addr.setAddress1(this.p_Prefix + "_addr_" + i);
        addr.setCity("City_" + i);
        if (!addr.save()) {
          error++;
        } else {
          MBPartnerLocation loc = new MBPartnerLocation(bp);
          loc.setC_Location_ID(addr.getC_Location_ID());
          if (!loc.save()) {
            error++;
          } else {
            for (int u = 0; u < this.p_VolumeUser; u++) {
              MUser user = new MUser((X_C_BPartner)bp);
              user.setValue(this.p_Prefix + "_U_" + i + "-" + u);
              user.setValue(this.p_Prefix + "_User_" + i + "-" + u);
              user.setEMail(this.p_Prefix + i + u + "@cyprusbrstest.com");
              user.setPassword(user.getValue());
              if (!user.save(get_TrxName())) {
                error++;
              } else if (this.p_AD_Role_ID != 0) {
                MUserRoles ur = new MUserRoles(getCtx(), user.getAD_User_ID(), this.p_AD_Role_ID, get_TrxName());
                if (!ur.save())
                  error++; 
              } 
            } 
            this.m_bpartners.add(bp);
            count++;
            if (i > 0 && i % COMMITCOUNT == 0)
              commit(); 
          } 
        } 
      } 
    } 
    commit();
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "BPartner #" + count + "(Errors=" + error + ") in " + duration + "s = " + (durationMS / count) + "ms/BPartner";
    this.log.info(msg);
    addLog(msg);
  }
  
  private void createProducts(boolean isSOTrx) {
    long start = System.currentTimeMillis();
    int count = 0;
    int error = 0;
    BigDecimal factorList = new BigDecimal(1.2D);
    BigDecimal factorLimit = new BigDecimal(0.8D);
    Random random = new Random();
    for (int i = 0; i < this.p_VolumeProduct; i++) {
      MProduct p = new MProduct(getCtx(), 0, get_TrxName());
      p.setValue(this.p_Prefix + "_P_" + i);
      p.setName(this.p_Prefix + "_P_" + i);
      p.setIsStocked(true);
      if (isSOTrx) {
        p.setIsSold(true);
      } else {
        p.setIsPurchased(true);
      } 
      if (!p.save()) {
        error++;
      } else {
        MProductPrice pp = new MProductPrice(getCtx(), this.m_plv.getM_PriceList_Version_ID(), p.getM_Product_ID(), get_TrxName());
        double dd = random.nextDouble() * 10.0D;
        BigDecimal price = new BigDecimal(dd);
        pp.setPrices(price.multiply(factorList), price, price.multiply(factorLimit));
        if (!pp.save()) {
          error++;
        } else {
          MProductPrice pppo = new MProductPrice(getCtx(), this.m_plvpo.getM_PriceList_Version_ID(), p.getM_Product_ID(), get_TrxName());
          double ddpo = random.nextDouble() * 10.0D;
          BigDecimal pricepo = new BigDecimal(ddpo);
          pppo.setPrices(pricepo.multiply(factorList), pricepo, pricepo.multiply(factorLimit));
          if (!pppo.save()) {
            error++;
          } else {
            this.m_products.add(p);
            count++;
            if (i > 0 && i % COMMITCOUNT == 0)
              commit(); 
          } 
        } 
      } 
    } 
    commit();
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "BProduct #" + count + "(Errors=" + error + ") in " + duration + "s = " + (durationMS / count) + "ms/Product";
    this.log.info(msg);
    addLog(msg);
  }
  
  private void createOrders() {
    long start = System.currentTimeMillis();
    Random random = new Random();
    int SalesRep_ID = Env.getAD_User_ID(getCtx());
    ArrayList<MBPartner> bpartners = getBPartners();
    if (bpartners == null)
      throw new IllegalArgumentException("No BPartner found"); 
    int indexBP = 0;
    ArrayList<MProduct> products = getProducts();
    if (products == null)
      throw new IllegalArgumentException("No Product found"); 
    int indexProduct = 0;
    int countOrder = 0;
    int countLine = 0;
    int errorOrder = 0;
    int errorProcess = 0;
    ArrayList<MOrder> orders = new ArrayList<MOrder>();
    for (int i = 0; i < this.p_VolumeOrder; i++) {
      MBPartner bp = bpartners.get(indexBP++);
      if (indexBP >= bpartners.size())
        indexBP = 0; 
      MOrder order = new MOrder(getCtx(), 0, get_TrxName());
      order.setDescription(this.p_Prefix + "_O_" + i);
      order.setC_DocTypeTarget_ID(this.p_C_DocTypeTarget_ID);
      order.setBPartner(bp);
      order.setSalesRep_ID(SalesRep_ID);
      order.setM_Warehouse_ID(this.p_M_Warehouse_ID);
      order.setDeliveryRule(X_C_Order.DELIVERYRULE_CompleteOrder);
      if (!order.save()) {
        this.log.warning("Order #" + i + ": Not saved(1)");
        errorOrder++;
      } else {
        ArrayList<MOrderLine> lines = new ArrayList<MOrderLine>();
        for (int k = 0; k < this.p_VolumeLine; k++) {
          MProduct prod = products.get(indexProduct++);
          if (indexProduct >= products.size())
            indexProduct = 0; 
          MOrderLine line = new MOrderLine(order);
          line.setProduct(prod);
          int qty = random.nextInt(10) + 1;
          line.setQty(new BigDecimal(qty));
          line.setDescription(this.p_Prefix + "_O_" + i + "_L_" + k);
          line.setC_Tax_ID(this.C_Tax_ID);
          lines.add(line);
          countLine++;
        } 
//        if (!PO.saveAll(order.get_TrxName(), lines)) {
        if (!po.save(order.get_TrxName())) {

        this.log.warning("#" + i + ": Lines not saved");
        } else {
          orders.add(order);
          countOrder++;
          if (i > 0 && i % COMMITCOUNT == 0)
            commit(); 
        } 
      } 
    } 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "Order #" + countOrder + " | " + countLine + " in " + duration + "s = " + (durationMS / countOrder) + "ms/Order = " + (durationMS / countLine) + "ms/Line";
    this.log.info(msg);
    addLog(msg);
    int j = 0;
    for (MOrder order : orders) {
      order.setDocAction("CO");
      
      DocumentEngine engine = new DocumentEngine((DocAction)order, "CO");
      if(!engine.processIt("CO")) {
     // if (!DocumentEngine.processIt((DocAction)order, "CO")) {
        this.log.warning("#" + j + ": Not processed");
        errorProcess++;
        j++;
        continue;
      } 
      if (!order.save()) {
        this.log.warning("#" + j + ": Not saved(2)");
        errorProcess++;
        j++;
        continue;
      } 
      j++;
    } 
    commit();
    long end2 = System.currentTimeMillis();
    long durationMS2 = end2 - end;
    long duration2 = durationMS2 / 1000L;
    String msg2 = "DocAction " + this.p_PickDocAction + " Order #" + countOrder + " | " + countLine + " in " + duration2 + "s = " + (durationMS2 / countOrder) + "ms/Order = " + (durationMS2 / countLine) + "ms/Line";
    this.log.info(msg2);
    addLog(msg2);
    String errorMsg = "  #Errors: Order=" + errorOrder + ", Process=" + errorProcess;
    this.log.info(errorMsg);
    addLog(errorMsg);
  }
  
  public void createFromOrder(ArrayList<MOrder> orders) {
    if (orders == null || orders.size() == 0)
      throw new IllegalArgumentException("No Order"); 
    long start = System.currentTimeMillis();
    ArrayList<MInOut> receipts = new ArrayList<MInOut>();
    for (MOrder order : orders) {
      MInOut inout = new MInOut(order, 0, new Timestamp(System.currentTimeMillis()));
      inout.setC_DocType_ID(this.p_C_DocTypeReceipt_ID);
      inout.save();
      receipts.add(inout);
      MOrderLine[] oLines = order.getLines();
      ArrayList<MInOutLine> ioLines = new ArrayList<MInOutLine>();
      for (MOrderLine element : oLines) {
        MInOutLine iol = null;
        iol = new MInOutLine(inout);
        iol.setM_Product_ID(element.getM_Product_ID(), element.getC_UOM_ID());
        iol.setM_Locator_ID(this.p_M_Locator_ID);
        iol.setC_UOM_ID(element.getC_UOM_ID());
        iol.setQtyEntered(element.getQtyEntered());
        iol.setMovementQty(element.getQtyOrdered());
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
        ioLines.add(iol);
      } 
//      if (!PO.saveAll(get_TrxName(), ioLines))
      if (!po.save(get_TrxName()))

      this.log.warning("Shipment Lines not saved"); 
    } 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    int countLine = receipts.size() * this.m_products.size();
    String msg = "Created Receipt in " + duration + "s = " + durationMS + "ms = " + (durationMS / receipts.size()) + "ms/receipt = " + (durationMS / countLine) + "ms/line";
    this.log.info(msg);
    addLog(msg);
    for (MInOut inout : receipts) {
      inout.setDocAction("CO");
      
      DocumentEngine engine = new DocumentEngine((DocAction)inout, "CO");
      if(!engine.processIt("CO"))
     // if (!DocumentEngine.processIt((DocAction)inout, "CO"))
        this.log.warning(" Not processed"); 
      if (!inout.save())
        this.log.warning("Not saved(2)"); 
    } 
    long end2 = System.currentTimeMillis();
    long durationMS2 = end2 - end;
    long duration2 = durationMS2 / 1000L;
    String msg2 = "DocAction CO Completed Receipt in " + duration2 + "s = " + durationMS2 + "ms = " + (durationMS2 / receipts.size()) + "ms/receipt = " + (durationMS2 / countLine) + "ms/line";
    this.log.info(msg2);
    addLog(msg2);
  }
  
  private void createReceipts() {
    long start = System.currentTimeMillis();
    int SalesRep_ID = Env.getAD_User_ID(getCtx());
    MBPartner bp = new MBPartner(getCtx(), this.p_Vendor_ID, get_TrxName());
    if (bp == null)
      throw new IllegalArgumentException("No BPartner found"); 
    ArrayList<MProduct> products = getProducts();
    if (products == null)
      throw new IllegalArgumentException("No Product found"); 
    int countOrder = 1;
    int countLine = 0;
    ArrayList<MOrder> orders = new ArrayList<MOrder>();
    for (int i = 0; i < this.p_VolumePurchaseOrder; i++) {
      MOrder order = new MOrder(getCtx(), 0, get_TrxName());
      order.setIsSOTrx(false);
      order.setDescription(this.p_Prefix + "_O_");
      order.setC_DocTypeTarget_ID(this.p_C_DocTypePO_ID);
      order.setBPartner(bp);
      order.setSalesRep_ID(SalesRep_ID);
      order.setM_Warehouse_ID(this.p_M_Warehouse_ID);
      if (!order.save())
        this.log.warning("Purchase Order Not saved"); 
      ArrayList<MOrderLine> lines = new ArrayList<MOrderLine>();
      for (int j = 0; j < products.size(); j++) {
        MProduct prod = products.get(j);
        MOrderLine line = new MOrderLine(order);
        line.setProduct(prod);
        line.setQty(new BigDecimal(this.p_QtyEntered));
        line.setDescription(this.p_Prefix + "_O_L_" + j);
        line.setC_Tax_ID(this.C_Tax_ID);
        lines.add(line);
        countLine++;
      } 
//      if (!PO.saveAll(order.get_TrxName(), lines))
      if (!po.save(order.get_TrxName()))

      this.log.warning(": Lines not saved"); 
      orders.add(order);
    } 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "Created POs in " + duration + "s = " + durationMS + "ms = " + (durationMS / orders.size()) + "ms/PO ";
    this.log.info(msg);
    addLog(msg);
    for (MOrder order : orders) {
      if (!Util.isEmpty("CO")) {
        order.setDocAction("CO");
        
        DocumentEngine engine = new DocumentEngine((DocAction)order, "CO");
        if(!engine.processIt("CO"))
       // if (!DocumentEngine.processIt((DocAction)order, "CO"))
          this.log.warning(" Not processed"); 
        if (!order.save())
          this.log.warning("Not saved(2)"); 
      } 
    } 
    long end2 = System.currentTimeMillis();
    long durationMS2 = end2 - end;
    long duration2 = durationMS2 / 1000L;
    countOrder = orders.size();
    countLine = orders.size() * this.m_products.size();
    String msg2 = "DocAction CO Order #" + orders.size() + " | " + countLine + " in " + duration2 + "s = " + durationMS2 + "ms = " + (durationMS2 / countOrder) + "ms/Order = " + (durationMS2 / countLine) + "ms/Line";
    this.log.info(msg2);
    addLog(msg2);
    createFromOrder(orders);
  }
  
  private MWMSTaskList[] generatePutaway() {
    long start = System.currentTimeMillis();
    int AD_Process_ID = this.p_PutawayProcess_ID;
    MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
    if (!instance.save()) {
      this.log.log(Level.SEVERE, Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
      return null;
    } 
    ProcessInfo pi = new ProcessInfo("Generate Putaway", AD_Process_ID);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    MPInstancePara ip = new MPInstancePara(instance, 10);
    ip.setParameter("M_Warehouse_ID", this.p_M_Warehouse_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 20);
    ip.setParameter("M_Locator_ID", this.p_M_Locator_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 30);
    ip.setParameter("C_DocTypeTask_ID", this.p_C_DocTypePutaway_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 40);
    ip.setParameter("DocAction", this.p_PutawayDocAction);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    pi.setAD_Client_ID(Env.getAD_Client_ID(getCtx()));
    pi.setAD_User_ID(Env.getAD_User_ID(getCtx()));
    pi.setIsBatch(false);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    GeneratePutaway putaway = new GeneratePutaway();
    putaway.startProcess(getCtx(), pi, trx);
    MWMSTaskList[] putawayLists = putaway.getTaskLists();
    commit();
    if (putawayLists != null) {
      String str = "Created #" + putawayLists.length + " Putaway lists";
      this.log.info(str);
      addLog(str);
    } 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "Generated Putaway in " + duration + " s = " + durationMS + " ms ";
    this.log.info(msg);
    addLog(msg);
    return putawayLists;
  }
  
  private MWMSTaskList[] generateWave() {
    long start = System.currentTimeMillis();
    int AD_Process_ID = this.p_WavePlanningProcess_ID;
    MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
    if (!instance.save()) {
      this.log.log(Level.SEVERE, Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
      return null;
    } 
    ProcessInfo pi = new ProcessInfo("Wave Planning Process", AD_Process_ID);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    MPInstancePara ip = new MPInstancePara(instance, 10);
    ip.setParameter("M_Warehouse_ID", this.p_M_Warehouse_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 20);
    ip.setParameter("WaveDocBaseType", "SOO");
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    if (this.isGenerateReleaseSingleStep) {
      ip = new MPInstancePara(instance, 30);
      ip.setParameter("IsGeneratePickList", "Y");
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
      ip = new MPInstancePara(instance, 40);
      ip.setParameter("PickMethod", this.p_PickMethod);
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
      ip = new MPInstancePara(instance, 50);
      ip.setParameter("ClusterSize", this.p_clusterSize);
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
      ip = new MPInstancePara(instance, 60);
      ip.setParameter("C_DocTypeTask_ID", this.p_C_DocTypePick_ID);
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
      ip = new MPInstancePara(instance, 70);
      ip.setParameter("DocAction", this.p_PickDocAction);
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
      ip = new MPInstancePara(instance, 80);
      ip.setParameter("M_Locator_ID", this.p_stagingLocator_ID);
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
      ip = new MPInstancePara(instance, 90);
      ip.setParameter("IsPrintPickList", this.p_IsPrintPickList);
      if (!ip.save()) {
        String str = "No Parameter added";
        this.log.log(Level.SEVERE, str);
        return null;
      } 
    } 
    pi.setAD_Client_ID(Env.getAD_Client_ID(getCtx()));
    pi.setAD_User_ID(Env.getAD_User_ID(getCtx()));
    pi.setIsBatch(false);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    CreateWave wave = new CreateWave();
    wave.startProcess(getCtx(), pi, trx);
    this.m_wave = wave.getWave();
    commit();
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "Generated Wave in " + duration + " s = " + durationMS + " ms ";
    this.log.info(msg);
    addLog(msg);
    if (this.m_wave != null) {
      msg = "Wave No : " + this.m_wave.getDocumentNo();
      this.log.info(msg);
      addLog(msg);
    } 
    if (this.isGenerateReleaseSingleStep)
      return wave.getM_taskLists(); 
    return null;
  }
  
  private MWMSTaskList[] releaseWave() {
    if (this.isGenerateReleaseSingleStep)
      return null; 
    if (this.m_wave == null) {
      this.log.warning("No wave created");
      return null;
    } 
    long start = System.currentTimeMillis();
    int AD_Process_ID = this.p_ReleaseWaveProcess_ID;
    MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
    if (!instance.save()) {
      this.log.log(Level.SEVERE, Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
      return null;
    } 
    ProcessInfo pi = new ProcessInfo("Wave Planning Process", AD_Process_ID);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    MPInstancePara ip = new MPInstancePara(instance, 10);
    ip.setParameter("M_Warehouse_ID", this.p_M_Warehouse_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 20);
    ip.setParameter("C_Wave_ID", this.m_wave.getWMS_Wave_ID());
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 30);
    ip.setParameter("PickMethod", this.p_PickMethod);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 40);
    ip.setParameter("ClusterSize", this.p_clusterSize);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 50);
    ip.setParameter("C_DocTypeTask_ID", this.p_C_DocTypePick_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 60);
    ip.setParameter("DocAction", this.p_PickDocAction);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 70);
    ip.setParameter("M_Locator_ID", this.p_stagingLocator_ID);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    ip = new MPInstancePara(instance, 80);
    ip.setParameter("IsPrintPickList", this.p_IsPrintPickList);
    if (!ip.save()) {
      String str = "No Parameter added";
      this.log.log(Level.SEVERE, str);
      return null;
    } 
    pi.setAD_Client_ID(Env.getAD_Client_ID(getCtx()));
    pi.setAD_User_ID(Env.getAD_User_ID(getCtx()));
    pi.setIsBatch(false);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    ReleaseWave wave = new ReleaseWave();
    wave.startProcess(getCtx(), pi, trx);
    commit();
    MWMSTaskList[] taskLists = wave.getTaskLists();
    if (taskLists != null) {
      String str = "Created #" + taskLists.length + " task lists";
      this.log.info(str);
      addLog(str);
    } 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "Released Wave in " + duration + " s = " + durationMS + " ms ";
    this.log.info(msg);
    addLog(msg);
    return taskLists;
  }
  
  private void completeTasks(MWMSTaskList[] taskLists) throws Exception {
    long start = System.currentTimeMillis();
    ArrayList<MWMSWarehouseTask> tasks = new ArrayList<MWMSWarehouseTask>();
    for (MWMSTaskList taskList : taskLists) {
      for (MWMSWarehouseTask task : taskList.getTasks()) {
        task.setDocAction(X_WMS_WarehouseTask.DOCACTION_Complete);
        
        DocumentEngine engine = new DocumentEngine((DocAction)task,X_WMS_WarehouseTask.DOCACTION_Complete);
        if(!engine.processIt(X_WMS_WarehouseTask.DOCACTION_Complete)) {
     //   if (!DocumentEngine.processIt((DocAction)task, X_WMS_WarehouseTask.DOCACTION_Complete)) {
          String str = "Could not complete task " + task.getDocumentNo();
          this.log.severe(str);
          return;
        } 
        tasks.add(task);
      } 
    } 
//    if (!PO.saveAll(get_TrxName(), tasks)) {
    if (!po.save(get_TrxName())) {

    String str = "Could not save tasks ";
      this.log.severe(str);
    } 
    long end = System.currentTimeMillis();
    long durationMS = end - start;
    long duration = durationMS / 1000L;
    String msg = "Completed # " + tasks.size() + " tasks in " + duration + " s = " + durationMS + " ms = " + (durationMS / tasks.size()) + " ms/task ";
    this.log.info(msg);
    addLog(msg);
  }
  
  private ArrayList<MBPartner> getBPartners() {
    CPreparedStatement cPreparedStatement = null;
    if (this.m_bpartners.size() > 0)
      return this.m_bpartners; 
    String sql = "SELECT * FROM C_BPartner WHERE IsActive='Y' AND (IsCustomer='Y' OR IsVendor='Y') AND AD_Client_ID=?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, Env.getAD_Client_ID(getCtx()));
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        this.m_bpartners.add(new MBPartner(getCtx(), rs, null)); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    this.log.info("Loaded #" + this.m_bpartners.size());
    if (this.m_bpartners.size() == 0)
      return null; 
    return this.m_bpartners;
  }
  
  private ArrayList<MProduct> getProducts() {
    CPreparedStatement cPreparedStatement = null;
    if (this.m_products.size() > 0)
      return this.m_products; 
    String sql = "SELECT * FROM M_Product p WHERE IsActive='Y' AND AD_Client_ID=? AND EXISTS (SELECT * FROM M_ProductPrice pp WHERE pp.M_PriceList_Version_ID=? AND pp.M_Product_ID=p.M_Product_ID)";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, Env.getAD_Client_ID(getCtx()));
      cPreparedStatement.setInt(2, this.m_plv.getM_PriceList_Version_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        this.m_products.add(new MProduct(getCtx(), rs, null)); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, sql, e);
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    this.log.info("Loaded #" + this.m_products.size());
    if (this.m_products.size() == 0)
      return null; 
    return this.m_products;
  }
  
  public static void main(String[] args) {
    System.setProperty("PropertyFile", "/home/namitha/Useful/cyprusbrs.properties");
 //   cyprusbrs.startup(true);
    CLogMgt.setLoggerLevel(Level.INFO, null);
    CLogMgt.setLevel(Level.INFO);
    Ini.setProperty("ApplicationUserID", "GardenAdmin");
    Ini.setProperty("ApplicationPassword", "GardenAdmin");
    Ini.setProperty("Role", "GardenWorld Admin");
    Ini.setProperty("Client", "GardenWorld");
    Ini.setProperty("Organization", "HQ");
    Ini.setProperty("Warehouse", "HQ Warehouse");
    Ini.setProperty("Language", "English");
    Properties ctx = Env.getCtx();
    Login login = new Login(ctx);
    if (!login.batchLogin(null))
      System.exit(1); 
    CLogMgt.setLoggerLevel(Level.WARNING, null);
    CLogMgt.setLevel(Level.WARNING);
    int AD_Client_ID = Env.getAD_Client_ID(ctx);
    int AD_User_ID = Env.getAD_User_ID(ctx);
    int AD_Process_ID = 412;
    int AD_Table_ID = 0;
    int Record_ID = 0;
    MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, Record_ID);
    instance.save();
    ProcessInfo pi = new ProcessInfo("VolumeTest", AD_Process_ID, AD_Table_ID, Record_ID);
    pi.setAD_Client_ID(AD_Client_ID);
    pi.setAD_User_ID(AD_User_ID);
    pi.setIsBatch(false);
    pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    VolumeTestTasks test = new VolumeTestTasks();
    if (args.length > 0)
      test.p_VolumeBPartner = PO.convertToInt(args[0]); 
    if (args.length > 1)
      test.p_VolumeProduct = PO.convertToInt(args[1]); 
    if (args.length > 2)
      test.p_VolumeOrder = PO.convertToInt(args[2]); 
    if (args.length > 3)
      test.p_VolumeLine = PO.convertToInt(args[3]); 
    test.p_PickDocAction = X_WMS_WarehouseTask.DOCACTION_Prepare;
    test.p_PutawayDocAction = X_WMS_WarehouseTask.DOCACTION_Prepare;
    test.startProcess(ctx, pi, null);
    if (pi.isError()) {
      System.err.println("Error: " + pi.getSummary());
    } else {
      System.out.println("OK: " + pi.getSummary());
    } 
    System.out.println(pi.getLogInfo());
    String logResult = DB.stopLoggingUpdates(0);
    System.out.println(logResult);
  }
}
