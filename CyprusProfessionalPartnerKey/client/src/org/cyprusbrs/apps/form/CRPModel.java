package org.cyprusbrs.apps.form;


import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.swing.JTree;

import org.cyprusbrs.framework.MResource;
import org.jfree.data.category.CategoryDataset;

/**
 * @author Gunther Hoppe, tranSIT GmbH Ilmenau/Germany
 * @version 1.0, October 14th 2005
 */
public interface CRPModel
{
	public JTree getTree();	
	public CategoryDataset getDataset();
	public BigDecimal calculateLoad(Timestamp dateTime, MResource r, String docStatus);
	
}

