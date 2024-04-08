package com.example.tdmpa_3p_ex_nhba_issc_512

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = PageController(supportFragmentManager)
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)

        mostrarIconosEnTabPage()

    }

    fun mostrarIconosEnTabPage(){
        tabLayout.getTabAt(0)?.setIcon(R.drawable.icon_event)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.icon_note)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.icon_task)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.icon_complete)

    }

}
