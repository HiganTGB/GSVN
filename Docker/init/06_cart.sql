CREATE SCHEMA IF NOT EXISTS cart_db;
SET search_path TO cart_db;


CREATE TABLE cart (
                      id SERIAL PRIMARY KEY,
                      customer_id BIGINT UNIQUE NOT NULL,
                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE cart_item (
                           id SERIAL PRIMARY KEY,
                           cart_id INT NOT NULL,
                           sku_id INT NOT NULL,
                           is_deposit BOOLEAN NOT NULL ,
                           quantity INT NOT NULL CHECK (quantity > 0),
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE(cart_id, sku_id)
);

CREATE OR REPLACE FUNCTION update_timestamp() RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = NOW(); RETURN NEW; END; $$ language 'plpgsql';

CREATE TRIGGER trg_cart_updated BEFORE UPDATE ON cart FOR EACH ROW EXECUTE FUNCTION update_timestamp();
CREATE TRIGGER trg_cart_item_updated BEFORE UPDATE ON cart_item FOR EACH ROW EXECUTE FUNCTION update_timestamp();


CREATE TABLE wishlist (
                          id SERIAL PRIMARY KEY,
                          customer_id BIGINT NOT NULL,
                          product_id INT NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE(customer_id, product_id)
);
CREATE INDEX idx_wishlist_customer_id ON wishlist(customer_id);