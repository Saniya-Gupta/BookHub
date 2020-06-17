package com.saniya.bookhub.model

// Add all keys of response in the same order
data class Book (
    val bookId : String,
    val bookName : String,
    val bookAuthor : String,
    val bookRating : String,
    val bookPrice : String,
    val bookImage : String
)