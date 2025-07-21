package org.cyprus.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.cyprus.crm.model.MLead;
import org.cyprus.crm.model.MLeadInfo;
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
import org.cyprus.webui.editor.WStringEditor;
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
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;

public class WLeadForm extends CustomForm implements EventListener, ValueChangeListener {

//  
	 private static final long serialVersionUID = 1L;
	 
	 private static CLogger log = CLogger.getCLogger(WLeadForm.class);
	 
	    private boolean Created = false;
	    
	    private Vbox mainLayout = new Vbox();
	    private Grid leadInfoGrid = new Grid();
	    
	    private WStringEditor companyNameEditor, contactNameEditor, emailEditor, phoneEditor;
	    private WStringEditor address1Editor, summaryEditor;
	    
	    private WTableDirEditor campaignEditor, leadSourceEditor, priceListEditor, bpGroupEditor, regionEditor;
	    private WTableDirEditor countryEditor, leadRatingEditor, leadQualificationEditor, SalesRepEditor, warehouseEditor;
	    
	    private WSearchEditor productEditor;
	    private WTableDirEditor chargeEditor;
	    private WNumberEditor qtyEditor;
	    
	    private List<LeadInfoRow> leadInfoRows = new ArrayList<>();
	    
	    private Button saveButton = new Button("Save Lead");
	    private Button cancelButton = new Button("Cancel");
	    private Button addLineButton = new Button(Msg.getMsg(Env.getCtx(), "AddLine"));
	    
	    private int currentLeadId = 0;
	    
	    private static final String LEAD_SECTION_HEIGHT = "160px";
	    private static final String LEAD_INFO_SECTION_HEIGHT = "195px";
	    private static final String BUTTON_SECTION_HEIGHT = "40px";
	    private static final String FORM_HEIGHT = "460px"; // Sum of sections + padding
	    private static final String MANDATORY_FIELD_STYLE = "color: red; font-weight: bold;";
	    private static final String MANDATORY_LABEL_SUFFIX = " *";

	    public WLeadForm() {
	        super();
	        try {
	            init();
	        } catch (Exception e) {
	            FDialog.error(0, "ErrorInitializingForm", e.getLocalizedMessage());
	        }
	    }
	  
	    protected void init() {
	        this.setWidth("100%");
	        this.setHeight(FORM_HEIGHT);
	        this.setStyle("padding: 5px; display: flex; flex-direction: column;");
	        this.setClosable(true);
	        this.setSizable(true);
	        
	        mainLayout.setWidth("100%");
	        mainLayout.setHeight("100%");
	        mainLayout.setStyle("display: flex; flex-direction: column; gap: 5px;");
	        this.appendChild(mainLayout);
	        
	        mainLayout.appendChild(createLeadSection());
	        
	        mainLayout.appendChild(createLeadInfoSection());
	        
	        mainLayout.appendChild(createButtonPanel());
	        
	        loadNewLead();
	    }
	    
	    private Div createLeadSection() {
	        Div leadDiv = new Div();
	        leadDiv.setStyle("padding: 5px; border: 1px solid #ccc; height: " + LEAD_SECTION_HEIGHT + 
	                        "; overflow-x: auto; overflow-y: auto;");
	        
	        Hbox columnsContainer = new Hbox();
	        columnsContainer.setWidth("100%");
	        columnsContainer.setStyle("display: flex; gap: 10px; height: 100%;");
	        
	        Vbox column1 = new Vbox();
	        column1.setWidth("33%");
	        column1.setStyle("flex: 1; overflow-y: auto;");
	        
	        Vbox column2 = new Vbox();
	        column2.setWidth("34%");
	        column2.setStyle("flex: 1; overflow-y: auto;");
	        
	        Vbox column3 = new Vbox();
	        column3.setWidth("33%");
	        column3.setStyle("flex: 1; overflow-y: auto;");
	        
	        populateBasicInfoColumn(column1);
	        populateLeadDetailsColumn(column2);
	        populateAddressColumn(column3);
	        
	        columnsContainer.appendChild(column1);
	        columnsContainer.appendChild(column2);
	        columnsContainer.appendChild(column3);
	        
	        leadDiv.appendChild(columnsContainer);
	        return leadDiv;
	    }
	    
