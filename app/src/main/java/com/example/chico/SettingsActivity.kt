
package com.example.chico

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    private val backgroundColor = Color.rgb(5, 5, 5)
    private val cardColor = Color.rgb(25, 25, 25)
    private val accentColor = Color.rgb(0, 174, 239)
    private val primaryText = Color.WHITE
    private val secondaryText = Color.rgb(170, 170, 170)
    private val logoutColor = Color.rgb(55, 55, 55)
    private val dangerColor = Color.rgb(255, 91, 91)

    private var userEmail = ""
    private var userRole = "Customer"

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).roundToInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        userEmail = intent.getStringExtra("email")
            ?: getSharedPreferences("ChicoPrefs", MODE_PRIVATE)
                .getString("email", "")
                .orEmpty()

        userRole = intent.getStringExtra("role")
            ?: getSharedPreferences("ChicoPrefs", MODE_PRIVATE)
                .getString("role", "Customer")
                .orEmpty()

        createSettingsScreen()
    }

    private fun createSettingsScreen() {
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(25), dp(24), dp(35))
        }

        scrollView.addView(mainLayout)

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            mainLayout.setPadding(
                dp(24),
                bars.top + dp(25),
                dp(24),
                bars.bottom + dp(35)
            )

            insets
        }

        val header = TextView(this).apply {
            text = "SETTINGS"
            textSize = 28f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            includeFontPadding = true
        }

        mainLayout.addView(
            header,
            matchWidthWrapHeight()
        )

        addSpace(mainLayout, 8)

        val subtitle = TextView(this).apply {
            text = "Manage your account and preferences."
            textSize = 14f
            setTextColor(secondaryText)
            gravity = Gravity.CENTER
        }

        mainLayout.addView(subtitle, matchWidthWrapHeight())
        addSpace(mainLayout, 28)

        // ACCOUNT
        addSectionTitle(mainLayout, "ACCOUNT")

        val accountCard = createCard()
        accountCard.addView(
            createSettingRow(
                "Personal Information",
                "View your account details",
                "USER"
            ) {
                showPersonalInformation()
            }
        )

        addDivider(accountCard)

        accountCard.addView(
            createSettingRow(
                "Change Password",
                "Password management",
                "LOCK"
            ) {
                showChangePasswordSample()
            }
        )

        mainLayout.addView(accountCard)
        addSpace(mainLayout, 24)

        // PREFERENCES
        addSectionTitle(mainLayout, "PREFERENCES")

        val preferencesCard = createCard()

        preferencesCard.addView(
            createReminderRow()
        )

        addDivider(preferencesCard)

        preferencesCard.addView(
            createSettingRow(
                "App Appearance",
                "Dark theme",
                "THEME"
            ) {
                AlertDialog.Builder(this)
                    .setTitle("App Appearance")
                    .setMessage(
                        "Chico currently uses the dark theme."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        )

        mainLayout.addView(preferencesCard)
        addSpace(mainLayout, 24)

        // SUPPORT
        addSectionTitle(mainLayout, "SUPPORT")

        val supportCard = createCard()

        supportCard.addView(
            createSettingRow(
                "Help & Support",
                "Get help using the Chico app",
                "HELP"
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Help & Support")
                    .setMessage(
                        "Need help with Chico?\n\n" +
                                "For this demonstration, contact the " +
                                "barbershop directly for assistance with " +
                                "appointments, account details or services."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        )

        addDivider(supportCard)

        supportCard.addView(
            createSettingRow(
                "About Chico",
                "App information and version",
                "INFO"
            ) {
                AlertDialog.Builder(this)
                    .setTitle("About Chico")
                    .setMessage(
                        "CHICO BARBERSHOP\n\n" +
                                "Version 1.0\n\n" +
                                "Book appointments and manage your " +
                                "barbershop experience in one place."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        )

        mainLayout.addView(supportCard)
        addSpace(mainLayout, 30)

        // LOG OUT
        val logoutButton = createButton(
            "LOG OUT",
            logoutColor
        )

        logoutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Log Out") { _, _ ->
                    logOut()
                }
                .show()
        }

        mainLayout.addView(logoutButton)
        addSpace(mainLayout, 18)

        val footer = TextView(this).apply {
            text = "CHICO BARBERSHOP · VERSION 1.0"
            textSize = 11f
            setTextColor(secondaryText)
            gravity = Gravity.CENTER
        }

        mainLayout.addView(footer, matchWidthWrapHeight())

        setContentView(scrollView)
        ViewCompat.requestApplyInsets(scrollView)
    }

    private fun showPersonalInformation() {
        val email = userEmail.ifBlank { "Not available" }

        AlertDialog.Builder(this)
            .setTitle("Personal Information")
            .setMessage(
                "Account type: $userRole\n\n" +
                        "Email: $email\n\n" +
                        "To update your full profile details, " +
                        "use the account/profile section of the app."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showChangePasswordSample() {
        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage(
                "Password changes are not implemented on " +
                        "this sample screen yet.\n\n" +
                        "Your existing password has not been changed."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun createReminderRow(): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(14), dp(4), dp(14))
        }

        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(this).apply {
            text = "Appointment Reminders"
            textSize = 15f
            setTextColor(primaryText)
            typeface = Typeface.DEFAULT_BOLD
        }

        val description = TextView(this).apply {
            text = "Sample reminder preference"
            textSize = 12f
            setTextColor(secondaryText)
            setPadding(0, dp(4), 0, 0)
        }

        textColumn.addView(title)
        textColumn.addView(description)

        row.addView(
            textColumn,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        val reminderSwitch = Switch(this).apply {
            isChecked = getSharedPreferences(
                "ChicoPrefs",
                MODE_PRIVATE
            ).getBoolean("appointment_reminders", true)

            buttonTintList = android.content.res.ColorStateList.valueOf(
                accentColor
            )

            setOnCheckedChangeListener {
                    _: CompoundButton, checked: Boolean ->
                getSharedPreferences(
                    "ChicoPrefs",
                    MODE_PRIVATE
                ).edit()
                    .putBoolean("appointment_reminders", checked)
                    .apply()

                Toast.makeText(
                    this@SettingsActivity,
                    if (checked) {
                        "Appointment reminders enabled"
                    } else {
                        "Appointment reminders disabled"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        row.addView(reminderSwitch)
        return row
    }

    private fun createSettingRow(
        titleValue: String,
        descriptionValue: String,
        iconLabel: String,
        onClick: () -> Unit
    ): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setPadding(dp(4), dp(14), dp(4), dp(14))
            setOnClickListener { onClick() }
        }

        val icon = TextView(this).apply {
            text = iconLabel
            textSize = 10f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            background = roundedBackground(cardColor, 10f)
            setPadding(dp(8), dp(10), dp(8), dp(10))
        }

        row.addView(
            icon,
            LinearLayout.LayoutParams(dp(58), dp(42))
        )

        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, dp(6), 0)
        }

        val title = TextView(this).apply {
            text = titleValue
            textSize = 15f
            setTextColor(primaryText)
            typeface = Typeface.DEFAULT_BOLD
        }

        val description = TextView(this).apply {
            text = descriptionValue
            textSize = 12f
            setTextColor(secondaryText)
            setPadding(0, dp(4), 0, 0)
        }

        textColumn.addView(title)
        textColumn.addView(description)

        row.addView(
            textColumn,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        val arrow = TextView(this).apply {
            text = "›"
            textSize = 28f
            setTextColor(secondaryText)
            gravity = Gravity.CENTER
        }

        row.addView(
            arrow,
            LinearLayout.LayoutParams(dp(24), -2)
        )

        return row
    }

    private fun addSectionTitle(
        parent: LinearLayout,
        title: String
    ) {
        val heading = TextView(this).apply {
            text = title
            textSize = 13f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, dp(10))
        }

        parent.addView(heading, matchWidthWrapHeight())
    }

    private fun createCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(6), dp(16), dp(6))
            background = roundedBackground(cardColor, 14f)
            clipChildren = false
            clipToPadding = false
        }
    }

    private fun addDivider(parent: LinearLayout) {
        val divider = View(this).apply {
            setBackgroundColor(Color.rgb(50, 50, 50))
        }

        parent.addView(
            divider,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            )
        )
    }

    private fun createButton(
        label: String,
        color: Int
    ): TextView {
        return TextView(this).apply {
            text = label
            textSize = 15f
            setTextColor(primaryText)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = true
            isClickable = true
            isFocusable = true
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(color, 12f)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(60)
            )
        }
    }

    private fun roundedBackground(
        color: Int,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = dp(radius.toInt()).toFloat()
        }
    }

    private fun matchWidthWrapHeight() =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    private fun addSpace(
        parent: LinearLayout,
        height: Int
    ) {
        parent.addView(
            View(this),
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)
            )
        )
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()

        getSharedPreferences(
            "ChicoPrefs",
            MODE_PRIVATE
        ).edit().clear().apply()

        val loginIntent = Intent(
            this,
            LoginActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(loginIntent)
        finish()
    }
}
