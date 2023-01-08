package com.example.pharmacyapp.tcp_connection

import android.util.Log
import android.widget.Toast
import com.example.pharmacyapp.MainActivity
import com.example.pharmacyapp.data.PharmacyNetworkList
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
                    private val activity: MainActivity
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
                activity.connType = 2
                Thread(Thread2Server()).start()
                sendDataToServer("*")
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
                        processingInputStream(message)
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

    internal inner class ThreadT : Runnable
    {
        override fun run() {
            while (true)
            {
                if (System.currentTimeMillis() - startTime > 5000L && activity.connType == 0)
                {
                    activity.runOnUiThread { Toast.makeText(
                        activity,
                        "Не удалось подключиться к серверу. " +
                                "Будут использованы данные из локальной базы данных.",
                        Toast.LENGTH_LONG
                    ).show() }
                    activity.showDBPharmacyNetworks()
                    activity.connType = 1
                }
            }
        }
    }

    fun sendDataToServer(text: String)
    {
        Log.d("com.example.pharmacyapp.tcp_connection", "Before send data: $text")
        Thread(Thread3Server(text + "\n")).start()
    }

    private fun processingInputStream(text: String)
    {
        val serverData: PharmacyNetworkList = gson.fromJson(text, PharmacyNetworkList::class.java)
        Log.d("com.example.pharmacyapp.tcp_connection",
            "Text: $text \nServer output: ${serverData.pharmacyNetworkList}")
        pr.deleteAllData()
        for (i in serverData.pharmacyNetworkList)
        {
            if (i != null) {
                pr.addPharmacyNetwork(i)
                for(j in i.pharmacyList){
                    if(j != null)
                        pr.addPharmacy(j)
                }
            }
        }
        activity.showDBPharmacyNetworks()
    }

    init {
        thread1 = Thread(Thread1Server())
        thread1!!.start()
        threadT = Thread(ThreadT())
        threadT!!.start()
    }
}