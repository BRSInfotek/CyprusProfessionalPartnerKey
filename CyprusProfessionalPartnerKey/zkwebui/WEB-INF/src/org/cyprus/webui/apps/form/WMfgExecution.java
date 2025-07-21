
package org.cyprus.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.Listbox;
import org.cyprus.webui.component.Row;
import org.cyprus.webui.editor.WDateEditor;
import org.cyprus.webui.editor.WNumberEditor;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.editor.WTableDirEditor;
import org.cyprus.webui.event.ValueChangeEvent;
import org.cyprus.webui.event.ValueChangeListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprus.webui.window.FDialog;
import org.cyprusbrs.apps.TimeRecordDTO;
import org.cyprusbrs.apps.form.MFGExecution;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MWarehouse;
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
 * Manufacturing Execution view class
 * 
 */
public class WMfgExecution extends MFGExecution implements IFormController, EventListener, ValueChangeListener
{
	private static WGenForm form;
		
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(WMfgExecution.class);
	
	 private List<TimeRecordDTO> timeRecords = new ArrayList<>();
	
	private static final String s_none = "----";
	//
	private Label lOrder = new Label();
	private WSearchEditor fOrder;
	private Label lTrxDate = new Label();
	private WDateEditor fTrxDate = new WDateEditor("TransactionDate", true, false, true, "TransactionDate");
	private Label lActivity = new Label();
	private Label lOperation = new Label();
	private WSearchEditor fOperation;
	private Label lBom = new Label();
	private WTableDirEditor fBom;
	private Label lWorkflow = new Label();
	private WTableDirEditor fWorkflow;
	private Label lWarehouse = new Label();
	private WTableDirEditor fWarehouse;
	private Label lProduct = new Label();
	private WSearchEditor fProduct;
	private Label     lTrxType = new Label();
	private WTableDirEditor  cmbTrxType;
	private Label     lQty = new Label();
	private WNumberEditor fQtyField = new WNumberEditor("Qty", false, false, true, 29, "Qty");
	private Label lLocator = new Label();
	 private Listbox pickActivity = new Listbox();
	 private Listbox pickLocator = new Listbox();
	
	public WMfgExecution()
	{
		log.info("");
		
		form = new WGenForm(this);
		
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
		lOrder.setText(Msg.translate(Env.getCtx(), "PP_Order_ID"));
		lOperation.setText(Msg.translate(Env.getCtx(), "M_Operation_ID"));
		lBom.setText(Msg.translate(Env.getCtx(), "PP_Product_BOM_ID"));
		lWorkflow.setText(Msg.translate(Env.getCtx(), "AD_Workflow_ID"));
		lWarehouse.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
		lProduct.setText(Msg.translate(Env.getCtx(), "M_Product_ID"));
		lLocator.setText(Msg.translate(Env.getCtx(), "M_Locator_ID"));
		lTrxType.setText("Transaction Type");
		lTrxDate.setText("Transaction Date");
		lActivity.setText("Activity");
		lQty.setText(Msg.translate(Env.getCtx(), "Qty"));
		
		Row row = form.getParameterPanel().newRows().newRow();
		row.appendChild(lOrder.rightAlign());
		row.appendChild(fOrder.getComponent());
		row.appendChild(new Space());
		row.appendChild(lTrxDate.rightAlign());
		row.appendChild(fTrxDate.getComponent());
		row.appendChild(new Space());
		row.appendChild(lProduct.rightAlign());
		row.appendChild(fProduct.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lBom.rightAlign());
		row.appendChild(fBom.getComponent());
		row.appendChild(new Space());
		row.appendChild(lWorkflow.rightAlign());
		row.appendChild(fWorkflow.getComponent());
		row.appendChild(new Space());
		row.appendChild(lQty.rightAlign());
		row.appendChild(fQtyField.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lTrxType.rightAlign());
		row.appendChild(cmbTrxType.getComponent());
		row.appendChild(new Space());
		row.appendChild(lActivity.rightAlign());
		row.appendChild(pickActivity);
		row.appendChild(new Space());
		row.appendChild(lOperation.rightAlign());
		row.appendChild(fOperation.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lWarehouse.rightAlign());
		row.appendChild(fWarehouse.getComponent());
		row.appendChild(new Space());
		row.appendChild(lLocator.rightAlign());
		row.appendChild(pickLocator);
		row.appendChild(new Space());
		
		
	}	//	jbInit

