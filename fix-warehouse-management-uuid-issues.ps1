#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Fix UUID/String type mismatches in the warehouse-management-service
.DESCRIPTION
    This script fixes UUID/String type conversion issues in the warehouse-management-service
    following the same pattern successfully used for inventory and fulfillment services
#>

param(
    [string]$ProjectPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-management-service"
)

Write-Host "🔧 Starting UUID/String fixes for warehouse-management-service..." -ForegroundColor Cyan

# Step 1: Update WarehouseServiceImpl to match the interface (String IDs)
Write-Host "`n📝 Updating WarehouseServiceImpl..." -ForegroundColor Yellow

$serviceImplPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\management\service\impl\WarehouseServiceImpl.java"
$serviceImplContent = Get-Content $serviceImplPath -Raw

# Replace UUID with String in method signatures
$serviceImplContent = $serviceImplContent -replace 'extends BaseEntityService<Warehouse, UUID>', 'extends BaseEntityService<Warehouse, String>'
$serviceImplContent = $serviceImplContent -replace 'JpaRepository<Warehouse, UUID>', 'JpaRepository<Warehouse, String>'
$serviceImplContent = $serviceImplContent -replace 'public Optional<Warehouse> findById\(UUID', 'public Optional<Warehouse> findById(String'
$serviceImplContent = $serviceImplContent -replace 'public WarehouseDTO getWarehouseDTOById\(UUID', 'public WarehouseDTO getWarehouseDTOById(String'
$serviceImplContent = $serviceImplContent -replace 'public WarehouseDTO update\(UUID', 'public WarehouseDTO update(String'
$serviceImplContent = $serviceImplContent -replace 'public Warehouse updateWarehouse\(UUID', 'public Warehouse updateWarehouse(String'
$serviceImplContent = $serviceImplContent -replace 'public WarehouseDTO changeStatus\(UUID', 'public WarehouseDTO changeStatus(String'
$serviceImplContent = $serviceImplContent -replace 'public Warehouse changeWarehouseStatus\(UUID', 'public Warehouse changeWarehouseStatus(String'
$serviceImplContent = $serviceImplContent -replace 'public WarehouseDTO setActive\(UUID', 'public WarehouseDTO setActive(String'
$serviceImplContent = $serviceImplContent -replace 'private Warehouse getWarehouseById\(UUID', 'private Warehouse getWarehouseById(String'
$serviceImplContent = $serviceImplContent -replace 'public Optional<Warehouse> getWarehouse\(UUID', 'public Optional<Warehouse> getWarehouse(String'
$serviceImplContent = $serviceImplContent -replace 'public void deleteWarehouse\(UUID', 'public void deleteWarehouse(String'

# Remove UUID import if it's not needed
$serviceImplContent = $serviceImplContent -replace 'import java.util.UUID;', ''

Set-Content -Path $serviceImplPath -Value $serviceImplContent -Encoding UTF8
Write-Host "✅ Updated WarehouseServiceImpl" -ForegroundColor Green

# Step 2: Update WarehouseRepository to use String IDs
Write-Host "`n📝 Updating WarehouseRepository..." -ForegroundColor Yellow

$repositoryPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\management\repository\WarehouseRepository.java"
if (Test-Path $repositoryPath) {
    $repositoryContent = Get-Content $repositoryPath -Raw
    $repositoryContent = $repositoryContent -replace 'JpaRepository<Warehouse, UUID>', 'JpaRepository<Warehouse, String>'
    $repositoryContent = $repositoryContent -replace 'import java.util.UUID;', ''
    Set-Content -Path $repositoryPath -Value $repositoryContent -Encoding UTF8
    Write-Host "✅ Updated WarehouseRepository" -ForegroundColor Green
}

# Step 3: Update WarehouseController to use String IDs
Write-Host "`n📝 Updating WarehouseController..." -ForegroundColor Yellow

$controllerPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\management\controller\WarehouseController.java"
if (Test-Path $controllerPath) {
    $controllerContent = Get-Content $controllerPath -Raw
    
    # Replace UUID parameters with String
    $controllerContent = $controllerContent -replace '@PathVariable UUID', '@PathVariable String'
    $controllerContent = $controllerContent -replace '@PathVariable\("id"\) UUID', '@PathVariable("id") String'
    $controllerContent = $controllerContent -replace 'import java.util.UUID;', ''
    
    Set-Content -Path $controllerPath -Value $controllerContent -Encoding UTF8
    Write-Host "✅ Updated WarehouseController" -ForegroundColor Green
}

# Step 4: Update other service implementations that might be using UUID
Write-Host "`n📝 Checking other service implementations..." -ForegroundColor Yellow

$serviceImplDir = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\management\service\impl"
$serviceFiles = Get-ChildItem -Path $serviceImplDir -Filter "*.java" -File

foreach ($file in $serviceFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # Check if file uses UUID and needs updating
    if ($content -match 'UUID' -and $file.Name -ne "WarehouseServiceImpl.java") {
        Write-Host "  📄 Updating $($file.Name)..." -ForegroundColor Gray
        
        # Replace common UUID patterns with String
        $content = $content -replace 'warehouseId: UUID', 'warehouseId: String'
        $content = $content -replace 'zoneId: UUID', 'zoneId: String'
        $content = $content -replace 'locationId: UUID', 'locationId: String'
        $content = $content -replace 'taskId: UUID', 'taskId: String'
        $content = $content -replace 'staffId: UUID', 'staffId: String'
        $content = $content -replace 'UUID\.fromString\(([^)]+)\)', '$1'
        $content = $content -replace '\.toString\(\)', ''
        
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
    }
}

Write-Host "✅ Updated service implementations" -ForegroundColor Green

# Step 5: Test the compilation
Write-Host "`n🔨 Testing compilation..." -ForegroundColor Yellow

Push-Location $ProjectPath
try {
    # First try to build with Maven
    $result = mvn clean compile 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Compilation had issues. This might be due to missing parent dependencies." -ForegroundColor Yellow
        Write-Host "  Run the parent build first, then try again." -ForegroundColor Gray
    }
} finally {
    Pop-Location
}

Write-Host "`n✅ UUID/String fixes completed for warehouse-management-service!" -ForegroundColor Green
Write-Host "📋 Summary of changes:" -ForegroundColor Cyan
Write-Host "  - Updated WarehouseServiceImpl to use String IDs"
Write-Host "  - Updated WarehouseRepository to use String as ID type"
Write-Host "  - Updated WarehouseController to accept String parameters"
Write-Host "  - Removed unnecessary UUID imports and conversions"

Write-Host "`n📌 Next steps:" -ForegroundColor Yellow
Write-Host "  1. Build the parent POM if not already done"
Write-Host "  2. Apply similar fixes to warehouse-subscription"
Write-Host "  3. Set up the Central Configuration services"
