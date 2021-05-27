package com.ulsee.ulti_a100.model

import java.io.Serializable

open class WIFIInfo : Serializable {

    enum class WIFIType {
        wep, wpa, open, unknown
    }

    var wirelessMode = "ap"
    var inUsed = false
    var ssid = "ULSEE"
    var mode = "infra"
    var chan = "52"
    var rate = "540 Mbit/s"
    var signal = "100"
    var bars = "▂▄▆█"
    var security = "WPA2" // [WPA2] [--] [WPA1 WPA2]
    var bssid = ""

    // [WPA2-PSK-CCMP][ESS]
    // [WPA2-PSK-CCMP][ESS][WPS]
    // [ESS]
    var capabilities = "WPA2"
    var password : String? = null

    val passwordRequired: Boolean
        get() = !security.equals("--")

    val wifiType: WIFIType
        get() = when {
            capabilities.toLowerCase().contains("wpa") -> WIFIType.wpa
            capabilities.toLowerCase().contains("wep") -> WIFIType.wep
            capabilities.toLowerCase().equals("[ess]") -> WIFIType.open
            else -> WIFIType.unknown
//            security.contains("WPA") -> WIFIType.wpa
//            security.equals("--") -> WIFIType.open
//            else -> WIFIType.unknown
        }

    fun test () {
        val responseString =
                "IN-USE  SSID          MODE   CHAN  RATE        SIGNAL  BARS  SECURITY  \n" +
                "        --            Infra  52    540 Mbit/s  100     ▂▄▆█  WPA2      \n" +
                "        ULSEE         Infra  52    540 Mbit/s  100     ▂▄▆█  WPA2      \n" +
                "        ULSEE-GUESTS  Infra  52    540 Mbit/s  97      ▂▄▆█  --        \n" +
                "        ulseetest     Infra  3     135 Mbit/s  90      ▂▄▆█  WPA1 WPA2 \n" +
                "        ULSEE-GUESTS  Infra  36    540 Mbit/s  62      ▂▄▆_  --        \n" +
                "        ULSEE         Infra  36    540 Mbit/s  62      ▂▄▆_  WPA2      \n" +
                "        ULSEE-CEO     Infra  11    195 Mbit/s  57      ▂▄▆_  WPA2      \n" +
                "        ULSEE         Infra  11    130 Mbit/s  57      ▂▄▆_  WPA2      \n" +
                "        120-6F        Infra  4     270 Mbit/s  50      ▂▄__  WPA1 WPA2 \n" +
                "        angela02-2G   Infra  2     130 Mbit/s  45      ▂▄__  WPA2      \n" +
                "        CLRBOT-B88    Infra  8     130 Mbit/s  45      ▂▄__  WPA2      \n" +
                "        USD40B        Infra  11    195 Mbit/s  45      ▂▄__  WPA2      \n" +
                "        ULSEE         Infra  149   270 Mbit/s  45      ▂▄__  WPA2      \n" +
                "        ULSEE         Infra  36    540 Mbit/s  40      ▂▄__  WPA2      \n" +
                "        AMYHOME       Infra  9     270 Mbit/s  39      ▂▄__  WPA1 WPA2 \n" +
                "        ULSEE-GUESTS  Infra  36    540 Mbit/s  39      ▂▄__  --        \n" +
                "        USD40B        Infra  149   405 Mbit/s  39      ▂▄__  WPA2      \n" +
                "        Ritahome      Infra  8     130 Mbit/s  34      ▂▄__  WPA2      \n" +
                "        ULSEE-CEO     Infra  149   405 Mbit/s  34      ▂▄__  WPA2      \n" +
                "        AMYHOME       Infra  149   540 Mbit/s  32      ▂▄__  WPA1 WPA2 \n" +
                "        ASUS          Infra  6     65 Mbit/s   24      ▂___  WPA1 WPA2 \n" +
                "        ULSEE         Infra  100   540 Mbit/s  24      ▂___  WPA2      \n" +
                "        --            Infra  100   540 Mbit/s  24      ▂___  WPA2      \n" +
                "        ULSEE-GUESTS  Infra  100   540 Mbit/s  22      ▂___  --        "
    }
}