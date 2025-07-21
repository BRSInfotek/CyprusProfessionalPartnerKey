package org.cyprus.mrp.server.window;


	import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

//import javax.transaction.Synchronization;

import org.cyprus.exceptions.FillMandatoryException;
//import org.cyprus.mrp.model.MMRPInventoryAudit;
import org.cyprus.mrp.model.MMRPPlan;
//import org.cyprus.mrp.model.X_MRP_Inventory_Audit;
//import org.zkoss.io.Serializables;
import org.cyprusbrs.apps.ADialog;
import org.cyprusbrs.apps.form.FormFrame;
import org.cyprusbrs.apps.form.FormPanel;
import org.cyprusbrs.apps.form.VGenPanel;
import org.cyprusbrs.framework.MLookup;
import org.cyprusbrs.framework.MLookupFactory;
import org.cyprusbrs.framework.MRMA;
import org.cyprusbrs.grid.ed.VComboBox;
import org.cyprusbrs.grid.ed.VLookup;
import org.cyprusbrs.swing.CLabel;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;

	/**
	 * Generate Shipment (manual) view class
	 * 
	 */
	
	
	public class ImplementPlanWindowImplNew extends ImplementPlanWindowNew implements FormPanel, ActionListener, VetoableChangeListener
	{
		private VGenPanel panel;
		
		/**	Window No			*/
		private int         	m_WindowNo = 0;
		/**	FormFrame			*/
		private FormFrame 		m_frame;

		/**	Logger			*/
		private static CLogger log = CLogger.getCLogger(ImplementPlanWindowImplNew.class);
		//

//		private CLabel lWarehouse = new CLabel();
//		private VLookup fWarehouse;
		
		private CLabel lPlan = new CLabel();
		private VLookup fPlan;
		
		private CLabel lProduct = new CLabel();
		private VLookup fProduct;
		
		private CLabel lPeriod = new CLabel();
		private VLookup fPeriod;
		
		private CLabel lBPartner = new CLabel();
		private VLookup fBPartner;	
		
		private CLabel     lOrderType = new CLabel();
		private VLookup    fOrderType;
		
		private CLabel     lDocType = new CLabel();
		private VComboBox  cmbDocType = new VComboBox();
//		private CLabel     lDocAction = new CLabel();
//		private VLookup    docAction;
		
		/**
		 *	Initialize Panel
		 *  @param WindowNo window
		 *  @param frame frame
		 */
		public void init (int WindowNo, FormFrame frame)
		{
			log.info("");
			m_WindowNo = WindowNo;
			m_frame = frame;
			Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "Y");

			panel = new VGenPanel(this, WindowNo, frame);

			try
			{
				super.dynInit();
				dynInit();
				jbInit();
			}
			catch(Exception ex)
			{
				log.log(Level.SEVERE, "init", ex);
			}
		}	//	init
		
		/**
		 * 	Dispose
		 */
		public void dispose()
		{
			if (m_frame != null)
				m_frame.dispose();
			m_frame = null;
		}	//	dispose
		
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
		void jbInit() throws Exception
		{		
		//	lWarehouse.setLabelFor(fWarehouse);
			lPlan.setLabelFor(fPlan);
			
			lProduct.setLabelFor(fProduct);
			lProduct.setText(Msg.translate(Env.getCtx(), "M_Product_ID"));
			
			lPeriod.setLabelFor(fPeriod);
			lPeriod.setText(Msg.translate(Env.getCtx(), "C_Period_ID"));
			
			lBPartner.setLabelFor(fBPartner);
			lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			
			lOrderType.setLabelFor(fOrderType);
			lOrderType.setText(Msg.translate(Env.getCtx(), "PriorityImplementation"));
			
//			lDocAction.setLabelFor(docAction);
//			lDocAction.setText(Msg.translate(Env.getCtx(), "DocAction"));
		//	lDocType.setLabelFor(cmbDocType);
			
			
//			panel.getParameterPanel().add(lWarehouse, null);
//			panel.getParameterPanel().add(fWarehouse, null);
			
			panel.getParameterPanel().add(lPlan, null);
			panel.getParameterPanel().add(fPlan, null);
			
			panel.getParameterPanel().add(lProduct, null);
			panel.getParameterPanel().add(fProduct, null);
			
			panel.getParameterPanel().add(lPeriod, null);
			panel.getParameterPanel().add(fPeriod, null);
			
			panel.getParameterPanel().add(lBPartner, null);
			panel.getParameterPanel().add(fBPartner, null);
						
			panel.getParameterPanel().add(lOrderType, null);
			panel.getParameterPanel().add(fOrderType, null);
			
		//	panel.getParameterPanel().add(lDocType, null);
		//	panel.getParameterPanel().add(cmbDocType, null);
//			panel.getParameterPanel().add(lDocAction, null);
//			panel.getParameterPanel().add(docAction, null);
		}	//	jbInit
		
		/**
		 *	Fill Picks.
		 *		Column_ID from C_Order
		 *  @throws Exception if Lookups cannot be initialized
		 */
		public void dynInit() throws Exception
		{
//			//	C_OrderLine.M_Warehouse_ID
//			MLookup orgL = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2223, DisplayType.TableDir);
//			fWarehouse = new VLookup ("M_Warehouse_ID", true, false, true, orgL);
//			lWarehouse.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
//			fWarehouse.addVetoableChangeListener(this);
//			setM_Warehouse_ID(fWarehouse.getValue());
			
//		//	MRP_Plan.MRP_Plan_ID
					MLookup orgL1 = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 1000930, DisplayType.Table);
					fPlan = new VLookup ("MRP_Plan_ID", true, false, true, orgL1);
					lPlan.setText(Msg.translate(Env.getCtx(), "MRP_Plan_ID"));
					fPlan.addVetoableChangeListener(this);
					setMRP_Plan_ID(fPlan.getValue());
					
