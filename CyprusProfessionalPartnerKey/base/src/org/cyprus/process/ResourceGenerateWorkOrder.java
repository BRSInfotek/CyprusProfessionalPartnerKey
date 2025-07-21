

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

import org.cyprus.mfg.model.MMFGWorkCenter;
import org.cyprus.mfg.model.MMFGWorkCenterResource;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprus.mfg.model.MMFGWorkOrderResourceTxnLine;
import org.cyprus.mfg.model.MMFGWorkOrderTransaction;
import org.cyprus.mfg.model.X_MFG_WorkOrderResource;
import org.cyprus.mfg.model.X_MFG_WorkOrderTransaction;
import org.cyprus.mfg.util.MMFGWorkOrderTxnUtil;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

/**
 * Generate invoice for Vendor RMA
 * @author  Ashley Ramdass
 * 
 * Based on org.cyprusbrs.process.InvoiceGenerate
 */
public class ResourceGenerateWorkOrder extends SvrProcess
{
    /** Manual Selection        */
    private boolean     p_Selection = false;
   
   
    private int         m_created = 0;
 

    /**	Logger			*/
	private static CLogger s_log = CLogger.getCLogger(ResourceGenerateWorkOrder.class);
    
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
            
            else
                log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        
    }

    protected String doIt() throws Exception
    {
        if (!p_Selection)
        {
            throw new IllegalStateException("Resources can only be generated from selection");
        }
        
//        String sql = "SELECT wo.MFG_WorkOrder_ID FROM MFG_WorkOrder wo, T_Selection "
//            + "WHERE wo.DocStatus='CO' AND wo.AD_Client_ID=? "
//            + "AND wo.MFG_WorkOrder_ID = T_Selection.T_Selection_ID " 
//            + "AND T_Selection.AD_PInstance_ID=? ";
        
        String sql = "SELECT wo.MFG_WorkOrder_ID FROM MFG_WorkOrder wo, T_Selection "
                + "WHERE wo.AD_Client_ID=? "
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
            	System.out.println(rs.getInt(1));
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
    
    private MMFGWorkOrderTransaction  createResource(MMFGWorkOrder wo)
    {
        //-------------------------------
        String sql = "select MFG_WorkOrderOperation_ID from MFG_WorkOrderOperation  where mfg_workorder_id =" + wo.getMFG_WorkOrder_ID();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	MMFGWorkOrderTransaction woTxn = null;
        try {
        pstmt = DB.prepareStatement(sql, get_TrxName());
		 rs = pstmt.executeQuery();
		
		while (rs.next())
		{
			int MFG_WorkOrderOperation_ID = rs.getInt(1);
			MMFGWorkOrderOperation wop = new MMFGWorkOrderOperation(Env.getCtx(), MFG_WorkOrderOperation_ID, get_TrxName());
			   int M_WorkCenter_ID = wop.getMFG_WorkCenter_ID();
		        System.out.println(M_WorkCenter_ID);
		        MMFGWorkCenter wc = new MMFGWorkCenter(Env.getCtx(), M_WorkCenter_ID, get_TrxName());
		        MMFGWorkCenterResource[] wcrs = MMFGWorkCenterResource.getResources(wc, " AND IsActive = 'Y' ", "");
		        if (wcrs == null || wcrs.length == 0) {
//		          change.addError("Resource not assigned to work center");
		          s_log.severe("Resource not assigned to work center");
//		          error++;
//		          trx.rollback();
		        } 
//		       
		        
		            MMFGWorkOrderTxnUtil txnUtil = new MMFGWorkOrderTxnUtil(false);
		            if(woTxn==null) {
		             woTxn = txnUtil.createWOTxn(Env.getCtx(), wo.getMFG_WorkOrder_ID(), X_MFG_WorkOrderTransaction.WORKORDERTXNTYPE_ResourceUsage, get_TrxName());
		             System.out.println(woTxn);
		             if (!woTxn.save(get_TrxName())) {
		            	 System.out.println(woTxn);
		            	  s_log.severe("Error in saving work order transaction header");
		             }
		            }
		             
		    
		            	 MMFGWorkOrderResourceTxnLine resourceTxnLine = null;
		              //MMFGWorkOrderResourceTxnLine resourceTxnLine = null;
		            	System.out.println("createResource5");
		              resourceTxnLine = new MMFGWorkOrderResourceTxnLine(Env.getCtx(), 0, get_TrxName());
		              resourceTxnLine.setQtyEntered(wo.getQtyEntered());
		              resourceTxnLine.setMFG_WORKORDERTRANSACTION_ID(woTxn.getMFG_WORKORDERTRANSACTION_ID());
		              resourceTxnLine.setClientOrg((PO)woTxn);
//		              resourceTxnLine.setM_Product_ID(resource_id);
		              resourceTxnLine.setM_Product_ID(wo.getM_Product_ID());
		              resourceTxnLine.setBasisType(X_MFG_WorkOrderResource.BASISTYPE_PerItem);
		              resourceTxnLine.setC_UOM_ID(wo.getC_UOM_ID());
		              resourceTxnLine.setMFG_WorkOrderOperation_ID(wop.getMFG_WorkOrderOperation_ID());
		              resourceTxnLine.setSeqNo(10);
		              resourceTxnLine.setIsActive(true);
//		              if (!resourceTxnLine.save()) {
////		                change.addError("Error in saving work order transaction line");
////		                s_log.severe("Error in saving work order transaction line");
////		                error++;
////		                trx.rollback();
//		              } else if (DocumentEngine.processIt((DocAction)woTxn, "CO")) {
//		                woTxn.save();
////		                trx.commit();
////		                success++;
//		              } else {
//		                s_log.severe("Could not complete work order transaction");
////		                error++;
////		                trx.rollback();
//		              } 
		             
		//            }
		       // invoice.setC_DocTypeTarget_ID(docTypeId);
		        if (!resourceTxnLine.save(get_TrxName()))
		        {
		            throw new IllegalStateException("Could not create resource");
		        }
		}
		rs.close();
		pstmt.close();
		pstmt = null;
        }
        catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
        
        finally
        {
        	DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }
  
        
        return woTxn;
    }
    

    
    private void generateResource(int MFG_WORKORDER_ID)
    {
    	System.out.println("generateResource");
        MMFGWorkOrder wo = new MMFGWorkOrder(getCtx(), MFG_WORKORDER_ID, get_TrxName());
        
        MMFGWorkOrderTransaction  resource = createResource(wo);
        System.out.println(resource.getDocumentNo());
       
        
      
        
        // Add processing information to process log
      //  addLog(resource.getC_Invoice_ID(), invoice.getDateInvoiced(), null, processMsg.toString());
        m_created++;
    }
}

