package org.cyprusbrs.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.cyprus.exceptions.CyprusException;
import org.cyprusbrs.util.DB;

public class ReStructureElement extends SvrProcess {

	private String p_CoAFile = null;
	
	private Integer p_C_Element_ID=0;
	private Integer p_AD_Tree_ID=0;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;			
			else if (name.equals("isAccountingFile"))
				p_CoAFile = (String) para[i].getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_C_Element_ID=getRecord_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		// Validate existence and read permissions on CoA file
		File coaFile = new File(p_CoAFile);
		if (!coaFile.exists())
			throw new CyprusException("CoaFile " + p_CoAFile + " does not exist");
		if (!coaFile.canRead())
			throw new CyprusException("Cannot read CoaFile " + p_CoAFile);
		if (!coaFile.isFile())
			throw new CyprusException("CoaFile " + p_CoAFile + " is not a file");
		if (coaFile.length() <= 0L)
			throw new CyprusException("CoaFile " + p_CoAFile + " is empty");
		
		// Get AD_Tree_ID directly from C_Element (SQL version)
	    p_AD_Tree_ID = DB.getSQLValueEx(get_TrxName(),
	        "SELECT AD_Tree_ID FROM C_Element WHERE C_Element_ID = ?", 
	        p_C_Element_ID);
	        
	    if (p_AD_Tree_ID <= 0) {
	        throw new CyprusException("No accounting tree configured for Element_ID=" + p_C_Element_ID);
	    }
		// 1. Read CSV file
        List<AccountRecord> accounts = readCSV(coaFile);
        
        // 2. Process hierarchy
        for (AccountRecord account : accounts) {
            updateTreeNode(account);
        }
        
        return "Accounting hierarchy imported successfully";				
	}

	private void updateTreeNode(AccountRecord account) {
        // Get Element_ID for current account
        int elementId = DB.getSQLValueEx(null,
            "SELECT C_ElementValue_ID FROM C_ElementValue WHERE Value = ? AND C_Element_ID="+p_C_Element_ID, 
            account.accountValue);
        System.out.println(account.accountValue);
            
        if (elementId <= 0) {
            log.warning("Account not found: " + account.accountValue);
            return;
        }
        
        // Get Parent_ID if exists
        int parentId = 0;
        if (!account.parentValue.isEmpty()) {
            parentId = DB.getSQLValueEx(null,
                "SELECT C_ElementValue_ID FROM C_ElementValue WHERE Value = ? AND C_Element_ID="+p_C_Element_ID, 
                account.parentValue);
        }
        
        // Update AD_TreeNode
        if (parentId > 0) {
        	
        	String sql="UPDATE AD_TreeNode SET Parent_ID = "+parentId+" WHERE Node_ID = "+elementId+" AND AD_Tree_ID = "+p_AD_Tree_ID;
        	int ret=DB.executeUpdateEx(sql, get_TrxName());
            if(ret>0)
            {
            	try {
                    DB.commit(true, get_TrxName());
                    }catch(Exception e) {}
            }
            	
        }
        
    }
	
	private List<AccountRecord> readCSV(File file) throws IOException {
        List<AccountRecord> records = new LinkedList<AccountRecord>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                if(values[8]!=null && values[8].trim().length()>0)
                {	
                	records.add(new AccountRecord(
                    values[0].trim(),  // Account_Value
                    values[1].trim(),  // Account_Name
                    values[8].trim(), // Parent_Value
                    values[5].trim()   // IsActive        
                ));
                }
                }catch(Exception e)
                {
                	System.out.println(values+" "+e.getMessage());
                }
            }
        }
        return records;
    }
	
	
	class AccountRecord {
        String accountValue;
        String accountName;
        String parentValue;
        String isActive;
        
        public AccountRecord(String val, String name, String parent, String active) {
            this.accountValue = val;
            this.accountName = name;
            this.parentValue = parent;
            this.isActive = active;
        }
    }
}
