package org.eevolution.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.framework.MAttribute;
import org.cyprusbrs.framework.MAttributeInstance;
import org.cyprusbrs.framework.MAttributeSet;
import org.cyprusbrs.framework.MAttributeSetInstance;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;

public class MQMSpecification extends X_QM_Specification {
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MQMSpecificationLine[] m_lines;
  
  public MQMSpecification(Properties ctx, int QM_Specification_ID, String trxName) {
    super(ctx, QM_Specification_ID, trxName);
    this.m_lines = null;
  }
  
  public MQMSpecification(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_lines = null;
  }
  
  public MQMSpecificationLine[] getLines(String where) {
    CPreparedStatement cPreparedStatement = null;
    if (this.m_lines != null)
      return this.m_lines; 
    ArrayList<MQMSpecificationLine> list = new ArrayList<MQMSpecificationLine>();
    String sql = "SELECT * FROM QM_SpecificationLine WHERE QM_SpecificationLine_ID=? AND " + where + " ORDER BY Line";
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, getQM_Specification_ID());
      ResultSet rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MQMSpecificationLine(getCtx(), rs, get_TrxName())); 
      rs.close();
      cPreparedStatement.close();
      cPreparedStatement = null;
    } catch (Exception e) {
      this.log.log(Level.SEVERE, "getLines", e);
    } 
    try {
      if (cPreparedStatement != null)
        cPreparedStatement.close(); 
      cPreparedStatement = null;
    } catch (Exception e) {
      cPreparedStatement = null;
    } 
    this.m_lines = new MQMSpecificationLine[list.size()];
    list.toArray(this.m_lines);
    return this.m_lines;
  }
  
  public boolean isValid(int M_AttributeSetInstance_ID) {
    MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), M_AttributeSetInstance_ID, get_TrxName());
    MAttributeSet as = MAttributeSet.get(getCtx(), asi.getM_AttributeSet_ID());
    MAttribute[] attributes = as.getMAttributes(false);
    for (int i = 0; i < attributes.length; i++) {
      MAttributeInstance instance = attributes[i].getMAttributeInstance(M_AttributeSetInstance_ID);
      MQMSpecificationLine[] lines = getLines(" M_Attribute_ID=" + attributes[i].getM_Attribute_ID());
      for (int s = 0; s < lines.length; i++) {
        MQMSpecificationLine line = lines[s];
        if ("N".equals(attributes[i].getAttributeValueType())) {
          BigDecimal bigDecimal = instance.getValueNumber();
          if (!line.evaluate(bigDecimal, instance.getValue()));
          return false;
        } 
        String objValue = instance.getValue();
        if (!line.evaluate(objValue, instance.getValue()))
          return false; 
      } 
    } 
    return true;
  }
}
