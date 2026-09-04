package com.example.chico

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ChicoDb(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "chico.db"

        // Increased from 1 to 2 so existing databases are upgraded
        private const val DATABASE_VERSION = 2

        // =====================================================
        // USERS TABLE
        // =====================================================

        const val TABLE_USERS = "users"

        const val USER_ID = "id"
        const val USER_NAME = "name"
        const val USER_PHONE = "phone"
        const val USER_EMAIL = "email"
        const val USER_PASSWORD = "password"
        const val USER_ROLE = "role"

        // =====================================================
        // CUSTOMERS TABLE
        // =====================================================

        const val TABLE_CUSTOMERS = "customers"

        const val CUSTOMER_ID = "id"
        const val CUSTOMER_NAME = "name"
        const val CUSTOMER_PHONE = "phone"
        const val CUSTOMER_EMAIL = "email"

        // =====================================================
        // SERVICES TABLE
        // =====================================================

        const val TABLE_SERVICES = "services"

        const val SERVICE_ID = "id"
        const val SERVICE_NAME = "name"
        const val SERVICE_PRICE = "price"
        const val SERVICE_DURATION = "duration"

        // =====================================================
        // APPOINTMENTS TABLE
        // =====================================================

        const val TABLE_APPOINTMENTS = "appointments"

        const val APPOINTMENT_ID = "id"
        const val APPOINTMENT_CUSTOMER_ID = "customer_id"
        const val APPOINTMENT_SERVICE_ID = "service_id"
        const val APPOINTMENT_DATE = "appointment_date"
        const val APPOINTMENT_TIME = "appointment_time"
        const val APPOINTMENT_STATUS = "status"

        // =====================================================
        // PAYMENTS TABLE
        // =====================================================

        const val TABLE_PAYMENTS = "payments"

        const val PAYMENT_ID = "id"
        const val PAYMENT_APPOINTMENT_ID = "appointment_id"
        const val PAYMENT_AMOUNT = "amount"
        const val PAYMENT_DATE = "payment_date"
        const val PAYMENT_METHOD = "payment_method"
    }

    // =========================================================
    // CREATE DATABASE
    // =========================================================

    override fun onCreate(db: SQLiteDatabase) {

        // =====================================================
        // USERS
        // =====================================================

        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $USER_NAME TEXT NOT NULL,
                $USER_PHONE TEXT NOT NULL,
                $USER_EMAIL TEXT NOT NULL UNIQUE,
                $USER_PASSWORD TEXT NOT NULL,
                $USER_ROLE TEXT NOT NULL DEFAULT 'CUSTOMER'
            )
            """.trimIndent()
        )

        // =====================================================
        // CUSTOMERS
        // =====================================================

        db.execSQL(
            """
            CREATE TABLE $TABLE_CUSTOMERS (
                $CUSTOMER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CUSTOMER_NAME TEXT NOT NULL,
                $CUSTOMER_PHONE TEXT NOT NULL,
                $CUSTOMER_EMAIL TEXT NOT NULL UNIQUE
            )
            """.trimIndent()
        )

        // =====================================================
        // SERVICES
        // =====================================================

        db.execSQL(
            """
            CREATE TABLE $TABLE_SERVICES (
                $SERVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $SERVICE_NAME TEXT NOT NULL,
                $SERVICE_PRICE REAL NOT NULL,
                $SERVICE_DURATION INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // =====================================================
        // APPOINTMENTS
        // =====================================================

        db.execSQL(
            """
            CREATE TABLE $TABLE_APPOINTMENTS (
                $APPOINTMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $APPOINTMENT_CUSTOMER_ID INTEGER NOT NULL,
                $APPOINTMENT_SERVICE_ID INTEGER NOT NULL,
                $APPOINTMENT_DATE TEXT NOT NULL,
                $APPOINTMENT_TIME TEXT NOT NULL,
                $APPOINTMENT_STATUS TEXT NOT NULL DEFAULT 'BOOKED',

                FOREIGN KEY ($APPOINTMENT_CUSTOMER_ID)
                    REFERENCES $TABLE_CUSTOMERS($CUSTOMER_ID),

                FOREIGN KEY ($APPOINTMENT_SERVICE_ID)
                    REFERENCES $TABLE_SERVICES($SERVICE_ID),

                UNIQUE (
                    $APPOINTMENT_DATE,
                    $APPOINTMENT_TIME
                )
            )
            """.trimIndent()
        )

        // =====================================================
        // PAYMENTS
        // =====================================================

        db.execSQL(
            """
            CREATE TABLE $TABLE_PAYMENTS (
                $PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $PAYMENT_APPOINTMENT_ID INTEGER NOT NULL,
                $PAYMENT_AMOUNT REAL NOT NULL,
                $PAYMENT_DATE TEXT NOT NULL,
                $PAYMENT_METHOD TEXT NOT NULL,

                FOREIGN KEY ($PAYMENT_APPOINTMENT_ID)
                    REFERENCES $TABLE_APPOINTMENTS($APPOINTMENT_ID)
            )
            """.trimIndent()
        )

        // =====================================================
        // DEFAULT SERVICES
        // =====================================================

        insertDefaultServices(db)

        // =====================================================
        // CREATE ADMIN ACCOUNT
        // =====================================================

        createAdminAccount(db)
    }

    // =========================================================
    // DEFAULT SERVICES
    // =========================================================

    private fun insertDefaultServices(db: SQLiteDatabase) {

        val haircut = ContentValues().apply {
            put(SERVICE_NAME, "Haircut")
            put(SERVICE_PRICE, 100.00)
            put(SERVICE_DURATION, 30)
        }

        db.insert(
            TABLE_SERVICES,
            null,
            haircut
        )

        val fade = ContentValues().apply {
            put(SERVICE_NAME, "Fade")
            put(SERVICE_PRICE, 120.00)
            put(SERVICE_DURATION, 45)
        }

        db.insert(
            TABLE_SERVICES,
            null,
            fade
        )

        val beard = ContentValues().apply {
            put(SERVICE_NAME, "Beard Trim")
            put(SERVICE_PRICE, 70.00)
            put(SERVICE_DURATION, 20)
        }

        db.insert(
            TABLE_SERVICES,
            null,
            beard
        )

        val haircutBeard = ContentValues().apply {
            put(SERVICE_NAME, "Haircut + Beard")
            put(SERVICE_PRICE, 160.00)
            put(SERVICE_DURATION, 50)
        }

        db.insert(
            TABLE_SERVICES,
            null,
            haircutBeard
        )

        val kids = ContentValues().apply {
            put(SERVICE_NAME, "Kids Haircut")
            put(SERVICE_PRICE, 80.00)
            put(SERVICE_DURATION, 30)
        }

        db.insert(
            TABLE_SERVICES,
            null,
            kids
        )
    }

    // =========================================================
    // CREATE ADMIN ACCOUNT
    // =========================================================

    private fun createAdminAccount(db: SQLiteDatabase) {

        val adminEmail = "admin@chico.com"

        // Check if admin already exists
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(USER_ID),
            "$USER_EMAIL = ?",
            arrayOf(adminEmail),
            null,
            null,
            null
        )

        val adminExists = cursor.moveToFirst()

        cursor.close()

        // Only create the account if it doesn't already exist
        if (!adminExists) {

            val adminValues = ContentValues().apply {

                put(
                    USER_NAME,
                    "Chico Admin"
                )

                put(
                    USER_PHONE,
                    "0000000000"
                )

                put(
                    USER_EMAIL,
                    adminEmail
                )

                put(
                    USER_PASSWORD,
                    "ChicoAdmin123!"
                )

                put(
                    USER_ROLE,
                    "ADMIN"
                )
            }

            db.insert(
                TABLE_USERS,
                null,
                adminValues
            )
        }
    }

    // =========================================================
    // DATABASE UPGRADE
    // =========================================================

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        // IMPORTANT:
        // We do NOT delete any existing data.

        if (oldVersion < 2) {

            // Add the pre-created admin account
            createAdminAccount(db)
        }
    }

    // =========================================================
    // INSERT
    // =========================================================

    fun insert(
        table: String,
        values: ContentValues
    ): Long {

        val db = writableDatabase

        return db.insert(
            table,
            null,
            values
        )
    }

    // =========================================================
    // DELETE
    // =========================================================

    fun delete(
        table: String,
        whereClause: String,
        whereArgs: Array<String>
    ): Int {

        val db = writableDatabase

        return db.delete(
            table,
            whereClause,
            whereArgs
        )
    }

    // =========================================================
    // CHECK IF EMAIL EXISTS
    // =========================================================

    fun emailExists(
        email: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(USER_ID),
            "$USER_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }

    // =========================================================
    // CHECK IF USER EXISTS
    // =========================================================

    fun userExists(
        email: String
    ): Boolean {

        return emailExists(email)
    }

    // =========================================================
    // GET USER
    // =========================================================

    fun getUser(
        email: String,
        password: String
    ): Long {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(USER_ID),
            "$USER_EMAIL = ? AND $USER_PASSWORD = ?",
            arrayOf(
                email,
                password
            ),
            null,
            null,
            null
        )

        var userId = -1L

        if (cursor.moveToFirst()) {

            userId = cursor.getLong(
                cursor.getColumnIndexOrThrow(USER_ID)
            )
        }

        cursor.close()

        return userId
    }

    // =========================================================
    // GET USER ROLE
    // =========================================================

    fun getUserRole(
        email: String
    ): String? {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(USER_ROLE),
            "$USER_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        var role: String? = null

        if (cursor.moveToFirst()) {

            role = cursor.getString(
                cursor.getColumnIndexOrThrow(USER_ROLE)
            )
        }

        cursor.close()

        return role
    }

    // =========================================================
    // CREATE CUSTOMER PROFILE
    // =========================================================

    fun createCustomer(
        name: String,
        phone: String,
        email: String
    ): Long {

        val values = ContentValues().apply {

            put(
                CUSTOMER_NAME,
                name
            )

            put(
                CUSTOMER_PHONE,
                phone
            )

            put(
                CUSTOMER_EMAIL,
                email
            )
        }

        return insert(
            TABLE_CUSTOMERS,
            values
        )
    }

    // =========================================================
    // CHECK IF CUSTOMER EXISTS
    // =========================================================

    fun customerExists(
        email: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_CUSTOMERS,
            arrayOf(CUSTOMER_ID),
            "$CUSTOMER_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }

    // =========================================================
    // GET CUSTOMER ID
    // =========================================================

    fun getCustomerId(
        email: String
    ): Long {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_CUSTOMERS,
            arrayOf(CUSTOMER_ID),
            "$CUSTOMER_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        var customerId = -1L

        if (cursor.moveToFirst()) {

            customerId = cursor.getLong(
                cursor.getColumnIndexOrThrow(CUSTOMER_ID)
            )
        }

        cursor.close()

        return customerId
    }

    // =========================================================
    // CHECK APPOINTMENT
    // =========================================================

    fun appointmentExists(
        date: String,
        time: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_APPOINTMENTS,
            arrayOf(APPOINTMENT_ID),
            """
            $APPOINTMENT_DATE = ?
            AND $APPOINTMENT_TIME = ?
            AND $APPOINTMENT_STATUS != ?
            """.trimIndent(),
            arrayOf(
                date,
                time,
                "CANCELLED"
            ),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }
}