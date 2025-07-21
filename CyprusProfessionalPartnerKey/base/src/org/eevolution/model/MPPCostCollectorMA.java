package org.eevolution.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.model.engines.IInventoryAllocation;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;

public class MPPCostCollectorMA extends X_PP_Cost_CollectorMA implements IInventoryAllocation {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static CLogger	s_log	= CLogger.getCLogger (MPPCostCollectorMA.class);


	public MPPCostCollectorMA(Properties ctx, int PP_Cost_CollectorMA_ID, String trxName) {
	    super(ctx, PP_Cost_CollectorMA_ID, trxName);
	    if (PP_Cost_CollectorMA_ID != 0)
	      throw new IllegalArgumentException("Multi-Key"); 
	  }
	  
	  public MPPCostCollectorMA(Properties ctx, ResultSet rs, String trxName) {
	    super(ctx, rs, trxName);
	  }
	
  /**
	 * 	Delete all Material Allocation for Inventory
	 *	@param M_InventoryLine_ID inventory
	 *	@param trxName transaction
	 *	@return number of rows deleted or -1 for error PP_Cost_CollectorLine 
	 */
	public static int deleteCostCollectorMA (int PP_Cost_CollectorLine_ID, String trxName)
	{
		String sql = "DELETE FROM PP_Cost_CollectorMA ma WHERE EXISTS "
			+ "(SELECT * FROM PP_Cost_CollectorLine l WHERE l.PP_Cost_CollectorLine_ID=ma.PP_Cost_CollectorLine_ID"
			+ " AND PP_Cost_CollectorLine_ID=" + PP_Cost_CollectorLine_ID + ")";
		return DB.executeUpdate(sql, trxName);
	}	//	deleteInventoryMA  
	
	
	/**
	 * 	Parent Constructor
	 *	@param parent parent
	 *	@param M_AttributeSetInstance_ID asi
	 *	@param MovementQty qty
	 */
	public MPPCostCollectorMA (MPPCostCollectorLine parent, int M_AttributeSetInstance_ID, BigDecimal QtyEntered)
	{
		this (parent.getCtx(), 0, parent.get_TrxName());
		setClientOrg(parent);
		set_ValueNoCheck("PP_Cost_CollectorLine_ID", parent.getPP_Cost_CollectorLine_ID());
		setPP_Cost_Collector_ID(parent.getPP_Cost_Collector_ID());
		//setM_MovementLine_ID(parent.getM_MovementLine_ID());
		//
		setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
		setMovementQty(QtyEntered);
	}	//	MMovementLineMA
	  
	/**
	 * 	Get Material Allocations for Line
	 *	@param ctx context
	 *	@param M_MovementLine_ID line
	 *	@param trxName trx
	 *	@return allocations
	 */
	public static MPPCostCollectorMA[] get (Properties ctx, int M_MovementLine_ID, String trxName)
	{
		ArrayList<MPPCostCollectorMA> list = new ArrayList<MPPCostCollectorMA>();
		String sql = "SELECT * FROM PP_Cost_CollectorMA WHERE PP_Cost_CollectorLine_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs  = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, M_MovementLine_ID);
			 rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				list.add (new MPPCostCollectorMA (ctx, rs, trxName));
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			s_log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MPPCostCollectorMA[] retValue = new MPPCostCollectorMA[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	get
}
