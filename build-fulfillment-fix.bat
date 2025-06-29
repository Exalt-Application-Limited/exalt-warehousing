@echo off
echo Building warehousing-shared module...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\warehousing-shared"
call mvn clean install -DskipTests

echo.
echo Building fulfillment-service...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"
call mvn clean compile

echo.
echo Build complete!
pause
