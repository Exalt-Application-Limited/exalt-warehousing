# Fix UUID/String issues in fulfillment-service
$ProjectPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"

Write-Host "Fixing UUID/String issues in fulfillment-service..." -ForegroundColor Cyan

# Update FulfillmentOrderService interface
$interfacePath = "$ProjectPath\src\main\java\com\exalt\warehousing\fulfillment\service\FulfillmentOrderService.java"
$interfaceContent = Get-Content $interfacePath -Raw

# Replace UUID with String in method signatures
$interfaceContent = $interfaceContent -replace 'FulfillmentOrder getFulfillmentOrder\(UUID', 'FulfillmentOrder getFulfillmentOrder(String'
$interfaceContent = $interfaceContent -replace 'List<FulfillmentOrder> getFulfillmentOrdersByOrderId\(UUID', 'List<FulfillmentOrder> getFulfillmentOrdersByOrderId(String'
$interfaceContent = $interfaceContent -replace 'FulfillmentOrder updateFulfillmentOrderStatus\(UUID', 'FulfillmentOrder updateFulfillmentOrderStatus(String'
$interfaceContent = $interfaceContent -replace 'FulfillmentOrder updateItemStatus\(UUID fulfillmentOrderId, UUID', 'FulfillmentOrder updateItemStatus(String fulfillmentOrderId, String'
$interfaceContent = $interfaceContent -replace 'FulfillmentOrder cancelFulfillmentOrder\(UUID', 'FulfillmentOrder cancelFulfillmentOrder(String'
$interfaceContent = $interfaceContent -replace 'List<FulfillmentOrder> getFulfillmentOrdersByWarehouse\(UUID', 'List<FulfillmentOrder> getFulfillmentOrdersByWarehouse(Long'
$interfaceContent = $interfaceContent -replace 'List<FulfillmentOrder> splitFulfillmentOrder\(UUID', 'List<FulfillmentOrder> splitFulfillmentOrder(String'
$interfaceContent = $interfaceContent -replace 'FulfillmentOrder assignToWarehouse\(UUID fulfillmentOrderId, UUID', 'FulfillmentOrder assignToWarehouse(String fulfillmentOrderId, Long'
$interfaceContent = $interfaceContent -replace 'import java.util.UUID;', ''

Set-Content -Path $interfacePath -Value $interfaceContent -Encoding UTF8
Write-Host "Updated FulfillmentOrderService interface" -ForegroundColor Green

# Update FulfillmentOrderServiceImpl
$implPath = "$ProjectPath\src\main\java\com\exalt\warehousing\fulfillment\service\impl\FulfillmentOrderServiceImpl.java"
$implContent = Get-Content $implPath -Raw

# Replace method signatures
$implContent = $implContent -replace 'public FulfillmentOrder getFulfillmentOrder\(UUID', 'public FulfillmentOrder getFulfillmentOrder(String'
$implContent = $implContent -replace 'public List<FulfillmentOrder> getFulfillmentOrdersByOrderId\(UUID', 'public List<FulfillmentOrder> getFulfillmentOrdersByOrderId(String'
$implContent = $implContent -replace 'public FulfillmentOrder updateFulfillmentOrderStatus\(UUID', 'public FulfillmentOrder updateFulfillmentOrderStatus(String'
$implContent = $implContent -replace 'public FulfillmentOrder updateItemStatus\(UUID fulfillmentOrderId, UUID', 'public FulfillmentOrder updateItemStatus(String fulfillmentOrderId, String'
$implContent = $implContent -replace 'public FulfillmentOrder cancelFulfillmentOrder\(UUID', 'public FulfillmentOrder cancelFulfillmentOrder(String'
$implContent = $implContent -replace 'public List<FulfillmentOrder> getFulfillmentOrdersByWarehouse\(UUID', 'public List<FulfillmentOrder> getFulfillmentOrdersByWarehouse(Long'
$implContent = $implContent -replace 'public List<FulfillmentOrder> splitFulfillmentOrder\(UUID', 'public List<FulfillmentOrder> splitFulfillmentOrder(String'
$implContent = $implContent -replace 'public FulfillmentOrder assignToWarehouse\(UUID fulfillmentOrderId, UUID', 'public FulfillmentOrder assignToWarehouse(String fulfillmentOrderId, Long'

# Remove TypeConverterUtil usage
$implContent = $implContent -replace 'Long entityId = TypeConverterUtil.uuidToLong\(fulfillmentOrderId\);', ''
$implContent = $implContent -replace 'return fulfillmentOrderRepository.findById\(entityId\)', 'return fulfillmentOrderRepository.findById(fulfillmentOrderId)'
$implContent = $implContent -replace 'Long itemEntityId = TypeConverterUtil.uuidToLong\(itemId\);', ''
$implContent = $implContent -replace '\.filter\(i -> i.getId\(\).equals\(itemEntityId\)\)', '.filter(i -> i.getId().equals(itemId))'
$implContent = $implContent -replace 'Long warehouseEntityId = TypeConverterUtil.uuidToLong\(warehouseId\);', ''
$implContent = $implContent -replace 'return fulfillmentOrderRepository.findByWarehouseId\(warehouseEntityId\);', 'return fulfillmentOrderRepository.findByWarehouseId(warehouseId);'
$implContent = $implContent -replace 'order.setWarehouseId\(warehouseEntityId\);', 'order.setWarehouseId(warehouseId);'
$implContent = $implContent -replace 'String orderIdString = orderId.toString\(\);', ''
$implContent = $implContent -replace 'Optional<FulfillmentOrder> optionalOrder = fulfillmentOrderRepository.findByExternalOrderId\(orderIdString\);', 'Optional<FulfillmentOrder> optionalOrder = fulfillmentOrderRepository.findByExternalOrderId(orderId);'

# Remove import for TypeConverterUtil
$implContent = $implContent -replace 'import com.exalt.warehousing.fulfillment.util.TypeConverterUtil;', ''

# Add missing setCancelledAt method call
$implContent = $implContent -replace '(order\.setStatus\(FulfillmentStatus\.CANCELLED\);)', '$1
        order.setCancelledAt(LocalDateTime.now());'

Set-Content -Path $implPath -Value $implContent -Encoding UTF8
Write-Host "Updated FulfillmentOrderServiceImpl" -ForegroundColor Green

# Update FulfillmentController
$controllerPath = "$ProjectPath\src\main\java\com\exalt\warehousing\fulfillment\controller\FulfillmentController.java"
if (Test-Path $controllerPath) {
    $controllerContent = Get-Content $controllerPath -Raw
    
    # Replace UUID parameters with String
    $controllerContent = $controllerContent -replace '@PathVariable UUID', '@PathVariable String'
    $controllerContent = $controllerContent -replace '@RequestParam UUID', '@RequestParam Long'
    $controllerContent = $controllerContent -replace 'import java.util.UUID;', ''
    
    Set-Content -Path $controllerPath -Value $controllerContent -Encoding UTF8
    Write-Host "Updated FulfillmentController" -ForegroundColor Green
}

Write-Host "`nFixes applied successfully!" -ForegroundColor Green
Write-Host "Now testing compilation..." -ForegroundColor Yellow

# Test compilation
cd $ProjectPath
mvn clean compile

Write-Host "`nDone!" -ForegroundColor Green
