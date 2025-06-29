@echo off
echo ============================================================
echo WAREHOUSING DOMAIN - SERVICE LISTING
echo ============================================================
echo.

echo Backend Services with pom.xml:
echo ------------------------------
set /a COUNT=0

REM Check main app
if exist "pom.xml" (
    set /a COUNT+=1
    echo [%COUNT%] Main Warehousing Application - ./
)

REM Check each service
for %%s in (billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription warehousing-shared) do (
    if exist "%%s\pom.xml" (
        set /a COUNT+=1
        echo [!COUNT!] %%s
    )
)

echo.
echo Frontend Applications:
echo ---------------------
if exist "staff-mobile-app\package.json" echo - staff-mobile-app (React Native)
if exist "global-hq-admin\package.json" echo - global-hq-admin (React)
if exist "regional-admin\package.json" echo - regional-admin (Vue.js)
if exist "self-storage-service\frontend\web-portal\package.json" echo - self-storage-service/frontend/web-portal (React)
if exist "self-storage-service\frontend\mobile-app\package.json" echo - self-storage-service/frontend/mobile-app (React Native)

echo.
echo Total Backend Services: %COUNT%
echo ============================================================