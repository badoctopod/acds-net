REM Initial variables
set APP_HOME=C:\Programm_Vist\acds\acds-net
set SERVICE_NAME=acds-net

REM Start service
sc start %SERVICE_NAME%

REM Waiting for 10 seconds...
timeout 10 > NUL

REM Quering service state
sc queryex %SERVICE_NAME%

pause
