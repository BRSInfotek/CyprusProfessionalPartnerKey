package org.cyprusbrs.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProduction;
import org.cyprusbrs.framework.MProductionLine;
import org.cyprusbrs.framework.X_M_ProductionLine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CyprusUserError;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.ValueNamePair;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

public class ProductionCreate extends SvrProcess {

	private int p_M_Production_ID=0;
	private MProduction m_production = null;
	private boolean mustBeStocked = false;  //not used
	private boolean recreate = false;
	private BigDecimal newQty = null;
	private int p_PP_Product_BOM_ID=0;
	private int m_level = 0;

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if ("Recreate".equals(name))
				recreate = "Y".equals(para[i].getParameter());
			else if ("ProductionQty".equals(name))
				newQty  = (BigDecimal) para[i].getParameter();
			else if ("PP_Product_BOM_ID".equals(name))
				p_PP_Product_BOM_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_M_Production_ID = getRecord_ID();
		

	}

	@Override
	protected String doIt() throws Exception {
		
		m_production = new MProduction(getCtx(), p_M_Production_ID, get_TrxName());
		/**
		 * No Action
		 */
		if (m_production.isProcessed()) 
		{
			log.info("Already Posted");
			return "@AlreadyPosted@";
		}
		
		if (!recreate && m_production.isCreated())
			throw new CyprusUserError("Production already created.");
		
		if (!m_production.isCreated() || recreate) 
		{
			int line = 100;
			int no = DB.executeUpdateEx("DELETE M_ProductionLine WHERE M_Production_ID = ?", new Object[]{m_production.getM_Production_ID()},get_TrxName());
			if (no == -1) raiseError("ERROR", "DELETE M_ProductionLine WHERE M_Production_ID = "+ m_production.getM_Production_ID());
			
			MProduct product = MProduct.get(getCtx(), m_production.getM_Product_ID());
/**
			MProductionLine pl = new MProductionLine(getCtx(), 0 , get_TrxName());
			pl.setLine(line);
			BigDecimal moveQty=m_production.getProductionQty().compareTo(newQty)!=0?newQty:m_production.getProductionQty();
			pl.setAD_Org_ID(m_production.getAD_Org_ID());
			pl.setDescription(m_production.getDescription());
			pl.setM_Product_ID(m_production.getM_Product_ID());
			pl.setM_Locator_ID(m_production.getM_Locator_ID());
			pl.setMovementQty(moveQty);
			pl.setM_Production_ID(m_production.getM_Production_ID());
			pl.setIsEndProduct(true);
			pl.saveEx(get_TrxName());
			if (explosion(pl, product, moveQty , line) == 0 )
				raiseError("No BOM Lines", "");
				**/
			BigDecimal moveQty=m_production.getProductionQty().compareTo(newQty)!=0?newQty:m_production.getProductionQty();
			Integer lineNo=explosion(m_production, product, moveQty , line);
			if (lineNo == 0 )
				raiseError("No BOM Lines", "");
			else
			{	
				MProductionLine pl = new MProductionLine(getCtx(), 0 , get_TrxName());
				pl.setLine(++lineNo+line);
				pl.setAD_Org_ID(m_production.getAD_Org_ID());
				pl.setDescription(m_production.getDescription());
				pl.setM_Product_ID(m_production.getM_Product_ID());
				pl.setM_Locator_ID(m_production.getM_Locator_ID());
				pl.setMovementQty(moveQty);
				pl.setM_Production_ID(m_production.getM_Production_ID());
				pl.setIsEndProduct(true);
				pl.saveEx(get_TrxName());
			}		
		}

		if(!m_production.isCreated() || recreate)	
		{	
			m_production.setIsCreated(true);
			m_production.setPP_Product_BOM_ID(p_PP_Product_BOM_ID);
			m_production.setProductionQty(newQty);
			m_production.saveEx(get_TrxName());
		}
		return "@OK@";
	}
	
	
	/**
	 * Explosion the Production Plan
	 * @param pp
	 * @param product
	 * @param qty
	 * @throws Exception 
	 */
	private int explosion(MProduction pp , MProduct product , BigDecimal qty , int line) throws Exception
	{
		MPPProductBOM bom = MPPProductBOM.getDefault(product, get_TrxName());
		if(bom == null )
		{	
			raiseError("Do not exist default BOM for this product :" 
					+ product.getValue() + "-" 
					+ product.getName(),"");
			
		}				
		MPPProductBOMLine[] bom_lines = bom.getLines(new Timestamp (System.currentTimeMillis()));
		m_level += 1;
		int components = 0;
		line = line * m_level;
		for(MPPProductBOMLine bomline : bom_lines)
		{
			MProduct component = MProduct.get(getCtx(), bomline.getM_Product_ID());
			
			if(component.isBOM() && !component.isStocked())
			{	
				explosion(pp, component, bomline.getQtyBOM() , line);
			}
			else
			{	
				line += 1;
				X_M_ProductionLine pl = new X_M_ProductionLine(getCtx(), 0 , get_TrxName());
				pl.setLine(line);
				pl.setAD_Org_ID(pp.getAD_Org_ID());
				pl.setDescription(bomline.getDescription());
				pl.setM_Product_ID(bomline.getM_Product_ID());
				pl.setM_Locator_ID(pp.getM_Locator_ID());
				pl.setM_Production_ID(pp.getM_Production_ID());
				pl.setMovementQty(bomline.getQtyBOM().multiply(qty).negate());
				pl.saveEx(get_TrxName());
				components += 1;
				
			}
		
		}
		return  components;
	}	
	
	private void raiseError(String string, String sql) throws Exception {
		String msg = string;
		ValueNamePair pp = CLogger.retrieveError();
		if (pp != null)
			msg = pp.getName() + " - ";
		msg += sql;
		throw new CyprusUserError (msg);
	}
}
