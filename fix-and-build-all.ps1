# Fix parent POM and build everything
$parentPomPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\pom.xml"
$content = Get-Content $parentPomPath -Raw
$content = $content -replace '<n>Warehousing Services</n>', '<name>Warehousing Services</name>'
Set-Content -Path $parentPomPath -Value $content -Encoding UTF8
Write-Host "Fixed parent POM" -ForegroundColor Green

# Build parent POM first
Write-Host "Building parent POM..." -ForegroundColor Yellow
cd "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"
mvn clean install -N -DskipTests

# Build warehousing-shared
Write-Host "`nBuilding warehousing-shared..." -ForegroundColor Yellow
cd "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehousing-shared"
mvn clean install -DskipTests

# Build fulfillment-service
Write-Host "`nBuilding fulfillment-service..." -ForegroundColor Yellow
cd "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"
mvn clean compile

Write-Host "`nBuild complete!" -ForegroundColor Green
