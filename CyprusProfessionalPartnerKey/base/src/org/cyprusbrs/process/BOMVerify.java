package org.cyprusbrs.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.cyprusbrs.framework.MBOM;
import org.cyprusbrs.framework.MBOMProduct;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.util.DB;

public class BOMVerify extends SvrProcess {

	
	/**	The Product			*/
	private int		p_M_Product_ID = 0;
	
	/** List of PriceListVersions where the BOMProduct is defined */
	private ArrayList<Integer> parentPLV = null;
	
	/** Parent BOM existence..  */
	private boolean parentPLVexist = false;
	
	/**	List of Products	*/
	private ArrayList<MProduct>	m_products = null;
	
	
	/**	Product				*/
	private MProduct			m_product = null;
	
	/** Where is the BOMValidate process called from
	 *  true  = Called from menu option Verify BOMs 
	 *  false = Called by clicking the Verify button in the Product window */
	private boolean m_CalledFromMenu = true;
	
	/** Product Category	*/
	private int		p_M_Product_Category_ID = 0;
	
	/** Re-Validate			*/
	private boolean	p_IsReValidate = false;
	
	/** Check Price List     */
	private boolean	p_IsCheckPriceList = false;	
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
		ProcessInfoParameter[] para = getParameter();
		for (ProcessInfoParameter element : para) {
			String name = element.getParameterName();
			if (element.getParameter() == null)
				;
			else if (name.equals("M_Product_Category_ID"))
				p_M_Product_Category_ID = element.getParameterAsInt();
			else if (name.equals("IsReValidate"))
				p_IsReValidate = "Y".equals(element.getParameter());
			else if (name.equals("IsCheckPriceList"))
				p_IsCheckPriceList = "Y".equals(element.getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_M_Product_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		
		if (p_M_Product_ID != 0)
		{
			m_CalledFromMenu = false;
			log.info("M_Product_ID=" + p_M_Product_ID);
			return validateProduct(new MProduct(getCtx(), p_M_Product_ID, get_TrxName()));
		}
		log.info("M_Product_Category_ID=" + p_M_Product_Category_ID
				+ ", IsReValidate=" + p_IsReValidate + ", IsCheckPriceList=" + p_IsCheckPriceList );
		//
		int counter = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM M_Product "
			+ "WHERE IsBOM='Y' AND ";
		if (p_M_Product_Category_ID == 0)
			sql += "AD_Client_ID=? ";
		else
			sql += "M_Product_Category_ID=? ";
		if (!p_IsReValidate)
			sql += "AND IsVerified<>'Y' ";
		sql += "ORDER BY Name";
		int AD_Client_ID = getAD_Client_ID();
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			if (p_M_Product_Category_ID == 0)
				pstmt.setInt (1, AD_Client_ID);
			else
				pstmt.setInt(1, p_M_Product_Category_ID);
			 rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				String info = validateProduct(new MProduct(getCtx(), rs, get_TrxName()));
				addLog(0, null, null, info);
				counter++;
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
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
		return "#" + counter;
		
		/**Old technique
		
		if (p_M_Product_ID != 0)
		{
			log.info("M_Product_ID=" + p_M_Product_ID);
			m_CalledFromMenu = false;
			MProduct m_product=new MProduct(getCtx(), p_M_Product_ID, get_TrxName());
			String validateProd=validateProduct(m_product);
			if(validateProd!=null && validateProd.length()>0)
			{
				log.warning (m_product.getName() + " Not Verified :  " + validateProd);
				return validateProd;
			}
			else
			{
				m_product.setIsVerified(true);
				if(m_product.save(get_TrxName()))
				{
					DB.commit(true, get_TrxName());
				}
			}
		}
//		log.info("M_Product_Category_ID=" + p_M_Product_Category_ID
//			+ ", IsReValidate=" + p_IsReValidate);
		//
		
		return "Verified";
		
		*/
	}

	
	/**
	 * 	Validate Product
	 *	@param product product
	 *	@return Info
	 */
	private String validateProduct (MProduct product)
	{
		m_product = product;

		if (!m_product.isBOM())
		{				
			m_product.setIsVerified(false);
			m_product.save();
			return m_product.getName() + " @NotValid@";
		}

		/** Price list versions where the BOMproduct is included*/ 
		parentPLV = new ArrayList<Integer>();
		parentPLVexist = false;
		if(p_IsCheckPriceList)
		{       
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql = "SELECT M_Pricelist_Version_ID FROM M_ProductPrice WHERE M_Product_ID=? AND IsActive = 'Y'";

			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, m_product.get_ID());
				 rs = pstmt.executeQuery ();
				while (rs.next ())
					parentPLV.add(Integer.valueOf(rs.getInt(1)));

				if (parentPLV.size() > 0)
					parentPLVexist = true;
				rs.close ();
				pstmt.close ();
				pstmt = null;
			}
			catch (Exception e)
			{
				log.log (Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
//			try
//			{
//				if (pstmt != null)
//					pstmt.close ();
//				pstmt = null;
//			}
//			catch (Exception e)
//			{
//				pstmt = null;
//			}
		}/** End of finding PLV's for the BOMProduct */


		MBOM[] boms = MBOM.getOfProduct(getCtx(), m_product.getM_Product_ID(), get_TrxName(), " IsActive = 'Y' ");
		if (boms.length == 0)
		{
			log.warning (m_product.getName() + ": "  + "Does not have any active BOMs.");
			m_product.setIsVerified(false);
			m_product.save();
			return m_product.getName() + " @NotValid@";
		}

		for (int i = 0; i < boms.length; i++)
		{
			if (!validateBOM(boms[i]))
			{
				m_product.setIsVerified(false);
				m_product.save();
				return m_product.getName() + " " + boms[i].getName() + " @NotValid@";
			}
		}

		//	OK
		m_product.setIsVerified(true);
		m_product.save();
		return m_product.getName() + " @IsValid@";
	}	//	validateProduct
	
	
	/**
	 * 	Validate BOM
	 *	@param bom bom
	 *	@return true if valid
	 */
	private boolean validateBOM (MBOM bom)
	{
		MBOMProduct[] BOMproducts = MBOMProduct.getOfBOM(bom);
		if (BOMproducts.length == 0)
		{
			log.warning (m_product.getName() + ": "  + "Does not have any active BOM components for one of its BOMs.");
			return false;
		}
		boolean retvalue = true;
		for (MBOMProduct BOMproduct : BOMproducts) {
			m_products = new ArrayList<MProduct>();
			m_products.add(m_product);
			MProduct pp = new MProduct(getCtx(), BOMproduct.getM_ProductBOM_ID(), get_TrxName());
			if (pp.isBOM())
				retvalue = validateProduct(pp, BOMproduct.getComponentBOM(), bom.getBOMType(), bom.getBOMUse());
			if(parentPLVexist)
				retvalue &= checkPLV(pp);
			if (!(retvalue))
				return false;
		}
		if (!(retvalue))
			return false;
		return true;
	}	//	validateBOM
	
	
	/**
	 * 	Validate Product
	 *	@param product product
	 *  @param componentBOM bom
	 *	@param BOMType type
	 *	@param BOMUse use
	 *	@return true if valid
	 */
	private boolean validateProduct (MProduct product, MBOM componentBOM, String BOMType, String BOMUse)
	{
		if (!product.isBOM())
			return true;

		if (m_products.contains(product))
		{
			log.warning (m_product.getName() + ": " + product.getName() + " is recursively included.");
			return false;
		}

		/** If BOMValidate is being run for a single product, set validation to false
		 *  if any of the included BOMS are not verified. We cannot do this for the
		 *  Verify BOMs process because we cannot control the order in which the BOMs
		 *  will be processed.
		 */
		if (!m_CalledFromMenu && !product.isVerified())
		{
			log.warning (m_product.getName() + ": " + product.getName() + " does not have a valid BOM. Try verifying its BOM first.");
			return false;
		}

		m_products.add(product);
		log.fine(product.getName());

		MBOM bom = null;
		// The included component is a BOM component but no Component BOM was 
		// specified in the BOM line. In this case only validate that BOM of 
		// the included component having the same BOM Type and Use as the parent product.
		if (componentBOM == null)
		{
			String restriction = "BOMType='" + BOMType + "' AND BOMUse='" + BOMUse + "'" + " AND IsActive = 'Y' ";
			MBOM[] boms = MBOM.getOfProduct(getCtx(), product.getM_Product_ID(), get_TrxName(),
					restriction);
			if (boms.length != 1)
			{
				log.warning("Component (M_Product_ID) " + product.getM_Product_ID() + ", " + 
						restriction + " - Length=" + boms.length);
				return false;
			}
			bom = boms[0];
		}
		// A Component BOM was specified for the included component. 
		// Hence only validate that component BOM.
		else
		{
			bom = componentBOM;
		}
		MBOMProduct[] BOMproducts = MBOMProduct.getOfBOM(bom);
		if (BOMproducts.length == 0)
		{
			log.warning (m_product.getName() + ": " + product.getName() + " does not have any active BOM components for one of its BOMs.");
			return false;
		}
		boolean retvalue = true;
		for (MBOMProduct BOMproduct : BOMproducts) {
			MProduct pp = new MProduct(getCtx(), BOMproduct.getM_ProductBOM_ID(), get_TrxName());
			if (pp.isBOM())
				retvalue = validateProduct(pp, BOMproduct.getComponentBOM(), bom.getBOMType(), bom.getBOMUse());
			if(parentPLVexist)
				retvalue &= checkPLV(pp);
			if (!(retvalue))
				return false;
		}
		if (!(retvalue))
			return false;

		return true;			
	}	//	validateProduct
	
	
	/**When the process is executed for a BOM product,
	 * it checks whether the BOM components are included in the price list versions where the BOMProduct is included. 
	 */
	private boolean checkPLV(MProduct prod)
	{
		ArrayList<Integer> childPLV = new ArrayList<Integer>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT M_Pricelist_Version_ID FROM M_ProductPrice WHERE M_Product_ID=? AND IsActive = 'Y'";

		try
		{

			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, prod.get_ID());
			 rs = pstmt.executeQuery ();
			while (rs.next ())
				childPLV.add(Integer.valueOf(rs.getInt(1)));

			if (childPLV.size() == 0)
			{
				log.warning (prod.getName() + ": "  + "Is not included in any Price List");
				return false;	
			}				
			if (!( (childPLV.size() >= parentPLV.size()) && (childPLV.containsAll(parentPLV)) ))
			{
				log.warning (prod.getName() + ": "  + "Is not included in all the parent BOM product Price List Versions");
				return false;	
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;

		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
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
		return true;
	}
	/**  End of verification of Price List Versions	 */
	
	
	/**
	 * Verified the BOM Product
	 * @param mProduct
	 * @return
	 */
	/**
	private String validateProduct(MProduct mProduct) {
		
		StringBuffer sbCheck=new StringBuffer(); 
		
		// a. All items in Bill of Material tab at least an item and all items should have at least 1 qty.
		String sqlBlank="select count(*) from M_Product_BOM where M_Product_ID=?";
		if(DB.getSQLValue(get_TrxName(), sqlBlank, mProduct.getM_Product_ID())<=0)
		{
			sbCheck.append("No any component added in Bill of Material");
			return sbCheck.toString();
		}
		
		String sqlZeroQty="select count(*) from M_Product_BOM where M_Product_ID=? AND bomqty<=0";
		if(DB.getSQLValue(get_TrxName(), sqlZeroQty, mProduct.getM_Product_ID())>0)
		{
			sbCheck.append("BOM qty not added any component in Bill of Material");
			return sbCheck.toString();
		}
		
		// b. If any item has already a BOM or sub BOM then it must have "Verifed" checkbox BOM. Otherwise it will not be verified parent BOM.
		
		String sqlSubBOM="select mpb.M_ProductBOM_ID,mp.name, mp.IsBOM, mp.IsVerified from M_Product_BOM mpb " + 
				" inner join M_Product mp ON (mpb.M_ProductBOM_ID=mp.M_Product_ID) " + 
				" where mpb.M_Product_ID=? and mp.IsBOM='Y' and mp.IsVerified='N'";// --- Check any item which are subBOM and it is not verified."
		if(DB.getSQLValue(get_TrxName(), sqlSubBOM, mProduct.getM_Product_ID())>0)
		{
			sbCheck.append("Sub BOM is not verified for any component in Bill of Material");
			return sbCheck.toString();
		}
		return sbCheck.toString();
	}
	*/

}
