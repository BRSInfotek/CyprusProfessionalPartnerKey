package org.cyprus.webui.apps;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cyprus.exceptions.CyprusException;
import org.cyprus.model.SystemIDs;
import org.cyprus.webui.component.Button;
import org.cyprus.webui.component.ConfirmPanel;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.ListItem;
import org.cyprus.webui.component.Listbox;
import org.cyprus.webui.component.Window;
import org.cyprus.webui.panel.AbstractADWindowPanel;
import org.cyprus.webui.window.FDialog;
import org.cyprusbrs.framework.MCountry;
import org.cyprusbrs.framework.MLocation;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MLookupInfo;
import org.cyprusbrs.framework.MQuery;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.model.MRefTable;
import org.cyprusbrs.model.MTable;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.ProcessInfo;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;
import org.cyprusbrs.util.Util;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

public class FileImportDailog extends Window implements EventListener{

	
	private static final long serialVersionUID = 1L;
	private boolean m_autoStart;
	private AbstractADWindowPanel panel;
	private int m_WindowNo;
	private Properties m_ctx;
	private ProcessInfo m_pi = null;
	private static CLogger log = CLogger.getCLogger(FileImportDailog.class);
	private Window winImportFile = null;
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private Listbox cboType = new Listbox();
	private Listbox listImportMode = new Listbox();
	private boolean				m_isCanExport;
	private Button bFile = new Button();
	private Label uploadFileName=new Label("Upload file : ");
	public final static int REFERENCE_IMPORT_MODE = 1000237;
	private File tempFile=null;
	private Integer primaryKeyIndex=-1;
	String m_trx = Trx.createTrxName("Create Transaction to save excel data info");
	Trx localTrx=null;
	public FileImportDailog(AbstractADWindowPanel panel, int windowNo, ProcessInfo pi, boolean autoStart) {
		
		
		System.out.println(panel.getWindowNo()+" WindowNo "+windowNo+" autoStart  "+autoStart);
		m_ctx = Env.getCtx();
		this.panel = panel;
		m_WindowNo = windowNo;
		m_pi = pi;
		m_autoStart = autoStart;
		m_isCanExport = MRole.getDefault().isCanExport(pi.getTable_ID());
		System.out.println("3. m_isCanIxport "+m_isCanExport);
		log.info("Process=" + pi.getAD_Process_ID());		
		try
		{
			init();
			//			dynInit();
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "", ex);
		}
		
	}

	@Override
	public void onEvent(Event event) throws Exception {
		
		if (event.getTarget().equals(bFile)) 
		cmd_load();
		else if(event.getTarget().getId().equals(ConfirmPanel.A_CANCEL))
			winImportFile.onClose();
		else if(event.getTarget().getId().equals(ConfirmPanel.A_OK))			
			importFile();
		
	}
	
	/**
	 * Load Data Command
	 */
	private void cmd_load() {
		System.out.println("Step 1... ");
		log.config("");
		if (!m_isCanExport) {
			FDialog.error(m_WindowNo, this, "AccessCannotLoad", getTitle());
			return;
		}
		//  Show File Open Dialog
		Media file = null;
		System.out.println("Step 1.1... ");

		try {
			file = Fileupload.get(true);
			if (file == null)
				return;
		}
		catch (InterruptedException e)
		{
			log.warning(e.getLocalizedMessage());
			return;
		}
		System.out.println("Step 2... ");

		System.out.println("File Name : "+file.getName());
		
		FileOutputStream fos = null;
		try {

			tempFile = File.createTempFile("cyprus", "_"+file.getName());

			fos = new FileOutputStream(tempFile);
			byte[] bytes = null;
			if (file.inMemory()) {
				bytes = file.getByteData();
			} else {
				InputStream is = file.getStreamData();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[ 1000 ];
				int byteread = 0;
				while (( byteread=is.read(buf) )!=-1)
					baos.write(buf,0,byteread);
				bytes = baos.toByteArray();
			}

			fos.write(bytes);
			fos.flush();
			fos.close();
			System.out.println("tempFile File Name : "+tempFile.getName());
			System.out.println("tempFile File Name : "+tempFile.getPath());

			/**
			 * Here import the file
			 */
			System.out.println("Step 3... ");
			
			uploadFileName.setValue("Upload file : "+file.getName());
			
			
			
//			if (ImpExpUtil.importPrintFormat(tempFile)) {
//				FDialog.info(m_WindowNo, this, "Window Definition Loaded", getTitle());
//				fillComboReport(m_reportEngine.getPrintFormat().get_ID());}
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {}
		}
		
	}
	private void init() {
		
//		FDialog.error(m_WindowNo, winExportFile, "Work in progress...");
		System.out.println("Test1 ");
		
		MLookupInfo lookupInfo = MLookupFactory.getLookup_List(Env.getLanguage(Env.getCtx()), REFERENCE_IMPORT_MODE);
		MLookup lookup = new MLookup(lookupInfo, 0);
//		fImportMode = new WTableDirEditor("ImportMode",true,false,true,lookup);
		
		
		if(winImportFile == null)
		{
			winImportFile = new Window();
			winImportFile.setTitle(Msg.getMsg(Env.getCtx(), "FileImport") + ": " + panel.getActiveGridTab().getName());
			winImportFile.setWidth("420px");
			winImportFile.setClosable(true);
			winImportFile.setBorder("normal");
			winImportFile.setStyle("position:absolute");
			System.out.println("Test2 ");
			cboType.setMold("select");
			cboType.getItems().clear();

			ListItem li = cboType.appendItem("xlsx" + " - " + Msg.getMsg(m_ctx, "FileXLSX"), "xlsx");
			cboType.appendItem("xls" + " - " + Msg.getMsg(m_ctx, "FileXLS"), "xls");
			cboType.setSelectedItem(li);
			
			listImportMode.setMold("select");
			listImportMode.getItems().clear();
			ListItem liImportMode = listImportMode.appendItem("I" + " - " + Msg.getMsg(m_ctx, SystemIDs.Insert), SystemIDs.Insert);
			listImportMode.appendItem("U" + " - " + Msg.getMsg(m_ctx, SystemIDs.Update), SystemIDs.Update);
			listImportMode.setSelectedItem(liImportMode);
			
			Vbox hb = new Vbox();
			Div div = new Div();
			div.setAlign("left");
			div.appendChild(new Label("Files of Type: "));
			hb.appendChild(div);
			hb.appendChild(cboType);
			cboType.setWidth("100%");
		
			div = new Div();
			div.setAlign("left");
			div.appendChild(new Label("Import Mode: "));
			hb.appendChild(div);
//			hb.appendChild(fImportMode.getComponent());
//			fImportMode.getComponent().setWidth("100%");
//			fImportMode.setMandatory(true);
			hb.appendChild(listImportMode);
			listImportMode.setWidth("100%");
			
			winImportFile.appendChild(hb);
			winImportFile.appendChild(new Separator());
			
			div = new Div();
			div.setAlign("left");
			div.appendChild(uploadFileName);

			hb.appendChild(div);
			hb.appendChild(bFile);
			bFile.setLabel(Msg.getMsg(Env.getCtx(), "FileImportFile"));
			bFile.setTooltiptext(Msg.getMsg(Env.getCtx(), "FileImportFileInfo"));
			bFile.setWidth("100%");
			bFile.addEventListener(Events.ON_CLICK, e->cmd_load());

			hb.setWidth("390px");
			winImportFile.appendChild(hb);
			hb.appendChild(confirmPanel);	
			confirmPanel.addActionListener(this);
			
			winImportFile.appendChild(hb);
			
			winImportFile.appendChild(new Separator());

			winImportFile.addEventListener(Events.ON_CANCEL, e -> onCancel());

			panel.getComponent().getParent().appendChild(winImportFile);
			winImportFile.addEventListener("onImporterException", this);
			winImportFile.focus();
		}
		AEnv.showCenterScreen(winImportFile);

	}

	///// Excel file utility
	private String getCellDataFromExcel(Cell cell) {
		if(cell!=null)
		{	
			String data="";
			switch (cell.getCellType())               
			{  
			case Cell.CELL_TYPE_STRING:    //field that represents string cell type
				data=	cell.getStringCellValue();
				break;  
			case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type updated by Mukesh @20250527
				data = new java.math.BigDecimal(cell.getNumericCellValue()).toPlainString();
				break; 
			case Cell.CELL_TYPE_BOOLEAN:    //field that represents boolean cell type
				data=	cell.getBooleanCellValue()+"";	
				break; 	
			default:
			}  
			return data;
		}
		
		return null;
	}	
	
	
	private void onCancel() {
		winImportFile.onClose();
	}
	private void throwCyprusException(String msg){
	    throw new CyprusException(msg);
	}
	
