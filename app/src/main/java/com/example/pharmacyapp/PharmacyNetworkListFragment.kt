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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacyapp.Consts.PHARMACY_NETWORK_LIST_FRAGMENT_TAG
import com.example.pharmacyapp.data.PharmacyNetwork
import com.example.pharmacyapp.data.Template
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

class PharmacyNetworkListFragment : Fragment() {
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()
    private lateinit var pharmacyNetworkListViewModel: PharmacyNetworkListViewModel
    private lateinit var pharmacyNetworkListRecyclerView: RecyclerView

    private var adapter: PharmacyNetworkListAdapter? = PharmacyNetworkListAdapter(emptyList())

    companion object {
        private var INSTANCE: PharmacyNetworkListFragment? = null

        fun getInstance():PharmacyNetworkListFragment {
            if(INSTANCE == null){
                INSTANCE = PharmacyNetworkListFragment()
            }
            return INSTANCE?: throw IllegalStateException("Отображение списка не создано!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.pharmacy_network_list, container, false)
        pharmacyNetworkListRecyclerView=layoutView.findViewById(R.id.rvPharmacyNetworkList)
        pharmacyNetworkListRecyclerView.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        pharmacyNetworkListRecyclerView.adapter = adapter
        return layoutView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reqAct = requireActivity() as MainActivity
        pharmacyNetworkListViewModel = ViewModelProvider(this).get(PharmacyNetworkListViewModel::class.java)
        pharmacyNetworkListViewModel.pharmacyNetworkListLiveData.observe(
            viewLifecycleOwner,
            Observer { pharmacyNetwork ->
                pharmacyNetwork?.let {
                    updateUI(pharmacyNetwork)
                }
            })
        pharmacyNetworkListViewModel.joinedData.observe(
            viewLifecycleOwner,
            Observer { joinData ->
                joinData?.let {
                    Log.d(PHARMACY_NETWORK_LIST_FRAGMENT_TAG, joinData.toString())
                    val template = Template(joinData)
                    val dataToSend = gson.toJson(template)
                    reqAct.conn.sendDataToServer("a$dataToSend")
                }
            })
    }

    private inner class PharmacyNetworkListAdapter(private val items: List<PharmacyNetwork>)
        : RecyclerView.Adapter<PharmacyNetworkHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyNetworkHolder {
            val view = layoutInflater.inflate(R.layout.pharmacy_network_list_element, parent, false)
            return PharmacyNetworkHolder(view)
        }

        override fun getItemCount():Int = items.size

        override fun onBindViewHolder(holder: PharmacyNetworkHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    private inner class PharmacyNetworkHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
        private lateinit var pharmacyNetwork: PharmacyNetwork
        private val pnNameTextView: TextView = itemView.findViewById(R.id.tvPharmacyNetworkName)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(pharmacyNetwork: PharmacyNetwork){
            this.pharmacyNetwork = pharmacyNetwork
            pnNameTextView.text = pharmacyNetwork.name
        }

        override fun onClick(v: View?) {
            val reqAct = requireActivity() as MainActivity
            reqAct.fragment_name = "PharmacyListFragment"
            reqAct.showDBPharmacies(pharmacyNetwork.id)
        }

        override fun onLongClick(view: View?): Boolean {
            callbacks?.onPharmacyNetworkSelected(pharmacyNetwork.id)
            return true
        }
    }

    private fun updateUI(pharmacyNetwork: List<PharmacyNetwork>){
        if(pharmacyNetwork == null) return
        adapter=PharmacyNetworkListAdapter(pharmacyNetwork)
        pharmacyNetworkListRecyclerView.adapter = PharmacyNetworkListAdapter(pharmacyNetwork)
    }

    interface Callbacks {
        fun onPharmacyNetworkSelected(pharmacyNetworkId: UUID)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }

}