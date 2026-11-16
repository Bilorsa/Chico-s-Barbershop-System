CREATE DATABASE IF NOT EXISTS barbershop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE barbershop_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS barbers;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(25) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('customer','admin','barber') NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE services (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration SMALLINT UNSIGNED NOT NULL COMMENT 'Duration in minutes',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE barbers (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60) NOT NULL,
    specialty VARCHAR(120) NOT NULL DEFAULT 'All-round barber',
    experience_years TINYINT UNSIGNED NOT NULL DEFAULT 1,
    bio VARCHAR(500) DEFAULT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    user_id INT UNSIGNED DEFAULT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_barbers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE appointments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    barber_id INT UNSIGNED NOT NULL,
    service_id INT UNSIGNED NOT NULL,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('Pending','Confirmed','Completed','Cancelled') NOT NULL DEFAULT 'Pending',
    customer_note VARCHAR(500) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_barber FOREIGN KEY (barber_id) REFERENCES barbers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_appointments_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE RESTRICT,
    INDEX idx_appointment_date_barber (appointment_date, barber_id),
    INDEX idx_appointment_user_date (user_id, appointment_date),
    INDEX idx_appointment_status (status)
) ENGINE=InnoDB;

INSERT INTO services (name, description, price, duration) VALUES
('Classic Haircut', 'A clean, tailored haircut finished to suit your style.', 120.00, 30),
('Skin Fade', 'A precise fade with a crisp finish and detailed blending.', 150.00, 45),
('Beard Trim & Shape', 'Neat beard shaping, line-up and finishing touches.', 80.00, 30),
('Haircut & Beard', 'The complete refresh: haircut, beard trim and sharp detailing.', 190.00, 60),
('Hot Towel Shave', 'A traditional close shave with a relaxing hot towel finish.', 110.00, 30),
('Kids Haircut', 'A comfortable, stylish cut for younger clients.', 90.00, 30);

INSERT INTO barbers (first_name, last_name, specialty, experience_years, bio) VALUES
('Mike', 'Daniels', 'Fades & modern cuts', 5, 'Known for detailed fades, clean line-ups and a relaxed chair experience.'),
('James', 'Lee', 'Classic cuts & beard work', 3, 'A versatile barber with an eye for timeless styles and sharp beard shaping.'),
('Lebo', 'Mokoena', 'Precision cuts & styling', 6, 'Focused on precision, consistency and helping every client find their look.');