	    private void populateBasicInfoColumn(Vbox column) {
	        Grid grid = new Grid();
	        grid.setWidth("100%");
	        grid.setStyle("border: none;"); // Remove grid borders
	        Rows rows = new Rows();
	        rows.setStyle("border: none;"); // Remove row borders
	        grid.appendChild(rows);
	        
	        companyNameEditor = new WStringEditor("CompanyName", false, false, true, 10, 30, null, null);
	        companyNameEditor.setMandatory(false);
	        setupDynamicTooltip(companyNameEditor);
	        addField(rows, Msg.translate(Env.getCtx(), "CompanyName"), companyNameEditor, false);
	        
	        address1Editor = new WStringEditor("Address1", false, false, true, 10, 30, null, null);
//	        address1Editor.setMandatory(true);
	        setupDynamicTooltip(address1Editor);
	        addField(rows, "Address", address1Editor, false);
	        
	        summaryEditor = new WStringEditor("Summary", true, false, true, 10, 30, null, null);
	        summaryEditor.setMandatory(true);
	        setupDynamicTooltip(summaryEditor);
	        addField(rows, "Summary" + MANDATORY_LABEL_SUFFIX, summaryEditor, true);
	        
	        MLookup leadRatingL = null;
	        try {
	            leadRatingL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 
	                MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_LeadRating),
	                DisplayType.List, Env.getLanguage(Env.getCtx()), "LeadRating", 1000010,
	                false, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        leadRatingEditor = new WTableDirEditor("LeadRating", true, false, true, leadRatingL);
	        leadRatingEditor.setMandatory(true);
	        setupDynamicTooltip(leadRatingEditor);
	        addField(rows, Msg.translate(Env.getCtx(), "LeadRating") + MANDATORY_LABEL_SUFFIX, leadRatingEditor, true);
	        
//	        MLookup warehouseL = null;
//	        String validation = "AD_Org_ID = " + Env.getAD_Org_ID(Env.getCtx()); 
//			try {
//				 warehouseL = MLookupFactory.get(Env.getCtx(), getWindowNo(), MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_M_Warehouse_ID),
//						DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "M_Warehouse_ID", 0 ,
//						false, validation);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	        
//			warehouseEditor = new WTableDirEditor("M_Warehouse_ID", false, false, true, warehouseL);
//	        setupDynamicTooltip(warehouseEditor);
//	        addField(rows, Msg.translate(Env.getCtx(), "M_Warehouse_ID"), warehouseEditor, false);
            
	        column.appendChild(grid);
	    }
	    
	    private void populateLeadDetailsColumn(Vbox column) {
	        Grid grid = new Grid();
	        grid.setWidth("100%");
	        grid.setStyle("border: none;"); // Remove grid borders
	        Rows rows = new Rows();
	        rows.setStyle("border: none;"); // Remove row borders
	        grid.appendChild(rows);
	        
	        contactNameEditor = new WStringEditor("ContactName", true, false, true, 10, 30, null, null);
	        contactNameEditor.setMandatory(true);
	        setupDynamicTooltip(contactNameEditor);
	        addField(rows, Msg.translate(Env.getCtx(), "ContactName") + MANDATORY_LABEL_SUFFIX, contactNameEditor, true);
	        
	        // Country
	        MLookup countryL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
	            MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_Country_ID), DisplayType.TableDir);
	        countryEditor = new WTableDirEditor("C_Country_ID", true, false, true, countryL);
	        countryEditor.setMandatory(true);
	        setupDynamicTooltip(countryEditor);
	        addField(rows, "Country" + MANDATORY_LABEL_SUFFIX, countryEditor, true);
	        
//	        MLookup campaignL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
//	            MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_Campaign_ID), DisplayType.TableDir);
//	        campaignEditor = new WTableDirEditor("C_Campaign_ID", false, false, true, campaignL);
//	        setupDynamicTooltip(campaignEditor);
//	        addField(rows, Msg.translate(Env.getCtx(), "C_Campaign_ID"), campaignEditor, false);
	        
