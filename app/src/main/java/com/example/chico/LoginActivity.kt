
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

@Suppress("SetTextI18n")
class LoginActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private val backgroundColor = Color.BLACK
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 200, 255)
    private val white = Color.WHITE
    private val grey = Color.rgb(150, 150, 150)

    // TEMPORARY shared password for barber/admin access.
    // Change this to the password you want to distribute.
    // This client-side password is not secure enough for a production app.
    private val sharedAdminPassword = "ChangeMe123!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)
        createLoginScreen()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    private fun createLoginScreen() {

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(24), dp(25), dp(24), dp(35))
        }

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { _, insets ->
            val systemBars = insets.getInsets(
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

        val logo = TextView(this).apply {
            text = "CHICO"
            textSize = 40f
            setTextColor(white)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        root.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        val subtitle = TextView(this).apply {
            text = "BARBERSHOP MANAGEMENT"
            textSize = 12f
            setTextColor(accentColor)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        root.addView(
            subtitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        addSpace(root, 45)

        val welcomeText = TextView(this).apply {
            text = "WELCOME BACK"
            textSize = 27f
            setTextColor(white)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        root.addView(
            welcomeText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        addSpace(root, 45)

        val emailInput = createInput(
            hintText = "Email",
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )

        root.addView(
            emailInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        addSpace(root, 18)

        val passwordInput = createInput(
            hintText = "Password",
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        root.addView(
            passwordInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        addSpace(root, 28)

        val loginButton = createButton(
            buttonText = "LOGIN",
            buttonColor = Color.rgb(0, 150, 220),
            textColor = white
        )

        root.addView(
            loginButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        addSpace(root, 14)

        val registerButton = createButton(
            buttonText = "REGISTER",
            buttonColor = Color.rgb(55, 55, 55),
            textColor = white
        )

        root.addView(
            registerButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        // Separate access option for barbers and the administrator.
        addSpace(root, 24)

        val barberAccessLabel = TextView(this).apply {
            text = "STAFF ACCESS"
            textSize = 12f
            setTextColor(grey)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
        }

        root.addView(
            barberAccessLabel,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(25)
            )
        )

        addSpace(root, 8)

        val barberAccessButton = createButton(
            buttonText = "BARBER / ADMIN ACCESS",
            buttonColor = Color.rgb(0, 75, 100),
            textColor = white
        )

        root.addView(
            barberAccessButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        )

        loginButton.setOnClickListener {

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter your email and password",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            loginButton.isEnabled = false
            loginButton.text = "LOGGING IN..."

            loginUser(email, password, loginButton)
        }

        registerButton.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        barberAccessButton.setOnClickListener {
            showBarberAccessDialog()
        }

        scrollView.addView(root)
        setContentView(scrollView)
        ViewCompat.requestApplyInsets(scrollView)
    }

    private fun createInput(
        hintText: String,
        inputType: Int
    ): EditText {

        return EditText(this).apply {
            hint = hintText
            textSize = 15f
            setTextColor(white)
            setHintTextColor(grey)
            setSingleLine(true)
            this.inputType = inputType
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true

            setPadding(dp(18), 0, dp(18), 0)

            background = createInputBackground()

            minHeight = 0
            minimumHeight = 0
        }
    }

    private fun createButton(
        buttonText: String,
        buttonColor: Int,
        textColor: Int
    ): Button {

        return Button(this).apply {
            text = buttonText
            textSize = 15f
            setTextColor(textColor)

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

            setPadding(dp(16), 0, dp(16), 0)

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(buttonColor)
                cornerRadius = dp(12).toFloat()
            }
        }
    }

    // =========================================================
    // BARBER / ADMIN SHARED-PASSWORD ACCESS
    // =========================================================

    private fun showBarberAccessDialog() {

        val passwordInput = EditText(this).apply {
            hint = "Enter staff password"
            textSize = 16f
            setTextColor(white)
            setHintTextColor(grey)
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
            setSingleLine(true)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), 0, dp(16), 0)
            background = createInputBackground()
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(8), dp(24), 0)
            addView(
                passwordInput,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(55)
                )
            )
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Barber / Admin Access")
            .setMessage("Enter the shared staff password to continue.")
            .setView(container)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("CONTINUE", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(accentColor)
                setOnClickListener {

                    val enteredPassword =
                        passwordInput.text.toString()

                    if (enteredPassword.isBlank()) {
                        passwordInput.error =
                            "Please enter the staff password"
                        return@setOnClickListener
                    }

                    if (enteredPassword == sharedAdminPassword) {

                        // Clear customer Firebase authentication so the
                        // staff flow does not reuse a customer's session.
                        FirebaseAuth.getInstance().signOut()

                        // Mark the session as staff access for the
                        // existing dashboard's logout/preferences flow.
                        getSharedPreferences(
                            "ChicoPrefs",
                            MODE_PRIVATE
                        ).edit()
                            .clear()
                            .putString("email", "barber@chico.com")
                            .putString("role", "ADMIN")
                            .apply()

                        dialog.dismiss()

                        openAdminDashboard("barber@chico.com")
                    } else {
                        passwordInput.error = "Incorrect staff password"
                        passwordInput.text?.clear()
                    }
                }
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(grey)
        }

        dialog.show()
    }

    // =========================================================
    // LOGIN USING FIREBASE AUTHENTICATION
    // =========================================================

    private fun loginUser(
        email: String,
        password: String,
        loginButton: Button
    ) {

        // Keep the existing local administrator account working.
        val localUserId = db.getUser(email, password)

        val localRole = if (localUserId != -1L) {
            db.getUserRole(email)?.uppercase()
        } else {
            null
        }

        if (localRole == "ADMIN") {
            saveLoginEmail(email)
            openAdminDashboard(email)
            return
        }

        // Authenticate customers through Firebase.
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (!task.isSuccessful) {
                    android.util.Log.e(
                        "ChicoLogin",
                        "Firebase authentication failed",
                        task.exception
                    )

                    resetLoginButton(loginButton)

                    Toast.makeText(
                        this,
                        "Incorrect email or password",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnCompleteListener
                }

                val firebaseUser =
                    FirebaseAuth.getInstance().currentUser

                if (firebaseUser == null) {
                    resetLoginButton(loginButton)

                    Toast.makeText(
                        this,
                        "Could not verify your account. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnCompleteListener
                }

                // Load the customer's Firestore profile.
                FirebaseFirestore.getInstance()
                    .collection("customers")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->

                        if (!document.exists()) {
                            FirebaseAuth.getInstance().signOut()
                            resetLoginButton(loginButton)

                            Toast.makeText(
                                this,
                                "Customer profile not found in Firebase. Please contact support.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addOnSuccessListener
                        }

                        val role = document.getString("role")
                            ?.uppercase()
                            ?: "CUSTOMER"

                        saveLoginEmail(email)

                        if (role == "ADMIN") {
                            openAdminDashboard(email)
                            return@addOnSuccessListener
                        }

                        // Existing booking features use the local
                        // SQLite customer ID.
                        var customerId = db.getCustomerId(email)

                        // Re-create the local customer record if missing.
                        if (customerId == -1L) {
                            val name =
                                document.getString("name").orEmpty()

                            val phone =
                                document.getString("phone").orEmpty()

                            if (name.isNotBlank() && phone.isNotBlank()) {
                                db.createCustomer(name, phone, email)
                                customerId = db.getCustomerId(email)
                            }
                        }

                        if (customerId == -1L) {
                            FirebaseAuth.getInstance().signOut()
                            resetLoginButton(loginButton)

                            Toast.makeText(
                                this,
                                "Could not prepare your customer profile for bookings. Please contact support.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addOnSuccessListener
                        }

                        val intent = Intent(
                            this,
                            CustomerDashboardActivity::class.java
                        ).apply {
                            putExtra("email", email)
                            putExtra("customer_id", customerId)
                        }

                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->

                        FirebaseAuth.getInstance().signOut()
                        resetLoginButton(loginButton)

                        android.util.Log.e(
                            "ChicoLogin",
                            "Could not load customer profile from Firestore",
                            exception
                        )

                        Toast.makeText(
                            this,
                            "Could not load your profile. Check your internet connection and Firestore rules.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
    }

    private fun resetLoginButton(loginButton: Button) {
        loginButton.isEnabled = true
        loginButton.text = "LOGIN"
    }

    private fun saveLoginEmail(email: String) {
        getSharedPreferences(
            "ChicoPrefs",
            MODE_PRIVATE
        ).edit()
            .putString("email", email)
            .apply()
    }

    private fun openAdminDashboard(email: String) {
        val intent = Intent(
            this,
            AdminDashboardActivity::class.java
        )

        intent.putExtra("email", email)

        startActivity(intent)
        finish()
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
