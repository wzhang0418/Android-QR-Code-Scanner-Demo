package com.apolis.wenzhao.qrcodescannerdemo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class EmailActivity : AppCompatActivity(), View.OnClickListener {

    //UI Components
    private var inSubject: EditText? = null
    private var inBody: EditText? = null
    private var txtEmailAddress: TextView? = null
    private var btnSendEmail: Button? = null
    private var btnScanBarcode: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        inSubject = findViewById(R.id.inSubject);
        inBody = findViewById(R.id.inBody);
        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        btnSendEmail = findViewById(R.id.btnSendEmail)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)

        if(intent.getStringExtra("email_address") != null)
            txtEmailAddress!!.text = "Recipient: ${intent.getStringExtra("email_address")}"

        btnSendEmail!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(txtEmailAddress!!.text.toString()))
            intent.putExtra(Intent.EXTRA_SUBJECT, inSubject!!.text.toString().trim())
            intent.putExtra(Intent.EXTRA_TEXT, inBody!!.text.toString().trim())
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnScanBarcode -> startActivity(Intent(this, ScannedBarcodeActivity::class.java))
        }
    }
}