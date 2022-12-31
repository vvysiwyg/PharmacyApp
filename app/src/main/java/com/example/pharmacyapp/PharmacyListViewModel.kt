package com.example.pharmacyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.repository.PharmacyRepository
import java.util.*

class PharmacyListViewModel : ViewModel() {
    private val pharmacyRepository = PharmacyRepository.get()
    fun getPharmacyListLiveData(pnId: UUID): LiveData<List<Pharmacy>> = pharmacyRepository.getPharmacies(pnId)
}