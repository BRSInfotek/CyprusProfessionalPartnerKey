package org.cyprus.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.cyprus.exceptions.DBException;
import org.cyprus.webui.component.Borderlayout;
import org.cyprus.webui.component.Button;
import org.cyprus.webui.component.Checkbox;
import org.cyprus.webui.component.ConfirmPanel;
import org.cyprus.webui.component.Grid;
import org.cyprus.webui.component.GridFactory;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.Panel;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.component.Rows;
import org.cyprus.webui.component.Tab;
import org.cyprus.webui.component.Tabbox;
import org.cyprus.webui.component.Textbox;
import org.cyprus.webui.component.WListbox;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.editor.WNumberEditor;
import org.cyprus.webui.editor.WPAttributeEditor;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.event.WTableModelEvent;
import org.cyprus.webui.event.WTableModelListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.CustomForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprus.webui.panel.StatusBarPanel;
import org.cyprus.webui.window.WPAttributeInstance;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MQuery;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.minigrid.ColumnInfo;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridFieldVO;
import org.cyprusbrs.model.Lookup;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.model.MRefList;
import org.cyprusbrs.model.MTab;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Language;
import org.cyprusbrs.util.Msg;
//import org.eevolution.form.MRPDetailed;
import org.eevolution.model.MPPProductPlanning;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Space;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.impl.api.LabelElement;

public class WMRPDetailed  implements IFormController, EventListener, ListDataListener, WTableModelListener {
  private CustomForm m_frame = new CustomForm();
  
 public int m_WindowNo;
  
  public int AD_Client_ID;
  
  public int p_WindowNo;
  
  public String p_keyColumn;
  
  public boolean p_multiSelection;
  
  public String p_whereClause;
  
  public int m_keyColumnIndex;
  
//  public boolean m_cancel;
  
  public String m_sqlMain;
  
  public String m_sqlAdd;
  
  public int INFO_WIDTH = 800;
  
  public int AD_Window_ID;
  
  public MQuery query;
  
//  private boolean isBaseLanguage;
  
  public ColumnInfo[] m_layout;
  
  private StatusBarPanel statusBar;
  
  protected WListbox p_table;
  
  private Panel panel;
  
  private Panel southPanel;
  
  private Borderlayout southLayout;
  
  ConfirmPanel confirmPanel;
  
  private Grid parameterPanel;
  
  private Menupopup popup;
  
  private Menuitem calcMenu;
  
  private Label lProduct_ID;
  
  private WSearchEditor fProduct_ID;
  
  private Label lAttrSetInstance_ID;
  
  private WPAttributeEditor fAttrSetInstance_ID = null;
  
//  private Button fAttrSetInstance_ID;
  
  private Label lResource_ID;
  
  private WSearchEditor fResource_ID;
  
  private Label lWarehouse_ID;
  
  private WSearchEditor fWarehouse_ID;
  
  private Label lPlanner_ID;
  
  private WSearchEditor fPlanner_ID;
  
  private Tabbox OrderPlanning;
  
  private Panel PanelBottom;
  
  private Panel PanelCenter;
  
  private Panel PanelFind;
  
  private Tab PanelOrder;
  
  private Tab Results;
  
  private Borderlayout mainLayout;
  
  private Label lDateFrom;
  
  private WDateEditor fDateFrom;
  
  private Label lDateTo;
  
  private WDateEditor fDateTo;
  
  private Label lType;
  
  private Textbox fType;
  
  private Label lUOM;
  
  private Textbox fUOM;
  
  private Label lOrderPeriod;
  
  private WNumberEditor fOrderPeriod;
  
  private Label lTimefence;
  
  private WNumberEditor fTimefence;
  
  private Label lLeadtime;
  
  private WNumberEditor fLeadtime;
  
  private Label lReplenishMin;
  
  private WNumberEditor fReplenishMin;
  
  private Label lMinOrd;
  
  private WNumberEditor fMinOrd;
  
  private Label lMaxOrd;
  
  private WNumberEditor fMaxOrd;
  
  private Label lOrdMult;
  
  private WNumberEditor fOrdMult;
  
  private Label lOrderQty;
  
  private WNumberEditor fOrderQty;
  
  private Label lYield;
  
  private WNumberEditor fYield;
  
  private Label lOnhand;
  
  private WNumberEditor fOnhand;
  
  private Label lSafetyStock;
  
  private WNumberEditor fSafetyStock;
  
  private Label lOrdered;
  
  private WNumberEditor fOrdered;
  
  private Label lReserved;
  
  private WNumberEditor fReserved;
  
  private Label lAvailable;
  
  private WNumberEditor fAvailable;
  
  private Label lSupplyType;
  
  private WSearchEditor fSupplyType;
  
  private Checkbox fMaster;
  
  private Checkbox fMRPReq;
  
  private Checkbox fCreatePlan;
  
  private int ASI_ID;
  
  private boolean isBaseLanguage;
  
  public boolean m_cancel;
  
  private String getTableName() {
	    return "RV_PP_MRP";
	  }
  
  public String getWhereClause(String staticWhere) {
	    StringBuffer where = new StringBuffer("" + getTableName() + ".DocStatus IN ('DR','IP','CO')  AND " + getTableName() + ".IsActive='Y' and " + getTableName() + ".Qty!=0 ");
	    if (!staticWhere.equals(""))
	      where.append(staticWhere); 
	    return where.toString();
	  }
  
