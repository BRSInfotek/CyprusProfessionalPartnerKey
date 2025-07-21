package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.DB;

public class MProductionLineMA extends X_M_ProductionLineMA 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9056217457105828481L;


	/**
	 * 	Get Material Allocations for Line
	 *	@param ctx context
	 *	@param M_MovementLine_ID line
	 *	@param trxName trx
	 *	@return allocations
	 */
	public static MProductionLineMA[] get (Properties ctx, int M_ProductionLine_ID, String trxName)
	{
		ArrayList<MProductionLineMA> list = new ArrayList<MProductionLineMA>();
		String sql = "SELECT * FROM M_ProductionLineMA WHERE M_ProductionLine_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, M_ProductionLine_ID);
			 rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				list.add (new MProductionLineMA (ctx, rs, trxName));
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
//		try
//		{
//			if (pstmt != null)
//				pstmt.close ();
//			pstmt = null;
//		}
//		catch (Exception e)
//		{
//			pstmt = null;
//		}
//		
		MProductionLineMA[] retValue = new MProductionLineMA[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	get
	
	/**
	 * 	Delete all Material Allocation for Movement
	 *	@param M_Movement_ID movement
	 *	@param trxName transaction
	 *	@return number of rows deleted or -1 for error
	 */
	public static int deleteMovementMA (int M_ProductionLine_ID, String trxName)
	{
		String sql = "DELETE FROM M_ProductionLineMA ma WHERE EXISTS "
			+ "(SELECT * FROM M_ProductionLine l WHERE l.M_ProductionLine_ID=ma.M_ProductionLine_ID"
			+ " AND M_ProductionLine_ID=" + M_ProductionLine_ID + ")";
		return DB.executeUpdate(sql, trxName);
	}	//	deleteInOutMA
	
	/**	Logger	*/
	private static CLogger	s_log	= CLogger.getCLogger (MMovementLineMA.class);

	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_MovementLineMA_ID ignored
	 *	@param trxName trx
	 */
	public MProductionLineMA (Properties ctx, int M_ProductionLineMA_ID,
		String trxName)
	{
		super (ctx, M_ProductionLineMA_ID, trxName);
		if (M_ProductionLineMA_ID != 0)
			throw new IllegalArgumentException("Multi-Key");
	}	//	MMovementLineMA

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result ser
	 *	@param trxName trx
	 */
	public MProductionLineMA (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MMovementLineMA
	
	/**
	 * 	Parent Constructor
	 *	@param parent parent
	 *	@param M_AttributeSetInstance_ID asi
	 *	@param MovementQty qty
	 */
	public MProductionLineMA (MProductionLine parent, int M_AttributeSetInstance_ID, BigDecimal MovementQty)
	{
		this (parent.getCtx(), 0, parent.get_TrxName());
		setClientOrg(parent);
		setM_ProductionLine_ID(parent.getM_ProductionLine_ID());
		//
		setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
		setMovementQty(MovementQty);
	}	//	MMovementLineMA
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	@Override
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MProductionLineMA[");
		sb.append("M_ProductionLine_ID=").append(getM_ProductionLine_ID())
			.append(",M_AttributeSetInstance_ID=").append(getM_AttributeSetInstance_ID())
			.append(", Qty=").append(getMovementQty())
			.append ("]");
		return sb.toString ();
	}	//	toString

}
