#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Clean up and fix the warehousing services to ensure they compile and test properly
.DESCRIPTION
    This script removes problematic controller-compatible methods and fixes remaining issues
#>

Write-Host "🔧 Cleaning up warehousing services for compilation..." -ForegroundColor Cyan

# Step 1: Fix SubscriptionService interface
Write-Host "`n📝 Cleaning up SubscriptionService interface..." -ForegroundColor Yellow

$subscriptionServicePath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription\src\main\java\com\exalt\warehousing\subscription\service\SubscriptionService.java"

# Read the file and remove the problematic section
$content = Get-Content $subscriptionServicePath -Raw

# Find the start and end of the controller-compatible methods section
$startMarker = "// ========== Controller-Compatible Methods =========="
$endMarker = "}"

# Find the position of the start marker
$startIndex = $content.IndexOf($startMarker)

if ($startIndex -gt 0) {
    # Keep everything before the controller-compatible methods
    $newContent = $content.Substring(0, $startIndex).TrimEnd()
    
    # Add the closing brace for the interface
    $newContent += "`n}"
    
    # Write the cleaned content
    Set-Content -Path $subscriptionServicePath -Value $newContent -Encoding UTF8
    Write-Host "✅ Cleaned up SubscriptionService interface" -ForegroundColor Green
}

# Step 2: Clean up SubscriptionServiceImpl
Write-Host "`n📝 Cleaning up SubscriptionServiceImpl..." -ForegroundColor Yellow

$subscriptionServiceImplPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription\src\main\java\com\exalt\warehousing\subscription\service\SubscriptionServiceImpl.java"

# Read the file
$implContent = Get-Content $subscriptionServiceImplPath -Raw

# Find and remove the controller-compatible implementations section
$implStartMarker = "// ========== Controller-Compatible Method Implementations - WITH FIXES =========="
$implStartIndex = $implContent.IndexOf($implStartMarker)

if ($implStartIndex -gt 0) {
    # Keep everything before the controller-compatible methods
    $newImplContent = $implContent.Substring(0, $implStartIndex).TrimEnd()
    
    # Add the closing brace for the class
    $newImplContent += "`n}"
    
    # Write the cleaned content
    Set-Content -Path $subscriptionServiceImplPath -Value $newImplContent -Encoding UTF8
    Write-Host "✅ Cleaned up SubscriptionServiceImpl" -ForegroundColor Green
}

# Step 3: Build all warehousing services
Write-Host "`n🔨 Building warehousing parent..." -ForegroundColor Yellow
Push-Location "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"
mvn clean install -N -DskipTests
Pop-Location

Write-Host "`n🔨 Building warehousing-shared..." -ForegroundColor Yellow
Push-Location "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehousing-shared"
mvn clean install -DskipTests
Pop-Location

Write-Host "`n🔨 Building all warehousing modules..." -ForegroundColor Yellow
Push-Location "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"
mvn clean install -DskipTests
Pop-Location

Write-Host "`n✅ Warehousing services cleanup complete!" -ForegroundColor Green
