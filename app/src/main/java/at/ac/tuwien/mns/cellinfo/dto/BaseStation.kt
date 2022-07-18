package at.ac.tuwien.mns.cellinfo.dto

class BaseStation {
    // Getter and Setter for all fields
    var mcc // Mobile Country Code
            = 0
    var mnc // Mobile Network Code
            = 0
    var lac // Location Area Code or TAC(Tracking Area Code) for LTE
            = 0
    var cid // Cell Identity
            = 0
    var arfcn // Absolute RF Channel Number (or UMTS Absolute RF Channel Number for WCDMA)
            = 0
    var bsic_psc_pci /* bsic for GSM, psc for WCDMA, pci for LTE,
                                   GSM has #getPsc() but always get Integer.MAX_VALUE,
                                   psc is undefined for GSM */ = 0
    var lon // Base station longitude
            = 0.0
    var lat // Base station latitude
            = 0.0
    var asuLevel /* Signal level as an asu value, asu is calculated based on 3GPP RSRP
                                   for GSM, between 0..31, 99 is unknown
                                   for WCDMA, between 0..31, 99 is unknown
                                   for LTE, between 0..97, 99 is unknown
                                   for CDMA, between 0..97, 99 is unknown */ = 0
    var signalLevel // Signal level as an int from 0..4
            = 0
    var dbm // Signal strength as dBm
            = 0
    var type // Signal type, GSM or WCDMA or LTE or CDMA
            : String? = null
    // Additional data requested from GP team
    var snir = 0 // Android fetch SNR (Signal to Noise Ratio) for GSM or WCDMA or LTE or CDMA. API level >= 26
    var rsrp = 0 // API > 26
    var throughput = "" // current download speed (player, during content download)
    var ca = 0 // ca (handset feature) - Carrier Aggregation
    var failureRate = 0f // ping, get packet loss (other than the main thread) calculate from data while ping
    var handsetModel = "" // get phone/ handset model
    var radioAccessType = "" // Radio Access Type or technology used for accessing the content i.e. 2G/3G/4G/5G


    override fun toString(): String {
        return "BaseStation{" +
                "mcc=" + mcc +
                ", mnc=" + mnc +
                ", lac=" + lac +
                ", cid=" + cid +
                ", arfcn=" + arfcn +
                ", bsic_psc_pci=" + bsic_psc_pci +
                ", lon=" + lon +
                ", lat=" + lat +
                ", asuLevel=" + asuLevel +
                ", signalLevel=" + signalLevel +
                ", dbm=" + dbm +
                ", type='" + type + '\'' +
                '}'
    }
}