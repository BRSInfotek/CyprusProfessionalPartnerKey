package org.cyprus.mrp.util;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import org.cyprus.mrp.model.MMRPForecast;
import org.cyprus.mrp.model.MMRPMasterDemand;
import org.cyprus.mrp.model.MMRPPlan;
import org.cyprusbrs.framework.MForecast;
import org.cyprusbrs.framework.MMasterDemand;
import org.cyprusbrs.framework.MPeriod;
import org.cyprusbrs.util.DB;

public class MRPFreezeUtil {

	//Updated by Mukesh -----------------------20241008

	public int freeze(MForecast forecast, String trx) {
		forecast.setIsFrozen(true);
		String sql = "UPDATE M_ForecastLine SET Processed = 'Y' WHERE M_Forecast_ID = ?";
		// int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(forecast.getMRP_Forecast_ID()) });
		int numLine =DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(forecast.getM_Forecast_ID()) },trx);
		return numLine;
	}

	public int unfreeze(MForecast forecast, String trx) {
		forecast.setIsFrozen(false);
		String sql = "UPDATE M_ForecastLine SET Processed = 'N' WHERE M_Forecast_ID = ?";
		//  int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(forecast.getMRP_Forecast_ID()) });
		int numLine =DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(forecast.getM_Forecast_ID()) }, trx);
		return numLine;
	}
	public int freeze(MMasterDemand masterDemand, String trx) {
		masterDemand.setIsFrozen(true);
		String sql = "UPDATE M_MasterDemandLine SET Processed = 'Y' WHERE M_MasterDemand_ID = ?";
		// int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()) });
		int numLine = DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(masterDemand.getM_MasterDemand_ID()) }, trx);
		return numLine;
	}

	public int unfreeze(MMasterDemand masterDemand, String trx) {
		masterDemand.setIsFrozen(false);
		String sql = "UPDATE M_MasterDemandLine SET Processed = 'N' WHERE M_MasterDemand_ID = ?";
		// int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()) });
		int numLine =DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(masterDemand.getM_MasterDemand_ID()) }, trx);
		return numLine;
	}
	/// End of the code by Mukesh

	public int freeze(MMRPForecast forecast, String trx) {
		forecast.setIsFrozen(true);
		String sql = "UPDATE MRP_ForecastLine SET IsLineFrozen = 'Y' WHERE MRP_Forecast_ID = ?";
		// int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(forecast.getMRP_Forecast_ID()) });
		int numLine =DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(forecast.getMRP_Forecast_ID()) },trx);
		return numLine;
	}

	public int freeze(MMRPMasterDemand masterDemand, String trx) {
		masterDemand.setIsFrozen(true);
		String sql = "UPDATE MRP_MasterDemandLine SET IsLineFrozen = 'Y' WHERE MRP_MasterDemand_ID = ?";
		// int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()) });
		int numLine = DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()) }, trx);
		return numLine;
	}

	public int unfreeze(MMRPForecast forecast, String trx) {
		forecast.setIsFrozen(false);
		String sql = "UPDATE MRP_ForecastLine SET IsLineFrozen = 'N' WHERE MRP_Forecast_ID = ?";
		//  int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(forecast.getMRP_Forecast_ID()) });
		int numLine =DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(forecast.getMRP_Forecast_ID()) }, trx);
		return numLine;
	}

	public int unfreeze(MMRPMasterDemand masterDemand, String trx) {
		masterDemand.setIsFrozen(false);
		String sql = "UPDATE MRP_MasterDemandLine SET IsLineFrozen = 'N' WHERE MRP_MasterDemand_ID = ?";
		// int numLine = DB.executeUpdate(trx, sql, new Object[] { Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()) });
		int numLine =DB.executeUpdateEx(sql, new Object[] { Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()) }, trx);
		return numLine;
	}

	public int unfreeze(MMRPForecast forecast, Timestamp unfreezeDate, Properties ctx, String trx) {
		forecast.setIsFrozen(false);
		MMRPPlan plan = new MMRPPlan(ctx, forecast.getMRP_Plan_ID(), trx);
		MPeriod currentPeriod = MPeriod.getOfCalendar(ctx, plan.getC_Calendar_ID(), unfreezeDate);
		if (currentPeriod == null)
			return unfreeze(forecast, trx); 
		ArrayList<Integer> params = new ArrayList<Integer>();
		params.add(Integer.valueOf(forecast.getMRP_Forecast_ID()));
		params.add(Integer.valueOf(currentPeriod.getC_Year_ID()));
		params.add(Integer.valueOf(currentPeriod.getPeriodNo()));
		String sql = "UPDATE MRP_ForecastLine SET IsLineFrozen='N' WHERE MRP_Forecast_ID = ? AND EXISTS (SELECT * FROM C_Period  WHERE C_Period.C_Period_ID = MRP_ForecastLine.C_Period_ID AND ((C_Period.C_Year_ID * 1000) + C_Period.PeriodNo)       >= ((? * 1000) + ?))";
		// int numLine = DB.executeUpdate(trx, sql, params.toArray());
		int numLine =DB.executeUpdateEx(sql, params.toArray(), trx);
		return numLine;
	}

	public int unfreeze(MMRPMasterDemand masterDemand, Timestamp unfreezeDate, Properties ctx, String trx) {
		masterDemand.setIsFrozen(false);
		MMRPPlan plan = new MMRPPlan(ctx, masterDemand.getMRP_Plan_ID(), trx);
		MPeriod currentPeriod = MPeriod.getOfCalendar(ctx, plan.getC_Calendar_ID(), unfreezeDate);
		if (currentPeriod == null)
			return unfreeze(masterDemand, trx); 
		ArrayList<Integer> params = new ArrayList<Integer>();
		params.add(Integer.valueOf(masterDemand.getMRP_MasterDemand_ID()));
		params.add(Integer.valueOf(currentPeriod.getC_Year_ID()));
		params.add(Integer.valueOf(currentPeriod.getPeriodNo()));
		String sql = "UPDATE MRP_MasterDemandLine SET IsLineFrozen='N' WHERE MRP_MasterDemand_ID = ? AND EXISTS (SELECT * FROM C_Period  WHERE C_Period.C_Period_ID = MRP_MasterDemandLine.C_Period_ID AND ((C_Period.C_Year_ID * 1000) + C_Period.PeriodNo)       >= ((? * 1000) + ?))";
		//int numLine = DB.executeUpdate(trx, sql, params.toArray());
		int numLine = DB.executeUpdateEx(sql,  params.toArray(), trx);
		return numLine;
	}
}

