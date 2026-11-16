-- Run this ONCE on an existing barbershop_db database.
-- Do not run this migration if you imported the updated database/barbershop_db.sql.
USE barbershop_db;

ALTER TABLE users
    MODIFY role ENUM('customer', 'admin', 'barber') NOT NULL DEFAULT 'customer';

ALTER TABLE barbers
    ADD COLUMN user_id INT UNSIGNED NULL UNIQUE AFTER is_active,
    ADD CONSTRAINT fk_barbers_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL;
