package org.example.pagoqr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.pagoqr.auth.presentation.screen.LoginScreen
import org.example.pagoqr.history.presentation.screen.HistoryScreen
import org.example.pagoqr.home.presentation.screen.HomeScreen
import org.example.pagoqr.payment.presentation.screen.PaymentScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.LOGIN
    ) {

        composable(NavRoute.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(NavRoute.HOME) {
                        popUpTo(NavRoute.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(NavRoute.HOME) {
            HomeScreen(
                onPaymentClick = {
                    navController.navigate(NavRoute.PAYMENT)
                },
                onHistoryClick = {
                    navController.navigate(NavRoute.HISTORY)
                }
            )
        }

        composable(NavRoute.PAYMENT) {
            PaymentScreen()
        }

        composable(NavRoute.HISTORY) {
            HistoryScreen()
        }
    }
}