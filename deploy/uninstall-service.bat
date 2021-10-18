REM Initial variables
set APP_HOME=C:\Programm_Vist\acds\acds-net
set SERVICE_NAME=acds-net

REM Uninstall service
%APP_HOME%\bin\acds-net.exe //DS//%SERVICE_NAME%

pause
