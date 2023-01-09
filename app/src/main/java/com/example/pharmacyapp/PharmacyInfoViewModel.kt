package com.example.pharmacyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.repository.PharmacyRepository
import java.util.*

class PharmacyInfoViewModel : ViewModel() {
    private val pharmacyRepository = PharmacyRepository.get()
    private val pharmacyIdLiveData = MutableLiveData<UUID>()
    var pharmacyLiveData: LiveData<Pharmacy?> =
        Transformations.switchMap(pharmacyIdLiveData) { pharmacyId ->
            pharmacyRepository.getPharmacy(pharmacyId)
        }

    fun loadPharmacy(pharmacyId: UUID){
        pharmacyIdLiveData.value = pharmacyId
    }

    fun newPharmacy(pharmacy: Pharmacy) {
        pharmacyRepository.addPharmacy(pharmacy)
    }

    fun savePharmacy(pharmacy: Pharmacy) {
        pharmacyRepository.updatePharmacy(pharmacy)
    }

    fun deletePharmacy(pharmacy: Pharmacy) {
        pharmacyRepository.deletePharmacy(pharmacy)
    }

    fun getPharmacyIds(): LiveData<List<UUID>?> {
        return pharmacyRepository.getPharmacyIds()
    }
}