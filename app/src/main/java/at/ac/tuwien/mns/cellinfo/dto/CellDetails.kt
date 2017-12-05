package at.ac.tuwien.mns.cellinfo.dto

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by johannesvass on 02.12.17.
 */
class CellDetails(c: Cell) : Cell(c), ClusterItem {
    var lat: Double = Double.NaN
    var lon: Double = Double.NaN

    constructor() : this(Cell())

    override fun getPosition(): LatLng {
        return LatLng(lat, lon)
    }

    fun setLocation(latlon: LatLng) {
        lat = latlon.latitude
        lon = latlon.longitude
    }

    override fun toString(): String {
        return "CellDetails(radio='$radio' ,strength=${strength?.dbm}, lac=$lac, cid=$cid, mnc=$mcc, mnc=$mcc registered=$registered)"
    }
}
