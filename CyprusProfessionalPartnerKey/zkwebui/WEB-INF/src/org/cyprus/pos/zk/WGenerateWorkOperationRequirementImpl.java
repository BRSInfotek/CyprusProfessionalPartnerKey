

package org.cyprus.pos.zk;



import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkCenter;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.vos.ChangeVO;
//import org.cyprus.vos.WindowCtx;
//import org.cyprus.vos.WindowCtx;
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
import org.cyprusbrs.apps.form.Generateopertionrequirementgen;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
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
public class WGenerateWorkOperationRequirementImpl extends Generateopertionrequirementgen implements IFormController, EventListener, ValueChangeListener
{
	private static WGenForm form;
	
//	protected WindowCtx windowCtx = new WindowCtx();
	
//	 private SearchComponentImpl c_search;
//	  
//	  protected ArrayList<ComponentImplIntf> components = new ArrayList<ComponentImplIntf>();
//	  
//	  protected Ctx serverCtx;
//	  
//	  protected  WindowCtx windowCtx;
//	  
//	  protected  UWindowID m_uid;
//	  
//	  protected  int windowNO;
//	  
//	  InfoComponentImpl c_info = null;
//	  
//	  public SearchComponentImpl c_process;
	 
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(WGenerateWorkOperationRequirementImpl.class);
	//
	
	private Label lWorkOrder = new Label();
	private WTableDirEditor fWorkOrder;
	private Label lProductAssembly = new Label();
	private WSearchEditor fProductAssembly;
	private Label lOperetaionSequence = new Label();
	private int fOperetaionSequence;
	private Label lWarehouse= new Label();
	private WTableDirEditor fWarehouse;
	private Label lWorkCenter = new Label();
	private WTableDirEditor fWorkCenter;
	
	private Label lOperationScheduledDateFrom= new Label();
	private WDateEditor fOperationScheduledDateFrom;
	private Label lBusinessPartner = new Label();
	private WTableDirEditor fBusinessPartner;
	private WTableDirEditor fPriority;
	private Label lPriority = new Label();
	private Label lOperationScheduledDateTo= new Label();
	private WDateEditor fOperationScheduledDateTo;
	private Label lWorkOrderSortCriteria = new Label();
	private WTableDirEditor fWorkOrderSortCriteria;
	private Label     lSupervisedOperationOnly = new Label();
	private WYesNoEditor fSupervisedOperationOnly;
	private Listbox  cmbDocType = ListboxFactory.newDropdownListbox();
	
	public WGenerateWorkOperationRequirementImpl()
	{
//		this.windowCtx = new WindowCtx();
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
	
		lWorkOrder.setText(Msg.translate(Env.getCtx(), "MFG_WorkOrder_ID"));
		lProductAssembly.setText(Msg.translate(Env.getCtx(), "Product Assembly"));
//		lOperetaionSequence.setText(Msg.translate(Env.getCtx(), "Operation Sequence"));
		lWarehouse.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
		lWorkCenter.setText(Msg.translate(Env.getCtx(), "MFG_WorkCenter_ID"));
		lOperationScheduledDateFrom.setText("Operation Scheduled Date From");
		lBusinessPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		//lPriority.setText(Msg.translate(Env.getCtx(), "Priority"));
		lOperationScheduledDateTo.setText("Operation Scheduled Date To");
		lWorkOrderSortCriteria.setText(Msg.translate(Env.getCtx(), "MFG_WaveSortCriteria_ID"));
	//	lSupervisedOperationOnly.setText("Supervised Operation Only");
		
		
		//lResourceinoperation.setText("Resource in Operation");
	
		
		Row row = form.getParameterPanel().newRows().newRow();
		row.appendChild(lWorkOrder.rightAlign());
		row.appendChild(fWorkOrder.getComponent());
		row.appendChild(new Space());
		row.appendChild(lProductAssembly.rightAlign());
		row.appendChild(fProductAssembly.getComponent());
		row.appendChild(new Space());
//		row.appendChild(lOperetaionSequence.rightAlign());
//		row.appendChild(fOperetaionSequence.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lWarehouse.rightAlign());
		row.appendChild(fWarehouse.getComponent());
		row.appendChild(new Space());
		row.appendChild(lWorkCenter.rightAlign());
		row.appendChild(fWorkCenter.getComponent());
		row.appendChild(new Space());
		row.appendChild(lOperationScheduledDateFrom.rightAlign());
		row.appendChild(fOperationScheduledDateFrom.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lBusinessPartner.rightAlign());
		row.appendChild(fBusinessPartner.getComponent());
		row.appendChild(new Space());
//		row.appendChild(lPriority.rightAlign());
//		row.appendChild(fPriority.getComponent());
		row.appendChild(new Space());
		row.appendChild(lOperationScheduledDateTo.rightAlign());
		row.appendChild(fOperationScheduledDateTo.getComponent());
		row.appendChild(new Space());
		
		row = new Row();
		form.getParameterPanel().getRows().appendChild(row);
		row.appendChild(lWorkOrderSortCriteria.rightAlign());
		row.appendChild(fWorkOrderSortCriteria.getComponent());
		row.appendChild(new Space());
		row.appendChild(lSupervisedOperationOnly.rightAlign());
		row.appendChild(fSupervisedOperationOnly.getComponent());
		row.appendChild(new Space());
	}	//	jbInit

