package com.example.chico

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val white = Color.WHITE
    private val grey = Color.rgb(170, 170, 170)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)

        createDashboard()
    }

    private fun createDashboard() {

        // =====================================================
        // MAIN LAYOUT
        // =====================================================

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        // =====================================================
        // HEADER
        // =====================================================

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 20)
            setBackgroundColor(backgroundColor)
        }

        val title = TextView(this).apply {
            text = "CHICO"
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
        }

        val subtitle = TextView(this).apply {
            text = "BARBERSHOP MANAGEMENT"
            textSize = 11f
            letterSpacing = 0.15f
            setTextColor(accentColor)
        }

        header.addView(title)
        header.addView(subtitle)

        root.addView(header)

        // =====================================================
        // SCROLL VIEW
        // =====================================================

        val scrollView = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 10, 20, 30)
        }

        scrollView.addView(content)

        // =====================================================
        // DASHBOARD TITLE
        // =====================================================

        val dashboardTitle = TextView(this).apply {
            text = "Dashboard"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            setPadding(0, 5, 0, 5)
        }

        content.addView(dashboardTitle)

        val todayText = TextView(this).apply {
            text = "Manage your barbershop from one place."
            textSize = 14f
            setTextColor(grey)
            setPadding(0, 0, 0, 20)
        }

        content.addView(todayText)

        // =====================================================
        // STAT CARDS
        // =====================================================

        content.addView(sectionTitle("TODAY"))

        val statsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val appointmentsCard = createStatCard(
            "APPOINTMENTS",
            getAppointmentCount()
        )

        val customersCard = createStatCard(
            "CUSTOMERS",
            getCustomerCount()
        )

        statsLayout.addView(
            appointmentsCard,
            LinearLayout.LayoutParams(
                0,
                110,
                1f
            )
        )

        val space = android.view.View(this)

        statsLayout.addView(
            space,
            LinearLayout.LayoutParams(
                12,
                1
            )
        )

        statsLayout.addView(
            customersCard,
            LinearLayout.LayoutParams(
                0,
                110,
                1f
            )
        )

        content.addView(statsLayout)

        addSpace(content, 20)

        // =====================================================
        // MANAGEMENT
        // =====================================================

        content.addView(
            sectionTitle("MANAGEMENT")
        )

        // Customers
        val customersButton = createMenuButton(
            "CUSTOMERS",
            "Add and manage customer information"
        )

        customersButton.setOnClickListener {

            // Customer management screen will be added later.

            showMessage(
                "Customer management will be available here."
            )
        }

        content.addView(customersButton)

        addSpace(content, 10)

        // Appointments
        val appointmentsButton = createMenuButton(
            "APPOINTMENTS",
            "View and manage today's appointments"
        )

        appointmentsButton.setOnClickListener {

            showMessage(
                "Appointment management will be available here."
            )
        }

        content.addView(appointmentsButton)

        addSpace(content, 10)

        // Payments
        val paymentsButton = createMenuButton(
            "PAYMENTS",
            "Record and track customer payments"
        )

        paymentsButton.setOnClickListener {

            showMessage(
                "Payment management will be available here."
            )
        }

        content.addView(paymentsButton)

        addSpace(content, 10)

        // Reports
        val reportsButton = createMenuButton(
            "REPORTS",
            "View income and business performance"
        )

        reportsButton.setOnClickListener {

            showMessage(
                "Reports will be available here."
            )
        }

        content.addView(reportsButton)

        addSpace(content, 20)

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        content.addView(
            sectionTitle("QUICK ACTIONS")
        )

        val addCustomerButton = Button(this).apply {

            text = "+ ADD CUSTOMER"

            textSize = 13f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)

            setBackgroundColor(accentColor)

            setOnClickListener {

                showMessage(
                    "Add customer screen will open here."
                )
            }
        }

        content.addView(
            addCustomerButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        addSpace(content, 10)

        val recordPaymentButton = Button(this).apply {

            text = "+ RECORD PAYMENT"

            textSize = 13f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)

            setBackgroundColor(cardColor)

            setOnClickListener {

                showMessage(
                    "Record payment screen will open here."
                )
            }
        }

        content.addView(
            recordPaymentButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        addSpace(content, 25)

        // =====================================================
        // LOGOUT
        // =====================================================

        val logoutButton = Button(this).apply {

            text = "LOG OUT"

            textSize = 13f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)

            setBackgroundColor(cardColor)

            setOnClickListener {

                val intent = Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                finish()
            }
        }

        content.addView(
            logoutButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        // =====================================================
        // ADD CONTENT TO ROOT
        // =====================================================

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private fun createStatCard(
        label: String,
        value: String
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(15, 10, 15, 10)
            setBackgroundColor(cardColor)
        }

        val valueText = TextView(this).apply {
            text = value
            textSize = 27f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(accentColor)
            gravity = Gravity.CENTER
        }

        val labelText = TextView(this).apply {
            text = label
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.1f
            setTextColor(grey)
            gravity = Gravity.CENTER
        }

        card.addView(valueText)
        card.addView(labelText)

        return card
    }

    // =========================================================
    // MENU BUTTON
    // =========================================================

    private fun createMenuButton(
        title: String,
        description: String
    ): LinearLayout {

        val container = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                20,
                16,
                20,
                16
            )

            setBackgroundColor(cardColor)

            isClickable = true
        }

        val titleText = TextView(this).apply {

            text = title

            textSize = 15f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(white)
        }

        val descriptionText = TextView(this).apply {

            text = description

            textSize = 12f

            setTextColor(grey)

            setPadding(
                0,
                5,
                0,
                0
            )
        }

        container.addView(titleText)
        container.addView(descriptionText)

        return container
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private fun sectionTitle(
        text: String
    ): TextView {

        return TextView(this).apply {

            this.text = text

            textSize = 11f

            typeface = Typeface.DEFAULT_BOLD

            letterSpacing = 0.15f

            setTextColor(accentColor)

            setPadding(
                0,
                0,
                0,
                10
            )
        }
    }

    // =========================================================
    // GET CUSTOMER COUNT
    // =========================================================

    private fun getCustomerCount(): String {

        return try {

            val cursor = db.readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM customers",
                null
            )

            var count = 0

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }

            cursor.close()

            count.toString()

        } catch (e: Exception) {

            "0"
        }
    }

    // =========================================================
    // GET TODAY'S APPOINTMENT COUNT
    // =========================================================

    private fun getAppointmentCount(): String {

        return try {

            val cursor = db.readableDatabase.rawQuery(
                """
                SELECT COUNT(*)
                FROM appointments
                WHERE appointment_date = date('now')
                AND status != 'CANCELLED'
                """.trimIndent(),
                null
            )

            var count = 0

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }

            cursor.close()

            count.toString()

        } catch (e: Exception) {

            "0"
        }
    }

    // =========================================================
    // ADD SPACE
    // =========================================================

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space = android.view.View(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }

    // =========================================================
    // MESSAGE
    // =========================================================

    private fun showMessage(
        message: String
    ) {

        android.widget.Toast.makeText(
            this,
            message,
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // CLOSE DATABASE
    // =========================================================

    override fun onDestroy() {

        db.close()

        super.onDestroy()
    }
}