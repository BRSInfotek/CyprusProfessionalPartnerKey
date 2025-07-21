package org.cyprus.webui.apps.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.exceptions.DBException;
import org.cyprus.webui.component.Button;
import org.cyprus.webui.component.Combobox;
import org.cyprus.webui.component.Grid;
import org.cyprus.webui.component.GridFactory;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.ListboxFactory;
import org.cyprus.webui.component.Panel;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.component.Rows;
import org.cyprus.webui.component.Tab;
import org.cyprus.webui.component.Tabbox;
import org.cyprus.webui.component.Tabs;
import org.cyprus.webui.component.Textbox;
import org.cyprus.webui.component.WListbox;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.editor.WLocatorEditor;
import org.cyprus.webui.editor.WNumberEditor;
import org.cyprus.webui.editor.WPAttributeEditor;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.event.WTableModelEvent;
import org.cyprus.webui.event.WTableModelListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.CustomForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprus.webui.session.SessionManager;
import org.cyprusbrs.framework.MAttributeSetInstance;
import org.cyprusbrs.framework.MLocatorLookup;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MWindow;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.minigrid.IMiniTable;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridFieldVO;
import org.cyprusbrs.model.Lookup;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.model.MTab;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Language;
import org.cyprusbrs.util.Msg;
//import org.eevolution.model.MPPOrder;
//import org.eevolution.model.MPPOrderBOMLine;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Html;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;

public class WOrderReceiptIssue implements IFormController, EventListener, ValueChangeListener, Serializable, WTableModelListener {
  private static final long serialVersionUID = 1544662359277562456L;
  
  private int m_WindowNo = 0;
  
  private String m_sql;
  
//  private MPPOrder m_PP_order = null;
  
  private Panel Generate = new Panel();
  
  private Panel PanelBottom = new Panel();
  
  private Panel mainPanel = new Panel();
  
  private Panel northPanel = new Panel();
  
  private Button Process = new Button();
  
  private Label attributeLabel = new Label();
  
  private Label orderedQtyLabel = new Label();
  
  private Label deliveredQtyLabel = new Label();
  
  private Label openQtyLabel = new Label();
  
  private Label orderLabel = new Label();
  
  private Label toDeliverQtyLabel = new Label();
  
  private Label movementDateLabel = new Label();
  
  private Label rejectQtyLabel = new Label();
  
  private Label resourceLabel = new Label();
  
  private CustomForm form = new CustomForm();
  
  private Borderlayout ReceiptIssueOrder = new Borderlayout();
  
  private Tabbox TabsReceiptsIssue = new Tabbox();
  
  private Html info = new Html();
  
  private Grid fieldGrid = GridFactory.newGridLayout();
  
  private WPAttributeEditor attribute = null;
  
  private Label warehouseLabel = new Label();
  
  private Label scrapQtyLabel = new Label();
  
  private Label productLabel = new Label(Msg.translate(Env.getCtx(), "M_Product_ID"));
  
  private Label uomLabel = new Label(Msg.translate(Env.getCtx(), "C_UOM_ID"));
  
  private Label uomorderLabel = new Label(Msg.translate(Env.getCtx(), "Altert UOM"));
  
  private Label locatorLabel = new Label(Msg.translate(Env.getCtx(), "M_Locator_ID"));
  
  private Label backflushGroupLabel = new Label(Msg.translate(Env.getCtx(), "BackflushGroup"));
  
  private Label labelcombo = new Label(Msg.translate(Env.getCtx(), "DeliveryRule"));
  
  private Label QtyBatchsLabel = new Label();
  
  private Label QtyBatchSizeLabel = new Label();
  
  private Textbox backflushGroup = new Textbox();
  
  private WNumberEditor orderedQtyField = new WNumberEditor("QtyOrdered", false, false, false, 29, "QtyOrdered");
  
  private WNumberEditor deliveredQtyField = new WNumberEditor("QtyDelivered", false, false, false, 29, "QtyDelivered");
  
  private WNumberEditor openQtyField = new WNumberEditor("QtyOpen", false, false, false, 29, "QtyOpen");
  
  private WNumberEditor toDeliverQty = new WNumberEditor("QtyToDeliver", true, false, true, 29, "QtyToDeliver");
  
  private WNumberEditor rejectQty = new WNumberEditor("Qtyreject", false, false, true, 29, "QtyReject");
  
  private WNumberEditor scrapQtyField = new WNumberEditor("Qtyscrap", false, false, true, 29, "Qtyscrap");
  
  private WNumberEditor qtyBatchsField = new WNumberEditor("QtyBatchs", false, false, false, 29, "QtyBatchs");
  
  private WNumberEditor qtyBatchSizeField = new WNumberEditor("QtyBatchSize", false, false, false, 29, "QtyBatchSize");
  
  private WSearchEditor orderField = null;
  
  private WSearchEditor resourceField = null;
  
  private WSearchEditor warehouseField = null;
  
  private WSearchEditor productField = null;
  
  private WSearchEditor uomField = null;
  
  private WSearchEditor uomorderField = null;
  
  private WListbox issue = ListboxFactory.newDataTable();
  
  private WDateEditor movementDateField = new WDateEditor("MovementDate", true, false, true, "MovementDate");
  
  private WLocatorEditor locatorField = null;
  
  private Combobox pickcombo = new Combobox();
  
  public WOrderReceiptIssue() {
    Env.setContext(Env.getCtx(), this.form.getWindowNo(), "IsSOTrx", "Y");
    try {
      fillPicks();
      jbInit();
      dynInit();
      this.pickcombo.addEventListener("onChange", this);
    } catch (Exception e) {
      throw new CyprusException(e);
    } 
  }
  
