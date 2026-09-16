@echo off
setlocal
echo ============================================================
echo Starting HostelDesk Spring Boot Backend
echo ============================================================

rem Configure Java 17 runtime
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr\bin\java.exe" (
        set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr"
    )
)

rem Find Maven
set "MVN_CMD="
where mvn >nul 2>nul
if %errorlevel% equ 0 (
    set "MVN_CMD=mvn"
) else (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\plugins\maven\lib\maven3\bin\mvn.cmd" (
        set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\plugins\maven\lib\maven3\bin\mvn.cmd"
    )
)

if "%MVN_CMD%"=="" (
    echo [ERROR] Neither system Maven nor IntelliJ bundled Maven was found.
    pause
    exit /b 1
)

call "%MVN_CMD%" spring-boot:run %*
