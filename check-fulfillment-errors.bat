@echo off
echo Checking fulfillment-service compilation errors...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"
call mvn compile >compile-output.txt 2>&1
echo.
echo Compilation errors found:
echo ========================
type compile-output.txt | findstr /i "error"
echo.
echo Full output saved to compile-output.txt
