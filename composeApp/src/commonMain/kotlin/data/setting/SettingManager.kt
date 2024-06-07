package data.setting

interface SettingManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserId(userId: Int)
    fun getUserId(): Int?
}