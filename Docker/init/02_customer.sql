-- ==========================================================
-- customer_db (Customer Profile & Address Card Service)
-- ==========================================================
CREATE SCHEMA IF NOT EXISTS customer_db;
SET search_path TO customer_db;
-- ==========================================================
-- function
-- ==========================================================
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';
-- ==========================================================
-- table
-- ==========================================================
-- 2.CUSTOMERS
CREATE TABLE CUSTOMERS (
                           customer_id BIGSERIAL PRIMARY KEY,
                           user_id VARCHAR(36) UNIQUE,
                           email VARCHAR(255) UNIQUE,
                           full_name VARCHAR(255) NOT NULL,
                           gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
                           dob DATE,
                           phone_number VARCHAR(20),
                           deleted_at TIMESTAMP DEFAULT NULL,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3.ADDRESS_CARD
CREATE TABLE ADDRESS_CARD (
                              address_id SERIAL PRIMARY KEY,
                              customer_id BIGINT NOT NULL,
                              receiver_name VARCHAR(255) NOT NULL,
                              receiver_phone VARCHAR(20) NOT NULL,
                              province_code VARCHAR(20) NOT NULL,
                              ward_code VARCHAR(20) NOT NULL,
                              address_detail TEXT NOT NULL,
                              is_default BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
-- Function
-- ==========================================================

CREATE OR REPLACE FUNCTION handle_default_address()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_default = TRUE THEN
        UPDATE address_card
        SET is_default = FALSE
        WHERE customer_id = NEW.customer_id
          AND address_id <> NEW.address_id;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';


-- ==========================================================
-- TRIGGERS
-- ==========================================================
CREATE TRIGGER trg_unique_default_address
    BEFORE INSERT OR UPDATE OF is_default ON address_card
    FOR EACH ROW
    WHEN (NEW.is_default = TRUE)
EXECUTE FUNCTION handle_default_address();
CREATE TRIGGER set_timestamp_customers BEFORE UPDATE ON CUSTOMERS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
CREATE TRIGGER set_timestamp_address_card BEFORE UPDATE ON address_card FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
