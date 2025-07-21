package org.eevolution.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.cyprusbrs.model.Query;

public class MPPOrderBOM extends X_PP_Order_BOM {
  private static final long serialVersionUID = 1L;
  
  public MPPOrderBOM(Properties ctx, int PP_Order_BOM_ID, String trxName) {
    super(ctx, PP_Order_BOM_ID, trxName);
    if (PP_Order_BOM_ID == 0)
      setProcessing(false); 
  }
  
  public MPPOrderBOM(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }
  
  public MPPOrderBOM(MPPProductBOM bom, int PP_Order_ID, String trxName) {
    this(bom.getCtx(), 0, trxName);
    setBOMType(bom.getBOMType());
    setBOMUse(bom.getBOMUse());
    setM_ChangeNotice_ID(bom.getM_ChangeNotice_ID());
    setHelp(bom.getHelp());
    setProcessing(bom.isProcessing());
    setHelp(bom.getHelp());
    setDescription(bom.getDescription());
    setM_AttributeSetInstance_ID(bom.getM_AttributeSetInstance_ID());
    setM_Product_ID(bom.getM_Product_ID());
    setName(bom.getName());
    setRevision(bom.getRevision());
    setValidFrom(bom.getValidFrom());
    setValidTo(bom.getValidTo());
    setValue(bom.getValue());
    setDocumentNo(bom.getDocumentNo());
    setC_UOM_ID(bom.getC_UOM_ID());
    setPP_Order_ID(PP_Order_ID);
  }
  
  public MPPOrderBOMLine[] getLines() {
    String whereClause = "PP_Order_BOM_ID=?";
    List<MPPOrderBOMLine> list = (new Query(getCtx(), "PP_Order_BOMLine", whereClause, get_TrxName()))
      .setParameters(new Object[] { Integer.valueOf(get_ID()) }).list();
    return list.<MPPOrderBOMLine>toArray(new MPPOrderBOMLine[list.size()]);
  }
  
  protected boolean beforeDelete() {
    byte b;
    int i;
    MPPOrderBOMLine[] arrayOfMPPOrderBOMLine;
    for (i = (arrayOfMPPOrderBOMLine = getLines()).length, b = 0; b < i; ) {
      MPPOrderBOMLine line = arrayOfMPPOrderBOMLine[b];
      line.deleteEx(false);
      b++;
    } 
    return true;
  }
  
  public String toString() {
    StringBuffer sb = (new StringBuffer("MPPOrderBOM["))
      .append(get_ID()).append("-").append(getDocumentNo())
      .append("]");
    return sb.toString();
  }
}