	/**
	 *	Fill Picks.
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	public void dynInit() throws Exception
	{
		
		//
		MLookup woL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1001626, DisplayType.TableDir);
		fWorkOrder = new WTableDirEditor ("MFG_WorkOrder_ID", false, false, true, woL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fWorkOrder.addValueChangeListener(this);
		
		MLookup resourceL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1402, DisplayType.Search);
		fProductAssembly = new WSearchEditor ("M_Product_ID", false, false, true, resourceL);
	//	lOrg.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		fProductAssembly.addValueChangeListener(this);
		
		MLookup whL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1151, DisplayType.TableDir);
		fWarehouse = new WTableDirEditor ("M_Warehouse_ID", false, false, true, whL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fWarehouse.addValueChangeListener(this);
		
		MLookup wcL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 1001533, DisplayType.TableDir);
		fWorkCenter = new WTableDirEditor ("MFG_WorkCenter_ID", false, false, true, wcL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fWorkCenter.addValueChangeListener(this);
		
		
		
		//MLookup sdL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.Date);
		fOperationScheduledDateFrom = new WDateEditor("Operation Scheduled Date From", false, false, true, "Operation Scheduled Date From");
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fOperationScheduledDateFrom.addValueChangeListener(this);
		
	
		
		MLookup spL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.TableDir);
		fBusinessPartner = new WTableDirEditor ("C_BPartner_ID", false, false, true, spL);
	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fBusinessPartner.addValueChangeListener(this);
		
//		MLookup sspL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.TableDir);
//	//	fPriority = new WTableDirEditor ("C_BPartner_ID", false, false, true, sspL);
//	//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
//		fPriority.addValueChangeListener(this);
		
		//MLookup sdtLL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.Search);
			fOperationScheduledDateTo = new WDateEditor("Operation Scheduled Date To", false, false, true, "Operation Scheduled Date To");
		//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			fOperationScheduledDateTo.addValueChangeListener(this);
			
			MLookup ssspL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.TableDir);
			fWorkOrderSortCriteria = new WTableDirEditor ("MFG_WaveSortCriteria_ID", false, false, true, ssspL);
		//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			fWorkOrderSortCriteria.addValueChangeListener(this);
			
			fSupervisedOperationOnly = new WYesNoEditor("Supervised Operations Only", "Supervised Operations Only", "", false, false, true);
			//	lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			fSupervisedOperationOnly.addValueChangeListener(this);
	
		cmbDocType.addItem(new KeyNamePair(MMFGWorkOrder.Table_ID, Msg.translate(Env.getCtx(), "Work Order")));
        cmbDocType.addItem(new KeyNamePair(MMFGWorkCenter.Table_ID, Msg.translate(Env.getCtx(), "Work Center")));
        cmbDocType.addActionListener(this);
        cmbDocType.setSelectedIndex(0);
		
		// @02092022
		
//		  this.serverCtx = serverCtx;
//		    this.windowCtx = windowCtx;
//		    this.m_uid = m_uid;
//		    this.windowNO = windowNO;
//		    this.c_info = new InfoComponentImpl(Env.getCtxCtx(), 129, true, false) {
//		        protected QueryVO processQueryVO(QueryVO p_queryVO, WindowCtx windowCtx) {
//		          QueryVO b = TableComponentImpl.buildQueryVO(windowCtx, this.componentVO.fieldVOs);
//		          TableComponentImpl.convertColumnNameToSelectClause(p_queryVO, this.componentVO.fieldVOs);
//		          String isPartofOperation = ((Boolean)WGenerateResourceChargesWindowImpl.this.c_search.getFieldValue(windowCtx, "IsPartofOperation")).booleanValue() ? "Y" : "N";
//		          b.addRestrictions(p_queryVO);
//		          if ("Y".equals(isPartofOperation)) {
//		            String addWhereClause = " (SELECT COUNT(1) FROM  MFG_WorkOrderResource wopr  WHERE  wopr.MFG_WorkOrderOperation_ID = wop.MFG_WorkOrderOperation_ID  AND wopr.M_Product_ID = p.M_Product_ID)";
//		            b.restrictions.add(new QueryRestrictionVO(addWhereClause, ">=", "1", null, null, 13));
//		          } 
//		          return b;
//		        }
//		      };
//		    (this.c_info.getComponentVO().getFieldVO("QtyRequired")).IsReadOnly = false;
//		    lengthenField(this.c_info.getComponentVO(), "QtyRequired");
//		    FieldVO f_supervisor = this.c_info.getComponentVO().getFieldVO("AD_User_ID");
//		    MLookup lookupSupervisor = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 212, 18, Env.getLanguage(Env.getCtx()), "AD_User_ID", 316, false, "");
//		 //   MLookup spL = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), 0, 2762, DisplayType.Search);
//		    GridFieldVO gfvos = new GridFieldVO(Env.getCtxCtx(), f_supervisor);
//		    GridField mFields = new GridField(gfvos);
//		    mFields.loadLookup();
//		    mFields.lookupLoadComplete();
//		    MLookupInfo mls = mFields.getLookupInfo();
//		    ListBoxVO listBoxVOS = Util.convertLookupToVO((Lookup)lookupSupervisor, f_supervisor.IsMandatoryUI, null);
//		    ConvertUIVO.copyLookupInfo(listBoxVOS, mls);
//		    f_supervisor.listBoxVO = listBoxVOS;
//		    this.c_search = new SearchComponentImpl();
//		    ArrayList<FieldVO> fieldVOs = (this.c_info.getComponentVO()).fieldVOs;
//		    ArrayList<FieldVO> searchFields = new ArrayList<FieldVO>();
//		    FieldVO f_workorder = this.c_info.getComponentVO().getFieldVO("MFG_WorkOrder_ID");
//		    String workorderValidation = "MFG_WorkOrder.DocStatus IN ('IP')";
//		    MLookup lookupWorkOrder = MLookupFactory.get(Env.getCtx(), 0, 21412, 19, Env.getLanguage(Env.getCtx()), "MFG_WorkOrder_ID", 0, false, workorderValidation);
//		    GridFieldVO gfvo = new GridFieldVO(Env.getCtxCtx(), f_workorder);
//		    GridField mField = new GridField(gfvo);
//		    mField.loadLookup();
//		    mField.lookupLoadComplete();
//		    MLookupInfo ml = mField.getLookupInfo();
//		    ListBoxVO listBoxVO = Util.convertLookupToVO((Lookup)lookupWorkOrder, f_workorder.IsMandatoryUI, null);
//		    ConvertUIVO.copyLookupInfo(listBoxVO, ml);
//		    f_workorder.listBoxVO = listBoxVO;
//		    FieldVO f_product = new FieldVO("M_Product_ID", Msg.translate(Env.getCtx(), "Resource"), 30, true);
//		    f_product.listBoxVO = new ListBoxVO();
//		    f_product.listBoxVO.KeyColumn = "M_Product_ID";
//		    String productValidation = " M_Product.IsActive='Y' AND M_Product.ProductType = 'R' AND M_Product.ResourceGroup IN ('P','E') ";
//		    f_product.ValidationCode = productValidation;
//		    f_product.IsMandatoryUI = true;
//		    MLookup lookupProduct = MLookupFactory.get(Env.getCtx(), 0, 27940, 30, Env.getLanguage(serverCtx), "M_Product_ID", 0, false, productValidation);
//		    GridFieldVO pfvo = new GridFieldVO(Env.getCtxCtx(), f_product);
//		    GridField pField = new GridField(pfvo);
//		    pField.loadLookup();
//		    pField.lookupLoadComplete();
//		    MLookupInfo ml_p = pField.getLookupInfo();
//		    ListBoxVO listBoxVO_p = Util.convertLookupToVO((Lookup)lookupProduct, f_product.IsMandatoryUI, null);
//		    ConvertUIVO.copyLookupInfo(listBoxVO_p, ml_p);
//		    f_product.listBoxVO = listBoxVO_p;
//		    searchFields.add(f_product);
//		    for (int i = 0; i < fieldVOs.size(); i++) {
//		      FieldVO f_info = fieldVOs.get(i);
//		      if (f_info.isQueryCriteria) {
//		        FieldVO f = f_info.copySearch();
//		        f.IsDisplayed = true;
//		        searchFields.add(f);
//		      } 
//		    } 
//		    FieldVO f_isPartofOperation = Util.createFieldVO(Env.getCtxCtx(), "IsPartofOperation", Msg.translate(Env.getCtx(), "IsPartofOperation"), 20);
//		    f_isPartofOperation.IsMandatoryUI = true;
//		    f_isPartofOperation.DefaultValue = "Y";
//		    searchFields.add(f_isPartofOperation);
//		    ArrayList<FieldVO> processFields = new ArrayList<FieldVO>();
//		    this.c_search = new SearchComponentImpl(searchFields.<FieldVO>toArray(new FieldVO[0]));
//		    this.c_process = new SearchComponentImpl(processFields.<FieldVO>toArray(new FieldVO[0]));
//		    this.components.add(this.c_search);
//		    this.components.add(this.c_info);
//		    this.components.add(this.c_process);
		  
        
       // form.getStatusBar().setStatusLine(Msg.getMsg(Env.getCtx(), "InvGenerateSel"));//@@
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
			//fBPartner.setValue(m_C_BPartner_ID);	//	display value
		}
		if (e.getPropertyName().equals("MFG_WorkCenter_ID"))
		{
			m_MFG_WorkCenter_ID = e.getNewValue();
			//fBPartner.setValue(m_C_BPartner_ID);	//	display value
		}
		if (e.getPropertyName().equals("M_Warehouse_ID"))
		{
			m_M_Warehouse_ID = e.getNewValue();
			//fBPartner.setValue(m_C_BPartner_ID);	//	display value
		}
		form.postQueryEvent();
	}	//	vetoableChange
	
	
	public String generate()
	{
		System.out.println("generate");
		ChangeVO changeVO = new ChangeVO();
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem().toKeyNamePair();
		
		return generate(form.getStatusBar(), docTypeKNPair);
	}	//	

	
	public ADForm getForm()
	{
		return form;
	}
	
//	private static char NBSP = '_';
//	
//	private void lengthenField(ComponentVO comp, String fName) {
//	    FieldVO fAmount = comp.getFieldVO(fName);
//	    fAmount.name = " " + NBSP + NBSP + NBSP + NBSP + fAmount.name + NBSP + NBSP + NBSP + NBSP + NBSP + NBSP + NBSP + NBSP + NBSP;
//	  }
}
