package org.cyprus.webui.apps.form;


import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.cyprus.crm.model.MLead;
import org.cyprus.crm.model.MLeadInfo;
import org.cyprus.crm.model.MOpportunityLine;
import org.cyprus.crm.model.MSalesOpportunity;
import org.cyprus.exceptions.DBException;
import org.cyprus.util.UtilTax;
import org.cyprus.webui.component.Button;
import org.cyprus.webui.component.Grid;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.component.Rows;
import org.cyprus.webui.editor.WEditor;
import org.cyprus.webui.editor.WNumberEditor;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.editor.WTableDirEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.panel.CustomForm;
import org.cyprus.webui.window.FDialog;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MPriceList;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductPrice;
import org.cyprusbrs.framework.MUOMConversion;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;

public class WProductLineForm extends CustomForm implements EventListener, ValueChangeListener {

    private static final long serialVersionUID = 1L;
    
    private boolean created = false;
    
    private Vbox mainLayout = new Vbox();
    private Grid lineGrid = new Grid();
    
    private WTableDirEditor priceListEditor;
    private WSearchEditor productEditor;
    private WTableDirEditor chargeEditor;
    private WNumberEditor qtyEditor;
    private WNumberEditor priceEditor;
    
    private List<LineRow> lineRows = new ArrayList<>();
    
    private Button saveButton = new Button("Save");
    private Button cancelButton = new Button("Cancel");
    private Button addLineButton = new Button(Msg.getMsg(Env.getCtx(), "AddLine"));
    
    // Header references
    private int headerId = 0; // Will store either C_Lead_ID or C_SalesOpportunity_ID
    private String headerType = ""; // "Lead" or "Opportunity"
    
    private static final String LINE_SECTION_HEIGHT = "300px";
    private static final String BUTTON_SECTION_HEIGHT = "40px";
    private static final String FORM_HEIGHT = "460px";
    
    
    public WProductLineForm(ArrayList<KeyNamePair> keyNamePairVal, String type) {
        super();
        try {
            init(keyNamePairVal, type);
        } catch (Exception e) {
            FDialog.error(0, "ErrorInitializingForm", e.getLocalizedMessage());
        }
    }
  
    protected void init(ArrayList<KeyNamePair> keyNamePairVal, String type) {
        this.setWidth("100%");
        this.setHeight(FORM_HEIGHT);
        this.setStyle("padding: 5px; display: flex; flex-direction: column;");
        this.setClosable(true);
        this.setSizable(true);
        
        // Set header ID and type from parameters
        if (keyNamePairVal != null && !keyNamePairVal.isEmpty()) {
            KeyNamePair knp = keyNamePairVal.get(0);
            this.headerType = type;
            this.headerId = knp.getKey();
        }
        
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setStyle("display: flex; flex-direction: column; gap: 5px;");
        this.appendChild(mainLayout);
                
        mainLayout.appendChild(createLineSection());
        mainLayout.appendChild(createButtonPanel());
         
        // Load existing lines if we have a header ID
        if (headerId > 0) {
            loadExistingLines();
        } else {
            addLineRow(); // Add empty row if new record
        }
    }
    
