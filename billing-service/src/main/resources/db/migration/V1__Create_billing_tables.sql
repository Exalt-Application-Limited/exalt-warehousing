-- Create billing service database schema
-- Version: 1.0.0
-- Description: Initial schema for billing accounts, subscriptions, invoices, and payments

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create billing_accounts table
CREATE TABLE billing_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_partner_id UUID UNIQUE NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    billing_email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    billing_address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    tax_id VARCHAR(50),
    vat_number VARCHAR(50),
    account_status VARCHAR(50) NOT NULL DEFAULT 'PENDING_ACTIVATION',
    preferred_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    credit_limit DECIMAL(15,2),
    current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    outstanding_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    payment_terms_days INTEGER,
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    auto_pay_enabled BOOLEAN DEFAULT FALSE,
    billing_cycle_day INTEGER,
    next_billing_date TIMESTAMP,
    last_billing_date TIMESTAMP,
    account_opened_date TIMESTAMP,
    account_closed_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create subscriptions table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    billing_account_id UUID NOT NULL REFERENCES billing_accounts(id) ON DELETE CASCADE,
    warehouse_id UUID NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    plan_type VARCHAR(50) NOT NULL,
    plan_description VARCHAR(500),
    monthly_fee DECIMAL(15,2) NOT NULL,
    setup_fee DECIMAL(15,2),
    currency VARCHAR(3) NOT NULL,
    billing_frequency VARCHAR(50) NOT NULL,
    subscription_status VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    next_billing_date TIMESTAMP,
    last_billing_date TIMESTAMP,
    trial_end_date TIMESTAMP,
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    storage_limit_cubic_meters DECIMAL(10,2),
    storage_limit_weight_kg DECIMAL(10,2),
    monthly_transaction_limit INTEGER,
    daily_transaction_limit INTEGER,
    api_calls_per_minute INTEGER,
    api_calls_per_day INTEGER,
    analytics_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    priority_support BOOLEAN NOT NULL DEFAULT FALSE,
    custom_integrations BOOLEAN NOT NULL DEFAULT FALSE,
    white_label BOOLEAN NOT NULL DEFAULT FALSE,
    multi_warehouse BOOLEAN NOT NULL DEFAULT FALSE,
    cancellation_date TIMESTAMP,
    cancellation_reason VARCHAR(50),
    cancellation_notes TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create subscription_usage table
