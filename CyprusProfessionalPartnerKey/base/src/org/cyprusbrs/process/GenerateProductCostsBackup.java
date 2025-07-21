package org.cyprusbrs.process;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

public class GenerateProductCostsBackup extends SvrProcess{

	Timestamp currentDate =  new Timestamp(System.currentTimeMillis());
	
	@Override
	protected void prepare() {
		// No Parameters
	}

	@Override
	protected String doIt() throws Exception {
		 StringBuilder result = new StringBuilder();
	        Trx trx = null;
	        
	        try {
	            trx = Trx.get(get_TrxName(), true);	           
	            result.append(backupMCostTable(trx));
	            result.append("\n");	            
	            result.append(backupMCostQueueTable(trx));	            
	            trx.commit();	            
	            return result.toString();	            
	        }
	        catch (Exception e) {
	            log.log(Level.SEVERE, "Error in backup process", e);
	            if (trx != null) {
	                try {
	                    trx.rollback();
	                } catch (Exception ex) {
	                    log.log(Level.SEVERE, "Could not rollback transaction", ex);
	                }
	            }
	            throw e;
	        } finally {
	            if (trx != null) {
	                trx.close();
	            }
	        }
    }

	private String backupMCostTable(Trx trx) {
        String sql;
        int rows = 0;        
        try {          
            sql = "INSERT INTO M_Cost_Backup (" +
                "ad_client_id, ad_org_id, m_product_id, m_costtype_id, " +
                "c_acctschema_id, m_costelement_id, m_attributesetinstance_id, " +
                "isactive, created, createdby, updated, updatedby, " +
                "currentcostprice, currentqty, cumulatedamt, cumulatedqty, " +
                "futurecostprice, description, percent, currentcostpricell, " +
                "futurecostpricell, iscostfrozen, brs_backupdate) " +
                "SELECT " +
                "ad_client_id, ad_org_id, m_product_id, m_costtype_id, " +
                "c_acctschema_id, m_costelement_id, m_attributesetinstance_id, " +
                "isactive, created, createdby, updated, updatedby, " +
                "currentcostprice, currentqty, cumulatedamt, cumulatedqty, " +
                "futurecostprice, description, percent, currentcostpricell, " +
                "futurecostpricell, iscostfrozen, " + 
                DB.TO_DATE(currentDate) + " " +
                "FROM M_Cost";
                
            rows = DB.executeUpdateEx(sql, trx.getTrxName());
            
            return "M_Cost backup completed. " + rows + " records copied.";
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in M_Cost backup", e);
            throw new RuntimeException("Error in M_Cost backup: " + e.getMessage(), e);
        }
    }

	private String backupMCostQueueTable(Trx trx) {
	    String sql;
	    int rows = 0;	    
	    try {	        
	        sql = "INSERT INTO M_CostQueue_Backup (" +
	            "m_costqueue_backup_id, ad_client_id, ad_org_id, isactive, " +
	            "created, createdby, updated, updatedby, m_costtype_id, " +
	            "c_acctschema_id, m_product_id, m_attributesetinstance_id, " +
	            "m_costelement_id, currentcostprice, currentqty, brs_backupdate, m_costqueue_id) " +
	            "SELECT nextval('M_CostQueue_Backup_seq'), ad_client_id, ad_org_id, isactive, " +
	            "created, createdby, updated, updatedby, m_costtype_id, " +
	            "c_acctschema_id, m_product_id, m_attributesetinstance_id, " +
	            "m_costelement_id, currentcostprice, currentqty, " +
	            DB.TO_DATE(currentDate) + ",m_costqueue_id " +
	            "FROM M_CostQueue";
	            
	        rows = DB.executeUpdateEx(sql, trx.getTrxName());
	        
	        return "M_CostQueue backup completed. " + rows + " records copied.";
	        
	    } catch (Exception e) {
	        log.log(Level.SEVERE, "Error in M_CostQueue backup", e);
	        return "Error in M_CostQueue backup: " + e.getMessage();
	    }
	}

}
