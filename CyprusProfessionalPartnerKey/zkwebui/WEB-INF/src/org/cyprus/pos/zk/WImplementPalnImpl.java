package org.cyprus.pos.zk;




import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.webui.apps.form.WGenForm;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.Listbox;
import org.cyprus.webui.component.ListboxFactory;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.editor.WTableDirEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprusbrs.apps.form.Implementplangen;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MOrder;
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
public class WImplementPalnImpl extends Implementplangen implements IFormController, EventListener, ValueChangeListener
{
	private static WGenForm form;
	

	 
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(WGenerateResourceChargesWindowImpl.class);
	//
	private Label lPlan = new Label();
	private WTableDirEditor fPlan;
	private Label lProduct = new Label();
	private WSearchEditor fProduct;
	private Label lPeriod = new Label();
	private WTableDirEditor fPeriod;
	private Label lOrderType= new Label();
	private Listbox  OrderType = ListboxFactory.newDropdownListbox();
	private Label lOrderDate= new Label();
	private WDateEditor fOrderDate;
	private Label lBPartner = new Label();
	private WTableDirEditor fBPartner;

	
	public WImplementPalnImpl()
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
		lPlan.setText(Msg.translate(Env.getCtx(), "MRP_Plan_ID"));
		lProduct.setText(Msg.translate(Env.getCtx(), "M_Product_ID"));
		lPeriod.setText(Msg.translate(Env.getCtx(), "C_Period_ID"));
		lOrderDate.setText("Order Date");
		lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		
		Row row = form.getParameterPanel().newRows().newRow();
		row.appendChild(lPlan.rightAlign());
		row.appendChild(fPlan.getComponent());
		row.appendChild(new Space());
		row.appendChild(lProduct.rightAlign());
		row.appendChild(fProduct.getComponent());
		row.appendChild(new Space());
		row.appendChild(lPeriod.rightAlign());
		row.appendChild(fPeriod.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lOrderType.rightAlign());
//		row.appendChild(fOrderType.getComponent());
		row.appendChild(OrderType);
		row.appendChild(new Space());
		row.appendChild(lOrderDate.rightAlign());
		row.appendChild(fOrderDate.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lBPartner.rightAlign());
		row.appendChild(fBPartner.getComponent());
		row.appendChild(new Space());
	}	//	jbInit

	/**
	 *	Fill Picks.
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	public void dynInit() throws Exception
	{
		MLookup planL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1001988, DisplayType.TableDir);
		fPlan = new WTableDirEditor ("MRP_Plan_ID", false, false, true, planL);
		fPlan.addValueChangeListener(this);
		//
		MLookup productL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1402, DisplayType.Search);
		fProduct = new WSearchEditor ("M_Product_ID", false, false, true, productL);
		fProduct.addValueChangeListener(this);
		
		MLookup periodL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 837, DisplayType.TableDir);
		fPeriod = new WTableDirEditor ("C_Period_ID", false, false, true, periodL);
		fPeriod.addValueChangeListener(this);
		
		fOrderDate = new WDateEditor("Order Date", false, false, true, "Order Date");
		fOrderDate.addValueChangeListener(this);
		
	
		
		MLookup bpartnerL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.TableDir);
		fBPartner = new WTableDirEditor ("C_BPartner_ID", false, false, true, bpartnerL);
		fBPartner.addValueChangeListener(this);
		
		lOrderType.setText("Order Type");
		OrderType.addItem(new KeyNamePair(MOrder.Table_ID, Msg.translate(Env.getCtx(), "Purchase Order")));
		OrderType.addItem(new KeyNamePair(MMFGWorkOrder.Table_ID, Msg.translate(Env.getCtx(), "Work Order")));
        OrderType.addActionListener(this);
        OrderType.setSelectedIndex(0);
		

	}	//	fillPicks
    
	/**
	 *  Query Info
	 */
	public void executeQuery()
	{
		KeyNamePair docTypeKNPair = OrderType.getSelectedItem().toKeyNamePair();
		
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
		if(OrderType.equals(e.getTarget()))
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
		if (e.getPropertyName().equals("MRP_Plan_ID"))
			m_MRP_Plan_ID = e.getNewValue();
		if (e.getPropertyName().equals("M_Product_ID"))
		{
			m_M_Product_ID = e.getNewValue();
		
		}
		if (e.getPropertyName().equals("C_Period_ID"))
		{
			m_C_Period_ID = e.getNewValue();
			
		}
		if (e.getPropertyName().equals("Order Date"))
		{
			m_OrderDate = e.getNewValue();
			
		}
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			m_C_BPartner_ID = e.getNewValue();
			
		}
		
		form.postQueryEvent();
	}	//	vetoableChange
	
	
	public String generate()
	{
		
		KeyNamePair docTypeKNPair = (KeyNamePair)OrderType.getSelectedItem().toKeyNamePair();
		
		return generate(form.getStatusBar(), docTypeKNPair);
	}	//	

	
	public ADForm getForm()
	{
		return form;
	}
	

}
