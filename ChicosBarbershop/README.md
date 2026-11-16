# Chico's Barbershop

A responsive PHP and MySQL appointment-booking and shop-management application designed to run locally with XAMPP. The interface uses a modern blue-and-black theme with dark navy surfaces, bright blue accents and responsive layouts.

## Features

### Customer experience
- Register and sign in with securely hashed passwords.
- Browse the live service menu, pricing and appointment duration.
- Choose an active barber and view available appointment times for a selected date.
- Book appointments using the signed-in customer's account (no hard-coded customer ID).
- View upcoming appointments and booking history.
- Cancel eligible upcoming appointments.
- Receive clear success, validation and error messages.

### Administration
- Separate administrator access and dashboard.
- View today's bookings, pending requests, customers and active services.
- Review all appointments and update their status: Pending, Confirmed, Completed or Cancelled.
- Add, edit, activate and hide services.
- Add, edit, activate and hide barber profiles.
- View registered customers and their booking counts.
- Create and update individual barber sign-in accounts from the barber management page.

### Barber workspace
- Each barber can sign in using a separate account created by an administrator.
- Dedicated barber dashboard with today's assigned appointments, pending requests and upcoming bookings.
- View customer contact details, service information, appointment notes, date, time and price for assigned bookings.
- Filter personal appointments by date and status.
- Confirm pending appointments, cancel eligible future appointments and mark appointments completed after their scheduled start time.
- Barber accounts can only access appointments linked to their own barber profile; they cannot access the administrator dashboard or another barber's schedule.

### Booking rules
- Shop hours: Monday–Friday, 09:00–17:00; Saturday, 09:00–15:00; Sunday closed.
- Appointment start times are offered in 30-minute increments.
- Available slots account for the selected service duration and existing non-cancelled bookings.
- Past times and overlapping appointments are rejected on the server, even if a user bypasses the browser interface.
- Booking creation locks the selected barber row in a database transaction before checking and saving the slot, helping prevent simultaneous overlapping bookings.

## Requirements

- Windows with XAMPP (or an equivalent PHP/MySQL stack).
- PHP 8.1 or newer (PHP 8.2+ recommended).
- MySQL 5.7+ or MariaDB 10.4+.
- PHP extensions: PDO and pdo_mysql.
- Apache with `mod_rewrite` not required. `.htaccess` protection assumes Apache `AllowOverride` is enabled.
- Internet access is only needed to load the optional Google Fonts and remote hero photography; the application layout and functionality do not depend on those assets.

## Installation with XAMPP

1. Extract the `ChicosBarbershop` folder into `C:\xampp\htdocs\`.
2. Start **Apache** and **MySQL** from the XAMPP Control Panel.
3. Open `http://localhost/phpmyadmin`.
4. Select **Import**, choose `database/barbershop_db.sql`, and run the import. The script creates the `barbershop_db` database and sample services/barbers. **If you already have data in a database with these table names, back it up first:** this starter schema drops and recreates its own `users`, `services`, `barbers` and `appointments` tables.
5. Open `config/database.php` and confirm the host, database name, username and password match your local MySQL configuration. The XAMPP defaults are `127.0.0.1`, `barbershop_db`, `root` and an empty password.
6. Open a terminal/Command Prompt and run:

   ```bat
   cd C:\xampp\htdocs\ChicosBarbershop
   C:\xampp\php\php.exe create_admin.php
   ```

   Follow the prompts to create your own administrator account. Use a strong password of at least 12 characters. The script is command-line only and does not create a hard-coded default admin login.

7. Visit `http://localhost/ChicosBarbershop/` in your browser.
8. Register a normal customer account, sign in, and make a test booking. Sign out and sign in using the administrator account to manage bookings, services and barbers.
9. In **Admin → Barbers**, edit a barber profile and enter a unique login email, phone number and password (minimum 12 characters) under **Barber Portal Access**. Save the profile to create that barber's separate account. Repeat for each barber who needs portal access.
10. Sign out and sign in with the barber's email and password. The account opens the dedicated `barber/index.php` workspace.

