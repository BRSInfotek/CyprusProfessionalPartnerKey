package org.cyprus.mrp.model;

import java.sql.ResultSet;
import java.util.Properties;

@SuppressWarnings("serial")
public class MMRPInventoryAudit extends X_MRP_Inventory_Audit {

	public MMRPInventoryAudit(Properties ctx, int MRP_Inventory_Audit_ID, String trxName) {
		super(ctx, MRP_Inventory_Audit_ID, trxName);
		
	}

	public MMRPInventoryAudit(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		
	}

		
	
	
 
}
