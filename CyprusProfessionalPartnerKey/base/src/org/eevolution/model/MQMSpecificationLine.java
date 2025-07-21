package org.eevolution.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

public class MQMSpecificationLine extends X_QM_SpecificationLine {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public MQMSpecificationLine(Properties ctx, int QM_SpecificationLine_ID, String trxName) {
    super(ctx, QM_SpecificationLine_ID, trxName);
  }
  
  public MQMSpecificationLine(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }
  
  public boolean evaluate(Object valueObj, String value1) {
    boolean result = false;
    if (valueObj instanceof Number) {
      result = compareNumber((Number)valueObj, value1, getValue());
    } else {
      result = compareString(valueObj, value1, getValue());
    } 
    return result;
  }
  
  private boolean compareNumber(Number valueObj, String value1, String value2) {
    BigDecimal valueObjB = null;
    BigDecimal value1B = null;
    BigDecimal value2B = null;
    try {
      if (valueObj instanceof BigDecimal) {
        valueObjB = (BigDecimal)valueObj;
      } else if (valueObj instanceof Integer) {
        valueObjB = new BigDecimal(((Integer)valueObj).intValue());
      } else {
        valueObjB = new BigDecimal(String.valueOf(valueObj));
      } 
    } catch (Exception e) {
      this.log.fine("compareNumber - valueObj=" + valueObj + " - " + e.toString());
      return compareString(valueObj, value1, value2);
    } 
    try {
      value1B = new BigDecimal(value1);
    } catch (Exception e) {
      this.log.fine("compareNumber - value1=" + value1 + " - " + e.toString());
      return compareString(valueObj, value1, value2);
    } 
    String op = getOperation();
    if ("==".equals(op))
      return (valueObjB.compareTo(value1B) == 0); 
    if (">>".equals(op))
      return (valueObjB.compareTo(value1B) > 0); 
    if (">=".equals(op))
      return (valueObjB.compareTo(value1B) >= 0); 
    if ("<<".equals(op))
      return (valueObjB.compareTo(value1B) < 0); 
    if ("<=".equals(op))
      return (valueObjB.compareTo(value1B) <= 0); 
    if ("~~".equals(op))
      return (valueObjB.compareTo(value1B) == 0); 
    if ("!=".equals(op))
      return (valueObjB.compareTo(value1B) != 0); 
    if ("SQ".equals(op))
      throw new IllegalArgumentException("SQL not Implemented"); 
    if ("AB".equals(op)) {
      if (valueObjB.compareTo(value1B) < 0)
        return false; 
      try {
        value2B = new BigDecimal(String.valueOf(value2));
        return (valueObjB.compareTo(value2B) <= 0);
      } catch (Exception e) {
        this.log.fine("compareNumber - value2=" + value2 + " - " + e.toString());
        return false;
      } 
    } 
    throw new IllegalArgumentException("Unknown Operation=" + op);
  }
  
  private boolean compareString(Object valueObj, String value1S, String value2S) {
    String valueObjS = String.valueOf(valueObj);
    String op = getOperation();
    if ("==".equals(op))
      return (valueObjS.compareTo(value1S) == 0); 
    if (">>".equals(op))
      return (valueObjS.compareTo(value1S) > 0); 
    if (">=".equals(op))
      return (valueObjS.compareTo(value1S) >= 0); 
    if ("<<".equals(op))
      return (valueObjS.compareTo(value1S) < 0); 
    if ("<=".equals(op))
      return (valueObjS.compareTo(value1S) <= 0); 
    if ("~~".equals(op))
      return (valueObjS.compareTo(value1S) == 0); 
    if ("!=".equals(op))
      return (valueObjS.compareTo(value1S) != 0); 
    if ("SQ".equals(op))
      throw new IllegalArgumentException("SQL not Implemented"); 
    if ("AB".equals(op)) {
      if (valueObjS.compareTo(value1S) < 0)
        return false; 
      return (valueObjS.compareTo(value2S) <= 0);
    } 
    throw new IllegalArgumentException("Unknown Operation=" + op);
  }
}
