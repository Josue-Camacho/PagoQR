package org.example.pagoqr.di

import org.example.pagoqr.payment.data.datasource.PaymentRemoteDataSource
import org.example.pagoqr.payment.data.repository.PaymentRepositoryImpl
import org.example.pagoqr.payment.domain.repository.PaymentRepository
import org.example.pagoqr.payment.domain.usecase.ProcessPaymentUseCase
import org.example.pagoqr.payment.presentation.viewmodel.PaymentViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.example.pagoqr.history.data.repository.HistoryRepositoryImpl
import org.example.pagoqr.history.domain.repository.HistoryRepository
import org.example.pagoqr.history.domain.usecase.ClearHistoryUseCase
import org.example.pagoqr.history.domain.usecase.ObserveHistoryUseCase
import org.example.pagoqr.history.domain.usecase.SaveHistoryUseCase
import org.example.pagoqr.history.presentation.viewmodel.HistoryViewModel

val appModule = module {

    singleOf(::PaymentRemoteDataSource)

    single<PaymentRepository> {
        PaymentRepositoryImpl(get())
    }

    singleOf(::ProcessPaymentUseCase)

    viewModelOf(::PaymentViewModel)

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    singleOf(::ObserveHistoryUseCase)
    singleOf(::SaveHistoryUseCase)
    singleOf(::ClearHistoryUseCase)

    viewModelOf(::HistoryViewModel)
}