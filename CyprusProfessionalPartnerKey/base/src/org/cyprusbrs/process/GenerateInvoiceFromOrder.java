package org.cyprusbrs.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class GenerateInvoiceFromOrder extends SvrProcess {

	Integer recordId=0;
	/**	The current Shipment	*/
	private MInvoice 		m_Invoice = null;
	/** Movement Date			*/
	private Timestamp	m_movementDate = null;
	
	/** The date to calculate the days due from			*/
	private Timestamp	p_DateInvoiced = null;
	
	private String vendorInvoiceNumber=null;
	@Override
	protected void prepare() {
		
		recordId=getRecord_ID();
		
		m_movementDate = Env.getContextAsDate(getCtx(), "#Date");
		if (m_movementDate == null)
			m_movementDate = new Timestamp(System.currentTimeMillis());
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("DocumentNo"))
				vendorInvoiceNumber = para[i].getParameter().toString();
			else if (name.equals("DateInvoiced"))
				p_DateInvoiced = (Timestamp)para[i].getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		
		// Checks before creating another GRN
		String checks=checksBeforeCreateInvoice();
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
		if(m_Invoice!=null)
		return "Created Invoice "+m_Invoice.getDocumentNo();
		else
		return "Not Created Invoice ";
	}

	/**
	 * Checks before create the invoice
	 * @return
	 */
	private String checksBeforeCreateInvoice() {
		String documentNo=null;
		String sql=null;
		
		sql="SELECT co.documentno FROM C_Order co "				
				+ " INNER JOIN c_orderline col ON (co.c_order_id=col.c_order_id) "
				+ " WHERE co.C_Order_ID=? "
				+ " GROUP BY co.documentno "
				+ " HAVING SUM (col.qtydelivered)!=SUM(col.qtyordered)";
		 documentNo=DB.getSQLValueString(get_TrxName(), sql, recordId);
		if(documentNo!=null && documentNo.length()>0)
			return "GRN has not been Completed, Kindly Complete the GRN for this order: "+documentNo;
		
		sql="SELECT ci.documentNo FROM C_Order co "
				+ "INNER JOIN C_DocType cd ON ( cd.C_DocType_ID=co.C_DocType_ID) "
				+ "INNER JOIN C_Invoice ci ON (ci.C_Order_ID=co.C_Order_ID) "
				+ "WHERE co.issotrx='N' AND ci.docstatus IN ('DR','PR') "
				+ "AND co.C_Order_ID= ?";
		documentNo=DB.getSQLValueString(get_TrxName(), sql, recordId);		
		if(documentNo!=null && documentNo.length()>0)
			return "Invoice already exist without complete : "+documentNo;
		
		sql="SELECT co.documentno FROM C_Order co "				
				+ " INNER JOIN c_orderline col ON (co.c_order_id=col.c_order_id) "
				+ " WHERE co.C_Order_ID=? "
				+ " GROUP BY co.documentno "
				+ " HAVING SUM (col.qtyinvoiced)=SUM(col.qtyordered)";
		System.out.println("Query :: "+sql);
		 documentNo=DB.getSQLValueString(get_TrxName(), sql, recordId);
		if(documentNo!=null && documentNo.length()>0)
			return "All quantity has been invoiced for this order : "+documentNo;
		
		return null;
	}
	
	/**
	 * Created Header and lines of invoice
	 * @param order
	 * @param line
	 */
	private void createLine(MOrder order, MOrderLine orderLine) {

		if (m_Invoice == null)
		{
			Integer docType=0;
			if(order.isSOTrx())
				docType=MDocType.getDocType(MDocType.DOCBASETYPE_ARInvoice);
			else
				docType=MDocType.getDocType(MDocType.DOCBASETYPE_APInvoice);	
			log.info(" Doc Type Qty :::: "+docType);			
			m_Invoice = new MInvoice (order, docType, m_movementDate);
			m_Invoice.setM_PriceList_ID(order.getM_PriceList_ID());
			if (order.getC_BPartner_ID() != orderLine.getC_BPartner_ID())
				m_Invoice.setC_BPartner_ID(orderLine.getC_BPartner_ID());
			if (order.getC_BPartner_Location_ID() != orderLine.getC_BPartner_Location_ID())
				m_Invoice.setC_BPartner_Location_ID(orderLine.getC_BPartner_Location_ID());
			
			// Updated the code as discussed with Surya
			/**
			 * When click on the 'Generate Invoice to Vendor'  button the system will ask the couple of mandatory parameter for generating the AP Invoice as follows:
				1. Vendor Invoice Number
				2. Invoice Date
			 *@author Mukesh
			 *@Date 20230719
			 */
			if(vendorInvoiceNumber!=null && vendorInvoiceNumber.length()>0)
				m_Invoice.setDocumentNo(vendorInvoiceNumber);
			if(p_DateInvoiced!=null)
				m_Invoice.setDateInvoiced(p_DateInvoiced);
			//End of the code
			
			if (!m_Invoice.save())
				throw new IllegalStateException("Could not create Shipment");
		}
		MInvoiceLine line = new MInvoiceLine (m_Invoice);
		BigDecimal 	toDeliver=orderLine.getQtyEntered().subtract(orderLine.getQtyInvoiced());
		log.info("Delivery Qty "+toDeliver);
		line.setOrderLine(orderLine);
		line.setQty(toDeliver);
		if (!line.save())
			throw new IllegalStateException("Could not create Shipment Line");
	}
}
