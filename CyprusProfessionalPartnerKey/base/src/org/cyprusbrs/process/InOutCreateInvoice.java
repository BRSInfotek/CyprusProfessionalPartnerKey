/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.cyprusbrs.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.cyprusbrs.framework.MInOut;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.util.DB;
 
/**
 *	Create (Generate) Invoice from Shipment
 *	
 *  @author Jorg Janke
 *  @version $Id: InOutCreateInvoice.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class InOutCreateInvoice extends SvrProcess
{
	/**	Shipment					*/
	private int 	p_M_InOut_ID = 0;
	/**	Price List Version			*/
	private int		p_M_PriceList_ID = 0;
	/* Document No					*/
	private String	p_InvoiceDocumentNo = null;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_PriceList_ID"))
				p_M_PriceList_ID = para[i].getParameterAsInt();
			else if (name.equals("InvoiceDocumentNo"))
				p_InvoiceDocumentNo = (String)para[i].getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		p_M_InOut_ID = getRecord_ID();
	}	//	prepare

	/**
	 * 	Create Invoice.
	 *	@return document no
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		log.info("M_InOut_ID=" + p_M_InOut_ID 
			+ ", M_PriceList_ID=" + p_M_PriceList_ID
			+ ", InvoiceDocumentNo=" + p_InvoiceDocumentNo);
		if (p_M_InOut_ID == 0)
			throw new IllegalArgumentException("No Shipment");
		//
		MInOut ship = new MInOut (getCtx(), p_M_InOut_ID, null);
		if (ship.get_ID() == 0)
			throw new IllegalArgumentException("Shipment not found");
		if (!MInOut.DOCSTATUS_Completed.equals(ship.getDocStatus()))
			throw new IllegalArgumentException("Shipment not completed");
		
		// **Check if DocStatus is 'Completed' before proceeding**
	    if (!MInOut.DOCSTATUS_Completed.equals(ship.getDocStatus())) {
	        return "Error: Material Receipt must be in 'Completed' status before creating an Invoice.";
	    }
		
		int inOutBPartnerID = ship.getC_BPartner_ID();

	    // **Check if p_InvoiceDocumentNo exists in C_Invoice with the same C_BPartner_ID**
	    if (p_InvoiceDocumentNo != null && p_InvoiceDocumentNo.length() > 0) {
	        String sql = "SELECT C_Invoice_ID FROM C_Invoice " +
	                     "WHERE VendorInvoiceNo = ? AND C_BPartner_ID = ? " +
	                     "AND DocStatus NOT IN ('VO', 'RE')";
	        int existingInvoiceID = DB.getSQLValue(get_TrxName(), sql, p_InvoiceDocumentNo, inOutBPartnerID);

	        if (existingInvoiceID > 0) {
	            return "An Invoice with this Vendor Invoice No (" + p_InvoiceDocumentNo + 
	                   ") already exists for the same Business Partner. Invoice creation is not allowed.";
	        }
	    }
			
		// **Check if an Invoice already exists for this Material Receipt**
	    String existingInvoice = DB.getSQLValueString(
	        get_TrxName(),
	        "SELECT i.DocumentNo FROM C_Invoice i " +
	        "INNER JOIN C_InvoiceLine il ON i.C_Invoice_ID = il.C_Invoice_ID " +
	        "INNER JOIN M_InOutLine iol ON il.M_InOutLine_ID = iol.M_InOutLine_ID " +
	        "WHERE iol.M_InOut_ID = ? AND i.DocStatus NOT IN ('VO', 'RE')",
	        p_M_InOut_ID
	    );

	    if (existingInvoice != null && existingInvoice.length() > 0) {
	        return "An Invoice has already been created from this Material Receipt: " + existingInvoice;
	    }

	    // **Proceed with Invoice creation since no previous invoice exists**
		
		MInvoice invoice = new MInvoice (ship, null);
		// Should not override pricelist for RMA
		if (p_M_PriceList_ID != 0 && ship.getM_RMA_ID() == 0)
			invoice.setM_PriceList_ID(p_M_PriceList_ID);
		if (p_InvoiceDocumentNo != null && p_InvoiceDocumentNo.length() > 0)
		{
			invoice.set_ValueNoCheck("VendorInvoiceNo",p_InvoiceDocumentNo); // As per discussion with Surya @20240621
		}
		//invoice.setDocumentNo(p_InvoiceDocumentNo); 
		if (!invoice.save())
			throw new IllegalArgumentException("Cannot save Invoice");
		MInOutLine[] shipLines = ship.getLines(false);
		for (int i = 0; i < shipLines.length; i++)
		{
			MInOutLine sLine = shipLines[i];
			MInvoiceLine line = new MInvoiceLine(invoice);
			line.setShipLine(sLine);
			if (sLine.sameOrderLineUOM())
				line.setQtyEntered(sLine.getQtyEntered());
			else
				line.setQtyEntered(sLine.getMovementQty());
			line.setQtyInvoiced(sLine.getMovementQty());
			if(sLine.getC_OrderLine_ID() > 0)
			{
				BigDecimal TCSAmt = DB.getSQLValueBD(null, "SELECT TCSAmt FROM C_OrderLine WHERE C_OrderLine_ID=?", sLine.getC_OrderLine_ID());
				line.set_ValueOfColumn("TCSAmt", TCSAmt);
			}	
			if (!line.save())
				throw new IllegalArgumentException("Cannot save Invoice Line");
		}
		
		return invoice.getDocumentNo();
	}	//	InOutCreateInvoice
	
}	//	InOutCreateInvoice
