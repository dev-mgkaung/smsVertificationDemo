package com.example.otp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val smsBroadcastReceiver by lazy { SmsBroadcastReceiver() }
    private val CREDENTIAL_PICKER_REQUEST = 1  // Set to an unused request code

    // Construct a request for phone numbers and show the picker
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        val credentialsClient = Credentials.getClient(this)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST,
                null, 0, 0, 0
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestHint()
        val client = SmsRetriever.getClient(this)
        val retriever = client.startSmsRetriever()
        retriever.addOnSuccessListener {

            Toast.makeText(this@MainActivity,"Listener started", Toast.LENGTH_SHORT).show()

            val otpListener = object : SmsBroadcastReceiver.OTPListener {
                override fun onOTPReceived(otp: String) {
                    Toast.makeText(this@MainActivity,"otp $otp", Toast.LENGTH_SHORT).show()
                    etVerificationCode.setText(otp)
                }

                override fun onOTPTimeOut() {
                    Toast.makeText(this@MainActivity,"TimeOut", Toast.LENGTH_SHORT).show()
                }
            }
            smsBroadcastReceiver.injectOTPListener(otpListener)
            registerReceiver(smsBroadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }
        retriever.addOnFailureListener {
            Toast.makeText(this@MainActivity,"Problem to start listener", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->

                // Obtain the phone number from the result
                if (resultCode == Activity.RESULT_OK && data != null) {
                    showMessage("Obtain the phone number from the result")
                    val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    // credential.getId();  <-- will need to process phone number string
                    showMessage(credential?.getId().toString())
                }

        }
    }



    fun showMessage(data: String)
    {
        Toast.makeText(this,data,Toast.LENGTH_LONG).show()
    }
    companion object {
        const val TAG = "SMS_USER_CONSENT"    }
}