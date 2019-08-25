package com.kieferd.smartertv

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main2.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var callIntent: Intent
    private lateinit var ipIntent: String
    private lateinit var portIntent: String

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_tv -> callTV()
            R.id.nav_radio -> callRadio()
            R.id.nav_settings -> callSettings()
        }

        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        callIntent = this.intent
        portIntent = "${callIntent.extras["port"]}"
        ipIntent = "${callIntent.extras["id"]}"

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        System.out.println("Package: $packageName")

        if (Build.VERSION.SDK_INT >= 27) {
            this.setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        callTV()

        val navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        toast("Exit with home button", Toast.LENGTH_LONG)
    }

    private fun toast(text: String, dur: Int) {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else {
            Toast.makeText(this, text, dur).show()
        }
    }

    private fun callTV(){
        val fragment = TVFragment()
        val args = Bundle()
        args.putString("ip", ipIntent)
        args.putInt("port", portIntent.toInt())
        fragment.arguments = args

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun callRadio(){
        val fragment = RadioFragment()
        val args = Bundle()
        args.putString("ip", ipIntent)
        args.putInt("port", portIntent.toInt())
        fragment.arguments = args

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    private fun callSettings(){
        val fragment = SettingsFragment()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}
