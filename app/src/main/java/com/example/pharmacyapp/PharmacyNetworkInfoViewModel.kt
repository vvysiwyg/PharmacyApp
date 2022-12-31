package com.example.pharmacyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.pharmacyapp.data.PharmacyNetwork
import com.example.pharmacyapp.repository.PharmacyRepository
import java.util.*

class PharmacyNetworkInfoViewModel : ViewModel() {
    private val pharmacyRepository = PharmacyRepository.get()
    private val pharmacyNetworkIdLiveData = MutableLiveData<UUID>()
    var pharmacyNetworkLiveData: LiveData<PharmacyNetwork?> =
        Transformations.switchMap(pharmacyNetworkIdLiveData) { pharmacyNetworkId ->
            pharmacyRepository.getPharmacyNetwork(pharmacyNetworkId)
        }

    fun loadPharmacyNetwork(pharmacyNetworkId: UUID){
        pharmacyNetworkIdLiveData.value = pharmacyNetworkId
    }

    fun newPharmacyNetwork(pharmacyNetwork: PharmacyNetwork) {
        pharmacyRepository.addPharmacyNetwork(pharmacyNetwork)
    }

    fun savePharmacyNetwork(pharmacyNetwork: PharmacyNetwork) {
        pharmacyRepository.updatePharmacyNetwork(pharmacyNetwork)
    }

    fun deletePharmacyNetwork(pharmacyNetwork: PharmacyNetwork) {
        pharmacyRepository.deletePharmacyNetwork(pharmacyNetwork)
    }
}