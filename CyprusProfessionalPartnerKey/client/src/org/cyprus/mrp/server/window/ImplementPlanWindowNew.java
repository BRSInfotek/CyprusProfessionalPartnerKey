package org.cyprus.mrp.server.window;

	
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


	import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.apps.IStatusBar;
import org.cyprusbrs.apps.form.GenForm;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
import org.cyprusbrs.framework.MPrivateAccess;
import org.cyprusbrs.framework.MRMA;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.minigrid.IMiniTable;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.process.ProcessInfo;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;

	/**
	 * Generate Shipment (manual) controller class
	 * 
	 */
	
	
	public class ImplementPlanWindowNew extends GenForm
	{
		/**	Logger			*/
		private static CLogger log = CLogger.getCLogger(ImplementPlanWindowNew.class);
		//
		
		public Object 			m_M_Warehouse_ID = null;
		public Object 			m_MRP_Plan_ID = null;
		public Object 			m_C_BPartner_ID = null;
		public Object           m_M_Product_ID= null;
		public Object           m_C_Period_ID= null;
		public Object           m_PriorityImplementation= null;
		
		
		public void dynInit() throws Exception
		{
			setTitle("InOutGenerateInfo");
			setReportEngineType(ReportEngine.SHIPMENT);
			setAskPrintMsg("PrintShipments");
		}
		
		public void configureMiniTable(IMiniTable miniTable)
		{
			//  create Columns
			miniTable.addColumn("MRP_PlanRun_ID");
			miniTable.addColumn("M_Product_ID");
			miniTable.addColumn("C_Period_ID");
			miniTable.addColumn("C_BPartner_ID");
			miniTable.addColumn("PriorityImplementation");
			miniTable.addColumn("IsBOM");
			miniTable.addColumn("QtyOnHand");
			miniTable.addColumn("QtyOrdered");
			miniTable.addColumn("QtyExpectedWO");
			miniTable.addColumn("DateExpectedWO");
			miniTable.addColumn("QtyExpectedPO");				
			miniTable.addColumn("DateExpectedPO");
//			miniTable.addColumn("DateOrdered");
			
			//
			miniTable.setMultiSelection(true);

			//  set details
			miniTable.setColumnClass(0, IDColumn.class, false, " ");
			miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "M_Product_ID"));
			miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "C_Period_ID"));
			miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "C_BPartner_ID"));
			miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "PriorityImplementation"));
			miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "IsBOM"));
			miniTable.setColumnClass(6, BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyOnHand"));
			miniTable.setColumnClass(7, BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyOrdered"));
			miniTable.setColumnClass(8, BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyExpectedWO"));
			miniTable.setColumnClass(9, Timestamp.class, true, Msg.translate(Env.getCtx(), "DateExpectedWO"));
			miniTable.setColumnClass(10, BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyExpectedPO"));
			miniTable.setColumnClass(11, Timestamp.class, true, Msg.translate(Env.getCtx(), "DateExpectedPO"));
			
			//
			miniTable.autoSize();
		}
		
		/**
		 * Get SQL for Orders that needs to be shipped
		 * @return sql
		 */
		private String getOrderSQL()
		{
			
			int	m_runplan_id = 0;
		
		  StringBuffer m_sql = new StringBuffer(
		 "select max(run.MRP_PlanRun_ID) id from MRP_PlanRun run \r\n" + 
		"left join MRP_Plan plan on plan.MRP_Plan_id=run.MRP_Plan_id \r\n" + 
		"where run.MRP_Plan_id=plan.MRP_Plan_ID \r\n" );
		
		  if (m_MRP_Plan_ID != null)
			  m_sql.append(" AND plan.MRP_Plan_ID=").append(m_MRP_Plan_ID);
		System.out.println(m_sql);
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(m_sql.toString(), null);
		//	pstmt.setInt(1, AD_Client_ID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
			m_runplan_id=rs.getInt(1);
			}
			System.out.println(m_runplan_id);
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, m_sql.toString(), e);
		}
		 String         a_PriorityImplementation= null;
		      //     a_PriorityImplementation= null;
		   a_PriorityImplementation=String. valueOf(m_PriorityImplementation);
			
		//  Create SQL
	        StringBuffer sql = new StringBuffer(
//	           "SELECT MPR.MRP_PlanRun_ID, mp.name,cp.name,mdl.Qty,plan.PRIORITYIMPLEMENTATION,plan.DateLastRun,cb.name  from MRP_Plan plan, \r\n" + 
//	           "	              MRP_MasterDemand md, MRP_MasterDemandLine mdl, M_Product mp,M_Product_PO mpo,C_BPartner cb,C_Period cp,MRP_PlanRun MPR  \r\n" + 
//	           "	              WHERE md.MRP_Plan_ID=plan.MRP_Plan_ID AND mdl.MRP_MasterDemand_ID=md.MRP_MasterDemand_ID AND mp.M_Product_ID=mdl.M_Product_ID \r\n" + 
//	           "                  AND mpo.M_Product_ID=mp.M_Product_ID \r\n" + 
//	           "	              AND cb.C_BPartner_ID=mpo.C_BPartner_ID AND cp.C_Period_ID=mdl.C_Period_ID AND Plan.MRP_Plan_id=MPR.MRP_Plan_id AND plan.AD_Client_ID=?" );

//	     "select distinct inventoryaudit.MRP_PlanRun_ID,mp.name,cp.name,cb.name,inventoryaudit.PRIORITYIMPLEMENTATION,inventoryaudit.ISBOM,\r\n" + 
//	     "inventoryaudit.QTYONHAND,inventoryaudit.QTYORDERED,inventoryaudit.QTYEXPECTEDWO,inventoryaudit.DATEEXPECTEDWO,inventoryaudit.QTYEXPECTEDPO,inventoryaudit.DATEEXPECTEDPO\r\n" + 
//	     "from mrp_inventory_audit inventoryaudit ,M_Product mp, C_Period cp,C_BPartner cb,mrp_plan plan,mrp_planrun run\r\n" + 
//	     "where mp.M_Product_ID = inventoryaudit.M_Product_ID and\r\n" + 
//	     "cp.C_Period_ID = run.C_PERIOD_FROM_ID and\r\n" + 
//	     "cb.C_BPartner_ID = inventoryaudit.C_BPartner_ID and\r\n" + 
//	     "run.MRP_PlanRun_ID = inventoryaudit.MRP_PlanRun_ID and\r\n" + 
//	     "run.mrp_plan_id =plan.mrp_plan_id\r\n" + 
//	     "AND plan.AD_Client_ID=?"  
	        		
//	        		"select  inventoryaudit.MRP_PlanRun_ID,mp.name Product,cp.name period,cb.name partner,inventoryaudit.PRIORITYIMPLEMENTATION,inventoryaudit.ISBOM,\r\n"+
//	        		"inventoryaudit.QTYONHAND,inventoryaudit.QTYORDERED,inventoryaudit.QTYEXPECTEDWO,inventoryaudit.DATEEXPECTEDWO,inventoryaudit.QTYEXPECTEDPO,inventoryaudit.DATEEXPECTEDPO \r\n"+ 
//	        		"from  mrp_inventory_audit inventoryaudit \r\n"+
//	        		"inner join M_Product mp on  mp.M_Product_ID=inventoryaudit.M_Product_ID \r\n"+
//	        		"inner join MRP_PlanRun run on run.MRP_PlanRun_ID=inventoryaudit.MRP_PlanRun_ID \r\n"+
//	        		"left join MRP_Plan plan on plan.MRP_Plan_id=run.MRP_Plan_id \r\n"+
//	        		"left join C_Period cp on  cp.C_Period_ID=run.C_Period_From_ID \r\n"+
//	        		"left join C_BPartner cb on cb.C_BPartner_ID=inventoryaudit.C_BPartner_ID \r\n"+
//	        		"where plan.MRP_Plan_id=run.mrp_plan_id \r\n"+
//	        		"and plan.ad_client_id=? \r\n"
	        		
 		"select  inventoryaudit.MRP_PlanRun_ID,mp.name Product,cp.name period,cb.name partner,inventoryaudit.PRIORITYIMPLEMENTATION,inventoryaudit.ISBOM,\r\n"+
		"inventoryaudit.QTYONHAND,inventoryaudit.QTYORDERED,inventoryaudit.QTYEXPECTEDWO,inventoryaudit.DATEEXPECTEDWO,inventoryaudit.QTYEXPECTEDPO,inventoryaudit.DATEEXPECTEDPO \r\n"+ 
		"from  mrp_inventory_audit inventoryaudit \r\n"+
		"inner join M_Product mp on  mp.M_Product_ID=inventoryaudit.M_Product_ID \r\n"+
		"inner join MRP_PlanRun run on run.MRP_PlanRun_ID=inventoryaudit.MRP_PlanRun_ID \r\n"+
		"left join MRP_Plan plan on plan.MRP_Plan_id=run.MRP_Plan_id \r\n"+
		"left join C_Period cp on  cp.C_Period_ID=run.C_Period_From_ID \r\n"+
		"left join C_BPartner cb on cb.C_BPartner_ID=inventoryaudit.C_BPartner_ID \r\n"+
		" where inventoryaudit.MRP_PlanRun_ID=run.MRP_PlanRun_ID \r\n"+
		"and plan.ad_client_id=? \r\n"
	     
	     
		);
//	        if (m_M_Warehouse_ID != null)
//	            sql.append(" AND ic.M_Warehouse_ID=").append(m_M_Warehouse_ID);
	       
//	        if (m_MRP_Plan_ID != null)
//	            sql.append(" AND plan.MRP_Plan_ID=").append(m_MRP_Plan_ID);
	    
	        if (m_MRP_Plan_ID != null)
	            sql.append(" AND run.MRP_PlanRun_ID=").append(m_runplan_id);
	        
	        if (m_M_Product_ID != null)
	            sql.append(" AND mp.M_Product_ID=").append(m_M_Product_ID);
	        
	        if (m_C_Period_ID != null)
	            sql.append(" AND cp.C_Period_ID=").append(m_C_Period_ID);
	        
	        if (m_C_BPartner_ID != null)
	            sql.append(" AND cb.C_BPartner_ID=").append(m_C_BPartner_ID);
	        
	        if (m_PriorityImplementation != null)
	        //    sql.append(" AND inventoryaudit.PRIORITYIMPLEMENTATION=").append(a_PriorityImplementation);
	        	sql.append(" AND inventoryaudit.PRIORITYIMPLEMENTATION='" +a_PriorityImplementation + "'");
	        			
	        
	        // bug - [ 1713317 ] Generate Shipments (manual) show locked records
	        /* begin - Exclude locked records; @Trifon */
	        int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
//	        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MOrder.Table_ID, AD_User_ID);
//	        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MMRPPlan.Table_ID, AD_User_ID);
//
//	        if (lockedIDs != null)
//	        { 
//	            if (sql.length() > 0)
//	                sql.append(" AND ");
//	            sql.append("MRP_Plan_ID").append(lockedIDs);
//	        }
	        /* eng - Exclude locked records; @Trifon */
	          
	        //
//	        sql.append(" ORDER BY o.Name,bp.Name,DateOrdered");
	       
	        
	        return sql.toString();
		}
		 
		/**
		 * Get SQL for Vendor RMA that need to be shipped
		 * @return sql
		 */
		private String getRMASql()
		{
		    StringBuffer sql = new StringBuffer();
		    
		    sql.append("SELECT rma.M_RMA_ID, org.Name, dt.Name, rma.DocumentNo, bp.Name, rma.Created, rma.Amt ");
		    sql.append("FROM M_RMA rma INNER JOIN AD_Org org ON rma.AD_Org_ID=org.AD_Org_ID ");
		    sql.append("INNER JOIN C_DocType dt ON rma.C_DocType_ID=dt.C_DocType_ID ");
		    sql.append("INNER JOIN C_BPartner bp ON rma.C_BPartner_ID=bp.C_BPartner_ID ");
		    sql.append("INNER JOIN M_InOut io ON rma.InOut_ID=io.M_InOut_ID ");
		    sql.append("WHERE rma.DocStatus='CO' ");
		    sql.append("AND dt.DocBaseType = 'POO' ");
		    sql.append("AND EXISTS (SELECT * FROM M_RMA r INNER JOIN M_RMALine rl ");
		    sql.append("ON r.M_RMA_ID=rl.M_RMA_ID WHERE r.M_RMA_ID=rma.M_RMA_ID ");
		    sql.append("AND rl.IsActive='Y' AND rl.M_InOutLine_ID > 0 AND rl.QtyDelivered < rl.Qty) ");
		    sql.append("AND NOT EXISTS (SELECT * FROM M_InOut oio WHERE oio.M_RMA_ID=rma.M_RMA_ID ");
		    sql.append("AND oio.DocStatus IN ('IP', 'CO', 'CL')) " );
		    sql.append("AND rma.AD_Client_ID=?");
		    
		    if (m_M_Warehouse_ID != null)
	            sql.append(" AND io.M_Warehouse_ID=").append(m_M_Warehouse_ID);
		    
		    if (m_MRP_Plan_ID != null)
	            sql.append(" AND ic.MRP_Plan_ID=").append(m_MRP_Plan_ID);
		    
	        if (m_C_BPartner_ID != null)
	            sql.append(" AND bp.C_BPartner_ID=").append(m_C_BPartner_ID);
	        
	        int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
	        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MRMA.Table_ID, AD_User_ID);
	        if (lockedIDs != null)
	        {
	            sql.append(" AND rma.M_RMA_ID").append(lockedIDs);
	        }
		    
		    sql.append(" ORDER BY org.Name, bp.Name, rma.Created ");

		    return sql.toString();
		}
		
		/**
		 *  Query Info
		 */
		public void executeQuery(KeyNamePair docTypeKNPair, IMiniTable miniTable)
		{
			log.info("");
			int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
			
			String sql = "";
			
			if (docTypeKNPair.getKey() == MRMA.Table_ID)
			{
			    sql = getRMASql();
			}
			else
			{
			    sql = getOrderSQL();
			}

			log.fine(sql);
			//  reset table
			int row = 0;
			miniTable.setRowCount(row);
			//  Execute
			try
			{
				PreparedStatement pstmt = DB.prepareStatement(sql.toString(), null);
				pstmt.setInt(1, AD_Client_ID);
				ResultSet rs = pstmt.executeQuery();
				//
				while (rs.next())
				{
					//  extend table
					miniTable.setRowCount(row+1);
					//  set values
					miniTable.setValueAt(new IDColumn(rs.getInt(1)), row, 0);   //  C_Order_ID
					miniTable.setValueAt(rs.getString(2), row, 1);              //  Org
					miniTable.setValueAt(rs.getString(3), row, 2);              //  DocType
					miniTable.setValueAt(rs.getString(4), row, 3);              //  Doc No
					miniTable.setValueAt(rs.getString(5), row, 4);  
					miniTable.setValueAt(rs.getString(6), row, 5);//  BPartner
					miniTable.setValueAt(rs.getBigDecimal(7), row, 6);           //  DateOrdered
					miniTable.setValueAt(rs.getBigDecimal(8), row, 7);          //  TotalLines
					miniTable.setValueAt(rs.getBigDecimal(9), row, 8); 
					miniTable.setValueAt(rs.getTimestamp(10), row, 9); 
					miniTable.setValueAt(rs.getBigDecimal(11), row, 10); 
					miniTable.setValueAt(rs.getTimestamp(12), row, 11); 
					 
					//  prepare next
					row++;
				}
				rs.close();
				pstmt.close();
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			//
			miniTable.autoSize();
		//	statusBar.setStatusDB(String.valueOf(miniTable.getRowCount()));
		}   //  executeQuery
		
		/**
		 *	Save Selection & return selection Query or ""
		 *  @return where clause like C_Order_ID IN (...)
		 */
		public void saveSelection(IMiniTable miniTable)
		{
			log.info("");
			//  Array of Integers
			ArrayList<Integer> results = new ArrayList<Integer>();
			setSelection(null);

			//	Get selected entries
			int rows = miniTable.getRowCount();
			for (int i = 0; i < rows; i++)
			{
				IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);     //  ID in column 0
			//	log.fine( "Row=" + i + " - " + id);
				if (id != null && id.isSelected())
					results.add(id.getRecord_ID());
			}

			if (results.size() == 0)
				return;
			log.config("Selected #" + results.size());
			setSelection(results);
		}	//	saveSelection

		
		/**************************************************************************
		 *	Generate Shipments
		 */
		public String generate(IStatusBar statusBar, KeyNamePair docTypeKNPair, String docActionSelected)
		{
			String info = "";
			log.info("M_Warehouse_ID=" + m_M_Warehouse_ID);
			log.info("MRP_Plan_ID=" + m_MRP_Plan_ID);
			
			String trxName = Trx.createTrxName("IOG");	
			Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
			
			setSelectionActive(false);  //  prevents from being called twice
			statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "ImplementPlanWindowNew"));
			statusBar.setStatusDB(String.valueOf(getSelection().size()));

			//	Prepare Process
			int AD_Process_ID = 0;	  
	        
	        if (docTypeKNPair.getKey() == MRMA.Table_ID)
	        {
	            AD_Process_ID = 52001; // M_InOut_GenerateRMA - org.adempiere.process.InOutGenerateRMA
	        }
	        else
	        {
	         //   AD_Process_ID = 199;      // M_InOut_Generate - org.cyprusbrs.process.InOutGenerate
	        //    AD_Process_ID =1000138;         //Implement Plan - org.cyprusbrs.process.ImplementPlanPurchase
	          AD_Process_ID =1000141;         //Implement Plan - org.cyprusbrs.process.ImplementPlanPurchase
	        	
	        }
			
			MPInstance instance = new MPInstance(Env.getCtx(), AD_Process_ID, 0);
			if (!instance.save())
			{
				info = Msg.getMsg(Env.getCtx(), "ProcessNoInstance");
				return info;
			}
			
			//insert selection
			StringBuffer insert = new StringBuffer();
			insert.append("INSERT INTO T_SELECTION(AD_PINSTANCE_ID, T_SELECTION_ID) ");
			int counter = 0;
			for(Integer selectedId : getSelection())
			{
				counter++;
				if (counter > 1)
					insert.append(" UNION ");
				insert.append("SELECT ");
				insert.append(instance.getAD_PInstance_ID());
				insert.append(", ");
				insert.append(selectedId);
				insert.append(" FROM DUAL ");
				
				if (counter == 1000) 
				{
					if ( DB.executeUpdate(insert.toString(), trxName) < 0 )
					{
						String msg = "No Shipments";     //  not translated!
						log.config(msg);
						info = msg;
						trx.rollback();
						return info;
					}
					insert = new StringBuffer();
					insert.append("INSERT INTO T_SELECTION(AD_PINSTANCE_ID, T_SELECTION_ID) ");
					counter = 0;
				}
			}
			if (counter > 0)
			{
				if ( DB.executeUpdate(insert.toString(), trxName) < 0 )
				{
					String msg = "No Shipments";     //  not translated!
					log.config(msg);
					info = msg;
					trx.rollback();
					return info;
				}
			}
			
			//call process
			//ProcessInfo pi = new ProcessInfo ("VInOutGen", AD_Process_ID);
			ProcessInfo pi = new ProcessInfo ("ImplementPlanWindowImplNew", AD_Process_ID);
			
			pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());

			//	Add Parameter - Selection=Y
			MPInstancePara ip = new MPInstancePara(instance, 10);
			ip.setParameter("Selection","Y");
			if (!ip.save())
			{
				String msg = "No Parameter added";  //  not translated
				info = msg;
				log.log(Level.SEVERE, msg);
				return info;
			}
			//Add Document action parameter
			ip = new MPInstancePara(instance, 20);
