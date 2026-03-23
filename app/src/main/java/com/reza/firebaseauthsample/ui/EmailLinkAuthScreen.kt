package com.reza.firebaseauthsample.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth

@Composable
fun EmailLinkAuthScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign in with Email Link", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                val actionCodeSettings = actionCodeSettings {
                    // URL you want to redirect back to. The domain (www.example.com) for this
                    // URL must be whitelisted in the Firebase Console.
                    url = "https://www.example.com/finishSignUp?cartId=1234"
                    // This must be true
                    handleCodeInApp = true
                    setAndroidPackageName(
                        "com.reza.firebaseauthsample",
                        true, // installIfNotAvailable
                        "24", // minimumVersion
                    )
                }
                Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Log.d("TAG", "Email sent.")
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotEmpty() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Send Login Link")
            }
        }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}