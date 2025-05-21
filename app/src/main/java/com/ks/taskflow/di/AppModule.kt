//package com.ks.taskflow.di
//
//import android.content.Context
//import androidx.work.WorkManager
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
///**
// * Application module for providing dependencies.
// */
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    /**
//     * Provides an instance of WorkManager.
//     */
//    @Singleton
//    @Provides
//    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
//        return WorkManager.getInstance(context)
//    }
//
//    /**
//     * Provides the application context.
//     */
//    @Provides
//    fun provideContext(@ApplicationContext context: Context): Context {
//        return context
//    }
//}