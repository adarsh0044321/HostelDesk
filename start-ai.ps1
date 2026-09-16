Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Starting HostelDesk Python AI Microservice (Port 8000)" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

Set-Location -Path "$PSScriptRoot\ai-service"
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
