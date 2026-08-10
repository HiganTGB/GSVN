CREATE SCHEMA IF NOT EXISTS product_db;
SET search_path TO product_db;

CREATE OR REPLACE FUNCTION update_timestamp() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = NOW(); RETURN NEW; END; $$ language 'plpgsql';

CREATE TABLE CATEGORY (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(500) NOT NULL UNIQUE,
                          parent_category_id INT REFERENCES CATEGORY(id),
                          description TEXT,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE BRAND (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(500) NOT NULL UNIQUE,
                       description TEXT,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PRODUCT (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(500) NOT NULL UNIQUE,
                         category_id INT NOT NULL REFERENCES CATEGORY(id),
                         brand_id INT NOT NULL REFERENCES BRAND(id),
                         description TEXT,
                         release_date VARCHAR(50),
                         image_url VARCHAR(512),
                         gallery_images JSONB DEFAULT '[]'::jsonb,
                         sale_status VARCHAR(20) CHECK (sale_status IN ('RUMOR','COMING_SOON','PREORDER_OPEN','PREORDER_CLOSED', 'AVAILABLE')),
                         deleted_at TIMESTAMP,
                         is_active BOOLEAN DEFAULT TRUE,
                         pre_name VARCHAR(255),
                         pre_is_active BOOLEAN DEFAULT FALSE,
                         pre_start_at TIMESTAMP WITH TIME ZONE,
                         pre_end_at TIMESTAMP WITH TIME ZONE,
                         pre_release_date DATE,

                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PRODUCT_PRE_HISTORY (
                                     id SERIAL PRIMARY KEY,
                                     product_id INT NOT NULL,
                                     pre_name VARCHAR(255),
                                     pre_start_at TIMESTAMP WITH TIME ZONE,
                                     pre_end_at TIMESTAMP WITH TIME ZONE,
                                     pre_release_date DATE,
                                     total_orders_achieved INT DEFAULT 0,
                                     sku_prices_snapshot JSONB DEFAULT '[]'::jsonb,
                                     archived_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SKU (
                     id BIGSERIAL PRIMARY KEY,
                     product_id INT NOT NULL,
                     sku_code VARCHAR(50) UNIQUE NOT NULL,
                     import_price DECIMAL(19, 4) NOT NULL DEFAULT 0,
                     selling_price DECIMAL(19, 4) NOT NULL DEFAULT 0,

                     pre_price DECIMAL(19, 4),
                     pre_deposit_amount DECIMAL(19, 4) DEFAULT 0,
                     pre_per_qty INT DEFAULT 3,
                     weight_gram INT DEFAULT 0,
                     dimensions_cm JSONB DEFAULT '{"l":0.0, "w":0.0, "h":0.0}'::jsonb,
                     is_active BOOLEAN NOT NULL DEFAULT TRUE,
                     deleted_at TIMESTAMP,
                     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE PRODUCT_VARIANT (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id INT NOT NULL,
                                 name VARCHAR(50) NOT NULL,
                                 UNIQUE(product_id, name)
);

CREATE TABLE VARIANT_OPTION (
                                id BIGSERIAL PRIMARY KEY,
                                variant_id BIGINT NOT NULL,
                                name VARCHAR(50) NOT NULL,
                                UNIQUE(variant_id, name)
);

CREATE TABLE SKU_VARIANT_MAP (
                                 sku_id BIGSERIAL NOT NULL,
                                 option_id BIGINT NOT NULL,
                                 PRIMARY KEY (sku_id, option_id)
);

CREATE TABLE outbox (
                        id VARCHAR(50)PRIMARY KEY DEFAULT gen_random_uuid(),
                        aggregate_id VARCHAR(50),
                        event_type VARCHAR(50) NOT NULL,
                        payload JSONB NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        retry_count INT DEFAULT 0,
                        last_attempt_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE inbox (
                       event_id VARCHAR(50) UNIQUE,
                       event_type VARCHAR(50),
                       payload JSONB NOT NULL,
                       status VARCHAR(20) DEFAULT 'PENDING',
                       error_log TEXT,
                       processed_at TIMESTAMP,
                       received_at TIMESTAMP DEFAULT NOW()
);





CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_brand ON product(brand_id);
CREATE INDEX idx_sku_product_id ON sku(product_id);
CREATE INDEX idx_product_pre_dates ON product(pre_start_at, pre_end_at) WHERE pre_is_active = TRUE;
CREATE INDEX idx_variant_product ON product_variant(product_id);
CREATE INDEX idx_option_variant ON variant_option(variant_id);


CREATE TRIGGER trg_category_updated_at BEFORE UPDATE ON category FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_brand_updated_at BEFORE UPDATE ON brand FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_product_updated_at BEFORE UPDATE ON product FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_sku_updated_at BEFORE UPDATE ON sku FOR EACH ROW EXECUTE FUNCTION update_timestamp();



CREATE OR REPLACE FUNCTION sync_product_sale_status()
    RETURNS TRIGGER AS $$
BEGIN

    IF NEW.pre_is_active = TRUE THEN
        IF NEW.pre_start_at IS NULL OR NEW.pre_end_at IS NOT NULL THEN
            IF NOW() < NEW.pre_start_at THEN
                NEW.sale_status = 'COMING_SOON';
            ELSIF NOW() BETWEEN NEW.pre_start_at AND NEW.pre_end_at THEN
                NEW.sale_status = 'PREORDER_OPEN';
            ELSE
                NEW.sale_status = 'PREORDER_CLOSED';
            END IF;
        END IF;
    ELSE
        IF OLD.sale_status IN ('COMING_SOON', 'PREORDER_OPEN', 'PREORDER_CLOSED') THEN
            NEW.sale_status = 'AVAILABLE';

        ELSE
            NEW.sale_status = COALESCE(OLD.sale_status, 'RUMOR');
        END IF;
    END IF;

    RETURN NEW;
END; $$ LANGUAGE plpgsql;




CREATE TRIGGER trg_sync_sale_status
    BEFORE INSERT OR UPDATE OF pre_is_active, pre_start_at, pre_end_at ON product
    FOR EACH ROW EXECUTE FUNCTION sync_product_sale_status();
