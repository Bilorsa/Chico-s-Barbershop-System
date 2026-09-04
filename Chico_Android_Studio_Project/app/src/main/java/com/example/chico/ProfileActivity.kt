package com.example.chico

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    // =========================================================
    // DATABASE
    // =========================================================

    private lateinit var db: ChicoDb

    // =========================================================
    // INPUT FIELDS
    // =========================================================

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText

    // =========================================================
    // CUSTOMER INFORMATION
    // =========================================================

    private var customerEmail = ""
    private var userId = -1L

    // =========================================================
    // COLOURS
    // =========================================================

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)
    private val backButtonColor = Color.rgb(45, 45, 45)

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

        db = ChicoDb(this)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        // -----------------------------------------------------
        // GET EMAIL
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
        // SAVE EMAIL
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

        createProfileScreen()

        loadCustomer()
    }

    // =========================================================
    // CREATE PROFILE SCREEN
    // =========================================================

    private fun createProfileScreen() {

        val root = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setBackgroundColor(backgroundColor)
        }

        // =====================================================
        // HEADER
        // =====================================================

        val header = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(24),
                dp(55),
                dp(24),
                dp(20)
            )
        }

        val title = TextView(this).apply {

            text = "MY PROFILE"

            textSize = 26f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(primaryText)

            includeFontPadding = true

            gravity = Gravity.CENTER_VERTICAL
        }

        header.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this).apply {

            text = "Manage your Chico customer information."

            textSize = 14f

            setTextColor(secondaryText)

            includeFontPadding = true

            setPadding(
                0,
                dp(6),
                0,
                0
            )
        }

        header.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(header)

        // =====================================================
        // SCROLL CONTENT
        // =====================================================

        val scrollView = ScrollView(this).apply {

            isFillViewport = true
        }

        val content = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(20),
                dp(8),
                dp(20),
                dp(30)
            )
        }

        scrollView.addView(content)

        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        content.addView(
            sectionTitle("PERSONAL INFORMATION")
        )

        // =====================================================
        // NAME
        // =====================================================

        content.addView(
            createLabel("FULL NAME")
        )

        nameInput = createInput(
            "Your full name"
        )

        content.addView(
            nameInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(
            content,
            18
        )

        // =====================================================
        // EMAIL
        // =====================================================

        content.addView(
            createLabel("EMAIL")
        )

        emailInput = createInput(
            "Your email address"
        )

        content.addView(
            emailInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(
            content,
            18
        )

        // =====================================================
        // PHONE
        // =====================================================

        content.addView(
            createLabel("PHONE NUMBER")
        )

        phoneInput = createInput(
            "Your phone number"
        )

        content.addView(
            phoneInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(
            content,
            30
        )

        // =====================================================
        // SAVE BUTTON
        // =====================================================

        val saveButton = TextView(this).apply {

            text = "SAVE CHANGES"

            textSize = 14f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(Color.WHITE)

            gravity = Gravity.CENTER

            includeFontPadding = true

            setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
            )

            background = createRoundedBackground(
                accentColor,
                dp(10)
            )

            setOnClickListener {

                updateCustomer()
            }
        }

        content.addView(
            saveButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        addSpace(
            content,
            14
        )

        // =====================================================
        // BACK BUTTON
        // =====================================================

        val backButton = TextView(this).apply {

            text = "BACK TO DASHBOARD"

            textSize = 14f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(primaryText)

            gravity = Gravity.CENTER

            includeFontPadding = true

            setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
            )

            background = createRoundedBackground(
                backButtonColor,
                dp(10)
            )

            setOnClickListener {

                finish()
            }
        }

        content.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        // =====================================================
        // ADD SCROLL VIEW
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
    // LOAD CUSTOMER
    // =========================================================

    private fun loadCustomer() {

        if (customerEmail.isEmpty()) {

            Toast.makeText(
                this,
                "Customer account could not be identified.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val cursor = db.readableDatabase.query(

            ChicoDb.TABLE_USERS,

            arrayOf(
                ChicoDb.USER_ID,
                ChicoDb.USER_NAME,
                ChicoDb.USER_EMAIL,
                ChicoDb.USER_PHONE,
                ChicoDb.USER_ROLE
            ),

            "${ChicoDb.USER_EMAIL} = ?",

            arrayOf(customerEmail),

            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {

            userId = cursor.getLong(
                cursor.getColumnIndexOrThrow(
                    ChicoDb.USER_ID
                )
            )

            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    ChicoDb.USER_NAME
                )
            )

            val email = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    ChicoDb.USER_EMAIL
                )
            )

            val phone = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    ChicoDb.USER_PHONE
                )
            )

            nameInput.setText(name)

            emailInput.setText(email)

            phoneInput.setText(phone)

            // Email identifies the account,
            // so don't allow it to be changed here.

            emailInput.isEnabled = false

        } else {

            Toast.makeText(
                this,
                "Customer profile could not be found.",
                Toast.LENGTH_LONG
            ).show()
        }

        cursor.close()
    }

    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    private fun updateCustomer() {

        // -----------------------------------------------------
        // CHECK CUSTOMER
        // -----------------------------------------------------

        if (userId == -1L) {

            Toast.makeText(
                this,
                "Customer profile could not be found.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // -----------------------------------------------------
        // GET VALUES
        // -----------------------------------------------------

        val name = nameInput.text
            .toString()
            .trim()

        val phone = phoneInput.text
            .toString()
            .trim()

        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (name.isEmpty()) {

            nameInput.error =
                "Enter your full name"

            nameInput.requestFocus()

            return
        }

        // -----------------------------------------------------
        // VALIDATE PHONE
        // -----------------------------------------------------

        if (phone.isEmpty()) {

            phoneInput.error =
                "Enter your phone number"

            phoneInput.requestFocus()

            return
        }

        // -----------------------------------------------------
        // UPDATE DATABASE
        // -----------------------------------------------------

        val values = ContentValues().apply {

            put(
                ChicoDb.USER_NAME,
                name
            )

            put(
                ChicoDb.USER_PHONE,
                phone
            )
        }

        val rowsUpdated = db.writableDatabase.update(

            ChicoDb.TABLE_USERS,

            values,

            "${ChicoDb.USER_ID} = ?",

            arrayOf(
                userId.toString()
            )
        )

        // -----------------------------------------------------
        // RESULT
        // -----------------------------------------------------

        if (rowsUpdated > 0) {

            Toast.makeText(
                this,
                "Profile updated successfully!",
                Toast.LENGTH_LONG
            ).show()

        } else {

            Toast.makeText(
                this,
                "No changes were made.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =========================================================
    // CREATE INPUT
    // =========================================================

    private fun createInput(
        hintText: String
    ): EditText {

        return EditText(this).apply {

            hint = hintText

            textSize = 15f

            setTextColor(primaryText)

            setHintTextColor(secondaryText)

            setBackground(
                createRoundedBackground(
                    cardColor,
                    dp(8)
                )
            )

            setPadding(
                dp(18),
                dp(8),
                dp(18),
                dp(8)
            )

            includeFontPadding = true

            gravity = Gravity.CENTER_VERTICAL

            minimumHeight = 0
        }
    }

    // =========================================================
    // CREATE LABEL
    // =========================================================

    private fun createLabel(
        textValue: String
    ): TextView {

        return TextView(this).apply {

            text = textValue

            textSize = 11f

            typeface = Typeface.DEFAULT_BOLD

            letterSpacing = 0.15f

            setTextColor(accentColor)

            includeFontPadding = true

            setPadding(
                0,
                0,
                0,
                dp(8)
            )
        }
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private fun sectionTitle(
        textValue: String
    ): TextView {

        return TextView(this).apply {

            text = textValue

            textSize = 11f

            typeface = Typeface.DEFAULT_BOLD

            letterSpacing = 0.15f

            setTextColor(accentColor)

            includeFontPadding = true

            setPadding(
                0,
                0,
                0,
                dp(12)
            )
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
                dp(height)
            )
        )
    }

    // =========================================================
    // ROUNDED BACKGROUND
    // =========================================================

    private fun createRoundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {

            setColor(color)

            cornerRadius = radius.toFloat()
        }
    }

    // =========================================================
    // CLOSE DATABASE
    // =========================================================

    override fun onDestroy() {

        db.close()

        super.onDestroy()
    }
}