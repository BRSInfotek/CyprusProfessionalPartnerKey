package org.cyprusbrs.framework;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class MJobOrderLine extends X_C_JobOrderLine {
	
    private static final long serialVersionUID = 1L;

    private MJobLineComponents[]	m_lines = null;

    
    public MJobOrderLine(Properties ctx, int C_JobOrderLine_ID, String trxName) {
        super(ctx, C_JobOrderLine_ID, trxName);
    }

    public MJobOrderLine(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    @Override
    protected boolean beforeSave(boolean newRecord) {
    	
    	if (!newRecord && is_ValueChanged("QtyEntered")) {
            if (hasComponents()) {  // Check if components exist
                updateComponentQuantities();
            }
        }
    	
    	if (getC_Charge_ID() != 0)
		{
			this.set_ValueNoCheck("M_Product_ID", null);
		}
        return true;
    }
    
 // Method to update component quantities based on new QtyEntered value
    private void updateComponentQuantities() {
        String sql = "UPDATE C_JobLineComponents " +
                     "SET QtyEntered = QtyEntered / ? * ? " +
                     "WHERE C_JobOrderLine_ID=?";
        
        int updated = DB.executeUpdate(sql, new Object[]{
            getQtyEnteredOld(), // Old quantity to scale the update
            getQtyEntered(),    // New quantity
            get_ID()
        },true, get_TrxName());

        log.info("Updated Component Quantities: " + updated);
    }
    
 // Get the old QtyEntered value before update
    private BigDecimal getQtyEnteredOld() {
        String sql = "SELECT QtyEntered FROM C_JobOrderLine WHERE C_JobOrderLine_ID=?";
        return DB.getSQLValueBD(get_TrxName(), sql, get_ID());
    }
    
 // Method to check if C_JobLineComponents exist for this Job Order Line
    private boolean hasComponents() {
        String sql = "SELECT COUNT(*) FROM C_JobLineComponents WHERE C_JobOrderLine_ID=?";
        int count = DB.getSQLValue(get_TrxName(), sql, get_ID());
        return count > 0;
    }
    
    
    
    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (success) {
        	
        	
        	// Update header totals if this is a charge line
            if (getC_Charge_ID() > 0) {
                updateJobOrderHeaderTotals();
            }
        	
        	/// Check is Componenet Issue by Vendor at Header window
        	
        	String sql = "SELECT JobOrderType FROM C_JobOrder WHERE C_JobOrder_ID=?";
            String jobType=DB.getSQLValueString(get_TrxName(), sql, getC_JobOrder_ID());
        	
            if(MMFGRequisition.JOBORDERTYPE_ComponentIssuedByOrganization.equalsIgnoreCase(jobType))
            {
            	if (!newRecord && is_ValueChanged("PP_Product_BOM_ID")) {
            		deleteOldComponents(); // Remove old components
            		if (getPP_Product_BOM_ID() > 0) {
            			createBOMComponents(); // Insert new components
            		}
            	} else if (newRecord) {
            		createBOMComponents(); // Insert components for new order line
            	}

            	// update Current Cost price
            	String sqlcost="select sum(qtyconverted * currentcostprice) from C_JobLineComponents where C_JobOrderLine_ID=?";
            	BigDecimal costPrice=DB.getSQLValueBD(get_TrxName(), sqlcost, getC_JobOrderLine_ID());
            	if(costPrice!=null && costPrice.signum()>0)
            	{	
            		BigDecimal currentcostprice=costPrice.divide(getQtyConverted(), 3, BigDecimal.ROUND_HALF_UP);            		           		
//            			Update Order Header
            			String sqlCost = "UPDATE C_JobOrderLine i"
            				+ " SET currentcostprice= "+currentcostprice       					
            				+ " WHERE C_JobOrderLine_ID=" + getC_JobOrderLine_ID();
            			int no = DB.executeUpdate(sqlCost, get_TrxName());
            			if (no != 1)
            				log.warning("(1) #" + no);	           			
            	}
            	else
            	{
            		BigDecimal currentcostprice=Env.ZERO;           		           		
//        			Update Order Header
        			String sqlCost = "UPDATE C_JobOrderLine i"
        				+ " SET currentcostprice= "+currentcostprice       					
        				+ " WHERE C_JobOrderLine_ID=" + getC_JobOrderLine_ID();
        			int no = DB.executeUpdate(sqlCost, get_TrxName());
        			if (no != 1)
        				log.warning("(1) #" + no);
            	}
            }
            else
        	deleteOldComponents(); // Remove old components

        }
        
        
        return true;
    }

    @Override
    protected boolean beforeDelete() {
    	
    	if (getC_Charge_ID() > 0) {
            updateJobOrderHeaderTotals();
        }
    	
        deleteOldComponents();  // Remove old components before deletion
        return true;
    }

    // Method to delete old components when BOM changes
    private void deleteOldComponents() {
        String sql = "DELETE FROM C_JobLineComponents WHERE C_JobOrderLine_ID=?";
        int deleted = DB.executeUpdate(sql, new Object[]{get_ID()},true, get_TrxName());
        log.info("Deleted Components: " + deleted);
    }

    private void createBOMComponents() {
        String sql = "SELECT M_Product_ID, Line, C_UOM_ID, Description, QtyBOM " +
                     "FROM PP_Product_BOMLine WHERE PP_Product_BOM_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setInt(1, getPP_Product_BOM_ID());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("M_Product_ID");
                BigDecimal qtyBOM = rs.getBigDecimal("QtyBOM");

                if (qtyBOM == null) {
                    qtyBOM = BigDecimal.ZERO;
                }

                // Ensure getQtyEntered() and getQtyConverted() are not null
                BigDecimal qtyEntered = getQtyEntered() != null ? getQtyEntered() : BigDecimal.ONE;
                BigDecimal qtyConverted = getQtyConverted() != null ? getQtyConverted() : BigDecimal.ONE;

                MJobLineComponents component = new MJobLineComponents(getCtx(), 0, get_TrxName());
                component.setAD_Org_ID(getAD_Org_ID());
                component.setC_JobOrderLine_ID(get_ID()); // Fix: Use get_ID() instead of getC_JobOrderLine_ID()
                component.setLineNo(rs.getInt("Line"));
                component.setDescription(rs.getString("Description"));
                component.setM_Product_ID(productId);
                component.setQtyEntered(qtyBOM.multiply(qtyConverted));
                component.setC_UOM_ID(rs.getInt("C_UOM_ID"));
                component.setQtyConverted(qtyBOM.multiply(qtyConverted));
                component.set_ValueNoCheck("QtyComponent", qtyBOM);
              //  component.save(get_TrxName());
                if(component.save(get_TrxName()))
                		updateComponentCurrentCost(component); /// Zero value update
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        } finally {
            DB.close(rs, pstmt);
        }
    }
    
    public MJobLineComponents[] getLines (boolean requery)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		List<MInOutLine> list = new Query(getCtx(), I_C_JobLineComponents.Table_Name, "C_JobOrderLine_ID=?", get_TrxName())
		.setParameters(getC_JobOrderLine_ID())
		.list();
		//
		m_lines = new MJobLineComponents[list.size()];
		list.toArray(m_lines);
		return m_lines;
	}	//
    
    /**
     * Get List of Job Line Components for a given Job Order Line
     */
    public List<MJobLineComponents> getJobLineComponents(MJobOrderLine jobOrderLine) {
        List<MJobLineComponents> components = new ArrayList<>();
        String whereClause = "C_JobOrderLine_ID=?";

        List<MJobLineComponents> list = new Query(getCtx(), MJobLineComponents.Table_Name, whereClause, get_TrxName())
                .setParameters(jobOrderLine.getC_JobOrderLine_ID())
                .list();

        if (list != null) {
            components.addAll(list);
        }

        return components;
    }
    
    /**
	 * Create Cost Detail
	 * @param line
	 * @param Qty
	 * @return an EMPTY String on success otherwise an ERROR message
	 */
	private String updateComponentCurrentCost(MJobLineComponents line)
	{
	
		MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());

    
		for(int asn = 0; asn < acctschemas.length; asn++)
		{
			MAcctSchema as = acctschemas[asn];
		
			BigDecimal unitCost=Env.ZERO;
			BigDecimal costs = Env.ZERO;

			if (as.isSkipOrg(getAD_Org_ID()) || as.isSkipOrg(line.getAD_Org_ID()))
			{
				continue;
			}
			MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), line.get_TrxName());
			String costingMethod = product.getCostingMethod(as);
			
			if(line.getQtyConverted().signum()>0 && 
					 (MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod)))
			{
				MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
				MCostQueue[] costQ=MCostQueue.getQueue(product, 0, as, line.getAD_Org_ID(), ce, get_TrxName());
				unitCost=costQ[0].getCurrentCostPrice();
				costs=unitCost.multiply(line.getQtyConverted());
			}
			else if(line.getQtyConverted().signum()>0)
			{
				ProductCost pc = new ProductCost (getCtx(), 
				line.getM_Product_ID(), 0, line.get_TrxName());
				pc.setQty(line.getQtyConverted());
				costs = pc.getProductCosts(as, line.getAD_Org_ID(), costingMethod, 0,true);
				unitCost=costs.divide(line.getQtyConverted(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);	
			}
			if (costs.signum()> 0)
			{
				// @ M_InventoryLine
				int retVal=DB.executeUpdate("UPDATE C_JobLineComponents SET CurrentCostPrice="+Env.ZERO+" WHERE C_JobLineComponents_ID="+line.getC_JobLineComponents_ID(), get_TrxName() );
				log.fine("Current Cost updated at C_JobLineComponents = " +retVal);
				//return "No Costs for " + line.getProduct().getName();
			}
		}
    return "";
   }
	
	private void updateJobOrderHeaderTotals() {
	    String sql = "SELECT SUM(LineNetAmt) " +
	                 "FROM C_JobOrderLine " +
	                 "WHERE C_JobOrder_ID = ? AND C_Charge_ID IS NOT NULL";

	    BigDecimal LineNetAmt = DB.getSQLValueBD(get_TrxName(), sql, getC_JobOrder_ID());
	    if (LineNetAmt == null)
	    	LineNetAmt = Env.ZERO;
	    
	    String sqlTotal = "SELECT SUM(TotalLineNetAmt) " +
                "FROM C_JobOrderLine " +
                "WHERE C_JobOrder_ID = ? AND C_Charge_ID IS NOT NULL";

	   BigDecimal TotalLineNetAmt = DB.getSQLValueBD(get_TrxName(), sqlTotal, getC_JobOrder_ID());
	   if (TotalLineNetAmt == null)
		   TotalLineNetAmt = Env.ZERO;

	    String update = "UPDATE C_JobOrder " +
	                    "SET TotalLines = ?, GrandTotal = ? " +
	                    "WHERE C_JobOrder_ID = ?";
	    DB.executeUpdate(update, new Object[]{LineNetAmt, TotalLineNetAmt, getC_JobOrder_ID()}, true, get_TrxName());
	}

    
}
