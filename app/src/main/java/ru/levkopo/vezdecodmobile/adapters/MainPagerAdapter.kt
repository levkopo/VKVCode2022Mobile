package ru.levkopo.vezdecodmobile.adapters

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.levkopo.vezdecodmobile.fragments.ActiveOrderFragment
import ru.levkopo.vezdecodmobile.fragments.MapFragment
import ru.levkopo.vezdecodmobile.fragments.OrdersHistoryFragment

class MainPagerAdapter(private val activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ActiveOrderFragment()
            1 -> OrdersHistoryFragment()
            2 -> MapFragment()
            else -> {
                Toast.makeText(activity, "Oops :(", Toast.LENGTH_LONG).show()
                throw Error("Oops")
            }
        }
    }
}