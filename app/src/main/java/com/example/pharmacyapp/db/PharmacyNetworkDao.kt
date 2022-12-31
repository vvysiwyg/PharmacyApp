package com.example.pharmacyapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.data.PharmacyNetwork
import java.util.*

@Dao
interface PharmacyNetworkDao {
    @Query("SELECT * FROM pharmacy_network")
    fun getPharmacyNetworks(): LiveData<List<PharmacyNetwork>>
    @Query("SELECT * FROM pharmacy_network WHERE id = (:id)")
    fun getPharmacyNetwork(id: UUID): LiveData<PharmacyNetwork?>
    @Query("SELECT * FROM pharmacy_network JOIN pharmacy ON pharmacy_network.id = pharmacy.pnId")
    fun getJoinData(): LiveData<Map<PharmacyNetwork, List<Pharmacy?>>>
    @Query("DELETE FROM pharmacy_network")
    fun deleteAllData()

    @Update
    fun updatePharmacyNetwork(pharmacyNetwork: PharmacyNetwork)
    @Insert
    fun addPharmacyNetwork(pharmacyNetwork: PharmacyNetwork)
    @Delete
    fun deletePharmacyNetwork(pharmacyNetwork: PharmacyNetwork)
}