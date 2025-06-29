@echo off
echo ============================================================
echo WAREHOUSING DOMAIN - SERVICE TEST RUNNER
echo ============================================================
echo.

REM Set Java environment
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
set MAVEN_OPTS=-Xmx2048m -XX:+TieredCompilation -XX:TieredStopAtLevel=1

REM Create test results directory
if not exist "test-results" mkdir test-results
set TEST_TIMESTAMP=%date:~-4%%date:~4,2%%date:~7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TEST_TIMESTAMP=%TEST_TIMESTAMP: =0%

echo Test run started at: %date% %time%
echo Results will be saved in: test-results\
echo.

REM Initialize counters
set /a TOTAL_SERVICES=0
set /a PASSED_SERVICES=0
set /a FAILED_SERVICES=0
set /a SKIPPED_SERVICES=0

REM Create test report header
(
echo # Warehousing Domain Test Report
echo Generated: %date% %time%
echo.
echo ## Test Environment
echo - Java Version: 17.0.15
echo - Maven: 3.8+
echo - Spring Boot: 3.1.1
echo.
echo ## Service Test Results
echo.
) > test-results\TEST_REPORT_%TEST_TIMESTAMP%.md

REM List of all services to test
echo ============================================================
echo DISCOVERING SERVICES...
echo ============================================================
echo.

REM Main warehousing application
if exist "pom.xml" (
    echo [1] Main Warehousing Application
    set /a TOTAL_SERVICES+=1
)

REM Microservices
set SERVICE_COUNT=1
for %%s in (billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription warehousing-shared) do (
    if exist "%%s\pom.xml" (
        set /a SERVICE_COUNT+=1
        set /a TOTAL_SERVICES+=1
        echo [!SERVICE_COUNT!] %%s
    )
)

echo.
echo Total services found: %TOTAL_SERVICES%
echo.

REM Test each service
echo ============================================================
echo RUNNING UNIT TESTS FOR EACH SERVICE...
echo ============================================================
echo.

REM Test main application
echo [1/%TOTAL_SERVICES%] Testing Main Warehousing Application...
echo ----------------------------------------
if exist "pom.xml" (
    echo Running: mvn test
    
    if exist "mvnw.cmd" (
        call mvnw.cmd test > test-results\main-app-test.log 2>&1
    ) else (
        call mvn test > test-results\main-app-test.log 2>&1
    )
    
    if %ERRORLEVEL% EQU 0 (
        echo [PASSED] Main Warehousing Application
        set /a PASSED_SERVICES+=1
        
        REM Extract test summary
        findstr /C:"Tests run:" test-results\main-app-test.log > test-results\temp.txt
        set /p TEST_SUMMARY=<test-results\temp.txt
        
        (
        echo ### Main Warehousing Application - ✅ PASSED
        echo %TEST_SUMMARY%
        echo.
        ) >> test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
    ) else (
        echo [FAILED] Main Warehousing Application
        set /a FAILED_SERVICES+=1
        
        (
        echo ### Main Warehousing Application - ❌ FAILED
        echo Check test-results\main-app-test.log for details
        echo.
        ) >> test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
    )
) else (
    echo [SKIPPED] No pom.xml found
    set /a SKIPPED_SERVICES+=1
)
echo.

