$baseDir = "c:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\Exalt-Application-Limited\social-ecommerce-ecosystem\warehousing"

# Define source and target package path segments
$oldPathSegment = "com\microcommerce"
$newPathSegment = "com\ecosystem"

# Stats tracking
$filesMoved = 0
$dirsCreated = 0
$errors = 0

Write-Host "Starting file migration from $oldPathSegment to $newPathSegment..." -ForegroundColor Green

# Function to create directory if it doesn't exist
function Ensure-Directory {
    param([string]$path)
    
    if (-not (Test-Path -Path $path -PathType Container)) {
        New-Item -Path $path -ItemType Directory -Force | Out-Null
        Write-Host "Created directory: $path" -ForegroundColor Yellow
        $script:dirsCreated++
    }
}

# Find all directories within microcommerce directory structure
$services = @("fulfillment-service", "inventory-service", "warehouse-operations")
foreach ($service in $services) {
    $microDir = Join-Path -Path $baseDir -ChildPath "$service\src\main\java\com\microcommerce"
    if (Test-Path $microDir) {
        Write-Host "Processing $service..." -ForegroundColor Cyan
        
        # Find all files in this directory and subdirectories
        $allFiles = Get-ChildItem -Path $microDir -Recurse -File
        
        foreach ($file in $allFiles) {
            try {
                # Calculate the relative path from microcommerce
                $relativePath = $file.FullName.Substring($microDir.Length)
                
                # Calculate the ecosystem target path
                $targetDir = Join-Path -Path $baseDir -ChildPath "$service\src\main\java\com\ecosystem"
                $targetFilePath = Join-Path -Path $targetDir -ChildPath $relativePath
                $targetFileDir = Split-Path -Path $targetFilePath -Parent
                
                # Ensure target directory exists
                Ensure-Directory -path $targetFileDir
                
                # Check if file already exists
                if (Test-Path $targetFilePath) {
                    Write-Host "  File already exists at target: $targetFilePath" -ForegroundColor DarkYellow
                } else {
                    # Copy the file to the new location
                    Copy-Item -Path $file.FullName -Destination $targetFilePath -Force
                    Write-Host "  Moved: $($file.FullName) -> $targetFilePath" -ForegroundColor Green
                    $filesMoved++
                }
            }
            catch {
                $errors++
                Write-Host "ERROR processing $($file.FullName): $_" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "Skipping $service - no microcommerce directory found" -ForegroundColor DarkGray
    }
}

# Summary
Write-Host "`nFile Migration Complete!" -ForegroundColor Green
Write-Host "Files moved: $filesMoved" -ForegroundColor Cyan
Write-Host "Directories created: $dirsCreated" -ForegroundColor Cyan
if ($errors -gt 0) {
    Write-Host "Errors encountered: $errors" -ForegroundColor Red
}

Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Build the project to identify any remaining issues" -ForegroundColor Yellow
Write-Host "2. After confirming everything works, you can delete the old microcommerce directories" -ForegroundColor Yellow
Write-Host "3. Run the following commands to delete old directories:" -ForegroundColor Yellow
foreach ($service in $services) {
    $microDir = Join-Path -Path $baseDir -ChildPath "$service\src\main\java\com\microcommerce"
    if (Test-Path $microDir) {
        Write-Host "   Remove-Item -Path '$microDir' -Recurse -Force" -ForegroundColor DarkYellow
    }
}
