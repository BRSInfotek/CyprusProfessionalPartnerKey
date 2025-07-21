package org.cyprusbrs.framework;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.CycleCountLockKey;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.util.CCache;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.compiere.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Trx;

public class MCycleCountLock extends X_M_CycleCountLock {
	
	static PO po;
  private static final long serialVersionUID = 1L;
  
  private static CLogger s_log = CLogger.getCLogger(MCycleCountLock.class);
  
  private static final CCache<CycleCountLockKey, MCycleCountLock> s_cache = new CCache("M_CycleCountLock", 1000);
  
  public MCycleCountLock(Properties ctx, int MCycleCountLock_ID, String trx) {
    super(ctx, MCycleCountLock_ID, trx);
  }
  
  public static MCycleCountLock getLock(Properties ctx, MInventory inv, int M_Product_ID, int M_Locator_ID, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    MCycleCountLock lock = null;
    String sql = " SELECT M_CycleCountLock_ID FROM M_CycleCountLock  WHERE M_Inventory_ID = ?  AND M_Product_ID = ?  AND M_Locator_ID = ?  AND IsLocked = 'Y' ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setInt(1, inv.getM_Inventory_ID());
      cPreparedStatement.setInt(2, M_Product_ID);
      cPreparedStatement.setInt(3, M_Locator_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next())
        lock = new MCycleCountLock(ctx, rs.getInt(1), trx.getTrxName()); 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return lock;
  }
  
  public MCycleCountLock(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
  }
  
  public static boolean lock(MInventory inv) {
    boolean retVal = true;
    ArrayList<MCycleCountLock> list = new ArrayList<MCycleCountLock>();
//    if (inv.getIsLocked() != null && inv.getIsLocked().equals("Y")) {
//      s_log.log(Level.INFO, "Already Locked");
//      return retVal;
//    } 
    MInventoryLine[] lines = inv.getLines(true);
    if (lines == null || lines.length == 0) {
      s_log.log(Level.INFO, "Lines not found in the document");
      return retVal;
    } 
    for (MInventoryLine line : lines) {
    //  if (line.getM_ABCAnalysisGroup_ID() != 0)
        if (!lockExists(inv.getCtx(), line.getM_Product_ID(), line.getM_Locator_ID(), inv.get_Trx())) {
          MCycleCountLock lock = new MCycleCountLock(inv.getCtx(), 0, inv.get_TrxName());
          lock.setM_Inventory_ID(inv.getM_Inventory_ID());
          lock.setM_Product_ID(line.getM_Product_ID());
          lock.setM_Locator_ID(line.getM_Locator_ID());
          lock.setM_Warehouse_ID(inv.getM_Warehouse_ID());
          lock.setIsLocked(true);
          lock.setIsActive(true);
          lock.setAD_Client_ID(line.getAD_Client_ID());
          lock.setAD_Org_ID(line.getAD_Org_ID());
          list.add(lock);
          CycleCountLockKey key = new CycleCountLockKey(line.getM_Product_ID(), line.getM_Locator_ID());
          s_cache.put(key, lock);
        } else {
          s_log.log(Level.INFO, "Already Locked");
        }  
    } 
//    if (saveAll(inv.get_Trx(), list)) {
    if (po.save(inv.get_TrxName())) {

//      inv.setIsLocked("Y");
//      inv.setUnLock("N");
      retVal = true;
    } else {
      s_log.log(Level.SEVERE, "Unable to Save the Changes");
      retVal = false;
      s_cache.reset();
    } 
    return retVal;
  }
  
  public static boolean unLock(MInventory inv, MInventoryLine invLine) {
    boolean retVal = true;
    ArrayList<MCycleCountLock> list = new ArrayList<MCycleCountLock>();
    MInventoryLine[] lines = inv.getLines(true);
    for (MInventoryLine line : lines) {
      if (invLine == null || invLine.getM_InventoryLine_ID() == line.getM_InventoryLine_ID())
       // if (line.getM_ABCAnalysisGroup_ID() != 0) {
      {
          MCycleCountLock lock = getLock(inv.getCtx(), inv, line.getM_Product_ID(), line.getM_Locator_ID(), inv.get_Trx());
          if (lock != null) {
            lock.setIsLocked(false);
            list.add(lock);
            CycleCountLockKey key = new CycleCountLockKey(line.getM_Product_ID(), line.getM_Locator_ID());
            s_cache.remove(key);
          } else {
            s_log.log(Level.INFO, "Lock Not found");
          } 
        }  
    }
  //  } 
//    if (saveAll(inv.get_Trx(), list)) {
    if (po.save(inv.get_TrxName())) {
//      inv.setIsLocked("N");
//      inv.setUnLock("Y");
      retVal = true;
    } else {
      s_log.log(Level.SEVERE, "Unable to Save the Changes");
      retVal = false;
      s_cache.reset();
    } 
    return retVal;
  }
  
  public static boolean lockExists(Properties ctx, int M_Product_ID, int M_Locator_ID, Trx trx) {
    CPreparedStatement cPreparedStatement = null;
    CycleCountLockKey key = new CycleCountLockKey(M_Product_ID, M_Locator_ID);
    MCycleCountLock lock = s_cache.get(ctx);
    if (lock != null)
      return true; 
    boolean retVal = false;
    String sql = " SELECT * FROM M_CycleCountLock WHERE M_Product_ID = ? AND M_Locator_ID = ? AND IsLocked = 'Y' ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, trx.getTrxName());
      cPreparedStatement.setInt(1, M_Product_ID);
      cPreparedStatement.setInt(2, M_Locator_ID);
      rs = cPreparedStatement.executeQuery();
      if (rs.next()) {
        MCycleCountLock newLock = new MCycleCountLock(ctx, rs, trx.getTrxName());
        s_cache.put(key, newLock);
        retVal = true;
      } else {
        retVal = false;
      } 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql.toString());
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    return retVal;
  }
  
  public static void delete(MInventory inv, MInventoryLine line) {
    CPreparedStatement cPreparedStatement = null;
    MCycleCountLock lock = null;
    String sql = " SELECT M_CycleCountLock_ID FROM M_CycleCountLock  WHERE M_Inventory_ID = ?  AND M_Product_ID = ?  AND M_Locator_ID = ? ";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql, inv.get_TrxName());
      cPreparedStatement.setInt(1, inv.getM_Inventory_ID());
      cPreparedStatement.setInt(2, line.getM_Product_ID());
      cPreparedStatement.setInt(3, line.getM_Locator_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        lock = new MCycleCountLock(inv.getCtx(), rs.getInt(1), inv.get_TrxName());
        if (lock != null)
          lock.delete(true); 
      } 
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql);
    } finally {
      DB.close(rs);
      DB.close(cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
  }
}

