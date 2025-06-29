# Documentation Verification and Generation Script
# This script verifies and generates documentation for all services

# Configuration
$baseDir = Split-Path -Parent $PSScriptRoot
$templateDir = $PSScriptRoot  # Templates are in the same directory as the script
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

# Required documentation files and their templates
$docsStructure = @{
    "README.md" = "README_TEMPLATE.md"
    "docs/README.md" = "README_TEMPLATE.md"
    "docs/setup/README.md" = "SETUP_TEMPLATE.md"
    "docs/operations/README.md" = "OPERATIONS_TEMPLATE.md"
    "docs/api/README.md" = "API_TEMPLATE.md"
    "docs/architecture/README.md" = "ARCHITECTURE_TEMPLATE.md"
}

function Write-Status {
    param(
        [string]$Message,
        [string]$Status = "INFO"
    )
    
    $colors = @{
        "SUCCESS" = "Green"
        "WARNING" = "Yellow"
        "ERROR" = "Red"
        "INFO" = "Cyan"
    }
    
    $color = if ($colors.ContainsKey($Status)) { $colors[$Status] } else { "White" }
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] [$Status] $Message" -ForegroundColor $color
}

function Copy-Template {
    param(
        [string]$Source,
        [string]$Destination
    )
    
    try {
        $dir = Split-Path -Parent $Destination
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
            Write-Status "Created directory: $dir" "SUCCESS"
        }
        
        Copy-Item -Path $Source -Destination $Destination -Force
        Write-Status "Created documentation: $Destination" "SUCCESS"
        return $true
    }
    catch {
        Write-Status "Failed to create $Destination : $_" "ERROR"
        return $false
    }
}

# Main script
Write-Host "=== Documentation Verification and Generation ===" -ForegroundColor Cyan
Write-Host "Base Directory: $baseDir"
Write-Host "Template Directory: $templateDir"
Write-Host ""

$results = @()

foreach ($service in $services) {
    $servicePath = Join-Path $baseDir $service
    
    if (-not (Test-Path $servicePath)) {
        Write-Status "Service directory not found: $service" "WARNING"
        $results += [PSCustomObject]@{
            Service = $service
            Status = "Not Found"
            MissingFiles = @()
            CreatedFiles = @()
        }
        continue
    }
    
    Write-Host "`n=== Verifying $service ===" -ForegroundColor Green
    
    $missingFiles = @()
    $createdFiles = @()
    
    foreach ($docFile in $docsStructure.Keys) {
        $fullPath = Join-Path $servicePath $docFile
        $templateFile = $docsStructure[$docFile]
        $templatePath = Join-Path $templateDir $templateFile
        
        if (-not (Test-Path $fullPath)) {
            Write-Status "Missing: $docFile" "WARNING"
            $missingFiles += $docFile
            
            # Create the documentation from template
            if (Test-Path $templatePath) {
                if (Copy-Template -Source $templatePath -Destination $fullPath) {
                    $createdFiles += $docFile
                    
                    # Replace placeholders in the new file
                    try {
                        $content = Get-Content $fullPath -Raw
                        $content = $content -replace '\[Service Name\]', $service
                        $content | Set-Content $fullPath -NoNewline
                        Write-Status "Updated placeholders in: $docFile" "SUCCESS"
                    }
                    catch {
                        Write-Status "Failed to update placeholders in $docFile : $_" "ERROR"
                    }
                }
            }
            else {
                Write-Status "Template not found: $templateFile" "ERROR"
            }
        }
        else {
            Write-Status "Found: $docFile" "SUCCESS"
        }
    }
    
    # Check for additional documentation files
    $docsDir = Join-Path $servicePath "docs"
    if (Test-Path $docsDir) {
        $allDocs = Get-ChildItem -Path $docsDir -Recurse -File | 
                  Where-Object { $_.Extension -match '\.(md|markdown|txt|html)' } |
                  Select-Object -ExpandProperty FullName |
                  ForEach-Object { $_.Substring($servicePath.Length + 1) }
        
        $additionalDocs = $allDocs | Where-Object { $docsStructure.Keys -notcontains $_ }
        
        if ($additionalDocs) {
            Write-Host "`nAdditional documentation found:" -ForegroundColor Cyan
            $additionalDocs | ForEach-Object { Write-Host "  - $_" }
        }
    }
    
    $status = if ($missingFiles.Count -eq 0) { "Complete" } else { "Incomplete" }
    $results += [PSCustomObject]@{
        Service = $service
        Status = $status
        MissingFiles = $missingFiles
        CreatedFiles = $createdFiles
    }
}

# Generate report
$reportPath = Join-Path $baseDir "documentation_report_$(Get-Date -Format 'yyyyMMdd_HHmmss').md"
$report = @"
# Documentation Status Report

Generated: $(Get-Date)

## Summary

| Service | Status | Missing Files | Created Files |
|---------|--------|---------------|---------------|
"@

foreach ($result in $results) {
    $missing = if ($result.MissingFiles.Count -gt 0) {
        "- " + ($result.MissingFiles -join "`n- ")
    } else {
        "None"
    }
    
    $created = if ($result.CreatedFiles.Count -gt 0) {
        "- " + ($result.CreatedFiles -join "`n- ")
    } else {
        "None"
    }
    
    $report += "| $($result.Service) | $($result.Status) | $($missing) | $($created) |`n"
}

$report | Out-File -FilePath $reportPath -Encoding utf8
Write-Host "`n=== Documentation Report ===" -ForegroundColor Cyan
Write-Host "Report generated at: $reportPath"

# Show summary
$complete = ($results | Where-Object { $_.Status -eq "Complete" }).Count
$incomplete = ($results | Where-Object { $_.Status -ne "Complete" }).Count

Write-Host "`n=== Summary ===" -ForegroundColor Cyan
Write-Host "Total services checked: $($results.Count)"
Write-Host "Complete: $complete" -ForegroundColor Green
Write-Host "Incomplete: $incomplete" -ForegroundColor $(if ($incomplete -gt 0) { "Red" } else { "Green" })

if ($incomplete -gt 0) {
    Write-Host "`nRun this script again to generate missing documentation." -ForegroundColor Yellow
}

# Open the report
Invoke-Item $reportPath
