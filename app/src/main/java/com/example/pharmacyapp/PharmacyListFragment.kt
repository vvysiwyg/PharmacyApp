package com.example.pharmacyapp

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyapp.Consts.PHARMACY_LIST_FRAGMENT_TAG
import com.example.pharmacyapp.data.Pharmacy
import java.util.*

class PharmacyListFragment : Fragment() {
    private lateinit var pharmacyListViewModel: PharmacyListViewModel
    private lateinit var pharmacyListRecyclerView: RecyclerView

    private var adapter: PharmacyListAdapter? = PharmacyListAdapter(emptyList())

    companion object {
        fun newInstance(pharmacyNetworkId: UUID): PharmacyListFragment {
            var args = Bundle().apply {
                putString(Consts.PHARMACY_NETWORK_ID_TAG, pharmacyNetworkId.toString())
            }
            return PharmacyListFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.pharmacy_list, container, false)
        pharmacyListRecyclerView=layoutView.findViewById(R.id.rvPharmacyList)
        pharmacyListRecyclerView.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        pharmacyListRecyclerView.adapter = adapter
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pnId = UUID.fromString(arguments?.getString(Consts.PHARMACY_NETWORK_ID_TAG))
        val reqAct = requireActivity() as MainActivity
        reqAct.pharmacyNetworkId = pnId
        pharmacyListViewModel = ViewModelProvider(this).get(PharmacyListViewModel::class.java)
        pharmacyListViewModel.getPharmacyListLiveData(pnId).observe(
            viewLifecycleOwner,
            Observer { pharmacies ->
                pharmacies?.let {
                    updateUI(pharmacies)
                    pharmacyListSort(reqAct.sortType, pharmacies)
                }
            })
    }

    private inner class PharmacyListAdapter(private val items: List<Pharmacy>)
        : RecyclerView.Adapter<PharmacyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyHolder {
            val view = layoutInflater.inflate(R.layout.pharmacy_list_element, parent, false)
            return PharmacyHolder(view)
        }

        override fun getItemCount():Int = items.size

        override fun onBindViewHolder(holder: PharmacyHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    private inner class PharmacyHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
        private lateinit var pharmacy: Pharmacy
        private val numTextView: TextView = itemView.findViewById(R.id.tvPharmacyNum)
        private val addressTextView: TextView = itemView.findViewById(R.id.tvPharmacyAddress)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(pharmacy: Pharmacy){
            this.pharmacy = pharmacy
            numTextView.text = pharmacy.number.toString()
            addressTextView.text = pharmacy.address
        }

        override fun onClick(v: View?) {
            Toast.makeText(requireActivity(), "График работы\nДень недели: ${pharmacy.workingWeekDay}\n" +
                    "Время начала: ${pharmacy.workingStartTime}\nВремя окончания: ${pharmacy.workingEndTime}",
            Toast.LENGTH_SHORT).show()
        }

        override fun onLongClick(view: View?): Boolean {
            callbacks?.onPharmacySelected(pharmacy.id, UUID.fromString(arguments?.getString(Consts.PHARMACY_NETWORK_ID_TAG)))
            return true
        }
    }

    private fun updateUI(pharmacies: List<Pharmacy>){
        pharmacyListRecyclerView.adapter = PharmacyListAdapter(pharmacies)
    }

    interface Callbacks {
        fun onPharmacySelected(pharmacyId: UUID, pharmacyNetworkId: UUID)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?

        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    closeFragment()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }

    private fun closeFragment(){
        val reqAct = requireActivity() as MainActivity
        reqAct.fragment_name = "PharmacyNetworkListFragment"
        reqAct.showDBPharmacyNetworks()
    }

    private fun pharmacyListSort(sortType: Int, pharmacies: List<Pharmacy>){
        updateUI(when(sortType) {
            1 -> pharmacies.sortedBy { it.number }
            2 -> pharmacies.sortedBy { it.address }
            3 -> pharmacies.sortedBy { it.workingWeekDay }
            4 -> pharmacies.sortedBy { it.workingStartTime }
            5 -> pharmacies.sortedBy { it.workingEndTime }
            6 -> pharmacies.sortedBy { it.rating }
            7 -> pharmacies.sortedBy { it.isMedicineDeliver }
            8 -> pharmacies.sortedBy { it.paymentOption }
            else -> pharmacies
        })
        Log.d(PHARMACY_LIST_FRAGMENT_TAG, sortType.toString())
    }
}