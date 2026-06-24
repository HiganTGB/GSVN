CREATE SCHEMA IF NOT EXISTS hrm_db;
SET search_path TO hrm_db;
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TABLE POSITIONS (
                           position_id SERIAL PRIMARY KEY,
                           position_name VARCHAR(255) UNIQUE NOT NULL,
                           default_base_salary DECIMAL(19, 4) DEFAULT 0 NOT NULL, -- for ref only
                           description TEXT,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE STAFFS (
                        staff_id BIGSERIAL PRIMARY KEY,
                        user_id VARCHAR(36) UNIQUE,
                        full_name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) UNIQUE,
                        dob DATE NOT NULL,
                        gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
                        phone_number VARCHAR(20) NOT NULL ,
                        address VARCHAR(255) NOT NULL,
                        identity_card VARCHAR(255) NOT NULL,
                        avatar_url VARCHAR(255),
                        warehouse_id INT,
                        position_id INT NOT NULL,
                        base_salary DECIMAL(19, 4) NOT NULL,

                        deleted_at TIMESTAMP DEFAULT NULL,
                        is_active BOOLEAN DEFAULT TRUE,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE STAFF_SALARIES (
                                id BIGSERIAL PRIMARY KEY,
                                staff_id BIGINT NOT NULL,
                                position_id INT NOT NULL ,
                                position_name VARCHAR(255) NOT NULL ,
                                base_salary DECIMAL(19, 4) NOT NULL,
                                effective_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                note TEXT,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE LEAVE_REQUESTS (
                                id BIGSERIAL PRIMARY KEY,
                                staff_id BIGINT NOT NULL,
                                staff_name VARCHAR(255) NOT NULL ,
                                leave_type VARCHAR(50) NOT NULL CHECK (leave_type IN('SICK','ANNUAL','RESIGNATION','OTHER') ),
                                reason TEXT,

                                start_date DATE,
                                end_date DATE,

                                effective_date DATE,

                                status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),

                                approved_by BIGINT,
                                approved_name VARCHAR(255),
                                approved_at TIMESTAMP,
                                note TEXT,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE PAYROLLS (
                          id SERIAL PRIMARY KEY,
                          staff_id BIGINT NOT NULL,
                          staff_name VARCHAR(255),
                          salary_period VARCHAR(7) NOT NULL, -- Định dạng 'YYYY-MM'
                          position_id INT,
                          position_name VARCHAR(255) NOT NULL,
                          base_salary DECIMAL(19, 4),
                          working_days DECIMAL(4, 1),
                          total_bonus DECIMAL(19, 4) DEFAULT 0,
                          total_deduction DECIMAL(19, 4) DEFAULT 0,
                          final_salary DECIMAL(19, 4),
                          status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'PAID', 'REJECTED')),
                          note TEXT,
                          approved_by BIGINT,
                          approved_name VARCHAR(255),
                          approved_at TIMESTAMP,
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT unique_staff_period UNIQUE (staff_id, salary_period)
);
CREATE TRIGGER set_timestamp_positions BEFORE UPDATE ON POSITIONS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
CREATE TRIGGER set_timestamp_staffs BEFORE UPDATE ON STAFFS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
CREATE TRIGGER set_timestamp_leave_requests BEFORE UPDATE ON LEAVE_REQUESTS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
CREATE TRIGGER set_timestamp_payrolls BEFORE UPDATE ON PAYROLLS FOR EACH ROW EXECUTE PROCEDURE update_timestamp();