	/**
	 *	Fill Picks.
	 *		Column_ID from PP_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	public void dynInit() throws Exception
	{
		MLookup orderL = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), MColumn.getColumn_ID("PP_Order", "PP_Order_ID"),
				DisplayType.Search, Env.getLanguage(Env.getCtx()), "PP_Order_ID", 0 /* _Document Action */,
				false, " DocStatus='CO' ");
		fOrder = new WSearchEditor ("PP_Order_ID", false, false, true, orderL);
		fOrder.addValueChangeListener(this);
		
		MLookup operationL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1002341, DisplayType.Search);
		fOperation = new WSearchEditor ("M_Operation_ID", false, true, true, operationL);
		fOperation.addValueChangeListener(this);
		
		MLookup bomL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 53334, DisplayType.TableDir);
		fBom = new WTableDirEditor ("PP_Product_BOM_ID", false, true, true, bomL);
		fBom.addValueChangeListener(this);
		
		MLookup workflowL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 351, DisplayType.TableDir);
		fWorkflow = new WTableDirEditor ("AD_Workflow_ID", false, true, true, workflowL);
		fWorkflow.addValueChangeListener(this);
		
		MLookup warehouseL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1151, DisplayType.TableDir);
		fWarehouse = new WTableDirEditor ("M_Warehouse_ID", false, true, true, warehouseL);
		fWarehouse.addValueChangeListener(this);
		
		MLookup productL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1402, DisplayType.Search);
		fProduct = new WSearchEditor ("M_Product_ID", false, true, true, productL);
		fProduct.addValueChangeListener(this);
		
		pickActivity.setMold("select");
		pickActivity.setRows(0);
//		pickActivity.setSelectedIndex(0);
		pickActivity.addEventListener(Events.ON_SELECT, this);
		
		pickLocator.setMold("select");
		pickLocator.setRows(0);
