package org.cyprus.crm.process;

import java.sql.Timestamp;

import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MOrder;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.SvrProcess;

public class GenerateOrderFromQuotation extends SvrProcess {

    private Integer p_C_Order_ID = 0;

    @Override
    protected void prepare() {
        p_C_Order_ID = getRecord_ID();
    }

    @Override
    protected String doIt() throws Exception {
        Timestamp p_DateDoc = new Timestamp(System.currentTimeMillis());
        
        // Get DocType where DocBaseType='SOO' and SO_SubType='MO'
        Integer p_C_DocType_ID = new Query(getCtx(), MDocType.Table_Name,
            "DocBaseType = ? AND DocSubTypeSO = ? AND AD_Client_ID = ?",
            get_TrxName())
            .setParameters("SOO", "MO", getAD_Client_ID())
            .setOrderBy("C_DocType_ID")
            .firstId();
        
        log.info("C_Order_ID=" + p_C_Order_ID + ", C_DocType_ID=" + p_C_DocType_ID);
            
        if (p_C_Order_ID == 0)
            throw new IllegalArgumentException("No Order Selected");
            
        // Get the source quotation
        MOrder from = new MOrder(getCtx(), p_C_Order_ID, get_TrxName());
        
        // Check if the quotation is completed
        if (!from.getDocStatus().equals(MOrder.DOCSTATUS_Completed)) {
            return "Sales Quotation must be Completed first - Current Status: " + from.getDocStatus();
        }
        
     // Check if a Sales Order already exists for this Quotation
        int existingOrderCount = new Query(getCtx(), MOrder.Table_Name,
            "Ref_Order_ID = ? AND DocStatus IN ('IP','CO','CL','DR') AND AD_Client_ID = ?",
            get_TrxName())
            .setParameters(p_C_Order_ID, getAD_Client_ID())
            .count();
            
        if (existingOrderCount > 0) {
            return "A Sales Order has already been created from this Quotation. Cannot create another one.";
        }
        
        MDocType dt = MDocType.get(getCtx(), p_C_DocType_ID);
        if (dt.get_ID() == 0)
            throw new IllegalArgumentException("No Valid Document Type Found");
            
        // Create new order from quotation
        MOrder newOrder = MOrder.copyFrom(from, p_DateDoc, 
            dt.getC_DocType_ID(), dt.isSOTrx(), false, true, get_TrxName());
        
        newOrder.setC_DocTypeTarget_ID(p_C_DocType_ID);
        newOrder.setRef_Order_ID(from.getC_Order_ID());
        newOrder.setDateOrdered(p_DateDoc);
        newOrder.setDatePromised(p_DateDoc);
        newOrder.setSalesRep_ID(from.getSalesRep_ID());  /// Added Sales Rep
        
        if (!newOrder.save()) {
            throw new IllegalStateException("Could not create new Order");
        }
            
        // Return success message with document info
        return "Successfully Created " + dt.getName() + ": " + newOrder.getDocumentNo();    
    }
}