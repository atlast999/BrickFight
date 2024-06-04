package data.setting

interface SettingManager {
    fun saveToken(token: String)
    fun getToken(): String?

}