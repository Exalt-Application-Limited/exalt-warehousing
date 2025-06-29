@echo off
echo Fixing enum constructors...

REM Fix ZoneType enum
echo Fixing ZoneType.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\ZoneType.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\ZoneType.java'"

REM Fix TaskPriority enum  
echo Fixing TaskPriority.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\TaskPriority.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\TaskPriority.java'"

REM Fix TaskType enum
echo Fixing TaskType.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\TaskType.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\TaskType.java'"

REM Fix TaskStatus enum
echo Fixing TaskStatus.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\TaskStatus.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\TaskStatus.java'"

REM Fix AssignmentType enum
echo Fixing AssignmentType.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\AssignmentType.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\AssignmentType.java'"

REM Fix AssignmentStatus enum
echo Fixing AssignmentStatus.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\AssignmentStatus.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\AssignmentStatus.java'"

REM Fix MaintenanceFrequency enum
echo Fixing MaintenanceFrequency.java...
powershell -Command "(Get-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\MaintenanceFrequency.java') -replace 'import lombok.RequiredArgsConstructor;', '' -replace '@RequiredArgsConstructor', '' | Set-Content 'src\main\java\com\microcommerce\warehousing\operations\enums\MaintenanceFrequency.java'"

echo Done fixing enums!
