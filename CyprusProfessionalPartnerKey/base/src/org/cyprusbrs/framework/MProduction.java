package org.cyprusbrs.framework;

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.model.ModelValidationEngine;
import org.cyprusbrs.model.ModelValidator;
import org.cyprusbrs.model.ProductCost;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.DocAction;
import org.cyprusbrs.process.DocumentEngine;
import org.cyprusbrs.util.CCache;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Msg;
import org.cyprusbrs.util.QualityResultUtility;

public class MProduction extends X_M_Production implements DocAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5809636642895911343L;
	private String m_processMsg;
	protected int lineno;
	protected int count;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;
	/**	Cache						*/
	private static CCache<Integer,MProduction> s_cache = new CCache<Integer,MProduction>("M_Production", 5, 5);

	public static MProduction get (Properties ctx, int M_Production_ID)
	{
		Integer key = new Integer (M_Production_ID);
		MProduction retValue = s_cache.get (key);
		if (retValue != null)
			return retValue;
		retValue = new MProduction (ctx, M_Production_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (key, retValue);
		return retValue;
	} //	get

	

	/**
	 * 	Standard Constructor
	 *	@param ctx context 
	 *	@param M_Inventory_ID id
	 *	@param trxName transaction
	 */
	public MProduction (Properties ctx, int M_Production_ID, String trxName)
	{
		super (ctx, M_Production_ID, trxName);
	    this.m_processMsg = null;

		
	}	//	MInventory

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MProduction (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	    this.m_processMsg = null;

	}	//	MProduction

	
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		
		String sql = "SELECT IsQualityCheck FROM C_DocType WHERE C_DocType_ID=?";
		String IsQualityCheck = DB.getSQLValueString(null, sql, getC_DocType_ID());
		if("Y".equalsIgnoreCase(IsQualityCheck))
		{
			QualityResultUtility.saveQualityResult("PR", getM_Production_ID(), getM_Product_ID());
		}
		
		return true;
	}//	beforeSave
	
	public MProductionLine[] getLines() {
		List<MProductionLine> list = new ArrayList<MProductionLine>();
		
		String sql = "SELECT pl.M_ProductionLine_ID "
			+ "FROM M_ProductionLine pl "
			+ "WHERE pl.M_Production_ID = ? "
			+ "ORDER BY pl.Line, pl.M_ProductionLine_ID ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add( new MProductionLine( getCtx(), rs.getInt(1), get_TrxName() ) );	
		}
		catch (SQLException ex)
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
			throw new CyprusException("Unable to load production lines", ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		MProductionLine[] retValue = new MProductionLine[list.size()];
		list.toArray(retValue);
		return retValue;
	}
	
	@Override
	public boolean processIt(String processAction) {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}

	@Override
	public boolean unlockIt() {
		log.info("unlockIt - " + toString());
		setProcessing(false);
		return true;
	}

	@Override
	public boolean invalidateIt() {
		log.info(toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}

	@Override
	public String prepareIt() {
		log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Std Period open?
		MPeriod.testPeriodOpen(getCtx(), getMovementDate(), X_C_DocType.DOCBASETYPE_MaterialProduction, getAD_Org_ID());
		MProductionLine[] lines = getLines();
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		//	TODO: Add up Amounts
	//	setApprovalAmt();
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		log.info(toString());
		setIsApproved(true);
		return true;
	}

	@Override
	public boolean rejectIt() {
		log.info("rejectIt - " + toString());
	   // setIsApproved(false); // Field is not exist in Production window..
	    return true;
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
		
		MDocType dt = new MDocType(getCtx(), getC_DocType_ID(), get_TrxName());	
		if(dt.get_ValueAsBoolean("IsQualityCheck"))
		{
			int count = DB.getSQLValue(null, "SELECT Count(*) FROM QC_ProductionQualityResult WHERE M_Production_ID=" + getM_Production_ID());
			if(count <= 0)
			{
				return  m_processMsg = Msg.getMsg(getCtx(), "Cyprus_ProductionQualityCheck");
			}
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		log.info(toString());
		MProductionLine[] lines = getLines();
		StringBuffer info = new StringBuffer();

//		String whereClause = "M_Production_ID=? ";
//		List<MProductionLine> lines = new Query(getCtx(), MProductionLine.Table_Name , whereClause, get_TrxName())
//				.setParameters(this.getM_Production_ID())
//				.setOrderBy("Line, M_Product_ID")
//				.list();
		
//		for (X_M_ProductionPlan pp :lines)
//		{	
//
//			whereClause = "M_ProductionPlan_ID= ? ";
//			List<MProductionLine> production_lines = new Query(getCtx(), MProductionLine.Table_Name , whereClause, get_TrxName())
//					.setParameters(pp.getM_ProductionPlan_ID())
//					.setOrderBy("Line")
//					.list();
			for (MProductionLine pline : lines)
			{
				//Ignore the Material Policy when is Reverse Correction 
				if(!isReversal())
					checkMaterialPolicy(pline);
				
				//Stock Movement - Counterpart MOrder.reserveStock
				MProduct product = pline.getProduct();
				if (product != null && product.isStocked() )
				{
					log.fine("Material Transaction");
					MTransaction mtrx = null;
					MLocator locator = MLocator.get(getCtx(), pline.getM_Locator_ID());
					String MovementType = X_M_Transaction.MOVEMENTTYPE_ProductionPlus;					
					BigDecimal MovementQty = pline.getMovementQty();
					System.out.println(" Locator "+pline.getM_Locator_ID()+" "+MovementQty);
					
					//If AttributeSetInstance = Zero then create new  AttributeSetInstance use Inventory Line MA else use current AttributeSetInstance
					if (pline.getM_AttributeSetInstance_ID() == 0)
					{
						if (MovementQty.signum() == 0)
							continue ;
						else if(MovementQty.signum() < 0)
						{
							BigDecimal QtyAvailable = MStorage.getQtyAvailable(
									locator.getM_Warehouse_ID(), 
									locator.getM_Locator_ID(), 
									pline.getM_Product_ID(), 
									pline.getM_AttributeSetInstance_ID(),
									get_TrxName());
							System.out.println(" QtyAvailable "+QtyAvailable+" M_Warehouse_ID "+locator.getM_Warehouse_ID());
							if(QtyAvailable.add(MovementQty).signum() < 0)
							{	
								this.m_processMsg = "@NotEnoughStocked@";
								return DocAction.STATUS_InProgress;
							}
							MovementType = X_M_Transaction.MOVEMENTTYPE_Production_;
						}
						
						MProductionLineMA mas[] = MProductionLineMA.get(getCtx(),
								pline.getM_ProductionLine_ID(), get_TrxName());
						for (int j = 0; j < mas.length; j++)
						{
							MProductionLineMA ma = mas[j];
							if (!MStorage.add(getCtx(), locator.getM_Warehouse_ID(),
									locator.getM_Locator_ID(),
									pline.getM_Product_ID(), 
									ma.getM_AttributeSetInstance_ID(), 0 , 
									ma.getMovementQty().negate(), 
									Env.ZERO,
									Env.ZERO,
									get_TrxName()))
							{
								this.m_processMsg = "Cannot correct Inventory";
								log.severe("Cannot correct Inventory");
								return DocAction.STATUS_InProgress;
							}

							//Create Transaction
							mtrx = new MTransaction (getCtx(), pline.getAD_Org_ID(), 
									MovementType, locator.getM_Locator_ID(),
									pline.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), 
									ma.getMovementQty().negate(),
									this.getMovementDate(), get_TrxName());
							mtrx.setM_ProductionLine_ID(pline.getM_ProductionLine_ID());
							if (!mtrx.save())
							{
								m_processMsg = "Transaction To not inserted";
								return DocAction.STATUS_Invalid;
							}

							// Updated by Mukesh @20240713 regarding costing while complete button
							if(ma.getMovementQty().signum() != 0)
							{	
								String err = createCostDetail(pline,ma.getM_AttributeSetInstance_ID(), ma.getMovementQty().negate());
								if (err != null && err.length() > 0) {
									m_processMsg = err;
									return DocAction.STATUS_Invalid;
								}
							}

							pline.setProcessed(true);
							pline.saveEx();
						}
					}// For AttributeMA
					
//					Fallback - We have ASI // Inventory IN
					if (mtrx == null)
					{
						//Fallback: Update Storage - see also VMatch.createMatchRecord
						if (!MStorage.add(getCtx(), locator.getM_Warehouse_ID(),
								locator.getM_Locator_ID(),
								pline.getM_Product_ID(), 
								pline.getM_AttributeSetInstance_ID(), 0, 
								MovementQty, Env.ZERO, Env.ZERO, get_TrxName()))
						{
							m_processMsg = "Cannot correct Inventory (MA)";
							return DocAction.STATUS_Invalid;
						}
						
						//Create Transaction
						mtrx = new MTransaction (getCtx(), pline.getAD_Org_ID(), 
								MovementType, locator.getM_Locator_ID(),
								pline.getM_Product_ID(), pline.getM_AttributeSetInstance_ID(), 
								MovementQty,
								this.getMovementDate(), get_TrxName());
						mtrx.setM_ProductionLine_ID(pline.getM_ProductionLine_ID());
						if (!mtrx.save())
						{
							m_processMsg = "Transaction To not inserted";
							return DocAction.STATUS_Invalid;
						}
						
						// Updated by Mukesh @20240713 regarding costing while complete button
						if(MovementQty.signum()> 0)
						{	
							String err = createCostDetail(pline,pline.getM_AttributeSetInstance_ID(), MovementQty);
							if (err != null && err.length() > 0) {
								m_processMsg = err;
								return DocAction.STATUS_Invalid;
							}
						}
						pline.setProcessed(true);
						pline.saveEx();
						
					}// End of Fallback
					
				} // Production Line

//				pp.setProcessed(true);
//				pp.saveEx();
//			} // product stock
		}
//			User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		
		setProcessed(true);	
		m_processMsg = info.toString();
		//
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
		
		// We can set the logic while void the transaction 
		if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus()))
			{
				m_processMsg = "Document Closed: " + getDocStatus();
				return false;
			}
		
