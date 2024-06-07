package data.setting.impl

import data.setting.SettingManager

class SettingManagerImpl : SettingManager {
    private var token: String? = null
    private var userId: Int? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? {
        return token
    }

    override fun saveUserId(userId: Int) {
        this.userId = userId
    }

    override fun getUserId(): Int? {
        return userId
    }
}