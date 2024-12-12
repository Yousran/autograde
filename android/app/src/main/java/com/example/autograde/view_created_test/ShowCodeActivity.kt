package com.example.autograde.view_created_test

import android.os.Bundle
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.example.autograde.databinding.ActivityShowCodeBinding
import com.journeyapps.barcodescanner.BarcodeEncoder

class ShowCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val testId: String? = intent.getStringExtra("TEST_ID")

        if (!testId.isNullOrEmpty()) {

            val qrCodeBitmap = generateQRCode(testId)

            binding.ivQr.setImageBitmap(qrCodeBitmap)

            binding.qrCode.text = testId
        } else {
            binding.qrCode.text = "Invalid Test ID"
        }
    }

    fun generateQRCode(data: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(data, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
