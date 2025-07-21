package org.cyprusbrs.framework;

import java.util.Properties;

import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.Env;

public class CalloutAsset extends CalloutEngine {

	public String assetGroup (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		if (isCalloutActive() || value == null)
			return "";
		//	get value
		int a_asset_group_ID = ((Integer)value).intValue();
		if (a_asset_group_ID == 0)
			return "";
		MAssetGroup assetGroup=new MAssetGroup(Env.getCtx(), a_asset_group_ID, null);
		if(assetGroup!=null)
		{
			mTab.setValue("ScrapValue", assetGroup.getScrapValue());
			mTab.setValue("UseableLifeType", assetGroup.getUseableLifeType());
			mTab.setValue("UseLifeYears", assetGroup.getUseLifeYears());
			mTab.setValue("UseLifeMonths", assetGroup.getUseLifeMonths());
		}
		return "";
	}
	/**
	 * Disposal Type
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 * Callout : org.cyprusbrs.model.CalloutAsset.assetDisposalType
	 */
	public String assetDisposalType (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
	{
		if (isCalloutActive() || value == null)
			return "";
		//	get value
		int a_DisposalType_ID = ((Integer)value).intValue();
		if (a_DisposalType_ID == 0)
			return "";
		MDisposalType disposalType=new MDisposalType(Env.getCtx(), a_DisposalType_ID, null);
		if(disposalType!=null)
		{
			mTab.setValue("DisposalType", disposalType.getDisposalType());
		}
		return "";
	}
}