//	        MLookup regionL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
//	                MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_Region_ID), DisplayType.TableDir);
//	            regionEditor = new WTableDirEditor("C_Region_ID", false, false, true, regionL);
//	            regionEditor.setMandatory(true);
//	            setupDynamicTooltip(regionEditor);
	        regionEditor = createRegionEditor(null);
	            addField(rows, Msg.translate(Env.getCtx(), "C_Region_ID") + MANDATORY_LABEL_SUFFIX, regionEditor, true);
	            
	            // Add dynamic filtering for regions based on country selection
	            countryEditor.addValueChangeListener(new ValueChangeListener() {
	                public void valueChange(ValueChangeEvent evt) {
	                    Integer countryId = (Integer)evt.getNewValue();	                  
	                    if (countryId != null && countryId > 0) {
	                        String validation = "C_Country_ID = " + countryId;                       
	                        updateRegionEditor(validation, rows);

	                    }
	                }
	            });
	        
	        // Lead Source
	        MLookup leadSourceL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
	            MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_LeadSource_ID), DisplayType.TableDir);
	        leadSourceEditor = new WTableDirEditor("C_LeadSource_ID", false, false, true, leadSourceL);
	        leadSourceEditor.setMandatory(true);
	        setupDynamicTooltip(leadSourceEditor);
	        addField(rows, Msg.translate(Env.getCtx(), "C_LeadSource_ID") + MANDATORY_LABEL_SUFFIX, leadSourceEditor, true);
	        
	     // BP Group
	        MLookup bpGroupL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, 
	            MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_BP_Group_ID), DisplayType.TableDir);
	        bpGroupEditor = new WTableDirEditor("C_BP_Group_ID", false, false, true, bpGroupL);
	        bpGroupEditor.setMandatory(true);
	        setupDynamicTooltip(bpGroupEditor);
	        addField(rows, Msg.translate(Env.getCtx(), "C_BP_Group_ID") + MANDATORY_LABEL_SUFFIX, bpGroupEditor, true);
	        
	        column.appendChild(grid);
	    }
	    
	    private WTableDirEditor createRegionEditor(String validation) {
	        MLookup regionL = null;
			try {
				regionL = MLookupFactory.get(Env.getCtx(), getWindowNo(), MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_Region_ID),
						DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_Region_ID", 0 ,
						false, validation);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
	        WTableDirEditor editor = new WTableDirEditor("C_Region_ID", true, false, true, regionL);
	        editor.setMandatory(true);
	        setupDynamicTooltip(editor);
	        return editor;
	    }
	    
	    

	    private void updateRegionEditor(String validation, Rows rows) {
	        // Get the parent row of the current region editor
	        Row regionRow = (Row)regionEditor.getComponent().getParent();
	        int index = rows.getChildren().indexOf(regionRow);
	        
	        // Remove current editor
	        rows.removeChild(regionRow);
	        
	        // Create new editor with updated filter
	        regionEditor = createRegionEditor(validation);
	        
	        // Create new row and add it back at the same position
	        Row newRow = new Row();
	        newRow.setStyle("border: none; margin: 0; padding: 2px 0;");
	        Label labelComponent = new Label("Region" + MANDATORY_LABEL_SUFFIX);
            labelComponent.setStyle(MANDATORY_FIELD_STYLE);
            newRow.appendChild(labelComponent);
	        newRow.appendChild(regionEditor.getComponent());
	        rows.insertBefore(newRow, (Component) rows.getChildren().get(index));
	    }
	    
//	    private WTableDirEditor createWarehouseEditor(String validation) {
//	        MLookup warehouseL = null;
//			try {
//				warehouseL = MLookupFactory.get(Env.getCtx(), getWindowNo(), MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_M_Warehouse_ID),
//						DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "M_Warehouse_ID", 0 ,
//						false, validation);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	        
//	        WTableDirEditor editor = new WTableDirEditor("M_Warehouse_ID", false, false, true, warehouseL);
//	        setupDynamicTooltip(editor);
//	        return editor;
//	    }
	    