    private void loadExistingLines() {
        if ("Lead".equalsIgnoreCase(headerType)) {
            // Load lead lines
            String sql = "SELECT * FROM C_LeadInfo WHERE C_Lead_ID = ? ORDER BY Line";
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = DB.prepareStatement(sql, null);
                pstmt.setInt(1, headerId);
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    LineRow row = addLineRow();
                    row.setLineId(rs.getInt("C_LeadInfo_ID"));
                    row.setLine(rs.getInt("Line"));
                    
                    // Set product if exists
                    int productId = rs.getInt("M_Product_ID");
                    if (productId > 0) {
                        row.setProductId(productId);
                        row.getProductEditor().setValue(productId);
                    }
                    
                    // Set charge if exists
                    int chargeId = rs.getInt("C_Charge_ID");
                    if (chargeId > 0) {
                        row.setChargeId(chargeId);
                        row.getChargeEditor().setValue(chargeId);
                    }
                    
                    // Set quantity 
                    BigDecimal qty = rs.getBigDecimal("PlannedQty");
                    if (qty != null) {
                        row.setQuantity(qty);
                        row.getQtyEditor().setValue(qty);
                    }
                    
                    // Set price
                    BigDecimal price = rs.getBigDecimal("PlannedPrice");
                    if (price != null) {
                        row.setPrice(price);
                        row.getPriceEditor().setValue(price);
                    }
                    
                    
                }
            } catch (SQLException e) {
                FDialog.error(0, "ErrorLoadingLines", e.getLocalizedMessage());
            } finally {
                DB.close(rs, pstmt);
            }
        } 
        else if ("Opportunity".equalsIgnoreCase(headerType)) {
            // Load opportunity lines
            String sql = "SELECT * FROM C_OpportunityLine WHERE C_SalesOpportunity_ID = ? ORDER BY Line";
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = DB.prepareStatement(sql, null);
                pstmt.setInt(1, headerId);
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                	LineRow row = addLineRow();
                    row.setLineId(rs.getInt("C_SalesOpportunityLine_ID"));
                    row.setLine(rs.getInt("Line"));
                    
                    // Set product if exists
                    int productId = rs.getInt("M_Product_ID");
                    if (productId > 0) {
                        row.setProductId(productId);
                        row.getProductEditor().setValue(productId);
                    }
                    
                    // Set charge if exists
                    int chargeId = rs.getInt("C_Charge_ID");
                    if (chargeId > 0) {
                        row.setChargeId(chargeId);
                        row.getChargeEditor().setValue(chargeId);
                    }
                    
                    // Set quantity and price
                    BigDecimal qty = rs.getBigDecimal("Qty");
                    if (qty != null) {
                        row.setQuantity(qty);
                        row.getQtyEditor().setValue(qty);
                    }
                    
                    BigDecimal price = rs.getBigDecimal("Price");
                    if (price != null) {
                        row.setPrice(price);
                        row.getPriceEditor().setValue(price);
                    }
                    
                }
            } catch (SQLException e) {
                FDialog.error(0, "ErrorLoadingLines", e.getLocalizedMessage());
            } finally {
                DB.close(rs, pstmt);
            }
        }
        
        // If no lines found, add empty row
        if (lineRows.isEmpty()) {
            addLineRow();
        }
    }
    
    private Div createLineSection() {
        Div lineDiv = new Div();
        lineDiv.setStyle("padding: 5px; border: 1px solid #ccc; height: " + LINE_SECTION_HEIGHT + 
                       "; display: flex; flex-direction: column;");
        
        addLineButton.addEventListener(Events.ON_CLICK, this);
        lineDiv.appendChild(addLineButton);
        
        lineGrid.setWidth("100%");
        lineGrid.setStyle("flex: 1; height:" + LINE_SECTION_HEIGHT + "; overflow-y: auto;");
        
        Rows rows = new Rows();
        lineGrid.appendChild(rows);
        Row header = new Row();
        header.appendChild(new Label(Msg.translate(Env.getCtx(), "M_Product_ID")));
        header.appendChild(new Label(Msg.translate(Env.getCtx(), "C_Charge_ID")));
        header.appendChild(new Label(Msg.translate(Env.getCtx(), "Qty")));
        header.appendChild(new Label(Msg.translate(Env.getCtx(), "Price")));
        header.appendChild(new Label("")); 
        rows.appendChild(header);
        
        lineDiv.appendChild(lineGrid);
        return lineDiv;
    }
    
    private Hbox createButtonPanel() {
        Hbox buttonBox = new Hbox();
        buttonBox.setStyle("gap: 10px; justify-content: flex-end; height: " + BUTTON_SECTION_HEIGHT + 
                          "; align-items: center; padding: 5px;");
        
        saveButton.setStyle("min-width: 80px;");        
        cancelButton.setStyle("min-width: 80px;");
        
        saveButton.addEventListener(Events.ON_CLICK, this);
        cancelButton.addEventListener(Events.ON_CLICK, this);
        
        buttonBox.appendChild(saveButton);
        buttonBox.appendChild(cancelButton);
        
        return buttonBox;
    }
    
