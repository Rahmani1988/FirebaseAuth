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
        const val TAG = "MainActivity"
    }

    private fun handleEmailLinkSignIn() {
        val auth = Firebase.auth
        val emailLink = intent.data?.toString() ?: return

        if (auth.isSignInWithEmailLink(emailLink)) {
            val sharedPref = getSharedPreferences(AUTH_PREFS, MODE_PRIVATE)
            sharedPref.getString(EMAIL_LINK_KEY, null)?.let {
                auth.signInWithEmailLink(it, emailLink)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
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