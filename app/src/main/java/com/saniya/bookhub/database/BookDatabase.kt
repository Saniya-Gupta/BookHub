package com.saniya.bookhub.database

import androidx.room.Database
import androidx.room.RoomDatabase

// It is important to add version(N) since when we update our app to different versions we may need to update DB
@Database(entities = [BookEntity::class], version = 1)
abstract class BookDatabase : RoomDatabase() {

    // Tell the compiler that all our DB operations will be performed by DAO and hence this fun allows us to use all fun of DAO
    // Hence this fun serves as a doorway for all Dao operations
    abstract fun bookDao() : BookDao
}