private void importFile() {
	
	// Below code copy of importing excel file ******************************************************
	int lastRowNum=0;
	int headerLastCellNum=0;
	XSSFWorkbook  workbook=null;
	XSSFSheet sheetLog=null;
	try {
	FileInputStream fis = new FileInputStream(tempFile);
	//creating workbook instance that refers to .xls file  
	workbook = new XSSFWorkbook(fis);
	
	sheetLog = workbook.getSheetAt(0); // Get the first sheet
    
    // Find the next empty row
    lastRowNum = sheetLog.getLastRowNum();
    if (lastRowNum == 0 && sheetLog.getRow(0) == null) { // if the sheet is empty
        lastRowNum = -1;
    }

    // Create a new row for the header (if necessary)
    XSSFRow headerRow = sheetLog.getRow(0);
    if (headerRow == null) {
        headerRow = sheetLog.createRow(0);
    }
	
	// Find the next available header cell
    headerLastCellNum = headerRow.getLastCellNum();
    if (headerLastCellNum == -1) { // if the row is empty
        headerLastCellNum = 0;
    }

    // Create a new header cell and set its value
    Cell newHeaderCell = headerRow.createCell(headerLastCellNum);
    newHeaderCell.setCellValue("Status");
	}catch(Exception e) {System.out.println("Workbook not intialized in importFile method "+e.getMessage());}
	
	// End of the code by Mukesh**********************************************************************
	
	/// Load the work book
		XSSFSheet sheet= loadWorkBook();// load the workbook
	/// Get Header details from excel file	
		Map<String,Object> headerList = getHeaderOfExcelFile(sheet);
	//	
		headerList.forEach((key,value)->{
			System.out.println(key+"  "+value);
		});
		GridTab gridTab =panel.getActiveGridTab();
		System.out.println("Grid Tab "+gridTab.getAD_Tab_ID());
		MTable table = MTable.get (m_ctx, gridTab.getAD_Table_ID());
		ListItem liMode = listImportMode.getSelectedItem();
		String extMode = liMode.getValue().toString();
		
		localTrx = Trx.get(m_trx, true);
		
		for(int j=1;j<sheet.getLastRowNum()+1;j++)
		{
			PO po=null;
			if(SystemIDs.Update.equals(extMode) && primaryKeyIndex>=0)
			{	
				String primaryKey=gridTab.getTableName().concat("_ID");
				Integer recordId=Integer.parseInt(sheet.getRow(j).getCell(primaryKeyIndex).toString()); 
				po = table.getPO(recordId, null); /// for old record update in particular field
			}
			else
			po = table.getPO(0, null); /// for new record created in particular field
			
			for (Cell cell: sheet.getRow(j)) //iteration over cell using for each loop  
    		{	
				if(cell!=null && cell.toString().length()>0)
				{					
					String columnName=headerList.get(cell.getColumnIndex()+"").toString();
					System.out.println("Column Name :: "+columnName);
					
					if(!columnName.equalsIgnoreCase("AD_Client_ID") // && !columnName.equalsIgnoreCase("AD_Org_ID") 
						&& !columnName.equalsIgnoreCase(gridTab.getTableName().concat("_ID")))
					{	
						String obj=getCellDataFromExcel(cell);
						System.out.println(columnName+" :"+obj);
						System.out.println("Celle Value  :"+cell);

						if(columnName.contains("C_Location_ID"))
							columnName="C_Location_ID";
						
						GridField field = gridTab.getField(columnName);
						System.out.println("Column ID : "+field.getAD_Column_ID());

						MColumn column=MColumn.get(Env.getCtx(), field.getAD_Column_ID());
						System.out.println(column.getColumnName()+"  "+columnName+"  "+column.getAD_Reference_ID());
						/// boolean values
						if(columnName.equalsIgnoreCase(column.getColumnName()))
						{
							//						if(columnName.toUpperCase().startsWith("IS"))
							if(column.getAD_Reference_ID()==SystemIDs.YesNo)
							{
								String yn= getYAndN(columnName,obj);
								po.set_ValueNoCheck(columnName, yn);
							}
							// Check Parent Link Column
							/*
							 * else if(column.isParent() && (column.getAD_Reference_ID()==SystemIDs.Table ||
							 * column.getAD_Reference_ID()==SystemIDs.Search ||
							 * column.getAD_Reference_ID()==SystemIDs.TableDirect)) { Integer intValue=0;
							 * String tableName=columnName.substring(0, columnName.lastIndexOf("_ID"));
							 * po.set_ValueNoCheck(columnName, intValue);
							 * 
							 * }
							 */
							/// Checked for Foreign data, end with ID
							//						else if(columnName.toUpperCase().endsWith("ID"))
							else if( column.getAD_Reference_ID()==SystemIDs.TableDirect)
							{	
								String tableName=columnName.substring(0, columnName.lastIndexOf("_ID"));

								System.out.println("tableName ::  "+tableName);
								Integer intValue=0;
								Map<String,Object> mapIden=column.getAllIdentifiers(tableName);
								
								System.out.println(mapIden);
								
								if(mapIden!=null && mapIden.size()>0)
								intValue=getDataFromForeignId(columnName,obj,mapIden);
								else
								intValue=getDataFromForeignId(columnName,obj);
								
								po.set_ValueNoCheck(columnName, intValue);
							}
							else if(column.getAD_Reference_ID()==SystemIDs.Table || column.getAD_Reference_ID()==SystemIDs.Search)
							{	
								
								int refval = field.getVO().AD_Reference_Value_ID;
								Object intValue=null;
								if(column.getAD_Reference_Value_ID()>0)
								{
									final MRefTable refTable = new Query(Env.getCtx(), MRefTable.Table_Name, "AD_Reference_ID=?", localTrx.getTrxName())
											.setParameters(refval)
											.firstOnly();
									System.out.println("refTable : "+refTable);
									if(refTable!=null)
									{

										MColumn filterBy=new MColumn(Env.getCtx(), refTable.getAD_Display(), localTrx.getTrxName());
										MTable tableName=new MTable(Env.getCtx(), refTable.getAD_Table_ID(), localTrx.getTrxName());
										MColumn selectedValue=new MColumn(Env.getCtx(), refTable.getAD_Key(), localTrx.getTrxName());

										StringBuilder selectId = new StringBuilder("SELECT ").append(selectedValue.getColumnName()).append(" FROM ").append(tableName.getTableName());
										String whereClause = refTable.getWhereClause();
										System.out.println(" selectId "+selectId);
										System.out.println(" whereClause "+whereClause);
										if (!Util.isEmpty(whereClause)) {

											selectId.append(" WHERE "+whereClause+" AND UPPER("+filterBy.getColumnName()+") = ?");
											intValue= DB.getSQLValueStringEx(localTrx.getTrxName(), selectId.toString(), new Object[] {obj.toString().toUpperCase()});
										}
										else
										{
											selectId.append(" WHERE UPPER("+filterBy.getColumnName()+") = ?");
											intValue= DB.getSQLValueStringEx(localTrx.getTrxName(), selectId.toString(), new Object[] {obj.toString().toUpperCase()});										
										}
										System.out.println(selectId);
										System.out.println("intValue : "+intValue);

									}
								}
								else
								{
									String tableName=columnName.substring(0, columnName.lastIndexOf("_ID"));
									System.out.println("tableName in Search and Table References ::  "+tableName);
									Map<String,Object> mapIden=column.getAllIdentifiers(tableName);
									
									System.out.println(mapIden);
									
									if(mapIden!=null && mapIden.size()>0)
									intValue=getDataFromForeignId(columnName,obj,mapIden);
									else
									intValue=getDataFromForeignId(columnName,obj);
								}
								if(intValue!=null )
									po.set_ValueNoCheck(columnName, intValue);
								else
									po.set_ValueNoCheck(columnName, null);
							}
							
							//					/// Checked for date type field
							//						else if(columnName.toUpperCase().contains("DATE"))
							else if(column.getAD_Reference_ID()==SystemIDs.Date || 
									column.getAD_Reference_ID()==SystemIDs.DateTime)
							{	
								Timestamp timest=getTimeStampFrom(cell);
								po.set_ValueNoCheck(columnName, timest);
							}
							else if(column.getAD_Reference_ID()==SystemIDs.CostsPrice || 
									column.getAD_Reference_ID()==SystemIDs.Amount ||
									column.getAD_Reference_ID()==SystemIDs.Quantity ||
									column.getAD_Reference_ID()==SystemIDs.Integer||
									column.getAD_Reference_ID()==SystemIDs.Number)
							{	
								if(cell!=null && cell.toString().length()>0)
									po.set_ValueNoCheck(columnName, new BigDecimal(cell.toString()));
								else
									po.set_ValueNoCheck(columnName, Env.ZERO);
							}

							else if(column.getAD_Reference_ID()==SystemIDs.List)
							{	
								String list=resolveForeignList(column, "Name", obj, null);
								System.out.println("list Value :: "+list);
								po.set_ValueNoCheck(columnName, list);
							}
							else if(column.getAD_Reference_ID()==SystemIDs.LocationAddress)
							{
								Integer locationID=getLocationFromAddress(columnName, obj);
								System.out.println("locationID Value :: "+locationID);
								po.set_ValueNoCheck(columnName, locationID);
							}
							else if(column.getAD_Reference_ID()==SystemIDs.WarehouseLocator)
							{
								Integer locatorID=getLocatorFromWarehouse(columnName, obj);
								System.out.println("locationID Value :: "+locatorID);
								po.set_ValueNoCheck(columnName, locatorID);
							}
							// Check and set Account Data
							else if(column.getAD_Reference_ID()==SystemIDs.Account)
							{
								Integer accountID=getAccountIDFromAccountString(columnName, obj);
								System.out.println("accountID Value :: "+accountID);
								po.set_ValueNoCheck(columnName, accountID);
							}
							else
							{	
								System.out.println("Else Value : "+columnName+" :"+obj);
								po.set_ValueNoCheck(columnName, obj);
							}
						}
					}
				}
				
    		}
			try {
				XSSFRow newRow = sheetLog.getRow(j);
				// Create a new cell in the new row and set its value
				Cell newDataCell = newRow.createCell(headerLastCellNum);
				if(po.save(localTrx.getTrxName()))
				{
					DB.commit(true, localTrx.getTrxName());
					newDataCell.setCellValue("Inserted/Updated");
				}
				else
				{
					System.out.println("Not Intserted the record...................................");
					newDataCell.setCellValue("Not Inserted/Updated");

				}


			} catch (Exception e) {
				XSSFRow newRow = sheetLog.getRow(j);
	            // Create a new cell in the new row and set its value
	            Cell newDataCell = newRow.createCell(headerLastCellNum);
	            newDataCell.setCellValue("Exception : "+ e.getMessage());
				FDialog.error(m_WindowNo, this, "Exception : ", e.getMessage());
			}
			
		}
		// File Download method
		try {
		downloadExcelFileWithOutput(workbook);
		}
		catch(Exception e)
		{
			System.out.println("Output file do not download...");
			System.out.println(e);
		}
//		onCancel();
		localTrx.close();
//		dispose();
		onCancel();
		
	}

