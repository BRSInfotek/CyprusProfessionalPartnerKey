package org.cyprus.webui.apps.form;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.cyprus.webui.component.Checkbox;
import org.cyprus.webui.component.ConfirmPanel;
import org.cyprus.webui.component.Label;
import org.cyprus.webui.component.ListModelTable;
import org.cyprus.webui.component.Panel;
import org.cyprus.webui.component.SimpleTreeModel;
import org.cyprus.webui.component.WListbox;
import org.cyprus.webui.editor.WSearchEditor;
import org.cyprus.webui.event.WTableModelEvent;
import org.cyprus.webui.event.WTableModelListener;
import org.cyprus.webui.panel.ADForm;
import org.cyprus.webui.panel.CustomForm;
import org.cyprus.webui.panel.IFormController;
import org.cyprusbrs.apps.form.TreeBOM;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Language;
import org.cyprusbrs.util.Msg;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zkex.zul.West;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;




public class WTreeBOM extends TreeBOM implements IFormController, EventListener, WTableModelListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8534705083972399511L;
	
	private int         	m_WindowNo = 0;
	private CustomForm		m_frame = new CustomForm();
	private Tree			m_tree = new Tree();
	private Borderlayout 	mainLayout = new Borderlayout();
	private Panel			northPanel = new Panel();
	private Label			labelProduct = new Label();
	private WSearchEditor   fieldProduct;
	
	private Checkbox		implosion	= new Checkbox ();
	private Label			treeInfo	= new Label ();
	
	
	private Panel dataPane = new Panel();
	private Panel treePane = new Panel();

	private ConfirmPanel confirmPanel = new ConfirmPanel(true);


	private WListbox tableBOM = new WListbox();
	private Vector<Vector<Object>> dataBOM = new Vector<Vector<Object>>();


	
	
	public WTreeBOM(){
		try{
			preInit();
			jbInit ();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "VTreeBOM.init", e);
		}
	}
	
	private void loadTableBOM()
	{
		Vector<String> columnNames = new Vector<String>();		
		columnNames.add(Msg.translate(getCtx(), "Select"));	    	
		columnNames.add(Msg.translate(getCtx(), "IsActive"));     
		columnNames.add(Msg.translate(getCtx(), "Line"));          
		columnNames.add(Msg.translate(getCtx(), "ValidFrom"));     
		columnNames.add(Msg.translate(getCtx(), "ValidTo"));        
		columnNames.add(Msg.translate(getCtx(), "M_Product_ID"));  
		columnNames.add(Msg.translate(getCtx(), "C_UOM_ID"));      
		columnNames.add(Msg.translate(getCtx(), "IsQtyPercentage"));                        
		columnNames.add(Msg.translate(getCtx(), "QtyBatch"));  
		columnNames.add(Msg.translate(getCtx(), "QtyBOM"));   		
		columnNames.add(Msg.translate(getCtx(), "IsCritical"));    
		columnNames.add(Msg.translate(getCtx(), "LeadTimeOffset"));     
		columnNames.add(Msg.translate(getCtx(), "Assay"));         
		columnNames.add(Msg.translate(getCtx(), "Scrap"));        
		columnNames.add(Msg.translate(getCtx(), "IssueMethod"));   
		columnNames.add(Msg.translate(getCtx(), "BackflushGroup")); 
		columnNames.add(Msg.translate(getCtx(), "Forecast"));       		
		tableBOM.clear();
		tableBOM.getModel().removeTableModelListener(this);
		ListModelTable model = new ListModelTable(dataBOM);
		model.addTableModelListener(this);		
		tableBOM.setData(model, columnNames);
		tableBOM.setColumnClass( 0, Boolean.class, false); 
		tableBOM.setColumnClass( 1, Boolean.class, false);    
		tableBOM.setColumnClass( 2, Integer.class,false);    
		tableBOM.setColumnClass( 3, Timestamp.class,false);   
		tableBOM.setColumnClass( 4, Timestamp.class,false);   
		tableBOM.setColumnClass( 5, KeyNamePair.class,false); 
		tableBOM.setColumnClass( 6, KeyNamePair.class,false);  
		tableBOM.setColumnClass( 7, Boolean.class,false);                              
		tableBOM.setColumnClass( 8, BigDecimal.class,false);   
		tableBOM.setColumnClass( 9, BigDecimal.class,false);  
		tableBOM.setColumnClass( 10, Boolean.class,false);                                                
		tableBOM.setColumnClass( 11, BigDecimal.class,false);  
		tableBOM.setColumnClass( 12, BigDecimal.class,false);   
		tableBOM.setColumnClass( 13, Integer.class,false);   
		tableBOM.setColumnClass( 14, String.class,false);       
		tableBOM.setColumnClass( 15, String.class,false);       
		tableBOM.setColumnClass( 16, BigDecimal.class,false);  
		tableBOM.autoSize();		
	}   
	
	private void preInit() throws Exception
	{
		Properties ctx = getCtx();
		Language language = Language.getLoginLanguage(); // Base Language
		MLookup m_fieldProduct = MLookupFactory.get(ctx, m_WindowNo,
				MColumn.getColumn_ID(MProduct.Table_Name, "M_Product_ID"),
				DisplayType.Search, language, MProduct.COLUMNNAME_M_Product_ID, 0, false,
				" M_Product.IsSummary = 'N'");
		
		fieldProduct = new WSearchEditor("M_Product_ID", true, false, true, m_fieldProduct)
		{
			public void setValue(Object value) {
				super.setValue(value);
				action_loadBOM();
			}
		};
		
		implosion.addActionListener(this);
	}
	
	private void jbInit()
	{
	
		m_frame.setWidth("99%");
		m_frame.setHeight("100%");
		m_frame.setStyle("position: absolute; padding: 0; margin: 0");
		m_frame.appendChild (mainLayout);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setStyle("position: absolute");
		
		labelProduct.setText (Msg.getElement(getCtx(), "M_Product_ID"));

		implosion.setText (Msg.getElement(getCtx(), "Implosion"));
		
		North north = new North();
		mainLayout.appendChild(north);
		north.appendChild(northPanel);
		north.setHeight("28px");
		northPanel.appendChild(labelProduct);
		northPanel.appendChild(new Space());
		fieldProduct.getComponent().setWidth("20%");
		northPanel.appendChild(fieldProduct.getComponent());
		northPanel.appendChild(new Space());
		northPanel.appendChild(implosion);
		northPanel.appendChild(new Space());
		northPanel.appendChild(treeInfo);
		
		South south = new South();
		mainLayout.appendChild(south);
		south.appendChild(confirmPanel);
		confirmPanel.addActionListener(this);
		
		West west = new West();
		mainLayout.appendChild(west);
		west.setSplittable(true);
		west.appendChild(treePane);
		treePane.appendChild(m_tree);
		m_tree.setStyle("border: none");
		west.setWidth("25%");
		west.setAutoscroll(true);
		
		Center center = new Center();
		mainLayout.appendChild(center);
		center.appendChild(dataPane);
		dataPane.appendChild(tableBOM);
		tableBOM.setVflex(true);
		tableBOM.setFixedLayout(true);
		center.setFlex(true);
		center.setAutoscroll(true);
	}
	
	public void dispose()
	{
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
	}	
	
	public void vetoableChange (PropertyChangeEvent e)
	{
		String name = e.getPropertyName();
		Object value = e.getNewValue();
		
		if (value == null)
			return;

		if (name.equals("M_Product_ID"))
		{
			if (fieldProduct != null)
				action_loadBOM();	
		}		
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		
		if (event.getTarget().equals(implosion)) 
		{
			action_loadBOM();
		}
		if (event.getName().equals(Events.ON_OK))
		{
			action_loadBOM();
		}
		if (event.getName().equals(Events.ON_CANCEL)) 
		{
			dispose();
		}
	}
	
	
	
	private void action_loadBOM()
	{
		int M_Product_ID = getM_Product_ID(); 
		if (M_Product_ID == 0)
			return;
		MProduct product = MProduct.get(getCtx(), M_Product_ID);
        SimpleTreeNode parent = new SimpleTreeNode(productSummary(product, false), new ArrayList());

		dataBOM.clear();

		if (isImplosion())
		{
			try{
				m_tree.setModel(null);
			}catch(Exception e)
			{}
			
			if (m_tree.getTreecols() != null)
				m_tree.getTreecols().detach();
			if (m_tree.getTreefoot() != null)
				m_tree.getTreefoot().detach();
			if (m_tree.getTreechildren() != null)
				m_tree.getTreechildren().detach();
			
			for (MPPProductBOMLine bomline : MPPProductBOMLine.getByProduct(product))
			{
				parent.getChildren().add(parent(bomline));
			} 
			
			Treecols treeCols = new Treecols();
			m_tree.appendChild(treeCols);
			Treecol treeCol = new Treecol();
			treeCols.appendChild(treeCol);
			
			SimpleTreeModel model = new SimpleTreeModel(parent);
			m_tree.setPageSize(-1);
			m_tree.setTreeitemRenderer(model);
			m_tree.setModel(model);
			
		}
		else
		{
			try{
				m_tree.setModel(null);
			}catch(Exception e)
			{}
			
			if (m_tree.getTreecols() != null)
				m_tree.getTreecols().detach();
			if (m_tree.getTreefoot() != null)
				m_tree.getTreefoot().detach();
			if (m_tree.getTreechildren() != null)
				m_tree.getTreechildren().detach();
			for (MPPProductBOM bom : MPPProductBOM.getProductBOMs(product))
			{
				parent.getChildren().add(parent(bom));                    
			} 
			
			Treecols treeCols = new Treecols();
			m_tree.appendChild(treeCols);
			Treecol treeCol = new Treecol();
			treeCols.appendChild(treeCol);
			
			SimpleTreeModel model = new SimpleTreeModel(parent);
			m_tree.setPageSize(-1);
			m_tree.setTreeitemRenderer(model);
			m_tree.setModel(model);
			
		}
		m_tree.addEventListener(Events.ON_SELECTION, this);
		loadTableBOM();
	}

    public SimpleTreeNode parent(MPPProductBOMLine bomline)
	{

		
		MProduct M_Product = MProduct.get(getCtx(), bomline.getM_Product_ID());
		MPPProductBOM bomproduct = new MPPProductBOM(getCtx(), bomline.getPP_Product_BOM_ID(), null);
        SimpleTreeNode parent = new SimpleTreeNode(productSummary(M_Product, false), new ArrayList());

		Vector<Object> line = new Vector<Object>(17);
		line.add( new Boolean(false));  
		line.add( new Boolean(true)); 
		line.add( new Integer(bomline.getLine()));             
		line.add( (Timestamp) bomline.getValidFrom()); 
		line.add( (Timestamp) bomline.getValidTo());
		KeyNamePair pp = new KeyNamePair(M_Product.getM_Product_ID(),M_Product.getName());
		line.add(pp); 
		KeyNamePair uom = new KeyNamePair(bomline.getC_UOM_ID(),bomline.getC_UOM().getUOMSymbol());
		line.add(uom); 
		line.add(new Boolean(bomline.isQtyPercentage())); 
		line.add((BigDecimal) bomline.getQtyBatch()); 
		line.add((BigDecimal) ((bomline.getQtyBOM()!=null) ? bomline.getQtyBOM() : new BigDecimal(0)));  
		line.add(new Boolean(bomline.isCritical()));                
		line.add( (Integer) bomline.getLeadTimeOffset());
		line.add( (BigDecimal) bomline.getAssay()); 
		line.add( (BigDecimal) (bomline.getScrap())); 
		line.add( (String) bomline.getIssueMethod()); 
		line.add( (String) bomline.getBackflushGroup());
		line.add( (BigDecimal) bomline.getForecast()); 
		dataBOM.add(line);

		for (MPPProductBOM bom : MPPProductBOM.getProductBOMs((MProduct) bomproduct.getM_Product()))
		{
			MProduct component = MProduct.get(getCtx(), bom.getM_Product_ID());
			return component(component);
		}
		return parent;
	}

	public SimpleTreeNode parent(MPPProductBOM bom)
    {
		SimpleTreeNode parent = new SimpleTreeNode(productSummary(bom), new ArrayList());

		for (MPPProductBOMLine bomline : bom.getLines())
		{
			MProduct component = MProduct.get(getCtx(), bomline.getM_Product_ID());
			Vector<Object> line = new Vector<Object>(17);
			line.add( new Boolean(false)); 
			line.add( new Boolean(true));   
			line.add( new Integer(bomline.getLine()));              
			line.add( (Timestamp) bomline.getValidFrom()); 
			line.add( (Timestamp) bomline.getValidTo());
			KeyNamePair pp = new KeyNamePair(component.getM_Product_ID(),component.getName());
			line.add(pp); 
			KeyNamePair uom = new KeyNamePair(bomline.getC_UOM_ID(),bomline.getC_UOM().getUOMSymbol());
			line.add(uom); 
			line.add(new Boolean(bomline.isQtyPercentage()));
			line.add((BigDecimal) bomline.getQtyBatch()); 
			line.add((BigDecimal) bomline.getQtyBOM());  
			line.add(new Boolean(bomline.isCritical()));      
			line.add( (Integer) bomline.getLeadTimeOffset());
			line.add( (BigDecimal) bomline.getAssay());
			line.add( (BigDecimal) (bomline.getScrap())); 
			line.add( (String) bomline.getIssueMethod()); 
			line.add( (String) bomline.getBackflushGroup());  
			line.add( (BigDecimal) bomline.getForecast()); 
			dataBOM.add(line);
			parent.getChildren().add(component(component));

		}
		return parent;
	}

    public SimpleTreeNode component(MProduct product)
    {

		if (isImplosion())
		{
            SimpleTreeNode parent = new SimpleTreeNode(productSummary(product, false), new ArrayList());
			for (MPPProductBOMLine bomline : MPPProductBOMLine.getByProduct(product)) 
			{
				parent.getChildren().add(parent(bomline));
			}  
			return parent;  
		}
		else
		{
			for (MPPProductBOM bom : MPPProductBOM.getProductBOMs(product))
			{
				return parent(bom);
			}
            return new SimpleTreeNode(productSummary(product, true), new ArrayList());
        }
	}

	private int getM_Product_ID() {
		Integer Product = (Integer)fieldProduct.getValue();
		if (Product == null)
			return 0;
		return Product.intValue(); 
	}
	
	private boolean isImplosion() {
		return implosion.isSelected();
	}
	
	@Override
	public ADForm getForm() {
		return m_frame;
	}

	

	@Override
	public void tableChanged(WTableModelEvent event) {
	}

	
}
