package com.example.market.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.market.model.cart.CartItem

@Database(entities = [CartItem::class], version = 1)
abstract class MarketDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}