package org.cyprus.webui.apps.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.cyprus.crm.model.MLead;
import org.cyprus.crm.model.MSalesOpportunity;
import org.cyprus.util.SearchUtility;
import org.cyprus.webui.apps.AEnv;
import org.cyprus.webui.component.Button;
import org.cyprus.webui.component.Combobox;
import org.cyprus.webui.component.Grid;
import org.cyprus.webui.component.GridFactory;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.ListHead;
import org.cyprus.webui.component.ListboxFactory;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.component.Rows;
import org.cyprus.webui.component.Textbox;
import org.cyprus.webui.component.WListbox;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.panel.CustomForm;
import org.cyprus.webui.window.FDialog;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MQuery;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.model.MClient;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.EMail;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
//import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
//import org.zkoss.zul.Listitem;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

public class WCRMSalesManagement extends CustomForm implements EventListener, ValueChangeListener {

    private static final long serialVersionUID = 1L;
    private static CLogger log = CLogger.getCLogger(WCRMSalesManagement.class);
    private static String trxName = Trx.createTrxName("CreateWCRMSalesManagement");
    
    // UI Components
    private Combobox orgCombo = new Combobox();
    private Combobox mailTempCombo = new Combobox();
    private WDateEditor dateFrom = new WDateEditor("DateFrom", false, false, true, "DateFrom");
    private WDateEditor dateTo = new WDateEditor("DateTo", false, false, true, "DateTo");
    private Textbox searchField = new Textbox();
    private Button searchButton = new Button();
    private Button leadsBtn = new Button();
    private Button opportunityBtn = new Button();
    private Button quotationBtn = new Button();
    private Button orderBtn = new Button();
    private Button invoiceBtn = new Button();
    private Button createLeadBtn = new Button();
    Div customBtn = null;
    private WListbox dataTable = ListboxFactory.newDataTable();
    private Button sendEmailBtn = new Button(Msg.getMsg(Env.getCtx(), "SendEmail"));
    private Button okBtn = new Button(Msg.getMsg(Env.getCtx(), "GenerateRecord"));
    
    // State variables
    private boolean leadButtonClicked = false;
    private boolean opportButtonClicked = false;
    private boolean quorationButtonClicked = false;
    private boolean orderButtonClicked = false;
    private boolean invoiceButtonClicked = false;
    private boolean selected = false;
    
    private Button firstBtn = new Button("<<");
    private Button prevBtn = new Button("<");
    private Label pageLabel = new Label("Page 1");
    private Button nextBtn = new Button(">");
    private Button lastBtn = new Button(">>");
    private int currentPage = 1;
    private int pageSize = 50; // Records per page
    private int totalRecords = 0;

    public WCRMSalesManagement() {
        super();
        try {
            init();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Initialization error", e);
        }
    }