////			//		M_Product.M_Product_ID
//							MLookup orgL2 = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0,1001303, DisplayType.TableDir);
//							fProduct = new VLookup ("M_Product_ID", true, false, true, orgL2);
//							lProduct.setText(Msg.translate(Env.getCtx(), "M_Product_ID"));
//							fProduct.addVetoableChangeListener(this);
						
//							M_Product.M_Product_ID
							MLookup orgL2 = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0,1001303, DisplayType.Search);
							fProduct = new VLookup ("M_Product_ID", false, false, true, orgL2);
							lProduct.setText(Msg.translate(Env.getCtx(), "M_Product_ID"));
							fProduct.addVetoableChangeListener(this);
							
							
		//					C_Period.C_Period_ID
							MLookup orgL3 = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 1001299, DisplayType.TableDir);
							fPeriod = new VLookup ("C_Period_ID", true, false, true, orgL3);
							lPeriod.setText(Msg.translate(Env.getCtx(), "C_Period_ID"));
							fPeriod.addVetoableChangeListener(this);
							
							//   PriorityImplementation Purchase/Manufacture
							MLookup OrderTypeL = MLookupFactory.get(Env.getCtx(), m_WindowNo, 1000979 /* M_InOut.DocStatus */, 
									DisplayType.List, Env.getLanguage(Env.getCtx()), "PriorityImplementation", 1000032 /* _Document Action */,
									false, "AD_Ref_List.Value IN ('M','P')");
							fOrderType = new VLookup("PriorityImplementation", true, false, true,OrderTypeL);
							fOrderType.addVetoableChangeListener(this);
		
			
			//		C_BPartner.C_BPartner_ID
			MLookup bpL = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2762, DisplayType.Search);
			fBPartner = new VLookup ("C_BPartner_ID", false, false, true, bpL);
			lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			fBPartner.addVetoableChangeListener(this);
			
			//   Document Action Prepared/ Completed
