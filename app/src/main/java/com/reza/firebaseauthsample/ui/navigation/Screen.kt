package com.reza.firebaseauthsample.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Home : Screen()
    @Serializable
    data object EmailLinkAuth : Screen()
    @Serializable
    data object GoogleAuth : Screen()
}