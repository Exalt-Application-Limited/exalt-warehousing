$baseDir = "c:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"
$oldPackage = "com.microcommerce.warehousing"
$newPackage = "com.ecosystem.warehousing"
$oldDir = "com\microcommerce\warehousing"
$newDir = "com\ecosystem\warehousing"

Write-Host "Starting package standardization script..." -ForegroundColor Green

# Step 1: Count total files to process
$javaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse
$totalFiles = $javaFiles.Count
$processedFiles = 0
$modifiedFiles = 0

Write-Host "Found $totalFiles Java files to analyze" -ForegroundColor Cyan

# Step 2: Update package declarations in all Java files
foreach ($file in $javaFiles) {
    $processedFiles++
    $content = Get-Content -Path $file.FullName -Raw
    
    # Check if this file contains the old package name
    if ($content -match $oldPackage) {
        Write-Host "Processing ($processedFiles/$totalFiles): $($file.FullName)" -ForegroundColor Yellow
        
        # Replace the package declaration
        $newContent = $content -replace "package\s+$oldPackage", "package $newPackage"
        
        # Replace all import statements containing the old package
        $newContent = $newContent -replace "import\s+$oldPackage", "import $newPackage"
        
        # Write the modified content back to the file
        Set-Content -Path $file.FullName -Value $newContent
        
        $modifiedFiles++
        Write-Host "  - Updated package references" -ForegroundColor Green
    }
    
    # Show progress
    if ($processedFiles % 10 -eq 0) {
        Write-Host "Progress: $processedFiles / $totalFiles files processed" -ForegroundColor Cyan
    }
}

# Step 3: Move files from old directory structure to new directory structure
Write-Host "Moving files from old directory structure to new directory structure..." -ForegroundColor Cyan

# Find all directories that match the old directory structure
$microcommerceDirs = Get-ChildItem -Path $baseDir -Filter "microcommerce" -Directory -Recurse | Where-Object {
    $_.FullName -match "\\com\\microcommerce\\warehousing"
}

foreach ($dir in $microcommerceDirs) {
    $parentDir = $dir.Parent.FullName # com directory
    $newParentDir = $parentDir
    
    # Get all subdirectories and files recursively from the microcommerce dir
    $allItems = Get-ChildItem -Path $dir.FullName -Recurse -Directory
    foreach ($subDir in $allItems) {
        if ($subDir.FullName -match "\\com\\microcommerce\\warehousing\\(.*)") {
            $relativePath = $Matches[1]
            $targetDir = Join-Path -Path $parentDir -ChildPath "ecosystem\warehousing\$relativePath"
            
            # Create the target directory if it doesn't exist
            if (-not (Test-Path $targetDir)) {
                New-Item -Path $targetDir -ItemType Directory -Force | Out-Null
                Write-Host "Created directory: $targetDir" -ForegroundColor Magenta
            }
            
            # Copy all files from this subdirectory to the corresponding ecosystem directory
            $filesToMove = Get-ChildItem -Path $subDir.FullName -File
            foreach ($fileToCopy in $filesToMove) {
                $targetFile = Join-Path -Path $targetDir -ChildPath $fileToCopy.Name
                
                # Check if target file already exists
                if (Test-Path $targetFile) {
                    # Compare content to see which is newer/more complete
                    $sourceContent = Get-Content -Path $fileToCopy.FullName -Raw
                    $targetContent = Get-Content -Path $targetFile -Raw
                    
                    if ($sourceContent -ne $targetContent) {
                        Write-Host "Warning: Different versions exist for $($fileToCopy.Name)" -ForegroundColor Yellow
                        Write-Host "  - Source: $($fileToCopy.FullName)" -ForegroundColor Yellow
                        Write-Host "  - Target: $targetFile" -ForegroundColor Yellow
                        # Keep the newer version (by modified date)
                        if ($fileToCopy.LastWriteTime -gt (Get-Item $targetFile).LastWriteTime) {
                            Copy-Item -Path $fileToCopy.FullName -Destination $targetFile -Force
                            Write-Host "  - Used newer source version" -ForegroundColor Yellow
                        }
                        else {
                            Write-Host "  - Kept existing target version" -ForegroundColor Yellow
                        }
                    }
                }
                else {
                    # Target file doesn't exist, simply copy
                    Copy-Item -Path $fileToCopy.FullName -Destination $targetFile -Force
                    Write-Host "Copied: $($fileToCopy.Name) to $targetDir" -ForegroundColor Green
                }
            }
        }
    }
}

Write-Host "Package standardization completed!" -ForegroundColor Green
Write-Host "Modified $modifiedFiles out of $totalFiles files" -ForegroundColor Green
Write-Host "Note: The script has copied files to the new structure. Once you've verified everything works correctly, you may delete the old com\microcommerce directory." -ForegroundColor Yellow