CREATE TABLE subscription_usage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subscription_id UUID NOT NULL REFERENCES subscriptions(id) ON DELETE CASCADE,
    usage_date TIMESTAMP NOT NULL,
    billing_period_start TIMESTAMP NOT NULL,
    billing_period_end TIMESTAMP NOT NULL,
    storage_used_cubic_meters DECIMAL(10,2),
    storage_used_weight_kg DECIMAL(10,2),
    peak_storage_cubic_meters DECIMAL(10,2),
    peak_storage_weight_kg DECIMAL(10,2),
    transactions_count INTEGER,
    inbound_transactions INTEGER,
    outbound_transactions INTEGER,
    transfer_transactions INTEGER,
    api_calls_count INTEGER,
    peak_api_calls_per_minute INTEGER,
    bandwidth_used_gb DECIMAL(10,3),
    storage_cost DECIMAL(15,2),
    transaction_cost DECIMAL(15,2),
    api_cost DECIMAL(15,2),
    bandwidth_cost DECIMAL(15,2),
    total_usage_cost DECIMAL(15,2),
    storage_limit_exceeded BOOLEAN NOT NULL DEFAULT FALSE,
    transaction_limit_exceeded BOOLEAN NOT NULL DEFAULT FALSE,
    api_limit_exceeded BOOLEAN NOT NULL DEFAULT FALSE,
    storage_overage_cost DECIMAL(15,2),
    transaction_overage_cost DECIMAL(15,2),
    api_overage_cost DECIMAL(15,2),
    total_overage_cost DECIMAL(15,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create invoices table
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    billing_account_id UUID NOT NULL REFERENCES billing_accounts(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_status VARCHAR(50) NOT NULL,
    invoice_type VARCHAR(50) NOT NULL,
    invoice_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    paid_date TIMESTAMP,
    billing_period_start TIMESTAMP NOT NULL,
    billing_period_end TIMESTAMP NOT NULL,
    currency VARCHAR(3) NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    tax_amount DECIMAL(15,2),
    tax_rate DECIMAL(5,4),
    discount_amount DECIMAL(15,2),
    discount_percentage DECIMAL(5,2),
    total_amount DECIMAL(15,2) NOT NULL,
    amount_paid DECIMAL(15,2),
    amount_due DECIMAL(15,2),
    description TEXT,
    notes TEXT,
    payment_reference VARCHAR(100),
    payment_method VARCHAR(50),
    late_fee DECIMAL(15,2),
    is_recurring BOOLEAN,
    parent_invoice_id UUID,
    billing_address VARCHAR(500),
    po_number VARCHAR(100),
    auto_pay_attempted BOOLEAN,
    auto_pay_failure_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create invoice_line_items table
CREATE TABLE invoice_line_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description VARCHAR(255) NOT NULL,
    item_code VARCHAR(100),
    item_category VARCHAR(100),
    quantity DECIMAL(10,3) NOT NULL,
    unit_of_measure VARCHAR(20),
    unit_price DECIMAL(15,4) NOT NULL,
    discount_amount DECIMAL(15,2),
    discount_percentage DECIMAL(5,2),
    total_amount DECIMAL(15,2) NOT NULL,
    tax_rate DECIMAL(5,4),
    tax_amount DECIMAL(15,2),
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    subscription_id UUID,
    usage_record_id UUID,
    warehouse_id UUID,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create payments table
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    billing_account_id UUID NOT NULL,
    payment_reference VARCHAR(50),
    external_transaction_id VARCHAR(100),
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP,
    processed_date TIMESTAMP,
    cleared_date TIMESTAMP,
    failed_date TIMESTAMP,
    failure_reason VARCHAR(500),
    processor_name VARCHAR(100),
    processor_transaction_id VARCHAR(100),
    processor_fee DECIMAL(15,2),
    net_amount DECIMAL(15,2),
    bank_reference VARCHAR(100),
    check_number VARCHAR(100),
    is_manual BOOLEAN,
    is_refund BOOLEAN,
    refund_amount DECIMAL(15,2),
    refund_date TIMESTAMP,
    refund_reason VARCHAR(500),
    notes TEXT,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_billing_accounts_warehouse_partner ON billing_accounts(warehouse_partner_id);
CREATE INDEX idx_billing_accounts_status ON billing_accounts(account_status);
CREATE INDEX idx_billing_accounts_email ON billing_accounts(billing_email);
CREATE INDEX idx_billing_accounts_next_billing ON billing_accounts(next_billing_date);

CREATE INDEX idx_subscriptions_billing_account ON subscriptions(billing_account_id);
CREATE INDEX idx_subscriptions_warehouse ON subscriptions(warehouse_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(subscription_status);
CREATE INDEX idx_subscriptions_next_billing ON subscriptions(next_billing_date);

CREATE INDEX idx_usage_subscription ON subscription_usage(subscription_id);
CREATE INDEX idx_usage_period ON subscription_usage(billing_period_start, billing_period_end);

CREATE INDEX idx_invoices_billing_account ON invoices(billing_account_id);
CREATE INDEX idx_invoices_number ON invoices(invoice_number);
CREATE INDEX idx_invoices_status ON invoices(invoice_status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_invoices_period ON invoices(billing_period_start, billing_period_end);

CREATE INDEX idx_line_items_invoice ON invoice_line_items(invoice_id);
CREATE INDEX idx_line_items_subscription ON invoice_line_items(subscription_id);

CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_billing_account ON payments(billing_account_id);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_payments_reference ON payments(payment_reference);

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_billing_accounts_updated_at BEFORE UPDATE
    ON billing_accounts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_subscriptions_updated_at BEFORE UPDATE
    ON subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE
    ON invoices FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE
    ON payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data for development
INSERT INTO billing_accounts (
    id, warehouse_partner_id, account_name, company_name, billing_email,
    billing_address, city, state_province, postal_code, country,
    account_status, preferred_currency, payment_terms_days, billing_cycle_day
) VALUES (
    uuid_generate_v4(), uuid_generate_v4(), 'Demo Warehouse Corp',
    'Demo Warehouse Corporation', 'billing@demowarehouse.com',
    '123 Warehouse St', 'Demo City', 'Demo State', '12345', 'United States',
    'ACTIVE', 'USD', 30, 1
);