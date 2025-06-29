@echo off
echo Fixing javax to jakarta imports in warehousing domain...

REM Fix javax.persistence imports
powershell -Command "(Get-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\logistics\model\TransferItem.java') -replace 'import javax.persistence', 'import jakarta.persistence' | Set-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\logistics\model\TransferItem.java'"

powershell -Command "(Get-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\fulfillment\model\ShipmentPackage.java') -replace 'import javax.persistence', 'import jakarta.persistence' | Set-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\fulfillment\model\ShipmentPackage.java'"

powershell -Command "(Get-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\management\model\Staff.java') -replace 'import javax.persistence', 'import jakarta.persistence' | Set-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\management\model\Staff.java'"

REM Fix javax.validation imports
powershell -Command "(Get-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\management\dto\StaffDTO.java') -replace 'import javax.validation', 'import jakarta.validation' | Set-Content 'C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\social-ecommerce-ecosystem\warehousing\src\main\java\com\ecosystem\warehousing\management\dto\StaffDTO.java'"

echo Import fixes applied successfully!
pause