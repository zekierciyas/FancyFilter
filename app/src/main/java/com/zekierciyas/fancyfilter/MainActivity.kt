package com.zekierciyas.fancyfilter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zekierciyas.fancyfilterlib.FancyFilter
import com.zekierciyas.fancyfilterlib.FancyFilters
import com.zekierciyas.library.observe.Observers
import com.zekierciyas.library.view.SimpleCameraView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    /** Camera Preview object that represent camera operations*/
    private val camera: SimpleCameraView by lazy {
        findViewById(R.id.simple_camera_view)
    }

    /** Handle Image capturing*/
    private val button: AppCompatImageView by lazy {
        findViewById(R.id.capture_button)
    }

    /** Filtered image preview */
    private val preview1: AppCompatImageView by lazy {
        findViewById(R.id.filtered_image_preview)
    }

    /** Filtered image preview */
    private val preview2: AppCompatImageView by lazy {
        findViewById(R.id.filtered_image_preview2)
    }

    /** Camera Permissions */
    private val permissions = listOf(Manifest.permission.CAMERA)

    /** Camera permission req code */
    private val permissionsRequestCode = Random.nextInt(0, 10000)

    /** To keep it simple code as possible; camera ui state holding on view layer  */
    private var cameraState: Camera = Camera.EMPTY

    /** Camera UI state as enum */
    enum class Camera {
        EMPTY,
        READY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera.imageCapture(this) {
            if (it) {
                //Setting camera state
                cameraState = Camera.READY
            }
        }

        button.setOnClickListener{
            if (cameraState == Camera.READY) {
                // If camera state is ready, taking picture
                camera.takePhoto(observerImageCapture)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Camera permission check
       if (!hasPermissions(this)){
           Toast
               .makeText(
                   this,
                   "Camera permissions denied",
                   Toast.LENGTH_LONG).show()
           ActivityCompat.requestPermissions(
               this, permissions.toTypedArray(), permissionsRequestCode)
       }
    }

    /** Applying LUT filters on camera image then setting to preview view temporarily.
     * @param bitmap : Bitmap that will be applied filter
     */
    private fun applyFilter(bitmap: Bitmap) {
        val start = System.currentTimeMillis()
       FancyFilter.Builder()
            .withContext(this)
            .filter(FancyFilters.NO_9)
            .bitmap(bitmap)
            .applyFilter{
                runOnUiThread {
                println("Time period while applying filter ${System.currentTimeMillis() - start}")
                preview1.setImageBitmap(it)
            }
        }
    }

    /** Applying list of filters on camera image then setting to preview view temporarily.
     * @param bitmap : Bitmap that will be applied filter
     */
    private fun applyFilters(bitmap: Bitmap) {
        val start = System.currentTimeMillis()
        FancyFilter.Builder()
            .withContext(this)
            .filters(listOf(FancyFilters.NO_49, FancyFilters.NO_28))
            .bitmap(bitmap)
            .applyFilters{
                runOnUiThread {
                    println("Time period while applying filter ${System.currentTimeMillis() - start}")
                    preview1.setImageBitmap(it.first())
                    preview2.setImageBitmap(it[1])
                }
            }
    }

    /** Observing Image Capture Events*/
    private val observerImageCapture: Observers.ImageCapture = object : Observers.ImageCapture {
        override fun result(savedUri: Uri?, exception: Exception?) {
            if (savedUri != null) {
                println( "Image capture is succeed")

                applyFilters(savedUri.uriToBitmap(this@MainActivity)!!.rotateHorizontally())
            }
        }
    }

    /** Returns has required permissions or not */
    private fun hasPermissions(context: Context) = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}