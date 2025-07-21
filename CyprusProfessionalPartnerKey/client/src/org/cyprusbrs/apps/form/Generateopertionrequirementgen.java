

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
public class Generateopertionrequirementgen extends GenForm
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(Generateopertionrequirementgen.class);
	//
	
	public Object 			m_M_Product_ID = null;
	public Object 			m_MFG_WorkOrder_ID = null;
	public Object 			m_MFG_WorkCenter_ID = null;
	public Object 			m_M_Warehouse_ID = null;
	
	public void dynInit() throws Exception
	{
		setTitle("GenerateWorkOrderOperationRequirement");
		setReportEngineType(ReportEngine.GenerateOperationRequirement);
		setAskPrintMsg("Print WorkOrder Operation Requirement");
	}
	
	public void configureMiniTable(IMiniTable miniTable)
	{
		//  create Columns
		miniTable.addColumn("MFG_WorkOrder_ID");
		miniTable.addColumn("MFG_WorkOrder_ID");
		miniTable.addColumn("M_Product_ID");
		miniTable.addColumn("MFG_WorkOrderOperation_ID");
		miniTable.addColumn("Operation Scheduled Date From");
		miniTable.addColumn("Operation Scheduled Date To");
		//
		miniTable.setMultiSelection(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "MFG_WorkOrder_ID"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "M_Product_ID"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "MFG_WorkOrderOperation_ID"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "Operation Scheduled Date From"));
		miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "Operation Scheduled Date To"));
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
	           "select wo.mfg_workorder_id,wo.documentno as WorkOrderNo,pr.name as ProductAssembly,mo.name as Operation,wop.DATESCHEDULEFROM as DATESCHEDULEFROM,wop.DATESCHEDULETO as DATESCHEDULETO from mfg_workorder wo " + 
	           "left outer join MFG_WorkOrderOperation wop on (wop.mfg_workorder_id=wo.mfg_workorder_id) " + 
	           "left outer join mfg_workcenter wc on (wc.mfg_workcenter_id=wop.mfg_workcenter_id) " + 
	           "left outer join MFG_Operation mo on (mo.MFG_Operation_ID=wop.MFG_Operation_ID) " + 
	           "left outer join c_uom uom on (uom.c_uom_id=wo.c_uom_id) " +
	           "left outer join m_product pr on (pr.m_product_id=wo.m_product_id) "
	           + "where wo.AD_Client_ID=?");

	    
        if (m_M_Product_ID != null)
            sql.append(" AND wo.M_Product_ID=").append(m_M_Product_ID);
        if (m_MFG_WorkOrder_ID != null)
            sql.append(" AND wo.MFG_WorkOrder_ID=").append(m_MFG_WorkOrder_ID);
        if (m_MFG_WorkCenter_ID != null)
            sql.append(" AND wop.MFG_WorkCenter_ID=").append(m_MFG_WorkCenter_ID);
        if (m_M_Warehouse_ID != null)
            sql.append(" AND wo.M_Warehouse_ID=").append(m_M_Warehouse_ID);
        
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
	
//	/**
//	 * Get SQL for Customer RMA that need to be invoiced
//	 * @return sql
//	 */
//	private String getRMASql()
//	{
//		StringBuffer sql = new StringBuffer();
//	    sql.append("SELECT rma.M_RMA_ID, org.Name, dt.Name, rma.DocumentNo, bp.Name, rma.Created, rma.Amt ");
//        sql.append("FROM M_RMA rma INNER JOIN AD_Org org ON rma.AD_Org_ID=org.AD_Org_ID ");
//        sql.append("INNER JOIN C_DocType dt ON rma.C_DocType_ID=dt.C_DocType_ID ");
//        sql.append("INNER JOIN C_BPartner bp ON rma.C_BPartner_ID=bp.C_BPartner_ID ");
//        sql.append("INNER JOIN M_InOut io ON rma.InOut_ID=io.M_InOut_ID ");
//        sql.append("WHERE rma.DocStatus='CO' ");
//        sql.append("AND dt.DocBaseType = 'SOO' ");
//        // sql.append("AND NOT EXISTS (SELECT * FROM C_Invoice i ");
//        // sql.append("WHERE i.M_RMA_ID=rma.M_RMA_ID AND i.DocStatus IN ('IP', 'CO', 'CL')) ");
//        // sql.append("AND EXISTS (SELECT * FROM C_InvoiceLine il INNER JOIN M_InOutLine iol ");
//        // sql.append("ON il.M_InOutLine_ID=iol.M_InOutLine_ID INNER JOIN C_Invoice i ");
//        // sql.append("ON i.C_Invoice_ID=il.C_Invoice_ID WHERE i.DocStatus IN ('CO', 'CL') ");
//        // sql.append("AND iol.M_InOutLine_ID IN ");
//        // sql.append("(SELECT M_InOutLine_ID FROM M_RMALine rl WHERE rl.M_RMA_ID=rma.M_RMA_ID ");
//        // sql.append("AND rl.M_InOutLine_ID IS NOT NULL)) ");
//        sql.append("AND rma.AD_Client_ID=?");
//        
//        if (m_AD_Org_ID != null)
//            sql.append(" AND rma.AD_Org_ID=").append(m_AD_Org_ID);
//        if (m_C_BPartner_ID != null)
//            sql.append(" AND bp.C_BPartner_ID=").append(m_C_BPartner_ID);
//        
//        int AD_User_ID = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");
//        String lockedIDs = MPrivateAccess.getLockedRecordWhere(MRMA.Table_ID, AD_User_ID);
//        if (lockedIDs != null)
//        {
//            sql.append(" AND rma.M_RMA_ID").append(lockedIDs);
//        }
//        
//        sql.append(" ORDER BY org.Name, bp.Name, rma.Created ");
//        
//        return sql.toString();
//	}
	
	/**
	 *  Query Info
	 */
	public void executeQuery(KeyNamePair docTypeKNPair, IMiniTable miniTable)
	{
		log.info("");
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
		//  Create SQL
		
		String sql = "";
        
//        if (docTypeKNPair.getKey() == MMFGWorkOrder.Table_ID)
//        {
//            sql = getOrderSQL();
//        }
        
        sql = getOrderSQL();
//        else
//        {
//            sql = getRMASql();
//        }

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
				miniTable.setValueAt(rs.getString(3), row, 2);              //  ProductAssembly
				miniTable.setValueAt(rs.getString(4), row, 3);              //  Operation
				miniTable.setValueAt(rs.getDate(5), row, 4);                 // DateSceduledFrom
				miniTable.setValueAt(rs.getDate(6), row, 5);              //  DateSceduledTo
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
            AD_Process_ID = 1000185; // C_Invoice_GenerateRMA - org.adempiere.process.InvoiceGenerateRMA
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

