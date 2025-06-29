#!/bin/bash

echo "=== Adding Lombok Annotations to Warehouse Services ==="
echo "Date: $(date)"
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Update parent POM to include Lombok in dependency management
echo -e "${YELLOW}Updating parent POM with Lombok dependency management...${NC}"
sed -i '/<dependencyManagement>/,/<\/dependencyManagement>/s|</dependencies>|            <dependency>\
                <groupId>org.projectlombok</groupId>\
                <artifactId>lombok</artifactId>\
                <version>1.18.30</version>\
                <scope>provided</scope>\
            </dependency>\
        </dependencies>|' pom.xml

echo -e "${GREEN}✓ Parent POM updated${NC}"

# Fix Warehouse.java to use Lombok
echo -e "${YELLOW}Refactoring Warehouse.java to use Lombok...${NC}"
cat > warehouse-management-service/src/main/java/com/exalt/warehousing/management/model/Warehouse.java << 'EOF'
package com.exalt.warehousing.management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "warehouses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Warehouse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
    @Column(nullable = false)
    private Double totalCapacity;
    
    @Column(nullable = false)
    private Double availableCapacity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseType type;
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Location> locations = new HashSet<>();
    
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Staff> staff = new HashSet<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
EOF

echo -e "${GREEN}✓ Warehouse.java refactored${NC}"

# Fix Staff.java to use Lombok
echo -e "${YELLOW}Refactoring Staff.java to use Lombok...${NC}"
cat > warehouse-management-service/src/main/java/com/exalt/warehousing/management/model/Staff.java << 'EOF'
package com.exalt.warehousing.management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "warehouse")
public class Staff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String employeeId;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Column(nullable = false)
    private LocalDate joinDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
EOF

echo -e "${GREEN}✓ Staff.java refactored${NC}"

# Check and add Lombok annotations to other entity files
echo -e "${YELLOW}Checking other services for missing Lombok annotations...${NC}"

# Services to check
SERVICES=(
    "warehouse-analytics"
    "warehouse-subscription"
    "warehouse-operations"
    "warehouse-onboarding"
)

for service in "${SERVICES[@]}"; do
    if [ -d "$service/src/main/java" ]; then
        echo -e "${YELLOW}Checking $service...${NC}"
        
        # Find all entity classes
        find "$service/src/main/java" -name "*.java" -path "*/model/*" -o -path "*/entity/*" | while read -r file; do
            # Check if file has @Entity annotation but no @Data
            if grep -q "@Entity" "$file" && ! grep -q "@Data" "$file"; then
                echo "  Adding Lombok annotations to: $(basename "$file")"
                
                # Add Lombok imports after package declaration
                sed -i '/^package/a\\nimport lombok.*;\n' "$file"
                
                # Add annotations before @Entity
                sed -i '/@Entity/i\@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor' "$file"
            fi
        done
    fi
done

echo -e "${GREEN}✓ Lombok annotation check complete${NC}"

echo ""
echo "=== Summary ==="
echo "1. Added Lombok to parent POM dependency management"
echo "2. Refactored Warehouse.java to use Lombok annotations"
echo "3. Refactored Staff.java to use Lombok annotations"
echo "4. Checked all services for missing Lombok annotations"
echo ""
echo -e "${GREEN}Lombok standardization complete!${NC}"