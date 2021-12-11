package com.mobilesecurity.mobileapp.CallBlocker.di

import android.app.Application
import android.content.Context
import com.mobilesecurity.mobileapp.CallBlocker.data.repository.ContactsRepository
import com.mobilesecurity.mobileapp.CallBlocker.data.repository.ContactsRepositoryImpl
import com.mobilesecurity.mobileapp.CallBlocker.data.source.CallLogDataSource
import com.mobilesecurity.mobileapp.CallBlocker.data.source.CallLogDataSourceImpl
import com.mobilesecurity.mobileapp.CallBlocker.data.source.ContactsDataSource
import com.mobilesecurity.mobileapp.CallBlocker.data.source.ContactsDataSourceImpl
import com.mobilesecurity.commons.SystemPermissionUtil
import com.mobilesecurity.datasource.local.LocalDataSource
import com.mobilesecurity.mobileapp.CallBlocker.NotificationProvider
import com.mobilesecurity.mobileapp.CallBlocker.NotificationsProviderImpl
import com.mobilesecurity.mobileapp.CallBlocker.PhoneReceiver
import com.mobilesecurity.mobileapp.CallBlocker.PhoneReceiverImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun providesContactRepository(
        localDataSource: LocalDataSource,
        contactsDataSource: ContactsDataSource,
        callLogDataSource: CallLogDataSource
    ): ContactsRepository =
        ContactsRepositoryImpl(
            localDataSource,
            contactsDataSource,
            callLogDataSource
        )

    @Provides
    @Singleton
    fun providesPhoneReceiver(
        localDataSource: LocalDataSource,
        notificationProvider: NotificationProvider
    ): PhoneReceiver = PhoneReceiverImpl(localDataSource, notificationProvider)

    @Provides
    @Singleton
    fun providesNotificationsProvider(systemPermissionUtil: SystemPermissionUtil): NotificationProvider =
        NotificationsProviderImpl(systemPermissionUtil)

    @Provides
    @Singleton
    fun providesContactProvider(
        @ApplicationContext context: Context,
        permissionUtil: SystemPermissionUtil
    ): ContactsDataSource =
        ContactsDataSourceImpl(
            context,
            permissionUtil
        )

    @Provides
    @Singleton
    fun providesCallLogProvider(
        @ApplicationContext context: Context,
        systemPermissionUtil: SystemPermissionUtil
    ): CallLogDataSource =
        CallLogDataSourceImpl(
            context,
            systemPermissionUtil
        )

}