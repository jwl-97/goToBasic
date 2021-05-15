package com.ljw.calculatorapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ljw.calculatorapp.dao.HistoryDao
import com.ljw.calculatorapp.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}