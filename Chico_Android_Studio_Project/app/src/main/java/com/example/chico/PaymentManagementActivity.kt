package com.example.chico

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class PaymentManagementActivity : AppCompatActivity() {

    // =========================================================
    // COLORS
    // =========================================================

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)

    private val paymentColor = Color.rgb(0, 150, 100)
    private val backColor = Color.rgb(50, 50, 50)

    private lateinit var db: ChicoDb
    private lateinit var paymentContainer: LinearLayout
    private lateinit var unpaidContainer: LinearLayout

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

        createPaymentManagement()

        loadUnpaidAppointments()
        loadPayments()
    }

    // =========================================================
    // CREATE PAYMENT MANAGEMENT SCREEN
    // =========================================================

    private fun createPaymentManagement() {

        val scrollView = ScrollView(this)

        scrollView.setBackgroundColor(
            backgroundColor
        )

        scrollView.clipToPadding = false

        val mainLayout = LinearLayout(this)

        mainLayout.orientation =
            LinearLayout.VERTICAL

        mainLayout.setPadding(
            dp(24),
            dp(30),
            dp(24),
            dp(35)
        )

        scrollView.addView(mainLayout)

        // =====================================================
        // STATUS BAR / CAMERA NOTCH SPACING
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

        title.setTextColor(
            accentColor
        )

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

        addSpace(
            mainLayout,
            22
        )

        // =====================================================
        // PAGE TITLE
        // =====================================================

        val pageTitle = TextView(this)

        pageTitle.text =
            "Payments"

        pageTitle.textSize =
            25f

        pageTitle.setTextColor(
            primaryText
        )

        pageTitle.typeface =
            Typeface.DEFAULT_BOLD

        pageTitle.gravity =
            Gravity.CENTER_VERTICAL

        pageTitle.includeFontPadding =
            true

        mainLayout.addView(
            pageTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(42)
            )
        )

        addSpace(
            mainLayout,
            2
        )

        // =====================================================
        // DESCRIPTION
        // =====================================================

        val description = TextView(this)

        description.text =
            "Record and track customer payments."

        description.textSize =
            14f

        description.setTextColor(
            secondaryText
        )

        description.gravity =
            Gravity.CENTER_VERTICAL

        description.includeFontPadding =
            true

        mainLayout.addView(
            description,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(32)
            )
        )

        addSpace(
            mainLayout,
            22
        )

        // =====================================================
        // UNPAID APPOINTMENTS TITLE
        // =====================================================

        val unpaidTitle = TextView(this)

        unpaidTitle.text =
            "Unpaid Appointments"

        unpaidTitle.textSize =
            20f

        unpaidTitle.setTextColor(
            primaryText
        )

        unpaidTitle.typeface =
            Typeface.DEFAULT_BOLD

        unpaidTitle.gravity =
            Gravity.CENTER_VERTICAL

        unpaidTitle.includeFontPadding =
            true

        mainLayout.addView(
            unpaidTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        addSpace(
            mainLayout,
            2
        )

        // =====================================================
        // UNPAID DESCRIPTION
        // =====================================================

        val unpaidDescription = TextView(this)

        unpaidDescription.text =
            "Appointments that still need payment."

        unpaidDescription.textSize =
            14f

        unpaidDescription.setTextColor(
            secondaryText
        )

        unpaidDescription.gravity =
            Gravity.CENTER_VERTICAL

        unpaidDescription.includeFontPadding =
            true

        mainLayout.addView(
            unpaidDescription,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(
            mainLayout,
            15
        )

        // =====================================================
        // UNPAID APPOINTMENTS CONTAINER
        // =====================================================

        unpaidContainer =
            LinearLayout(this)

        unpaidContainer.orientation =
            LinearLayout.VERTICAL

        mainLayout.addView(
            unpaidContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            25
        )

        // =====================================================
        // PAYMENTS TITLE
        // =====================================================

        val paymentsTitle = TextView(this)

        paymentsTitle.text =
            "Recorded Payments"

        paymentsTitle.textSize =
            20f

        paymentsTitle.setTextColor(
            primaryText
        )

        paymentsTitle.typeface =
            Typeface.DEFAULT_BOLD

        paymentsTitle.gravity =
            Gravity.CENTER_VERTICAL

        paymentsTitle.includeFontPadding =
            true

        mainLayout.addView(
            paymentsTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        addSpace(
            mainLayout,
            2
        )

        // =====================================================
        // PAYMENTS DESCRIPTION
        // =====================================================

        val paymentsDescription = TextView(this)

        paymentsDescription.text =
            "Payment history for the barbershop."

        paymentsDescription.textSize =
            14f

        paymentsDescription.setTextColor(
            secondaryText
        )

        paymentsDescription.gravity =
            Gravity.CENTER_VERTICAL

        paymentsDescription.includeFontPadding =
            true

        mainLayout.addView(
            paymentsDescription,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(
            mainLayout,
            15
        )

        // =====================================================
        // PAYMENTS CONTAINER
        // =====================================================

        paymentContainer =
            LinearLayout(this)

        paymentContainer.orientation =
            LinearLayout.VERTICAL

        mainLayout.addView(
            paymentContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            25
        )

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

        addSpace(
            mainLayout,
            20
        )

        setContentView(scrollView)
    }

    // =========================================================
    // LOAD UNPAID APPOINTMENTS
    // =========================================================

    private fun loadUnpaidAppointments() {

        unpaidContainer.removeAllViews()

        val cursor =
            db.readableDatabase.rawQuery(
                """
                SELECT
                    a.id,
                    a.appointment_date,
                    a.appointment_time,
                    c.name AS customer_name,
                    s.name AS service_name,
                    s.price AS service_price
                FROM appointments a
                LEFT JOIN customers c
                    ON a.customer_id = c.id
                LEFT JOIN services s
                    ON a.service_id = s.id
                LEFT JOIN payments p
                    ON a.id = p.appointment_id
                WHERE p.id IS NULL
                AND a.status != 'CANCELLED'
                ORDER BY
                    a.appointment_date ASC,
                    a.appointment_time ASC
                """.trimIndent(),
                null
            )

        // =====================================================
        // NO UNPAID APPOINTMENTS
        // =====================================================

        if (cursor.count == 0) {

            val emptyText =
                TextView(this)

            emptyText.text =
                "No unpaid appointments."

            emptyText.textSize =
                15f

            emptyText.setTextColor(
                secondaryText
            )

            emptyText.gravity =
                Gravity.CENTER

            emptyText.includeFontPadding =
                true

            emptyText.setPadding(
                dp(10),
                dp(20),
                dp(10),
                dp(20)
            )

            unpaidContainer.addView(
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

            val customerName =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "customer_name"
                    )
                ) ?: "Unknown Customer"

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

            createUnpaidCard(
                appointmentId,
                customerName,
                serviceName,
                servicePrice,
                date,
                time
            )
        }

        cursor.close()
    }

    // =========================================================
    // CREATE UNPAID APPOINTMENT CARD
    // =========================================================

    private fun createUnpaidCard(
        appointmentId: Long,
        customerName: String,
        serviceName: String,
        servicePrice: Double,
        date: String,
        time: String
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dp(18),
            dp(20),
            dp(18),
            dp(20)
        )

        card.background =
            createCardBackground()

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

        unpaidContainer.addView(
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

        addSpace(
            card,
            5
        )

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
            createTextParams(28)
        )

        addSpace(
            card,
            2
        )

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
            createTextParams(28)
        )

        addSpace(
            card,
            2
        )

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
            createTextParams(28)
        )

        addSpace(
            card,
            2
        )

        // =====================================================
        // AMOUNT
        // =====================================================

        val amountText =
            createInfoText(
                "Amount Due: R%.2f".format(
                    servicePrice
                ),
                14f,
                secondaryText,
                false
            )

        card.addView(
            amountText,
            createTextParams(28)
        )

        addSpace(
            card,
            12
        )

        // =====================================================
        // RECORD PAYMENT BUTTON
        // =====================================================

        val paymentButton =
            createPaymentButton()

        paymentButton.setOnClickListener {

            showPaymentDialog(
                appointmentId,
                customerName,
                serviceName,
                servicePrice
            )
        }

        card.addView(
            paymentButton
        )
    }

    // =========================================================
    // LOAD RECORDED PAYMENTS
    // =========================================================

    private fun loadPayments() {

        paymentContainer.removeAllViews()

        val cursor =
            db.readableDatabase.rawQuery(
                """
                SELECT
                    p.id,
                    p.amount,
                    p.payment_date,
                    p.payment_method,
                    c.name AS customer_name,
                    s.name AS service_name
                FROM payments p
                LEFT JOIN appointments a
                    ON p.appointment_id = a.id
                LEFT JOIN customers c
                    ON a.customer_id = c.id
                LEFT JOIN services s
                    ON a.service_id = s.id
                ORDER BY p.id DESC
                """.trimIndent(),
                null
            )

        // =====================================================
        // NO PAYMENTS
        // =====================================================

        if (cursor.count == 0) {

            val emptyText =
                TextView(this)

            emptyText.text =
                "No payments recorded yet."

            emptyText.textSize =
                15f

            emptyText.setTextColor(
                secondaryText
            )

            emptyText.gravity =
                Gravity.CENTER

            emptyText.includeFontPadding =
                true

            emptyText.setPadding(
                dp(10),
                dp(20),
                dp(10),
                dp(20)
            )

            paymentContainer.addView(
                emptyText
            )

            cursor.close()

            return
        }

        // =====================================================
        // READ PAYMENTS
        // =====================================================

        while (cursor.moveToNext()) {

            val customerName =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "customer_name"
                    )
                ) ?: "Unknown Customer"

            val serviceName =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "service_name"
                    )
                ) ?: "Unknown Service"

            val amount =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "amount"
                    )
                )

            val paymentDate =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "payment_date"
                    )
                )

            val paymentMethod =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "payment_method"
                    )
                )

            createPaymentCard(
                customerName,
                serviceName,
                amount,
                paymentDate,
                paymentMethod
            )
        }

        cursor.close()
    }

    // =========================================================
    // CREATE PAYMENT CARD
    // =========================================================

    private fun createPaymentCard(
        customerName: String,
        serviceName: String,
        amount: Double,
        paymentDate: String,
        paymentMethod: String
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            dp(18),
            dp(20),
            dp(18),
            dp(20)
        )

        card.background =
            createCardBackground()

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

        paymentContainer.addView(
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

        addSpace(
            card,
            5
        )

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
            createTextParams(28)
        )

        addSpace(
            card,
            2
        )

        // =====================================================
        // AMOUNT
        // =====================================================

        val amountText =
            createInfoText(
                "Amount Paid: R%.2f".format(
                    amount
                ),
                15f,
                paymentColor,
                true
            )

        card.addView(
            amountText,
            createTextParams(30)
        )

        addSpace(
            card,
            2
        )

        // =====================================================
        // PAYMENT DATE
        // =====================================================

        val dateText =
            createInfoText(
                "Payment Date: $paymentDate",
                14f,
                secondaryText,
                false
            )

        card.addView(
            dateText,
            createTextParams(28)
        )

        addSpace(
            card,
            2
        )

        // =====================================================
        // PAYMENT METHOD
        // =====================================================

        val methodText =
            createInfoText(
                "Payment Method: $paymentMethod",
                14f,
                secondaryText,
                false
            )

        card.addView(
            methodText,
            createTextParams(28)
        )
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
    // RECORD PAYMENT BUTTON
    // =========================================================

    private fun createPaymentButton():
            TextView {

        val button =
            TextView(this)

        button.text =
            "RECORD PAYMENT"

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
            createPaymentButtonBackground()

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
    // PAYMENT DIALOG
    // =========================================================

    private fun showPaymentDialog(
        appointmentId: Long,
        customerName: String,
        serviceName: String,
        servicePrice: Double
    ) {

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            dp(24),
            dp(10),
            dp(24),
            dp(10)
        )

        // =====================================================
        // CUSTOMER
        // =====================================================

        val customerText =
            TextView(this)

        customerText.text =
            "Customer: $customerName"

        customerText.textSize =
            15f

        customerText.setTextColor(
            Color.BLACK
        )

        customerText.typeface =
            Typeface.DEFAULT_BOLD

        customerText.gravity =
            Gravity.CENTER_VERTICAL

        customerText.includeFontPadding =
            true

        layout.addView(
            customerText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(32)
            )
        )

        addSpace(
            layout,
            6
        )

        // =====================================================
        // SERVICE
        // =====================================================

        val serviceText =
            TextView(this)

        serviceText.text =
            "Service: $serviceName"

        serviceText.textSize =
            14f

        serviceText.setTextColor(
            Color.DKGRAY
        )

        serviceText.gravity =
            Gravity.CENTER_VERTICAL

        serviceText.includeFontPadding =
            true

        layout.addView(
            serviceText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(
            layout,
            8
        )

        // =====================================================
        // AMOUNT
        // =====================================================

        val amountInput =
            EditText(this)

        amountInput.hint =
            "Amount"

        amountInput.textSize =
            15f

        amountInput.setText(
            "%.2f".format(servicePrice)
        )

        amountInput.setTextColor(
            Color.BLACK
        )

        amountInput.setHintTextColor(
            Color.GRAY
        )

        amountInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL

        amountInput.setSingleLine(
            true
        )

        amountInput.gravity =
            Gravity.CENTER_VERTICAL

        layout.addView(
            amountInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        addSpace(
            layout,
            8
        )

        // =====================================================
        // PAYMENT METHOD
        // =====================================================

        val methodInput =
            EditText(this)

        methodInput.hint =
            "Payment Method (Cash, Card, EFT)"

        methodInput.textSize =
            15f

        methodInput.setTextColor(
            Color.BLACK
        )

        methodInput.setHintTextColor(
            Color.GRAY
        )

        methodInput.setSingleLine(
            true
        )

        methodInput.gravity =
            Gravity.CENTER_VERTICAL

        layout.addView(
            methodInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        // =====================================================
        // DIALOG
        // =====================================================

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Record Payment"
                )
                .setView(layout)
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "SAVE PAYMENT",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val saveButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            val cancelButton =
                dialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
                )

            // =================================================
            // SAVE BUTTON
            // =================================================

            saveButton.setTextColor(
                Color.WHITE
            )

            saveButton.typeface =
                Typeface.DEFAULT_BOLD

            saveButton.background =
                createPaymentButtonBackground()

            saveButton.setPadding(
                dp(20),
                dp(10),
                dp(20),
                dp(10)
            )

            // =================================================
            // CANCEL BUTTON
            // =================================================

            cancelButton.setTextColor(
                Color.DKGRAY
            )

            cancelButton.typeface =
                Typeface.DEFAULT_BOLD

            // =================================================
            // SAVE PAYMENT
            // =================================================

            saveButton.setOnClickListener {

                val amountText =
                    amountInput.text
                        .toString()
                        .trim()

                val method =
                    methodInput.text
                        .toString()
                        .trim()

                if (amountText.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please enter the payment amount.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (method.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please enter the payment method.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val amount =
                    amountText.toDoubleOrNull()

                if (
                    amount == null ||
                    amount <= 0
                ) {

                    Toast.makeText(
                        this,
                        "Please enter a valid amount.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                savePayment(
                    appointmentId,
                    amount,
                    method
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // =========================================================
    // SAVE PAYMENT
    // =========================================================

    private fun savePayment(
        appointmentId: Long,
        amount: Double,
        paymentMethod: String
    ) {

        val currentDate =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

        val values =
            ContentValues()

        values.put(
            "appointment_id",
            appointmentId
        )

        values.put(
            "amount",
            amount
        )

        values.put(
            "payment_date",
            currentDate
        )

        values.put(
            "payment_method",
            paymentMethod
        )

        val result =
            db.writableDatabase.insert(
                "payments",
                null,
                values
            )

        if (result == -1L) {

            Toast.makeText(
                this,
                "Payment could not be recorded.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        Toast.makeText(
            this,
            "Payment recorded successfully.",
            Toast.LENGTH_SHORT
        ).show()

        loadUnpaidAppointments()
        loadPayments()
    }

    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private fun createCardBackground():
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
    // PAYMENT BUTTON BACKGROUND
    // =========================================================

    private fun createPaymentButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                accentColor
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
}