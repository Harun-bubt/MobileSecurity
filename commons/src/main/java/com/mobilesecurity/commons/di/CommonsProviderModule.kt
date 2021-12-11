package com.mobilesecurity.commons.di

import com.mobilesecurity.commons.SystemPermissionUtil
import com.mobilesecurity.commons.SystemPermissionUtilImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class CommonsProviderModule {

    @Provides
    @Singleton
    fun providesSystemPermissionUtil(): SystemPermissionUtil = SystemPermissionUtilImpl()
}