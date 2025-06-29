# Warehousing Shared Library

A comprehensive shared library providing common utilities, DTOs, enums, and infrastructure components for all warehousing domain microservices.

## 🎯 Overview

The Warehousing Shared Library centralizes common functionality used across all warehousing microservices, ensuring consistency, reducing code duplication, and providing a single source of truth for shared domain concepts.

### Key Features

- **Common Domain Models** - Base entities, enums, and DTOs
- **Utility Classes** - Calculation, validation, and helper utilities
- **Exception Handling** - Standardized exception hierarchy
- **Configuration** - Auto-configuration for Spring Boot services
- **Validation Framework** - Comprehensive validation utilities
- **Mathematical Calculations** - Warehouse-specific business calculations

## 🏗️ Architecture

### Package Structure

```
com.ecosystem.warehousing.shared/
├── common/           # Base entities and common interfaces
├── enums/            # Domain enumerations
├── dto/              # Data Transfer Objects
├── utils/            # Utility classes
├── exception/        # Exception hierarchy
└── config/           # Auto-configuration classes
```

### Design Principles

- **Domain-Driven Design** - Reflects warehousing domain concepts
- **Immutability** - DTOs and value objects are immutable where possible
- **Validation-First** - Comprehensive validation at all levels
- **Performance-Optimized** - Efficient algorithms and data structures
- **Spring Boot Integration** - Auto-configuration and Spring-friendly

## 📦 Components

### 1. Common Base Classes

#### BaseEntity
```java
@MappedSuperclass
public abstract class BaseEntity {
    // UUID primary key
    // Audit timestamps (created/updated)
    // Soft delete support
    // Version control for optimistic locking
    // Tenant support for multi-tenancy
}
```

**Features:**
- UUID primary keys for distributed systems
- Automatic audit timestamp management
- Soft delete functionality
- Optimistic locking with version control
- Multi-tenant support
- Common entity lifecycle methods

### 2. Domain Enumerations

#### WarehouseType
```java
public enum WarehouseType {
    FULFILLMENT_CENTER, STORAGE_FACILITY, CROSS_DOCK,
    REGIONAL_DC, LOCAL_DELIVERY, COLD_STORAGE,
    // ... 16 total types with capabilities
}
```

**Features:**
- 16 different warehouse types
- Capability flags (storage, processing, shipping)
- Operational characteristics
- Capacity categories
- Suitability assessments

#### WarehouseStatus
```java
public enum WarehouseStatus {
    PLANNING, UNDER_CONSTRUCTION, SETUP, COMMISSIONING,
    ACTIVE, LIMITED_OPERATIONS, MAINTENANCE, SUSPENDED,
    DECOMMISSIONING, CLOSED, UNKNOWN
}
```

**Features:**
- Complete operational lifecycle
- Transition validation
- Capacity calculations
- Priority levels for alerts
- Color coding for UI

### 3. Data Transfer Objects

#### AddressDTO
```java
public class AddressDTO {
    // Comprehensive address fields
    // International address support
    // Geocoding coordinates
    // Address type classification
    // Validation and formatting
}
```

**Features:**
- International address formats
- Geocoding support (latitude/longitude)
- Address type classification
- Validation for all fields
- Formatted address output
- Distance calculations

### 4. Utility Classes

#### WarehouseCalculationUtils
```java
@UtilityClass
public class WarehouseCalculationUtils {
    // 20+ calculation methods
    // Performance metrics
    // Statistical analysis
    // Financial calculations
}
```

**Available Calculations:**
- Capacity utilization percentage
- Throughput rates (items/hour)
- Order fulfillment accuracy
- Inventory turnover ratios
- Days sales of inventory (DSI)
- Order cycle times
- Picking productivity
- Cost per order
- Space productivity
- Equipment utilization
- Labor productivity
- Damage and return rates
- Weighted performance scores
- Statistical measures
- Trend analysis

#### ValidationUtils
```java
@UtilityClass
public class ValidationUtils {
    // 30+ validation methods
    // Format validations
    // Business rule validations
    // Data sanitization
}
```

**Validation Types:**
- String format validation (email, phone, postal codes)
- Numeric range validation
- Date and time validation
- Geographic coordinate validation
- Business identifier validation
- Warehouse-specific validations
- Data sanitization methods

### 5. Exception Hierarchy

#### WarehouseException (Base)
- Error codes and timestamps
- Context information
- Detailed error messages
- Cause chain support

#### ResourceNotFoundException
- Type-safe resource identification
- Static factory methods for common resources
- Contextual error information

#### ValidationException
- Multiple validation error support
- Field-specific error mapping
- Comprehensive error reporting

### 6. Auto-Configuration

#### WarehouseSharedAutoConfiguration
- ObjectMapper configuration
- JSON serialization standards
- Timezone management
- Default validation settings
- Calculation configurations

## 🚀 Usage

### Maven Dependency

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.ecosystem</groupId>
    <artifactId>warehousing-shared</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Auto-Configuration

The library automatically configures when included:

```java
@SpringBootApplication
public class YourWarehouseService {
    // Auto-configuration happens automatically
}
```

### Using Base Entity

