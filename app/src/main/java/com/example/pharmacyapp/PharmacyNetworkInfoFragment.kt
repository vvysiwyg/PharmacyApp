package com.example.pharmacyapp

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import com.example.pharmacyapp.data.PharmacyNetwork
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

class PharmacyNetworkInfoFragment : Fragment() {

    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()
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
        val reqAct = requireActivity() as MainActivity
        reqAct.miAdd?.isVisible = false
        val view = inflater.inflate(R.layout.pharmacy_network_info, container, false)
        etPharmacyNetwork=view.findViewById(R.id.etPharmacyNetwork)
        btnSave=view.findViewById(R.id.pnAddBtn)
        btnSave.setOnClickListener{ btnSaveClick() }
        btnDelete=view.findViewById(R.id.pnDeleteBtn)
        btnDelete.setOnClickListener{ btnDeleteClick() }
        if(reqAct.connType == 1){
            btnSave.visibility = View.INVISIBLE
            btnDelete.visibility = View.INVISIBLE
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
        val reqAct = requireActivity() as MainActivity

        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    callbacks?.showDBPharmacyNetworks()
                    reqAct.miAdd?.isVisible = true
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

    private fun btnSaveClick(){
        if(!setEditTextError())
        {
            val reqAct = requireActivity() as MainActivity

            if (pharmacyNetwork == null) {
                pharmacyNetwork = PharmacyNetwork()
                updatePharmacyNetwork()
                pharmacyNetworkInfoViewModel.newPharmacyNetwork(pharmacyNetwork!!)
                reqAct.conn.sendDataToServer("a0&${gson.toJson(pharmacyNetwork!!)}")
            } else {
                updatePharmacyNetwork()
                pharmacyNetworkInfoViewModel.savePharmacyNetwork(pharmacyNetwork!!)
                reqAct.conn.sendDataToServer("e0&${gson.toJson(pharmacyNetwork!!)}")
            }
            reqAct.miAdd?.isVisible = true
            callbacks?.showDBPharmacyNetworks()
        }
    }

    private fun btnDeleteClick(){
        val reqAct = requireActivity() as MainActivity

        if(pharmacyNetwork != null) {
            pharmacyNetworkInfoViewModel.deletePharmacyNetwork(pharmacyNetwork!!)
            reqAct.conn.sendDataToServer("d0&${pharmacyNetwork!!.id}")
            reqAct.miAdd?.isVisible = true
            callbacks?.showDBPharmacyNetworks()
        }
    }

    private fun setEditTextError(): Boolean{
        var flag = false
        if(etPharmacyNetwork.text.isBlank()){
            etPharmacyNetwork.error = "Поле не может быть пустым"
            flag = true
        }
        return flag
    }
}