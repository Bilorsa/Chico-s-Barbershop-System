# Chico's Barbershop Management System

Chico's Barbershop is a barbershop management solution comprising an
Android application and a companion PHP/MySQL booking website. It
supports customer access, appointment booking and administrative
management.

> **CI status:** The reviewed Android GitHub Actions run completed
> successfully and built the debug APK. The log also showed Gradle
> unit-test tasks marked `NO-SOURCE`; therefore, that run does not prove
> that Android unit-test cases were executed.

## Project components

### Android application

The Android manifest registers activities for: - Splash screen -
Customer login and registration - Customer dashboard and profile -
Appointment booking and My Bookings - Administration dashboard -
Customer management - Appointment management - Payment management -
Reports and settings

### PHP booking website

The companion website provides a public-facing barbershop experience and
administration pages. The website documentation describes customer
registration, login/logout, service and barber selection, appointment
booking, booking confirmation, booking history, and administration of
appointments, services and barbers.

## Technology stack

  Area                        Technologies
  --------------------------- -------------------------------------
  Android                     Android Studio, Android SDK, Gradle
  Android CI                  GitHub Actions, Java 17
  Website backend             PHP, PDO
  Website database            MySQL / MariaDB
  Website frontend            HTML5, CSS3, JavaScript
  Local website environment   XAMPP, Apache, phpMyAdmin
  Source control              Git and GitHub

The project feedback and demonstration describe Firebase integration in
the mobile solution. Document the exact Firebase services and how
Firebase relates to the website's MySQL database in the relevant
technical documentation. Never commit private keys, service-account
files or secrets.

## Repository structure

The Android Gradle project is at the repository root. The GitHub Actions
workflow is:

``` text
.github/workflows/main.yml
```

The Android manifest is:

``` text
app/src/main/AndroidManifest.xml
```

The PHP website is organised into areas such as `admin/`, `assets/`,
`config/`, `database/` and `includes/`, with entry pages for the
homepage, booking, login, registration and customer bookings. Confirm
the exact website folder and paths in the current checkout before
following the setup steps.

## Website setup with XAMPP

1.  Install and open XAMPP.
2.  Start Apache and MySQL.
3.  Place the PHP website folder inside XAMPP's `htdocs` directory.
4.  Open phpMyAdmin at `http://localhost/phpmyadmin`.
5.  Create the database using the name expected by the website's
    database configuration.
6.  Import the SQL schema supplied with the website.
7.  Configure the database host, database name, username and password in
    the website's database configuration file.
8.  Open `http://localhost/<website-folder>/`.

A typical XAMPP setup may use host `127.0.0.1`, username `root` and an
empty password, but only use these values if they match your local MySQL
configuration. Confirm the database name in the project's configuration
and SQL schema; do not assume a default.

### Website functional checks

-   Homepage and navigation load.
-   Customer registration, login and logout work.
-   A customer can select a service and barber.
-   A valid future appointment can be submitted and saved.
-   A booking confirmation is displayed.
-   Customers can view their bookings.
-   An authorised administrator can access the administration area.
-   Appointment, service and barber management pages work.
-   Invalid input and unavailable appointment times are rejected
    appropriately.

## Android setup

1.  Install Android Studio.
2.  Open the repository in Android Studio.
3.  Install the Android SDK platform and build tools required by the
    Gradle project.
4.  Use JDK 17 to match the current CI configuration.
5.  Allow Gradle to sync and download dependencies.
6.  Configure required Firebase settings locally, following the app's
    approved setup.
7.  Select an emulator or connected Android device and run the
    application.

Do not commit `local.properties`: it contains a machine-specific Android
SDK path. Keep it local and add it to `.gitignore`.

Gradle commands from the repository root:

``` bash
./gradlew test
./gradlew assembleDebug
```

On Windows PowerShell:

``` powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

## Testing and continuous integration

The GitHub Actions workflow is named **Chico Android CI** and is stored
in `.github/workflows/main.yml`. It runs for pushes to `main` or
`master`, pull requests targeting those branches, and manual
`workflow_dispatch` runs.

The workflow: 1. Checks out the repository. 2. Sets up Java 17. 3.
Verifies the Android SDK. 4. Sets up Gradle and makes the wrapper
executable. 5. Runs `./gradlew test`. 6. Builds the debug APK using
`./gradlew assembleDebug`. 7. Uploads available test reports. 8. Uploads
the debug APK after a successful build.

### Important interpretation of the reviewed run

The reviewed log included:

``` text
:app:compileDebugUnitTestKotlin NO-SOURCE
:app:testDebugUnitTest NO-SOURCE
:app:testReleaseUnitTest NO-SOURCE
```

`NO-SOURCE` means Gradle found no source files for those test tasks. The
green workflow result confirms that the workflow completed and the APK
build succeeded, but it must not be presented as evidence that unit-test
cases passed. Add real test classes, rerun the workflow and inspect the
report for the number of tests executed, passed, failed and skipped.

### Manual test record

Record actual outcomes and attach screenshots or other evidence. Do not
mark a test as passed until it has been executed.

  --------------------------------------------------------------------------
  Area              Test                 Expected result   Actual result
  ----------------- -------------------- ----------------- -----------------
  Android           Launch application   App starts and    To be recorded
                                         displays its      
                                         opening screen    

  Android           Customer             Valid details are To be recorded
                    registration         accepted          

  Android           Customer login       Valid credentials To be recorded
                                         authenticate the  
                                         user              

  Android           Appointment booking  Valid booking     To be recorded
                                         details are       
                                         handled           

  Android           My Bookings          Customer can view To be recorded
                                         their bookings    

  Android           Administration       Authorised        To be recorded
                                         administrator     
                                         accesses          
                                         management        
                                         screens           

  Website           Homepage             Website loads     To be recorded
                                         without PHP       
                                         errors            

  Website           Registration/login   Customer          To be recorded
                                         authentication    
                                         works             

  Website           Appointment booking  Valid booking is  To be recorded
                                         stored            

  Website           Availability         Past, invalid or  To be recorded
                                         conflicting slots 
                                         are rejected      
                                         where applicable  

  Website           Administration       Appointment,      To be recorded
                                         barber and        
                                         service           
                                         management work   

  Database          Connection           Website connects  To be recorded
                                         to configured     
                                         MySQL database    
  --------------------------------------------------------------------------

## Security and configuration

-   Keep database credentials and API keys out of source control.
-   Do not commit `local.properties`, Firebase private keys or
    service-account credentials.
-   Use PDO prepared statements for database operations.
-   Hash passwords using PHP password-hashing functions.
-   Validate input on the server, not only in the browser.
-   Restrict administration functions to authorised users.
-   Use HTTPS and secure session-cookie settings in production.
-   Use a database account with only the permissions required.
-   Back up the database before importing scripts that may recreate or
    delete tables.
-   Replace development and seeded credentials before deployment.

## Known limitations and future improvements

Recommended work: - Add and execute meaningful Android unit tests; the
reviewed CI log reports test tasks as `NO-SOURCE`. - Add automated PHP
syntax and website tests to GitHub Actions. - Document API endpoints,
request/response formats and authentication requirements. - Document the
Firebase/MySQL relationship and provide a complete database model/ERD. -
Record functional test results and retain test reports. - Review Android
API deprecation warnings. - Review GitHub Actions runtime/deprecation
warnings and update action versions using official release guidance. -
Consider appointment notifications, rescheduling, cancellation, improved
reporting and secure online payments.

## Demonstration

[Watch the Chico's Barbershop
demonstration](https://advtechonline-my.sharepoint.com/:v:/r/personal/st10460566_rcconnect_edu_za/_layouts/15/stream.aspx?id=%2Fpersonal%2Fst10460566_rcconnect_edu_za%2FDocuments%2FChico%20Demonstration%20Video.mp4&nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&share=cQqJKBcxEcAbSrncMK8qKZB7EgUCPKMz2n_m4OnZFzfMeY2g3g)



