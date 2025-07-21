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
package org.cyprusbrs.framework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

/**
 *	Product PO Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MProductPO.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MProductPO extends X_M_Product_PO
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -747761340543484440L;


	/**
	 * 	Get current PO of Product
	 * 	@param ctx context
	 *	@param M_Product_ID product
	 *	@param trxName transaction
	 *	@return PO - current vendor first
	 */
	public static MProductPO[] getOfProduct (Properties ctx, int M_Product_ID, String trxName)
	{
		final String whereClause = "M_Product_ID=?";
		List<MProductPO> list = new Query(ctx, Table_Name, whereClause, trxName)
									.setParameters(M_Product_ID)
									.setOnlyActiveRecords(true)
									.setOrderBy("IsCurrentVendor DESC")
									.list();
		return list.toArray(new MProductPO[list.size()]);
	}	//	getOfProduct

	/**
	 * 	Persistency Constructor
	 *	@param ctx context
	 *	@param ignored ignored
	 *	@param trxName transaction
	 */
	public MProductPO (Properties ctx, int ignored, String trxName)
	{
		super(ctx, 0, trxName);
		if (ignored != 0)
			throw new IllegalArgumentException("Multi-Key");
		else
		{
		//	setM_Product_ID (0);	// @M_Product_ID@
		//	setC_BPartner_ID (0);	// 0
		//	setVendorProductNo (null);	// @Value@
			setIsCurrentVendor (true);	// Y
		}
	}	//	MProduct_PO
	
	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MProductPO(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MProductPO
	
	private static CLogger s_log = CLogger.getCLogger(MProductPO.class);

	
	  public static MProductPO getOfVendorProduct(Properties ctx, int C_BPartner_ID, int M_Product_ID, Trx trx) {
		    CPreparedStatement cPreparedStatement = null;
		    MProductPO productPO = null;
		    String sql = "SELECT * FROM M_Product_PO WHERE C_BPartner_ID=? AND M_Product_ID = ? ";
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;
		    try {
		      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
		      cPreparedStatement.setInt(1, C_BPartner_ID);
		      cPreparedStatement.setInt(2, M_Product_ID);
		      rs = cPreparedStatement.executeQuery();
		      if (rs.next())
		        productPO = new MProductPO(ctx, rs, trx.getTrxName()); 
		    } catch (SQLException ex) {
		      s_log.log(Level.SEVERE, sql, ex);
		    } finally {
		      DB.close(rs);
		      DB.close(cPreparedStatement);
		      rs = null; cPreparedStatement = null;
		    } 
		    return productPO;
		  }

}	//	MProductPO
