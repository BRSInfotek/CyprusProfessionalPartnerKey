package org.cyprus.mrp.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.cyprusbrs.framework.MForecast;
import org.cyprusbrs.framework.MForecastLine;
import org.cyprusbrs.framework.MWarehouse;
import org.cyprusbrs.framework.X_T_Replenish;
import org.cyprusbrs.process.ProcessInfoParameter;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.CyprusSystemError;
import org.cyprusbrs.util.CyprusUserError;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.ReplenishInterface;

public class GenerateForecast extends SvrProcess {

	private String forecastName;
	private String RuleType;
	private Integer C_Calendar_ID;
	private Integer C_Year_ID;
	private Integer C_Period_ID;
	private Integer M_Warehouse_ID;
	private Timestamp m_currentDate = null;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
	    for (ProcessInfoParameter element : para) {
	      String name = element.getParameterName();
	      if (element.getParameter() != null)
	        if (name.equals("Name")) {
	        	forecastName = element.getParameterAsString();
	        } else if (name.equals("C_Calendar_ID")) {
	        	C_Calendar_ID = element.getParameterAsInt();
	        } else if (name.equals("C_Year_ID")) {
	        	C_Year_ID = element.getParameterAsInt();
	        } else if (name.equals("C_Period_ID")) {
	        	C_Period_ID = element.getParameterAsInt();
	        }else if (name.equals("M_Warehouse_ID")) {
	        	M_Warehouse_ID = element.getParameterAsInt();
	        }else if (name.equals("RuleType")) {
	        	RuleType = element.getParameterAsString();
	        }else {
	        	
	          log.log(Level.SEVERE, "Unknown Parameter: " + name);
	        }  
	    } 
	    if (m_currentDate == null)
	    	m_currentDate = new Timestamp(System.currentTimeMillis());

	}

	@Override
	protected String doIt() throws Exception {

		
		String checks=checkValidation();
		if(checks!=null && checks.length()>0)
			return checks;
		
		
		MWarehouse wh = MWarehouse.get(getCtx(), M_Warehouse_ID);
		if (wh.get_ID() == 0)  
			throw new CyprusSystemError("@FillMandatory@ @M_Warehouse_ID@");
			
		System.out.println("Ad Instance "+getAD_PInstance_ID());
		MForecast forcast=null;
		if(RuleType.equalsIgnoreCase("RR"))
		{	
			prepareTable(); // Prepare the data
			fillTable(wh);  // Fill the data in T_Replenish temp table
			List<ForcastLineData> lineData =getListOfProductsFromT_Replenish(getAD_PInstance_ID());
			if(lineData!=null && lineData.size()>0)
			{
				forcast=createForecastFrom(lineData);
			}
		}
		else if(RuleType.equalsIgnoreCase("LY"))
		{
			List<ForcastLineData> lineData =getListOfProductsFromLastYearRule(wh);
			if(lineData!=null && lineData.size()>0)
				forcast=createForecastFrom(lineData);
		}
		
		return "Forecast "+forcast!=null?forcast.getName()+" Created":"not Created";
	}
	
	private List<ForcastLineData> getListOfProductsFromLastYearRule(MWarehouse wh) {
		
		// Sql to Get the list of item in for selected warehouse
		List<ForcastLineData> finalStock=new ArrayList<ForcastLineData>();

		Map<Integer,BigDecimal> actualQtyToOrder=new HashMap<Integer,BigDecimal>();
		String sql="SELECT SUM(qtyonhand) as qtyonhandSum, SUM(qtyreserved) as qtyreservedSum, SUM(qtyordered) as qtyorderedSUM, ms.m_product_id FROM M_Storage ms "
				+ "	INNER JOIN m_locator ml ON(ms.m_locator_id=ml.m_locator_id) "
				+ "	WHERE ml.M_Warehouse_ID="+wh.getM_Warehouse_ID()
				+ "	GROUP BY ms.m_product_id ORDER BY ms.m_product_id";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				Integer M_Product_ID=rs.getInt("M_Product_ID");
				BigDecimal qtytoorder=rs.getBigDecimal("qtyonhandSum");				
				actualQtyToOrder.put(M_Product_ID, qtytoorder);
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close ();
			}
			catch (Exception e)
			{}
			pstmt = null;
		}
		
		/// Get All item for last Year with Month
		
		sql="SELECT SUM(ABS(mt.movementqty)) AS qtyonhandSum, mt.m_product_id "
			+ " FROM M_Transaction mt "
			+ " INNER JOIN M_Locator ml ON (mt.M_Locator_ID=ml.M_Locator_ID) "
			+ " WHERE EXTRACT(MONTH FROM movementdate) = EXTRACT(MONTH FROM CURRENT_DATE) "
			+ " AND EXTRACT(YEAR FROM movementdate) = EXTRACT(YEAR FROM CURRENT_DATE) -1 "
			+ " AND ml.M_Warehouse_ID="+wh.getM_Warehouse_ID()
			+ " AND mt.movementqty<0 "
			+ " GROUP BY mt.m_product_id ORDER BY mt.m_product_id" ;
		pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				Integer M_Product_ID=rs.getInt("M_Product_ID");
				BigDecimal qtytoorder=rs.getBigDecimal("qtyonhandSum");
				
				if(actualQtyToOrder.containsKey(M_Product_ID))
				{
					BigDecimal qtytoorderOld=actualQtyToOrder.get(M_Product_ID);
					if(qtytoorderOld.compareTo(qtytoorder)>=0)
					{
						ForcastLineData fl=new ForcastLineData(M_Product_ID,qtytoorderOld,C_Period_ID);
						finalStock.add(fl);
					}
					else
					{
						ForcastLineData fl=new ForcastLineData(M_Product_ID,qtytoorder,C_Period_ID);
						finalStock.add(fl);
					}
				}			
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close ();
			}
			catch (Exception e)
			{}
			pstmt = null;
		}

		return finalStock;
	}

	/**
	 * Create Forecast Header and lines
	 * @param lineData
	 * @return
	 */
	private MForecast createForecastFrom(List<ForcastLineData> lineData) {


		MForecast mForecast=null;
		for(ForcastLineData line:lineData)//lineData.forEach(line->
		{
			if(mForecast==null) /// For Header Save
			{
				mForecast=new MForecast(getCtx(),0,get_TrxName());
				mForecast.setName(forecastName);
				mForecast.setC_Calendar_ID(C_Calendar_ID);
				mForecast.setC_Year_ID(C_Year_ID);
				mForecast.save();
			}					
			if(mForecast!=null) // For line save
			{
				MForecastLine mForecastLine=new MForecastLine(getCtx(), 0, get_TrxName());
				mForecastLine.setM_Forecast_ID(mForecast.getM_Forecast_ID());
				mForecastLine.setM_Product_ID(line.getProduct_ID());
				mForecastLine.setC_Period_ID(line.getPeriod_Id());
				mForecastLine.setQty(line.getQuantity());
				mForecastLine.setSalesRep_ID(getAD_User_ID());
				mForecastLine.setM_Warehouse_ID(M_Warehouse_ID);
				mForecastLine.save();
			}		
		}
		return mForecast;
	}

	private List<ForcastLineData> getListOfProductsFromT_Replenish(int ad_PInstance_ID) {
		
		List<ForcastLineData> lf=new ArrayList<ForcastLineData>();
		String sql="SELECT *FROM T_Replenish WHERE AD_PInstance_ID="+ad_PInstance_ID;
		
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				Integer M_Product_ID=rs.getInt("M_Product_ID");
				BigDecimal qtytoorder=rs.getBigDecimal("qtytoorder");
				ForcastLineData fl=new ForcastLineData(M_Product_ID,qtytoorder,C_Period_ID);
				lf.add(fl);
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close ();
			}
			catch (Exception e)
			{}
			pstmt = null;
		}
		return lf;
	}
	
	class ForcastLineData implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private Integer product_ID;
		private BigDecimal quantity=Env.ZERO;
		private Integer period_Id;
		
		public ForcastLineData() {}
		
		public ForcastLineData(Integer product_ID, BigDecimal quantity, Integer period_Id) {
			super();
			this.product_ID = product_ID;
			this.quantity = quantity;
			this.period_Id = period_Id;
		}
		public Integer getProduct_ID() {
			return product_ID;
		}
		public void setProduct_ID(Integer product_ID) {
			this.product_ID = product_ID;
		}
		public BigDecimal getQuantity() {
			return quantity;
		}
		public void setQuantity(BigDecimal quantity) {
			this.quantity = quantity;
		}
		public Integer getPeriod_Id() {
			return period_Id;
		}
		public void setPeriod_Id(Integer period_Id) {
			this.period_Id = period_Id;
		}
		
	}
	
	/**
	 * 	Fill Table
	 * 	@param wh warehouse
	 */
	private void fillTable (MWarehouse wh) throws Exception
	{
		String sql = "INSERT INTO T_Replenish "
			+ "(AD_PInstance_ID, M_Warehouse_ID, M_Product_ID, AD_Client_ID, AD_Org_ID,"
			+ " ReplenishType, Level_Min, Level_Max,"
			+ " C_BPartner_ID, Order_Min, Order_Pack, QtyToOrder, ReplenishmentCreate) "
			+ "SELECT " + getAD_PInstance_ID() 
				+ ", r.M_Warehouse_ID, r.M_Product_ID, r.AD_Client_ID, r.AD_Org_ID,"
			+ " r.ReplenishType, r.Level_Min, r.Level_Max,"
			+ " po.C_BPartner_ID, po.Order_Min, po.Order_Pack, 0, null ";
		
		sql += " FROM M_Replenish r"
			+ " INNER JOIN M_Product_PO po ON (r.M_Product_ID=po.M_Product_ID) "
			+ "WHERE po.IsCurrentVendor='Y'"	//	Only Current Vendor
			+ " AND r.ReplenishType<>'0'"
			+ " AND po.IsActive='Y' AND r.IsActive='Y'"
			+ " AND r.M_Warehouse_ID=" + M_Warehouse_ID;
		
		int no = DB.executeUpdate(sql, get_TrxName());
		log.finest(sql);
		log.fine("Insert (1) #" + no);
		
		
		
		sql = "UPDATE T_Replenish t SET "
			+ "QtyOnHand = (SELECT COALESCE(SUM(QtyOnHand),0) FROM M_Storage s, M_Locator l WHERE t.M_Product_ID=s.M_Product_ID"
				+ " AND l.M_Locator_ID=s.M_Locator_ID AND l.M_Warehouse_ID=t.M_Warehouse_ID),"
			+ "QtyReserved = (SELECT COALESCE(SUM(QtyReserved),0) FROM M_Storage s, M_Locator l WHERE t.M_Product_ID=s.M_Product_ID"
				+ " AND l.M_Locator_ID=s.M_Locator_ID AND l.M_Warehouse_ID=t.M_Warehouse_ID),"
			+ "QtyOrdered = (SELECT COALESCE(SUM(QtyOrdered),0) FROM M_Storage s, M_Locator l WHERE t.M_Product_ID=s.M_Product_ID"
				+ " AND l.M_Locator_ID=s.M_Locator_ID AND l.M_Warehouse_ID=t.M_Warehouse_ID)";
		
		sql += " WHERE AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Update #" + no);

		//	Delete inactive products and replenishments
		sql = "DELETE T_Replenish r "
			+ "WHERE (EXISTS (SELECT * FROM M_Product p "
				+ "WHERE p.M_Product_ID=r.M_Product_ID AND p.IsActive='N')"
			+ " OR EXISTS (SELECT * FROM M_Replenish rr "
				+ " WHERE rr.M_Product_ID=r.M_Product_ID AND rr.IsActive='N'"
				+ " AND rr.M_Warehouse_ID=" + M_Warehouse_ID + " ))"
			+ " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Delete Inactive=" + no);
	 
		//	Ensure Data consistency
		sql = "UPDATE T_Replenish SET QtyOnHand = 0 WHERE QtyOnHand IS NULL";
		no = DB.executeUpdate(sql, get_TrxName());
		sql = "UPDATE T_Replenish SET QtyReserved = 0 WHERE QtyReserved IS NULL";
		no = DB.executeUpdate(sql, get_TrxName());
		sql = "UPDATE T_Replenish SET QtyOrdered = 0 WHERE QtyOrdered IS NULL";
		no = DB.executeUpdate(sql, get_TrxName());

		//	Set Minimum / Maximum Maintain Level
		//	X_M_Replenish.REPLENISHTYPE_ReorderBelowMinimumLevel
		sql = "UPDATE T_Replenish"
			+ " SET QtyToOrder = CASE WHEN QtyOnHand - QtyReserved + QtyOrdered <= Level_Min "
			+ " THEN Level_Max - QtyOnHand + QtyReserved - QtyOrdered "
			+ " ELSE 0 END "
			+ "WHERE ReplenishType='1'" 
			+ " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Update Type-1=" + no);
		//
		//	X_M_Replenish.REPLENISHTYPE_MaintainMaximumLevel
		sql = "UPDATE T_Replenish"
			+ " SET QtyToOrder = Level_Max - QtyOnHand + QtyReserved - QtyOrdered "
			+ "WHERE ReplenishType='2'" 
			+ " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Update Type-2=" + no);
	

		//	Minimum Order Quantity
		sql = "UPDATE T_Replenish"
			+ " SET QtyToOrder = Order_Min "
			+ "WHERE QtyToOrder < Order_Min"
			+ " AND QtyToOrder > 0" 
			+ " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Set MinOrderQty=" + no);

		//	Even dividable by Pack
		sql = "UPDATE T_Replenish"
			+ " SET QtyToOrder = QtyToOrder - MOD(QtyToOrder, Order_Pack) + Order_Pack "
			+ "WHERE MOD(QtyToOrder, Order_Pack) <> 0"
			+ " AND QtyToOrder > 0"
			+ " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Set OrderPackQty=" + no);
		
		//	Source from other warehouse
		if (wh.getM_WarehouseSource_ID() != 0)
		{
			sql = "UPDATE T_Replenish"
				+ " SET M_WarehouseSource_ID=" + wh.getM_WarehouseSource_ID() 
				+ " WHERE AD_PInstance_ID=" + getAD_PInstance_ID();
			no = DB.executeUpdate(sql, get_TrxName());
			if (no != 0)
				log.fine("Set Source Warehouse=" + no);
		}
		//	Check Source Warehouse
		sql = "UPDATE T_Replenish"
			+ " SET M_WarehouseSource_ID = NULL " 
			+ "WHERE M_Warehouse_ID=M_WarehouseSource_ID"
			+ " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Set same Source Warehouse=" + no);
		
		//	Custom Replenishment
		String className = wh.getReplenishmentClass();
		if (className != null && className.length() > 0)
		{	
			//	Get Replenishment Class
			ReplenishInterface custom = null;
			try
			{
				Class<?> clazz = Class.forName(className);
				custom = (ReplenishInterface)clazz.newInstance();
			}
			catch (Exception e)
			{
				throw new CyprusUserError("No custom Replenishment class "
						+ className + " - " + e.toString());
			}

			X_T_Replenish[] replenishs = getReplenish("ReplenishType='9'");
			for (int i = 0; i < replenishs.length; i++)
			{
				X_T_Replenish replenish = replenishs[i];
				if (replenish.getReplenishType().equals(X_T_Replenish.REPLENISHTYPE_Custom))
				{
					BigDecimal qto = null;
					try
					{
						qto = custom.getQtyToOrder(wh, replenish);
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, custom.toString(), e);
					}
					if (qto == null)
						qto = Env.ZERO;
					replenish.setQtyToOrder(qto);
					replenish.save();
				}
			}
		}
		//	Delete rows where nothing to order
		sql = "DELETE T_Replenish "
			+ "WHERE QtyToOrder < 1"
		    + " AND AD_PInstance_ID=" + getAD_PInstance_ID();
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Delete No QtyToOrder=" + no);
	}	//	fillTable
	
	
	
	private void prepareTable()
	{
		//	Level_Max must be >= Level_Max
		String sql = "UPDATE M_Replenish"
			+ " SET Level_Max = Level_Min "
			+ "WHERE Level_Max < Level_Min";
		int no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Corrected Max_Level=" + no);
		
		//	Minimum Order should be 1
		sql = "UPDATE M_Product_PO"
			+ " SET Order_Min = 1 "
			+ "WHERE Order_Min IS NULL OR Order_Min < 1";
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Corrected Order Min=" + no);
		
		//	Pack should be 1
		sql = "UPDATE M_Product_PO"
			+ " SET Order_Pack = 1 "
			+ "WHERE Order_Pack IS NULL OR Order_Pack < 1";
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Corrected Order Pack=" + no);

		//	Set Current Vendor where only one vendor
		sql = "UPDATE M_Product_PO p"
			+ " SET IsCurrentVendor='Y' "
			+ "WHERE IsCurrentVendor<>'Y'"
			+ " AND EXISTS (SELECT pp.M_Product_ID FROM M_Product_PO pp "
				+ "WHERE p.M_Product_ID=pp.M_Product_ID "
				+ "GROUP BY pp.M_Product_ID "
				+ "HAVING COUNT(*) = 1)";
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Corrected CurrentVendor(Y)=" + no);

		//	More then one current vendor
		sql = "UPDATE M_Product_PO p"
			+ " SET IsCurrentVendor='N' "
			+ "WHERE IsCurrentVendor = 'Y'"
			+ " AND EXISTS (SELECT pp.M_Product_ID FROM M_Product_PO pp "
				+ "WHERE p.M_Product_ID=pp.M_Product_ID AND pp.IsCurrentVendor='Y' "
				+ "GROUP BY pp.M_Product_ID "
				+ "HAVING COUNT(*) > 1)";
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 0)
			log.fine("Corrected CurrentVendor(N)=" + no);
		
		//	Just to be sure
