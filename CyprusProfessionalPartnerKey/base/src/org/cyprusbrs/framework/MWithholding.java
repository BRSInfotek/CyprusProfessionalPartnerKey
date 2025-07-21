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

import java.sql.ResultSet;
import java.util.Properties;

import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Msg;


/**
 *	Withholding Model
 *	
 *  @author Jorg Janke
 *  @version $Id: MWithholding.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MWithholding extends X_C_Withholding
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7734620609620104180L;

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param C_Withholding_ID id
	 *	@param trxName transaction
	 */
	public MWithholding (Properties ctx, int C_Withholding_ID, String trxName)
	{
		super (ctx, C_Withholding_ID, trxName);
	}	//	MWithholding

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MWithholding (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MWithholding
	
	protected boolean beforeSave(boolean newRecord) 
	{
		String trxType = this.get_Value("TrxType") != null ? "'" + this.get_Value("TrxType") + "'" : null;
		String payCalculationOn = this.get_Value("PayCalculationOn") != null ? "'" + this.get_Value("PayCalculationOn") + "'" : null;
		String invCalculationOn = this.get_Value("InvCalculationOn") != null ? "'" + this.get_Value("InvCalculationOn") + "'" : null;
		int count = 0;
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT COUNT(*) FROM C_Withholding WHERE C_Country_ID=" + this.get_ValueAsInt("C_Country_ID"));

		if(this.get_ValueAsInt("C_Region_ID") > 0)
		{
			sql.append(" AND C_Region_ID=" + this.get_ValueAsInt("C_Region_ID"));
		}

		if(this.get_Value("TrxType") != null)
		{
			sql.append(" AND TrxType=" + trxType);
		}

		if(this.get_Value("InvCalculationOn") != null)
		{
			sql.append("AND InvCalculationOn=" + invCalculationOn);
		}

		if(this.get_Value("PayCalculationOn") != null)
		{
			sql.append(" AND PayCalculationOn=" + payCalculationOn );
		}

		if(this.get_ValueAsInt("C_WithholdingCategory_ID") > 0)
		{
			sql.append(" AND C_WithholdingCategory_ID=" + this.get_ValueAsInt("C_WithholdingCategory_ID"));
		}

		sql.append("AND AD_Org_ID =" + getAD_Org_ID() + " AND C_Withholding_ID !=" + getC_Withholding_ID());

		count = DB.getSQLValue(get_TrxName(), sql.toString());

		if(count >= 1)
		{
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@C_Withholding_Save@")); 
			return false;
		}
		return true;
	}
	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return success
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (newRecord && success)
			insert_Accounting("C_Withholding_Acct", "C_AcctSchema_Default", null);

		return success;
	}	//	afterSave

	/**
	 * 	Before Delete
	 *	@return true
	 */
	protected boolean beforeDelete ()
	{
		return delete_Accounting("C_Withholding_Acct"); 
	}	//	beforeDelete

}	//	MWithholding
