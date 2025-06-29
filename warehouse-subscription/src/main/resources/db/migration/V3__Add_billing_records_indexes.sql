-- Additional indexes for performance optimization
-- Version 3.0.0 - Billing and performance optimization

-- Add composite indexes for billing queries
CREATE INDEX idx_billing_records_subscription_period 
ON billing_records(subscription_id, billing_period_start, billing_period_end);

CREATE INDEX idx_billing_records_due_date 
ON billing_records(due_date) WHERE status IN ('PENDING', 'OVERDUE');

CREATE INDEX idx_billing_records_status_amount 
ON billing_records(status, total_amount);

-- Add indexes for usage tracking queries
CREATE INDEX idx_usage_records_subscription_period 
ON usage_records(subscription_id, usage_date);

CREATE INDEX idx_usage_records_type_amount 
ON usage_records(usage_type, usage_amount);

-- Add indexes for subscription analytics
CREATE INDEX idx_subscriptions_created_plan 
ON warehouse_subscriptions(created_at, plan) WHERE status = 'ACTIVE';

CREATE INDEX idx_subscriptions_status_billing 
ON warehouse_subscriptions(status, next_billing_date);

-- Add partial indexes for performance
CREATE INDEX idx_active_subscriptions_billing 
ON warehouse_subscriptions(next_billing_date) 
WHERE status = 'ACTIVE' AND next_billing_date IS NOT NULL;

CREATE INDEX idx_overdue_subscriptions 
ON warehouse_subscriptions(status, updated_at) 
WHERE status = 'OVERDUE';

-- Add indexes for warehouse queries
CREATE INDEX idx_subscriptions_warehouse_status 
ON warehouse_subscriptions(warehouse_id, status);

-- Add function for calculating MRR (Monthly Recurring Revenue)
CREATE OR REPLACE FUNCTION calculate_mrr(start_date DATE, end_date DATE)
RETURNS DECIMAL AS $$
DECLARE
    mrr_amount DECIMAL;
BEGIN
    SELECT COALESCE(SUM(
        CASE 
            WHEN plan = 'TRIAL' THEN 0
            WHEN plan = 'BASIC' THEN 99.00
            WHEN plan = 'STANDARD' THEN 299.00
            WHEN plan = 'PREMIUM' THEN 599.00
            WHEN plan = 'ENTERPRISE' THEN 1299.00
            ELSE 0
        END
    ), 0) INTO mrr_amount
    FROM warehouse_subscriptions 
    WHERE status = 'ACTIVE' 
    AND created_at >= start_date 
    AND created_at <= end_date;
    
    RETURN mrr_amount;
END;
$$ LANGUAGE plpgsql;

-- Add function for calculating churn rate
CREATE OR REPLACE FUNCTION calculate_churn_rate(start_date DATE, end_date DATE)
RETURNS DECIMAL AS $$
DECLARE
    total_at_start INTEGER;
    churned_count INTEGER;
    churn_rate DECIMAL;
BEGIN
    -- Count active subscriptions at start of period
    SELECT COUNT(*) INTO total_at_start
    FROM warehouse_subscriptions 
    WHERE status = 'ACTIVE' 
    AND created_at <= start_date;
    
    -- Count subscriptions that churned during period
    SELECT COUNT(*) INTO churned_count
    FROM warehouse_subscriptions 
    WHERE status IN ('CANCELLED', 'EXPIRED')
    AND updated_at >= start_date 
    AND updated_at <= end_date;
    
    -- Calculate churn rate
    IF total_at_start > 0 THEN
        churn_rate := (churned_count::DECIMAL / total_at_start::DECIMAL) * 100;
    ELSE
        churn_rate := 0;
    END IF;
    
    RETURN churn_rate;
END;
$$ LANGUAGE plpgsql;