//		Not Processed
			if (DOCSTATUS_Drafted.equals(getDocStatus())
				|| DOCSTATUS_Invalid.equals(getDocStatus())
				|| DOCSTATUS_InProgress.equals(getDocStatus())
				|| DOCSTATUS_Approved.equals(getDocStatus())
				|| DOCSTATUS_NotApproved.equals(getDocStatus()) )
			{
				//	Set lines to 0
				String whereClause = "M_Production_ID=? ";
				List<MProductionPlan> lines = new Query(getCtx(), I_M_ProductionPlan.Table_Name , whereClause, get_TrxName())
						.setParameters(this.getM_Production_ID())
						.setOrderBy("Line, M_Product_ID")
						.list();
				for (MProductionPlan pp :lines)
				{	

					BigDecimal old = pp.getProductionQty();
					if (old.compareTo(Env.ZERO) != 0)
					{
						pp.setProductionQty(Env.ZERO);
						pp.addDescription("Void (" + old + ")");
						pp.save(get_TrxName());
					}
					whereClause = "M_ProductionPlan_ID= ? ";
					List<MProductionLine> production_lines = new Query(getCtx(), I_M_ProductionLine.Table_Name , whereClause, get_TrxName())
							.setParameters(pp.getM_ProductionPlan_ID())
							.setOrderBy("Line")
							.list();

					for (MProductionLine pline : production_lines)
					{
						BigDecimal oldQty = pline.getMovementQty();
						if (oldQty.compareTo(Env.ZERO) != 0)
						{
							pline.setMovementQty(Env.ZERO);
							pline.addDescription("Void (" + oldQty + ")");
							pline.save(get_TrxName());
						}		
					}
				}
			}
			else
			{
				return reverseCorrectIt();
			}
		
		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;
		
		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean closeIt() {

		log.info(toString());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;
		
		// We can set logic while close the document
		
		setProcessed(true);
		setDocAction(DOCACTION_None);
		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;
		return true;
	
	}

	@Override
	public boolean reverseCorrectIt() {
		log.info(toString());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;
		
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		MPeriod.testPeriodOpen(getCtx(), getMovementDate(), dt.getDocBaseType(), getAD_Org_ID());
		
//		Deep Copy
		MProduction reversal = new MProduction(getCtx(), 0, get_TrxName());
		copyValues(this, reversal, getAD_Client_ID(), getAD_Org_ID());
		reversal.setDocStatus(DOCSTATUS_Drafted);
		reversal.setDocAction(DOCACTION_Complete);
		reversal.setPosted(false);
		reversal.setProcessed(false);
		reversal.addDescription("{->" + getDocumentNo() + ")");
		//FR1948157
		reversal.setReversal_ID(getM_Production_ID()); 
		reversal.saveEx();
		reversal.setReversal(true);
		
		String whereClause = "M_Production_ID=? ";
		List<MProductionPlan> lines = new Query(getCtx(), I_M_ProductionPlan.Table_Name , whereClause, get_TrxName())
				.setParameters(this.getM_Production_ID())
				.setOrderBy("Line, M_Product_ID")
				.list();
		for (MProductionPlan pp :lines)
		{
			MProductionPlan rLine = new MProductionPlan(getCtx(), 0, get_TrxName());
			copyValues(pp, rLine, pp.getAD_Client_ID(), pp.getAD_Org_ID());
			
			rLine.setM_Production_ID(reversal.getM_Production_ID());
			rLine.setParent(reversal); 
			//AZ Goodwill
			// store original (voided/reversed) document line
			rLine.setReversalLine_ID(pp.getM_ProductionPlan_ID());
			//
			rLine.setProductionQty(pp.getProductionQty().negate());		
			rLine.saveEx();
			
			whereClause = "M_ProductionPlan_ID= ? ";
			List<MProductionLine> production_lines = new Query(getCtx(), I_M_ProductionLine.Table_Name , whereClause, get_TrxName())
					.setParameters(pp.getM_ProductionPlan_ID())
					.setOrderBy("Line")
					.list();

			for (MProductionLine pline : production_lines)
			{
				
				MProductionLine mpLine = new MProductionLine(getCtx(), 0, get_TrxName());
				copyValues(pline, mpLine, pline.getAD_Client_ID(), pline.getAD_Org_ID());
				mpLine.setM_ProductionPlan_ID(pline.getM_ProductionPlan_ID());
				mpLine.setParent(rLine); 
				//AZ Goodwill
				// store original (voided/reversed) document line
				mpLine.setReversalLine_ID(pline.getM_ProductionLine_ID());
				//
				mpLine.setMovementQty(pline.getMovementQty().negate());		
				mpLine.saveEx();
			}
		}
		
		//
		if (!reversal.processIt(DocAction.ACTION_Complete))
		{
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return false;
		}
		reversal.closeIt();
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx();
		m_processMsg = reversal.getDocumentNo();

		//	Update Reversed (this)
		addDescription("(" + reversal.getDocumentNo() + "<-)");
		
		
		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;
		
		setProcessed(true);
		//FR1948157
		setReversal_ID(reversal.getM_Production_ID());
		setDocStatus(DOCSTATUS_Reversed);	//	may come from void
		setDocAction(DOCACTION_None);
		
		return true;
	}

	@Override
	public boolean reverseAccrualIt() {
		log.info(toString());
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;
		
		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;
		
		return false;
	}

	@Override
	public boolean reActivateIt() {
		log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;	
		
		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;
		
		return false;
	}

	@Override
	public String getSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDocumentNo());
		//	: Total Lines = 123.00 (#1)
		sb.append(": ")
			.append(Msg.translate(getCtx(),"ApprovalAmt")).append("=").append(getApprovalAmt())
