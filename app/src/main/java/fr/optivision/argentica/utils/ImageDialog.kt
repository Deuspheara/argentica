package fr.optivision.argentica.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.adapter_item_add_image.view.*
import java.io.ByteArrayOutputStream
import java.util.*

object ImageDialog {

    fun takePicture(launcher: ActivityResultLauncher<Intent>, context: Activity, maxSize: Int = 640, maxMo: Float = 0.5f, ratioX: Float = 16f, ratioY: Float = 9f) {
        ImagePicker.with(context)
            .crop(ratioX, ratioY)
            .compress((maxMo * 1024).toInt())
            .maxResultSize(maxSize, maxSize)
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }

    fun Activity.fileUriToBase64(fileUri: Uri, onImageConverted: (String) -> Unit) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, fileUri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    onImageConverted(
                        convertBitmapToBase64(bitmap)
                    )
                }
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, fileUri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                onImageConverted(
                    convertBitmapToBase64(bitmap)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertBitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return Base64.getEncoder().encodeToString(baos.toByteArray())
    }

    fun decodeBase64ToBitmap(base64: String, onImageDecode: (Bitmap) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val imageBytes = Base64.getDecoder().decode(base64)
                onImageDecode(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}