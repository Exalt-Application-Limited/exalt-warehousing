@echo off
echo ============================================
echo Testing Billing Service Build and Startup
echo ============================================

echo.
echo [1/6] Setting JAVA_HOME...
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.15.6-hotspot
echo JAVA_HOME set to: %JAVA_HOME%

echo.
echo [2/6] Verifying Java version...
"%JAVA_HOME%\bin\java" -version
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java not found or not working
    pause
    exit /b 1
)

echo.
echo [3/6] Cleaning previous builds...
call mvnw.cmd clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)

echo.
echo [4/6] Compiling the application...
call mvnw.cmd compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo [5/6] Running tests...
call mvnw.cmd test -Dspring.profiles.active=test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Tests failed
    pause
    exit /b 1
)

echo.
echo [6/6] Building the application package...
call mvnw.cmd package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Package build failed
    pause
    exit /b 1
)

echo.
echo ============================================
echo BUILD SUCCESSFUL!
echo ============================================
echo.
echo Generated artifacts:
dir target\*.jar
echo.
echo To run the application:
echo java -jar target\billing-service-1.0.0-SNAPSHOT.jar
echo.
echo Or use Docker:
echo docker-compose up -d
echo.

pause