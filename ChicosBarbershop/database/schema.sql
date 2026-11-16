CREATE DATABASE IF NOT EXISTS barbershop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE barbershop;

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS payments, appointments, services, barbers, customers;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE customers (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    password_hash VARCHAR(255) NULL,
    role ENUM('customer','admin') NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE barbers (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(190) UNIQUE,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE services (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    duration_minutes SMALLINT UNSIGNED NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE appointments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    customer_id INT UNSIGNED NOT NULL,
    barber_id INT UNSIGNED NOT NULL,
    service_id INT UNSIGNED NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    status ENUM('pending','confirmed','completed','cancelled','no_show') NOT NULL DEFAULT 'pending',
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_customer FOREIGN KEY(customer_id) REFERENCES customers(id),
    CONSTRAINT fk_appt_barber FOREIGN KEY(barber_id) REFERENCES barbers(id),
    CONSTRAINT fk_appt_service FOREIGN KEY(service_id) REFERENCES services(id),
    INDEX idx_appt_barber_time (barber_id,start_at,end_at),
    INDEX idx_appt_customer (customer_id,start_at)
) ENGINE=InnoDB;

CREATE TABLE payments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT UNSIGNED NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('unpaid','paid','refunded') NOT NULL DEFAULT 'unpaid',
    method ENUM('cash','card','eft','online') NOT NULL DEFAULT 'cash',
    paid_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_appt FOREIGN KEY(appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO customers(name,email,phone,password_hash,role) VALUES
('Admin User','admin@sharpcut.test','0800000000','$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC4P6H5ZxJ4K8yVQh3fW','admin'),
('Demo Customer','customer@sharpcut.test','0811111111',NULL,'customer');

INSERT INTO barbers(name,email) VALUES
('Marcus','marcus@sharpcut.test'),('Jayden','jayden@sharpcut.test'),('Theo','theo@sharpcut.test');

INSERT INTO services(name,duration_minutes,price) VALUES
('Classic Cut',30,180.00),('Skin Fade',45,250.00),('Beard Trim',20,120.00),('Cut + Beard',60,320.00);

INSERT INTO appointments(customer_id,barber_id,service_id,start_at,end_at,status,notes) VALUES
(2,1,1,DATE_ADD(NOW(),INTERVAL 1 DAY),DATE_ADD(NOW(),INTERVAL 1 DAY) + INTERVAL 30 MINUTE,'confirmed','Demo appointment');

INSERT INTO payments(appointment_id,amount,status,method) VALUES (1,180.00,'unpaid','cash');
