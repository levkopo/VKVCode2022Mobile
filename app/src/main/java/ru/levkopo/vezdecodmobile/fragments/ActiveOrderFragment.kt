package ru.levkopo.vezdecodmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.kotlin.where
import ru.levkopo.vezdecodmobile.App
import ru.levkopo.vezdecodmobile.R
import ru.levkopo.vezdecodmobile.databinding.FragmentActiveOrdersBinding
import ru.levkopo.vezdecodmobile.models.OrderModel
import ru.levkopo.vezdecodmobile.utils.ColorUtils
import ru.levkopo.vezdecodmobile.widgets.FlingCardView
import java.util.*


class ActiveOrderFragment: Fragment() {

    private lateinit var viewModel: OrdersHistoryViewModel
    private var _binding: FragmentActiveOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<OrdersHistoryViewModel>().value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActiveOrdersBinding.inflate(inflater, container, false)
        binding.orderCard.acceptBtn.setOnClickListener {
            if(App.activeOrder.value!=null) return@setOnClickListener
            binding.orderCard.card.setStatus(false)
            acceptOrder()
        }

        binding.orderCard.rejectBtn.setOnClickListener {
            if(App.activeOrder.value!=null) return@setOnClickListener

            binding.orderCard.card.setStatus(false)
            rejectOrder()
        }

        binding.complete.setOnClickListener { completeOrder() }

        binding.orderCard.card.listener = object: FlingCardView.FlingListener() {
            override fun onLeftEnd(): Boolean {
                rejectOrder()
                return false
            }

            override fun onRightEnd(): Boolean {
                acceptOrder()
                return false
            }

            override fun leftScroll(position: Float, max: Float) {
                binding.orderCard.root.setBackgroundColor(ColorUtils.adjustAlpha(
                    ContextCompat.getColor(requireContext(), R.color.red),
                    position/max
                ))
            }

            override fun rightsScroll(position: Float, max: Float) {
                binding.orderCard.root.setBackgroundColor(ColorUtils.adjustAlpha(
                    ContextCompat.getColor(requireContext(), R.color.green),
                    position/max
                ))
            }
        }

        viewModel.currentCardStore.observe(this) { order ->
            binding.order = order
            binding.orderCard.card.setStatus(true)
        }

        App.activeOrder.observe(this) { activeOrder ->
            if(activeOrder!=null) {
                binding.order = null
            }else getNextStore()
        }

        return binding.root
    }

    private fun acceptOrder() {
        App.activeOrder.postValue(binding.order)

        val order = binding.order ?: return
        order.startDate = System.currentTimeMillis()/1000L
        order.statusEnum = OrderModel.State.WORKING

        App.realm.executeTransaction { transition ->
            transition.insert(order)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_accept)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog?.dismiss() }
            .create()
            .show()
    }

    private fun rejectOrder() {
        getNextStore()

        val order = binding.order ?: return
        order.startDate = System.currentTimeMillis()/1000L
        order.statusEnum = OrderModel.State.REJECT

        App.realm.executeTransaction { transition ->
            transition.insert(order)
        }
    }

    private fun completeOrder() {
        App.realm.executeTransaction { transition ->
            val order = transition.where<OrderModel>()
                .equalTo("status", OrderModel.State.WORKING.name)
                .findFirst()

            order?.statusEnum = OrderModel.State.COMPLETE
        }

        App.activeOrder.postValue(null)

        getNextStore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getNextStore() {
        viewModel.currentCardStore.postValue(OrderModel.generate())
    }

    class OrdersHistoryViewModel: ViewModel() {
        var currentCardStore = MutableLiveData<OrderModel?>(null)
    }
}