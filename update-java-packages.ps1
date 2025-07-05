# Script to update Java package structure from com.exalt to com.gogidix
# This script will:
# 1. Create com.gogidix directory structure
# 2. Copy all files from com.exalt to com.gogidix
# 3. Update package declarations in all Java files

$baseDir = "$PSScriptRoot"
Write-Host "Starting Java package update in $baseDir"

# Find all Java source directories
$javaDirs = Get-ChildItem -Path $baseDir -Filter "java" -Recurse -Directory | 
           Where-Object { $_.FullName -match "\\src\\main\\java$" -or $_.FullName -match "\\src\\test\\java$" }

foreach ($javaDir in $javaDirs) {
    $comDir = Join-Path -Path $javaDir.FullName -ChildPath "com"
    
    if (Test-Path $comDir) {
        $exaltDir = Join-Path -Path $comDir -ChildPath "exalt"
        $gogidixDir = Join-Path -Path $comDir -ChildPath "gogidix"
        
        if (Test-Path $exaltDir) {
            Write-Host "Processing directory: $exaltDir"
            
            # Create gogidix directory if it doesn't exist
            if (-not (Test-Path $gogidixDir)) {
                New-Item -Path $gogidixDir -ItemType Directory -Force | Out-Null
                Write-Host "Created directory: $gogidixDir"
            }
            
            # Copy all items from exalt to gogidix recursively
            Copy-Item -Path "$exaltDir\*" -Destination $gogidixDir -Recurse -Force
            Write-Host "Copied files from $exaltDir to $gogidixDir"
            
            # Update package declarations in all Java files
            $javaFiles = Get-ChildItem -Path $gogidixDir -Filter "*.java" -Recurse
            $count = 0
            
            foreach ($javaFile in $javaFiles) {
                $content = Get-Content -Path $javaFile.FullName -Raw
                $updatedContent = $content -replace "package\s+com\.exalt", "package com.gogidix"
                $updatedContent = $updatedContent -replace "import\s+com\.exalt", "import com.gogidix"
                
                if ($content -ne $updatedContent) {
                    Set-Content -Path $javaFile.FullName -Value $updatedContent
                    $count++
                }
            }
            
            Write-Host "Updated package declarations in $count Java files"
        }
    }
}

Write-Host "\nUpdating import statements in all Java files..."

# Update import statements in all Java files (including those not moved)
$allJavaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse
$importUpdateCount = 0

foreach ($javaFile in $allJavaFiles) {
    $content = Get-Content -Path $javaFile.FullName -Raw
    $updatedContent = $content -replace "import\s+com\.exalt", "import com.gogidix"
    
    if ($content -ne $updatedContent) {
        Set-Content -Path $javaFile.FullName -Value $updatedContent
        $importUpdateCount++
    }
}

Write-Host "Updated import statements in $importUpdateCount Java files"

Write-Host "\nJava package update completed successfully!"
Write-Host "IMPORTANT: Please review all changes and ensure all services compile correctly."