//			String docActionSelected = (String)docAction.getValue();
			ip.setParameter("DocAction", docActionSelected);
			if(!ip.save())
			{
				String msg = "No DocACtion Parameter added";
				info = msg;
				log.log(Level.SEVERE, msg);
				return info;
			}
//			//	Add Parameter - M_Warehouse_ID=x
//			ip = new MPInstancePara(instance, 30);
//			ip.setParameter("M_Warehouse_ID", Integer.parseInt(m_M_Warehouse_ID.toString()));
//			if(!ip.save())
//			{
//				String msg = "No Parameter added";  //  not translated
//				info = msg;
//				log.log(Level.SEVERE, msg);
//				return info;
//			}
			
//			Add Parameter - M_Warehouse_ID=x
			ip = new MPInstancePara(instance, 30);
			ip.setParameter("MRP_Plan_ID", Integer.parseInt(m_MRP_Plan_ID.toString()));
			if(!ip.save())
			{
				String msg = "No Parameter added";  //  not translated
				info = msg;
				log.log(Level.SEVERE, msg);
				return info;
			}
			
			setTrx(trx);
			setProcessInfo(pi);
			
			return info;
		}	//	generateShipments
		
		
		
		public void setMRP_Plan_ID(Object value)
		{
			this.m_MRP_Plan_ID = value;
		}
		
		public int getMRP_Plan_ID()
		{
			if (m_MRP_Plan_ID == null)
				return -1;
			return ((Integer)m_MRP_Plan_ID);
		}
	}