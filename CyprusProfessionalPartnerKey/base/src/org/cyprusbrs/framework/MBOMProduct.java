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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;

/**
 * 	BOM Product/Component Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MBOMProduct.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class MBOMProduct extends X_M_BOMProduct
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3431041011059529621L;

	private MProduct m_component;
	/**
	 * 	Get Products of BOM
	 *	@param bom bom
	 *	@return array of BOM Products
	 */
	public static MBOMProduct[] getOfBOM (MBOM bom) 
	{
		//FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
		String whereClause = "M_BOM_ID=?";
		List <MBOMProduct> list = new Query(bom.getCtx(), I_M_BOMProduct.Table_Name, whereClause, bom.get_TrxName()) 
		.setParameters(bom.getM_BOM_ID())
		.setOrderBy("SeqNo")
		.list(); 
		
		MBOMProduct[] retValue = new MBOMProduct[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	getOfProduct

	/**	Logger	*/
	private static CLogger s_log = CLogger.getCLogger (MBOMProduct.class);
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param M_BOMProduct_ID id
	 *	@param trxName trx
	 */
	public MBOMProduct (Properties ctx, int M_BOMProduct_ID, String trxName)
	{
		super (ctx, M_BOMProduct_ID, trxName);
		if (M_BOMProduct_ID == 0)
		{
		//	setM_BOM_ID (0);
			setBOMProductType (BOMPRODUCTTYPE_StandardProduct);	// S
			setBOMQty (Env.ONE);
			setIsPhantom (false);
			setLeadTimeOffset (0);
		//	setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_BOMProduct WHERE M_BOM_ID=@M_BOM_ID@
		}
	}	//	MBOMProduct

	/**
	 * 	Parent Constructor
	 *	@param bom product
	 */
	public MBOMProduct (MBOM bom)
	{
		this (bom.getCtx(), 0, bom.get_TrxName());
		m_bom = bom;
	}	//	MBOMProduct

	
	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName trx
	 */
	public MBOMProduct (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MBOMProduct

	/**	BOM Parent				*/
	private MBOM		m_bom = null;
	
	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MBOM getBOM()
	{
		if (m_bom == null && getM_BOM_ID() != 0)
			m_bom = MBOM.get(getCtx(), getM_BOM_ID());
		return m_bom;
	}	//	getBOM
	
	
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true/false
	 */
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		//	Product
		if (getBOMProductType().equals(BOMPRODUCTTYPE_OutsideProcessing))
		{
			if (getM_ProductBOM_ID() != 0)
				setM_ProductBOM_ID(0);
		}
		else if (getM_ProductBOM_ID() == 0)
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@ @M_ProductBOM_ID@"));
			return false;
		}
		//	Operation
		if (getM_ProductOperation_ID() == 0)
		{
			if (getSeqNo() != 0)
				setSeqNo(0);
		}
		else if (getSeqNo() == 0)
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@ @SeqNo@"));
			return false;
		}
		//	Product Attribute Instance
		if (getM_AttributeSetInstance_ID() != 0)
		{
			getBOM();
			if (m_bom != null 
				&& X_M_BOM.BOMTYPE_Make_To_Order.equals(m_bom.getBOMType()))
				;
			else
			{
				log.saveError("Error", Msg.parseTranslation(getCtx(), 
					"Reset @M_AttributeSetInstance_ID@: Not Make-to-Order"));
				setM_AttributeSetInstance_ID(0);
				return false;
			}
		}
		//	Alternate
		if ((getBOMProductType().equals(BOMPRODUCTTYPE_Alternative)
			|| getBOMProductType().equals(BOMPRODUCTTYPE_AlternativeDefault))
			&& getM_BOMAlternative_ID() == 0)
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@ @M_BOMAlternative_ID@"));
			return false;
		}
		//	Operation
		if (getM_ProductOperation_ID() != 0)
		{
			if (getSeqNo() == 0)
			{
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@ @SeqNo@"));
				return false;
			}
		}
		else	//	no op
		{
			if (getSeqNo() != 0)
				setSeqNo(0);
			if (getLeadTimeOffset() != 0)
				setLeadTimeOffset(0);
		}
		
		//	Set Line Number
		if (getLine() == 0)
		{
			String sql = "SELECT NVL(MAX(Line),0)+10 FROM M_BOMProduct WHERE M_BOM_ID=?";
			int ii = DB.getSQLValue (get_TrxName(), sql, getM_BOM_ID());
			setLine (ii);
		}

		return true;
	}	//	beforeSave
	
	 public static MBOMProduct[] getBOMLinesOrderByProductName(MProduct product, String bomType, String bomUse, boolean isAscending) {
		    CPreparedStatement cPreparedStatement = null;
		    String sql = "SELECT M_BOM_ID FROM M_BOM WHERE M_Product_ID=? AND BOMType = ? AND BOMUse = ? AND IsActive = 'Y' ";
		    String trx = product.get_TrxName();
		    int bomID = 0;
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;
		    try {
		      cPreparedStatement = DB.prepareStatement(sql, trx);
		      cPreparedStatement.setInt(1, product.getM_Product_ID());
		      cPreparedStatement.setString(2, bomType);
		      cPreparedStatement.setString(3, bomUse);
		      rs = cPreparedStatement.executeQuery();
		      if (rs.next())
		        bomID = rs.getInt(1); 
		    } catch (Exception e) {
		      s_log.log(Level.SEVERE, sql, e);
		    } finally {
		      DB.close(rs);
		      DB.close(cPreparedStatement);
		      rs = null; cPreparedStatement = null;
		    } 
		    return getBOMLinesOrderByProductName(MBOM.get(product.getCtx(), bomID), isAscending);
		  }
	 
	 public static MBOMProduct[] getBOMLinesOrderByProductName(MBOM bom, boolean isAscending) {
		    CPreparedStatement cPreparedStatement = null;
		    String sql = "SELECT * FROM M_BOMProduct WHERE M_BOM_ID=? AND IsActive='Y'";
		    if (isAscending) {
		      sql = sql.concat(" ORDER BY getProductName(M_ProductBOM_ID)");
		    } else {
		      sql = sql.concat(" ORDER BY getProductName(M_ProductBOM_ID) DESC");
		    } 
		    ArrayList<MBOMProduct> list = new ArrayList<MBOMProduct>();
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;
		    try {
		      cPreparedStatement = DB.prepareStatement(sql, bom.get_TrxName());
		      cPreparedStatement.setInt(1, bom.getM_BOM_ID());
		      rs = cPreparedStatement.executeQuery();
		      while (rs.next())
		        list.add(new MBOMProduct(bom.getCtx(), rs, bom.get_TrxName())); 
		    } catch (Exception e) {
		      s_log.log(Level.SEVERE, sql, e);
		    } finally {
		      DB.close(cPreparedStatement);
		      DB.close(rs);
		      rs = null; cPreparedStatement = null;
		    } 
		    MBOMProduct[] retValue = new MBOMProduct[list.size()];
		    list.toArray(retValue);
		    return retValue;
		  }
	

	  
	 
	  public MProduct getComponent() {
	    if (this.m_component == null && getM_ProductBOM_ID() != 0)
	      this.m_component = MProduct.get(getCtx(), getM_ProductBOM_ID()); 
	    return this.m_component;
	  }
	  
	  /**	Component BOM				*/
		private MBOM		m_componentBOM = null;
		
		/**
		 * 	Get Component BOM
		 *	@return MBOM
		 */
		public MBOM getComponentBOM()
		{
			if (m_componentBOM == null && getM_ProductBOMVersion_ID() != 0)
				m_componentBOM = MBOM.get(getCtx(), getM_ProductBOMVersion_ID());
			return m_componentBOM;
		}	//	getComponentBOM

	  /**
		 * 	Get BOM Lines for Product. Default to Current Active, Master BOM
		 *	@param product product
		 *  @param bomType bomtype
		 *  @param bomUse bomuse
		 *	@return array of BOMs
		 */
		public static MBOMProduct[] getBOMLines (MProduct product, String bomType, String bomUse)
		{
			// return lines for Current Active, Master BOM
			String sql = "SELECT M_BOM_ID FROM M_BOM WHERE M_Product_ID=? " +
			             "AND BOMType = ? AND BOMUse = ? AND IsActive = 'Y' ";
			String trx = product.get_TrxName();
			int bomID = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, trx);
				pstmt.setInt(1, product.getM_Product_ID());
				pstmt.setString(2, bomType);
				pstmt.setString(3, bomUse);
				 rs = pstmt.executeQuery();
				if (rs.next())
					bomID = rs.getInt(1);
				rs.close();
				pstmt.close();
				pstmt = null;
			}
			catch (Exception e)
			{
				s_log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return getBOMLines(MBOM.get(product.getCtx(), bomID));
		}	//	getBOMLines
		
		/**
		 * 	Get BOM Lines for Product given a specific BOM
		 * 	@param ctx context
		 *	@param M_Product_ID product
		 *	@param trx transaction
		 *	@return array of BOMs
		 */
		public static MBOMProduct[] getBOMLines (MBOM bom)
		{
			String sql = "SELECT * FROM M_BOMProduct WHERE M_BOM_ID=? AND IsActive='Y' ORDER BY Line";
			ArrayList<MBOMProduct> list = new ArrayList<MBOMProduct>();
			PreparedStatement pstmt = null;
			ResultSet rs  = null;
			try
			{
				pstmt = DB.prepareStatement(sql, bom.get_TrxName());
				pstmt.setInt(1, bom.getM_BOM_ID());
				 rs = pstmt.executeQuery();
				while (rs.next())
					list.add(new MBOMProduct (bom.getCtx(), rs, bom.get_TrxName()));
				rs.close();
				pstmt.close();
				pstmt = null;
			}
			catch (Exception e)
			{
				s_log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			//
			MBOMProduct[] retValue = new MBOMProduct[list.size()];
			list.toArray(retValue);
			return retValue;
		}	//	getBOMLines
	
}	//	MBOMProduct
