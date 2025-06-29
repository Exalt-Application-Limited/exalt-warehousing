#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Fix UUID/String type mismatches in the warehouse-subscription service
.DESCRIPTION
    This script fixes UUID/String type conversion issues in the warehouse-subscription service
    following the same pattern successfully used for other services
#>

param(
    [string]$ProjectPath = "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehouse-subscription"
)

Write-Host "🔧 Starting UUID/String fixes for warehouse-subscription service..." -ForegroundColor Cyan

# Step 1: Update repository interfaces to use String IDs
Write-Host "`n📝 Updating Repository interfaces..." -ForegroundColor Yellow

$repositoryPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\subscription\repository"

# Update SubscriptionRepository
$subscriptionRepoPath = Join-Path $repositoryPath "SubscriptionRepository.java"
if (Test-Path $subscriptionRepoPath) {
    $content = Get-Content $subscriptionRepoPath -Raw
    $content = $content -replace 'extends JpaRepository<WarehouseSubscription, UUID>', 'extends JpaRepository<WarehouseSubscription, String>'
    $content = $content -replace 'Optional<WarehouseSubscription> findByWarehouseId\(UUID', 'Optional<WarehouseSubscription> findByWarehouseId(String'
    $content = $content -replace 'Optional<WarehouseSubscription> findActiveByWarehouseId\(UUID', 'Optional<WarehouseSubscription> findActiveByWarehouseId(String'
    $content = $content -replace 'List<WarehouseSubscription> findByTenantId\(UUID', 'List<WarehouseSubscription> findByTenantId(String'
    $content = $content -replace 'import java.util.UUID;', ''
    Set-Content -Path $subscriptionRepoPath -Value $content -Encoding UTF8
    Write-Host "  ✅ Updated SubscriptionRepository" -ForegroundColor Green
}

# Update BillingRecordRepository
$billingRepoPath = Join-Path $repositoryPath "BillingRecordRepository.java"
if (Test-Path $billingRepoPath) {
    $content = Get-Content $billingRepoPath -Raw
    $content = $content -replace 'extends JpaRepository<BillingRecord, UUID>', 'extends JpaRepository<BillingRecord, String>'
    $content = $content -replace 'import java.util.UUID;', ''
    Set-Content -Path $billingRepoPath -Value $content -Encoding UTF8
    Write-Host "  ✅ Updated BillingRecordRepository" -ForegroundColor Green
}

# Update UsageRecordRepository
$usageRepoPath = Join-Path $repositoryPath "UsageRecordRepository.java"
if (Test-Path $usageRepoPath) {
    $content = Get-Content $usageRepoPath -Raw
    $content = $content -replace 'extends JpaRepository<UsageRecord, UUID>', 'extends JpaRepository<UsageRecord, String>'
    $content = $content -replace 'import java.util.UUID;', ''
    Set-Content -Path $usageRepoPath -Value $content -Encoding UTF8
    Write-Host "  ✅ Updated UsageRecordRepository" -ForegroundColor Green
}

# Step 2: Update service interface to use String IDs consistently
Write-Host "`n📝 Updating SubscriptionService interface..." -ForegroundColor Yellow

