CREATE SCHEMA IF NOT EXISTS payment_db;
SET search_path TO payment_db;
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';
CREATE TABLE payment_transactions (
                                      id BIGSERIAL PRIMARY KEY,
                                      shipment_code VARCHAR(50) ,
                                      order_code VARCHAR(50) NOT NULL,


                                      reference_id VARCHAR(100) UNIQUE NOT NULL,
                                      confirmed_by BIGINT,
                                      confirmed_at TIMESTAMP,

                                      external_transaction_id VARCHAR(100),

                                      provider VARCHAR(20) NOT NULL,         -- 'VNPAY', 'MOMO', 'BANK_TRANSFER', 'CASH' , 'COD'
                                      payment_method VARCHAR(50) NOT NULL ,            -- 'ATM', 'QR_CODE', 'CREDIT_CARD'

                                      amount DECIMAL(19, 4) NOT NULL,
                                      currency VARCHAR(10) NOT NULL DEFAULT 'VND',


                                      payment_type VARCHAR(20) NOT NULL
                                          CHECK (payment_type IN ('DEPOSIT', 'BALANCE', 'FULL', 'REFUND', 'ADJUSTMENT')),

                                      status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                          CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED')),

                                      checkout_url TEXT,
                                      expires_at TIMESTAMP WITH TIME ZONE,


                                      provider_response JSONB DEFAULT '{}'::jsonb,
                                      note TEXT,

                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox (
                        id VARCHAR(50) PRIMARY KEY,
                        aggregate_id VARCHAR(50),
                        event_type VARCHAR(50) NOT NULL,
                        payload JSONB NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        retry_count INT DEFAULT 0,
                        last_attempt_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE inbox (
                       event_id VARCHAR(50) PRIMARY KEY,
                       event_type VARCHAR(50),
                       payload JSONB NOT NULL,      -- sku success, sku failed , inv_succes, inv_failed ,
                       status VARCHAR(20) DEFAULT 'PENDING',
                       error_log TEXT,
                       processed_at TIMESTAMP,
                       received_at TIMESTAMP DEFAULT NOW()
);




CREATE INDEX idx_pay_order_code ON payment_transactions(order_code);
CREATE INDEX idx_pay_external_id ON payment_transactions(external_transaction_id);
CREATE INDEX idx_pay_status ON payment_transactions(status);
CREATE INDEX idx_pay_reference ON payment_transactions(reference_id);

CREATE TRIGGER trg_update_timestamp_payment
    BEFORE UPDATE ON payment_transactions
    FOR EACH ROW EXECUTE PROCEDURE update_timestamp();

