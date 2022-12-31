package com.example.pharmacyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.example.pharmacyapp.Consts.PHARMACY_NETWORK_LIST_FRAGMENT_TAG
import com.example.pharmacyapp.data.Pharmacy
import com.example.pharmacyapp.data.PharmacyNetwork
import com.example.pharmacyapp.repository.PharmacyRepository
import com.example.pharmacyapp.tcp_connection.TCPConnection
import java.util.*

class MainActivity : AppCompatActivity(),
    PharmacyInfoFragment.Callbacks,
    PharmacyListFragment.Callbacks,
    PharmacyNetworkInfoFragment.Callbacks,
    PharmacyNetworkListFragment.Callbacks {
    private val IP = "192.168.43.165"
    private val Port = 1123
    lateinit var conn: TCPConnection
    private var startTime: Long = 0
    var fragment_name: String = "PharmacyNetworkListFragment"
    var sortType: Int = 0
    lateinit var pharmacyNetworkId: UUID
    private var miAdd: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showDBPharmacyNetworks()
        val callback: OnBackPressedCallback =
            object: OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    checkLogout()
                }
            }

        onBackPressedDispatcher.addCallback(this, callback)

        startTime = System.currentTimeMillis()
        conn = TCPConnection(IP, Port, startTime, this@MainActivity)
    }

    private fun checkLogout(){
        AlertDialog.Builder(this)
            .setTitle("Выход")
            .setMessage("Вы хотите выйти ?")
            .setPositiveButton("Да")
            {
                    _, _ -> finish()
            }
            .setNegativeButton("Нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        miAdd=menu?.findItem(R.id.miAdd)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.miAdd -> {
                if(fragment_name == "PharmacyNetworkListFragment"){
                    showPharmacyNetworkDetailDB(PharmacyNetwork().id)
                }
                else if(fragment_name == "PharmacyListFragment"){
                    showPharmacyDetailDB(Pharmacy(pnId = pharmacyNetworkId).id, pharmacyNetworkId)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPharmacyNetworkSelected(pharmacyNetworkId: UUID) {
        showPharmacyNetworkDetailDB(pharmacyNetworkId)
    }

    private fun showPharmacyNetworkDetailDB(pharmacyNetworkId: UUID){
        val fragment = PharmacyNetworkInfoFragment.newInstance(pharmacyNetworkId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }

    override fun showDBPharmacyNetworks(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, PharmacyNetworkListFragment.getInstance(), PHARMACY_NETWORK_LIST_FRAGMENT_TAG)
            .commit()
    }

    override fun showDBPharmacies(pharmacyNetworkId: UUID) {
        val fragment = PharmacyListFragment.newInstance(pharmacyNetworkId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }

    override fun onPharmacySelected(pharmacyId: UUID, pharmacyNetworkId: UUID) {
        showPharmacyDetailDB(pharmacyId, pharmacyNetworkId)
    }

    private fun showPharmacyDetailDB(pharmacyId: UUID, pharmacyNetworkId: UUID){
        val fragment = PharmacyInfoFragment.newInstance(pharmacyId, pharmacyNetworkId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }
}