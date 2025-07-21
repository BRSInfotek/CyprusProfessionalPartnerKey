package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

public class MResources extends X_M_Resources {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param S_Resource_ID id
	 */
	public MResources (Properties ctx, int M_Resources_ID, String trxName)
	{
		super (ctx, M_Resources_ID, trxName);
	}	//	MResource

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 */
	public MResources (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MResource

	@Override
	protected boolean beforeSave(boolean newRecord) {
	    // Get the asset linked to this resource
	    int assetId = getA_Asset_ID();  // Assuming this method retrieves the Asset ID
	    if (assetId > 0) {
	        // Fetch the asset quantity from the Asset Master
	        MAsset asset = new MAsset(getCtx(), assetId, get_TrxName());
	        BigDecimal assetQty = asset.getA_QTY_Current();  // Assuming getQty() returns the Asset Quantity
	        
	        BigDecimal qtyRequired = new BigDecimal(getQtyRequiered());  // Get the required quantity from the resource
	        if (qtyRequired.compareTo(assetQty) > 0) {
	            log.saveError("InvalidQty", "Qty Required cannot exceed the available Asset Quantity: " + assetQty);
	            return false;  // Prevent saving the record
	        }
	    }
	    return true;  // Proceed with the normal save process
	}

	
}
