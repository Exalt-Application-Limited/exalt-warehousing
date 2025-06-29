@echo off
echo ============================================
echo WAREHOUSE OPERATIONS STANDALONE TEST
echo ============================================

REM Set Java environment - trying multiple paths
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
if not exist "%JAVA_HOME%" set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.15.6-hotspot"
if not exist "%JAVA_HOME%" set "JAVA_HOME=C:\Program Files\Java\jdk-17"

echo Using JAVA_HOME: %JAVA_HOME%
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Verify Java
echo Testing Java installation...
java -version
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java not found or not working
    exit /b 1
)

echo.
echo ============================================
echo WAREHOUSE OPERATIONS COMPILATION
echo ============================================

echo Starting Maven compilation...
call mvnw.cmd clean compile -DskipTests -q
if %ERRORLEVEL% equ 0 (
    echo.
    echo ✅ SUCCESS: Warehouse Operations compilation completed!
    echo.
    echo Checking compiled classes...
    if exist "target\classes\com\microcommerce\warehousing\operations\WarehouseOperationsApplication.class" (
        echo ✅ Main application class compiled successfully!
    )
    if exist "target\classes\com\microcommerce\warehousing\operations\entity" (
        echo ✅ Entity classes compiled successfully!
    )
    if exist "target\classes\com\microcommerce\warehousing\operations\repository" (
        echo ✅ Repository classes compiled successfully!
    )
    if exist "target\classes\com\microcommerce\warehousing\operations\service" (
        echo ✅ Service classes compiled successfully!
    )
    if exist "target\classes\com\microcommerce\warehousing\operations\controller" (
        echo ✅ Controller classes compiled successfully!
    )
) else (
    echo ❌ FAILED: Warehouse Operations compilation failed
    echo Error code: %ERRORLEVEL%
)

echo.
echo ============================================
echo TEST COMPLETE
echo ============================================