//		sql = "DELETE T_Replenish WHERE AD_PInstance_ID=" + getAD_PInstance_ID();
//		no = DB.executeUpdate(sql, get_TrxName());
//		if (no != 0)
//			log.fine("Delete Existing Temp=" + no);
		
	}	//	prepareTable
	
	
	/**
	 * 	Get Replenish Records
	 *	@return replenish
	 */
	private X_T_Replenish[] getReplenish (String where)
	{
		String sql = "SELECT * FROM T_Replenish "
			+ "WHERE AD_PInstance_ID=? AND C_BPartner_ID > 0 ";
		if (where != null && where.length() > 0)
			sql += " AND " + where;
		sql	+= " ORDER BY M_Warehouse_ID, M_WarehouseSource_ID, C_BPartner_ID";
		ArrayList<X_T_Replenish> list = new ArrayList<X_T_Replenish>();
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, getAD_PInstance_ID());
			ResultSet rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add (new X_T_Replenish (getCtx(), rs, get_TrxName()));
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		X_T_Replenish[] retValue = new X_T_Replenish[list.size ()];
		list.toArray (retValue);
		return retValue;
	}	//	getReplenish

	
	private String checkValidation() {
		
		StringBuilder sb=new StringBuilder();
		String sql="SELECT count(*) FROM m_forecast mf "
				+ "  INNER JOIN m_forecastline mfl on (mfl.m_forecast_id=mf.m_forecast_id) "
				+ "  WHERE NAME=? AND C_Calendar_ID=? AND C_Year_ID=? AND mfl.C_Period_ID=? AND mfl.M_Warehouse_ID=?";
		
		int returnValue=DB.getSQLValue(get_TrxName(), sql, new Object[] {forecastName,C_Calendar_ID,C_Year_ID,C_Period_ID,M_Warehouse_ID});
		if(returnValue>0)
			sb.append("Combination of this forecast already exist");
		
		return sb.toString();
	}
	
}
