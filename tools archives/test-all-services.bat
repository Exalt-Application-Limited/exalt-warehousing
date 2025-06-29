@echo off
REM test-all-services.bat - Test compilation and startup for all warehousing services

echo ==========================================
echo WAREHOUSING SERVICES COMPILATION TEST
echo ==========================================
echo.

REM Base directory
set BASE_DIR=%~dp0

REM Test each service
echo Testing inventory-service...
cd /d "%BASE_DIR%inventory-service"
echo   Compiling...
call mvn clean compile -DskipTests > "%TEMP%\inventory-service-compile.log" 2>&1
if %ERRORLEVEL% EQU 0 (
    echo   [OK] Compilation successful
) else (
    echo   [ERROR] Compilation failed - check %TEMP%\inventory-service-compile.log
)
echo.

echo Testing fulfillment-service...
cd /d "%BASE_DIR%fulfillment-service"
echo   Compiling...
call mvn clean compile -DskipTests > "%TEMP%\fulfillment-service-compile.log" 2>&1
if %ERRORLEVEL% EQU 0 (
    echo   [OK] Compilation successful
) else (
    echo   [ERROR] Compilation failed - check %TEMP%\fulfillment-service-compile.log
)
echo.

echo Testing warehouse-management-service...
cd /d "%BASE_DIR%warehouse-management-service"
echo   Compiling...
call mvn clean compile -DskipTests > "%TEMP%\warehouse-management-service-compile.log" 2>&1
if %ERRORLEVEL% EQU 0 (
    echo   [OK] Compilation successful
) else (
    echo   [ERROR] Compilation failed - check %TEMP%\warehouse-management-service-compile.log
)
echo.

echo Testing cross-region-logistics-service...
cd /d "%BASE_DIR%cross-region-logistics-service"
echo   Compiling...
call mvn clean compile -DskipTests > "%TEMP%\cross-region-logistics-service-compile.log" 2>&1
if %ERRORLEVEL% EQU 0 (
    echo   [OK] Compilation successful
) else (
    echo   [ERROR] Compilation failed - check %TEMP%\cross-region-logistics-service-compile.log
)
echo.

echo Testing self-storage-service...
cd /d "%BASE_DIR%self-storage-service"
echo   Compiling...
call mvn clean compile -DskipTests > "%TEMP%\self-storage-service-compile.log" 2>&1
if %ERRORLEVEL% EQU 0 (
    echo   [OK] Compilation successful
) else (
    echo   [ERROR] Compilation failed - check %TEMP%\self-storage-service-compile.log
)
echo.

echo ==========================================
echo TEST COMPLETE
echo ==========================================
echo.
echo Check %TEMP%\*-compile.log for compilation logs
echo.
echo To run a specific service:
echo   cd [service-name]
echo   mvn spring-boot:run
echo.

cd /d "%BASE_DIR%"
pause