//			MLookup docActionL = MLookupFactory.get(Env.getCtx(), m_WindowNo, 4324 /* M_InOut.DocStatus */, 
//					DisplayType.List, Env.getLanguage(Env.getCtx()), "DocAction", 135 /* _Document Action */,
//					false, "AD_Ref_List.Value IN ('CO','PR')");
//			docAction = new VLookup("DocAction", true, false, true,docActionL);
//			docAction.addVetoableChangeListener(this);
			
			//Document Type Sales Order/Vendor RMA
			lDocType.setText(Msg.translate(Env.getCtx(), "C_DocType_ID"));
			cmbDocType.addItem(new KeyNamePair(MMRPPlan.Table_ID, Msg.translate(Env.getCtx(), "Purchase")));
			cmbDocType.addItem(new KeyNamePair(MRMA.Table_ID, Msg.translate(Env.getCtx(), "VendorRMA")));
			cmbDocType.addActionListener(this);
			
			panel.getStatusBar().setStatusLine(Msg.getMsg(Env.getCtx(), "InOutGenerateSel"));//@@
		}	//	fillPicks
		
		public void executeQuery()
		{
		KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem();
		
			executeQuery(docTypeKNPair, panel.getMiniTable());
		}   //  executeQuery
		
		/**
		 *	Action Listener
		 *  @param e event
		 */
		public void actionPerformed(ActionEvent e)
		{
//			if (cmbDocType.equals(e.getSource()))
			if (cmbDocType.equals(e.getSource()))
			{
			   executeQuery();
			    return;
			}
			
			try
			{
				validate();
			}
			catch(Exception ex)
			{
				ADialog.error(m_WindowNo, this.panel, "Error", ex.getLocalizedMessage());
			}
			
			int m_product_id  = 135;
			List<ActualInventoryDetails> listnew = new ArrayList<ActualInventoryDetails>();
			
			// for loop
			for(ActualInventoryDetails i:listnew)
			{
				ActualInventoryDetails aid = new ActualInventoryDetails();
				aid.setM_product_id(m_product_id);
				aid.setOnhandqty(Env.ONE);
				listnew.add(aid);
			}
			
		//	MActualInventory act = new MActualInventory(ctx,trx,listnew);
			
		}	//	actionPerformed
		
		public void validate()
		{
			panel.saveSelection();
			
//			if (getM_Warehouse_ID() <= 0)
//			{
//				throw new FillMandatoryException("M_Warehouse_ID");
//			}
			
			if (getMRP_Plan_ID() <= 0)
			{
				throw new FillMandatoryException("MRP_Plan_ID");
			}
			
			ArrayList<Integer> selection = getSelection();
			if (selection != null
				&& selection.size() > 0
				&& isSelectionActive())	//	on selection tab
			{
				panel.generate();
			}
			else
			{
				panel.dispose();
			}
		}

		/**
		 *	Vetoable Change Listener - requery
		 *  @param e event
		 */
		public void vetoableChange(PropertyChangeEvent e)
		{
			log.info(e.getPropertyName() + "=" + e.getNewValue());
//			if (e.getPropertyName().equals("M_Warehouse_ID"))
//				setM_Warehouse_ID(e.getNewValue());
			
			if (e.getPropertyName().equals("MRP_Plan_ID"))
				setMRP_Plan_ID(e.getNewValue());
			
			if (e.getPropertyName().equals("M_Product_ID"))
			{
				m_M_Product_ID = e.getNewValue();
				fProduct.setValue(m_M_Product_ID);	//	display value
			}
			
			if (e.getPropertyName().equals("C_Period_ID"))
			{
				m_C_Period_ID = e.getNewValue();
				fPeriod.setValue(m_C_Period_ID);	//	display value
			}
			
			if (e.getPropertyName().equals("PriorityImplementation"))
			{
				m_PriorityImplementation = e.getNewValue();
				fOrderType.setValue(m_PriorityImplementation);	//	display value
			}
			
			if (e.getPropertyName().equals("C_BPartner_ID"))
			{
				m_C_BPartner_ID = e.getNewValue();
				fBPartner.setValue(m_C_BPartner_ID);	//	display value
			}
			
			executeQuery();
		}	//	vetoableChange
		
		/**************************************************************************
		 *	Generate Shipments
		 */
		public String generate()
		{
			KeyNamePair docTypeKNPair = (KeyNamePair)cmbDocType.getSelectedItem();
			String docActionSelected = (String)fOrderType.getValue();	
			return generate(panel.getStatusBar(), docTypeKNPair, docActionSelected);
		}	//	generateShipments
		
		class ActualInventoryDetails 
		{
			private int m_product_id;
			private BigDecimal onhandqty;
			
			
			public int getM_product_id() {
				return m_product_id;
			}
			public void setM_product_id(int m_product_id) {
				this.m_product_id = m_product_id;
			}
			public BigDecimal getOnhandqty() {
				return onhandqty;
			}
			public void setOnhandqty(BigDecimal onhandqty) {
				this.onhandqty = onhandqty;
			}
			
			public void setDetails()
			{
//				if (C_BankAccount_ID == 0)
//					return;	
				
				String Sql="select plan.ad_client_id, plan.ad_org_id,mdl.m_product_id,planrun.mrp_planrun_id  from mrp_plan plan, mrp_planrun planrun, mrp_masterdemand md,mrp_masterdemandline mdl\r\n" + 
						"where \r\n" + 
						"plan.mrp_plan_id= planrun.mrp_plan_id and\r\n" + 
						"planrun.mrp_masterdemand_id = md.mrp_masterdemand_id and\r\n" + 
						"md.mrp_masterdemand_id = mdl.mrp_masterdemand_id and \r\n" + 
						"plan.mrp_plan_id = ?;";
				
				PreparedStatement pstmt = null; 
				ResultSet rs = null;
				try
				{
					pstmt = DB.prepareStatement(Sql, null);
		//			pstmt.setInt(1, AD_Client_ID);
					rs = pstmt.executeQuery();
					
		//			MMRPInventoryAudit mia=new MMRPInventoryAudit();
//					int cou=0;
//					while(rs.next())
//					{
//		//				setRoutingNo (rs.getString(1));
//						setAccountNo (rs.getString(2));
//						cou++;
//					}
					rs.close();
					pstmt.close();
				}
				catch (SQLException e)
				{
					log.log(Level.SEVERE, Sql, e);
				}
				finally
				{
					DB.close(rs, pstmt);
					rs = null;
					pstmt = null;
				
			}
			}
		
			
		}
	}
