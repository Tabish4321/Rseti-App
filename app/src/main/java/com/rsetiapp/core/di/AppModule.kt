package com.rsetiapp.core.di

import android.content.Context
import androidx.room.Room
import com.rsetiapp.BuildConfig
import com.rsetiapp.core.data.local.database.AppDatabase
import com.rsetiapp.core.data.remote.AppLevelApi
import com.rsetiapp.core.util.ApiConstant
import com.rsetiapp.core.util.AppConstant
import com.rsetiapp.core.util.CustomInterceptor
import com.rsetiapp.core.util.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PreLoginOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PostLoginOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PreLoginAppLevelApi

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PostLoginAppLevelApi

    @Provides
    @Singleton
    fun providesRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "Kaushal_panjee_db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    @PreLoginOkHttpClient
    fun providesRetrofitForPreLogin(
        userPreferences: UserPreferences,
        @ApplicationContext context: Context
    ): Retrofit {
        return Retrofit.Builder()
          //  .baseUrl(AppConstant.StaticURL.liveUrl) // Ensure this is correct
            .baseUrl(AppConstant.StaticURL.baseUrl) // Ensure this is correct
            .client(
                getRetrofitClient(
                    null, userPreferences = userPreferences,
                    isPostLogin = false, context = context
                )
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())   // ✅ Ensure Gson is added
            .addConverterFactory(MoshiConverterFactory.create())  // Keeping Moshi for compatibility
            .build()
    }

    @Provides
    @Singleton
    @PostLoginOkHttpClient
    fun providesRetrofitForPostLogin(
        userPreferences: UserPreferences,
        @ApplicationContext context: Context
    ): Retrofit {
        return Retrofit.Builder()
           // .baseUrl(AppConstant.StaticURL.liveUrl)
            .baseUrl(AppConstant.StaticURL.baseUrl)
            .client(
                getRetrofitClient(null, userPreferences = userPreferences, context = context)
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getRetrofitClient(
        authenticator: Authenticator? = null,
        userPreferences: UserPreferences,
        isPostLogin: Boolean = true,
        isAuthenticationRequired: Boolean = true,
        context: Context
    ): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong() // 5MB cache
        val myCache = Cache(context.cacheDir, cacheSize)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logs request & response body
        }

        return OkHttpClient.Builder()
            .cache(myCache)
            .connectTimeout(ApiConstant.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstant.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstant.READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(
                CustomInterceptor(isPostLogin, userPreferences, isAuthenticationRequired, context)
            )
            .apply {
                authenticator?.let { this.authenticator(it) }

                if (BuildConfig.DEBUG){
                    addInterceptor(logging)
                }
            }
            .build()
    }

    @Provides
    @Singleton
    @PreLoginAppLevelApi
    fun providePreLoginAppLevelApi(@PreLoginOkHttpClient retrofit: Retrofit): AppLevelApi =
        retrofit.create(AppLevelApi::class.java)

    @Provides
    @Singleton
    @PostLoginAppLevelApi
    fun providePostLoginAppLevelApi(@PostLoginOkHttpClient retrofit: Retrofit): AppLevelApi =
        retrofit.create(AppLevelApi::class.java)
}
