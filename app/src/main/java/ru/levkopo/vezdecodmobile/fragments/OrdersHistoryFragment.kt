package ru.levkopo.vezdecodmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Sort
import io.realm.kotlin.where
import ru.levkopo.vezdecodmobile.App
import ru.levkopo.vezdecodmobile.adapters.OrdersAdapter
import ru.levkopo.vezdecodmobile.models.OrderModel

class OrdersHistoryFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = OrdersAdapter(App.realm.where<OrderModel>()
            .sort("startDate", Sort.DESCENDING)
            .findAll())

        return recyclerView
    }
}