    private void init() throws Exception {
        this.setWidth("100%");
        this.setHeight("100%");
        this.setStyle("padding: 10px;");

        Vbox mainLayout = new Vbox();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        this.appendChild(mainLayout);

        Label title = new Label(Msg.getMsg(Env.getCtx(), "CRMSalesManagement"));
        title.setStyle("font-size: 16px; font-weight: bold; color: #1E90FF; padding-bottom: 10px;");
        mainLayout.appendChild(title);

        mainLayout.appendChild(createFilterPanel());
        mainLayout.appendChild(createStatusButtons());
        mainLayout.appendChild(new Separator());

        prepareTable(dataTable);
        mainLayout.appendChild(dataTable);
        
        dataTable.addEventListener(Events.ON_SELECT, new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
            	updateSelectedCountLabel();            	
                okBtn.setDisabled(shouldDisableGenerateButton());
                
            }
        });

        
        Hbox buttonBox = createActionButtons();
        buttonBox.setParent(mainLayout);
        

        leadButtonClicked = true;
        loadLeadData();
    }
    

    
    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil((double)totalRecords/pageSize);
        totalPages = Math.max(totalPages, 1); // Ensure at least 1 page
        
        pageLabel.setValue("Page " + currentPage + " of " + totalPages);
        
        firstBtn.setDisabled(currentPage == 1);
        prevBtn.setDisabled(currentPage == 1);
        nextBtn.setDisabled(currentPage >= totalPages);
        lastBtn.setDisabled(currentPage >= totalPages);
        
        // Force UI refresh
        Events.echoEvent("onPaginationUpdate", this, null);
    }
    
    private Grid createFilterPanel() {
        Grid filterGrid = GridFactory.newGridLayout();
        filterGrid.setWidth("100%");
        
        Rows filterRows = filterGrid.newRows();
        Row filterRow = filterRows.newRow();

        filterRow.appendChild(new Label(Msg.translate(Env.getCtx(), "AD_Org_ID")));
        loadOrganizations();
        filterRow.appendChild(orgCombo);
        filterRow.setWidth("50%");
        orgCombo.addEventListener(Events.ON_SELECT, this);

        // Enhanced search field with loading indicator
        Hbox searchBox = new Hbox();
        searchField.setAttribute("placeholder", Msg.getMsg(Env.getCtx(), "Search"));
        searchBox.appendChild(new Label(Msg.translate(Env.getCtx(), "Search")));
        searchBox.appendChild(searchField);
        
        // Add loading indicator (initially hidden)
        Image loadingIcon = new Image("/images/loading.gif");
        loadingIcon.setVisible(false);
        loadingIcon.setId("searchLoadingIcon");
        searchBox.appendChild(loadingIcon);
        
        filterRow.appendChild(searchBox);
        
        // Set up search events
       searchField.addEventListener(Events.ON_CHANGING, this);
//        searchField.addEventListener("onDelayedSearch", this);
        
        searchField.addEventListener(Events.ON_OK, this);
        
        filterRow.appendChild(new Label(Msg.translate(Env.getCtx(), "DateFrom")));
        dateFrom.addValueChangeListener(this); 
        filterRow.appendChild(dateFrom.getComponent());

        filterRow.appendChild(new Label(Msg.translate(Env.getCtx(), "DateTo")));
        dateTo.addValueChangeListener(this); 
        filterRow.appendChild(dateTo.getComponent());

        searchButton.setImage("/images/Find24.png");
        searchButton.setTooltiptext(Msg.getMsg(Env.getCtx(), "Search"));
        searchButton.addEventListener(Events.ON_CLICK, this);
        
        dataTable.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow >= 0) {
                	
                    int recordID = getRecordIDFromRow(selectedRow);
                    String docType = getDocumentTypeFromRow(selectedRow);
                    int AD_Table_ID = getTableIDForDocType(docType);
                    if (AD_Table_ID > 0 && recordID > 0 && !docType.equalsIgnoreCase("Quotation")) {
                        AEnv.zoom(AD_Table_ID, recordID);
                    }
                    else if (recordID > 0)
                    {
                    	MQuery zoomQuery = new MQuery();
                 		zoomQuery.addRestriction(MOrder.COLUMNNAME_C_Order_ID, MQuery.EQUAL, recordID);
                 		zoomQuery.setRecordCount(1);	//	guess                 		
                 		int AD_Window_ID = 1000006; // Quotation window ID
                     	AEnv.zoom(AD_Window_ID, zoomQuery);                        
                    }                   
                }                
            }
        });

        return filterGrid;
    }


    
    @Override
    public void onEvent(Event event) throws Exception {
        if (event.getTarget() == searchField && 
            (Events.ON_CHANGING.equals(event.getName()) || Events.ON_OK.equals(event.getName()))) {
        	executeNewSearch();
        	updateSelectedCountLabel();
        	selected = false;
        	customBtn.setVisible(true);
        } 
        else if (event.getTarget() == searchButton) {
            currentPage = 1; // Reset to first page on new search
            refreshStatusCounts();
            executeNewSearch();
            updateSelectedCountLabel();
            selected = false;
            customBtn.setVisible(true);
        } else if (event.getTarget() == leadsBtn) {
            currentPage = 1;
            setActiveButton(true, false, false, false, false);
            loadLeadData();
            updateSelectedCountLabel();
            selected = false;
            customBtn.setVisible(true);
        } else if (event.getTarget() == opportunityBtn) {
            currentPage = 1;
            setActiveButton(false, true, false, false, false);
            loadOpportunityData();
            updateSelectedCountLabel();
            selected = false;
            customBtn.setVisible(true);
        } else if (event.getTarget() == quotationBtn) {
            currentPage = 1;
            setActiveButton(false, false, true, false, false);
            loadOrderData("Quotation");
            updateSelectedCountLabel(); 
            selected = false;
            customBtn.setVisible(true);
        } else if (event.getTarget() == orderBtn) {
            currentPage = 1;
            setActiveButton(false, false, false, true, false);
            loadOrderData("Order");
            updateSelectedCountLabel();
            selected = false;
            customBtn.setVisible(true);
        } else if (event.getTarget() == invoiceBtn) {
            currentPage = 1;
            setActiveButton(false, false, false, false, true);
            loadInvoiceData();
            updateSelectedCountLabel();
            selected = false;
            customBtn.setVisible(true);
        }else if (event.getTarget() == customBtn) {
        	createNewLead();
        	currentPage = 1;
        	refreshStatusCounts();      	
        	updateSelectedCountLabel();
//            currentPage = 1;
//            setActiveButton(false, false, false, false, true);
//            loadInvoiceData();
        }
        else if (event.getTarget() == sendEmailBtn) {
        	sendEmailToCustomer();
        	selected = false;
        	loadLeadData();
            // Handle send email
        } else if (event.getTarget() == okBtn) {
            createOKButtonProcess();         
        } else if (event.getTarget() == orgCombo) {
            currentPage = 1;
            refreshStatusCounts();
            executeNewSearch();
            updateSelectedCountLabel();
            selected = false;
        }
        if (event.getTarget() == firstBtn) {
            currentPage = 1;
            executeSearch();
            updateSelectedCountLabel();
            selected = false;
        } else if (event.getTarget() == prevBtn) {
            currentPage--;
            executeSearch();
            updateSelectedCountLabel();
            selected = false;
        } else if (event.getTarget() == nextBtn) {
            currentPage++;
            executeSearch();
            updateSelectedCountLabel();
            selected = false;
        } else if (event.getTarget() == lastBtn) {
            currentPage = (int)Math.ceil((double)totalRecords/pageSize);
            executeSearch();
            updateSelectedCountLabel();
            selected = false;
        }
    }

    private void sendEmailToCustomer() throws Exception {
		
    	List<SendMailData> sendMailDatas=null;//new LinkedList<SendMailData>();
    	
		ArrayList<KeyNamePair> results = new ArrayList<KeyNamePair>();
		
		// 1. Check if template is selected
        if (getSelectedMailTempId() <= 0) {
            FDialog.error(-1, this, "Please select an email template first!");
            return;
        }
		
		String typeName=null;
    	int rows = dataTable.getRowCount();

		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)dataTable.getValueAt(i, 0);     //  ID in column 0
			KeyNamePair productKeyName = (KeyNamePair)dataTable.getValueAt(i, 1);     //  ID in column 0			
			if (id != null && id.isSelected())
			{
				if(typeName==null)
					typeName=(String)dataTable.getValueAt(i, 2);
				results.add(productKeyName);
			}
		}
		 // 2. Check if records are selected
        if (results == null || results.size() == 0) {
            FDialog.error(-1, this, "No records selected for email!");
            return;
        }
        
		if (results.size()> 0 && typeName.equalsIgnoreCase("Lead"))
		{
			sendMailDatas =WCRMSalesManagementUtility.createLeadEmailData(Trx.get(trxName, true), results, getSelectedMailTempId());	
		}
		else
		{
			FDialog.info(-1, this, " Email Send only at Lead Window");
		}

		// 3. Process emails only if validations pass
        if (sendMailDatas == null || sendMailDatas.isEmpty()) {
            FDialog.error(-1, this, "No valid email data generated!");
            return;
        }
		
		if (sendMailDatas != null && !sendMailDatas.isEmpty()) {
		    MClient client = MClient.get(Env.getCtx());
		    StringBuilder statusMessage = new StringBuilder("Email Status:\n"); // To aggregate results

		    for (SendMailData sendMailData : sendMailDatas) {
		    	try {
		    		// 1. Split comma-separated emails into an array
		    		String[] recipients = sendMailData.getEmailsTo().split("\\s*,\\s*");
		    		if (recipients.length == 0) {
		    			statusMessage.append("- No recipients specified.\n");
		    			continue;
		    		}

		    		// 2. Use the first email in the constructor
		    		EMail mail = client.createEMail(
		    				recipients[0],  // First email
		    				sendMailData.getEmailsHeader(),
		    				sendMailData.getEmailsBody(),
		    				sendMailData.getEmailIsHtml()
		    				);

		    		// 3. Add remaining emails via addTo()
		    		for (int i = 1; i < recipients.length; i++) {
		    			mail.addTo(recipients[i]);
		    		}
		    		
		    		// 4. Add CC/BCC
		            
		    		//mail.addCc("mkvishwakarma@gmail.com");
		    		mail.addBcc(sendMailData.getEmailsBcc());

		    		if (mail != null) {
		    			// 2. Send and capture status
		    			String result = mail.send();
		    			String recipient = sendMailData.getEmailsTo(); // Extract recipient(s)
		    			statusMessage.append("- Sent to: ").append(recipient)
		    			.append(" | Status: ").append(result).append("\n");
		    		}
		    	} catch (Exception e) {
		            // 3. Handle errors gracefully
		            statusMessage.append("- Failed to send to: ").append(sendMailData.getEmailsTo())
		                         .append(" | Error: ").append(e.getMessage()).append("\n");
		            log.severe("Email send failed: " + e.getMessage());
		        }
		    }

		    // 4. Show consolidated popup
		    FDialog.info(-1, this, statusMessage.toString());
		}
	}

	private void executeSearch() {
        try {
            if (leadButtonClicked) {
                loadLeadData();
            } else if (opportButtonClicked) {
                loadOpportunityData();
            } else if (quorationButtonClicked) {
                loadOrderData("Quotation");
            } else if (orderButtonClicked) {
                loadOrderData("Order");
            } else if (invoiceButtonClicked) {
                loadInvoiceData();
            }
        } finally {
            // Hide loading indicator
            Image loadingIcon = (Image) getFellow("searchLoadingIcon");
            loadingIcon.setVisible(false);
        }
    }
	
	private void executeNewSearch() {
        currentPage = 1; // Only reset for new searches
        executeSearch();
    }

    private void setActiveButton(boolean lead, boolean opport, boolean quotation, boolean order, boolean invoice) {
        leadButtonClicked = lead;
        opportButtonClicked = opport;
        quorationButtonClicked = quotation;
        orderButtonClicked = order;
        invoiceButtonClicked = invoice;
    }
    
