package org.cyprusbrs.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.cyprus.exceptions.DBException;
import org.cyprusbrs.framework.MCostDetail;
import org.cyprusbrs.framework.MCostElement;
import org.cyprusbrs.framework.MCostQueue;
import org.cyprusbrs.framework.MDocType;
import org.cyprusbrs.framework.MInOutLine;
import org.cyprusbrs.framework.MInOutLineMA;
import org.cyprusbrs.framework.MInventoryLine;
import org.cyprusbrs.framework.MInvoiceLine;
import org.cyprusbrs.framework.MLocator;
import org.cyprusbrs.framework.MMovementLine;
import org.cyprusbrs.framework.MProduct;
import org.cyprusbrs.framework.MProductCategoryAcct;
import org.cyprusbrs.framework.MProductionLine;
import org.cyprusbrs.framework.MTransaction;
import org.cyprusbrs.model.MAcctSchema;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Env;
import org.cyprusbrs.util.Trx;
import org.eevolution.model.MPPCostCollectorLine;

/**
 * Process for recalculating and updating product costs for transactions within a specific period.
 * This process handles various document types including shipments, receipts, movements, productions,
 * invoices, and cost collectors. It performs the following main operations:
 * 1. Deletes existing cost detail entries
 * 2. Unposts related documents and deletes accounting facts
 * 3. Restores costs from backup tables
 * 4. Updates transaction costs based on current costing methods
 */
public class ReCostEvaluation extends SvrProcess {
    
    // Process parameters
    private int p_AD_Client_ID = 0;
    private int p_AD_Org_ID = 0;
    private int p_C_AcctSchema_ID = 0;
    private int p_C_Period_ID = 0;
    private int p_M_Product_ID = 0;
    private Timestamp p_DateFrom = null;
    private Timestamp p_DateTo = null;
    private int p_BatchSize = 100; // Number of transactions to process before committing
    private int p_TimeoutMinutes = 9; // Maximum processing time in minutes
    
    // Processing statistics
    private int totalTransactions = 0;
    private int processedTransactions = 0;
    private int productsRecosted = 0;
    private int documentsUnposted = 0;
    private int factAcctDeleted = 0;
    private int costRecordsRestored = 0;
    
