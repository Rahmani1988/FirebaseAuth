package com.reza.firebaseauthsample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.reza.firebaseauthsample.ui.EmailLinkAuthScreen
import com.reza.firebaseauthsample.ui.HomeScreen
import com.reza.firebaseauthsample.ui.navigation.Screen
import com.reza.firebaseauthsample.ui.theme.FirebaseAuthSamplesTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val AUTH_PREFS = "auth_prefs"
        const val EMAIL_LINK_KEY = "email_link_key"
        const val TAG = "MainActivity"
    }

    private fun handleEmailLinkSignIn() {
        val auth = Firebase.auth
        val emailLink = intent.data?.toString() ?: return

        if (auth.isSignInWithEmailLink(emailLink)) {
            val sharedPref = getSharedPreferences(AUTH_PREFS, MODE_PRIVATE)
            val email = sharedPref.getString(EMAIL_LINK_KEY, null) ?: return

            val credential = EmailAuthProvider.getCredentialWithLink(email, emailLink)
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // SCENARIO A: User is already logged in (e.g., via Anonymous or Google)
                // We LINK this email to their current account.
                currentUser.linkWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Successfully linked emailLink credential!")
                        } else {
                            Log.e(TAG, "Error linking credential", task.exception)

                            // SCENARIO B: If linking fails because of a "recent login" requirement,
                            // you use Re-authentication.
                            currentUser.reauthenticateAndRetrieveData(credential)
                                .addOnCompleteListener { reAuthTask ->
                                    if (reAuthTask.isSuccessful) {
                                        Log.d(TAG, "User successfully re-authenticated")
                                    }
                                }
                        }
                    }
            } else {
                // SCENARIO C: No user is logged in. Perform a standard sign-in.
                auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully signed in with email link!", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Successfully signed in with email link!")
                            val result = task.result
                            // You can access the new user via result.getUser()
                        } else {
                            Log.e(TAG, "Error signing in with email link", task.exception)
                        }
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleEmailLinkSignIn()

        enableEdgeToEdge()
        setContent {
            FirebaseAuthSamplesTheme {
                val backStack = rememberNavBackStack(Screen.Home)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                        },
                        entryProvider = entryProvider {
                            entry<Screen.Home> {
                                HomeScreen(
                                    onNavigateToEmailLinkAuth = { backStack.add(Screen.EmailLinkAuth) },
                                    onNavigateToGoogleAuth = { backStack.add(Screen.GoogleAuth) }
                                )
                            }
                            entry<Screen.EmailLinkAuth> {
                                EmailLinkAuthScreen(onBack = { backStack.removeAt(backStack.lastIndex) })
                            }
                            entry<Screen.GoogleAuth> {
                                // todo implement
                                Text("Google Auth Screen")
                            }
                        }
                    )
                }
            }
        }
    }
}