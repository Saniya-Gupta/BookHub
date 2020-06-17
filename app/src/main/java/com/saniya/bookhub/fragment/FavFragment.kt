package com.saniya.bookhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.saniya.bookhub.R
import com.saniya.bookhub.adapter.FavouritesRecyclerAdapter
import com.saniya.bookhub.database.BookDatabase
import com.saniya.bookhub.database.BookEntity

class FavFragment : Fragment() {

    lateinit var recyclerFav : RecyclerView
    //lateinit var progressLayout : RelativeLayout
    //lateinit var progressBar : ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FavouritesRecyclerAdapter
    var dbBookList = listOf<BookEntity>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fav, container, false)

        recyclerFav = view.findViewById(R.id.recyclerFav)
        //progressLayout = view.findViewById(R.id.progressLayout)
        //progressBar = view.findViewById(R.id.progressBar)

        //progressLayout.visibility = View.VISIBLE
        layoutManager = GridLayoutManager(activity as Context,2)

        dbBookList = RetrieveFav(activity as Context).execute().get()

        if(activity != null) {
            //progressBar.visibility = View.GONE
            recyclerAdapter = FavouritesRecyclerAdapter(activity as Context, dbBookList)
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFav(val context: Context): AsyncTask<Void,Void,List<BookEntity>>() {
        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
            return db.bookDao().getAllBooks()
        }
    }
}
