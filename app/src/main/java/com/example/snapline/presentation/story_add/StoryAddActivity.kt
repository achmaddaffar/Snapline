package com.example.snapline.presentation.story_add

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.snapline.R
import com.example.snapline.databinding.ActivityStoryAddBinding
import com.example.snapline.presentation.camera.CameraActivity
import com.example.snapline.presentation.camera.CameraActivity.Companion.CAMERA_X_RESULT
import com.example.snapline.util.CropActivityResultContract
import com.example.snapline.util.UiText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoryAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryAddBinding
    private lateinit var dialog: Dialog
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: StoryAddViewModel by viewModels()

    private var lat: Double? = null
    private var lon: Double? = null
    private var locationToggle: Boolean = false

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { cropMedia.launch(uri) }
        }

    private val cropMedia = registerForActivityResult(CropActivityResultContract(16f, 9f)) { uri ->
        uri?.let {
            viewModel.setUriImage(uri)
            setButtonEnable()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val uri = it.data?.getStringExtra(CameraActivity.PICTURE_EXTRA)?.toUri()
            uri?.let { cropMedia.launch(uri) }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_request_granted), Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.permission_request_denied), Toast.LENGTH_LONG
                ).show()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {}
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupAction()
        collectState()
    }

    private fun setupAction() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding) {
            dialog = Dialog(this@StoryAddActivity)
            dialog.setContentView(R.layout.dialog_loading)
            dialog.setCancelable(false)
            if (dialog.window != null)
                dialog.window?.setBackgroundDrawable(ColorDrawable(0))

            btnGallery.setOnClickListener {
                if (!allPermissionsGranted()) {
                    requestPermissionLauncher.launch(REQUIRED_PERMISSION)
                    return@setOnClickListener
                }
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            btnCamera.setOnClickListener {
                if (!allPermissionsGranted()) {
                    requestPermissionLauncher.launch(REQUIRED_PERMISSION)
                    return@setOnClickListener
                }
                val intent = Intent(this@StoryAddActivity, CameraActivity::class.java)
                launcherIntentCameraX.launch(intent)
            }

            buttonAdd.setOnClickListener {
                if (!locationToggle) {
                    lat = null
                    lon = null
                } else
                    getMyLastLocation()
                viewModel.uploadStory(
                    description = edAddDescription.text.toString(),
                    latitude = lat?.toFloat(),
                    longitude = lon?.toFloat()
                )
            }

            edAddDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                locationToggle = isChecked
                if (isChecked)
                    getMyLastLocation()
            }
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uriImage.collectLatest { uri ->
                        setButtonEnable()
                        uri?.let {
                            Glide
                                .with(this@StoryAddActivity)
                                .load(uri)
                                .apply(RequestOptions().transform(RoundedCorners(48)))
                                .into(binding.ivPreviewImage)
                        }
                    }
                }

                launch {
                    viewModel.uploadStoryError.collectLatest {
                        when (it) {
                            is UiText.DynamicString -> {
                                Toast.makeText(
                                    this@StoryAddActivity,
                                    it.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is UiText.StringResource -> {
                                Toast.makeText(
                                    this@StoryAddActivity,
                                    getString(it.id),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.uploadStoryLoading.collect { isLoading ->
                        if (isLoading) dialog.show() else dialog.cancel()
                    }
                }

                launch {
                    viewModel.uploadStoryData.collectLatest { response ->
                        response?.let {
                            MaterialAlertDialogBuilder(this@StoryAddActivity, R.style.CustomAlertDialogTheme)
                                .setCancelable(false)
                                .setTitle(R.string.success)
                                .setMessage(R.string.you_have_uploaded_your_story)
                                .setNegativeButton(
                                    R.string.back
                                ) { _, _ ->
                                    finish()
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun setButtonEnable() {
        val storyImage = binding.ivPreviewImage.drawable
        val description = binding.edAddDescription.text

        binding.buttonAdd.isEnabled =
            storyImage != null && description != null && description.toString()
                .isNotEmpty()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}