  private void fillPicks() throws Exception {
    Properties ctx = Env.getCtx();
    Language language = Language.getLoginLanguage();
    MLookup orderLookup = MLookupFactory.get(ctx, this.m_WindowNo, 
        MColumn.getColumn_ID("PP_Order", "PP_Order_ID"), 
        30, language, "PP_Order_ID", 0, false, 
        "PP_Order.DocStatus = 'DR'"); // Updated by Mukesh to check 
    this.orderField = new WSearchEditor("PP_Order_ID", false, false, true, (Lookup)orderLookup);
    this.orderField.addValueChangeListener(this);
    MLookup resourceLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, 
        MColumn.getColumn_ID("PP_Order", "S_Resource_ID"), 
        19);
    this.resourceField = new WSearchEditor("S_Resource_ID", false, false, false, (Lookup)resourceLookup);
    MLookup warehouseLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, 
        MColumn.getColumn_ID("PP_Order", "M_Warehouse_ID"), 
        19);
    this.warehouseField = new WSearchEditor("M_Warehouse_ID", false, false, false, (Lookup)warehouseLookup);
    MLookup productLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, 
        MColumn.getColumn_ID("PP_Order", "M_Product_ID"), 
        19);
    this.productField = new WSearchEditor("M_Product_ID", false, false, false, (Lookup)productLookup);
    MLookup uomLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, 
        MColumn.getColumn_ID("PP_Order", "C_UOM_ID"), 
        19);
    this.uomField = new WSearchEditor("C_UOM_ID", false, false, false, (Lookup)uomLookup);
    MLookup uomOrderLookup = MLookupFactory.get(ctx, this.m_WindowNo, 0, 
        MColumn.getColumn_ID("PP_Order", "C_UOM_ID"), 
        19);
    this.uomorderField = new WSearchEditor("C_UOM_ID", false, false, false, (Lookup)uomOrderLookup);
    MLocatorLookup locatorL = new MLocatorLookup(ctx, this.m_WindowNo);
    this.locatorField = new WLocatorEditor("M_Locator_ID", true, false, true, locatorL, this.m_WindowNo);
    int m_Window = MWindow.getWindow_ID("Manufacturing Order");
    GridFieldVO vo = GridFieldVO.createStdField(ctx, this.m_WindowNo, 0, m_Window, MTab.getTab_ID(m_Window, "Order"), 
        false, false, false);
    vo.AD_Column_ID = MColumn.getColumn_ID("PP_Order", "M_AttributeSetInstance_ID");
    GridField field = new GridField(vo);
    this.attribute = new WPAttributeEditor(field.getGridTab(), field);
    this.attribute.setValue(Integer.valueOf(0));
    this.scrapQtyField.setValue(Env.ZERO);
    this.rejectQty.setValue(Env.ZERO);
    this.pickcombo.appendItem(Msg.translate(Env.getCtx(), "IsBackflush"), Integer.valueOf(1));
    this.pickcombo.appendItem(Msg.translate(Env.getCtx(), "OnlyIssue"), Integer.valueOf(2));
    this.pickcombo.appendItem(Msg.translate(Env.getCtx(), "OnlyReceipt"), Integer.valueOf(3));
    this.pickcombo.addEventListener("onChange", this);
    this.Process.addActionListener(this);
    this.toDeliverQty.addValueChangeListener(this);
    this.scrapQtyField.addValueChangeListener(this);
  }
  
  private void jbInit() throws Exception {
    Center center = new Center();
    South south = new South();
    North north = new North();
    this.form.appendChild((Component)this.mainPanel);
    this.mainPanel.appendChild((Component)this.TabsReceiptsIssue);
    this.mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
    this.ReceiptIssueOrder.setWidth("100%");
    this.ReceiptIssueOrder.setHeight("99%");
    this.ReceiptIssueOrder.appendChild((Component)north);
    north.appendChild((Component)this.northPanel);
    this.northPanel.appendChild((Component)this.fieldGrid);
    this.orderLabel.setText(Msg.translate(Env.getCtx(), "PP_Order_ID"));
    Rows tmpRows = this.fieldGrid.newRows();
    Row row = tmpRows.newRow();
    row.appendChild((Component)this.orderLabel);
    row.appendChild((Component)this.orderField.getComponent());
    this.resourceLabel.setText(Msg.translate(Env.getCtx(), "S_Resource_ID"));
    row.appendChild((Component)this.resourceLabel);
    row.appendChild((Component)this.resourceField.getComponent());
    this.warehouseLabel.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
    row.appendChild((Component)this.warehouseLabel);
    row.appendChild((Component)this.warehouseField.getComponent());
    row = tmpRows.newRow();
    row.appendChild((Component)this.productLabel);
    row.appendChild((Component)this.productField.getComponent());
    row.appendChild((Component)this.uomLabel);
    row.appendChild((Component)this.uomField.getComponent());
    row.appendChild((Component)this.uomorderLabel);
    row.appendChild((Component)this.uomorderField.getComponent());
    row = tmpRows.newRow();
    this.orderedQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOrdered"));
    row.appendChild((Component)this.orderedQtyLabel);
    row.appendChild((Component)this.orderedQtyField.getComponent());
    this.deliveredQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyDelivered"));
    row.appendChild((Component)this.deliveredQtyLabel);
    row.appendChild((Component)this.deliveredQtyField.getComponent());
    this.openQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOpen"));
    row.appendChild((Component)this.openQtyLabel);
    row.appendChild((Component)this.openQtyField.getComponent());
    row = tmpRows.newRow();
    row.appendChild((Component)this.productLabel);
    row.appendChild((Component)this.productField.getComponent());
    row.appendChild((Component)this.uomLabel);
    row.appendChild((Component)this.uomField.getComponent());
    row.appendChild((Component)this.uomorderLabel);
    row.appendChild((Component)this.uomorderField.getComponent());
    row = tmpRows.newRow();
    this.QtyBatchsLabel.setText(Msg.translate(Env.getCtx(), "QtyBatchs"));
    row.appendChild((Component)this.QtyBatchsLabel);
    row.appendChild((Component)this.qtyBatchsField.getComponent());
    this.QtyBatchSizeLabel.setText(Msg.translate(Env.getCtx(), "QtyBatchSize"));
    row.appendChild((Component)this.QtyBatchSizeLabel);
    row.appendChild((Component)this.qtyBatchSizeField.getComponent());
    this.openQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOpen"));
    row.appendChild((Component)this.openQtyLabel);
    row.appendChild((Component)this.openQtyField.getComponent());
    row = tmpRows.newRow();
    row.appendChild((Component)this.labelcombo);
    row.appendChild((Component)this.pickcombo);
    row.appendChild((Component)this.backflushGroupLabel);
    row.appendChild((Component)this.backflushGroup);
    row.appendChild((Component)new Space());
    row.appendChild((Component)new Space());
    row = tmpRows.newRow();
    this.toDeliverQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyToDeliver"));
    row.appendChild((Component)this.toDeliverQtyLabel);
    row.appendChild((Component)this.toDeliverQty.getComponent());
    this.scrapQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyScrap"));
    row.appendChild((Component)this.scrapQtyLabel);
    row.appendChild((Component)this.scrapQtyField.getComponent());
    this.rejectQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyReject"));
    row.appendChild((Component)this.rejectQtyLabel);
    row.appendChild((Component)this.rejectQty.getComponent());
    row = tmpRows.newRow();
    this.movementDateLabel.setText(Msg.translate(Env.getCtx(), "MovementDate"));
    row.appendChild((Component)this.movementDateLabel);
    row.appendChild((Component)this.movementDateField.getComponent());
    this.locatorLabel.setText(Msg.translate(Env.getCtx(), "M_Locator_ID"));
    row.appendChild((Component)this.locatorLabel);
    row.appendChild((Component)this.locatorField.getComponent());
    this.attributeLabel.setText(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
    row.appendChild((Component)this.attributeLabel);
    row.appendChild((Component)this.attribute.getComponent());
    this.ReceiptIssueOrder.appendChild((Component)center);
    center.appendChild((Component)this.issue);
    this.ReceiptIssueOrder.appendChild((Component)south);
    south.appendChild((Component)this.PanelBottom);
    this.Process.setLabel(Msg.translate(Env.getCtx(), "OK"));
    this.PanelBottom.appendChild((Component)this.Process);
    Tabs tabs = new Tabs();
    Tab tab1 = new Tab();
    Tab tab2 = new Tab();
    tab1.setLabel(Msg.translate(Env.getCtx(), "IsShipConfirm"));
    tab2.setLabel(Msg.translate(Env.getCtx(), "Generate"));
    tabs.appendChild((Component)tab1);
    tabs.appendChild((Component)tab2);
    this.TabsReceiptsIssue.appendChild((Component)tabs);
    Tabpanels tabps = new Tabpanels();
    Tabpanel tabp1 = new Tabpanel();
    Tabpanel tabp2 = new Tabpanel();
    this.TabsReceiptsIssue.appendChild((Component)tabps);
    this.TabsReceiptsIssue.setWidth("100%");
    this.TabsReceiptsIssue.setHeight("100%");
    tabps.appendChild((Component)tabp1);
    tabps.appendChild((Component)tabp2);
    tabp1.appendChild((Component)this.ReceiptIssueOrder);
    tabp1.setWidth("100%");
    tabp1.setHeight("100%");
    tabp2.appendChild((Component)this.Generate);
    tabp2.setWidth("100%");
    tabp2.setHeight("100%");
    this.Generate.appendChild((Component)this.info);
    this.Generate.setVisible(true);
    this.info.setVisible(true);
    this.TabsReceiptsIssue.addEventListener("onChange", this);
  }
  
  public void dynInit() {
    disableToDeliver();
    prepareTable((IMiniTable)this.issue);
    this.issue.autoSize();
    this.issue.getModel().addTableModelListener(this);
    this.issue.setRowCount(0);
  }
  
  public void prepareTable(IMiniTable miniTable) {
//    this.m_sql = this.issue.prepareTable(
//        new ColumnInfo[] { 
//          new ColumnInfo(" ", "obl.PP_Order_BOMLine_ID as id", IDColumn.class, false, false, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "IsCritical"), "obl.IsCritical as isCritical", Boolean.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "Value"), "p.Value as Value", String.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "M_Product_ID"), "obl.M_Product_ID as id_p,p.Name as name_p", KeyNamePair.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "C_UOM_ID"), "p.C_UOM_ID as id_u ,u.Name as name_u", KeyNamePair.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"), "obl.ComponentType as componentType", String.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyRequired"), "obl.QtyRequiered as qtyRequired", BigDecimal.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyDelivered"), "obl.QtyDelivered as qtyDelivered", BigDecimal.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyToDeliver"), "obl.QtyRequiered - QtyDelivered AS qtyOpen", BigDecimal.class, false, false, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyScrap"), "QtyScrap", BigDecimal.class, false, false, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyOnHand"), "bomQtyOnHand(obl.M_Product_ID,obl.M_Warehouse_ID,0) AS qtyOnHand", BigDecimal.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyReserved"), "obl.QtyReserved as QtyReserved", BigDecimal.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyAvailable"), "bomQtyAvailable(obl.M_Product_ID,obl.M_Warehouse_ID,0 ) AS QtyAvailable", BigDecimal.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "M_Locator_ID"), "p.M_Locator_ID", String.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "M_Warehouse_ID"), "obl.M_Warehouse_ID as id_w,w.Name as name_w", KeyNamePair.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyBom"), "obl.QtyBom as qtyBom", BigDecimal.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "IsQtyPercentage"), "obl.isQtyPercentage as isQtyPercentage", Boolean.class, true, true, null), 
//          new ColumnInfo(Msg.translate(Env.getCtx(), "QtyBatch"), "obl.QtyBatch as qtyBatch", BigDecimal.class, true, true, null) }"PP_Order_BOMLine obl INNER JOIN M_Product p ON (obl.M_Product_ID = p.M_Product_ID)  INNER JOIN C_UOM u ON (p.C_UOM_ID = u.C_UOM_ID)  INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = obl.M_Warehouse_ID) ", 
//        
//        " obl.PP_Order_ID = ?", 
//        true, "obl");
  }
  
  public void onEvent(Event e) throws Exception {
    if (e.getName().equals("onCancel")) {
      dispose();
      return;
    } 
    if (e.getTarget().equals(this.Process)) {
      if (getMovementDate() == null) {
        try {
          Messagebox.show(Msg.getMsg(Env.getCtx(), "NoDate"), "Info", 1, "z-msgbox z-msgbox-information");
        } catch (InterruptedException ex) {
          throw new CyprusException(ex);
        } 
        return;
      } 
      if ((isOnlyReceipt() || isBackflush()) && getM_Locator_ID() <= 0) {
        try {
          Messagebox.show(Msg.getMsg(Env.getCtx(), "NoLocator"), "Info", 1, "z-msgbox z-msgbox-information");
        } catch (InterruptedException ex) {
          throw new CyprusException(ex);
        } 
        return;
      } 
      this.TabsReceiptsIssue.setSelectedIndex(1);
      generateSummaryTable();
      int result = -1;
      try {
        result = Messagebox.show(Msg.getMsg(Env.getCtx(), "Update"), "", 3, "z-msgbox z-msgbox-question");
      } catch (InterruptedException ex) {
        throw new CyprusException(ex);
      } 
      if (result == 1) {
        Clients.showBusy(null, false);
      } 
      this.TabsReceiptsIssue.setSelectedIndex(0);
    } 
    if (e.getTarget().equals(this.toDeliverQty) || e.getTarget().equals(this.scrapQtyField))
      if (getPP_Order_ID() > 0 && isBackflush())
        executeQuery();  
    if (e.getTarget().equals(this.pickcombo)) {
      if (isOnlyReceipt()) {
        enableToDeliver();
        this.locatorLabel.setVisible(true);
        this.locatorField.setVisible(true);
        this.attribute.setVisible(true);
        this.attributeLabel.setVisible(true);
        this.issue.setVisible(false);
      } else if (isOnlyIssue()) {
        disableToDeliver();
        this.locatorLabel.setVisible(false);
        this.locatorField.setVisible(false);
        this.attribute.setVisible(false);
        this.attributeLabel.setVisible(false);
        this.issue.setVisible(true);
        executeQuery();
      } else if (isBackflush()) {
        enableToDeliver();
        this.locatorLabel.setVisible(true);
        this.locatorField.setVisible(true);
        this.attribute.setVisible(true);
        this.attributeLabel.setVisible(true);
        this.issue.setVisible(true);
        executeQuery();
      } 
      setToDeliverQty(getOpenQty());
    } 
  }
  
  public void enableToDeliver() {
    setToDeliver(Boolean.valueOf(true));
  }
  
  public void disableToDeliver() {
    setToDeliver(Boolean.valueOf(false));
  }
  
  private void setToDeliver(Boolean state) {
    this.toDeliverQty.getComponent().setEnabled(state.booleanValue());
    this.scrapQtyLabel.setVisible(state.booleanValue());
    this.scrapQtyField.setVisible(state.booleanValue());
    this.rejectQtyLabel.setVisible(state.booleanValue());
    this.rejectQty.setVisible(state.booleanValue());
  }
  
  public void executeQuery() {
    CPreparedStatement cPreparedStatement = null;
    String sql = String.valueOf(this.m_sql) + " ORDER BY obl." + "Line";
    this.issue.clearTable();
    int row = 0;
    this.issue.setRowCount(row);
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, null);
      cPreparedStatement.setInt(1, getPP_Order_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        this.issue.setRowCount(row + 1);
        IDColumn id = new IDColumn(rs.getInt("id"));
        BigDecimal qtyBom = rs.getBigDecimal("qtyBom");
        Boolean isQtyPercentage = Boolean.valueOf(rs.getString("isQtyPercentage").equals("Y"));
        Boolean isCritical = Boolean.valueOf(rs.getString("isCritical").equals("Y"));
        BigDecimal qtyBatch = rs.getBigDecimal("qtyBatch");
        BigDecimal qtyRequired = rs.getBigDecimal("qtyRequired");
        BigDecimal qtyOnHand = rs.getBigDecimal("qtyOnHand");
        BigDecimal qtyOpen = rs.getBigDecimal("qtyOpen");
        BigDecimal qtyDelivered = rs.getBigDecimal("qtyDelivered");
        String componentType = rs.getString("componentType");
        BigDecimal toDeliverQty = getToDeliverQty();
        BigDecimal openQty = getOpenQty();
        BigDecimal scrapQty = getScrapQty();
        BigDecimal componentToDeliverQty = Env.ZERO;
        BigDecimal componentScrapQty = Env.ZERO;
        BigDecimal componentQtyReq = Env.ZERO;
        BigDecimal componentQtyToDel = Env.ZERO;
        id.setSelected(isOnlyReceipt());
        this.issue.setValueAt(id, row, 0);
        this.issue.setValueAt(isCritical, row, 1);
        this.issue.setValueAt(rs.getString("Value"), row, 2);
        this.issue.setValueAt(new KeyNamePair(rs.getInt("id_p"), rs.getString("name_p")), row, 3);
        this.issue.setValueAt(new KeyNamePair(rs.getInt("id_u"), rs.getString("name_u")), row, 4);
        this.issue.setValueAt(qtyRequired, row, 6);
        this.issue.setValueAt(qtyDelivered, row, 7);
        this.issue.setValueAt(qtyOnHand, row, 10);
        this.issue.setValueAt(rs.getBigDecimal("QtyReserved"), row, 11);
        this.issue.setValueAt(rs.getBigDecimal("QtyAvailable"), row, 12);
        this.issue.setValueAt(new KeyNamePair(rs.getInt("id_w"), rs.getString("name_w")), row, 14);
        this.issue.setValueAt(qtyBom, row, 15);
        this.issue.setValueAt(isQtyPercentage, row, 16);
        this.issue.setValueAt(qtyBatch, row, 17);
        if (componentType.equals("CO") || 
          componentType.equals("PK")) {
          id.setSelected((qtyOnHand.signum() > 0 && qtyRequired.signum() > 0));
          if (isQtyPercentage.booleanValue()) {
            BigDecimal qtyBatchPerc = qtyBatch.divide(Env.ONEHUNDRED, 8, 4);
            if (isBackflush()) {
              if (qtyRequired.signum() == 0 || qtyOpen.signum() == 0) {
                componentToDeliverQty = Env.ZERO;
              } else {
                componentToDeliverQty = toDeliverQty.multiply(qtyBatchPerc);
                if ((((qtyRequired.subtract(qtyDelivered).signum() < 0) ? 1 : 0) | ((componentToDeliverQty.signum() == 0) ? 1 : 0)) != 0)
                  componentToDeliverQty = qtyRequired.subtract(qtyDelivered); 
              } 
              if (componentToDeliverQty.signum() != 0) {
                componentQtyToDel = componentToDeliverQty.setScale(4, 4);
                this.issue.setValueAt(componentToDeliverQty, row, 8);
              } 
            } else {
              componentToDeliverQty = qtyOpen;
              if (componentToDeliverQty.signum() != 0) {
                componentQtyReq = openQty.multiply(qtyBatchPerc);
                componentQtyToDel = componentToDeliverQty.setScale(4, 4);
                this.issue.setValueAt(componentToDeliverQty.setScale(8, 4), row, 8);
                this.issue.setValueAt(openQty.multiply(qtyBatchPerc), row, 6);
              } 
            } 
            if (scrapQty.signum() != 0) {
              componentScrapQty = scrapQty.multiply(qtyBatchPerc);
              if (componentScrapQty.signum() != 0)
                this.issue.setValueAt(componentScrapQty, row, 9); 
            } 
          } else {
            if (isBackflush()) {
              componentToDeliverQty = toDeliverQty.multiply(qtyBom);
              if (componentToDeliverQty.signum() != 0) {
                componentQtyReq = toDeliverQty.multiply(qtyBom);
                componentQtyToDel = componentToDeliverQty;
                this.issue.setValueAt(componentQtyReq, row, 6);
                this.issue.setValueAt(componentToDeliverQty, row, 8);
              } 
            } else {
              componentToDeliverQty = qtyOpen;
              if (componentToDeliverQty.signum() != 0) {
                componentQtyReq = openQty.multiply(qtyBom);
                componentQtyToDel = componentToDeliverQty;
                this.issue.setValueAt(componentQtyReq, row, 6);
                this.issue.setValueAt(componentToDeliverQty, row, 8);
              } 
            } 
            if (scrapQty.signum() != 0) {
              componentScrapQty = scrapQty.multiply(qtyBom);
              if (componentScrapQty.signum() != 0)
                this.issue.setValueAt(componentScrapQty, row, 9); 
            } 
          } 
        } else if (componentType.equals("TL")) {
          componentToDeliverQty = qtyBom;
          if (componentToDeliverQty.signum() != 0) {
            componentQtyReq = qtyBom;
            componentQtyToDel = componentToDeliverQty;
            this.issue.setValueAt(qtyBom, row, 6);
            this.issue.setValueAt(componentToDeliverQty, row, 8);
          } 
        } else {
          this.issue.setValueAt(Env.ZERO, row, 6);
          this.issue.setValueAt(Env.ZERO, row, 8);
        } 
        if (this.issue.getValueAt(row, 9) == null)
          this.issue.setValueAt(Env.ZERO, row, 9); 
        if (this.issue.getValueAt(row, 8) == null)
          this.issue.setValueAt(Env.ZERO, row, 8); 
        row++;
        if (isOnlyIssue() || isBackflush()) {
          int warehouse_id = rs.getInt("id_w");
          int product_id = rs.getInt("id_p");
          row += lotes(row, id, warehouse_id, product_id, componentQtyReq, componentQtyToDel);
        } 
      } 
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DB.close(rs, (Statement)cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
    this.issue.repaint();
  }
  
  private int lotes(int row, IDColumn id, int Warehouse_ID, int M_Product_ID, BigDecimal qtyRequired, BigDecimal qtyToDelivery) {
    CPreparedStatement cPreparedStatement = null;
    int linesNo = 0;
    BigDecimal qtyRequiredActual = qtyRequired;
    String sql = "SELECT s.M_Product_ID , s.QtyOnHand, s.M_AttributeSetInstance_ID, p.Name, masi.Description, l.Value, w.Value, w.M_warehouse_ID,p.Value  FROM M_Storage s  INNER JOIN M_Product p ON (s.M_Product_ID = p.M_Product_ID)  INNER JOIN C_UOM u ON (u.C_UOM_ID = p.C_UOM_ID)  INNER JOIN M_AttributeSetInstance masi ON (masi.M_AttributeSetInstance_ID = s.M_AttributeSetInstance_ID)  INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = ?)  INNER JOIN M_Locator l ON(l.M_Warehouse_ID=w.M_Warehouse_ID and s.M_Locator_ID=l.M_Locator_ID)  WHERE s.M_Product_ID = ? and s.QtyOnHand > 0  and s.M_AttributeSetInstance_ID <> 0  ORDER BY s.Created ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement("SELECT s.M_Product_ID , s.QtyOnHand, s.M_AttributeSetInstance_ID, p.Name, masi.Description, l.Value, w.Value, w.M_warehouse_ID,p.Value  FROM M_Storage s  INNER JOIN M_Product p ON (s.M_Product_ID = p.M_Product_ID)  INNER JOIN C_UOM u ON (u.C_UOM_ID = p.C_UOM_ID)  INNER JOIN M_AttributeSetInstance masi ON (masi.M_AttributeSetInstance_ID = s.M_AttributeSetInstance_ID)  INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = ?)  INNER JOIN M_Locator l ON(l.M_Warehouse_ID=w.M_Warehouse_ID and s.M_Locator_ID=l.M_Locator_ID)  WHERE s.M_Product_ID = ? and s.QtyOnHand > 0  and s.M_AttributeSetInstance_ID <> 0  ORDER BY s.Created ", null);
      cPreparedStatement.setInt(1, Warehouse_ID);
      cPreparedStatement.setInt(2, M_Product_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        this.issue.setRowCount(row + 1);
        BigDecimal qtyOnHand = rs.getBigDecimal(2);
        IDColumn id1 = new IDColumn(rs.getInt(3));
        id1.setSelected(false);
        this.issue.setValueAt(id1, row, 0);
        KeyNamePair productkey = new KeyNamePair(rs.getInt(1), rs.getString(4));
        this.issue.setValueAt(productkey, row, 3);
        this.issue.setValueAt(qtyOnHand, row, 10);
        this.issue.setValueAt(rs.getString(5), row, 5);
        this.issue.setValueAt(rs.getString(6), row, 13);
        KeyNamePair m_warehousekey = new KeyNamePair(rs.getInt(8), rs.getString(7));
        this.issue.setValueAt(m_warehousekey, row, 14);
        if (qtyRequiredActual.compareTo(qtyOnHand) < 0) {
          this.issue.setValueAt((qtyRequiredActual.signum() > 0) ? qtyRequiredActual : Env.ZERO, row, 6);
        } else {
          this.issue.setValueAt(qtyOnHand, row, 6);
        } 
        qtyRequiredActual = qtyRequiredActual.subtract(qtyOnHand);
        if (this.issue.getValueAt(row, 9) == null)
          this.issue.setValueAt(Env.ZERO, row, 9); 
        if (this.issue.getValueAt(row, 8) == null)
          this.issue.setValueAt(Env.ZERO, row, 8); 
        linesNo++;
        row++;
      } 
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DB.close(rs, (Statement)cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
    return linesNo;
  }
  
  public void valueChange(ValueChangeEvent e) {
//    String name = e.getPropertyName();
//    Object value = e.getNewValue();
//    if (value == null)
//      return; 
//    if (name.equals("PP_Order_ID")) {
//      this.orderField.setValue(value);
//      MPPOrder pp_order = getPP_Order();
//      if (pp_order != null) {
//        setS_Resource_ID(pp_order.getS_Resource_ID());
//        setM_Warehouse_ID(pp_order.getM_Warehouse_ID());
//        setDeliveredQty(pp_order.getQtyDelivered());
//        setOrderedQty(pp_order.getQtyOrdered());
//        setQtyBatchs(pp_order.getQtyBatchs());
//        setQtyBatchSize(pp_order.getQtyBatchSize());
//        setOpenQty(pp_order.getQtyOrdered().subtract(pp_order.getQtyDelivered()));
//        setToDeliverQty(getOpenQty());
//        setM_Product_ID(pp_order.getM_Product_ID());
//        MProduct m_product = MProduct.get(Env.getCtx(), pp_order.getM_Product_ID());
//        setC_UOM_ID(m_product.getC_UOM_ID());
//        setOrder_UOM_ID(pp_order.getC_UOM_ID());
//        setM_AttributeSetInstance_ID(pp_order.getMPPOrderBOM().getM_AttributeSetInstance_ID());
//        this.pickcombo.setSelectedIndex(0);
//        Event ev = new Event("onChange", (Component)this.pickcombo);
//        try {
//          onEvent(ev);
//        } catch (Exception e1) {
//          throw new CyprusException(e1);
//        } 
//      } 
//    } 
  }
  
//  private boolean cmd_process() throws InterruptedException {
//    if (isOnlyReceipt() || isBackflush())
//      if (getM_Locator_ID() <= 0)
//        Messagebox.show(Msg.getMsg(Env.getCtx(), "NoLocator"), "Info", 1, "z-msgbox z-msgbox-information");  
//    if (getPP_Order() == null || getMovementDate() == null)
//      return false; 
//    boolean isCloseDocument = (Messagebox.show(Msg.parseTranslation(Env.getCtx(), "@IsCloseDocument@ : " + getPP_Order().getDocumentNo()), "", 3, "z-msgbox z-msgbox-question") == 1);
//    try {
//      Trx.run((TrxRunnable)new Object(this, isCloseDocument));
//    } catch (Exception e) {
//      Messagebox.show(e.getLocalizedMessage(), "", 1, "z-msgbox z-msgbox-error");
//      return false;
//    } finally {
//      this.m_PP_order = null;
//    } 
//    return true;
//  }
  
//  private void createIssue(MPPOrder order) {
//    Timestamp movementDate = getMovementDate();
//    Timestamp minGuaranteeDate = movementDate;
//    boolean isCompleteQtyDeliver = false;
//    ArrayList[][] m_issue = new ArrayList[this.issue.getRowCount()][1];
//    int row = 0;
//    int i;
//    for (i = 0; i < this.issue.getRowCount(); i++) {
//      ArrayList<Object> data = new ArrayList();
//      IDColumn id = (IDColumn)this.issue.getValueAt(i, 0);
//      KeyNamePair key = new KeyNamePair(id.getRecord_ID().intValue(), id.isSelected() ? "Y" : "N");
//      data.add(key);
//      data.add(this.issue.getValueAt(i, 1));
//      data.add(this.issue.getValueAt(i, 2));
//      data.add(this.issue.getValueAt(i, 3));
//      data.add(getValueBigDecimal(i, 8));
//      data.add(getValueBigDecimal(i, 9));
//      m_issue[row][0] = data;
//      row++;
//    } 
//    isCompleteQtyDeliver = MPPOrder.isQtyAvailable(order, m_issue, minGuaranteeDate);
//    if (!isCompleteQtyDeliver) {
//      try {
//        Messagebox.show(Msg.translate(Env.getCtx(), "NoQtyAvailable"), "", 1, "z-msgbox z-msgbox-error");
//      } catch (InterruptedException e) {
//        throw new CyprusException(e);
//      } 
//      throw new CyprusException("@NoQtyAvailable@");
//    } 
//    for (i = 0; i < m_issue.length; i++) {
//      KeyNamePair key = (KeyNamePair) m_issue[i][0].get(0);
//      boolean isSelected = key.getName().equals("Y");
//      if (key != null && isSelected) {
//        String value = (String) m_issue[i][0].get(2);
//        KeyNamePair productkey = (KeyNamePair) m_issue[i][0].get(3);
//        int M_Product_ID = productkey.getKey();
//        MPPOrderBOMLine orderbomLine = null;
//        int PP_Order_BOMLine_ID = 0;
//        int M_AttributeSetInstance_ID = 0;
//        BigDecimal qtyToDeliver = (BigDecimal) m_issue[i][0].get(4);
//        BigDecimal qtyScrapComponent = m_issue[i][0].get(5);
//        MProduct product = MProduct.get(order.getCtx(), M_Product_ID);
//        if (product != null && product.get_ID() != 0 && product.isStocked()) {
//          if (value == null && isSelected) {
//            M_AttributeSetInstance_ID = Integer.valueOf(key.getKey()).intValue();
//            orderbomLine = MPPOrderBOMLine.forM_Product_ID(Env.getCtx(), order.get_ID(), M_Product_ID, order.get_TrxName());
//            if (orderbomLine != null)
//              PP_Order_BOMLine_ID = orderbomLine.get_ID(); 
//          } else if (value != null && isSelected) {
//            PP_Order_BOMLine_ID = Integer.valueOf(key.getKey()).intValue();
//            if (PP_Order_BOMLine_ID > 0) {
//              orderbomLine = new MPPOrderBOMLine(order.getCtx(), PP_Order_BOMLine_ID, order.get_TrxName());
//              M_AttributeSetInstance_ID = orderbomLine.getM_AttributeSetInstance_ID();
//            } 
//          } 
//          MStorage[] storages = MPPOrder.getStorages(Env.getCtx(), 
//              M_Product_ID, 
//              order.getM_Warehouse_ID(), 
//              M_AttributeSetInstance_ID, minGuaranteeDate, order.get_TrxName());
//          MPPOrder.createIssue(order, PP_Order_BOMLine_ID, movementDate, 
//              qtyToDeliver, qtyScrapComponent, 
//              Env.ZERO, storages, false);
//        } 
//      } 
//    } 
//  }
  
  private void generateSummaryTable() {
    StringBuffer iText = new StringBuffer();
    iText.append("<b>");
    iText.append(Msg.translate(Env.getCtx(), "IsShipConfirm"));
    iText.append("</b>");
    iText.append("<br />");
    if (isOnlyReceipt() || isBackflush()) {
      String[][] table = { { Msg.translate(Env.getCtx(), "Name"), 
            Msg.translate(Env.getCtx(), "C_UOM_ID"), 
            Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"), 
            Msg.translate(Env.getCtx(), "QtyToDeliver"), 
            Msg.translate(Env.getCtx(), "QtyDelivered"), 
            Msg.translate(Env.getCtx(), "QtyScrap") }, { this.productField.getDisplay(), 
            this.uomField.getDisplay(), 
            this.attribute.getDisplay(), 
            this.toDeliverQty.getDisplay(), 
            this.deliveredQtyField.getDisplay(), 
            this.scrapQtyField.getDisplay() } };
      iText.append(createHTMLTable(table));
    } 
    if (isBackflush() || isOnlyIssue()) {
      iText.append("<br /><br />");
      ArrayList<String[]> table = (ArrayList)new ArrayList<String>();
      table.add(new String[] { Msg.translate(Env.getCtx(), "Value"), 
            Msg.translate(Env.getCtx(), "Name"), 
            Msg.translate(Env.getCtx(), "C_UOM_ID"), 
            Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"), 
            Msg.translate(Env.getCtx(), "QtyToDeliver"), 
            Msg.translate(Env.getCtx(), "QtyDelivered"), 
            Msg.translate(Env.getCtx(), "QtyScrap") });
      for (int i = 0; i < this.issue.getRowCount(); i++) {
        IDColumn id = (IDColumn)this.issue.getValueAt(i, 0);
        if (id != null && id.isSelected()) {
          KeyNamePair m_productkey = (KeyNamePair)this.issue.getValueAt(i, 3);
          int m_M_Product_ID = m_productkey.getKey();
          KeyNamePair m_uomkey = (KeyNamePair)this.issue.getValueAt(i, 4);
          if (this.issue.getValueAt(i, 5) == null) {
            Timestamp m_movementDate = getMovementDate();
            Timestamp minGuaranteeDate = m_movementDate;
//            MStorage[] storages = MPPOrder.getStorages(Env.getCtx(), 
//                m_M_Product_ID, 
//                getPP_Order().getM_Warehouse_ID(), 
//                0, minGuaranteeDate, null);
            MStorage[] storages = null;
            BigDecimal todelivery = getValueBigDecimal(i, 8);
            BigDecimal scrap = getValueBigDecimal(i, 9);
            BigDecimal toIssue = todelivery.add(scrap);
            byte b;
            int j;
            MStorage[] arrayOfMStorage1;
            for (j = (arrayOfMStorage1 = storages).length, b = 0; b < j; ) {
              MStorage storage = arrayOfMStorage1[b];
              if (storage.getQtyOnHand().signum() != 0) {
                BigDecimal issueact = toIssue;
                if (issueact.compareTo(storage.getQtyOnHand()) > 0)
                  issueact = storage.getQtyOnHand(); 
                toIssue = toIssue.subtract(issueact);
                String desc = (new MAttributeSetInstance(Env.getCtx(), storage.getM_AttributeSetInstance_ID(), null)).getDescription();
                String[] row = { "", "", "", "", "0.00", "0.00", "0.00" };
                row[0] = (this.issue.getValueAt(i, 2) != null) ? this.issue.getValueAt(i, 2).toString() : "";
                row[1] = m_productkey.toString();
                row[2] = (m_uomkey != null) ? m_uomkey.toString() : "";
                row[3] = (desc != null) ? desc : "";
                row[4] = issueact.setScale(2, 4).toString();
                row[5] = getValueBigDecimal(i, 7).setScale(2, 4).toString();
                row[6] = getValueBigDecimal(i, 9).toString();
                table.add(row);
                if (toIssue.signum() <= 0)
                  break; 
              } 
              b++;
            } 
          } else {
            String[] row = { "", "", "", "", "0.00", "0.00", "0.00" };
            row[0] = (this.issue.getValueAt(i, 2) != null) ? this.issue.getValueAt(i, 2).toString() : "";
            row[1] = m_productkey.toString();
            row[2] = (m_uomkey != null) ? m_uomkey.toString() : "";
            row[3] = (this.issue.getValueAt(i, 5) != null) ? this.issue.getValueAt(i, 5).toString() : "";
            row[4] = getValueBigDecimal(i, 8).toString();
            row[5] = getValueBigDecimal(i, 7).toString();
            row[6] = getValueBigDecimal(i, 9).toString();
            table.add(row);
          } 
        } 
      } 
      String[][] tableArray = table.<String[]>toArray(new String[table.size()][]);
      iText.append(createHTMLTable(tableArray));
    } 
    this.info.setContent(iText.toString());
  }
  
  private boolean isOnlyReceipt() {
    return this.pickcombo.getText().equals("OnlyReceipt");
  }
  
  private boolean isOnlyIssue() {
    return this.pickcombo.getText().equals("OnlyIssue");
  }
  
  protected boolean isBackflush() {
    return this.pickcombo.getText().equals("IsBackflush");
  }
  
  protected Timestamp getMovementDate() {
    return (Timestamp)this.movementDateField.getValue();
  }
  
  protected BigDecimal getOrderedQty() {
    BigDecimal bd = (BigDecimal)this.orderedQtyField.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected void setOrderedQty(BigDecimal qty) {
    this.orderedQtyField.setValue(qty);
  }
  
  protected BigDecimal getDeliveredQty() {
    BigDecimal bd = (BigDecimal)this.deliveredQtyField.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected void setDeliveredQty(BigDecimal qty) {
    this.deliveredQtyField.setValue(qty);
  }
  
  protected BigDecimal getToDeliverQty() {
    BigDecimal bd = (BigDecimal)this.toDeliverQty.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected void setToDeliverQty(BigDecimal qty) {
    this.toDeliverQty.setValue(qty);
  }
  
  protected BigDecimal getScrapQty() {
    BigDecimal bd = (BigDecimal)this.scrapQtyField.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected BigDecimal getRejectQty() {
    BigDecimal bd = (BigDecimal)this.rejectQty.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected BigDecimal getOpenQty() {
    BigDecimal bd = (BigDecimal)this.openQtyField.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected void setOpenQty(BigDecimal qty) {
    this.openQtyField.setValue(qty);
  }
  
  protected BigDecimal getQtyBatchs() {
    BigDecimal bd = (BigDecimal)this.qtyBatchsField.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected void setQtyBatchs(BigDecimal qty) {
    this.qtyBatchsField.setValue(qty);
  }
  
  protected BigDecimal getQtyBatchSize() {
    BigDecimal bd = (BigDecimal)this.qtyBatchSizeField.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  protected void setQtyBatchSize(BigDecimal qty) {
    this.qtyBatchSizeField.setValue(qty);
  }
  
  protected int getM_AttributeSetInstance_ID() {
    Integer ii = (Integer)this.attribute.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {
    this.attribute.setValue(Integer.valueOf(M_AttributeSetInstance_ID));
  }
  
  protected int getM_Locator_ID() {
    Integer ii = (Integer)this.locatorField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setM_Locator_ID(int M_Locator_ID) {
    this.locatorField.setValue(Integer.valueOf(M_Locator_ID));
  }
  
  protected int getPP_Order_ID() {
    Integer ii = (Integer)this.orderField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
//  protected MPPOrder getPP_Order() {
//    int id = getPP_Order_ID();
//    if (id <= 0) {
//      this.m_PP_order = null;
//      return null;
//    } 
//    if (this.m_PP_order == null || this.m_PP_order.get_ID() != id)
//      this.m_PP_order = new MPPOrder(Env.getCtx(), id, null); 
//    return this.m_PP_order;
//  }
  
  protected int getS_Resource_ID() {
    Integer ii = (Integer)this.resourceField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setS_Resource_ID(int S_Resource_ID) {
    this.resourceField.setValue(Integer.valueOf(S_Resource_ID));
  }
  
  protected int getM_Warehouse_ID() {
    Integer ii = (Integer)this.warehouseField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setM_Warehouse_ID(int M_Warehouse_ID) {
    this.warehouseField.setValue(Integer.valueOf(M_Warehouse_ID));
  }
  
  protected int getM_Product_ID() {
    Integer ii = (Integer)this.productField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setM_Product_ID(int M_Product_ID) {
    this.productField.setValue(Integer.valueOf(M_Product_ID));
    Env.setContext(Env.getCtx(), this.m_WindowNo, "M_Product_ID", M_Product_ID);
  }
  
  protected int getC_UOM_ID() {
    Integer ii = (Integer)this.uomField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setC_UOM_ID(int C_UOM_ID) {
    this.uomField.setValue(Integer.valueOf(C_UOM_ID));
  }
  
  protected int getOrder_UOM_ID() {
    Integer ii = (Integer)this.uomorderField.getValue();
    return (ii != null) ? ii.intValue() : 0;
  }
  
  protected void setOrder_UOM_ID(int C_UOM_ID) {
    this.uomorderField.setValue(Integer.valueOf(C_UOM_ID));
  }
  
  private String createHTMLTable(String[][] table) {
    StringBuffer html = new StringBuffer("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
    for (int i = 0; i < table.length; i++) {
      if (table[i] != null) {
        html.append("<tr>");
        for (int j = 0; j < (table[i]).length; j++) {
          html.append("<td>");
          if (table[i][j] != null)
            html.append(table[i][j]); 
          html.append("</td>");
        } 
        html.append("</tr>");
      } 
    } 
    html.append("</table>");
    return html.toString();
  }
  
  private BigDecimal getValueBigDecimal(int row, int col) {
    BigDecimal bd = (BigDecimal)this.issue.getValueAt(row, col);
    return (bd == null) ? Env.ZERO : bd;
  }
  
  public void dispose() {
    SessionManager.getAppDesktop().closeActiveWindow();
  }
  
  public ADForm getForm() {
    return (ADForm)this.form;
  }
  
  public void tableChanged(WTableModelEvent event) {}
}

