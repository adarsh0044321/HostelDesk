Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Starting HostelDesk Spring Boot Backend" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

if (-not $env:JAVA_HOME) {
    $jbrPath = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\jbr"
    if (Test-Path $jbrPath) {
        $env:JAVA_HOME = $jbrPath
        Write-Host "Auto-configured JAVA_HOME: $jbrPath" -ForegroundColor DarkGray
    }
}

$intellijMaven = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.8\plugins\maven\lib\maven3\bin\mvn.cmd"

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    & mvn spring-boot:run @args
} elseif (Test-Path $intellijMaven) {
    Write-Host "Using IntelliJ bundled Maven 3.9.5..." -ForegroundColor DarkGray
    & $intellijMaven spring-boot:run @args
} else {
    Write-Error "Maven could not be found. Please ensure IntelliJ IDEA or Apache Maven is installed."
}
