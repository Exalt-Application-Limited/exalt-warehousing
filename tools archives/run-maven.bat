@echo off
echo ====================================================
echo Warehousing Service Maven Build with Enhanced Settings
echo ====================================================

:: Set Java environment variables
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java from: %JAVA_HOME%

:: Set Maven memory options to prevent OOM errors (Java 17 compatible)
set MAVEN_OPTS=-Xmx1024m -Xms512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./java_heap_dump.hprof -XX:+UseG1GC

:: Set Java parameters for improved stability
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 -XX:+UseG1GC -XX:+UseStringDeduplication

echo Maven options: %MAVEN_OPTS%
echo Java tool options: %JAVA_TOOL_OPTIONS%

:: Display Java version
echo Java version information:
call java -version
echo.

:: Run Maven build with explicit error handling
echo Starting Maven build...
echo.

:: First compile without tests to ensure all classes compile properly
echo Step 1: Compiling without tests...
call .\mvnw.cmd clean compile -DskipTests

IF %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed! See above for details.
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.

:: Now run tests if compilation was successful
echo Step 2: Running test-compile to verify test classes...
call .\mvnw.cmd test-compile

IF %ERRORLEVEL% NEQ 0 (
    echo ERROR: Test compilation failed! See above for details.
    exit /b %ERRORLEVEL%
)

echo All compilation steps completed successfully!
echo.
echo You can now run full tests with: .\mvnw.cmd test
echo Or package the application with: .\mvnw.cmd package
echo.
