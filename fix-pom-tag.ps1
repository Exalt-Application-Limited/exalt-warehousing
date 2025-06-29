# Fix the parent POM XML tag issue
$pomPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\pom.xml"
$content = Get-Content $pomPath -Raw

# Replace the malformed <n> tag with proper <name> tag
$content = $content -replace '<n>Warehousing Services</n>', '<n>Warehousing Services</n>'

# Save the fixed content
Set-Content -Path $pomPath -Value $content -Encoding UTF8
Write-Host "Fixed parent POM" -ForegroundColor Green
