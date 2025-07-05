# Script to migrate com.exalt package to com.gogidix for warehousing-shared module

$oldPackage = "com.exalt"
$newPackage = "com.gogidix"
$modulePath = "C:\Users\frich\Desktop\Exalt-Application-Limited\CLEAN-SOCIAL-ECOMMERCE-ECOSYSTEM\warehousing\warehousing-shared"

Write-Host "Starting package migration from $oldPackage to $newPackage for warehousing-shared"

# Step 1: Create directory structure
$oldStructure = Join-Path -Path $modulePath -ChildPath "src\main\java\com\exalt\warehousing\shared"
$newStructure = Join-Path -Path $modulePath -ChildPath "src\main\java\com\gogidix\warehousing\shared"

Write-Host "Creating new package structure: $newStructure"
New-Item -Path $newStructure -ItemType Directory -Force | Out-Null

# Step 2: Copy subdirectories
$subDirs = Get-ChildItem -Path $oldStructure -Directory
foreach ($dir in $subDirs) {
    $targetDir = Join-Path -Path $newStructure -ChildPath $dir.Name
    Write-Host "Creating directory: $targetDir"
    New-Item -Path $targetDir -ItemType Directory -Force | Out-Null
}

# Step 3: Copy and update Java files
Write-Host "\nCopying and updating Java files..."
$javaFiles = Get-ChildItem -Path $oldStructure -Filter "*.java" -Recurse

foreach ($file in $javaFiles) {
    # Create relative path from old structure
    $relativePath = $file.FullName.Substring($oldStructure.Length)
    
    # Build target path
    $targetFile = Join-Path -Path $newStructure -ChildPath $relativePath
    $targetDir = Split-Path -Path $targetFile -Parent
    
    # Ensure target directory exists
    if (!(Test-Path $targetDir)) {
        Write-Host "Creating directory: $targetDir"
        New-Item -Path $targetDir -ItemType Directory -Force | Out-Null
    }
    
    # Read, update, and write file
    $content = Get-Content -Path $file.FullName -Raw
    
    # Replace package declarations
    $updatedContent = $content -replace "package\s+$($oldPackage.Replace(".", "\."))", "package $newPackage"
    
    # Replace import statements
    $updatedContent = $updatedContent -replace "import\s+$($oldPackage.Replace(".", "\."))", "import $newPackage"
    
    Write-Host "Writing updated file: $targetFile"
    Set-Content -Path $targetFile -Value $updatedContent -Force
}

# Step 4: Update all other Java files that might import from com.exalt
# Find all Java files in the module
Write-Host "\nUpdating import statements in all other Java files..."
$allJavaFiles = Get-ChildItem -Path $modulePath -Filter "*.java" -Recurse |
                Where-Object { $_.FullName -notlike "*\com\gogidix\*" }

foreach ($file in $allJavaFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $updatedContent = $content -replace "import\s+$($oldPackage.Replace(".", "\."))", "import $newPackage"
    
    if ($content -ne $updatedContent) {
        Write-Host "Updating imports in: $($file.FullName)"
        Set-Content -Path $file.FullName -Value $updatedContent -Force
    }
}

Write-Host "\nPackage migration completed for warehousing-shared module!"
