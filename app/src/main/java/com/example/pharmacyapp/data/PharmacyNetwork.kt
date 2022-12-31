package com.example.pharmacyapp.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = PharmacyNetwork.TABLE_NAME)
data class PharmacyNetwork(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String=""
) {
    companion object {
        const val TABLE_NAME = "pharmacy_network"
    }
    @Ignore
    var pharmacyList: MutableList<Pharmacy?> = mutableListOf()
}