$serviceInterfacePath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\subscription\service\SubscriptionService.java"
if (Test-Path $serviceInterfacePath) {
    $content = Get-Content $serviceInterfacePath -Raw
    
    # Replace UUID parameters with String in core methods
    $content = $content -replace 'WarehouseSubscription createSubscription\(UUID warehouseId', 'WarehouseSubscription createSubscription(String warehouseId'
    $content = $content -replace 'WarehouseSubscription startTrial\(UUID warehouseId', 'WarehouseSubscription startTrial(String warehouseId'
    $content = $content -replace 'WarehouseSubscription getSubscription\(UUID', 'WarehouseSubscription getSubscription(String'
    $content = $content -replace 'WarehouseSubscription getSubscriptionByWarehouse\(UUID', 'WarehouseSubscription getSubscriptionByWarehouse(String'
    $content = $content -replace 'WarehouseSubscription getActiveSubscriptionByWarehouse\(UUID', 'WarehouseSubscription getActiveSubscriptionByWarehouse(String'
    $content = $content -replace 'WarehouseSubscription upgradePlan\(UUID', 'WarehouseSubscription upgradePlan(String'
    $content = $content -replace 'WarehouseSubscription changeBillingFrequency\(UUID', 'WarehouseSubscription changeBillingFrequency(String'
    $content = $content -replace 'WarehouseSubscription cancelSubscription\(UUID', 'WarehouseSubscription cancelSubscription(String'
    $content = $content -replace 'WarehouseSubscription reactivateSubscription\(UUID', 'WarehouseSubscription reactivateSubscription(String'
    $content = $content -replace 'WarehouseSubscription pauseSubscription\(UUID', 'WarehouseSubscription pauseSubscription(String'
    $content = $content -replace 'WarehouseSubscription resumeSubscription\(UUID', 'WarehouseSubscription resumeSubscription(String'
    $content = $content -replace 'UsageRecord recordUsage\(UUID', 'UsageRecord recordUsage(String'
    $content = $content -replace 'void updateUsageCounters\(UUID', 'void updateUsageCounters(String'
    $content = $content -replace 'Page<UsageRecord> getUsageRecords\(UUID', 'Page<UsageRecord> getUsageRecords(String'
    $content = $content -replace 'Map<String, Object> getCurrentUsageSummary\(UUID', 'Map<String, Object> getCurrentUsageSummary(String'
    $content = $content -replace 'Map<String, Boolean> checkUsageLimits\(UUID', 'Map<String, Boolean> checkUsageLimits(String'
    $content = $content -replace 'BillingRecord processBilling\(UUID', 'BillingRecord processBilling(String'
    $content = $content -replace 'BillingRecord generateInvoice\(UUID', 'BillingRecord generateInvoice(String'
    $content = $content -replace 'BigDecimal calculateOverageCharges\(UUID', 'BigDecimal calculateOverageCharges(String'
    $content = $content -replace 'Page<BillingRecord> getBillingRecords\(UUID', 'Page<BillingRecord> getBillingRecords(String'
    $content = $content -replace 'void markInvoiceAsPaid\(UUID', 'void markInvoiceAsPaid(String'
    $content = $content -replace 'void markInvoiceAsFailed\(UUID', 'void markInvoiceAsFailed(String'
    $content = $content -replace 'WarehouseSubscription setupPaymentMethod\(UUID', 'WarehouseSubscription setupPaymentMethod(String'
    $content = $content -replace 'boolean processPayment\(UUID', 'boolean processPayment(String'
    $content = $content -replace 'boolean retryFailedPayment\(UUID', 'boolean retryFailedPayment(String'
    $content = $content -replace 'boolean processRefund\(UUID', 'boolean processRefund(String'
    $content = $content -replace 'WarehouseSubscription applyDiscount\(UUID', 'WarehouseSubscription applyDiscount(String'
    $content = $content -replace 'WarehouseSubscription removeDiscount\(UUID', 'WarehouseSubscription removeDiscount(String'
    $content = $content -replace 'Page<WarehouseSubscription> getSubscriptionsForTenant\(UUID', 'Page<WarehouseSubscription> getSubscriptionsForTenant(String'
    $content = $content -replace 'Map<String, Object> getTenantUsageSummary\(UUID', 'Map<String, Object> getTenantUsageSummary(String'
    $content = $content -replace 'List<WarehouseSubscription> splitFulfillmentOrder\(UUID', 'List<WarehouseSubscription> splitFulfillmentOrder(String'
    
    # Remove UUID import if present
    $content = $content -replace 'import java.util.UUID;', ''
    
    Set-Content -Path $serviceInterfacePath -Value $content -Encoding UTF8
    Write-Host "✅ Updated SubscriptionService interface" -ForegroundColor Green
}

# Step 3: Fix SubscriptionServiceImpl
Write-Host "`n📝 Fixing SubscriptionServiceImpl implementation..." -ForegroundColor Yellow

$serviceImplPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\subscription\service\SubscriptionServiceImpl.java"
if (Test-Path $serviceImplPath) {
    $content = Get-Content $serviceImplPath -Raw
    
    # Update method signatures to use String
    $content = $content -replace 'public WarehouseSubscription createSubscription\(UUID warehouseId', 'public WarehouseSubscription createSubscription(String warehouseId'
    $content = $content -replace 'public WarehouseSubscription startTrial\(UUID warehouseId', 'public WarehouseSubscription startTrial(String warehouseId'
    $content = $content -replace 'public WarehouseSubscription getSubscription\(UUID', 'public WarehouseSubscription getSubscription(String'
    $content = $content -replace 'public WarehouseSubscription getSubscriptionByWarehouse\(UUID', 'public WarehouseSubscription getSubscriptionByWarehouse(String'
    $content = $content -replace 'public WarehouseSubscription getActiveSubscriptionByWarehouse\(UUID', 'public WarehouseSubscription getActiveSubscriptionByWarehouse(String'
    $content = $content -replace 'public WarehouseSubscription upgradePlan\(UUID', 'public WarehouseSubscription upgradePlan(String'
    $content = $content -replace 'public WarehouseSubscription changeBillingFrequency\(UUID', 'public WarehouseSubscription changeBillingFrequency(String'
    $content = $content -replace 'public WarehouseSubscription cancelSubscription\(UUID', 'public WarehouseSubscription cancelSubscription(String'
    $content = $content -replace 'public WarehouseSubscription reactivateSubscription\(UUID', 'public WarehouseSubscription reactivateSubscription(String'
    $content = $content -replace 'public WarehouseSubscription pauseSubscription\(UUID', 'public WarehouseSubscription pauseSubscription(String'
    $content = $content -replace 'public WarehouseSubscription resumeSubscription\(UUID', 'public WarehouseSubscription resumeSubscription(String'
    $content = $content -replace 'public UsageRecord recordUsage\(UUID', 'public UsageRecord recordUsage(String'
    $content = $content -replace 'public void updateUsageCounters\(UUID', 'public void updateUsageCounters(String'
    $content = $content -replace 'public Page<UsageRecord> getUsageRecords\(UUID', 'public Page<UsageRecord> getUsageRecords(String'
    $content = $content -replace 'public Map<String, Object> getCurrentUsageSummary\(UUID', 'public Map<String, Object> getCurrentUsageSummary(String'
    $content = $content -replace 'public Map<String, Boolean> checkUsageLimits\(UUID', 'public Map<String, Boolean> checkUsageLimits(String'
    $content = $content -replace 'public BillingRecord processBilling\(UUID', 'public BillingRecord processBilling(String'
    $content = $content -replace 'public BillingRecord generateInvoice\(UUID', 'public BillingRecord generateInvoice(String'
    $content = $content -replace 'public BigDecimal calculateOverageCharges\(UUID', 'public BigDecimal calculateOverageCharges(String'
    $content = $content -replace 'public Page<BillingRecord> getBillingRecords\(UUID', 'public Page<BillingRecord> getBillingRecords(String'
    $content = $content -replace 'public void markInvoiceAsPaid\(UUID', 'public void markInvoiceAsPaid(String'
    $content = $content -replace 'public void markInvoiceAsFailed\(UUID', 'public void markInvoiceAsFailed(String'
    $content = $content -replace 'public WarehouseSubscription setupPaymentMethod\(UUID', 'public WarehouseSubscription setupPaymentMethod(String'
    $content = $content -replace 'public boolean processPayment\(UUID', 'public boolean processPayment(String'
    $content = $content -replace 'public boolean retryFailedPayment\(UUID', 'public boolean retryFailedPayment(String'
    $content = $content -replace 'public boolean processRefund\(UUID', 'public boolean processRefund(String'
    $content = $content -replace 'public WarehouseSubscription applyDiscount\(UUID', 'public WarehouseSubscription applyDiscount(String'
    $content = $content -replace 'public WarehouseSubscription removeDiscount\(UUID', 'public WarehouseSubscription removeDiscount(String'
    $content = $content -replace 'public Page<WarehouseSubscription> getSubscriptionsForTenant\(UUID', 'public Page<WarehouseSubscription> getSubscriptionsForTenant(String'
    $content = $content -replace 'public Map<String, Object> getTenantUsageSummary\(UUID', 'public Map<String, Object> getTenantUsageSummary(String'
    
    # Remove UUID.fromString conversions
    $content = $content -replace 'UUID\.fromString\(subscription\.getId\(\)\)', 'subscription.getId()'
    $content = $content -replace 'UUID\.fromString\(billingRecord\.getId\(\)\)', 'billingRecord.getId()'
    $content = $content -replace 'UUID subscriptionUuid = UUID\.fromString\(subscriptionId\);', ''
    $content = $content -replace 'UUID warehouseUuid = UUID\.fromString\(warehouseId\);', ''
    $content = $content -replace 'getSubscription\(subscriptionUuid\)', 'getSubscription(subscriptionId)'
    $content = $content -replace 'findByWarehouseId\(warehouseUuid\)', 'findByWarehouseId(warehouseId)'
    $content = $content -replace 'processPayment\(subscriptionUuid\)', 'processPayment(subscriptionId)'
    $content = $content -replace 'processBilling\(subscriptionUuid\)', 'processBilling(subscriptionId)'
    $content = $content -replace 'upgradePlan\(subscriptionUuid,', 'upgradePlan(subscriptionId,'
    $content = $content -replace 'cancelSubscription\(subscriptionUuid,', 'cancelSubscription(subscriptionId,'
    $content = $content -replace 'getUsageRecords\(subscriptionUuid,', 'getUsageRecords(subscriptionId,'
    $content = $content -replace 'getCurrentUsageSummary\(subscriptionUuid\)', 'getCurrentUsageSummary(subscriptionId)'
    $content = $content -replace 'recordUsage\(subscriptionUuid,', 'recordUsage(subscriptionId,'
    
    # Fix the subscription.getWarehouseId().toString() calls
    $content = $content -replace 'subscription\.getWarehouseId\(\)\.toString\(\)', 'subscription.getWarehouseId()'
    
    # Remove remaining UUID imports/references
    $content = $content -replace 'import java.util.UUID;', ''
    
    Set-Content -Path $serviceImplPath -Value $content -Encoding UTF8
    Write-Host "✅ Fixed SubscriptionServiceImpl" -ForegroundColor Green
}

