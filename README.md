# Chico's Barber Shop — Online Booking System

A modern, responsive barbershop booking system built with **PHP, PDO, MySQL, HTML5 and CSS3**.

The system provides a customer-facing website, online appointment booking, customer authentication, a My Bookings area, and an administration panel for managing appointments, services and barbers.

---

## ✂️ Project Overview

**Chico's Barber Shop** is designed around three application layers:

### 1. Presentation Layer
- HTML5
- Responsive CSS3
- Modern blue/black visual design
- Premium landing page
- Service cards with haircut imagery
- Barber/team profiles
- Booking interface
- Mobile-friendly navigation

### 2. Application Layer
- PHP
- PDO prepared statements
- Session-based authentication
- Appointment validation
- Availability checking
- Double-booking protection
- Customer registration/login
- Admin functionality
- Booking confirmation workflow

### 3. Database Layer
- MySQL
- Normalized relational structure
- Customers
- Barbers
- Services
- Appointments
- Payments
- Foreign-key relationships
- Seed data for initial testing

---

# 🚀 Features

## Customer Features

### Homepage
- Chico's Barber Shop branding
- Premium hero section
- Services section
- Barber/team section
- Client testimonial section
- Strong "Book Now" calls to action
- Responsive design

### Booking
Customers can:

1. Select a service
2. Select a barber
3. Select an appointment date
4. View available time slots
5. Enter their details
6. Add optional booking notes
7. Confirm an appointment

The system checks existing appointments before confirming a booking.

### Availability
The booking system:
- Uses the selected service duration
- Checks the selected barber's existing appointments
- Prevents overlapping bookings
- Prevents past appointments
- Keeps bookings within configured business hours
- Updates available slots based on existing bookings

### Customer Accounts
Customers can:
- Register
- Log in
- Log out
- View their bookings
- Review upcoming appointments
- Review previous bookings

---

# 🛠️ Admin Features

The administration area provides management pages for:

- Dashboard
- Appointments
- Services
- Barbers

Administrators can review appointments and manage the information used by the booking system.

Admin area:

```text
/admin/
```

---

# 🗂️ Project Structure

```text
chicos_barber_shop/
│
├── admin/
│   ├── index.php
│   ├── appointments.php
│   ├── barbers.php
│   └── services.php
│
├── assets/
│   ├── css/
│   │   └── style.css
│   │
│   └── images/
│       ├── chico-logo.png
│       ├── classic-cut.webp
│       ├── skin-fade.webp
│       ├── beard-x-haircut.webp
│       ├── barber-chico.webp
│       ├── barber-joe.webp
│       ├── barber-mike.webp
│       └── barber-troy.webp
│
├── config/
│   ├── config.php
│   └── database.php
│
├── database/
│   └── schema.sql
│
├── includes/
│   ├── auth.php
│   ├── footer.php
│   ├── functions.php
│   └── header.php
│
├── admin/
│
├── index.php
├── book.php
├── booking_success.php
├── login.php
├── register.php
├── logout.php
├── my_bookings.php
└── README.md
```

---

# 💻 Requirements

You need a local PHP/MySQL development environment such as:

- XAMPP
- WAMP
- Laragon
- MAMP
- Apache + PHP + MySQL/MariaDB

Recommended:

```text
PHP 8.0+
MySQL 5.7+ / MariaDB
Apache
PDO MySQL extension
```

---

# ⚙️ Installation

## Step 1 — Install XAMPP

Install XAMPP and start:

```text
Apache
MySQL
```

---

## Step 2 — Copy the project

Copy the project folder into XAMPP's web directory:

```text
C:\xampp\htdocs\
```

For example:

```text
C:\xampp\htdocs\chicos_barber_shop\
```

---

## Step 3 — Create the database

Open:

```text
http://localhost/phpmyadmin
```

Create a database, for example:

```text
chicos_barbershop
```

Then select the database and import:

```text
database/schema.sql
```

The schema contains the required tables and seed data.

---

# 🔐 Database Configuration

Open:

```text
config/database.php
```

Update the database settings if required.

Typical XAMPP configuration:

```php
$host = '127.0.0.1';
$db   = 'chicos_barbershop';
$user = 'root';
$pass = '';
```

If your MySQL installation has a password, replace the empty password with your MySQL password.

---

# 🌐 Run the Website

Open:

```text
http://localhost/chicos_barber_shop/
```

The homepage should load.

Then click:

```text
Book Now
```

to test the booking system.

---

# 📅 Test the Booking System

Recommended testing sequence:

### 1. Open the website

```text
http://localhost/chicos_barber_shop/
```

### 2. Select a service

Example:

```text
Classic Cut
```

### 3. Select a barber

Example:

```text
Chico
```

### 4. Select a future date

Choose a date that is not in the past.

### 5. Select an available time

The system displays available appointment slots based on the barber's existing appointments.

### 6. Enter customer details

For a guest booking, provide:

```text
Name
Phone
Email
```

### 7. Confirm

The system creates:

```text
Appointment
Payment record
```

and displays the booking confirmation.

---

# 🔒 Booking Protection

The booking system performs server-side validation.

It checks:

- Service exists
- Barber exists
- Date is valid
- Time is valid
- Appointment is not in the past
- Appointment fits within opening hours
- Barber is not already booked
- Appointment duration does not overlap another appointment

The appointment insert is performed inside a database transaction to reduce the risk of race-condition double bookings.

---

# 💳 Payments

