package org.cyprusbrs.apps.form;

import java.util.Properties;

import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.Env;
import org.eevolution.model.MPPProductBOM;

public class TreeBOM {

	
	public static CLogger log = CLogger.getCLogger(TreeMaintenance.class);
	
	public Properties getCtx() {
		return Env.getCtx();
	}
	
	/**
	 * get Product Summary
	 * @param product Product
	 * @param isLeaf is Leaf
	 * @return String
	 */
	public String productSummary(MProduct product, boolean isLeaf) {
		MUOM uom = MUOM.get(getCtx(), product.getC_UOM_ID());
		String value = product.getValue();
		String name = product.get_Translation(MProduct.COLUMNNAME_Name);
		//
		StringBuffer sb = new StringBuffer(value);
		if (name != null && !value.equals(name))
			sb.append("_").append(product.getName());
		sb.append(" [").append(uom.get_Translation(MUOM.COLUMNNAME_UOMSymbol)).append("]");
		//
		return sb.toString();
	}
	
	/**
	 * get Product Summary
	 * @param bom Product BOM
	 * @return String 
	 */
	public String productSummary(MPPProductBOM bom) {
		String value = bom.getValue();
		String name = bom.get_Translation(MPPProductBOM.COLUMNNAME_Name);
		//
		StringBuffer sb = new StringBuffer(value);
		if (name != null && !name.equals(value))
			sb.append("_").append(name);
		//
		return sb.toString();
	}
	
}

