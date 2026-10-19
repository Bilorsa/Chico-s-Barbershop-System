
package com.example.chico

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: ChicoDb

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var saveButton: TextView

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)
    private val backButtonColor = Color.rgb(45, 45, 45)

    private var customerEmail = ""
    private var customerId = -1L
    private var isProfileLoaded = false

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ChicoDb(this)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        customerEmail = auth.currentUser?.email
            ?: intent.getStringExtra("email")
                    ?: intent.getStringExtra("registered_email")
                    ?: getSharedPreferences("ChicoPrefs", MODE_PRIVATE)
                .getString("email", "")
                .orEmpty()

        if (customerEmail.isNotBlank()) {
            getSharedPreferences("ChicoPrefs", MODE_PRIVATE)
                .edit()
                .putString("email", customerEmail)
                .apply()
        }

        createProfileScreen()
        loadCustomer()
    }

    private fun createProfileScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

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
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle = TextView(this).apply {
            text = "Manage your Chico customer information."
            textSize = 14f
            setTextColor(secondaryText)
            includeFontPadding = true
            setPadding(0, dp(6), 0, 0)
        }

        header.addView(
            subtitle,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(header)

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

        content.addView(sectionTitle("PERSONAL INFORMATION"))

        content.addView(createLabel("FULL NAME"))

        nameInput = createInput("Your full name")

        content.addView(
            nameInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(content, 18)

        content.addView(createLabel("EMAIL"))

        emailInput = createInput("Your email address").apply {
            isEnabled = false
            alpha = 0.75f
        }

        content.addView(
            emailInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(content, 18)

        content.addView(createLabel("PHONE NUMBER"))

        phoneInput = createInput("Your phone number")

        content.addView(
            phoneInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        addSpace(content, 30)

        saveButton = TextView(this).apply {
            text = "SAVE CHANGES"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            includeFontPadding = true
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = createRoundedBackground(
                accentColor,
                dp(10)
            )
            isEnabled = false
            alpha = 0.6f

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

        addSpace(content, 14)

        val backButton = TextView(this).apply {
            text = "BACK TO DASHBOARD"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(primaryText)
            gravity = Gravity.CENTER
            includeFontPadding = true
            setPadding(dp(12), dp(12), dp(12), dp(12))
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
    // LOAD CUSTOMER PROFILE FROM FIRESTORE
    // =========================================================

    private fun loadCustomer() {

        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            Toast.makeText(
                this,
                "Please log in again to view your profile.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        customerEmail = firebaseUser.email ?: customerEmail

        if (customerEmail.isBlank()) {
            Toast.makeText(
                this,
                "Your account email could not be identified.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        emailInput.setText(customerEmail)

        firestore.collection("customers")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    Toast.makeText(
                        this,
                        "Your Firebase customer profile could not be found.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                val name = document.getString("name").orEmpty()
                val email = document.getString("email")
                    ?: customerEmail
                val phone = document.getString("phone").orEmpty()

                nameInput.setText(name)
                emailInput.setText(email)
                phoneInput.setText(phone)

                customerEmail = email

                // Keep the existing local customer record in sync
                // for booking features that still use SQLite.
                customerId = db.getCustomerId(customerEmail)

                if (customerId == -1L && name.isNotBlank()) {
                    db.createCustomer(name, phone, customerEmail)
                    customerId = db.getCustomerId(customerEmail)
                }

                isProfileLoaded = true
                saveButton.isEnabled = true
                saveButton.alpha = 1f
            }
            .addOnFailureListener { exception ->

                android.util.Log.e(
                    "ChicoProfile",
                    "Failed to load customer profile from Firestore",
                    exception
                )

                Toast.makeText(
                    this,
                    "Could not load your profile. Check your connection and Firestore rules.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // UPDATE CUSTOMER PROFILE IN FIRESTORE
    // =========================================================

    private fun updateCustomer() {

        val firebaseUser = auth.currentUser

        if (firebaseUser == null || !isProfileLoaded) {
            Toast.makeText(
                this,
                "Your profile is not ready to update.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val name = nameInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()

        if (name.isEmpty()) {
            nameInput.error = "Enter your full name"
            nameInput.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            phoneInput.error = "Enter your phone number"
            phoneInput.requestFocus()
            return
        }

        saveButton.isEnabled = false
        saveButton.text = "SAVING..."

        val updates = mapOf(
            "name" to name,
            "phone" to phone
        )

        firestore.collection("customers")
            .document(firebaseUser.uid)
            .update(updates)
            .addOnSuccessListener {

                // Keep the local SQLite customer details updated too.
                customerId = db.getCustomerId(customerEmail)

                if (customerId != -1L) {
                    val values = android.content.ContentValues().apply {
                        put(ChicoDb.USER_NAME, name)
                        put(ChicoDb.USER_PHONE, phone)
                    }

                    db.writableDatabase.update(
                        ChicoDb.TABLE_USERS,
                        values,
                        "${ChicoDb.USER_EMAIL} = ?",
                        arrayOf(customerEmail)
                    )

                    db.writableDatabase.update(
                        ChicoDb.TABLE_CUSTOMERS,
                        values,
                        "${ChicoDb.CUSTOMER_EMAIL} = ?",
                        arrayOf(customerEmail)
                    )
                } else {
                    db.createCustomer(name, phone, customerEmail)
                    customerId = db.getCustomerId(customerEmail)
                }

                saveButton.isEnabled = true
                saveButton.text = "SAVE CHANGES"

                Toast.makeText(
                    this,
                    "Profile updated successfully!",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->

                android.util.Log.e(
                    "ChicoProfile",
                    "Failed to update customer profile in Firestore",
                    exception
                )

                saveButton.isEnabled = true
                saveButton.text = "SAVE CHANGES"

                Toast.makeText(
                    this,
                    "Could not save your profile. Check your connection and Firestore rules.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // CREATE INPUT
    // =========================================================

    private fun createInput(hintText: String): EditText {

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

    private fun createLabel(textValue: String): TextView {

        return TextView(this).apply {
            text = textValue
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.15f
            setTextColor(accentColor)
            includeFontPadding = true
            setPadding(0, 0, 0, dp(8))
        }
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private fun sectionTitle(textValue: String): TextView {

        return TextView(this).apply {
            text = textValue
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.15f
            setTextColor(accentColor)
            includeFontPadding = true
            setPadding(0, 0, 0, dp(12))
        }
    }

    // =========================================================
    // ADD SPACE
    // =========================================================

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {
        parent.addView(
            View(this),
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
        if (::db.isInitialized) {
            db.close()
        }
        super.onDestroy()
    }
}
