package com.example.market.di

import android.content.Context
import androidx.room.Room
import com.example.market.data.local.CartDao
import com.example.market.data.local.MarketDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarketDatabase {
        return Room.databaseBuilder(
            context,
            MarketDatabase::class.java,
            "market_db"
        ).build()
    }

    @Provides
    fun provideCartDao(database: MarketDatabase): CartDao {
        return database.cartDao()
    }
}