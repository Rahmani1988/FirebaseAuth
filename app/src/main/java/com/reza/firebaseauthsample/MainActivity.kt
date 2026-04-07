package com.reza.firebaseauthsample

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.auth
import com.reza.firebaseauthsample.ui.EmailLinkAuthScreen
import com.reza.firebaseauthsample.ui.HomeScreen
import com.reza.firebaseauthsample.ui.navigation.Screen
import com.reza.firebaseauthsample.ui.theme.FirebaseAuthSamplesTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val AUTH_PREFS = "auth_prefs"
        const val EMAIL_LINK_KEY = "email_link_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Check if the intent contains a Firebase Auth link
        val intentData = intent.data?.toString()
        if (intentData != null && Firebase.auth.isSignInWithEmailLink(intentData)) {

            // 2. Retrieve the email we saved earlier
            val sharedPref = getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
            val email = sharedPref.getString(EMAIL_LINK_KEY, null)

            // 3. Security Fallback: If the user opened the link on a DIFFERENT device,
            // the SharedPreferences will be empty. We must ask them for their email again.
            if (email == null) {
                // Logic to show a dialog asking: "Please confirm your email to finish signing in"
                // For now, let's assume it's the same device
                return
            }

            // 4. Complete the sign-in
            Firebase.auth.signInWithEmailLink(email, intentData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AUTH", "Successfully signed in!")
                        val user = task.result?.user
                        // Navigate to Home or Update State
                    } else {
                        Log.e("AUTH", "Error signing in", task.exception)
                    }
                }
        }

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