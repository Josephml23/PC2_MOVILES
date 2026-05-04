package com.tecsup.stockmin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.stockmin.data.local.dao.ItemDao
import com.tecsup.stockmin.data.local.dao.MovimientoDao
import com.tecsup.stockmin.data.local.entity.ItemEntity
import com.tecsup.stockmin.data.local.entity.MovimientoEntity

@Database(
    entities = [ItemEntity::class, MovimientoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StockMinDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var INSTANCE: StockMinDatabase? = null

        fun getInstance(context: Context): StockMinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    StockMinDatabase::class.java,
                    "stockmin.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}
