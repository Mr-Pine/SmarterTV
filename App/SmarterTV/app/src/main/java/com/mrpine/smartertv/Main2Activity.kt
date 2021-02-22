package com.mrpine.smartertv

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
import com.mrpine.smartertv.databinding.ActivityMain2Binding

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var callIntent: Intent
    private lateinit var ipIntent: String
    private lateinit var portIntent: String
    private lateinit var binding: ActivityMain2Binding

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_tv -> callTV()
            R.id.nav_radio -> callRadio()
            R.id.nav_settings -> callSettings()
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        callIntent = this.intent
        portIntent = "${callIntent.extras?.get("port")}"
        ipIntent = "${callIntent.extras?.get("id")}"

        binding = ActivityMain2Binding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        println("Package: $packageName")

        if (Build.VERSION.SDK_INT >= 27) {
            this.setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        callTV()

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        toast("Exit with home button", Toast.LENGTH_LONG)
    }

    private fun toast(text: String, dur: Int) {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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
