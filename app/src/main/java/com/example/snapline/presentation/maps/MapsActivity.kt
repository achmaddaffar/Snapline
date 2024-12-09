package com.example.snapline.presentation.maps

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.snapline.R
import com.example.snapline.databinding.ActivityMapsBinding
import com.example.snapline.util.UiText
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var mMap: GoogleMap

    private val viewModel: MapsViewModel by viewModels()
    private val boundsBuilder = LatLngBounds.Builder()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted)
            getMyLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        collectState()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success)
                Toast.makeText(
                    this,
                    getString(R.string.error_when_implementing_map_style),
                    Toast.LENGTH_SHORT
                ).show()
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(
                this,
                exception.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupAction() {
        loadingDialog = Dialog(this@MapsActivity)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        if (loadingDialog.window != null)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsActivity)
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.storyError.collectLatest {
                        showErrorLayout(true)
                        loadingDialog.cancel()
                        when (it) {
                            is UiText.DynamicString -> {
                                Toast.makeText(
                                    this@MapsActivity,
                                    it.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is UiText.StringResource -> {
                                Toast.makeText(
                                    this@MapsActivity,
                                    getString(it.id),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.storyLoading.collectLatest {
                        showErrorLayout(false)
                        loadingDialog.show()
                    }
                }

                launch {
                    viewModel.storyData.collectLatest { response ->
                        loadingDialog.cancel()
                        response?.let {
                            val storyList = response.listStory ?: emptyList()
                            storyList.forEach {
                                val latitude = it.lat as Double
                                val longitude = it.lon as Double
                                val latLng = LatLng(latitude, longitude)
                                val title = it.name as String
                                val desc = it.description as String
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(title)
                                        .snippet(desc)
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_GREEN)
                                        )
                                )
                                boundsBuilder.include(latLng)
                            }

                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    200
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showErrorLayout(isError: Boolean) {
        binding.errorLayout.visibility = if (isError) View.VISIBLE else View.GONE
    }
}