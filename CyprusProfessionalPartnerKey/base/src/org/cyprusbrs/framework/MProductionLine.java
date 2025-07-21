
package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.cyprus.util.UtilCosting;



public class MProductionLine extends X_M_ProductionLine 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2823233331871756749L;

	private MProductionPlan 	m_parent = null;
	protected MProduction productionParent=null;

	
	public MProductionLine(Properties ctx, int M_ProductionLine_ID, String trxName) {
		super(ctx, M_ProductionLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MProductionLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);

	}	//	MProductionLine
	/**
	 * 
	 */
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true if can be saved
	 */
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		
		if (getM_Product_ID() != 0 && is_ValueChanged(COLUMNNAME_M_Product_ID))
		{
            BigDecimal CurrentCostPrice = UtilCosting.getCurrentCostPrice(getM_Product_ID(),getAD_Client_ID(),getAD_Org_ID(),getM_AttributeSetInstance_ID());
            //setCurrentCostPrice (CurrentCostPrice);
		}
		return true;
	}	//	beforeSave

	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return true
	 */
	@Override
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success)
			return false;
		
		//	Create MA
		//if (newRecord && success 
		//	&& m_isManualEntry && getM_AttributeSetInstance_ID() == 0)
		//	createMA();
		return true;
	}	//	afterSave
	
	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else
			setDescription(desc + " | " + description);
	}	//	addDescription
	
	
	/**
	 * 	Get Parent
	 *	@param parent parent
	 */
	protected void setParent(MProductionPlan parent)
	{
		m_parent = parent; 
	}	//	setParent

	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MProductionPlan getParent()
	{
		if (m_parent == null)
			m_parent = new MProductionPlan (getCtx(), getM_ProductionPlan_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	
	/**
	 * 	Get Parent
	 *	@param parent parent
	 */
	protected void setParentMProductLine(MProduction parent)
	{
		productionParent = parent; 
	}	//	setParent

	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MProduction getParentMProductLine()
	{
		if (productionParent == null)
			productionParent = new MProduction (getCtx(), getM_Production_ID(), get_TrxName());
		return productionParent;
	}	//	getParent
	
	
	/**
	 * 	Get Product
	 *	@return product or null if not defined
	 */
	public MProduct getProduct()
	{
		if (getM_Product_ID() != 0)
			return MProduct.get(getCtx(), getM_Product_ID());
		return null;
	}	//	getProduct
	
	/**
	 * Parent Constructor
	 * @param header
	 */
	public MProductionLine( MProduction header ) {
		super( header.getCtx(), 0, header.get_TrxName() );
		setM_Production_ID( header.get_ID());
		setAD_Client_ID(header.getAD_Client_ID());
		setAD_Org_ID(header.getAD_Org_ID());
		productionParent = header;
	}
}	//	MInventoryLine

