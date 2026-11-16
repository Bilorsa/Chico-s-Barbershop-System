# Chico's Barbershop System

A barbershop management solution developed as a Work Integrated Learning
(WIL) project. Chico's Barbershop combines an Android application with a
companion PHP/MySQL website to support customer appointments and
day-to-day barbershop administration.

**Project repository:**
https://github.com/Bilorsa/Chico-s-Barbershop-System\
**Website source:**
https://github.com/Bilorsa/Chico-s-Barbershop-System/tree/main/ChicosBarbershop

## Table of Contents

-   [Project Overview](#project-overview)
-   [Project Components](#project-components)
-   [Key Features](#key-features)
-   [Technology Stack](#technology-stack)
-   [System Design](#system-design)
-   [Website Setup](#website-setup)
-   [Android Application Setup](#android-application-setup)
-   [How the Booking Process Works](#how-the-booking-process-works)
-   [Administrator Guide](#administrator-guide)
-   [Security and Data Handling](#security-and-data-handling)
-   [Testing and Continuous
    Integration](#testing-and-continuous-integration)
-   [Repository Structure](#repository-structure)
-   [Known Limitations](#known-limitations)
-   [Future Enhancements](#future-enhancements)
-   [Development Team](#development-team)
-   [AI Development Support](#ai-development-support)

## Project Overview

Chico's Barbershop aims to make appointment scheduling and barbershop
administration more organised. The system provides customers with
digital access to services, barber information and appointment booking,
while authorised staff can manage appointments and important business
records.

The project contains two related components:

1.  **Android application** -- developed with Kotlin in Android Studio,
    with Firebase integration for backend-related functionality.
2.  **Companion website** -- a server-rendered PHP application using PDO
    and MySQL, providing customer booking and a protected administration
    area.

The mobile application and website are separate components of the wider
solution. The project documentation identifies Firebase as part of the
Android application's backend integration and MySQL as the database used
by the PHP website. The exact data exchange or synchronisation
relationship between Firebase and the website's MySQL database is not
specified here.

## Project Components

### Android Application

The Android application provides customer-facing and staff-facing
functionality. Its documented screens include:

-   Login and customer registration.
-   Customer home screen and navigation.
-   Service listing and prices.
-   Appointment booking and confirmation.
-   Staff administration dashboard.
-   Customer and appointment management.
-   Business and appointment reporting.

The application uses a dark visual theme with black backgrounds,
dark-grey interface sections, cyan accents and white text.

### Companion Website

The website provides an online customer booking process and an
administrator interface.

Customer-facing pages support service and barber browsing, account
access, guest booking, appointment confirmation, booking history and
eligible cancellations.

The administration area is protected and provides tools for managing
appointments, payment records, services and barbers.

## Key Features

### Customer Features

-   Customer registration and sign-in.
-   Guest appointment booking.
-   Browse available barbershop services and barber information.
-   Select a service, barber, date and available time.
-   Receive a booking confirmation/reference.
-   View booking history when signed in.
-   Cancel eligible future appointments.
-   Convert an eligible guest profile into a password-protected customer
    account.

### Staff and Administrator Features

-   Protected administrator access.
-   Dashboard summaries and upcoming appointment information.
-   Search and filter appointments.
-   Update appointment status.
-   Record payment status and payment method.
-   Add and edit services.
-   Add and edit barber records.
-   Activate or deactivate services and barbers.
-   View and manage customer information.
-   Access business and appointment-related reports in the Android
    application.

### Booking Rules

The website applies the following booking rules:

-   Configured business hours are **09:00 to 18:00**.
-   Appointment start times are offered in **30-minute intervals**.
-   The selected service duration determines the appointment end time.
-   Past appointments and appointments outside business hours are
    rejected.
-   The server revalidates the selected service, barber, date and time
    before saving.
-   The system checks for overlapping pending or confirmed appointments
    for the selected barber.

## Technology Stack

  -----------------------------------------------------------------------
  Technology                          Purpose
  ----------------------------------- -----------------------------------
  Kotlin                              Android application development

  Android Studio                      Android development environment

  Firebase                            Backend integration, including
                                      authentication and data-related
                                      functionality for the Android
                                      application

  PHP 8.1+                            Server-side processing for the
                                      companion website

  Apache                              Local web server used during
                                      website development

  MySQL                               Relational database for the website

  PDO                                 Database access from PHP

  HTML5                               Website structure

  CSS3                                Website styling and responsive
                                      layout

  JavaScript                          Browser-side interactions

  XAMPP                               Local PHP and MySQL development
                                      environment

  phpMyAdmin                          Local database administration

  Git                                 Local source control and version
                                      management

  GitHub                              Remote repository hosting and
                                      collaboration

  GitHub Actions                      Configured Android build workflow
                                      and CI checks
  -----------------------------------------------------------------------

## System Design

### Website Architecture

The website uses a server-rendered PHP structure:

1.  A user's browser sends a request to the Apache web server.
2.  PHP processes the request and applies application rules.
3.  PDO communicates with the MySQL database.
4.  The server returns the resulting page to the browser.
5.  HTML, CSS and JavaScript provide the user interface and browser
    interactions.

### Website Database

The website database is named `barbershop`. Its principal tables are:

  -----------------------------------------------------------------------
  Table                               Purpose
  ----------------------------------- -----------------------------------
  `customers`                         Stores customer account and contact
                                      information.

  `barbers`                           Stores barber profile information
                                      and active/inactive status.

  `services`                          Stores service names, prices,
                                      durations and active/inactive
                                      status.

  `appointments`                      Connects a customer, barber and
                                      service to a date, time, status and
                                      booking notes.

  `payments`                          Stores the amount, payment status,
                                      payment method and paid date
                                      associated with an appointment.
  -----------------------------------------------------------------------

Primary keys identify records and foreign keys maintain relationships
between related records. An appointment references a customer, barber
and service. A payment record is associated with an appointment. Booking
transactions help keep appointment creation and its related payment
record consistent.

## Website Setup

The following instructions describe a local development setup using
XAMPP.

### Prerequisites

Install the following:

-   XAMPP with Apache, MySQL and PHP.
-   PHP 8.1 or later.
-   A web browser.
-   Git (optional, for cloning the repository).

### 1. Get the Project

Clone the repository:

``` bash
git clone https://github.com/Bilorsa/Chico-s-Barbershop-System.git
```

Alternatively, download the repository as a ZIP file and extract it.

### 2. Copy the Website into XAMPP

1.  Locate the `ChicosBarbershop` website folder in the repository.
2.  Copy it into the XAMPP `htdocs` directory.
3.  Confirm that the website folder is named `ChicosBarbershop`.

The expected local folder will be similar to:

``` text
C:\xampp\htdocs\ChicosBarbershop
```

### 3. Start Apache and MySQL

1.  Open the XAMPP Control Panel.
2.  Start **Apache**.
3.  Start **MySQL**.
4.  Confirm that both services are running.

### 4. Create and Import the Database

1.  Open phpMyAdmin at `http://localhost/phpmyadmin`.
2.  Create a MySQL database named:

``` text
barbershop
```

3.  Locate the SQL database/schema file supplied with the website
    project.
4.  Select the `barbershop` database in phpMyAdmin.
5.  Use the **Import** tab to import the supplied SQL file.
6.  Confirm that the required tables have been created.

> The project report identifies the database name and principal tables.
> Use the actual SQL file included in the repository for the schema; do
> not create substitute tables if the project already supplies a
> database script.

### 5. Configure the Database Connection

Review the website's database configuration file, located at:

``` text
config/database.php
```

Configure the connection values for the local MySQL environment. For a
standard XAMPP installation, the local database host is commonly
`localhost`; use the username, password and database settings configured
on your machine.

The website supports environment-based configuration. Keep credentials
and other sensitive values out of public source control. Follow the
configuration approach and example files supplied in the project.

### 6. Create an Administrator Account

Use the administrator-creation command-line script included in the
website project. Review the script's instructions and run it from the
appropriate project directory using the PHP command-line interpreter.

Do not assume a default administrator username or password. Create and
use credentials that are known only to the authorised administrator.

### 7. Open the Website

Visit:

``` text
http://localhost/ChicosBarbershop
```

If the website does not load, check that Apache is running, the project
folder is in `htdocs`, the database exists and the database connection
settings are correct.

## Android Application Setup

### Prerequisites

-   Android Studio.
-   A compatible JDK and Android SDK as required by the project.
-   Access to the project's Firebase configuration.
-   An Android emulator or compatible Android device.

### Setup Steps

1.  Clone or download the GitHub repository.
2.  Open Android Studio.
3.  Open the Android application project from the repository.
4.  Allow Android Studio to complete Gradle synchronisation.
5.  Configure the Firebase project and the Android application's
    Firebase settings using the project's existing configuration and
    Firebase documentation.
6.  Resolve any SDK or dependency prompts shown by Android Studio.
7.  Select an emulator or connect a compatible Android device.
8.  Build and run the application.

> The project uses Firebase integration. Make sure that the Firebase
> configuration belongs to the correct project and that private
> credentials or service-account files are not committed to the public
> repository.

## How the Booking Process Works

The website's customer booking process follows these steps:

1.  The customer opens the website and reviews the available services
    and barbers.
2.  The customer signs in, registers, or chooses the guest booking
    option.
3.  The customer selects a service, barber, appointment date and
    available time.
4.  The server validates the submitted details, business hours and
    appointment availability.
5.  The system checks that the selected barber does not have an
    overlapping pending or confirmed appointment.
6.  If validation succeeds, the appointment and its related payment
    record are saved using a database transaction.
7.  The customer receives a booking confirmation/reference.
8.  Registered customers can return to their booking history to review
    appointments and cancel eligible future bookings.

## Administrator Guide

1.  Open the website and navigate to the protected administration area.
2.  Sign in using the administrator account created during setup.
3.  Use the dashboard to review operational summaries and upcoming
    appointments.
4.  Search or filter appointments when managing bookings.
5.  Update appointment statuses and record payment status or method.
6.  Add or edit barber and service records as required.
7.  Deactivate a barber or service when it should no longer be available
    for new bookings.

Deactivation is used to prevent a barber or service from being selected
for new bookings while preserving historical appointment references.

The Android application also includes staff-oriented management screens
for customers, appointments and reports.

## Security and Data Handling

Security controls documented for the website include:

-   Password hashing and password verification.
-   PDO prepared statements for parameterised database queries.
-   Server-side validation of submitted data.
-   CSRF token checks.
-   Output escaping.
-   Session-cookie security settings.
-   Session identifier regeneration after successful sign-in.
-   Login-attempt throttling.
-   POST requests for state-changing sign-out and cancellation actions.
-   Database transactions and additional booking safeguards, including
    row locking.
-   Access restrictions for sensitive configuration, include and
    database directories.

### Production Deployment Considerations

Before deploying the website publicly:

-   Use HTTPS.
-   Store secrets and database credentials securely outside public
    source control.
-   Use a least-privilege database account.
-   Configure regular database backups.
-   Apply appropriate privacy controls to customer information.
-   Complete functional, security, accessibility and compatibility
    testing.

The website's payment section records payment information; it is **not a
live payment gateway**.

## Testing and Continuous Integration

Testing is important to confirm that the application behaves as expected
and that changes do not introduce regressions.

### Suggested Website Functional Checks

The following checks can be performed in the local XAMPP environment:

  -----------------------------------------------------------------------
  Area                                Check
  ----------------------------------- -----------------------------------
  Database connection                 Confirm that the website connects
                                      to the `barbershop` database.

  Registration                        Register a customer and verify that
                                      the account is stored.

  Sign-in                             Confirm that valid credentials
                                      allow access and invalid
                                      credentials are rejected.

  Service and barber selection        Confirm that active services and
                                      barbers are available for booking.

  Appointment booking                 Submit a valid booking and verify
                                      its confirmation and database
                                      record.

  Booking validation                  Try a past date, a time outside
                                      business hours and an overlapping
                                      booking.

  Customer bookings                   Confirm that a registered customer
                                      can view their booking history.

  Cancellation                        Confirm that only eligible future
                                      bookings can be cancelled.

  Administration                      Check appointment search, filters
                                      and status updates.

  Service and barber management       Test adding, editing, activating
                                      and deactivating records.

  Payment records                     Check that payment status and
                                      method can be recorded and updated.

  Security                            Check validation, CSRF protection
                                      and access restrictions.
  -----------------------------------------------------------------------

These are recommended test cases, not a claim that every test has
passed. Record actual outcomes when executing tests.

### GitHub Actions

A GitHub Actions workflow is configured for the Android application. It
runs the configured build process when the relevant repository events
occur. This provides an automated way to check whether the application
builds with the current source code and configuration.

GitHub Actions can also execute automated tests when tests are included
in the workflow. Review the **Actions** tab in the GitHub repository to
inspect workflow runs and their results. A successful build should not
be treated as proof that every application feature has passed functional
testing.

### Manual Android Checks

When testing the Android application, check the documented user
journeys:

-   Customer registration and login.
-   Customer navigation and home screen.
-   Service listing and prices.
-   Appointment selection and booking.
-   Booking confirmation.
-   Staff dashboard access.
-   Customer and appointment management.
-   Reports screen.

Document the device/emulator, test data, expected result, actual result
and pass/fail outcome for each test performed.

## Repository Structure

The repository contains the Android Studio application, the companion
website and supporting project documentation. The website source is
identified in the repository at:

``` text
ChicosBarbershop/
```

The exact Android module, workflow and database-script paths should be
confirmed against the current repository contents. A simplified view of
the documented components is:

``` text
Chico-s-Barbershop-System/
├── Android application
│   └── Kotlin source and Android Studio project files
├── ChicosBarbershop/
│   └── PHP companion website
├── GitHub Actions workflow
└── Project documentation
```

This is a high-level overview rather than a complete listing of every
repository file.

## Known Limitations

The project documentation identifies the following limitations:

-   No live payment gateway is currently included.
-   Automated email, SMS or WhatsApp customer notifications are not
    included.
-   Complete barber shift and leave management is not included.
-   Full production testing remains outstanding.
-   Further keyboard, screen-size, accessibility and security testing is
    required before production use.
-   The exact relationship and any data synchronisation between the
    Android application's Firebase services and the website's MySQL
    database require clearer technical documentation.

The Android application was prepared for publication, but the project
report states that it was not published on the Google Play Store because
of financial constraints.

## Future Enhancements

Potential improvements include:

-   Integrating a secure third-party payment provider.
-   Adding appointment reminders and customer notifications.
-   Supporting appointment rescheduling.
-   Introducing complete barber shift and leave management.
-   Expanding automated functional and security tests.
-   Completing accessibility and device-size testing.
-   Improving documentation of the Android/Firebase and website/MySQL
    architecture.
-   Considering light and dark theme selection and further accessibility
    refinements in the Android application.

## Development Team

**Work Integrated Learning 3B -- Chico's Barbershop**

-   Babalo Jama
-   Liam Frans

The project was completed as a group development project. Team members
contributed to the development and refinement of the wider barbershop
solution.

## AI Development Support

ChatGPT was used as a development support tool during the project. It
assisted with understanding programming concepts, troubleshooting
errors, improving code structure, researching technical approaches and
refining selected application features and project documentation. AI
assistance supported the development process; implementation decisions,
integration and project work remained part of the team's development
activities.

## References and Useful Resources

-   [Chico's Barbershop GitHub
    Repository](https://github.com/Bilorsa/Chico-s-Barbershop-System)
-   [PHP Manual](https://www.php.net/manual/en/)
-   [MySQL Documentation](https://dev.mysql.com/doc/)
-   [Android Developers Documentation](https://developer.android.com/)
-   [Firebase Documentation](https://firebase.google.com/docs)
-   [GitHub Actions Documentation](https://docs.github.com/en/actions)
-   [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)

------------------------------------------------------------------------

**Project:** Chico's Barbershop System\
**Module:** Work Integrated Learning 3B\
**Institution:** Rosebank International\
**Course code:** XISD6329
