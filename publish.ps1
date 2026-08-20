# PowerShell script to push code and trigger Automatic Release
Write-Host "==================================================="
Write-Host "       Push Code to GitHub                         "
Write-Host "==================================================="
Write-Host ""
Write-Host "Committing all new changes..."
$commitMsg = Read-Host "Enter your commit message (e.g., 'feat: added new login method' or 'fix: resolved timeout issue')"

if ([string]::IsNullOrWhiteSpace($commitMsg)) {
    $commitMsg = "chore: update code"
}

git add .
git commit -m $commitMsg
Write-Host ""
Write-Host "Pushing the code to GitHub..."
git push origin main
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Git push failed. Please make sure you authenticate when prompted." -ForegroundColor Red
    Pause
    exit 1
}

Write-Host ""
Write-Host "===================================================" -ForegroundColor Green
Write-Host " SUCCESS! Code pushed to GitHub! " -ForegroundColor Green
Write-Host "===================================================" -ForegroundColor Green
Write-Host ""
Write-Host "A 'Release PR' will automatically be created on your GitHub repository by the Release Please bot."
Write-Host "To publish a new version and generate the CHANGELOG:"
Write-Host "  1. Go to your GitHub repository -> Pull Requests."
Write-Host "  2. Merge the 'Release' pull request."
Write-Host "  3. Once merged, GitHub will automatically create the release, write the CHANGELOG.md file, and deploy to Maven Central!"
Write-Host ""
Write-Host "Opening your browser to your GitHub Pull Requests page..."
Start-Process "https://github.com/growthquantix/upstox-auth-pro-java/pulls"
Pause
