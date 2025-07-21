package org.cyprus.webui.apps.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.cyprus.webui.apps.AEnv;
import org.cyprus.webui.component.Grid;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.ListHead;
import org.cyprus.webui.component.ListboxFactory;
import org.cyprus.webui.component.Panel;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.component.Rows;
import org.cyprus.webui.component.WListbox;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.CustomForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MQuery;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class WBPTransactionDetails extends CustomForm implements IFormController {
    private static final long serialVersionUID = 1L;
    private static CLogger log = CLogger.getCLogger(WBPTransactionDetails.class);

    private Panel mainPanel = new Panel();
    private Vbox outerLayout = new Vbox();  // Added outer layout for header
    private Panel headerPanel = new Panel(); // Added header panel
    private Hbox mainLayout = new Hbox();
    private Vbox leftPanel = new Vbox();
    private Vbox rightPanel = new Vbox();
    private Panel upperSection = new Panel();
    private Panel lowerSection = new Panel();
    private WListbox dataTable = ListboxFactory.newDataTable();
    private WListbox transactionTable = ListboxFactory.newDataTable();

    public WBPTransactionDetails() {
        super();
        try {
            dynInit();
            zkInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void zkInit() throws Exception {
        mainPanel.setWidth("100%");
        mainPanel.setHeight("100%");
        mainPanel.setStyle("padding: 0px;"); // Removed padding from main panel
        
        // Create outer layout to hold header and content
        outerLayout.setWidth("100%");
        outerLayout.setHeight("100%");
        
     // Configure header panel (15% height)
        headerPanel.setWidth("100%");
        headerPanel.setHeight("40px");
        headerPanel.setStyle("border-bottom: 2px solid #ddd; background: #e9e9e9; display: flex; align-items: center; justify-content: left;");
        
        // Add centered title to header
        Label titleLabel = new Label("Customer Transaction Details");
        titleLabel.setStyle("font-size: 18px; font-weight: bold; text-align: left;");
        headerPanel.appendChild(titleLabel);
        
        // Configure main content layout (85% height)
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setStyle("border: 1px solid #ddd;");
        
        // Configure left panel (30% width)
        leftPanel.setWidth("180px");
        leftPanel.setHeight("540px");
//        leftPanel.setWidth("30%");
//        leftPanel.setHeight("100%");
        leftPanel.setStyle("border-right: 1px solid #ddd; padding: 10px; background: #f5f5f5;");
        // Configure right panel (70% width)
        rightPanel.setWidth("640px");
        rightPanel.setHeight("540px");
        rightPanel.setStyle("padding: 10px;");
        
        // Configure upper section (40% height)
        upperSection.setWidth("100%");
        upperSection.setHeight("50%");
        upperSection.setStyle("border-bottom: 1px solid #ddd; padding: 10px;");
        
        // Configure lower section (60% height)
        //lowerSection.setWidth("100%");
        //lowerSection.setHeight("50%");
        
        transactionTable.setHeight("400px");
        transactionTable.setWidth("800px"); 
        lowerSection.setStyle("padding: 10px;");
        
        // Build the layout hierarchy
        Vbox rightContent = new Vbox();
        rightContent.setWidth("100%");
        rightContent.setHeight("100%");
        rightContent.appendChild(upperSection);
        rightContent.appendChild(new Separator());
        rightContent.appendChild(new Separator());
        rightContent.appendChild(lowerSection);
        rightContent.setWidth("70%");
        rightContent.setHeight("560px");
        rightPanel.appendChild(rightContent);
        
        
        prepareLeftPanelTable(dataTable); // Business Partner Left Section
        leftPanel.appendChild(dataTable);
        
        //prepareRightUpperPanelTable()
        
        mainLayout.appendChild(leftPanel);
        mainLayout.appendChild(rightPanel);
        
        // Add header and main content to outer layout
        outerLayout.appendChild(headerPanel);
        outerLayout.appendChild(mainLayout);
        
        mainPanel.appendChild(outerLayout);
        this.appendChild(mainPanel);
        
        // Add sample content
     //   createCustomerInformation();
        loadCustomerData();
        createTransactionDetails(); // In this section include Upper and Lower section details
    }
    
    private void loadCustomerData() 
    {
    	
    	String sql = "SELECT cb.Value, cb.Name, cb.C_BPartner_ID "+
                " FROM C_BPartner cb " +
                " WHERE cb.IsCustomer = 'Y' AND cb.Ad_Client_ID=? "+
                " ORDER BY cb.Created DESC";
    	
    	PreparedStatement pstmt = null;
        ResultSet rs = null;
        dataTable.setRowCount(0);
        List<BusinessPartnerDetails> bpDatas = new ArrayList<BusinessPartnerDetails>();
        
        try {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
            rs = pstmt.executeQuery();
            
            int row = 0;
            while (rs.next()) {
                BusinessPartnerDetails smd = new BusinessPartnerDetails();
                
                smd.setRow(row);
                smd.setCbpartnerid(rs.getInt("C_BPartner_ID"));
                smd.setValue(rs.getString("Value"));
                smd.setName(rs.getString("Name"));
                bpDatas.add(smd);
                row++;
            }
        }catch (SQLException e) {
            log.log(Level.SEVERE, "Database error", e);
        } finally {
            DB.close(rs, pstmt);
        }
        
        // set data in table
        if(bpDatas!=null && bpDatas.size()>0)
        {
        	for(BusinessPartnerDetails data:bpDatas)
        	{
        		Integer row=data.getRow();
    			dataTable.setRowCount(row+1);
				KeyNamePair valueWithPrimaryKey=new KeyNamePair(data.getCbpartnerid(),data.getValue());
    	        dataTable.setValueAt(row+1, row, 0);
    	        dataTable.setValueAt(valueWithPrimaryKey, row, 1);
    	        dataTable.setValueAt(data.getName(), row, 2); 
        	}
        }
        
        dataTable.repaint();
    	
    	
	}

	private void prepareLeftPanelTable(WListbox table) {

            table.clear();
            ListHead listhead = new ListHead();
            listhead.setHeight("10px");
            listhead.appendChild(new Listheader("No"));
            listhead.appendChild(new Listheader("Value"));
            listhead.appendChild(new Listheader("Name"));
            
            table.appendChild(listhead);
            table.addColumn("No"); 
            table.addColumn("Value");
            table.addColumn("Name");
       
            table.setColumnClass(0, IDColumn.class, true, "No");
            table.setColumnClass(1, KeyNamePair.class, true, "Value");
            table.setColumnClass(2, String.class, true, "Name");
            
            ((Listheader) table.getListHead().getChildren().get(0)).setWidth("30px");  // IDColumn field

            table.setFixedLayout(false);
            table.setHeight("510px");
            table.setWidth("180px"); 
            table.setStyle("overflow:auto; border:1px solid #ccc;");
            table.setName("Business Partner");
            table.autoSize();
            
            dataTable.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) throws Exception {
                    int selectedRow = dataTable.getSelectedRow();
                    if (selectedRow >= 0) {                   	
                        int recordID = getRecordIDFromRow(selectedRow);
                        if (recordID > 0 ) 
                        {
                        	MQuery zoomQuery = new MQuery();
                     		zoomQuery.addRestriction(MBPartner.COLUMNNAME_C_BPartner_ID, MQuery.EQUAL, recordID);
                     		zoomQuery.setRecordCount(1);	//	guess                 		
                     		int AD_Window_ID = 123; // Business Partner window ID
                         	AEnv.zoom(AD_Window_ID, zoomQuery);                        
                        }                   
                    }                
                }
            });
            
            
	}

	private void createCustomerInformation() {
        // Left Panel - Customer Information using Grid for 2-column layout
        Grid customerGrid = new Grid();
        customerGrid.setWidth("100%");
        customerGrid.setStyle("border: none;");
        
        Rows rows = new Rows();
        customerGrid.appendChild(rows);
        
        // Add customer details in 2-column grid format
        addGridRow(rows, "Customer Name", "Addi Nicolus");
        addGridRow(rows, "Name", "Addi Nicolus");
        addGridRow(rows, "Address", "5/154, Vikas Khand");
        addGridRow(rows, "Email", "mkvishwakarma@gmail.com");
        addGridRow(rows, "Customer Group", "Regular");
        addGridRow(rows, "Total Balance", "33432");
        addGridRow(rows, "Currency", "USD");
        
        leftPanel.appendChild(customerGrid);
    }
    
    private void createTransactionDetails() {
        // Upper Section - Transaction Summary using Grid for consistency
    	Row row = new Row();
        row.setStyle("padding: 8px 0;");
        
        // Type
        Hbox typeBox = new Hbox();
        typeBox.appendChild(new Label("Type:sdfdf-"));
        typeBox.appendChild(new Space());
        typeBox.appendChild(new Label("Invoice"));
        typeBox.setStyle("min-width: 200px; display: inline-block;");
        
        // Document No
        Hbox docNoBox = new Hbox();
        docNoBox.appendChild(new Label("Document No:"));
        docNoBox.appendChild(new Space());
        docNoBox.appendChild(new Label("INV-1001"));
        docNoBox.setStyle("min-width: 250px; display: inline-block;");
        
        // Paid Amount
        Hbox paidBox = new Hbox();
        paidBox.appendChild(new Label("Paid Amount:"));
        paidBox.appendChild(new Space());
        paidBox.appendChild(new Label("$1,200.00"));
        paidBox.setStyle("min-width: 200px; display: inline-block; color: green;");
        
        row.appendChild(typeBox);
        row.appendChild(docNoBox);
        row.appendChild(paidBox);
        upperSection.appendChild(row);
                
        
       
        
        
        
        
        
        
        
        
        
        
        
        // Lower Section - Transaction Details
        Panel transactionDetails = new Panel();
        transactionDetails.setStyle("overflow: auto; height: 90%;");
        transactionDetails.appendChild(new Label("Transaction line details would appear here"));
        
        //transactionTable
        
        lowerSection.appendChild(transactionTable);
        prepareLoowerSectionData(transactionTable);
        
        
    }
    
    private void prepareLoowerSectionData(WListbox transactionTable) {
		
    	
    	transactionTable.clear();
         ListHead listhead = new ListHead();
         listhead.setHeight("10px");
         listhead.appendChild(new Listheader("No"));
         listhead.appendChild(new Listheader("Type"));
         listhead.appendChild(new Listheader("DocumentNo"));
         listhead.appendChild(new Listheader("DocType"));
         listhead.appendChild(new Listheader("GrandTotal"));
         listhead.appendChild(new Listheader("PaidAmount"));
         listhead.appendChild(new Listheader("BalanceAmount"));
         listhead.appendChild(new Listheader("DocStatus"));

         transactionTable.appendChild(listhead);
         transactionTable.addColumn("No"); 
         transactionTable.addColumn("Type");
         transactionTable.addColumn("DocumentNo");
         transactionTable.addColumn("DocType"); 
         transactionTable.addColumn("GrandTotal");
         transactionTable.addColumn("PaidAmount");
         transactionTable.addColumn("BalanceAmount"); 
         transactionTable.addColumn("DocStatus");
    
         transactionTable.setColumnClass(0, IDColumn.class, true, "No");
         transactionTable.setColumnClass(1, String.class, true, "Type");
         transactionTable.setColumnClass(2, KeyNamePair.class, true, "DocumentNo");
         transactionTable.setColumnClass(3, String.class, true, "DocType");
         transactionTable.setColumnClass(0, String.class, true, "GrandTotal");
         transactionTable.setColumnClass(1, BigDecimal.class, true, "PaidAmount");
         transactionTable.setColumnClass(2, BigDecimal.class, true, "BalanceAmount");
         transactionTable.setColumnClass(3, String.class, true, "DocStatus");
         
         ((Listheader) transactionTable.getListHead().getChildren().get(0)).setWidth("30px");  // IDColumn field

         transactionTable.setFixedLayout(true);
         transactionTable.setHeight("400px");
         transactionTable.setWidth("800px"); 
         transactionTable.setStyle("overflow:auto; border:1px solid #ccc;");
         transactionTable.setName("Transaction Window");
         transactionTable.autoSize();
         
        
         /**  //Later implemet it
         transactionTable.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
             @Override
             public void onEvent(Event event) throws Exception {
                 int selectedRow = dataTable.getSelectedRow();
                 if (selectedRow >= 0) {                   	
                     int recordID = getRecordIDFromRow(selectedRow);
                     if (recordID > 0 ) 
                     {
                     	MQuery zoomQuery = new MQuery();
                  		zoomQuery.addRestriction(MBPartner.COLUMNNAME_C_BPartner_ID, MQuery.EQUAL, recordID);
                  		zoomQuery.setRecordCount(1);	//	guess                 		
                  		int AD_Window_ID = 123; // Business Partner window ID
                      	AEnv.zoom(AD_Window_ID, zoomQuery);                        
                     }                   
                 }                
             }
         });
    	**/
    	
		
	}

	private void addGridRow(Rows rows, String label, String value) {
        Row row = new Row();
        row.setStyle("margin-bottom: 5px;");
        
        // Label column (45% width)
        Label labelComp = new Label(label + ":");
        labelComp.setStyle("font-weight: bold; width: 45%; display: inline-block;");
        row.appendChild(labelComp);
        
        // Value column (55% width)
        Label valueComp = new Label(value);
        valueComp.setStyle("width: 55%; display: inline-block;");
        row.appendChild(valueComp);
        
        rows.appendChild(row);
    }
    
    private void dynInit() throws Exception {
        // Initialize any dynamic components or data here
    }
    
    @Override
    public ADForm getForm() {
        return this;
    }
    
/// Action on grid Double Click==============================
    
    
    
    
    
    
    private int getRecordIDFromRow(int row) {
        // Example: assuming your grid model stores record IDs
    	
    	KeyNamePair docId=(KeyNamePair)dataTable.getModel().getValueAt(row, 1);
    	return docId.getKey();
    }
    
    
    
    /// Serializable class 
    
    public class BusinessPartnerDetails implements Serializable
    {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private Integer row;
		private String value ;
		private Integer cbpartnerid;
		private String name;
		public Integer getRow() {
			return row;
		}
		public void setRow(Integer row) {
			this.row = row;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public Integer getCbpartnerid() {
			return cbpartnerid;
		}
		public void setCbpartnerid(Integer cbpartnerid) {
			this.cbpartnerid = cbpartnerid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
    }
    
    
}