### Updating an existing installation
If you are upgrading an existing database and do not want to delete its current customers, barbers or appointments, **do not re-import** the full starter SQL file because it drops and recreates the tables. Back up your database, then run `database/migrations/add_barber_portal.sql` once in phpMyAdmin. This adds the `barber` user role and the link between each barber profile and its login account. After that, use **Admin → Barbers** to create login accounts.

## Main routes

| Page | Route | Access |
|---|---|---|
| Home | `index.php` | Public |
| Services | `services.php` | Public |
| Register | `register.php` | Guest |
| Sign in | `login.php` | Guest |
| Book appointment | `book.php` | Signed-in customer |
| Customer dashboard | `dashboard.php` | Signed-in customer |
| Admin dashboard | `admin/index.php` | Administrator |
| Barber dashboard | `barber/index.php` | Barber account |
| Barber appointment list | `barber/appointments.php` | Barber account (own bookings only) |
| Appointment management | `admin/appointments.php` | Administrator |
| Service management | `admin/services.php` | Administrator |
| Barber management | `admin/barbers.php` | Administrator |
| Customer directory | `admin/customers.php` | Administrator |

## Database overview

- `users`: customer, administrator and barber accounts, roles and password hashes.
- `services`: service name, description, price, duration and active state.
- `barbers`: barber profile, specialty, experience, active state and optional unique `user_id` link to the barber's login account.
- `appointments`: customer, barber, service, date, start/end time, status and optional note.

Foreign keys protect appointment relationships. Prepared statements are used for user input. Passwords are stored using PHP's `password_hash()` and verified using `password_verify()`.

## Configuration and troubleshooting

### `Unknown database 'barbershop_db'`
Import `database/barbershop_db.sql` in phpMyAdmin and verify the `$dbname` value in `config/database.php`. The database name must match exactly.

### `Access denied for user 'root'`
Check the MySQL username and password in `config/database.php`. Some installations set a password for `root`.

### Blank page or HTTP 500
Check `C:\xampp\apache\logs\error.log` and the PHP error log. Ensure PHP 8.1+ and the PDO MySQL extension are enabled.

### No appointment times appear
Confirm that a service, active barber and valid date are selected. Sundays are closed; Saturday closes at 15:00. Existing bookings and service duration also affect the available times.

### Admin page says access denied
Create an administrator account by running `create_admin.php` from the command line, then sign in using that account. Customer registration never grants administrator privileges.

## Security notes before public deployment

This project is a local development / coursework starter, not a fully audited production booking platform. Before deploying it publicly:
- Use HTTPS and secure production database credentials; do not use the XAMPP root account.
- Set `secure` session cookies and configure session lifetime and rate limiting appropriately.
- Add email verification, password reset and account lockout/rate limiting.
- Configure a real business timezone, opening hours, holidays and barber-specific schedules.
- Add automated tests, backups, monitoring and a privacy policy/data-retention process.
- Review Apache `.htaccess` protection and ensure database/configuration files cannot be downloaded.
- Replace the sample business contact/location information and test all booking scenarios with real operating rules.

## Project structure

```text
ChicosBarbershop/
├── admin/                  # Admin dashboard and management pages
├── barber/                 # Separate barber dashboard and personal appointment schedule
├── assets/
│   ├── css/style.css       # Responsive visual design
│   └── js/app.js           # Mobile navigation and live slot lookup
├── config/database.php     # PDO database connection
├── database/               # SQL setup script and existing-database migrations
├── includes/               # Shared layout and application helpers
├── index.php                # Public landing page
├── register.php / login.php / logout.php
├── services.php / book.php / process_booking.php
├── available_slots.php      # AJAX availability endpoint
├── dashboard.php / cancel_booking.php
└── create_admin.php         # CLI-only administrator setup
```
