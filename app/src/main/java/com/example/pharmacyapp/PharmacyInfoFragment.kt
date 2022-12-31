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
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.example.pharmacyapp.Consts.PHARMACY_ID_TAG
import com.example.pharmacyapp.Consts.PHARMACY_NETWORK_ID_TAG
import com.example.pharmacyapp.data.Pharmacy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

class PharmacyInfoFragment : Fragment() {

    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()

    private var pharmacy: Pharmacy? = null
    private lateinit var pharmacyInfoViewModel: PharmacyInfoViewModel

    private lateinit var tvNum: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvWorkingWeekday: TextView
    private lateinit var tvWorkingStartTime: TextView
    private lateinit var tvWorkingEndTime: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvIsMedicineDeliver: TextView
    private lateinit var tvPaymentOption: TextView

    private lateinit var etNum: EditText
    private lateinit var etAddress: EditText
    private lateinit var etWorkingWeekday: EditText
    private lateinit var etWorkingStartTime: EditText
    private lateinit var etWorkingEndTime: EditText
    private lateinit var etRating: EditText
    private lateinit var etIsMedicineDeliver: EditText
    private lateinit var etPaymentOption: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    companion object {
        fun newInstance(pharmacyId: UUID, pharmacyNetworkId: UUID): PharmacyInfoFragment {
            var args = Bundle().apply {
                putString(PHARMACY_ID_TAG, pharmacyId.toString())
                putString(PHARMACY_NETWORK_ID_TAG, pharmacyNetworkId.toString())
            }
            return PharmacyInfoFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        var pharmacyId: UUID = UUID.fromString(arguments?.getString(PHARMACY_ID_TAG))
        pharmacyInfoViewModel = ViewModelProvider(this).get(PharmacyInfoViewModel::class.java)
        pharmacyInfoViewModel.loadPharmacy(pharmacyId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pharmacy_info, container, false)
        val pharmacyNetworkId = UUID.fromString(arguments?.getString(PHARMACY_NETWORK_ID_TAG))

        etNum=view.findViewById(R.id.etNum)
        etAddress=view.findViewById(R.id.etAddress)
        etWorkingWeekday=view.findViewById(R.id.etWorkingWeekday)
        etWorkingStartTime=view.findViewById(R.id.etWorkingStartTime)
        etWorkingEndTime=view.findViewById(R.id.etWorkingEndTime)
        etRating=view.findViewById(R.id.etRating)
        etIsMedicineDeliver=view.findViewById(R.id.etIsMedicineDeliver)
        etPaymentOption=view.findViewById(R.id.etPaymentOption)

        tvNum=view.findViewById(R.id.tvNum)
        tvAddress=view.findViewById(R.id.tvAddress)
        tvWorkingWeekday=view.findViewById(R.id.tvWorkingWeekday)
        tvWorkingStartTime=view.findViewById(R.id.tvWorkingStartTime)
        tvWorkingEndTime=view.findViewById(R.id.tvWorkingEndTime)
        tvRating=view.findViewById(R.id.tvRating)
        tvIsMedicineDeliver=view.findViewById(R.id.tvIsMedicineDeliver)
        tvPaymentOption=view.findViewById(R.id.tvPaymentOption)

        tvNum.setOnLongClickListener { setSortType(1) }
        tvAddress.setOnLongClickListener { setSortType(2) }
        tvWorkingWeekday.setOnLongClickListener { setSortType(3) }
        tvWorkingStartTime.setOnLongClickListener { setSortType(4) }
        tvWorkingEndTime.setOnLongClickListener { setSortType(5) }
        tvRating.setOnLongClickListener { setSortType(6) }
        tvIsMedicineDeliver.setOnLongClickListener { setSortType(7) }
        tvPaymentOption.setOnLongClickListener { setSortType(8) }

        btnSave=view.findViewById(R.id.addBtn)
        btnSave.setOnClickListener{ btnSaveClick(pharmacyNetworkId) }
        btnDelete=view.findViewById(R.id.deleteBtn)
        btnDelete.setOnClickListener{ btnDeleteClick(pharmacyNetworkId) }
        return view
    }

    private fun updatePharmacy(){
        pharmacy?.number = etNum.text.toString().toInt()
        pharmacy?.address = etAddress.text.toString()
        pharmacy?.workingWeekDay = etWorkingWeekday.text.toString()
        pharmacy?.workingStartTime = etWorkingStartTime.text.toString()
        pharmacy?.workingEndTime = etWorkingEndTime.text.toString()
        pharmacy?.rating = etRating.text.toString().toDouble()
        pharmacy?.isMedicineDeliver = etIsMedicineDeliver.text.toString().toBoolean()
        pharmacy?.paymentOption = etPaymentOption.text.toString()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pharmacyInfoViewModel.pharmacyLiveData.observe(viewLifecycleOwner){
                pharmacy -> pharmacy?.let {
            this.pharmacy = pharmacy
            updateUI()
        }
            btnDelete.isEnabled = pharmacyInfoViewModel.pharmacyLiveData.value != null
        }
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        callbacks = context as Callbacks?

        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    callbacks?.showDBPharmacies(UUID.fromString(arguments?.getString(PHARMACY_NETWORK_ID_TAG)))
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
        etNum.setText(pharmacy?.number.toString())
        etAddress.setText(pharmacy?.address)
        etWorkingWeekday.setText(pharmacy?.workingWeekDay)
        etWorkingStartTime.setText(pharmacy?.workingStartTime)
        etWorkingEndTime.setText(pharmacy?.workingEndTime)
        etRating.setText(pharmacy?.rating.toString())
        etIsMedicineDeliver.setText(pharmacy?.isMedicineDeliver.toString())
        etPaymentOption.setText(pharmacy?.paymentOption)
    }

    private fun setSortType(sortType: Int): Boolean{
        val reqAct = requireActivity() as MainActivity
        reqAct.sortType = sortType
        return true
    }

    interface Callbacks {
        fun showDBPharmacies(pnId: UUID)
    }

    private var callbacks: Callbacks? = null

    private fun btnSaveClick(pharmacyNetworkId: UUID){
        val reqAct = requireActivity() as MainActivity

        if(pharmacy == null) {
            pharmacy = Pharmacy(pnId = pharmacyNetworkId)
            updatePharmacy()
            pharmacyInfoViewModel.newPharmacy(pharmacy!!)
            reqAct.conn.sendDataToServer("a1&$pharmacyNetworkId&${gson.toJson(pharmacy!!)}")
        }
        else {
            updatePharmacy()
            pharmacyInfoViewModel.savePharmacy(pharmacy!!)
            reqAct.conn.sendDataToServer("e1&$pharmacyNetworkId&${gson.toJson(pharmacy!!)}")
        }
        callbacks?.showDBPharmacies(pharmacyNetworkId)
    }

    private fun btnDeleteClick(pharmacyNetworkId: UUID){
        val reqAct = requireActivity() as MainActivity

        if(pharmacy != null){
            pharmacyInfoViewModel.deletePharmacy(pharmacy!!)
            reqAct.conn.sendDataToServer("d1&$pharmacyNetworkId&${pharmacy!!.id}")
            callbacks?.showDBPharmacies(pharmacyNetworkId)
        }
    }
}