```java
@Entity
@Table(name = "warehouses")
public class Warehouse extends BaseEntity {
    
    @NotBlank
    private String name;
    
    @Enumerated(EnumType.STRING)
    private WarehouseType type;
    
    @Enumerated(EnumType.STRING)
    private WarehouseStatus status;
    
    // Entity-specific fields...
}
```

### Using Calculations

```java
@Service
public class MetricsService {
    
    public BigDecimal calculateUtilization(BigDecimal used, BigDecimal total) {
        return WarehouseCalculationUtils.calculateCapacityUtilization(used, total);
    }
    
    public BigDecimal calculateThroughput(int items, Duration timeSpan) {
        return WarehouseCalculationUtils.calculateThroughputRate(items, timeSpan);
    }
}
```

### Using Validation

```java
@Service
public class WarehouseService {
    
    public void validateWarehouse(WarehouseDTO warehouse) {
        if (!ValidationUtils.isNotNullOrEmpty(warehouse.getName())) {
            throw ValidationException.requiredField("name");
        }
        
        if (!ValidationUtils.isValidEmail(warehouse.getContactEmail())) {
            throw ValidationException.invalidFormat("contactEmail", "email");
        }
    }
}
```

### Using DTOs

```java
@RestController
public class WarehouseController {
    
    @PostMapping("/warehouses")
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        // AddressDTO validation happens automatically
        AddressDTO address = request.getAddress();
        
        if (!address.isComplete()) {
            throw new ValidationException("address", "Incomplete address information");
        }
        
        // Business logic...
    }
}
```

## 🧪 Testing

### Test Utilities

The library includes test utilities:

```java
public class WarehouseTestData {
    
    public static AddressDTO createValidAddress() {
        return AddressDTO.builder()
                .addressLine1("123 Warehouse St")
                .city("Industrial City")
                .state("CA")
                .postalCode("90210")
                .country("United States")
                .countryCode("US")
                .build();
    }
    
    public static WarehouseType getRandomWarehouseType() {
        WarehouseType[] types = WarehouseType.values();
        return types[new Random().nextInt(types.length)];
    }
}
```

### Running Tests

```bash
mvn test
```

### Code Coverage

```bash
mvn test jacoco:report
```

## 📈 Performance Considerations

### Calculation Performance
- **Optimized algorithms** for statistical calculations
- **BigDecimal precision** for financial calculations
- **Caching** for expensive operations
- **Minimal object creation** in utility methods

### Memory Usage
- **Immutable DTOs** where possible
- **Efficient collections** usage
- **String interning** for constants
- **Lazy initialization** for expensive computations

### Serialization
- **Optimized JSON serialization** with Jackson
- **Reduced payload sizes** with selective serialization
- **Date/time handling** with proper timezone support

## 🔧 Configuration

### Application Properties

```yaml
warehouse:
  shared:
    validation:
      max-string-length: 255
      max-text-length: 4000
      max-name-length: 100
    calculation:
      decimal-scale: 4
      percentage-scale: 2
      currency-scale: 2
    timezone:
      default: "UTC"
```

### Custom Configuration

```java
@Configuration
public class CustomWarehouseConfig {
    
    @Bean
    @Primary
    public ValidationConfiguration customValidation() {
        // Override default validation settings
        return new ValidationConfiguration() {
            @Override
            public int getDefaultMaxStringLength() {
                return 500; // Custom length
            }
        };
    }
}
```

## 🔄 Version Compatibility

### Version 1.0.0
- **Spring Boot**: 3.1.x
- **Java**: 17+
- **Jackson**: 2.15.x
- **Apache Commons**: 3.13.x
- **MapStruct**: 1.5.x

### Backward Compatibility
- **Semantic versioning** for releases
- **Deprecation warnings** before breaking changes
- **Migration guides** for major updates

## 🤝 Contributing

### Development Guidelines

1. **Follow package conventions**
2. **Add comprehensive tests**
3. **Document public APIs**
4. **Validate performance impact**
5. **Update version compatibility**

### Adding New Utilities

```java
@UtilityClass
public class NewWarehouseUtils {
    
    /**
     * Comprehensive JavaDoc
     * 
     * @param parameter description
     * @return return description
     */
    public static ReturnType newUtilityMethod(ParameterType parameter) {
        // Implementation with validation
        // Error handling
        // Performance optimization
    }
}
```

### Testing Requirements

- **90%+ code coverage** for new utilities
- **Unit tests** for all public methods
- **Integration tests** for complex workflows
- **Performance tests** for calculation methods

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## 🏷️ Version History

### v1.0.0 (Current)
- ✅ Base entity with audit support
- ✅ Comprehensive enum definitions
- ✅ Address DTO with geocoding
- ✅ Calculation utilities (20+ methods)
- ✅ Validation utilities (30+ methods)
- ✅ Exception hierarchy
- ✅ Auto-configuration support
- ✅ Test utilities

### Planned Features (v1.1.0)
- 🔄 Additional calculation methods
- 🔄 Enhanced validation rules
- 🔄 Performance optimizations
- 🔄 Additional DTOs
- 🔄 Internationalization support

---

**Warehousing Shared Library** - Foundation for consistent, high-quality warehousing microservices

🤖 Generated with [Memex](https://memex.tech)
Co-Authored-By: Memex <noreply@memex.tech>