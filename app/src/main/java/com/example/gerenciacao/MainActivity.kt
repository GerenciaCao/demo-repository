package com.example.gerenciacao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.gerenciacao.navegacao.GerenciaCaoApp
import com.example.gerenciacao.ui.GerenciaCaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash do sistema (fundo laranja + logo). Precisa vir antes do super.onCreate.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GerenciaCaoTheme {
                GerenciaCaoApp()
            }
        }
    }
}
