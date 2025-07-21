package org.cyprus.crm.process;

import java.util.logging.Logger;

import org.cyprus.crm.model.MLead;
import org.cyprusbrs.framework.MBPartner;
import org.cyprusbrs.framework.MBPartnerLocation;
import org.cyprusbrs.framework.MLocation;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.model.Query;
import org.cyprusbrs.process.SvrProcess;
import org.cyprusbrs.util.DB;

public class GenerateProspect extends SvrProcess {

    private static Logger log = Logger.getLogger(GenerateProspect.class.getName());

    /** Lead ID */
    int v_C_Lead_ID = 0;
    
    String prospectName = null;
    
    int v_C_BPartner_ID = 0;
    
    int v_C_BPartnerLocation_ID = 0;
    
    int v_Ad_User_ID = 0;
    
    @Override
    protected void prepare() {
        v_C_Lead_ID = getRecord_ID();
    }

    @Override
    protected String doIt() throws Exception {
        String returnVal = "Prospect Customer Name : ";
        log.info("p_C_Lead_ID=" + v_C_Lead_ID);
        if (v_C_Lead_ID == 0)
            throw new Exception("Lead not found - p_C_Lead_ID=" + v_C_Lead_ID);
        
        MLead lead = new MLead(getCtx(), v_C_Lead_ID, get_TrxName());
        
        if (lead.getBPType().equalsIgnoreCase(MLead.BPTYPE_New)) {    
            // First check if company already exists in Business Partner
            MBPartner existingBPartner = null;
            if (lead.getCompanyName() != null && !lead.getCompanyName().trim().isEmpty()) {
                existingBPartner = new Query(getCtx(), MBPartner.Table_Name, 
                    "Name = ? AND AD_Client_ID = ?", get_TrxName())
                    .setParameters(lead.getCompanyName(), getAD_Client_ID())
                    .first();
            }
            
            if (existingBPartner != null) {
                // Company exists, use existing BP
                v_C_BPartner_ID = existingBPartner.getC_BPartner_ID();
                
                // Get or create location
                MBPartnerLocation loc = new Query(getCtx(), MBPartnerLocation.Table_Name, 
                    "C_BPartner_ID=? AND IsActive='Y'", get_TrxName())
                    .setParameters(v_C_BPartner_ID)
                    .setOrderBy("Created DESC")
                    .first();

                MBPartnerLocation locn[] = existingBPartner.getLocations(true);
                if(locn.length > 0)
                {
                	boolean sameLocation = false;
                	   for(int i = 0; i < locn.length; i++)
                       {
	                       	MBPartnerLocation bploc = locn[i];
	                       	MLocation mloc = new MLocation(getCtx(), bploc.getC_Location_ID(), null);
	                       	
	                       	if (lead.getAddress1() != null && mloc.getAddress1() != null && mloc.getC_Country_ID() > 0 && lead.getC_Country_ID() > 0) {
	                       	    sameLocation = (mloc.getAddress1().equalsIgnoreCase(lead.getAddress1()) &&
	                       	                   mloc.getC_Country_ID() == lead.getC_Country_ID());
	                       	    
	                       	    if (sameLocation && mloc.getAddress2() != null && lead.getAddress2() != null) {
	                       	        sameLocation = sameLocation && 
	                       	                      mloc.getAddress2().equalsIgnoreCase(lead.getAddress2());
	                       	        
	                       	        if (sameLocation && mloc.getC_City_ID() > 0 && lead.getC_City_ID() > 0) {
	                       	            sameLocation = sameLocation && 
	                       	                          mloc.getC_City_ID() == lead.getC_City_ID();
	                       	            
	                       	            if (sameLocation && mloc.getCity() != null && lead.getCity() != null) {
	                       	                sameLocation = sameLocation && 
	                       	                              mloc.getCity().equalsIgnoreCase(lead.getCity());
	                       	                
	                       	                if (sameLocation && mloc.getRegionName() != null && lead.getRegionName() != null) {
	                       	                    sameLocation = sameLocation && 
	                       	                                  mloc.getRegionName().equalsIgnoreCase(lead.getRegionName());
	                       	                    
	                       	                    if (sameLocation && mloc.getC_Region_ID() > 0 && lead.getC_Region_ID() > 0) {
	                       	                        sameLocation = sameLocation && 
	                       	                                      mloc.getC_Region_ID() == lead.getC_Region_ID();
	                       	                        
	                       	                        if (sameLocation &&  mloc.getPostal() != null && lead.getPostal() != null) {
	                       	                            sameLocation = sameLocation && 
	                       	                                          mloc.getPostal().equalsIgnoreCase(lead.getPostal());
	                       	                        }
	                       	                    }
	                       	                }
	                       	            }
	                       	        }
	                       	    }
	                       	}	                       
                       }
                		if(!sameLocation)
                       	{
                       		loc = lead.createBPartnerLocation(existingBPartner);
                       	}
                }
                else
                {
                	loc = lead.createBPartnerLocation(existingBPartner);
                }
             
//                if (loc == null) {
//                    // Create new location if none exists
//                    loc = lead.createBPartnerLocation(existingBPartner);
//                }
                v_C_BPartnerLocation_ID = loc.getC_BPartner_Location_ID();
                
                // Check if user exists with same email
                MUser existingUser = null;
                if (lead.getEMail() != null && !lead.getEMail().trim().isEmpty()) {
                    existingUser = new Query(getCtx(), MUser.Table_Name, 
                        "Email = ? AND AD_Client_ID = ?", get_TrxName())
                        .setParameters(lead.getEMail(), getAD_Client_ID())
                        .first();
                }
                
                if (existingUser != null) {
                    // User exists, use it
                    v_Ad_User_ID = existingUser.getAD_User_ID();
                } else {
                    // Create new user for existing BP
                    MUser user = lead.createProspectUser(existingBPartner);
                    v_Ad_User_ID = user.getAD_User_ID();
                }
                
                // Update lead references
                lead.setRef_User_ID(v_Ad_User_ID);
                lead.setRef_BPartner_ID(v_C_BPartner_ID);
                lead.setRef_BPartner_Location_ID(v_C_BPartnerLocation_ID);
                lead.setBPType(MLead.BPTYPE_Prospect);
                lead.saveEx(get_TrxName());
                
                returnVal += existingBPartner.getName() + " (Existing Business Partner)";
            } else {
                // Company doesn't exist, create new prospect
                MBPartner bPartner = lead.createProspectBPartner();
                if (bPartner != null) {
                    prospectName = bPartner.getName();
                    log.info("Propect Customer Name is " + bPartner.getName());
                    
                    MBPartnerLocation location = lead.createBPartnerLocation(bPartner);
                    log.info("Propect Customer location " + location.getC_Location_ID());
                    
                    MUser user = lead.createProspectUser(bPartner);
                    log.info("Propect Customer User " + user.getName());
                    
                    lead.setBPType(MLead.BPTYPE_Prospect);
                    lead.setRef_BPartner_ID(bPartner.getC_BPartner_ID());
                    lead.setRef_BPartner_Location_ID(location.getC_BPartner_Location_ID());
                    lead.setRef_User_ID(user.getAD_User_ID());
                    
                    if (lead.save(get_TrxName())) {
                        DB.commit(true, get_TrxName());
                    }
                    
                    returnVal += bPartner.getName();
                }
            }
        } else {
            return "No new prospect customer to add";
        }
        return returnVal;
    }
}