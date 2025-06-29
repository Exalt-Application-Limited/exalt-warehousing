@echo off
echo ============================================================
echo Warehousing Core Services Compilation Script
echo ============================================================

REM Set environment variables for Java and Maven
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
set MAVEN_OPTS=-Xmx1024m -XX:MaxMetaspaceSize=512m -XX:+UseParallelGC

echo Java Home: %JAVA_HOME%
java -version
echo.
echo Maven Options: %MAVEN_OPTS%
echo.

echo ============================================================
echo STEP 1: Clean the project workspace
echo ============================================================
call .\mvnw.cmd clean -B
echo.

echo ============================================================
echo STEP 2: Compile the core services with optimized JVM settings
echo ============================================================
echo Compiling: Inventory Service, Fulfillment Service, Logistics Service
echo Management Service, Task Service, and Shared Module...
echo.

call .\mvnw.cmd compile -DskipTests -B

if %ERRORLEVEL% NEQ 0 (
    echo !!!! Compilation failed with errors !!!!
) else (
    echo ==== Core Services Compilation Completed Successfully ====
)
echo.

echo ============================================================
echo Summary of Core Services Status:
echo ============================================================
echo - Inventory Service: See compilation results above
echo - Fulfillment Service: See compilation results above
echo - Logistics Service: See compilation results above
echo - Management Service: See compilation results above
echo - Task Service: See compilation results above
echo - Shared Module: See compilation results above
echo ============================================================
