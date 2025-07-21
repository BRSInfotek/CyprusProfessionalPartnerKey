/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.cyprus.webui.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.Listbox;
import org.cyprus.webui.component.ListboxFactory;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.editor.WTableDirEditor;
import org.cyprus.webui.editor.WYesNoEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprusbrs.apps.form.PlanImplGen;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MRequisition;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Space;

/**
 * Generate Shipment (manual) view class
 * 
 */
public class WPlanImplGen extends PlanImplGen implements IFormController, EventListener, ValueChangeListener
{
	private static WGenForm form;
	private static final String s_none = "----";	//	no format indicator

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(WPlanImplGen.class);
	//
	private Label lMasterDemand = new Label();
	private WTableDirEditor fMasterDemand;
	private Label lPeriod = new Label();
	private WTableDirEditor fPeriod;
	private Label dateTrxLabel = new Label();
	private WDateEditor dateTrx ;
	
	private Label lManufacturingOrder = new Label();
	private WYesNoEditor manufacturingOrder;

	private Label lRequisitionOrder = new Label();
	private WYesNoEditor requisitionOrder;
	
	private Label lPurchaseOrder = new Label();
	private WYesNoEditor purchaseOrder;

	//private Label lBPartner = new Label();
	//private WSearchEditor fBPartner;
	private Label     lDocType = new Label();
	private Listbox pickFormat = new Listbox();
	private Listbox  cmbDocType = ListboxFactory.newDropdownListbox();
	//private Label   lDocAction = new Label();
	//private WTableDirEditor docAction;
	
	public WPlanImplGen()
	{
		log.info("");
		
		form = new WGenForm(this);
		Env.setContext(Env.getCtx(), form.getWindowNo(), "IsSOTrx", "Y");
		form.setLabelName("Generated Order Type"); // Set Review as label Name
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
		//lBPartner.setText("BPartner");
		dateTrxLabel.setText(Msg.translate(Env.getCtx(), "Transaction Date"));

		Row row = form.getParameterPanel().newRows().newRow();
		row.appendChild(lMasterDemand.rightAlign());
		row.appendChild(fMasterDemand.getComponent());
		row.appendChild(new Space());
		
		row.appendChild(lPeriod.rightAlign());
		row.appendChild(pickFormat);
		//row.appendChild(fPeriod.getComponent());
		row.appendChild(new Space());
		
		row.appendChild(dateTrxLabel.rightAlign());
		row.appendChild(dateTrx.getComponent());
		row.appendChild(new Space());
//		row.appendChild(lBPartner.rightAlign());
//		row.appendChild(fBPartner.getComponent());
//		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
//		row.appendChild(lDocType.rightAlign());
//		row.appendChild(cmbDocType);
//		row.appendChild(new Space());
		
		//Manufacturing Order
		row.appendChild(lManufacturingOrder.rightAlign());
		row.appendChild(manufacturingOrder.getComponent());
		row.appendChild(new Space());
		
		
		//Manufacturing Order
		row.appendChild(lRequisitionOrder.rightAlign());
		row.appendChild(requisitionOrder.getComponent());
		row.appendChild(new Space());
				
		//Manufacturing Order
		row.appendChild(lPurchaseOrder.rightAlign());
		row.appendChild(purchaseOrder.getComponent());
		row.appendChild(new Space());		
		
		//row.appendChild(lDocAction.rightAlign());
		//row.appendChild(docAction.getComponent());
		// row.appendChild(new Space());
		
	}	//	jbInit

