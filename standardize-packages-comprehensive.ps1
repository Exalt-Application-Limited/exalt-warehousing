$baseDir = "c:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem"

# Define source and target package names
$oldPackage = "com.microcommerce"
$newPackage = "com.ecosystem"

# Stats tracking
$filesMoved = 0
$filesDeclUpdated = 0
$filesImportUpdated = 0
$errors = 0

Write-Host "Starting comprehensive package standardization..." -ForegroundColor Green
Write-Host "Will standardize all packages from '$oldPackage' to '$newPackage'" -ForegroundColor Cyan

# Function to create directory if it doesn't exist
function Ensure-Directory {
    param([string]$path)
    
    if (-not (Test-Path -Path $path -PathType Container)) {
        New-Item -Path $path -ItemType Directory -Force | Out-Null
        Write-Host "Created directory: $path" -ForegroundColor Yellow
    }
}

# Function to process imports in a file
function Update-Imports {
    param([string]$filePath)
    
    $content = Get-Content -Path $filePath -Raw
    $originalContent = $content
    
    # Update import statements
    $content = $content -replace "import\s+$oldPackage\.", "import $newPackage."
    
    # Update package declaration
    $content = $content -replace "package\s+$oldPackage\.", "package $newPackage."
    
    # Check if any changes were made
    $importsChanged = $originalContent -ne $content
    
    # Write back the content if changed
    if ($importsChanged) {
        Set-Content -Path $filePath -Value $content
        return $true
    }
    
    return $false
}

# Step 1: Move files from microcommerce to ecosystem directories
Write-Host "Step 1: Moving files from $oldPackage directories to $newPackage directories..." -ForegroundColor Cyan

# Find all Java files within microcommerce directory structure
$javaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse | 
    Where-Object { $_.DirectoryName -match "\\$($oldPackage.Replace('.', '\\'))(\\|\$)" }

foreach ($file in $javaFiles) {
    try {
        # Calculate new path by replacing the microcommerce part with ecosystem
        $relPath = $file.FullName.Substring($file.FullName.IndexOf("\$oldPackage\") + $oldPackage.Length + 1)
        $packagePath = $file.DirectoryName.Substring(0, $file.DirectoryName.IndexOf("\$oldPackage\") + 1)
        $newDir = "$packagePath$newPackage\$relPath"
        $newDir = $newDir.Substring(0, $newDir.LastIndexOf("\"))
        
        # Create the new directory structure if needed
        Ensure-Directory $newDir
        
        $newFilePath = "$newDir\$($file.Name)"
        
        # Check if the file already exists at destination
        if (Test-Path -Path $newFilePath) {
            # If file exists, check if it's identical
            $srcContent = Get-Content -Path $file.FullName -Raw
            $destContent = Get-Content -Path $newFilePath -Raw
            
            if ($srcContent -eq $destContent) {
                Write-Host "Skipping identical file: $($file.Name)" -ForegroundColor DarkGray
            } else {
                Write-Host "WARNING: File $($file.Name) exists at destination with different content." -ForegroundColor Yellow
                Write-Host "Source: $($file.FullName)" -ForegroundColor DarkGray
                Write-Host "Destination: $newFilePath" -ForegroundColor DarkGray
                
                # Update imports in the existing file but don't overwrite it
                if (Update-Imports -filePath $newFilePath) {
                    $filesImportUpdated++
                }
            }
        } else {
            # Move the file to the new location
            Copy-Item -Path $file.FullName -Destination $newFilePath
            
            # Update imports in the moved file
            if (Update-Imports -filePath $newFilePath) {
                $filesImportUpdated++
            }
            
            $filesMoved++
            Write-Host "Moved: $($file.FullName) -> $newFilePath" -ForegroundColor Green
        }
    }
    catch {
        $errors++
        Write-Host "ERROR processing $($file.FullName): $_" -ForegroundColor Red
    }
}

# Step 2: Update package declarations and imports in all Java files
Write-Host "Step 2: Updating package declarations and imports in all Java files..." -ForegroundColor Cyan

$allJavaFiles = Get-ChildItem -Path $baseDir -Filter "*.java" -Recurse | 
    Where-Object { -not ($_.FullName -match "\\target\\") }  # Exclude target directories

foreach ($file in $allJavaFiles) {
    try {
        if (Update-Imports -filePath $file.FullName) {
            $filesDeclUpdated++
            Write-Host "Updated imports/package in: $($file.FullName)" -ForegroundColor Green
        }
    }
    catch {
        $errors++
        Write-Host "ERROR updating imports in $($file.FullName): $_" -ForegroundColor Red
    }
}

# Step 3: Update pom.xml files that may reference the old package
Write-Host "Step 3: Checking pom.xml files for package references..." -ForegroundColor Cyan

$pomFiles = Get-ChildItem -Path $baseDir -Filter "pom.xml" -Recurse

foreach ($pomFile in $pomFiles) {
    try {
        $content = Get-Content -Path $pomFile.FullName -Raw
        $originalContent = $content
        
        # Update groupId references
        $content = $content -replace "<groupId>$oldPackage", "<groupId>$newPackage"
        
        # Update any other references
        $content = $content -replace "$oldPackage\.", "$newPackage."
        
        if ($content -ne $originalContent) {
            Set-Content -Path $pomFile.FullName -Value $content
            Write-Host "Updated package references in pom.xml: $($pomFile.FullName)" -ForegroundColor Green
        }
    }
    catch {
        $errors++
        Write-Host "ERROR updating pom.xml $($pomFile.FullName): $_" -ForegroundColor Red
    }
}

# Summary
Write-Host "`nStandardization Complete!" -ForegroundColor Green
Write-Host "Files moved: $filesMoved" -ForegroundColor Cyan
Write-Host "Files with package declarations updated: $filesDeclUpdated" -ForegroundColor Cyan
Write-Host "Files with imports updated: $filesImportUpdated" -ForegroundColor Cyan
if ($errors -gt 0) {
    Write-Host "Errors encountered: $errors" -ForegroundColor Red
}

Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Review any error messages" -ForegroundColor Yellow
Write-Host "2. Build the project to identify any remaining issues" -ForegroundColor Yellow
Write-Host "3. Fix any specific errors (model/entity duplications)" -ForegroundColor Yellow
Write-Host "4. Run tests to ensure functionality is maintained" -ForegroundColor Yellow
