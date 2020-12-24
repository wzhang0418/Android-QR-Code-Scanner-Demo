package com.apolis.wenzhao.qrcodescannerdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException


class ScannedBarcodeActivity : AppCompatActivity() {

    //UI components
    private var surfaceView: SurfaceView? = null
    private var txtBarcodeValue: TextView? = null
    private var btnAction: Button? = null

    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    private var intentData: String = ""
    private var isEmail: Boolean = false

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 201
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_barcode)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    private fun initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue)
        surfaceView = findViewById(R.id.surfaceView)
        btnAction = findViewById(R.id.btnAction)

        btnAction!!.setOnClickListener {
            if(intentData.isNotEmpty()) {
                if(isEmail)
                    startActivity(Intent(this, EmailActivity::class.java))
                else
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(intentData)))
            }
        }
    }


    private fun initialiseDetectorsAndSources() {

        Toast.makeText(this, "Barcode scanner started", Toast.LENGTH_SHORT).show()

        barcodeDetector =
            BarcodeDetector
                .Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        cameraSource =
            CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build()

        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScannedBarcodeActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        cameraSource.start(surfaceView!!.holder)
                    else
                        ActivityCompat.requestPermissions(
                            this@ScannedBarcodeActivity, arrayOf(
                                Manifest.permission.CAMERA
                            ), REQUEST_CAMERA_PERMISSION
                        )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }

        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    this@ScannedBarcodeActivity,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val barcodes: SparseArray<Barcode> = p0.detectedItems
                if(barcodes.size() != 0) {
                    txtBarcodeValue!!.post(object: Runnable {
                        @SuppressLint("SetTextI18n")
                        override fun run() {
                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue!!.removeCallbacks(null)
                                intentData = barcodes.valueAt(0).email.address
                                txtBarcodeValue!!.text = intentData
                                isEmail = true
                                btnAction?.text = "ADD CONTENT TO THE MAIL"
                            } else {
                                isEmail = false
                                btnAction!!.text = "LAUNCH URL"
                                intentData = barcodes.valueAt(0).displayValue
                                txtBarcodeValue!!.text = intentData
                            }
                        }

                    })
                }
            }

        })
    }
}