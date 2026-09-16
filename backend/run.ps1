Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Starting HostelDesk Spring Boot Backend" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    & mvn spring-boot:run @args
} elseif ($env:M2_HOME -and (Test-Path "$env:M2_HOME\bin\mvn.cmd")) {
    & "$env:M2_HOME\bin\mvn.cmd" spring-boot:run @args
} elseif ($env:MAVEN_HOME -and (Test-Path "$env:MAVEN_HOME\bin\mvn.cmd")) {
    & "$env:MAVEN_HOME\bin\mvn.cmd" spring-boot:run @args
} else {
    Write-Error "Apache Maven was not found on PATH. Please ensure Apache Maven 3.8+ is installed."
}
