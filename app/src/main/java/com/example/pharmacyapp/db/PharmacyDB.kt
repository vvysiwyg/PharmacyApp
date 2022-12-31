package com.example.pharmacyapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.data.PharmacyNetwork

@Database(entities = [Pharmacy::class, PharmacyNetwork::class], version=1)
abstract class PharmacyDB: RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun pharmacyNetworkDao(): PharmacyNetworkDao
}