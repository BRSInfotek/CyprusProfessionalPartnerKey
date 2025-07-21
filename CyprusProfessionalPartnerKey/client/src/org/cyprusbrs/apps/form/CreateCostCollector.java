package org.cyprusbrs.apps.form;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.cyprusbrs.apps.TimeRecordDTO;
import org.cyprusbrs.minigrid.IDColumn;
import org.cyprusbrs.minigrid.IMiniTable;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Trx;
import org.eevolution.model.MMFGRecordingDate;
import org.eevolution.model.MMFGRecordingTime;
import org.eevolution.model.MMFGTimeRecording;
import org.eevolution.model.MPPCostCollector;
import org.eevolution.model.MPPCostCollectorLine;
import org.eevolution.model.MPPOrder;

public class CreateCostCollector {
	

	public static String createCostCollector(Map<String, Object> dataMap, IMiniTable miniTable, List<TimeRecordDTO> timeRecords) 
	{
			String trxName = Trx.createTrxName("CCG");
			Trx trx = Trx.get(trxName, true);
			MPPOrder order = new MPPOrder(Env.getCtx(), (Integer) dataMap.get("Order_ID"), null);
			MPPCostCollector cc = new MPPCostCollector(Env.getCtx(), 0, trxName);
			cc.setAD_Client_ID(order.getAD_Client_ID());
			cc.setAD_Org_ID(order.getAD_Org_ID());
			cc.setPP_Order_ID((Integer) dataMap.get("Order_ID"));
			cc.setC_DocTypeTarget_ID("MCC"); 
			cc.setC_DocType_ID(0); // hard coded need to update later
			cc.setM_Warehouse_ID((Integer) dataMap.get("Warehouse_ID"));
			cc.setM_Locator_ID((Integer) dataMap.get("Locator_ID"));
			cc.setM_Product_ID(order.getM_Product_ID());
			cc.setM_AttributeSetInstance_ID(order.getM_AttributeSetInstance_ID());
			cc.setMovementDate(new Timestamp(System.currentTimeMillis()));
			cc.setDateAcct(new Timestamp(System.currentTimeMillis()));
			cc.setMovementQty((BigDecimal) dataMap.get("Qty"));
			cc.setCostCollectorType((String)dataMap.get("CostCollectorType"));
			if(dataMap.get("CostCollectorType").toString().equalsIgnoreCase("170"))
			{						
				cc.set_ValueOfColumn("StepFrom", (String) dataMap.get("StepFrom"));
				cc.set_ValueOfColumn("StepTo", (String) dataMap.get("StepTo"));
				cc.set_ValueOfColumn("PP_Order_Node_ID", (Integer) dataMap.get("ActivityFrom_ID"));
				cc.set_ValueOfColumn("PP_Order_NodeRef_ID", (Integer) dataMap.get("ActivityTo_ID"));
				cc.set_ValueOfColumn("M_Operation_ID", (Integer) dataMap.get("OperationFrom_ID"));
				cc.set_ValueOfColumn("M_OperationRef_ID", (Integer) dataMap.get("OperationTo_ID"));
			}
			cc.setS_Resource_ID(1000001);  // hard coded need to update later		
		    cc.setDocStatus("DR");
		    if(!cc.save(trxName))
		    {
		    	trx.rollback();
		    	trx.close();
		    	return "Cost Collector could not Saved..";
		    }
		    int lineNo = 10;
		    for (int i = 0; i < miniTable.getRowCount(); i++)
		    {
		    	IDColumn idCol = (IDColumn) miniTable.getValueAt(i, 0);
		    	if(idCol.isSelected())
		    	{
		    		MPPCostCollectorLine ccl = new MPPCostCollectorLine(Env.getCtx(), 0, trxName);
		    		ccl.setAD_Client_ID(cc.getAD_Client_ID());
		    		ccl.setAD_Org_ID(cc.getAD_Org_ID());
		    		ccl.setPP_Cost_Collector_ID(cc.getPP_Cost_Collector_ID());
		    		ccl.setLine(lineNo);
		    		lineNo += 10;
		    		if(dataMap.get("CostCollectorType").toString().equalsIgnoreCase("170"))
					{
		    			ccl.setM_Operation_ID((Integer) dataMap.get("OperationFrom_ID"));
		    			ccl.setQtyEntered((BigDecimal) miniTable.getValueAt(i, 6));
		    			ccl.setM_Product_ID((Integer) miniTable.getValueAt(i, 8));
		    			ccl.setC_UOM_ID((Integer) miniTable.getValueAt(i, 9));
					}
		    		else if(dataMap.get("CostCollectorType").toString().equalsIgnoreCase("180"))
		    		{
		    			ccl.setM_Operation_ID((Integer) dataMap.get("Operation_ID"));
		    			ccl.setQtyEntered((BigDecimal) miniTable.getValueAt(i, 5));
		    			ccl.setM_Product_ID((Integer) miniTable.getValueAt(i, 6));
		    			ccl.setC_UOM_ID((Integer) miniTable.getValueAt(i, 7));		    			
		    		}
		    		else 
		    		{
		    			ccl.setM_Operation_ID((Integer) dataMap.get("Operation_ID"));
		    			ccl.setQtyEntered((BigDecimal) miniTable.getValueAt(i, 6));
		    			ccl.setM_Product_ID((Integer) miniTable.getValueAt(i, 9));
		    			ccl.setC_UOM_ID((Integer) miniTable.getValueAt(i, 10));	
		    		}
		    		ccl.setM_Locator_ID((Integer) dataMap.get("Locator_ID"));
		    		if(!ccl.save(trxName))
		    		{
		    			trx.rollback();
		    			trx.close();
				    	return "Cost Collector Line could not Saved..";
		    		}		    		
		    	}
		    }
		    
		    try {
				DB.commit(true,trxName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    trx.close();
		    
		    if(dataMap.get("CostCollectorType").toString().equalsIgnoreCase("170"))
		    {
		    	String trxNameTRG = Trx.createTrxName("TRG");
				Trx trxTRG = Trx.get(trxName, true);
				int C_DocType_ID = DB.getSQLValue(null, "SELECT C_DocType_ID FROM C_Doctype WHERE DocBaseType='TRT'");
				int C_UOM_ID = DB.getSQLValue(null, "SELECT C_UOM_ID FROM M_Product WHERE M_Product_ID=" + order.getM_Product_ID());
				MMFGTimeRecording TR = new MMFGTimeRecording(Env.getCtx(), 0, trxNameTRG);
				TR.setAD_Client_ID(order.getAD_Client_ID());
				TR.setAD_Org_ID(order.getAD_Org_ID());
				TR.setC_DocType_ID(C_DocType_ID);
				TR.setDateTrx(new Timestamp(System.currentTimeMillis()));
				TR.setPP_Order_ID(order.getPP_Order_ID());
//				TR.setPP_Order_Workflow_ID(C_DocType_ID);
				TR.setM_Product_ID(order.getM_Product_ID());
				TR.setM_AttributeSetInstance_ID(order.getM_AttributeSetInstance_ID());
				TR.setQty((BigDecimal) dataMap.get("Qty"));
				TR.setC_UOM_ID(C_UOM_ID);
				TR.setM_Warehouse_ID((Integer) dataMap.get("Warehouse_ID"));
				TR.setM_Locator_ID((Integer) dataMap.get("Locator_ID"));
				TR.setProcessed(true);
				if(!TR.save(trxNameTRG))
			    {
					trxTRG.rollback();
					trxTRG.close();
			    	return "Time Recording could not Saved..";
			    }
				MMFGRecordingDate RD = new MMFGRecordingDate(Env.getCtx(), 0, trxNameTRG);
				RD.setAD_Client_ID(TR.getAD_Client_ID());
				RD.setAD_Org_ID(TR.getAD_Org_ID());
				RD.setMFG_TimeRecording_ID(TR.getMFG_TimeRecording_ID());
				RD.setPP_Order_Node_ID( (Integer) dataMap.get("ActivityFrom_ID"));
				RD.setM_Operation_ID( (Integer) dataMap.get("OperationFrom_ID"));
				RD.setDateRecording(new Timestamp(System.currentTimeMillis()));
				RD.setMFG_Shift("S1");
				RD.setLine(10);
				RD.setProcessed(true);
				if(!RD.save(trxNameTRG))
			    {
					trxTRG.rollback();
					trxTRG.close();
			    	return "Recording Date could not Saved..";
//					throw new CyprusException("Recording Date could not Saved..");
			    }
				int lineRT = 10;
				for (TimeRecordDTO record : timeRecords) {
					 MMFGRecordingTime RT = new MMFGRecordingTime(Env.getCtx(), 0, trxNameTRG);
					 RT.setAD_Client_ID(RD.getAD_Client_ID());
					 RT.setAD_Org_ID(RD.getAD_Org_ID());
					 RT.setMFG_RecordingDate_ID(RD.getMFG_RecordingDate_ID());
					 RT.setLine(lineRT);
					 lineRT += 10;
					 RT.setC_Activity_ID(record.getActivityId());
					 RT.setC_UOM_ID(record.getUOMID());
					 RT.setMFG_RecordingTime((Timestamp)record.getTime());
					 RT.setProcessed(true);
					 if(!RT.save(trxNameTRG))
			    		{
						 	trxTRG.rollback();
						 	trxTRG.close();
					    	return "Recording Time could not Saved..";
			    		}
				 }
				  try {
						DB.commit(true,trxNameTRG);
					} catch (Exception e) {
						e.printStackTrace();
					}
				  trxTRG.close();
		    }
		    
		return "Cost Collector Created : " + cc.getDocumentNo();
	}

}