The current system creates a payment record when an appointment is confirmed.

The default payment state is:

```text
unpaid
```

and the default method is:

```text
cash
```

This provides a foundation for adding an online payment provider later.

Possible integrations include:

- PayFast
- Stripe
- PayPal

---

# 🧑‍💼 Admin Area

Open:

```text
http://localhost/chicos_barber_shop/admin/
```

The admin area provides access to:

```text
Dashboard
Appointments
Services
Barbers
```

Use the seeded database/admin credentials from `database/schema.sql` where applicable.

For production, change all seeded credentials before deployment.

---

# 🗄️ Database Model

The core database entities are:

```text
CUSTOMERS
    │
    └──< APPOINTMENTS >── BARBERS
              │
              └── SERVICES
              │
              └── PAYMENTS
```

### Customers

Stores customer information and authentication data.

### Barbers

Stores barber profiles and availability status.

### Services

Stores haircut/service information including:

- Name
- Duration
- Price
- Active status

### Appointments

Connects:

```text
Customer
Barber
Service
Date/time
Status
Notes
```

### Payments

Stores payment information associated with appointments.

---

# 🎨 Design System

The website uses a premium dark theme based on:

```text
Primary:      Blue
Secondary:    Black
Background:   #05070B
Panel:        #111827
Accent:       #1683FF
Accent Light: #45A3FF
Text:         #F8FBFF
Muted:        #94A3B8
```

The interface is designed to feel:

- Premium
- Clean
- Modern
- Professional
- Mobile friendly

---

# 📱 Responsive Design

The website adapts to:

- Desktop
- Laptop
- Tablet
- Mobile

The navigation switches to a mobile menu on smaller screens.

Booking forms and service cards also adapt to smaller displays.

---

# 🧪 Suggested Test Cases

Before deployment, test:

### Customer

- [ ] Register account
- [ ] Login
- [ ] Logout
- [ ] Guest booking
- [ ] Logged-in booking
- [ ] Select service
- [ ] Select barber
- [ ] Select date
- [ ] Select time
- [ ] Confirm appointment
- [ ] View My Bookings

### Availability

- [ ] Book an available slot
- [ ] Attempt to book the same slot twice
- [ ] Attempt to book a past time
- [ ] Attempt to book outside business hours
- [ ] Test different service durations

### Admin

- [ ] Open dashboard
- [ ] View appointments
- [ ] Add/edit services
- [ ] Add/edit barbers
- [ ] Verify new services appear on booking page
- [ ] Verify new barbers appear on booking page

---

# 🔧 Customization

## Change Business Hours

The current booking logic uses:

```text
09:00 — 18:00
```

Update the opening/closing values in:

```text
book.php
```

---

## Change Appointment Interval

The current booking slots are generated every:

```text
30 minutes
```

This can be changed in the slot-generation logic in:

```text
book.php
```

---

## Add Services

Services can be added through the admin area or directly through MySQL.

Once a service is active, it becomes available to customers in the booking system.

---

## Add Barbers

Barbers can be managed through:

```text
/admin/barbers.php
```

Active barbers appear in the booking form.

---

# 🔐 Security Notes

The project uses several basic security practices:

- PDO prepared statements
- Password hashing through PHP password functions
- Session authentication
- CSRF protection for booking forms
- Server-side validation
- Database transactions for booking creation

For production deployment, also configure:

- HTTPS
- Secure session cookies
- Environment variables for database credentials
- Strong admin passwords
- Database user permissions
- Production error logging
- Email verification
- Rate limiting
- Automated backups

Never commit real production database passwords into a public repository.

---

# 🚀 Future Improvements

Recommended production upgrades:

### Payments

Add:

```text
PayFast / Stripe / PayPal
```

### Notifications

Add:

```text
Email confirmation
SMS confirmation
WhatsApp confirmation
Appointment reminders
```

### Advanced Scheduling

Add:

```text
Barber working hours
Days off
Holiday blocking
Break periods
Multiple locations
```

### Admin Dashboard

Add:

```text
Calendar view
Revenue analytics
Daily appointment statistics
Customer management
Payment reports
Export to CSV/PDF
```

### Customer Experience

Add:

```text
Reschedule appointment
Cancel appointment
Favourite barber
Booking history
Online reviews
Loyalty programme
```

---

# 📐 System Architecture

```text
┌─────────────────────────┐
│       CUSTOMER          │
│  Desktop / Tablet /     │
│        Mobile           │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│       PRESENTATION      │
│       HTML5 + CSS       │
│       JavaScript        │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│     APPLICATION         │
│          PHP            │
│ Authentication          │
│ Booking Logic           │
│ Validation              │
│ Admin Functions         │
└────────────┬────────────┘
             │ PDO
             ▼
┌─────────────────────────┐
│        MYSQL            │
│ Customers               │
│ Barbers                 │
│ Services                │
│ Appointments            │
│ Payments                │
└─────────────────────────┘
```

---

# 🗺️ Main Site Map

```text
HOME
│
├── Services
│   └── Service Details
│
├── Barbers
│   └── Barber Profiles
│
├── Book Appointment
│   ├── Select Service
│   ├── Select Barber
│   ├── Select Date
│   ├── Select Time
│   ├── Customer Details
│   └── Confirmation
│
├── My Bookings
│   ├── Upcoming
│   └── Previous
│
├── Login
├── Register
│
└── Admin
    ├── Dashboard
    ├── Appointments
    ├── Services
    └── Barbers
```


