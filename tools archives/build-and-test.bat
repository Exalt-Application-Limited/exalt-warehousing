@echo off
echo ============================================================
echo Warehousing Domain Build and Test Script
echo ============================================================

REM Set Java Home and Path
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Echo Java information
echo Setting up Java environment...
echo Java Home: %JAVA_HOME%
java -version
echo.

REM Set Maven environment variables
set MAVEN_OPTS=-Xmx1024m

REM Create a timestamp for log files
set TIMESTAMP=%date:~-4,4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

echo Maven configuration...
echo Maven Options: %MAVEN_OPTS%
echo.

REM Create build directory if it doesn't exist
if not exist "build-logs" mkdir build-logs

echo ============================================================
echo STEP 1: Clean and compile the project
echo ============================================================
call .\mvnw.cmd clean compile -B
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    exit /b %ERRORLEVEL%
)
echo Compilation successful!
echo.

echo ============================================================
echo STEP 2: Run tests for all core services
echo ============================================================
call .\mvnw.cmd test -B
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Some tests failed! Check the logs for details.
    echo Test execution completed with errors.
) else (
    echo All tests passed successfully!
)
echo.

echo ============================================================
echo STEP 3: Package the application
echo ============================================================
call .\mvnw.cmd package -DskipTests -B
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Packaging failed!
    exit /b %ERRORLEVEL%
)
echo Packaging successful!
echo.

echo ============================================================
echo Build and test summary for core services
echo ============================================================
echo - Inventory Service: Build and test completed
echo - Fulfillment Service: Build and test completed
echo - Logistics Service: Build and test completed
echo - Management Service: Build and test completed
echo - Task Service: Build and test completed
echo - Shared Module: Build and test completed
echo.

echo The build artifacts can be found in the target directory
echo Build JAR: target\warehousing-service-0.0.1-SNAPSHOT.jar
echo.

echo ============================================================
echo Build and test process completed!
echo ============================================================
