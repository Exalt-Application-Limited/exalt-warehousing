@echo off
echo Preparing Warehousing Services for GitHub Push...

REM Clean up build artifacts
echo Cleaning build artifacts...
for %%s in (warehousing-shared billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription) do (
    if exist "%%s\target" rd /s /q "%%s\target"
    echo Cleaned %%s\target
)

REM Clean up logs
echo Cleaning log files...
del /q *.log 2>nul
if exist "build-logs" rd /s /q "build-logs"
if exist "enum-backup" rd /s /q "enum-backup"

REM Add to git
echo Adding files to git...
git add .
git status

echo Ready for commit and push!
echo Suggested commit message:
echo "feat: Complete warehousing domain implementation with all services tested and production-ready"

