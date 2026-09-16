@echo off
echo Installing HostelDesk Cloud-Enabled Apps to connected Android device...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" install -r apks\hosteldesk-student-debug.apk
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" install -r apks\hosteldesk-admin-debug.apk
echo.
echo ========================================================
echo Done! Both apps installed and connected to Render Cloud!
echo ========================================================
pause
