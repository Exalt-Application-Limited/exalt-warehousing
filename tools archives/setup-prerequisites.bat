@echo off
REM setup-prerequisites.bat - Install prerequisites for warehousing services
echo ==========================================
echo WAREHOUSING SERVICES - PREREQUISITES SETUP
echo ==========================================
echo.

echo This script will guide you through installing required tools.
echo.

echo 1. Java JDK 17 Check
echo --------------------
java -version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [NOT FOUND] Java is not installed
    echo Please download from: https://adoptium.net/
    echo.
) else (
    echo [FOUND] Java is installed
    java -version
    echo.
)

echo 2. Maven Check
echo --------------
mvn --version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [NOT FOUND] Maven is not installed
    echo Please download from: https://maven.apache.org/download.cgi
    echo Installation steps:
    echo   1. Download apache-maven-3.9.x-bin.zip
    echo   2. Extract to C:\Program Files\
    echo   3. Add C:\Program Files\apache-maven-3.9.x\bin to PATH
    echo   4. Restart command prompt
    echo.
) else (
    echo [FOUND] Maven is installed
    mvn --version
    echo.
)

echo 3. Docker Check
echo ---------------
docker --version 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [NOT FOUND] Docker is not installed
    echo Please download from: https://www.docker.com/products/docker-desktop/
    echo.
) else (
    echo [FOUND] Docker is installed
    docker --version
    echo.
)

echo ==========================================
echo After installing missing tools, run the tests:
echo   1. warehousing-test-dashboard.bat - View status
echo   2. cd [service-name] - Navigate to service
echo   3. test-and-deploy.bat - Run full test suite
echo ==========================================
pause
