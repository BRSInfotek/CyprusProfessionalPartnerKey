/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.cyprusbrs.framework;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

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

/**
 *  Physical Inventory Model
 *
 *  @author Jorg Janke
 *  @version $Id: MInventory.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 			<li>FR [ 1948157  ]  Is necessary the reference for document reverse
 * 			<li> FR [ 2520591 ] Support multiples calendar for Org 
 *			@see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962 	
 *  @author Armen Rizal, Goodwill Consulting
 * 			<li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 *  @see http://sourceforge.net/tracker/?func=detail&atid=879335&aid=1948157&group_id=176962
 */
public class MInventory extends X_M_Inventory implements DocAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7137974064086172763L;

	/**
	 * 	Get Inventory from Cache
	 *	@param ctx context
	 *	@param M_Inventory_ID id
	 *	@return MInventory
	 */
	public static MInventory get (Properties ctx, int M_Inventory_ID)
	{
		Integer key = new Integer (M_Inventory_ID);
		MInventory retValue = s_cache.get (key);
		if (retValue != null)
			return retValue;
		retValue = new MInventory (ctx, M_Inventory_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (key, retValue);
		return retValue;
	} //	get

	/**	Cache						*/
	private static CCache<Integer,MInventory> s_cache = new CCache<Integer,MInventory>("M_Inventory", 5, 5);


	/**
	 * 	Standard Constructor
	 *	@param ctx context 
	 *	@param M_Inventory_ID id
	 *	@param trxName transaction
	 */
	public MInventory (Properties ctx, int M_Inventory_ID, String trxName)
	{
		super (ctx, M_Inventory_ID, trxName);
		if (M_Inventory_ID == 0)
		{
		//	setName (null);
		//  setM_Warehouse_ID (0);		//	FK
			setMovementDate (new Timestamp(System.currentTimeMillis()));
			setDocAction (DOCACTION_Complete);	// CO
			setDocStatus (DOCSTATUS_Drafted);	// DR
			setIsApproved (false);
			setMovementDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
			setPosted (false);
			setProcessed (false);
		}
	}	//	MInventory

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MInventory (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MInventory

	/**
	 * Warehouse Constructor
	 * @param wh warehouse
	 * @deprecated since 3.5.3a . Please use {@link #MInventory(MWarehouse, String)}.
	 */
	@Deprecated
	public MInventory (MWarehouse wh)
	{
		this(wh, wh.get_TrxName());
	}	//	MInventory
	
	/**
	 * Warehouse Constructor
	 * @param wh
	 * @param trxName
	 */
	public MInventory (MWarehouse wh, String trxName)
	{
		this (wh.getCtx(), 0, trxName);
		setClientOrg(wh);
		setM_Warehouse_ID(wh.getM_Warehouse_ID());
	}
	
	
	/**	Lines						*/
	private MInventoryLine[]	m_lines = null;
	
	/**
	 * 	Get Lines
	 *	@param requery requery
	 *	@return array of lines
	 */
	public MInventoryLine[] getLines (boolean requery)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		List<MInventoryLine> list = new Query(getCtx(), I_M_InventoryLine.Table_Name, "M_Inventory_ID=?", get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(I_M_InventoryLine.COLUMNNAME_Line)
										.list();
		m_lines = list.toArray(new MInventoryLine[list.size()]);
		return m_lines;
	}	//	getLines
	
	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else
			setDescription(desc + " | " + description);
	}	//	addDescription
	
	/**
	 * 	Overwrite Client/Org - from Import.
	 * 	@param AD_Client_ID client
	 * 	@param AD_Org_ID org
	 */
	@Override
	public void setClientOrg (int AD_Client_ID, int AD_Org_ID)
	{
		super.setClientOrg(AD_Client_ID, AD_Org_ID);
	}	//	setClientOrg

	/**
	 * 	String Representation
	 *	@return info
	 */
	@Override
	public String toString ()
	{
		StringBuffer sb = new StringBuffer ("MInventory[");
		sb.append (get_ID())
			.append ("-").append (getDocumentNo())
			.append (",M_Warehouse_ID=").append(getM_Warehouse_ID())
			.append ("]");
		return sb.toString ();
	}	//	toString
	
	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	@Override
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		return dt.getName() + " " + getDocumentNo();
	}	//	getDocumentInfo

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	@Override
	public File createPDF ()
	{
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
	}	//	getPDF

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

	
	/**
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	@Override
	protected boolean beforeSave (boolean newRecord)
	{
		System.out.println("getC_DocType_ID() "+getC_DocType_ID());
		if (getC_DocType_ID() == 0)
		{
			MDocType types[] = MDocType.getOfDocBaseType(getCtx(), X_C_DocType.DOCBASETYPE_MaterialPhysicalInventory);
			if (types.length > 0)	//	get first
				setC_DocType_ID(types[0].getC_DocType_ID());
			else
			{
				log.saveError("Error", Msg.parseTranslation(getCtx(), "@NotFound@ @C_DocType_ID@"));
				return false;
			}
		}
		
		/// Check for new update by Mukesh @20201014
		if(getC_DocType_ID()>0)
		{
			MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
			if(dt.isInternalUse())
				setIsInternalUse(true);
		}
		/// End of code
		
		return true;
	}	//	beforeSave
	
	
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
		//
		final String sql = "UPDATE M_InventoryLine SET Processed=? WHERE M_Inventory_ID=?";
		int noLine = DB.executeUpdateEx(sql, new Object[]{processed, getM_Inventory_ID()}, get_TrxName());
		m_lines = null;
		log.fine("Processed=" + processed + " - Lines=" + noLine);
	}	//	setProcessed

	
	/**************************************************************************
	 * 	Process document
	 *	@param processAction document action
	 *	@return true if performed
	 */
	@Override
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}	//	processIt
	
	/**	Process Message 			*/
	private String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

	/**
	 * 	Unlock Document.
	 * 	@return true if success 
	 */
	@Override
	public boolean unlockIt()
	{
		log.info(toString());
		setProcessing(false);
		return true;
	}	//	unlockIt
	
	/**
	 * 	Invalidate Document
	 * 	@return true if success 
	 */
	@Override
	public boolean invalidateIt()
	{
		log.info(toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}	//	invalidateIt
	
	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid) 
	 */
	@Override
	public String prepareIt()
	{
		log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Std Period open?
		MPeriod.testPeriodOpen(getCtx(), getMovementDate(), X_C_DocType.DOCBASETYPE_MaterialPhysicalInventory, getAD_Org_ID());
		MInventoryLine[] lines = getLines(false);
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
	}	//	prepareIt
	
	/**
	 * 	Approve Document
	 * 	@return true if success 
	 */
	@Override
	public boolean  approveIt()
	{
		log.info(toString());
		setIsApproved(true);
		return true;
	}	//	approveIt
	
	/**
	 * 	Reject Approval
	 * 	@return true if success 
	 */
	@Override
	public boolean rejectIt()
	{
		log.info(toString());
		setIsApproved(false);
		return true;
	}	//	rejectIt
	
	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	@Override
	public String completeIt()
	{
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		log.info(toString());

		MInventoryLine[] lines = getLines(false);
		for (MInventoryLine line : lines)
		{
			if (!line.isActive())
				continue;

			MProduct product = line.getProduct();	

			//Get Quantity to Inventory Inernal Use
			BigDecimal qtyDiff = line.getQtyInternalUse().negate();
			//If Quantity to Inventory Internal Use = Zero Then is Physical Inventory  Else is  Inventory Internal Use 
			if (qtyDiff.signum() == 0)
				qtyDiff = line.getQtyCount().subtract(line.getQtyBook());

			//Ignore the Material Policy when is Reverse Correction
			if(!isReversal())
				checkMaterialPolicy(line, qtyDiff);

			//	Stock Movement - Counterpart MOrder.reserveStock
			if (product != null 
					&& product.isStocked() )
			{
				log.fine("Material Transaction");
				MTransaction mtrx = null; 

				//If AttributeSetInstance = Zero then create new  AttributeSetInstance use Inventory Line MA else use current AttributeSetInstance
				if (line.getM_AttributeSetInstance_ID() == 0 || qtyDiff.compareTo(Env.ZERO) == 0)
				{
					MInventoryLineMA mas[] = MInventoryLineMA.get(getCtx(),
							line.getM_InventoryLine_ID(), get_TrxName());

					for (int j = 0; j < mas.length; j++)
					{
						MInventoryLineMA ma = mas[j];
						BigDecimal QtyMA = ma.getMovementQty();
						BigDecimal QtyNew = QtyMA.add(qtyDiff);
						log.fine("Diff=" + qtyDiff 
								+ " - Instance OnHand=" + QtyMA + "->" + QtyNew);

						if (!MStorage.add(getCtx(), getM_Warehouse_ID(),
								line.getM_Locator_ID(),
								line.getM_Product_ID(), 
								ma.getM_AttributeSetInstance_ID(), 0, 
								QtyMA.negate(), Env.ZERO, Env.ZERO, get_TrxName()))
						{
							m_processMsg = "Cannot correct Inventory (MA)";
							return DocAction.STATUS_Invalid;
						}

						// Only Update Date Last Inventory if is a Physical Inventory
						if(line.getQtyInternalUse().compareTo(Env.ZERO) == 0)
						{	
							MStorage storage = MStorage.get(getCtx(), line.getM_Locator_ID(), 
									line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(), get_TrxName());						
							storage.setDateLastInventory(getMovementDate());
							if (!storage.save(get_TrxName()))
							{
								m_processMsg = "Storage not updated(2)";
								return DocAction.STATUS_Invalid;
							}
						}

						String m_MovementType =null;
						if(QtyMA.negate().compareTo(Env.ZERO) > 0 )
							m_MovementType = X_M_Transaction.MOVEMENTTYPE_InventoryIn;
						else
							m_MovementType = X_M_Transaction.MOVEMENTTYPE_InventoryOut;
						//	Transaction
						mtrx = new MTransaction (getCtx(), line.getAD_Org_ID(), m_MovementType,
								line.getM_Locator_ID(), line.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
								QtyMA.negate(), getMovementDate(), get_TrxName());
						
							mtrx.setM_InventoryLine_ID(line.getM_InventoryLine_ID());
							if (!mtrx.save())
							{
								m_processMsg = "Transaction not inserted(2)";
								return DocAction.STATUS_Invalid;
							}
							if(QtyMA.signum() != 0)
							{	
								String err = createCostDetail(line, ma.getM_AttributeSetInstance_ID() , QtyMA.negate());
								if (err != null && err.length() > 0) {
									m_processMsg = err;
									return DocAction.STATUS_Invalid;
								}
							}
							
							qtyDiff = QtyNew;						

					}	
				}

				//sLine.getM_AttributeSetInstance_ID() != 0
				// Fallback
				if (mtrx == null)
				{
					//Fallback: Update Storage - see also VMatch.createMatchRecord
					if (!MStorage.add(getCtx(), getM_Warehouse_ID(),
							line.getM_Locator_ID(),
							line.getM_Product_ID(), 
							line.getM_AttributeSetInstance_ID(), 0, 
							qtyDiff, Env.ZERO, Env.ZERO, get_TrxName()))
					{
						m_processMsg = "Cannot correct Inventory (MA)";
						return DocAction.STATUS_Invalid;
					}

					// Only Update Date Last Inventory if is a Physical Inventory
					if(line.getQtyInternalUse().compareTo(Env.ZERO) == 0)
					{	
						MStorage storage = MStorage.get(getCtx(), line.getM_Locator_ID(), 
								line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(), get_TrxName());						

						storage.setDateLastInventory(getMovementDate());
						if (!storage.save(get_TrxName()))
						{
							m_processMsg = "Storage not updated(2)";
							return DocAction.STATUS_Invalid;
						}
					}

					String m_MovementType = null;
					if(qtyDiff.compareTo(Env.ZERO) > 0 )
						m_MovementType = X_M_Transaction.MOVEMENTTYPE_InventoryIn;
					else
						m_MovementType = X_M_Transaction.MOVEMENTTYPE_InventoryOut;
					//	Transaction
					mtrx = new MTransaction (getCtx(), line.getAD_Org_ID(), m_MovementType,
							line.getM_Locator_ID(), line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							qtyDiff, getMovementDate(), get_TrxName());
					mtrx.setM_InventoryLine_ID(line.getM_InventoryLine_ID());
					if (!mtrx.save())
					{
						m_processMsg = "Transaction not inserted(2)";
						return DocAction.STATUS_Invalid;
					}
					
					if(qtyDiff.signum() != 0)
					{	
						String err = createCostDetail(line, line.getM_AttributeSetInstance_ID(), qtyDiff);
						if (err != null && err.length() > 0) {
							m_processMsg = err;
							return DocAction.STATUS_Invalid;
						}
					}
				}	//	Fallback
			}	//	stock movement

		}	//	for all lines

		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		
		

		// Set the definite document number after completed (if needed)
		setDefiniteDocumentNo();

		//
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	/**
	 * 	Set the definite document number after completed
	 */
	private void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setMovementDate(new Timestamp (System.currentTimeMillis()));
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
			if (value != null)
				setDocumentNo(value);
		}
	}

	/**
	 * 	Check Material Policy.
	 */
	private void checkMaterialPolicy(MInventoryLine line, BigDecimal qtyDiff)
	{
		int no = MInventoryLineMA.deleteInventoryLineMA(line.getM_InventoryLine_ID(), get_TrxName());
		if (no > 0)
			log.config("Delete old #" + no);

		BigDecimal trxCost=Env.ZERO;
		//	Check Line
		boolean needSave = false;
		//	Attribute Set Instance
		if (line.getM_AttributeSetInstance_ID() == 0)
		{
			MProduct product = MProduct.get(getCtx(), line.getM_Product_ID());
			if (qtyDiff.signum() > 0)	//	Incoming Trx
			{
				MAttributeSetInstance asi = null;
				//auto balance negative on hand
				MStorage[] storages = MStorage.getWarehouse(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0,
						null, X_AD_Client.MMPOLICY_FiFo.equals(product.getMMPolicy()), false, line.getM_Locator_ID(), get_TrxName());
				for (MStorage storage : storages)
				{
					if (storage.getQtyOnHand().signum() < 0)
					{
						asi = new MAttributeSetInstance(getCtx(), storage.getM_AttributeSetInstance_ID(), get_TrxName());
						break;
					}
				}
				if (asi == null)
				{
					asi = MAttributeSetInstance.create(getCtx(), product, get_TrxName());
				}
				line.setM_AttributeSetInstance_ID(asi.getM_AttributeSetInstance_ID());
				needSave = true;
			}
			else	//	Outgoing Trx
			{
				String MMPolicy = product.getMMPolicy();
				MStorage[] storages = MStorage.getWarehouse(getCtx(), getM_Warehouse_ID(), line.getM_Product_ID(), 0,
						null, X_AD_Client.MMPOLICY_FiFo.equals(MMPolicy), true, line.getM_Locator_ID(), get_TrxName());

				BigDecimal qtyToDeliver = qtyDiff.negate();

				for (MStorage storage: storages)
				{
					MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), getAD_Client_ID());
					for(int asn = 0; asn < acctschemas.length; asn++)
					{
						MAcctSchema as = acctschemas[asn];
						String costingMethod = null;
						//							MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), get_TrxName());
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
									MInventoryLineMA ma = new MInventoryLineMA (line, 
											storage.getM_AttributeSetInstance_ID(),
											qtyToDeliver);
									ma.setTrxCost(trxCost.divide(qtyToDeliver));
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
								MInventoryLineMA ma = new MInventoryLineMA (line, 
								storage.getM_AttributeSetInstance_ID(),
								storage.getQtyOnHand());
								ma.setTrxCost(trxCost.divide(qtyToDeliver));
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
					MInventoryLineMA ma = new MInventoryLineMA (line, M_AttributeSetInstance_ID , qtyToDeliver);

					ma.saveEx();
					log.fine("##: " + ma);
				}
			}	//	outgoing Trx

			if (needSave)
			{
				line.saveEx();
			}
		}	//	for all lines

	}	//	checkMaterialPolicy

	/**
	 * 	Void Document.
	 * 	@return false 
	 */
	@Override
	public boolean voidIt()
	{
		log.info(toString());
		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;
		
		if (DOCSTATUS_Closed.equals(getDocStatus())
			|| DOCSTATUS_Reversed.equals(getDocStatus())
			|| DOCSTATUS_Voided.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			return false;
		}

		//	Not Processed
		if (DOCSTATUS_Drafted.equals(getDocStatus())
			|| DOCSTATUS_Invalid.equals(getDocStatus())
			|| DOCSTATUS_InProgress.equals(getDocStatus())
			|| DOCSTATUS_Approved.equals(getDocStatus())
			|| DOCSTATUS_NotApproved.equals(getDocStatus()) )
		{
			//	Set lines to 0
			MInventoryLine[] lines = getLines(false);
			for (int i = 0; i < lines.length; i++)
			{
				MInventoryLine line = lines[i];
				BigDecimal oldCount = line.getQtyCount();
				BigDecimal oldInternal = line.getQtyInternalUse();
				if (oldCount.compareTo(line.getQtyBook()) != 0 
					|| oldInternal.signum() != 0)
				{
					line.setQtyInternalUse(Env.ZERO);
					line.setQtyCount(line.getQtyBook());
					line.addDescription("Void (" + oldCount + "/" + oldInternal + ")");
					line.save(get_TrxName());
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
	}	//	voidIt
	
	/**
	 * 	Close Document.
	 * 	@return true if success 
	 */
	@Override
	public boolean closeIt()
	{
		log.info(toString());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_None);
		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;
		return true;
	}	//	closeIt
	
	/**
	 * 	Reverse Correction
	 * 	@return false 
	 */
	@Override
	public boolean reverseCorrectIt()
	{
		log.info(toString());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		MPeriod.testPeriodOpen(getCtx(), getMovementDate(), dt.getDocBaseType(), getAD_Org_ID());

		//	Deep Copy
		MInventory reversal = new MInventory(getCtx(), 0, get_TrxName());
		copyValues(this, reversal, getAD_Client_ID(), getAD_Org_ID());
		reversal.setDocStatus(DOCSTATUS_Drafted);
		reversal.setDocAction(DOCACTION_Complete);
		reversal.setIsApproved (false);
		reversal.setPosted(false);
		reversal.setProcessed(false);
		reversal.addDescription("{->" + getDocumentNo() + ")");
		//FR1948157
		reversal.setReversal_ID(getM_Inventory_ID());
		reversal.saveEx();
		reversal.setReversal(true);

		//	Reverse Line Qty
		MInventoryLine[] oLines = getLines(true);
		for (int i = 0; i < oLines.length; i++)
		{
			MInventoryLine oLine = oLines[i];
			MInventoryLine rLine = new MInventoryLine(getCtx(), 0, get_TrxName());
			copyValues(oLine, rLine, oLine.getAD_Client_ID(), oLine.getAD_Org_ID());
			rLine.setM_Inventory_ID(reversal.getM_Inventory_ID());
			rLine.setParent(reversal);
			//AZ Goodwill
			// store original (voided/reversed) document line
			rLine.setReversalLine_ID(oLine.getM_InventoryLine_ID());
			//
			rLine.setQtyBook (oLine.getQtyCount());		//	switch
			rLine.setQtyCount (oLine.getQtyBook());
			rLine.setQtyInternalUse (oLine.getQtyInternalUse().negate());		
			
			rLine.saveEx();

			//We need to copy MA
			if (rLine.getM_AttributeSetInstance_ID() == 0)
			{
				MInventoryLineMA mas[] = MInventoryLineMA.get(getCtx(),
						oLines[i].getM_InventoryLine_ID(), get_TrxName());
				for (int j = 0; j < mas.length; j++)
				{
					MInventoryLineMA ma = new MInventoryLineMA (rLine, 
							mas[j].getM_AttributeSetInstance_ID(),
							mas[j].getMovementQty().negate());
					ma.saveEx();
				}
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
		setReversal_ID(reversal.getM_Inventory_ID());
		setDocStatus(DOCSTATUS_Reversed);	//	may come from void
		setDocAction(DOCACTION_None);

		return true;
	}	//	reverseCorrectIt
	
	/**
	 * 	Reverse Accrual
	 * 	@return false 
	 */
	@Override
	public boolean reverseAccrualIt()
	{
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
	}	//	reverseAccrualIt
	
	/** 
	 * 	Re-activate
	 * 	@return false 
	 */
	@Override
	public boolean reActivateIt()
	{
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
	}	//	reActivateIt
	
	
	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	@Override
	public String getSummary()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getDocumentNo());
		//	: Total Lines = 123.00 (#1)
		sb.append(": ")
			.append(Msg.translate(getCtx(),"ApprovalAmt")).append("=").append(getApprovalAmt())
			.append(" (#").append(getLines(false).length).append(")");
		//	 - Description
		if (getDescription() != null && getDescription().length() > 0)
			sb.append(" - ").append(getDescription());
		return sb.toString();
	}	//	getSummary
	
	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	@Override
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg
	
	/**
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	@Override
	public int getDoc_User_ID()
	{
		return getUpdatedBy();
	}	//	getDoc_User_ID
	
	/**
	 * 	Get Document Currency
	 *	@return C_Currency_ID
	 */
	@Override
	public int getC_Currency_ID()
	{
	//	MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID());
	//	return pl.getC_Currency_ID();
		return 0;
	}	//	getC_Currency_ID
	
	/** Reversal Flag		*/
	private boolean m_reversal = false;
	
	/**
	 * 	Set Reversal
	 *	@param reversal reversal
	 */
	private void setReversal(boolean reversal)
	{
		m_reversal = reversal;
	}	//	setReversal
	/**
	 * 	Is Reversal
	 *	@return reversal
	 */
	private boolean isReversal()
	{
		return m_reversal;
	}	//	isReversal
	
	/**
	 * Create Cost Detail
	 * @param line
	 * @param Qty
	 * @return an EMPTY String on success otherwise an ERROR message
	 */
	private String createCostDetail(MInventoryLine line, int M_AttributeSetInstance_ID, BigDecimal qty)
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
			BigDecimal trxcost=Env.ZERO;
			Boolean checkUpdatedCost=false;
			BigDecimal additionalCost = Env.ZERO;

			if (isReversal())
			{				
				String sql = "SELECT amt * -1 FROM M_CostDetail WHERE M_InventoryLine_ID=?"; // negate costs				
				String CostingLevel = product.getCostingLevel(as);
			//	MProduct product = new MProduct(getCtx(), line.getM_Product_ID(), line.get_TrxName());
				if (X_C_AcctSchema.COSTINGLEVEL_Organization.equals(CostingLevel))
					sql = sql + " AND AD_Org_ID=" + getAD_Org_ID(); 
				else if (X_C_AcctSchema.COSTINGLEVEL_BatchLot.equals(CostingLevel) && M_AttributeSetInstance_ID != 0)
					sql = sql + " AND M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID;
				costs = DB.getSQLValueBD(line.get_TrxName(), sql, line.getReversalLine_ID());
			}
			else if(line.getCurrentCostPrice()!=null && line.getCurrentCostPrice().signum()>0)
			{
				//BigDecimal qtyDiff = line.getQtyCount().subtract(line.getQtyBook());
				costs=line.getCurrentCostPrice().multiply(qty);
				unitCost=costs;
				trxcost=costs;
				checkUpdatedCost=true;
			}
			else 
			{	
				if(line.getQtyCount().compareTo(line.getQtyBook())>0 && 
				 (X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod)))
				{
					MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
					MCostQueue[] costQ=MCostQueue.getQueue(product, 0, as, line.getAD_Org_ID(), ce, get_TrxName());
					unitCost=costQ[0].getCurrentCostPrice();
					costs=unitCost.multiply(qty);
				}
				if(line.getQtyCount().compareTo(line.getQtyBook())<0 && 
				(X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod)))
				{
					BigDecimal toGetCost=getCost(line,as);
					unitCost=toGetCost;	
					costs=toGetCost.multiply(qty);
					
					String sql = "SELECT " +
							"COALESCE(SUM(c.CurrentCostPrice),0) " + 
							"FROM M_Cost c " + 
							"LEFT OUTER JOIN M_CostElement ce ON (c.M_CostElement_ID=ce.M_CostElement_ID) " + 
							"WHERE c.AD_Client_ID=?" +  
							" AND c.AD_Org_ID=?" +  
							" AND c.M_Product_ID=?" + 
							" AND (c.M_AttributeSetInstance_ID=0)" + 
							" AND c.M_CostType_ID=?" + 
							" AND c.C_AcctSchema_ID=?" +  
							" AND (ce.CostingMethod IS NULL) ";
					additionalCost = DB.getSQLValueBD(get_TrxName(), sql, product.getAD_Client_ID(), line.getAD_Org_ID(), line.getM_Product_ID(), as.getM_CostType_ID(), as.getC_AcctSchema_ID());
					if(additionalCost.signum() != 0)
					{
						costs = costs.add(additionalCost.multiply(qty.abs()));
					}					
				}
				else
				{
					ProductCost pc = new ProductCost (getCtx(), 
					line.getM_Product_ID(), M_AttributeSetInstance_ID, line.get_TrxName());
					pc.setQty(qty);
					costs = pc.getProductCosts(as, line.getAD_Org_ID(), costingMethod, 0,true);
					unitCost=costs.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);	
				}
			}
			if (costs == null)
			{
				return "No Costs for " + line.getProduct().getName();
			}
			
			
			// Set Total Amount and Total Quantity from Inventory
			MCostDetail.createInventory(as, line.getAD_Org_ID(), 
					line.getM_Product_ID(), M_AttributeSetInstance_ID,
					line.getM_InventoryLine_ID(), 0,	//	no cost element
					costs, qty,			
					line.getDescription(), line.get_TrxName());
			
			// To update Current Cost price at Cost details tab window at Product Cost window by Mukesh @20240726
			if(qty.signum()<0)
			{
				MCostDetail cd = MCostDetail.get (as.getCtx(), "M_InventoryLine_ID=?", 
						line.getM_InventoryLine_ID(), M_AttributeSetInstance_ID, as.getC_AcctSchema_ID(), get_TrxName());
				cd.setCurrentCostPrice(costs.abs().divide(qty.abs(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
				cd.save();
//				if((MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod) || MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod)))
//				{
//				MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
//		    	BigDecimal currentCostDT = MCostQueue.getCosts(product, M_AttributeSetInstance_ID, as, getAD_Org_ID(), ce, qty.negate(), null);
//			    cd.setCurrentCostPrice(currentCostDT.divide(qty.negate(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));			    
//				}
//				else
//				{
//					cd.setCurrentCostPrice(costs.divide(qty.negate(), as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP));
//				}
//				cd.save();
			}
			
			// this condition only Run when Current cost price will exist in Inventory line window 
			// check and Get Updated cost from Product Cost after created Cost Details
			if(checkUpdatedCost)
			{
				// cost for LIFO/FIFO only
				if(X_M_Product_Category_Acct.COSTINGMETHOD_Lifo.equals(costingMethod) || X_M_Product_Category_Acct.COSTINGMETHOD_Fifo.equals(costingMethod))
				{
					unitCost=line.getCurrentCostPrice();
				}
				else
				{
					ProductCost pc = new ProductCost (getCtx(), 
							line.getM_Product_ID(), M_AttributeSetInstance_ID, line.get_TrxName());
					pc.setQty(qty);
					BigDecimal costsForUnitCost = pc.getProductCosts(as, line.getAD_Org_ID(), costingMethod, 0,true);
					if(costsForUnitCost!=null && costsForUnitCost.signum()>0)	
						unitCost=costsForUnitCost.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
					else
						unitCost=line.getCurrentCostPrice();
				}
			}
				
			if(unitCost.signum()!=0) // update TrxCost @Mukesh 20240716
			{
				// @ M_InventoryLine
				int retVal=DB.executeUpdate("UPDATE M_InventoryLine SET TrxCost="+unitCost.abs()+" WHERE M_InventoryLine_ID="+line.getM_InventoryLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_InventoryLine = " +retVal);

				// @ M_Transaction
//				retVal=DB.executeUpdate("UPDATE M_Transaction SET TrxCost="+unitCost.abs()+" WHERE M_InventoryLine_ID="+line.getM_InventoryLine_ID(), get_TrxName() );
//				log.fine("Trx Cost updated at M_Transaction = " +retVal);
			}
			// code update by anshul @17022024

			if(line.getCurrentCostPrice()!=null && line.getCurrentCostPrice().signum()>0 && !X_M_Product_Category_Acct.COSTINGMETHOD_StandardCosting.equals(costingMethod))
			{
				int retValue=DB.executeUpdate("UPDATE M_Transaction SET TrxCost="+trxcost.abs()+" WHERE M_InventoryLine_ID="+line.getM_InventoryLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_Transaction = " +retValue);
			}
			else {
				int retValue=DB.executeUpdate("UPDATE M_Transaction SET TrxCost="+unitCost.abs()+" WHERE M_InventoryLine_ID="+line.getM_InventoryLine_ID(), get_TrxName() );
				log.fine("Trx Cost updated at M_Transaction = " +retValue);
			}			
		}
		
		return "";
	}

	/**
	 * 	Document Status is Complete or Closed
	 *	@return true if CO, CL or RE
	 */
	public boolean isComplete()
	{
		String ds = getDocStatus();
		return DOCSTATUS_Completed.equals(ds) 
			|| DOCSTATUS_Closed.equals(ds)
			|| DOCSTATUS_Reversed.equals(ds);
	}	//	isComplete
	
	public BigDecimal getCost(MInventoryLine line, MAcctSchema as)
	{
		MInventoryLineMA[] m_linesma = MInventoryLineMA.get(getCtx(), line.getM_InventoryLine_ID(), get_TrxName());
		BigDecimal cost = Env.ZERO;
		BigDecimal qty = Env.ZERO;

		for(int ma = 0; ma < m_linesma.length; ma++)
		{
			MInventoryLineMA lineMA = m_linesma[ma];
			cost = cost.add(lineMA.getTrxCost().multiply(lineMA.getMovementQty()));
			qty=qty.add(lineMA.getMovementQty());
		}
		cost=cost.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
		return cost;
	}	//	getCost
}	//	MInventory