//	    private void updateWarehouseEditor(String validation, Rows rows) {
//	        // Get the parent row of the current region editor
//	        Row regionRow = (Row)warehouseEditor.getComponent().getParent();
//	        int index = rows.getChildren().indexOf(regionRow);
//	        
//	        // Remove current editor
//	        rows.removeChild(regionRow);
//	        
//	        // Create new editor with updated filter
//	        warehouseEditor = createWarehouseEditor(validation);
//	        
//	        // Create new row and add it back at the same position
//	        Row newRow = new Row();
//	        newRow.setStyle("border: none; margin: 0; padding: 2px 0;");
//	        Label labelComponent = new Label("Warehouse");
//            newRow.appendChild(labelComponent);
//	        newRow.appendChild(warehouseEditor.getComponent());
//	        rows.insertBefore(newRow, (Component) rows.getChildren().get(index));
//	    }
	    
	    private void populateAddressColumn(Vbox column) {
	        Grid grid = new Grid();
	        grid.setWidth("100%");
	        grid.setStyle("border: none;"); // Remove grid borders
	        Rows rows = new Rows();
	        rows.setStyle("border: none;"); // Remove row borders
	        grid.appendChild(rows);
	        
	        emailEditor = new WStringEditor("Email", false, false, true, 10, 30, null, null);
//	        emailEditor.setMandatory(true);
	        setupDynamicTooltip(summaryEditor);
	        addField(rows, "Email", emailEditor, false);
	        	       
	        
	        // Phone
	        phoneEditor = new WStringEditor("Phone", false, false, true, 10, 30, null, null);
//	        phoneEditor.setMandatory(true);
	        setupDynamicTooltip(phoneEditor);
	        addField(rows, "Phone", phoneEditor, false);
	        
	    
	        int priceListID = DB.getSQLValue(null, "SELECT M_PriceList_ID FROM M_PriceList WHERE IsActive = 'Y' AND IsDefault = 'Y' AND IsSOPriceList = 'Y'");
	        MLookup priceListL = null;
			try {
				priceListL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 
				        MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_M_PriceList_ID), DisplayType.TableDir,Env.getLanguage(Env.getCtx()),"M_PriceList_ID",0, false,"M_PriceList.M_PriceList_ID=" + priceListID);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        priceListEditor = new WTableDirEditor("M_PriceList_ID", true, false, true, priceListL);
	        priceListEditor.setMandatory(true);
	        setupDynamicTooltip(priceListEditor);

	        priceListEditor.getComponent().addEventListener(Events.ON_RENDER, new EventListener() {
	            @Override
	            public void onEvent(Event event) throws Exception {
	                
	                priceListEditor.setValue(priceListID);

	                System.out.println("Current value: " + priceListEditor.getValue());
	            }
	        });
	        
	        addField(rows, Msg.translate(Env.getCtx(), "M_PriceList_ID") + MANDATORY_LABEL_SUFFIX, priceListEditor, true);
	       
	     // Lead Qualification
	        MLookup leadQualificationL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_C_LeadQualification_ID), DisplayType.TableDir);
	        leadQualificationEditor = new WTableDirEditor("C_LeadQualification_ID", true, false, true, leadQualificationL);
	        leadQualificationEditor.setMandatory(true);
	        setupDynamicTooltip(leadQualificationEditor);
		        addField(rows, "Lead Qualification" + MANDATORY_LABEL_SUFFIX, leadQualificationEditor, true);
		        
	     // Lead Qualification
