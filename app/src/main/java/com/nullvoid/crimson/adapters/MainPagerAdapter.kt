package com.nullvoid.crimson.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nullvoid.crimson.R
import com.nullvoid.crimson.fragments.ContactsFragment
import com.nullvoid.crimson.fragments.MapsFragment

class MainPagerAdapter(private var ctx: Context, fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return if (position == 0) ContactsFragment() else MapsFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) ctx.getString(R.string.contacts) else ctx.getString(R.string.maps)
    }

}