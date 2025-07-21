
package org.cyprusbrs.apps.form;

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
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.minigrid.IMiniTable;
import org.cyprusbrs.print.ReportEngine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.KeyNamePair;
import org.cyprusbrs.util.Msg;

/**
 * Manufacturing Execution controller class
 * 
 */
public class MFGExecution extends GenForm
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(MFGExecution.class);
	
	IMiniTable miniTableNew;
	//
	
	public Object 			m_PP_Order_ID = null;
	public Object 			m_M_Operation_ID = null;
	public Object 			m_PP_Order_Node_ID = null;
	public Object 			m_CostCollectorType = null;
	public Object 			m_M_Product_ID = null;
	public Object 			m_AD_Workflow_ID = null;
	public Object 			m_PP_Product_BOM_ID = null;
	public Object 			m_M_Warehouse_ID = null;
	public Object 			m_Qty = null;
	public Object 			m_M_Locator_ID = null;
	
	public void dynInit() throws Exception
	{
		setReportEngineType(ReportEngine.MANUFACTURING_ORDER);
	}
	
	public void configureMiniTable(IMiniTable miniTable)
	{
		//  create Columns
		miniTable.addColumn("PP_Order_ID");
		miniTable.addColumn("S.No");
		miniTable.addColumn("Component");
		miniTable.addColumn("UOM");
		miniTable.addColumn("M_AttributeSetInstance_ID");
		miniTable.addColumn("IssueMethod");
		miniTable.addColumn("Qty");
		miniTable.addColumn("SFG");
		miniTable.addColumn("ComponentType");
		miniTable.addColumn("M_Product_ID");
		miniTable.addColumn("C_UOM_ID");
		//
		miniTable.setMultiSelection(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "S.No"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "Component"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "C_UOM_ID"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
		miniTable.setColumnClass(5, String.class, true, "Supply Type");
		miniTable.setColumnClass(6, BigDecimal.class, true, Msg.translate(Env.getCtx(), "Qty"));	
		miniTable.setColumnClass(7, String.class, true, Msg.translate(Env.getCtx(), "SFG"));
		miniTable.setColumnClass(8, String.class, true,"Component Type");
		miniTable.setColumnClass(9, Integer.class, true,"M_Product_ID");
		miniTable.setColumnClass(10, Integer.class, true,"C_UOM_ID");
	
		//
		miniTable.autoSize();
	}
		
	
	
	/**
	 *  Query Info
	 */
	public void executeQuery(KeyNamePair docTypeKNPair, IMiniTable miniTable)
	{
		String sql = "SELECT PO.PP_ORDER_ID, ROW_NUMBER() OVER (ORDER BY PO.PP_ORDER_ID) AS SNO , "
				+ " PPRB.M_PRODUCT_ID, PRB.NAME AS PRODUCT, PPRB.C_UOM_ID, UOM.NAME AS UOM,"
				+ "PPRB.M_ATTRIBUTESETINSTANCE_ID, MA.DESCRIPTION ,"
				+ "PPRB.ISSUEMETHOD, "
				+ "CASE WHEN PPRB.ISSUEMETHOD='1' THEN 'BACKFLUSH'"
				+ "	 WHEN PPRB.ISSUEMETHOD='2' THEN 'FLOOR STOCK'"
				+ "	 WHEN PPRB.ISSUEMETHOD='0' THEN 'ISSUE' END AS SUPPLYTYPE,"
				+ "PPRB.QTYBOM, "
				+ "CASE WHEN PPRB.COMPONENTTYPE='SF' THEN 'Y' ELSE 'N' END AS SFG, "
				+ "CASE WHEN PPRB.COMPONENTTYPE='BY' THEN 'BY-PRODUCT' "
				+ "     WHEN PPRB.COMPONENTTYPE='CP' THEN 'CO-PRODUCT' "
				+ "     WHEN PPRB.COMPONENTTYPE='CO' THEN 'COMPONENT' "
				+ "     WHEN PPRB.COMPONENTTYPE='OP' THEN 'OPTION' "
				+ "     WHEN PPRB.COMPONENTTYPE='OR' THEN 'OUTSIDE PROCESSING' "
				+ "     WHEN PPRB.COMPONENTTYPE='PK' THEN 'PACKING' "
				+ "     WHEN PPRB.COMPONENTTYPE='PH' THEN 'PHANTOM' "
				+ "     WHEN PPRB.COMPONENTTYPE='PL' THEN 'PLANNING' "
				+ "     WHEN PPRB.COMPONENTTYPE='SF' THEN 'SFG' "
				+ "     WHEN PPRB.COMPONENTTYPE='TL' THEN 'TOOLS' "
				+ "     WHEN PPRB.COMPONENTTYPE='VA' THEN 'VARIANT' END AS COMPONENTTYPE"
				+ " FROM PP_ORDER PO "
				+ " JOIN M_PRODUCT PR ON (PR.M_PRODUCT_ID=PO.M_PRODUCT_ID)"
				+ " JOIN PP_PRODUCT_BOM PPR ON (PPR.M_PRODUCT_ID=PR.M_PRODUCT_ID)"
				+ " JOIN PP_PRODUCT_BOMLINE PPRB ON (PPRB.PP_PRODUCT_BOM_ID=PPR.PP_PRODUCT_BOM_ID)"
				+ " JOIN M_PRODUCT PRB ON (PRB.M_PRODUCT_ID=PPRB.M_PRODUCT_ID)"
				+ " JOIN C_UOM UOM ON (UOM.C_UOM_ID=PPRB.C_UOM_ID)"
				+ " LEFT JOIN M_ATTRIBUTESETINSTANCE MA ON (MA.M_ATTRIBUTESETINSTANCE_ID=PPRB.M_ATTRIBUTESETINSTANCE_ID)"
				+ " WHERE PO.PP_ORDER_ID=" + m_PP_Order_ID
				+ " ORDER BY PO.PP_ORDER_ID";
		
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
				miniTable.setValueAt(rs.getString(10), row, 5);
				miniTable.setValueAt(rs.getBigDecimal(11).multiply((BigDecimal) m_Qty), row, 6);             
				miniTable.setValueAt(rs.getString(12), row, 7);          
				miniTable.setValueAt(rs.getString(13), row, 8);     
				miniTable.setValueAt(rs.getInt(3), row, 9);  
				miniTable.setValueAt(rs.getInt(5), row, 10);  

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
		dataMap.put("CostCollectorType", "110");
		dataMap.put("Activity_ID", m_PP_Order_Node_ID);
		dataMap.put("WorkFlow_ID", m_AD_Workflow_ID);
		dataMap.put("Operation_ID", m_M_Operation_ID);
		dataMap.put("Qty", m_Qty);
		
		Msg = CreateCostCollector.createCostCollector(dataMap, miniTableNew, timeRecords);

		return Msg;
		
	}	
	
}