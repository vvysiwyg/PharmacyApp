package com.example.pharmacyapp.tcp_connection

import android.app.Activity
import android.util.Log
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.data.PharmacyNetwork
import com.example.pharmacyapp.data.Template
import com.example.pharmacyapp.repository.PharmacyRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class TCPConnection(private val IP: String,
                    private val PORT: Int,
                    private val startTime: Long,
                    private val activity: Activity
) {
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()
    private var output: PrintWriter? = null
    private var input: BufferedReader? = null
    var thread1: Thread? = null
    private var threadT: Thread? = null
    val pr = PharmacyRepository.get()

    internal inner class Thread1Server : Runnable {
        override fun run()
        {
            val socket: Socket
            try {
                Log.d("com.example.pharmacyapp.tcp_connection", "before socket init")
                socket = Socket(IP, PORT)
                Log.d("com.example.pharmacyapp.tcp_connection", "after socket init")
                output = PrintWriter(socket.getOutputStream())
                input = BufferedReader(InputStreamReader(socket.getInputStream()))
                Thread(Thread2Server()).start()
                sendDataToServer("{R}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    internal inner class Thread2Server : Runnable {
        override fun run() {
            while (true) {
                try {
                    val message = input!!.readLine()
                    Log.d("com.example.pharmacyapp.tcp_connection", "message read")
                    if (message != null)
                    {
                        activity.runOnUiThread { processingInputStream(message) }
                    } else {
                        Log.d("com.example.pharmacyapp.tcp_connection", "message is null")
                        thread1 = Thread(Thread1Server())
                        thread1!!.start()
                        return
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal inner class Thread3Server(private val message: String) : Runnable
    {
        override fun run()
        {
            Log.d("com.example.pharmacyapp.tcp_connection", "Thread3Server.run")
            output!!.write(message)
            output!!.flush()
        }
    }

//    internal inner class ThreadT : Runnable
//    {
//        override fun run() {
//            while (true)
//            {
//                if (System.currentTimeMillis() - startTime > 5000L)
//                {
//                    activity.runOnUiThread { Toast.makeText(
//                        activity,
//                        "Подключиться не удалось!\n" +
//                                "Будут использоваться данные из локальной базы данных.",
//                        Toast.LENGTH_LONG
//                    ).show() }
//                }
//            }
//        }
//    }

    fun sendDataToServer(text: String)
    {
        Log.d("com.example.pharmacyapp.tcp_connection", "Before send data: $text")
        Thread(Thread3Server(text + "\n")).start()
    }

    private fun processingInputStream(text: String)
    {
        val serverData: Template = gson.fromJson(text, Template::class.java)
        Log.d("com.example.pharmacyapp.tcp_connection", "Text: $text \nServer output: ${serverData.template}")
//        pr.deleteAllData()
//        val template: Map<PharmacyNetwork, List<Pharmacy?>> = mapOf()
//        val serverData: Map<PharmacyNetwork, List<Pharmacy?>> = gson.fromJson(text, template::class.java)
//
//        for (i in serverData)
//        {
//            pr.addPharmacyNetwork(i.key)
//            for(j in i.value){
//                if(j != null)
//                    pr.addPharmacy(j)
//            }
//        }
    }

    init {
        thread1 = Thread(Thread1Server())
        thread1!!.start()
//        threadT = Thread(ThreadT())
//        threadT!!.start()
    }
}