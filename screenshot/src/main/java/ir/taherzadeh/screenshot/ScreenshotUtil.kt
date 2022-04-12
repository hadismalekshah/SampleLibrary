package ir.taherzadeh.screenshot

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
object ScreenshotUtil {

    lateinit var context: Context
    lateinit var customView: View
    var permissionListener: Boolean = false

    fun shareScreenShootResult( context: Context, view: View, permissionListener: Boolean) {
        this.context=context
        this.customView=view
        this.permissionListener=permissionListener

        val dateFormatter by lazy {
            SimpleDateFormat(
                "yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault()
            )
        }
        val filename =
            "${context.getString(R.string.screenshot)}${dateFormatter.format(Date())}.png"
        val screenShootFolderPath = File.separator + context.getAppName()

        val uri = view.makeScreenShot()
            .saveScreenShot(
                context, filename, screenShootFolderPath,
                permissionListener
            )
            ?: return

        dispatchShareImageIntent(screenShotUri = uri)
    }

    private fun dispatchShareImageIntent(screenShotUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, screenShotUri)
        context.startActivity(Intent.createChooser(intent, "Share"))

    }

    private fun Context.getAppName(): String {
        var appName: String = ""
        val applicationInfo = applicationInfo
        val stringId = applicationInfo.labelRes
        appName = if (stringId == 0) {
            applicationInfo.nonLocalizedLabel.toString()
        } else {
            getString(stringId)
        }
        return appName
    }

    private fun View.makeScreenShot(): Bitmap {
        setBackgroundColor(context.getColor(R.color.app_black))
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    private fun Bitmap.saveScreenShot(
        requireContext: Context,
        filename: String,
        ScreenShootFolderPath: String,
        permissionListener: Boolean,
    ): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            saveImageInQ(this, filename, ScreenShootFolderPath, requireContext.contentResolver)
        else
            legacySave(this, filename, ScreenShootFolderPath, permissionListener, requireContext)
    }

    private fun saveImageInQ(
        bitmap: Bitmap,
        filename: String,
        parentFileName: String,
        contentResolver: ContentResolver
    ): Uri? {
        val fos: OutputStream?
        val uri: Uri?
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + parentFileName
            )
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { contentResolver.openOutputStream(it) }.also { fos = it }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        fos?.flush()
        fos?.close()

        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        uri?.let {
            contentResolver.update(it, contentValues, null, null)
        }
        return uri
    }

    private fun legacySave(
        bitmap: Bitmap,
        filename: String,
        parentFileName: String,
        permissionListener: Boolean,
        context: Context
    ): Uri? {
        val fos: OutputStream?
        if (!permissionListener) {
            return null
        }

        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() +
                    parentFileName + File.separator + filename
        val imageFile = File(path)
        if (imageFile.parentFile?.exists() == false) {
            imageFile.parentFile?.mkdir()
        }
        imageFile.createNewFile()
        fos = FileOutputStream(imageFile)

        val photoURI = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider", imageFile
//            BuildConfig.APPLICATION_ID + ".provider", imageFile
        )

        fos.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        fos.flush()
        fos.close()

        return photoURI
    }

    fun Context.toast(message: String) { //Just to display a toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}