package org.example.pagoqr.di

import org.example.pagoqr.payment.data.datasource.PaymentRemoteDataSource
import org.example.pagoqr.payment.data.repository.PaymentRepositoryImpl
import org.example.pagoqr.payment.domain.repository.PaymentRepository
import org.example.pagoqr.payment.domain.usecase.ProcessPaymentUseCase
import org.example.pagoqr.payment.presentation.viewmodel.PaymentViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    singleOf(::PaymentRemoteDataSource)

    single<PaymentRepository> {
        PaymentRepositoryImpl(get())
    }

    singleOf(::ProcessPaymentUseCase)

    viewModelOf(::PaymentViewModel)
}