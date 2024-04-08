package com.example.tdmpa_3p_ex_nhba_issc_512

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageController(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> EventsFragment()
            1 -> NotesFragment()
            2 -> TasksFragment()
            else -> CompleteFragment()
        }
    }

    override fun getCount(): Int {
        return 4 // Número total de pestañas
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "EVENTS"
            1 -> "NOTES"
            2 -> "TASKS"
            else -> "COMPLETE"
        }
    }
}