	/**
	 *	Fill Picks.
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	public void dynInit() throws Exception
	{
		MLookup demandL = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), MColumn.getColumn_ID("M_MasterDemand", "M_MasterDemand_ID"),
				DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "M_MasterDemand_ID", 0 /* _Document Action */,
				false, " isFrozen='Y' ");
	   // System.out.println(demandL.getColumnName()+"  "+demandL.getValidation());
		//	M_MasterDemand.M_MasterDemand_ID
		//MLookup demandL = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, MColumn.getColumn_ID("M_MasterDemand", "M_MasterDemand_ID"), DisplayType.TableDir);
		demandL.setMandatory(true);
		fMasterDemand = new WTableDirEditor ("M_MasterDemand_ID", true, false, true, demandL);
		lMasterDemand.setText(Msg.translate(Env.getCtx(), "Master Demand"));
		fMasterDemand.addValueChangeListener(this);
		fMasterDemand.setValue(Env.getContextAsInt(Env.getCtx(), "#M_MasterDemand_ID"));
		setM_MasterDemand_ID(fMasterDemand.getValue());
			
		// Transaction Date
		dateTrx = new WDateEditor("DateTrx", true, false, true, "Transaction Date");
		dateTrx.setValue(new Timestamp(System.currentTimeMillis()));
		// Manufacturing Order checks
		manufacturingOrder = new WYesNoEditor("Manufacturing", Msg.getMsg(Env.getCtx(), "Manufacturing Order", true),
				null, true, false, true);
		manufacturingOrder.setValue(true);
		
		// Requisition Order 
		
		// Requisition Order checks
		requisitionOrder = new WYesNoEditor("Requisition", Msg.getMsg(Env.getCtx(), "Purchase Requisition", true),
				null, true, false, true);
		requisitionOrder.addValueChangeListener(this);
		requisitionOrder.setValue(true);
		
		// Purchase Order checks
		purchaseOrder = new WYesNoEditor("Purchase", Msg.getMsg(Env.getCtx(), "Purchase Order", true),
				null, true, false, true);
		purchaseOrder.addValueChangeListener(this);
		purchaseOrder.setValue(false);
		///purchaseOrder.setReadWrite();
		
		
		pickFormat.setMold("select");
		pickFormat.setRows(0);
		pickFormat.setSelectedIndex(0);
		pickFormat.addEventListener(Events.ON_SELECT, this);
		
		//Document Type Sales Order/Vendor RMA
		lDocType.setText(Msg.translate(Env.getCtx(), "C_DocType_ID"));
		cmbDocType.addItem(new KeyNamePair(MRequisition.Table_ID, Msg.translate(Env.getCtx(), "Requisition")));
		cmbDocType.addItem(new KeyNamePair(MOrder.Table_ID, Msg.translate(Env.getCtx(), "Purchase Order")));
		cmbDocType.addActionListener(this);
		cmbDocType.setSelectedIndex(0);
		
		form.getStatusBar().setStatusLine(Msg.getMsg(Env.getCtx(), "InOutGenerateSel"));//@@
	}	//	fillPicks
    
	/**
	 *  Query Info
	 */
	public void executeQuery()
	{
		String periodID="";
//		executeQuery(docTypeKNPair, form.getMiniTable());
		executeQueryMasterDemand(periodID, form.getMiniTable());
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
		else if (e.getTarget() == pickFormat)
		{
			KeyNamePair docTypeKNPair=null; 
			try {
				docTypeKNPair=pickFormat.getSelectedItem().toKeyNamePair();
			}catch(Exception exception) {
				System.out.println(" Error Message "+exception.getMessage());
			}
			if(docTypeKNPair!=null)
			setC_Period_ID(docTypeKNPair.getKey());
			else
			setC_Period_ID(0);	
		
			form.postQueryEvent();
			return;
		}
		
		//
		validate();
	}	//	actionPerformed
	
	public void validate()
	{
//		if (isSelectionActive() && getM_Warehouse_ID() <= 0)
//		{
//			throw new WrongValueException(fMasterDemand.getComponent(), Msg.translate(Env.getCtx(), "FillMandatory"));
//		}
		form.saveSelection();
		
		ArrayList<Integer> selection = getSelection();
		if (selection != null
			&& selection.size() > 0
			&& isSelectionActive() )	//	on selection tab
		{
			//form.generate();
			form.generateManufacturing();
		}
		else
		{
			form.dispose();
		}
	}

	/**
	 *	Value Change Listener - requery
	 *  @param e event
	 */
	public void valueChange(ValueChangeEvent e)
	{
			
		log.info(e.getPropertyName() + "=" + e.getNewValue());
		form.getStatusBar().setStatusLine(" Get new Value "+e.getNewValue());

		if (e.getPropertyName().equals("M_Warehouse_ID"))
			setM_Warehouse_ID(e.getNewValue());
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			m_C_BPartner_ID = e.getNewValue();
			///fBPartner.setValue(m_C_BPartner_ID);	//	display value
		}
		if (e.getPropertyName()!=null && e.getPropertyName().equals("M_MasterDemand_ID"))
		{
			if(getC_Period_ID()>0)
				setC_Period_ID(null);
			
			setM_MasterDemand_ID(e.getNewValue());
		
			lPeriod.setText(Msg.translate(Env.getCtx(), "Period"));
			pickFormat.removeAllItems();
//			Load Formats
			pickFormat.addItem (new KeyNamePair(0,s_none)) ;//(s_none, s_none);
			
			String sql =" SELECT DISTINCT ml.C_Period_ID, cp.NAME FROM M_MasterDemandLine ml "
					+" INNER JOIN C_Period cp ON (cp.C_Period_ID=ml.C_Period_ID) "
					+" WHERE M_MasterDemand_ID="+getM_MasterDemand_ID();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				 pstmt = DB.prepareStatement(sql, null);
				 rs = pstmt.executeQuery();
			
				while (rs.next())
					pickFormat.addItem(new KeyNamePair(rs.getInt("C_Period_ID"),rs.getString("NAME")));
				
				rs.close();
				pstmt.close();
			}
			catch (SQLException e1)
			{
				log.log(Level.SEVERE, sql, e1);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}		
		}
		
		if (e.getPropertyName().equals("Purchase"))
		{
			Boolean check=(Boolean) e.getNewValue();
			if(check)
				requisitionOrder.setValue(false);		
		}
		if (e.getPropertyName().equals("Requisition"))
		{
			Boolean check=(Boolean) e.getNewValue();
			if(check)
				purchaseOrder.setValue(false);		
		}
				
		form.postQueryEvent();
	}	//	vetoableChange
	

	/**
	 *  Query Info
	 */
	public void executeQueryMasterDemand()
	{
		String periodID="";
		if(pickFormat.getSelectedItem()!=null)
		{	
			KeyNamePair docTypeKNPair = pickFormat.getSelectedItem().toKeyNamePair();
			periodID=docTypeKNPair.getKey()+"";
		}
		System.out.println(" periodID "+periodID);
		//KeyNamePair docTypeKNPair = cmbDocType.getSelectedItem().toKeyNamePair();
		executeQueryMasterDemand(periodID, form.getMiniTable());
		form.getMiniTable().repaint();
		form.invalidate();
		System.out.println(" <Period Value close> ");
	}   //  executeQuery
	
	/**************************************************************************
	 *	Generate Shipments
	 */
	public String generate()
	{
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem().toKeyNamePair();
		String docActionSelected = "";//(String)docAction.getValue();	
		return generate(form.getStatusBar(), docTypeKNPair, docActionSelected);
	}	//	generateShipments
	
	
	public String generateManufacturing()
	{
		java.util.Map<String,Object> orderType=new HashMap<String,Object>();
		orderType.put("M", manufacturingOrder.getValue());
		orderType.put("P", purchaseOrder.getValue());
		orderType.put("R", requisitionOrder.getValue());
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem().toKeyNamePair();
		String docActionSelected = " DR ";//(String)docAction.getValue();
		String getData=generateManufacturing(form.getStatusBar(),getSelection(),form.getMiniTable(),getSelectedDataList(),orderType);
		return "Plan Implementation Created : "+getData;
	}
	
	public ADForm getForm()
	{
		return form;
	}
}