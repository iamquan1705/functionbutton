package com.example.funtionqr

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.funtionqr.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PHONE_REQUEST = 1
    private val SMS_REQUEST = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnCopy.setOnClickListener {
                copyText()
            }
            btnPhone.setOnClickListener {
                //makePhoneCall()
                // callPhone()
                createContact()
            }
            btnSMS.setOnClickListener {
                sentSMS()
            }
            btnUrl.setOnClickListener {
                var url = tvUrl.text.toString()
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://$url";
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        }
    }

    private fun copyText() {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val text = binding.tvText.text.toString()
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }


    private fun sentSMS() {
        var phonesms = binding.tvPhoneSms.text.toString()
        var body = binding.tvBody.text.toString()
        val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phonesms, null))
        smsIntent.putExtra("sms_body", body)
        smsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        startActivity(smsIntent)
    }


    private fun callPhone() {
        var phone = binding.tvPhone.text.toString()
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phone")
        startActivity(dialIntent)
    }


    private fun makePhoneCall() {
        val number: String = binding.tvPhone.text.toString()
        if (number.trim { it <= ' ' }.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PHONE_REQUEST
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this@MainActivity, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PHONE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createContact() {
        val intent = Intent(
            Intent.ACTION_INSERT,
            ContactsContract.Contacts.CONTENT_URI
        )
        intent.putExtra(ContactsContract.Intents.Insert.NAME, binding.tvBody.text.toString())
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, binding.tvPhone.text.toString())
        startActivity(intent)
    }

}
