@echo off
setlocal enabledelayedexpansion

echo ================================================
echo WAREHOUSING DOMAIN - COMPLETE BUILD AND TEST
echo ================================================
echo.

set BASE_DIR=C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing
set FAILED_COUNT=0
set SUCCESS_COUNT=0

:: Step 1: Build parent POM
echo Step 1: Building parent POM...
cd /d "%BASE_DIR%"
call mvn clean install -N -DskipTests >build.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    [OK] Parent POM built successfully
) else (
    echo    [FAIL] Parent POM build failed
    type build.log
    exit /b 1
)
echo.

:: Step 2: Build shared library
echo Step 2: Building warehousing-shared...
cd /d "%BASE_DIR%\warehousing-shared"
call mvn clean install -DskipTests >build.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    [OK] warehousing-shared built successfully
    set /a SUCCESS_COUNT+=1
) else (
    echo    [FAIL] warehousing-shared build failed
    echo    Error details:
    type build.log | findstr /i "error"
    set /a FAILED_COUNT+=1
)
echo.

:: Step 3: Build and test each service
echo Step 3: Building and testing all services...
echo.

set SERVICES=billing-service cross-region-logistics-service fulfillment-service inventory-service self-storage-service staff-mobile-app warehouse-analytics warehouse-management-service warehouse-onboarding warehouse-operations warehouse-subscription warehousing-production warehousing-staging

for %%s in (%SERVICES%) do (
    echo ----------------------------------------
    echo Building %%s...
    cd /d "%BASE_DIR%\%%s"
    
    :: Build
    call mvn clean compile >build.log 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo    [OK] %%s compiled successfully
        
        :: Run tests
        echo    Running tests for %%s...
        call mvn test >test.log 2>&1
        if !ERRORLEVEL! EQU 0 (
            echo    [OK] %%s tests passed
            set /a SUCCESS_COUNT+=1
        ) else (
            echo    [FAIL] %%s tests failed
            echo    Test failures:
            type test.log | findstr /i "failed error"
            set /a FAILED_COUNT+=1
        )
    ) else (
        echo    [FAIL] %%s compilation failed
        echo    Compilation errors:
        type build.log | findstr /i "error"
        set /a FAILED_COUNT+=1
    )
    echo.
)

:: Final Report
echo ================================================
echo FINAL REPORT
echo ================================================
echo.
echo Successful: %SUCCESS_COUNT%
echo Failed: %FAILED_COUNT%
echo.

if %FAILED_COUNT% EQU 0 (
    echo ALL BUILDS AND TESTS PASSED!
    exit /b 0
) else (
    echo SOME BUILDS OR TESTS FAILED!
    exit /b 1
)
