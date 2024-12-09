package com.example.snapline.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import com.yalantis.ucrop.UCrop
import java.io.File

class CropActivityResultContract(
    private val xAspectRatio: Float,
    private val yAspectRatio: Float,
) : ActivityResultContract<Uri, Uri?>() {
    override fun createIntent(context: Context, input: Uri): Intent {
        return UCrop.of(
            input,
            File(
                context.externalCacheDir,
                System.currentTimeMillis().toString() + ".jpg"
            ).toUri()
        )
            .withAspectRatio(xAspectRatio, yAspectRatio)
            .getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return UCrop.getOutput(intent ?: return null)
    }
}