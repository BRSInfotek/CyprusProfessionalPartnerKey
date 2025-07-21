
package org.cyprusbrs.framework;

import java.sql.ResultSet;
import java.util.Properties;



public class MProductionPlan extends X_M_ProductionPlan 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8009045030932557461L;
	private MProduction 	m_parent = null;

	/**
	 * 
	 */
	
	public MProductionPlan(Properties ctx, int M_ProductionPlan_ID, String trxName) {
		super(ctx, M_ProductionPlan_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MProductionPlan (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MProductionPlan
	
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
		/// Code commented by Mukesh as per discussion by Surya @20220411
//		if (getM_Product_ID() != 0 && is_ValueChanged(COLUMNNAME_M_Product_ID))
//		{
//            BigDecimal CurrentCostPrice = UtilCosting.getCurrentCostPrice(getM_Product_ID(),getAD_Client_ID(),getAD_Org_ID(),getM_AttributeSetInstance_ID());
//            setCurrentCostPrice (CurrentCostPrice);
//		}
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
	protected void setParent(MProduction parent)
	{
		m_parent = parent; 
	}	//	setParent

	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MProduction getParent()
	{
		if (m_parent == null)
			m_parent = new MProduction (getCtx(), getM_Production_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	/**
	 * 	Create Material Allocations for new Instances
	 */
}	//	MInventoryLine

