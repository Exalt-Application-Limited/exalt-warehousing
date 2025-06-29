-- Self Storage Service Database Schema
-- Version 1.0.0 - Initial vendor storage locations table creation

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create vendor_storage_locations table
CREATE TABLE vendor_storage_locations (
    -- Primary Key and Audit Fields (from BaseEntity)
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),
    version                 BIGINT NOT NULL DEFAULT 0,
    tenant_id               VARCHAR(100),
    is_deleted              BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at              TIMESTAMP WITH TIME ZONE,
    deleted_by              VARCHAR(100),
    
    -- Vendor Information
    vendor_id               VARCHAR(100) NOT NULL,
    vendor_name             VARCHAR(100) NOT NULL,
    location_name           VARCHAR(100) NOT NULL,
    storage_type            VARCHAR(30) NOT NULL,
    status                  VARCHAR(20) NOT NULL,
    
    -- Address Information
    street_address          VARCHAR(200) NOT NULL,
    address_line_2          VARCHAR(100),
    city                    VARCHAR(50) NOT NULL,
    state                   VARCHAR(50) NOT NULL,
    postal_code             VARCHAR(20) NOT NULL,
    country                 VARCHAR(3) NOT NULL,
    latitude                DECIMAL(10,8),
    longitude               DECIMAL(11,8),
    
    -- Contact Information
    contact_name            VARCHAR(100) NOT NULL,
    contact_phone           VARCHAR(20) NOT NULL,
    contact_email           VARCHAR(100),
    alternative_phone       VARCHAR(20),
    
    -- Storage Specifications
    storage_area_sqft       INTEGER NOT NULL CHECK (storage_area_sqft > 0),
    current_utilization     INTEGER NOT NULL DEFAULT 0 CHECK (current_utilization >= 0 AND current_utilization <= 100),
    max_weight_capacity     INTEGER CHECK (max_weight_capacity >= 0),
    has_climate_control     BOOLEAN NOT NULL DEFAULT FALSE,
    target_temperature      INTEGER CHECK (target_temperature >= 32 AND target_temperature <= 90),
    target_humidity         INTEGER CHECK (target_humidity >= 10 AND target_humidity <= 80),
    has_security            BOOLEAN NOT NULL DEFAULT FALSE,
    security_features       VARCHAR(500),
    
    -- Operating Hours
    opening_time            TIME,
    closing_time            TIME,
    operating_days          VARCHAR(50),
    is_24_7                 BOOLEAN NOT NULL DEFAULT FALSE,
    pickup_instructions     VARCHAR(1000),
    
    -- Performance Metrics
    avg_processing_time     DECIMAL(5,2) NOT NULL DEFAULT 0.00 CHECK (avg_processing_time >= 0),
    fulfillment_rate        DECIMAL(5,2) NOT NULL DEFAULT 100.00 CHECK (fulfillment_rate >= 0 AND fulfillment_rate <= 100),
    performance_score       DECIMAL(5,2) NOT NULL DEFAULT 85.00 CHECK (performance_score >= 0 AND performance_score <= 100),
    total_orders_processed  BIGINT NOT NULL DEFAULT 0 CHECK (total_orders_processed >= 0),
    last_pickup_date        TIMESTAMP WITH TIME ZONE,
    
    -- Verification and Compliance
    verification_date       TIMESTAMP WITH TIME ZONE,
    verification_notes      VARCHAR(1000),
    
    -- Notes and Additional Information
    notes                   VARCHAR(1000)
);

-- Create indexes for performance optimization
CREATE INDEX idx_vendor_storage_vendor_id ON vendor_storage_locations(vendor_id);
CREATE INDEX idx_vendor_storage_status ON vendor_storage_locations(status);
CREATE INDEX idx_vendor_storage_type ON vendor_storage_locations(storage_type);
CREATE INDEX idx_vendor_storage_city ON vendor_storage_locations(city);
CREATE INDEX idx_vendor_storage_postal ON vendor_storage_locations(postal_code);
CREATE INDEX idx_vendor_storage_active ON vendor_storage_locations(status, vendor_id);
CREATE INDEX idx_vendor_storage_capacity ON vendor_storage_locations(current_utilization);
CREATE INDEX idx_vendor_storage_performance ON vendor_storage_locations(performance_score);
CREATE INDEX idx_vendor_storage_created_at ON vendor_storage_locations(created_at);
CREATE INDEX idx_vendor_storage_updated_at ON vendor_storage_locations(updated_at);

-- Create spatial index for geographic queries if PostGIS is available
-- CREATE INDEX idx_vendor_storage_location ON vendor_storage_locations USING GIST(ST_Point(longitude, latitude));

-- Create unique constraint to prevent duplicate locations
CREATE UNIQUE INDEX uk_vendor_storage_location 
ON vendor_storage_locations(vendor_id, street_address, city) 
WHERE is_deleted = FALSE;

