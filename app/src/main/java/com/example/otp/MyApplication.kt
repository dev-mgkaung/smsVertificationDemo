package com.example.otp

import android.app.Application
import android.util.Log
import android.widget.Toast

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Log.i("AppApplication","appSignatures = ${AppSignatureHelper(this).getAppSignatures()}")
        Toast.makeText(this,"App Signatures  ${AppSignatureHelper(this).getAppSignatures()}", Toast.LENGTH_SHORT).show()

    }
}