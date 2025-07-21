package org.eevolution.exceptions;

import org.cyprus.exceptions.CyprusException;
import org.eevolution.model.MPPOrderNode;

public class ActivityProcessedException extends CyprusException {
  private static final long serialVersionUID = 1L;
  
  public ActivityProcessedException(MPPOrderNode activity) {
    super("Order Activity Already Processed - " + activity);
  }
}
