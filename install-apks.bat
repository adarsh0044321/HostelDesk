@echo off
setlocal
set "ADB_EXE=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not exist "%ADB_EXE%" set "ADB_EXE=adb"

echo ============================================================
echo Installing HostelDesk Apps to connected Android device...
echo ============================================================

where "%ADB_EXE%" >nul 2>nul
if errorlevel 1 (
    echo [ERROR] ADB was not found on your system.
    echo Please install Android platform-tools or ensure adb is on your PATH.
    pause
    exit /b 1
)

if not exist "apks" (
    echo [INFO] Local 'apks' directory not found.
    echo Download pre-built APKs directly from GitHub Releases:
    echo https://github.com/adarsh0044321/HostelDesk/releases
    pause
    exit /b 0
)

for %%f in (apks\*student*.apk) do (
    echo Installing Student App: %%f...
    "%ADB_EXE%" install -r "%%f"
)

for %%f in (apks\*admin*.apk) do (
    echo Installing Admin App: %%f...
    "%ADB_EXE%" install -r "%%f"
)

echo.
echo ============================================================
echo Done! App installation complete.
echo ============================================================
pause
