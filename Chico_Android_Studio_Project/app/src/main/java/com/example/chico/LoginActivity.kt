package com.example.chico

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
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
class LoginActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private val backgroundColor = Color.BLACK
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 200, 255)
    private val white = Color.WHITE
    private val grey = Color.rgb(150, 150, 150)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)

        createLoginScreen()
    }

    // =========================================================
    // DP HELPER
    // =========================================================

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    // =========================================================
    // CREATE LOGIN SCREEN
    // =========================================================

    private fun createLoginScreen() {

        // =====================================================
        // SCROLL VIEW
        // =====================================================

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        val root = LinearLayout(this).apply {
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

        ViewCompat.setOnApplyWindowInsetsListener(
            scrollView
        ) { _, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            root.setPadding(
                dp(24),
                systemBars.top + dp(25),
                dp(24),
                systemBars.bottom + dp(35)
            )

            insets
        }

        // =====================================================
        // CHICO LOGO
        // =====================================================

        val logo = TextView(this).apply {

            text = "CHICO"

            textSize = 40f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true
        }

        root.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // =====================================================
        // BARBERSHOP MANAGEMENT
        // =====================================================

        val subtitle = TextView(this).apply {

            text = "BARBERSHOP MANAGEMENT"

            textSize = 12f

            setTextColor(accentColor)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true
        }

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        // =====================================================
        // SPACE
        // =====================================================

        addSpace(root, 45)

        // =====================================================
        // WELCOME BACK
        // =====================================================

        val welcomeText = TextView(this).apply {

            text = "WELCOME BACK"

            textSize = 27f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

            includeFontPadding = true
        }

        root.addView(
            welcomeText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        // =====================================================
        // SPACE
        // =====================================================

        addSpace(root, 45)

        // =====================================================
        // EMAIL INPUT
        // =====================================================

        val emailInput = EditText(this).apply {

            hint = "Email"

            textSize = 15f

            setTextColor(white)

            setHintTextColor(grey)

            setSingleLine(true)

            inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

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
        }

        root.addView(
            emailInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // =====================================================
        // SPACE
        // =====================================================

        addSpace(root, 18)

        // =====================================================
        // PASSWORD INPUT
        // =====================================================

        val passwordInput = EditText(this).apply {

            hint = "Password"

            textSize = 15f

            setTextColor(white)

            setHintTextColor(grey)

            setSingleLine(true)

            inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

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
        }

        root.addView(
            passwordInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // =====================================================
        // SPACE
        // =====================================================

        addSpace(root, 28)

        // =====================================================
        // LOGIN BUTTON
        // =====================================================

        val loginButton = Button(this).apply {

            text = "LOGIN"

            textSize = 15f

            setTextColor(Color.WHITE)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

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
                createLoginButtonBackground()
        }

        root.addView(
            loginButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // =====================================================
        // SPACE
        // =====================================================

        addSpace(root, 14)

        // =====================================================
        // REGISTER BUTTON
        // =====================================================

        val registerButton = Button(this).apply {

            text = "REGISTER"

            textSize = 15f

            setTextColor(white)

            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            gravity = Gravity.CENTER

            textAlignment =
                View.TEXT_ALIGNMENT_CENTER

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
                createRegisterButtonBackground()
        }

        root.addView(
            registerButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // =====================================================
        // LOGIN FUNCTION
        // =====================================================

        loginButton.setOnClickListener {

            val email =
                emailInput.text
                    .toString()
                    .trim()

            val password =
                passwordInput.text
                    .toString()

            // =================================================
            // VALIDATION
            // =================================================

            if (
                email.isEmpty() ||
                password.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter your email and password",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // =================================================
            // CHECK USER
            // =================================================

            val userId =
                db.getUser(
                    email,
                    password
                )

            if (userId == -1L) {

                Toast.makeText(
                    this,
                    "Incorrect email or password",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // =================================================
            // GET ROLE
            // =================================================

            val role =
                db.getUserRole(email)
                    ?.uppercase()

            if (role == null) {

                Toast.makeText(
                    this,
                    "Could not determine account role",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // =================================================
            // SAVE LOGIN
            // =================================================

            val prefs =
                getSharedPreferences(
                    "ChicoPrefs",
                    MODE_PRIVATE
                )

            prefs.edit()
                .putString(
                    "email",
                    email
                )
                .apply()

            // =================================================
            // ADMIN LOGIN
            // =================================================

            if (role == "ADMIN") {

                val intent =
                    Intent(
                        this,
                        AdminDashboardActivity::class.java
                    )

                intent.putExtra(
                    "email",
                    email
                )

                startActivity(intent)

                finish()

            } else {

                // =============================================
                // CUSTOMER LOGIN
                // =============================================

                val customerId =
                    db.getCustomerId(email)

                if (customerId == -1L) {

                    Toast.makeText(
                        this,
                        "Customer profile not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val intent =
                    Intent(
                        this,
                        CustomerDashboardActivity::class.java
                    )

                intent.putExtra(
                    "email",
                    email
                )

                intent.putExtra(
                    "customer_id",
                    customerId
                )

                startActivity(intent)

                finish()
            }
        }

        // =====================================================
        // REGISTER FUNCTION
        // =====================================================

        registerButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        // =====================================================
        // ADD ROOT TO SCROLL VIEW
        // =====================================================

        scrollView.addView(root)

        setContentView(scrollView)

        ViewCompat.requestApplyInsets(scrollView)
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
    // LOGIN BUTTON BACKGROUND
    // =========================================================

    private fun createLoginButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                Color.rgb(
                    0,
                    150,
                    220
                )
            )

            cornerRadius =
                dp(12).toFloat()
        }
    }

    // =========================================================
    // REGISTER BUTTON BACKGROUND
    // =========================================================

    private fun createRegisterButtonBackground():
            GradientDrawable {

        return GradientDrawable().apply {

            shape =
                GradientDrawable.RECTANGLE

            setColor(
                Color.rgb(
                    55,
                    55,
                    55
                )
            )

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
}