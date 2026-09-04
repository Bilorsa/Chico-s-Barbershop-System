# SharpCut Barbershop Booking System

A complete PHP + PDO + MySQL starter application with:
- Customer registration/login
- Public booking form
- Server-side appointment validation and overlap prevention
- Customer booking history
- Admin dashboard
- Admin appointment status management
- Service and barber management
- Payments table and seed data
- CSRF protection, password hashing, prepared statements
- Responsive HTML5/CSS presentation layer

## Requirements
- PHP 8.1+
- MySQL 8.0+ or MariaDB 10.5+
- Apache/Nginx with PHP enabled

## Installation
1. Create a folder named `barbershop` in your web root.
2. Copy this project into it.
3. Edit `config/config.php` with your MySQL credentials.
4. Import `database/schema.sql` into MySQL.
5. Visit `http://localhost/barbershop/`.

## Demo admin
Email: admin@sharpcut.test
Password: password

Change the demo admin password before production.

## Notes
Opening hours are 09:00–18:00 in `book.php`.
The overlap check is performed in a transaction with a locking query. For high-concurrency production systems, consider stronger slot locking/unique scheduling strategies.
This is intentionally framework-free so the PHP/PDO/MySQL architecture is easy to understand and extend.
