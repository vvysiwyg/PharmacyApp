package com.example.pharmacyapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pharmacyapp.data.Pharmacy
import java.util.*

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM pharmacy WHERE pnId = (:id)")
    fun getPharmacies(id: UUID): LiveData<List<Pharmacy>>
    @Query("SELECT * FROM pharmacy WHERE id = (:id)")
    fun getPharmacy(id: UUID): LiveData<Pharmacy?>
    @Query("SELECT id FROM pharmacy")
    fun getPharmacyIds(): LiveData<List<UUID>?>
    @Update
    fun updatePharmacy(pharmacy: Pharmacy)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPharmacy(pharmacy: Pharmacy)
    @Delete
    fun deletePharmacy(pharmacy: Pharmacy)
}