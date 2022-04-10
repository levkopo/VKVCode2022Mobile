package ru.levkopo.vezdecodmobile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import ru.levkopo.vezdecodmobile.R
import ru.levkopo.vezdecodmobile.databinding.OrderItemBinding
import ru.levkopo.vezdecodmobile.models.OrderModel


class OrdersAdapter(data: OrderedRealmCollection<OrderModel?>?) :
        RealmRecyclerViewAdapter<OrderModel?, OrdersAdapter.ViewHolder?>(data, true) {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }


    class ViewHolder(
        val parent: ViewGroup,
        val layout: OrderItemBinding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(layout.root) {
        init {
            itemView.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(order: OrderModel) {
            layout.order = order
            when(order.statusEnum) {
                OrderModel.State.WORKING -> {
                    layout.status.setTextColor(ContextCompat.getColor(parent.context, R.color.orange))
                    layout.status.text = parent.context.getString(R.string.working_status)
                }

                OrderModel.State.COMPLETE -> {
                    layout.status.setTextColor(ContextCompat.getColor(parent.context, R.color.green))
                    layout.status.text = parent.context.getString(R.string.complete_status)
                }

                OrderModel.State.REJECT -> {
                    layout.status.setTextColor(ContextCompat.getColor(parent.context, R.color.red))
                    layout.status.text = parent.context.getString(R.string.reject_status)
                }
            }
        }
    }
}