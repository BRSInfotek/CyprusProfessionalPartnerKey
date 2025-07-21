package org.cyprus.wms.rule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import org.cyprus.intf.WMSRuleIntf;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;

public class PickPTO implements WMSRuleIntf {
  private static final CLogger s_log = CLogger.getCLogger(PickPTO.class);
  
  public MLocator[] getValidLocators(Properties ctx, int M_Warehouse_ID, int M_Zone_ID, int M_Product_ID, int C_OrderLine_ID, String trx) {
    if (M_Warehouse_ID == 0 || M_Product_ID == 0 || C_OrderLine_ID == 0)
      return null; 
    ArrayList<MLocator> list = new ArrayList<MLocator>();
    MOrderLine oLine = new MOrderLine(ctx, C_OrderLine_ID, trx);
    MOrderLine poLine = (MOrderLine) oLine.getRef_OrderLine();
    if (poLine == null)
      return null; 
    MInOutLine[] ioLines = MInOutLine.getOfOrderLine(ctx, poLine.getC_OrderLine_ID(), "", trx);
    for (MInOutLine io : ioLines) {
      CPreparedStatement cPreparedStatement;
      ResultSet rs;
      int M_AttributeSetInstance_ID = io.getM_AttributeSetInstance_ID();
      String sql = "SELECT l.M_Locator_ID ";
      sql = sql + "FROM M_Locator l ";
      sql = sql + "LEFT OUTER JOIN WMS_ZoneLocator zl ON (zl.M_Locator_ID=l.M_Locator_ID) LEFT OUTER JOIN WMS_Zone z ON (z.WMS_Zone_ID=zl.WMS_Zone_ID AND z.IsStatic='Y') ";
      sql = sql + "WHERE l.M_Warehouse_ID=? AND l.IsAvailableForAllocation='Y' AND l.IsActive='Y' ";
      sql = sql + "AND EXISTS (SELECT 1 FROM M_StorageDetail s WHERE s.M_Locator_ID = L.M_Locator_ID AND s.M_Product_ID=? AND s.M_AttributeSetInstance_ID=? AND s.QtyType='H' AND s.Qty>0) ";
      if (M_Zone_ID != 0)
        sql = sql + "AND l.M_Locator_ID IN (SELECT M_Locator_ID FROM WMS_ZoneLocator zl  WHERE zl.WMS_Zone_ID=? ) "; 
      sql = sql + " ORDER BY ";
      if (M_Zone_ID == 0)
        sql = sql + "z.PickingSeqNo, "; 
      sql = sql + " l.PickingSeqNo ";
      PreparedStatement pstmt = null;
    } 
    MLocator[] retValue = new MLocator[list.size()];
    list.toArray(retValue);
    return retValue;
  }
//@Anil20122021
  //Have to check at run time
//@Override
public MLocator[] getValidLocators(Properties ctx, int M_Warehouse_ID, int M_Zone_ID, int M_Product_ID, String trx) {
	// TODO Auto-generated method stub
	return null;
}
}

