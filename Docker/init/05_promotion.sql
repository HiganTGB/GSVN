CREATE SCHEMA IF NOT EXISTS promotion_db;
SET search_path TO promotion_db;

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

-- Messaging
CREATE TABLE outbox (
                        id  VARCHAR(50) PRIMARY KEY,
                        aggregate_id  VARCHAR(50),
                        event_type VARCHAR(50) NOT NULL,
                        payload JSONB NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        retry_count INT DEFAULT 0,
                        last_attempt_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE inbox (
                       event_id  VARCHAR(50) PRIMARY KEY,
                       event_type VARCHAR(50),
                       payload JSONB NOT NULL,
                       status VARCHAR(20) DEFAULT 'PENDING',
                       error_log TEXT,
                       processed_at TIMESTAMP,
                       received_at TIMESTAMP DEFAULT NOW()
);

-- Vouchers
CREATE TABLE vouchers (
                          id SERIAL PRIMARY KEY,
                          voucher_code VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL ,
                          discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
                          discount_value DECIMAL(19, 4) NOT NULL,
                          max_discount_amount DECIMAL(19, 4) NOT NULL,
                          min_order_value DECIMAL(19, 4) NOT NULL DEFAULT 0,
                          usage_limit INT,
                          limit_per_customer INT DEFAULT 1,
                          used_count INT DEFAULT 0,
                          start_date TIMESTAMP WITH TIME ZONE NOT NULL,
                          end_date TIMESTAMP WITH TIME ZONE NOT NULL,
                          is_active BOOLEAN DEFAULT TRUE,
                          deleted_at TIMESTAMP DEFAULT NULL,
                          version INT default 0 NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- History
CREATE TABLE voucher_usage_history (
                                       id BIGSERIAL PRIMARY KEY,
                                       voucher_id INT NOT NULL,
                                       customer_id BIGINT,
                                       guest_email VARCHAR(255),
                                       order_id BIGINT NOT NULL,
                                       saga_id VARCHAR(50),
                                       used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_vouchers_active_code ON vouchers(voucher_code) WHERE is_active = TRUE;
CREATE INDEX idx_usage_order_id ON voucher_usage_history(order_id);
CREATE INDEX idx_usage_saga_id ON voucher_usage_history(saga_id);

-- Trigger
CREATE TRIGGER trg_vouchers_updated_at
    BEFORE UPDATE ON vouchers FOR EACH ROW EXECUTE FUNCTION update_timestamp();


-- Seed Data
INSERT INTO vouchers (voucher_code, name, discount_type, discount_value, min_order_value, usage_limit, start_date, end_date,max_discount_amount)
VALUES
    ('TANTHU2026', 'Voucher Tân Thủ', 'FIXED_AMOUNT', 50000.00, 500000.00, 100, NOW(), NOW() + INTERVAL '30 days',100000),
    ('SUMMER2026', 'Flash Sale Hè', 'PERCENTAGE', 15.00, 1000000.00, 50, NOW(), NOW() + INTERVAL '7 days',10000)
    ON CONFLICT (voucher_code) DO NOTHING;

SELECT setval(pg_get_serial_sequence('vouchers', 'id'), COALESCE(MAX(id), 1)) FROM vouchers;