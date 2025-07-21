package org.cyprus.pos.zk;



import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkCenter;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.webui.apps.form.WGenForm;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.Listbox;
import org.cyprus.webui.component.ListboxFactory;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.editor.WTableDirEditor;
import org.cyprus.webui.editor.WYesNoEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprusbrs.apps.form.Generateresourcechargegen;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Space;

/**
 * Generate Invoice (manual) view class
 * 
 */
public class WGenerateResourceChargesWindowImpl extends Generateresourcechargegen implements IFormController, EventListener, ValueChangeListener
{
	private static WGenForm form;
	

	 
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(WGenerateResourceChargesWindowImpl.class);
	//
	private Label lResource = new Label();
	private WSearchEditor fResource;
	private Label lWorkOrder = new Label();
	private WTableDirEditor fWorkOrder;
	private Label lWorkCenter = new Label();
	private WTableDirEditor fWorkCenter;
	private Label lWarehouse= new Label();
	private WTableDirEditor fWarehouse;
	private Label lScheduledDateFrom= new Label();
	private WDateEditor fScheduledDateFrom;
	private Label lScheduledDateTo= new Label();
	private WDateEditor fScheduledDateTo;
	private Label lSupervisor = new Label();
	private WTableDirEditor fSupervisor;
	private Label     lResourceinoperation = new Label();
	private WYesNoEditor fResourceinoperation;
	private Listbox  cmbDocType = ListboxFactory.newDropdownListbox();
	
