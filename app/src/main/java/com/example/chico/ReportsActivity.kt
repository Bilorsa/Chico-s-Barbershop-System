
package com.example.chico

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class ReportsActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)

    private lateinit var db: ChicoDb
    private lateinit var reportContainer: LinearLayout

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        db = ChicoDb(this)

        createReportsScreen()
    }

    override fun onResume() {
        super.onResume()

        if (::db.isInitialized && ::reportContainer.isInitialized) {
            loadReports()
        }
    }

    private fun createReportsScreen() {
        val scrollView = ScrollView(this)
        scrollView.setBackgroundColor(backgroundColor)
        scrollView.clipToPadding = false

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(30), dp(24), dp(35))
        }

        scrollView.addView(mainLayout)

        val title = TextView(this).apply {
            text = "Reports"
            textSize = 28f
            setTextColor(primaryText)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true
        }

        mainLayout.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(45)
            )
        )

        val subtitle = TextView(this).apply {
            text = "Overview of the barbershop's performance."
            textSize = 14f
            setTextColor(secondaryText)
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true
        }

        mainLayout.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        addSpace(mainLayout, 20)

        reportContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        mainLayout.addView(
            reportContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(mainLayout, 20)

        val backButton = TextView(this).apply {
            text = "BACK TO DASHBOARD"
            textSize = 14f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = createButtonBackground(Color.rgb(50, 50, 50))

            setOnClickListener {
                finish()
            }
        }

        mainLayout.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        setContentView(scrollView)

        loadReports()
    }

    private fun loadReports() {
        reportContainer.removeAllViews()

        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

        val database = db.readableDatabase

        // Today's recorded payment income
        val todayIncome = database.rawQuery(
            """
            SELECT COALESCE(SUM(amount), 0)
            FROM payments
            WHERE payment_date = ?
            """.trimIndent(),
            arrayOf(today)
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        }

        // Appointments scheduled for today, excluding cancellations
        val todayAppointments = database.rawQuery(
            """
            SELECT COUNT(*)
            FROM appointments
            WHERE appointment_date = ?
              AND status != 'CANCELLED'
            """.trimIndent(),
            arrayOf(today)
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }

        // Total registered local customer profiles
        val totalCustomers = database.rawQuery(
            """
            SELECT COUNT(*)
            FROM customers
            """.trimIndent(),
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }

        // Appointments marked as completed
        val completedAppointments = database.rawQuery(
            """
            SELECT COUNT(*)
            FROM appointments
            WHERE UPPER(status) = 'COMPLETED'
            """.trimIndent(),
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }

        createReportCard(
            "TODAY'S INCOME",
            "R${String.format(Locale.US, "%.2f", todayIncome)}",
            "Payments recorded today"
        )

        createReportCard(
            "TODAY'S APPOINTMENTS",
            todayAppointments.toString(),
            "Appointments scheduled for today"
        )

        createReportCard(
            "TOTAL CUSTOMERS",
            totalCustomers.toString(),
            "Customer profiles stored locally"
        )

        createReportCard(
            "COMPLETED APPOINTMENTS",
            completedAppointments.toString(),
            "Appointments marked as completed"
        )
    }

    private fun createReportCard(
        heading: String,
        value: String,
        description: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(20))
            background = createCardBackground()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(16)
        }

        reportContainer.addView(card, params)

        val headingText = TextView(this).apply {
            text = heading
            textSize = 13f
            setTextColor(secondaryText)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true
        }

        card.addView(
            headingText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(28)
            )
        )

        val valueText = TextView(this).apply {
            text = value
            textSize = 29f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true
        }

        card.addView(
            valueText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
            )
        )

        val descriptionText = TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(secondaryText)
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true
        }

        card.addView(
            descriptionText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(28)
            )
        )
    }

    private fun createCardBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(cardColor)
            cornerRadius = dp(16).toFloat()
        }
    }

    private fun createButtonBackground(
        color: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(12).toFloat()
        }
    }

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {
        parent.addView(
            android.view.View(this),
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)
            )
        )
    }
}
