@echo off
echo ============================================================
echo PREPARING WAREHOUSING DOMAIN FOR GIT COMMIT
echo ============================================================
echo.

echo Cleaning build artifacts...

REM Clean all target directories
for /d %%d in (*) do (
    if exist "%%d\target" (
        echo Removing %%d\target...
        rd /s /q "%%d\target"
    )
)

REM Clean root target
if exist "target" rd /s /q "target"

REM Clean temporary build directories
if exist "complete-build-logs" rd /s /q "complete-build-logs"
if exist "complete-build-artifacts" rd /s /q "complete-build-artifacts"
if exist "deployment-ready" rd /s /q "deployment-ready"
if exist "build-logs" rd /s /q "build-logs"
if exist "production-artifacts" rd /s /q "production-artifacts"
if exist "production-reports" rd /s /q "production-reports"
if exist "deployment-package" rd /s /q "deployment-package"
if exist "enum-backup" rd /s /q "enum-backup"

REM Clean log files
del /q *.log 2>nul
del /q hs_err_pid*.log 2>nul

REM Clean temporary scripts (keep the main ones)
del /q quick-fix-enums.bat 2>nul
del /q fix-all-enums.bat 2>nul
del /q pre-build-check.bat 2>nul
del /q complete-build-test.bat 2>nul
del /q production-readiness-check.bat 2>nul
del /q final-production-build.bat 2>nul
del /q final-build-with-wrapper.bat 2>nul
del /q automated-compilation-test.bat 2>nul
del /q test-compilation.bat 2>nul
del /q test-individual-services.bat 2>nul
del /q test-warehouse-operations.bat 2>nul

echo.
echo Creating .gitignore if missing...
if not exist ".gitignore" (
(
echo # Build artifacts
echo target/
echo *.class
echo *.jar
echo *.war
echo *.ear
echo.
echo # IDE files
echo .idea/
echo *.iml
echo .vscode/
echo *.swp
echo *.swo
echo.
echo # Log files
echo *.log
echo hs_err_pid*
echo.
echo # OS files
echo .DS_Store
echo Thumbs.db
echo.
echo # Build directories
echo build-logs/
echo production-artifacts/
echo deployment-ready/
echo complete-build-*/
echo.
echo # Sensitive files
echo *.env
echo application-local.yml
echo application-prod.yml
) > .gitignore
echo ✓ .gitignore created
)

echo.
echo ============================================================
echo CLEANUP COMPLETE!
echo ============================================================
echo.
echo Repository is clean and ready for Git commit.
echo.
echo Suggested commit message:
echo "feat(warehousing): Complete domain implementation with 9 microservices"
echo.
echo Details to include in commit description:
echo - Main warehousing application with comprehensive features
echo - 9 fully implemented microservices
echo - Complete service implementations for all modules
echo - Docker and Kubernetes ready configurations
echo - Database migrations for all services
echo - Shared libraries for common functionality
echo - Frontend applications for self-storage
echo - Comprehensive documentation
echo.
echo To commit:
echo   git add .
echo   git commit -m "feat(warehousing): Complete domain implementation with 9 microservices"
echo   git push origin main
echo ============================================================