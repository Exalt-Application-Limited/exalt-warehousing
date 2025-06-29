@echo off
echo ============================================================
echo Preparing Warehousing Services for GitHub Push
echo ============================================================
echo.

REM Define services
set SERVICES=warehousing-shared billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription

REM Clean build artifacts but keep source
echo Cleaning build artifacts...
for %%s in (%SERVICES%) do (
    if exist "%%s\target" (
        echo Cleaning %%s\target...
        rd /s /q "%%s\target"
    )
)

echo.
echo Cleaning root target directory...
if exist "target" rd /s /q "target"

echo.
echo Cleaning log files...
del /q *.log 2>nul
del /q hs_err_pid*.log 2>nul

echo.
echo Cleaning temporary directories...
if exist "build-logs" (
    echo Removing build-logs...
    rd /s /q "build-logs"
)
if exist "enum-backup" (
    echo Removing enum-backup...
    rd /s /q "enum-backup"
)
if exist "production-artifacts" (
    echo Removing production-artifacts...
    rd /s /q "production-artifacts"
)
if exist "production-reports" (
    echo Removing production-reports...
    rd /s /q "production-reports"
)
if exist "deployment-package" (
    echo Removing deployment-package...
    rd /s /q "deployment-package"
)

echo.
echo Cleaning temporary batch files...
del /q quick-fix-enums.bat 2>nul
del /q fix-all-enums.bat 2>nul
del /q pre-build-check.bat 2>nul
del /q complete-build-test.bat 2>nul
del /q production-readiness-check.bat 2>nul
del /q prepare-for-github.bat 2>nul
del /q final-production-build.bat 2>nul
del /q final-build-with-wrapper.bat 2>nul

echo.
echo ============================================================
echo Cleanup complete! 
echo ============================================================
echo.
echo The warehousing domain is now clean and ready for Git.
echo.
echo Remaining files:
echo - Source code (src directories)
echo - Configuration files (pom.xml, application.yml/properties)
echo - Documentation (README.md, .md files)
echo - Git files (.gitignore)
echo - Essential scripts
echo.
echo ============================================================
echo Next steps:
echo ============================================================
echo.
echo 1. Review changes:
echo    git status
echo    git diff
echo.
echo 2. Stage all changes:
echo    git add .
echo.
echo 3. Commit with message:
echo    git commit -m "feat(warehousing): Complete domain implementation with 9 production-ready services"
echo.
echo 4. Push to repository:
echo    git push origin main
echo.
echo 5. Create release tag:
echo    git tag -a v1.0.0-warehousing -m "Warehousing Domain v1.0.0 - Production Ready"
echo    git push origin v1.0.0-warehousing
echo.
echo ============================================================
echo.
pause
