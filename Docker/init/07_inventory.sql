CREATE SCHEMA IF NOT EXISTS inventory_db;
SET search_path TO inventory_db;

-- 1. Function cập nhật updated_at
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END; $$ language 'plpgsql';

-- 2. Warehouse & Suppliers
CREATE TABLE warehouses (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            code VARCHAR(50) UNIQUE NOT NULL,
                            staff_id BIGINT NOT NULL,
                            is_active BOOLEAN DEFAULT TRUE,
                            contact_name VARCHAR(255),
                            contact_phone VARCHAR(20),
                            address_detail TEXT,
                            province_code VARCHAR(20),
                            ward_code VARCHAR(20),
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suppliers (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           tax_code VARCHAR(50) UNIQUE NOT NULL,
                           contact_name VARCHAR(255),
                           phone VARCHAR(20),
                           email VARCHAR(255),
                           is_active BOOLEAN NOT NULL DEFAULT TRUE,
                           note TEXT,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Stock Management
-- Ghi chú logic:
-- nếu là available (pickup , có warehouse) : giảm physical_stock và reserver_stock
-- nếu là available (online) : tăng global
-- nếu là preorder:
-- nếu là pickup tăng reserver_stock và pre_current_orders
-- nếu là preorder tăng global và pre_current_orders
CREATE TABLE sku_stock (
                           sku_id BIGINT NOT NULL,
                           sku_code VARCHAR(50) NOT NULL ,
                           warehouse_id INT NOT NULL,
                           physical_stock INT NOT NULL DEFAULT 0 CHECK (physical_stock >= 0),
                           reserved_stock INT NOT NULL DEFAULT 0 CHECK (reserved_stock >= 0),
                           version INT DEFAULT 0,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (sku_id, warehouse_id)
);

-- Ghi chú logic:
-- Sau khi order xác nhận , tạo shipment tương ứng (gắn warehouse)
-- shipment khi tạo nếu là pickup --> bỏ qua
-- shipment khi tao nếu là preorder ->Chỉ định về kho , giảm global và tăng reserved_stock
CREATE TABLE sku_global (
                            sku_id BIGINT NOT NULL PRIMARY KEY,
                            sku_code VARCHAR(50) NOT NULL ,
                            pre_limit_quantity INT NOT NULL DEFAULT 0,
                            pre_current_orders  INT NOT NULL DEFAULT 0 CHECK (pre_current_orders >= 0),
                            reserved_global INT NOT NULL DEFAULT 0 CHECK (reserved_global >= 0),
                            version INT DEFAULT 0,
                            updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT chk_preorder_limit CHECK (pre_limit_quantity = 0 OR pre_current_orders <= pre_limit_quantity)
);

-- 4. Receipts & Items (Inbound/Outbound)
CREATE TABLE inbound_receipts (
                                  id BIGSERIAL PRIMARY KEY,
                                  warehouse_id INT NOT NULL,
                                  receipt_code VARCHAR(50) UNIQUE NOT NULL,
                                  supplier_id INT,
                                  source_outbound_code VARCHAR(50),
                                  type VARCHAR(20) NOT NULL, -- 'TRANSFER', 'ADJUST'
                                  staff_id BIGINT,
                                  note TEXT,
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- TODO : add supplier_id,
CREATE TABLE inbound_items (
                               id BIGSERIAL PRIMARY KEY,
                               inbound_id BIGINT NOT NULL,
                               sku_id BIGINT NOT NULL,
                               sku_code VARCHAR(50),
                               product_name VARCHAR(255),
                               quantity INT NOT NULL CHECK (quantity > 0),
                               import_price DECIMAL(19, 4) NOT NULL DEFAULT 0,
                               line_total DECIMAL(19, 4) GENERATED ALWAYS AS (quantity * import_price) STORED
);
CREATE TABLE outbound_receipts (
                                   id BIGSERIAL PRIMARY KEY,
                                   warehouse_id INT NOT NULL,
                                   receipt_code VARCHAR(50) UNIQUE NOT NULL,
                                   staff_id BIGINT,
                                   type VARCHAR(20) NOT NULL, -- 'TRANSFER', 'ADJUST'
                                   external_id VARCHAR(50), -- Target or orderCode
                                   note TEXT,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbound_items (
                                id BIGSERIAL PRIMARY KEY,
                                outbound_id BIGINT NOT NULL,
                                sku_id BIGINT NOT NULL,
                                sku_code VARCHAR(50),
                                product_name VARCHAR(255),
                                quantity INT NOT NULL CHECK (quantity > 0)
);

-- 5. Messaging (Outbox/Inbox)
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
                       payload JSONB NOT NULL,
                       status VARCHAR(20) DEFAULT 'PENDING',
                       error_log TEXT,
                       processed_at TIMESTAMP,
                       received_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE stock_logs (
                            id SERIAL PRIMARY KEY,
                            sku_id BIGINT NOT NULL,
                            sku_code VARCHAR(50) NOT NULL,
                            warehouse_id INT,
                            change_physical INT DEFAULT 0,
                            change_reserved INT DEFAULT 0,
                            type VARCHAR(50) NOT NULL,
                            reference_id VARCHAR(50),
                            note TEXT,
                            staff_id BIGINT,
                            saga_id VARCHAR(50) ,
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS inbound_code_num_seq START WITH 1;
CREATE SEQUENCE IF NOT EXISTS outbound_code_num_seq START WITH 1;

CREATE OR REPLACE FUNCTION generate_inbound_code(p_prefix VARCHAR) RETURNS VARCHAR AS $$
BEGIN
    RETURN p_prefix || '-' || TO_CHAR(NOW(), 'YYMMDD') || '-' || LPAD(nextval('inbound_code_num_seq')::text, 6, '0');
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generate_outbound_code(p_prefix VARCHAR) RETURNS VARCHAR AS $$
BEGIN
    RETURN p_prefix || '-' || TO_CHAR(NOW(), 'YYMMDD') || '-' || LPAD(nextval('outbound_code_num_seq')::text, 6, '0');
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trigger_set_inbound_code()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.receipt_code IS NULL OR NEW.receipt_code = '' THEN
        NEW.receipt_code := generate_inbound_code(NEW.type);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trg_inbound_code
    BEFORE INSERT ON inbound_receipts
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_inbound_code();

CREATE OR REPLACE FUNCTION trigger_set_outbound_code()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.receipt_code IS NULL OR NEW.receipt_code = '' THEN
        NEW.receipt_code := generate_outbound_code(NEW.type);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trg_receipt_code
    BEFORE INSERT ON outbound_receipts
    FOR EACH ROW
EXECUTE FUNCTION trigger_set_outbound_code();

-- 8. Triggers
CREATE TRIGGER trg_warehouses_upd BEFORE UPDATE ON warehouses FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_suppliers_upd BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_sku_stock_upd BEFORE UPDATE ON sku_stock FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_sku_global_upd BEFORE UPDATE ON sku_global FOR EACH ROW EXECUTE FUNCTION update_timestamp();
