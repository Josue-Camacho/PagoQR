package org.example.pagoqr.di

import android.content.Context
import org.example.pagoqr.config.AppDatabase
import org.example.pagoqr.config.getDatabaseBuilder
import org.example.pagoqr.config.getRoomDatabase
import org.koin.dsl.module
import org.example.pagoqr.payment.data.repository.StripePaymentGatewayImpl
import org.example.pagoqr.payment.domain.repository.StripePaymentGateway

actual val platformModule = module {

    single {
        getRoomDatabase(
            getDatabaseBuilder(
                get<Context>()
            )
        )
    }

    single {
        get<AppDatabase>().transactionDao()
    }

    single<StripePaymentGateway> {
        StripePaymentGatewayImpl()
    }
}