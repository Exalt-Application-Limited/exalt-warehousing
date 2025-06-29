@echo off
REM Warehouse Onboarding Service Build and Test Script
REM This script builds and tests the warehouse onboarding service

echo ============================================
echo Warehouse Onboarding Service Build Script
echo ============================================

set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Current directory: %CD%
echo Java version:
java -version

echo.
echo ============================================
echo Step 1: Cleaning previous builds
echo ============================================
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven clean failed
    exit /b 1
)

echo.
echo ============================================
echo Step 2: Compiling source code
echo ============================================
call mvn compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven compile failed
    exit /b 1
)

echo.
echo ============================================
echo Step 3: Running tests
echo ============================================
call mvn test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven test failed
    exit /b 1
)

echo.
echo ============================================
echo Step 4: Packaging application
echo ============================================
call mvn package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven package failed
    exit /b 1
)

echo.
echo ============================================
echo Step 5: Validating JAR file
echo ============================================
if exist target\warehouse-onboarding-1.0.0-SNAPSHOT.jar (
    echo SUCCESS: JAR file created successfully
    dir target\warehouse-onboarding-1.0.0-SNAPSHOT.jar
) else (
    echo ERROR: JAR file not found
    exit /b 1
)

echo.
echo ============================================
echo Step 6: Running Spring Boot health check
echo ============================================
echo Starting application for health check...
start /B java -jar target\warehouse-onboarding-1.0.0-SNAPSHOT.jar --spring.profiles.active=test --server.port=8085 > app.log 2>&1

REM Wait for application to start
echo Waiting for application to start...
timeout /t 30 /nobreak > nul

REM Check if application is running
curl -f http://localhost:8085/warehouse-onboarding/actuator/health > nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo SUCCESS: Application started and health check passed
) else (
    echo WARNING: Health check failed or application not ready
)

REM Stop the application
echo Stopping application...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8085') do taskkill /PID %%a /F > nul 2>&1

echo.
echo ============================================
echo Step 7: Build Summary
echo ============================================
echo Build completed successfully!
echo.
echo Generated artifacts:
if exist target\warehouse-onboarding-1.0.0-SNAPSHOT.jar (
    echo   - JAR: target\warehouse-onboarding-1.0.0-SNAPSHOT.jar
)
if exist target\site (
    echo   - Reports: target\site\
)
if exist target\surefire-reports (
    echo   - Test Reports: target\surefire-reports\
)

echo.
echo Build Status: SUCCESS
echo Service: Warehouse Onboarding Service
echo Version: 1.0.0-SNAPSHOT
echo Java Version: %JAVA_VERSION%
echo Build Time: %DATE% %TIME%

echo.
echo ============================================
echo Next Steps:
echo ============================================
echo 1. Deploy with: docker-compose up -d
echo 2. Access API docs: http://localhost:8085/warehouse-onboarding/swagger-ui.html
echo 3. Monitor health: http://localhost:8085/warehouse-onboarding/actuator/health
echo 4. View logs: docker-compose logs warehouse-onboarding-service

pause