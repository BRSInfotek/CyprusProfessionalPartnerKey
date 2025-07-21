package org.cyprus.wms.model;

//import com.cyprusbrs.client.SysEnv;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprus.wms.util.MWMSMContext;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductLocator;
import org.cyprusbrs.framework.MUOMConversion;
//import org.cyprus.wms.util.MMContext;
import org.cyprusbrs.util.CLogger;
import org.cyprusbrs.util.CPreparedStatement;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;

public class MWMSMMStrategy extends X_WMS_MMStrategy {
  private static final CLogger log = CLogger.getCLogger(MWMSMMStrategy.class);
  
  private static final long serialVersionUID = 1L;
  
  private MWMSMMRule[] m_rules;
  
  public MWMSMMStrategy(Properties ctx, int M_MMStrategy_ID, String trx) {
    super(ctx, M_MMStrategy_ID, trx);
    this.m_rules = null;
  }
  
  public MWMSMMStrategy(Properties ctx, ResultSet rs, String trx) {
    super(ctx, rs, trx);
    this.m_rules = null;
  }
  
  public MWMSMMRule[] getRules() throws Exception {
    CPreparedStatement cPreparedStatement=null;
    if (this.m_rules != null && this.m_rules.length > 0)
      return this.m_rules; 
    ArrayList<MWMSMMRule> list = new ArrayList<MWMSMMRule>();
  //  String sql = "SELECT * FROM M_MMStrategyLine WHERE M_MMStrategy_ID=? AND IsActive='Y'  ORDER BY SeqNo";
    String sql = "SELECT * FROM WMS_MMStrategyLine WHERE WMS_MMStrategy_ID=? AND IsActive='Y'  ORDER BY SeqNo";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      cPreparedStatement = DB.prepareStatement(sql.toString(), get_TrxName());
      cPreparedStatement.setInt(1, getWMS_MMStrategy_ID());
      rs = cPreparedStatement.executeQuery();
      while (rs.next()) {
        MWMSMMStrategyLine sl = new MWMSMMStrategyLine(getCtx(), rs, get_TrxName());
        MWMSMMRule rule = sl.getRule();
        if (rule.isActive())
          list.add(sl.getRule()); 
      } 
    } catch (SQLException e) {
      log.log(Level.SEVERE, sql.toString(), e);
      throw new Exception(Msg.translate(getCtx(), "SQLException"));
    } finally {
      DB.close(rs);
      DB.close((Statement)cPreparedStatement);
      rs = null; cPreparedStatement = null;
    } 
    this.m_rules = new MWMSMMRule[list.size()];
    list.toArray(this.m_rules);
    return this.m_rules;
  }
  
  public BigDecimal executePickStrategy(MWMSMContext mmContext, int M_Product_ID, int M_AttributeSetInstance_ID, BigDecimal demandQty, int C_UOM_ID, int C_Order_ID, int C_OrderLine_ID, int M_WorkOrderOpeComponent_ID, int M_SourceZone_ID, int destM_Locator_ID, int C_DocType_ID, String docAction, int C_WaveLine_ID) throws Exception {
	  MWMSMMRule[] rules = getRules();
    BigDecimal qtyToPick = demandQty;
    for (MWMSMMRule rule : rules) {
      MLocator[] locators = rule.getValidLocators(M_Product_ID, C_OrderLine_ID);
      for (MLocator element : locators) {
        CPreparedStatement cPreparedStatement = null;
        int M_Locator_ID = element.getM_Locator_ID();
        if (M_Locator_ID == destM_Locator_ID)
          continue; 
        if (M_SourceZone_ID != 0 && !MWMSZone.isLocatorInZone(getCtx(), M_SourceZone_ID, M_Locator_ID))
          continue; 
        BigDecimal qtyOnHand = BigDecimal.ZERO;
        BigDecimal qtyDedicated = BigDecimal.ZERO;
        BigDecimal qtyAllocated = BigDecimal.ZERO;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT SUM(QtyOnHand),SUM(QtyDedicated),SUM(QtyAllocated) FROM M_STORAGE_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) WHERE s.M_Product_ID=? AND l.M_Warehouse_ID=? AND l.M_Locator_ID=?";
        try {
          cPreparedStatement = DB.prepareStatement(sql, get_TrxName());
          cPreparedStatement.setInt(1, M_Product_ID);
          cPreparedStatement.setInt(2, getM_Warehouse_ID());
          cPreparedStatement.setInt(3, M_Locator_ID);
          rs = cPreparedStatement.executeQuery();
          if (rs.next()) {
            qtyOnHand = rs.getBigDecimal(1);
            if (qtyOnHand == null)
              qtyOnHand = BigDecimal.ZERO; 
            qtyDedicated = rs.getBigDecimal(2);
            if (qtyDedicated == null)
              qtyDedicated = BigDecimal.ZERO; 
            qtyAllocated = rs.getBigDecimal(3);
            if (qtyAllocated == null)
              qtyAllocated = BigDecimal.ZERO; 
          } 
        } catch (SQLException e) {
          log.log(Level.SEVERE, sql, e);
          throw new Exception(Msg.translate(getCtx(), "SQLException"));
        } finally {
          DB.close(rs);
          DB.close((Statement)cPreparedStatement);
          rs = null; cPreparedStatement = null;
        } 
        BigDecimal qtyAvailable = qtyOnHand.subtract(qtyDedicated).subtract(qtyAllocated);
        if (qtyAvailable.compareTo(BigDecimal.ZERO) <= 0)
          continue; 
        BigDecimal qtyEntered = BigDecimal.ZERO;
        if (qtyToPick.compareTo(qtyAvailable) <= 0) {
          qtyEntered = qtyToPick;
        } else {
          if (!isAllowSplit())
            continue; 
          qtyEntered = qtyAvailable;
        } 
        int picking_UOM_ID = element.getPicking_UOM_ID();
        if (!rule.isMaintainUOMIntegrity())
          picking_UOM_ID = C_UOM_ID; 
        BigDecimal roundedQty = qtyEntered;
        if (picking_UOM_ID != C_UOM_ID) {
          BigDecimal pickingUOMRate = MUOMConversion.getProductRateFrom(getCtx(), M_Product_ID, picking_UOM_ID);
          if (pickingUOMRate == null || pickingUOMRate.compareTo(BigDecimal.ZERO) <= 0)
            continue; 
          BigDecimal qtyPUOM = qtyEntered.divide(pickingUOMRate, 1);
          if (qtyPUOM.signum() <= 0)
            continue; 
          roundedQty = qtyPUOM.multiply(pickingUOMRate);
          if (qtyEntered.compareTo(roundedQty) != 0 && !isAllowSplit())
            continue; 
          qtyEntered = qtyPUOM;
        } 
        mmContext.addTask(getM_Warehouse_ID(), M_Locator_ID, destM_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, C_Order_ID, C_OrderLine_ID, M_WorkOrderOpeComponent_ID, qtyEntered, picking_UOM_ID, C_DocType_ID, C_WaveLine_ID, 0);
        qtyToPick = qtyToPick.subtract(roundedQty);
        if (qtyToPick.compareTo(BigDecimal.ZERO) <= 0)
          break; 
        continue;
      } 
      if (qtyToPick.compareTo(BigDecimal.ZERO) <= 0)
        break; 
    } 
    return qtyToPick;
  }
  
  public BigDecimal executePutawayStrategy(MWMSMContext mmContext, int M_Product_ID, int M_AttributeSetInstance_ID, BigDecimal receivedQty, int receipt_UOM_ID, int M_InOutLine_ID, int C_Order_ID, int C_OrderLine_ID, int M_DestZone_ID, int srcM_Locator_ID, int C_DocType_ID, String docAction) {
	//Have to check at run time @Anil 19122021
	  return receivedQty;
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual getRules : ()[Lorg/cyprusbrs/cwms/model/MWMSMMRule;
    //   4: astore #13
    //   6: aload #4
    //   8: astore #14
    //   10: aload_0
    //   11: invokevirtual getCtx : ()Lorg/cyprusbrs/util/Ctx;
    //   14: iload_2
    //   15: invokestatic get : (Lorg/cyprusbrs/util/Ctx;I)Lorg/cyprusbrs/model/MProduct;
    //   18: astore #15
    //   20: aload #15
    //   22: invokevirtual getC_UOM_ID : ()I
    //   25: istore #16
    //   27: getstatic java/math/BigDecimal.ONE : Ljava/math/BigDecimal;
    //   30: astore #17
    //   32: iload #5
    //   34: iload #16
    //   36: if_icmpeq -> 77
    //   39: aload_0
    //   40: invokevirtual getCtx : ()Lorg/cyprusbrs/util/Ctx;
    //   43: iload_2
    //   44: iload #5
    //   46: invokestatic getProductRateFrom : (Lorg/cyprusbrs/util/Ctx;II)Ljava/math/BigDecimal;
    //   49: astore #17
    //   51: aload #17
    //   53: ifnull -> 67
    //   56: aload #17
    //   58: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   61: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   64: ifgt -> 77
    //   67: new java/lang/IllegalStateException
    //   70: dup
    //   71: ldc 'Could not find conversion rate'
    //   73: invokespecial <init> : (Ljava/lang/String;)V
    //   76: athrow
    //   77: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   80: new java/lang/StringBuilder
    //   83: dup
    //   84: invokespecial <init> : ()V
    //   87: ldc 'qtyToPutaway'
    //   89: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: aload #14
    //   94: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   97: ldc ' Product UOM : '
    //   99: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: iload #16
    //   104: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   107: ldc ' Receipt UOM : '
    //   109: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: iload #5
    //   114: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   117: ldc ' Receipt UOM Rate : '
    //   119: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: aload #17
    //   124: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   127: invokevirtual toString : ()Ljava/lang/String;
    //   130: invokevirtual fine : (Ljava/lang/String;)V
    //   133: aload #13
    //   135: astore #18
    //   137: aload #18
    //   139: arraylength
    //   140: istore #19
    //   142: iconst_0
    //   143: istore #20
    //   145: iload #20
    //   147: iload #19
    //   149: if_icmpge -> 1120
    //   152: aload #18
    //   154: iload #20
    //   156: aaload
    //   157: astore #21
    //   159: aload #21
    //   161: iload_2
    //   162: iload #8
    //   164: invokevirtual getValidLocators : (II)[Lorg/cyprusbrs/model/MLocator;
    //   167: astore #22
    //   169: aload #22
    //   171: arraylength
    //   172: ifne -> 204
    //   175: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   178: new java/lang/StringBuilder
    //   181: dup
    //   182: invokespecial <init> : ()V
    //   185: ldc 'No valid locators found for rule : '
    //   187: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload #21
    //   192: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   195: invokevirtual toString : ()Ljava/lang/String;
    //   198: invokevirtual fine : (Ljava/lang/String;)V
    //   201: goto -> 1114
    //   204: aload #22
    //   206: astore #23
    //   208: aload #23
    //   210: arraylength
    //   211: istore #24
    //   213: iconst_0
    //   214: istore #25
    //   216: iload #25
    //   218: iload #24
    //   220: if_icmpge -> 1100
    //   223: aload #23
    //   225: iload #25
    //   227: aaload
    //   228: astore #26
    //   230: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   233: astore #27
    //   235: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   238: astore #28
    //   240: aload #26
    //   242: invokevirtual getM_Locator_ID : ()I
    //   245: istore #29
    //   247: iload #29
    //   249: iload #10
    //   251: if_icmpne -> 283
    //   254: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   257: new java/lang/StringBuilder
    //   260: dup
    //   261: invokespecial <init> : ()V
    //   264: ldc 'Locator is the same as the source locator, skipping locator :'
    //   266: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: aload #26
    //   271: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   274: invokevirtual toString : ()Ljava/lang/String;
    //   277: invokevirtual fine : (Ljava/lang/String;)V
    //   280: goto -> 1094
    //   283: iload #9
    //   285: ifeq -> 331
    //   288: aload_0
    //   289: invokevirtual getCtx : ()Lorg/cyprusbrs/util/Ctx;
    //   292: iload #9
    //   294: iload #29
    //   296: invokestatic isLocatorInZone : (Lorg/cyprusbrs/util/Ctx;II)Z
    //   299: ifne -> 331
    //   302: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   305: new java/lang/StringBuilder
    //   308: dup
    //   309: invokespecial <init> : ()V
    //   312: ldc 'Locator is not in destination zone, skipping locator : '
    //   314: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   317: aload #26
    //   319: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   322: invokevirtual toString : ()Ljava/lang/String;
    //   325: invokevirtual fine : (Ljava/lang/String;)V
    //   328: goto -> 1094
    //   331: aload #26
    //   333: invokevirtual getStocking_UOM_ID : ()I
    //   336: istore #30
    //   338: iload #30
    //   340: ifne -> 372
    //   343: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   346: new java/lang/StringBuilder
    //   349: dup
    //   350: invokespecial <init> : ()V
    //   353: ldc 'Stocking UOM not defined, skipping locator : '
    //   355: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   358: aload #26
    //   360: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   363: invokevirtual toString : ()Ljava/lang/String;
    //   366: invokevirtual fine : (Ljava/lang/String;)V
    //   369: goto -> 1094
    //   372: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   375: new java/lang/StringBuilder
    //   378: dup
    //   379: invokespecial <init> : ()V
    //   382: ldc 'Locator : '
    //   384: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   387: aload #26
    //   389: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   392: ldc ' Stocking UOM : '
    //   394: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   397: iload #30
    //   399: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   402: ldc ' Maintain UOM Integrity : '
    //   404: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   407: aload #21
    //   409: invokevirtual isMaintainUOMIntegrity : ()Z
    //   412: invokevirtual append : (Z)Ljava/lang/StringBuilder;
    //   415: invokevirtual toString : ()Ljava/lang/String;
    //   418: invokevirtual fine : (Ljava/lang/String;)V
    //   421: aload #21
    //   423: invokevirtual isMaintainUOMIntegrity : ()Z
    //   426: ifeq -> 618
    //   429: iload #5
    //   431: iload #30
    //   433: if_icmpeq -> 465
    //   436: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   439: new java/lang/StringBuilder
    //   442: dup
    //   443: invokespecial <init> : ()V
    //   446: ldc 'Maintain UOM Integrity is selected, but receipt UOM does not match stocking UOM, skipping locator : '
    //   448: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   451: aload #26
    //   453: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   456: invokevirtual toString : ()Ljava/lang/String;
    //   459: invokevirtual fine : (Ljava/lang/String;)V
    //   462: goto -> 1094
    //   465: aload_0
    //   466: aload_0
    //   467: invokevirtual getCtx : ()Lorg/cyprusbrs/util/Ctx;
    //   470: aload #26
    //   472: aload #15
    //   474: aload_0
    //   475: invokevirtual get_Trx : ()Lorg/cyprusbrs/util/Trx;
    //   478: invokespecial getLocatorStockableQty : (Lorg/cyprusbrs/util/Ctx;Lorg/cyprusbrs/model/MLocator;Lorg/cyprusbrs/model/MProduct;Lorg/cyprusbrs/util/Trx;)Ljava/math/BigDecimal;
    //   481: astore #27
    //   483: aload #27
    //   485: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   488: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   491: ifgt -> 533
    //   494: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   497: new java/lang/StringBuilder
    //   500: dup
    //   501: invokespecial <init> : ()V
    //   504: ldc 'Quantity stockable = '
    //   506: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   509: aload #27
    //   511: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   514: ldc ', skipping locator : '
    //   516: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   519: aload #26
    //   521: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   524: invokevirtual toString : ()Ljava/lang/String;
    //   527: invokevirtual fine : (Ljava/lang/String;)V
    //   530: goto -> 1094
    //   533: aload #14
    //   535: aload #27
    //   537: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   540: ifgt -> 550
    //   543: aload #14
    //   545: astore #28
    //   547: goto -> 990
    //   550: aload_0
    //   551: invokevirtual isAllowSplit : ()Z
    //   554: ifne -> 611
    //   557: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   560: new java/lang/StringBuilder
    //   563: dup
    //   564: invokespecial <init> : ()V
    //   567: ldc 'Quantity to Putaway = '
    //   569: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   572: aload #14
    //   574: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   577: ldc ', Quantity Stockable = '
    //   579: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   582: aload #27
    //   584: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   587: ldc ' and splitting is not allowed '
    //   589: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   592: ldc ', skipping locator : '
    //   594: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   597: aload #26
    //   599: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   602: invokevirtual toString : ()Ljava/lang/String;
    //   605: invokevirtual fine : (Ljava/lang/String;)V
    //   608: goto -> 1094
    //   611: aload #27
    //   613: astore #28
    //   615: goto -> 990
    //   618: getstatic java/math/BigDecimal.ONE : Ljava/math/BigDecimal;
    //   621: astore #31
    //   623: iload #30
    //   625: iload #16
    //   627: if_icmpeq -> 687
    //   630: aload_0
    //   631: invokevirtual getCtx : ()Lorg/cyprusbrs/util/Ctx;
    //   634: iload_2
    //   635: iload #30
    //   637: invokestatic getProductRateFrom : (Lorg/cyprusbrs/util/Ctx;II)Ljava/math/BigDecimal;
    //   640: astore #31
    //   642: aload #31
    //   644: ifnull -> 658
    //   647: aload #31
    //   649: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   652: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   655: ifgt -> 687
    //   658: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   661: new java/lang/StringBuilder
    //   664: dup
    //   665: invokespecial <init> : ()V
    //   668: ldc 'Stocking UOM conversion does not exist , skipping locator : '
    //   670: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   673: aload #26
    //   675: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   678: invokevirtual toString : ()Ljava/lang/String;
    //   681: invokevirtual fine : (Ljava/lang/String;)V
    //   684: goto -> 1094
    //   687: aload_0
    //   688: aload_0
    //   689: invokevirtual getCtx : ()Lorg/cyprusbrs/util/Ctx;
    //   692: aload #26
    //   694: aload #15
    //   696: aload_0
    //   697: invokevirtual get_Trx : ()Lorg/cyprusbrs/util/Trx;
    //   700: invokespecial getProductStockableQty : (Lorg/cyprusbrs/util/Ctx;Lorg/cyprusbrs/model/MLocator;Lorg/cyprusbrs/model/MProduct;Lorg/cyprusbrs/util/Trx;)Ljava/math/BigDecimal;
    //   703: astore #27
    //   705: aload #27
    //   707: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   710: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   713: ifgt -> 755
    //   716: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   719: new java/lang/StringBuilder
    //   722: dup
    //   723: invokespecial <init> : ()V
    //   726: ldc 'Quantity stockable = '
    //   728: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   731: aload #27
    //   733: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   736: ldc ', skipping locator : '
    //   738: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   741: aload #26
    //   743: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   746: invokevirtual toString : ()Ljava/lang/String;
    //   749: invokevirtual fine : (Ljava/lang/String;)V
    //   752: goto -> 1094
    //   755: aload #14
    //   757: aload #17
    //   759: invokevirtual multiply : (Ljava/math/BigDecimal;)Ljava/math/BigDecimal;
    //   762: astore #32
    //   764: aload #32
    //   766: aload #27
    //   768: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   771: ifgt -> 781
    //   774: aload #32
    //   776: astore #28
    //   778: goto -> 846
    //   781: aload_0
    //   782: invokevirtual isAllowSplit : ()Z
    //   785: ifne -> 842
    //   788: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   791: new java/lang/StringBuilder
    //   794: dup
    //   795: invokespecial <init> : ()V
    //   798: ldc 'Quantity to Stock = '
    //   800: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   803: aload #32
    //   805: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   808: ldc ', Quantity Stockable = '
    //   810: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   813: aload #27
    //   815: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   818: ldc ' and splitting is not allowed '
    //   820: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   823: ldc ', skipping locator : '
    //   825: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   828: aload #26
    //   830: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   833: invokevirtual toString : ()Ljava/lang/String;
    //   836: invokevirtual fine : (Ljava/lang/String;)V
    //   839: goto -> 1094
    //   842: aload #27
    //   844: astore #28
    //   846: aload #28
    //   848: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   851: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   854: ifgt -> 896
    //   857: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   860: new java/lang/StringBuilder
    //   863: dup
    //   864: invokespecial <init> : ()V
    //   867: ldc 'Qty Entered = '
    //   869: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   872: aload #28
    //   874: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   877: ldc ', skipping locator : '
    //   879: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   882: aload #26
    //   884: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   887: invokevirtual toString : ()Ljava/lang/String;
    //   890: invokevirtual fine : (Ljava/lang/String;)V
    //   893: goto -> 1094
    //   896: aload #28
    //   898: aload #17
    //   900: iconst_1
    //   901: invokevirtual divide : (Ljava/math/BigDecimal;I)Ljava/math/BigDecimal;
    //   904: astore #33
    //   906: aload #33
    //   908: aload #17
    //   910: invokevirtual multiply : (Ljava/math/BigDecimal;)Ljava/math/BigDecimal;
    //   913: astore #34
    //   915: aload #34
    //   917: aload #28
    //   919: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   922: ifeq -> 986
    //   925: aload_0
    //   926: invokevirtual isAllowSplit : ()Z
    //   929: ifne -> 986
    //   932: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   935: new java/lang/StringBuilder
    //   938: dup
    //   939: invokespecial <init> : ()V
    //   942: ldc 'Rounded Quantity = '
    //   944: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   947: aload #34
    //   949: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   952: ldc ', Quantity Entered = '
    //   954: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   957: aload #28
    //   959: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   962: ldc ' and splitting is not allowed '
    //   964: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   967: ldc ', skipping locator : '
    //   969: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   972: aload #26
    //   974: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   977: invokevirtual toString : ()Ljava/lang/String;
    //   980: invokevirtual fine : (Ljava/lang/String;)V
    //   983: goto -> 1094
    //   986: aload #33
    //   988: astore #28
    //   990: aload #28
    //   992: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   995: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   998: ifgt -> 1030
    //   1001: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   1004: new java/lang/StringBuilder
    //   1007: dup
    //   1008: invokespecial <init> : ()V
    //   1011: ldc 'Rounded quantity in stocking UOM is zero, skipping locator : '
    //   1013: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1016: aload #26
    //   1018: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1021: invokevirtual toString : ()Ljava/lang/String;
    //   1024: invokevirtual fine : (Ljava/lang/String;)V
    //   1027: goto -> 1094
    //   1030: aload_1
    //   1031: aload_0
    //   1032: invokevirtual getM_Warehouse_ID : ()I
    //   1035: iload #10
    //   1037: iload #29
    //   1039: iload_2
    //   1040: iload_3
    //   1041: iload #7
    //   1043: iload #8
    //   1045: iconst_0
    //   1046: aload #28
    //   1048: iload #5
    //   1050: iload #11
    //   1052: iconst_0
    //   1053: iload #6
    //   1055: invokevirtual addTask : (IIIIIIIILjava/math/BigDecimal;IIII)Z
    //   1058: ifne -> 1071
    //   1061: new java/lang/IllegalStateException
    //   1064: dup
    //   1065: ldc 'Could not generate task'
    //   1067: invokespecial <init> : (Ljava/lang/String;)V
    //   1070: athrow
    //   1071: aload #14
    //   1073: aload #28
    //   1075: invokevirtual subtract : (Ljava/math/BigDecimal;)Ljava/math/BigDecimal;
    //   1078: astore #14
    //   1080: aload #14
    //   1082: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   1085: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   1088: ifgt -> 1094
    //   1091: goto -> 1100
    //   1094: iinc #25, 1
    //   1097: goto -> 216
    //   1100: aload #14
    //   1102: getstatic java/math/BigDecimal.ZERO : Ljava/math/BigDecimal;
    //   1105: invokevirtual compareTo : (Ljava/math/BigDecimal;)I
    //   1108: ifgt -> 1114
    //   1111: goto -> 1120
    //   1114: iinc #20, 1
    //   1117: goto -> 145
    //   1120: getstatic org/cyprusbrs/cwms/model/MWMSMMStrategy.log : Lorg/cyprusbrs/util/CLogger;
    //   1123: new java/lang/StringBuilder
    //   1126: dup
    //   1127: invokespecial <init> : ()V
    //   1130: ldc 'Qty To Putaway : '
    //   1132: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1135: aload #14
    //   1137: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1140: invokevirtual toString : ()Ljava/lang/String;
    //   1143: invokevirtual fine : (Ljava/lang/String;)V
    //   1146: aload #14
    //   1148: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #232	-> 0
    //   #233	-> 6
    //   #235	-> 10
    //   #236	-> 20
    //   #238	-> 27
    //   #239	-> 32
    //   #241	-> 39
    //   #244	-> 51
    //   #245	-> 67
    //   #248	-> 77
    //   #253	-> 133
    //   #255	-> 159
    //   #257	-> 169
    //   #259	-> 175
    //   #260	-> 201
    //   #263	-> 204
    //   #265	-> 230
    //   #266	-> 235
    //   #268	-> 240
    //   #270	-> 247
    //   #272	-> 254
    //   #273	-> 280
    //   #276	-> 283
    //   #279	-> 302
    //   #280	-> 328
    //   #283	-> 331
    //   #285	-> 338
    //   #287	-> 343
    //   #288	-> 369
    //   #291	-> 372
    //   #296	-> 421
    //   #298	-> 429
    //   #300	-> 436
    //   #302	-> 462
    //   #305	-> 465
    //   #308	-> 483
    //   #310	-> 494
    //   #312	-> 530
    //   #315	-> 533
    //   #316	-> 543
    //   #319	-> 550
    //   #321	-> 557
    //   #325	-> 608
    //   #327	-> 611
    //   #333	-> 618
    //   #334	-> 623
    //   #336	-> 630
    //   #339	-> 642
    //   #341	-> 658
    //   #343	-> 684
    //   #347	-> 687
    //   #349	-> 705
    //   #351	-> 716
    //   #353	-> 752
    //   #357	-> 755
    //   #359	-> 764
    //   #360	-> 774
    //   #363	-> 781
    //   #365	-> 788
    //   #369	-> 839
    //   #371	-> 842
    //   #374	-> 846
    //   #376	-> 857
    //   #378	-> 893
    //   #382	-> 896
    //   #383	-> 906
    //   #384	-> 915
    //   #386	-> 932
    //   #390	-> 983
    //   #393	-> 986
    //   #396	-> 990
    //   #398	-> 1001
    //   #400	-> 1027
    //   #404	-> 1030
    //   #408	-> 1061
    //   #410	-> 1071
    //   #411	-> 1080
    //   #412	-> 1091
    //   #263	-> 1094
    //   #415	-> 1100
    //   #416	-> 1111
    //   #253	-> 1114
    //   #419	-> 1120
    //   #420	-> 1146
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   623	367	31	stockingUOMRate	Ljava/math/BigDecimal;
    //   764	226	32	qtyToStock	Ljava/math/BigDecimal;
    //   906	84	33	qtyReceiptUOM	Ljava/math/BigDecimal;
    //   915	75	34	roundedQty	Ljava/math/BigDecimal;
    //   235	859	27	qtyStockable	Ljava/math/BigDecimal;
    //   240	854	28	qtyEntered	Ljava/math/BigDecimal;
    //   247	847	29	M_Locator_ID	I
    //   338	756	30	stocking_UOM_ID	I
    //   230	864	26	element	Lorg/cyprusbrs/model/MLocator;
    //   208	892	23	arr$	[Lorg/cyprusbrs/model/MLocator;
    //   213	887	24	len$	I
    //   216	884	25	i$	I
    //   169	945	22	locators	[Lorg/cyprusbrs/model/MLocator;
    //   159	955	21	rule	Lorg/cyprusbrs/cwms/model/MWMSMMRule;
    //   137	983	18	arr$	[Lorg/cyprusbrs/cwms/model/MWMSMMRule;
    //   142	978	19	len$	I
    //   145	975	20	i$	I
    //   0	1149	0	this	Lorg/cyprusbrs/cwms/model/MWMSMMStrategy;
    //   0	1149	1	mmContext	Lorg/cyprusbrs/cwms/util/MMContext;
    //   0	1149	2	M_Product_ID	I
    //   0	1149	3	M_AttributeSetInstance_ID	I
    //   0	1149	4	receivedQty	Ljava/math/BigDecimal;
    //   0	1149	5	receipt_UOM_ID	I
    //   0	1149	6	M_InOutLine_ID	I
    //   0	1149	7	C_Order_ID	I
    //   0	1149	8	C_OrderLine_ID	I
    //   0	1149	9	M_DestZone_ID	I
    //   0	1149	10	srcM_Locator_ID	I
    //   0	1149	11	C_DocType_ID	I
    //   0	1149	12	docAction	Ljava/lang/String;
    //   6	1143	13	rules	[Lorg/cyprusbrs/cwms/model/MWMSMMRule;
    //   10	1139	14	qtyToPutaway	Ljava/math/BigDecimal;
    //   20	1129	15	product	Lorg/cyprusbrs/model/MProduct;
    //   27	1122	16	product_UOM_ID	I
    //   32	1117	17	rcvUOMRate	Ljava/math/BigDecimal;
  }
  
  private BigDecimal getLocatorStockableQty(Properties ctx, MLocator locator, MProduct product, String trx) throws Exception {
    if (locator == null || product == null)
      return BigDecimal.ZERO; 
    int M_Product_ID = product.getM_Product_ID();
    int M_Locator_ID = locator.getM_Locator_ID();
   // MLocator pc = MLocator.getOfProductLocator(ctx, M_Product_ID, M_Locator_ID);
    MProductLocator pc = MProductLocator.getOfProductLocator(ctx, M_Product_ID, M_Locator_ID);

    if (pc == null && locator.isFixed())
      return BigDecimal.ZERO; 
    BigDecimal qtyOnHand = BigDecimal.ZERO;
    BigDecimal qtyExpected = BigDecimal.ZERO;
    BigDecimal maxStockingQty = BigDecimal.ZERO;
    BigDecimal stockableQty = BigDecimal.ZERO;
    int product_UOM_ID = product.getC_UOM_ID();
    int stocking_UOM_ID = locator.getStocking_UOM_ID();
    BigDecimal stockingRate = BigDecimal.ONE;
    if (product_UOM_ID != stocking_UOM_ID)
      stockingRate = MUOMConversion.getProductRateFrom(ctx, M_Product_ID, stocking_UOM_ID); 
    if (stockingRate == null || stockingRate.compareTo(BigDecimal.ZERO) == 0)
      return BigDecimal.ZERO; 
    if (pc != null) {
      CPreparedStatement cPreparedStatement = null;
      maxStockingQty = pc.getMaxQuantity();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = "SELECT SUM(QtyOnHand),SUM(QtyExpected) FROM M_STORAGE_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) WHERE l.M_Locator_ID=? AND s.M_Product_ID=?";
      try {
        cPreparedStatement = DB.prepareStatement(sql, trx);
        cPreparedStatement.setInt(1, M_Locator_ID);
        cPreparedStatement.setInt(2, M_Product_ID);
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          qtyOnHand = rs.getBigDecimal(1);
          if (qtyOnHand == null)
            qtyOnHand = BigDecimal.ZERO; 
          qtyExpected = rs.getBigDecimal(2);
          if (qtyExpected == null)
            qtyExpected = BigDecimal.ZERO; 
          if (product_UOM_ID != stocking_UOM_ID) {
            qtyOnHand = qtyOnHand.divide(stockingRate, 0);
            qtyExpected = qtyExpected.divide(stockingRate, 0);
          } 
        } 
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
        throw new Exception(Msg.translate(getCtx(), "SQLException"));
      } finally {
        DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null; cPreparedStatement = null;
      } 
    } else {
      CPreparedStatement cPreparedStatement = null;
      maxStockingQty = locator.getMaxQuantity();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = "SELECT p.M_Product_ID, SUM(QtyOnHand),SUM(QtyExpected), p.C_UOM_ID FROM M_STORAGE_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID)  INNER JOIN M_Product p ON (s.M_Product_ID=p.M_Product_ID) WHERE l.M_Locator_ID=? GROUP BY p.M_Product_ID, p.C_UOM_ID ";
      try {
        cPreparedStatement = DB.prepareStatement(sql, trx);
        cPreparedStatement.setInt(1, M_Locator_ID);
        rs = cPreparedStatement.executeQuery();
        while (rs.next()) {
          BigDecimal onHand = rs.getBigDecimal(2);
          if (onHand == null)
            onHand = BigDecimal.ZERO; 
          BigDecimal rate = BigDecimal.ONE;
          int C_UOM_ID = rs.getInt(4);
          if (C_UOM_ID != stocking_UOM_ID)
            rate = MUOMConversion.getProductRateTo(ctx, rs.getInt(1), stocking_UOM_ID); 
          if (rate == null || rate.compareTo(BigDecimal.ZERO) == 0)
            continue; 
          qtyOnHand = qtyOnHand.add(onHand.multiply(rate));
          BigDecimal expected = rs.getBigDecimal(3);
          if (expected == null)
            expected = BigDecimal.ZERO; 
          qtyExpected = qtyExpected.add(expected.multiply(rate));
        } 
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
        throw new Exception(Msg.translate(getCtx(), "SQLException"));
      } finally {
        DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null; cPreparedStatement = null;
      } 
    } 
    stockableQty = maxStockingQty.subtract(qtyOnHand).subtract(qtyExpected);
    return stockableQty;
  }
  
  private BigDecimal getProductStockableQty(Properties ctx, MLocator locator, MProduct product, String trx) throws Exception {
    if (locator == null || product == null)
      return BigDecimal.ZERO; 
    int M_Product_ID = product.getM_Product_ID();
    int M_Locator_ID = locator.getM_Locator_ID();
//    MLocator pc = MLocator.getOfProductLocator(ctx, M_Product_ID, M_Locator_ID);
    MProductLocator pc = MProductLocator.getOfProductLocator(ctx, M_Product_ID, M_Locator_ID);

    if (pc == null && locator.isFixed())
      return BigDecimal.ZERO; 
    BigDecimal qtyOnHand = BigDecimal.ZERO;
    BigDecimal qtyExpected = BigDecimal.ZERO;
    BigDecimal maxStockingQty = BigDecimal.ZERO;
    BigDecimal stockableQty = BigDecimal.ZERO;
    int product_UOM_ID = product.getC_UOM_ID();
    int stocking_UOM_ID = locator.getStocking_UOM_ID();
    BigDecimal stockingRate = BigDecimal.ONE;
    if (product_UOM_ID != stocking_UOM_ID)
      stockingRate = MUOMConversion.getProductRateFrom(ctx, M_Product_ID, stocking_UOM_ID); 
    if (stockingRate == null || stockingRate.compareTo(BigDecimal.ZERO) == 0)
      return BigDecimal.ZERO; 
    if (pc != null) {
      CPreparedStatement cPreparedStatement = null;
      maxStockingQty = pc.getMaxQuantity().multiply(stockingRate);
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = "SELECT SUM(QtyOnHand),SUM(QtyExpected) FROM M_STORAGE_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) WHERE l.M_Locator_ID=? AND s.M_Product_ID=?";
      try {
        cPreparedStatement = DB.prepareStatement(sql, trx);
        cPreparedStatement.setInt(1, M_Locator_ID);
        cPreparedStatement.setInt(2, M_Product_ID);
        rs = cPreparedStatement.executeQuery();
        if (rs.next()) {
          qtyOnHand = rs.getBigDecimal(1);
          if (qtyOnHand == null)
            qtyOnHand = BigDecimal.ZERO; 
          qtyExpected = rs.getBigDecimal(2);
          if (qtyExpected == null)
            qtyExpected = BigDecimal.ZERO; 
        } 
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
        throw new Exception(Msg.translate(getCtx(), "SQLException"));
      } finally {
        DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null; cPreparedStatement = null;
      } 
    } else {
      CPreparedStatement cPreparedStatement = null;
      maxStockingQty = locator.getMaxQuantity().multiply(stockingRate);
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String sql = "SELECT p.M_Product_ID, SUM(QtyOnHand),SUM(QtyExpected), p.C_UOM_ID FROM M_STORAGE_V s INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID)  INNER JOIN M_Product p ON (s.M_Product_ID=p.M_Product_ID) WHERE l.M_Locator_ID=? GROUP BY p.M_Product_ID, p.C_UOM_ID ";
      try {
        cPreparedStatement = DB.prepareStatement(sql, trx);
        cPreparedStatement.setInt(1, M_Locator_ID);
        rs = cPreparedStatement.executeQuery();
        while (rs.next()) {
          BigDecimal onHand = rs.getBigDecimal(2);
          if (onHand == null)
            onHand = BigDecimal.ZERO; 
          BigDecimal rate = BigDecimal.ONE;
          int C_UOM_ID = rs.getInt(4);
          if (C_UOM_ID != stocking_UOM_ID)
            rate = MUOMConversion.getProductRateFrom(ctx, rs.getInt(1), stocking_UOM_ID); 
          if (rate == null || rate.compareTo(BigDecimal.ZERO) == 0)
            continue; 
          qtyOnHand = qtyOnHand.add(onHand.multiply(rate));
          BigDecimal expected = rs.getBigDecimal(3);
          if (expected == null)
            expected = BigDecimal.ZERO; 
          qtyExpected = qtyExpected.add(expected.multiply(rate));
        } 
        qtyOnHand = qtyOnHand.divide(stockingRate, 1);
        qtyExpected = qtyExpected.divide(stockingRate, 1);
      } catch (SQLException e) {
        log.log(Level.SEVERE, sql, e);
        throw new Exception(Msg.translate(getCtx(), "SQLException"));
      } finally {
        DB.close(rs);
        DB.close((Statement)cPreparedStatement);
        rs = null; cPreparedStatement = null;
      } 
    } 
    stockableQty = maxStockingQty.subtract(qtyOnHand).subtract(qtyExpected);
    return stockableQty;
  }
  
  protected boolean beforeSave(boolean newRecord) {
//    SysEnv se = SysEnv.get("CWMS");
//    if (se == null || !se.checkLicense())
//      return false; 
    return true;
  }
  
  public String toString() {
    return getWMS_MMStrategy_ID() + ":" + getName();
  }
}