//	        MLookup salesRepL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), 0, MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_SalesRep_ID), DisplayType.Table);
	        MLookup salesRepL = null;
			try {
				salesRepL = MLookupFactory.get(Env.getCtx(), this.getWindowNo(), MColumn.getColumn_ID(MLead.Table_Name, MLead.COLUMNNAME_SalesRep_ID),
						DisplayType.Table, Env.getLanguage(Env.getCtx()), "SalesRep_ID", 190,
						false,"");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        SalesRepEditor = new WTableDirEditor("SalesRep_ID", true, false, true, salesRepL);
	        SalesRepEditor.setMandatory(true);
	        setupDynamicTooltip(SalesRepEditor);
		        addField(rows, Msg.translate(Env.getCtx(), "SalesRep_ID") + MANDATORY_LABEL_SUFFIX, SalesRepEditor, true);
	        
	        column.appendChild(grid);
	    }

	    private Div createLeadInfoSection() {
	        Div leadInfoDiv = new Div();
	        leadInfoDiv.setStyle("padding: 5px; border: 1px solid #ccc; height: " + LEAD_INFO_SECTION_HEIGHT + 
	                           "; display: flex; flex-direction: column;");
	        
	        // Add line button
	        addLineButton.addEventListener(Events.ON_CLICK, this);
	        leadInfoDiv.appendChild(addLineButton);
	        
	        // Lead info grid with fixed height and scrolling
	        leadInfoGrid.setWidth("100%");
	        leadInfoGrid.setStyle("flex: 1; height:" + LEAD_INFO_SECTION_HEIGHT + ";  overflow-y: auto;");
	        
	        // Create header
	        Rows rows = new Rows();
	        leadInfoGrid.appendChild(rows);
	        Row header = new Row();
	        header.appendChild(new Label(Msg.translate(Env.getCtx(), "M_Product_ID")));
	        header.appendChild(new Label(Msg.translate(Env.getCtx(), "C_Charge_ID")));
	        header.appendChild(new Label(Msg.translate(Env.getCtx(), "Qty")));
	        header.appendChild(new Label("")); // For delete button
	        rows.appendChild(header);
	        
	        leadInfoDiv.appendChild(leadInfoGrid);
	        return leadInfoDiv;
	    }
	    
	    private void refreshLeadInfoGrid() {
	        // Clear existing rows (except header)
	        Rows rows = (Rows) leadInfoGrid.getFirstChild();
	        while (rows.getChildren().size() > 1) {
	            rows.removeChild(rows.getLastChild());
	        }
	        
	        // Add current rows
	        for (LeadInfoRow row : leadInfoRows) {
	            Row gridRow = new Row();
	            
	            // Product
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
	                
	                // If product is selected, make charge read-only and clear its value
	                if (newValue != null && newValue > 0) {
	                    row.getChargeEditor().setReadWrite(false);
	                    row.getChargeEditor().setValue(null);
	                    row.setChargeId(0);
	                } else {
	                    row.getChargeEditor().setReadWrite(true);
	                }
	            });
	            gridRow.appendChild(productEditor.getComponent());
	            
	            // Charge
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
	                
	                // If charge is selected, make product read-only and clear its value
	                if (newValue != null && newValue > 0) {
	                    row.getProductEditor().setReadWrite(false);
	                    row.getProductEditor().setValue(null);
	                    row.setProductId(0);
	                } else {
	                    row.getProductEditor().setReadWrite(true);
	                }
	            });
	            gridRow.appendChild(chargeEditor.getComponent());
	            
	            // Quantity
	            qtyEditor = new WNumberEditor("PlannedQty", false, false, true, 29, "PlannedQty");
	            qtyEditor.setValue(row.getQuantity());
	            row.setQtyEditor(qtyEditor);
	            setupDynamicTooltip(qtyEditor);
	            qtyEditor.addValueChangeListener(e -> {
	                row.setQuantity(e.getNewValue() != null ? (BigDecimal)e.getNewValue() : BigDecimal.ZERO);
	            });
	            gridRow.appendChild(qtyEditor.getComponent());
	            
	            // Delete button
	            Button deleteBtn = new Button("Delete");
	            deleteBtn.addEventListener(Events.ON_CLICK, e -> {
	                leadInfoRows.remove(row);
	                refreshLeadInfoGrid();
	            });
	            gridRow.appendChild(deleteBtn);
	            
	            // Set initial read-only state based on existing values
	            if (row.getProductId() > 0) {
	                row.getChargeEditor().setReadWrite(false);
	            } else if (row.getChargeId() > 0) {
	                row.getProductEditor().setReadWrite(false);
	            }
	            
	            rows.appendChild(gridRow);
	        }
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

	    private void loadNewLead() {
	        currentLeadId = 0;
	        clearForm();
	        addLeadInfoRow();
	    }
	    
	    private void clearForm() {
	        companyNameEditor.setValue(null);
	        contactNameEditor.setValue(null);
	        emailEditor.setValue(null);
	        phoneEditor.setValue(null);
	        address1Editor.setValue(null);
	        summaryEditor.setValue(null);
	        countryEditor.setValue(null);
	        leadRatingEditor.setValue(null);
//	        warehouseEditor.setValue(null);
//	        campaignEditor.setValue(null);
	        regionEditor.setValue(null);
	        leadSourceEditor.setValue(null);
	        priceListEditor.setValue(null);
	        leadQualificationEditor.setValue(null);
	        SalesRepEditor.setValue(null);
	        
	        leadInfoRows.clear();
	        refreshLeadInfoGrid();
	    }
	    
	    private LeadInfoRow addLeadInfoRow() {
	        LeadInfoRow row = new LeadInfoRow(leadInfoRows.size() + 10);
	        leadInfoRows.add(row);
	        refreshLeadInfoGrid();
	        return row;
	    }
	    
	    private void addField(Rows rows, String label, WEditor editor, boolean isMandatory) {
	        Row row = new Row();
	        row.setStyle("border: none; margin: 0; padding: 2px 0;"); // Remove borders and adjust spacing
	        Label labelComponent = new Label(label);
	        if (isMandatory) {
	            labelComponent.setStyle(MANDATORY_FIELD_STYLE);
	        }
	        row.appendChild(labelComponent);
	        row.appendChild(editor.getComponent());
	        rows.appendChild(row);
	    }

    
	    private void saveLead() {
	        // Start transaction
	        String trxName = Trx.createTrxName("LeadSave");
	        Trx trx = Trx.get(trxName, true);
	        MLead lead = null;
	        
	        try {
	            // Create and save lead
	            lead = new MLead(Env.getCtx(), 0, trxName);
	            lead.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));
	            lead.setAD_Org_ID(Env.getAD_Org_ID(Env.getCtx()));
	            lead.setName((String)summaryEditor.getValue());
	            lead.setEnquiryDate(new Timestamp(System.currentTimeMillis()));
	            lead.setFollowupDate(new Timestamp(System.currentTimeMillis()));
	            lead.setStatus("10");
	            
	            // Set email and other fields
	            String email = DB.TO_STRING((String)emailEditor.getValue()); 
	            
	            if (SalesRepEditor.getValue() != null) {
	                lead.setSalesRep_ID((Integer)SalesRepEditor.getValue());
	            } 
	            
	            if (leadQualificationEditor.getValue() != null) {
	                lead.setC_LeadQualification_ID((Integer)leadQualificationEditor.getValue());
	            } 
	            
	            if (bpGroupEditor.getValue() != null) {
	                lead.setC_BP_Group_ID((Integer)bpGroupEditor.getValue());
	            }
	            
	            // Check for existing user/bpartner
	            int AD_User_ID = 0;
	            int C_BPartner_ID = 0;
	            int C_BPartner_Location_ID = 0;
	            boolean IsCustomer = false;
	            boolean IsProspect = false;
	            
	            String sql = "SELECT usr.AD_User_ID, usr.C_BPartner_ID, usr.C_BPartner_Location_ID, bp.IsCustomer, bp.IsProspect"
	                    + " FROM AD_User usr"
	                    + " JOIN C_BPartner bp ON (bp.C_BPartner_ID = usr.C_BPartner_ID)"
	                    + " WHERE usr.AD_Client_ID=? AND usr.EMail=? AND usr.IsActive='Y'";
	            
	            PreparedStatement pstmt = null;
	            ResultSet rs = null;
	            try {
	                pstmt = DB.prepareStatement(sql, trxName);
	                pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
	                pstmt.setString(2, email);
	                rs = pstmt.executeQuery();
	                if (rs.next()) {
	                    AD_User_ID = rs.getInt(1);
	                    C_BPartner_ID = rs.getInt(2);
	                    C_BPartner_Location_ID = rs.getInt(3);
	                    IsCustomer = rs.getBoolean(4);
	                    IsProspect = rs.getBoolean(5);
	                }
	            } catch (SQLException e) {
	                throw new DBException(e, sql);
	            } finally {
	                DB.close(rs, pstmt);
	            }
	            
	            // Set BP type based on existing user
	            if (AD_User_ID > 0) {
	                if (IsCustomer) {
	                    lead.setBPType("C");
	                    lead.setC_BPartner_ID(C_BPartner_ID);
	                    lead.setAD_User_ID(AD_User_ID);
	                    lead.setC_BPartner_Location_ID(C_BPartner_Location_ID);
	                } else if (IsProspect) {
	                    lead.setBPType("P");
	                    lead.setRef_BPartner_ID(C_BPartner_ID);
	                    lead.setRef_User_ID(AD_User_ID);
	                    lead.setRef_BPartner_Location_ID(C_BPartner_Location_ID);
	                }
	            } else {
	                lead.setBPType("N");
	                lead.setCompanyName((String)companyNameEditor.getValue());                
	                lead.setAddress1((String)address1Editor.getValue());
	                if (countryEditor.getValue() != null) {
	                    lead.setC_Country_ID((Integer)countryEditor.getValue());
	                }
	            }
	            
	            // Set additional fields
	            lead.setContactName((String)contactNameEditor.getValue());
	            if(!email.equalsIgnoreCase("null"))
	            {
	            	lead.setEMail(email);
	            }
	            
	            lead.setPhone((String)phoneEditor.getValue());
	            lead.setSummary((String)summaryEditor.getValue());
	            lead.setLeadRating((String)leadRatingEditor.getValue());
	            
	            if (regionEditor.getValue() != null) {
	                lead.setC_Region_ID((Integer)regionEditor.getValue());
	            }
	            if (leadSourceEditor.getValue() != null) {
	                lead.setC_LeadSource_ID((Integer)leadSourceEditor.getValue());
	            }
