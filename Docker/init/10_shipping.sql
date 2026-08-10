CREATE SCHEMA IF NOT EXISTS shipping_db;
SET search_path TO shipping_db;
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END; $$ language 'plpgsql';
CREATE TABLE warehouse_partner (
                                   id SERIAL PRIMARY KEY,
                                   warehouse_code VARCHAR(50) NOT NULL ,
                                   partner_name VARCHAR(20) NOT NULL, -- 'GHN','VTP','GHTK'
                                   shop_id INT NOT NULL,
                                   partner_token TEXT NOT NULL,
                                   expires_at TIMESTAMP,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE (warehouse_code, partner_name)
);
CREATE TABLE shipment(
                         id BIGSERIAL PRIMARY KEY,
                         shipment_code varchar(50) NOT NULL,
                         order_code varchar(50) NOT NULL ,
                         warehouse_code VARCHAR,

                         delivery_method VARCHAR(20) NOT NULL CHECK (delivery_method IN ('SHIPPING','GHN','VTP', 'PICKUP')),
                         status VARCHAR(50) NOT NULL DEFAULT 'ON_HOLD'
                             CHECK (status IN ('ON_HOLD', 'READY_TO_PICK', 'PACKED', 'DELIVERING', 'DELIVERED', 'CANCELLED', 'RETURNED')),
                         scheduled_date DATE,     -- Ngày dự kiến xử lý (ghép từ pre_release_date)

                         total_cod_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
                         actual_shipping_cost DECIMAL(19, 4) NOT NULL DEFAULT 0  ,
                         tracking_number VARCHAR(100),

                         receiver_name VARCHAR(255) NOT NULL,
                         receiver_phone VARCHAR(20) NOT NULL,
                         receiver_email VARCHAR(255) NOT NULL ,
                         province_code VARCHAR(20),
                         ward_code VARCHAR(20),
                         address_detail TEXT,
                         customer_note TEXT,

                         total_weight INT DEFAULT 0,
                         length INT DEFAULT 0,
                         width INT DEFAULT 0,
                         height INT DEFAULT 0,


                         partner_province_code VARCHAR(20),
                         partner_district_code VARCHAR(20),
                         partner_ward_code VARCHAR(20),

                         confirmed_by BIGINT,                 -- Staff ID
                         confirmed_at TIMESTAMP WITH TIME ZONE,

                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE shipment_items(
                               id BIGSERIAL PRIMARY KEY,
                               shipment_id BIGINT NOT NULL,
                               sku_code VARCHAR(50) NOT NULL,
                               product_name VARCHAR(255) NOT NULL,
                               quantity INT NOT NULL CHECK (quantity > 0)
);

CREATE TABLE outbox (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        aggregate_id UUID,
                        event_type VARCHAR(50) NOT NULL,
                        payload JSONB NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        retry_count INT DEFAULT 0,
                        last_attempt_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE inbox (
                       event_id UUID PRIMARY KEY,
                       event_type VARCHAR(50),
                       payload JSONB NOT NULL,
                       status VARCHAR(20) DEFAULT 'PENDING',
                       error_log TEXT,
                       processed_at TIMESTAMP,
                       received_at TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_shipment_order_code ON shipment(order_code);
CREATE INDEX idx_shipment_status ON shipment(status);
CREATE INDEX idx_shipment_tracking ON shipment(tracking_number);
CREATE INDEX idx_shipment_items_sid ON shipment_items(shipment_id);
CREATE INDEX idx_warehouse_partner_wcode ON warehouse_partner(warehouse_code);


CREATE TRIGGER trg_warehouse_partner_upd BEFORE UPDATE ON warehouse_partner FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_shipment_upd BEFORE UPDATE ON shipment FOR EACH ROW EXECUTE FUNCTION update_timestamp();
