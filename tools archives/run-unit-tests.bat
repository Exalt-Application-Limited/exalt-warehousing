@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo WAREHOUSING DOMAIN - UNIT TEST RUNNER
echo ============================================================
echo.

REM Set Java environment
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM Create test results directory
if not exist "test-results" mkdir test-results

echo Starting test execution: %date% %time%
echo Java Version:
java -version 2>&1 | findstr /i version
echo.

REM Initialize counters
set /a TOTAL_SERVICES=0
set /a PASSED_TESTS=0
set /a FAILED_TESTS=0
set /a NO_TESTS=0

echo ============================================================
echo RUNNING UNIT TESTS FOR ALL SERVICES
echo ============================================================
echo.

REM Test main application
set /a TOTAL_SERVICES+=1
echo [%TOTAL_SERVICES%] Testing Main Warehousing Application...
echo --------------------------------------------
if exist "pom.xml" (
    if exist "src\test\java" (
        echo Executing: mvnw.cmd test
        call mvnw.cmd test > test-results\main-app-test.log 2>&1
        
        if !ERRORLEVEL! EQU 0 (
            echo Status: PASSED
            set /a PASSED_TESTS+=1
            findstr /C:"Tests run:" test-results\main-app-test.log | findstr /V /C:"Tests run: 0"
        ) else (
            echo Status: FAILED
            set /a FAILED_TESTS+=1
            echo Check test-results\main-app-test.log for details
        )
    ) else (
        echo Status: NO TESTS FOUND
        set /a NO_TESTS+=1
    )
) else (
    echo Status: NO POM.XML
    set /a NO_TESTS+=1
)
echo.

REM Define services list
set "SERVICES=billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription warehousing-shared"

REM Test each service
for %%s in (%SERVICES%) do (
    set /a TOTAL_SERVICES+=1
    echo [!TOTAL_SERVICES!] Testing %%s...
    echo --------------------------------------------
    
    if exist "%%s\pom.xml" (
        cd %%s
        
        if exist "src\test\java" (
            echo Executing: mvnw.cmd test
            
            REM Use parent mvnw if exists, otherwise use local mvnw
            if exist "..\mvnw.cmd" (
                call ..\mvnw.cmd test > ..\test-results\%%s-test.log 2>&1
            ) else if exist "mvnw.cmd" (
                call mvnw.cmd test > ..\test-results\%%s-test.log 2>&1
            ) else (
                echo ERROR: No Maven wrapper found
                set /a NO_TESTS+=1
                cd ..
                echo.
                continue
            )
            
            if !ERRORLEVEL! EQU 0 (
                echo Status: PASSED
                set /a PASSED_TESTS+=1
                findstr /C:"Tests run:" ..\test-results\%%s-test.log | findstr /V /C:"Tests run: 0"
            ) else (
                REM Check if it's actually no tests or a failure
                findstr /C:"Tests run: 0" ..\test-results\%%s-test.log >nul
                if !ERRORLEVEL! EQU 0 (
                    echo Status: NO TESTS IMPLEMENTED
                    set /a NO_TESTS+=1
                ) else (
                    echo Status: FAILED
                    set /a FAILED_TESTS+=1
                    echo Check test-results\%%s-test.log for details
                    
                    REM Extract failure summary
                    echo Failures:
                    findstr /C:"Failed tests:" ..\test-results\%%s-test.log
                    findstr /C:"Tests in error:" ..\test-results\%%s-test.log
                )
            )
        ) else (
            echo Status: NO TEST DIRECTORY
            set /a NO_TESTS+=1
        )
        
        cd ..
    ) else (
        echo Status: SERVICE NOT FOUND
        set /a NO_TESTS+=1
    )
    echo.
)

echo ============================================================
echo TEST EXECUTION SUMMARY
echo ============================================================
echo.
echo Total Services Tested: %TOTAL_SERVICES%
echo Passed:               %PASSED_TESTS%
echo Failed:               %FAILED_TESTS%
echo No Tests/Skipped:     %NO_TESTS%
echo.

REM Generate detailed report
(
echo # Warehousing Domain Test Report
echo Generated: %date% %time%
echo.
echo ## Summary
echo - Total Services: %TOTAL_SERVICES%
echo - Passed: %PASSED_TESTS%
echo - Failed: %FAILED_TESTS%
echo - No Tests: %NO_TESTS%
echo.
echo ## Individual Service Results
echo.
) > test-results\TEST_SUMMARY.md

REM Check each log file for details
for %%s in (main-app %SERVICES%) do (
    if exist "test-results\%%s-test.log" (
        echo ### %%s >> test-results\TEST_SUMMARY.md
        findstr /C:"Tests run:" test-results\%%s-test.log >> test-results\TEST_SUMMARY.md
        findstr /C:"BUILD SUCCESS" test-results\%%s-test.log >nul
        if !ERRORLEVEL! EQU 0 (
            echo Status: SUCCESS >> test-results\TEST_SUMMARY.md
        ) else (
            echo Status: FAILED >> test-results\TEST_SUMMARY.md
            findstr /C:"Failed tests:" test-results\%%s-test.log >> test-results\TEST_SUMMARY.md
        )
        echo. >> test-results\TEST_SUMMARY.md
    )
)

echo Test report saved to: test-results\TEST_SUMMARY.md
echo Individual logs in: test-results\
echo.

if %FAILED_TESTS% GTR 0 (
    echo ============================================================
    echo WARNING: %FAILED_TESTS% service(s) have failing tests!
    echo ============================================================
    echo.
    echo Failed services:
    for %%s in (%SERVICES%) do (
        if exist "test-results\%%s-test.log" (
            findstr /C:"BUILD FAILURE" test-results\%%s-test.log >nul
            if !ERRORLEVEL! EQU 0 (
                echo - %%s
            )
        )
    )
    echo.
    echo Run the following to see specific failures:
    echo - type test-results\[service-name]-test.log
) else if %PASSED_TESTS% EQU %TOTAL_SERVICES% (
    echo ============================================================
    echo SUCCESS: All services with tests are passing!
    echo ============================================================
) else (
    echo ============================================================
    echo INFO: Some services have no tests implemented yet.
    echo ============================================================
)

endlocal