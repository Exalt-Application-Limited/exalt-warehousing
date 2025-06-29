# Clean up warehousing services for compilation
Write-Host "Cleaning up warehousing services..." -ForegroundColor Cyan

# Fix SubscriptionService interface
$servicePath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription\src\main\java\com\exalt\warehousing\subscription\service\SubscriptionService.java"
$content = Get-Content $servicePath -Raw
$startIndex = $content.IndexOf("// ========== Controller-Compatible Methods ==========")
if ($startIndex -gt 0) {
    $newContent = $content.Substring(0, $startIndex).TrimEnd() + "`n}"
    Set-Content -Path $servicePath -Value $newContent -Encoding UTF8
    Write-Host "Cleaned SubscriptionService interface" -ForegroundColor Green
}

# Fix SubscriptionServiceImpl
$implPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription\src\main\java\com\exalt\warehousing\subscription\service\SubscriptionServiceImpl.java"
$implContent = Get-Content $implPath -Raw
$implStartIndex = $implContent.IndexOf("// ========== Controller-Compatible Method Implementations")
if ($implStartIndex -gt 0) {
    $newImplContent = $implContent.Substring(0, $implStartIndex).TrimEnd() + "`n}"
    Set-Content -Path $implPath -Value $newImplContent -Encoding UTF8
    Write-Host "Cleaned SubscriptionServiceImpl" -ForegroundColor Green
}

Write-Host "Cleanup complete!" -ForegroundColor Green
