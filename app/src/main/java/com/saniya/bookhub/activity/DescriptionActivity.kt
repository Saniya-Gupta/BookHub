package com.saniya.bookhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.saniya.bookhub.R
import com.saniya.bookhub.database.BookDatabase
import com.saniya.bookhub.database.BookEntity
import com.saniya.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookDesc: TextView

    lateinit var imgBookImage: ImageView

    lateinit var btnAddToFav: Button

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var toolbar: Toolbar

    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"
        // Fetch the bookId
        if (intent != null)
            bookId = intent.getStringExtra("book_id")
        else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occured!!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occured!!!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"

        // This time we need to send a jsonObject with id as parameter
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {

            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObj = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE

                            val bookImgUrl = bookJsonObj.getString("image")

                            Picasso.get().load(bookJsonObj.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)

                            txtBookName.text = bookJsonObj.getString("name")
                            txtBookAuthor.text = bookJsonObj.getString("author")
                            txtBookPrice.text = bookJsonObj.getString("price")
                            txtBookRating.text = bookJsonObj.getString("rating")
                            txtBookDesc.text = bookJsonObj.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImgUrl
                            )

                            val checkFav = DbAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                btnAddToFav.text = "Remove from Favourites"
                                val favColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorFav)
                                btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                btnAddToFav.text = "Add to Favourites"
                                val noFavColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }

                            btnAddToFav.setOnClickListener {

                                if (!DbAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {

                                    val async =
                                        DbAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val res = async.get()
                                    if (res) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Added to Fav",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFav.text = "Remove from Favourites"
                                        btnAddToFav.setBackgroundColor(
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.colorFav
                                            )
                                        )

                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book could not be Added to Fav",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {

                                    val async = DbAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val res = async.get()

                                    if(res) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Removed from Fav",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFav.text = "Add to Favourites"
                                        btnAddToFav.setBackgroundColor(
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.colorPrimary
                                            )
                                        )
                                    }
                                    else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book could not be removed from Fav",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some Unexpected Error Occurred!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(
                        this@DescriptionActivity,
                        "Volley Error Occured!!!",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "526f51e288db30"
                    return headers
                }
            }

            queue.add(jsonRequest)
        } else {
            // Internet not available
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open settings") { text, listener ->
                // Open settings in phone to open internet using implicit intent
                // This provides the path to intent that it needs to navigate to when the user clicks on open Settings
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit App") { text, listener ->
                // Close the App in case of no internet connection
                // Used to close all instances(activities) of the app at once and the app closes completely
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    class DbAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()

        /*
        * Mode 1 -> Check DB if book is fav or not
        * Mode 2 -> Save the book as fav
        * Mode 3 -> Remove the book from fav*/

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }
                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}
