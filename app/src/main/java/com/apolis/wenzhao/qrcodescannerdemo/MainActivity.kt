package com.apolis.wenzhao.qrcodescannerdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        button = findViewById(R.id.btnScanBarcode)
        button!!.setOnClickListener {
            startActivity(Intent(this, ScannedBarcodeActivity::class.java))
        }
    }

}