package com.saniya.bookhub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {

    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("Select * from books")
    fun getAllBooks() : List<BookEntity>

    // : is used to tell the compiler that we will get the value of bookId from the fun mentioned below
    @Query("Select * from books where book_id = :bookId")
    fun getBookById(bookId : String) : BookEntity

}