//		pickLocator.setSelectedIndex(0);
		pickLocator.addEventListener(Events.ON_SELECT, this);
	    
	    MLookup trxTypeL = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 53827,
				DisplayType.List, Env.getLanguage(Env.getCtx()), "CostCollectorType", 53287,
				false, "AD_Ref_List.Value='110'");
	    cmbTrxType = new WTableDirEditor("CostCollectorType", true, true, false,trxTypeL);
	    cmbTrxType.setValue("110");
	    cmbTrxType.addValueChangeListener(this);
	    
	    fQtyField.addValueChangeListener(this);
	    
	    fTrxDate.setValue(new Timestamp(System.currentTimeMillis()));
        
        form.getStatusBar().setStatusLine(Msg.getMsg(Env.getCtx(), "MFGExecution"));
	}	//	fillPicks
    
	/**
	 *  Query Info
	 */
	public void executeQuery()
	{
		KeyNamePair trxTypeKNPair = null; 
		executeQuery(trxTypeKNPair, form.getMiniTable());
		form.getMiniTable().repaint();
		form.invalidate();
	}   //  executeQuery

	/**
	 *	Action Listener
	 *  @param e event
	 */
	public void onEvent(Event e)
	{
		if (e.getTarget() == pickActivity)
		{
			KeyNamePair activityNamePair=null;
		
			try 
			{
				activityNamePair = pickActivity.getSelectedItem().toKeyNamePair();
				if(activityNamePair!=null)
				{
					m_PP_Order_Node_ID = activityNamePair.getKey();
					int operationID = DB.getSQLValue(null,"SELECT M_Operation_ID FROM PP_Order_Node WHERE PP_Order_Node_ID=" + activityNamePair.getKey());
			        fOperation.setValue(operationID);
			        m_M_Operation_ID = operationID;
				}
				else
				{
					 fOperation.setValue(null);
				     m_M_Operation_ID = null;
				}

			}
			catch(Exception exception) 
			{
				System.out.println(" Error Message "+exception.getMessage());
			}		
		}
		else if(e.getTarget() == pickLocator)
		{
			KeyNamePair locNamePair=null;
			try 
			{
				locNamePair = pickLocator.getSelectedItem().toKeyNamePair();
				if(locNamePair!=null)
				{
			        m_M_Locator_ID = locNamePair.getKey();
				}
				else
				{
					 m_M_Locator_ID = null;
				}

			}
			catch(Exception exception) 
			{
				System.out.println(" Error Message "+exception.getMessage());
			}	
		}
		
		form.postQueryEvent();
		return;
	}	//	actionPerformed
	
	public void validate()
	{
		form.saveSelection();
		
		if(m_PP_Order_ID == null)
		{
			FDialog.warn(0, "Please select Manufacting Order..");
//			throw new CyprusException("Please select Manufacting Order..");
			return;
		}
		
		else if(m_PP_Order_Node_ID == null)
		{
			FDialog.warn(0, "Please select Activity..");
//			throw new CyprusException("Please select Manufacting Order..");
			return;
		}
		 
		else if(m_M_Locator_ID == null)
		{
//			throw new CyprusException("Please select Locator..");
			FDialog.warn(0, "Please select Locator..");
			return;
		}
		
		ArrayList<Integer> selection = getSelection();
		if (selection != null && selection.size() > 0 && isSelectionActive())
			form.generateCostCollector();
		else
		{
			FDialog.warn(0, "Please select records to process..");
			return;
		}
	}

	/**
	 *	Value Change Listener - requery
	 *  @param e event
	 */
	public void valueChange(ValueChangeEvent e)
	{
		log.info(e.getPropertyName() + "=" + e.getNewValue());
		if (e.getPropertyName().equals("PP_Order_ID")) {
	        m_PP_Order_ID = e.getNewValue();

	        if (m_PP_Order_ID != null) {
	            int orderID = (Integer) m_PP_Order_ID;
	            pickActivity.removeAllItems();
				pickActivity.addItem (new KeyNamePair(0,s_none)) ;
				pickLocator.removeAllItems();
				pickLocator.addItem (new KeyNamePair(0,s_none)) ;
				
				Set<Integer> existingValues = new HashSet<>();

	            String sql = "SELECT po.M_Product_ID, po.AD_Workflow_ID, po.PP_Product_BOM_ID, po.M_Warehouse_ID, po.QtyEntered, pon.PP_Order_Node_ID, pon.Name FROM PP_Order po"
	            		+ " JOIN PP_Order_Workflow pow ON (pow.PP_Order_ID=po.PP_Order_ID)"
	            		+ " JOIN PP_Order_Node pon ON (pon.PP_Order_Workflow_ID=pow.PP_Order_Workflow_ID)"
	            		+ " WHERE po.PP_Order_ID=?";
	            PreparedStatement pstmt = null;
	    		ResultSet rs = null;
	    		try
	    		{
	    			pstmt = DB.prepareStatement (sql, null);
	    			pstmt.setInt (1, orderID);
	    			rs = pstmt.executeQuery ();
	    			while (rs.next ())
	    			{
	    				int productID = rs.getInt(1);
	    				int workflowID = rs.getInt(2);
	    				int BOMID = rs.getInt(3);
	    				int warehouseID = rs.getInt(4);
	    				BigDecimal qtyEntered = rs.getBigDecimal(5);
	    				int nodeID = rs.getInt(6);
	    				String nodeName = rs.getString(7);
	    				if (productID > 0) {
	    	                fProduct.setValue(productID);
	    	                m_M_Product_ID = productID;
	    	            }
	    				if(workflowID > 0) {
	    					fWorkflow.setValue(workflowID);
	    					m_AD_Workflow_ID = workflowID;
	    				}
	    				if(BOMID > 0) {
	    					fBom.setValue(BOMID);
	    					m_PP_Product_BOM_ID = BOMID;
	    				}
	    				if(warehouseID > 0) {
	    					fWarehouse.setValue(warehouseID);
	    					m_M_Warehouse_ID = warehouseID;	    					
	    					MWarehouse wh = new MWarehouse(Env.getCtx(), warehouseID, null);
	    					for (MLocator loc : wh.getLocators(false))
	    					{
	    						if (!existingValues.contains(loc.getM_Locator_ID())) {
	    						pickLocator.addItem(new KeyNamePair(loc.getM_Locator_ID(), loc.getValue()));
	    						existingValues.add(loc.getM_Locator_ID());
	    						}
	    					}	    					
	    				}
	    				if(qtyEntered != Env.ZERO)
	    				{
	    					fQtyField.setValue(qtyEntered);
	    					m_Qty = qtyEntered;
	    				}
	    				if(nodeID > 0)
	    				{	
	    					pickActivity.addItem(new KeyNamePair(nodeID, nodeName));
	    				}
    				}
	    			DB.close(rs, pstmt);
	    		}
	    		catch (Exception ex)
	    		{
	    			log.log(Level.SEVERE, sql, ex);
	    		}
	    		finally
	    		{
	    			DB.close(rs, pstmt);
	    			rs = null; pstmt = null;
	    		}            
	        }	       
		}

		 if (e.getPropertyName().equals("Qty")) {
		        m_Qty = e.getNewValue();
		        fQtyField.setValue(m_Qty);
	        }
		 
		 form.postQueryEvent();
	}	//	vetoableChange

	public String generateCostCollector()
	{		
		String getData =  generateCostCollector(timeRecords);
		FDialog.info(0, null, getData);
		return "";
	}	
	
	public ADForm getForm()
	{
		return form;
	}
}