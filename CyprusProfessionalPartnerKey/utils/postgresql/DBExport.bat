@Echo	CYPRUS Database Export 	$Revision: 1.2 $

@Rem $Id: DBExport.bat,v 1.2 2005/01/22 21:59:15 jjanke Exp $

@Echo Saving database %1@%CYPRUS_DB_NAME% to %CYPRUS_HOME%\data\ExpDat.dmp

@if (%CYPRUS_HOME%) == () goto environment
@if (%CYPRUS_DB_NAME%) == () goto environment
@if (%CYPRUS_DB_SERVER%) == () goto environment
@if (%CYPRUS_DB_PORT%) == () goto environment
@Rem Must have parameter: userAccount
@if (%1) == () goto usage

@set PGPASSWORD=%2
pg_dump -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -U %1 %CYPRUS_DB_NAME% > %CYPRUS_HOME%\data\ExpDat.dmp
@set PGPASSWORD=

@cd %CYPRUS_HOME%\Data
@jar cvfM ExpDat.jar ExpDat.dmp

@goto end

:environment
@Echo Please make sure that the enviroment variables are set correctly:
@Echo		CYPRUS_HOME	e.g. D:\cyprus2
@Echo		CYPRUS_DB_NAME 	e.g. cyprus or xe
@Echo		CYPRUS_DB_SERVER 	e.g. dbserver.cyprus.org
@Echo		CYPRUS_DB_PORT 	e.g. 5432 or 1521

:usage
@echo Usage:		%0 <userAccount>
@echo Examples:	%0 CYPRUS/CYPRUS

:end
