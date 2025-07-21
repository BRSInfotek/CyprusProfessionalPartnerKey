package org.eevolution.exceptions;

import java.sql.Timestamp;

import org.cyprus.exceptions.CyprusException;
import org.eevolution.model.I_PP_Product_BOM;

public class BOMExpiredException extends CyprusException {
  private static final long serialVersionUID = -3084324343550833077L;
  
  public BOMExpiredException(I_PP_Product_BOM bom, Timestamp date) {
    super(buildMessage(bom, date));
  }
  
  private static final String buildMessage(I_PP_Product_BOM bom, Timestamp date) {
    return "@NotValid@ @PP_Product_BOM_ID@:" + bom.getValue() + " - @Date@:" + date;
  }
}
