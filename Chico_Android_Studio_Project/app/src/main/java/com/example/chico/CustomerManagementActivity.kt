package com.example.chico

import android.app.AlertDialog
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

class CustomerManagementActivity : AppCompatActivity() {

    // =========================================================
    // COLORS
    // =========================================================

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)
    private val deleteColor = Color.rgb(180, 50, 50)
    private val backColor = Color.rgb(50, 50, 50)

    private lateinit var db: ChicoDb
    private lateinit var customerContainer: LinearLayout

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

        createCustomerManagement()
        loadCustomers()
    }

    // =========================================================
    // CUSTOMER MANAGEMENT SCREEN
    // =========================================================

    private fun createCustomerManagement() {

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
            "CUSTOMER MANAGEMENT"

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
            "Customers"

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
            "View and manage registered customers."

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
            18
        )

        // =====================================================
        // CUSTOMER LIST
        // =====================================================

        customerContainer =
            LinearLayout(this)

        customerContainer.orientation =
            LinearLayout.VERTICAL

        mainLayout.addView(
            customerContainer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(
            mainLayout,
            15
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
    // LOAD CUSTOMERS
    // =========================================================

    private fun loadCustomers() {

        customerContainer.removeAllViews()

        val cursor =
            db.readableDatabase.rawQuery(
                """
                SELECT id, name, phone, email
                FROM customers
                ORDER BY name ASC
                """.trimIndent(),
                null
            )

        // =====================================================
        // NO CUSTOMERS
        // =====================================================

        if (cursor.count == 0) {

            val emptyText =
                TextView(this)

            emptyText.text =
                "No customers registered yet."

            emptyText.textSize =
                16f

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

            customerContainer.addView(
                emptyText
            )

            cursor.close()

            return
        }

        // =====================================================
        // READ CUSTOMERS
        // =====================================================

        while (cursor.moveToNext()) {

            val customerId =
                cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        "id"
                    )
                )

            val name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "name"
                    )
                )

            val phone =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "phone"
                    )
                )

            val email =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "email"
                    )
                )

            createCustomerCard(
                customerId,
                name,
                phone,
                email
            )
        }

        cursor.close()
    }

    // =========================================================
    // CUSTOMER CARD
    // =========================================================

    private fun createCustomerCard(
        customerId: Long,
        name: String,
        phone: String,
        email: String
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.gravity =
            Gravity.CENTER_VERTICAL

        // Proper dp spacing
        card.setPadding(
            dp(18),
            dp(20),
            dp(18),
            dp(20)
        )

        card.background =
            createCustomerCardBackground()

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

        customerContainer.addView(
            card,
            cardParams
        )

        // =====================================================
        // CUSTOMER NAME
        // =====================================================

        val nameText =
            createInfoText(
                name,
                18f,
                primaryText,
                true
            )

        card.addView(
            nameText,
            createTextParams(34)
        )

        addSpace(
            card,
            7
        )

        // =====================================================
        // PHONE
        // =====================================================

        val phoneText =
            createInfoText(
                "Phone: $phone",
                14f,
                secondaryText,
                false
            )

        card.addView(
            phoneText,
            createTextParams(28)
        )

        addSpace(
            card,
            3
        )

        // =====================================================
        // EMAIL
        // =====================================================

        val emailText =
            createInfoText(
                "Email: $email",
                14f,
                secondaryText,
                false
            )

        card.addView(
            emailText,
            createTextParams(28)
        )

        addSpace(
            card,
            14
        )

        // =====================================================
        // DELETE BUTTON
        // =====================================================

        val deleteButton =
            createDeleteButton()

        deleteButton.setOnClickListener {

            showDeleteConfirmation(
                customerId,
                name
            )
        }

        card.addView(
            deleteButton
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
    // DELETE BUTTON
    // =========================================================

    private fun createDeleteButton():
            TextView {

        val button =
            TextView(this)

        button.text =
            "DELETE"

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
            createDeleteButtonBackground()

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
    // CUSTOMER CARD BACKGROUND
    // =========================================================

    private fun createCustomerCardBackground():
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
    // DELETE BUTTON BACKGROUND
    // =========================================================

    private fun createDeleteButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                deleteColor
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
    // DELETE CONFIRMATION
    // =========================================================

    private fun showDeleteConfirmation(
        customerId: Long,
        customerName: String
    ) {

        AlertDialog.Builder(this)
            .setTitle(
                "Delete Customer"
            )
            .setMessage(
                "Are you sure you want to delete $customerName?"
            )
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "DELETE"
            ) { _, _ ->

                deleteCustomer(
                    customerId
                )
            }
            .show()
    }

    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    private fun deleteCustomer(
        customerId: Long
    ) {

        db.writableDatabase.delete(
            "customers",
            "id = ?",
            arrayOf(
                customerId.toString()
            )
        )

        loadCustomers()
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