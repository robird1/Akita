package com.ulsee.shiba.data.response

data class getUIConfig(
    val command: Int,
    val `data`: Data2,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class Data2(
    val FaceUIConfig: FaceUIConfig
)

data class FaceUIConfig(
    val alarmMaskRes: AlarmMaskRes,
    val alarmTempRes: AlarmTempRes,
    val announcement: String,
    val authPassRes: AuthPassRes,
    val display_content: String,
    val display_mode: String,
    val display_style: Int,
    val enableLiveness: Boolean,
    val enableOpenDoorByGuest: Boolean,
    val enableOpenDoorByMask: Boolean,
    val enableOpenDoorByTemp: Boolean,
    val enableSingleWarning: Boolean,
    val enableStrangerWarning: Boolean,
    val language: String,
    val lcdMode: Int,
    val lightMode: Int,
    val livenessRes: LivenessRes,
    val logo_file_md5: String,
    val logo_file_name: String,
    val logo_file_path: String,
    val logo_file_size: Int,
    val logo_file_url: String,
    val logo_file_uuid: String,
    val maxBodyTemperature: Double,
    val minBodyTemperature: Double,
    val modeEmulateTemperature: String,
    val offsetBodyTemperature: Double,
    val offsetEnvTemperature: Int,
    val show_body_temperature: Boolean,
    val show_frame: Boolean,
    val show_ip: Boolean,
    val show_mac_address: Boolean,
    val show_people_count: Boolean,
    val show_recognize_area: Boolean,
    val show_recognize_result: Boolean,
    val startupLogoRes: StartupLogoRes,
    val strangerWarningRes: StrangerWarningRes,
    val temperatureUnit: String,
    val voice_content: String,
    val voice_file_md5: String,
    val voice_file_name: String,
    val voice_file_size: Int,
    val voice_file_url: String,
    val voice_file_uuid: String,
    val voice_mode: String,
    val voice_sex: String,
    val volume: Int
)

data class AlarmMaskRes(
    val audioPath: String,
    val audioType: String,
    val imagePath: String,
    val imageType: String,
    val switch: Boolean
)

data class AlarmTempRes(
    val audioPath: String,
    val audioType: String,
    val imagePath: String,
    val imageType: String,
    val switch: Boolean
)

data class AuthPassRes(
    val audioPath: String,
    val audioType: String,
    val imagePath: String,
    val imageType: String,
    val switch: Boolean
)

data class LivenessRes(
    val audioPath: String,
    val audioType: String,
    val imagePath: String,
    val imageType: String,
    val switch: Boolean
)

data class StartupLogoRes(
    val audioPath: String,
    val audioType: String,
    val imagePath: String,
    val imageType: String,
    val switch: Boolean
)

data class StrangerWarningRes(
    val audioPath: String,
    val audioType: String,
    val imagePath: String,
    val imageType: String,
    val switch: Boolean
)