-- Warehouse Onboarding Service Database Schema
-- Version: 1.0.0
-- Description: Initial schema for warehouse partner onboarding

-- Create sequence for generating unique IDs
CREATE SEQUENCE IF NOT EXISTS onboarding_seq START 1000 INCREMENT 1;

-- Partner Onboarding Requests Table
CREATE TABLE partner_onboarding_requests (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Basic Company Information
    company_name VARCHAR(200) NOT NULL,
    legal_business_name VARCHAR(200) NOT NULL,
    business_registration_number VARCHAR(50) UNIQUE NOT NULL,
    tax_identification_number VARCHAR(50) UNIQUE NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    country_of_incorporation VARCHAR(100) NOT NULL,
    date_of_incorporation TIMESTAMP NOT NULL,
    
    -- Contact Information
    primary_contact_name VARCHAR(100) NOT NULL,
    primary_contact_email VARCHAR(100) UNIQUE NOT NULL,
    primary_contact_phone VARCHAR(20) NOT NULL,
    business_address VARCHAR(300) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    
    -- Warehouse Capabilities
    total_storage_capacity DECIMAL(15,2) NOT NULL,
    available_storage_capacity DECIMAL(15,2) NOT NULL,
    has_temperature_control BOOLEAN NOT NULL DEFAULT FALSE,
    has_security_systems BOOLEAN NOT NULL DEFAULT FALSE,
    has_inventory_management_system BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Financial Information
    proposed_pricing_per_cubic_meter DECIMAL(10,2) NOT NULL,
    minimum_order_value DECIMAL(10,2) NOT NULL,
    preferred_payment_terms VARCHAR(100) NOT NULL,
    
    -- Status and Workflow
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    kyc_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    workflow_instance_id VARCHAR(100),
    rejection_reason TEXT,
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    
    -- Constraints
    CONSTRAINT chk_storage_capacity CHECK (available_storage_capacity <= total_storage_capacity),
    CONSTRAINT chk_pricing CHECK (proposed_pricing_per_cubic_meter > 0),
    CONSTRAINT chk_minimum_order CHECK (minimum_order_value > 0)
);

-- Storage Types Junction Table
CREATE TABLE partner_storage_types (
    request_id BIGINT NOT NULL,
    storage_types VARCHAR(50) NOT NULL,
    PRIMARY KEY (request_id, storage_types),
    FOREIGN KEY (request_id) REFERENCES partner_onboarding_requests(id) ON DELETE CASCADE
);

-- Service Capabilities Junction Table
CREATE TABLE partner_service_capabilities (
    request_id BIGINT NOT NULL,
    service_capabilities VARCHAR(50) NOT NULL,
    PRIMARY KEY (request_id, service_capabilities),
    FOREIGN KEY (request_id) REFERENCES partner_onboarding_requests(id) ON DELETE CASCADE
);

-- Onboarding Documents Table
CREATE TABLE onboarding_documents (
    id BIGSERIAL PRIMARY KEY,
    document_id VARCHAR(50) UNIQUE NOT NULL,
    onboarding_request_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_size VARCHAR(20) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    storage_location VARCHAR(500) NOT NULL,
    document_hash VARCHAR(64),
    status VARCHAR(50) NOT NULL DEFAULT 'UPLOADED',
    verification_result TEXT,
    verification_notes TEXT,
    verified_at TIMESTAMP,
    verified_by VARCHAR(100),
    expiry_date TIMESTAMP,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_confidential BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(100) NOT NULL,
    
    FOREIGN KEY (onboarding_request_id) REFERENCES partner_onboarding_requests(id) ON DELETE CASCADE
);

