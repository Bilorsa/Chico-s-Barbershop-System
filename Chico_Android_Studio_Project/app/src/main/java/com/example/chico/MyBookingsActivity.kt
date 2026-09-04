package com.example.chico

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb
    private lateinit var bookingContainer: LinearLayout

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val redColor = Color.rgb(220, 50, 50)
    private val white = Color.WHITE
    private val grey = Color.rgb(170, 170, 170)

    // =========================================================
    // DP HELPER
    // =========================================================

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)

        createScreen()
        loadBookings()
    }

    // =========================================================
    // CREATE SCREEN
    // =========================================================

    private fun createScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        // =====================================================
        // HEADER
        // =====================================================

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(24),
                dp(55),
                dp(24),
                dp(20)
            )
        }

        val title = TextView(this).apply {

            text = "MY BOOKINGS"

            textSize = 25f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)

            includeFontPadding = true

            gravity = Gravity.CENTER_VERTICAL
        }

        header.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this).apply {

            text = "View and manage your upcoming appointments."

            textSize = 14f

            setTextColor(grey)

            includeFontPadding = true

            setPadding(
                0,
                dp(6),
                0,
                0
            )
        }

        header.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(header)

        // =====================================================
        // SCROLL VIEW
        // =====================================================

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
        }

        bookingContainer = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(18),
                dp(5),
                dp(18),
                dp(25)
            )
        }

        scrollView.addView(bookingContainer)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // =====================================================
        // BACK TO DASHBOARD
        // =====================================================

        val backButton = TextView(this).apply {

            text = "BACK TO DASHBOARD"

            textSize = 14f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)

            gravity = Gravity.CENTER

            includeFontPadding = true

            setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
            )

            background = createRoundedBackground(
                Color.rgb(45, 45, 45),
                dp(10)
            )

            setOnClickListener {
                finish()
            }
        }

        val backParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(58)
        ).apply {

            setMargins(
                dp(18),
                dp(8),
                dp(18),
                dp(18)
            )
        }

        root.addView(
            backButton,
            backParams
        )

        setContentView(root)
    }

    // =========================================================
    // LOAD BOOKINGS
    // =========================================================

    private fun loadBookings() {

        bookingContainer.removeAllViews()

        val email =
            intent.getStringExtra("email")
                ?: intent.getStringExtra("registered_email")

        if (email.isNullOrEmpty()) {

            showMessage(
                "Customer account could not be identified."
            )

            return
        }

        val customerId = db.getCustomerId(email)

        if (customerId == -1L) {

            showMessage(
                "Customer profile could not be found."
            )

            return
        }

        val cursor = db.readableDatabase.rawQuery(
            """
            SELECT
                appointments.id,
                appointments.appointment_date,
                appointments.appointment_time,
                appointments.status,
                services.name,
                services.price
            FROM appointments
            INNER JOIN services
                ON appointments.service_id = services.id
            WHERE appointments.customer_id = ?
            ORDER BY
                appointments.appointment_date,
                appointments.appointment_time
            """.trimIndent(),
            arrayOf(customerId.toString())
        )

        if (!cursor.moveToFirst()) {

            cursor.close()

            showMessage(
                "You currently have no appointments."
            )

            return
        }

        do {

            val appointmentId =
                cursor.getLong(
                    cursor.getColumnIndexOrThrow("id")
                )

            val date =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "appointment_date"
                    )
                )

            val time =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "appointment_time"
                    )
                )

            val status =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "status"
                    )
                )

            val service =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "name"
                    )
                )

            val price =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "price"
                    )
                )

            createBookingCard(
                appointmentId,
                service,
                date,
                time,
                price,
                status
            )

        } while (cursor.moveToNext())

        cursor.close()
    }

    // =========================================================
    // BOOKING CARD
    // =========================================================

    private fun createBookingCard(
        appointmentId: Long,
        service: String,
        date: String,
        time: String,
        price: Double,
        status: String
    ) {

        val card = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setBackgroundColor(cardColor)

            setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
            )
        }

        // =====================================================
        // SERVICE NAME
        // =====================================================

        val serviceText = TextView(this).apply {

            text = service

            textSize = 19f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)

            includeFontPadding = true

            gravity = Gravity.CENTER_VERTICAL
        }

        card.addView(
            serviceText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // =====================================================
        // DATE
        // =====================================================

        val dateText = TextView(this).apply {

            text = "DATE: $date"

            textSize = 14f

            setTextColor(grey)

            includeFontPadding = true

            setPadding(
                0,
                dp(10),
                0,
                0
            )
        }

        card.addView(dateText)

        // =====================================================
        // TIME
        // =====================================================

        val timeText = TextView(this).apply {

            text = "TIME: $time"

            textSize = 14f

            setTextColor(grey)

            includeFontPadding = true

            setPadding(
                0,
                dp(5),
                0,
                0
            )
        }

        card.addView(timeText)

        // =====================================================
        // PRICE
        // =====================================================

        val priceText = TextView(this).apply {

            text = String.format(
                "PRICE: R%.2f",
                price
            )

            textSize = 14f

            setTextColor(grey)

            includeFontPadding = true

            setPadding(
                0,
                dp(5),
                0,
                0
            )
        }

        card.addView(priceText)

        // =====================================================
        // STATUS
        // =====================================================

        val statusText = TextView(this).apply {

            text = "STATUS: $status"

            textSize = 14f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(
                if (status == "CANCELLED") {
                    grey
                } else {
                    accentColor
                }
            )

            includeFontPadding = true

            setPadding(
                0,
                dp(7),
                0,
                dp(15)
            )
        }

        card.addView(statusText)

        // =====================================================
        // CANCEL BUTTON
        // =====================================================

        if (status != "CANCELLED") {

            val cancelButton = TextView(this).apply {

                text = "CANCEL APPOINTMENT"

                textSize = 14f

                typeface = Typeface.DEFAULT_BOLD

                setTextColor(Color.WHITE)

                gravity = Gravity.CENTER

                includeFontPadding = true

                setPadding(
                    dp(10),
                    dp(10),
                    dp(10),
                    dp(10)
                )

                background = createRoundedBackground(
                    redColor,
                    dp(10)
                )

                setOnClickListener {

                    cancelAppointment(
                        appointmentId
                    )
                }
            }

            card.addView(
                cancelButton,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(58)
                )
            )

        } else {

            // =================================================
            // CANCELLED LABEL
            // =================================================

            val cancelledText = TextView(this).apply {

                text = "APPOINTMENT CANCELLED"

                textSize = 14f

                typeface = Typeface.DEFAULT_BOLD

                setTextColor(grey)

                gravity = Gravity.CENTER

                includeFontPadding = true

                setPadding(
                    dp(10),
                    dp(10),
                    dp(10),
                    dp(10)
                )

                background = createRoundedBackground(
                    Color.rgb(45, 45, 45),
                    dp(10)
                )
            }

            card.addView(
                cancelledText,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(58)
                )
            )
        }

        // =====================================================
        // ADD CARD TO SCREEN
        // =====================================================

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {

            setMargins(
                0,
                0,
                0,
                dp(16)
            )
        }

        bookingContainer.addView(
            card,
            cardParams
        )
    }

    // =========================================================
    // CANCEL APPOINTMENT
    // =========================================================

    private fun cancelAppointment(
        appointmentId: Long
    ) {

        val values = ContentValues().apply {

            put(
                ChicoDb.APPOINTMENT_STATUS,
                "CANCELLED"
            )
        }

        val result = db.writableDatabase.update(
            ChicoDb.TABLE_APPOINTMENTS,
            values,
            "${ChicoDb.APPOINTMENT_ID} = ?",
            arrayOf(
                appointmentId.toString()
            )
        )

        if (result > 0) {

            Toast.makeText(
                this,
                "Appointment cancelled.",
                Toast.LENGTH_SHORT
            ).show()

            loadBookings()

        } else {

            Toast.makeText(
                this,
                "Could not cancel appointment.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================================================
    // EMPTY / ERROR MESSAGE
    // =========================================================

    private fun showMessage(
        message: String
    ) {

        val messageText = TextView(this).apply {

            text = message

            textSize = 16f

            setTextColor(grey)

            gravity = Gravity.CENTER

            includeFontPadding = true

            setPadding(
                dp(20),
                dp(50),
                dp(20),
                dp(50)
            )
        }

        bookingContainer.addView(
            messageText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    // =========================================================
    // ROUNDED BACKGROUND
    // =========================================================

    private fun createRoundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    // =========================================================
    // CLOSE DATABASE
    // =========================================================

    override fun onDestroy() {

        db.close()

        super.onDestroy()
    }
}