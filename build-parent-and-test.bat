@echo off
echo Fixing POM and building warehousing modules...

cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"

echo Installing parent POM...
call mvn clean install -N -DskipTests

echo.
echo Testing fulfillment-service compilation...
cd /d "C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing\fulfillment-service"
call mvn clean compile

echo.
echo Build complete!
pause
