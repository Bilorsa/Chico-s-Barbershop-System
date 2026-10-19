
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
import android.widget.LinearLayout.LayoutParams as LinearLayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb
    private lateinit var bookingContainer: LinearLayout

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val redColor = Color.rgb(220, 50, 50)
    private val whiteColor = Color.WHITE
    private val greyColor = Color.LTGRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)
        createScreen()
        loadBookings()
    }

    private fun createScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(20), dp(24), dp(20), dp(20))
        }

        val title = TextView(this).apply {
            setText(R.string.my_bookings_title)
            textSize = 27f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
        }

        val subtitle = TextView(this).apply {
            setText(R.string.my_bookings_subtitle)
            textSize = 14f
            setTextColor(greyColor)
            setPadding(0, dp(8), 0, dp(20))
        }

        bookingContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            addView(bookingContainer)
        }

        val backButton = createButton(
            textRes = R.string.back_to_dashboard,
            color = accentColor,
            textColor = Color.BLACK
        ) {
            finish()
        }

        root.addView(title)
        root.addView(subtitle)

        root.addView(
            scrollView,
            LinearLayoutParams(
                LinearLayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            backButton,
            LinearLayoutParams(
                LinearLayoutParams.MATCH_PARENT,
                dp(50)
            ).apply {
                topMargin = dp(16)
            }
        )

        setContentView(root)
    }

    private fun loadBookings() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            showMessage(R.string.login_to_view_bookings)
            return
        }

        bookingContainer.removeAllViews()
        showMessage(R.string.loading_bookings)

        firestore.collection("appointments")
            .whereEqualTo("customerUid", currentUser.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                bookingContainer.removeAllViews()

                if (snapshot.isEmpty) {
                    showMessage(R.string.no_bookings)
                    return@addOnSuccessListener
                }

                val bookings = snapshot.documents.sortedWith(
                    compareBy(
                        { it.getString("date") ?: "" },
                        { it.getString("time") ?: "" }
                    )
                )

                bookings.forEach { document ->
                    val serviceName =
                        document.getString("serviceName")
                            ?: getString(R.string.unknown_service)

                    val date =
                        document.getString("date")
                            ?: getString(R.string.not_specified)

                    val time =
                        document.getString("time")
                            ?: getString(R.string.not_specified)

                    val price = document.getDouble("price")
                        ?: document.getLong("price")?.toDouble()
                        ?: 0.0

                    val status =
                        document.getString("status") ?: "BOOKED"

                    val localAppointmentId =
                        document.getLong("localAppointmentId")

                    createBookingCard(
                        documentId = document.id,
                        localAppointmentId = localAppointmentId,
                        service = serviceName,
                        date = date,
                        time = time,
                        price = price,
                        status = status
                    )
                }
            }
            .addOnFailureListener { error ->
                bookingContainer.removeAllViews()
                showMessage(R.string.load_bookings_failed)

                android.util.Log.e(
                    "ChicoMyBookings",
                    "Failed to load Firestore appointments",
                    error
                )
            }
    }

    private fun createBookingCard(
        documentId: String,
        localAppointmentId: Long?,
        service: String,
        date: String,
        time: String,
        price: Double,
        status: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = roundedBackground(cardColor, 14)
        }

        val serviceTitle = TextView(this).apply {
            text = service
            textSize = 19f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
        }

        val formattedPrice = String.format(
            Locale.getDefault(),
            "%.2f",
            price
        )

        val details = TextView(this).apply {
            text = getString(
                R.string.booking_details,
                date,
                time,
                formattedPrice
            )
            textSize = 15f
            setTextColor(whiteColor)
            setLineSpacing(dp(5).toFloat(), 1f)
            setPadding(0, dp(12), 0, dp(12))
        }

        val statusText = TextView(this).apply {
            text = getString(R.string.booking_status, status)
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(
                if (status.equals("CANCELLED", ignoreCase = true)) {
                    redColor
                } else {
                    accentColor
                }
            )
        }

        card.addView(serviceTitle)
        card.addView(details)
        card.addView(statusText)

        if (!status.equals("CANCELLED", ignoreCase = true)) {
            val cancelButton = createButton(
                textRes = R.string.cancel_appointment,
                color = redColor,
                textColor = whiteColor
            ) {
                confirmCancellation(documentId, localAppointmentId)
            }

            card.addView(
                cancelButton,
                LinearLayoutParams(
                    LinearLayoutParams.MATCH_PARENT,
                    dp(46)
                ).apply {
                    topMargin = dp(16)
                }
            )
        } else {
            val cancelledLabel = TextView(this).apply {
                setText(R.string.appointment_cancelled_label)
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(redColor)
                gravity = Gravity.CENTER
                setPadding(0, dp(14), 0, 0)
            }

            card.addView(cancelledLabel)
        }

        bookingContainer.addView(
            card,
            LinearLayoutParams(
                LinearLayoutParams.MATCH_PARENT,
                LinearLayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }
        )
    }

    private fun confirmCancellation(
        documentId: String,
        localAppointmentId: Long?
    ) {
        AlertDialog.Builder(this)
            .setTitle(R.string.cancel_dialog_title)
            .setMessage(R.string.cancel_dialog_message)
            .setNegativeButton(R.string.keep_booking, null)
            .setPositiveButton(R.string.confirm_cancel_booking) { _, _ ->
                cancelAppointment(documentId, localAppointmentId)
            }
            .show()
    }

    private fun cancelAppointment(
        documentId: String,
        localAppointmentId: Long?
    ) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                R.string.login_again,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val appointmentRef = firestore.collection("appointments")
            .document(documentId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(appointmentRef)

            if (!snapshot.exists()) {
                throw IllegalStateException(
                    getString(R.string.appointment_missing)
                )
            }

            if (snapshot.getString("customerUid") != currentUser.uid) {
                throw SecurityException(
                    getString(R.string.cannot_cancel_others_booking)
                )
            }

            val currentStatus = snapshot.getString("status")

            if (currentStatus.equals("CANCELLED", ignoreCase = true)) {
                throw IllegalStateException(
                    getString(R.string.already_cancelled)
                )
            }

            transaction.update(appointmentRef, "status", "CANCELLED")
        }.addOnSuccessListener {

            if (localAppointmentId != null) {
                val values = ContentValues().apply {
                    put(ChicoDb.APPOINTMENT_STATUS, "CANCELLED")
                }

                db.writableDatabase.update(
                    ChicoDb.TABLE_APPOINTMENTS,
                    values,
                    "${ChicoDb.APPOINTMENT_ID} = ?",
                    arrayOf(localAppointmentId.toString())
                )
            }

            Toast.makeText(
                this,
                R.string.cancellation_success,
                Toast.LENGTH_SHORT
            ).show()

            loadBookings()
        }.addOnFailureListener { error ->
            Toast.makeText(
                this,
                R.string.cancellation_failed,
                Toast.LENGTH_LONG
            ).show()

            android.util.Log.e(
                "ChicoMyBookings",
                "Failed to cancel appointment: $documentId",
                error
            )
        }
    }

    private fun showMessage(messageRes: Int) {
        val messageView = TextView(this).apply {
            setText(messageRes)
            textSize = 15f
            setTextColor(greyColor)
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(24), dp(12), dp(24))
        }

        bookingContainer.addView(messageView)
    }

    private fun createButton(
        textRes: Int,
        color: Int,
        textColor: Int,
        onClick: () -> Unit
    ): TextView {
        return TextView(this).apply {
            setText(textRes)
            textSize = 14f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            includeFontPadding = false
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = roundedBackground(color, 10)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
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

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        if (::db.isInitialized) {
            db.close()
        }
        super.onDestroy()
    }
}
