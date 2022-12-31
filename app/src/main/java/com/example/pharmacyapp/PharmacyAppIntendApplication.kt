package com.example.pharmacyapp

import android.app.Application
import android.content.Context
import com.example.pharmacyapp.repository.PharmacyRepository

class PharmacyAppIntendApplication: Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: PharmacyAppIntendApplication? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        PharmacyRepository.initialize(this)
    }
}