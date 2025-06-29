#!/usr/bin/env pwsh
# Comprehensive Warehousing Domain Test Script

$baseDir = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "WAREHOUSING DOMAIN - COMPREHENSIVE TEST SUITE" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Initialize counters
$totalServices = 0
$passedBuilds = 0
$failedBuilds = 0
$passedTests = 0
$failedTests = 0

# Build parent and shared first
Write-Host "Building prerequisites..." -ForegroundColor Yellow
Set-Location $baseDir
mvn clean install -N -DskipTests *>$null
Set-Location "$baseDir\warehousing-shared"
mvn clean install -DskipTests *>$null

# List of services
$services = @(
    "billing-service",
    "cross-region-logistics-service",
    "fulfillment-service",
    "inventory-service",
    "self-storage-service",
    "staff-mobile-app",
    "warehouse-analytics",
    "warehouse-management-service",
    "warehouse-onboarding",
    "warehouse-operations",
    "warehouse-subscription",
    "warehousing-production",
    "warehousing-staging"
)

Write-Host "`nTesting all services..." -ForegroundColor Yellow
Write-Host "========================" -ForegroundColor Yellow

foreach ($service in $services) {
    $totalServices++
    Write-Host "`n[$service]" -ForegroundColor White
    
    Set-Location "$baseDir\$service"
    
    # Build
    Write-Host "  Building..." -NoNewline
    $buildResult = mvn clean compile -DskipTests 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host " OK" -ForegroundColor Green
        $passedBuilds++
        
        # Test
        Write-Host "  Testing..." -NoNewline
        $testResult = mvn test 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host " PASSED" -ForegroundColor Green
            $passedTests++
        } else {
            Write-Host " FAILED" -ForegroundColor Red
            $failedTests++
            # Show test summary
            $testSummary = $testResult | Select-String "Tests run:" | Select -First 1
            if ($testSummary) { Write-Host "    $testSummary" -ForegroundColor Gray }
        }
    } else {
        Write-Host " FAILED" -ForegroundColor Red
        $failedBuilds++
        $failedTests++
        # Show first error
        $firstError = $testResult | Select-String "ERROR" | Select -First 1
        if ($firstError) { Write-Host "    $firstError" -ForegroundColor Gray }
    }
}

# Summary Report
Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "FINAL REPORT" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Services: $totalServices"
Write-Host ""
Write-Host "Build Results:" -ForegroundColor Yellow
Write-Host "  Passed: $passedBuilds" -ForegroundColor Green
Write-Host "  Failed: $failedBuilds" -ForegroundColor Red
Write-Host ""
Write-Host "Test Results:" -ForegroundColor Yellow
Write-Host "  Passed: $passedTests" -ForegroundColor Green
Write-Host "  Failed: $failedTests" -ForegroundColor Red
Write-Host ""

if ($failedBuilds -eq 0 -and $failedTests -eq 0) {
    Write-Host "✅ ALL BUILDS AND TESTS PASSED!" -ForegroundColor Green
    Write-Host "The warehousing domain is fully functional." -ForegroundColor Green
    exit 0
} else {
    Write-Host "❌ Some builds or tests failed." -ForegroundColor Red
    exit 1
}
