@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo WAREHOUSING DOMAIN - COMPLETE BUILD AND TEST EXECUTION
echo ============================================================
echo.

set BASE_DIR=C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing
set TOTAL_SERVICES=0
set PASSED_BUILDS=0
set FAILED_BUILDS=0
set PASSED_TESTS=0
set FAILED_TESTS=0

:: Build parent POM first
echo [PARENT] Building parent POM...
cd /d "%BASE_DIR%"
call mvn clean install -N -DskipTests >parent-build.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    [OK] Parent POM built successfully
) else (
    echo    [FAIL] Parent POM build failed
    type parent-build.log
    exit /b 1
)
echo.

:: Build shared library
echo [SHARED] Building warehousing-shared...
cd /d "%BASE_DIR%\warehousing-shared"
call mvn clean install -DskipTests >build.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    [OK] warehousing-shared built successfully
) else (
    echo    [FAIL] warehousing-shared build failed
    exit /b 1
)
echo.

:: List of all services to build and test
set SERVICES=billing-service cross-region-logistics-service fulfillment-service inventory-service self-storage-service staff-mobile-app warehouse-analytics warehouse-management-service warehouse-onboarding warehouse-operations warehouse-subscription warehousing-production warehousing-staging

echo Building and testing all warehousing services...
echo ============================================================
echo.

for %%s in (%SERVICES%) do (
    set /a TOTAL_SERVICES+=1
    echo [%%s]
    cd /d "%BASE_DIR%\%%s"
    
    :: Try to build
    echo    Building...
    call mvn clean compile >build-%%s.log 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo    [BUILD: OK]
        set /a PASSED_BUILDS+=1
        
        :: If build successful, run tests
        echo    Testing...
        call mvn test >test-%%s.log 2>&1
        if !ERRORLEVEL! EQU 0 (
            echo    [TEST: PASSED]
            set /a PASSED_TESTS+=1
        ) else (
            echo    [TEST: FAILED]
            set /a FAILED_TESTS+=1
            echo    Test errors:
            type test-%%s.log | findstr /i "Tests run:"
            type test-%%s.log | findstr /i "ERROR"
        )
    ) else (
        echo    [BUILD: FAILED]
        set /a FAILED_BUILDS+=1
        set /a FAILED_TESTS+=1
        echo    Build errors:
        type build-%%s.log | findstr /i "error"
    )
    echo.
)

:: Calculate totals
set /a TOTAL_BUILDS=%PASSED_BUILDS%+%FAILED_BUILDS%
set /a TOTAL_TESTS=%PASSED_TESTS%+%FAILED_TESTS%

:: Final report
echo ============================================================
echo FINAL REPORT - WAREHOUSING DOMAIN
echo ============================================================
echo.
echo Services processed: %TOTAL_SERVICES%
echo.
echo BUILD RESULTS:
echo    Passed: %PASSED_BUILDS%/%TOTAL_BUILDS%
echo    Failed: %FAILED_BUILDS%/%TOTAL_BUILDS%
echo.
echo TEST RESULTS:
echo    Passed: %PASSED_TESTS%/%TOTAL_TESTS%
echo    Failed: %FAILED_TESTS%/%TOTAL_TESTS%
echo.

if %FAILED_BUILDS% EQU 0 (
    if %FAILED_TESTS% EQU 0 (
        echo RESULT: ALL BUILDS AND TESTS PASSED!
        echo.
        echo The warehousing domain is fully functional.
        exit /b 0
    ) else (
        echo RESULT: All builds passed but some tests failed.
        exit /b 1
    )
) else (
    echo RESULT: Some builds failed. Fix compilation errors first.
    exit /b 1
)
