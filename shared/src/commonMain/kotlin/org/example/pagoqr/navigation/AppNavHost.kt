package org.example.pagoqr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.pagoqr.auth.presentation.screen.LoginScreen
import org.example.pagoqr.history.presentation.screen.HistoryScreen
import org.example.pagoqr.home.presentation.screen.HomeScreen
import org.example.pagoqr.payment.presentation.screen.PaymentScreen
import org.example.pagoqr.qr.presentation.screen.GenerateQrScreen
import org.example.pagoqr.qr.presentation.screen.ScanQrScreen

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
                    navController.navigate(
                        "payment?amount=&description="
                    )
                },
                onHistoryClick = {
                    navController.navigate(NavRoute.HISTORY)
                },
                onGenerateQrClick = {
                    navController.navigate(NavRoute.GENERATE_QR)
                },
                onScanQrClick = {
                    navController.navigate(NavRoute.SCAN_QR)
                }
            )
        }

        composable(
            route = NavRoute.PAYMENT_QR
        ) { backStackEntry ->

            val amount = backStackEntry.arguments
                ?.getString("amount")

            val description = backStackEntry.arguments
                ?.getString("description")

            PaymentScreen(
                initialAmount = amount,
                initialDescription = description
            )
        }

        composable(NavRoute.HISTORY) {
            HistoryScreen()
        }

        composable(NavRoute.GENERATE_QR) {
            GenerateQrScreen()
        }

        composable(NavRoute.SCAN_QR) {
            ScanQrScreen(
                onPayClick = { data ->

                    navController.navigate(
                        "payment?amount=${data.amount}&description=${data.description}"
                    )
                }
            )
        }
    }
}