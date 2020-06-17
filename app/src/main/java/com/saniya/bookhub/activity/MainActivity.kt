package com.saniya.bookhub.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.saniya.bookhub.*
import com.saniya.bookhub.fragment.AboutAppFragment
import com.saniya.bookhub.fragment.DashboardFragment
import com.saniya.bookhub.fragment.FavFragment
import com.saniya.bookhub.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout : CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)

        var previousMenuItem : MenuItem? = null

        setUpToolbar()
        openDashboard()

        // Adding functionality to the toggle button
        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        // Enable drawer layout to listen to the action on the toggle
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        /* Used to change the hamburger icon to back arrow icon and vice-versa
         Synchronise the state of the toggle with the state of the navigation drawer.*/
        actionBarDrawerToggle.syncState()

        // Adding functionality to different navigation bar menu items
        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null)
                previousMenuItem?.isChecked = false

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId) {
                R.id.dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }

                R.id.fav -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            FavFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            ProfileFragment()
                        )
                        .commit()

                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frameLayout,
                            AboutAppFragment()
                        )
                        .commit()

                    supportActionBar?.title = "About App"
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    // Setting up the toolbar to behave like an action bar
    private fun setUpToolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"

        // We need to position hamburger icon at its place and it is prexisting button, hence we just enable it
        supportActionBar?.setHomeButtonEnabled(true)

        // To display the button. By default adds the back button. Now we just need to change its icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if(id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }

    private fun openDashboard() {
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        navigationView.setCheckedItem(R.id.dashboard)
        supportActionBar?.title = "Dashboard"
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when(fragment) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
        // We remove it, to get rid of the default behaviour of the activity
        //super.onBackPressed()
    }
}
