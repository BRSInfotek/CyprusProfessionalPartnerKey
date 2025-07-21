package org.cyprusbrs.framework;



public enum X_Ref_Quantity_Type {
  ALLOCATED("A"),
  DEDICATED("D"),
  EXPECTED("E"),
  ON_HAND("H"),
  ORDERED("O"),
  RESERVED("R");
  
  public static final int AD_Reference_ID = 533;
  
  private final String value;
  
  X_Ref_Quantity_Type(String value) {
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public static boolean isValid(String test) {
    for (X_Ref_Quantity_Type v : values()) {
      if (v.getValue().equals(test))
        return true; 
    } 
    return false;
  }
  
  public static X_Ref_Quantity_Type getEnum(String value) {
    for (X_Ref_Quantity_Type v : values()) {
      if (v.getValue().equals(value))
        return v; 
    } 
    return null;
  }
}

