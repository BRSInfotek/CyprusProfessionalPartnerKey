package org.cyprusbrs.process;

import org.cyprusbrs.framework.MAsset;
import org.cyprusbrs.framework.MAssetDisposal;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInvoice;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.util.DB;

public class GenerateInvoice extends SvrProcess {

	Integer recordId=0;
	@Override
	protected void prepare() {
		recordId=getRecord_ID();

	}

	@Override
	protected String doIt() throws Exception {
		
		
		MAssetDisposal assetDisposal=new MAssetDisposal(getCtx(), recordId, get_TrxName());
		
		MAsset asset=new MAsset(getCtx(), assetDisposal.getA_Asset_ID());
		
		log.info("Product Id "+asset.getM_Product_ID());
		log.info("Qty "+assetDisposal.getQty());
		log.info("Customer "+assetDisposal.getC_BPartner_ID());
		log.info("Location "+assetDisposal.getC_BPartner_Location_ID());
		log.info("User  "+assetDisposal.getAD_User_ID());

		/// Document Type
		Integer docType=MDocType.getDocType(MDocType.DOCBASETYPE_ARInvoice);
		
		
		MInvoice invoice=new MInvoice(getCtx(), 0, get_TrxName());
		invoice.setC_DocType_ID(docType);
		invoice.setC_BPartner_ID(assetDisposal.getC_BPartner_ID());
		invoice.setC_BPartner_Location_ID(assetDisposal.getC_BPartner_Location_ID());
		invoice.setAD_User_ID(assetDisposal.getAD_User_ID());
		
		if(invoice.save(get_TrxName()))
		{
			DB.commit(true, get_TrxName());
		}
		
		if(invoice!=null)
		{
			MInvoiceLine invoiceLine=new MInvoiceLine(invoice);
			invoiceLine.setM_Product_ID(asset.getM_Product_ID());
			invoiceLine.setQtyEntered(asset.getQty());
			
		}
		
		// TODO Auto-generated method stub
		return "Invoice Created";
	}

}
