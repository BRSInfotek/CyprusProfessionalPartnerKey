
package org.eevolution.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.cyprus.model.GridTabWrapper;
import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;

/**
 * Cost Collector Callout
 *
 * @author Victor Perez www.e-evolution.com     
 * @author Teo Sarca, www.arhipac.ro
 */
public class CalloutCostCollector extends CalloutEngine
{
	public String order (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer PP_Order_ID = (Integer)value;
		if (PP_Order_ID == null || PP_Order_ID <= 0)
			return "";
		I_PP_Cost_Collector cc = GridTabWrapper.create(mTab, I_PP_Cost_Collector.class);
		//
		MPPOrder pp_order =  new MPPOrder(ctx, PP_Order_ID, null);
		MPPCostCollector.setPP_Order(cc, pp_order);
		//
		return "";
	}

	public String node (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		Integer PP_Order_Node_ID = (Integer)value;
		if (PP_Order_Node_ID == null || PP_Order_Node_ID <= 0)
			return "";
		I_PP_Cost_Collector cc = GridTabWrapper.create(mTab, I_PP_Cost_Collector.class);
		//
		MPPOrderNode node = getPP_Order_Node(ctx, PP_Order_Node_ID);
		cc.setS_Resource_ID(node.getS_Resource_ID());
		cc.setIsSubcontracting(node.isSubcontracting());
		cc.setMovementQty(node.getQtyToDeliver());
		//
		duration(ctx, WindowNo, mTab, mField, value);
		//
		return "";
	}
	
	public String duration (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		I_PP_Cost_Collector cc = GridTabWrapper.create(mTab, I_PP_Cost_Collector.class);
		if (cc.getPP_Order_Node_ID() <= 0)
			return "";
		
		RoutingService routingService = RoutingServiceFactory.get().getRoutingService(ctx);
		BigDecimal durationReal = routingService.estimateWorkingTime(cc);
		// If Activity Control Duration should be specified
		// FIXME: this message is really anoying. We need to find a proper solution - teo_sarca
//		if(durationReal.signum() == 0)
//		{
//			throw new FillMandatoryException(MPPOrderNode.COLUMNNAME_SetupTimeReal, MPPOrderNode.COLUMNNAME_DurationReal);
//		}
		//
		cc.setDurationReal(durationReal);
		//
		return "";
	}
	

	private MPPOrderNode m_node = null;
	private MPPOrderNode getPP_Order_Node(Properties ctx, int PP_Order_Node_ID)
	{
		if (m_node != null && m_node.get_ID() == PP_Order_Node_ID)
		{
			return m_node;
		}
		m_node = new MPPOrderNode(ctx, PP_Order_Node_ID, null);
		return m_node;
	}

}


