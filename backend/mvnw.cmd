@echo off
setlocal
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr\bin\java.exe" (
        set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr"
    )
)

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
    echo [ERROR] Maven not found.
    exit /b 1
)

call "%MVN_CMD%" %*