/***
 * 
 * @param columnName
 * @param obj
 * @return
 */
private Integer getAccountIDFromAccountString(String columnName, String obj) {
	String column="C_ValidCombination";//columnName.substring(0, columnName.lastIndexOf("_ID"));
	System.out.println("column Name : "+column);
	MQuery m_query = new MQuery(column);
	m_query.addRestriction("Combination", MQuery.EQUAL, obj);
	System.out.println("Where Clause for Accouting Combination "+m_query.getWhereClause());
	Query query=new Query(m_ctx,MTable.get(m_ctx, column), m_query.getWhereClause(), null);
	return query.firstId();
	
}

private void downloadExcelFileWithOutput(XSSFWorkbook workbook) throws IOException {
	
    // Write the workbook to an in-memory stream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();
    // Create a media object for the file
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    AMedia media = new AMedia("Status.xlsx", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", in);
    // Use Filedownload to save the file
    Filedownload.save(media);
	
}

private Integer getLocatorFromWarehouse(String columnName, String obj) {
	String column=columnName.substring(0, columnName.lastIndexOf("_ID"));
	MQuery m_query = new MQuery(column);
	m_query.addRestriction("Value", MQuery.EQUAL, obj);
	Query query=new Query(Env.getCtx(),MTable.get(Env.getCtx(), column), m_query.getWhereClause(), null);
	return query.firstId();
}

/**
 * Get/Create Location ID from Location window 
 * @param columnName
 * @param obj
 * @return
 */
private Integer getLocationFromAddress(String columnName, String obj) {
	
	Integer retVal=0;
	if(obj==null)
		return retVal;
	
	StringTokenizer  st = new StringTokenizer(obj.trim(),",");   
	
	System.out.println("Count :: "+st.countTokens());
	Map<String,Object> linkedMap=new LinkedHashMap<String,Object>();
	int i=0;
	while (st.hasMoreElements())   
     {  
		String data=st.nextToken();
		linkedMap.put(i+"",data!=null?data.trim():"");
		i++;
     }    
	
	linkedMap.forEach((key,value)->
	{
		System.out.println(key+"  "+value);
	});

	String address1=	linkedMap.get("0").toString();
	String address2=	linkedMap.get("1").toString();
	String address3=	linkedMap.get("2").toString();
	String address4=	linkedMap.get("3").toString();
	String city=	linkedMap.get("4").toString();
	String region=	linkedMap.get("5").toString();
	String postalCode=	linkedMap.get("6").toString();
	String country=	linkedMap.get("7").toString().substring(0, linkedMap.get("7").toString().indexOf("("));
	String countryCode=	linkedMap.get("7").toString().substring(linkedMap.get("7").toString().indexOf("(")+1, 
			 linkedMap.get("7").toString().lastIndexOf(")"));
	
	 System.out.println("Address1 : "+ address1);
	 System.out.println("Address2 : "+ address2);	
	 System.out.println("Address3 : "+ address3);
	 System.out.println("Address4 : "+ address4);
	 System.out.println("City : "+ city);
	 System.out.println("Region : "+ region);
	 System.out.println("Postal Code : "+ postalCode);
	 System.out.println("Country : "+ country);
	 System.out.println("Country Code : "+ countryCode);
 
 Integer countId=DB.getSQLValue(null,"SELECT C_COUNTRY_ID FROM C_COUNTRY WHERE UPPER(COUNTRYCODE)='"+countryCode.toUpperCase()+"'");
 MCountry countryId=MCountry.get(Env.getCtx(), countId);
 
 System.out.println(countryId.getDisplaySequence());
 
// MRegion regionMaster=new MRegion(countryId, region.toString());
 /// C_Region - C_Region_ID=118
 Integer regionId=DB.getSQLValue(null,"SELECT C_REGION_ID FROM C_REGION WHERE UPPER(NAME)='"+region.toUpperCase()+"'");

/// C_City - C_City_ID=118
Integer cityId=DB.getSQLValue(null,"SELECT C_CITY_ID FROM C_CITY WHERE UPPER(NAME)='"+city.toUpperCase()+"'");
 
 MLocation location=MLocation.getLocationToCheckAllFields(Env.getCtx(), countId, region.toString(),regionId, postalCode, city, cityId, 
		 address1, address2, address3, address4, null);
 
 System.out.println("Location Id "+location);
 
 if(location!=null && location.getC_Location_ID()>0)
 {
	 System.out.println("Location Id "+location.getC_Location_ID());
	 retVal=location.getC_Location_ID();
 }
 else
 {
	 location=new MLocation(Env.getCtx(), countId, regionId, city,localTrx.getTrxName());
	 if(postalCode!=null)
		 location.setPostal(postalCode);
	 
	 if(address1!=null)
		 location.setAddress1(address1);
	 if(address2!=null)
		 location.setAddress2(address2);
	 if(address3!=null)
		 location.setAddress3(address3);
	 if(address4!=null)
		 location.setAddress4(address4);
	 if(location.save(localTrx.getTrxName()))
	 {
		 try {
			DB.commit(true, localTrx.getTrxName());
		} catch (IllegalStateException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 retVal=location.getC_Location_ID();
	 }
 }
 
 	System.out.println(retVal);
 	
	return retVal;
}

/**
 * Load the Header the in Map from excel file
 * @param sheet
 * @return
 */
	private Map<String, Object> getHeaderOfExcelFile(XSSFSheet sheet) {

		Map<String,Object> headerList=new LinkedHashMap<String,Object>();
		int val=sheet.getFirstRowNum();
		System.out.println(val);
		
		XSSFRow ro=	sheet.getRow(val);
		
		for(int i=0;i<sheet.getRow(sheet.getFirstRowNum()).getLastCellNum(); i++)
		{
			Cell cell=ro.getCell(i);
			String headName=getCellDataFromExcel(cell);
			headName = headName.substring(headName.indexOf("[")+1,headName.indexOf("]"));
			headerList.put(String.valueOf(i), headName);
			if(headName.equalsIgnoreCase(panel.getActiveGridTab().getTableName().concat("_ID")))
				primaryKeyIndex=i;
//			headerList.put(String.valueOf(i), getCellDataFromExcel(cell));

		}
		
		return headerList;
	}

/**
 * Load the excel file in Sheet
 * @return
 */
	private XSSFSheet loadWorkBook() {
				
		XSSFSheet sheet=null;
		if(tempFile!=null)
		{
			System.out.println(tempFile.getPath());
			System.out.println(tempFile.canRead());
			
			try {
				FileInputStream fis = new FileInputStream(tempFile);
				//creating workbook instance that refers to .xls file  
				XSSFWorkbook  wb = new XSSFWorkbook(fis);
				//creating a Sheet object to retrieve the object  
				sheet = wb.getSheetAt(0);
				//evaluating cell type
				wb.close();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			FDialog.error(m_WindowNo, this, "FileLoadIssue : ", "File is not exist");
			throwCyprusException("File is not exist");
		}
		return sheet;
	}

	private String getYAndN(String columnName, String str) {

			if(str.toString().equalsIgnoreCase("Yes"))
				str="Y";
			else
				str="N";
			
		return str;
	}
	
	/// Get Id data
	private Integer getDataFromForeignId(String columnName, Object obj) {
		
		String column=columnName.substring(0, columnName.lastIndexOf("_ID"));
		MQuery m_query = new MQuery(column);
		m_query.addRestriction("Name", MQuery.EQUAL, obj);
		Query query=new Query(Env.getCtx(),MTable.get(Env.getCtx(), column), m_query.getWhereClause(), null);
		return query.firstId();
	}

	/// Get Id data by Identifier
	private Integer getDataFromForeignId(String columnName, Object obj, Map<String,Object> identi) {
		
		String column=columnName.substring(0, columnName.lastIndexOf("_ID"));
		System.out.println(columnName+" column :  "+column);
		MQuery m_query = new MQuery(column);
		for(Map.Entry<String, Object> map: identi.entrySet())
		{
			int ind=obj.toString().indexOf("_");
			if(ind>0)
			obj=obj.toString().substring(0, ind);
			System.out.println(map.getValue()+" Where Restriction "+obj);
			m_query.addRestriction(map.getValue().toString(), MQuery.EQUAL, obj);
		}
		System.out.println("m_query.getWhereClause()  "+m_query.getWhereClause());
		
		
		Query query=new Query(Env.getCtx(),MTable.get(Env.getCtx(), column), m_query.getWhereClause(), null);
		return query.firstId();
	}
		
	private String resolveForeignList(MColumn column, String foreignColumn, Object value ,Trx trx) {
		String idS = null;
		String trxName = (trx!=null?trx.getTrxName():null); 
		StringBuilder select = new StringBuilder("SELECT Value FROM AD_Ref_List WHERE ")
			.append(foreignColumn).append("=? AND AD_Reference_ID=? AND IsActive='Y'");
		idS = DB.getSQLValueStringEx(trxName, select.toString(), value, column.getAD_Reference_Value_ID());
		return idS;
	}	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	private Timestamp getTimeStampFrom(Cell obj) {

		DataFormatter dataFormatter = new DataFormatter();
		String cellStringValue = dataFormatter.formatCellValue(obj);
		System.out.println(cellStringValue);
		Date date = obj.getDateCellValue();
		if(date!=null)
		{	
			Timestamp ts=new Timestamp(date.getTime());  
			return ts;
		}
		else
			return null;
	}
	
}
