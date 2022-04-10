package ru.levkopo.vezdecodmobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mapkit.MapKitFactory
import io.realm.kotlin.where
import ru.levkopo.vezdecodmobile.databinding.ActivityMainBinding
import ru.levkopo.vezdecodmobile.adapters.MainPagerAdapter
import ru.levkopo.vezdecodmobile.models.OrderModel

class MainActivity : AppCompatActivity() {
    private val TAB_TITLES = arrayOf(
        R.string.active_order,
        R.string.orders_history,
        R.string.map
    )


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("7de0887c-b166-429f-bf1d-e204d527152f")
        MapKitFactory.initialize(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = MainPagerAdapter(this)

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        App.realm.executeTransaction { transition ->
            val order = transition.where<OrderModel>()
                .equalTo("status", OrderModel.State.WORKING.name)
                .findFirst()

            if(order!=null) App.activeOrder.postValue(order)
        }
    }
}