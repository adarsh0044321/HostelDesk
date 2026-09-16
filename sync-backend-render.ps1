# Sync backend subfolder directly to the Render backend repository
Write-Host 'Syncing backend/ to adarsh0044321/hosteldesk-backend...' -ForegroundColor Cyan
git subtree push --prefix=backend https://github.com/adarsh0044321/hosteldesk-backend.git master
Write-Host 'Sync complete! Render will automatically deploy latest commit.' -ForegroundColor Green
