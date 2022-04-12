package ir.taherzadeh.screenshot

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //    lateinit var mView:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("tag", "messages")
//        findViewById<TextView>(R.id.hello).setOnClickListener {
//            mView=it
//            shareScreenShootResult()
//        }
    }

//    fun shareScreenShootResult() {
//        ScreenshotUtil.shareScreenShootResult(this,mView,permissionListener())
//    }
//    private val permissionListener: () -> Boolean = {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            true
//        } else {
//            requestStoragePermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//            )
//            false
//        }
//    }
//
//    private val requestStoragePermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            var saveImageFlag = true
//            permissions.entries.forEach {
//                saveImageFlag = it.value
//            }
//            if (saveImageFlag) {
//                shareScreenShootResult()
//            } else {
//                toast("cant_share_ScreenShoot")
//            }
//        }
}