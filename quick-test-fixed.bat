@echo off
echo ============================================
echo TESTING FIXED WAREHOUSING SERVICES
echo ============================================
echo.

set BASE_DIR=C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing

:: Quick compile test for our three fixed services
echo Testing compilation of fixed services...
echo.

echo [1] fulfillment-service
cd /d "%BASE_DIR%\fulfillment-service"
call mvn clean compile -DskipTests >compile.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    COMPILE: OK
    call mvn test >test.log 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo    TEST: PASSED
    ) else (
        echo    TEST: FAILED
        type test.log | findstr /i "Tests run:"
    )
) else (
    echo    COMPILE: FAILED
    type compile.log | findstr /i "error" | head -5
)

echo.
echo [2] warehouse-management-service
cd /d "%BASE_DIR%\warehouse-management-service"
call mvn clean compile -DskipTests >compile.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    COMPILE: OK
    call mvn test >test.log 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo    TEST: PASSED
    ) else (
        echo    TEST: FAILED
        type test.log | findstr /i "Tests run:"
    )
) else (
    echo    COMPILE: FAILED
    type compile.log | findstr /i "error" | head -5
)

echo.
echo [3] warehouse-subscription
cd /d "%BASE_DIR%\warehouse-subscription"
call mvn clean compile -DskipTests >compile.log 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    COMPILE: OK
    call mvn test >test.log 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo    TEST: PASSED
    ) else (
        echo    TEST: FAILED
        type test.log | findstr /i "Tests run:"
    )
) else (
    echo    COMPILE: FAILED
    type compile.log | findstr /i "error" | head -5
)

echo.
echo ============================================
echo TEST COMPLETE
echo ============================================
