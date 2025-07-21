

package org.cyprusbrs.apps.form;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.apps.IStatusBar;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
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
 * Generate Invoice (manual) controller class
 * 
 */
public class Implementplangen extends GenForm
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(Implementplangen.class);
	//
	
	public Object 			m_MRP_Plan_ID = null;
	public Object 			m_M_Product_ID = null;
	public Object 			m_C_Period_ID = null;
	public Object 			m_OrderDate = null;
	public Object 			m_C_BPartner_ID = null;
	
	
	public void dynInit() throws Exception
	{
		setTitle("ImplementPlan");
		setReportEngineType(ReportEngine.ImplementPlan);
		setAskPrintMsg("Print Implement Plan");
	}
	
	public void configureMiniTable(IMiniTable miniTable)
	{
		//  create Columns
		miniTable.addColumn("MRP_Plan_ID");
		miniTable.addColumn("M_Product_ID");
		miniTable.addColumn("C_Period_ID");
		miniTable.addColumn("Order Quantity");
		miniTable.addColumn("Order Type");
		miniTable.addColumn("Order Date");
		miniTable.addColumn("C_BPartner_ID");
		//
		miniTable.setMultiSelection(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "M_Product_ID"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "C_Period_ID"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "Order Quantity"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "Order Type"));
		miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "Order Date"));
		miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		//
		miniTable.autoSize();
	}
	
	/**
	 * Get SQL for Orders that needs to be shipped
	 * @return sql
	 */
	private String getOrderSQL()
	{
		
		 StringBuffer sql = new StringBuffer(
				 "select distinct plan.MRP_Plan_ID,pr.name as ProductName, cper.name as PeriodName,mdl.qty as OrderQuantity from MRP_Plan plan " +
		        "left outer join mrp_planrun planrun on (planrun.MRP_Plan_ID=plan.MRP_Plan_ID) " +
		 		"left outer join MRP_MasterDemand md on (md.MRP_Plan_ID=md.MRP_Plan_ID) " + 
		 		"left outer join MRP_MasterDemandLine mdl on (mdl.MRP_MasterDemand_ID=md.MRP_MasterDemand_ID) " + 
		 		"left outer join m_product pr on (pr.m_product_id=mdl.m_product_id)  " + 
		 		"left outer join c_period cper on (cper.c_period_id=mdl.c_period_id) "
		 		+ "where plan.AD_Client_ID=? ");

	    
        if (m_MRP_Plan_ID != null)
            sql.append(" AND plan.MRP_Plan_ID=").append(m_MRP_Plan_ID);
        if (m_M_Product_ID != null)
            sql.append(" AND mdl.M_Product_ID=").append(m_M_Product_ID);
        if (m_C_Period_ID != null)
            sql.append(" AND mdl.C_Period_ID=").append(m_C_Period_ID);
        
        log.info(sql.toString());
        
        return sql.toString();
	}
	

	
	/**
	 *  Query Info
	 */
	public void executeQuery(KeyNamePair docTypeKNPair, IMiniTable miniTable)
	{
		log.info("");
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		//  Create SQL
		
		String sql = "";
       
        
        sql = getOrderSQL();


		//  reset table
		int row = 0;
		miniTable.setRowCount(row);
		//  Execute
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			 pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, AD_Client_ID);
			 rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//  extend table
				miniTable.setRowCount(row+1);
				//  set values
				miniTable.setValueAt(new IDColumn(rs.getInt(1)), row, 0);   //  
				miniTable.setValueAt(rs.getString(2), row, 1);              //  Product
				miniTable.setValueAt(rs.getString(3), row, 2);              //  Period
				miniTable.setValueAt(rs.getInt(4), row, 3);              //  Quantity

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
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//
		miniTable.autoSize();
	//	statusBar.setStatusDB(String.valueOf(miniTable.getRowCount()));
	}   //  executeQuery
	
	/**
	 *	Save Selection & return selecion Query or ""
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
	 *	Generate Resources
	 */
	public String generate(IStatusBar statusBar, KeyNamePair docTypeKNPair)
	{
		String info = "";
		String trxName = Trx.createTrxName("IVG");
		Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
		
		setSelectionActive(false);  //  prevents from being called twice
		//statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "InvGenerateGen"));
		statusBar.setStatusDB(String.valueOf(getSelection().size()));

		//	Prepare Process
		int AD_Process_ID = 0;
        
        if (docTypeKNPair.getKey() == MOrder.Table_ID)
        {
            AD_Process_ID = 1000265; // MRP_Plan
        }
        else
        {
            AD_Process_ID = 1000266;  // HARDCODED    C_InvoiceCreate
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
					String msg = "No Resources";     //  not translated!
					info = msg;
					log.config(msg);
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
				String msg = "No Resources";     //  not translated!
				info = msg;
				log.config(msg);
				trx.rollback();
				return info;
			}
		}
		
		ProcessInfo pi = new ProcessInfo ("", AD_Process_ID);
		pi.setAD_PInstance_ID (instance.getAD_PInstance_ID());

		//	Add Parameters
		MPInstancePara para = new MPInstancePara(instance, 10);
		para.setParameter("Selection", "Y");
		if (!para.save())
		{
			String msg = "No Selection Parameter added";  //  not translated
			info = msg;
			log.log(Level.SEVERE, msg);
			return info;
		}
		
		para = new MPInstancePara(instance, 20);
		//para.setParameter("DocAction", docActionSelected);
		
		if (!para.save())
		{
			String msg = "No DocAction Parameter added";  //  not translated
			info = msg;
			log.log(Level.SEVERE, msg);
			return info;
		}
		
		setTrx(trx);
		setProcessInfo(pi);
		
		return info;
	}	//	generateResourceCharges
	
	
	
}

