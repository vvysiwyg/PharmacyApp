package com.example.pharmacyapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.data.PharmacyNetwork
import com.example.pharmacyapp.db.PharmacyDB
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "pharmacyDB"

class PharmacyRepository private constructor(context: Context){
    companion object {
        private var INSTANCE: PharmacyRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = PharmacyRepository(context)
            }
        }

        fun get(): PharmacyRepository {
            return INSTANCE ?: throw IllegalStateException("PharmacyRepository не инициализирована")
        }
    }

    private val db: PharmacyDB = Room.databaseBuilder(
        context.applicationContext,
        PharmacyDB::class.java,
        DATABASE_NAME
    ).build()

    private val pharmacyDao = db.pharmacyDao()
    private val pharmacyNetworkDao = db.pharmacyNetworkDao()

    fun getPharmacies(pnId: UUID): LiveData<List<Pharmacy>> = pharmacyDao.getPharmacies(pnId)
    fun getPharmacy(id: UUID): LiveData<Pharmacy?> = pharmacyDao.getPharmacy(id)
    fun getPharmacyIds(): LiveData<List<UUID>?> = pharmacyDao.getPharmacyIds()

    fun getPharmacyNetworks(): LiveData<List<PharmacyNetwork>> = pharmacyNetworkDao.getPharmacyNetworks()
    fun getPharmacyNetwork(id: UUID): LiveData<PharmacyNetwork?> = pharmacyNetworkDao.getPharmacyNetwork(id)
    fun getPharmacyNetworkIds(): LiveData<List<UUID>?> = pharmacyNetworkDao.getPharmacyNetworkIds()

    fun deleteAllData() = pharmacyNetworkDao.deleteAllData()

    private val executor = Executors.newSingleThreadExecutor()

    fun updatePharmacy(pharmacy: Pharmacy){
        executor.execute{
            pharmacyDao.updatePharmacy(pharmacy)
        }
    }

    fun addPharmacy(pharmacy: Pharmacy){
        executor.execute{
            pharmacyDao.addPharmacy(pharmacy)
        }
    }

    fun deletePharmacy(pharmacy: Pharmacy){
        executor.execute {
            pharmacyDao.deletePharmacy(pharmacy)
        }
    }

    fun updatePharmacyNetwork(pharmacyNetwork: PharmacyNetwork){
        executor.execute{
            pharmacyNetworkDao.updatePharmacyNetwork(pharmacyNetwork)
        }
    }

    fun addPharmacyNetwork(pharmacyNetwork: PharmacyNetwork){
        executor.execute{
            pharmacyNetworkDao.addPharmacyNetwork(pharmacyNetwork)
        }
    }

    fun deletePharmacyNetwork(pharmacyNetwork: PharmacyNetwork){
        executor.execute {
            pharmacyNetworkDao.deletePharmacyNetwork(pharmacyNetwork)
        }
    }
}