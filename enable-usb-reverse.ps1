# Enable USB Reverse Port Forwarding for Connected Android Phone
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) {
    $adbPath = "adb"
}

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Configuring USB Reverse Port Forwarding for HostelDesk" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

& $adbPath devices
& $adbPath reverse tcp:8080 tcp:8080
& $adbPath reverse tcp:8000 tcp:8000

Write-Host "`nActive Port Forwarding Rules:" -ForegroundColor Yellow
& $adbPath reverse --list
Write-Host "`n[SUCCESS] Your phone can now connect directly to http://127.0.0.1:8080 over USB!" -ForegroundColor Green
