package com.example.pharmacyapp

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import com.example.pharmacyapp.Consts.PHARMACY_NETWORK_INFO_FRAGMENT_TAG
import com.example.pharmacyapp.data.PharmacyNetwork
import java.util.*

class PharmacyNetworkInfoFragment : Fragment() {

    private var pharmacyNetwork: PharmacyNetwork? = null
    private lateinit var pharmacyNetworkInfoViewModel: PharmacyNetworkInfoViewModel
    private lateinit var etPharmacyNetwork: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    companion object {
        fun newInstance(pharmacyNetworkId: UUID): PharmacyNetworkInfoFragment {
            var args = Bundle().apply {
                putString(Consts.PHARMACY_NETWORK_ID_TAG, pharmacyNetworkId.toString())
            }
            return PharmacyNetworkInfoFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        var pharmacyNetworkId: UUID = UUID.fromString(arguments?.getString(Consts.PHARMACY_NETWORK_ID_TAG))
        pharmacyNetworkInfoViewModel = ViewModelProvider(this).get(PharmacyNetworkInfoViewModel::class.java)
        pharmacyNetworkInfoViewModel.loadPharmacyNetwork(pharmacyNetworkId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pharmacy_network_info, container, false)
        etPharmacyNetwork=view.findViewById(R.id.etPharmacyNetwork)
        btnSave=view.findViewById(R.id.pnAddBtn)
        btnSave.setOnClickListener{
            if(pharmacyNetwork == null) {
                pharmacyNetwork = PharmacyNetwork()
                updatePharmacyNetwork()
                pharmacyNetworkInfoViewModel.newPharmacyNetwork(pharmacyNetwork!!)
            }
            else {
                updatePharmacyNetwork()
                pharmacyNetworkInfoViewModel.savePharmacyNetwork(pharmacyNetwork!!)
            }
            callbacks?.showDBPharmacyNetworks()
        }
        btnDelete=view.findViewById(R.id.pnDeleteBtn)
        btnDelete.setOnClickListener{
            if(pharmacyNetwork != null) {
                pharmacyNetworkInfoViewModel.deletePharmacyNetwork(pharmacyNetwork!!)
                callbacks?.showDBPharmacyNetworks()
            }
        }
        return view
    }

    private fun updatePharmacyNetwork(){
        pharmacyNetwork?.name = etPharmacyNetwork.text.toString()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pharmacyNetworkInfoViewModel.pharmacyNetworkLiveData.observe(viewLifecycleOwner){
                pharmacyNetwork -> pharmacyNetwork?.let {
            this.pharmacyNetwork = pharmacyNetwork
            updateUI()
        }
            btnDelete.isEnabled = pharmacyNetworkInfoViewModel.pharmacyNetworkLiveData.value != null
        }
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        callbacks = context as Callbacks?

        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    callbacks?.showDBPharmacyNetworks()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    fun updateUI(){
        etPharmacyNetwork.setText(pharmacyNetwork?.name)
    }

    interface Callbacks {
        fun showDBPharmacyNetworks()
    }

    private var callbacks: Callbacks? = null
}