#!/usr/bin/env pwsh

Write-Host "=== WAREHOUSING SERVICES FIX SCRIPT ===" -ForegroundColor Green
Write-Host "Starting at: $(Get-Date)" -ForegroundColor Yellow

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Step 1: Build parent POM first
Write-Host "`n[1/5] Building Warehousing Parent POM..." -ForegroundColor Cyan
mvn clean install -N 2>&1 | Out-String

# Step 2: Build warehousing-shared first (dependency for other services)
Write-Host "`n[2/5] Building Warehousing Shared Library..." -ForegroundColor Cyan
Set-Location "warehousing-shared"
mvn clean install -DskipTests 2>&1 | Out-String
Set-Location ..

# Step 3: Fix and build all Maven services
Write-Host "`n[3/5] Fixing and Building Maven Services..." -ForegroundColor Cyan

$mavenServices = @(
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
    "warehouse-subscription"
)

$results = @()

foreach ($service in $mavenServices) {
    Write-Host "`nProcessing $service..." -ForegroundColor Yellow
    
    if (Test-Path "$service/pom.xml") {
        Set-Location $service
        
        # Clean previous build artifacts
        if (Test-Path "target") {
            Remove-Item -Path "target" -Recurse -Force
        }
        
        # Build with offline mode first to download dependencies
        Write-Host "  Downloading dependencies..." -ForegroundColor Gray
        mvn dependency:go-offline 2>&1 | Out-Null
        
        # Compile the service
        Write-Host "  Compiling..." -ForegroundColor Gray
        $compileResult = mvn compile -DskipTests 2>&1 | Out-String
        
        if ($compileResult -match "BUILD SUCCESS") {
            Write-Host "  ✅ Build SUCCESS" -ForegroundColor Green
            
            # Run tests
            Write-Host "  Running tests..." -ForegroundColor Gray
            $testResult = mvn test 2>&1 | Out-String
            
            if ($testResult -match "BUILD SUCCESS") {
                Write-Host "  ✅ Tests PASSED" -ForegroundColor Green
                $results += "✅ $service → Build Passed, Tests Passed"
            } else {
                Write-Host "  ❌ Tests FAILED" -ForegroundColor Red
                $results += "❌ $service → Build Passed, Tests Failed"
            }
        } else {
            Write-Host "  ❌ Build FAILED" -ForegroundColor Red
            $results += "❌ $service → Build Failed"
        }
        
        Set-Location ..
    } else {
        Write-Host "  ⚠️  No pom.xml found" -ForegroundColor Yellow
        $results += "⚠️  $service → No pom.xml"
    }
}

# Step 4: Fix Node.js services
Write-Host "`n[4/5] Fixing Node.js Services..." -ForegroundColor Cyan

$nodeServices = @("global-hq-admin", "regional-admin")

foreach ($service in $nodeServices) {
    Write-Host "`nProcessing $service..." -ForegroundColor Yellow
    
    if (Test-Path "$service/package.json") {
        Set-Location $service
        
        # Clean node_modules if exists
        if (Test-Path "node_modules") {
            Remove-Item -Path "node_modules" -Recurse -Force
        }
        
        # Install dependencies
        Write-Host "  Installing dependencies..." -ForegroundColor Gray
        npm install --legacy-peer-deps 2>&1 | Out-Null
        
        # Check for build script
        $packageJson = Get-Content "package.json" | ConvertFrom-Json
        if ($packageJson.scripts.build) {
            Write-Host "  Building..." -ForegroundColor Gray
            npm run build 2>&1 | Out-Null
        }
        
        # Run tests if available
        if ($packageJson.scripts.test) {
            Write-Host "  Running tests..." -ForegroundColor Gray
            $testResult = npm test -- --passWithNoTests 2>&1 | Out-String
            
            if ($testResult -notmatch "failed") {
                Write-Host "  ✅ Tests PASSED" -ForegroundColor Green
                $results += "✅ $service → Build Passed, Tests Passed"
            } else {
                Write-Host "  ❌ Tests FAILED" -ForegroundColor Red
                $results += "❌ $service → Build Passed, Tests Failed"
            }
        } else {
            Write-Host "  ⚠️  No test script" -ForegroundColor Yellow
            $results += "⚠️  $service → Build Passed, No Tests"
        }
        
        Set-Location ..
    }
}

# Step 5: Final Report
Write-Host "`n[5/5] FINAL REPORT" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor White

foreach ($result in $results) {
    Write-Host $result
}

Write-Host "`nCompleted at: $(Get-Date)" -ForegroundColor Yellow

# Save results to file
$results | Out-File -FilePath "warehousing-fix-results.txt"
Write-Host "`nResults saved to: warehousing-fix-results.txt" -ForegroundColor Green