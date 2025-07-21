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

package org.cyprus.model;

import java.util.Properties;

import org.cyprusbrs.framework.CalloutEngine;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MOrderLine;
import org.cyprusbrs.model.GridField;
import org.cyprusbrs.model.GridTab;
import org.cyprusbrs.util.DB;

/**
 *
 * @author  Ashley G Ramdass
 */
public class CalloutRMA extends CalloutEngine
{

    /**
    *  docType - set document properties based on document type.
    *  @param ctx
    *  @param WindowNo
    *  @param mTab
    *  @param mField
    *  @param value
    *  @return error message or ""
    */
   public String docType (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
   {
       Integer C_DocType_ID = (Integer)value;
       if (C_DocType_ID == null || C_DocType_ID.intValue() == 0)
           return "";
       
       String sql = "SELECT d.IsSoTrx "
           + "FROM C_DocType d WHERE C_DocType_ID=?";
       
       String docSOTrx = DB.getSQLValueString(null, sql, C_DocType_ID);
       
       boolean isSOTrx = "Y".equals(docSOTrx);
       
       mTab.setValue("IsSOTrx", isSOTrx);
       
       return "";
   }
   
// added by Anshul @29072024 to set product, UOM, Amount on RMA Line based on Receipt Line
   /**
    *  Product - set product, uom, amount  based on Receipt line.
    *  @param ctx
    *  @param WindowNo
    *  @param mTab
    *  @param mField
    *  @param value
    *  @return error message or ""
    */
   public String setFromReceiptLine (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
   {
       Integer M_InOutLine_ID = (Integer)value;
       if (M_InOutLine_ID == null || M_InOutLine_ID.intValue() == 0)
       {
    	   mTab.setValue("M_Product_ID", null);
    	   mTab.setValue("C_UOM_ID", null);
           return "";
       }
       MInOutLine il = new MInOutLine(ctx, M_InOutLine_ID.intValue(), null);
       MOrderLine ol = new MOrderLine(ctx, il.getC_OrderLine_ID(), null);
       
       mTab.setValue("M_Product_ID", il.getM_Product_ID());
       
       mTab.setValue("C_UOM_ID", il.getC_UOM_ID());
       
       mTab.setValue("Amt", ol.getPriceActual());
       
       return "";
   }

}
