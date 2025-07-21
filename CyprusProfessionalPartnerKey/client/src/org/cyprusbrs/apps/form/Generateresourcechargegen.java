package org.cyprusbrs.apps.form;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprusbrs.apps.IStatusBar;
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
public class Generateresourcechargegen extends GenForm
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(Generateresourcechargegen.class);
	//
	
	public Object 			m_M_Product_ID = null;
	public Object 			m_MFG_WorkOrder_ID = null;
	public Object 			m_MFG_WorkCenter_ID = null;
	public Object 			m_M_Warehouse_ID = null;
	public Object 			m_ScheduledDateFrom = null;
	public Object 			m_ScheduledDateTo= null;
	public Object 			m_Supervisor= null;
	
	public void dynInit() throws Exception
	{
		setTitle("GenerateResourceCharges");
		setReportEngineType(ReportEngine.GenerateResourceCharges);
		setAskPrintMsg("Print Resource Charges");
	}
	
	public void configureMiniTable(IMiniTable miniTable)
	{
		//  create Columns
		miniTable.addColumn("MFG_WorkOrder_ID");
		miniTable.addColumn("MFG_WorkOrder_ID");
		miniTable.addColumn("MFG_WorkCenter_ID");
		miniTable.addColumn("MFG_Operation_ID");
		miniTable.addColumn("Quantity");
		miniTable.addColumn("C_UOM_ID");
		//
		miniTable.setMultiSelection(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "MFG_WorkOrder_ID"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "MFG_WorkCenter_ID"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "MFG_Operation_ID"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "Quantity"));
		miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "C_UOM_ID"));
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
	           "select wo.mfg_workorder_id,wo.documentno as WorkOrderNo,wc.name as WorkCenter,mo.name as Operation,wo.qtyentered as Quantity,uom.name as UOM from mfg_workorder wo " + 
	           "left outer join MFG_WorkOrderOperation wop on (wop.mfg_workorder_id=wo.mfg_workorder_id) " + 
	           "left outer join mfg_workcenter wc on (wc.mfg_workcenter_id=wop.mfg_workcenter_id) " + 
	           "left outer join MFG_Operation mo on (mo.MFG_Operation_ID=wop.MFG_Operation_ID) " + 
	           "left outer join c_uom uom on (uom.c_uom_id=wo.c_uom_id) " +
	           "left outer join m_product pr on (pr.m_product_id=wo.m_product_id) "
	           + "where wo.AD_Client_ID=? and wo.docstatus='IP' and wo.mfg_workorder_id not in (select mfg_workorder_id from mfg_workordertransaction) ");

	    
        if (m_M_Product_ID != null)
            sql.append(" AND wo.M_Product_ID=").append(m_M_Product_ID);
        if (m_MFG_WorkOrder_ID != null)
            sql.append(" AND wo.MFG_WorkOrder_ID=").append(m_MFG_WorkOrder_ID);
        if (m_MFG_WorkCenter_ID != null)
            sql.append(" AND wop.MFG_WorkCenter_ID=").append(m_MFG_WorkCenter_ID);
        if (m_M_Warehouse_ID != null)
            sql.append(" AND wo.M_Warehouse_ID=").append(m_M_Warehouse_ID);
        if (m_ScheduledDateFrom != null)
            sql.append(" AND wo.DATESCHEDULEFROM=").append(m_ScheduledDateFrom);
        if (m_Supervisor != null)
            sql.append(" AND wo.Supervisor_ID=").append(m_Supervisor);
        if (m_ScheduledDateTo != null)
            sql.append(" AND wo.DATESCHEDULETO=").append(m_ScheduledDateTo);
        
        // bug - [ 1713337 ] "Generate Invoices (manual)" show locked records.
        /* begin - Exclude locked records; @Trifon */
//        int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
//        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MOrder.Table_ID, AD_User_ID);
//        if (lockedIDs != null)
//        {
//            if (sql.length() > 0)
//                sql.append(" AND ");
//            sql.append("C_Order_ID").append(lockedIDs);
//        }
        /* eng - Exclude locked records; @Trifon */

        //
    //    sql.append(" ORDER BY o.Name,bp.Name,DateOrdered");
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
				miniTable.setValueAt(new IDColumn(rs.getInt(1)), row, 0);   //  MFG_WorkOrder_ID
				miniTable.setValueAt(rs.getString(2), row, 1);              //  Work Order
				miniTable.setValueAt(rs.getString(3), row, 2);              //  Work Center
				miniTable.setValueAt(rs.getString(4), row, 3);              //  Operation
				miniTable.setValueAt(rs.getInt(5), row, 4);                 // Quantity
				miniTable.setValueAt(rs.getString(6), row, 5);              //  UOM
//				miniTable.setValueAt(rs.getTimestamp(6), row, 5);           //  DateOrdered
			//	miniTable.setValueAt(rs.getBigDecimal(7), row, 6);          //  TotalLines
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
        
        if (docTypeKNPair.getKey() == MMFGWorkOrder.Table_ID)
        {
            AD_Process_ID = 1000186; // C_Invoice_GenerateRMA - org.adempiere.process.InvoiceGenerateRMA
        }
//        else
//        {
//            AD_Process_ID = 134;  // HARDCODED    C_InvoiceCreate
//        }
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