-- Add trigger for automatic usage aggregation
CREATE OR REPLACE FUNCTION aggregate_daily_usage()
RETURNS TRIGGER AS $$
BEGIN
    -- Update daily usage aggregation table
    INSERT INTO daily_usage_aggregates (
        subscription_id, 
        usage_date, 
        usage_type, 
        total_amount,
        created_at
    )
    VALUES (
        NEW.subscription_id,
        DATE(NEW.usage_date),
        NEW.usage_type,
        NEW.usage_amount,
        CURRENT_TIMESTAMP
    )
    ON CONFLICT (subscription_id, usage_date, usage_type)
    DO UPDATE SET 
        total_amount = daily_usage_aggregates.total_amount + EXCLUDED.total_amount,
        updated_at = CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create daily usage aggregates table for performance
CREATE TABLE IF NOT EXISTS daily_usage_aggregates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subscription_id UUID NOT NULL REFERENCES warehouse_subscriptions(id),
    usage_date DATE NOT NULL,
    usage_type VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(subscription_id, usage_date, usage_type)
);

-- Add indexes for aggregates table
CREATE INDEX idx_daily_usage_aggregates_subscription 
ON daily_usage_aggregates(subscription_id, usage_date);

CREATE INDEX idx_daily_usage_aggregates_date_type 
ON daily_usage_aggregates(usage_date, usage_type);

-- Create trigger for usage records
CREATE TRIGGER trigger_aggregate_usage
    AFTER INSERT ON usage_records
    FOR EACH ROW
    EXECUTE FUNCTION aggregate_daily_usage();

-- Add trigger for automatic billing record creation
CREATE OR REPLACE FUNCTION create_billing_record_on_subscription()
RETURNS TRIGGER AS $$
BEGIN
    -- Create initial billing record for new active subscriptions
    IF NEW.status = 'ACTIVE' AND (OLD IS NULL OR OLD.status != 'ACTIVE') THEN
        INSERT INTO billing_records (
            subscription_id,
            billing_period_start,
            billing_period_end,
            amount_due,
            total_amount,
            status,
            due_date,
            created_at
        ) VALUES (
            NEW.id,
            CURRENT_DATE,
            CURRENT_DATE + INTERVAL '1 month',
            NEW.monthly_rate,
            NEW.monthly_rate,
            'PENDING',
            CURRENT_DATE + INTERVAL '30 days',
            CURRENT_TIMESTAMP
        );
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for subscription billing
CREATE TRIGGER trigger_create_billing_record
    AFTER INSERT OR UPDATE ON warehouse_subscriptions
    FOR EACH ROW
    EXECUTE FUNCTION create_billing_record_on_subscription();

-- Add materialized view for subscription analytics
CREATE MATERIALIZED VIEW subscription_analytics AS
SELECT 
    s.plan,
    s.status,
    COUNT(*) as subscription_count,
    AVG(s.monthly_rate) as avg_monthly_rate,
    SUM(s.monthly_rate) as total_mrr,
    AVG(EXTRACT(DAYS FROM (CURRENT_DATE - s.created_at::date))) as avg_age_days,
    COUNT(*) FILTER (WHERE s.created_at >= CURRENT_DATE - INTERVAL '30 days') as new_subscriptions_30d,
    COUNT(*) FILTER (WHERE s.status = 'CANCELLED' AND s.updated_at >= CURRENT_DATE - INTERVAL '30 days') as churned_30d
FROM warehouse_subscriptions s
GROUP BY s.plan, s.status;

-- Create index on materialized view
CREATE INDEX idx_subscription_analytics_plan_status 
ON subscription_analytics(plan, status);

-- Add function to refresh analytics
CREATE OR REPLACE FUNCTION refresh_subscription_analytics()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW subscription_analytics;
END;
$$ LANGUAGE plpgsql;

-- Add comments for documentation
COMMENT ON TABLE daily_usage_aggregates IS 'Daily usage aggregates for improved query performance';
COMMENT ON MATERIALIZED VIEW subscription_analytics IS 'Pre-calculated subscription analytics for reporting';
COMMENT ON FUNCTION calculate_mrr IS 'Calculate Monthly Recurring Revenue for a date range';
COMMENT ON FUNCTION calculate_churn_rate IS 'Calculate churn rate for a date range';

-- Update table statistics
ANALYZE warehouse_subscriptions;
ANALYZE usage_records;
ANALYZE billing_records;
ANALYZE daily_usage_aggregates;