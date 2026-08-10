package com.phonebridge

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Bundle
import android.telecom.TelecomManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread


class NetworkService : Service() {

    companion object {

        private const val PORT = 5000

        private const val CHANNEL_ID =
            "phonebridge_service"

        private const val NOTIFICATION_ID =
            1001
    }


    private var serverSocket: ServerSocket? = null

    @Volatile
    private var running = false


    override fun onCreate() {

        super.onCreate()

        createNotificationChannel()

        startForeground(
            NOTIFICATION_ID,
            createNotification()
        )

        startServer()
    }


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        return START_STICKY
    }


    private fun startServer() {

        if (running) {
            return
        }

        running = true


        thread(
            name = "PhoneBridgeServer"
        ) {

            try {

                serverSocket =
                    ServerSocket(PORT)


                println(
                    "PhoneBridge server started on port $PORT"
                )


                while (running) {

                    val socket =
                        serverSocket!!.accept()


                    println(
                        "Connection from: ${socket.inetAddress.hostAddress}"
                    )


                    thread {

                        handleClient(socket)
                    }
                }


            } catch (error: Exception) {

                error.printStackTrace()
            }
        }
    }


    private fun handleClient(
        socket: Socket
    ) {

        socket.use {

            try {

                val reader =
                    BufferedReader(
                        InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8
                        )
                    )


                val writer =
                    OutputStreamWriter(
                        socket.getOutputStream(),
                        StandardCharsets.UTF_8
                    )


                val command =
                    reader.readLine()


                if (command == null) {

                    writer.write(
                        "ERROR:EMPTY"
                    )

                    writer.flush()

                    return
                }


                val cleanCommand =
                    command.trim()


                println(
                    "Command: $cleanCommand"
                )


                when {

                    cleanCommand.startsWith(
                        "CALL:"
                    ) -> {

                        val number =
                            cleanCommand
                                .substringAfter("CALL:")
                                .trim()


                        println(
                            "Call request: $number"
                        )


                        if (
                            isValidPhoneNumber(
                                number
                            )
                        ) {

                            val success =
                                placePhoneCall(
                                    number
                                )


                            if (success) {

                                writer.write(
                                    "OK:CALL"
                                )

                            } else {

                                writer.write(
                                    "ERROR:CALL"
                                )
                            }

                        } else {

                            writer.write(
                                "ERROR:INVALID_NUMBER"
                            )
                        }
                    }


                    else -> {

                        println(
                            "Unknown command: $cleanCommand"
                        )


                        writer.write(
                            "ERROR:UNKNOWN_COMMAND"
                        )
                    }
                }


                writer.flush()


            } catch (error: Exception) {

                error.printStackTrace()
            }
        }
    }


    private fun isValidPhoneNumber(
        number: String
    ): Boolean {

        if (number.isEmpty()) {
            return false
        }


        if (number.length > 25) {
            return false
        }


        return number.all { character ->

            character.isDigit()
                    ||
            character == '+'
                    ||
            character == '*'
                    ||
            character == '#'
                    ||
            character == '-'
                    ||
            character == ' '
                    ||
            character == '('
                    ||
            character == ')'
        }
    }


    private fun placePhoneCall(
        number: String
    ): Boolean {

        if (
            Build.VERSION.SDK_INT >= 23
            &&
            checkSelfPermission(
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            println(
                "CALL_PHONE permission not granted"
            )

            return false
        }


        return try {

            val telecomManager =
                getSystemService(
                    TelecomManager::class.java
                )


            if (telecomManager == null) {

                println(
                    "TelecomManager unavailable"
                )

                return false
            }


            val uri =
                Uri.fromParts(
                    "tel",
                    number,
                    null
                )


            val extras =
                Bundle()


            extras.putBoolean(
                TelecomManager
                    .EXTRA_START_CALL_WITH_SPEAKERPHONE,
                false
            )


            telecomManager.placeCall(
                uri,
                extras
            )


            println(
                "Call requested: $number"
            )


            true


        } catch (error: Exception) {

            println(
                "Call error: ${error.message}"
            )

            error.printStackTrace()

            false
        }
    }


    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "PhoneBridge",
                    NotificationManager
                        .IMPORTANCE_LOW
                )


            channel.description =
                "خدمة استقبال طلبات الاتصال"


            val manager =
                getSystemService(
                    NotificationManager::class.java
                )


            manager.createNotificationChannel(
                channel
            )
        }
    }


    private fun createNotification():
            Notification {

        val builder =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {

                Notification.Builder(
                    this,
                    CHANNEL_ID
                )

            } else {

                Notification.Builder(
                    this
                )
            }


        return builder

            .setContentTitle(
                "PhoneBridge"
            )

            .setContentText(
                "خدمة الاتصال تعمل على المنفذ 5000"
            )

            .setSmallIcon(
                android.R.drawable.ic_menu_call
            )

            .setOngoing(true)

            .build()
    }


    override fun onDestroy() {

        running = false


        try {

            serverSocket?.close()

        } catch (_: Exception) {
        }


        serverSocket = null


        super.onDestroy()
    }


    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}
