package data.setting.impl

import data.setting.SettingManager

class SettingManagerImpl : SettingManager {
    private var token: String? = null
    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? {
        return token
    }
}