package at.ac.tuwien.mns.cellinfo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.telephony.*
import android.util.Log
import at.ac.tuwien.mns.cellinfo.dto.BaseStation
import at.ac.tuwien.mns.cellinfo.fragments.CellListViewFragment
import at.ac.tuwien.mns.cellinfo.fragments.CellMapFragment
import at.ac.tuwien.mns.cellinfo.service.CellInfoService
import at.ac.tuwien.mns.cellinfo.service.impl.CellInfoServiceImpl
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        cellInfoService = CellInfoServiceImpl(this, getString(R.string.opencellid_key))
        showCellinfo()
        fetchDeviceAndNetworkInfo()
    }

    private fun fetchDeviceAndNetworkInfo() {

    }

    // No settings menu needed at the moment
    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }*/


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val tabs: List<Fragment> = listOf(
                CellListViewFragment.newInstance(1),
                CellMapFragment.newInstance(2))

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return tabs[position]
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }
    }

    companion object {
        var cellInfoService: CellInfoService? = null
    }

    @SuppressLint("SetTextI18n")
    fun showCellinfo() {
        var cellInfoList: List<CellInfo?>? = null
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val telephonyManager: TelephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        cellInfoList = telephonyManager.allCellInfo
        //val ca: Int = telephonyManager.dataNetworkType
        if (cellInfoList?.size!! > 0) {
            val cellNumber = cellInfoList.size
            for (cellInfo in cellInfoList) {
                val bs: BaseStation? = bindData(cellInfo)
                Log.d("cellDetailsList-Main", "showCellinfo() called with ${bs.toString()}")
                //Log.i("cellDetailsList-Main", "CellNumber: $cellNumber \ncellinfo: ${Gson().toJson(cellInfo).toString()}")
            }
        }
    }
    private fun bindData(cellInfo: CellInfo): BaseStation? {
        var baseStation: BaseStation? = null
        //Network type：2G，3G，4G
        if (cellInfo is CellInfoWcdma) {
            //联通3G
            val cellInfoWcdma = cellInfo
            val cellIdentityWcdma = cellInfoWcdma.cellIdentity
            baseStation = BaseStation()
            baseStation.type = "WCDMA"
            baseStation.cid = cellIdentityWcdma.cid
            baseStation.lac = cellIdentityWcdma.lac
            baseStation.mcc = cellIdentityWcdma.mcc
            baseStation.mnc = cellIdentityWcdma.mnc
            baseStation.bsic_psc_pci = cellIdentityWcdma.psc
            if (cellInfoWcdma.cellSignalStrength != null) {
                baseStation.asuLevel =
                    cellInfoWcdma.cellSignalStrength.asuLevel //Get the signal level as an asu value between 0..31, 99 is unknown Asu is calculated based on 3GPP RSRP.
                baseStation.signalLevel =
                    cellInfoWcdma.cellSignalStrength.level //Get signal level as an int from 0..4
                baseStation.dbm =
                    cellInfoWcdma.cellSignalStrength.dbm //Get the signal strength as dBm
            }
        } else if (cellInfo is CellInfoLte) {
            //4G
            val cellInfoLte = cellInfo
            val cellIdentityLte = cellInfoLte.cellIdentity
            baseStation = BaseStation()
            // additional data
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                baseStation.snir = cellInfoLte.cellSignalStrength.rssnr
                baseStation.rsrp = cellInfoLte.cellSignalStrength.rsrp
            }
            baseStation.throughput = ""
            baseStation.ca = 0
            baseStation.failureRate = 1f
            baseStation.handsetModel = Build.MODEL
            baseStation.radioAccessType = "4G"

            baseStation.type = "LTE"
            baseStation.cid = cellIdentityLte.ci
            baseStation.mnc = cellIdentityLte.mnc
            baseStation.mcc = cellIdentityLte.mcc
            baseStation.lac = cellIdentityLte.tac
            baseStation.bsic_psc_pci = cellIdentityLte.pci
            if (cellInfoLte.cellSignalStrength != null) {
                baseStation.asuLevel = cellInfoLte.cellSignalStrength.asuLevel
                baseStation.signalLevel = cellInfoLte.cellSignalStrength.level
                baseStation.dbm = cellInfoLte.cellSignalStrength.dbm
            }
        } else if (cellInfo is CellInfoGsm) {
            //2G
            val cellInfoGsm = cellInfo
            val cellIdentityGsm = cellInfoGsm.cellIdentity
            baseStation = BaseStation()
            baseStation.type = "GSM"
            baseStation.cid = cellIdentityGsm.cid
            baseStation.lac = cellIdentityGsm.lac
            baseStation.mcc = cellIdentityGsm.mcc
            baseStation.mnc = cellIdentityGsm.mnc
            baseStation.bsic_psc_pci = cellIdentityGsm.psc
            if (cellInfoGsm.cellSignalStrength != null) {
                baseStation.asuLevel = cellInfoGsm.cellSignalStrength.asuLevel
                baseStation.signalLevel = cellInfoGsm.cellSignalStrength.level
                baseStation.dbm = cellInfoGsm.cellSignalStrength.dbm
            }
        } else {
            //电信2/3G
            Log.e("cellDetailsList-Main-2", "CDMA CellInfo................................................")
        }
        return baseStation
    }
}
