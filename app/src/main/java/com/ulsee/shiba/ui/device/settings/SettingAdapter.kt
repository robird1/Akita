package com.ulsee.shiba.ui.device.settings

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.shiba.R
import com.ulsee.shiba.data.response.FaceUIConfig
import com.ulsee.shiba.ui.device.settings.SettingAttributes.Companion.ITEM_COUNT

private val TAG = SettingAdapter::class.java.simpleName

class SettingAdapter(private val viewModel: SettingViewModel, private val fragment: SettingFragment, private val url: String) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    private var config: FaceUIConfig? = null

    fun setConfig(data: FaceUIConfig) {
        Log.d(TAG, "[Enter] setConfig")
        config = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = ITEM_COUNT

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_device_settings, parent, false)
        return ViewHolder(view, fragment)
    }

    inner class ViewHolder(itemView: View, private val fragment: SettingFragment) : RecyclerView.ViewHolder(itemView) {
//        private val configName = itemView.findViewById<TextView>(R.id.config_name)
//        private val configValue = itemView.findViewById<TextView>(R.id.config_value)
//
        init {
            itemView.setOnClickListener {
                AttributeInstance(bindingAdapterPosition, config).onClick(fragment, url)
            }
        }

        fun bind(position: Int) {
            AttributeInstance(position, config).setValue(itemView)
        }
    }

}


private class AttributeInstance(position: Int, config: FaceUIConfig?) {
    val instance = when(position) {
        SettingAttributes.POSITION_WIFI -> WiFi(config)
        SettingAttributes.POSITION_VOLUME -> Volume(config)
        SettingAttributes.POSITION_LANGUAGE -> Language(config)
        SettingAttributes.POSITION_TEMPERATURE -> Temperature(config)
        SettingAttributes.POSITION_PANEL_UI -> PanelUI(config)
        SettingAttributes.POSITION_LIGHT_MODE -> LightMode(config)
        SettingAttributes.POSITION_CAPTURE -> Capture(config)
        SettingAttributes.POSITION_TIME -> Time(config)
        SettingAttributes.POSITION_OTHERS -> Others(config)
        else -> Unknown(config)
    }

    fun setValue(itemView: View) { instance.setValue(itemView) }
    fun onClick(fragment: Fragment, url: String) { instance.onClick(fragment, url)}
}


abstract class SettingAttributes(val config: FaceUIConfig?) {
    var configName: TextView? = null
    var configValue: TextView? = null

    open fun setValue(itemView: View) {
        configName = itemView.findViewById(R.id.config_name)
        configValue = itemView.findViewById(R.id.config_value)
    }

    abstract fun onClick(fragment: Fragment, url: String)

    companion object {
        const val ITEM_COUNT = 9
        const val POSITION_WIFI = 0
        const val POSITION_VOLUME = 1
        const val POSITION_LANGUAGE = 2
        const val POSITION_TEMPERATURE = 3
        const val POSITION_PANEL_UI = 4
        const val POSITION_LIGHT_MODE = 5
        const val POSITION_CAPTURE = 6
        const val POSITION_TIME = 7
        const val POSITION_OTHERS = 8
    }
}


class WiFi(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Wi-Fi"
        configValue?.text = ""
    }

    override fun onClick(fragment: Fragment, url: String) {
        val action = SettingFragmentDirections.actionToConnectMode(url)
        fragment.findNavController().navigate(action)
    }
}


class Volume(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Volume"
        configValue?.text = if (config?.volume == 0) "Off" else "On"
    }

    override fun onClick(fragment: Fragment, url: String) {
        if (config != null) {
            val action = SettingFragmentDirections.actionToVolumeConfig(config.volume, url)
            fragment.findNavController().navigate(action)
        }
    }
}


class Language(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Language"
        configValue?.text = config?.language ?: ""
    }

    override fun onClick(fragment: Fragment, url: String) {
//        Log.d(TAG, "[Enter] onClick() url: $url")
        if (config != null) {
            val action = SettingFragmentDirections.actionToLanguageConfig(url, config.language)
            fragment.findNavController().navigate(action)
        }
    }
}


class Temperature(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Body Temperature"
        var value = ""
        if (config != null) {
            value = "${String.format("%.1f", config.minBodyTemperature)} - ${String.format("%.1f", config.maxBodyTemperature)} °${config.temperatureUnit}"
        }
        configValue?.text = value
    }

    override fun onClick(fragment: Fragment, url: String) {
        if (config != null) {
            val args = TemperatureData(
                config.temperatureUnit, config.maxBodyTemperature, config.minBodyTemperature,
                config.offsetBodyTemperature, url
            )
            val action = SettingFragmentDirections.actionToTemperatureConfig(args)
            fragment.findNavController().navigate(action)
        }
    }
}


class PanelUI(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Panel UI"
        configValue?.text = ""
    }

    override fun onClick(fragment: Fragment, url: String) {
        if (config != null) {
            val args = PanelUIData(
                config.show_ip, config.show_mac_address, config.show_frame,
                config.show_recognize_area,
                config.show_body_temperature, url
            )
            val action = SettingFragmentDirections.actionToPanelUiConfig(args)
            fragment.findNavController().navigate(action)
        }
    }
}


class LightMode(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Light Mode / LCD Mode"
//        configValue?.text = config?.lightMode.toString() ?: ""
        configValue?.text = ""
    }

    override fun onClick(fragment: Fragment, url: String) {
        if (config != null) {
            val args = LightModeData(config.lightMode, config.lcdMode, url)
            val action = SettingFragmentDirections.actionToLightConfig(args)
            fragment.findNavController().navigate(action)
        }
    }
}

class Capture(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Capture Attributes"
        configValue?.text = ""
    }

    override fun onClick(fragment: Fragment, url: String) {
        val action = SettingFragmentDirections.actionToCaptureConfig(url)
        fragment.findNavController().navigate(action)
    }
}

class Time(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Time"
        configValue?.text = ""
    }

    override fun onClick(fragment: Fragment, url: String) {
        val action = SettingFragmentDirections.actionToTimeSync(url)
        fragment.findNavController().navigate(action)
    }
}


class Others(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "Others"
        configValue?.text = ""
    }

    override fun onClick(fragment: Fragment, url: String) {
        if (config != null) {
            val args = OthersConfigData(config.enableSingleWarning, config.enableLiveness, url)
            val action = SettingFragmentDirections.actionToOthersConfig(args)
            fragment.findNavController().navigate(action)
        }
    }
}


class Unknown(config: FaceUIConfig?): SettingAttributes(config) {
    override fun setValue(itemView: View) {
        super.setValue(itemView)
        configName?.text = "??"
        configValue?.text = "??"
    }

    override fun onClick(fragment: Fragment, url: String) {

    }
}
