package org.example.pagoqr

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.pagoqr.di.appModule
import org.example.pagoqr.navigation.AppNavHost
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.example.pagoqr.di.platformModule

@Composable
fun App() {

    KoinApplication(
        configuration = koinConfiguration {
            modules(
                appModule,
                platformModule
            )
        }
    ) {
        MaterialTheme {
            AppNavHost()
        }
    }
}