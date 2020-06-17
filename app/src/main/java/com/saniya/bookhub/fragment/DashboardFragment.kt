package com.saniya.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.saniya.bookhub.R
import com.saniya.bookhub.adapter.DashboardRecyclerAdapter
import com.saniya.bookhub.model.Book
import com.saniya.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

/*
    val bookList = arrayListOf("The Great Gatsby","War and Peace","Madame Bovary","Anna Karenina","Merchant of Venice","Much Ado About Nothing","Macbeth","The Subconscious Mind","The Lord of Rings")
*/

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

/*
    val bookInfoList = arrayListOf<Book>(
        Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
        Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
        Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
        Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
        Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
        Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
        Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
        Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
        Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
        Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
    )
*/

    lateinit var btnCheckInternet: Button

    val bookInfoList = arrayListOf<Book>()

    var ratingComparator = Comparator<Book> { book1, book2 ->

        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            // Sort the books alphabetically
            book1.bookName.compareTo(book2.bookName, true)
        } else
            book1.bookRating.compareTo(book2.bookRating, true)
        // Else sort them according to ratings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // To inform the compiler that this toolbar has a menu item: sort. Only required in fragment. In activity, automatically added.
        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)

        btnCheckInternet = view.findViewById(R.id.btnCheckInternet)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        btnCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                // Internet available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("OK") { text, listener ->
                    // Do nothing
                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                    // Do nothing
                }
                dialog.create()
                dialog.show()
            } else {
                // Internet not available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("OK") { text, listener ->
                    // Do nothing
                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                    // Do nothing
                }
                dialog.create()
                dialog.show()
            }
        }

        /* Making requests */

        // queue used to store the queue of requests
        val queue = Volley.newRequestQueue(activity as Context)

        // define the URL which will give us the response
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        // Do not send request if there is no internet connection
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectResponse = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    // Handle Response

                    // TO handle JSON exception
                    try {
                        progressLayout.visibility = View.GONE
                        // Check Response in Logcat window
                        println("Response is $it")

                        // Extract the value of success key to check if the response is successful
                        val success = it.getBoolean("success")

                        if (success) {
                            //Extract Json Array
                            val data = it.getJSONArray("data")
                            // Iterate through JSON array and retrieve its data

                            for (i in 0 until data.length()) {
                                // Get jsonObjects
                                val bookJsonObject = data.getJSONObject(i)
                                // Parse json object into book object
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )

                                // Add objects to arrayList
                                bookInfoList.add(bookObject)
                            }

                            // Send data to adapter
                            recyclerAdapter =
                                DashboardRecyclerAdapter(activity as Context, bookInfoList)

                            recyclerDashboard.adapter = recyclerAdapter

                            recyclerDashboard.layoutManager = layoutManager

                            // Add divider lines for items
                            /*recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,(layoutManager as LinearLayoutManager).orientation
                                )
                            )*/
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occurred!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },

                Response.ErrorListener {
                    // Handle Error [Volley Error]
                    println("Error is $it")
                    Toast.makeText(
                        activity as Context,
                        "Volley Error Occured!!!",
                        Toast.LENGTH_LONG
                    ).show()
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    // HashMap derived from MutableMap hence can be used since essentially same
                    val headers = HashMap<String, String>()
                    // To imply that the data we send and receive would be in the form of json
                    headers["Content-type"] = "application/json"
                    // Unique token
                    headers["token"] = "526f51e288db30"
                    return headers
                }
            }

            // Add the request we have created to the request queue
            queue.add(jsonObjectResponse)
        } else {
            // Internet not available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open settings") { text, listener ->
                // Open settings in phone to open internet using implicit intent
                // This provides the path to intent that it needs to navigate to when the user clicks on open Settings
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit App") { text, listener ->
                // Close the App in case of no internet connection
                // Used to close all instances(activities) of the app at once and the app closes completely
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}
