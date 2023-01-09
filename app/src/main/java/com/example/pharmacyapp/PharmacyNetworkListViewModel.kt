package com.example.pharmacyapp

import androidx.lifecycle.ViewModel
import com.example.pharmacyapp.repository.PharmacyRepository

class PharmacyNetworkListViewModel : ViewModel() {
    private val pharmacyRepository = PharmacyRepository.get()
    val pharmacyNetworkListLiveData = pharmacyRepository.getPharmacyNetworks()
}