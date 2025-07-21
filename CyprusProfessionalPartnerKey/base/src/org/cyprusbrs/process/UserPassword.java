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
package org.cyprusbrs.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.cyprus.exceptions.DBException;
import org.cyprusbrs.framework.MUser;
import org.cyprusbrs.util.DB;
import org.cyprusbrs.util.Util;

/**
 *	Reset Password
 *	
 *  @author Jorg Janke
 *  @version $Id: UserPassword.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class UserPassword extends SvrProcess
{
	private int			p_AD_User_ID = -1;
	private String 		p_OldPassword = null;
	private String 		p_CurrentPassword = null; // Anshul @28072021 
	private String 		p_NewPassword = null;
	private String		p_NewEMail = null;
	private String		p_NewEMailUser = null;
	private String		p_NewEMailUserPW = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_User_ID"))
				p_AD_User_ID = para[i].getParameterAsInt();
			else if (name.equals("OldPassword"))
				p_OldPassword = (String)para[i].getParameter();
			// Anshul @28072021
			else if (name.equals("CurrentPassword"))
				p_CurrentPassword = (String)para[i].getParameter(); 
			
			else if (name.equals("NewPassword"))
				p_NewPassword = (String)para[i].getParameter();
			else if (name.equals("NewEMail"))
				p_NewEMail = (String)para[i].getParameter();
			else if (name.equals("NewEMailUser"))
				p_NewEMailUser = (String)para[i].getParameter();
			else if (name.equals("NewEMailUserPW"))
				p_NewEMailUserPW = (String)para[i].getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message 
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		log.info ("AD_User_ID=" + p_AD_User_ID + " from " + getAD_User_ID());
		
		MUser user = MUser.get(getCtx(), p_AD_User_ID);
		MUser operator = MUser.get(getCtx(), getAD_User_ID());
	
		log.fine("User=" + user + ", Operator=" + operator);
		
		//	Do we need a password ?
		if (Util.isEmpty(p_CurrentPassword))
		{
		if (Util.isEmpty(p_OldPassword))		//	Password required
		{
			if (p_AD_User_ID == 0			//	change of System
				|| p_AD_User_ID == 100		//	change of SuperUser
				|| !operator.isAdministrator())
				throw new IllegalArgumentException("@OldPasswordMandatory@");
		}
		
		else if (!p_OldPassword.equals(user.getPassword()))
		{
			throw new IllegalArgumentException("@OldPasswordNoMatch@");
		}
		}
	// Changes done by @Anshul13102021 as per instruction given by Surya
		boolean loginWithValue = false;// M_Element.get(Env.getCtx(), I_AD_User.COLUMNNAME_IsLoginUser) != null;
		String userLogin = "Name";
		if(loginWithValue) {
			userLogin = "Value";
		}
		String userPwd = null;
		String userSalt = null;
		String ssql = "SELECT AD_User_ID, Password, Salt FROM AD_User " + 
				"WHERE COALESCE(LDAPUser," + userLogin + ") = ? AND" +
				" EXISTS (SELECT 1 FROM AD_User_Roles ur" +
				"         INNER JOIN AD_Role r ON (ur.AD_Role_ID=r.AD_Role_ID)" +
				"         WHERE ur.AD_User_ID=AD_User.AD_User_ID AND ur.IsActive='Y' AND r.IsActive='Y') AND " +
				" EXISTS (SELECT 1 FROM AD_Client c" +
				"         WHERE c.AD_Client_ID=AD_User.AD_Client_ID" +
				"         AND c.IsActive='Y') " + 
				"AND AD_User.IsActive='Y' ";
		if(loginWithValue) {
			ssql += "AND IsLoginUser = 'Y'";
		}
		String app_user = operator.getName();
		PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(ssql, null);
            pstmt.setString(1, app_user);
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	userPwd = rs.getString("Password");
            	userSalt = rs.getString("Salt");
            }
        } catch (SQLException e) {
            throw new DBException(e, ssql);
        } finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }
    
	        //	Match Password
			if (userSalt != null && !MUser.authenticateHash(p_CurrentPassword, userPwd , userSalt)) 
				throw new IllegalArgumentException("@OldPasswordNoMatch@");
			
		
		

		//	is entered Password correct ?
//		else if (!p_OldPassword.equals(user.getPassword()))
//			throw new IllegalArgumentException("@OldPasswordNoMatch@");
		
		//	Change Super User
		if (p_AD_User_ID == 0)
		{
			String sql = "UPDATE AD_User SET Updated=SysDate, UpdatedBy=" + getAD_User_ID();
			if (!Util.isEmpty(p_NewPassword))
				sql += ", Password=" + DB.TO_STRING(p_NewPassword);
			if (!Util.isEmpty(p_NewEMail))
				sql += ", Email=" + DB.TO_STRING(p_NewEMail);
			if (!Util.isEmpty(p_NewEMailUser))
				sql += ", EmailUser=" + DB.TO_STRING(p_NewEMailUser);
			if (!Util.isEmpty(p_NewEMailUserPW))
				sql += ", EmailUserPW=" + DB.TO_STRING(p_NewEMailUserPW);
			sql += " WHERE AD_User_ID=0";
			if (DB.executeUpdate(sql, get_TrxName()) == 1)
				return "OK";
			else 
				return "@Error@";
		}
		else
		{
			if (!Util.isEmpty(p_NewPassword))
				user.setPassword(p_NewPassword);
			if (!Util.isEmpty(p_NewEMail))
				user.setEMail(p_NewEMail);
			if (!Util.isEmpty(p_NewEMailUser))
				user.setEMailUser(p_NewEMailUser);
			if (!Util.isEmpty(p_NewEMailUserPW))
				user.setEMailUserPW(p_NewEMailUserPW);
			//
			if (user.save())
				return "OK";
			else 
				return "@Error@";
		}
	}	//	doIt

}	//	UserPassword