-- Create composite indexes for common query patterns
CREATE INDEX idx_vendor_storage_search ON vendor_storage_locations(status, storage_type, has_climate_control);
CREATE INDEX idx_vendor_storage_geo ON vendor_storage_locations(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;
CREATE INDEX idx_vendor_storage_pickup ON vendor_storage_locations(status, current_utilization, performance_score);

-- Add check constraints for business rules
ALTER TABLE vendor_storage_locations ADD CONSTRAINT chk_vendor_storage_operating_hours 
CHECK (
    (is_24_7 = TRUE) OR 
    (is_24_7 = FALSE AND opening_time IS NOT NULL AND closing_time IS NOT NULL)
);

ALTER TABLE vendor_storage_locations ADD CONSTRAINT chk_vendor_storage_climate_control 
CHECK (
    (has_climate_control = FALSE) OR 
    (has_climate_control = TRUE AND target_temperature IS NOT NULL AND target_humidity IS NOT NULL)
);

-- Add valid enum constraints
ALTER TABLE vendor_storage_locations ADD CONSTRAINT chk_vendor_storage_status 
CHECK (status IN (
    'ACTIVE', 'INACTIVE', 'SUSPENDED', 'MAINTENANCE', 
    'VERIFICATION', 'REJECTED', 'CAPACITY_FULL'
));

ALTER TABLE vendor_storage_locations ADD CONSTRAINT chk_vendor_storage_type 
CHECK (storage_type IN (
    'HOME_GARAGE', 'HOME_BASEMENT', 'HOME_SPARE_ROOM',
    'COMMERCIAL_WAREHOUSE', 'COMMERCIAL_UNIT',
    'RETAIL_STORE', 'RETAIL_BACK_ROOM',
    'SHARED_WAREHOUSE', 'COWORKING_STORAGE',
    'CLIMATE_CONTROLLED', 'COLD_STORAGE', 'SECURE_FACILITY'
));

-- Add trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_vendor_storage_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_vendor_storage_updated_at
    BEFORE UPDATE ON vendor_storage_locations
    FOR EACH ROW
    EXECUTE FUNCTION update_vendor_storage_updated_at();

-- Add function to calculate distance between two points (Haversine formula)
CREATE OR REPLACE FUNCTION calculate_distance_km(
    lat1 DECIMAL, lon1 DECIMAL, 
    lat2 DECIMAL, lon2 DECIMAL
) RETURNS DECIMAL AS $$
DECLARE
    earth_radius DECIMAL := 6371; -- Earth radius in kilometers
    dlat DECIMAL;
    dlon DECIMAL;
    a DECIMAL;
    c DECIMAL;
BEGIN
    dlat := RADIANS(lat2 - lat1);
    dlon := RADIANS(lon2 - lon1);
    
    a := SIN(dlat/2) * SIN(dlat/2) + 
         COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * 
         SIN(dlon/2) * SIN(dlon/2);
    
    c := 2 * ATAN2(SQRT(a), SQRT(1-a));
    
    RETURN earth_radius * c;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Create function to update performance score based on metrics
CREATE OR REPLACE FUNCTION calculate_performance_score(
    p_avg_processing_time DECIMAL,
    p_fulfillment_rate DECIMAL,
    p_current_utilization INTEGER
) RETURNS DECIMAL AS $$
DECLARE
    base_score DECIMAL := 50.0;
    processing_score DECIMAL := 0.0;
    fulfillment_score DECIMAL := 0.0;
    utilization_score DECIMAL := 0.0;
    final_score DECIMAL;
BEGIN
    -- Processing time factor (faster is better)
    IF p_avg_processing_time <= 24 THEN
        processing_score := 20.0;
    ELSIF p_avg_processing_time <= 48 THEN
        processing_score := 10.0;
    END IF;
    
    -- Fulfillment rate factor
    fulfillment_score := p_fulfillment_rate * 0.3;
    
    -- Utilization efficiency (sweet spot 70-90%)
    IF p_current_utilization BETWEEN 70 AND 90 THEN
        utilization_score := 10.0;
    ELSIF p_current_utilization BETWEEN 50 AND 95 THEN
        utilization_score := 5.0;
    END IF;
    
    final_score := base_score + processing_score + fulfillment_score + utilization_score;
    
    -- Ensure score is within valid range
    RETURN LEAST(100.0, GREATEST(0.0, final_score));
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Add comments for documentation
COMMENT ON TABLE vendor_storage_locations IS 'Vendor-owned storage locations for self-managed inventory fulfillment';
COMMENT ON COLUMN vendor_storage_locations.vendor_id IS 'Unique identifier for the vendor who owns this storage location';
COMMENT ON COLUMN vendor_storage_locations.storage_type IS 'Type of storage facility (HOME_GARAGE, COMMERCIAL_WAREHOUSE, etc.)';
COMMENT ON COLUMN vendor_storage_locations.status IS 'Current operational status of the storage location';
COMMENT ON COLUMN vendor_storage_locations.current_utilization IS 'Current storage utilization percentage (0-100)';
COMMENT ON COLUMN vendor_storage_locations.performance_score IS 'Overall performance score based on metrics (0-100)';
COMMENT ON COLUMN vendor_storage_locations.avg_processing_time IS 'Average order processing time in hours';
COMMENT ON COLUMN vendor_storage_locations.fulfillment_rate IS 'Order fulfillment success rate percentage';

-- Create view for active locations with calculated distances (example for future use)
-- CREATE VIEW active_vendor_locations AS
-- SELECT 
--     vsl.*,
--     CASE 
--         WHEN vsl.latitude IS NOT NULL AND vsl.longitude IS NOT NULL 
--         THEN TRUE 
--         ELSE FALSE 
--     END AS has_coordinates
-- FROM vendor_storage_locations vsl
-- WHERE vsl.status = 'ACTIVE' 
--   AND vsl.is_deleted = FALSE;