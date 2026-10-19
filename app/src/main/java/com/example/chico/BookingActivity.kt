
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
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class BookingActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val whiteColor = Color.WHITE
    private val greyColor = Color.LTGRAY

    private lateinit var serviceSpinner: Spinner
    private lateinit var dateButton: TextView
    private lateinit var timeButton: TextView
    private lateinit var confirmButton: TextView

    private val serviceIds = mutableListOf<Long>()
    private val serviceNames = mutableListOf<String>()

    private var selectedDate = ""
    private var selectedTime = ""
    private var bookingInProgress = false

    private val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    )

    private val timeFormat = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)

        createScreen()
        loadServices()
    }

    private fun createScreen() {
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(24),
                dp(28),
                dp(24),
                dp(24)
            )
            gravity = Gravity.CENTER_HORIZONTAL
        }

        scrollView.addView(root)

        val title = TextView(this).apply {
            text = "BOOK APPOINTMENT"
            textSize = 26f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        root.addView(title, matchWidthWrapHeight())

        val subtitle = TextView(this).apply {
            text = "Choose your service, date and time."
            textSize = 15f
            setTextColor(greyColor)
            gravity = Gravity.CENTER
        }

        root.addView(
            subtitle,
            matchWidthWrapHeight().apply {
                topMargin = dp(10)
                bottomMargin = dp(28)
            }
        )

        addSectionLabel(root, "SELECT SERVICE")

        serviceSpinner = Spinner(this).apply {
            background = roundedBackground(cardColor, 12)
            setPadding(
                dp(12),
                dp(6),
                dp(12),
                dp(6)
            )
        }

        root.addView(
            serviceSpinner,
            matchWidthWrapHeight().apply {
                bottomMargin = dp(22)
            }
        )

        addSectionLabel(root, "SELECT DATE")

        dateButton = createButton("CHOOSE DATE")

        dateButton.setOnClickListener {
            showDatePicker()
        }

        root.addView(
            dateButton,
            matchWidthWrapHeight().apply {
                bottomMargin = dp(22)
            }
        )

        addSectionLabel(root, "SELECT TIME")

        timeButton = createButton("CHOOSE TIME")

        timeButton.setOnClickListener {
            showTimePicker()
        }

        root.addView(
            timeButton,
            matchWidthWrapHeight().apply {
                bottomMargin = dp(30)
            }
        )

        confirmButton = createButton("CONFIRM BOOKING")

        confirmButton.setOnClickListener {
            createBooking()
        }

        root.addView(
            confirmButton,
            matchWidthWrapHeight().apply {
                bottomMargin = dp(14)
            }
        )

        val backButton = createButton(
            "BACK",
            cardColor,
            whiteColor
        )

        backButton.setOnClickListener {
            finish()
        }

        root.addView(
            backButton,
            matchWidthWrapHeight()
        )

        setContentView(scrollView)
    }

    private fun loadServices() {
        serviceIds.clear()
        serviceNames.clear()

        val cursor = db.readableDatabase.query(
            ChicoDb.TABLE_SERVICES,
            arrayOf(
                ChicoDb.SERVICE_ID,
                ChicoDb.SERVICE_NAME
            ),
            null,
            null,
            null,
            null,
            "${ChicoDb.SERVICE_ID} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                serviceIds.add(
                    it.getLong(
                        it.getColumnIndexOrThrow(
                            ChicoDb.SERVICE_ID
                        )
                    )
                )

                serviceNames.add(
                    it.getString(
                        it.getColumnIndexOrThrow(
                            ChicoDb.SERVICE_NAME
                        )
                    )
                )
            }
        }

        // Set the selected service text to white.
        // Keep the spinner's existing dark-grey background.
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            serviceNames
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val textView = super.getView(
                    position,
                    convertView,
                    parent
                ) as TextView

                textView.setTextColor(whiteColor)
                textView.textSize = 16f
                textView.setPadding(
                    dp(4),
                    dp(8),
                    dp(4),
                    dp(8)
                )

                return textView
            }

            // Set dropdown service names to white as well.
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val textView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView

                textView.setTextColor(whiteColor)
                textView.textSize = 16f
                textView.setBackgroundColor(cardColor)
                textView.setPadding(
                    dp(16),
                    dp(12),
                    dp(16),
                    dp(12)
                )

                return textView
            }
        }

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        serviceSpinner.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val chosenDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                selectedDate = dateFormat.format(
                    chosenDate.time
                )

                dateButton.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val chosenTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }

                selectedTime = timeFormat.format(
                    chosenTime.time
                )

                timeButton.text = selectedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun createBooking() {
        if (bookingInProgress) return

        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            showMessage(
                "Please log in before booking an appointment."
            )
            return
        }

        if (
            serviceIds.isEmpty() ||
            serviceSpinner.selectedItemPosition < 0
        ) {
            showMessage("Please select a service.")
            return
        }

        if (selectedDate.isBlank()) {
            showMessage("Please select a date.")
            return
        }

        if (selectedTime.isBlank()) {
            showMessage("Please select a time.")
            return
        }

        val customerEmail = firebaseUser.email
            ?: intent.getStringExtra("email")
            ?: getSharedPreferences(
                "ChicoPrefs",
                MODE_PRIVATE
            ).getString("email", null)
            ?: ""

        if (customerEmail.isBlank()) {
            showMessage(
                "Your customer account could not be identified."
            )
            return
        }

        val customerId = db.getCustomerId(customerEmail)

        if (customerId == -1L) {
            showMessage(
                "Your local customer profile could not be found. Please log in again."
            )
            return
        }

        if (db.appointmentExists(selectedDate, selectedTime)) {
            showMessage(
                "This time slot is already booked. Please choose another time."
            )
            return
        }

        val selectedServiceId =
            serviceIds[serviceSpinner.selectedItemPosition]

        val serviceCursor = db.readableDatabase.query(
            ChicoDb.TABLE_SERVICES,
            arrayOf(
                ChicoDb.SERVICE_NAME,
                ChicoDb.SERVICE_PRICE,
                ChicoDb.SERVICE_DURATION
            ),
            "${ChicoDb.SERVICE_ID} = ?",
            arrayOf(selectedServiceId.toString()),
            null,
            null,
            null
        )

        var serviceName = ""
        var servicePrice = 0.0
        var serviceDuration = 0

        serviceCursor.use {
            if (it.moveToFirst()) {
                serviceName = it.getString(
                    it.getColumnIndexOrThrow(
                        ChicoDb.SERVICE_NAME
                    )
                )

                servicePrice = it.getDouble(
                    it.getColumnIndexOrThrow(
                        ChicoDb.SERVICE_PRICE
                    )
                )

                serviceDuration = it.getInt(
                    it.getColumnIndexOrThrow(
                        ChicoDb.SERVICE_DURATION
                    )
                )
            }
        }

        if (serviceName.isBlank()) {
            showMessage(
                "Could not load the selected service."
            )
            return
        }

        val slotDocumentId =
            "${selectedDate}_${selectedTime.replace(":", "-")}"

        val appointmentRef = firestore
            .collection("appointments")
            .document(slotDocumentId)

        bookingInProgress = true
        confirmButton.isEnabled = false
        confirmButton.alpha = 0.6f

        appointmentRef.get()
            .addOnSuccessListener { existingDocument ->
                if (existingDocument.exists()) {
                    bookingInProgress = false
                    restoreConfirmButton()

                    showMessage(
                        "This time slot already has a booking. Please choose another time."
                    )

                    return@addOnSuccessListener
                }

                if (
                    db.appointmentExists(
                        selectedDate,
                        selectedTime
                    )
                ) {
                    bookingInProgress = false
                    restoreConfirmButton()

                    showMessage(
                        "This time slot is already booked locally. Please choose another time."
                    )

                    return@addOnSuccessListener
                }

                saveBookingLocallyAndToFirebase(
                    appointmentRef = appointmentRef,
                    customerId = customerId,
                    customerUid = firebaseUser.uid,
                    customerEmail = customerEmail,
                    serviceId = selectedServiceId,
                    serviceName = serviceName,
                    servicePrice = servicePrice,
                    serviceDuration = serviceDuration
                )
            }
            .addOnFailureListener { error ->
                bookingInProgress = false
                restoreConfirmButton()

                android.util.Log.e(
                    "ChicoBooking",
                    "Could not check the Firestore booking slot",
                    error
                )

                showMessage(
                    "Could not check availability online. Check your connection and try again."
                )
            }
    }

    private fun saveBookingLocallyAndToFirebase(
        appointmentRef: DocumentReference,
        customerId: Long,
        customerUid: String,
        customerEmail: String,
        serviceId: Long,
        serviceName: String,
        servicePrice: Double,
        serviceDuration: Int
    ) {
        val values = ContentValues().apply {
            put(
                ChicoDb.APPOINTMENT_CUSTOMER_ID,
                customerId
            )
            put(
                ChicoDb.APPOINTMENT_SERVICE_ID,
                serviceId
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

        val localAppointmentId = db.insert(
            ChicoDb.TABLE_APPOINTMENTS,
            values
        )

        if (localAppointmentId == -1L) {
            bookingInProgress = false
            restoreConfirmButton()

            showMessage(
                "Could not save the appointment locally. Please try another time."
            )
            return
        }

        val appointmentData = hashMapOf<String, Any>(
            "customerUid" to customerUid,
            "customerEmail" to customerEmail,
            "customerId" to customerId,
            "serviceId" to serviceId,
            "serviceName" to serviceName,
            "price" to servicePrice,
            "duration" to serviceDuration,
            "date" to selectedDate,
            "time" to selectedTime,
            "status" to "BOOKED",
            "localAppointmentId" to localAppointmentId,
            "createdAt" to FieldValue.serverTimestamp()
        )

        appointmentRef.set(appointmentData)
            .addOnSuccessListener {
                bookingInProgress = false
                restoreConfirmButton()

                Toast.makeText(
                    this,
                    "Appointment booked successfully!",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
            .addOnFailureListener { error ->
                db.delete(
                    ChicoDb.TABLE_APPOINTMENTS,
                    "${ChicoDb.APPOINTMENT_ID} = ?",
                    arrayOf(localAppointmentId.toString())
                )

                bookingInProgress = false
                restoreConfirmButton()

                android.util.Log.e(
                    "ChicoBooking",
                    "Could not save the appointment to Firestore",
                    error
                )

                showMessage(
                    "The booking could not be saved online. Please try again."
                )
            }
    }

    private fun addSectionLabel(
        parent: LinearLayout,
        label: String
    ) {
        val text = TextView(this).apply {
            this.text = label
            textSize = 14f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
        }

        parent.addView(
            text,
            matchWidthWrapHeight().apply {
                bottomMargin = dp(10)
            }
        )
    }

    private fun createButton(
        label: String,
        color: Int = accentColor,
        textColor: Int = backgroundColor
    ): TextView {
        return TextView(this).apply {
            text = label
            textSize = 15f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            includeFontPadding = true

            setPadding(
                dp(16),
                dp(15),
                dp(16),
                dp(15)
            )

            background = roundedBackground(color, 12)
            isClickable = true
            isFocusable = true
        }
    }

    private fun roundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun matchWidthWrapHeight(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun restoreConfirmButton() {
        confirmButton.isEnabled = true
        confirmButton.alpha = 1f
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun dp(value: Int): Int {
        return (
                value * resources.displayMetrics.density
                ).roundToInt()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::db.isInitialized) {
            db.close()
        }
    }
}
