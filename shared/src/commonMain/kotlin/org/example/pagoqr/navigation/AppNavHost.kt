package org.example.pagoqr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.pagoqr.auth.presentation.screen.LoginScreen
import org.example.pagoqr.demo.presentation.screen.DemoScenariosScreen
import org.example.pagoqr.history.presentation.screen.HistoryScreen
import org.example.pagoqr.home.presentation.screen.HomeScreen
import org.example.pagoqr.payment.presentation.screen.PaymentScreen
import org.example.pagoqr.qr.presentation.screen.GenerateQrScreen
import org.example.pagoqr.qr.presentation.screen.ScanQrScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val onViewHistory: () -> Unit = {
        navController.navigate(NavRoute.HISTORY) { launchSingleTop = true }
    }
    val onNewPayment: (paymentRoute: String) -> Unit = { paymentRoute ->
        navController.navigate(NavRoute.PAYMENT) {
            popUpTo(paymentRoute) { inclusive = true }
        }
    }

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
                onDemoScenariosClick = {
                    navController.navigate(NavRoute.DEMO_SCENARIOS)
                },
                onPaymentClick = {
                    navController.navigate(NavRoute.PAYMENT)
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

        composable(NavRoute.DEMO_SCENARIOS) {
            DemoScenariosScreen(
                onBackClick = { navController.popBackStack() },
                onPaymentClick = { amount, description ->
                    navController.navigate(NavRoute.paymentWithDetails(amount, description))
                },
                onRefundClick = { navController.navigate(NavRoute.HISTORY) }
            )
        }

        composable(NavRoute.PAYMENT) {
            PaymentScreen(
                onHistoryClick = onViewHistory,
                onNewPaymentClick = { onNewPayment(NavRoute.PAYMENT) }
            )
        }

        composable(
            route = NavRoute.PAYMENT_QR
        ) { backStackEntry ->

            val amount = backStackEntry.arguments
                ?.getString("amount")

            val description = backStackEntry.arguments
                ?.getString("description")
                ?.let { decodeRouteText(it) }

            PaymentScreen(
                onHistoryClick = onViewHistory,
                onNewPaymentClick = { onNewPayment(NavRoute.PAYMENT_QR) },
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
                        NavRoute.paymentWithDetails(data.amount.toString(), data.description)
                    )
                }
            )
        }
    }
}
