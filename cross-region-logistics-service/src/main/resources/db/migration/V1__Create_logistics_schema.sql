-- V1__Create_logistics_schema.sql

-- Transfer Request Table
CREATE TABLE IF NOT EXISTS transfer_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transfer_number VARCHAR(100) NOT NULL UNIQUE,
    from_warehouse_id UUID NOT NULL,
    to_warehouse_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    reason VARCHAR(255),
    notes TEXT,
    requested_by UUID,
    approved_by UUID,
    approval_notes TEXT,
    estimated_departure DATE,
    actual_departure TIMESTAMP,
    estimated_arrival DATE,
    actual_arrival TIMESTAMP,
    shipping_method VARCHAR(50),
    carrier VARCHAR(100),
    tracking_number VARCHAR(255),
    shipping_cost DECIMAL(19, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_transfer_status CHECK (status IN ('DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'IN_PREPARATION', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT chk_transfer_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'))
);

-- Transfer Item Table
CREATE TABLE IF NOT EXISTS transfer_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transfer_request_id UUID NOT NULL REFERENCES transfer_request(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    requested_quantity INTEGER NOT NULL,
    approved_quantity INTEGER,
    shipped_quantity INTEGER,
    received_quantity INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    unit_cost DECIMAL(19, 2),
    total_cost DECIMAL(19, 2),
    from_location_code VARCHAR(50),
    to_location_code VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_item_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'SHIPPED', 'RECEIVED', 'CANCELLED'))
);

-- Shipping Route Table
CREATE TABLE IF NOT EXISTS shipping_route (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_warehouse_id UUID NOT NULL,
    to_warehouse_id UUID NOT NULL,
    carrier VARCHAR(100) NOT NULL,
    route_code VARCHAR(100),
    distance_km DECIMAL(10, 2),
    estimated_transit_days INTEGER,
    base_cost DECIMAL(19, 2),
    cost_per_kg DECIMAL(10, 2),
    cost_per_cbm DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_warehouse_route UNIQUE (from_warehouse_id, to_warehouse_id, carrier)
);

-- Transfer Analytics Table
CREATE TABLE IF NOT EXISTS transfer_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    warehouse_id UUID,
    total_transfers INTEGER DEFAULT 0,
    total_items INTEGER DEFAULT 0,
    total_value DECIMAL(19, 2) DEFAULT 0,
    avg_transit_time_days DECIMAL(5, 2),
    on_time_delivery_rate DECIMAL(5, 2),
    damage_rate DECIMAL(5, 2),
    cost_efficiency_score DECIMAL(5, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_transfer_status ON transfer_request(status);
CREATE INDEX idx_transfer_from_warehouse ON transfer_request(from_warehouse_id);
CREATE INDEX idx_transfer_to_warehouse ON transfer_request(to_warehouse_id);
CREATE INDEX idx_transfer_created ON transfer_request(created_at);
CREATE INDEX idx_transfer_item_request ON transfer_item(transfer_request_id);
CREATE INDEX idx_transfer_item_product ON transfer_item(product_id);
CREATE INDEX idx_route_from_warehouse ON shipping_route(from_warehouse_id);
CREATE INDEX idx_route_to_warehouse ON shipping_route(to_warehouse_id);
CREATE INDEX idx_analytics_warehouse ON transfer_analytics(warehouse_id);
CREATE INDEX idx_analytics_period ON transfer_analytics(period_start, period_end);

-- Create updated_at trigger function (if not exists)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for updated_at
CREATE TRIGGER update_transfer_request_updated_at BEFORE UPDATE ON transfer_request FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_transfer_item_updated_at BEFORE UPDATE ON transfer_item FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_shipping_route_updated_at BEFORE UPDATE ON shipping_route FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