  public WMRPDetailed() {
    this.m_layout = null;
    this.m_cancel = false;
	this.m_frame = new CustomForm();
    this.statusBar = new StatusBarPanel();
    this.p_table = new WListbox();
    this.panel = new Panel();
    this.southPanel = new Panel();
    this.southLayout = new Borderlayout();
    this.confirmPanel = new ConfirmPanel(true, true, true, true, true, true, true);
    this.parameterPanel = GridFactory.newGridLayout();
    this.popup = new Menupopup();
    this.calcMenu = new Menuitem(Msg.getMsg(Env.getCtx(), "Calculator"), "/images/Calculator16.png");
    this.lProduct_ID = new Label(Msg.translate(Env.getCtx(), "M_Product_ID"));
    this.lAttrSetInstance_ID = new Label(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
    GridFieldVO vo = GridFieldVO.createStdField(Env.getCtx(), this.m_WindowNo, 0, 0, MTab.getTab_ID(0, "Order"), 
            false, false, false);
        vo.AD_Column_ID = MColumn.getColumn_ID("PP_Order", "M_AttributeSetInstance_ID");
        GridField field = new GridField(vo);
        this.fAttrSetInstance_ID = new WPAttributeEditor(field.getGridTab(), field);
        this.fAttrSetInstance_ID.setValue(Integer.valueOf(0));
//    this.fAttrSetInstance_ID = new Button();
    this.lResource_ID = new Label(Msg.translate(Env.getCtx(), "S_Resource_ID"));
    this.lWarehouse_ID = new Label(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
    this.lPlanner_ID = new Label(Msg.translate(Env.getCtx(), "Planner_ID"));
    this.mainLayout = new Borderlayout();
    this.lDateFrom = new Label(Msg.translate(Env.getCtx(), "DateFrom"));
    this.fDateFrom = new WDateEditor();
    this.lDateTo = new Label(Msg.translate(Env.getCtx(), "DateTo"));
    this.fDateTo = new WDateEditor();
    this.lType = new Label();
    this.fType = new Textbox();
    this.lUOM = new Label();
    this.fUOM = new Textbox();
    this.lOrderPeriod = new Label();
    this.fOrderPeriod = new WNumberEditor();
    this.lTimefence = new Label();
    this.fTimefence = new WNumberEditor();
    this.lLeadtime = new Label();
    this.fLeadtime = new WNumberEditor();
    this.lReplenishMin = new Label();
    this.fReplenishMin = new WNumberEditor();
    this.lMinOrd = new Label();
    this.fMinOrd = new WNumberEditor();
    this.lMaxOrd = new Label();
    this.fMaxOrd = new WNumberEditor();
    this.lOrdMult = new Label();
    this.fOrdMult = new WNumberEditor();
    this.lOrderQty = new Label();
    this.fOrderQty = new WNumberEditor();
    this.lYield = new Label();
    this.fYield = new WNumberEditor();
    this.lOnhand = new Label();
    this.fOnhand = new WNumberEditor();
    this.lSafetyStock = new Label();
    this.fSafetyStock = new WNumberEditor();
    this.lOrdered = new Label();
    this.fOrdered = new WNumberEditor();
    this.lReserved = new Label();
    this.fReserved = new WNumberEditor();
    this.lAvailable = new Label();
    this.fAvailable = new WNumberEditor();
    this.lSupplyType = new Label(Msg.translate(Env.getCtx(), "TypeMRP"));
    this.fSupplyType = null;
    this.fMaster = new Checkbox();
    this.fMRPReq = new Checkbox();
    this.fCreatePlan = new Checkbox();
    this.ASI_ID = 0;
    this.m_keyColumnIndex = -1;
    Env.getLanguage(Env.getCtx());
    this.isBaseLanguage = (Language.getBaseAD_Language().compareTo(Env.getLoginLanguage(Env.getCtx()).getAD_Language()) == 0);
    this.m_WindowNo = 0;
    this.AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
    this.p_multiSelection = true;
    this.p_whereClause = "";
    this.m_keyColumnIndex = -1;
    this.m_cancel = false;
    this.INFO_WIDTH = 800;
    Env.getLanguage(Env.getCtx());
//    try {
//		statInit();
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
    init();
//    this.isBaseLanguage = (Language.getBaseAD_Language().compareTo(Env.getLoginLanguage(Env.getCtx()).getAD_Language()) == 0);
//    this.m_layout = new ColumnInfo[] { 
//        new ColumnInfo(" ", getTableName() + ".PP_MRP_ID", IDColumn.class), new ColumnInfo(Msg.translate(Env.getCtx(), "Value"), "(SELECT Value FROM M_Product p WHERE p.M_Product_ID=" + getTableName() + ".M_Product_ID) AS ProductValue", String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "Name"), "(SELECT Name FROM M_Product p WHERE p.M_Product_ID=" + getTableName() + ".M_Product_ID)", String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "S_Resource_ID"), "(SELECT Name FROM S_Resource sr WHERE sr.S_Resource_ID=" + getTableName() + ".S_Resource_ID)", String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "M_Warehouse_ID"), "(SELECT Name FROM M_Warehouse wh WHERE wh.M_Warehouse_ID=" + getTableName() + ".M_Warehouse_ID)", String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "DatePromised"), "" + getTableName() + ".DatePromised", Timestamp.class), new ColumnInfo(Msg.translate(Env.getCtx(), "QtyGrossReq"), "(CASE WHEN " + getTableName() + ".TypeMRP='D' THEN " + getTableName() + ".Qty ELSE NULL END)", BigDecimal.class), new ColumnInfo(Msg.translate(Env.getCtx(), "QtyScheduledReceipts"), "(CASE WHEN " + getTableName() + ".TypeMRP='S' AND " + getTableName() + ".DocStatus  IN ('IP','CO') THEN " + getTableName() + ".Qty ELSE NULL END)", BigDecimal.class), new ColumnInfo(Msg.translate(Env.getCtx(), "PlannedQty"), "(CASE WHEN " + getTableName() + ".TypeMRP='S' AND " + getTableName() + ".DocStatus ='DR' THEN " + getTableName() + ".Qty ELSE NULL END)", BigDecimal.class), new ColumnInfo(Msg.translate(Env.getCtx(), "QtyOnHandProjected"), "bomQtyOnHand(" + getTableName() + ".M_Product_ID , " + getTableName() + ".M_Warehouse_ID, 0)", BigDecimal.class), 
//        this.isBaseLanguage ? new ColumnInfo(Msg.translate(Env.getCtx(), "TypeMRP"), "(SELECT Name FROM  AD_Ref_List WHERE AD_Reference_ID=53230 AND Value = " + getTableName() + ".TypeMRP)", String.class) : new ColumnInfo(Msg.translate(Env.getCtx(), "TypeMRP"), "(SELECT rlt.Name FROM  AD_Ref_List rl INNER JOIN AD_Ref_List_Trl  rlt ON (rl.AD_Ref_List_ID=rlt.AD_Ref_List_ID)  WHERE rl.AD_Reference_ID=53230 AND rlt.AD_Language = '" + Env.getLoginLanguage(Env.getCtx()).getAD_Language() + "' AND Value = " + getTableName() + ".TypeMRP)", String.class), this.isBaseLanguage ? new ColumnInfo(Msg.translate(Env.getCtx(), "OrderType"), "(SELECT Name FROM  AD_Ref_List WHERE AD_Reference_ID=53229 AND Value = " + getTableName() + ".OrderType)", String.class) : new ColumnInfo(Msg.translate(Env.getCtx(), "OrderType"), "(SELECT rlt.Name FROM  AD_Ref_List rl INNER JOIN AD_Ref_List_Trl  rlt ON (rl.AD_Ref_List_ID=rlt.AD_Ref_List_ID)  WHERE rl.AD_Reference_ID=53229 AND rlt.AD_Language = '" + Env.getLoginLanguage(Env.getCtx()).getAD_Language() + "' AND Value = " + getTableName() + ".OrderType)", String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "DocumentNo"), "documentNo(" + getTableName() + ".PP_MRP_ID)", String.class), this.isBaseLanguage ? new ColumnInfo(Msg.translate(Env.getCtx(), "DocStatus"), "(SELECT Name FROM  AD_Ref_List WHERE AD_Reference_ID=131 AND Value = " + getTableName() + ".DocStatus)", String.class) : new ColumnInfo(Msg.translate(Env.getCtx(), "DocStatus"), "(SELECT rlt.Name FROM  AD_Ref_List rl INNER JOIN AD_Ref_List_Trl  rlt ON (rl.AD_Ref_List_ID=rlt.AD_Ref_List_ID)  WHERE rl.AD_Reference_ID=131 AND rlt.AD_Language = '" + Env.getLoginLanguage(Env.getCtx()).getAD_Language() + "' AND Value = " + getTableName() + ".DocStatus)", String.class), new ColumnInfo(Msg.translate(Env.getCtx(), "DateStartSchedule"), "" + getTableName() + ".DateStartSchedule", Timestamp.class), new ColumnInfo(Msg.translate(Env.getCtx(), "C_BPartner_ID"), "(SELECT cb.Name FROM C_BPartner cb WHERE cb.C_BPartner_ID=" + getTableName() + ".C_BPartner_ID)", String.class) };
//    init();
  }
  
  private void init() {
    try {
    	statInit();
//      fillPicks();
      jbInit();
      
//      dynInit();
    } catch (Exception e) {
//      log.log(Level.SEVERE, "VMRPDetailed.init", e);
    } 
  }
  
  private void statInit() throws Exception {
    Language language = Language.getLoginLanguage();
    MLookup resourceL = MLookupFactory.get(Env.getCtx(), this.p_WindowNo, MColumn.getColumn_ID("S_Resource", "S_Resource_ID"), 19, language, "S_Resource_ID", 0, false, "S_Resource.ManufacturingResourceType= 'PT'");
    this.fResource_ID = new WSearchEditor( "S_Resource_ID", false, false, true, (Lookup)resourceL);
    this.fPlanner_ID = new WSearchEditor( "Planner_ID", false, false, true, (Lookup)MLookupFactory.get(Env.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("PP_Product_Planning", "Planner_ID"), 18));
    this.fWarehouse_ID = new WSearchEditor( "M_Warehouse_ID", false, false, true, (Lookup)MLookupFactory.get(Env.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("M_Warehouse", "M_Warehouse_ID"), 19));
    this.fMaster.setSelected(false);
    this.fMaster.setEnabled(false);
    this.fMRPReq.setSelected(false);
    this.fMRPReq.setEnabled(false);
    this.fCreatePlan.setSelected(false);
    this.fCreatePlan.setEnabled(false);
    this.lUOM.setText(Msg.translate(Env.getCtx(), "C_UOM_ID"));
    this.fUOM.setReadonly(true);
    this.lType.setText(Msg.translate(Env.getCtx(), "Order_Policy"));
    this.fType.setReadonly(true);
    this.lOrderPeriod.setText(Msg.translate(Env.getCtx(), "Order_Period"));
    this.fOrderPeriod.setReadWrite(false);
    this.lTimefence.setText(Msg.translate(Env.getCtx(), "TimeFence"));
    this.fTimefence.setReadWrite(false);
    this.lLeadtime.setText(Msg.translate(Env.getCtx(), "DeliveryTime_Promised"));
    this.fLeadtime.setReadWrite(false);
    this.lMinOrd.setText(Msg.translate(Env.getCtx(), "Order_Min"));
    this.fMinOrd.setReadWrite(false);
    this.lMaxOrd.setText(Msg.translate(Env.getCtx(), "Order_Max"));
    this.fMaxOrd.setReadWrite(false);
    this.lOrdMult.setText(Msg.translate(Env.getCtx(), "Order_Pack"));
    this.fOrdMult.setReadWrite(false);
    this.lOrderQty.setText(Msg.translate(Env.getCtx(), "Order_Qty"));
    this.fOrderQty.setReadWrite(false);
    this.lYield.setText(Msg.translate(Env.getCtx(), "Yield"));
    this.fYield.setReadWrite(false);
    this.lOnhand.setText(Msg.translate(Env.getCtx(), "QtyOnHand"));
    this.fOnhand.setReadWrite(false);
    this.lSafetyStock.setText(Msg.translate(Env.getCtx(), "SafetyStock"));
    this.fSafetyStock.setReadWrite(false);
    this.lReserved.setText(Msg.translate(Env.getCtx(), "QtyReserved"));
    this.fReserved.setReadWrite(false);
    this.lAvailable.setText(Msg.translate(Env.getCtx(), "QtyAvailable"));
    this.fAvailable.setReadWrite(false);
    this.lOrdered.setText(Msg.translate(Env.getCtx(), "QtyOrdered"));
    this.fOrdered.setReadWrite(false);
    this.fProduct_ID = new WSearchEditor("M_Product_ID", true, false, true, (Lookup)MLookupFactory.get(Env.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("M_Product", "M_Product_ID"), 30));
    this.fMaster.setText(Msg.translate(Env.getCtx(), "IsMPS"));
    this.fMRPReq.setText(Msg.translate(Env.getCtx(), "IsRequiredMRP"));
    this.fCreatePlan.setText(Msg.translate(Env.getCtx(), "IsCreatePlan"));
    GridFieldVO vo = GridFieldVO.createStdField(Env.getCtx(), this.m_WindowNo, 0, 0, MTab.getTab_ID(0, "Order"), 
            false, false, false);
        vo.AD_Column_ID = MColumn.getColumn_ID("PP_Order", "M_AttributeSetInstance_ID");
        GridField field = new GridField(vo);
        this.fAttrSetInstance_ID = new WPAttributeEditor(field.getGridTab(), field);
        this.fAttrSetInstance_ID.setValue(Integer.valueOf(0));
//    this.fAttrSetInstance_ID = new Button(this);
//    this.fAttrSetInstance_ID.addActionListener((EventListener)new Object());
    this.fDateFrom.getComponent().setTooltiptext(Msg.translate(Env.getCtx(), "DateFrom"));
    this.fDateTo.getComponent().setTooltiptext(Msg.translate(Env.getCtx(), "DateTo"));
    this.fSupplyType = new WSearchEditor("TypeMRP", false, false, true, (Lookup)MLookupFactory.get(Env.getCtx(), this.p_WindowNo, 0, MColumn.getColumn_ID("PP_MRP", "TypeMRP"), 17));
    Rows rows = null;
    Row row = null;
    rows = new Rows();
    rows.setParent((Component)this.parameterPanel);
    row = rows.newRow();
    row.appendChild(this.lProduct_ID.rightAlign());
    row.appendChild((Component)this.fProduct_ID.getComponent());
    row.appendChild(this.lUOM.rightAlign());
    row.appendChild((Component)this.fUOM);
    row.appendChild(this.lType.rightAlign());
    row.appendChild((Component)this.fType);
    row = rows.newRow();
    row.appendChild(this.lAttrSetInstance_ID.rightAlign());
    row.appendChild((Component)this.fAttrSetInstance_ID.getComponent());
    row.appendChild(this.lOnhand.rightAlign());
    row.appendChild((Component)this.fOnhand.getComponent());
    row.appendChild(this.lOrderPeriod.rightAlign());
    row.appendChild((Component)this.fOrderPeriod.getComponent());
    row = rows.newRow();
    row.appendChild(this.lPlanner_ID.rightAlign());
    row.appendChild((Component)this.fPlanner_ID.getComponent());
    row.appendChild(this.lSafetyStock.rightAlign());
    row.appendChild((Component)this.fSafetyStock.getComponent());
    row.appendChild(this.lMinOrd.rightAlign());
    row.appendChild((Component)this.fMinOrd.getComponent());
    row = rows.newRow();
    row.appendChild(this.lWarehouse_ID.rightAlign());
    row.appendChild((Component)this.fWarehouse_ID.getComponent());
    row.appendChild(this.lReserved.rightAlign());
    row.appendChild((Component)this.fReserved.getComponent());
    row.appendChild(this.lMaxOrd.rightAlign());
    row.appendChild((Component)this.fMaxOrd.getComponent());
    row = rows.newRow();
    row.appendChild(this.lResource_ID.rightAlign());
    row.appendChild((Component)this.fResource_ID.getComponent());
    row.appendChild(this.lAvailable.rightAlign());
    row.appendChild((Component)this.fAvailable.getComponent());
    row.appendChild(this.lOrdMult.rightAlign());
    row.appendChild((Component)this.fOrdMult.getComponent());
    row = rows.newRow();
    row.appendChild(this.lDateFrom.rightAlign());
    row.appendChild((Component)this.fDateFrom.getComponent());
    row.appendChild(this.lOrdered.rightAlign());
    row.appendChild((Component)this.fOrdered.getComponent());
    row.appendChild(this.lOrderQty.rightAlign());
    row.appendChild((Component)this.fOrderQty.getComponent());
    row = rows.newRow();
    row.appendChild(this.lDateTo.rightAlign());
    row.appendChild((Component)this.fDateTo.getComponent());
    row.appendChild((Component)new Space());
    row.appendChild((Component)new Space());
    row.appendChild(this.lTimefence.rightAlign());
    row.appendChild((Component)this.fTimefence.getComponent());
    row = rows.newRow();
    row.appendChild((Component)new Space());
    row.appendChild((Component)this.fMaster);
    row.appendChild((Component)new Space());
    row.appendChild((Component)this.fCreatePlan);
    row.appendChild(this.lLeadtime.rightAlign());
    row.appendChild((Component)this.fLeadtime.getComponent());
    row = rows.newRow();
    row.appendChild((Component)new Space());
    row.appendChild((Component)new Space());
    row.appendChild((Component)new Space());
    row.appendChild((Component)this.fMRPReq);
    row.appendChild(this.lYield.rightAlign());
    row.appendChild((Component)this.fYield.getComponent());
  }
  
  private void selectAttributeSetInstance() {
    int m_warehouse_id = 0;
    int m_product_id = 0;
    if (m_product_id <= 0)
      return; 
    MProduct product = MProduct.get(Env.getCtx(), m_product_id);
    MWarehouse wh = MWarehouse.get(Env.getCtx(), m_warehouse_id);
    String title = product.get_Translation("Name") + " - " + wh.get_Translation("Name");
    WPAttributeInstance pai = new WPAttributeInstance(title, m_warehouse_id, 0, m_product_id, 0);
    if (pai.getM_AttributeSetInstance_ID() != -1) {
      ((LabelElement) this.fAttrSetInstance_ID).setLabel(pai.getM_AttributeSetInstanceName());
      this.ASI_ID = (new Integer(pai.getM_AttributeSetInstance_ID())).intValue();
    } else {
      this.ASI_ID = 0;
    } 
  }
  
  private boolean isAttributeSetInstance() {
    return (getM_AttributeSetInstance_ID() > 0);
  }
  
  private void initComponents() {
    this.OrderPlanning = new Tabbox();
    this.PanelOrder = new Tab();
    this.PanelFind = new Panel();
    this.PanelCenter = new Panel();
    this.PanelBottom = new Panel();
    this.Results = new Tab();
    Borderlayout PanelOrderLayout = new Borderlayout();
    this.PanelOrder.appendChild((Component)PanelOrderLayout);
    North north = new North();
    PanelOrderLayout.appendChild((Component)north);
    north.appendChild((Component)this.PanelFind);
    Center center = new Center();
    PanelOrderLayout.appendChild((Component)center);
    center.appendChild((Component)this.PanelCenter);
    South south = new South();
    PanelOrderLayout.appendChild((Component)south);
    south.appendChild((Component)this.PanelBottom);
    this.OrderPlanning.appendChild((Component)this.PanelOrder);
    this.OrderPlanning.appendChild((Component)this.Results);
    this.PanelOrder.setLabel("Order");
    this.Results.setLabel("Result");
    Center center2 = new Center();
    this.mainLayout.appendChild((Component)center2);
    center2.appendChild((Component)this.OrderPlanning);
    this.m_frame.setWidth("99%");
    this.m_frame.setHeight("100%");
    this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
    this.m_frame.appendChild((Component)this.mainLayout);
    this.mainLayout.setWidth("100%");
    this.mainLayout.setHeight("100%");
    this.mainLayout.setStyle("position: absolute");
  }
  
  protected void jbInit() throws Exception {
    this.m_frame.setWidth("99%");
    this.m_frame.setHeight("100%");
    this.m_frame.setStyle("position: absolute; padding: 0; margin: 0");
    this.m_frame.appendChild((Component)this.mainLayout);
    this.mainLayout.setWidth("100%");
    this.mainLayout.setHeight("100%");
    this.mainLayout.setStyle("position: absolute");
    North north = new North();
    north.appendChild((Component)this.parameterPanel);
    this.mainLayout.appendChild((Component)north);
    Center center = new Center();
    center.appendChild((Component)this.p_table);
    this.mainLayout.appendChild((Component)center);
    this.p_table.setVflex(true);
    this.p_table.setFixedLayout(true);
    center.setFlex(true);
    Div div = new Div();
    div.appendChild((Component)this.confirmPanel);
    div.appendChild((Component)this.statusBar);
    South south = new South();
    south.appendChild((Component)div);
    this.mainLayout.appendChild((Component)south);
    this.confirmPanel.addActionListener(this);
    this.confirmPanel.setVisible("Reset", false);
    this.confirmPanel.setVisible("Customize", false);
    this.confirmPanel.setVisible("History", false);
    this.confirmPanel.setVisible("Zoom", true);
    South southPanel = new South();
    southPanel.appendChild((Component)this.southLayout);
    Button print = this.confirmPanel.createButton("Print");
    print.addActionListener(this);
    this.confirmPanel.addButton(print);
    this.popup.appendChild((Component)this.calcMenu);
    this.calcMenu.addEventListener("onClick", this);
    this.p_table.getModel().addListDataListener(this);
    enableButtons();
  }
  
  private void fillPicks() throws Exception {
    this.m_keyColumnIndex = 0;
    this.m_sqlMain = this.p_table.prepareTable(this.m_layout, getTableName(), getWhereClause(getSQLWhere()), false, "RV_PP_MRP", false);
  }
  
  public ADForm getForm() {
    return (ADForm)this.m_frame;
  }
  
  public void onEvent(Event event) throws Exception {
    String cmd = event.getTarget().getId();
    if (cmd.equals("Ok")) {
      this.m_frame.dispose();
    } else if (cmd.equals("Cancel")) {
      this.m_cancel = true;
      this.m_frame.dispose();
    } else if (cmd.equals("Zoom")) {
      zoom();
    } else if (cmd.equals("Refresh")) {
      executeQuery();
    } 
  }
  
  public void dispose() {
    if (this.m_frame != null)
      this.m_frame.dispose(); 
    this.m_frame = null;
  }
  
  private String getSQLWhere() {
    StringBuffer sql = new StringBuffer();
    if (this.fProduct_ID.getValue() != null) {
      sql.append(" AND " + getTableName() + ".M_Product_ID=?");
      sql.append(" AND ((" + getTableName() + ".OrderType IN ('SOO','MOP','POO','POR','STK','DOO')) OR (" + getTableName() + ".OrderType='FCT' AND " + getTableName() + ".DatePromised >= SYSDATE))");
      fillHead();
      setMRP();
    } 
    if (isAttributeSetInstance()) {
      sql.append(" AND " + getTableName() + ".M_AttributeSetInstance_ID=?");
      fillHead();
      setMRP();
    } 
    if (this.fResource_ID.getValue() != null)
      sql.append(" AND " + getTableName() + ".S_Resource_ID=?"); 
    if (this.fPlanner_ID.getValue() != null)
      sql.append(" AND " + getTableName() + ".Planner_ID=?"); 
    if (this.fWarehouse_ID.getValue() != null)
      sql.append(" AND " + getTableName() + ".M_Warehouse_ID=?"); 
    if (this.fDateFrom.getValue() != null || this.fDateFrom.getValue() != null) {
      Timestamp from = (Timestamp)this.fDateFrom.getValue();
      Timestamp to = (Timestamp)this.fDateTo.getValue();
      if (from == null && to != null) {
        sql.append(" AND TRUNC(" + getTableName() + ".DatePromised) <= ?");
      } else if (from != null && to == null) {
        sql.append(" AND TRUNC(" + getTableName() + ".DatePromised) >= ?");
      } else if (from != null && to != null) {
        sql.append(" AND TRUNC(" + getTableName() + ".DatePromised) BETWEEN ? AND ?");
      } 
    } 
//    log.fine("MRP Info.setWhereClause=" + sql.toString());
    return sql.toString();
  }
  
  private void fillHead() {
    MPPProductPlanning pp = MPPProductPlanning.find(Env.getCtx(), getAD_Org_ID(), getM_Warehouse_ID(), getS_Resource_ID(), getM_Product_ID(), null);
    if (pp == null)
      pp = new MPPProductPlanning(Env.getCtx(), 0, null); 
    this.fMaster.setSelected(pp.isMPS());
    this.fMRPReq.setSelected(pp.isRequiredMRP());
    this.fCreatePlan.setSelected(pp.isCreatePlan());
    this.fOrderPeriod.setValue(pp.getOrder_Period());
    this.fLeadtime.setValue(pp.getDeliveryTime_Promised());
    this.fTimefence.setValue(pp.getTimeFence());
    this.fMinOrd.setValue(pp.getOrder_Min());
    this.fMaxOrd.setValue(pp.getOrder_Max());
    this.fOrdMult.setValue(pp.getOrder_Pack());
    this.fOrderQty.setValue(pp.getOrder_Qty());
    this.fYield.setValue(Integer.valueOf(pp.getYield()));
    this.fType.setText(MRefList.getListName(Env.getCtx(), 53228, pp.getOrder_Policy()));
    this.fSafetyStock.setValue(pp.getSafetyStock());
  }
  
  private void setMRP() {
    CPreparedStatement cPreparedStatement = null;
    int M_Product_ID = getM_Product_ID();
    int M_AttributeSetInstance_ID = getM_AttributeSetInstance_ID();
    int M_Warehouse_ID = getM_Warehouse_ID();
    if (M_Product_ID <= 0)
      return; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      StringBuffer sql = (new StringBuffer("SELECT ")).append("BOMQtyOnHandASI(M_Product_ID,?,?,?) as qtyonhand, ").append("BOMQtyReservedASI(M_Product_ID,?,?,?) as qtyreserved, ").append("BOMQtyAvailableASI(M_Product_ID,?,?,?) as qtyavailable, ").append("BOMQtyOrderedASI(M_Product_ID,?,?,?) as qtyordered").append(" FROM M_Product WHERE M_Product_ID=?");
      cPreparedStatement = DB.prepareStatement(sql.toString(), null);
      DB.setParameters((PreparedStatement)cPreparedStatement, new Object[] { 
            Integer.valueOf(getM_AttributeSetInstance_ID()), Integer.valueOf(getM_Warehouse_ID()), Integer.valueOf(0), Integer.valueOf(getM_AttributeSetInstance_ID()), Integer.valueOf(getM_Warehouse_ID()), Integer.valueOf(0), Integer.valueOf(getM_AttributeSetInstance_ID()), Integer.valueOf(getM_Warehouse_ID()), Integer.valueOf(0), Integer.valueOf(getM_AttributeSetInstance_ID()), 
            Integer.valueOf(getM_Warehouse_ID()), Integer.valueOf(0), Integer.valueOf(getM_Product_ID()) });
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        this.fOnhand.setValue(rs.getBigDecimal(1));
        this.fReserved.setValue(rs.getBigDecimal(2));
        this.fAvailable.setValue(rs.getBigDecimal(3));
        this.fOrdered.setValue(rs.getBigDecimal(4));
      } 
    } catch (SQLException ex) {
      throw new DBException(ex);
    } finally {
      DB.close(rs, (Statement)cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
    int uom_id = MProduct.get(Env.getCtx(), M_Product_ID).getC_UOM_ID();
    MUOM um = MUOM.get(Env.getCtx(), uom_id);
    KeyNamePair kum = new KeyNamePair(um.getC_UOM_ID(), um.get_Translation("Name"));
    this.fUOM.setText(kum.toString());
    BigDecimal replenishLevelMin = Env.ZERO;
    if (getM_Warehouse_ID() > 0) {
      String sql = "SELECT Level_Min FROM M_Replenish WHERE AD_Client_ID=? AND M_Product_ID=? AND M_Warehouse_ID=?";
      replenishLevelMin = DB.getSQLValueBD(null, sql, new Object[] { Integer.valueOf(Env.getAD_Client_ID(Env.getCtx())), Integer.valueOf(M_Product_ID), Integer.valueOf(M_Warehouse_ID) });
    } 
    this.fReplenishMin.setValue(replenishLevelMin);
  }
  
  public void zoom() {
//    super.zoom();
//    AEnv.zoom(this.AD_Window_ID, this.query);
  }
  
  void enableButtons() {
    boolean enable = true;
    this.confirmPanel.getOKButton().setEnabled(true);
//    if (hasHistory())
      this.confirmPanel.getButton("History").setEnabled(enable); 
//    if (hasZoom())
      this.confirmPanel.getButton("Zoom").setEnabled(enable); 
  }
  
  void executeQuery() {
    work();
  }
  
  protected void setParameters(PreparedStatement pstmt, boolean forCount) throws SQLException {
    int index = 1;
    if (getM_Product_ID() > 0) {
      int product_id = getM_Product_ID();
      pstmt.setInt(index++, product_id);
//      log.fine("Product=" + product_id);
    } 
    if (isAttributeSetInstance()) {
      int asi = getM_AttributeSetInstance_ID();
      pstmt.setInt(index++, asi);
//      log.fine("AttributeSetInstance=" + asi);
    } 
    if (getS_Resource_ID() > 0) {
      int resource_id = getS_Resource_ID();
      pstmt.setInt(index++, resource_id);
//      log.fine("Resource=" + resource_id);
    } 
    if (getM_Warehouse_ID() > 0) {
      int warehouse_id = getM_Warehouse_ID();
      pstmt.setInt(index++, getM_Warehouse_ID());
//      log.fine("Warehouse=" + warehouse_id);
    } 
    if (getPlanner_ID() > 0) {
      int planner_id = getPlanner_ID();
      pstmt.setInt(index++, planner_id);
//      log.fine("Planner=" + planner_id);
    } 
    if (getDueStart() != null || getDueEnd() != null) {
      Timestamp from = getDueStart();
      Timestamp to = getDueEnd();
//      log.fine("Date From=" + from + ", Date To=" + to);
      if (from == null && to != null) {
        pstmt.setTimestamp(index++, to);
      } else if (from != null && to == null) {
        pstmt.setTimestamp(index++, from);
      } else if (from != null && to != null) {
        pstmt.setTimestamp(index++, from);
        pstmt.setTimestamp(index++, to);
      } 
    } 
  }
  
  protected int getM_Product_ID() {
    Object o = this.fProduct_ID.getValue();
    return ((o != null && o instanceof Integer) ? (Integer)o : Integer.valueOf(0)).intValue();
  }
  
  protected int getM_AttributeSetInstance_ID() {
    return this.ASI_ID;
  }
  
  protected int getAD_Org_ID() {
    int warehouse_id = getM_Warehouse_ID();
    if (warehouse_id <= 0)
      return 0; 
    return MWarehouse.get(Env.getCtx(), warehouse_id).getAD_Org_ID();
  }
  
  protected int getM_Warehouse_ID() {
    Object o = this.fWarehouse_ID.getValue();
    return ((o != null && o instanceof Integer) ? (Integer)o : Integer.valueOf(0)).intValue();
  }
  
  protected int getS_Resource_ID() {
    Object o = this.fResource_ID.getValue();
    return ((o != null && o instanceof Integer) ? (Integer)o : Integer.valueOf(0)).intValue();
  }
  
  protected int getPlanner_ID() {
    Object o = this.fPlanner_ID.getValue();
    return ((o != null && o instanceof Integer) ? (Integer)o : Integer.valueOf(0)).intValue();
  }
  
  protected Timestamp getDueStart() {
    return (Timestamp)this.fDateFrom.getValue();
  }
  
  protected Timestamp getDueEnd() {
    return (Timestamp)this.fDateTo.getValue();
  }
  
  protected BigDecimal getQtyOnHand() {
    BigDecimal bd = (BigDecimal)this.fOnhand.getValue();
    return (bd != null) ? bd : Env.ZERO;
  }
  
  public void onChange(ListDataEvent event) {}
  
  public void tableChanged(WTableModelEvent event) {}
  
  public Integer getSelectedRowKey() {
    int row = this.p_table.getSelectedRow();
    if (row != -1 && this.m_keyColumnIndex != -1) {
      Object data = this.p_table.getModel().getValueAt(row, this.m_keyColumnIndex);
      if (data instanceof IDColumn)
        data = ((IDColumn)data).getRecord_ID(); 
      if (data instanceof Integer)
        return (Integer)data; 
    } 
    return null;
  }
  
  public void zoom(int AD_Window_ID, MQuery zoomQuery) {}
  
  public void work() {
//    log.fine("Info.Worker.run");
    StringBuilder sql = new StringBuilder(null);
    String dynWhere = getSQLWhere();
    if (dynWhere.length() > 0) {
      System.out.println("where" + dynWhere);
      sql.append(dynWhere);
    } 
    StringBuilder sqlFinal = new StringBuilder(MRole.getDefault().addAccessSQL(Msg.parseTranslation(Env.getCtx(), sql.toString()), null, true, false));
    sqlFinal.append(" ORDER BY DatePromised,ProductValue");
    CPreparedStatement cPreparedStatement = null;
	ResultSet rs = null;
    try {
       cPreparedStatement = DB.prepareStatement(sqlFinal.toString(), null);
//      log.fine("SQL=" + sqlFinal.toString());
      setParameters((PreparedStatement)cPreparedStatement, false);
       rs = cPreparedStatement.executeQuery();
      this.p_table.loadTable(rs);
      rs.close();
      cPreparedStatement.close();
    } catch (SQLException e) {
//      log.log(Level.SEVERE, "Info.Worker.run - " + sqlFinal.toString(), e);
    } 
    finally
	{
		DB.close(rs, cPreparedStatement);
		rs = null; cPreparedStatement = null;
	}
	
    if (getM_Product_ID() > 0) {
      BigDecimal OnHand = getQtyOnHand();
      for (int row = 0; row < this.p_table.getRowCount(); row++) {
        Timestamp datepromised = (Timestamp)this.p_table.getValueAt(row, 5);
        Timestamp today = new Timestamp(System.currentTimeMillis());
        IDColumn id = (IDColumn)this.p_table.getValueAt(row, 0);
        String TypeMRP = DB.getSQLValueString(null, "SELECT TypeMRP FROM " + getTableName() + " WHERE PP_MRP_ID=?", id.getRecord_ID().intValue());
        String OrderType = (String)this.p_table.getValueAt(row, 11);
        if ("D".equals(TypeMRP) || ("FCT".equals(OrderType) && datepromised.after(today))) {
          BigDecimal QtyGrossReqs = (BigDecimal)this.p_table.getValueAt(row, 6);
          OnHand = OnHand.subtract(QtyGrossReqs);
          this.p_table.setValueAt(OnHand, row, 9);
        } 
        if ("S".equals(TypeMRP)) {
          BigDecimal QtyScheduledReceipts = (BigDecimal)this.p_table.getValueAt(row, 7);
          BigDecimal QtyPlan = (BigDecimal)this.p_table.getValueAt(row, 8);
          if (QtyPlan == null)
            QtyPlan = Env.ZERO; 
          if (QtyScheduledReceipts == null)
            QtyScheduledReceipts = Env.ZERO; 
          OnHand = OnHand.add(QtyScheduledReceipts.add(QtyPlan));
          this.p_table.setValueAt(OnHand, row, 9);
        } 
      } 
    } 
  }
}
