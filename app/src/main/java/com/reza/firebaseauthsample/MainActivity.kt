package com.reza.firebaseauthsample

import android.os.Bundle
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
import com.reza.firebaseauthsample.ui.EmailLinkAuthScreen
import com.reza.firebaseauthsample.ui.HomeScreen
import com.reza.firebaseauthsample.ui.navigation.Screen
import com.reza.firebaseauthsample.ui.theme.FirebaseAuthSamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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