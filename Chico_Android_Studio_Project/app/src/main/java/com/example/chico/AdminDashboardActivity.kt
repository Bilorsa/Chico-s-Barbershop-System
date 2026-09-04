package com.example.chico

import android.content.Intent
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

@Suppress("SetTextI18n")
class AdminDashboardActivity : AppCompatActivity() {

    // =========================================================
    // COLORS
    // =========================================================

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 150, 220)
    private val cyanColor = Color.rgb(0, 200, 255)
    private val white = Color.WHITE
    private val grey = Color.LTGRAY

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

        createDashboard()
    }

    // =========================================================
    // CREATE DASHBOARD
    // =========================================================

    private fun createDashboard() {

        val scrollView = ScrollView(this)

        scrollView.setBackgroundColor(backgroundColor)

        scrollView.clipToPadding = false

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL

        root.setPadding(
            dp(18),
            dp(100),
            dp(18),
            dp(40)
        )

        scrollView.addView(root)

        // =====================================================
        // STATUS BAR / CAMERA NOTCH
        // =====================================================

        ViewCompat.setOnApplyWindowInsetsListener(
            scrollView
        ) { _, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            /*
             * Large fixed space below the status bar.
             *
             * This deliberately places CHICO well below
             * the camera/notch area.
             */
            root.setPadding(
                dp(18),
                systemBars.top + dp(100),
                dp(18),
                systemBars.bottom + dp(40)
            )

            insets
        }

        ViewCompat.requestApplyInsets(scrollView)

        // =====================================================
        // TOP HEADER AREA
        // =====================================================

        val header =
            LinearLayout(this)

        header.orientation =
            LinearLayout.VERTICAL

        header.gravity =
            Gravity.CENTER

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(105)
            )
        )

        // =====================================================
        // CHICO LOGO
        // =====================================================

        val logo =
            TextView(this)

        logo.text =
            "CHICO"

        logo.textSize =
            28f

        logo.setTextColor(
            white
        )

        logo.typeface =
            Typeface.DEFAULT_BOLD

        logo.gravity =
            Gravity.CENTER

        logo.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        logo.includeFontPadding =
            true

        header.addView(
            logo,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        // =====================================================
        // SUBTITLE
        // =====================================================

        val subtitle =
            TextView(this)

        subtitle.text =
            "BARBERSHOP MANAGEMENT"

        subtitle.textSize =
            11f

        subtitle.setTextColor(
            cyanColor
        )

        subtitle.gravity =
            Gravity.CENTER

        subtitle.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        subtitle.includeFontPadding =
            true

        header.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        // Space below header
        addSpace(root, 25)

        // =====================================================
        // DASHBOARD TITLE
        // =====================================================

        val title =
            TextView(this)

        title.text =
            "Admin Dashboard"

        title.textSize =
            23f

        title.setTextColor(
            white
        )

        title.typeface =
            Typeface.DEFAULT_BOLD

        title.gravity =
            Gravity.CENTER

        title.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        title.includeFontPadding =
            true

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(root, 20)

        // =====================================================
        // APPOINTMENTS CARD
        // =====================================================

        val appointmentsCard =
            createManagementCard(
                "APPOINTMENTS",
                "View and manage customer appointments"
            )

        appointmentsCard.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AppointmentManagementActivity::class.java
                )
            )
        }

        root.addView(
            appointmentsCard
        )

        addSpace(root, 14)

        // =====================================================
        // CUSTOMERS CARD
        // =====================================================

        val customersCard =
            createManagementCard(
                "CUSTOMERS",
                "View and manage customer information"
            )

        customersCard.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CustomerManagementActivity::class.java
                )
            )
        }

        root.addView(
            customersCard
        )

        addSpace(root, 14)

        // =====================================================
        // PAYMENTS CARD
        // =====================================================

        val paymentsCard =
            createManagementCard(
                "PAYMENTS",
                "Record and track customer payments"
            )

        paymentsCard.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PaymentManagementActivity::class.java
                )
            )
        }

        root.addView(
            paymentsCard
        )

        addSpace(root, 14)

        // =====================================================
        // REPORTS CARD
        // =====================================================

        val reportsCard =
            createManagementCard(
                "REPORTS",
                "View income and business reports"
            )

        reportsCard.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ReportsActivity::class.java
                )
            )
        }

        root.addView(
            reportsCard
        )

        addSpace(root, 25)

        // =====================================================
        // LOG OUT
        // =====================================================

        val logoutButton =
            createLogoutButton()

        logoutButton.setOnClickListener {

            val prefs =
                getSharedPreferences(
                    "ChicoPrefs",
                    MODE_PRIVATE
                )

            prefs.edit()
                .clear()
                .apply()

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }

        root.addView(
            logoutButton
        )

        addSpace(root, 25)

        setContentView(scrollView)
    }

    // =========================================================
    // MANAGEMENT CARD
    // =========================================================

    private fun createManagementCard(
        heading: String,
        description: String
    ): LinearLayout {

        // Outer card
        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.gravity =
            Gravity.CENTER

        card.setPadding(
            dp(20),
            dp(20),
            dp(20),
            dp(20)
        )

        card.background =
            createCardBackground()

        card.isClickable =
            true

        card.isFocusable =
            true

        card.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(205)
            )

        // =====================================================
        // INNER CONTENT CONTAINER
        // =====================================================

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        content.gravity =
            Gravity.CENTER

        card.addView(
            content,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(160)
            )
        )

        // =====================================================
        // HEADING
        // =====================================================

        val headingText =
            TextView(this)

        headingText.text =
            heading

        headingText.textSize =
            18f

        headingText.setTextColor(
            white
        )

        headingText.typeface =
            Typeface.DEFAULT_BOLD

        headingText.gravity =
            Gravity.CENTER

        headingText.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        headingText.includeFontPadding =
            true

        content.addView(
            headingText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        // =====================================================
        // DESCRIPTION
        // =====================================================

        val descriptionText =
            TextView(this)

        descriptionText.text =
            description

        descriptionText.textSize =
            13f

        descriptionText.setTextColor(
            grey
        )

        descriptionText.gravity =
            Gravity.CENTER

        descriptionText.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        descriptionText.includeFontPadding =
            true

        val descriptionParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(38)
            )

        descriptionParams.setMargins(
            0,
            dp(8),
            0,
            dp(18)
        )

        content.addView(
            descriptionText,
            descriptionParams
        )

        // =====================================================
        // OPEN BUTTON
        // =====================================================

        val openButton =
            createOpenButton()

        /*
         * The button is placed inside the centered
         * content container instead of directly inside
         * the card.
         */

        content.addView(
            openButton
        )

        return card
    }

    // =========================================================
    // OPEN BUTTON
    // =========================================================

    private fun createOpenButton(): TextView {

        val button =
            TextView(this)

        button.text =
            "OPEN"

        button.textSize =
            14f

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.setTextColor(
            white
        )

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding =
            true

        button.minHeight =
            0

        button.minimumHeight =
            0

        button.setPadding(
            dp(16),
            dp(10),
            dp(16),
            dp(10)
        )

        button.background =
            createOpenButtonBackground()

        button.isClickable =
            false

        button.isFocusable =
            false

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )

        return button
    }

    // =========================================================
    // LOG OUT BUTTON
    // =========================================================

    private fun createLogoutButton(): TextView {

        val button =
            TextView(this)

        button.text =
            "LOG OUT"

        button.textSize =
            14f

        button.typeface =
            Typeface.DEFAULT_BOLD

        button.setTextColor(
            white
        )

        button.gravity =
            Gravity.CENTER

        button.textAlignment =
            View.TEXT_ALIGNMENT_CENTER

        button.includeFontPadding =
            true

        button.minHeight =
            0

        button.minimumHeight =
            0

        button.setPadding(
            dp(16),
            dp(10),
            dp(16),
            dp(10)
        )

        button.background =
            createLogoutBackground()

        button.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )

        return button
    }

    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private fun createCardBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                cardColor
            )

            cornerRadius =
                dp(12).toFloat()

            setStroke(
                dp(1),
                Color.rgb(
                    55,
                    55,
                    55
                )
            )
        }
    }

    // =========================================================
    // OPEN BUTTON BACKGROUND
    // =========================================================

    private fun createOpenButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                accentColor
            )

            cornerRadius =
                dp(8).toFloat()
        }
    }

    // =========================================================
    // LOG OUT BACKGROUND
    // =========================================================

    private fun createLogoutBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            setColor(
                Color.rgb(
                    70,
                    70,
                    70
                )
            )

            cornerRadius =
                dp(8).toFloat()
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