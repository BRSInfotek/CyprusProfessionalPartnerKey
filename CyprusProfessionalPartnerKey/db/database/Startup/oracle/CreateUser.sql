SET ECHO ON
/*************************************************************************
 * The contents of this file are subject to the Cyprus License.  
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details.
 *************************************************************************
 * $Id: CreateUser.sql
 ***
 * Title:	Drop User and re-create new
 * Description:	
 *	Parameter: UserID UserPwd
 *	Run as system
 ************************************************************************/

alter session set "_ORACLE_SCRIPT"=true
/
DROP USER &1 CASCADE
/
CREATE USER &1 IDENTIFIED BY &2
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE TEMP
    PROFILE DEFAULT
    ACCOUNT UNLOCK
/
GRANT CONNECT TO &1
/
GRANT DBA TO &1
/
GRANT RESOURCE TO &1
/
GRANT UNLIMITED TABLESPACE TO &1
/
ALTER USER &1 DEFAULT ROLE CONNECT, RESOURCE, DBA
/
GRANT CREATE TABLE TO &1
/
EXIT
