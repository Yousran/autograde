package com.example.autograde.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanQRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(true)
            setOrientationLocked(true)
            initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {

                finish()
            } else {

                val scannedData = result.contents

                Intent().apply {
                    putExtra("QR_RESULT", scannedData)
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
