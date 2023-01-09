package com.example.pharmacyapp.tcp_connection

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
    private var thread4: Thread? = null
    val pr = PharmacyRepository.get()

    internal inner class Thread1 : Runnable {
        override fun run()
        {
            val socket: Socket
            try {
                socket = Socket(IP, PORT)
                output = PrintWriter(socket.getOutputStream())
                input = BufferedReader(InputStreamReader(socket.getInputStream()))
                activity.connType = 2
                Thread(Thread2()).start()
                sendData("*")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    internal inner class Thread2 : Runnable {
        override fun run() {
            while (true) {
                try {
                    val message = input!!.readLine()
                    if (message != null)
                    {
                        readData(message)
                    } else {
                        thread1 = Thread(Thread1())
                        thread1!!.start()
                        return
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal inner class Thread3(private val message: String) : Runnable
    {
        override fun run()
        {
            output!!.write(message)
            output!!.flush()
        }
    }

    internal inner class Thread4 : Runnable
    {
        override fun run() {
            while (true)
            {
                if (System.currentTimeMillis() - startTime > 5000L && activity.connType == 0)
                {
                    activity.runOnUiThread { Toast.makeText(
                        activity,
                        "Не удалось подключиться к серверу. Будут использованы данные из локальной базы данных.",
                        Toast.LENGTH_LONG
                    ).show() }
                    activity.showDBPharmacyNetworks()
                    activity.connType = 1
                }
            }
        }
    }

    fun sendData(text: String)
    {
        Thread(Thread3(text + "\n")).start()
    }

    private fun readData(text: String)
    {
        val serverData: PharmacyNetworkList = gson.fromJson(text, PharmacyNetworkList::class.java)
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
        thread1 = Thread(Thread1())
        thread1!!.start()
        thread4 = Thread(Thread4())
        thread4!!.start()
    }
}