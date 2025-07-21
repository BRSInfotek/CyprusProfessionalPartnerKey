package org.cyprusbrs.framework;

public enum X_Ref__YesNo {

	  NO("N"),
	  YES("Y");
	
	  public static final int AD_Reference_ID = 319;
	  
	  private final String value;
	  
	  X_Ref__YesNo(String value) {
	    this.value = value;
	  }
	  
	  public String getValue() {
	    return this.value;
	  }
	  
	  public static boolean isValid(String test) {
	    if (test == null)
	      return true; 
	    for (X_Ref__YesNo v : values()) {
	      if (v.getValue().equals(test))
	        return true; 
	    } 
	    return false;
	  }
}
