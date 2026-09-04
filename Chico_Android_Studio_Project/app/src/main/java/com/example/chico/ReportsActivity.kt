package com.example.chico

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

class ReportsActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val white = Color.WHITE
    private val grey = Color.rgb(170, 170, 170)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createReportsScreen()
    }

    // =========================
    // DP HELPER
    // =========================

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    // =========================
    // CREATE SCREEN
    // =========================

    private fun createReportsScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            clipToPadding = false
        }

        // =========================
        // HEADER
        // =========================

        val logo = TextView(this).apply {
            text = "CHICO"
            textSize = 32f
            setTextColor(accentColor)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        content.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(content, 18)

        // =========================
        // TITLE
        // =========================

        val title = TextView(this).apply {
            text = "Reports"
            textSize = 28f
            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        content.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(content, 2)

        // =========================
        // SUBTITLE
        // =========================

        val subtitle = TextView(this).apply {
            text = "View your barbershop performance"
            textSize = 15f
            setTextColor(grey)

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(content, 25)

        // =========================
        // TODAY'S INCOME
        // =========================

        content.addView(
            createReportCard(
                "TODAY'S INCOME",
                "R0.00",
                "Total income recorded today"
            )
        )

        addSpace(content, 15)

        // =========================
        // TODAY'S APPOINTMENTS
        // =========================

        content.addView(
            createReportCard(
                "TODAY'S APPOINTMENTS",
                "0",
                "Appointments scheduled for today"
            )
        )

        addSpace(content, 15)

        // =========================
        // TOTAL CUSTOMERS
        // =========================

        content.addView(
            createReportCard(
                "TOTAL CUSTOMERS",
                "0",
                "Customers registered in Chico"
            )
        )

        addSpace(content, 15)

        // =========================
        // COMPLETED APPOINTMENTS
        // =========================

        content.addView(
            createReportCard(
                "COMPLETED APPOINTMENTS",
                "0",
                "Completed appointments"
            )
        )

        addSpace(content, 30)

        // =========================
        // BACK BUTTON
        // =========================

        val backButton = TextView(this).apply {

            text = "BACK TO DASHBOARD"

            setTextColor(white)

            textSize = 15f

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true

            minHeight = 0
            minimumHeight = 0

            setPadding(
                dp(16),
                dp(10),
                dp(16),
                dp(10)
            )

            background = createButtonBackground(
                Color.rgb(55, 55, 55)
            )

            isClickable = true
            isFocusable = true

            setOnClickListener {
                finish()
            }
        }

        content.addView(
            backButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        addSpace(content, 20)

        // =========================
        // SCROLL VIEW
        // =========================

        scrollView.addView(content)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)

        // =========================
        // STATUS BAR / NOTCH
        // =========================

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { _, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            content.setPadding(
                dp(24),
                systemBars.top + dp(20),
                dp(24),
                systemBars.bottom + dp(25)
            )

            insets
        }

        ViewCompat.requestApplyInsets(scrollView)
    }

    // =========================
    // REPORT CARD
    // =========================

    private fun createReportCard(
        heading: String,
        value: String,
        description: String
    ): LinearLayout {

        val card = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            gravity = Gravity.CENTER

            setPadding(
                dp(18),
                dp(20),
                dp(18),
                dp(20)
            )

            background = createCardBackground()

            minimumHeight = dp(150)
        }

        // =========================
        // HEADING
        // =========================

        val headingText = TextView(this).apply {

            text = heading

            textSize = 14f

            setTextColor(accentColor)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        card.addView(
            headingText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(28)
            )
        )

        addSpace(card, 5)

        // =========================
        // VALUE
        // =========================

        val valueText = TextView(this).apply {

            text = value

            textSize = 30f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        card.addView(
            valueText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
            )
        )

        addSpace(card, 3)

        // =========================
        // DESCRIPTION
        // =========================

        val descriptionText = TextView(this).apply {

            text = description

            textSize = 13f

            setTextColor(grey)

            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true

            maxLines = 2
        }

        card.addView(
            descriptionText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(32)
            )
        )

        return card
    }

    // =========================
    // CARD BACKGROUND
    // =========================

    private fun createCardBackground(): GradientDrawable {

        return GradientDrawable().apply {

            shape = GradientDrawable.RECTANGLE

            setColor(cardColor)

            cornerRadius = dp(15).toFloat()

            setStroke(
                dp(1),
                Color.rgb(45, 45, 45)
            )
        }
    }

    // =========================
    // BUTTON BACKGROUND
    // =========================

    private fun createButtonBackground(
        color: Int
    ): GradientDrawable {

        return GradientDrawable().apply {

            shape = GradientDrawable.RECTANGLE

            setColor(color)

            cornerRadius = dp(12).toFloat()
        }
    }

    // =========================
    // SPACING
    // =========================

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {

        val space = Space(this)

        parent.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }
}