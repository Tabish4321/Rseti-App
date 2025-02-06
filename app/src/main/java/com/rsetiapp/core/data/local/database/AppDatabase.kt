package com.rsetiapp.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rsetiapp.core.data.local.dao.UserDao
import com.rsetiapp.core.data.local.entity.UserEntity

@Database(entities = [UserEntity::class],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getUserDao() : UserDao
}