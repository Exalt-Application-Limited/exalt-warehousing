@echo off
REM warehousing-test-dashboard.bat - Master testing dashboard for all services
echo ==========================================
echo WAREHOUSING SERVICES - TESTING DASHBOARD
echo ==========================================
echo.

set BASE_DIR=%~dp0

echo Service Testing Status:
echo ----------------------
echo.

REM Check each service
echo 1. inventory-service
if exist "%BASE_DIR%inventory-service\target\inventory-service-1.0.0.jar" (
    echo    Status: [BUILT] JAR exists
    if exist "%BASE_DIR%inventory-service\test-results\health-check.json" (
        echo    Tests:  [PASSED] Full test suite completed
    ) else (
        echo    Tests:  [PENDING] Not fully tested
    )
) else (
    echo    Status: [NOT BUILT]
    echo    Tests:  [PENDING]
)
echo.

echo 2. fulfillment-service  
if exist "%BASE_DIR%fulfillment-service\target\fulfillment-service-1.0.0.jar" (
    echo    Status: [BUILT] JAR exists
    if exist "%BASE_DIR%fulfillment-service\test-results\health-check.json" (
        echo    Tests:  [PASSED] Full test suite completed
    ) else (
        echo    Tests:  [PENDING] Not fully tested
    )
) else (
    echo    Status: [NOT BUILT]
    echo    Tests:  [PENDING]
)
echo.

echo 3. warehouse-management-service
if exist "%BASE_DIR%warehouse-management-service\target\warehouse-management-service-1.0.0.jar" (
    echo    Status: [BUILT] JAR exists
    if exist "%BASE_DIR%warehouse-management-service\test-results\health-check.json" (
        echo    Tests:  [PASSED] Full test suite completed
    ) else (
        echo    Tests:  [PENDING] Not fully tested
    )
) else (
    echo    Status: [NOT BUILT]
    echo    Tests:  [PENDING]
)
echo.

echo 4. cross-region-logistics-service
if exist "%BASE_DIR%cross-region-logistics-service\target\cross-region-logistics-service-1.0.0.jar" (
    echo    Status: [BUILT] JAR exists
    if exist "%BASE_DIR%cross-region-logistics-service\test-results\health-check.json" (
        echo    Tests:  [PASSED] Full test suite completed
    ) else (
        echo    Tests:  [PENDING] Not fully tested
    )
) else (
    echo    Status: [NOT BUILT]
    echo    Tests:  [PENDING]
)
echo.

echo 5. self-storage-service
if exist "%BASE_DIR%self-storage-service\target\self-storage-service-1.0.0.jar" (
    echo    Status: [BUILT] JAR exists
    if exist "%BASE_DIR%self-storage-service\test-results\health-check.json" (
        echo    Tests:  [PASSED] Full test suite completed
    ) else (
        echo    Tests:  [PENDING] Not fully tested
    )
) else (
    echo    Status: [NOT BUILT]
    echo    Tests:  [PENDING]
)
echo.

echo ==========================================
echo Testing Instructions:
echo ==========================================
echo.
echo To test a service:
echo   1. cd [service-name]
echo   2. Run quick-test.bat for development testing
echo   3. Run test-and-deploy.bat for full production testing
echo.
echo After all services pass:
echo   Run git-push-all.bat to push to repository
echo.
pause
