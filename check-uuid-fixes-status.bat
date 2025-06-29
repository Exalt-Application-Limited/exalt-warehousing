@echo off
echo ===========================================
echo WAREHOUSING SERVICES UUID FIX SUMMARY
echo ===========================================
echo.

echo [1] Building Parent POM...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"
call mvn clean install -N -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    ✓ Parent POM built successfully
) else (
    echo    ✗ Parent POM build failed
)

echo.
echo [2] Building warehousing-shared...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehousing-shared"
call mvn clean install -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    ✓ warehousing-shared built successfully
) else (
    echo    ✗ warehousing-shared build failed
)

echo.
echo [3] Testing fulfillment-service...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"
call mvn clean compile -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    ✓ fulfillment-service compiles successfully
) else (
    echo    ✗ fulfillment-service compilation failed
)

echo.
echo [4] Testing warehouse-management-service...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-management-service"
call mvn clean compile -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    ✓ warehouse-management-service compiles successfully
) else (
    echo    ✗ warehouse-management-service compilation failed
)

echo.
echo [5] Testing warehouse-subscription...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription"
call mvn clean compile -DskipTests >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo    ✓ warehouse-subscription compiles successfully
) else (
    echo    ✗ warehouse-subscription compilation failed
)

echo.
echo ===========================================
echo UUID/STRING FIX SUMMARY COMPLETE
echo ===========================================
echo.
echo Next Steps:
echo 1. Fix any compilation errors shown above
echo 2. Set up Central Configuration services
echo 3. Fix POM configuration errors
echo.
pause
