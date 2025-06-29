@echo off
echo ================================================
echo TESTING FIXED WAREHOUSING SERVICES
echo ================================================
echo.

set BASE_DIR=C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing

:: Build parent and shared first
echo [1/5] Building parent POM...
cd /d "%BASE_DIR%"
call mvn clean install -N -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (echo    OK) else (echo    FAILED & goto :error)

echo [2/5] Building warehousing-shared...
cd /d "%BASE_DIR%\warehousing-shared"
call mvn clean install -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (echo    OK) else (echo    FAILED & goto :error)

:: Test our three fixed services
echo.
echo Testing the three fixed services:
echo =================================

echo [3/5] Testing fulfillment-service...
cd /d "%BASE_DIR%\fulfillment-service"
call mvn clean test >test-output.txt 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    PASSED - All tests successful
) else (
    echo    FAILED - See test-output.txt for details
    type test-output.txt | findstr /i "error failed"
)

echo.
echo [4/5] Testing warehouse-management-service...
cd /d "%BASE_DIR%\warehouse-management-service"
call mvn clean test >test-output.txt 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    PASSED - All tests successful
) else (
    echo    FAILED - See test-output.txt for details
    type test-output.txt | findstr /i "error failed"
)

echo.
echo [5/5] Testing warehouse-subscription...
cd /d "%BASE_DIR%\warehouse-subscription"
call mvn clean test >test-output.txt 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    PASSED - All tests successful
) else (
    echo    FAILED - See test-output.txt for details
    type test-output.txt | findstr /i "error failed"
)

echo.
echo ================================================
echo TEST RUN COMPLETE
echo ================================================
goto :end

:error
echo Build prerequisites failed!

:end
pause
