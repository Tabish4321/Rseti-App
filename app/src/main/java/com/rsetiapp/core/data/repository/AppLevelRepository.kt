package com.rsetiapp.core.data.repository

import com.rsetiapp.core.data.local.database.AppDatabase
import com.rsetiapp.core.data.remote.AppLevelApi
import com.rsetiapp.core.di.AppModule
import javax.inject.Inject

class AppLevelRepository @Inject constructor(
    @AppModule.PreLoginAppLevelApi private val appLevelApiPreLogin: AppLevelApi,
    @AppModule.PostLoginAppLevelApi private val appLevelApiPostLogin: AppLevelApi,
    appDatabase: AppDatabase
) {



}