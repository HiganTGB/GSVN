CREATE SCHEMA IF NOT EXISTS order_db;
SET search_path TO order_db;

CREATE OR REPLACE FUNCTION update_timestamp() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = NOW(); RETURN NEW; END; $$ language 'plpgsql';

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_code VARCHAR(50) UNIQUE NOT NULL,
    --                  Mã khách hàng
                        customer_id BIGINT,
    --                  Cửa hàng phát sinh giao dịch
                        warehouse_code VARCHAR(50), -- NUll nghĩa là internet


    --                  Thông tin giao hàng
                        receiver_name VARCHAR(255) NOT NULL,
                        receiver_phone VARCHAR(20) NOT NULL,
                        receiver_email VARCHAR(255),
                        province_code VARCHAR(20),
                        ward_code VARCHAR(20),
                        address_detail TEXT,
                        customer_note TEXT,
                        payment_method VARCHAR(20) NOT NULL, --CASH,BANK,VNPAY
                        delivery_method VARCHAR(20) NOT NULL CHECK (delivery_method IN ('SHIPPING', 'PICKUP')),

                        payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID' -- Trạng thái thanh toán (nếu amount_paid < final_amount thì partially_paid)
                            CHECK (payment_status IN ('UNPAID', 'PARTIALLY_PAID', 'FULLY_PAID')),

                        total_amount DECIMAL(19, 4) NOT NULL,    -- Tổng tiền hàng (chưa ship/giảm)
                        discount_amount DECIMAL(19, 4) DEFAULT 0,
                        final_amount DECIMAL(19, 4) NOT NULL,


                        total_required_now DECIMAL(19, 4) NOT NULL, -- Số tiền cần trả để Confirm (Cọc + Món trả thẳng + Ship)
                        amount_paid DECIMAL(19, 4) DEFAULT 0,
                        voucher_code VARCHAR(50),

                        order_status VARCHAR(30) DEFAULT 'PENDING'
                            CHECK (order_status IN ('PENDING','VALIDATED','AWAITING', 'CONFIRMED', 'PROCESSING', 'COMPLETED', 'CANCELLED')),

    -- PENDING (chờ step xong)
    -- VALIDATED (Chờ thanh toán)
    -- Awaiting(chờ nhân viên confirmed)
    -- Confirmed( đã confirmed)
    -- Processing (khi bắt đầu giao 1 phần)
    -- completed (giao hết toàn bộ)
    -- cancelled (hệ thống huỷ đơn)
                        current_saga_id VARCHAR(50),
                        client_ip VARCHAR(255),
                        check_out_url TEXT,
                        reference_id VARCHAR(255),
    -- Vận hành
                        staff_id BIGINT,
                        confirmed_by BIGINT,
                        confirmed_at TIMESTAMP WITH TIME ZONE,
                        staff_note TEXT,

                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT check_delivery_address
                            CHECK (
                                (delivery_method = 'PICKUP' AND warehouse_code IS NOT NULL) OR
                                (delivery_method ='SHIPPING'AND province_code IS NOT NULL AND ward_code IS NOT NULL AND address_detail IS NOT NULL)
                                )
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id INT,
                             sku_code VARCHAR(50) NOT NULL,
                             product_name VARCHAR(500),
                             image_url VARCHAR(512),
                             scheduled_date DATE,

                             quantity INT NOT NULL CHECK (quantity > 0),
                             unit_price DECIMAL(19, 4) DEFAULT 0  NOT NULL,
                             sub_price DECIMAL(19, 4) DEFAULT 0 NOT NULL,

                             is_preorder BOOLEAN NOT NULL DEFAULT FALSE,
                             is_deposit_applied BOOLEAN NOT NULL DEFAULT FALSE,
                             applied_deposit_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Bảng quản lý trạng thái giao dịch phân tán (Saga Pattern)
CREATE TABLE order_saga_instances (
                                      saga_id VARCHAR(50) PRIMARY KEY DEFAULT gen_random_uuid(),
                                      order_id BIGINT NOT NULL,
                                      current_step VARCHAR(50) NOT NULL,
    -- START -> SKU_CHECKED | FAILED
    --> INV_RESERVED | FAILED | RELEASING_INV
    --> VOUCHER_APPLIED | VOUCHER_FAILED | RELEASING_VOUCHER
    --> PAYMENT_GENERATED | SUCCESS | COMPENSATED
                                      payload JSONB,
                                      status VARCHAR(20) DEFAULT 'STARTED', -- STARTED, COMPENSATING, FAILED, SUCCEEDED
                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox (
                        id VARCHAR(50) PRIMARY KEY,
                        aggregate_id VARCHAR(50),
                        event_type  VARCHAR(50) NOT NULL,
                        payload JSONB NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        retry_count INT DEFAULT 0,
                        last_attempt_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE inbox (
                       event_id VARCHAR(50) PRIMARY KEY,
                       event_type VARCHAR(50),
                       payload JSONB NOT NULL,
                       status VARCHAR(20) DEFAULT 'PENDING',
                       error_log TEXT,
                       processed_at TIMESTAMP,
                       received_at TIMESTAMP DEFAULT NOW()
);

-- Index
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_code ON orders(order_code);
CREATE INDEX idx_saga_order_id ON order_saga_instances(order_id);

-- Trigger
CREATE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE PROCEDURE update_timestamp();

CREATE TRIGGER trg_saga_updated_at
    BEFORE UPDATE ON order_saga_instances
    FOR EACH ROW EXECUTE PROCEDURE update_timestamp();

-- Mã đơn hàng tự động
CREATE SEQUENCE IF NOT EXISTS order_code_num_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    CACHE 20;

CREATE OR REPLACE FUNCTION generate_order_code(p_prefix VARCHAR)
    RETURNS VARCHAR AS $$
DECLARE
    new_val BIGINT;
    date_part VARCHAR;
BEGIN
    new_val := nextval('order_code_num_seq');
    date_part := TO_CHAR(NOW(), 'YYMMDD');
    RETURN p_prefix || '-' || date_part || '-' || LPAD(new_val::text, 6, '0');
END;$$ LANGUAGE plpgsql;

