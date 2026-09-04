package com.example.chico

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class BookingActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private lateinit var serviceSpinner: Spinner
    private lateinit var dateButton: TextView
    private lateinit var timeButton: TextView
    private lateinit var confirmButton: TextView

    private lateinit var selectedDateText: TextView
    private lateinit var selectedTimeText: TextView

    private var selectedDate = ""
    private var selectedTime = ""

    private var customerEmail = ""

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val white = Color.WHITE
    private val grey = Color.rgb(170, 170, 170)

    private val serviceNames = mutableListOf<String>()
    private val serviceIds = mutableListOf<Long>()

    // =========================================================
    // DP HELPER
    // =========================================================

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        db = ChicoDb(this)

        customerEmail =
            intent.getStringExtra("email")
                ?: intent.getStringExtra("registered_email")
                        ?: getSharedPreferences(
                    "ChicoPrefs",
                    MODE_PRIVATE
                ).getString("email", "")
                        ?: ""

        createBookingScreen()

        loadServices()
    }

    // =========================================================
    // CREATE BOOKING SCREEN
    // =========================================================

    private fun createBookingScreen() {

        val scrollView = NestedScrollView(this)

        scrollView.setBackgroundColor(
            backgroundColor
        )

        scrollView.clipToPadding = false

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL

        // Initial padding.
        // WindowInsets below will adjust the top and bottom.
        root.setPadding(
            dp(24),
            dp(25),
            dp(24),
            dp(35)
        )

        // IMPORTANT:
        // Do NOT use NestedScrollView.LayoutParams here.
        scrollView.addView(root)

        // =====================================================
        // STATUS BAR / NOTCH SPACING
        // =====================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            scrollView
        ) { _, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            root.setPadding(
                dp(24),
                systemBars.top + dp(25),
                dp(24),
                systemBars.bottom + dp(35)
            )

            insets
        }

        ViewCompat.requestApplyInsets(
            scrollView
        )

        // =====================================================
        // TITLE
        // =====================================================

        val title = TextView(this)

        title.text = "BOOK APPOINTMENT"

        title.textSize = 25f

        title.typeface = Typeface.DEFAULT_BOLD

        title.setTextColor(white)

        title.includeFontPadding = true

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(root, 8)

        // =====================================================
        // SUBTITLE
        // =====================================================

        val subtitle = TextView(this)

        subtitle.text =
            "Choose your service, date and time."

        subtitle.textSize = 14f

        subtitle.setTextColor(grey)

        subtitle.includeFontPadding = true

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(root, 30)

        // =====================================================
        // SERVICE
        // =====================================================

        root.addView(
            createLabel("SERVICE")
        )

        addSpace(root, 8)

        serviceSpinner = Spinner(this)

        serviceSpinner.background =
            createRoundedBackground(
                cardColor,
                12f
            )

        serviceSpinner.setPadding(
            dp(12),
            0,
            dp(12),
            0
        )

        root.addView(
            serviceSpinner,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        addSpace(root, 28)

        // =====================================================
        // DATE
        // =====================================================

        root.addView(
            createLabel("DATE")
        )

        addSpace(root, 8)

        dateButton =
            createActionButton("SELECT DATE")

        dateButton.setOnClickListener {
            showDatePicker()
        }

        root.addView(dateButton)

        addSpace(root, 8)

        selectedDateText =
            createSelectedText("No date selected")

        root.addView(selectedDateText)

        addSpace(root, 28)

        // =====================================================
        // TIME
        // =====================================================

        root.addView(
            createLabel("TIME")
        )

        addSpace(root, 8)

        timeButton =
            createActionButton("SELECT TIME")

        timeButton.setOnClickListener {
            showTimePicker()
        }

        root.addView(timeButton)

        addSpace(root, 8)

        selectedTimeText =
            createSelectedText("No time selected")

        root.addView(selectedTimeText)

        addSpace(root, 35)

        // =====================================================
        // CONFIRM BOOKING
        // =====================================================

        confirmButton =
            createConfirmButton("CONFIRM BOOKING")

        confirmButton.setOnClickListener {
            createBooking()
        }

        root.addView(confirmButton)

        addSpace(root, 15)

        // =====================================================
        // BACK
        // =====================================================

        val backButton =
            createBackButton("BACK")

        backButton.setOnClickListener {
            finish()
        }

        root.addView(backButton)

        addSpace(root, 20)

        setContentView(scrollView)
    }

    // =========================================================
    // LOAD SERVICES
    // =========================================================

    private fun loadServices() {

        serviceNames.clear()
        serviceIds.clear()

        val cursor =
            db.readableDatabase.rawQuery(
                """
                SELECT id, name
                FROM services
                ORDER BY id
                """.trimIndent(),
                null
            )

        while (cursor.moveToNext()) {

            val id =
                cursor.getLong(
                    cursor.getColumnIndexOrThrow("id")
                )

            val name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                )

            serviceIds.add(id)
            serviceNames.add(name)
        }

        cursor.close()

        if (serviceNames.isEmpty()) {

            serviceNames.add(
                "No services available"
            )
        }

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                serviceNames
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        serviceSpinner.adapter = adapter
    }

    // =========================================================
    // DATE PICKER
    // =========================================================

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val dialog =
            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val selectedCalendar =
                        Calendar.getInstance()

                    selectedCalendar.set(
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    )

                    val format =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        )

                    selectedDate =
                        format.format(
                            selectedCalendar.time
                        )

                    dateButton.text =
                        selectedDate

                    selectedDateText.text =
                        "Selected date: $selectedDate"

                    selectedDateText.setTextColor(
                        accentColor
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

        dialog.datePicker.minDate =
            System.currentTimeMillis()

        dialog.show()
    }

    // =========================================================
    // TIME PICKER
    // =========================================================

    private fun showTimePicker() {

        val calendar = Calendar.getInstance()

        val dialog =
            TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    selectedTime =
                        String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                        )

                    timeButton.text =
                        selectedTime

                    selectedTimeText.text =
                        "Selected time: $selectedTime"

                    selectedTimeText.setTextColor(
                        accentColor
                    )
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )

        dialog.show()
    }

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    private fun createBooking() {

        if (customerEmail.isEmpty()) {

            Toast.makeText(
                this,
                "Customer account could not be identified.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (selectedDate.isEmpty()) {

            Toast.makeText(
                this,
                "Please select a date.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (selectedTime.isEmpty()) {

            Toast.makeText(
                this,
                "Please select a time.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (serviceIds.isEmpty()) {

            Toast.makeText(
                this,
                "No services are available.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val selectedPosition =
            serviceSpinner.selectedItemPosition

        if (
            selectedPosition < 0 ||
            selectedPosition >= serviceIds.size
        ) {

            Toast.makeText(
                this,
                "Please select a service.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val selectedServiceId =
            serviceIds[selectedPosition]

        // =====================================================
        // DOUBLE BOOKING CHECK
        // =====================================================

        if (
            db.appointmentExists(
                selectedDate,
                selectedTime
            )
        ) {

            Toast.makeText(
                this,
                "This time is already booked. Please choose another time.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // =====================================================
        // GET CUSTOMER
        // =====================================================

        val customerId =
            db.getCustomerId(customerEmail)

        if (customerId == -1L) {

            Toast.makeText(
                this,
                "Customer profile could not be found.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // =====================================================
        // SAVE APPOINTMENT
        // =====================================================

        val values =
            ContentValues().apply {

                put(
                    ChicoDb.APPOINTMENT_CUSTOMER_ID,
                    customerId
                )

                put(
                    ChicoDb.APPOINTMENT_SERVICE_ID,
                    selectedServiceId
                )

                put(
                    ChicoDb.APPOINTMENT_DATE,
                    selectedDate
                )

                put(
                    ChicoDb.APPOINTMENT_TIME,
                    selectedTime
                )

                put(
                    ChicoDb.APPOINTMENT_STATUS,
                    "BOOKED"
                )
            }

        val result =
            db.insert(
                ChicoDb.TABLE_APPOINTMENTS,
                values
            )

        // =====================================================
        // RESULT
        // =====================================================

        if (result != -1L) {

            Toast.makeText(
                this,
                "Appointment booked successfully!",
                Toast.LENGTH_LONG
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "Booking failed. Please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================================================
    // LABEL
    // =========================================================

    private fun createLabel(
        textValue: String
    ): TextView {

        val label = TextView(this)

        label.text = textValue

        label.textSize = 11f

        label.typeface =
            Typeface.DEFAULT_BOLD

        label.letterSpacing = 0.15f

        label.setTextColor(
            accentColor
        )

        label.includeFontPadding = true

        label.minHeight = 0
        label.minimumHeight = 0

        return label
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private fun createActionButton(
        textValue: String
    ): TextView {

        val button = TextView(this)

        button.text = textValue

        button.textSize = 15f

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.setTextColor(white)

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding = true

        button.minHeight = 0
        button.minimumHeight = 0

        button.isClickable = true
        button.isFocusable = true

        button.background =
            createRoundedBackground(
                cardColor,
                12f
            )

        button.setPadding(
            dp(16),
            dp(12),
            dp(16),
            dp(12)
        )

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(64)
            )

        return button
    }

    // =========================================================
    // CONFIRM BUTTON
    // =========================================================

    private fun createConfirmButton(
        textValue: String
    ): TextView {

        val button = TextView(this)

        button.text = textValue

        button.textSize = 15f

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.setTextColor(white)

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding = true

        button.minHeight = 0
        button.minimumHeight = 0

        button.isClickable = true
        button.isFocusable = true

        button.background =
            createRoundedBackground(
                accentColor,
                12f
            )

        button.setPadding(
            dp(16),
            dp(12),
            dp(16),
            dp(12)
        )

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(64)
            )

        return button
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================

    private fun createBackButton(
        textValue: String
    ): TextView {

        val button = TextView(this)

        button.text = textValue

        button.textSize = 15f

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.setTextColor(white)

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding = true

        button.minHeight = 0
        button.minimumHeight = 0

        button.isClickable = true
        button.isFocusable = true

        button.background =
            createRoundedBackground(
                cardColor,
                12f
            )

        button.setPadding(
            dp(16),
            dp(12),
            dp(16),
            dp(12)
        )

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(64)
            )

        return button
    }

    // =========================================================
    // SELECTED DATE / TIME TEXT
    // =========================================================

    private fun createSelectedText(
        textValue: String
    ): TextView {

        val text = TextView(this)

        text.text = textValue

        text.textSize = 13f

        text.setTextColor(grey)

        text.includeFontPadding = true

        text.minHeight = 0
        text.minimumHeight = 0

        text.setPadding(
            dp(5),
            dp(2),
            0,
            dp(2)
        )

        return text
    }

    // =========================================================
    // ROUNDED BACKGROUND
    // =========================================================

    private fun createRoundedBackground(
        color: Int,
        radius: Float
    ): GradientDrawable {

        val drawable =
            GradientDrawable()

        drawable.shape =
            GradientDrawable.RECTANGLE

        drawable.setColor(color)

        drawable.cornerRadius =
            radius *
                    resources.displayMetrics.density

        return drawable
    }

    // =========================================================
    // SPACE
    // =========================================================

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space = View(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)
            )
        )
    }

    // =========================================================
    // CLOSE DATABASE
    // =========================================================

    override fun onDestroy() {

        db.close()

        super.onDestroy()
    }
}