//    private void loadLeadData() {
//        String searchText = searchField.getText();
//        Date fromDate = getFromDate();
//        Date toDate = getToDate();
//        
////        String Status = DB.getSQLValueString(null, "SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=? AND value=(SELECT Status From C_Lead)", 1000014);
//        
//        String searchCondition = SearchUtility.buildSearchCondition(
//                searchText, fromDate, toDate, "cl.enquirydate",
//                "cl.DocumentNo", "cl.companyname", "cl.contactname", "cl.phone", "cl.email", "cl.summary", "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=cl.Status)"
//            );
//         
//            totalRecords = getRecordCount("C_Lead");
//        
//        // First Column must always an Table ID    
//            String sql = "SELECT * FROM (" +
//        		"SELECT  cl.C_Lead_ID, cl.GrandTotal, cl.DocumentNo, cl.companyname, cl.contactname, " +
//            "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=cl.status) AS Status, " +
//            "cl.phone, cl.email, cl.salesrep_id, ad.name AS salesrep, cl.summary AS lineDesc, cl.enquirydate, " +
//            "ROW_NUMBER() OVER (ORDER BY " +
//            "CASE WHEN cl.status = '10' THEN 1 " +
//            "WHEN cl.status = '19' THEN 2 " +
//            "ELSE 3 END, cl.created DESC) AS rn " +
//            "FROM C_Lead cl " +
//            "LEFT OUTER JOIN ad_user ad ON (ad.ad_user_id = cl.salesrep_id) " +
//            "WHERE cl.AD_Client_ID = ? AND cl.AD_Org_ID = ? " +
//            searchCondition +
//           // " ORDER BY cl.enquirydate DESC";
////            "  ORDER BY "+
////            "  CASE WHEN cl.status = '10' THEN 1 "+
////            "  WHEN cl.status = '19' THEN 2 "+
////            "  ELSE 3 "+
////            "  END, "+
////            "  cl.created DESC ";  // Changed to DESC to show newest first"
//            ") numbered_results " +
//            "WHERE rn BETWEEN ? AND ?";
//            
//        int startRow = (currentPage - 1) * pageSize + 1;
//        int endRow = currentPage * pageSize;
//        
//        loadDataIntoTableWithRowNumbers(sql, "Lead", startRow, endRow);
//        
////        loadDataIntoTable(sql, "Lead");
//        updatePaginationControls();
//        dataTable.repaint();
//    }
    
    private void loadLeadData() {
        String searchText = searchField.getText();
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        
        int currentUserId = Env.getAD_User_ID(Env.getCtx());
        
        String searchCondition = SearchUtility.buildSearchCondition(
                searchText, fromDate, toDate, "cl.enquirydate",
                "cl.DocumentNo", "cl.companyname", "cl.contactname", "cl.phone", "cl.email", "cl.summary", 
                "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=cl.Status)"
            );
         
        totalRecords = getRecordCount("C_Lead");

        // Modified SQL to show current user's data first
        String sql = "SELECT * FROM (" +
            "SELECT  cl.C_Lead_ID, cl.GrandTotal, cl.DocumentNo, cl.companyname, cl.contactname, " +
            "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=cl.status) AS Status, " +
            "cl.phone, cl.email, cl.salesrep_id, ad.name AS salesrep, cl.summary AS lineDesc, cl.enquirydate, " +
            "ROW_NUMBER() OVER (ORDER BY " +
            "CASE WHEN cl.salesrep_id = " + currentUserId + " THEN 0 ELSE 1 END, "+ 
            "CASE WHEN cl.status = '10' THEN 1 " + // Then by status priority
            "WHEN cl.status = '19' THEN 2 " +
            "ELSE 3 END, cl.created DESC) AS rn " + // Then by creation date
            "FROM C_Lead cl " +
            "LEFT OUTER JOIN ad_user ad ON (ad.ad_user_id = cl.salesrep_id) " +
            "WHERE cl.AD_Client_ID = ? AND cl.AD_Org_ID = ? " +
            searchCondition +
            ") numbered_results " +
            "WHERE rn BETWEEN ? AND ?";
            
        int startRow = (currentPage - 1) * pageSize + 1;
        int endRow = currentPage * pageSize;
        
        loadDataIntoTableWithRowNumbers(sql, "Lead", startRow, endRow);
        updatePaginationControls();
        dataTable.repaint();
    }

    private void loadOpportunityData() {
        String searchText = searchField.getText();
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        
        		 String searchCondition = SearchUtility.buildSearchCondition(
        		            searchText, fromDate, toDate, "cl.OpportunityDate",
        		            "cl.DocumentNo", "bp.name", "ref_bp.name", "au.name", "ref_au.name", "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=cl.Status)"
        		        );
        
        totalRecords = getRecordCount("C_SalesOpportunity");
        
        int currentUserId = Env.getAD_User_ID(Env.getCtx());
        
        // First Column must always an Table ID    
        String sql = "SELECT * FROM (" +
        		"SELECT cl.C_SalesOpportunity_ID, cl.OpportunityDate, " +
            "cl.GrandTotal, " +
            "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=cl.status) AS status, " +            
            "COALESCE(bp.name, ref_bp.name) AS companyname, " +
            "COALESCE(au.name, ref_au.name) AS contactname, " +
            "COALESCE(au.phone, ref_au.phone) AS phone, " +
            "COALESCE(au.email, ref_au.email) AS email, " +
            "ad.name AS salesrep, " +
            "cl.DocumentNo, lead.DocumentNo AS LeadDoc, " +
			"fn_get_document_line_details('Opportunity',cl.C_SalesOpportunity_ID) AS lineDesc, "+            
			"ROW_NUMBER() OVER (ORDER BY " +
			"CASE WHEN cl.salesrep_id = " + currentUserId + " THEN 0 ELSE 1 END, "+ 
            "CASE WHEN cl.status = '24' THEN 1 " +
            "WHEN cl.status = '23' THEN 2 " +
            "ELSE 3 END, cl.created DESC) AS rn " +
            "FROM c_salesopportunity cl " +  
            "LEFT OUTER JOIN C_Lead lead ON (lead.C_Lead_id = cl.C_Lead_id) " +
            "LEFT OUTER JOIN ad_user ad ON (ad.ad_user_id = cl.salesrep_id) " +
            "LEFT OUTER JOIN c_bpartner bp ON (cl.c_bpartner_id = bp.c_bpartner_id) " +
            "LEFT OUTER JOIN c_bpartner ref_bp ON (cl.ref_bpartner_id = ref_bp.c_bpartner_id) " +   			
            "LEFT OUTER JOIN c_bpartner_location bpl ON (cl.c_bpartner_location_id = bpl.c_bpartner_location_id) " +
            "LEFT OUTER JOIN c_bpartner_location ref_bpl ON (cl.ref_bpartner_location_id = ref_bpl.c_bpartner_location_id) " +  			
            "LEFT OUTER JOIN ad_user au ON (cl.ad_user_id = au.ad_user_id) " +
            "LEFT OUTER JOIN ad_user ref_au ON (cl.ref_user_id = ref_au.ad_user_id) " +		
            "WHERE cl.AD_Client_ID = ? AND cl.AD_Org_ID = ? AND cl.isactive = 'Y' " +
//            searchCondition +
//           // " ORDER BY cl.OpportunityDate DESC";
//            "  ORDER BY "+
//            "  CASE WHEN cl.status = '24' THEN 1 "+
//            "  WHEN cl.status = '23' THEN 2 "+
//            "  ELSE 3 "+
//            "  END, "+
//            "  cl.created DESC ";  // Changed to DESC to show newest first"
			searchCondition +
			") numbered_results " +
			"WHERE rn BETWEEN ? AND ?";
			
			// Calculate row range
			int startRow = (currentPage - 1) * pageSize + 1;
			int endRow = currentPage * pageSize;
			
			loadDataIntoTableWithRowNumbers(sql, "Opportunity", startRow, endRow);
            
        
//        loadDataIntoTable(sql, "Opportunity");
        updatePaginationControls();
        dataTable.repaint();
    }

    private void loadOrderData(String type) {
        String searchText = searchField.getText();
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        
        String searchCondition = SearchUtility.buildSearchCondition(
                searchText, fromDate, toDate, "co.DateOrdered",
                "co.DocumentNo", "bp.name", "au.name", "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=co.docstatus)"
            );
        
        totalRecords = getRecordCount("C_Order");
        
        int currentUserId = Env.getAD_User_ID(Env.getCtx());
        
        String sqlApp = "Quotation".equalsIgnoreCase(type) ?
            "co.C_DocTypeTarget_ID IN (SELECT dt.C_DocType_ID FROM C_DocType dt WHERE dt.DocSubTypeSO IN ('ON','OB'))" :
            "co.C_DocTypeTarget_ID IN (SELECT dt.C_DocType_ID FROM C_DocType dt WHERE dt.DocSubTypeSO NOT IN ('ON','OB'))";

        // First Column must always an Table ID    
        String sql = "SELECT * FROM (" +
        		"SELECT co.C_Order_ID, co.DateOrdered, " +
            "co.DocumentNo, " +
            "co.GrandTotal, " +
            "bp.name AS companyname, " +
            "au.name AS contactname, " +
            "au.phone AS phone, " +
            "au.email AS email, " +
            "sr.name AS salesrep, " +
            "cl.DocumentNo AS salesOpportDoc, " +
			"quotOrder.DocumentNo AS salesQuotoDoc, "+
			"fn_get_document_line_details('Order',co.C_Order_ID) AS lineDesc, "+
            "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=131 AND value=co.docstatus) AS Status, " +            
            "ROW_NUMBER() OVER (ORDER BY " +
            "CASE WHEN co.salesrep_id = " + currentUserId + " THEN 0 ELSE 1 END, "+ 
            "CASE WHEN co.docstatus = 'DR' THEN 1 " +
            "WHEN co.docstatus = 'CO' THEN 2 " +
            "ELSE 3 END, co.created DESC) AS rn " +
            "FROM C_Order co " +
            "INNER JOIN c_bpartner bp ON co.c_bpartner_id = bp.c_bpartner_id " +
            "INNER JOIN c_bpartner_location bpl ON co.c_bpartner_location_id = bpl.c_bpartner_location_id " +
         // Ref_Order_ID
			"LEFT JOIN C_Order quotOrder ON co.Ref_Order_ID = quotOrder.C_Order_ID "+
            "LEFT JOIN c_salesopportunity cl ON co.C_Order_ID = cl.C_Order_ID " +
            "LEFT OUTER JOIN ad_user au ON co.ad_user_id = au.ad_user_id " +
            "LEFT JOIN ad_user sr ON co.salesrep_id = sr.ad_user_id " +
            "WHERE co.IsSOTrx='Y' AND " + sqlApp + " AND co.isActive='Y' " +
//            searchCondition +
//            " AND co.AD_Client_ID = ? AND co.AD_Org_ID = ? "+
//            //" ORDER BY co.DateOrdered DESC";
//            "  ORDER BY "+
//            "  CASE WHEN co.docstatus = 'DR' THEN 1 "+
//            "  WHEN co.docstatus = 'CO' THEN 2 "+
//            "  ELSE 3 "+
//            "  END, "+
//            "  co.created DESC ";  // Changed to DESC to show newest first"
			searchCondition +
			") numbered_results " +
			"WHERE rn BETWEEN ? AND ?";
			
			// Calculate row range
			int startRow = (currentPage - 1) * pageSize + 1;
			int endRow = currentPage * pageSize;
			
			loadDataIntoTableWithRowNumbers(sql, type, startRow, endRow);
                
//        loadDataIntoTable(sql, type);
        updatePaginationControls();
        dataTable.repaint();
    }

    private void loadInvoiceData() {
        String searchText = searchField.getText();
        Date fromDate = getFromDate();
        Date toDate = getToDate();
        
        String searchCondition = SearchUtility.buildSearchCondition(
                searchText, fromDate, toDate, "ci.DateInvoiced",
                "ci.DocumentNo", "bp.name", "au.name", "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=1000014 AND value=ci.docstatus)"
            );
        
        totalRecords = getRecordCount("C_Invoice");
        
        int currentUserId = Env.getAD_User_ID(Env.getCtx());
        
        // First Column must always an Table ID    
        String sql = "SELECT * FROM (" +
        		"SELECT ci.C_Invoice_ID, ci.DateInvoiced, " +
            "ci.DocumentNo, " +
            "ci.GrandTotal, " +
            "bp.name AS companyname, " +
            "au.name AS contactname, " +
            "au.phone AS phone, " +
            "au.email AS email, " +
            "sr.name AS salesrep, " +
            "co.DocumentNo AS RefDoc, " +
			"fn_get_document_line_details('Invoice',ci.C_Invoice_ID) AS lineDesc, "+
            "(SELECT name FROM AD_Ref_List WHERE AD_Reference_ID=131 AND value=ci.docstatus) AS status, " +
            "ROW_NUMBER() OVER (ORDER BY " +
            "CASE WHEN ci.salesrep_id = " + currentUserId + " THEN 0 ELSE 1 END, "+ 
            "CASE WHEN ci.docstatus = 'DR' THEN 1 " +
            "WHEN ci.docstatus = 'CO' THEN 2 " +
            "ELSE 3 END, ci.created DESC) AS rn " +
            "FROM C_Invoice ci " +
            "LEFT OUTER JOIN C_Order co ON co.C_Order_ID = ci.C_Order_ID " +
            "INNER JOIN c_bpartner bp ON ci.c_bpartner_id = bp.c_bpartner_id " +
            "INNER JOIN c_bpartner_location bpl ON ci.c_bpartner_location_id = bpl.c_bpartner_location_id " +
            "LEFT OUTER JOIN ad_user au ON ci.ad_user_id = au.ad_user_id " +
            "LEFT JOIN ad_user sr ON ci.salesrep_id = sr.ad_user_id " +
            "WHERE ci.IsSOTrx='Y' " +
//            + searchCondition +
//            " AND ci.ad_client_id = ? AND ci.ad_org_id = ? " +
//         //   "ORDER BY ci.Created DESC";           
//			//" ORDER BY co.DateOrdered DESC";
//			"  ORDER BY "+
//			"  CASE WHEN ci.docstatus = 'DR' THEN 1 "+
//			"  WHEN ci.docstatus = 'CO' THEN 2 "+
//			"  ELSE 3 "+
//			"  END, "+
//			"  ci.created DESC ";  // Changed to DESC to show newest first"
			searchCondition +
			") numbered_results " +
			"WHERE rn BETWEEN ? AND ?";
			
			// Calculate row range
			int startRow = (currentPage - 1) * pageSize + 1;
			int endRow = currentPage * pageSize;
			
			loadDataIntoTableWithRowNumbers(sql, "Invoice", startRow, endRow);
        
//        loadDataIntoTable(sql, "Invoice");
        updatePaginationControls();
        dataTable.repaint();
    }

    private void loadDataIntoTable(String sql, String type) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        dataTable.setRowCount(0);
        List<SalesManagementData> salesDatas = new ArrayList<>();
        
        try {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
            pstmt.setInt(2, getSelectedOrgId());
            rs = pstmt.executeQuery();
            
            int row = 0;
            while (rs.next()) {
                SalesManagementData smd = new SalesManagementData();
                IDColumn id = new IDColumn(rs.getInt(1)); // First column is always ID
                
                smd.setRow(row);
                smd.setIdColumn(id);
                smd.setPrimaryKey(rs.getInt(1));
                smd.setType(type);
                
                // Set common fields
                smd.setDocumentNo(rs.getString("DocumentNo"));
                smd.setCompanyName(rs.getString("companyname"));
                smd.setContactName(rs.getString("contactname"));
                smd.setPhone(rs.getString("phone"));
                smd.setEmail(rs.getString("email"));
                smd.setSalesRep(rs.getString("salesrep"));
                smd.setTotalLines(rs.getBigDecimal("GrandTotal"));  // Changed From Total Lines to GrandTotal by Mukesh @20250516
                smd.setStatus(rs.getString("Status"));
                smd.setDescription(rs.getString("lineDesc"));

                // Set type-specific fields
                if ("Lead".equals(type)) {
                   // smd.setDescription(rs.getString("summary"));
                    smd.setDate(rs.getTimestamp("enquirydate"));
                } else if ("Opportunity".equals(type)) {
                    smd.setRefDoc(rs.getString("LeadDoc"));
                    smd.setDate(rs.getTimestamp("OpportunityDate"));
                } else if ("Quotation".equals(type) || "Order".equals(type)) {
                	                	
                    smd.setRefDoc(rs.getString("salesOpportDoc"));
                    smd.setDate(rs.getTimestamp("DateOrdered")); 
                    if("Quotation".equalsIgnoreCase(type))                  
                    smd.setRefDoc(rs.getString("salesOpportDoc"));
                    else
                    smd.setRefDoc(rs.getString("salesQuotoDoc"));
                                      
                } else if ("Invoice".equals(type)) {
                    smd.setRefDoc(rs.getString("RefDoc"));
                    smd.setDate(rs.getTimestamp("DateInvoiced"));
                }
                
                salesDatas.add(smd);
                row++;
            }
            
            updateDatasInRows(salesDatas);
            refreshStatusCounts();
            
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Database error", e);
        } finally {
            DB.close(rs, pstmt);
        }
        
        dataTable.repaint();
    }
    
    private void loadDataIntoTableWithRowNumbers(String sql, String type, int startRow, int endRow) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        dataTable.setRowCount(0);
        List<SalesManagementData> salesDatas = new ArrayList<>();
        
        try {
            log.fine("Executing paginated query: " + sql);
            pstmt = DB.prepareStatement(sql, null);
            if ("Quotation".equals(type) || "Order".equals(type) || "Invoice".equals(type))
            {
            	pstmt.setInt(1, startRow);
                pstmt.setInt(2, endRow);
            }
            else
            {
            	pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
                pstmt.setInt(2, getSelectedOrgId());
                pstmt.setInt(3, startRow);
                pstmt.setInt(4, endRow);
            }
            
            
            rs = pstmt.executeQuery();
            
            int row = 0;
            while (rs.next()) {
            	SalesManagementData smd = new SalesManagementData();
                IDColumn id = new IDColumn(rs.getInt(1)); // First column is always ID
                
                smd.setRow(row);
                smd.setIdColumn(id);
                smd.setPrimaryKey(rs.getInt(1));
                smd.setType(type);
                
                // Set common fields
                smd.setDocumentNo(rs.getString("DocumentNo"));
                smd.setCompanyName(rs.getString("companyname"));
                smd.setContactName(rs.getString("contactname"));
                smd.setPhone(rs.getString("phone"));
                smd.setEmail(rs.getString("email"));
                smd.setSalesRep(rs.getString("salesrep"));
                smd.setTotalLines(rs.getBigDecimal("GrandTotal"));  // Changed From Total Lines to GrandTotal by Mukesh @20250516
                smd.setStatus(rs.getString("Status"));
                smd.setDescription(rs.getString("lineDesc"));

                // Set type-specific fields
                if ("Lead".equals(type)) {
                   // smd.setDescription(rs.getString("summary"));
                    smd.setDate(rs.getTimestamp("enquirydate"));
                } else if ("Opportunity".equals(type)) {
                    smd.setRefDoc(rs.getString("LeadDoc"));
                    smd.setDate(rs.getTimestamp("OpportunityDate"));
                } else if ("Quotation".equals(type) || "Order".equals(type)) {
                	                	
                    smd.setRefDoc(rs.getString("salesOpportDoc"));
                    smd.setDate(rs.getTimestamp("DateOrdered")); 
                    if("Quotation".equalsIgnoreCase(type))                  
                    smd.setRefDoc(rs.getString("salesOpportDoc"));
                    else
                    smd.setRefDoc(rs.getString("salesQuotoDoc"));
                                      
                } else if ("Invoice".equals(type)) {
                    smd.setRefDoc(rs.getString("RefDoc"));
                    smd.setDate(rs.getTimestamp("DateInvoiced"));
                }
                salesDatas.add(smd);
                row++;
            }
            
            updateDatasInRows(salesDatas);
            
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Database error in paginated query", e);
            FDialog.error(0, "Database Error", "Error loading data: " + e.getMessage());
        } finally {
            DB.close(rs, pstmt);
        }
    }
    
    private Hbox createStatusButtons() {
        Hbox statusBox = new Hbox();
        statusBox.setWidth("100%");
        statusBox.setStyle("margin: 10px 0; text-align: center");

        refreshStatusCounts();

        statusBox.appendChild(leadsBtn);
        statusBox.appendChild(opportunityBtn);
        statusBox.appendChild(quotationBtn);
        statusBox.appendChild(orderBtn);
        statusBox.appendChild(invoiceBtn);
        
//        statusBox.appendChild(createLeadBtn);
         customBtn = new Div();
        customBtn.setStyle(
            "width: 60px; height: 60px; " +
            "background: #FFEE93 url('images/Plus_1.png') no-repeat center/contain; " +
            "border: 1px solid black; cursor: pointer;"
        );
        customBtn.addEventListener(Events.ON_CLICK, this); // Same event handler
        statusBox.appendChild(customBtn); // Replace parentComponent
        return statusBox;
    }
    
   
    
    private Hbox createActionButtons() {
        Hbox actionBox = new Hbox();
//      actionBox.setWidth("100%");
//      actionBox.setPack("end"); 
////      actionBox.setStyle("padding: 10px; background: #f5f5f5; border-top: 1px solid #ddd;"
////          + "position: absolute; bottom: 0; left: 0; right: 0;");
//      actionBox.setStyle("display: flex !important; justify-content: flex-end !important; gap: 5px !important;");
      actionBox.setStyle("display: flex !important;" + 
              "justify-content: flex-end !important;" +
              "gap: 5px !important;" + 
              "padding: 10px;" +
              "background: #f5f5f5;" +
              "border-top: 1px solid #ddd;" +
              "position: absolute;" +
              "bottom: 0;" +
              "left: 0;" +
              "right: 0;");
      sendEmailBtn.setLabel("Send Email"); 
      sendEmailBtn.setVisible(true);
      okBtn.setLabel(Msg.getMsg(Env.getCtx(), "GenerateRecord"));
      okBtn.setVisible(true);
      
      Label selectedCountLabel = createSelectedCountLabel();
 
      String pageButtonStyle = "height: 20px; min-width: 50px; padding: 0 5px; font-size: 14px;";
      String pageButtonRightStyle = "display: flex !important; height: 20px; min-width: 50px; margin-right: 15 rem ; padding: 0 5px; font-size: 14px;";
      String buttonStyle = "height: 20px; min-width: 100px; padding: 0 12px; font-size: 14px;";
      sendEmailBtn.setStyle(buttonStyle);
      okBtn.setStyle(buttonStyle);
      sendEmailBtn.addEventListener(Events.ON_CLICK, this); 
      okBtn.addEventListener(Events.ON_CLICK, this);       
       
      firstBtn.setStyle(pageButtonStyle);
      prevBtn.setStyle(pageButtonStyle);
      nextBtn.setStyle(pageButtonStyle);
      lastBtn.setStyle(pageButtonRightStyle); 
      
      firstBtn.addEventListener(Events.ON_CLICK, this);
      prevBtn.addEventListener(Events.ON_CLICK, this);
      nextBtn.addEventListener(Events.ON_CLICK, this);
      lastBtn.addEventListener(Events.ON_CLICK, this);
      
      
//      actionBox.appendChild(new Label(Msg.translate(Env.getCtx(), "R_MailText_ID")));
//      loadMailTemp();
////      actionBox.setWidth("50%");
//      mailTempCombo.addEventListener(Events.ON_SELECT, this);
      actionBox.appendChild(selectedCountLabel);
      actionBox.appendChild(firstBtn);
      actionBox.appendChild(prevBtn);
      actionBox.appendChild(pageLabel);
      actionBox.appendChild(nextBtn);
      actionBox.appendChild(lastBtn);
      actionBox.appendChild(new Label(Msg.translate(Env.getCtx(), "R_MailText_ID")));
      loadMailTemp();
//      actionBox.setWidth("50%");
      mailTempCombo.addEventListener(Events.ON_SELECT, this);
      actionBox.appendChild(mailTempCombo);
      actionBox.appendChild(sendEmailBtn);
      actionBox.appendChild(okBtn);
      return actionBox;
  }
    
    private void loadOrganizations() {
        try {
            orgCombo.getItems().clear();
            orgCombo.appendItem("", new KeyNamePair(-1, ""));

            String sql = "SELECT AD_Org_ID, Name FROM AD_Org WHERE AD_Client_ID=? AND IsActive='Y' ORDER BY Name";
            KeyNamePair[] orgs = DB.getKeyNamePairs(sql, false, Env.getAD_Client_ID(Env.getCtx()));

            for (KeyNamePair org : orgs) {
                orgCombo.appendItem(org.getName(), org);
            }

            int currentOrgId = Env.getAD_Org_ID(Env.getCtx());
            for (int i = 0; i < orgCombo.getItemCount(); i++) {
                KeyNamePair pair = (KeyNamePair)orgCombo.getItemAtIndex(i).getValue();
                if (pair != null && pair.getKey() == currentOrgId) {
                    orgCombo.setSelectedIndex(i);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
    
    
    private void loadMailTemp() {
        try {
        	mailTempCombo.getItems().clear();
        	mailTempCombo.appendItem("", new KeyNamePair(-1, ""));

            String sql = "SELECT R_MailText_ID, Name FROM R_MailText WHERE AD_Client_ID=? AND IsActive='Y' ORDER BY Name";
            KeyNamePair[] orgs = DB.getKeyNamePairs(sql, false, Env.getAD_Client_ID(Env.getCtx()));

            for (KeyNamePair org : orgs) {
            	mailTempCombo.appendItem(org.getName(), org);
            }

            int currentOrgId = Env.getAD_Org_ID(Env.getCtx());
            for (int i = 0; i < mailTempCombo.getItemCount(); i++) {
                KeyNamePair pair = (KeyNamePair)mailTempCombo.getItemAtIndex(i).getValue();
                if (pair != null && pair.getKey() == currentOrgId) {
                	mailTempCombo.setSelectedIndex(i);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
    
    private int getRecordIDFromRow(int row) {
        // Example: assuming your grid model stores record IDs
    	
    	KeyNamePair docId=(KeyNamePair)dataTable.getModel().getValueAt(row, 1);
    	return docId.getKey();
    }

    private String getDocumentTypeFromRow(int row) {
        // Example: get "Type" column value (Lead / Opportunity / Quotation etc.)
        return (String) dataTable.getModel().getValueAt(row, 2); // Assuming 1st column stores the Type
    }

    private int getTableIDForDocType(String docType) {
        if ("Lead".equalsIgnoreCase(docType))
            return MLead.Table_ID; // Example Table ID for C_Lead
        else if ("Opportunity".equalsIgnoreCase(docType))
            return MSalesOpportunity.Table_ID; // Example Table ID for C_SalesOpportunity
        else if ("Quotation".equalsIgnoreCase(docType)) // Quotation
            return MOrder.Table_ID; // C_Order Table ID (Sales Quotation)
        else if ("Order".equalsIgnoreCase(docType))
            return MOrder.Table_ID; // C_Order Table ID
        else if ("Invoice".equalsIgnoreCase(docType))
            return MInvoice.Table_ID; // C_Invoice Table ID
        else
            return -1;
    }

    
    private int getSelectedOrgId() {
        Object selected = orgCombo.getSelectedItem().getValue();
        return selected != null ? ((KeyNamePair)selected).getKey() : 0;
    }
    
    private int getSelectedMailTempId() {
    	
    	if(mailTempCombo.getSelectedItem()!=null)
    	{
    		Object selected = mailTempCombo.getSelectedItem().getValue();
            return selected != null ? ((KeyNamePair)selected).getKey() : 0;
    	}
    	else
    	return 0;
        
    }
    
    private Date getFromDate()
    {
    	Object selected = dateFrom.getComponent();
        return selected != null ? ((Date)dateFrom.getComponent().getValue()) : null;
    }
    
    private Date getToDate()
    {
    	Object selected = dateTo.getComponent();
        return selected != null ? ((Date)dateTo.getComponent().getValue()) : null;
    }

    private Map<String, Integer> getStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        
        counts.put("Lead", getRecordCount("C_Lead"));
        counts.put("Opportunity", getRecordCount("C_SalesOpportunity"));
        counts.put("Quotation", getRecordCount("Quotation"));
        counts.put("Order", getRecordCount("Order"));
        counts.put("Invoice", getRecordCount("C_Invoice"));

        return counts;
    }

    private int getRecordCount(String tableName) {
    	
    	String sql=null;
    	if(tableName.equalsIgnoreCase("Quotation"))
        sql="SELECT COUNT(*) FROM C_Order  WHERE AD_Client_ID=? AND AD_Org_ID=? AND IsSOTrx='Y' AND IsActive='Y' AND C_DocTypeTarget_ID IN (SELECT dt.C_DocType_ID FROM C_DocType dt WHERE dt.DocSubTypeSO IN ( 'ON' , 'OB' ))";
    	else if(tableName.equalsIgnoreCase("Order"))
        sql="SELECT COUNT(*) FROM C_Order  WHERE AD_Client_ID=? AND AD_Org_ID=? AND IsSOTrx='Y' AND IsActive='Y' AND C_DocTypeTarget_ID IN (SELECT dt.C_DocType_ID FROM C_DocType dt WHERE dt.DocSubTypeSO NOT IN ( 'ON' , 'OB' ))";
    	else if(tableName.equalsIgnoreCase("C_Invoice"))
    	sql = "SELECT COUNT(*) FROM " + tableName + " WHERE IsSOTrx='Y' AND IsActive='Y' AND AD_Client_ID=? AND AD_Org_ID=?";	
    	else    		
    	sql = "SELECT COUNT(*) FROM " + tableName + " WHERE IsActive='Y' AND AD_Client_ID=? AND AD_Org_ID=?";
        
    	
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, Env.getAD_Client_ID(Env.getCtx()));
            pstmt.setInt(2, getSelectedOrgId());
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Database error", e);
            throw new RuntimeException("Failed to load data", e);
        } finally {
            DB.close(rs, pstmt);
        }
        return 0;
    }

    private void refreshStatusCounts() {
        Map<String, Integer> counts = getStatusCounts();

//        setupStatusButton(leadsBtn, 
//            Msg.getMsg(Env.getCtx(), "Leads") + " " + counts.get("Lead"), 
//            "#FFEE93");
//        setupStatusButton(opportunityBtn, 
//            Msg.getMsg(Env.getCtx(), "Opportunity") + " " + counts.get("Opportunity"), 
//            "#FFB347");
//        setupStatusButton(quotationBtn, 
//            Msg.getMsg(Env.getCtx(), "Quotation") + " " + counts.get("Quotation"), 
//            "#A8E6CF");
//        setupStatusButton(orderBtn, 
//            Msg.getMsg(Env.getCtx(), "Order") + " " + counts.get("Order"), 
//            "#89CFF0");
//        setupStatusButton(invoiceBtn, 
//            Msg.getMsg(Env.getCtx(), "Invoice") + " " + counts.get("Invoice"), 
//            "#D3D3D3");
        
//        setupStatusButton(leadsBtn, 
//                Msg.getMsg(Env.getCtx(), "Leads") + " " + "("+ counts.get("Lead") + ")", 
//                "#FFEE93");
//            setupStatusButton(opportunityBtn, 
//                Msg.getMsg(Env.getCtx(), "Opportunity") + " " + "(" + counts.get("Opportunity") + ")", 
//                "#FFB347");
//            setupStatusButton(quotationBtn, 
//                Msg.getMsg(Env.getCtx(), "Quotation") + " " + "(" + counts.get("Quotation") + ")", 
//                "#A8E6CF");
//            setupStatusButton(orderBtn, 
//                Msg.getMsg(Env.getCtx(), "Order") + " " + "(" + counts.get("Order") + ")", 
//                "#89CFF0");
//            setupStatusButton(invoiceBtn, 
//                Msg.getMsg(Env.getCtx(), "Invoice") + " " + "(" + counts.get("Invoice") + ")", 
//                "#D3D3D3");
        setupStatusButton(leadsBtn, 
                Msg.getMsg(Env.getCtx(), "Leads"), "("+ counts.get("Lead") + ")", 
                "#FFEE93");
            setupStatusButton(opportunityBtn, 
                Msg.getMsg(Env.getCtx(), "Opportunity"), "(" + counts.get("Opportunity") + ")", 
                "#FFB347");
            setupStatusButton(quotationBtn, 
                Msg.getMsg(Env.getCtx(), "Quotation"),  "(" + counts.get("Quotation") + ")", 
                "#A8E6CF");
            setupStatusButton(orderBtn, 
                Msg.getMsg(Env.getCtx(), "Order"),  "(" + counts.get("Order") + ")", 
                "#89CFF0");
            setupStatusButton(invoiceBtn, 
                Msg.getMsg(Env.getCtx(), "Invoice") ,"(" + counts.get("Invoice") + ")", 
                "#D3D3D3");
            
//            createLeadBtn.setStyle(
//            	    "background-color: #FFEE93; " + // light yellow
//            	    "border: 1px solid black; " +
//            	    "font-weight: bold; " +
//            	    "text-align: center; " +
//            	    "white-space: pre; " + // allow line break
//            	    "width: 60px; height: 60px;" + // matching image size
//            	    "padding: 0px;" // remove any default padding
//            	);
//            	createLeadBtn.setImage("images/Plus_1.png");
//            	createLeadBtn.addEventListener(Events.ON_CLICK, this);
           
    }
    
 

    private void setupStatusButton(Button btn, String label, String count, String color) {
    	 btn.setLabel(label + "\u2028" + count);

//        btn.setStyle
////        ("background-color: " + color + "; " +
////                    "border: 1px solid black; " +
////                    "font-weight: bold; " +
////                    "margin: 0 5px; " +
////                    "padding: 5px 10px;");
//        ("background-color: " + color +  " ; " + // light yellow
//	    "border: 1px solid black; " +
//	    "font-weight: bold; " +
//	    "text-align: center; " +
//	    "white-space: pre; " + // allow line break 
//	    "padding: 0px 0px;" +
//	    "width: 80px; height: 80px;"); // optional sizing
        String style = new StringBuilder()
                .append("background-color: ").append(color).append(" !important;")
                .append(" border: 1px solid black !important;")
                .append(" font-weight: bold !important;")
                .append(" text-align: center !important;")
                .append(" white-space: pre !important;")
                .append(" padding: 20px 20px !important;")
                .append(" width: 80px !important;")
                .append(" height: 60px !important;")
                .toString();
            
            btn.setStyle(style);

        btn.addEventListener(Events.ON_CLICK, this);
    }
    
    
    private void prepareTable(WListbox table) {
        table.clear();

        ListHead listhead = new ListHead();
        listhead.setHeight("5px");
        listhead.appendChild(new Listheader(" "));
        listhead.appendChild(new Listheader("DocumentNo"));
        listhead.appendChild(new Listheader("Type"));
        listhead.appendChild(new Listheader("Date"));
        listhead.appendChild(new Listheader("Description"));
        listhead.appendChild(new Listheader("Source"));
        listhead.appendChild(new Listheader("Company"));
        listhead.appendChild(new Listheader("Contact"));
        listhead.appendChild(new Listheader("Phone"));
        listhead.appendChild(new Listheader("Email"));
        listhead.appendChild(new Listheader("SalesRep"));
        listhead.appendChild(new Listheader("Amount"));
        listhead.appendChild(new Listheader("Status"));
        listhead.setSizable(true);
        table.appendChild(listhead);

        table.addColumn(" "); 
        table.addColumn("DocumentNo");
        table.addColumn("Type");
        table.addColumn("Date");
        table.addColumn("Description");
        table.addColumn("Source");
        table.addColumn("Company");
        table.addColumn("Contact");
        table.addColumn("Phone");
        table.addColumn("Email");
        table.addColumn("SalesRep");
        table.addColumn("Amount");
        table.addColumn("Status");

        table.setMultiSelection(true);

        table.setColumnClass(0, IDColumn.class, false, " ");
        table.setColumnClass(1, KeyNamePair.class, true, "DocumentNo");
        table.setColumnClass(2, String.class, true, "Type");
        table.setColumnClass(3, Timestamp.class, true, "Date");
        table.setColumnClass(4, String.class, true, "Description");
        table.setColumnClass(5, String.class, true, "Source");
        table.setColumnClass(6, String.class, true, "Company");
        table.setColumnClass(7, String.class, true, "Contact");
        table.setColumnClass(8, String.class, true, "Phone");
        table.setColumnClass(9, String.class, true, "Email");
        table.setColumnClass(10, String.class, true, "SalesRep");
        table.setColumnClass(11, BigDecimal.class, true, "Amount");
        table.setColumnClass(12, String.class, true, "Status");

        ((Listheader) table.getListHead().getChildren().get(0)).setWidth("30px");  // IDColumn field
        ((Listheader) table.getListHead().getChildren().get(1)).setWidth("80px");  // Document field
        ((Listheader) table.getListHead().getChildren().get(2)).setWidth("80px");  // Type field
        ((Listheader) table.getListHead().getChildren().get(3)).setWidth("70px");  // Date field
        ((Listheader) table.getListHead().getChildren().get(4)).setWidth("250px");  // Desc field
        ((Listheader) table.getListHead().getChildren().get(5)).setWidth("80px");  // RefDoc

//        ((Listheader) table.getListHead().getChildren().get(8)).setWidth("100px");  // RefDoc

        ((Listheader) table.getListHead().getChildren().get(11)).setWidth("80px");  // IDColumn field
        ((Listheader) table.getListHead().getChildren().get(12)).setWidth("100px");  // IDColumn field

        table.setFixedLayout(true);
        table.setHeight("380px");
        table.setWidth("98%"); 
//        table.setStyle("border: 1px solid #ccc; margin-top: 10px;");
        table.setStyle("overflow:auto; border:1px solid #ccc;");
//        // Make sure columns are properly sized
//        table.setVflex(true); // Allow vertical flex
        table.autoSize();
        
    }


    private void updateDatasInRows(List<SalesManagementData> salesDatas) {
    	
    	if(salesDatas!=null && salesDatas.size()>0)
    	{
    		for(SalesManagementData data:salesDatas)
    		{           
    			Integer row=data.getRow();
    			dataTable.setRowCount(row+1);
				KeyNamePair documentNoWithPrimaryKey=new KeyNamePair(data.getPrimaryKey(),data.getDocumentNo());
    	        dataTable.setValueAt(data.getIdColumn(), row, 0);   //  Requisition/PO    	        
 //   	        dataTable.setValueAt(new IDColumn(data.getPrimaryKey()), row, 0);   //  C_Order_ID
    	        dataTable.setValueAt(documentNoWithPrimaryKey, row, 1);   //  DocumentNo
    	        dataTable.setValueAt(data.getType(), row, 2);   //  Type
    	        dataTable.setValueAt(data.getDate(), row, 3);   //  Type
    	        dataTable.setValueAt(data.getDescription(), row, 4);   //  Type
    	        dataTable.setValueAt(data.getRefDoc(), row, 5);   //  Type
    	        dataTable.setValueAt(data.getCompanyName(), row, 6);   //  Company Name
    	        dataTable.setValueAt(data.getContactName(), row, 7);   //  Contact Name
    	        dataTable.setValueAt(data.getPhone(), row, 8);   //  Phone
    	        dataTable.setValueAt(data.getEmail(), row, 9);   //  Email
    	        dataTable.setValueAt(data.getSalesRep(), row, 10);   //  Sales Rep
    	        dataTable.setValueAt(data.getTotalLines(), row, 11);   //  Total Lines
    	        dataTable.setValueAt(data.getStatus(), row, 12);   //  Status
    	
    		}
    		dataTable.autoSize();
    	}
    	
    	okBtn.setDisabled(shouldDisableGenerateButton());
    	
	}

    private void createOKButtonProcess() throws Exception {
    	
    	if (shouldDisableGenerateButton()) {
            FDialog.warn(-1, "", "Cannot process documents with status 'In Progress' or 'Drafted'");
            return;
        }
		//  Array of Integers
		ArrayList<KeyNamePair> results = new ArrayList<KeyNamePair>();
		//Map<String, ArrayList<Integer>> dataToCreate=new HashMap<String, ArrayList<Integer>>();
		//	Get selected entries
		
		String typeName=null;
		
		int rows = dataTable.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)dataTable.getValueAt(i, 0);     //  ID in column 0
			KeyNamePair productKeyName = (KeyNamePair)dataTable.getValueAt(i, 1);     //  ID in column 0			
			if (id != null && id.isSelected())
			{
				if(typeName==null)
					typeName=(String)dataTable.getValueAt(i, 2);
				results.add(productKeyName);
			}
		}

		if (results.size()> 0 && typeName.equalsIgnoreCase("Lead"))
		{
			StringBuilder returnValue=WCRMSalesManagementUtility.createSalesOpportunity(Trx.get(trxName, true), results);
			FDialog.info(-1, this, returnValue.toString());
			refreshStatusCounts();
			if("Created".contains(returnValue))
        	loadOpportunityData();
			else
			loadLeadData();
				
		}
		else if(results.size()> 0 && typeName.equalsIgnoreCase("Opportunity"))
		{
			StringBuilder returnValue=WCRMSalesManagementUtility.createSalesQuotation(Trx.get(trxName, true), results);
			FDialog.info(-1, this, returnValue.toString());
			refreshStatusCounts();
			if("Created".contains(returnValue))
			loadOrderData("Quotation");
			else
	        loadOpportunityData();
		}
		else if(results.size()> 0 && typeName.equalsIgnoreCase("Quotation"))
		{
			StringBuilder returnValue=WCRMSalesManagementUtility.createSalesOrder(Trx.get(trxName, true), results);
			FDialog.info(-1, this, returnValue.toString());
			refreshStatusCounts();
			
			if("Created".contains(returnValue))
			loadOrderData("Order");
			else
        	loadOrderData("Quotation");
		}
		else if(results.size()> 0 && typeName.equalsIgnoreCase("Order"))
		{
			StringBuilder returnValue=WCRMSalesManagementUtility.createInvoice(Trx.get(trxName, true), results);
			FDialog.info(-1, this, returnValue.toString());
			refreshStatusCounts();
			if("Created".contains(returnValue))
	        loadInvoiceData();
			else
        	loadOrderData("Order");
		}
		else if(results.size()> 0 && typeName.equalsIgnoreCase("Invoice"))
		{
			StringBuilder returnValue=WCRMSalesManagementUtility.completeInvoice(Trx.get(trxName, true), results);
			FDialog.info(-1, this, returnValue.toString());
			refreshStatusCounts();
        	loadInvoiceData();
			//FDialog.info(-1, this, "No further process invoice from selection");
		}
		else
			FDialog.info(-1, this, "No any selection");
		
	}
    
    private Label createSelectedCountLabel() {
        Label selectedCountLabel = new Label("0 selected");
        selectedCountLabel.setId("selectedCountLabel");
        selectedCountLabel.setStyle("margin-right: 100px; font-weight: bold;");
        selected = false;
        return selectedCountLabel;
    }
    
    private void updateSelectedCountLabel() {
        int selectedCount = dataTable.getSelectedIndices().length;
        Label selectedCountLabel = (Label) getFellow("selectedCountLabel");
        if (selectedCountLabel != null) {
        	selected = true;
            selectedCountLabel.setValue(selectedCount + " selected");
        }
        else
        {
        	selected = false;
        }
    }
    
    private void createNewLead() {
        try {
        	if((selected && leadButtonClicked) || (selected && opportButtonClicked))
        	{
        		if(dataTable.getSelectedIndices().length > 1)
        		{
        			FDialog.error(0, "", "More than one record selected..");
        		}
        		else
        		{
        			ArrayList<KeyNamePair> results = new ArrayList<KeyNamePair>();
        			String typeName=null;
        			int rows = dataTable.getRowCount();
        			for (int i = 0; i < rows; i++)
        			{
        				IDColumn id = (IDColumn)dataTable.getValueAt(i, 0);  
        				KeyNamePair productKeyName = (KeyNamePair)dataTable.getValueAt(i, 1); 		
        				if (id != null && id.isSelected())
        				{
        					if(typeName==null)
        						typeName=(String)dataTable.getValueAt(i, 2);
        					results.add(productKeyName);
        				}
        			}
        			if (results.size()> 0)
        			{
        				WProductLineForm plForm = new WProductLineForm(results, typeName);
                		plForm.setTitle("Create/Edit Line");
                		plForm.setClosable(true);
                		plForm.setBorder("normal");
                		plForm.setWidth("766px");
                        plForm.setHeight("60%");
                        plForm.setPage(this.getPage());
                        plForm.doHighlighted();
                        if(typeName.equalsIgnoreCase("Lead"))
                        {
                        	 plForm.addEventListener("onLeadInfoSaved", e -> {
                             	setActiveButton(true, false, false, false, false);
             	                loadLeadData();
             	                refreshStatusCounts();
             	                selected = false;
             	            });
             	            
                             plForm.addEventListener(Events.ON_CLOSE, e -> {
             	                if (leadButtonClicked) {
             	                	setActiveButton(true, false, false, false, false);
             	                    loadLeadData();
             	                    selected = false;
             	                    refreshStatusCounts();
             	                }
             	            });
                        }
                        else if(typeName.equalsIgnoreCase("Opportunity"))
                        {
                        	plForm.addEventListener("onOpportunityLineSaved", e -> {
                             	setActiveButton(false, true, false, false, false);
                             	loadOpportunityData();
             	                refreshStatusCounts();
             	                selected = false;
             	            });
             	            
                             plForm.addEventListener(Events.ON_CLOSE, e -> {
             	                if (opportButtonClicked) {
             	                	setActiveButton(false, true, false, false, false);
             	                	loadOpportunityData();
             	                    refreshStatusCounts();
             	                    selected = false;
             	                }
             	            });
                        }
                       
        			}      			
        		}       		
        	}
        	else
        	{
	            WLeadForm leadForm = new WLeadForm();
	            leadForm.setTitle("Create New Lead");
	            leadForm.setClosable(true);
	            leadForm.setBorder("normal");
	            leadForm.setWidth("872px");
	            leadForm.setHeight("70%");
	            
	            // Center the window on screen
	            leadForm.setPage(this.getPage());
	            leadForm.doHighlighted();
	            
	         // Refresh when lead is saved
	            leadForm.addEventListener("onLeadSaved", e -> {
	            	setActiveButton(true, false, false, false, false);
	                loadLeadData();
	                refreshStatusCounts();
	                selected = false;
	            });
	            
	            // Refresh lead data after form is closed
	            leadForm.addEventListener(Events.ON_CLOSE, e -> {
	                if (leadButtonClicked) {
	                	setActiveButton(true, false, false, false, false);
	                    loadLeadData();
	                    refreshStatusCounts();
	                    selected = false;
	                }
	            });
        	}
        } catch (Exception e) {
            FDialog.error(0, "ErrorCreatingLeadForm", e.getLocalizedMessage());
        }
    }

	@Override
    public void valueChange(ValueChangeEvent evt) {
        if (evt.getSource() == dateFrom || evt.getSource() == dateTo) {
           
        	if(leadButtonClicked)
                loadLeadData();
            	else if (opportButtonClicked)
            	loadOpportunityData();	
            	else if (quorationButtonClicked)
                	loadOrderData("Quotation");	
            	else if (orderButtonClicked)
                	loadOrderData("Order");	
            	else if (invoiceButtonClicked)
                	loadInvoiceData();
            	
            	refreshStatusCounts();
        } 
    }
   
	public static class SendMailData implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String EmailsTo;
		private String EmailsCC;
		private String EmailsBcc;
		private String EmailsBody;
		private String EmailsHeader;
		private Boolean EmailIsHtml;
		private String type;
		private Integer Id;
		
		public String getEmailsTo() {
			return EmailsTo;
		}
		public void setEmailsTo(String emailsTo) {
			EmailsTo = emailsTo;
		}
		public String getEmailsCC() {
			return EmailsCC;
		}
		public void setEmailsCC(String emailsCC) {
			EmailsCC = emailsCC;
		}
		public String getEmailsBcc() {
			return EmailsBcc;
		}
		public void setEmailsBcc(String emailsBcc) {
			EmailsBcc = emailsBcc;
		}
		public String getEmailsBody() {
			return EmailsBody;
		}
		public void setEmailsBody(String emailsBody) {
			EmailsBody = emailsBody;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Integer getId() {
			return Id;
		}
		public void setId(Integer id) {
			Id = id;
		}
		public String getEmailsHeader() {
			return EmailsHeader;
		}
		public void setEmailsHeader(String emailsHeader) {
			EmailsHeader = emailsHeader;
		}
		public Boolean getEmailIsHtml() {
			return EmailIsHtml;
		}
		public void setEmailIsHtml(Boolean emailIsHtml) {
			EmailIsHtml = emailIsHtml;
		}
		
	}
    
    public class SalesManagementData implements Serializable
    {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private Integer row;
		private IDColumn idColumn;
		private Integer primaryKey;
		private String documentNo;
		private String companyName;
		private String contactName;
		private String phone;
		private String email;
		private String salesRep;
		private BigDecimal totalLines;
		private String status;
		private String type;
		private String refDoc;
		private String description;
		private Timestamp date;
		
		public String getDocumentNo() {
			return documentNo;
		}
		public void setDocumentNo(String documentNo) {
			this.documentNo = documentNo;
		}
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getContactName() {
			return contactName;
		}
		public void setContactName(String contactName) {
			this.contactName = contactName;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getSalesRep() {
			return salesRep;
		}
		public void setSalesRep(String salesRep) {
			this.salesRep = salesRep;
		}
		public BigDecimal getTotalLines() {
			return totalLines;
		}
		public void setTotalLines(BigDecimal totalLines) {
			this.totalLines = totalLines;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		@Override
		public String toString() {
			return "SalesManagementData [row=" + row + ", documentNo=" + documentNo + ", companyName=" + companyName
					+ ", contactName=" + contactName + ", phone=" + phone + ", email=" + email + ", salesRep="
					+ salesRep + ", totalLines=" + totalLines + ", status=" + status + "]";
		}
		public IDColumn getIdColumn() {
			return idColumn;
		}
		public void setIdColumn(IDColumn idColumn) {
			this.idColumn = idColumn;
		}
		public Integer getRow() {
			return row;
		}
		public void setRow(Integer row) {
			this.row = row;
		}
		public Integer getPrimaryKey() {
			return primaryKey;
		}
		public void setPrimaryKey(Integer primaryKey) {
			this.primaryKey = primaryKey;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getRefDoc() {
			return refDoc;
		}
		public void setRefDoc(String refDoc) {
			this.refDoc = refDoc;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Timestamp getDate() {
			return date;
		}
		public void setDate(Timestamp date) {
			this.date = date;
		}

    }
    
    private boolean shouldDisableGenerateButton() {
        int[] selectedRows = dataTable.getSelectedIndices();
        if (selectedRows.length == 0) return true;
        
        for (int row : selectedRows) {
            String status = (String) dataTable.getValueAt(row, 12); 
            String type = (String) dataTable.getValueAt(row, 2);
            if(type.equalsIgnoreCase("Lead"))
    		{
            	if ("Converted to Opportunity".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
            		customBtn.setVisible(false);
            		return true;                  
                }
    		}
            else if(type.equalsIgnoreCase("Opportunity"))
    		{
            	if ("Converted to Quotation".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
            		customBtn.setVisible(false);
            		return true;
                }
    		}
            else if(type.equalsIgnoreCase("Quotation") ||type.equalsIgnoreCase("Order"))
    		{
            	customBtn.setVisible(false);
            	if ("Completed".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
                    return true;
                }
    		}
            else if(type.equalsIgnoreCase("Invoice"))
    		{
            	customBtn.setVisible(false);
            	if ("Completed".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) 
            	{
                    return true;
                }
    		}   		
        }
        return false;
    }
    
   /** 
    private boolean shouldDisableGenerateButton() {
        int[] selectedRows = dataTable.getSelectedIndices();
        if (selectedRows.length == 0) return true;
        
        for (int row : selectedRows) {
            String status = (String) dataTable.getValueAt(row, 12); 
            String type = (String) dataTable.getValueAt(row, 2);
            if(type.equalsIgnoreCase("Lead"))
    		{
            	if ("Converted to Opportunity".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
                    return true;
                }
    		}
            else if(type.equalsIgnoreCase("Opportunity"))
    		{
            	if ("Converted to Quotation".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
                    return true;
                }
    		}
            else if(type.equalsIgnoreCase("Quotation") ||type.equalsIgnoreCase("Order"))
    		{
            	if ("In Progress".equalsIgnoreCase(status) || "Drafted".equalsIgnoreCase(status)) {
                    return true;
                }
    		}
            else if(type.equalsIgnoreCase("Invoice"))
    		{
            	if ("In Progress".equalsIgnoreCase(status) || "Drafted".equalsIgnoreCase(status)) 
            	{
                    return true;
                }
    		}   		
        }
        return false;
    }**/
    
    
}