package com.mobilesecurity.datasource.di

import android.content.Context
import com.mobilesecurity.datasource.local.LocalDataSource
import com.mobilesecurity.datasource.local.LocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DataSourceProviderModule {

    @Singleton
    @Provides
    fun providesLocalDataSource(applicationContext: Context): LocalDataSource =
        LocalDataSourceImpl(applicationContext)
}