//    private void refreshLineGrid() {
//        Rows rows = (Rows) lineGrid.getFirstChild();
//        while (rows.getChildren().size() > 1) {
//            rows.removeChild(rows.getLastChild());
//        }
//        
//        for (LineRow row : lineRows) {
//            Row gridRow = new Row();
//            
//            MLookup productL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
//                    MColumn.getColumn_ID(MLeadInfo.Table_Name, MLeadInfo.COLUMNNAME_M_Product_ID), 
//                    DisplayType.Search);
//                productEditor = new WSearchEditor("M_Product_ID", false, false, true, productL);
//                productEditor.setValue(row.getProductId() > 0 ? row.getProductId() : null);
//                setupDynamicTooltip(productEditor);
//                row.setProductEditor(productEditor);
//                productEditor.addValueChangeListener(e -> {
//                    Integer newValue = (Integer)e.getNewValue();
//                    row.setProductId(newValue != null ? newValue : 0);
//                    
//                    if (newValue != null && newValue > 0) {
//                        row.getChargeEditor().setReadWrite(false);
//                        row.getChargeEditor().setValue(null);
//                        row.setChargeId(0);
//                        
//                        // Try to get price for the product
//                        MProduct product = MProduct.get(Env.getCtx(), newValue);
//                        if (product != null && priceListEditor != null && priceListEditor.getValue() != null) {
//                            int M_PriceList_Version_ID = DB.getSQLValue(null, 
//                                "SELECT MAX(M_PriceList_Version_ID) FROM M_PriceList_Version " +
//                                "WHERE IsActive = 'Y' AND M_PriceList_ID =" + priceListEditor.getValue());
//                            
//                            BigDecimal priceStd = DB.getSQLValueBD(null, 
//                                "SELECT PriceStd FROM M_ProductPrice " +
//                                "WHERE M_Product_ID = ? AND M_PriceList_Version_ID = ?", 
//                                newValue, M_PriceList_Version_ID);
//                            
//                            if (priceStd != null) {
//                                row.setPrice(priceStd);
//                                row.getPriceEditor().setValue(priceStd);
//                            }
//                        }
//                    } else {
//                        row.getChargeEditor().setReadWrite(true);
//                    }
//                });
//                gridRow.appendChild(productEditor.getComponent());
//                
//                MLookup chargeL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
//                    MColumn.getColumn_ID(MLeadInfo.Table_Name, MLeadInfo.COLUMNNAME_C_Charge_ID), 
//                    DisplayType.TableDir);
//                chargeEditor = new WTableDirEditor("C_Charge_ID", false, false, true, chargeL);
//                chargeEditor.setValue(row.getChargeId() > 0 ? row.getChargeId() : null);
//                row.setChargeEditor(chargeEditor);
//                setupDynamicTooltip(chargeEditor);
//                chargeEditor.addValueChangeListener(e -> {
//                    Integer newValue = (Integer)e.getNewValue();
//                    row.setChargeId(newValue != null ? newValue : 0);
//                    
//                    if (newValue != null && newValue > 0) {
//                        row.getProductEditor().setReadWrite(false);
//                        row.getProductEditor().setValue(null);
//                        row.setProductId(0);
//                    } else {
//                        row.getProductEditor().setReadWrite(true);
//                    }
//                });
//                gridRow.appendChild(chargeEditor.getComponent());
//                
//                qtyEditor = new WNumberEditor("PlannedQty", false, false, true, 29, "PlannedQty");
//                qtyEditor.setValue(row.getQuantity());
//                row.setQtyEditor(qtyEditor);
//                setupDynamicTooltip(qtyEditor);
//                qtyEditor.addValueChangeListener(e -> {
//                    row.setQuantity(e.getNewValue() != null ? (BigDecimal)e.getNewValue() : BigDecimal.ZERO);
//                });
//                gridRow.appendChild(qtyEditor.getComponent());
//                
//                priceEditor = new WNumberEditor("PlannedPrice", false, false, true, 29, "PlannedPrice");
//                priceEditor.setValue(row.getPrice());
//                row.setPriceEditor(priceEditor);
//                setupDynamicTooltip(priceEditor);
//                priceEditor.addValueChangeListener(e -> {
//                    row.setPrice(e.getNewValue() != null ? (BigDecimal)e.getNewValue() : BigDecimal.ZERO);
//                });
//                gridRow.appendChild(priceEditor.getComponent());
//                
//                Button deleteBtn = new Button("Delete");
//                deleteBtn.addEventListener(Events.ON_CLICK, e -> {
//                	lineRows.remove(row);
//                    refreshLineGrid();
//                });
//                if(row.getLineId() > 0)
//                {
//                	deleteBtn.setDisabled(true);
//                }
//                gridRow.appendChild(deleteBtn);
//                
//                if (row.getProductId() > 0) {
//                    row.getChargeEditor().setReadWrite(false);
//                } else if (row.getChargeId() > 0) {
//                    row.getProductEditor().setReadWrite(false);
//                }
//            
//            rows.appendChild(gridRow);
//        }
//    }
    
    private void refreshLineGrid() {
        Rows rows = (Rows) lineGrid.getFirstChild();
        while (rows.getChildren().size() > 1) {
            rows.removeChild(rows.getLastChild());
        }
        
        for (LineRow row : lineRows) {
            Row gridRow = new Row();
            
            MLookup productL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
                    MColumn.getColumn_ID(MLeadInfo.Table_Name, MLeadInfo.COLUMNNAME_M_Product_ID), 
                    DisplayType.Search);
                productEditor = new WSearchEditor("M_Product_ID", false, false, true, productL);
                productEditor.setValue(row.getProductId() > 0 ? row.getProductId() : null);
                setupDynamicTooltip(productEditor);
                row.setProductEditor(productEditor);
                productEditor.addValueChangeListener(e -> {
                    Integer newValue = (Integer)e.getNewValue();
                    row.setProductId(newValue != null ? newValue : 0);
                    
                    if (newValue != null && newValue > 0) {
                        row.getChargeEditor().setReadWrite(false);
                        row.getChargeEditor().setValue(null);
                        row.setChargeId(0);
                        
                        // Try to get price for the product
                        MProduct product = MProduct.get(Env.getCtx(), newValue);
                        if (product != null && priceListEditor != null && priceListEditor.getValue() != null) {
                            int M_PriceList_Version_ID = DB.getSQLValue(null, 
                                "SELECT MAX(M_PriceList_Version_ID) FROM M_PriceList_Version " +
                                "WHERE IsActive = 'Y' AND M_PriceList_ID =" + priceListEditor.getValue());
                            
                            BigDecimal priceStd = DB.getSQLValueBD(null, 
                                "SELECT PriceStd FROM M_ProductPrice " +
                                "WHERE M_Product_ID = ? AND M_PriceList_Version_ID = ?", 
                                newValue, M_PriceList_Version_ID);
                            
                            if (priceStd != null) {
                                row.setPrice(priceStd);
                                row.getPriceEditor().setValue(priceStd);
                            }
                        }
                    } else {
                        row.getChargeEditor().setReadWrite(true);
                    }
                });
                gridRow.appendChild(productEditor.getComponent());
                
                MLookup chargeL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
                    MColumn.getColumn_ID(MLeadInfo.Table_Name, MLeadInfo.COLUMNNAME_C_Charge_ID), 
                    DisplayType.TableDir);
                chargeEditor = new WTableDirEditor("C_Charge_ID", false, false, true, chargeL);
                chargeEditor.setValue(row.getChargeId() > 0 ? row.getChargeId() : null);
                row.setChargeEditor(chargeEditor);
                setupDynamicTooltip(chargeEditor);
                chargeEditor.addValueChangeListener(e -> {
                    Integer newValue = (Integer)e.getNewValue();
                    row.setChargeId(newValue != null ? newValue : 0);
                    
                    if (newValue != null && newValue > 0) {
                        row.getProductEditor().setReadWrite(false);
                        row.getProductEditor().setValue(null);
                        row.setProductId(0);
                    } else {
                        row.getProductEditor().setReadWrite(true);
                    }
                });
                gridRow.appendChild(chargeEditor.getComponent());
                
                qtyEditor = new WNumberEditor("PlannedQty", false, false, true, 29, "PlannedQty");
                qtyEditor.setValue(row.getQuantity());
                row.setQtyEditor(qtyEditor);
                setupDynamicTooltip(qtyEditor);
                qtyEditor.addValueChangeListener(e -> {
                    row.setQuantity(e.getNewValue() != null ? (BigDecimal)e.getNewValue() : BigDecimal.ZERO);
                });
                gridRow.appendChild(qtyEditor.getComponent());
                
                priceEditor = new WNumberEditor("PlannedPrice", false, false, true, 29, "PlannedPrice");
                priceEditor.setValue(row.getPrice());
                row.setPriceEditor(priceEditor);
                setupDynamicTooltip(priceEditor);
                priceEditor.addValueChangeListener(e -> {
                    row.setPrice(e.getNewValue() != null ? (BigDecimal)e.getNewValue() : BigDecimal.ZERO);
                });
                gridRow.appendChild(priceEditor.getComponent());
                
                Button deleteBtn = new Button("Delete");
                deleteBtn.addEventListener(Events.ON_CLICK, e -> {
                        lineRows.remove(row);
                        refreshLineGrid();
                });
                              
                gridRow.appendChild(deleteBtn);
                
                if (row.getProductId() > 0) {
                    row.getChargeEditor().setReadWrite(false);
                } else if (row.getChargeId() > 0) {
                    row.getProductEditor().setReadWrite(false);
                }
            
            rows.appendChild(gridRow);
        }
    }
    
    private void setupDynamicTooltip(WEditor editor) {
        updateEditorTooltip(editor);
        editor.addValueChangeListener(e -> updateEditorTooltip(editor));
    }
    
    private void updateEditorTooltip(WEditor editor) {
        Object value = editor.getValue();
        String tooltip = value != null ? value.toString() : "";
        
        if (editor.getComponent() instanceof org.zkoss.zul.Combobox) {
            ((org.zkoss.zul.Combobox)editor.getComponent()).setTooltiptext(tooltip);
        } else {
            ((HtmlBasedComponent) editor.getComponent()).setTooltiptext(tooltip);
        }
    }
    
    private void clearForm() {
        lineRows.clear();
        refreshLineGrid();
    }
    
    private LineRow addLineRow() {
        LineRow row = new LineRow(lineRows.size() + 10);
        lineRows.add(row);
        refreshLineGrid();
        return row;
    }
    
    private void saveLines() {
        String trxName = Trx.createTrxName("LineSave");
        Trx trx = Trx.get(trxName, true); 
       
        try {
            if ("Lead".equalsIgnoreCase(headerType)) {
            	 // Save lead info items
                for (LineRow row : lineRows) {
                    if (row.getProductEditor().getValue() == null && row.getChargeEditor().getValue() == null) {
                        FDialog.error(0, "Please select either Product or Charge");
                        trx.rollback();
                        return;
                    }
                    
                    MLead lead = new MLead(Env.getCtx(), headerId, trxName);
                    MLeadInfo leadInfo = null;
                    int lineNo = 0;
                    
                    // Check if this is an existing line
                    if (row.getLineId() > 0) {
                        leadInfo = new MLeadInfo(Env.getCtx(), row.getLineId(), trxName);
                        lineNo = leadInfo.getLine();
                    } else {
                        leadInfo = new MLeadInfo(Env.getCtx(), 0, trxName);
                        leadInfo.setLine(lineNo + 10);
                        lineNo = lineNo + 10;
                    }
                    
                    leadInfo.setAD_Client_ID(lead.getAD_Client_ID());
                    leadInfo.setAD_Org_ID(lead.getAD_Org_ID());
                    leadInfo.setC_Lead_ID(lead.getC_Lead_ID());                    
                    
                    int C_UOM_ID = DB.getSQLValue(trxName, 
                        "SELECT C_UOM_ID FROM C_UOM WHERE AD_Client_ID =" + Env.getAD_Client_ID(Env.getCtx()) + 
                        " AND IsDefault='Y' AND IsActive='Y'");
                  
                    int C_Tax_ID = DB.getSQLValue(trxName, 
                        "SELECT C_Tax_ID FROM C_Tax WHERE AD_Client_ID =" + Env.getAD_Client_ID(Env.getCtx()) + 
                        " AND IsDefault='Y' AND IsActive='Y'");
                    
                    BigDecimal qty = row.getQtyEditor().getValue() != null ? 
                        (BigDecimal)row.getQtyEditor().getValue() : BigDecimal.ZERO;
              
                    if (row.getProductEditor().getValue() != null) {
                        Integer productId = (Integer)row.getProductEditor().getValue();
                        leadInfo.setM_Product_ID(productId != null ? productId : 0);
                        
                        MProduct product = new MProduct(Env.getCtx(), productId, trxName);    
                        
//                        int taxID = product.getC_Tax_ID() > 0 ? product.getC_Tax_ID() : C_Tax_ID;
                        
                        leadInfo.setC_UOM_ID(product.getC_UOM_ID() > 0 ? product.getC_UOM_ID() : C_UOM_ID);
                                                                    
                        int M_PriceList_Version_ID = DB.getSQLValue(trxName, 
                            "SELECT MAX(M_PriceList_Version_ID) FROM M_PriceList_Version " +
                            "WHERE IsActive = 'Y' AND M_PriceList_ID =" + lead.getM_PriceList_ID());
                        
                        BigDecimal PriceStd = Env.ZERO;
                        BigDecimal PriceList = Env.ZERO; 
                        
                        String sqlNew = "SELECT PriceStd, PriceList FROM M_ProductPrice " +
                                      "WHERE AD_Client_ID = ? AND M_PriceList_Version_ID = ? AND M_product_id = ?";
                        PreparedStatement pstmtNew = null;
                        ResultSet rsNew = null;
                        try {
                            pstmtNew = DB.prepareStatement(sqlNew, trxName);
                            pstmtNew.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
                            pstmtNew.setInt(2, M_PriceList_Version_ID);
                            pstmtNew.setInt(3, productId);
                            rsNew = pstmtNew.executeQuery();
                            if (rsNew.next()) {
                                PriceStd = rsNew.getBigDecimal(1);
                                PriceList = rsNew.getBigDecimal(2);                        
                            }
                        } catch (SQLException e) {
                            throw new DBException(e, sqlNew);
                        } finally {
                            DB.close(rsNew, pstmtNew);
                        }
                        
                        BigDecimal BaseQty = MUOMConversion.convertProductFrom(Env.getCtx(), productId, 
                                product.getC_UOM_ID() > 0 ? product.getC_UOM_ID() : C_UOM_ID, qty);
                        
                        leadInfo.setBaseQty(BaseQty);
                        leadInfo.setBasePrice(PriceStd);
                        leadInfo.setPriceList(PriceList);
                        
                        int StdPrecision = MPriceList.getStandardPrecision(Env.getCtx(), lead.getM_PriceList_ID());
                       
                        BigDecimal PricePlanned = Env.ZERO;
                         
                        
                        if(((BigDecimal) priceEditor.getValue()).compareTo(BigDecimal.ZERO) != 0)
                        {
                        	PricePlanned = (BigDecimal)priceEditor.getValue();
                        	PricePlanned = PricePlanned.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
                        }
                        else
                        {
                        	PricePlanned = PriceStd.multiply(BaseQty.divide(qty))
                                    .setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
                        }
                       
                        leadInfo.setPlannedPrice(PricePlanned);
                                              
                        BigDecimal LineNetAmt = BaseQty.multiply(PricePlanned);
                        LineNetAmt = LineNetAmt.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP); 
                        leadInfo.setLineNetAmt(LineNetAmt);
                        
                        if(M_PriceList_Version_ID > 0)
	                    {
	                    	 MProductPrice mpp=MProductPrice.get(Env.getCtx(),M_PriceList_Version_ID, productId, null);
	 	                    
	 	                    if(mpp.getPriceList().compareTo(Env.ZERO)>0)
	 	    				{
	 	    					BigDecimal Discount = new BigDecimal ((mpp.getPriceList().doubleValue() - mpp.getPriceStd().doubleValue()) / mpp.getPriceList().doubleValue() * 100.0);
	 	    					if (Discount.scale() > 2)
	 	    						Discount = Discount.setScale(2, BigDecimal.ROUND_HALF_UP);
	 	    					leadInfo.setDiscount(Discount);
	 	    				}
	                    }
                        
                        int taxID = UtilTax.getTaxIDBasedOnState(MLead.Table_ID,lead.getC_Lead_ID(),productId,LineNetAmt, PricePlanned);
                        
                        leadInfo.setC_Tax_ID(taxID);
                       
                        BigDecimal taxRate = DB.getSQLValueBD(trxName, 
                            "SELECT Rate FROM C_Tax WHERE C_Tax_ID =" + taxID);    
                        
                        if (taxRate == null || taxRate.compareTo(Env.ZERO) == 0) {
                            taxRate = Env.ONE;
                        }                    
                        taxRate = taxRate.divide(Env.ONEHUNDRED);
                        
                        BigDecimal taxAmount = LineNetAmt.multiply(taxRate)
                            .setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
                        leadInfo.setTaxAmt(taxAmount);
                        leadInfo.setLineTotalAmount(LineNetAmt.add(taxAmount));
                    } else if (row.getChargeEditor().getValue() != null) {
                        Integer chargeId = (Integer)row.getChargeEditor().getValue();
                        leadInfo.setC_Charge_ID(chargeId != null ? chargeId : 0);
                        leadInfo.setPlannedQty(qty);
                        leadInfo.setC_UOM_ID(C_UOM_ID);
                        leadInfo.setBaseQty(qty);                   
                        leadInfo.setPlannedPrice(Env.ZERO);
                        leadInfo.setBasePrice(Env.ZERO);
                        leadInfo.setPriceList(Env.ZERO);
                        leadInfo.setLineNetAmt(Env.ZERO);
                        leadInfo.setC_Tax_ID(C_Tax_ID);
                        leadInfo.setTaxAmt(Env.ZERO);
                        leadInfo.setLineTotalAmount(Env.ZERO);                                                   
                    }
                    
                    leadInfo.setPlannedQty(qty);
                    
                    if (!leadInfo.save()) {
                        FDialog.error(0, "ErrorSavingLeadInfo");
                        trx.rollback();
                        return;
                    }
                }
            } 
            else if ("Opportunity".equalsIgnoreCase(headerType)) {
            	// Save opportunity lines
                for (LineRow row : lineRows) {
                    if (row.getProductEditor().getValue() == null && row.getChargeEditor().getValue() == null) {
                        FDialog.error(0, "Please select either Product or Charge");
                        trx.rollback();
                        return;
                    }
                    
                    MSalesOpportunity opportunity = new MSalesOpportunity(Env.getCtx(), headerId, trxName);
                    MOpportunityLine line = null;
                    int lineNo = 0;
                    // Check if this is an existing line
                    if (row.getLineId() > 0) {
                        line = new MOpportunityLine(Env.getCtx(), row.getLineId(), trxName);
                        lineNo = line.getLine();
                    } else {
                        line = new MOpportunityLine(Env.getCtx(), 0, trxName);
                        line.setLine(lineNo + 10);
                        lineNo = lineNo + 10;
                    }
                    
                    line.setAD_Client_ID(opportunity.getAD_Client_ID());
                    line.setAD_Org_ID(opportunity.getAD_Org_ID());
                    line.setC_SalesOpportunity_ID(opportunity.getC_SalesOpportunity_ID());
                    
                    
                    
                    int C_UOM_ID = DB.getSQLValue(trxName, 
                            "SELECT C_UOM_ID FROM C_UOM WHERE AD_Client_ID =" + Env.getAD_Client_ID(Env.getCtx()) + 
                            " AND IsDefault='Y' AND IsActive='Y'");
                      
                    int C_Tax_ID = DB.getSQLValue(trxName, 
                        "SELECT C_Tax_ID FROM C_Tax WHERE AD_Client_ID =" + Env.getAD_Client_ID(Env.getCtx()) + 
                        " AND IsDefault='Y' AND IsActive='Y'");
                    
                    BigDecimal qty = row.getQtyEditor().getValue() != null ? 
                        (BigDecimal)row.getQtyEditor().getValue() : BigDecimal.ZERO;
                    
                    if (row.getProductEditor().getValue() != null) {
                        Integer productId = (Integer)row.getProductEditor().getValue();
                        line.setM_Product_ID(productId != null ? productId : 0);
                        
                        MProduct product = new MProduct(Env.getCtx(), productId, trxName);    
                        
//                        int taxID = product.getC_Tax_ID() > 0 ? product.getC_Tax_ID() : C_Tax_ID;
                        
                        line.setC_UOM_ID(product.getC_UOM_ID() > 0 ? product.getC_UOM_ID() : C_UOM_ID);
                                                                    
                        int M_PriceList_Version_ID = DB.getSQLValue(trxName, 
                            "SELECT MAX(M_PriceList_Version_ID) FROM M_PriceList_Version " +
                            "WHERE IsActive = 'Y' AND M_PriceList_ID =" + opportunity.getM_PriceList_ID());
                        
                        BigDecimal PriceStd = Env.ZERO;
                        BigDecimal PriceList = Env.ZERO; 
                        
                        String sqlNew = "SELECT PriceStd, PriceList FROM M_ProductPrice " +
                                      "WHERE AD_Client_ID = ? AND M_PriceList_Version_ID = ? AND M_product_id = ?";
                        PreparedStatement pstmtNew = null;
                        ResultSet rsNew = null;
                        try {
                            pstmtNew = DB.prepareStatement(sqlNew, trxName);
                            pstmtNew.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
                            pstmtNew.setInt(2, M_PriceList_Version_ID);
                            pstmtNew.setInt(3, productId);
                            rsNew = pstmtNew.executeQuery();
                            if (rsNew.next()) {
                                PriceStd = rsNew.getBigDecimal(1);
                                PriceList = rsNew.getBigDecimal(2);                        
                            }
                        } catch (SQLException e) {
                            throw new DBException(e, sqlNew);
                        } finally {
                            DB.close(rsNew, pstmtNew);
                        }
                        
                        BigDecimal BaseQty = MUOMConversion.convertProductFrom(Env.getCtx(), productId, 
                                product.getC_UOM_ID() > 0 ? product.getC_UOM_ID() : C_UOM_ID, qty);
                        
                        line.setBaseQty(BaseQty);
                        line.setBasePrice(PriceStd);
                        line.setPriceList(PriceList);
                        
                        int StdPrecision = MPriceList.getStandardPrecision(Env.getCtx(), opportunity.getM_PriceList_ID());
                       
                        BigDecimal PricePlanned = Env.ZERO;
                         
                        
                        if(((BigDecimal) priceEditor.getValue()).compareTo(BigDecimal.ZERO) != 0)
                        {
                        	PricePlanned = (BigDecimal)priceEditor.getValue();
                        	PricePlanned = PricePlanned.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
                        }
                        else
                        {
                        	PricePlanned = PriceStd.multiply(BaseQty.divide(qty))
                                    .setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
                        }
                       
                        line.setPlannedPrice(PricePlanned);
                                              
                        BigDecimal LineNetAmt = BaseQty.multiply(PricePlanned);
                        LineNetAmt = LineNetAmt.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP); 
                        line.setLineNetAmt(LineNetAmt);
                        
                        if(M_PriceList_Version_ID > 0)
	                    {
	                    	 MProductPrice mpp=MProductPrice.get(Env.getCtx(),M_PriceList_Version_ID, productId, null);
	 	                    
	 	                    if(mpp.getPriceList().compareTo(Env.ZERO)>0)
	 	    				{
	 	    					BigDecimal Discount = new BigDecimal ((mpp.getPriceList().doubleValue() - mpp.getPriceStd().doubleValue()) / mpp.getPriceList().doubleValue() * 100.0);
	 	    					if (Discount.scale() > 2)
	 	    						Discount = Discount.setScale(2, BigDecimal.ROUND_HALF_UP);
	 	    					line.setDiscount(Discount);
	 	    				}
	                    }                      
                        
                        int taxID = UtilTax.getTaxIDBasedOnState(MSalesOpportunity.Table_ID,opportunity.getC_SalesOpportunity_ID(),productId, LineNetAmt, PricePlanned);
                        
                        line.setC_Tax_ID(taxID);
                       
                        BigDecimal taxRate = DB.getSQLValueBD(trxName, 
                            "SELECT Rate FROM C_Tax WHERE C_Tax_ID =" + taxID);    
                        
                        if (taxRate == null || taxRate.compareTo(Env.ZERO) == 0) {
                            taxRate = Env.ONE;
                        }                    
                        taxRate = taxRate.divide(Env.ONEHUNDRED);
                        
                        BigDecimal taxAmount = LineNetAmt.multiply(taxRate)
                            .setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
                        line.setTaxAmt(taxAmount);
                        line.setLineTotalAmount(LineNetAmt.add(taxAmount));
                    } else if (row.getChargeEditor().getValue() != null) {
                        Integer chargeId = (Integer)row.getChargeEditor().getValue();
                        line.setC_Charge_ID(chargeId != null ? chargeId : 0);
                        line.setC_UOM_ID(C_UOM_ID);
                        line.setPlannedQty(qty);
                        line.setC_UOM_ID(C_UOM_ID);
                        line.setBaseQty(qty);                   
                        line.setPlannedPrice(Env.ZERO);
                        line.setBasePrice(Env.ZERO);
                        line.setPriceList(Env.ZERO);
                        line.setLineNetAmt(Env.ZERO);
                        line.setC_Tax_ID(C_Tax_ID);
                        line.setTaxAmt(Env.ZERO);
                        line.setLineTotalAmount(Env.ZERO);  
                    }
                    
                    line.setPlannedQty(qty);
                    
                    if (!line.save()) {
                        FDialog.error(0, "ErrorSavingOpportunityLine");
                        trx.rollback();
                        return;
                    }
                }
            }
            
            trx.commit();
            created = true;
            FDialog.info(0, null, "LinesSavedSuccessfully");
        } catch (Exception e) {
            trx.rollback();
            FDialog.error(0, "ErrorSavingData", e.getLocalizedMessage());
        } finally {
            trx.close();
        }
    }
    
    @Override
    public void valueChange(ValueChangeEvent evt) {
        // Handle value change events if needed
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getTarget() == saveButton) {        	
            saveLines();
            if (created) {
            	if(headerType.equalsIgnoreCase("Lead"))
            		Events.postEvent("onLeadInfoSaved", this, null);
            	
            	else if(headerType.equalsIgnoreCase("Opportunity"))
            		Events.postEvent("onOpportunityLineSaved", this, null);
            	
                clearForm();
                this.dispose();
            }            
        } else if (event.getTarget() == cancelButton) {
            clearForm();
            this.dispose(); 
        } else if (event.getTarget() == addLineButton) {
            addLineRow();
        }
    }
    
  
    private class LineRow {
        private int lineId = 0; // Stores either C_LeadInfo_ID or C_SalesOpportunityLine_ID
        private int line;
        private int productId = 0;
        private int chargeId = 0;
        private BigDecimal quantity = BigDecimal.ONE;
        private BigDecimal price = BigDecimal.ZERO;
        
        // Editor references
        private transient WSearchEditor productEditor;
        private transient WTableDirEditor chargeEditor;
        private transient WNumberEditor qtyEditor;
        private transient WNumberEditor priceEditor;
        
        public LineRow(int line) {
            this.line = line;
        }
        
        // Getters and setters 
        public int getLineId() {
            return this.lineId;
        }
        public void setLineId(int lineId) {
            System.out.println("Setting lineId: " + lineId); // Debug log
            this.lineId = lineId;
        }
        public int getLine() { return line; }
        public void setLine(int line) { this.line = line; }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getChargeId() { return chargeId; }
        public void setChargeId(int chargeId) { this.chargeId = chargeId; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public WSearchEditor getProductEditor() { return productEditor; }
        public void setProductEditor(WSearchEditor editor) { this.productEditor = editor; }
        public WTableDirEditor getChargeEditor() { return chargeEditor; }
        public void setChargeEditor(WTableDirEditor editor) { this.chargeEditor = editor; }
        public WNumberEditor getQtyEditor() { return qtyEditor; }
        public void setQtyEditor(WNumberEditor editor) { this.qtyEditor = editor; }
        public WNumberEditor getPriceEditor() { return priceEditor; }
        public void setPriceEditor(WNumberEditor editor) { this.priceEditor = editor; }
    }
}