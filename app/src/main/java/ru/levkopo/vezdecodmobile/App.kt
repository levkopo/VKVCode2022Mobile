package ru.levkopo.vezdecodmobile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import ru.levkopo.vezdecodmobile.models.OrderModel

class App: Application() {
    companion object {
        val realm get() = Realm.getDefaultInstance()
        var okhttp = OkHttpClient()

        var activeOrder = MutableLiveData<OrderModel?>(null)
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(config)
    }
}