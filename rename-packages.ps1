# Script to rename Java packages from com.exalt to com.gogidix across the warehousing domain
# This script handles:
# 1. Java package declarations
# 2. Import statements
# 3. POM file groupId references
# 4. Directory structure renaming

$oldPackage = "com.exalt"
$newPackage = "com.gogidix"
$baseDir = "$PSScriptRoot"

Write-Host "Starting package rename from $oldPackage to $newPackage in $baseDir"

# Step 1: Update all pom.xml files (groupId references)
Write-Host "\nStep 1: Updating POM files..."
$pomFiles = Get-ChildItem -Path $baseDir -Filter "pom.xml" -Recurse
$totalPomCount = $pomFiles.Count
$currentPomCount = 0

foreach ($pomFile in $pomFiles) {
    $currentPomCount++
    Write-Host "Processing POM file ($currentPomCount/$totalPomCount): $($pomFile.FullName)"
    
    $content = Get-Content -Path $pomFile.FullName -Raw
    $updatedContent = $content -replace "$oldPackage\.warehousing", "$newPackage.warehousing"
    
    if ($content -ne $updatedContent) {
        Write-Host "  - Updated POM: $($pomFile.FullName)"
        Set-Content -Path $pomFile.FullName -Value $updatedContent
    }
}

# Step 2: Update Java files (package declarations and imports)
Write-Host "\nStep 2: Updating Java files..."
$javaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse
$totalJavaCount = $javaFiles.Count
$currentJavaCount = 0

foreach ($javaFile in $javaFiles) {
    $currentJavaCount++
    Write-Host "Processing Java file ($currentJavaCount/$totalJavaCount): $($javaFile.FullName)"
    
    $content = Get-Content -Path $javaFile.FullName -Raw
    $updatedContent = $content -replace "$oldPackage\.", "$newPackage."
    
    if ($content -ne $updatedContent) {
        Write-Host "  - Updated Java file: $($javaFile.FullName)"
        Set-Content -Path $javaFile.FullName -Value $updatedContent
    }
}

# Step 3: Update directory structure
Write-Host "\nStep 3: Updating directory structure..."

# Find all com.exalt package directories
$exaltDirs = Get-ChildItem -Path $baseDir -Filter "exalt" -Recurse -Directory | 
             Where-Object { $_.FullName -match "\\com\\exalt$" }

foreach ($dir in $exaltDirs) {
    $parentDir = $dir.Parent.FullName
    $oldPath = $dir.FullName
    $newPath = Join-Path -Path $parentDir -ChildPath "gogidix"
    
    Write-Host "Renaming directory: $oldPath -> $newPath"
    
    # First, check if target directory already exists
    if (Test-Path $newPath) {
        # Move all files and subdirectories from old to new
        $items = Get-ChildItem -Path $oldPath -Force
        foreach ($item in $items) {
            $targetPath = Join-Path -Path $newPath -ChildPath $item.Name
            if (!(Test-Path $targetPath)) {
                Move-Item -Path $item.FullName -Destination $newPath -Force
                Write-Host "  - Moved $($item.Name) to $newPath"
            }
        }
    } else {
        # Rename the directory
        Rename-Item -Path $oldPath -NewName "gogidix"
    }
}

# Step 4: Update Spring configuration files
Write-Host "\nStep 4: Updating Spring configuration files..."
$configFiles = Get-ChildItem -Path $baseDir -Include "*.properties", "*.yml", "*.yaml" -Recurse
$totalConfigCount = $configFiles.Count
$currentConfigCount = 0

foreach ($configFile in $configFiles) {
    $currentConfigCount++
    Write-Host "Processing config file ($currentConfigCount/$totalConfigCount): $($configFile.FullName)"
    
    $content = Get-Content -Path $configFile.FullName -Raw
    $updatedContent = $content -replace "$oldPackage\.", "$newPackage."
    
    if ($content -ne $updatedContent) {
        Write-Host "  - Updated config file: $($configFile.FullName)"
        Set-Content -Path $configFile.FullName -Value $updatedContent
    }
}

Write-Host "\nPackage rename completed successfully!"
Write-Host "Please review the changes and ensure all services compile and pass their tests."
