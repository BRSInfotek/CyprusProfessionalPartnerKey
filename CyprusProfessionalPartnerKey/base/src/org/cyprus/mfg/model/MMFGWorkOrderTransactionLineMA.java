package org.cyprus.mfg.model;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;

public class MMFGWorkOrderTransactionLineMA extends X_MFG_WorkOrderTrxLineMA {
	  private static final CLogger log = CLogger.getCLogger(MMFGWorkOrderTransactionLineMA.class);
	  
	  private static final long serialVersionUID = 1L;
	  
	  public static MMFGWorkOrderTransactionLineMA[] get(Properties ctx, int MFG_WorkOrderTransactionLine_ID, String trxName) {
	    CPreparedStatement cPreparedStatement = null;
	    ArrayList<MMFGWorkOrderTransactionLineMA> list = new ArrayList<MMFGWorkOrderTransactionLineMA>();
	    String sql = "SELECT * FROM MFG_WorkOrderTrxLineMA WHERE MFG_WorkOrderTrxLine_ID=?";
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	      cPreparedStatement = DB.prepareStatement(sql, trxName);
	      cPreparedStatement.setInt(1, MFG_WorkOrderTransactionLine_ID);
	       rs = cPreparedStatement.executeQuery();
	      while (rs.next())
	        list.add(new MMFGWorkOrderTransactionLineMA(ctx, rs, null)); 
	      rs.close();
	      cPreparedStatement.close();
	      cPreparedStatement = null;
	    } catch (Exception e) {
	      s_log.log(Level.SEVERE, sql, e);
	    } 
	    finally
	    {
	    	DB.close(rs, cPreparedStatement);
	    	rs = null; cPreparedStatement = null;
	    }
//	    try {
//	      if (cPreparedStatement != null)
//	        cPreparedStatement.close(); 
//	      cPreparedStatement = null;
//	    } catch (Exception e) {
//	      cPreparedStatement = null;
//	    } 
	    MMFGWorkOrderTransactionLineMA[] retValue = new MMFGWorkOrderTransactionLineMA[list.size()];
	    list.toArray(retValue);
	    return retValue;
	  }
	  
	  public static int deleteWorkOrderTransactionMA(int MFG_WorkOrderTransaction_ID, String trxName) {
	    String sql = "DELETE FROM MFG_WorkOrderTrxLineMA ma WHERE EXISTS (SELECT * FROM MFG_WorkOrderTrxLine l WHERE l.MFG_WorkOrderTrxLine_ID=ma.M_WorkOrderTrxLine_ID AND MFG_WorkOrderTransaction_ID=" + MFG_WorkOrderTransaction_ID + ")";
	    return DB.executeUpdateEx( sql, new Object[0],trxName);
	  }
	  
	  public static int deleteWorkOrderTransactionLineMA(int MFG_WorkOrderTransactionLine_ID, String trxName) {
	    String sql = "DELETE FROM MFG_WorkOrderTrxLineMA ma WHERE EXISTS (SELECT * FROM MFG_WorkOrderTrxLine l WHERE l.MFG_WorkOrderTrxLine_ID=ma.MFG_WorkOrderTrxLine_ID AND MFG_WorkOrderTrxLine_ID=" + MFG_WorkOrderTransactionLine_ID + ")";
	    return DB.executeUpdateEx( sql, new Object[0], trxName);
	  }
	  
	  private static CLogger s_log = CLogger.getCLogger(MMFGWorkOrderTransactionLineMA.class);
	  
	  public MMFGWorkOrderTransactionLineMA(Properties ctx, int MFG_WorkOrderTransactionLineMA_ID, String trxName) {
	    super(ctx, MFG_WorkOrderTransactionLineMA_ID, trxName);
	    if (MFG_WorkOrderTransactionLineMA_ID != 0)
	      throw new IllegalArgumentException("Multi-Key"); 
	  }
	  
	  public MMFGWorkOrderTransactionLineMA(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
	  
	  public MMFGWorkOrderTransactionLineMA(MMFGWorkOrderTransactionLine parent, int M_AttributeSetInstance_ID, BigDecimal MovementQty) {
	    this(parent.getCtx(), 0, parent.get_TrxName());
	    setClientOrg((PO)parent);
	    setMFG_WorkOrderTrxLine_ID(parent.getMFG_WorkOrderTrxLine_ID());
	    setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
	    setMovementQty(MovementQty);
	  }
	  
	  public MMFGWorkOrderTransactionLineMA(Properties ctx, String trxName, int MFG_WorkOrderTransactionLine_ID, int M_AttributeSetInstance_ID, BigDecimal MovementQty) {
	    this(ctx, 0, trxName);
	    MMFGWorkOrderTransactionLine line = new MMFGWorkOrderTransactionLine(ctx, MFG_WorkOrderTransactionLine_ID, trxName);
	    setClientOrg((PO)line);
	    setMFG_WorkOrderTrxLine_ID(MFG_WorkOrderTransactionLine_ID);
	    setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
	    setMovementQty(MovementQty);
	  }
	  
	  public String toString() {
	    StringBuffer sb = new StringBuffer("MMFGWorkOrderTransactionLineMA[");
	    sb.append("MFG_WorkOrderTrxLine_ID=").append(getMFG_WorkOrderTrxLine_ID()).append(",M_AttributeSetInstance_ID=").append(getM_AttributeSetInstance_ID()).append(", Qty=").append(getMovementQty()).append("]");
	    return sb.toString();
	  }
}