//	            if (warehouseEditor.getValue() != null) {
//	                lead.setM_Warehouse_ID((Integer)warehouseEditor.getValue());
//	            }
	            
	            int M_Warehouse_ID = DB.getSQLValue(null, "SELECT M_Warehouse_ID FROM AD_OrgInfo WHERE AD_Org_ID=" + Env.getAD_Org_ID(Env.getCtx()));
	            lead.setM_Warehouse_ID(M_Warehouse_ID);
	            
	            if (priceListEditor.getValue() != null) {
	                lead.setM_PriceList_ID((Integer)priceListEditor.getValue());
	            }
	            
	            // Set currency and conversion type
	            int currencyID = DB.getSQLValue(trxName, 
	                "SELECT C_Currency_ID FROM C_Country WHERE C_Country_ID = ?", 
	                (Integer)countryEditor.getValue());
	            
	            int currencyTypeID = DB.getSQLValue(trxName, 
	                "SELECT C_ConversionType_ID FROM C_ConversionType WHERE IsActive = 'Y' AND IsDefault = 'Y'");
	            
	            lead.setC_Currency_ID(currencyID);
	            lead.setC_ConversionType_ID(currencyTypeID);
	            
	            // Save lead - first save to get ID
	            if (!lead.save(trxName)) {
	                FDialog.error(0, "ErrorSavingLead");
	                trx.rollback();
	                return;
	            }
	            
	            // Commit lead save to ensure we have an ID before calculating taxes
	            trx.commit();
	            
	            // Start new transaction for lead info items
	            trxName = Trx.createTrxName("LeadInfoSave");
	            trx = Trx.get(trxName, true);
	            
	            // Save lead info items
	            int lineNo = 0;
	            for (LeadInfoRow row : leadInfoRows) {
	                if (row.getProductEditor().getValue() == null && row.getChargeEditor().getValue() == null) {
	                    FDialog.error(0, "Please select either Product or Charge");
	                    trx.rollback();
	                    return;
	                }
	                
	                MLeadInfo leadInfo = new MLeadInfo(Env.getCtx(), 0, trxName);
	                leadInfo.setAD_Client_ID(lead.getAD_Client_ID());
	                leadInfo.setAD_Org_ID(lead.getAD_Org_ID());
	                leadInfo.setC_Lead_ID(lead.getC_Lead_ID());
	                leadInfo.setLine(lineNo + 10);
	                lineNo += 10;
	                
	                int C_UOM_ID = DB.getSQLValue(trxName, 
	                    "SELECT C_UOM_ID FROM C_UOM WHERE AD_Client_ID = ? AND IsDefault='Y' AND IsActive='Y'", 
	                    Env.getAD_Client_ID(Env.getCtx()));
	                
	                int C_Tax_ID = DB.getSQLValue(trxName, 
	                    "SELECT C_Tax_ID FROM C_Tax WHERE AD_Client_ID = ? AND IsDefault='Y' AND IsActive='Y'", 
	                    Env.getAD_Client_ID(Env.getCtx()));
	                
	                BigDecimal qty = row.getQtyEditor().getValue() != null ? 
	                    (BigDecimal)row.getQtyEditor().getValue() : BigDecimal.ZERO;
	                
	                if (row.getProductEditor().getValue() != null) {
	                    Integer productId = (Integer)row.getProductEditor().getValue();
	                    leadInfo.setM_Product_ID(productId != null ? productId : 0);
	                    
	                    MProduct product = MProduct.get(Env.getCtx(), productId);
	                    
	                    leadInfo.setC_UOM_ID(product.getC_UOM_ID() > 0 ? product.getC_UOM_ID() : C_UOM_ID);
	                    
	                    int M_PriceList_Version_ID = DB.getSQLValue(trxName, 
	                        "SELECT MAX(M_PriceList_Version_ID) FROM M_PriceList_Version " +
	                        "WHERE IsActive = 'Y' AND M_PriceList_ID = ?", 
	                        (Integer)priceListEditor.getValue());
	                    
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
	                    
	                    int StdPrecision = MPriceList.getStandardPrecision(Env.getCtx(), (Integer)priceListEditor.getValue());
	                    BigDecimal PricePlanned = PriceStd.multiply(BaseQty.divide(qty))
	                        .setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
	                    
	                    leadInfo.setPlannedPrice(PricePlanned);
	                    BigDecimal LineNetAmt = BaseQty.multiply(PricePlanned)
	                        .setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
	                    
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
	                    
	                    // Get tax ID based on state - now that lead is saved and committed
	                    int taxID = UtilTax.getTaxIDBasedOnState(MLead.Table_ID, lead.getC_Lead_ID(), productId, LineNetAmt, PricePlanned);
	                    leadInfo.setC_Tax_ID(taxID);
	                    
	                    BigDecimal taxRate = DB.getSQLValueBD(trxName, 
	                        "SELECT Rate FROM C_Tax WHERE C_Tax_ID = ?", taxID);
	                    
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
	     
	            trx.commit();
	            currentLeadId = lead.getC_Lead_ID();
	            Created = true;
	            FDialog.info(0, null, "LeadSavedSuccessfully" + "  Lead No " + lead.getDocumentNo());
	            
	        } catch (Exception e) {
	            if (trx != null) {
	                trx.rollback();
	            }
	            FDialog.error(0, "ErrorSavingData", e.getLocalizedMessage());
	            log.log(Level.SEVERE, "Error saving lead", e);
	        } finally {
	            if (trx != null) {
	                trx.close();
	            }
	        }
	    }
    
    @Override
    public void valueChange(ValueChangeEvent evt) {
        // Handle value change events if needed
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getTarget() == saveButton) {        	
            saveLead();
            if(Created)
            {
            	Events.postEvent("onLeadSaved", this, null);
            	clearForm();
            	this.dispose();
            }            
        } else if (event.getTarget() == cancelButton) {
            clearForm();
            this.dispose();
        } else if (event.getTarget() == addLineButton) {
            addLeadInfoRow();
        }
    }
    
    // Inner LeadInfoRow class
    private class LeadInfoRow {
        private int leadInfoId = 0;
        private int line;
        private int productId = 0;
        private int chargeId = 0;
        private BigDecimal quantity = BigDecimal.ONE;
        
        // Editor references
        private transient WSearchEditor productEditor;
        private transient WTableDirEditor chargeEditor;
        private transient WNumberEditor qtyEditor;
        
        public LeadInfoRow(int line) {
            this.line = line;
        }
        
        // Getters and setters
        public int getLeadInfoId() { return leadInfoId; }
        public void setLeadInfoId(int leadInfoId) { this.leadInfoId = leadInfoId; }
        public int getLine() { return line; }
        public void setLine(int line) { this.line = line; }
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getChargeId() { return chargeId; }
        public void setChargeId(int chargeId) { this.chargeId = chargeId; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public WSearchEditor getProductEditor() { return productEditor; }
        public void setProductEditor(WSearchEditor editor) { this.productEditor = editor; }
        public WTableDirEditor getChargeEditor() { return chargeEditor; }
        public void setChargeEditor(WTableDirEditor editor) { this.chargeEditor = editor; }
        public WNumberEditor getQtyEditor() { return qtyEditor; }
        public void setQtyEditor(WNumberEditor editor) { this.qtyEditor = editor; }
    }
}