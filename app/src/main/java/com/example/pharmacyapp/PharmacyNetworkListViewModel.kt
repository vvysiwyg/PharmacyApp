package com.example.pharmacyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.data.PharmacyNetwork
import com.example.pharmacyapp.repository.PharmacyRepository

class PharmacyNetworkListViewModel : ViewModel() {
    private val pharmacyRepository = PharmacyRepository.get()
    val pharmacyNetworkListLiveData = pharmacyRepository.getPharmacyNetworks()
    var joinedData = pharmacyRepository.getJoinData()
}