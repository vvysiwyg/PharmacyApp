package com.example.pharmacyapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = Pharmacy.TABLE_NAME, foreignKeys = [ForeignKey(
    PharmacyNetwork::class,
    ["id"],
    ["pnId"],
    CASCADE,
    NO_ACTION)])
data class Pharmacy(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var number: Int = 0,
    var address: String="",
    var workingWeekDay: String = "",
    var workingStartTime: String = "",
    var workingEndTime: String = "",
    var rating: Double = 0.0,
    var isMedicineDeliver: Boolean = false,
    var paymentOption: String = "",
    var pnId: UUID
) {
    companion object {
        const val TABLE_NAME = "pharmacy"
    }
}
