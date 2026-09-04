package com.example.chico

import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

@Suppress("SetTextI18n")
class RegisterActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText

    private val backgroundColor = Color.rgb(8, 8, 8)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val white = Color.WHITE
    private val grey = Color.rgb(170, 170, 170)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)

        createRegisterScreen()
    }

    // =========================================================
    // DP HELPER
    // =========================================================

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    // =========================================================
    // CREATE REGISTER SCREEN
    // =========================================================

    private fun createRegisterScreen() {

        // =====================================================
        // SCROLL VIEW
        // =====================================================

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        // =====================================================
        // CONTENT
        // =====================================================

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(backgroundColor)

            setPadding(
                dp(24),
                dp(25),
                dp(24),
                dp(35)
            )
        }

        // =====================================================
        // WINDOW INSETS
        // =====================================================

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { _, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            content.setPadding(
                dp(24),
                systemBars.top + dp(25),
                dp(24),
                systemBars.bottom + dp(35)
            )

            insets
        }

        // =====================================================
        // LOGO
        // =====================================================

        val logo = TextView(this).apply {

            text = "CHICO"

            textSize = 40f

            setTextColor(accentColor)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment = View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true
        }

        val logoParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(60)
        )

        content.addView(
            logo,
            logoParams
        )

        // =====================================================
        // LOGO SUBTITLE
        // =====================================================

        val logoSubtitle = TextView(this).apply {

            text = "BARBERSHOP"

            textSize = 13f

            setTextColor(grey)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment = View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true

            letterSpacing = 0.2f
        }

        val logoSubtitleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(30)
        )

        content.addView(
            logoSubtitle,
            logoSubtitleParams
        )

        addSpace(content, 30)

        // =====================================================
        // TITLE
        // =====================================================

        val title = TextView(this).apply {

            text = "CREATE ACCOUNT"

            textSize = 27f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment = View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true
        }

        val titleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(50)
        )

        content.addView(
            title,
            titleParams
        )

        // =====================================================
        // SUBTITLE
        // =====================================================

        val subtitle = TextView(this).apply {

            text = "Create your Chico customer account"

            textSize = 15f

            setTextColor(grey)

            gravity = Gravity.CENTER

            textAlignment = View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true
        }

        val subtitleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(35)
        )

        content.addView(
            subtitle,
            subtitleParams
        )

        addSpace(content, 28)

        // =====================================================
        // NAME
        // =====================================================

        content.addView(
            createLabel("Full Name")
        )

        addSpace(content, 7)

        nameInput = createInput(
            "Enter your full name"
        )

        content.addView(nameInput)

        addSpace(content, 18)

        // =====================================================
        // EMAIL
        // =====================================================

        content.addView(
            createLabel("Email Address")
        )

        addSpace(content, 7)

        emailInput = createInput(
            "Enter your email address"
        )

        emailInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        content.addView(emailInput)

        addSpace(content, 18)

        // =====================================================
        // PHONE
        // =====================================================

        content.addView(
            createLabel("Phone Number")
        )

        addSpace(content, 7)

        phoneInput = createInput(
            "Enter your phone number"
        )

        phoneInput.inputType =
            InputType.TYPE_CLASS_PHONE

        content.addView(phoneInput)

        addSpace(content, 18)

        // =====================================================
        // PASSWORD
        // =====================================================

        content.addView(
            createLabel("Password")
        )

        addSpace(content, 7)

        passwordInput = createInput(
            "Enter your password"
        )

        passwordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        content.addView(passwordInput)

        addSpace(content, 18)

        // =====================================================
        // CONFIRM PASSWORD
        // =====================================================

        content.addView(
            createLabel("Confirm Password")
        )

        addSpace(content, 7)

        confirmPasswordInput = createInput(
            "Confirm your password"
        )

        confirmPasswordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        content.addView(confirmPasswordInput)

        // =====================================================
        // CREATE ACCOUNT BUTTON
        // =====================================================

        addSpace(content, 28)

        val createAccountButton = Button(this).apply {

            text = "CREATE ACCOUNT"

            textSize = 15f

            setTextColor(Color.BLACK)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment = View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true

            isAllCaps = false

            minHeight = 0
            minimumHeight = 0

            setPadding(
                dp(16),
                0,
                dp(16),
                0
            )

            background =
                createButtonBackground(
                    accentColor
                )

            setOnClickListener {
                registerUser()
            }
        }

        val createButtonParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )

        content.addView(
            createAccountButton,
            createButtonParams
        )

        // =====================================================
        // BACK TO LOGIN
        // =====================================================

        addSpace(content, 14)

        val backButton = Button(this).apply {

            text = "BACK TO LOGIN"

            textSize = 15f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment = View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true

            isAllCaps = false

            minHeight = 0
            minimumHeight = 0

            setPadding(
                dp(16),
                0,
                dp(16),
                0
            )

            background =
                createButtonBackground(
                    Color.rgb(55, 55, 55)
                )

            setOnClickListener {
                finish()
            }
        }

        val backButtonParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )

        content.addView(
            backButton,
            backButtonParams
        )

        addSpace(content, 15)

        // =====================================================
        // ADD CONTENT TO SCROLL VIEW
        // =====================================================

        scrollView.addView(content)

        setContentView(scrollView)

        ViewCompat.requestApplyInsets(scrollView)
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

            setTextColor(white)

            setHintTextColor(
                Color.rgb(
                    120,
                    120,
                    120
                )
            )

            setSingleLine(true)

            gravity = Gravity.CENTER_VERTICAL

            includeFontPadding = true

            setPadding(
                dp(18),
                0,
                dp(18),
                0
            )

            background =
                createInputBackground()

            minHeight = 0
            minimumHeight = 0

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(60)
                )
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

            textSize = 14f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER_VERTICAL

            includeFontPadding = true

            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(28)
                )
        }
    }

    // =========================================================
    // INPUT BACKGROUND
    // =========================================================

    private fun createInputBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(cardColor)

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
    // BUTTON BACKGROUND
    // =========================================================

    private fun createButtonBackground(
        color: Int
    ): GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(color)

            cornerRadius =
                dp(12).toFloat()
        }
    }

    // =========================================================
    // SPACING
    // =========================================================

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

    // =========================================================
    // REGISTER USER
    // =========================================================

    private fun registerUser() {

        val name =
            nameInput.text
                .toString()
                .trim()

        val email =
            emailInput.text
                .toString()
                .trim()

        val phone =
            phoneInput.text
                .toString()
                .trim()

        val password =
            passwordInput.text
                .toString()

        val confirmPassword =
            confirmPasswordInput.text
                .toString()

        // =====================================================
        // VALIDATE NAME
        // =====================================================

        if (name.isEmpty()) {

            nameInput.error =
                "Enter your name"

            nameInput.requestFocus()

            return
        }

        // =====================================================
        // VALIDATE EMAIL
        // =====================================================

        if (email.isEmpty()) {

            emailInput.error =
                "Enter your email"

            emailInput.requestFocus()

            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            emailInput.error =
                "Enter a valid email"

            emailInput.requestFocus()

            return
        }

        // =====================================================
        // VALIDATE PHONE
        // =====================================================

        if (phone.isEmpty()) {

            phoneInput.error =
                "Enter your phone number"

            phoneInput.requestFocus()

            return
        }

        // =====================================================
        // VALIDATE PASSWORD
        // =====================================================

        if (password.isEmpty()) {

            passwordInput.error =
                "Enter a password"

            passwordInput.requestFocus()

            return
        }

        if (password.length < 6) {

            passwordInput.error =
                "Password must be at least 6 characters"

            passwordInput.requestFocus()

            return
        }

        // =====================================================
        // VALIDATE CONFIRM PASSWORD
        // =====================================================

        if (confirmPassword.isEmpty()) {

            confirmPasswordInput.error =
                "Confirm your password"

            confirmPasswordInput.requestFocus()

            return
        }

        if (password != confirmPassword) {

            confirmPasswordInput.error =
                "Passwords do not match"

            confirmPasswordInput.requestFocus()

            return
        }

        // =====================================================
        // CHECK EXISTING ACCOUNT
        // =====================================================

        if (db.userExists(email)) {

            emailInput.error =
                "An account with this email already exists"

            emailInput.requestFocus()

            return
        }

        // =====================================================
        // INSERT USER
        // =====================================================

        val userValues =
            ContentValues().apply {

                put(
                    ChicoDb.USER_NAME,
                    name
                )

                put(
                    ChicoDb.USER_PHONE,
                    phone
                )

                put(
                    ChicoDb.USER_EMAIL,
                    email
                )

                put(
                    ChicoDb.USER_PASSWORD,
                    password
                )

                put(
                    ChicoDb.USER_ROLE,
                    "CUSTOMER"
                )
            }

        val userId =
            db.insert(
                ChicoDb.TABLE_USERS,
                userValues
            )

        if (userId == -1L) {

            Toast.makeText(
                this,
                "Registration failed",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // INSERT CUSTOMER
        // =====================================================

        val customerValues =
            ContentValues().apply {

                put(
                    ChicoDb.CUSTOMER_NAME,
                    name
                )

                put(
                    ChicoDb.CUSTOMER_PHONE,
                    phone
                )

                put(
                    ChicoDb.CUSTOMER_EMAIL,
                    email
                )
            }

        val customerId =
            db.insert(
                ChicoDb.TABLE_CUSTOMERS,
                customerValues
            )

        if (customerId == -1L) {

            // Roll back user if customer
            // insert fails

            db.delete(
                ChicoDb.TABLE_USERS,
                "${ChicoDb.USER_EMAIL} = ?",
                arrayOf(email)
            )

            Toast.makeText(
                this,
                "Could not create customer profile",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // =====================================================
        // SUCCESS
        // =====================================================

        Toast.makeText(
            this,
            "Account created successfully",
            Toast.LENGTH_SHORT
        ).show()

        val intent =
            android.content.Intent(
                this,
                LoginActivity::class.java
            )

        intent.putExtra(
            "registered_email",
            email
        )

        startActivity(intent)

        finish()
    }

    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroy() {

        super.onDestroy()

        if (::db.isInitialized) {
            db.close()
        }
    }
}