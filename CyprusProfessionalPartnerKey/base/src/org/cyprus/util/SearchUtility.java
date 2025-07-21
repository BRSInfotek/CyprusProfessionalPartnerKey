package org.cyprus.util;

import java.sql.Timestamp;
import java.util.Date;

import org.cyprusbrs.util.CLogger;

/**
 * Utility class for handling search functionality in ZK web UI
 */
public class SearchUtility {
    private static final CLogger log = CLogger.getCLogger(SearchUtility.class);
    
    /**
     * Builds SQL search condition for text fields
     * @param searchText The text to search for
     * @param columns Database columns to search in
     * @return SQL condition string
     */
    public static String buildTextSearchCondition(String searchText, String... columns) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return "";
        }
        
        searchText = searchText.trim().toUpperCase();
        StringBuilder condition = new StringBuilder(" AND (");
        
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                condition.append(" OR ");
            }
            condition.append("UPPER(").append(columns[i]).append(") LIKE '%")
                    .append(searchText).append("%'");
        }
        
        condition.append(")");
        return condition.toString();
    }
    
    /**
     * Builds SQL date range condition
     * @param fromDate Start date (inclusive)
     * @param toDate End date (inclusive)
     * @param dateColumn Database column containing date
     * @return SQL condition string
     */
    public static String buildDateRangeCondition(Date fromDate, Date toDate, String dateColumn) {
        StringBuilder condition = new StringBuilder();
        
        if (fromDate != null) {
            condition.append(" AND ").append(dateColumn).append(" >= '")
                   .append(new Timestamp(fromDate.getTime())).append("'");
        }
        
        if (toDate != null) {
            condition.append(" AND ").append(dateColumn).append(" <= '")
                   .append(new Timestamp(toDate.getTime())).append("'");
        }
        
        if (fromDate != null && toDate == null) {
            condition.append(" AND ").append(dateColumn).append(" <= '")
                   .append(new Timestamp(System.currentTimeMillis())).append("'");
        }
        
        return condition.toString();
    }
    
    /**
     * Builds complete search condition combining text and date filters
     * @param searchText Text to search for
     * @param fromDate Start date
     * @param toDate End date
     * @param dateColumn Database date column
     * @param textColumns Database text columns to search
     * @return Combined SQL condition
     */
    public static String buildSearchCondition(String searchText, Date fromDate, Date toDate, 
                                           String dateColumn, String... textColumns) {
        return buildTextSearchCondition(searchText, textColumns) + 
               buildDateRangeCondition(fromDate, toDate, dateColumn);
    }
}