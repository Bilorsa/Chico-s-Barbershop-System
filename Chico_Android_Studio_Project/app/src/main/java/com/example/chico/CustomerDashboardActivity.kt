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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

class CustomerDashboardActivity : AppCompatActivity() {

    private var customerEmail = ""

    // =========================================================
    // COLOURS
    // =========================================================

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)
    private val logoutColor = Color.rgb(55, 55, 55)

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

        // -----------------------------------------------------
        // GET CUSTOMER EMAIL
        // -----------------------------------------------------

        customerEmail =
            intent.getStringExtra("email")
                ?: intent.getStringExtra("registered_email")
                        ?: getSharedPreferences(
                    "ChicoPrefs",
                    MODE_PRIVATE
                ).getString("email", "")
                        ?: ""

        // -----------------------------------------------------
        // SAVE CUSTOMER EMAIL
        // -----------------------------------------------------

        if (customerEmail.isNotEmpty()) {

            getSharedPreferences(
                "ChicoPrefs",
                MODE_PRIVATE
            )
                .edit()
                .putString(
                    "email",
                    customerEmail
                )
                .apply()
        }

        createDashboard()
    }

    // =========================================================
    // CREATE DASHBOARD
    // =========================================================

    private fun createDashboard() {

        val scrollView = ScrollView(this).apply {

            setBackgroundColor(backgroundColor)

            isFillViewport = true

            clipToPadding = false
        }

        val mainLayout = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(24),
                dp(25),
                dp(24),
                dp(35)
            )
        }

        scrollView.addView(mainLayout)

        // =====================================================
        // STATUS BAR / NOTCH INSET
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
                systemBars.top + dp(25),
                dp(24),
                systemBars.bottom + dp(35)
            )

            insets
        }

        // =====================================================
        // HEADER
        // =====================================================

        val title = TextView(this).apply {

            text = "CHICO"

            textSize = 30f

            setTextColor(accentColor)

            typeface = Typeface.DEFAULT_BOLD

            gravity = Gravity.CENTER

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0
        }

        mainLayout.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            5
        )

        val subtitle = TextView(this).apply {

            text = "BARBERSHOP"

            textSize = 14f

            setTextColor(secondaryText)

            gravity = Gravity.CENTER

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0
        }

        mainLayout.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            35
        )

        // =====================================================
        // WELCOME
        // =====================================================

        val welcome = TextView(this).apply {

            text = "Welcome Back"

            textSize = 26f

            setTextColor(primaryText)

            typeface = Typeface.DEFAULT_BOLD

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0
        }

        mainLayout.addView(
            welcome,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            10
        )

        val welcomeSub = TextView(this).apply {

            text =
                "Manage your barbershop appointments from one place."

            textSize = 15f

            setTextColor(secondaryText)

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0
        }

        mainLayout.addView(
            welcomeSub,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            30
        )

        // =====================================================
        // BOOK APPOINTMENT CARD
        // =====================================================

        val bookingCard = createCard()

        bookingCard.addView(
            createCardTitle(
                "BOOK AN APPOINTMENT"
            )
        )

        addSpace(
            bookingCard,
            12
        )

        bookingCard.addView(
            createDescription(
                "Choose a service, date and time for your next haircut."
            )
        )

        addSpace(
            bookingCard,
            22
        )

        val bookingButton =
            createActionButton(
                "BOOK APPOINTMENT"
            )

        bookingButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    BookingActivity::class.java
                )

            intent.putExtra(
                "email",
                customerEmail
            )

            startActivity(intent)
        }

        bookingCard.addView(
            bookingButton
        )

        mainLayout.addView(
            bookingCard
        )

        addSpace(
            mainLayout,
            20
        )

        // =====================================================
        // MY BOOKINGS CARD
        // =====================================================

        val bookingsCard = createCard()

        bookingsCard.addView(
            createCardTitle(
                "MY BOOKINGS"
            )
        )

        addSpace(
            bookingsCard,
            12
        )

        bookingsCard.addView(
            createDescription(
                "View your upcoming and previous appointments."
            )
        )

        addSpace(
            bookingsCard,
            22
        )

        val bookingsButton =
            createActionButton(
                "VIEW MY BOOKINGS"
            )

        bookingsButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MyBookingsActivity::class.java
                )

            intent.putExtra(
                "email",
                customerEmail
            )

            startActivity(intent)
        }

        bookingsCard.addView(
            bookingsButton
        )

        mainLayout.addView(
            bookingsCard
        )

        addSpace(
            mainLayout,
            20
        )

        // =====================================================
        // MY ACCOUNT CARD
        // =====================================================

        val accountCard = createCard()

        accountCard.addView(
            createCardTitle(
                "MY ACCOUNT"
            )
        )

        addSpace(
            accountCard,
            12
        )

        accountCard.addView(
            createDescription(
                "View and update your personal customer information."
            )
        )

        addSpace(
            accountCard,
            22
        )

        val accountButton =
            createActionButton(
                "MY ACCOUNT"
            )

        accountButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ProfileActivity::class.java
                )

            intent.putExtra(
                "email",
                customerEmail
            )

            startActivity(intent)
        }

        accountCard.addView(
            accountButton
        )

        mainLayout.addView(
            accountCard
        )

        addSpace(
            mainLayout,
            20
        )

        // =====================================================
        // CHICO BARBERSHOP CARD
        // =====================================================

        val infoCard = createCard()

        infoCard.addView(
            createCardTitle(
                "CHICO BARBERSHOP"
            )
        )

        addSpace(
            infoCard,
            12
        )

        infoCard.addView(
            createDescription(
                "Book your haircut, manage your appointments " +
                        "and keep your customer information up to date."
            )
        )

        addSpace(
            infoCard,
            22
        )

        val infoButton =
            createActionButton(
                "VIEW INFORMATION"
            )

        infoButton.setOnClickListener {

            Toast.makeText(
                this,
                "CHICO BARBERSHOP\n\nProfessional barber services and haircuts.",
                Toast.LENGTH_LONG
            ).show()
        }

        infoCard.addView(
            infoButton
        )

        mainLayout.addView(
            infoCard
        )

        addSpace(
            mainLayout,
            35
        )

        // =====================================================
        // LOG OUT
        // =====================================================

        val logoutButton =
            createLogoutButton()

        logoutButton.setOnClickListener {

            getSharedPreferences(
                "ChicoPrefs",
                MODE_PRIVATE
            )
                .edit()
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

        mainLayout.addView(
            logoutButton
        )

        addSpace(
            mainLayout,
            15
        )

        setContentView(scrollView)

        // Apply the system inset immediately
        ViewCompat.requestApplyInsets(scrollView)
    }

    // =========================================================
    // CREATE CARD
    // =========================================================

    private fun createCard(): LinearLayout {

        return LinearLayout(this).apply {

            orientation =
                LinearLayout.VERTICAL

            setPadding(
                dp(20),
                dp(22),
                dp(20),
                dp(22)
            )

            background =
                createRoundedBackground(
                    cardColor,
                    14f
                )

            clipChildren = false
            clipToPadding = false

            layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
        }
    }

    // =========================================================
    // CARD TITLE
    // =========================================================

    private fun createCardTitle(
        textValue: String
    ): TextView {

        return TextView(this).apply {

            text = textValue

            textSize = 18f

            setTextColor(primaryText)

            typeface =
                Typeface.DEFAULT_BOLD

            gravity =
                Gravity.CENTER_VERTICAL

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0

            setPadding(
                0,
                dp(2),
                0,
                dp(2)
            )
        }
    }

    // =========================================================
    // DESCRIPTION
    // =========================================================

    private fun createDescription(
        textValue: String
    ): TextView {

        return TextView(this).apply {

            text = textValue

            textSize = 14f

            setTextColor(
                secondaryText
            )

            gravity =
                Gravity.CENTER_VERTICAL

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0

            setPadding(
                0,
                dp(2),
                0,
                dp(2)
            )
        }
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private fun createActionButton(
        textValue: String
    ): TextView {

        return TextView(this).apply {

            text = textValue

            textSize = 15f

            setTextColor(
                Color.WHITE
            )

            typeface =
                Typeface.DEFAULT_BOLD

            gravity =
                Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0

            isClickable = true

            isFocusable = true

            setPadding(
                dp(16),
                dp(12),
                dp(16),
                dp(12)
            )

            background =
                createRoundedBackground(
                    accentColor,
                    12f
                )

            layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(64)
                )
        }
    }

    // =========================================================
    // LOGOUT BUTTON
    // =========================================================

    private fun createLogoutButton(): TextView {

        return TextView(this).apply {

            text = "LOG OUT"

            textSize = 15f

            setTextColor(
                Color.WHITE
            )

            typeface =
                Typeface.DEFAULT_BOLD

            gravity =
                Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true

            minHeight = 0

            minimumHeight = 0

            isClickable = true

            isFocusable = true

            setPadding(
                dp(16),
                dp(12),
                dp(16),
                dp(12)
            )

            background =
                createRoundedBackground(
                    logoutColor,
                    12f
                )

            layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(64)
                )
        }
    }

    // =========================================================
    // ROUNDED BACKGROUND
    // =========================================================

    private fun createRoundedBackground(
        color: Int,
        radius: Float
    ): GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(color)

            cornerRadius =
                radius *
                        resources.displayMetrics.density
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