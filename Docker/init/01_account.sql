-- ==========================================================
-- acc_db (Account & Access Control Service)
-- ==========================================================
CREATE SCHEMA IF NOT EXISTS acc_db;
SET search_path TO acc_db;
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
-- table
CREATE TABLE USERS (
                       user_id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       phone_number VARCHAR(20),
                       password VARCHAR(255) NOT NULL,
                       user_name VARCHAR(255) NOT NULL,

                       is_staff BOOLEAN NOT NULL DEFAULT FALSE,
                       verifier BOOLEAN NOT NULL DEFAULT FALSE,

                       deleted_at TIMESTAMP DEFAULT NULL,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       reference_id BIGINT ,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_active_id ON USERS(user_id)
    WHERE is_active = TRUE AND deleted_at IS NULL;

CREATE TABLE ROLES (
                       role_id SERIAL PRIMARY KEY,
                       role_name VARCHAR(255) UNIQUE NOT NULL,
                       description VARCHAR(255),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE PERMISSIONS (
                             permission_id SERIAL PRIMARY KEY,
                             permission_name VARCHAR(255) UNIQUE NOT NULL,
                             description VARCHAR(255),
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE USER_ROLE (
                           user_id VARCHAR(36) NOT NULL,
                           role_id INT NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (user_id, role_id)
);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON USER_ROLE(role_id);

CREATE TABLE ROLE_PERMISSION (
                                 role_id INT NOT NULL,
                                 permission_id INT NOT NULL,
                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (role_id, permission_id)
);
CREATE INDEX IF NOT EXISTS idx_role_perm_perm_id ON ROLE_PERMISSION(permission_id);

CREATE TABLE USER_PROVIDERS (
                                id SERIAL PRIMARY KEY,
                                user_id VARCHAR(36),
                                provider_name VARCHAR(50), -- 'GOOGLE', 'FACEBOOK'
                                provider_user_id VARCHAR(255),
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                UNIQUE(provider_name, provider_user_id)
);
CREATE INDEX IF NOT EXISTS idx_user_providers_user_id ON USER_PROVIDERS(user_id);

-- TRIGGERS:

CREATE TRIGGER set_timestamp_users BEFORE UPDATE ON USERS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
CREATE TRIGGER set_timestamp_roles BEFORE UPDATE ON ROLES FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
CREATE TRIGGER set_timestamp_user_providers BEFORE UPDATE ON USER_PROVIDERS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();


