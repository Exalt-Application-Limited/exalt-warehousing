@echo off
echo ============================================================
echo WAREHOUSING DOMAIN - QUICK COMPILE TEST
echo ============================================================
echo.

echo This will compile all services to check for build errors.
echo.

set /a TOTAL=0
set /a SUCCESS=0
set /a FAILED=0

REM Test main app compilation
echo [1] Compiling Main Application...
if exist "pom.xml" (
    mvn compile -q
    if %ERRORLEVEL% EQU 0 (
        echo    SUCCESS
        set /a SUCCESS+=1
    ) else (
        echo    FAILED
        set /a FAILED+=1
    )
    set /a TOTAL+=1
)

REM Test each service compilation
set SERVICES=billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription warehousing-shared

for %%s in (%SERVICES%) do (
    if exist "%%s\pom.xml" (
        echo [!TOTAL!] Compiling %%s...
        cd %%s
        mvn compile -q
        if !ERRORLEVEL! EQU 0 (
            echo    SUCCESS
            set /a SUCCESS+=1
        ) else (
            echo    FAILED - Check %%s for compilation errors
            set /a FAILED+=1
        )
        cd ..
        set /a TOTAL+=1
    )
)

echo.
echo ============================================================
echo COMPILATION SUMMARY
echo ============================================================
echo Total Services: %TOTAL%
echo Compiled Successfully: %SUCCESS%
echo Failed to Compile: %FAILED%
echo.

if %FAILED% GTR 0 (
    echo WARNING: Some services failed to compile!
    echo Fix compilation errors before running tests.
    exit /b 1
) else (
    echo SUCCESS: All services compiled successfully!
    echo You can now run: test-all-services.bat
    exit /b 0
)