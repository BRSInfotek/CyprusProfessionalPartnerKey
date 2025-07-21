package org.cyprus.mfg.util;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.cyprus.mfg.model.MMFGRouting;
import org.cyprus.mfg.model.MMFGRoutingOperation;
import org.cyprus.mfg.model.MMFGRoutingOperationResource;
import org.cyprus.mfg.model.MMFGWorkOrder;
import org.cyprus.mfg.model.MMFGWorkOrderOperation;
import org.cyprus.mfg.model.MMFGWorkOrderResource;
import org.cyprusbrs.framework.MAttachment;
import org.cyprusbrs.framework.MAttachmentEntry;
import org.cyprusbrs.util.CLogger;
//import org.cyprusbrs.util.Ctx;
import org.cyprusbrs.util.Msg;

public class MMFGCopyOperations {
  private int p_MFG_WorkOrder_ID = 0;
  
  static CLogger log = CLogger.getCLogger(MMFGCopyOperations.class);
  
  private final ArrayList<Integer> OpProcessed = new ArrayList<Integer>();
  
  public boolean addOperationLines(Properties ctx, String trx, int routingID, int workorderID, boolean isprocesscalled) {
    if (routingID == 0)
      return false; 
    MMFGRouting routing = new MMFGRouting(ctx, routingID, trx);
    MMFGWorkOrder workorder = new MMFGWorkOrder(ctx, workorderID, trx);
    if (workorder == null || 0 == workorder.getMFG_WorkOrder_ID())
      return false; 
    this.p_MFG_WorkOrder_ID = workorder.getMFG_WorkOrder_ID();
    MMFGWorkOrderOperation[] woos = MMFGWorkOrderOperation.getOfWorkOrder(workorder, null, "SeqNo");
    MMFGRoutingOperation[] ros = MMFGRoutingOperation.getOperationLines(routing, "", "ORDER BY SeqNo");
    log.fine(routing.toString());
    if (woos.length != 0) {
      for (MMFGRoutingOperation ro : ros) {
        int seqMatch = 0;
        for (MMFGWorkOrderOperation woo : woos) {
          if (woo.getSeqNo() == ro.getSeqNo()) {
            seqMatch++;
            if (!copyResources(ctx, trx, ro, woo))
              return false; 
            break;
          } 
        } 
        if (seqMatch == 0 && 
          !copyOperation(ctx, trx, ro, isprocesscalled))
          return false; 
      } 
    } else {
      for (MMFGRoutingOperation ro : ros) {
        if (!copyOperation(ctx, trx, ro, isprocesscalled))
          return false; 
      } 
    } 
    log.fine("#" + ros.length);
    return true;
  }
  
  public boolean copyOperation(Properties ctx, String trx, MMFGRoutingOperation ro, boolean isprocesscalled) {
    MMFGWorkOrderOperation woo = new MMFGWorkOrderOperation(ctx, trx, this.p_MFG_WorkOrder_ID, ro, isprocesscalled);
    if (!woo.save(trx)) {
      log.saveError("Error", Msg.translate(ctx, "Error while copying the Operations"));
      return false;
    }
    else
    {
    	try {
			org.cyprusbrs.util.DB.commit(true, trx);
		} catch (IllegalStateException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    if (!copyResources(ctx, trx, ro, woo))
      return false; 
    MAttachment attachment = ro.getAttachment();
    if (attachment != null) {
      MAttachmentEntry[] entries = attachment.getEntries();
      MAttachment wooAttachment = woo.createAttachment();
      for (MAttachmentEntry entry : entries)
        wooAttachment.addEntry(entry); 
      wooAttachment.save(trx);
    } 
    return true;
  }
  
  public boolean copyResources(Properties ctx, String trx, MMFGRoutingOperation ro, MMFGWorkOrderOperation workOrderOpLine) {
    MMFGRoutingOperationResource[] rors = MMFGRoutingOperationResource.getResourceLines(ro);
    MMFGWorkOrderResource[] wors = MMFGWorkOrderResource.getofWorkOrderOperation(workOrderOpLine, null, null);
    if (wors.length != 0) {
      for (MMFGRoutingOperationResource ror : rors) {
        int prodResourceMatch = 0;
        for (MMFGWorkOrderResource wor : wors) {
          if (wor.getM_Product_ID() == ror.getM_Product_ID()) {
            prodResourceMatch++;
            wor.setQtyRequired(ror.getQtyRequired().add(wor.getQtyRequired()));
            if (!wor.save(trx)) {
              log.saveError("Error", Msg.translate(ctx, "Error while updating the Resource"));
              return false;
            } 
            if (!this.OpProcessed.contains(Integer.valueOf(workOrderOpLine.getSeqNo())))
              this.OpProcessed.add(Integer.valueOf(workOrderOpLine.getSeqNo())); 
            break;
          } 
        } 
        if (prodResourceMatch == 0) {
          MMFGWorkOrderResource OpResLines = new MMFGWorkOrderResource(ctx, trx, workOrderOpLine, ror);
          if (!OpResLines.save(trx)) {
            log.saveError("Error", Msg.translate(ctx, "Error while copying the Resources"));
            return false;
          } 
          if (!this.OpProcessed.contains(Integer.valueOf(workOrderOpLine.getSeqNo())))
            this.OpProcessed.add(Integer.valueOf(workOrderOpLine.getSeqNo())); 
        } 
      } 
    } else {
      for (MMFGRoutingOperationResource ror : rors) {
        MMFGWorkOrderResource OpResLines = new MMFGWorkOrderResource(ctx, trx, workOrderOpLine, ror);
        if (!OpResLines.save(trx)) {
          log.saveError("Error", Msg.translate(ctx, "Error while copying the Resources"));
          return false;
        } 
      } 
    } 
    log.fine("#" + rors.length);
    if (!this.OpProcessed.contains(Integer.valueOf(workOrderOpLine.getSeqNo())))
      this.OpProcessed.add(Integer.valueOf(workOrderOpLine.getSeqNo())); 
    return true;
  }
  
  public ArrayList<Integer> operationsProcessed() {
    return this.OpProcessed;
  }
}

