package com.example.chico

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

class AppointmentManagementActivity : AppCompatActivity() {

    // =========================================================
    // COLORS
    // =========================================================

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(175, 175, 175)
    private val cancelColor = Color.rgb(190, 50, 50)
    private val backColor = Color.rgb(55, 55, 55)

    private lateinit var db: ChicoDb
    private lateinit var appointmentContainer: LinearLayout

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

        createAppointmentManagement()
        loadAppointments()
    }

    // =========================================================
    // CREATE SCREEN
    // =========================================================

    private fun createAppointmentManagement() {

        val scrollView = ScrollView(this)

        scrollView.setBackgroundColor(backgroundColor)

        scrollView.clipToPadding = false

        val mainLayout = LinearLayout(this)

        mainLayout.orientation = LinearLayout.VERTICAL

        mainLayout.setPadding(
            dp(24),
            dp(30),
            dp(24),
            dp(35)
        )

        scrollView.addView(mainLayout)

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

            mainLayout.setPadding(
                dp(24),
                systemBars.top + dp(35),
                dp(24),
                systemBars.bottom + dp(35)
            )

            insets
        }

        ViewCompat.requestApplyInsets(scrollView)

        // =====================================================
        // HEADER
        // =====================================================

        val header = LinearLayout(this)

        header.orientation =
            LinearLayout.VERTICAL

        header.gravity =
            Gravity.CENTER

        mainLayout.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(95)
            )
        )

        // =====================================================
        // CHICO
        // =====================================================

        val title = TextView(this)

        title.text = "CHICO"

        title.textSize = 28f

        title.setTextColor(accentColor)

        title.typeface =
            Typeface.DEFAULT_BOLD

        title.gravity =
            Gravity.CENTER

        title.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        title.includeFontPadding = true

        header.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        // =====================================================
        // SUBTITLE
        // =====================================================

        val subtitle = TextView(this)

        subtitle.text =
            "BARBERSHOP MANAGEMENT"

        subtitle.textSize = 12f

        subtitle.setTextColor(
            secondaryText
        )

        subtitle.gravity =
            Gravity.CENTER

        subtitle.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        subtitle.includeFontPadding = true

        header.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(mainLayout, 22)

        // =====================================================
        // PAGE TITLE
        // =====================================================

        val pageTitle = TextView(this)

        pageTitle.text =
            "Appointments"

        pageTitle.textSize = 25f

        pageTitle.setTextColor(
            primaryText
        )

        pageTitle.typeface =
            Typeface.DEFAULT_BOLD

        pageTitle.gravity =
            Gravity.CENTER_VERTICAL

        pageTitle.includeFontPadding = true

        mainLayout.addView(
            pageTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(42)
            )
        )

        addSpace(mainLayout, 2)

        // =====================================================
        // DESCRIPTION
        // =====================================================

        val description = TextView(this)

        description.text =
            "View and manage customer appointments."

        description.textSize = 14f

        description.setTextColor(
            secondaryText
        )

        description.gravity =
            Gravity.CENTER_VERTICAL

        description.includeFontPadding = true

        mainLayout.addView(
            description,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(32)
            )
        )

        addSpace(mainLayout, 18)

        // =====================================================
        // APPOINTMENT LIST
        // =====================================================

        appointmentContainer =
            LinearLayout(this)

        appointmentContainer.orientation =
            LinearLayout.VERTICAL

        mainLayout.addView(
            appointmentContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(mainLayout, 10)

        // =====================================================
        // BACK TO DASHBOARD
        // =====================================================

        val backButton =
            createBackButton()

        backButton.setOnClickListener {
            finish()
        }

        mainLayout.addView(
            backButton
        )

        addSpace(mainLayout, 20)

        setContentView(scrollView)
    }

    // =========================================================
    // LOAD APPOINTMENTS
    // =========================================================

    private fun loadAppointments() {

        appointmentContainer.removeAllViews()

        val cursor =
            db.readableDatabase.rawQuery(
                """
                SELECT 
                    a.id,
                    a.appointment_date,
                    a.appointment_time,
                    a.status,
                    c.name AS customer_name,
                    c.phone AS customer_phone,
                    s.name AS service_name,
                    s.price AS service_price
                FROM appointments a
                LEFT JOIN customers c
                    ON a.customer_id = c.id
                LEFT JOIN services s
                    ON a.service_id = s.id
                ORDER BY 
                    a.appointment_date ASC,
                    a.appointment_time ASC
                """.trimIndent(),
                null
            )

        // =====================================================
        // NO APPOINTMENTS
        // =====================================================

        if (cursor.count == 0) {

            val emptyText =
                TextView(this)

            emptyText.text =
                "No appointments found."

            emptyText.textSize = 16f

            emptyText.setTextColor(
                secondaryText
            )

            emptyText.gravity =
                Gravity.CENTER

            emptyText.includeFontPadding =
                true

            emptyText.setPadding(
                dp(10),
                dp(25),
                dp(10),
                dp(25)
            )

            appointmentContainer.addView(
                emptyText
            )

            cursor.close()

            return
        }

        // =====================================================
        // READ APPOINTMENTS
        // =====================================================

        while (cursor.moveToNext()) {

            val appointmentId =
                cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        "id"
                    )
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

            val customerName =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "customer_name"
                    )
                ) ?: "Unknown Customer"

            val customerPhone =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "customer_phone"
                    )
                ) ?: "N/A"

            val serviceName =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "service_name"
                    )
                ) ?: "Unknown Service"

            val servicePrice =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "service_price"
                    )
                )

            createAppointmentCard(
                appointmentId,
                customerName,
                customerPhone,
                serviceName,
                servicePrice,
                date,
                time,
                status
            )
        }

        cursor.close()
    }

    // =========================================================
    // APPOINTMENT CARD
    // =========================================================

    private fun createAppointmentCard(
        appointmentId: Long,
        customerName: String,
        customerPhone: String,
        serviceName: String,
        servicePrice: Double,
        date: String,
        time: String,
        status: String
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        /*
         * Proper DP padding.
         *
         * This is the important part that fixes the
         * cramped appearance on the Pixel 8 Pro.
         */
        card.setPadding(
            dp(18),
            dp(20),
            dp(18),
            dp(20)
        )

        card.background =
            createAppointmentCardBackground()

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            0,
            0,
            dp(14)
        )

        appointmentContainer.addView(
            card,
            cardParams
        )

        // =====================================================
        // CUSTOMER
        // =====================================================

        val customerText =
            createInfoText(
                "Customer: $customerName",
                17f,
                primaryText,
                true
            )

        card.addView(
            customerText,
            createTextParams(32)
        )

        addSpace(card, 5)

        // =====================================================
        // SERVICE
        // =====================================================

        val serviceText =
            createInfoText(
                "Service: $serviceName",
                14f,
                secondaryText,
                false
            )

        card.addView(
            serviceText,
            createTextParams(27)
        )

        addSpace(card, 2)

        // =====================================================
        // DATE
        // =====================================================

        val dateText =
            createInfoText(
                "Date: $date",
                14f,
                secondaryText,
                false
            )

        card.addView(
            dateText,
            createTextParams(27)
        )

        addSpace(card, 2)

        // =====================================================
        // TIME
        // =====================================================

        val timeText =
            createInfoText(
                "Time: $time",
                14f,
                secondaryText,
                false
            )

        card.addView(
            timeText,
            createTextParams(27)
        )

        addSpace(card, 2)

        // =====================================================
        // PHONE
        // =====================================================

        val phoneText =
            createInfoText(
                "Phone: $customerPhone",
                14f,
                secondaryText,
                false
            )

        card.addView(
            phoneText,
            createTextParams(27)
        )

        addSpace(card, 2)

        // =====================================================
        // PRICE
        // =====================================================

        val priceText =
            createInfoText(
                "Price: R%.2f".format(servicePrice),
                14f,
                secondaryText,
                false
            )

        card.addView(
            priceText,
            createTextParams(27)
        )

        addSpace(card, 5)

        // =====================================================
        // STATUS
        // =====================================================

        val statusText =
            createInfoText(
                "Status: $status",
                14f,
                accentColor,
                true
            )

        card.addView(
            statusText,
            createTextParams(30)
        )

        // =====================================================
        // CANCEL BUTTON
        // =====================================================

        if (status.uppercase() != "CANCELLED") {

            addSpace(card, 10)

            val cancelButton =
                createCancelButton()

            cancelButton.setOnClickListener {

                cancelAppointment(
                    appointmentId
                )
            }

            card.addView(
                cancelButton
            )
        }
    }

    // =========================================================
    // INFO TEXT
    // =========================================================

    private fun createInfoText(
        text: String,
        size: Float,
        color: Int,
        bold: Boolean
    ): TextView {

        val textView =
            TextView(this)

        textView.text =
            text

        textView.textSize =
            size

        textView.setTextColor(
            color
        )

        if (bold) {
            textView.typeface =
                Typeface.DEFAULT_BOLD
        }

        textView.gravity =
            Gravity.CENTER_VERTICAL

        textView.includeFontPadding =
            true

        textView.minHeight = 0
        textView.minimumHeight = 0

        return textView
    }

    // =========================================================
    // TEXT PARAMS
    // =========================================================

    private fun createTextParams(
        height: Int
    ): LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(height)
        )
    }

    // =========================================================
    // CANCEL BUTTON
    // =========================================================

    private fun createCancelButton():
            TextView {

        val button =
            TextView(this)

        button.text =
            "CANCEL APPOINTMENT"

        button.textSize =
            14f

        button.setTextColor(
            Color.WHITE
        )

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding =
            true

        button.minHeight = 0
        button.minimumHeight = 0

        button.setPadding(
            dp(12),
            dp(8),
            dp(12),
            dp(8)
        )

        button.background =
            createCancelButtonBackground()

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
            )

        return button
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================

    private fun createBackButton():
            TextView {

        val button =
            TextView(this)

        button.text =
            "BACK TO DASHBOARD"

        button.textSize =
            14f

        button.setTextColor(
            Color.WHITE
        )

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding =
            true

        button.minHeight = 0
        button.minimumHeight = 0

        button.setPadding(
            dp(12),
            dp(8),
            dp(12),
            dp(8)
        )

        button.background =
            createBackButtonBackground()

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )

        return button
    }

    // =========================================================
    // APPOINTMENT CARD BACKGROUND
    // =========================================================

    private fun createAppointmentCardBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                cardColor
            )

            cornerRadius =
                dp(10).toFloat()

            setStroke(
                dp(1),
                Color.rgb(
                    45,
                    45,
                    45
                )
            )
        }
    }

    // =========================================================
    // CANCEL BUTTON BACKGROUND
    // =========================================================

    private fun createCancelButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                cancelColor
            )

            cornerRadius =
                dp(7).toFloat()
        }
    }

    // =========================================================
    // BACK BUTTON BACKGROUND
    // =========================================================

    private fun createBackButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                backColor
            )

            cornerRadius =
                dp(7).toFloat()
        }
    }

    // =========================================================
    // SPACE
    // =========================================================

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space =
            View(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)
            )
        )
    }

    // =========================================================
    // CANCEL APPOINTMENT
    // =========================================================

    private fun cancelAppointment(
        appointmentId: Long
    ) {

        db.writableDatabase.update(
            "appointments",
            ContentValues().apply {
                put(
                    "status",
                    "CANCELLED"
                )
            },
            "id = ?",
            arrayOf(
                appointmentId.toString()
            )
        )

        loadAppointments()
    }
}