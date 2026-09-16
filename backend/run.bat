@echo off
setlocal
echo ============================================================
echo Starting HostelDesk Spring Boot Backend
echo ============================================================

set "MVN_CMD="
where mvn >nul 2>nul
if %errorlevel% equ 0 (
    set "MVN_CMD=mvn"
) else if defined M2_HOME (
    if exist "%M2_HOME%\bin\mvn.cmd" set "MVN_CMD=%M2_HOME%\bin\mvn.cmd"
) else if defined MAVEN_HOME (
    if exist "%MAVEN_HOME%\bin\mvn.cmd" set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
)

if "%MVN_CMD%"=="" (
    echo [ERROR] Apache Maven was not found on your system PATH.
    echo Please install Maven 3.8+ or configure M2_HOME / MAVEN_HOME.
    pause
    exit /b 1
)

call "%MVN_CMD%" spring-boot:run %*
