package com.phonebridge

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView


class MainActivity : Activity() {

    companion object {

        private const val
        CALL_PERMISSION_REQUEST = 1001
    }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        buildInterface()

        checkCallPermission()
    }


    private fun buildInterface() {

        val layout =
            LinearLayout(this)


        layout.orientation =
            LinearLayout.VERTICAL


        layout.gravity =
            Gravity.CENTER


        layout.setPadding(
            40,
            40,
            40,
            40
        )


        val title =
            TextView(this)


        title.text =
            "PhoneBridge"


        title.textSize =
            30f


        title.gravity =
            Gravity.CENTER


        title.setTextColor(
            Color.BLACK
        )


        layout.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        val status =
            TextView(this)


        status.text =
            "جاري تشغيل خدمة الاتصال..."


        status.textSize =
            18f


        status.gravity =
            Gravity.CENTER


        status.setPadding(
            0,
            30,
            0,
            30
        )


        layout.addView(
            status,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        val startButton =
            Button(this)


        startButton.text =
            "تشغيل خدمة PhoneBridge"


        startButton.textSize =
            18f


        startButton.setOnClickListener {

            startNetworkService()

            status.text =
                "الخدمة تعمل\n\nالمنفذ: 5000"
        }


        layout.addView(
            startButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        val info =
            TextView(this)


        info.text =
            """
            عنوان الخدمة:

            192.168.254.27:5000

            هاتف الزوجة يرسل:

            CALL:رقم الهاتف

            والتطبيق ينفذ المكالمة.
            """.trimIndent()


        info.textSize =
            16f


        info.gravity =
            Gravity.CENTER


        info.setPadding(
            0,
            40,
            0,
            0
        )


        layout.addView(
            info,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        setContentView(layout)
    }


    private fun checkCallPermission() {

        if (
            android.os.Build.VERSION.SDK_INT >= 23
            &&
            checkSelfPermission(
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.CALL_PHONE
                ),
                CALL_PERMISSION_REQUEST
            )
        }
    }


    private fun startNetworkService() {

        val intent =
            Intent(
                this,
                NetworkService::class.java
            )


        startService(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )


        if (
            requestCode ==
            CALL_PERMISSION_REQUEST
        ) {

            if (
                grantResults.isNotEmpty()
                &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                startNetworkService()
            }
        }
    }
}