-- KYC Verifications Table
CREATE TABLE kyc_verifications (
    id BIGSERIAL PRIMARY KEY,
    verification_id VARCHAR(50) UNIQUE NOT NULL,
    onboarding_request_id BIGINT NOT NULL,
    verification_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    external_verification_id VARCHAR(100),
    provider_name VARCHAR(100),
    verification_data TEXT,
    confidence_score DECIMAL(5,4),
    risk_level VARCHAR(20),
    findings TEXT,
    recommendations TEXT,
    failure_reason TEXT,
    completed_at TIMESTAMP,
    expiry_date TIMESTAMP,
    requires_manual_review BOOLEAN DEFAULT FALSE,
    reviewed_by VARCHAR(100),
    reviewed_at TIMESTAMP,
    review_notes TEXT,
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    initiated_by VARCHAR(100) NOT NULL,
    
    FOREIGN KEY (onboarding_request_id) REFERENCES partner_onboarding_requests(id) ON DELETE CASCADE
);

-- Compliance Checks Table
CREATE TABLE compliance_checks (
    id BIGSERIAL PRIMARY KEY,
    check_id VARCHAR(50) UNIQUE NOT NULL,
    onboarding_request_id BIGINT NOT NULL,
    compliance_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    jurisdiction VARCHAR(100) NOT NULL,
    regulatory_body VARCHAR(200),
    license_number VARCHAR(100),
    license_expiry_date TIMESTAMP,
    check_details TEXT,
    findings TEXT,
    requirements TEXT,
    remedial_actions TEXT,
    risk_rating VARCHAR(20),
    is_compliant BOOLEAN,
    completed_at TIMESTAMP,
    next_review_date TIMESTAMP,
    performed_by VARCHAR(100),
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    notes TEXT,
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    initiated_by VARCHAR(100) NOT NULL,
    
    FOREIGN KEY (onboarding_request_id) REFERENCES partner_onboarding_requests(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_partner_onboarding_requests_status ON partner_onboarding_requests(status);
CREATE INDEX idx_partner_onboarding_requests_kyc_status ON partner_onboarding_requests(kyc_status);
CREATE INDEX idx_partner_onboarding_requests_country ON partner_onboarding_requests(country);
CREATE INDEX idx_partner_onboarding_requests_created_at ON partner_onboarding_requests(created_at);
CREATE INDEX idx_partner_onboarding_requests_company_name ON partner_onboarding_requests(company_name);
CREATE INDEX idx_partner_onboarding_requests_workflow ON partner_onboarding_requests(workflow_instance_id);

CREATE INDEX idx_onboarding_documents_request_id ON onboarding_documents(onboarding_request_id);
CREATE INDEX idx_onboarding_documents_type ON onboarding_documents(document_type);
CREATE INDEX idx_onboarding_documents_status ON onboarding_documents(status);

CREATE INDEX idx_kyc_verifications_request_id ON kyc_verifications(onboarding_request_id);
CREATE INDEX idx_kyc_verifications_type ON kyc_verifications(verification_type);
CREATE INDEX idx_kyc_verifications_status ON kyc_verifications(status);
CREATE INDEX idx_kyc_verifications_external_id ON kyc_verifications(external_verification_id);

CREATE INDEX idx_compliance_checks_request_id ON compliance_checks(onboarding_request_id);
CREATE INDEX idx_compliance_checks_type ON compliance_checks(compliance_type);
CREATE INDEX idx_compliance_checks_status ON compliance_checks(status);
CREATE INDEX idx_compliance_checks_jurisdiction ON compliance_checks(jurisdiction);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_partner_onboarding_requests_updated_at 
    BEFORE UPDATE ON partner_onboarding_requests 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_onboarding_documents_updated_at 
    BEFORE UPDATE ON onboarding_documents 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_kyc_verifications_updated_at 
    BEFORE UPDATE ON kyc_verifications 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_compliance_checks_updated_at 
    BEFORE UPDATE ON compliance_checks 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert reference data for storage types and service capabilities
-- This would typically be handled by the application, but including for completeness

COMMENT ON TABLE partner_onboarding_requests IS 'Main table for warehouse partner onboarding requests';
COMMENT ON TABLE onboarding_documents IS 'Documents uploaded as part of the onboarding process';
COMMENT ON TABLE kyc_verifications IS 'KYC verification checks performed during onboarding';
COMMENT ON TABLE compliance_checks IS 'Regulatory compliance checks performed during onboarding';

-- Grant necessary permissions (adjust based on your security requirements)
-- GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO warehouse_onboarding_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO warehouse_onboarding_app;