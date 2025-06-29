#!/bin/bash
# Warehousing Domain Cleanup Script

echo "🧹 Starting warehousing domain cleanup..."

# Remove special remediation logs
echo "Removing special remediation logs..."
find . -name "special-remediation-*.log" -type f -delete 2>/dev/null

# Remove error logs
echo "Removing error logs..."
find . -name "*-errors.log" -type f -delete 2>/dev/null
find . -name "build-error.log" -type f -delete 2>/dev/null

# Remove backup files
echo "Removing backup files..."
find . -name "*.backup" -type f -delete 2>/dev/null
find . -name "pom.xml.backup" -type f -delete 2>/dev/null
find . -name "package.json.backup" -type f -delete 2>/dev/null

# Remove Java crash logs
echo "Removing Java crash logs..."
find . -name "hs_err_pid*.log" -type f -delete 2>/dev/null

# Remove compilation outputs
echo "Removing compilation outputs..."
find . -name "compile_output.txt" -type f -delete 2>/dev/null
find . -name "compilation-log.txt" -type f -delete 2>/dev/null

# Remove test files
echo "Removing temporary test files..."
find . -name "fix-test.txt" -type f -delete 2>/dev/null
find . -name "test_line.txt" -type f -delete 2>/dev/null
find . -name "temp_line.txt" -type f -delete 2>/dev/null

# Clean up irregular directories
echo "Removing irregular directories..."
rm -rf com.ecosystem 2>/dev/null
rm -rf compilation-results 2>/dev/null
rm -rf src_backup_* 2>/dev/null

# Clean target directories (optional - uncomment if needed)
# echo "Cleaning target directories..."
# find . -type d -name "target" -exec rm -rf {} + 2>/dev/null

echo "✅ Cleanup completed!"

# Count remaining files
echo ""
echo "📊 Cleanup Statistics:"
echo "- Java services: $(find . -name "pom.xml" -not -path "*/target/*" | wc -l)"
echo "- Node.js services: $(find . -name "package.json" -not -path "*/node_modules/*" | wc -l)"
echo "- Total services: $(find . -maxdepth 2 -name "pom.xml" -o -name "package.json" | wc -l)"