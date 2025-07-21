package org.cyprusbrs.framework;

import org.cyprusbrs.model.Lookup;
import org.cyprusbrs.model.MColumn;
import org.cyprusbrs.model.PO;
import org.cyprusbrs.model.POInfo;
import org.cyprusbrs.util.DisplayType;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.NamePair;

public class ReferenceResolver {
    
    public static String resolveReference(PO po, String columnName, Object value) {
        if (po == null || columnName == null || value == null) {
            return value != null ? value.toString() : "";
        }

        try {
            // Get column index first
            int colIndex = po.get_ColumnIndex(columnName);
            if (colIndex < 0) return value.toString();
            
            // Get column metadata through PO's table
            POInfo poi = POInfo.getPOInfo(po.getCtx(), po.get_Table_ID());
            int AD_Column_ID = poi.getAD_Column_ID(columnName);
            
            // Now get the full column definition
            MColumn column = new MColumn(po.getCtx(), AD_Column_ID, po.get_TrxName());
            int displayType = column.getAD_Reference_ID();
            int AD_Reference_Value_ID = column.getAD_Reference_Value_ID();
            
            // Handle direct display values
            if (!DisplayType.isLookup(displayType)) {
                return value.toString();
            }

            // Handle lookup values
//            MLookupInfo lookupInfo = MLookupFactory.getLookupInfo(
            Lookup lookup = MLookupFactory.get(
                po.getCtx(), 
                0, // WindowNo
                AD_Column_ID,
                displayType,
                Env.getLanguage(po.getCtx()),
                columnName,
                AD_Reference_Value_ID,
                false, 
                "" 
            );
            
            if (lookup != null) {
            	NamePair np = lookup.getDirect(value.toString(), false, false);
                return np != null ? np.getName() : value.toString();
            }
        } catch (Exception e) {
            System.err.println("Error resolving reference for " + columnName + ": " + e.getMessage());
        }
        
        return value.toString();
    }
}