# Step 4: Update SubscriptionController to use String IDs
Write-Host "`n📝 Updating SubscriptionController..." -ForegroundColor Yellow

$controllerPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\subscription\controller\SubscriptionController.java"
if (Test-Path $controllerPath) {
    $content = Get-Content $controllerPath -Raw
    
    # Replace UUID parameters with String
    $content = $content -replace '@PathVariable UUID', '@PathVariable String'
    $content = $content -replace '@PathVariable\("subscriptionId"\) UUID', '@PathVariable("subscriptionId") String'
    $content = $content -replace '@PathVariable\("warehouseId"\) UUID', '@PathVariable("warehouseId") String'
    $content = $content -replace '@RequestParam UUID', '@RequestParam String'
    $content = $content -replace 'import java.util.UUID;', ''
    
    Set-Content -Path $controllerPath -Value $content -Encoding UTF8
    Write-Host "✅ Updated SubscriptionController" -ForegroundColor Green
}

# Step 5: Update model classes if needed
Write-Host "`n📝 Checking model classes..." -ForegroundColor Yellow

$modelPath = Join-Path $ProjectPath "src\main\java\com\exalt\warehousing\subscription\model"
$warehouseSubPath = Join-Path $modelPath "WarehouseSubscription.java"

if (Test-Path $warehouseSubPath) {
    $content = Get-Content $warehouseSubPath -Raw
    
    # Check if warehouseId is UUID and change to String
    if ($content -match 'private UUID warehouseId') {
        $content = $content -replace 'private UUID warehouseId', 'private String warehouseId'
        $content = $content -replace 'public UUID getWarehouseId', 'public String getWarehouseId'
        $content = $content -replace 'public void setWarehouseId\(UUID', 'public void setWarehouseId(String'
        $content = $content -replace 'UUID warehouseId', 'String warehouseId'
        Set-Content -Path $warehouseSubPath -Value $content -Encoding UTF8
        Write-Host "  ✅ Updated WarehouseSubscription model" -ForegroundColor Green
    }
}

Write-Host "`n✅ UUID/String fixes completed for warehouse-subscription service!" -ForegroundColor Green
Write-Host "📋 Summary of changes:" -ForegroundColor Cyan
Write-Host "  - Updated repository interfaces to use String IDs"
Write-Host "  - Fixed SubscriptionService interface to use String parameters"
Write-Host "  - Updated SubscriptionServiceImpl to remove UUID conversions"
Write-Host "  - Fixed SubscriptionController to accept String parameters"
Write-Host "  - Removed unnecessary UUID imports and conversions"

Write-Host "`n📌 Next steps:" -ForegroundColor Yellow
Write-Host "  1. Build the parent POM and all dependencies"
Write-Host "  2. Set up the Central Configuration services"
Write-Host "  3. Fix the 2 POM configuration errors in shared-infrastructure"
