package org.eevolution.model;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.cyprus.exceptions.CyprusException;
import org.cyprus.exceptions.DBException;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MStorage;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.CPreparedStatement;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class MPPOrderBOMLine extends X_PP_Order_BOMLine {
  private static final long serialVersionUID = 1L;
  
  private MPPOrder m_parent;
  
  private boolean m_isExplodePhantom;
  
  private BigDecimal m_qtyRequiredPhantom;
  
  private BigDecimal m_qtyOnHand;
  
  private BigDecimal m_qtyAvailable;
  
  public static MPPOrderBOMLine forM_Product_ID(Properties ctx, int PP_Order_ID, int M_Product_ID, String trxName) {
    String whereClause = "PP_Order_ID=? AND M_Product_ID=?";
    return (MPPOrderBOMLine)(new Query(ctx, "PP_Order_BOMLine", "PP_Order_ID=? AND M_Product_ID=?", trxName))
      .setParameters(new Object[] { Integer.valueOf(PP_Order_ID), Integer.valueOf(M_Product_ID) }).firstOnly();
  }
  
  public MPPOrderBOMLine(Properties ctx, int PP_Order_BOMLine_ID, String trxName) {
    super(ctx, PP_Order_BOMLine_ID, trxName);
    this.m_parent = null;
    this.m_isExplodePhantom = false;
    this.m_qtyRequiredPhantom = null;
    this.m_qtyOnHand = null;
    this.m_qtyAvailable = null;
    if (PP_Order_BOMLine_ID == 0)
      setDefault(); 
  }
  
  public MPPOrderBOMLine(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
    this.m_parent = null;
    this.m_isExplodePhantom = false;
    this.m_qtyRequiredPhantom = null;
    this.m_qtyOnHand = null;
    this.m_qtyAvailable = null;
  }
  
  public MPPOrderBOMLine(MPPProductBOMLine bomLine, int PP_Order_ID, int PP_Order_BOM_ID, int M_Warehouse_ID, String trxName) {
    this(bomLine.getCtx(), 0, trxName);
    setPP_Order_BOM_ID(PP_Order_BOM_ID);
    setPP_Order_ID(PP_Order_ID);
    setM_Warehouse_ID(M_Warehouse_ID);
    setM_ChangeNotice_ID(bomLine.getM_ChangeNotice_ID());
    setDescription(bomLine.getDescription());
    setHelp(bomLine.getHelp());
    setAssay(bomLine.getAssay());
    setQtyBatch(bomLine.getQtyBatch());
    setQtyBOM(bomLine.getQtyBOM());
    setIsQtyPercentage(bomLine.isQtyPercentage());
    setComponentType(bomLine.getComponentType());
    setC_UOM_ID(bomLine.getC_UOM_ID());
    setForecast(bomLine.getForecast());
    setIsCritical(bomLine.isCritical());
    setIssueMethod(bomLine.getIssueMethod());
    setLeadTimeOffset(bomLine.getLeadTimeOffset());
    setM_AttributeSetInstance_ID(bomLine.getM_AttributeSetInstance_ID());
    setM_Product_ID(bomLine.getM_Product_ID());
    setScrap(bomLine.getScrap());
    setValidFrom(bomLine.getValidFrom());
    setValidTo(bomLine.getValidTo());
    setBackflushGroup(bomLine.getBackflushGroup());
  }
  
  protected boolean beforeSave(boolean newRecord) {
    if (!isActive())
      throw new CyprusException("De-Activating an BOM Line is not allowed"); 
    if (!newRecord && is_ValueChanged("M_Product_ID"))
      throw new CyprusException("Changing Product is not allowed"); 
    if (getLine() == 0) {
      String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM PP_Order_BOMLine WHERE PP_Order_ID=?";
      int ii = DB.getSQLValueEx(get_TrxName(), sql, new Object[] { Integer.valueOf(getPP_Order_ID()) });
      setLine(ii);
    } 
    if (newRecord && "PH".equals(getComponentType())) {
      this.m_qtyRequiredPhantom = getQtyRequiered();
      this.m_isExplodePhantom = true;
      setQtyRequiered(Env.ZERO);
    } 
    if (newRecord || is_ValueChanged("C_UOM_ID") || is_ValueChanged("QtyEntered") || is_ValueChanged("QtyRequiered")) {
      int precision = MUOM.getPrecision(getCtx(), getC_UOM_ID());
      setQtyEntered(getQtyEntered().setScale(precision, RoundingMode.UP));
      setQtyRequiered(getQtyRequiered().setScale(precision, RoundingMode.UP));
    } 
    if (is_ValueChanged("QtyDelivered") || is_ValueChanged("QtyRequiered"))
      reserveStock(); 
    return true;
  }
  
  protected boolean afterSave(boolean newRecord, boolean success) {
    if (!success)
      return false; 
    explodePhantom();
    return true;
  }
  
  protected boolean beforeDelete() {
    setQtyRequiered(Env.ZERO);
    reserveStock();
    return true;
  }
  
  private void explodePhantom() {
    if (this.m_isExplodePhantom && this.m_qtyRequiredPhantom != null) {
      MProduct parent = MProduct.get(getCtx(), getM_Product_ID());
      int PP_Product_BOM_ID = MPPProductBOM.getBOMSearchKey(parent);
      if (PP_Product_BOM_ID <= 0)
        return; 
      MPPProductBOM bom = MPPProductBOM.get(getCtx(), PP_Product_BOM_ID);
      if (bom != null) {
        byte b;
        int i;
        MPPProductBOMLine[] arrayOfMPPProductBOMLine;
        for (i = (arrayOfMPPProductBOMLine = bom.getLines()).length, b = 0; b < i; ) {
          MPPProductBOMLine PP_Product_BOMline = arrayOfMPPProductBOMLine[b];
          MPPOrderBOMLine PP_Order_BOMLine = new MPPOrderBOMLine(PP_Product_BOMline, getPP_Order_ID(), getPP_Order_BOM_ID(), getM_Warehouse_ID(), get_TrxName());
          PP_Order_BOMLine.setAD_Org_ID(getAD_Org_ID());
          PP_Order_BOMLine.setQtyOrdered(this.m_qtyRequiredPhantom);
          PP_Order_BOMLine.saveEx();
          b++;
        } 
      } 
      this.m_isExplodePhantom = false;
    } 
  }
  
  public MProduct getM_Product() {
    return MProduct.get(getCtx(), getM_Product_ID());
  }
  
  public MUOM getC_UOM() {
    return MUOM.get(getCtx(), getC_UOM_ID());
  }
  
  public MWarehouse getM_Warehouse() {
    return MWarehouse.get(getCtx(), getM_Warehouse_ID());
  }
  
  public BigDecimal getQtyRequiredPhantom() {
    return (this.m_qtyRequiredPhantom != null) ? this.m_qtyRequiredPhantom : Env.ZERO;
  }
  
  public MPPOrder getParent() {
    int id = getPP_Order_ID();
    if (id <= 0) {
      this.m_parent = null;
      return null;
    } 
    if (this.m_parent == null || this.m_parent.get_ID() != id)
      this.m_parent = new MPPOrder(getCtx(), id, get_TrxName()); 
    return this.m_parent;
  }
  
  public int getPrecision() {
    return MUOM.getPrecision(getCtx(), getC_UOM_ID());
  }
  
  public BigDecimal getQtyMultiplier() {
    BigDecimal qty;
    if (isQtyPercentage()) {
      qty = getQtyBatch().divide(Env.ONEHUNDRED, 8, RoundingMode.HALF_UP);
    } else {
      qty = getQtyBOM();
    } 
    return qty;
  }
  
  public void setQtyOrdered(BigDecimal QtyOrdered) {
    BigDecimal multiplier = getQtyMultiplier();
    BigDecimal qty = QtyOrdered.multiply(multiplier).setScale(8, RoundingMode.UP);
    if (isComponentType(new String[] { "CO", "PH", "PK", "BY", "CP" })) {
      setQtyRequiered(qty);
    } else if (isComponentType(new String[] { "TL" })) {
      setQtyRequiered(multiplier);
    } else {
      throw new CyprusException("@NotSupported@ @ComponentType@ " + getComponentType());
    } 
    BigDecimal qtyScrap = getScrap();
    if (qtyScrap.signum() != 0) {
      qtyScrap = qtyScrap.divide(Env.ONEHUNDRED, 8, 0);
      setQtyRequiered(getQtyRequiered().divide(Env.ONE.subtract(qtyScrap), 8, 4));
    } 
  }
  
  public void setQtyRequiered(BigDecimal QtyRequiered) {
    if (QtyRequiered != null && getC_UOM_ID() != 0) {
      int precision = getPrecision();
      QtyRequiered = QtyRequiered.setScale(precision, RoundingMode.HALF_UP);
    } 
    super.setQtyRequiered(QtyRequiered);
  }
  
  public void setQtyReserved(BigDecimal QtyReserved) {
    if (QtyReserved != null && getC_UOM_ID() != 0) {
      int precision = getPrecision();
      QtyReserved = QtyReserved.setScale(precision, RoundingMode.HALF_UP);
    } 
    super.setQtyReserved(QtyReserved);
  }
  
  public BigDecimal getQtyOpen() {
    return getQtyRequiered().subtract(getQtyDelivered());
  }
  
  private void loadStorage(boolean reload) {
    CPreparedStatement cPreparedStatement = null;
    if (!reload && this.m_qtyOnHand != null && this.m_qtyAvailable != null)
      return; 
    String sql = "SELECT  bomQtyAvailable(M_Product_ID, M_Warehouse_ID, 0),bomQtyOnHand(M_Product_ID, M_Warehouse_ID, 0) FROM PP_Order_BOMLine WHERE PP_Order_BOMLine_ID=?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement("SELECT  bomQtyAvailable(M_Product_ID, M_Warehouse_ID, 0),bomQtyOnHand(M_Product_ID, M_Warehouse_ID, 0) FROM PP_Order_BOMLine WHERE PP_Order_BOMLine_ID=?", get_TrxName());
      DB.setParameters((PreparedStatement)cPreparedStatement, new Object[] { Integer.valueOf(get_ID()) });
      rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        this.m_qtyAvailable = rs.getBigDecimal(1);
        this.m_qtyOnHand = rs.getBigDecimal(2);
      } 
    } catch (SQLException e) {
      throw new DBException(e, "SELECT  bomQtyAvailable(M_Product_ID, M_Warehouse_ID, 0),bomQtyOnHand(M_Product_ID, M_Warehouse_ID, 0) FROM PP_Order_BOMLine WHERE PP_Order_BOMLine_ID=?");
    } finally {
      DB.close(rs, cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
  }
  
  public BigDecimal getQtyAvailable() {
    loadStorage(false);
    return this.m_qtyAvailable;
  }
  
  public BigDecimal getQtyVariance() {
    String whereClause = "PP_Order_BOMLine_ID=? AND PP_Order_ID=? AND DocStatus IN (?,?) AND CostCollectorType=?";
    BigDecimal qtyUsageVariance = (new Query(getCtx(), "PP_Cost_Collector", "PP_Order_BOMLine_ID=? AND PP_Order_ID=? AND DocStatus IN (?,?) AND CostCollectorType=?", get_TrxName()))
      .setParameters(new Object[] { Integer.valueOf(getPP_Order_BOMLine_ID()), 
          Integer.valueOf(getPP_Order_ID()), 
          "CO", 
          "CL", 
          "120" }).sum("MovementQty");
    return qtyUsageVariance;
  }
  
  public BigDecimal getQtyOnHand() {
    loadStorage(false);
    return this.m_qtyOnHand;
  }
  
  public boolean isComponentType(String... componentTypes) {
    String currentType = getComponentType();
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = componentTypes).length, b = 0; b < i; ) {
      String type = arrayOfString[b];
      if (currentType.equals(type))
        return true; 
      b++;
    } 
    return false;
  }
  
  public boolean isCoProduct() {
    return isComponentType(new String[] { "CP" });
  }
  
  public boolean isByProduct() {
    return isComponentType(new String[] { "BY" });
  }
  
  public boolean isComponent() {
    return isComponentType(new String[] { "CO", "PK" });
  }
  
  public void addDescription(String description) {
    String desc = getDescription();
    if (desc == null) {
      setDescription(description);
    } else {
      setDescription(String.valueOf(desc) + " | " + description);
    } 
  }
  
  private void setDefault() {
    setDescription("");
    setQtyDelivered(Env.ZERO);
    setQtyPost(Env.ZERO);
    setQtyReject(Env.ZERO);
    setQtyRequiered(Env.ZERO);
    setQtyReserved(Env.ZERO);
    setQtyScrap(Env.ZERO);
  }
  
  protected void reserveStock() {
    int header_M_Warehouse_ID = getParent().getM_Warehouse_ID();
    if (header_M_Warehouse_ID != 0) {
      if (header_M_Warehouse_ID != getM_Warehouse_ID())
        setM_Warehouse_ID(header_M_Warehouse_ID); 
      if (getAD_Org_ID() != getAD_Org_ID())
        setAD_Org_ID(getAD_Org_ID()); 
    } 
    BigDecimal target = getQtyRequiered();
    BigDecimal difference = target.subtract(getQtyReserved()).subtract(getQtyDelivered());
    this.log.fine("Line=" + getLine() + " - Target=" + target + ",Difference=" + difference + " - Requiered=" + getQtyRequiered() + 
        ",Reserved=" + getQtyReserved() + ",Delivered=" + getQtyDelivered());
    if (difference.signum() == 0)
      return; 
    MProduct product = getM_Product();
    if (!product.isStocked())
      return; 
    BigDecimal reserved = difference;
    int M_Locator_ID = getM_Locator_ID(reserved);
    if (!MStorage.add(getCtx(), getM_Warehouse_ID(), M_Locator_ID, 
        getM_Product_ID(), getM_AttributeSetInstance_ID(), 
        getM_AttributeSetInstance_ID(), Env.ZERO, reserved, Env.ZERO, get_TrxName()))
      throw new CyprusException(); 
    setQtyReserved(getQtyReserved().add(difference));
  }
  
  private int getM_Locator_ID(BigDecimal qty) {
    int M_Locator_ID = 0;
    int M_ASI_ID = getM_AttributeSetInstance_ID();
    if (M_ASI_ID != 0)
      M_Locator_ID = MStorage.getM_Locator_ID(getM_Warehouse_ID(), getM_Product_ID(), M_ASI_ID, qty, get_TrxName()); 
    if (M_Locator_ID == 0)
      M_Locator_ID = getM_Locator_ID(); 
    if (M_Locator_ID == 0) {
      MLocator locator = MWarehouse.get(getCtx(), getM_Warehouse_ID()).getDefaultLocator();
      if (locator != null)
        M_Locator_ID = locator.get_ID(); 
    } 
    return M_Locator_ID;
  }
  
  public String toString() {
    return String.valueOf(getClass().getSimpleName()) + "[" + get_ID() + 
      ", Product=" + getM_Product_ID() + 
      ", ComponentType=" + getComponentType() + 
      ",QtyBatch=" + getQtyBatch() + 
      ",QtyRequiered=" + getQtyRequiered() + 
      ",QtyScrap=" + getQtyScrap() + 
      "]";
  }
}

