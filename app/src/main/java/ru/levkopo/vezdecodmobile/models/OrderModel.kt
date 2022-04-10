package ru.levkopo.vezdecodmobile.models

import java.text.SimpleDateFormat;
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*

@RealmClass
open class OrderModel(): RealmModel {
    @Required var name: String = ""
    @Required var deliveryTime: Long? = 0
    @Required var startDate: Long? = 0
    @Required var longitude: Double? = 0.0
    @Required var latitude: Double? = 0.0
    @Required var address: String = ""

    @Required var status: String = State.NONE.name

    var statusEnum: State
        get() = try {
            State.valueOf(status)
        } catch (e: IllegalArgumentException) {
            State.NONE
        }
        set(value) { status = value.name }

    constructor(
        name: String,
        address: String,
    ): this() {
        this.name = name
        this.address = address
    }

    fun getDeliveryTimeString(): String {
        return try {
            val sdf = SimpleDateFormat("HH:MM", Locale.ENGLISH)
            val netDate = Date(deliveryTime!! * 1000L)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    enum class State {
        NONE,
        REJECT,
        COMPLETE,
        WORKING,
    }

    companion object {
        fun generate(): OrderModel {
            val order = OrderModel()
            when(Random().nextInt(3)) {
                0 -> order.apply {
                    name = "ЖизньМарт"
                    address = "ул. Мельникова, 38, Екатеринбург, Свердловская обл., 620109"
                    latitude = 56.83173896045454
                    longitude = 60.56723415675936
                }
                1 -> order.apply {
                    name = "Монетка"
                    address = "ул. Ключевская, 15, Екатеринбург, Свердловская обл., 620109"
                    latitude = 56.832531765386676
                    longitude = 60.56705713096439
                }
                2 -> order.apply {
                    name = "Макдоналдс"
                    address = "ул. Викулова, 38Г, Екатеринбург, Свердловская обл., 620131"
                    latitude = 56.83154275473094
                    longitude = 60.543705069995426
                }
            }

            order.deliveryTime = System.currentTimeMillis()/1000L + 3600

            return order
        }
    }
}