
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

@Suppress("SetTextI18n")
class RegisterActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb
    private var isRegistering = false

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

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

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

        // Window insets
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { _, insets ->

            val systemBars = insets.getInsets(
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

        // Logo
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

        content.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // Logo subtitle
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

        content.addView(
            logoSubtitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(content, 30)

        // Title
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

        content.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        // Subtitle
        val subtitle = TextView(this).apply {
            text = "Create your Chico customer account"
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
                dp(35)
            )
        )

        addSpace(content, 28)

        // Full name
        content.addView(createLabel("Full Name"))
        addSpace(content, 7)

        nameInput = createInput("Enter your full name")
        content.addView(nameInput)

        addSpace(content, 18)

        // Email
        content.addView(createLabel("Email Address"))
        addSpace(content, 7)

        emailInput = createInput("Enter your email address")
        emailInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        content.addView(emailInput)

        addSpace(content, 18)

        // Phone
        content.addView(createLabel("Phone Number"))
        addSpace(content, 7)

        phoneInput = createInput("Enter your phone number")
        phoneInput.inputType = InputType.TYPE_CLASS_PHONE

        content.addView(phoneInput)

        addSpace(content, 18)

        // Password
        content.addView(createLabel("Password"))
        addSpace(content, 7)

        passwordInput = createInput("Enter your password")
        passwordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        content.addView(passwordInput)

        addSpace(content, 18)

        // Confirm password
        content.addView(createLabel("Confirm Password"))
        addSpace(content, 7)

        confirmPasswordInput = createInput("Confirm your password")
        confirmPasswordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        content.addView(confirmPasswordInput)

        addSpace(content, 28)

        // Create account button
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

            background = createButtonBackground(accentColor)

            setOnClickListener {
                registerUser()
            }
        }

        content.addView(
            createAccountButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // Back to login
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

            background = createButtonBackground(
                Color.rgb(55, 55, 55)
            )

            setOnClickListener {
                finish()
            }
        }

        content.addView(
            backButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        addSpace(content, 15)

        scrollView.addView(content)
        setContentView(scrollView)

        ViewCompat.requestApplyInsets(scrollView)
    }

    // =========================================================
    // CREATE INPUT
    // =========================================================

    private fun createInput(hintText: String): EditText {

        return EditText(this).apply {
            hint = hintText
            textSize = 15f
            setTextColor(white)

            setHintTextColor(
                Color.rgb(120, 120, 120)
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

            background = createInputBackground()

            minHeight = 0
            minimumHeight = 0

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        }
    }

    // =========================================================
    // CREATE LABEL
    // =========================================================

    private fun createLabel(textValue: String): TextView {

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

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(28)
            )
        }
    }

    // =========================================================
    // INPUT BACKGROUND
    // =========================================================

    private fun createInputBackground(): GradientDrawable {

        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(cardColor)
            cornerRadius = dp(12).toFloat()

            setStroke(
                dp(1),
                Color.rgb(55, 55, 55)
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
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = dp(12).toFloat()
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
    // REGISTER USER WITH FIREBASE
    // =========================================================

    private fun registerUser() {

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        // Validate name
        if (name.isEmpty()) {
            nameInput.error = "Enter your name"
            nameInput.requestFocus()
            return
        }

        // Validate email
        if (email.isEmpty()) {
            emailInput.error = "Enter your email"
            emailInput.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Enter a valid email"
            emailInput.requestFocus()
            return
        }

        // Validate phone
        if (phone.isEmpty()) {
            phoneInput.error = "Enter your phone number"
            phoneInput.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            passwordInput.error = "Enter a password"
            passwordInput.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordInput.error =
                "Password must be at least 6 characters"
            passwordInput.requestFocus()
            return
        }

        // Validate password confirmation
        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.error = "Confirm your password"
            confirmPasswordInput.requestFocus()
            return
        }

        if (password != confirmPassword) {
            confirmPasswordInput.error = "Passwords do not match"
            confirmPasswordInput.requestFocus()
            return
        }

        // Prevent duplicate submissions
        if (isRegistering) return
        isRegistering = true

        // Firebase Authentication handles the password.
        // Do not store the password in SQLite or Firestore.
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { authTask ->

                if (!authTask.isSuccessful) {
                    isRegistering = false

                    when (val exception = authTask.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            emailInput.error =
                                "An account with this email already exists"
                            emailInput.requestFocus()
                        }

                        is FirebaseAuthWeakPasswordException -> {
                            passwordInput.error =
                                "Please choose a stronger password"
                            passwordInput.requestFocus()
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            emailInput.error =
                                "Please enter a valid email address"
                            emailInput.requestFocus()
                        }

                        else -> {
                            Toast.makeText(
                                this,
                                exception?.localizedMessage
                                    ?: "Registration failed. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    return@addOnCompleteListener
                }

                val firebaseUser = authTask.result?.user

                if (firebaseUser == null) {
                    isRegistering = false
                    FirebaseAuth.getInstance().signOut()

                    Toast.makeText(
                        this,
                        "Could not create your account. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnCompleteListener
                }

                // Keep the local customer record available to the
                // existing SQLite-based booking screens.
                var insertedLocalCustomer = false

                try {
                    if (!db.customerExists(email)) {

                        val customerValues = ContentValues().apply {
                            put(ChicoDb.CUSTOMER_NAME, name)
                            put(ChicoDb.CUSTOMER_PHONE, phone)
                            put(ChicoDb.CUSTOMER_EMAIL, email)
                        }

                        val customerId = db.insert(
                            ChicoDb.TABLE_CUSTOMERS,
                            customerValues
                        )

                        if (customerId == -1L) {
                            throw IllegalStateException(
                                "Could not create the local customer record."
                            )
                        }

                        insertedLocalCustomer = true
                    }
                } catch (exception: Exception) {

                    if (insertedLocalCustomer) {
                        db.delete(
                            ChicoDb.TABLE_CUSTOMERS,
                            "${ChicoDb.CUSTOMER_EMAIL} = ?",
                            arrayOf(email)
                        )
                    }

                    firebaseUser.delete().addOnCompleteListener {
                        FirebaseAuth.getInstance().signOut()
                        isRegistering = false

                        Toast.makeText(
                            this,
                            "Could not create the customer record. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()

                        android.util.Log.e(
                            "ChicoRegistration",
                            "SQLite customer creation failed",
                            exception
                        )
                    }

                    return@addOnCompleteListener
                }

                // Store customer profile data in Firestore.
                // The document ID is the user's Firebase UID.
                val customerProfile = hashMapOf<String, Any>(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "role" to "CUSTOMER",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                FirebaseFirestore.getInstance()
                    .collection("customers")
                    .document(firebaseUser.uid)
                    .set(customerProfile)
                    .addOnSuccessListener {

                        isRegistering = false

                        // Registration finishes on the login screen.
                        FirebaseAuth.getInstance().signOut()

                        Toast.makeText(
                            this,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = android.content.Intent(
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
                    .addOnFailureListener { exception ->

                        // Remove only the local row created by this attempt.
                        if (insertedLocalCustomer) {
                            db.delete(
                                ChicoDb.TABLE_CUSTOMERS,
                                "${ChicoDb.CUSTOMER_EMAIL} = ?",
                                arrayOf(email)
                            )
                        }

                        // Attempt to remove the newly created Firebase account
                        // so the customer can retry registration.
                        firebaseUser.delete().addOnCompleteListener {
                            FirebaseAuth.getInstance().signOut()
                            isRegistering = false

                            Toast.makeText(
                                this,
                                "Could not save your profile to Firebase. " +
                                        "Check your internet connection and Firestore rules, then try again.",
                                Toast.LENGTH_LONG
                            ).show()

                            android.util.Log.e(
                                "ChicoRegistration",
                                "Firestore profile creation failed",
                                exception
                            )
                        }
                    }
            }
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