    /**
     * Prepare process parameters
     */
    @Override
    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();
        for (ProcessInfoParameter param : para) {
            String name = param.getParameterName();
            
            switch (name) {
                case "AD_Client_ID":
                    p_AD_Client_ID = param.getParameterAsInt();
                    break;
                case "AD_Org_ID":
                    p_AD_Org_ID = param.getParameterAsInt();
                    break;
                case "C_AcctSchema_ID":
                    p_C_AcctSchema_ID = param.getParameterAsInt();
                    break;
                case "C_Period_ID":
                    p_C_Period_ID = param.getParameterAsInt();
                    break;
                case "M_Product_ID":
                    p_M_Product_ID = param.getParameterAsInt();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
            }
        }
    }

    /**
     * Main process execution method
     * @return Completion message with processing statistics
     * @throws Exception if any error occurs during processing
     */
    @Override
    protected String doIt() throws Exception {
        long startTime = System.currentTimeMillis();
        final long timeoutMillis = p_TimeoutMinutes * 60 * 1000L;
        Trx trx = null;
        
        try {
            trx = Trx.get(get_TrxName(), true);
            log.info("Starting cost evaluation...");
            
            // Set date range based on period
            setPeriodDates();
            
            Set<Integer> documentIds = new HashSet<>();
            
            // Get all transactions in the specified period
            List<MTransaction> transactions = getTransactionsInPeriod();
            log.info("Found " + transactions.size() + " transactions to process");
            
            // Delete existing cost detail entries
            deleteCostDetailEntries(transactions);
            
            // Unpost documents and delete accounting facts
            unpostDocumentsAndDeleteFactAcct(transactions, documentIds);
            log.info("Unposted documents and deleted accounting facts");
             
            // Restore costs from backup tables
            restoreCostFromBackup();
                     
            // Restore cost queue from backup
            restoreCostQueueFromBackup();
            
            // Update costs for all transactions
            updateDocumentsCost(transactions, startTime, timeoutMillis);
            
            if (trx != null) {
                trx.commit();
            }
            return buildCompletionMessage();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Cost evaluation failed", e);
            if (trx != null) {
                try {
                    trx.rollback();
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Could not rollback transaction", ex);
                }
            }
            throw e;
        } finally {
            if (trx != null) {
                trx.close();
            }
        }
    }
    
    /**
     * Updates document costs in batches with timeout handling
     * @param transactions List of transactions to process
     * @param startTime Process start time
     * @param timeoutMillis Maximum processing time in milliseconds
     */
    private void updateDocumentsCost(List<MTransaction> transactions, long startTime, long timeoutMillis) {
        Trx trx = Trx.get(get_TrxName(), true);
        int batchCounter = 0;
        
        for (MTransaction transaction : transactions) {
            // Check for timeout
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw new RuntimeException("Timeout after " + p_TimeoutMinutes + 
                       " minutes. Processed " + processedTransactions + "/" + 
                       totalTransactions + " transactions.");
            }
            
            // Skip if specific product filter is set and doesn't match
            if (p_M_Product_ID > 0 && transaction.getM_Product_ID() != p_M_Product_ID) {
                continue;
            }
            
            try {
                // Update cost for this transaction
                updateDocumentCost(transaction, trx);
                processedTransactions++;
                batchCounter++;
                
                // Commit batch if batch size reached
                if (batchCounter >= p_BatchSize) {
                    if (!trx.commit()) {
                        throw new RuntimeException("Failed to commit transaction batch");
                    }
                    trx.start();
                    batchCounter = 0;
                    log.info("Processed " + processedTransactions + "/" + totalTransactions + " transactions");
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error updating cost for transaction ID=" + transaction.getM_Transaction_ID(), e);
                throw new RuntimeException("Error updating cost for transaction: " + e.getMessage(), e);
            }
        }
        
        // Commit final batch
        if (!trx.commit()) {
            throw new RuntimeException("Failed to commit final transaction batch");
        }
    }
    
    /**
     * Sets period dates based on C_Period_ID parameter
     * @throws SQLException if database error occurs
     */
    private void setPeriodDates() throws SQLException {
        String periodSql = "SELECT StartDate, EndDate FROM C_Period WHERE C_Period_ID=?";
        PreparedStatement periodStmt = null;
        ResultSet periodRs = null;
        
        try {
            periodStmt = DB.prepareStatement(periodSql, get_TrxName());
            periodStmt.setInt(1, p_C_Period_ID);
            periodRs = periodStmt.executeQuery();
            
            if (periodRs.next()) {
                p_DateFrom = periodRs.getTimestamp("StartDate");
                p_DateTo = periodRs.getTimestamp("EndDate");
            }
        } finally {
            DB.close(periodRs, periodStmt);
        }
    }
    
    /**
     * Builds completion message with processing statistics
     * @return Formatted completion message
     */
    private String buildCompletionMessage() {
        StringBuilder msg = new StringBuilder("Cost evaluation completed. ");
        msg.append("Transactions: ").append(totalTransactions).append(", ");
        msg.append("Processed: ").append(processedTransactions).append(", ");
        msg.append("Products recosted: ").append(productsRecosted).append(", ");
        msg.append("Documents unposted: ").append(documentsUnposted).append(", ");
        msg.append("Fact records deleted: ").append(factAcctDeleted).append(", ");
        msg.append("Cost records restored: ").append(costRecordsRestored);
        
        return msg.toString();
    }
    
    /**
     * Deletes cost detail entries for the specified transactions
     * @param transactions List of transactions to process
     */
    private void deleteCostDetailEntries(List<MTransaction> transactions) {
        try {
            Trx trx = Trx.get(get_TrxName(), true);
            MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
            
            for (MTransaction transaction : transactions) {
                if (p_M_Product_ID > 0 && transaction.getM_Product_ID() != p_M_Product_ID) {
                    continue;
                }
                
                for (MAcctSchema as : acctschemas) {
                    if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID) {
                        continue;
                    }
                    
                    String whereClause = getWhereClauseForTransaction(transaction);
                    int recordId = getRecordIdForTransaction(transaction);
                    
                    if (whereClause != null && recordId > 0) {
                        deleteCostDetailForTransaction(as, transaction, whereClause, recordId, trx);
                    }
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Cannot delete cost details", e);
            throw new RuntimeException("Error in deleting M_CostDetail: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets the where clause for a transaction based on its type
     * @param transaction The transaction to process
     * @return Where clause string or null if transaction type not supported
     */
    private String getWhereClauseForTransaction(MTransaction transaction) {
        if (transaction.getM_InOutLine_ID() > 0) {
            return "M_InOutLine_ID=?";
        } else if (transaction.getM_InventoryLine_ID() > 0) {
            return "M_InventoryLine_ID=?";
        } else if (transaction.getM_MovementLine_ID() > 0) {
            return "M_MovementLine_ID=?";
        } else if (transaction.getM_ProductionLine_ID() > 0) {
            return "M_ProductionLine_ID=?";
        } else if (transaction.get_ValueAsInt("C_InvoiceLine_ID") > 0) {
            return "C_InvoiceLine_ID=?";
        } else if (transaction.getPP_Cost_Collector_ID() > 0) {
            return "PP_Cost_Collector_ID=?";
        }
        return null;
    }
    
    /**
     * Gets the record ID for a transaction based on its type
     * @param transaction The transaction to process
     * @return Record ID or 0 if transaction type not supported
     */
    private int getRecordIdForTransaction(MTransaction transaction) {
        if (transaction.getM_InOutLine_ID() > 0) {
            return transaction.getM_InOutLine_ID();
        } else if (transaction.getM_InventoryLine_ID() > 0) {
            return transaction.getM_InventoryLine_ID();
        } else if (transaction.getM_MovementLine_ID() > 0) {
            return transaction.getM_MovementLine_ID();
        } else if (transaction.getM_ProductionLine_ID() > 0) {
            return transaction.getM_ProductionLine_ID();
        } else if (transaction.get_ValueAsInt("C_InvoiceLine_ID") > 0) {
            return transaction.get_ValueAsInt("C_InvoiceLine_ID");
        } else if (transaction.getPP_Cost_Collector_ID() > 0) {
            return transaction.getPP_Cost_Collector_ID();
        }
        return 0;
    }
    
    /**
     * Deletes cost detail entries for a specific transaction
     * @param as Accounting schema
     * @param transaction The transaction to process
     * @param whereClause Where clause for the delete operation
     * @param recordId Record ID to delete
     * @param trx Transaction object
     */
    private void deleteCostDetailForTransaction(MAcctSchema as, MTransaction transaction, 
            String whereClause, int recordId, Trx trx) {
        String sql = "DELETE FROM M_CostDetail WHERE " + whereClause + 
                     " AND C_AcctSchema_ID=? AND M_AttributeSetInstance_ID=?";
        
        int deleted = DB.executeUpdateEx(sql, 
            new Object[]{recordId, as.getC_AcctSchema_ID(), transaction.getM_AttributeSetInstance_ID()}, 
            trx.getTrxName());
        
        if (deleted > 0) {
            log.fine("Deleted " + deleted + " cost details for transaction ID=" + 
                     transaction.getM_Transaction_ID() + 
                     ", ASI ID=" + transaction.getM_AttributeSetInstance_ID() +
                     ", AcctSchema ID=" + as.getC_AcctSchema_ID());
        }
    }

    /**
     * Unposts documents and deletes accounting facts for the specified transactions
     * @param transactions List of transactions to process
     * @param documentIds Set to collect document IDs that were unposted
     */
    private void unpostDocumentsAndDeleteFactAcct(List<MTransaction> transactions, Set<Integer> documentIds) {
        try {
            Trx trx = Trx.get(get_TrxName(), true);
            
            collectDocumentIds(transactions, documentIds, trx);
            
            for (Integer docId : documentIds) {
                DocumentInfo docInfo = getDocumentInfo(docId, trx);
                
                if (docInfo != null) {
                    unpostDocument(docInfo, docId, trx);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in unposting documents", e);
            throw new RuntimeException("Error in unposting documents: " + e.getMessage(), e);
        }
    }
    
    /**
     * Collects document IDs from transactions
     * @param transactions List of transactions to process
     * @param documentIds Set to store collected document IDs
     * @param trx Transaction object
     */
    private void collectDocumentIds(List<MTransaction> transactions, Set<Integer> documentIds, Trx trx) {
        for (MTransaction transaction : transactions) {
            if (p_M_Product_ID > 0 && transaction.getM_Product_ID() != p_M_Product_ID) {
                continue;
            }
            
            int docId = getDocumentIdForTransaction(transaction, trx);
            if (docId > 0) {
                documentIds.add(docId);
            }
        }
    }
    
    /**
     * Gets the document ID for a transaction based on its type
     * @param transaction The transaction to process
     * @param trx Transaction object
     * @return Document ID or 0 if transaction type not supported
     */
    private int getDocumentIdForTransaction(MTransaction transaction, Trx trx) {
        if (transaction.getM_InOutLine_ID() > 0) {
            return DB.getSQLValueEx(trx.getTrxName(), 
                "SELECT M_InOut_ID FROM M_InOutLine WHERE M_InOutLine_ID=?", 
                transaction.getM_InOutLine_ID());
        } else if (transaction.getM_InventoryLine_ID() > 0) {
            return DB.getSQLValueEx(trx.getTrxName(), 
                "SELECT M_Inventory_ID FROM M_InventoryLine WHERE M_InventoryLine_ID=?", 
                transaction.getM_InventoryLine_ID());
        } else if (transaction.getM_MovementLine_ID() > 0) {
            return DB.getSQLValueEx(trx.getTrxName(), 
                "SELECT M_Movement_ID FROM M_MovementLine WHERE M_MovementLine_ID=?", 
                transaction.getM_MovementLine_ID());
        } else if (transaction.getM_ProductionLine_ID() > 0) {
            return DB.getSQLValueEx(trx.getTrxName(), 
                "SELECT M_Production_ID FROM M_ProductionLine WHERE M_ProductionLine_ID=?", 
                transaction.getM_ProductionLine_ID());
        } else if (transaction.get_ValueAsInt("C_InvoiceLine_ID") > 0) {
            return DB.getSQLValueEx(trx.getTrxName(), 
                "SELECT C_Invoice_ID FROM C_InvoiceLine WHERE C_InvoiceLine_ID=?", 
                transaction.get_ValueAsInt("C_InvoiceLine_ID"));
        } else if (transaction.getPP_Cost_Collector_ID() > 0) {
            return DB.getSQLValueEx(trx.getTrxName(), 
                "SELECT PP_Cost_Collector_ID FROM PP_Cost_Collector WHERE PP_Cost_Collector_ID=?", 
                transaction.getPP_Cost_Collector_ID());
        }
        return 0;
    }
    
    /**
     * Gets document information (table name and ID column) for a document ID
     * @param docId Document ID to look up
     * @param trx Transaction object
     * @return DocumentInfo object or null if document type not recognized
     */
    private DocumentInfo getDocumentInfo(int docId, Trx trx) {
        if (DB.getSQLValueEx(trx.getTrxName(), "SELECT 1 FROM M_InOut WHERE M_InOut_ID=?", docId) == 1) {
            return new DocumentInfo("M_InOut", "M_InOut_ID");
        } else if (DB.getSQLValueEx(trx.getTrxName(), "SELECT 1 FROM M_Inventory WHERE M_Inventory_ID=?", docId) == 1) {
            return new DocumentInfo("M_Inventory", "M_Inventory_ID");
        } else if (DB.getSQLValueEx(trx.getTrxName(), "SELECT 1 FROM M_Movement WHERE M_Movement_ID=?", docId) == 1) {
            return new DocumentInfo("M_Movement", "M_Movement_ID");
        } else if (DB.getSQLValueEx(trx.getTrxName(), "SELECT 1 FROM M_Production WHERE M_Production_ID=?", docId) == 1) {
            return new DocumentInfo("M_Production", "M_Production_ID");
        } else if (DB.getSQLValueEx(trx.getTrxName(), "SELECT 1 FROM C_Invoice WHERE C_Invoice_ID=?", docId) == 1) {
            return new DocumentInfo("C_Invoice", "C_Invoice_ID");
        } else if (DB.getSQLValueEx(trx.getTrxName(), "SELECT 1 FROM PP_Cost_Collector WHERE PP_Cost_Collector_ID=?", docId) == 1) {
            return new DocumentInfo("PP_Cost_Collector", "PP_Cost_Collector_ID");
        }
        return null;
    }

    /**
     * Unposts a document and deletes its accounting facts
     * @param docInfo Document information
     * @param docId Document ID to unpost
     * @param trx Transaction object
     */
    private void unpostDocument(DocumentInfo docInfo, int docId, Trx trx) {
        String updateSql = "UPDATE " + docInfo.tableName + " SET Posted='N', Processed='N' WHERE " + docInfo.idColumn + "=?";
        int updated = DB.executeUpdateEx(updateSql, new Object[]{docId}, trx.getTrxName());
        
        if (updated > 0) {
            documentsUnposted++;
            log.fine("Unposted document ID=" + docId + " (" + docInfo.tableName + ")");
            
            String deleteSql = "DELETE FROM Fact_Acct WHERE AD_Table_ID=? AND Record_ID=?";
            int deleted = DB.executeUpdateEx(deleteSql, 
                new Object[]{getTableID(docInfo.tableName), docId}, 
                trx.getTrxName());
            
            factAcctDeleted += deleted;
            log.fine("Deleted " + deleted + " Fact_Acct records for document ID=" + docId);
        }
    }
    
    /**
     * Helper class to store document information
     */
    private static class DocumentInfo {
        String tableName;
        String idColumn;
        
        DocumentInfo(String tableName, String idColumn) {
            this.tableName = tableName;
            this.idColumn = idColumn;
        }
    }
    
    /**
     * Gets the table ID for a table name
     * @param tableName Table name to look up
     * @return Table ID or 0 if not found
     */
    private int getTableID(String tableName) {
        return DB.getSQLValueEx(get_TrxName(), 
            "SELECT AD_Table_ID FROM AD_Table WHERE TableName=?", tableName);
    }
    
    /**
     * Gets all transactions within the specified period
     * @return List of transactions in the period
     */
    private List<MTransaction> getTransactionsInPeriod() {
        List<MTransaction> transactions = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT /*+ INDEX(t M_Transaction_PeriodProduct) */ t.* FROM M_Transaction t ")
            .append("WHERE t.AD_Client_ID=? ")
            .append("AND t.AD_Org_ID=? ")
            .append("AND t.MovementDate BETWEEN ? AND ? ")
            .append("AND EXISTS (SELECT 1 FROM M_Product p WHERE p.M_Product_ID=t.M_Product_ID ")
            .append("AND p.IsStocked='Y' AND p.ProductType='I') ");
        
        if (p_M_Product_ID > 0) {
            sql.append("AND t.M_Product_ID=? ");
        }
        
        sql.append("ORDER BY t.MovementDate, t.M_Transaction_ID, t.M_AttributeSetInstance_ID");

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, p_AD_Client_ID);
            pstmt.setInt(paramIndex++, p_AD_Org_ID);
            pstmt.setTimestamp(paramIndex++, p_DateFrom);
            pstmt.setTimestamp(paramIndex++, p_DateTo);
            
            if (p_M_Product_ID > 0) {
                pstmt.setInt(paramIndex++, p_M_Product_ID);
            }
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MTransaction trx = new MTransaction(getCtx(), rs, get_TrxName());
                transactions.add(trx);
            }
        } catch (SQLException e) {
            throw new DBException(e, sql.toString());
        } finally {
            DB.close(rs, pstmt);
        }
        
        totalTransactions = transactions.size();
        return transactions;
    }

    /**
     * Updates document cost based on transaction type
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateDocumentCost(MTransaction transaction, Trx trx) {
        if (transaction.getM_InOutLine_ID() > 0) {
            updateInOutLineCost(transaction, trx);
        } else if (transaction.getM_InventoryLine_ID() > 0) {
            updateInventoryLineCost(transaction, trx);
        } else if (transaction.getM_MovementLine_ID() > 0) {
            updateMovementLineCost(transaction, trx);
        } else if (transaction.getM_ProductionLine_ID() > 0) {
            updateProductionLineCost(transaction, trx);
        } else if (transaction.get_ValueAsInt("C_InvoiceLine_ID") > 0) {
            updateInvoiceLineCost(transaction, trx);
        } else if (transaction.getPP_Cost_Collector_ID() > 0) {
            updateCostCollectorCost(transaction, trx);
        }
    }

    /**
     * Updates cost for an InOut line (shipment/receipt)
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateInOutLineCost(MTransaction transaction, Trx trx) {
        MInOutLine line = new MInOutLine(getCtx(), transaction.getM_InOutLine_ID(), trx.getTrxName());
        MProduct product = line.getProduct();
        if (product == null || !product.isStocked()) {
            return;
        }
            
        BigDecimal qty = transaction.getMovementQty();
        if (transaction.getMovementType().charAt(1) == '-') {
            qty = qty.negate();
        }
        
        int C_DocType_ID = DB.getSQLValueEx(trx.getTrxName(), 
            "SELECT C_DocType_ID FROM M_InOut WHERE M_InOut_ID=?", 
            line.getM_InOut_ID());
        MDocType dt = new MDocType(getCtx(), C_DocType_ID, trx.getTrxName());
        String docBaseType = dt.getDocBaseType();
        
        // Handle material allocations if they exist
        MInOutLineMA[] mas = MInOutLineMA.get(getCtx(), line.getM_InOutLine_ID(), trx.getTrxName());
        if (mas.length > 0) {
            for (MInOutLineMA ma : mas) {
                String error = createCostDetailForInOutLine(line, ma.getM_AttributeSetInstance_ID(), 
                    ma.getMovementQty(), dt, docBaseType, trx);
                if (error != null && error.length() > 0) {
                	log.warning(error);
                }
            }
        } else {
            String error = createCostDetailForInOutLine(line, line.getM_AttributeSetInstance_ID(), 
                qty, dt, docBaseType, trx);
            if (error != null && error.length() > 0) {
                throw new RuntimeException(error);
            }
        }
        
        // Update transaction cost if available
        if (line.getTrxCost() != null && line.getTrxCost().signum() != 0) {
            String sql = "UPDATE M_Transaction SET TrxCost=? WHERE M_Transaction_ID=?";
            DB.executeUpdateEx(sql, new Object[]{line.getTrxCost(), transaction.getM_Transaction_ID()}, trx.getTrxName());
        }
        
        productsRecosted++;
    }
    
    /**
     * Creates cost detail for an InOut line
     * @param line InOut line to process
     * @param asiId Attribute Set Instance ID
     * @param qty Quantity
     * @param dt Document type
     * @param docBaseType Document base type
     * @param trx Transaction object
     * @return Error message if any, otherwise empty string
     */
    private String createCostDetailForInOutLine(MInOutLine line, int asiId, 
            BigDecimal qty, MDocType dt, String docBaseType, Trx trx) {

        MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
        MProduct product = line.getProduct();
        BigDecimal trxCost = Env.ZERO;

        for (MAcctSchema as : acctschemas) {
            if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID)
                continue;

            String costingMethod = getCostingMethod(product, as);
            BigDecimal costs = calculateCosts(product, asiId, qty, as, costingMethod, line.get_TrxName(), p_AD_Org_ID);
            
            if (costs == null || costs.signum() == 0) {
            	log.warning("No Costs for " + product.getName());
            }

            trxCost = trxCost.add(costs); 

            // Create cost detail record
            MCostDetail.createShipment(as, line.getAD_Org_ID(),
                product.getM_Product_ID(), asiId,
                line.getM_InOutLine_ID(), 0,
                costs, qty,
                line.getDescription(), true, trx.getTrxName());

            if (shouldUpdateTrxCost(docBaseType, dt, qty, costs)) {
                updateTrxCostForInOutLine(as, line, asiId, costs, qty, trx, docBaseType);
            }
        }

        // Update line transaction cost if calculated
        if (trxCost.signum() != 0) {
            updateInOutLineTrxCost(line, trxCost, trx, docBaseType);
        }

        return "";
    }

    /**
     * Gets the costing method for a product
     * @param product Product to check
     * @param as Accounting schema
     * @return Costing method
     */
    private String getCostingMethod(MProduct product, MAcctSchema as) {
        MProductCategoryAcct prca = MProductCategoryAcct.get(getCtx(), 
            product.getM_Product_Category_ID(), as.getC_AcctSchema_ID(), get_TrxName());
        
        return prca.getCostingMethod() != null ? prca.getCostingMethod() : as.getCostingMethod();
    }
    
    /**
     * Determines if transaction cost should be updated
     * @param docBaseType Document base type
     * @param dt Document type
     * @param qty Quantity
     * @param costs Calculated costs
     * @return true if transaction cost should be updated, false otherwise
     */
    private boolean shouldUpdateTrxCost(String docBaseType, MDocType dt, BigDecimal qty, BigDecimal costs) {
        return (MDocType.DOCBASETYPE_MaterialDelivery.equals(docBaseType) && !dt.isReturnTrx() || 
                docBaseType.equalsIgnoreCase("JWI") ||
                MDocType.DOCBASETYPE_MaterialDelivery.equals(docBaseType) && !dt.isSOTrx() && dt.isReturnTrx() ||
                MDocType.DOCBASETYPE_MaterialReceipt.equals(docBaseType) && dt.isSOTrx() && dt.isReturnTrx()) &&
                costs != null && qty.signum() != 0;
    }
    
    /**
     * Updates transaction cost for an InOut line
     * @param as Accounting schema
     * @param line InOut line
     * @param M_AttributeSetInstance_ID ASI ID
     * @param costs Calculated costs
     * @param qty Quantity
     * @param trx Transaction object
     * @param docBaseType Document base type
     */
    private void updateTrxCostForInOutLine(MAcctSchema as, MInOutLine line, 
            int M_AttributeSetInstance_ID, BigDecimal costs, BigDecimal qty, 
            Trx trx, String docBaseType) {
        MCostDetail cd = MCostDetail.get(as.getCtx(), "M_InOutLine_ID=?", 
                line.getM_InOutLine_ID(), M_AttributeSetInstance_ID, 
                as.getC_AcctSchema_ID(), trx.getTrxName());
        
        BigDecimal trxCost = costs.divide(qty, as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
        if (cd != null) {
            cd.setCurrentCostPrice(trxCost.abs());
            cd.save();
        }
    }

    /**
     * Updates InOut line transaction cost
     * @param line InOut line
     * @param trxCost Transaction cost
     * @param trx Transaction object
     * @param docBaseType Document base type
     */
    private void updateInOutLineTrxCost(MInOutLine line, BigDecimal trxCost, Trx trx, String docBaseType) {
        String updateSql = "UPDATE M_InOutLine SET TrxCost=? WHERE M_InOutLine_ID=?";
        DB.executeUpdateEx(updateSql, new Object[]{trxCost.abs(), line.getM_InOutLine_ID()}, trx.getTrxName());
        
        String updateTrxSql = docBaseType.equalsIgnoreCase("JWI") ? 
            "UPDATE M_Transaction SET TrxCost=?, IsJobWorkM='Y' WHERE M_InOutLine_ID=?" :
            "UPDATE M_Transaction SET TrxCost=? WHERE M_InOutLine_ID=?";
        
        DB.executeUpdateEx(updateTrxSql, 
            new Object[]{trxCost.abs(), line.getM_InOutLine_ID()}, 
            trx.getTrxName());
    }

    /**
     * Updates cost for an inventory line
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateInventoryLineCost(MTransaction transaction, Trx trx) {
        MInventoryLine line = new MInventoryLine(getCtx(), transaction.getM_InventoryLine_ID(), trx.getTrxName());
        MProduct product = line.getProduct();
        if (product == null || !product.isStocked()) {
            return;
        }

        BigDecimal qty = transaction.getMovementQty();
        if (qty.signum() == 0) return;

        MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
        for (MAcctSchema as : acctschemas) {
            if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID) {
                continue;
            }

            String costingMethod = getCostingMethod(product, as);
            BigDecimal costs = calculateCosts(product, transaction.getM_AttributeSetInstance_ID(), qty, as, costingMethod, line.get_TrxName(), p_AD_Org_ID);

            if (costs == null) {
            	log.warning("No Costs for " + product.getName());
            }

            // Create inventory cost detail
            MCostDetail.createInventory(as, line.getAD_Org_ID(), product.getM_Product_ID(),
                    transaction.getM_AttributeSetInstance_ID(), line.getM_InventoryLine_ID(), 0,
                    costs, qty, line.getDescription(), trx.getTrxName());

            DB.executeUpdateEx("UPDATE M_Transaction SET TrxCost=? WHERE M_Transaction_ID=?",
                    new Object[]{costs, transaction.getM_Transaction_ID()}, trx.getTrxName());
        }
        productsRecosted++;
    }

    /**
     * Updates cost for a movement line
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateMovementLineCost(MTransaction transaction, Trx trx) {
        MMovementLine line = new MMovementLine(getCtx(), transaction.getM_MovementLine_ID(), trx.getTrxName());
        MProduct product = line.getProduct();
        if (product == null || !product.isStocked()) {
            return;
        }

        BigDecimal qty = transaction.getMovementQty();
        if (qty.signum() == 0) return;

        MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
        for (MAcctSchema as : acctschemas) {
            if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID) {
                continue;
            }

            String costingMethod = getCostingMethod(product, as);
            BigDecimal costs = calculateCosts(product, transaction.getM_AttributeSetInstance_ID(), qty, as, costingMethod, line.get_TrxName(), p_AD_Org_ID);

            if (costs == null) {
            	log.warning("No Costs for " + product.getName());
            }
 
        	MLocator fromLocator=MLocator.get(getCtx(), line.getM_Locator_ID());
			MLocator toLocator=MLocator.get(getCtx(), line.getM_LocatorTo_ID());
            String description = line.getDescription();
			if (description == null)
				description = "";
				
			// Create movement cost details (outbound)
			MCostDetail.createMovement(as, fromLocator.getAD_Org_ID(), 	
					line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
					line.get_ID(), 0,
					costs.negate(), qty.negate(), true,
					description + "(Out|->)", get_TrxName());
					
			// Create movement cost details (inbound)
			MCostDetail.createMovement(as, toLocator.getAD_Org_ID(),
					line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
					line.get_ID(), 0,
					costs, qty, false,
					description + "(In|<-)", get_TrxName());

            DB.executeUpdateEx("UPDATE M_Transaction SET TrxCost=? WHERE M_Transaction_ID=?",
                    new Object[]{costs, transaction.getM_Transaction_ID()}, trx.getTrxName());
        }
        productsRecosted++;
    }

    /**
     * Updates cost for a production line
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateProductionLineCost(MTransaction transaction, Trx trx) {
        MProductionLine line = new MProductionLine(getCtx(), transaction.getM_ProductionLine_ID(), trx.getTrxName());
        MProduct product = line.getProduct();
        if (product == null || !product.isStocked()) {
            return;
        }

        BigDecimal qty = transaction.getMovementQty();
        if (qty.signum() == 0) return;

        MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
        for (MAcctSchema as : acctschemas) {
            if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID) {
                continue;
            }

            String costingMethod = getCostingMethod(product, as);
            BigDecimal costs = calculateCosts(product, transaction.getM_AttributeSetInstance_ID(), qty, as, costingMethod, line.get_TrxName(), p_AD_Org_ID);

            if (costs == null) {
            	log.warning("No Costs for " + product.getName());
            }
          
            // Create production cost detail
            MCostDetail.createProduction(as, line.getAD_Org_ID(), 
    				line.getM_Product_ID(), transaction.getM_AttributeSetInstance_ID(), 
    				line.get_ID(), 0, 
    				costs, qty, 
    				line.getDescription()+"(*)", get_TrxName());

            DB.executeUpdateEx("UPDATE M_Transaction SET TrxCost=? WHERE M_Transaction_ID=?",
                    new Object[]{costs, transaction.getM_Transaction_ID()}, trx.getTrxName());
        }
        productsRecosted++;
    }

    /**
     * Updates cost for an invoice line
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateInvoiceLineCost(MTransaction transaction, Trx trx) {
        MInvoiceLine line = new MInvoiceLine(getCtx(), transaction.get_ValueAsInt("C_InvoiceLine_ID"), trx.getTrxName());
        MProduct product = line.getProduct();
        if (product == null || !product.isStocked()) {
            return;
        }

        BigDecimal qty = transaction.getMovementQty();
        if (qty.signum() == 0) return;

        MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
        for (MAcctSchema as : acctschemas) {
            if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID) {
                continue;
            }

            String costingMethod = getCostingMethod(product, as);
            BigDecimal costs = calculateCosts(product, transaction.getM_AttributeSetInstance_ID(), qty, as, costingMethod, line.get_TrxName(), p_AD_Org_ID);

            if (costs == null) {
            	log.warning("No Costs for " + product.getName());
            }

            // Create invoice cost detail
            MCostDetail.createInvoice(as, line.getAD_Org_ID(), 
            		product.getM_Product_ID(), transaction.getM_AttributeSetInstance_ID(),
					line.getC_InvoiceLine_ID(), 0,		//	No cost element
					costs, qty,	line.getDescription(), get_TrxName());

            DB.executeUpdateEx("UPDATE M_Transaction SET TrxCost=? WHERE M_Transaction_ID=?",
                    new Object[]{costs, transaction.getM_Transaction_ID()}, trx.getTrxName());
        }
        productsRecosted++;
    }

    /**
     * Updates cost for a cost collector
     * @param transaction Transaction to process
     * @param trx Transaction object
     */
    private void updateCostCollectorCost(MTransaction transaction, Trx trx) {
    	Integer line_ID = (Integer)transaction.get_Value("PP_Cost_CollectorLine_ID");
        MPPCostCollectorLine line = new MPPCostCollectorLine(getCtx(), line_ID, trx.getTrxName());
        MProduct product = (MProduct) line.getM_Product();
        if (product == null || !product.isStocked()) {
            return;
        }

        BigDecimal qty = transaction.getMovementQty();
        if (qty.signum() == 0) return;

        MAcctSchema[] acctschemas = MAcctSchema.getClientAcctSchema(getCtx(), p_AD_Client_ID);
        for (MAcctSchema as : acctschemas) {
            if (p_C_AcctSchema_ID > 0 && as.getC_AcctSchema_ID() != p_C_AcctSchema_ID) {
                continue;
            }

            String costingMethod = getCostingMethod(product, as);
            BigDecimal costs = calculateCosts(product, transaction.getM_AttributeSetInstance_ID(), qty, as, costingMethod, line.get_TrxName(), p_AD_Org_ID);

            if (costs == null) {
            	log.warning("No Costs for " + product.getName());
            }
            
            MLocator fromLocator=MLocator.get(getCtx(), line.getM_Locator_ID());
            
            // Create cost collector cost detail
            MCostDetail.createCostCollector(as, fromLocator.getAD_Org_ID(), 	
					line.getM_Product_ID(), transaction.getM_AttributeSetInstance_ID(),
					line.get_ID(), 0,
					costs.negate(), qty.negate(), true,
					line.getDescription() + "Cost Collector", get_TrxName());

            DB.executeUpdateEx("UPDATE M_Transaction SET TrxCost=? WHERE M_Transaction_ID=?",
                    new Object[]{costs, transaction.getM_Transaction_ID()}, trx.getTrxName());
        }
        productsRecosted++;
    }

    /**
     * Calculates costs for a product based on costing method
     * @param product Product to calculate costs for
     * @param asiId Attribute Set Instance ID
     * @param qty Quantity
     * @param as Accounting schema
     * @param costingMethod Costing method
     * @param trxName Transaction name
     * @param orgID Organization ID
     * @return Calculated costs
     */
    private BigDecimal calculateCosts(MProduct product, int asiId,
            BigDecimal qty, MAcctSchema as, String costingMethod, String trxName, int orgID) {
        
        BigDecimal cost = getCurrentCost(product, asiId, as, costingMethod, trxName, orgID);
        
        String costingLevel = product.getCostingLevel(as);
        
        // For FIFO/LIFO methods, try to get cost from queue if no current cost
        if ((cost == null || cost.signum() == 0) && 
        	    (MProductCategoryAcct.COSTINGMETHOD_Fifo.equals(costingMethod) ||
        	     MProductCategoryAcct.COSTINGMETHOD_Lifo.equals(costingMethod))) {
        	    
        	    if (!MAcctSchema.COSTINGLEVEL_Client.equals(costingLevel)) {
        	        MCostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
        	        cost = MCostQueue.getCosts(product, asiId, as, orgID, ce, qty.abs(), trxName);
        	    }
        	}
        
        if (cost == null || cost.signum() == 0) {
            log.warning("No cost found for product " + product.getValue() + 
                       " using " + costingMethod + " - using 0");
            return Env.ZERO;
        }
        
        // Add any additional costs
        BigDecimal additional = getAdditionalCostForProduct(product, as);
        if (additional != null && additional.signum() != 0) {
            cost = cost.add(additional.multiply(qty.abs()));
        }
        
        return cost;
    }

    /**
     * Gets additional costs for a product
     * @param product Product to check
     * @param as Accounting schema
     * @return Additional costs or zero if none found
     */
    private BigDecimal getAdditionalCostForProduct(MProduct product, MAcctSchema as) {
        String sql = "SELECT COALESCE(SUM(c.CurrentCostPrice),0) " + 
                   "FROM M_Cost c " + 
                   "LEFT OUTER JOIN M_CostElement ce ON (c.M_CostElement_ID=ce.M_CostElement_ID) " + 
                   "WHERE c.AD_Client_ID=? " +  
                   "AND c.AD_Org_ID=? " +  
                   "AND c.M_Product_ID=? " + 
                   "AND (c.M_AttributeSetInstance_ID=0) " + 
                   "AND c.M_CostType_ID=? " + 
                   "AND c.C_AcctSchema_ID=? " +  
                   "AND (ce.CostingMethod IS NULL)";
        
        return DB.getSQLValueBD(get_TrxName(), sql, 
                product.getAD_Client_ID(), product.getAD_Org_ID(), 
                product.getM_Product_ID(), as.getM_CostType_ID(), 
                as.getC_AcctSchema_ID());
    }

    /**
     * Restores costs from backup table
     */
    private void restoreCostFromBackup() {
    	String trxName = Trx.createTrxName("CostCreate");
		Trx trxCostCreate = Trx.get(trxName, true);
    	try {
            // Delete existing costs
            String deleteSql = "DELETE FROM M_Cost WHERE " + getCostWhereClause();
            int deleted = DB.executeUpdateEx(deleteSql, getCostWhereParams(), trxName);
            log.fine("Deleted " + deleted + " existing cost records before restore");

            // Restore from backup
            String insertSql = 
                "INSERT INTO M_Cost (" +
                "AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, " +
                "C_AcctSchema_ID, M_Product_ID, M_CostElement_ID, M_CostType_ID, M_AttributeSetInstance_ID, " +
                "CumulatedAmt, CumulatedQty, CurrentCostPrice, CurrentCostPriceLL, CurrentQty, " +
                "Description, FutureCostPrice, FutureCostPriceLL, Percent, IsCostFrozen) " +
                "SELECT DISTINCT ON (" +
                "    b.AD_Client_ID, b.AD_Org_ID, b.M_Product_ID, b.M_CostType_ID, " +
                "    b.C_AcctSchema_ID, b.M_CostElement_ID, b.M_AttributeSetInstance_ID" +
                ") " +
                "b.AD_Client_ID, b.AD_Org_ID, b.IsActive, b.Created, b.CreatedBy, b.Updated, b.UpdatedBy, " +
                "b.C_AcctSchema_ID, b.M_Product_ID, b.M_CostElement_ID, b.M_CostType_ID, b.M_AttributeSetInstance_ID, " +
                "b.CumulatedAmt, b.CumulatedQty, b.CurrentCostPrice, b.CurrentCostPriceLL, b.CurrentQty, " +
                "b.Description, b.FutureCostPrice, b.FutureCostPriceLL, b.Percent, b.IsCostFrozen " +
                "FROM M_Cost_Backup b " +
                "WHERE " + getBackupWhereClause() + " " +
                "ORDER BY " +
                "    b.AD_Client_ID, b.AD_Org_ID, b.M_Product_ID, b.M_CostType_ID, " +
                "    b.C_AcctSchema_ID, b.M_CostElement_ID, b.M_AttributeSetInstance_ID, " +
                "    b.brs_backupdate DESC";

            int inserted = DB.executeUpdateEx(insertSql, getBackupWhereParams(), trxName);
            
            if(trxCostCreate.commit())
            {
            	  trxCostCreate.close();
	        	  log.info("Restored " + inserted + " cost records from latest backup in period");
	              costRecordsRestored = inserted;
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error restoring costs from backup", e);
            throw new RuntimeException("Failed to restore costs from backup", e);
        }
        
        finally
        {
        	 trxCostCreate.close();
        }
    }

    /**
     * Restores cost queue from backup table
     */
    private void restoreCostQueueFromBackup() {
     	String trxName = Trx.createTrxName("CostQueueCreate");
		Trx trxCostQueueCreate = Trx.get(trxName, true);
		
    	try {
            // Clear existing cost queue
            if (p_M_Product_ID > 0) {
                DB.executeUpdateEx("DELETE FROM M_CostQueue WHERE M_Product_ID=?", 
                    new Object[]{p_M_Product_ID}, trxName);
            } else {
                DB.executeUpdateEx("TRUNCATE TABLE M_CostQueue", trxName);
            }

            // Restore from backup
            String insertSQL = buildRestoreCostQueueSql();
            List<Object> params = buildRestoreCostQueueParams();
            
            int recordsRestored = DB.executeUpdateEx(insertSQL, params.toArray(), trxName);
            
            if (recordsRestored == 0) {
                log.warning("No cost queue records found in backup - initializing empty queue");
            } 
            else 
            {
            	if(trxCostQueueCreate.commit())
    			{
            		trxCostQueueCreate.close();
            		log.info("Restored " + recordsRestored + " records to M_CostQueue from backup");
    			}               
            }
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error restoring M_CostQueue from backup", e);
            throw new RuntimeException("Error restoring M_CostQueue: " + e.getMessage(), e);
        }
    	finally
    	{
    		trxCostQueueCreate.close();
    	}
    }

    /**
     * Builds SQL for restoring cost queue from backup
     * @return SQL statement
     */
    private String buildRestoreCostQueueSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO M_CostQueue ");
        sql.append("(M_CostQueue_ID, AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, ");
        sql.append("Updated, UpdatedBy, M_Product_ID, M_AttributeSetInstance_ID, C_AcctSchema_ID, ");
        sql.append("M_CostElement_ID, CurrentQty, CurrentCostPrice, M_CostType_ID) ");
        sql.append("SELECT nextval('m_costqueue_seq'), AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, ");
        sql.append("Updated, UpdatedBy, M_Product_ID, M_AttributeSetInstance_ID, C_AcctSchema_ID, ");
        sql.append("M_CostElement_ID, CurrentQty, CurrentCostPrice, M_CostType_ID ");
        sql.append("FROM M_CostQueue_Backup WHERE AD_Client_ID=?");
        
        if (p_AD_Org_ID > 0) {
            sql.append(" AND AD_Org_ID=?");
        }
        if (p_M_Product_ID > 0) {
            sql.append(" AND M_Product_ID=?");
        }
        if (p_C_AcctSchema_ID > 0) {
            sql.append(" AND C_AcctSchema_ID=?");
        }
        if (p_DateFrom != null && p_DateTo != null) {
            sql.append(" AND brs_backupdate BETWEEN ? AND ?");
        }
        
        return sql.toString();
    }

    /**
     * Builds parameters for cost queue restore SQL
     * @return List of parameters
     */
    private List<Object> buildRestoreCostQueueParams() {
        List<Object> params = new ArrayList<>();
        params.add(p_AD_Client_ID);
        
        if (p_AD_Org_ID > 0) {
            params.add(p_AD_Org_ID);
        }
        if (p_M_Product_ID > 0) {
            params.add(p_M_Product_ID);
        }
        if (p_C_AcctSchema_ID > 0) {
            params.add(p_C_AcctSchema_ID);
        }
        if (p_DateFrom != null && p_DateTo != null) {
            params.add(p_DateFrom);
            params.add(p_DateTo);
        }
        
        return params;
    }
    
    /**
     * Gets current cost for a product
     * @param product Product to check
     * @param asiId Attribute Set Instance ID
     * @param as Accounting schema
     * @param costingMethod Costing method
     * @param trxName Transaction name
     * @param orgID Organization ID
     * @return Current cost or null if not found
     */
    private BigDecimal getCurrentCost(MProduct product, int asiId, MAcctSchema as, 
            String costingMethod, String trxName, int orgID) {
        
        String costingLevel = product.getCostingLevel(as);
        String sql = "SELECT c.CurrentCostPrice " +
                   "FROM M_Cost c " +
                   "JOIN M_CostElement ce ON (c.M_CostElement_ID=ce.M_CostElement_ID) " +
                   "WHERE c.AD_Client_ID=? " +
                   "AND c.M_Product_ID=? " +
                   "AND c.M_CostType_ID=? " +
                   "AND c.C_AcctSchema_ID=? " +
                   "AND UPPER(ce.CostingMethod)=UPPER(?) ";

        if (MAcctSchema.COSTINGLEVEL_Organization.equals(costingLevel)) {
            sql += "AND c.AD_Org_ID=? ";
        } 
        else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(costingLevel)) {
            sql += "AND (c.M_AttributeSetInstance_ID=? OR c.M_AttributeSetInstance_ID=0) " +
                   "ORDER BY c.M_AttributeSetInstance_ID DESC ";
        }
        else {
            sql += "AND c.AD_Org_ID=0 ";
        }

        List<Object> params = new ArrayList<>();
        Collections.addAll(params, 
        		p_AD_Client_ID, 
            p_M_Product_ID,
            as.getM_CostType_ID(), 
            as.getC_AcctSchema_ID(), 
            costingMethod
        );

        if (MAcctSchema.COSTINGLEVEL_Organization.equals(costingLevel)) {
            params.add(orgID);
        } 
        
        else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(costingLevel)) {
            params.add(asiId);
        }

        return DB.getSQLValueBDEx(trxName, sql, params.toArray());
    }

    /**
     * Builds where clause for cost operations
     * @return Where clause string
     */
    private String getCostWhereClause() {
        StringBuilder where = new StringBuilder("AD_Client_ID=?");
        if (p_AD_Org_ID > 0) where.append(" AND AD_Org_ID=?");
        if (p_M_Product_ID > 0) where.append(" AND M_Product_ID=?");
        if (p_C_AcctSchema_ID > 0) where.append(" AND C_AcctSchema_ID=?");
        return where.toString();
    }
    
    /**
     * Builds parameters for cost operations
     * @return Parameter array
     */
    private Object[] getCostWhereParams() {
        List<Object> params = new ArrayList<>();
        params.add(p_AD_Client_ID);
        if (p_AD_Org_ID > 0) params.add(p_AD_Org_ID);
        if (p_M_Product_ID > 0) params.add(p_M_Product_ID);
        if (p_C_AcctSchema_ID > 0) params.add(p_C_AcctSchema_ID);
        return params.toArray();
    }
    
    /**
     * Builds where clause for backup operations
     * @return Where clause string
     */
    private String getBackupWhereClause() {
        StringBuilder where = new StringBuilder("AD_Client_ID=?");
        if (p_AD_Org_ID > 0) where.append(" AND AD_Org_ID=?");
        if (p_M_Product_ID > 0) where.append(" AND M_Product_ID=?");
        if (p_C_AcctSchema_ID > 0) where.append(" AND C_AcctSchema_ID=?");
        if (p_DateFrom != null && p_DateTo != null) {
            where.append(" AND brs_backupdate BETWEEN ? AND ?");
        }
        return where.toString();
    }

    /**
     * Builds parameters for backup operations
     * @return Parameter array
     */
    private Object[] getBackupWhereParams() {
        List<Object> params = new ArrayList<>();
        params.add(p_AD_Client_ID);
        if (p_AD_Org_ID > 0) params.add(p_AD_Org_ID);
        if (p_M_Product_ID > 0) params.add(p_M_Product_ID);
        if (p_C_AcctSchema_ID > 0) params.add(p_C_AcctSchema_ID);
        if (p_DateFrom != null && p_DateTo != null) {
            params.add(p_DateFrom);
            params.add(p_DateTo);
        }
        return params.toArray();
    }
}