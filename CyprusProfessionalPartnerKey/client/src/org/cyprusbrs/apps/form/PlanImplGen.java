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
package org.cyprusbrs.apps.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.cyprusbrs.apps.IStatusBar;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.framework.MPInstance;
import org.cyprusbrs.framework.MPInstancePara;
import org.cyprusbrs.framework.MPriceList;
import org.cyprusbrs.framework.MProductPO;
import org.cyprusbrs.framework.MRMA;
import org.cyprusbrs.framework.MRequisition;
import org.cyprusbrs.framework.MRequisitionLine;
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
import org.eevolution.model.MPPOrder;

/**
 * Generate Shipment (manual) controller class
 * 
 */
public class PlanImplGen extends GenForm
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(PlanImplGen.class);
	//
	
	public Object 			m_M_Warehouse_ID = null;
	public Object 			m_M_MasterDemand_ID = null;
	public Object 			m_C_Period_ID = null;
	public Object 			m_C_BPartner_ID = null;
	Timestamp timestamp=new Timestamp(System.currentTimeMillis());

	public List<SelectedDataList> selectedDataList=null;
	
	public void dynInit() throws Exception
	{
		setTitle("Plan Implementation");
		setReportEngineType(ReportEngine.SHIPMENT);
		setAskPrintMsg("PrintShipments");
	}
	
	public void configureMiniTable(IMiniTable miniTable)
	{
		//  create Columns
		miniTable.addColumn("ID");
		miniTable.addColumn("C_Period_ID");
		miniTable.addColumn("M_Product_ID");
		miniTable.addColumn("C_UOM_ID");
		miniTable.addColumn("OrderType");
		miniTable.addColumn("DemandQty");
		miniTable.addColumn("ActualQtyToOrder");
		miniTable.addColumn("VendorFromProduct");
//		miniTable.addColumn("TotalLines");
//		miniTable.addColumn("M_Product_PO");
		//
		miniTable.setMultiSelection(true);

		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "Period"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "Product"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "UOM"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "OrderType"));
		miniTable.setColumnClass(5, String.class, false, Msg.translate(Env.getCtx(), "DemandQty"));
		miniTable.setColumnClass(6, String.class, false, Msg.translate(Env.getCtx(), "ActualQtyToOrder"));
		miniTable.setColumnClass(7, String.class, true, Msg.translate(Env.getCtx(), "VendorFromProduct"));
		//miniTable.setColumnClass(7, BigDecimal.class, true, Msg.translate(Env.getCtx(), "VendorFromProduct"));

		//
		miniTable.autoSize();
	}
	
	
	/**
	 * Create Query for Master Demand
	 * @param docTypeKNPair
	 * @param miniTable
	 */
	public void executeQueryMasterDemand(String docTypeKNPair, IMiniTable miniTable) {
		
		log.info("");
		String sql = getMasterDemandSql();
		
		log.fine(sql);
		//  reset table
		int row = 0;
		miniTable.setRowCount(row);
		
		//  Execute
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			 pstmt = DB.prepareStatement(sql.toString(), null);
//			pstmt.setInt(1, AD_Client_ID);
			 rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//  extend table
				miniTable.setRowCount(row+1);
				//  set values
				Integer _Product_ID=rs.getInt("_Product_ID");
				Integer _BPartner_ID=rs.getInt("_BPartner_ID");
				Integer _C_UOM_ID=rs.getInt("_C_UOM_ID");
				Integer _M_MasterDemandLineID=rs.getInt("_M_MasterDemandLineID");
				
				//Integer _WarehouseID=rs.getInt("_WarehouseID");
				//Integer __PP_Product_BOM_ID=rs.getInt("_PP_Product_BOM_ID");
				KeyNamePair periodWithMasterDemandLineID=new KeyNamePair(_M_MasterDemandLineID,rs.getString("_Period"));
				KeyNamePair bPartnerId=new KeyNamePair(_BPartner_ID,rs.getString("_VendorOfProduct"));
				KeyNamePair productId=new KeyNamePair(_Product_ID,rs.getString("_ProductName"));
				KeyNamePair uomID=new KeyNamePair(_C_UOM_ID,rs.getString("_UOM")); 
				
				miniTable.setValueAt(new IDColumn(row+1), row, 0);   //  Requisition/PO
				miniTable.setValueAt(periodWithMasterDemandLineID, row, 1);              //  Period
				miniTable.setValueAt(productId, row, 2);         //  Product
				miniTable.setValueAt(uomID, row, 3);             	//  UOM
				miniTable.setValueAt(rs.getString("_OrderType"), row, 4);          	//  _OrderType		
				miniTable.setValueAt(rs.getString("_DemandQty"), row, 5);          	//  DemandQty
				miniTable.setValueAt(rs.getString("_ActualQtyToOrder"), row, 6);    //  Doc No
				miniTable.setValueAt(bPartnerId, row, 7);     //  BPartner	by KeyName Pair
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
		
	}
	
	private String getMasterDemandSql() {
		
		StringBuffer sql = new StringBuffer();
	    
	    sql.append(" SELECT mp.isBom AS _BOM, cp.NAME AS _Period,mp.NAME _ProductName, ");
	    sql.append(" CASE WHEN mp.isBom='Y' THEN 'Manufacture' ELSE 'Purchase' END AS _OrderType, ");
	    sql.append(" CASE WHEN mp.isBom='Y' THEN ROUND(SUM(mdl.qty), 2) ELSE NULL END AS _DemandQty, ");
	    sql.append(" CASE WHEN mp.isBom='N' THEN ROUND(SUM(mdl.qty), 2) ELSE NULL END AS _ActualQtyToOrder, ");
	    sql.append(" CASE WHEN mp.isBom='N' THEN cb.Name ELSE NULL END AS _VendorOfProduct, ");
	    sql.append(" cb.C_BPartner_ID AS _BPartner_ID, ");
	    sql.append(" mdl.M_MasterDemandLine_ID AS _M_MasterDemandLineID,");
	    sql.append(" cu.NAME AS _UOM, cu.C_UOM_ID AS _C_UOM_ID, mp.M_Product_ID AS _Product_ID FROM M_MasterDemand md ");
	    sql.append(" INNER JOIN M_MasterDemandLine mdl ON (md.M_MasterDemand_ID=mdl.M_MasterDemand_ID) ");
	    sql.append(" INNER JOIN C_Period cp ON (cp.C_Period_ID=mdl.C_Period_ID) ");
	    sql.append(" INNER JOIN M_Product mp ON (mp.M_Product_ID=mdl.M_Product_ID) ");
	    sql.append(" INNER JOIN C_UOM cu ON (cu.C_UOM_ID=mp.C_UOM_ID) ");
	    sql.append(" LEFT OUTER JOIN M_Product_PO mpp ON (mpp.M_Product_ID=mp.M_Product_ID) ");
	    sql.append(" LEFT OUTER JOIN C_BPartner cb on (mpp.C_BPartner_ID=cb.C_BPartner_ID) ");
	    // Below Code updated by Mukesh to filter by Period
	    if(getC_Period_ID()>0)
	    sql.append(" WHERE mdl.IsPlanExecuted='N' AND md.M_MasterDemand_ID="+getM_MasterDemand_ID()+" AND mdl.C_Period_ID="+getC_Period_ID());
    	else
    	// End of above code	
	    sql.append(" WHERE mdl.IsPlanExecuted='N' AND md.M_MasterDemand_ID="+getM_MasterDemand_ID());
	    
	    sql.append(" GROUP BY _ProductName,_Period,_UOM,_VendorOfProduct, _BOM,_Product_ID,_BPartner_ID, _M_MasterDemandLineID,_C_UOM_ID ");
	    
	    return sql.toString();
	}

	/**
	 *	Save Selection & return selection Query or ""
	 *  @return where clause like C_Order_ID IN (...)
	 */
	public void saveSelection(IMiniTable miniTable)
	{
		log.info("");
		//  Array of Integers
		ArrayList<Integer> results = new ArrayList<Integer>();
		List<SelectedDataList> resultDataList = new ArrayList<SelectedDataList>();

		setSelection(null);
		setSelectedDataList(null);
		//	Get selected entries
		int rows = miniTable.getRowCount();
		for (int i = 0; i <rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);     //  ID in column 0
			if (id != null && id.isSelected())
			{
				KeyNamePair periodWithMasterDeLineID = (KeyNamePair)miniTable.getValueAt(i, 1);     //  ID in column 0
				KeyNamePair product = (KeyNamePair)miniTable.getValueAt(i, 2);     //  ID in column 0
				KeyNamePair _uom = (KeyNamePair)miniTable.getValueAt(i, 3);     //  ID in column 0

				String _OrderType = (String)miniTable.getValueAt(i, 4);     //  ID in column 0
				String _DemandQty = (String)miniTable.getValueAt(i, 5);     //  ID in column 0
				String _ActualQty = (String)miniTable.getValueAt(i, 6);     //  ID in column 0
				KeyNamePair vendor = (KeyNamePair)miniTable.getValueAt(i, 7);     //  ID in column 0			
				Integer MasterDeLineID=periodWithMasterDeLineID.getKey();
				Integer _WarehouseID= DB.getSQLValue(null, "SELECT m_warehouse_id FROM M_MasterDemandLine where M_MasterDemandLine_id="+MasterDeLineID); // (Integer)miniTable.getValueAt(i, 8);
				Integer __PP_Product_BOM_ID=DB.getSQLValue(null, "SELECT PP_Product_BOM_ID FROM M_MasterDemandLine where M_MasterDemandLine_id="+MasterDeLineID); //(Integer)miniTable.getValueAt(i, 9);

				//			IDColumn id4 = (IDColumn)miniTable.getValueAt(i, 4);     //  ID in column 0
				//			IDColumn id5 = (IDColumn)miniTable.getValueAt(i, 5);     //  ID in column 0
				
				id.toString();
				results.add(id.getRecord_ID());
				Integer vendorId=0;
				if(vendor!=null && vendor.getKey()>0)
					vendorId=vendor.getKey();

				BigDecimal demandQty=Env.ZERO;
				if(_DemandQty!=null)
					demandQty=new BigDecimal(_DemandQty);

				BigDecimal actualQty=Env.ZERO;
				if(_ActualQty!=null)
					actualQty=new BigDecimal(_ActualQty);

				SelectedDataList dataList=new SelectedDataList(id.getRecord_ID(),product.getName(), 
						product.getKey(),_OrderType,  demandQty, actualQty, vendorId);
				dataList.setWarehouseID(_WarehouseID);
				dataList.setBomId(__PP_Product_BOM_ID);
				dataList.setcUOMID(_uom.getKey());
				dataList.setMasterDeLineID(MasterDeLineID);
				resultDataList.add(dataList);
			}
		}

		if (results.size() == 0)
			return;
		log.config("Selected #" + results.size());
		log.config("Selected #" + resultDataList.size());

		setSelection(results);
		setSelectedDataList(resultDataList);
	}	//	saveSelection

	
	/**************************************************************************
	 *	Generate Shipments
	 */
	
	// Generate Manufacturing Order 
	public String generateManufacturing(IStatusBar statusBar, ArrayList<Integer> selectItem,IMiniTable miniTable,
			List<SelectedDataList> list, java.util.Map<String,Object> selectedOrderType)
	{
		Boolean M=(Boolean)selectedOrderType.get("M");
		Boolean P=(Boolean)selectedOrderType.get("P");
		Boolean R=(Boolean)selectedOrderType.get("R");


		statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "Manufacturing Generated.."));
		statusBar.setStatusDB(String.valueOf(selectItem.size()));
		statusBar.setStatusDB(String.valueOf(list.size()));
		
		StringBuilder sb=new StringBuilder("<br> List Of Item : <br><br>");

		String trxName = Trx.createTrxName("PlanImplementationCommit");	
		Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
		setTrx(trx);
		Integer C_DocTypeM=0;
		Integer C_DocTypeP=0;
		Integer C_DocTypeR=0;

		if(M)
		C_DocTypeM=DB.getSQLValue(trx.getTrxName(), "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='MOP'");
		if(P)
		C_DocTypeP=DB.getSQLValue(trx.getTrxName(), "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='POO'");
		if(R)
		C_DocTypeR=DB.getSQLValue(trx.getTrxName(), "SELECT C_DocType_ID FROM C_DocType WHERE DocBaseType='POR'");
	
		for (SelectedDataList selectedDataList : list) {
			
			Integer id = selectedDataList.getIdColumn();     //  ID in column 0
			String productName = selectedDataList.getProductName();     //  ID in column 0
			String _OrderType = selectedDataList.getOrderType();     //  ID in column 0
			BigDecimal _DemandQty = selectedDataList.getDemandQty();     //  ID in column 0
			BigDecimal _ActualQty = selectedDataList.getActualQtyToOrder();     //  ID in column 0
			
			if(M && "Manufacture".equalsIgnoreCase(_OrderType))
			{	
				// Extra Fields 
				selectedDataList.setDocTypeId(C_DocTypeM);

				Integer workFlowId=0;
				
				if(selectedDataList.getMasterDeLineID()>0)
					workFlowId=DB.getSQLValue(trx.getTrxName(), "SELECT AD_Workflow_ID FROM M_MasterDemandLine WHERE M_MasterDemandLine_ID="+selectedDataList.getMasterDeLineID());
				else	
					workFlowId=DB.getSQLValue(trx.getTrxName(), "SELECT AD_Workflow_ID FROM AD_Workflow WHERE PP_Product_BOM_ID="+selectedDataList.getBomId());

				Integer resourceId=DB.getSQLValue(trxName, "SELECT s_resource_id FROM PP_Product_BOM WHERE PP_Product_BOM_ID="+selectedDataList.getBomId());
				selectedDataList.setResourceID(resourceId);

				if(workFlowId>0)
				{
					selectedDataList.setWorkflowID(workFlowId);
					// end of extra field
					MPPOrder ppOrder=createManufacturingOrder(selectedDataList);
					if(ppOrder!=null)
						sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" : "+ppOrder.getDocumentNo()+" <br>");
				}
				else
				{
					sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" : No WorkFlow for BOM <br>");
				}
			}
			else if("Purchase".equalsIgnoreCase(_OrderType))  // For Purchase and Requisition
			{
				
				if(P) // Purchase Order created
				{
					selectedDataList.setDocTypeId(C_DocTypeP);
					MOrder mOrder= createPurchaseOrder(selectedDataList);
					if(mOrder!=null)
					sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" Order : "+mOrder.getDocumentNo()+" <br>");
					else
					sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" Order : Purchase Order is not created <br>");
				}
				else if (R) // Requisition created
				{
					selectedDataList.setDocTypeId(C_DocTypeR);

					MRequisition mRequisition= createRequisition(selectedDataList);
					if(mRequisition!=null)
					sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" Requisition : "+mRequisition.getDocumentNo()+" <br>");
					else
					sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" Requisition : Requisition is not created <br>");
				}
				else
				{
					sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" : No Purchase Order OR Requisition selected <br>");
				}
			}
			else 
			{
				sb.append(id+" ( "+productName+" :  "+(_DemandQty.compareTo(Env.ZERO)>0?_DemandQty:_ActualQty)+") "+_OrderType+" : No Purchase Order OR Requisition OR Manufacturing selected <br>");

			}
						
		}
				
		return sb.toString();
	}
	
	public String generate(IStatusBar statusBar, KeyNamePair docTypeKNPair, String docActionSelected)
	{
		String info = "";
		System.out.println("docTypeKNPair "+docTypeKNPair.getKey()+" :: "+docTypeKNPair.getName());
		System.out.println("M_Warehouse_ID=" + m_M_Warehouse_ID);
		log.info("M_Warehouse_ID=" + m_M_Warehouse_ID);
		String trxName = Trx.createTrxName("IOG");	
		Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
		
		setSelectionActive(false);  //  prevents from being called twice
		statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "InOutGenerateGen"));
		statusBar.setStatusDB(String.valueOf(getSelection().size()));

		//	Prepare Process
		int AD_Process_ID = 0;	  
        
        if (docTypeKNPair.getKey() == MRMA.Table_ID)
        {
            AD_Process_ID = 52001; // M_InOut_GenerateRMA - org.adempiere.process.InOutGenerateRMA
        }
        else
        {
            AD_Process_ID = 199;      // M_InOut_Generate - org.cyprusbrs.process.InOutGenerate
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
		ProcessInfo pi = new ProcessInfo ("VInOutGen", AD_Process_ID);
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
//		String docActionSelected = (String)docAction.getValue();
		ip.setParameter("DocAction", docActionSelected);
		if(!ip.save())
		{
			String msg = "No DocACtion Parameter added";
			info = msg;
			log.log(Level.SEVERE, msg);
			return info;
		}
		//	Add Parameter - M_Warehouse_ID=x
		ip = new MPInstancePara(instance, 30);
		ip.setParameter("M_Warehouse_ID", 0);// Integer.parseInt(m_M_Warehouse_ID.toString()));
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
	
	public void setM_Warehouse_ID(Object value)
	{
		this.m_M_Warehouse_ID = value;
	}
	
	public int getM_Warehouse_ID()
	{
		if (m_M_Warehouse_ID == null)
			return -1;
		return ((Integer)m_M_Warehouse_ID);
	}
	
	public void setM_MasterDemand_ID(Object value)
	{
		this.m_M_MasterDemand_ID = value;
	}
	public int getM_MasterDemand_ID()
	{
		if (m_M_MasterDemand_ID == null)
			return -1;
		return ((Integer)m_M_MasterDemand_ID);
	}
	public void setC_Period_ID(Object value)
	{
		this.m_C_Period_ID = value;
	}
	public int getC_Period_ID()
	{
		if (m_C_Period_ID == null)
			return -1;
		return ((Integer)m_C_Period_ID);
	}
	
	public List<SelectedDataList> getSelectedDataList() {
		return selectedDataList;
	}

	public void setSelectedDataList(List<SelectedDataList> selectedDataList) {
		this.selectedDataList = selectedDataList;
	}

	class SelectedDataList implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Integer idColumn;
		private String productName;
		private Integer productId;
		private String orderType;
		private BigDecimal demandQty;
		private BigDecimal actualQtyToOrder;
		private Integer vendorId;
		private Integer bomId;
		private Integer resourceID;
		private Integer workflowID;
		private Integer docTypeId;
		private Integer warehouseID;
		private Integer cUOMID;
		private Integer masterDeLineID;
		public SelectedDataList() {}
		
		public SelectedDataList(Integer idColumn, String productName, Integer productId, String orderType,
				BigDecimal demandQty, BigDecimal actualQtyToOrder, Integer vendorId) {
			super();
			this.idColumn = idColumn;
			this.productName = productName;
			this.productId = productId;
			this.orderType = orderType;
			this.demandQty = demandQty;
			this.actualQtyToOrder = actualQtyToOrder;
			this.vendorId = vendorId;
		}
		
		public Integer getMasterDeLineID() {
			return masterDeLineID;
		}

		public void setMasterDeLineID(Integer masterDeLineID) {
			this.masterDeLineID = masterDeLineID;
		}

		public Integer getcUOMID() {
			return cUOMID;
		}

		public void setcUOMID(Integer cUOMID) {
			this.cUOMID = cUOMID;
		}

		public Integer getWarehouseID() {
			return warehouseID;
		}

		public void setWarehouseID(Integer warehouseID) {
			this.warehouseID = warehouseID;
		}

		public Integer getDocTypeId() {
			return docTypeId;
		}

		public void setDocTypeId(Integer docTypeId) {
			this.docTypeId = docTypeId;
		}

		public Integer getWorkflowID() {
			return workflowID;
		}

		public void setWorkflowID(Integer workflowID) {
			this.workflowID = workflowID;
		}

		public Integer getResourceID() {
			return resourceID;
		}

		public void setResourceID(Integer resourceID) {
			this.resourceID = resourceID;
		}

		public Integer getBomId() {
			return bomId;
		}

		public void setBomId(Integer bomId) {
			this.bomId = bomId;
		}
		public Integer getIdColumn() {
			return idColumn;
		}
		public void setIdColumn(Integer idColumn) {
			this.idColumn = idColumn;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public Integer getProductId() {
			return productId;
		}
		public void setProductId(Integer productId) {
			this.productId = productId;
		}
		public String getOrderType() {
			return orderType;
		}
		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}
		public BigDecimal getDemandQty() {
			return demandQty;
		}
		public void setDemandQty(BigDecimal demandQty) {
			this.demandQty = demandQty;
		}
		public BigDecimal getActualQtyToOrder() {
			return actualQtyToOrder;
		}
		public void setActualQtyToOrder(BigDecimal actualQtyToOrder) {
			this.actualQtyToOrder = actualQtyToOrder;
		}
		public Integer getVendorId() {
			return vendorId;
		}
		public void setVendorId(Integer vendorId) {
			this.vendorId = vendorId;
		}				
	}

	/********************************Create Manufacturing, Requisition and Purchase Order*************************/
	
	/**
	 * Create Manufacturing
	 * @param selectedDataList
	 * @return
	 */
	public MPPOrder createManufacturingOrder(SelectedDataList selectedDataList)
	{
//		String trxName = Trx.createTrxName("ManufacturingCommit");	
//		Trx trx = Trx.get(trxName, true);	//trx needs to be committed too
			MPPOrder mppOrder=null;
			if(selectedDataList!=null)
			{
				mppOrder=new MPPOrder(Env.getCtx(), 0,getTrx().getTrxName());
				mppOrder.setC_DocType_ID(selectedDataList.getDocTypeId());
				mppOrder.setC_DocTypeTarget_ID(selectedDataList.getDocTypeId());
				mppOrder.setS_Resource_ID(selectedDataList.getResourceID());
				mppOrder.setPriorityRule("5");
				mppOrder.setM_Warehouse_ID(selectedDataList.getWarehouseID());
				mppOrder.setAD_Workflow_ID(selectedDataList.getWorkflowID());
				mppOrder.setM_Product_ID(selectedDataList.getProductId());
				mppOrder.setPP_Product_BOM_ID(selectedDataList.getBomId());				
				mppOrder.setDatePromised(timestamp);
				mppOrder.setDateOrdered(timestamp);
				mppOrder.setDateStartSchedule(timestamp);
				mppOrder.set_ValueNoCheck("M_MasterDemandLine_ID", selectedDataList.getMasterDeLineID());
				mppOrder.setDescription("By Plan Implementation");
				if(mppOrder.save(getTrx().getTrxName()))
				{
					try {
						DB.commit(true, getTrx().getTrxName());
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					getTrx().rollback();
				}
				
			}
			return mppOrder; 	
		}
	
	/**
	 * Create Requisition
	 * @param selectedDataList2
	 */
	private MRequisition createRequisition(SelectedDataList selectedDataList) {
		// TODO Auto-generated method stub
		
		MRequisition mRequisition=null;
		if(selectedDataList!=null)
		{
			mRequisition=new MRequisition(Env.getCtx(), 0, getTrx().getTrxName());
			mRequisition.setC_DocType_ID(selectedDataList.getDocTypeId());
			mRequisition.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
			mRequisition.setPriorityRule("5");
			mRequisition.setDateRequired(timestamp);
			mRequisition.setDateDoc(timestamp);
			mRequisition.setM_Warehouse_ID(selectedDataList.getWarehouseID());
			mRequisition.setM_PriceList_ID(); // Get Default Price List
			mRequisition.setDescription("By Plan Implementation");
			if(!mRequisition.save(getTrx().getTrxName()))
			{
				getTrx().rollback();
				 
			}
			if(mRequisition!=null) // Create Lines
			{
				MRequisitionLine line=new MRequisitionLine(mRequisition);
				line.setM_Product_ID(selectedDataList.getProductId());
				line.setQty(selectedDataList.getActualQtyToOrder());
				line.setC_UOM_ID(selectedDataList.getcUOMID());
				line.set_ValueNoCheck("M_MasterDemandLine_ID", selectedDataList.getMasterDeLineID());
				line.save(getTrx().getTrxName());
			}

			try {
				DB.commit(true, getTrx().getTrxName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return mRequisition;
		
	}
	
	private MOrder createPurchaseOrder(SelectedDataList selectedDataList) {
		
		MOrder mOrder=null;
		if(selectedDataList!=null)
		{
			MBPartner vendor=MBPartner.get(Env.getCtx(), selectedDataList.getVendorId());
			MBPartnerLocation primaryLocation=vendor.getPrimaryC_BPartner_Location();
			MProductPO mProductPO=MProductPO.getOfVendorProduct(Env.getCtx(), selectedDataList.getVendorId(), selectedDataList.getProductId(), getTrx()); 
			mOrder=new MOrder(Env.getCtx(), 0, getTrx().getTrxName());
			mOrder.setIsSOTrx(false);		
			mOrder.setC_DocType_ID(selectedDataList.getDocTypeId());
			mOrder.setC_DocTypeTarget_ID(selectedDataList.getDocTypeId());
			mOrder.setC_BPartner_ID(selectedDataList.getVendorId());
			mOrder.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
			mOrder.setSalesRep_ID(Env.getAD_User_ID(Env.getCtx()));
			mOrder.setM_Warehouse_ID(selectedDataList.getWarehouseID());
			//Integer bPartnerLocation=DB.getSQLValue(null, "SELECT C_BPartner_Location_ID from C_BPartner_Location where C_BPartner_ID="+selectedDataList.getVendorId());
			mOrder.setC_BPartner_Location_ID(primaryLocation.getC_BPartner_Location_ID());
			if(vendor.getPO_PriceList_ID()>0)
			mOrder.setM_PriceList_ID(vendor.getPO_PriceList_ID());
			else
			mOrder.setM_PriceList_ID(MPriceList.getDefault(Env.getCtx(), true).getM_PriceList_ID());	
			
			mOrder.setC_PaymentTerm_ID(vendor.getPO_PaymentTerm_ID());
			// MPriceList.getDefault(getCtx(), true);
			
			mOrder.setPriorityRule("5");
			mOrder.setDateOrdered(timestamp);
			
			LocalDateTime dateTime = timestamp.toLocalDateTime().plusDays(mProductPO.getDeliveryTime_Promised());
			
			mOrder.setDatePromised(Timestamp.valueOf(dateTime)); /// Here add to No of days from Product Purchasing Tab
			mOrder.setDescription("By Plan Implementation");

			if(!mOrder.save(getTrx().getTrxName()))
			{
				getTrx().rollback();			 
			}
			
			if(mOrder!=null) // Create Lines
			{
				MOrderLine line=new MOrderLine(mOrder);
				line.setM_Product_ID(selectedDataList.getProductId());
				line.setQtyEntered(selectedDataList.getActualQtyToOrder());
				line.setQtyOrdered(selectedDataList.getActualQtyToOrder());
				line.setC_UOM_ID(selectedDataList.getcUOMID());
				line.set_ValueNoCheck("M_MasterDemandLine_ID", selectedDataList.getMasterDeLineID());
				line.save(getTrx().getTrxName());
			}

			try {
				DB.commit(true, getTrx().getTrxName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return mOrder;
	}
	
}