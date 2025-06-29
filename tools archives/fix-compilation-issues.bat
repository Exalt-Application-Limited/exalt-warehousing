@echo off
echo ============================================================
echo Warehousing Domain Compilation Fix Script
echo ============================================================

REM Set Java Home and Path
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Echo Java information
echo Setting up Java environment...
echo Java Home: %JAVA_HOME%
java -version
echo.

REM Step 1: Clean the project
echo ============================================================
echo STEP 1: Cleaning the project
echo ============================================================
call .\mvnw.cmd clean -B
echo.

REM Step 2: Fix compilation issues
echo ============================================================
echo STEP 2: Fixing compilation issues
echo ============================================================

REM First check if there are javax imports that need to be fixed
echo Checking for javax imports that need to be converted to jakarta...
call .\fix-javax-imports.bat
echo.

REM Build just to compile to see if any more errors
echo ============================================================
echo STEP 3: Compiling to identify any remaining issues
echo ============================================================
call .\mvnw.cmd compile -B
echo.

echo ============================================================
echo Compilation fix process completed!
echo If errors still exist, they will be shown above.
echo ============================================================
