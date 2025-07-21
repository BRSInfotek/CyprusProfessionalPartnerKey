package org.cyprusbrs.process;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class GenerateShipMRFromOrder extends SvrProcess{

	
	
	Integer recordId=0;
	/**	The current Shipment	*/
	private MInOut 		m_shipmentMR = null;
	/** Movement Date			*/
	private Timestamp	m_movementDate = null;
	
	@Override
	protected void prepare() {
		
		recordId=getRecord_ID();
		
		m_movementDate = Env.getContextAsDate(getCtx(), "#Date");
		if (m_movementDate == null)
			m_movementDate = new Timestamp(System.currentTimeMillis());
	}

	@Override
	protected String doIt() throws Exception {
		
		
		// Check if Purchase Order has a 'Completed' (CO) status before proceeding
	    String orderStatus = DB.getSQLValueString(
	        get_TrxName(),
	        "SELECT DocStatus FROM C_Order WHERE C_Order_ID = ?",
	        recordId
	    );

	    if (!"CO".equals(orderStatus)) {
	        return "Purchase Order must be completed before generating Material Receipt.";
	    }
		
		// Checks before creating another GRN
		String checks=checksBeforeCreateReceipt();
		if(checks!=null)
			return checks;
		MOrder order=new MOrder(getCtx(), recordId, get_TrxName());
		
		MOrderLine[] oLines=order.getLines(true, MOrderLine.COLUMNNAME_Line);
		
		log.info("No of order line "+oLines.length);
		
		if(oLines.length>0)
		{
			for(MOrderLine line:oLines)
			{
				createLine(order,line);
			}
		}
		if(m_shipmentMR!=null)
		return "Created Shipment/MR "+m_shipmentMR.getDocumentNo();
		else
		return "Not Created Shipment/MR ";
	}

	/**
	 * Checks before creating the MR from Purchase Order
	 * @return
	 */
	
	private String checksBeforeCreateReceipt() {
	    String documentNo = null;
	    String sql = null;
	    
	    // Check if there is an existing incomplete GRN
	    sql = "SELECT mi.documentNo FROM C_Order co "
	        + "INNER JOIN C_DocType cd ON (cd.C_DocType_ID = co.C_DocType_ID) "
	        + "INNER JOIN M_InOut mi ON (mi.C_Order_ID = co.C_Order_ID) "
	        + "WHERE co.issotrx = 'N' AND mi.docstatus IN ('DR', 'PR') "
	        + "AND co.C_Order_ID = ?";
	    documentNo = DB.getSQLValueString(get_TrxName(), sql, recordId);
	    
	    if (documentNo != null && documentNo.length() > 0)
	        return "GRN already exists without completion: " + documentNo;
	    
	    // Check if all ordered quantity has been delivered
	    sql = "SELECT co.documentno FROM C_Order co "
	        + "INNER JOIN c_orderline col ON (co.c_order_id = col.c_order_id) "
	        + "WHERE co.C_Order_ID = ? "
	        + "GROUP BY co.documentno "
	        + "HAVING SUM(col.qtydelivered) >= SUM(col.qtyordered)";
	    
	    documentNo = DB.getSQLValueString(get_TrxName(), sql, recordId);
	    
	    if (documentNo != null && documentNo.length() > 0)
	        return "All quantity has been delivered for this order: " + documentNo;
	    
	    // Check remaining quantity for each order line
	    sql = "SELECT SUM(col.qtyordered - col.qtydelivered) "
	        + "FROM c_orderline col "
	        + "WHERE col.c_order_id = ?";
	    BigDecimal remainingQty = DB.getSQLValueBD(get_TrxName(), sql, recordId);
	    
	    if (remainingQty == null || remainingQty.compareTo(BigDecimal.ZERO) <= 0)
	        return "No remaining quantity available for Material Receipt.";
	    
	    return null;
	}

	
//	private String checksBeforeCreateReceipt() {
//		String documentNo=null;
//		String sql=null;
//		sql="SELECT mi.documentNo FROM C_Order co "
//				+ "INNER JOIN C_DocType cd ON ( cd.C_DocType_ID=co.C_DocType_ID) "
//				+ "INNER JOIN M_InOut mi ON (mi.C_Order_ID=co.C_Order_ID) "
//				+ "WHERE co.issotrx='N' AND mi.docstatus IN ('DR','PR') "
//				+ "AND co.C_Order_ID= ?";
//		documentNo=DB.getSQLValueString(get_TrxName(), sql, recordId);
//		if(documentNo!=null && documentNo.length()>0)
//			return "GRN already exist without complete : "+documentNo;
//		
//		sql="SELECT co.documentno FROM C_Order co "				
//				+ " INNER JOIN c_orderline col ON (co.c_order_id=col.c_order_id) "
//				+ " WHERE co.C_Order_ID=? "
//				+ " GROUP BY co.documentno "
//				+ " HAVING SUM (col.qtydelivered)=SUM(col.qtyordered)";
//		
////		sql="SELECT co.documentNo FROM C_Order co "
////				+ " INNER JOIN c_orderline col ON (co.c_order_id=col.c_order_id) "
////				+ " WHERE qtydelivered >=qtyordered AND co.C_Order_ID=?";
//		System.out.println("Query :: "+sql);
//		 documentNo=DB.getSQLValueString(get_TrxName(), sql, recordId);
//		if(documentNo!=null && documentNo.length()>0)
//			return "All quantity has been delivered for this order : "+documentNo;
//		
//		return documentNo;
//	}

	/**
	 * Create Shipment/MR header and lines
	 * @param order
	 * @param line
	 */
	private void createLine(MOrder order, MOrderLine orderLine) {
		
		if (m_shipmentMR == null)
		{
			Integer docType=0;
			if(order.isSOTrx())
				docType=MDocType.getDocType(MDocType.DOCBASETYPE_MaterialDelivery);
			else
				docType=MDocType.getDocType(MDocType.DOCBASETYPE_MaterialReceipt);	
			
			log.info(" Doc Type Qty :::: "+docType);
			
			m_shipmentMR = new MInOut (order, docType, m_movementDate);
			m_shipmentMR.setM_Warehouse_ID(orderLine.getM_Warehouse_ID());	//	sets Org too
			if (order.getC_BPartner_ID() != orderLine.getC_BPartner_ID())
				m_shipmentMR.setC_BPartner_ID(orderLine.getC_BPartner_ID());
			if (order.getC_BPartner_Location_ID() != orderLine.getC_BPartner_Location_ID())
				m_shipmentMR.setC_BPartner_Location_ID(orderLine.getC_BPartner_Location_ID());
			if (!m_shipmentMR.save())
				throw new IllegalStateException("Could not create Shipment");
		}
		MInOutLine line = new MInOutLine (m_shipmentMR);
		
		BigDecimal 	toDeliver=orderLine.getQtyEntered().subtract(orderLine.getQtyDelivered());
		log.info("Delivery Qty "+toDeliver);
		line.setOrderLine(orderLine, 0, order.isSOTrx() ? toDeliver : Env.ZERO);
		line.setQty(toDeliver);
		line.setMovementQty(orderLine.getQtyOrdered());
		if (!line.save())
		 throw new IllegalStateException("Could not create Shipment Line");
		
	}

}
