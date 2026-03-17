package com.reza.firebaseauthsample.ui.navigation

sealed class Screen {
    object Home : Screen()
    object EmailLinkAuth : Screen()
    object GoogleAuth : Screen()
}