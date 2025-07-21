package org.cyprusbrs.framework;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.compiere.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.Trx;

public class MStorageDetail extends X_M_StorageDetail {
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MStorageDetail.class);
  
  private boolean isBulkUpdate = false;
  
  private int m_M_Warehouse_ID;
  
  public MStorageDetail(MLocator locator, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type) {
    this(locator.getCtx(), 0, locator.get_TrxName());
    setClientOrg(locator);
    setM_Locator_ID(locator.getM_Locator_ID());
    setM_Product_ID(M_Product_ID);
    setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
    setQtyType(type.getValue());
    setQty(Env.ZERO);
  }
  
  public MStorageDetail(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type, Trx trx) {
    this(ctx, 0, trx.getTrxName());
    setM_Locator_ID(M_Locator_ID);
    setM_Product_ID(M_Product_ID);
    setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
    setQtyType(type.getValue());
    setQty(Env.ZERO);
  }
  
  private MStorageDetail(Properties ctx, int M_StorageDetail_ID, String trx) {
    super(ctx, M_StorageDetail_ID, trx);
    this.m_M_Warehouse_ID = 0;
  }
  
  public MStorageDetail(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.m_M_Warehouse_ID = 0;
  }
  
  @Override
protected boolean beforeSave(boolean newRecord) {
    if (is_ValueChanged("Qty") && isType(X_Ref_Quantity_Type.ON_HAND) && MCycleCountLock.lockExists(getCtx(), getM_Product_ID(), getM_Locator_ID(), get_Trx())) {
      s_log.saveError("Error", Msg.getMsg(getCtx(), "LocatorLocked"));
      return false;
    } 
    if (this.isBulkUpdate)
      return super.beforeSave(newRecord); 
    if (newRecord || (is_ValueChanged("Qty") && (isType(X_Ref_Quantity_Type.ON_HAND) || isType(X_Ref_Quantity_Type.DEDICATED) || isType(X_Ref_Quantity_Type.ALLOCATED) || isType(X_Ref_Quantity_Type.EXPECTED)))) {
      MWarehouse wh = MWarehouse.get(getCtx(), getM_Warehouse_ID());
      if (wh.isDisallowNegativeInv()) {
        CPreparedStatement cPreparedStatement = null;
        if (getQty().signum() < 0) {
          s_log.saveError("Error", Msg.getMsg(getCtx(), "NegativeInventoryDisallowed"));
          return false;
        } 
        BigDecimal qtyOnHand = Env.ZERO;
        BigDecimal qtyDedicated = Env.ZERO;
        BigDecimal qtyAllocated = Env.ZERO;
        String sql = "SELECT SUM(QtyOnHand),SUM(QtyDedicated),SUM(QtyAllocated) FROM M_Storage_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) WHERE s.M_Product_ID=? AND l.M_Warehouse_ID=? AND l.M_Locator_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
          cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
          cPreparedStatement.setInt(1, getM_Product_ID());
          cPreparedStatement.setInt(2, getM_Warehouse_ID());
          cPreparedStatement.setInt(3, getM_Locator_ID());
          rs = cPreparedStatement.executeQuery();
          if (rs.next()) {
            qtyOnHand = rs.getBigDecimal(1);
            if (rs.wasNull())
              qtyOnHand = Env.ZERO; 
            qtyDedicated = rs.getBigDecimal(2);
            if (rs.wasNull())
              qtyDedicated = Env.ZERO; 
            qtyAllocated = rs.getBigDecimal(3);
            if (rs.wasNull())
              qtyAllocated = Env.ZERO; 
          } 
        } catch (Exception e) {
          s_log.log(Level.SEVERE, sql, e);
        } finally {
          DB.close(rs);
          DB.close(cPreparedStatement);
          rs = null;
          cPreparedStatement = null;
        } 
        BigDecimal asiQtyOnHand = null;
        BigDecimal asiQtyDedicated = null;
        BigDecimal asiQtyAllocated = null;
        if (isType(X_Ref_Quantity_Type.ON_HAND)) {
          if (newRecord) {
            qtyOnHand = qtyOnHand.add(getQty());
          } else {
            qtyOnHand = qtyOnHand.add(getQty()).subtract((BigDecimal)get_ValueOld("Qty"));
          } 
          asiQtyOnHand = getQty();
          asiQtyAllocated = getQty(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.ALLOCATED, get_Trx());
          asiQtyDedicated = getQty(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.DEDICATED, get_Trx());
        } else if (isType(X_Ref_Quantity_Type.DEDICATED)) {
          if (newRecord) {
            qtyDedicated = qtyDedicated.add(getQty());
          } else {
            qtyDedicated = qtyDedicated.add(getQty()).subtract((BigDecimal)get_ValueOld("Qty"));
          } 
          asiQtyOnHand = getQty(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.ON_HAND, get_Trx());
          asiQtyAllocated = getQty(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.ALLOCATED, get_Trx());
          asiQtyDedicated = getQty();
        } else if (isType(X_Ref_Quantity_Type.ALLOCATED)) {
          if (newRecord) {
            qtyAllocated = qtyAllocated.add(getQty());
          } else {
            qtyAllocated = qtyAllocated.add(getQty()).subtract((BigDecimal)get_ValueOld("Qty"));
          } 
          asiQtyOnHand = getQty(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.ON_HAND, get_Trx());
          asiQtyAllocated = getQty();
          asiQtyDedicated = getQty(getCtx(), getM_Locator_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID(), X_Ref_Quantity_Type.DEDICATED, get_Trx());
        } 
        asiQtyOnHand = (asiQtyOnHand == null) ? Env.ZERO : asiQtyOnHand;
        asiQtyDedicated = (asiQtyDedicated == null) ? Env.ZERO : asiQtyDedicated;
        asiQtyAllocated = (asiQtyAllocated == null) ? Env.ZERO : asiQtyAllocated;
        if (qtyOnHand.compareTo(qtyDedicated.add(qtyAllocated)) < 0 || asiQtyOnHand.compareTo(asiQtyDedicated.add(asiQtyAllocated)) < 0) {
          s_log.saveError("Error", Msg.getMsg(getCtx(), "NegativeInventoryDisallowed"));
          return false;
        } 
      } 
    } 
    return super.beforeSave(newRecord);
  }
  
  public static MStorageDetail getForRead(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type, Trx trx) {
    return get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, type, false, trx);
  }
  
  public static MStorageDetail getForUpdate(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type, Trx trx) {
    return get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, type, true, trx);
  }
  
  private static MStorageDetail get(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type, boolean forUpdate, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    MStorageDetail retValue = null;
    String sql = "SELECT * FROM M_StorageDetail WHERE M_Locator_ID=? AND M_Product_ID=? AND QtyType=? AND ";
    if (M_AttributeSetInstance_ID == 0) {
      sql = sql + "(M_AttributeSetInstance_ID=? OR M_AttributeSetInstance_ID IS NULL)";
    } else {
      sql = sql + "M_AttributeSetInstance_ID=?";
    } 
    if (forUpdate)
      sql = sql + " FOR UPDATE "; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setInt(1, M_Locator_ID);
      cPreparedStatement.setInt(2, M_Product_ID);
      cPreparedStatement.setString(3, type.getValue());
      cPreparedStatement.setInt(4, M_AttributeSetInstance_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = new MStorageDetail(ctx, rs, trx.getTrxName()); 
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    if (retValue == null) {
      s_log.fine("Not Found - M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } else {
      s_log.fine("M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } 
    return retValue;
  }
  
  public static BigDecimal getQty(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    BigDecimal retValue = null;
    String sql = "SELECT Qty FROM M_StorageDetail WHERE M_Locator_ID=? AND M_Product_ID=? AND QtyType=? AND ";
    if (M_AttributeSetInstance_ID == 0) {
      sql = sql + "(M_AttributeSetInstance_ID=? OR M_AttributeSetInstance_ID IS NULL)";
    } else {
      sql = sql + "M_AttributeSetInstance_ID=?";
    } 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setInt(1, M_Locator_ID);
      cPreparedStatement.setInt(2, M_Product_ID);
      cPreparedStatement.setString(3, type.getValue());
      cPreparedStatement.setInt(4, M_AttributeSetInstance_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = rs.getBigDecimal(1); 
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    if (retValue == null) {
      s_log.fine("Not Found - M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } else {
      s_log.fine("M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } 
    return retValue;
  }
  
  public static Collection<MStorageDetail> getMultipleForUpdate(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, Collection<X_Ref_Quantity_Type> types, Trx trx) {
    return getMultiple(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, types, true, trx);
  }
  
  public static Collection<MStorageDetail> getMultipleForRead(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, Collection<X_Ref_Quantity_Type> types, Trx trx) {
    return getMultiple(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, types, false, trx);
  }
  
  private static Collection<MStorageDetail> getMultiple(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, Collection<X_Ref_Quantity_Type> types, boolean forUpdate, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    Collection<MStorageDetail> retValue = new ArrayList<MStorageDetail>();
    StringBuilder qtyTypesSql = new StringBuilder(" ");
    if (!types.isEmpty()) {
      qtyTypesSql.append("AND QtyType in (");
      for (X_Ref_Quantity_Type type : types) {
        qtyTypesSql.append("'");
        qtyTypesSql.append(type.getValue());
        qtyTypesSql.append("',");
      } 
      qtyTypesSql.deleteCharAt(qtyTypesSql.length() - 1);
      qtyTypesSql.append("') ");
    } 
    String sql = "SELECT * FROM M_StorageDetail WHERE M_Locator_ID=? AND M_Product_ID=? AND ";
    if (M_AttributeSetInstance_ID == 0) {
      sql = sql + "(M_AttributeSetInstance_ID=? OR M_AttributeSetInstance_ID IS NULL)";
    } else {
      sql = sql + "M_AttributeSetInstance_ID=?";
    } 
    if (qtyTypesSql.length() > 1)
      sql = sql + qtyTypesSql.toString(); 
    if (forUpdate)
      sql = sql + " FOR UPDATE "; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setInt(1, M_Locator_ID);
      cPreparedStatement.setInt(2, M_Product_ID);
      cPreparedStatement.setInt(3, M_AttributeSetInstance_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        retValue.add(new MStorageDetail(ctx, rs, trx.getTrxName())); 
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
    if (retValue.isEmpty()) {
      s_log.fine("Not Found - M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } else {
      s_log.fine("M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } 
    return retValue;
  }
  
  public static MStorageDetail getCreate(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, X_Ref_Quantity_Type type, Trx trx) {
    if (M_Locator_ID == 0)
      throw new IllegalArgumentException("M_Locator_ID=0"); 
    if (M_Product_ID == 0)
      throw new IllegalArgumentException("M_Product_ID=0"); 
    MStorageDetail retValue = getForUpdate(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, type, trx);
    if (retValue != null)
      return retValue; 
    MLocator locator = new MLocator(ctx, M_Locator_ID, trx.getTrxName());
    if (locator.get_ID() != M_Locator_ID)
      throw new IllegalArgumentException("Not found M_Locator_ID=" + M_Locator_ID); 
    retValue = new MStorageDetail(locator, M_Product_ID, M_AttributeSetInstance_ID, type);
    retValue.save(trx.getTrxName());
    s_log.fine("New " + retValue);
    return retValue;
  }
  
  public static boolean set(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int reservationAttributeSetInstance_ID, BigDecimal newQty, X_Ref_Quantity_Type type, Trx trx) {
	  CPreparedStatement cPreparedStatement = null;
	  MStorageDetail retValue = null;
    String sql = "UPDATE M_StorageDetail SET QTY=?WHERE M_Locator_ID=? AND M_Product_ID=? AND QtyType=? AND ";
    if (M_AttributeSetInstance_ID == 0) {
      sql = sql + "(M_AttributeSetInstance_ID=? OR M_AttributeSetInstance_ID IS NULL)";
    } else {
      sql = sql + "M_AttributeSetInstance_ID=?";
    } 
    PreparedStatement pstmt = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setBigDecimal(1, newQty);
      cPreparedStatement.setInt(2, M_Locator_ID);
      cPreparedStatement.setInt(3, M_Product_ID);
      cPreparedStatement.setString(4, type.getValue());
      cPreparedStatement.setInt(5, M_AttributeSetInstance_ID);
      cPreparedStatement.executeUpdate();
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(cPreparedStatement);
      cPreparedStatement = null;
    } 
  //  CPreparedStatement cPreparedStatement = null;
    if (retValue == null) {
      s_log.fine("Not Found - M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } else {
      s_log.fine("M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID);
    } 
    return true;
  }
  
  public static List<MStorageDetail> getAll(Properties ctx, int M_Product_ID, int M_Locator_ID, X_Ref_Quantity_Type qtyType, boolean isFIFO, boolean withASI, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    List<MStorageDetail> list = new ArrayList<MStorageDetail>();
    String sql = "select s.* from M_STORAGEDETAIL  s INNER JOIN M_STORAGEDETAIL  t on ( s.AD_CLIENT_ID = t.AD_CLIENT_ID and s.AD_ORG_ID    = t.AD_ORG_ID and s.M_PRODUCT_ID = t.M_PRODUCT_ID and s.M_LOCATOR_ID = t.M_LOCATOR_ID) where s.QTYTYPE = ? and s.M_PRODUCT_ID = ? and s.M_LOCATOR_ID = ? and t.QTYTYPE = 'H' ";
    if (withASI) {
      sql = sql + "and t.QTY > 0 and s.M_ATTRIBUTESETINSTANCE_ID > 0 ";
    } else {
      sql = sql + "and t.QTY <> 0 ";
    } 
    sql = sql + " order by s.M_ATTRIBUTESETINSTANCE_ID ";
    if (!isFIFO)
      sql = sql + " DESC"; 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setString(1, qtyType.getValue());
      cPreparedStatement.setInt(2, M_Product_ID);
      cPreparedStatement.setInt(3, M_Locator_ID);
      rs = cPreparedStatement.executeQuery();
      while (rs.next())
        list.add(new MStorageDetail(ctx, rs, trx.getTrxName())); 
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return list;
  }
  
  public static Map<X_Ref_Quantity_Type, MStorageDetail> getAllForUpdate(Properties ctx, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, Set<X_Ref_Quantity_Type> updateQtyTypes, Trx trx) {
    Map<X_Ref_Quantity_Type, MStorageDetail> storageDetails = new HashMap<X_Ref_Quantity_Type, MStorageDetail>();
    for (X_Ref_Quantity_Type qtyType : X_Ref_Quantity_Type.values()) {
      MStorageDetail store = get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, qtyType, (updateQtyTypes == null) ? false : updateQtyTypes.contains(qtyType), trx);
      storageDetails.put(qtyType, store);
    } 
    return storageDetails;
  }
  
  public static boolean add(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int reservationAttributeSetInstance_ID, BigDecimal diffQty, X_Ref_Quantity_Type type, Trx trx) {
    StringBuffer diffText = new StringBuffer("(");
    MStorageDetail storage = null;
    storage = getCreate(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, type, trx);
    if (storage.getM_Locator_ID() != M_Locator_ID && storage.getM_Product_ID() != M_Product_ID && storage.getM_AttributeSetInstance_ID() != M_AttributeSetInstance_ID) {
      s_log.severe("No Storage found - M_Locator_ID=" + M_Locator_ID + ",M_Product_ID=" + M_Product_ID + ",ASI=" + M_AttributeSetInstance_ID);
      return false;
    } 
    MStorageDetail storageASI = null;
    if (M_AttributeSetInstance_ID != reservationAttributeSetInstance_ID && (type == X_Ref_Quantity_Type.RESERVED || type == X_Ref_Quantity_Type.ORDERED)) {
      int reservationM_Locator_ID = M_Locator_ID;
      if (reservationAttributeSetInstance_ID == 0) {
        MWarehouse wh = MWarehouse.get(ctx, M_Warehouse_ID);
        reservationM_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
      } 
      storageASI = get(ctx, reservationM_Locator_ID, M_Product_ID, reservationAttributeSetInstance_ID, type, true, trx);
      if (storageASI == null) {
        MProduct product = MProduct.get(ctx, M_Product_ID);
        int xM_Locator_ID = MProductLocator.getFirstM_Locator_ID(product, M_Warehouse_ID);
        if (xM_Locator_ID == 0) {
          MWarehouse wh = MWarehouse.get(ctx, M_Warehouse_ID);
          xM_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
        } 
        storageASI = getCreate(ctx, xM_Locator_ID, M_Product_ID, reservationAttributeSetInstance_ID, type, trx);
      } 
    } 
    boolean changed = false;
    if (diffQty != null && diffQty.signum() != 0) {
      if (storageASI == null) {
        storage.setQty(storage.getQty().add(diffQty));
      } else {
        storageASI.setQty(storageASI.getQty().add(diffQty));
      } 
      diffText.append(type.toString()).append("=").append(diffQty);
      changed = true;
    } 
    if (changed) {
      diffText.append(") -> ").append(storage.toString());
      s_log.fine(diffText.toString());
      if (storageASI != null)
        storageASI.save(trx.getTrxName()); 
      return storage.save(trx.getTrxName());
    } 
    return true;
  }
  
  public static void transfer(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int reservationAttributeSetInstance_ID, BigDecimal diffQty, X_Ref_Quantity_Type subtractFromType, X_Ref_Quantity_Type addToType, Trx trx) {
    add(ctx, M_Warehouse_ID, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, reservationAttributeSetInstance_ID, diffQty.negate(), subtractFromType, trx);
    add(ctx, M_Warehouse_ID, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, reservationAttributeSetInstance_ID, diffQty.negate(), addToType, trx);
  }
  
  public static BigDecimal getForUpdate(Properties ctx, int M_Warehouse_ID, int M_Locator_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int reservationAttributeSetInstance_ID, X_Ref_Quantity_Type type, Trx trx) {
    return null;
  }
  
  public int getM_Warehouse_ID() {
    if (this.m_M_Warehouse_ID == 0) {
      MLocator loc = MLocator.get(getCtx(), getM_Locator_ID());
      this.m_M_Warehouse_ID = loc.getM_Warehouse_ID();
    } 
    return this.m_M_Warehouse_ID;
  }
  
  public static List<MStorage.VO> getWarehouse(Properties ctx, int M_Warehouse_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int M_AttributeSet_ID, boolean allAttributeInstances, Timestamp minGuaranteeDate, boolean FiFo, boolean allocationCheck, int M_SourceZone_ID, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    if (M_Warehouse_ID == 0 || M_Product_ID == 0)
      return null; 
    if (M_AttributeSet_ID == 0) {
      allAttributeInstances = true;
    } else {
      MAttributeSet mas = MAttributeSet.get(ctx, M_AttributeSet_ID);
      if (!mas.isInstanceAttribute())
        allAttributeInstances = true; 
    } 
    List<MStorage.VO> list = new ArrayList<MStorage.VO>();
    String sql = "SELECT s.M_Product_ID,s.M_Locator_ID,s.M_AttributeSetInstance_ID,COALESCE(SUM(CASE WHEN QtyType LIKE 'H' THEN Qty ELSE 0 END),0) QtyOnhand,COALESCE(SUM(CASE WHEN QtyType LIKE 'D' THEN Qty ELSE 0 END),0) QtyDedicated,COALESCE(SUM(CASE WHEN QtyType LIKE 'A' THEN Qty ELSE 0 END),0) QtyAllocated FROM M_StorageDetail s INNER JOIN M_Locator l ON (l.M_Locator_ID=s.M_Locator_ID) WHERE l.M_Warehouse_ID=? AND s.M_Product_ID=? AND COALESCE(s.M_AttributeSetInstance_ID,0)=? ";
    if (allocationCheck)
      sql = sql + "AND l.IsAvailableForAllocation='Y' "; 
    if (M_SourceZone_ID != 0)
      sql = sql + "AND l.M_Locator_ID IN  (SELECT M_Locator_ID FROM M_ZoneLocator WHERE M_Zone_ID = ? ) "; 
    sql = sql + "GROUP BY l.PriorityNo, s.M_Product_ID,s.M_Locator_ID,s.M_AttributeSetInstance_ID ORDER BY l.PriorityNo DESC, M_AttributeSetInstance_ID";
    if (!FiFo)
      sql = sql + " DESC"; 
    if (allAttributeInstances) {
      sql = "SELECT s.M_Product_ID,s.M_Locator_ID,s.M_AttributeSetInstance_ID,COALESCE(SUM(CASE WHEN QtyType LIKE 'H' THEN Qty ELSE 0 END),0) QtyOnhand,COALESCE(SUM(CASE WHEN QtyType LIKE 'D' THEN Qty ELSE 0 END),0) QtyDedicated,COALESCE(SUM(CASE WHEN QtyType LIKE 'A' THEN Qty ELSE 0 END),0) QtyAllocated FROM M_StorageDetail s INNER JOIN M_Locator l ON (l.M_Locator_ID=s.M_Locator_ID) LEFT OUTER JOIN M_AttributeSetInstance asi ON (s.M_AttributeSetInstance_ID=asi.M_AttributeSetInstance_ID) WHERE l.M_Warehouse_ID=? AND s.M_Product_ID=? ";
      if (allocationCheck)
        sql = sql + "AND l.IsAvailableForAllocation='Y' "; 
      if (M_SourceZone_ID != 0)
        sql = sql + "AND l.M_Locator_ID IN  (SELECT M_Locator_ID FROM M_ZoneLocator WHERE M_Zone_ID = ? ) "; 
      if (minGuaranteeDate != null) {
        sql = sql + "AND (asi.GuaranteeDate IS NULL OR asi.GuaranteeDate>?) GROUP BY asi.GuaranteeDate, l.PriorityNo, s.M_Product_ID, s.M_Locator_ID, s.M_AttributeSetInstance_ID ORDER BY asi.GuaranteeDate, l.PriorityNo DESC, M_AttributeSetInstance_ID";
      } else {
        sql = sql + "GROUP BY l.PriorityNo, s.M_Product_ID, s.M_Locator_ID, s.M_AttributeSetInstance_ID ORDER BY l.PriorityNo DESC, s.M_AttributeSetInstance_ID";
      } 
      if (!FiFo)
        sql = sql + " DESC"; 
      sql = sql + ", COALESCE(SUM(CASE WHEN QtyType LIKE 'H' THEN Qty ELSE 0 END),0) DESC";
    } 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      int index = 1;
      cPreparedStatement.setInt(index++, M_Warehouse_ID);
      cPreparedStatement.setInt(index++, M_Product_ID);
      if (M_SourceZone_ID != 0)
        cPreparedStatement.setInt(index++, M_SourceZone_ID); 
      if (!allAttributeInstances) {
        cPreparedStatement.setInt(index++, M_AttributeSetInstance_ID);
      } else if (minGuaranteeDate != null) {
        cPreparedStatement.setTimestamp(index++, minGuaranteeDate);
      } 
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        index = 1;
        int rs_M_Product_ID = rs.getInt(1);
        int rs_M_Locator_ID = rs.getInt(index++);
        int rs_M_AttributeSetInstance_ID = rs.getInt(index++);
        BigDecimal rs_QtyOnhand = rs.getBigDecimal(index++);
        BigDecimal rs_QtyDedicated = rs.getBigDecimal(index++);
        BigDecimal rs_QtyAllocated = rs.getBigDecimal(index++);
        list.add(new MStorage.VO(rs_M_Product_ID, rs_M_Locator_ID, rs_M_AttributeSetInstance_ID, rs_QtyOnhand, rs_QtyDedicated, rs_QtyAllocated));
      } 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
    return list;
  }
  
  public static List<MStorage.VO> getWarehouse(Properties ctx, int M_Warehouse_ID, int M_Product_ID, int M_AttributeSetInstance_ID, int M_AttributeSet_ID, boolean allAttributeInstances, Timestamp minGuaranteeDate, boolean FiFo, Trx trx) {
    return getWarehouse(ctx, M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, M_AttributeSet_ID, allAttributeInstances, minGuaranteeDate, FiFo, false, 0, trx);
  }
  
  public MInOutLine getInOutLineOf() {
    CPreparedStatement cPreparedStatement = null;
    if (getM_AttributeSetInstance_ID() == 0)
      return null; 
    MInOutLine retValue = null;
    String sql = "SELECT * FROM M_InOutLine line WHERE M_AttributeSetInstance_ID=? OR EXISTS (SELECT 1 FROM M_InOutLineMA ma WHERE line.M_InOutLine_ID = ma.M_InOutLine_ID AND M_AttributeSetInstance_ID=?)";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
      cPreparedStatement.setInt(1, getM_AttributeSetInstance_ID());
      cPreparedStatement.setInt(2, getM_AttributeSetInstance_ID());
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        retValue = new MInOutLine(getCtx(), rs, get_TrxName()); 
    } catch (SQLException ex) {
      s_log.log(Level.SEVERE, sql, ex);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null;
      cPreparedStatement = null;
    } 
    return retValue;
  }
  
  @Override
public String toString() {
    return super.toString() + " Qty = " + getQty();
  }
  
  public boolean isType(X_Ref_Quantity_Type refType) {
    return getQtyType().equals(refType.getValue());
  }
  
  public void setIsBulkUpdate(boolean bulkUpdate) {
    this.isBulkUpdate = bulkUpdate;
  }
  
  public boolean isBulkUpdate() {
    return this.isBulkUpdate;
  }
}

