package org.eevolution.model;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Properties;

import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CCache;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class MPPWFNodeProduct extends X_PP_WF_Node_Product {
  private static final long serialVersionUID = 1L;
  
  private static CCache<Integer, Collection<MPPWFNodeProduct>> s_cache = new CCache("PP_WF_Node_Product", 20);
  
  public static Collection<MPPWFNodeProduct> forAD_WF_Node_ID(Properties ctx, int AD_WF_Node_ID) {
    Collection<MPPWFNodeProduct> lines = (Collection<MPPWFNodeProduct>)s_cache.get(Integer.valueOf(AD_WF_Node_ID));
    if (lines != null)
      return lines; 
    String whereClause = "AD_WF_Node_ID=?";
    lines = (new Query(ctx, "PP_WF_Node_Product", "AD_WF_Node_ID=?", null))
      .setParameters(new Object[] { Integer.valueOf(AD_WF_Node_ID) }).setOnlyActiveRecords(true)
      .setOrderBy("SeqNo")
      .list();
    s_cache.put(Integer.valueOf(AD_WF_Node_ID), lines);
    return lines;
  }
  
  public MPPWFNodeProduct(Properties ctx, int PP_WF_Node_Product_ID, String trxName) {
    super(ctx, PP_WF_Node_Product_ID, trxName);
  }
  
  public MPPWFNodeProduct(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (getSeqNo() == 0) {
      String sql = "SELECT COALESCE(MAX(SeqNo),0)+10 FROM PP_WF_Node_Product WHERE  AD_WF_Node_ID=? AND PP_WF_Node_Product_ID<>?";
      int seqNo = DB.getSQLValueEx(get_TrxName(), "SELECT COALESCE(MAX(SeqNo),0)+10 FROM PP_WF_Node_Product WHERE  AD_WF_Node_ID=? AND PP_WF_Node_Product_ID<>?", new Object[] { Integer.valueOf(getAD_WF_Node_ID()), Integer.valueOf(get_ID()) });
      setSeqNo(seqNo);
    } 
    if (getQty().compareTo(Env.ZERO) == 0 && isSubcontracting())
      setQty(Env.ONE); 
    return true;
  }
}
