package com.saniya.bookhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// BookEntity used to store data in form of tables, hence this will be a data class

// This here is called annotation, simply used to tell the compiler what we are creating.
// Hence here tells the compiler that this is an entity class.
@Entity(tableName = "books")

// Follow naming conventions for column names in a DB using annotations
    data class BookEntity(
    @PrimaryKey val book_id : Int,
    @ColumnInfo(name = "book_name") val bookName : String,
    @ColumnInfo(name = "book_author") val bookAuthor : String,
    @ColumnInfo(name = "book_price") val bookPrice : String,
    @ColumnInfo(name = "book_rating") val bookRating : String,
    @ColumnInfo(name = "book_desc") val bookDesc : String,
    @ColumnInfo(name = "book_image") val bookImg : String
)
