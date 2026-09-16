@echo off
setlocal
set "ADB_EXE=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not exist "%ADB_EXE%" set "ADB_EXE=adb"

echo ============================================================
echo Configuring USB Reverse Port Forwarding for HostelDesk
echo ============================================================

"%ADB_EXE%" devices
"%ADB_EXE%" reverse tcp:8080 tcp:8080
"%ADB_EXE%" reverse tcp:8000 tcp:8000

echo.
echo Active Port Forwarding:
"%ADB_EXE%" reverse --list
echo.
echo [SUCCESS] Phone can now access backend via http://127.0.0.1:8080 over USB!
pause
