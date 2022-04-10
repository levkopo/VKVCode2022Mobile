package ru.levkopo.vezdecodmobile.fragments

import android.Manifest
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.MaterialColors
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONObject
import ru.levkopo.vezdecodmobile.App
import ru.levkopo.vezdecodmobile.R
import ru.levkopo.vezdecodmobile.databinding.FragmentMapBinding
import ru.levkopo.vezdecodmobile.utils.ColorUtils

class MapFragment: Fragment(), UserLocationObjectListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var orderPosition: PlacemarkMapObject? = null
    private lateinit var userLocationLayer: UserLocationLayer
    private val mapKit = MapKitFactory.getInstance()

    private val mapView get() = binding.mapView
    private val map get() = binding.mapView.map
    private var userPositionLoading = false

    val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        permissions.entries.forEach {
            if (it.value) {
                mapView.isVisible = true

                userLocationLayer.isVisible = true
                userLocationLayer.isHeadingEnabled = true

                mapKit.createLocationManager().requestSingleUpdate(object : LocationListener {
                    override fun onLocationUpdated(@NonNull location: Location) {
                        if(userPositionLoading) return

                        userPositionLoading = true
                        lifecycleScope.launchWhenStarted {
                            val request = Request.Builder()
                                .url(
                                    "https://suggestions.dadata.ru/suggestions/api/4_1/rs/geolocate/address?" +
                                        "lat=" + location.position.latitude + "&" +
                                        "lon=" + location.position.longitude + "&" +
                                        "count=1"
                                )
                                .header("Accept", "application/json")
                                .header("Authorization", "Token 1768cc24362349db10b549b1baa380ee7f33ec64")
                                .build()

                            withContext(Dispatchers.IO) {
                                try {
                                    App.okhttp.newCall(request).execute().use { response ->
                                        val responseBody = response.body?.string()
                                        if(responseBody!=null) {
                                            val responseJSON = JSONObject(responseBody)
                                            val suggestion = responseJSON.optJSONArray("suggestions")
                                                ?.optJSONObject(0)

                                            lifecycleScope.launch {
                                                if(suggestion!=null) {
                                                    binding.location.text = suggestion.getString("value")
                                                }else binding.location.text = getString(R.string.unknown)
                                            }
                                        }
                                    }
                                }catch (e: Exception) {
                                    e.printStackTrace()

                                    lifecycleScope.launch {
                                        binding.location.text = getString(R.string.error)
                                    }
                                }

                                userPositionLoading = false
                            }
                        }
                    }

                    override fun onLocationStatusUpdated(status: LocationStatus) {}
                })
            } else {
                mapView.isVisible = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setObjectListener(this)

        val pointViewProvider = ViewProvider(
            View(requireContext()).apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_storefront_outline_24)
            }
        )


        App.activeOrder.observe(this) {
            if(it!=null) {
                if(orderPosition!=null)
                    map.mapObjects.remove(orderPosition!!)

                orderPosition = map.mapObjects.addPlacemark(Point(it.latitude!!, it.longitude!!), pointViewProvider)
            }
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        return binding.root
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer.setAnchor(
            PointF(mapView.width * 0.5f, mapView.height * 0.5f),
            PointF(mapView.width * 0.5f, mapView.height * 0.83f),
        )

        userLocationView.accuracyCircle.fillColor = ColorUtils.adjustAlpha(
            MaterialColors.getColor(mapView, R.attr.colorPrimary),
            0.5f
        )
    }

    override fun onObjectRemoved(p0: UserLocationView) {}
    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }
}