	public WGenerateResourceChargesWindowImpl()
	{
		log.info("");
		
		form = new WGenForm(this);
		Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", "Y");
		
		try
		{
			super.dynInit();
			dynInit();
			zkInit();
			
			form.postQueryEvent();
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "init", ex);
		}
	}	//	init
	
	/**
	 *	Static Init.
	 *  <pre>
	 *  selPanel (tabbed)
	 *      fOrg, fBPartner
	 *      scrollPane & miniTable
	 *  genPanel
	 *      info
	 *  </pre>
	 *  @throws Exception
	 */
	void zkInit() throws Exception
	{
		lResource.setText(Msg.translate(Env.getCtx(), "Resource"));
		lWorkOrder.setText(Msg.translate(Env.getCtx(), "MFG_WorkOrder_ID"));
		lWorkCenter.setText(Msg.translate(Env.getCtx(), "MFG_WorkCenter_ID"));
		lWarehouse.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
		lSupervisor.setText("Supervisor");
		lScheduledDateFrom.setText("Scheduled Date From");
		lScheduledDateTo.setText("Scheduled Date To");
		//lResourceinoperation.setText("Resource in Operation");
	
		
		Row row = form.getParameterPanel().newRows().newRow();
		row.appendChild(lResource.rightAlign());
		row.appendChild(fResource.getComponent());
		row.appendChild(new Space());
		row.appendChild(lWorkOrder.rightAlign());
		row.appendChild(fWorkOrder.getComponent());
		row.appendChild(new Space());
		row.appendChild(lWorkCenter.rightAlign());
		row.appendChild(fWorkCenter.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lWarehouse.rightAlign());
		row.appendChild(fWarehouse.getComponent());
		row.appendChild(new Space());
		row.appendChild(lScheduledDateFrom.rightAlign());
		row.appendChild(fScheduledDateFrom.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lSupervisor.rightAlign());
		row.appendChild(fSupervisor.getComponent());
		row.appendChild(new Space());
		row.appendChild(lScheduledDateTo.rightAlign());
		row.appendChild(fScheduledDateTo.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lResourceinoperation.rightAlign());
		row.appendChild(fResourceinoperation.getComponent());
		row.appendChild(new Space());
	}	//	jbInit

	/**
	 *	Fill Picks.
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	public void dynInit() throws Exception
	{
		MLookup resourceL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1402, DisplayType.Search);
		fResource = new WSearchEditor ("M_Product_ID", false, false, true, resourceL);
	//	lOrg.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		fResource.addValueChangeListener(this);
		//
		MLookup woL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1001626, DisplayType.TableDir);
		fWorkOrder = new WTableDirEditor ("MFG_WorkOrder_ID", false, false, true, woL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fWorkOrder.addValueChangeListener(this);
		
		MLookup wcL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1001533, DisplayType.TableDir);
		fWorkCenter = new WTableDirEditor ("MFG_WorkCenter_ID", false, false, true, wcL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fWorkCenter.addValueChangeListener(this);
		
		MLookup whL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1151, DisplayType.TableDir);
		fWarehouse = new WTableDirEditor ("M_Warehouse_ID", false, false, true, whL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fWarehouse.addValueChangeListener(this);
		
		//MLookup sdL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.Date);
		fScheduledDateFrom = new WDateEditor("Scheduled Date From", false, false, true, "Scheduled Date From");
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fScheduledDateFrom.addValueChangeListener(this);
		
	
		
		MLookup spL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 212, DisplayType.TableDir);
		fSupervisor = new WTableDirEditor ("AD_User_ID", false, false, true, spL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fSupervisor.addValueChangeListener(this);
		
		//MLookup sdtLL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.Search);
			fScheduledDateTo = new WDateEditor("Scheduled Date To", false, false, true, "Scheduled Date To");
		//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			fScheduledDateTo.addValueChangeListener(this);
		
	//	MLookup rsL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.Search);
		fResourceinoperation = new WYesNoEditor("Resource In Operation", "Resource In Operation", "", false, false, true);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fResourceinoperation.addValueChangeListener(this);
		
		cmbDocType.addItem(new KeyNamePair(MMFGWorkOrder.Table_ID, Msg.translate(Env.getCtx(), "Work Order")));
        cmbDocType.addItem(new KeyNamePair(MMFGWorkCenter.Table_ID, Msg.translate(Env.getCtx(), "Work Center")));
        cmbDocType.addActionListener(this);
        cmbDocType.setSelectedIndex(0);
		

	}	//	fillPicks
    
	/**
	 *  Query Info
	 */
	public void executeQuery()
	{
		KeyNamePair docTypeKNPair = cmbDocType.getSelectedItem().toKeyNamePair();
	//	KeyNamePair docTypeKNPair = fWorkOrder.getSelectedItem().toKeyNamePair();
		
		executeQuery(docTypeKNPair, form.getMiniTable());
		form.getMiniTable().repaint();
		form.invalidate();
	}   //  executeQuery

	/**
	 *	Action Listener
	 *  @param e event
	 */
	public void onEvent(Event e)
	{
		log.info("Cmd=" + e.getTarget().getId());
		//
		if(cmbDocType.equals(e.getTarget()))
		{
		    form.postQueryEvent();
		    return;
		}
		
		//
		validate();
	}	//	actionPerformed
	
	public void validate()
	{
		form.saveSelection();
		
		ArrayList<Integer> selection = getSelection();
		if (selection != null && selection.size() > 0 && isSelectionActive())
			form.generate();
		else
			form.dispose();
	}

	/**
	 *	Value Change Listener - requery
	 *  @param e event
	 */
	public void valueChange(ValueChangeEvent e)
	{
		log.info(e.getPropertyName() + "=" + e.getNewValue());
		if (e.getPropertyName().equals("M_Product_ID"))
			m_M_Product_ID = e.getNewValue();
		if (e.getPropertyName().equals("MFG_WorkOrder_ID"))
		{
			m_MFG_WorkOrder_ID = e.getNewValue();
		
		}
		if (e.getPropertyName().equals("MFG_WorkCenter_ID"))
		{
			m_MFG_WorkCenter_ID = e.getNewValue();
			
		}
		if (e.getPropertyName().equals("M_Warehouse_ID"))
		{
			m_M_Warehouse_ID = e.getNewValue();
			
		}
		if (e.getPropertyName().equals("Scheduled Date From"))
		{
			m_ScheduledDateFrom = e.getNewValue();
			
		}
		if (e.getPropertyName().equals("AD_User_ID"))
		{
			m_Supervisor = e.getNewValue();
			
		}
		if (e.getPropertyName().equals("Scheduled Date To"))
		{
			m_ScheduledDateTo = e.getNewValue();
			
		}
		form.postQueryEvent();
	}	//	vetoableChange
	
	
	public String generate()
	{
		
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem().toKeyNamePair();
		
		return generate(form.getStatusBar(), docTypeKNPair);
	}	//	

	
	public ADForm getForm()
	{
		return form;
	}
	

}