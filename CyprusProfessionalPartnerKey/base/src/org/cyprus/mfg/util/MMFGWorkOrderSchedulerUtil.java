package org.cyprus.mfg.util;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprusbrs.framework.MUOM;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Env;

public class MMFGWorkOrderSchedulerUtil {
  protected static CLogger log = CLogger.getCLogger(MMFGWorkOrderSchedulerUtil.class);
  
  public static BigDecimal TotalOperationTime(BigDecimal quantity, MMFGWorkOrderOperation woo, boolean includeSetupTime) {
    quantity = Env.ONE;
    if (includeSetupTime) {
      BigDecimal bigDecimal = woo.getSetupTime().add(woo.getUnitRuntime().multiply(quantity));
      return bigDecimal;
    } 
    BigDecimal totalTimeConsumed = woo.getUnitRuntime().multiply(quantity);
    return totalTimeConsumed;
  }
  
  public static Timestamp ScheduleDate(Properties ctx, MMFGWorkOrderOperation woo, BigDecimal quantity, Timestamp beginDate, boolean includeOptionalOperationTime, boolean includeSetupTime, boolean isForwardScheduling) {
    if (woo.isOptional() && !includeOptionalOperationTime)
      return beginDate; 
    MUOM uom = new MUOM(ctx, woo.getC_UOM_ID(), null);
    if (!uom.isDay() && !uom.isHour()) {
      log.warning("UOM should either be a Day or Hour");
      return null;
    } 
    BigDecimal totalTimeConsumed = null;
    totalTimeConsumed = TotalOperationTime(quantity, woo, includeSetupTime);
    if (totalTimeConsumed.intValue() != 0) {
      if (!isForwardScheduling)
        totalTimeConsumed = totalTimeConsumed.negate(); 
      Timestamp endDate = null;
      if (uom.isHour()) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(beginDate);
        BigDecimal[] totalTimeConsumed1 = totalTimeConsumed.divideAndRemainder(new BigDecimal(24));
        BigDecimal Days = totalTimeConsumed1[0];
        BigDecimal Hours = totalTimeConsumed1[1];
        BigDecimal Mins = Hours.subtract(new BigDecimal(Hours.intValue())).multiply(new BigDecimal(60));
        BigDecimal seconds = Mins.subtract(new BigDecimal(Mins.intValue())).multiply(new BigDecimal(60));
        cal.add(6, Days.intValue());
        cal.add(11, Hours.intValue());
        cal.add(12, Mins.intValue());
        cal.add(13, seconds.intValue());
        endDate = new Timestamp(cal.getTimeInMillis());
      } else if (uom.isDay()) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(beginDate);
        cal.add(6, totalTimeConsumed.intValue());
        BigDecimal Hours = totalTimeConsumed.remainder(new BigDecimal(1)).multiply(new BigDecimal(24));
        cal.add(11, Hours.intValue());
        BigDecimal Mins = Hours.subtract(new BigDecimal(Hours.intValue())).multiply(new BigDecimal(60));
        cal.add(12, Mins.intValue());
        BigDecimal Seconds = Mins.subtract(new BigDecimal(Mins.intValue())).multiply(new BigDecimal(60));
        cal.add(13, Seconds.intValue());
        endDate = new Timestamp(cal.getTimeInMillis());
      } 
      return endDate;
    } 
    return beginDate;
  }
}

