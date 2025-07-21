@Echo	Cyprus Database Import

@Echo	Importing Cyprus DB from %CYPRUS_HOME%\data\ExpDat.dmp (%CYPRUS_DB_NAME%)

@if (%CYPRUS_HOME%) == () goto environment
@if (%CYPRUS_DB_NAME%) == () goto environment
@if (%CYPRUS_DB_SERVER%) == () goto environment
@if (%CYPRUS_DB_PORT%) == () goto environment
@Rem Must have parameters systemAccount CyprusID CyprusPwd CyprusPwd
@if (%1) == () goto usage
@if (%2) == () goto usage
@if (%3) == () goto usage
@if (%4) == () goto usage

@set PGPASSWORD=%4
@echo -------------------------------------
@echo Re-Create user and database
@echo -------------------------------------
@dropdb -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -U postgres %CYPRUS_DB_NAME%
@dropuser -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -U postgres %2
@set CYPRUS_CREATE_ROLE_SQL=CREATE ROLE %2 SUPERUSER LOGIN PASSWORD '%3'
@psql -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -U postgres -c "%CYPRUS_CREATE_ROLE_SQL%"
@set CYPRUS_CREATE_ROLE_SQL=

@set PGPASSWORD=%3
@createdb -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -E UNICODE -O %2 -U %2 %CYPRUS_DB_NAME% 

@echo -------------------------------------
@echo Import Cyprus_pg.dmp
@echo -------------------------------------
@psql -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -d %CYPRUS_DB_NAME% -U %2 -c "drop schema sqlj cascade"
@set CYPRUS_ALTER_ROLE_SQL="ALTER ROLE %2 SET search_path TO cyprus, pg_catalog"
@psql -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -d %CYPRUS_DB_NAME% -U %2 -c "%CYPRUS_ALTER_ROLE_SQL%"
@psql -h %CYPRUS_DB_SERVER% -p %CYPRUS_DB_PORT% -d %CYPRUS_DB_NAME% -U %2 -f %CYPRUS_HOME%/data/ExpDat.dmp
@set CYPRUS_ALTER_ROLE_SQL=

@set PGPASSWORD=
@goto end

:environment
@Echo Please make sure that the environment variables are set correctly:
@Echo		CYPRUS_HOME	e.g. D:\CYPRUS2
@Echo		CYPRUS_DB_NAME 	e.g. cyprus or xe
@Echo		CYPRUS_DB_SERVER 	e.g. dbserver.cyprus.org
@Echo		CYPRUS_DB_PORT 	e.g. 5432 or 1521

:usage
@echo Usage:		%0 <systemAccount> <CyprusID> <CyprusPwd> <postgresPwd>
@echo Example:	%0 postgres Cyprus Cyprus postgrespwd

:end
