package org.cyprusbrs.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

public class GenerateInvoiceFromSO extends SvrProcess {

    private Integer recordId = 0;
    private MInvoice m_Invoice = null;
    private Timestamp m_movementDate = null;

    @Override
    protected void prepare() {
        recordId = getRecord_ID();
        m_movementDate = Env.getContextAsDate(getCtx(), "#Date");
        if (m_movementDate == null) {
            m_movementDate = new Timestamp(System.currentTimeMillis());
        }
    }

    @Override
    protected String doIt() throws Exception {
        if (recordId <= 0) {
            return Msg.getMsg(getCtx(), "InvalidRecordID");
        }

        MOrder order = new MOrder(getCtx(), recordId, get_TrxName());
        if (order.get_ID() == 0) {
            return Msg.getMsg(getCtx(), "OrderNotFound");
        }

        // Check Order Status - Must be Completed
        if (!order.isComplete()) {
            return Msg.getMsg(getCtx(), "OrderNotCompleted") + " (" + order.getDocStatus() + ")";
        }

        // Check for existing Drafted invoices
        List<MInvoice> existingInvoices = new Query(getCtx(), MInvoice.Table_Name,
                "C_Order_ID = ? AND DocStatus = 'DR' AND AD_Client_ID = ?",
                get_TrxName())
                .setParameters(recordId, getAD_Client_ID())
                .list();

        if (existingInvoices != null && !existingInvoices.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append(Msg.getMsg(getCtx(), "DraftInvoiceExists")).append(": ");
            for (MInvoice inv : existingInvoices) {
                errorMsg.append(inv.getDocumentNo()).append(", ");
            }
            errorMsg.setLength(errorMsg.length() - 2); // Remove trailing comma
            errorMsg.append(". ").append(Msg.getMsg(getCtx(), "CompleteExistingFirst"));
            return errorMsg.toString();
        }

        /**
        // Validate Manual Order (SO Subtype = 'MO')
        // MDocType orderDocType = MDocType.get(getCtx(), order.getC_DocTypeTarget_ID());
        if (true)//"MO".equals(orderDocType.getDocSubTypeSO())) 
        {
            int existingInvoiceCount = new Query(getCtx(), MInvoice.Table_Name,
                    "C_Order_ID = ? AND DocStatus IN ('CO','CL','DR') AND AD_Client_ID = ?",
                    get_TrxName())
                    .setParameters(recordId, getAD_Client_ID())
                    .count();
            if (existingInvoiceCount > 0) {
                return Msg.getMsg(getCtx(), "InvoiceExistsForManualOrder");
            }
        }
        **/
        
     /**   /// It will check fully Invoiced the it will not allowed to create others..
     // Check if any invoices already exist for this order (COmpleted, CLosed, or DRaft)
        List<MInvoice> existingInvoicesDoc = new Query(getCtx(), MInvoice.Table_Name,
                "C_Order_ID = ? AND DocStatus IN ('CO','CL','DR') AND AD_Client_ID = ?",
                get_TrxName())
                .setParameters(recordId, getAD_Client_ID())
                .list();

        if (!existingInvoicesDoc.isEmpty()) {
            // Collect all invoice document numbers
            StringBuilder invoiceNos = new StringBuilder();
            for (MInvoice inv : existingInvoicesDoc) {
                if (invoiceNos.length() > 0) {
                    invoiceNos.append(", ");
                }
                invoiceNos.append(inv.getDocumentNo());
            }    
            // Return error message with invoice numbers
            return Msg.getMsg(getCtx(), "InvoiceExistsForOrderWithNumbers", 
                    new Object[] { invoiceNos.toString() });
        }
        **/
        
        MOrderLine[] orderLines = order.getLines(true, MOrderLine.COLUMNNAME_Line);
        log.log(Level.INFO, "Found {0} order lines", orderLines.length);

        if (orderLines.length == 0) {
            return Msg.getMsg(getCtx(), "NoOrderLinesFound");
        }

        // ✅ Check if all lines are fully invoiced
        boolean hasQtyToInvoice = false;
        for (MOrderLine line : orderLines) {
            if (line.getQtyOrdered().compareTo(line.getQtyInvoiced()) > 0) {
                hasQtyToInvoice = true;
                break;
            }
        }
        if (!hasQtyToInvoice) {
            return Msg.getMsg(getCtx(), "OrderFullyInvoiced");
        }

        for (MOrderLine line : orderLines) {
            if (line.getQtyEntered().compareTo(line.getQtyInvoiced()) > 0) {
                createInvoiceLine(order, line);
            } else {
                log.log(Level.INFO, "Skipping fully invoiced line: {0}", line.getLine());
            }
        }

        if (m_Invoice != null && m_Invoice.save()) {
            return Msg.getMsg(getCtx(), "InvoiceCreated") + " " + m_Invoice.getDocumentNo();
        }
        return Msg.getMsg(getCtx(), "InvoiceNotCreated");
    }

    private void createInvoiceLine(MOrder order, MOrderLine orderLine) {
        try {
            // Create invoice header if not exists
            if (m_Invoice == null) {
                MDocType docType = MDocType.get(getCtx(), order.getC_DocTypeTarget_ID());
                Integer docTypeId = docType.getC_DocTypeInvoice_ID();

                if (docTypeId == null || docTypeId <= 0) {
                    throw new IllegalArgumentException("Invalid Invoice Document Type");
                }

                m_Invoice = new MInvoice(order, docTypeId, m_movementDate);
                m_Invoice.setM_PriceList_ID(order.getM_PriceList_ID());

                // Set partner info if different
                if (order.getC_BPartner_ID() != orderLine.getC_BPartner_ID()) {
                    m_Invoice.setC_BPartner_ID(orderLine.getC_BPartner_ID());
                }
                if (order.getC_BPartner_Location_ID() != orderLine.getC_BPartner_Location_ID()) {
                    m_Invoice.setC_BPartner_Location_ID(orderLine.getC_BPartner_Location_ID());
                }

                if (!m_Invoice.save()) {
                    throw new IllegalStateException("Could not save Invoice");
                }
            }

            // Create invoice line
            BigDecimal toInvoice = orderLine.getQtyOrdered().subtract(orderLine.getQtyInvoiced());
            BigDecimal qtyEntered = orderLine.getQtyEntered().subtract(orderLine.getQtyInvoiced());
            if(toInvoice.compareTo(Env.ZERO)>0)
            {
            	 MInvoiceLine line = new MInvoiceLine(m_Invoice);
                 line.setOrderLine(orderLine);
                 line.setQtyEntered(qtyEntered);
                 line.setQtyInvoiced(toInvoice);
                 line.setPriceList(orderLine.getPriceList());
				 line.set_ValueOfColumn("TCSAmt", orderLine.get_Value("TCSAmt"));
                 if (!line.save()) {
                     throw new IllegalStateException("Could not save Invoice Line");
                 }
            }
           
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error creating invoice line: " + orderLine.getLine(), e);
            throw e;
        }
    }
}
