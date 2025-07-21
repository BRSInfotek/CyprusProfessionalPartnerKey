package org.cyprus.mfg.model;



public enum X_Ref_M_BOMProduct_SupplyType {
  ASSEMBLY_PULL("A"),
  OPERATION_PULL("O"),
  PUSH("P");
  
  public static final int AD_Reference_ID = 444;
  
  private final String value;
  
  X_Ref_M_BOMProduct_SupplyType(String value) {
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public static boolean isValid(String test) {
    for (X_Ref_M_BOMProduct_SupplyType v : values()) {
      if (v.getValue().equals(test))
        return true; 
    } 
    return false;
  }
}

