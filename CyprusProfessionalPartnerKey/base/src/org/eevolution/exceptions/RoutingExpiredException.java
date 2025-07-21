package org.eevolution.exceptions;


import java.sql.Timestamp;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.framework.I_AD_Workflow;

public class RoutingExpiredException extends CyprusException {
  private static final long serialVersionUID = -7522979292063177848L;
  
  public RoutingExpiredException(I_AD_Workflow wf, Timestamp date) {
    super(buildMessage(wf, date));
  }
  
  private static final String buildMessage(I_AD_Workflow wf, Timestamp date) {
    return "@NotValid@ @AD_Workflow_ID@:" + wf.getValue() + " - @Date@:" + date;
  }
}
