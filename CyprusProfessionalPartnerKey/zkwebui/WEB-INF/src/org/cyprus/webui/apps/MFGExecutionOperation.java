package org.cyprus.webui.apps;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.cyprusbrs.apps.TimeRecordDTO;
import org.cyprusbrs.apps.form.CreateCostCollector;
import org.cyprusbrs.apps.form.GenForm;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.minigrid.IMiniTable;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;
import org.zkoss.zul.Button;


public class MFGExecutionOperation extends GenForm
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(MFGExecutionOperation.class);
	
	
	IMiniTable miniTableNew;
	//
	
	public Object 			m_PP_Order_ID = null;
	public Object 			m_StepFrom = null;
	public Object 			m_StepTo = null;
	public Object 			m_M_OperationFrom_ID = null;
	public Object 			m_M_OperationTo_ID = null;
	public Object 			m_PP_Order_NodeFrom_ID = null;
	public Object 			m_PP_Order_NodeTo_ID = null;
	public Object 			m_CostCollectorType = null;
	public Object 			m_M_Product_ID = null;
	public Object 			m_AD_Workflow_ID = null;
	public Object 			m_PP_Product_BOM_ID = null;
	public Object 			m_M_Warehouse_ID = null;
	public Object 			m_Qty = null;
	public Object 			m_M_Locator_ID = null;
	
	public void dynInit() throws Exception
	{

	}
	
	public void configureMiniTable(IMiniTable miniTable)
	{
		
	//  create Columns
			miniTable.addColumn("PP_Order_ID");
			miniTable.addColumn("S.No");
			miniTable.addColumn("FGProduct");
			miniTable.addColumn("UOM");
			miniTable.addColumn("M_AttributeSetInstance_ID");
			miniTable.addColumn("Operation");
			miniTable.addColumn("Qty");
			miniTable.addColumn("Icon");
			miniTable.addColumn("M_Product_ID");
			miniTable.addColumn("C_UOM_ID");
			miniTable.addColumn("M_Operation_ID");
			//
			miniTable.setMultiSelection(true);
			//  set details
			miniTable.setColumnClass(0, IDColumn.class, false, " ");
			miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "S.No"));
			miniTable.setColumnClass(2, String.class, true, "FG Product");
			miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "C_UOM_ID"));
			miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
			miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "M_Operation_ID"));
			miniTable.setColumnClass(6, BigDecimal.class, true, Msg.translate(Env.getCtx(), "Qty"));
			miniTable.setColumnClass(7, Button.class, true, "Icon");
			miniTable.setColumnClass(8, Integer.class, true,"M_Product_ID");
			miniTable.setColumnClass(9, Integer.class, true,"C_UOM_ID");
			miniTable.setColumnClass(10, Integer.class, true,"M_Operation_ID");
			//
			miniTable.autoSize();
	
		//
		miniTable.autoSize();
	}
		
	
	
	/**
	 *  Query Info
	 */
	public void executeQuery(KeyNamePair docTypeKNPair, IMiniTable miniTable)
	{
		String sql = "SELECT PO.PP_ORDER_ID, ROW_NUMBER() OVER (ORDER BY PO.PP_ORDER_ID) AS SNO ,"
				+ " PO.M_PRODUCT_ID, PR.NAME AS PRODUCT, PO.C_UOM_ID, UOM.NAME AS UOM,"
				+ "PO.M_ATTRIBUTESETINSTANCE_ID, MA.DESCRIPTION ,"
				+ "PO.QTYENTERED"
				+ " FROM PP_ORDER PO "
				+ " JOIN M_PRODUCT PR ON (PR.M_PRODUCT_ID=PO.M_PRODUCT_ID)"
				+ " JOIN C_UOM UOM ON (UOM.C_UOM_ID=PO.C_UOM_ID)"
				+ " LEFT JOIN M_ATTRIBUTESETINSTANCE MA ON (MA.M_ATTRIBUTESETINSTANCE_ID=PO.M_ATTRIBUTESETINSTANCE_ID)"
				+ " WHERE PO.PP_ORDER_ID=" + m_PP_Order_ID
				+ "  ORDER BY PO.PP_ORDER_ID";
		
		int row = 0;
		miniTable.setRowCount(row);
		//  Execute
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			 pstmt = DB.prepareStatement(sql.toString(), null);
			 rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//  extend table
				miniTable.setRowCount(row+1);
				//  set values
				miniTable.setValueAt(new IDColumn(rs.getInt(1)), row, 0);  
				miniTable.setValueAt(rs.getString(2), row, 1); 
				miniTable.setValueAt(rs.getString(4), row, 2);            
				miniTable.setValueAt(rs.getString(6), row, 3);             
				miniTable.setValueAt(rs.getString(8), row, 4);       
				miniTable.setValueAt((BigDecimal) m_Qty, row, 6);
				miniTable.setValueAt(rs.getInt(3), row, 8);  
				miniTable.setValueAt(rs.getInt(5), row, 9); 

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
	}   //  executeQuery
	
	/**
	 *	Save Selection & return selecion Query or ""
	 *  @return where clause like PP_Order_ID IN (...)
	 */
	public void saveSelection(IMiniTable miniTable)
	{
		log.info("");
		//  Array of Integers
		ArrayList<Integer> results = new ArrayList<Integer>();
		setSelection(null);

		//	Get selected entries
		int rows = miniTable.getRowCount();
		if(rows > 0)
		{
			miniTableNew = miniTable;
		}
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

	

	public String generateCostCollector(List<TimeRecordDTO> timeRecords)
	{
		String Msg = "";
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("Order_ID", m_PP_Order_ID);
		dataMap.put("Product_ID", m_M_Product_ID);
		dataMap.put("BOM_ID", m_PP_Product_BOM_ID);
		dataMap.put("Warehouse_ID", m_M_Warehouse_ID);
		dataMap.put("Locator_ID", m_M_Locator_ID);
		dataMap.put("CostCollectorType", "170");
		dataMap.put("ActivityFrom_ID", m_PP_Order_NodeFrom_ID);
		dataMap.put("ActivityTo_ID", m_PP_Order_NodeTo_ID);
		dataMap.put("WorkFlow_ID", m_AD_Workflow_ID);
		dataMap.put("OperationFrom_ID", m_M_OperationFrom_ID);
		dataMap.put("OperationTo_ID", m_M_OperationTo_ID);
		dataMap.put("StepFrom", m_StepFrom);
		dataMap.put("StepTo", m_StepTo);
		dataMap.put("Qty", m_Qty);
		
		Msg = CreateCostCollector.createCostCollector(dataMap, miniTableNew, timeRecords);

		return Msg;
		
	}
}

