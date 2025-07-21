



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

package org.cyprus.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprus.mfg.model.MMFGWorkOrderResource;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

/**
 * Generate invoice for Vendor RMA
 * @author  Ashley Ramdass
 * 
 * Based on org.cyprusbrs.process.InvoiceGenerate
 */
public class WorkOrderOperationGenerate extends SvrProcess
{
    /** Manual Selection        */
    private boolean     p_Selection = false;
    /** Invoice Document Action */
    private String      p_docAction = DocAction.ACTION_Complete;
    
    /** Number of Invoices      */
    private int         m_created = 0;
    /** Invoice Date            */
//    private Timestamp   m_dateinvoiced = null;

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
            else if (name.equals("Selection"))
                p_Selection = "Y".equals(para[i].getParameter());
            else if (name.equals("DocAction"))
                p_docAction = (String)para[i].getParameter();
            else
                log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        
//        m_dateinvoiced = Env.getContextAsDate(getCtx(), "#Date");
//        if (m_dateinvoiced == null)
//        {
//            m_dateinvoiced = new Timestamp(System.currentTimeMillis());
//        }
    }

    protected String doIt() throws Exception
    {
        if (!p_Selection)
        {
            throw new IllegalStateException("Operation can only be generated from selection");
        }
        
        String sql = "SELECT wo.MFG_WorkOrder_ID FROM MFG_WorkOrder wo, T_Selection "
            + "WHERE wo.DocStatus='CO' AND wo.AD_Client_ID=? "
            + "AND wo.MFG_WorkOrder_ID = T_Selection.T_Selection_ID " 
            + "AND T_Selection.AD_PInstance_ID=? ";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setInt(1, Env.getAD_Client_ID(getCtx()));
            pstmt.setInt(2, getAD_PInstance_ID());
             rs = pstmt.executeQuery();
            
            while (rs.next())
            {
                generateResource(rs.getInt(1));
            }
        }
        catch (Exception ex)
        {
            log.log(Level.SEVERE, sql, ex);
        }
        finally
        {
            try
            {
                pstmt.close();
                rs.close();
                rs = null; pstmt = null;
            }
            catch (Exception ex)
            {
                log.log(Level.SEVERE, "Could not close prepared statement");
            }
        }
        return "@Created@ = " + m_created;
    }
    
//    private int getDocTypeId(int M_RMA_ID)
//    {
//        String docTypeSQl = "SELECT dt.C_DocTypeInvoice_ID FROM C_DocType dt "
//            + "INNER JOIN M_RMA rma ON dt.C_DocType_ID=rma.C_DocType_ID "
//            + "WHERE rma.M_RMA_ID=?";
//        
//        int docTypeId = DB.getSQLValue(null, docTypeSQl, M_RMA_ID);
//        
//        return docTypeId;
//    }
    
    private MMFGWorkOrderResource  createResource(MMFGWorkOrder wo)
    {
      //  int docTypeId = getInvoiceDocTypeId(wo.get_ID());
            
//        if (docTypeId == -1)
//        {
//            throw new IllegalStateException("Could not get invoice document type for Vendor RMA");
//        }
        
    	MMFGWorkOrderOperation operation = new MMFGWorkOrderOperation(getCtx(), wo.getMFG_WorkOrder_ID(), get_TrxName()); 
    	MMFGWorkOrderResource resource = new MMFGWorkOrderResource(getCtx(), 0, get_TrxName());
    	resource.setMFG_WorkOrderOperation_ID(operation.getMFG_WorkOrderOperation_ID());
        resource.setM_Product_ID(wo.getM_Product_ID());
       // invoice.setC_DocTypeTarget_ID(docTypeId);
        if (!resource.save(get_TrxName()))
        {
            throw new IllegalStateException("Could not create operation");
        }
        
        return resource;
    }
    
//    private MInvoiceLine[] createInvoiceLines(MRMA rma, MInvoice invoice)
//    {
//        ArrayList<MInvoiceLine> invLineList = new ArrayList<MInvoiceLine>();
//        
//        MRMALine rmaLines[] = rma.getLines(true);
//        
//        for (MRMALine rmaLine : rmaLines)
//        {
//            if (rmaLine.getM_InOutLine_ID() == 0)
//            {
//                throw new IllegalStateException("No customer return line - RMA = " 
//                        + rma.getDocumentNo() + ", Line = " + rmaLine.getLine());
//            }
//            
//            MInvoiceLine invLine = new MInvoiceLine(invoice);
//            invLine.setRMALine(rmaLine);
//            
//            if (!invLine.save())
//            {
//                throw new IllegalStateException("Could not create invoice line");
//            }
//            
//            invLineList.add(invLine);
//        }
//        
//        MInvoiceLine invLines[] = new MInvoiceLine[invLineList.size()];
//        invLineList.toArray(invLines);
//        
//        return invLines;
//    }
    
    
    private void generateResource(int MFG_WORKORDER_ID)
    {
        MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), MFG_WORKORDER_ID, get_TrxName());
        
        MMFGWorkOrderResource  resource = createResource(wo);
       
        
        if (!resource.save())
        {
            throw new IllegalStateException("Could not update Operation");
        }
        
        // Add processing information to process log
      //  addLog(resource.getC_Invoice_ID(), invoice.getDateInvoiced(), null, processMsg.toString());
        m_created++;
    }
}


