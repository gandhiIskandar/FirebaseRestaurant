package com.example.ayamjumpa.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object datamodul {

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context)=DataStoreRepository(context)

}