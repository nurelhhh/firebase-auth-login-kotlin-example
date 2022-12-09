package com.nurelhhh.firebaseauthloginkotlinexample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var signupButton: Button
    private lateinit var loginButton: Button
    private lateinit var loginOrSignUpLayout: ConstraintLayout
    private lateinit var userInfoLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Get views
        signupButton = findViewById(R.id.signupButton)
        loginButton = findViewById(R.id.loginButton)
        loginOrSignUpLayout = findViewById(R.id.loginOrSignUpLayout)
        userInfoLayout = findViewById(R.id.userInfoLayout)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // At first time, show loginLayout and do not show userInfoLayout
        showLoginLayout(true)

        // Set on clicks
        signupButton.setOnClickListener {
            handleSignUpButtonOnClick()
        }

        loginButton.setOnClickListener {
            handleLoginButtonOnClick()
        }

        logoutButton.setOnClickListener {
            handleLogoutButtonOnClick()
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun showLoginLayout(show: Boolean) {
        if (show) {
            loginOrSignUpLayout.visibility = View.VISIBLE
            userInfoLayout.visibility = View.GONE
        } else {
            loginOrSignUpLayout.visibility = View.GONE
            userInfoLayout.visibility = View.VISIBLE
        }
    }

    private fun handleLoginButtonOnClick() {
        setBusy(true)

        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val email: String = emailEditText.text.toString()

        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val password: String = passwordEditText.text.toString()

        signInUser(email, password)
    }

    private fun handleSignUpButtonOnClick() {
        setBusy(true)

        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val email: String = emailEditText.text.toString()

        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val password: String = passwordEditText.text.toString()

        signUpNewUser(email, password)
    }

    private fun handleLogoutButtonOnClick() {
        Firebase.auth.signOut()
        showLoginLayout(true)
    }

    private fun signUpNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setNewlyRegisteredUserRole()
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        baseContext, "createUserWithEmail:success",
                        Toast.LENGTH_SHORT
                    ).show()

                    setBusy(false)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "createUserWithEmail:failure",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setNewlyRegisteredUserRole() {
        val roleRadioGroup = findViewById<RadioGroup>(R.id.roleRadioGroup)
        val adminRadioButton = findViewById<RadioButton>(R.id.adminRadioButton).id

        var role = ""

        when (roleRadioGroup.checkedRadioButtonId) {
            adminRadioButton -> {
                role = "admin"
            }
            else -> {
                role = "user"
            }
        }


        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse(role)
        }

        user!!.updateProfile(profileUpdates)
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        baseContext, "signInWithEmail:success",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = auth.currentUser
                    showLoginLayout(false)
                    showUserInfoToUserInfoLayout(user)
                    setBusy(false)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "signInWithEmail:failure",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showUserInfoToUserInfoLayout(user: FirebaseUser?) {
        val userInfoTextView: TextView = findViewById(R.id.userInfoTextView)

        if (user == null) {
            userInfoTextView.text = "USER IS NULL"
            return
        }

        userInfoTextView.text = "UID: " + user.uid + "\n\n" +
                "Email: " + user.email + "\n\n" +
                "Role: " + user.photoUrl.toString()
    }

    private fun setBusy(busy: Boolean) {
        if (busy) {
            signupButton.isEnabled = false
            loginButton.isEnabled = false
        } else {
            signupButton.isEnabled = true
            loginButton.isEnabled = true
        }
    }

    private fun reload() {

    }
}