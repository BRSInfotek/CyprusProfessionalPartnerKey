package org.cyprusbrs.framework;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.ModelValidationEngine;
import org.cyprusbrs.model.ModelValidator;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;

public class MJobOrder extends X_C_JobOrder implements DocAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**	Process Message 			*/
	private String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;
	
	private MJobOrderLine[]	m_lines = null;
	
	public MJobOrder(Properties ctx, int C_JobOrder_ID, String trxName) {
        super(ctx, C_JobOrder_ID, trxName);
    }
    
    public MJobOrder(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
    
    // Custom business logic for Job Order
    public void processJobOrder() {
        // Implement job order processing logic
    }

    /**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true or false
	 */
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String docDateStr = sdf.format(getDateOrdered());
		String reqDateStr = sdf.format(getDateRequired());
		if (reqDateStr.compareTo(docDateStr) < 0) {
			log.saveError("Error", "Job Order Date cannot be greater than Date Required.");
			return false;
		}
		
//		if (getDateOrdered() != null && getDateRequired() != null) {
//	        if (getDateOrdered().after(getDateRequired())) {
//	            log.saveError("Error", "Job Order Date cannot be greater than Date Required.");
//	            return false;
//	        }
//	    }	
		return true;
	}
    
	@Override
	public boolean processIt(String processAction) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}

	@Override
	public boolean unlockIt() {
		log.info(toString());
		setProcessed(false);
		return true;
	}

	@Override
	public boolean invalidateIt() {
		log.info(toString());
		return true;
	}

	@Override
	public String prepareIt() {
		log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		m_justPrepared = true;
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rejectIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String completeIt() {
//		Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		MJobOrderLine[] lines = getJobOrderLines(false);
		for (MJobOrderLine line : lines)
		{
			if (!line.isActive())
				continue;
			
			// Fetch list of MJobLineComponents related to the current Job Order Line
		    List<MJobLineComponents> components = line.getJobLineComponents(line);

		    for (MJobLineComponents component : components) {
		        String err = updateComponentCurrentCost(component);
		        if (err != null && err.length() > 0) {
		            m_processMsg = err;
		            return DocAction.STATUS_Invalid;
		        }
		    }
		}
				
		//
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
		
	}

	@Override
	public boolean voidIt() {
		log.info(toString());
		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		boolean ok_to_void = false;
		if (DOCSTATUS_Drafted.equals(getDocStatus()) 
			|| DOCSTATUS_Invalid.equals(getDocStatus()))
		{
			setProcessed(true);
			setDocAction(DOCACTION_None);
			ok_to_void = true;
		} else {
			return false;
		}
		
		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;
		
		return ok_to_void;
	}
	
	
	/**
	 * 	Set Processed.
	 * 	Propagate to Lines/Taxes
	 *	@param processed processed
	 */
	@Override
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		String set = "SET Processed='"
			+ (processed ? "Y" : "N")
			+ "' WHERE C_JobOrder_ID=" + getC_JobOrder_ID();
		int noLine = DB.executeUpdateEx("UPDATE C_JobOrderLine " + set, get_TrxName());		
		log.fine("setProcessed - " + processed + " - Lines=" + noLine );
	}	//	setProcessed

	@Override
	public boolean closeIt() {
		log.info(toString());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;

		boolean ok_to_close = false;
		if (DOCSTATUS_Completed.equals(getDocStatus())) 
		{
			setProcessed(true);
			setDocAction(DOCACTION_None);
			ok_to_close = true;
		} else {
			return false;
		}

		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;			

		return ok_to_close;
	}

	@Override
	public boolean reverseCorrectIt() {
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;
		
		boolean ok_correct = true;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;
		
		return ok_correct;
	}

	@Override
	public boolean reverseAccrualIt() {
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;
		
		boolean ok_reverse = true;

		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;
		
		return ok_reverse;
	}

	@Override
	public boolean reActivateIt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File createPDF() {
		try
		{
			File temp = File.createTempFile(get_TableName()+get_ID()+"_", ".pdf");
			return createPDF (temp);
		}
		catch (Exception e)
		{
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}

	@Override
	public String getProcessMsg() {
		return m_processMsg;
	}

	@Override
	public int getDoc_User_ID() {
		return getCreatedBy();

	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}
	public File createPDF (File file)
	{
	//	ReportEngine re = ReportEngine.get (getCtx(), ReportEngine.INVOICE, getC_Invoice_ID());
	//	if (re == null)
			return null;
	//	return re.getPDF(file);
	}	//	createPDF
	
	public MJobOrderLine[] getJobOrderLines (boolean requery)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		List<MJobOrderLine> list = new Query(getCtx(), I_C_JobOrderLine.Table_Name, "C_JobOrder_ID=?", get_TrxName())
		.setParameters(getC_JobOrder_ID())
		.list();
		//
		m_lines = new MJobOrderLine[list.size()];
		list.toArray(m_lines);
		return m_lines;
	}	//
	
	
	
	
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
					 (X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod)))
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
				int retVal=DB.executeUpdate("UPDATE C_JobLineComponents SET CurrentCostPrice="+unitCost.abs()+" WHERE C_JobLineComponents_ID="+line.getC_JobLineComponents_ID(), get_TrxName() );
				log.fine("Current Cost updated at C_JobLineComponents = " +retVal);
				//return "No Costs for " + line.getProduct().getName();
			}
		}
   
    return "";
   }
	
}
