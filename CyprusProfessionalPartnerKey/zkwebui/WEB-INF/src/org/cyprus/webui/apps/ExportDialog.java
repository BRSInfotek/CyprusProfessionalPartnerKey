package org.cyprus.webui.apps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.cyprus.impexp.GridTabExcelExporter;
import org.cyprus.impexp.GridTabXLSXExporter;
import org.cyprus.impexp.IGridTabExporter;
import org.cyprus.webui.component.Checkbox;
import org.cyprus.webui.component.ConfirmPanel;
import org.cyprus.webui.component.IADTab;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.ListItem;
import org.cyprus.webui.component.Listbox;
import org.cyprus.webui.component.Window;
import org.cyprus.webui.panel.AbstractADWindowPanel;
import org.cyprus.webui.panel.IADTabpanel;
import org.cyprus.webui.window.FDialog;
import org.cyprusbrs.framework.MRole;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.model.MTab;
import org.cyprusbrs.process.ProcessInfo;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;

public class ExportDialog extends Window implements EventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean m_autoStart;
	private AbstractADWindowPanel panel;
	private int m_WindowNo;
	private Properties m_ctx;
	
	private ProcessInfo m_pi = null;

	private static CLogger log = CLogger.getCLogger(ExportDialog.class);
	
	private Window winExportFile = null;
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private Listbox cboType = new Listbox();
	private boolean				m_isCanExport;
	private Checkbox chkCurrentRow = new Checkbox();
	private List<MTab> childsOld=null;
	private List<GridTab> childs;
	private int indxDetailSelected = 0;
	private List<Checkbox> chkSelectionTabForExport = null;
	private IGridTabExporter exporter;


	
	public ExportDialog(AbstractADWindowPanel panel, int WindowNo, ProcessInfo pi, boolean autoStart)
	{
		System.out.println(panel.getWindowNo()+" WindowNo "+WindowNo+" autoStart  "+autoStart);
		m_ctx = Env.getCtx();
		this.panel = panel;
		m_WindowNo = WindowNo;
		m_pi = pi;
		m_autoStart = autoStart;
		m_isCanExport = MRole.getDefault().isCanExport(pi.getTable_ID());
		System.out.println("3. m_isCanExport "+m_isCanExport);
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
		if(event.getTarget().getId().equals(ConfirmPanel.A_CANCEL))
			winExportFile.onClose();
		else if(event.getTarget().getId().equals(ConfirmPanel.A_OK))			
			exportFile();
		
	}
	
	/**
	 * Export the file in xls and other format
	 */
	private void exportFile() {

//		FDialog.error(m_WindowNo, winExportFile, "Work in progress...");
		try
		{
			ListItem li = cboType.getSelectedItem();
			
			System.out.println(li.getValue());
			if(li == null || li.getValue() == null)
			{
				FDialog.error(m_WindowNo, winExportFile, "FileInvalidExtension");
				return;
			}
			
			String ext = li.getValue().toString();
			
			byte[] data = null;
			File inputFile = null;
			inputFile = File.createTempFile("Export", "."+cboType.getSelectedItem().getValue().toString());
			System.out.println(" cboType.getSelectedItem().getValue().toString() :: "+cboType.getSelectedItem().getValue().toString());
			
			
			if(childs!=null)
			{	
				childs.clear();

				for (Checkbox chkSeletionTab : chkSelectionTabForExport){
					if (chkSeletionTab.isChecked()){
						childs.add((GridTab)chkSeletionTab.getAttribute("tabBinding"));
					}
				}

				System.out.println(" ************ Need to export others tab fields ****************");
				childs.forEach(tab->
				{
					System.out.println(tab);
				});
			}
			/**
			 * Test to add different tab
			 */			
			
			if (ext.equals("xls"))
			{
				//				inputFile = File.createTempFile("Export", ".xls");
				boolean currentRowOnly = chkCurrentRow.isSelected();
				System.out.println(inputFile.getName()+"  panel.getActiveGridTab().getCurrentRow() ***************** "+
						panel.getActiveGridTab().getCurrentRow()+" chkCurrentRow.isSelected() "+chkCurrentRow.isSelected());
				GridTab grid=panel.getActiveGridTab();
				
				exporter=new GridTabExcelExporter(m_ctx, grid,false);
				exporter.export(grid, childs, currentRowOnly, inputFile, indxDetailSelected);
			}
			else if (ext.equals("xlsx"))
			{
//				inputFile = File.createTempFile("Export", ".xls");
				boolean currentRowOnly = chkCurrentRow.isSelected();
				System.out.println(inputFile.getName()+"****************************  panel.getActiveGridTab().getCurrentRow() ***************** "+
						panel.getActiveGridTab().getCurrentRow()+" chkCurrentRow.isSelected() "+chkCurrentRow.isSelected());
				GridTab grid=panel.getActiveGridTab();
				
				exporter=new GridTabXLSXExporter(m_ctx, grid,true);
				exporter.export(grid, childs, currentRowOnly, inputFile, indxDetailSelected);
				
				System.out.println("Path "+inputFile.getAbsolutePath());
			}
			else
			{
				FDialog.error(m_WindowNo, winExportFile, "FileInvalidExtension");
				return;
			}
			
			
			winExportFile.onClose();
			AMedia media = null;
			System.out.println(exporter.getSuggestedFileName(panel.getActiveGridTab())+" exporter.getContentType() "+exporter.getContentType());
			media = new AMedia(exporter.getSuggestedFileName(panel.getActiveGridTab()), null, exporter.getContentType(), inputFile, true);
//			Filedownload.save(media, inputFile.getName() + "." + ext);
			Filedownload.save(media);

		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Failed to export content.", e);
		}
	}

	private void init() {
		log.config("");
		if (!m_isCanExport)
		{
			FDialog.error(m_WindowNo, this, "AccessCannotExport", getTitle());
			return;
		}

		if(winExportFile == null)
		{
			System.out.println("4. m_isCanExport "+m_isCanExport);

			winExportFile = new Window();
			winExportFile.setTitle(Msg.getMsg(m_ctx, "Export") + ": " + getTitle());
			winExportFile.setWidth("450px");
			winExportFile.setClosable(true);
			winExportFile.setBorder("normal");
			winExportFile.setStyle("position:absolute");

			cboType.setMold("select");

			cboType.getItems().clear();			
//			cboType.appendItem("ps" + " - " + Msg.getMsg(m_ctx, "FilePS"), "ps");
//			cboType.appendItem("xml" + " - " + Msg.getMsg(m_ctx, "FileXML"), "xml");
//			ListItem li = cboType.appendItem("pdf" + " - " + Msg.getMsg(m_ctx, "FilePDF"), "pdf");
//			cboType.appendItem("html" + " - " + Msg.getMsg(m_ctx, "FileHTML"), "html");
//			cboType.appendItem("ssv" + " - " + Msg.getMsg(m_ctx, "FileSSV"), "ssv");
			
			ListItem li = cboType.appendItem("xlsx" + " - " + Msg.getMsg(m_ctx, "FileXLSX"), "xlsx");
			cboType.appendItem("xls" + " - " + Msg.getMsg(m_ctx, "FileXLS"), "xls");
			cboType.setSelectedItem(li);

			Hbox hb = new Hbox();
			Div div = new Div();
			div.setAlign("right");
			div.appendChild(new Label("Files of Type: "));
			hb.appendChild(div);
			hb.appendChild(cboType);
			cboType.setWidth("100%");
			
			Vbox vb = new Vbox();
			vb.setWidth("390px");
			winExportFile.appendChild(vb);
			vb.appendChild(hb);
			vb.appendChild(confirmPanel);	
			confirmPanel.addActionListener(this);
			
			winExportFile.appendChild(new Space());
			chkCurrentRow.setText(Msg.getMsg(m_ctx, "ExportCurrentRowOnly"));
			chkCurrentRow.setSelected(true);
			winExportFile.appendChild(chkCurrentRow);
			
//			displayExportTabSelection();
			panel.getComponent().getParent().appendChild(winExportFile);
//			panel.showBusyMask(winExportFile);  // Need to Ask to Anshul
//			LayoutUtils.openOverlappedWindow(panel.getComponent(), winExportFile, "middle_center");
			winExportFile.addEventListener("onExporterException", this);
			winExportFile.focus();
			
			
//			GridTab grid =panel.getActiveGridTab();
//			
//			System.out.println(panel.getTitle()+" grid "+grid);
				
			
//			List<GridTab> includedList = panel.getActiveGridTab().getIncludedTabs();
//			Integer windowID=panel.getActiveGridTab().getAD_Window_ID();
//			System.out.println(windowID+" includedList "+includedList+"  m_WindowNo "+m_WindowNo);
//			
//			MWindow mWindow=new MWindow(m_ctx, windowID,null);
//			MTab[] tabs=mWindow.getTabs(true, null);
//			System.out.println(tabs.length);
//			Set<String> tables = new HashSet<String>();
//			childsOld = new ArrayList<MTab>();
//			
//			for(MTab tab: tabs)
//			{
//				childsOld.add(tab);
//				String tableName = tab.get_TableName();
//				if (tables.contains(tableName))
//					continue;
//				tables.add(tableName);
////				childs.add(tab);
//			}
//			
//			System.out.println("List of tab ");
//			childsOld.forEach(tab->{
//				System.out.println(tab.getName()+" "+tab.get_TableName()+" "+tab.get_Table_ID());
//			});
			
			

			// for to make each export tab with one checkbox
//			for (MTab child : childsOld){
//				Checkbox chkSelectionTab = new Checkbox();
//				chkSelectionTab.setLabel(child.getName());
//				// just allow selection tab can export
//				
//				chkSelectionTab.setAttribute("tabBinding", child);
//				winExportFile.appendChild(new Separator());
//				winExportFile.appendChild(chkSelectionTab);
//				
//				chkSelectionTab.addEventListener(Events.ON_CHECK, this);
//			}

		}
		AEnv.showCenterScreen(winExportFile);
		
}

	/**
	 * This method is use to display all required linked tab check box
	 */
	private void displayExportTabSelection() {
		// TODO Auto-generated method stub
		initTabInfo ();
		System.out.println(childs);
		if(childs!=null)
		System.out.println("Size of childs tab in displayExportTabSelection : "+childs.size());
		
		System.out.println("^^^^^^^^^^^^^^^^^Added GridTab Before display^^^^^^^^^^^^^^");
		childs.forEach(gr->{
			System.out.println(gr);
		});
		
		// for to make each export tab with one checkbox
		winExportFile.appendChild(new Separator());

		winExportFile.appendChild(new Separator());
		winExportFile.appendChild(new Label(Msg.getMsg(Env.getCtx(), "SelectTabToExport")));
		winExportFile.appendChild(new Separator());
		
		
		chkSelectionTabForExport = new ArrayList<Checkbox> ();

		
		for (GridTab child : childs){
			Checkbox chkSelectionTab = new Checkbox();
			chkSelectionTab.setLabel(child.getName());
			chkSelectionTab.setAttribute("tabBinding", child);
			winExportFile.appendChild(new Separator());
			winExportFile.appendChild(chkSelectionTab);
			chkSelectionTabForExport.add(chkSelectionTab);

			chkSelectionTab.addEventListener(Events.ON_CHECK, this);
		}
		
	}

	/**
	 * Initial tab information while we load the export popup
	 */
	private void initTabInfo() {
		IADTab adTab = panel.getADTab();
		int selected = adTab.getSelectedIndex();
		int tabLevel = panel.getActiveGridTab().getTabLevel();
		Set<String> tables = new HashSet<String>();
		childs = new ArrayList<GridTab>();
		
		List<GridTab> linkedAllList = panel.getActiveGridTab().getLinkedTabs();
		System.out.println("Size : "+linkedAllList.size());
		linkedAllList.forEach(gt->
		{
			System.out.println(gt);
		});
				
		GridTab gridTab = panel.getActiveGridTab();
		System.out.println("Grid Tab Information : "+gridTab);
		
		for(GridTab included : linkedAllList)
		{
			String tableName = included.getTableName();
			System.out.println("Table Name : "+tableName+"  "+included);
			if (tables.contains(tableName))
				continue;
			tables.add(tableName);
			childs.add(included);
		}
		System.out.println("****** before loop In selected for loop***** " +childs.size());
		childs.forEach(gt->
		{
			System.out.println(gt);
		});
		
		System.out.println("****** before loop In selected for loop***** " +adTab.getTabCount());
		for(int i = selected+1; i < adTab.getTabCount(); i++)
		{
			System.out.println("******In selected for loop*****");
			IADTabpanel adTabPanel = adTab.getADTabpanel(i);
			if (adTabPanel.getGridTab().isSortTab())
				continue;
			if (adTabPanel.getGridTab().getTabLevel() <= tabLevel)
				break;
			String tableName = adTabPanel.getGridTab().getTableName();
			System.out.println("******In selected for loop ***** "+tableName);
			if (tables.contains(tableName))
				continue;
			tables.add(tableName);
			childs.add(adTabPanel.getGridTab());
		}
		System.out.println("****** After loop In selected for loop***** " +childs.size());
		childs.forEach(gt->
		{
			System.out.println(gt);
		});
		indxDetailSelected = 0;
		if( adTab.getSelectedTabpanel()!=null )
			indxDetailSelected = adTab.getSelectedTabpanel().getGridTab().getTabNo();
		
	}

}
