package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Properties;

import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CCache;


public class MPPWFNodeAsset extends X_PP_WF_Node_Asset {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static CCache<Integer, Collection<MPPWFNodeAsset>> s_cache = new CCache("PP_WF_Node_Asset", 20);
	  
	  public static Collection<MPPWFNodeAsset> forAD_WF_Node_ID(Properties ctx, int AD_WF_Node_ID) {
	    Collection<MPPWFNodeAsset> lines = (Collection<MPPWFNodeAsset>)s_cache.get(Integer.valueOf(AD_WF_Node_ID));
	    if (lines != null)
	      return lines; 
	    lines = (new Query(ctx, "PP_WF_Node_Asset", "AD_WF_Node_ID=?", null))
	      .setParameters(new Object[] { Integer.valueOf(AD_WF_Node_ID) }).setOnlyActiveRecords(true)
	      .setOrderBy("SeqNo")
	      .list();
	    s_cache.put(Integer.valueOf(AD_WF_Node_ID), lines);
	    return lines;
	  }
	public MPPWFNodeAsset(Properties ctx, int PP_WF_Node_Asset_ID, String trxName)
	{
		super(ctx, PP_WF_Node_Asset_ID, trxName);
	} //
	
	public MPPWFNodeAsset(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	} //	 MPPProductBOMLine
}