//			.append(" (#").append(getLines(false).length).append(")");
			.append(" (#").append(0).append(")");
		//	 - Description
		if (getDescription() != null && getDescription().length() > 0)
			sb.append(" - ").append(getDescription());
		return sb.toString();
	}

	@Override
	public String getDocumentInfo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		return dt.getName() + " " + getDocumentNo();
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
		// TODO Auto-generated method stub
		return getCreatedBy();
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return Env.ZERO;
	}

	///----------------------Extra method which help to complete or void the transaction----------
	
	public void addDescription(String description) {
	    String desc = getDescription();
	    if (desc == null) {
	      setDescription(description);
	    } else {
	      setDescription(desc + " | " + description);
	    } 
	  }

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
	//	ReportEngine re = ReportEngine.get (getCtx(), ReportEngine.INVOICE, getC_Invoice_ID());
	//	if (re == null)
			return null;
	//	return re.getPDF(file);
	}	//	createPDF
	
	/** Reversal Flag		*/
	private boolean m_reversal = false;
	
	/**
	 * 	Set Reversal
	 *	@param reversal reversal
	 */
	public void setReversal(boolean reversal)
	{
		m_reversal = reversal;
	}	//	setReversal
	/**
	 * 	Is Reversal
	 *	@return reversal
	 */
	@Override
	public boolean isReversal()
	{
		return m_reversal;
	}	//	isReversal
	
	
	/**
	 * 	Check Material Policy
	 * 	Sets line ASI
	 */
	private void checkMaterialPolicy(MProductionLine line)
	{
		int no = MProductionLineMA.deleteMovementMA(line.getM_ProductionLine_ID(), get_TrxName());
		if (no > 0)
			log.config("Delete old #" + no);
		
		boolean needSave = false;
		BigDecimal trxCost=Env.ZERO;
		
		System.out.println(line.getMovementQty());
		//	Attribute Set Instance
		// line.getMovementQty().compareTo(Env.ZERO)<0 This condition is use to create attributes transaction only for out qty from Stock
		if (line.getM_AttributeSetInstance_ID() == 0 && line.getMovementQty().compareTo(Env.ZERO)<0) 
		{
			MProduct product = MProduct.get(getCtx(), line.getM_Product_ID());
			String MMPolicy = product.getMMPolicy();
			MStorage[] storages = MStorage.getWarehouse(getCtx(), 0, line.getM_Product_ID(), 0, 
					null, X_AD_Client.MMPOLICY_FiFo.equals(MMPolicy), true, line.getM_Locator_ID(), get_TrxName());

			BigDecimal qtyToDeliver = line.getMovementQty().abs();

			for (MStorage storage: storages)
			{
				MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
				for(int asn = 0; asn < acctschemas.length; asn++)
				{
					MAcctSchema as = acctschemas[asn];
					String costingMethod = null;
					MProductCategoryAcct prca = MProductCategoryAcct.get(getCtx(), product.getM_Product_Category_ID(), as.getC_AcctSchema_ID(), get_TrxName());
					if(prca.getCostingMethod() != null)
					{
						costingMethod = prca.getCostingMethod();
					}
					else
					{
						costingMethod = as.getCostingMethod();
					}
					MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);

					if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0)
					{
						if(qtyToDeliver.compareTo(Env.ZERO)>0)
						{
							
							if(X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod))
							{
								trxCost = MCostQueue.getCosts(product, storage.getM_AttributeSetInstance_ID(), as, getAD_Org_ID(), ce, qtyToDeliver, get_TrxName());
							}
							else
							{								
								trxCost = MCost.getCurrentCost(product, storage.getM_AttributeSetInstance_ID(), as, line.getAD_Org_ID(), costingMethod, qtyToDeliver, 0, true, get_TrxName());
							}
							if(trxCost!=null && trxCost.compareTo(Env.ZERO)>0)
							{	
								MProductionLineMA ma = new MProductionLineMA (line, 
										storage.getM_AttributeSetInstance_ID(),
										qtyToDeliver);
								ma.set_Value("TrxCost", trxCost.divide(qtyToDeliver, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
								ma.saveEx();		
								qtyToDeliver = Env.ZERO;
								log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);
							}
						}
					}
					else
					{	
						
						if(X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod))
						{
							trxCost = MCostQueue.getCosts(product, storage.getM_AttributeSetInstance_ID(), as, getAD_Org_ID(), ce, qtyToDeliver, get_TrxName());
						}
						else
						{								
							trxCost = MCost.getCurrentCost(product, storage.getM_AttributeSetInstance_ID(), as, line.getAD_Org_ID(), costingMethod, qtyToDeliver, 0, true, get_TrxName());
						}
						if(trxCost!=null && trxCost.compareTo(Env.ZERO)>0)
						{
							MProductionLineMA ma = new MProductionLineMA (line, 
							storage.getM_AttributeSetInstance_ID(),
							storage.getQtyOnHand());
							ma.set_Value("TrxCost", trxCost.divide(qtyToDeliver, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
							ma.saveEx();	
							qtyToDeliver = qtyToDeliver.subtract(storage.getQtyOnHand());
							log.fine( ma + ", QtyToDeliver=" + qtyToDeliver);
						}
					}
					
					if (qtyToDeliver.signum() == 0)
						break;
				}
			}
							
			//	No AttributeSetInstance found for remainder
			if (qtyToDeliver.signum() != 0)
			{
				//deliver using new asi
				MAttributeSetInstance asi = MAttributeSetInstance.create(getCtx(), product, get_TrxName());
				int M_AttributeSetInstance_ID = asi.getM_AttributeSetInstance_ID();
				MProductionLineMA ma = new MProductionLineMA (line, M_AttributeSetInstance_ID , qtyToDeliver);
				ma.saveEx();
				log.fine("##: " + ma);
			}
		}	//	attributeSetInstance
		
		if (needSave)
		{
			line.saveEx();
		}
	}	//	checkMaterialPolicy
	
	
	/**
	 * Create Cost Detail on Complete button
	 * @param line
	 * @param Qty
	 * @return an EMPTY String on success otherwise an ERROR message
	 */
	private String createCostDetail(MProductionLine line, int M_AttributeSetInstance_ID, BigDecimal qty)
	{
		// Get Account Schemas to create MCostDetail
		MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
		for(int asn = 0; asn < acctschemas.length; asn++)
		{
			
			
			MAcctSchema as = acctschemas[asn];
			
			if (as.isSkipOrg(getAD_Org_ID()) || as.isSkipOrg(line.getAD_Org_ID()))
			{
				continue;
			}
			
			MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), line.get_TrxName());
			String costingMethod = product.getCostingMethod(as);
			
			BigDecimal costs = Env.ZERO;
			BigDecimal unitCost=Env.ZERO;
			
			if (isReversal())
			{				
				String sql = "SELECT amt * -1 FROM M_CostDetail WHERE M_InventoryLine_ID=?"; // negate costs				
//				MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), line.get_TrxName());
				String CostingLevel = product.getCostingLevel(as);
				if (X_C_AcctSchema.COSTINGLEVEL_Organization.equals(CostingLevel))
					sql = sql + " AND AD_Org_ID=" + getAD_Org_ID(); 
				else if (X_C_AcctSchema.COSTINGLEVEL_BatchLot.equals(CostingLevel) && M_AttributeSetInstance_ID != 0)
					sql = sql + " AND M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID;
				 costs = DB.getSQLValueBD(line.get_TrxName(), sql, line.getReversalLine_ID());
			}
			else 
			{
				if(qty.compareTo(Env.ZERO)>0) // for new Finish product
				{	
					// For all types of Costing method
					costs=unitCost=getCostForNewItems(line,as,costingMethod);
					costs=costs.multiply(qty);
					//unitCost=costs.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
				}
				else /// Component Product
				{
					if(qty.compareTo(Env.ZERO)!=0)
					{
						if(X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod))
						{
							BigDecimal toGetCost=getCost(line,as);
							unitCost=toGetCost;	
							costs=toGetCost.multiply(qty);
						}
						else // Non Lifo Fifo
						{
							ProductCost pc = new ProductCost (getCtx(), 
									line.getM_Product_ID(), M_AttributeSetInstance_ID, line.get_TrxName());
							pc.setQty(qty);
							costs = pc.getProductCosts(as, line.getAD_Org_ID(), as.getCostingMethod(), 0,true);
							unitCost=costs.abs().divide(qty.abs(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
						}
					}
				}
			}
			if (costs == null)
			{
				return "No Costs for " + new MProduct(getCtx(), line.getM_Product_ID(),null).getName();
			}
			
			// update TrxCost @Mukesh 20240731
			if(unitCost.signum()!=0)
			{
				// @ M_MovementLine
				int retVal=DB.executeUpdate("UPDATE M_ProductionLine SET TrxCost="+unitCost+" WHERE M_ProductionLine_ID="+line.getM_ProductionLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_MovementLine = " +retVal);
				
				// @ M_Transaction
				retVal=DB.executeUpdate("UPDATE M_Transaction SET TrxCost="+unitCost+" WHERE M_ProductionLine_ID="+line.getM_ProductionLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_Transaction = " +retVal);
			}
			
			
//			Cost Detail
			String description = line.getDescription();
			if (description == null)
				description = "";
			/*
			 * if (line.isProductionBOM()) description += "(*)";
			 */
			
			MCostDetail.createProduction(as, line.getAD_Org_ID(), 
				line.getM_Product_ID(), M_AttributeSetInstance_ID, 
				line.get_ID(), 0, 
				costs, qty, 
				description+"(*)", get_TrxName());
			
			// Code updated by Mukesh @20240726 regarding to update current cost price at Cost Details
			if(qty.signum()!=0 )// && (MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod)))
			{
			    MCostDetail cd = MCostDetail.get (as.getCtx(), "M_ProductionLine_ID=?", 
						line.getM_ProductionLine_ID(), M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), get_TrxName());
			    cd.setCurrentCostPrice(costs.abs().divide(qty.abs(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
			    cd.save();
			}
			
		}
		
		return "";
	}
	
	// Get Cost if for new item do not have any cost
	private BigDecimal getCostForNewItems(MProductionLine line, MAcctSchema as, String costingMethod) {
		
		BigDecimal costNew=Env.ZERO;
		try {
			
			System.out.println(line.getM_Production_ID());
		String sql="SELECT ABS(SUM((trxcost * movementqty))) FROM M_ProductionLine WHERE MovementQty<0 AND M_Production_ID=?";
		costNew=DB.getSQLValueBD(get_TrxName(), sql, line.getM_Production_ID());
		if(costNew!=null && costNew.compareTo(Env.ZERO)>0)
			costNew=costNew.divide(line.getMovementQty(),as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
		
		}catch(Exception e) {log.info("Do not fetch cost of new Item "+e.getMessage());}	
		return costNew;
	}

	// Get Cost Average from LIfo/Fofo cost
	public BigDecimal getCost(MProductionLine line, MAcctSchema as)
	{
		MProductionLineMA[] m_linesma = MProductionLineMA.get(getCtx(), line.getM_ProductionLine_ID(), get_TrxName());
		BigDecimal cost = Env.ZERO;
		BigDecimal qty = Env.ZERO;
		
		for(int ma = 0; ma < m_linesma.length; ma++)
		{
			MProductionLineMA lineMA = m_linesma[ma];
			cost = cost.add(lineMA.get_ValueAsBigDecimal("TrxCost").multiply(lineMA.getMovementQty()));
			qty=qty.add(lineMA.getMovementQty());
		}
		cost=cost.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);;
		return cost;
	}	//	getCost

	/**	Lines						*/
	private MProductionLine[]	m_lines = null;
}
