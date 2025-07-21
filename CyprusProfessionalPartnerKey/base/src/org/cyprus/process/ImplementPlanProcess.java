



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

import org.cyprus.mrp.model.MMRPInventoryAudit;
import org.cyprus.mrp.model.MMRPMasterDemand;
import org.cyprus.mrp.model.MMRPMasterDemandLine;
import org.cyprus.mrp.model.MMRPPlan;
import org.cyprus.mrp.model.MMRPPlanRun;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.framework.MOrderLine;
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
public class ImplementPlanProcess extends SvrProcess
{
    /** Manual Selection        */
    private boolean     p_Selection = false;
   
   
    private int         m_created = 0;
 

  
    
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
            throw new IllegalStateException("Process Can Only be Processed from selection");
        }
        
        
        String sql = "SELECT plan.MRP_Plan_ID FROM MRP_Plan plan, T_Selection "
                + "WHERE plan.AD_Client_ID=? "
                + "AND plan.MRP_Plan_ID = T_Selection.T_Selection_ID " 
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
            	createOrder(rs.getInt(1));
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
    

    
    private String createOrder(int MRP_Plan_ID)
    {
        //-------------------------------
    	MMRPPlan plan = new MMRPPlan(getCtx(), MRP_Plan_ID, get_TrxName());
    	MOrder order = new MOrder(getCtx(), 0, get_TrxName());

		        String sqlrun = "SELECT MRP_PlanRun_ID FROM MRP_PlanRun WHERE MRP_Plan_ID=?";
		        int MRP_PlanRun_ID = DB.getSQLValue (null, sqlrun, plan.getMRP_Plan_ID());
		        String sqlaudit = "SELECT MRP_Inventory_Audit_ID FROM MRP_Inventory_Audit WHERE MRP_PlanRun_ID=?";
		        int MRP_Inventory_Audit_ID = DB.getSQLValue (null, sqlaudit, MRP_PlanRun_ID);
		       
		        
		        MMRPPlanRun run = new MMRPPlanRun(getCtx(), MRP_PlanRun_ID, get_TrxName());
		        MMRPInventoryAudit iaudit = new MMRPInventoryAudit(getCtx(), MRP_Inventory_Audit_ID, get_TrxName());
		        String sqlbplocation = "SELECT C_BPartner_Location_ID FROM C_BPartner_Location WHERE C_BPartner_ID=?";
		        int C_BPartner_Location_ID = DB.getSQLValue (null, sqlbplocation, iaudit.getC_BPartner_ID());
		      
		        
		        //
		        order.setAD_Org_ID(plan.getAD_Org_ID());
		        order.setC_BPartner_ID(iaudit.getC_BPartner_ID());
		        order.setC_BPartner_Location_ID(C_BPartner_Location_ID);
		        order.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
//		        order.setBill_BPartner_ID(originalOrder.getBill_BPartner_ID());
//		        order.setBill_Location_ID(originalOrder.getBill_Location_ID());
//		        order.setBill_User_ID(originalOrder.getBill_User_ID());
		    //    order.setSalesRep_ID(rma.getSalesRep_ID());
		        order.setM_PriceList_ID(plan.getM_PriceList_ID());
		        order.setIsSOTrx(false);
		        order.setM_Warehouse_ID(plan.getM_Warehouse_ID());
		        order.setC_DocTypeTarget_ID(plan.getC_DocTypeTarget_ID());
		   //     order.setC_PaymentTerm_ID(originalOrder.getC_PaymentTerm_ID());
		      //  order.setDeliveryRule(originalOrder.getDeliveryRule());
		        
		        if (!order.save())
		        {
		            throw new IllegalStateException("Could not create order");
		        }
		   //     String sqldemand = "SELECT MRP_MasterDemandLine_ID FROM MRP_MasterDemandLine WHERE MRP_MasterDemand_ID=?";
		   //     int MRP_MasterDemandLine_ID = DB.getSQLValue (null, sqldemand, run.getMRP_MasterDemand_ID());
		        MMRPMasterDemand demand = new MMRPMasterDemand(getCtx(), run.getMRP_MasterDemand_ID(), get_TrxName());
		   //     MMRPMasterDemandLine demandline = new MMRPMasterDemandLine(getCtx(), MRP_MasterDemandLine_ID, get_TrxName());
		                
		        MMRPMasterDemandLine lines[] = demand.getLines(true);
		        for (MMRPMasterDemandLine line : lines)
		        {
//		            if (line.getShipLine() != null && line.getShipLine().getC_OrderLine_ID() != 0)
//		            {
		                // Create order lines if the RMA Doc line has a shipment line 
		                MOrderLine orderLine = new MOrderLine(order);
		                orderLine.setAD_Org_ID(plan.getAD_Org_ID());
		                orderLine.setM_Product_ID(line.getM_Product_ID());
		           //     orderLine.setM_AttributeSetInstance_ID(originalOLine.getM_AttributeSetInstance_ID());
		                orderLine.setC_UOM_ID(line.getC_UOM_ID());
		           //     orderLine.setC_Tax_ID(originalOLine.getC_Tax_ID());
		                orderLine.setM_Warehouse_ID(plan.getM_Warehouse_ID());
		           //     orderLine.setC_Currency_ID(originalOLine.getC_Currency_ID());
		                orderLine.setQty(line.getQty());
		         //       orderLine.setC_Project_ID(originalOLine.getC_Project_ID());
		          //      orderLine.setC_Activity_ID(originalOLine.getC_Activity_ID());
		          //      orderLine.setC_Campaign_ID(originalOLine.getC_Campaign_ID());
		         //       orderLine.setPrice();
		          //      orderLine.setPrice(line.getAmt());
		                
		                if (!orderLine.save())
		                {
		                    throw new IllegalStateException("Could not create Order Line");
		                }
		}
        return "Order Created: " + order.getDocumentNo();
    }

}


