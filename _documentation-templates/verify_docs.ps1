# Documentation Verification Script
# This script checks for required documentation files in each service directory

$services = @(
    "billing-service",
    "fulfillment-service",
    "inventory-service",
    "warehouse-management-service",
    "warehouse-onboarding",
    "warehouse-operations",
    "warehouse-subscription",
    "config-server-enterprise",
    "integration-tests"
)

$requiredFiles = @(
    "README.md",
    "docs/README.md",
    "docs/setup/README.md",
    "docs/operations/README.md",
    "docs/architecture/README.md",
    "docs/api/README.md"
)

Write-Host "=== Documentation Status Report ===" -ForegroundColor Cyan
Write-Host "Generated: $(Get-Date)"
Write-Host ""

foreach ($service in $services) {
    $servicePath = Join-Path -Path $PSScriptRoot -ChildPath "..\$service"
    
    if (-not (Test-Path $servicePath)) {
        Write-Host "❌ $service - Service directory not found" -ForegroundColor Red
        continue
    }
    
    Write-Host "📁 $service" -ForegroundColor Green
    
    $allFilesExist = $true
    
    foreach ($file in $requiredFiles) {
        $filePath = Join-Path -Path $servicePath -ChildPath $file
        $status = if (Test-Path $filePath) { "✅" } else { "❌" }
        
        if ($status -eq "❌") {
            $allFilesExist = $false
            Write-Host "  $status $file" -ForegroundColor Red
            
            # Create missing directories and template files
            if ($file -ne "README.md") {
                $dirPath = Split-Path -Parent $filePath
                if (-not (Test-Path $dirPath)) {
                    New-Item -ItemType Directory -Force -Path $dirPath | Out-Null
                    Write-Host "  📂 Created directory: $dirPath" -ForegroundColor Yellow
                }
                
                # Copy appropriate template
                $templateFile = if ($file -like "*api*" -and $file -ne "api/README.md") {
                    "API_TEMPLATE.md"
                } elseif ($file -like "*setup*" -or $file -like "*operations*" -or $file -like "*architecture*") {
                    "${$file.Split('/')[-2].ToUpper()}_TEMPLATE.md"
                } else {
                    "README_TEMPLATE.md"
                }
                
                $templatePath = Join-Path -Path $PSScriptRoot -ChildPath $templateFile
                Copy-Item -Path $templatePath -Destination $filePath -Force
                Write-Host "  📄 Created template: $file" -ForegroundColor Yellow
            }
        } else {
            Write-Host "  $status $file" -ForegroundColor Green
        }
    }
    
    if ($allFilesExist) {
        Write-Host "  ✓ All documentation files present" -ForegroundColor Green
    }
    
    Write-Host ""
}

Write-Host "=== Documentation Verification Complete ===" -ForegroundColor Cyan
