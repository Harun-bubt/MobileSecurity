package com.mobilesecurity.mobileapp.CallBlocker.di

import com.mobilesecurity.mobileapp.CallBlocker.data.repository.ContactsRepository
import com.mobilesecurity.mobileapp.CallBlocker.domain.ContactsInteractor
import com.mobilesecurity.mobileapp.CallBlocker.domain.ContactsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DomainModule {

    @Provides
    @Singleton
    fun providesContactsUseCase(contactsRepository: ContactsRepository): ContactsUseCase =
        ContactsInteractor(contactsRepository)
}