REM Test each microservice
set CURRENT_SERVICE=1
for %%s in (billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription warehousing-shared) do (
    if exist "%%s\pom.xml" (
        set /a CURRENT_SERVICE+=1
        echo [!CURRENT_SERVICE!/%TOTAL_SERVICES%] Testing %%s...
        echo ----------------------------------------
        
        cd %%s
        
        REM Check if tests exist
        if exist "src\test\java" (
            echo Running: mvn test
            
            if exist "mvnw.cmd" (
                call mvnw.cmd test > ..\test-results\%%s-test.log 2>&1
            ) else if exist "..\mvnw.cmd" (
                call ..\mvnw.cmd test > ..\test-results\%%s-test.log 2>&1
            ) else (
                call mvn test > ..\test-results\%%s-test.log 2>&1
            )
            
            if !ERRORLEVEL! EQU 0 (
                echo [PASSED] %%s
                set /a PASSED_SERVICES+=1
                
                REM Extract test summary
                findstr /C:"Tests run:" ..\test-results\%%s-test.log > ..\test-results\temp.txt
                if exist "..\test-results\temp.txt" (
                    set /p TEST_SUMMARY=<..\test-results\temp.txt
                ) else (
                    set TEST_SUMMARY=Tests completed successfully
                )
                
                (
                echo ### %%s - ✅ PASSED
                echo !TEST_SUMMARY!
                echo.
                ) >> ..\test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
            ) else (
                echo [FAILED] %%s
                set /a FAILED_SERVICES+=1
                
                REM Try to extract error summary
                findstr /C:"Failed tests:" ..\test-results\%%s-test.log > ..\test-results\temp.txt
                if exist "..\test-results\temp.txt" (
                    set /p ERROR_SUMMARY=<..\test-results\temp.txt
                ) else (
                    set ERROR_SUMMARY=Test execution failed
                )
                
                (
                echo ### %%s - ❌ FAILED
                echo !ERROR_SUMMARY!
                echo Check test-results\%%s-test.log for details
                echo.
                ) >> ..\test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
            )
        ) else (
            echo [NO TESTS] No test directory found
            set /a SKIPPED_SERVICES+=1
            
            (
            echo ### %%s - ⚠️ NO TESTS
            echo No tests found in src\test\java
            echo.
            ) >> ..\test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
        )
        
        cd ..
        echo.
    )
)

REM Create summary
echo ============================================================
echo TEST EXECUTION SUMMARY
echo ============================================================
echo.
echo Total Services:    %TOTAL_SERVICES%
echo Passed:           %PASSED_SERVICES%
echo Failed:           %FAILED_SERVICES%
echo Skipped/No Tests: %SKIPPED_SERVICES%
echo.

REM Calculate success rate
set /a SUCCESS_RATE=(%PASSED_SERVICES%*100)/%TOTAL_SERVICES%

(
echo ## Summary
echo.
echo ^| Metric ^| Value ^|
echo ^|--------|--------|
echo ^| Total Services ^| %TOTAL_SERVICES% ^|
echo ^| Passed ^| %PASSED_SERVICES% ^|
echo ^| Failed ^| %FAILED_SERVICES% ^|
echo ^| Skipped/No Tests ^| %SKIPPED_SERVICES% ^|
echo ^| Success Rate ^| %SUCCESS_RATE%%% ^|
echo.
echo ## Failed Services
echo.
) >> test-results\TEST_REPORT_%TEST_TIMESTAMP%.md

if %FAILED_SERVICES% GTR 0 (
    echo The following services have failing tests:
    for %%s in (billing-service inventory-service fulfillment-service self-storage-service warehouse-analytics warehouse-onboarding warehouse-operations warehouse-subscription warehousing-shared) do (
        if exist "test-results\%%s-test.log" (
            findstr /C:"BUILD FAILURE" test-results\%%s-test.log >nul
            if !ERRORLEVEL! EQU 0 (
                echo - %%s
                echo - %%s >> test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
            )
        )
    )
) else (
    echo None - All tests passed! >> test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
)

echo.
echo ============================================================
echo TEST RUN COMPLETE
echo ============================================================
echo.
echo Test report saved to: test-results\TEST_REPORT_%TEST_TIMESTAMP%.md
echo Individual test logs saved in: test-results\
echo.

if %FAILED_SERVICES% GTR 0 (
    echo ⚠️  WARNING: Some services have failing tests!
    echo Please check the test logs for details.
    exit /b 1
) else if %PASSED_SERVICES% EQU %TOTAL_SERVICES% (
    echo ✅ SUCCESS: All services passed their tests!
    exit /b 0
) else (
    echo ⚠️  WARNING: Some services have no tests or were skipped.
    exit /b 0
)

REM Cleanup
if exist "test-results\temp.txt" del test-results\temp.txt