#!/usr/bin/env pwsh
# Fix the incorrect DTO package references in SubscriptionService

$serviceFile = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription\src\main\java\com\exalt\warehousing\subscription\service\SubscriptionService.java"
$implFile = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription\src\main\java\com\exalt\warehousing\subscription\service\SubscriptionServiceImpl.java"

Write-Host "Fixing incorrect package references..." -ForegroundColor Cyan

# Fix SubscriptionService interface
if (Test-Path $serviceFile) {
    $content = Get-Content $serviceFile -Raw
    $content = $content -replace 'com\.ecosystem\.warehousing\.subscription\.dto', 'com.exalt.warehousing.subscription.dto'
    Set-Content -Path $serviceFile -Value $content -Encoding UTF8
    Write-Host "✅ Fixed SubscriptionService interface" -ForegroundColor Green
}

# Fix SubscriptionServiceImpl
if (Test-Path $implFile) {
    $content = Get-Content $implFile -Raw
    $content = $content -replace 'com\.ecosystem\.warehousing\.subscription\.dto', 'com.exalt.warehousing.subscription.dto'
    Set-Content -Path $implFile -Value $content -Encoding UTF8
    Write-Host "✅ Fixed SubscriptionServiceImpl" -ForegroundColor Green
}

Write-Host "`nPackage references fixed!" -ForegroundColor Green
