@echo off
echo ============================================
echo WAREHOUSING COMPILATION FIXES
echo ============================================

REM Set Java environment
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo ============================================
echo STEP 1: Fix Missing Lombok Annotations
echo ============================================

echo Fixing GlobalExceptionHandler...
powershell -Command "(Get-Content 'src\main\java\com\ecosystem\warehousing\management\exception\GlobalExceptionHandler.java') -replace '^package', '@Slf4j`npackage' | Set-Content 'src\main\java\com\ecosystem\warehousing\management\exception\GlobalExceptionHandler.java'"

echo Fixing TransferController...
powershell -Command "(Get-Content 'src\main\java\com\ecosystem\warehousing\logistics\controller\TransferController.java') -replace '^package', '@Slf4j`npackage' | Set-Content 'src\main\java\com\ecosystem\warehousing\logistics\controller\TransferController.java'"

echo Fixing InventoryItemController...
powershell -Command "(Get-Content 'src\main\java\com\ecosystem\warehousing\inventory\controller\InventoryItemController.java') -replace '^package', '@Slf4j`npackage' | Set-Content 'src\main\java\com\ecosystem\warehousing\inventory\controller\InventoryItemController.java'"

echo ============================================
echo STEP 2: Fix Import Conflicts
echo ============================================

echo Fixing EventMapper import conflicts...
powershell -Command "(Get-Content 'src\main\java\com\ecosystem\warehousing\inventory\event\EventMapper.java') -replace 'import com.ecosystem.shared.events.inventory.InventoryReservationCreatedEvent.ReservationItem;', '// Removed conflicting import' | Set-Content 'src\main\java\com\ecosystem\warehousing\inventory\event\EventMapper.java'"

echo ============================================
echo STEP 3: Fix Method Duplications
echo ============================================

echo Fixing FulfillmentOrder duplicate methods...
powershell -Command "(Get-Content 'src\main\java\com\ecosystem\warehousing\fulfillment\model\FulfillmentOrder.java') -replace 'public void setCancellationReason\(String cancellationReason\) \{[^}]*\}', '' | Set-Content 'src\main\java\com\ecosystem\warehousing\fulfillment\model\FulfillmentOrder.java'"

echo ============================================
echo STEP 4: Add Missing Lombok Import
echo ============================================

echo Adding Lombok imports where needed...
powershell -Command "(Get-Content 'src\main\java\com\ecosystem\warehousing\management\exception\GlobalExceptionHandler.java') -replace '^import', 'import lombok.extern.slf4j.Slf4j;`nimport' | Set-Content 'src\main\java\com\ecosystem\warehousing\management\exception\GlobalExceptionHandler.java'"

echo ============================================
echo STEP 5: Test Compilation
echo ============================================

echo Testing compilation after fixes...
call mvnw.cmd compile -DskipTests -q

if %ERRORLEVEL% equ 0 (
    echo SUCCESS: Compilation completed after fixes!
) else (
    echo INFO: Additional fixes may be needed - check error details
)

echo ============================================
echo Compilation fix process completed!
echo ============================================
