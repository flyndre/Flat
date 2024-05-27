package de.flyndre.flat.services

import de.flyndre.flat.interfaces.ISettingService
import java.util.UUID
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingService(val preferences: SharedPreferences) : ISettingService {
    private val _clientIdKey = "USERID"
    private val _usernameKey = "USERNAME"
    private val _lastCollectionsKey = "LASTCOLLECTIONS"
    private val _serverBasePath = "SERVERBASEPATH"
    private val _defaultServerBasePath = "https://flat.buhss.de"

    override fun getClientId(): UUID {
        val clientId = preferences.getString(_clientIdKey,"")
        if(clientId!=null&& clientId != ""){
            return UUID.fromString(clientId)
        }
        return resetClientId()
    }

    override fun resetClientId(): UUID {
        val clientId = UUID.randomUUID()
        preferences.edit {
            putString(_clientIdKey,UUID.randomUUID().toString())
        }
        return clientId
    }

    override fun getServerBaseUrl(): String {
        val basePath = preferences.getString(_serverBasePath,_defaultServerBasePath)
        if(basePath!=null){
            return basePath
        }
        return _defaultServerBasePath
    }

    override fun setServerBaseUrl(baseUrl: String) {
        preferences.edit {
            putString(_serverBasePath,baseUrl)
        }
    }

    override fun getDefaultUserName(): String {
        val userName = preferences.getString(_usernameKey,"")
        if(userName!=null){
            return userName
        }
        return ""
    }

    override fun setDefaultUserName(username: String) {
        preferences.edit {
            putString(_usernameKey,username)
        }
    }

    override fun getResentJoinLinks(): Set<String> {
        try{

            val links = preferences.getString(_lastCollectionsKey, "[]")
            if(links!=null){
                return Json.decodeFromString<Set<String>>(links)
            }
        }catch (_:Exception){
            preferences.edit {
                putString(_lastCollectionsKey,Json.encodeToString(setOf<String>())).commit()
            }
        }
        return setOf()
    }

    override fun addResentJoinLink(link: String):Set<String> {
        var linkSet = getResentJoinLinks()
        linkSet = linkSet+link
        if(linkSet.size>5){
            linkSet=linkSet-linkSet.first()
        }
        preferences.edit {
            putString(_lastCollectionsKey,Json.encodeToString(linkSet)).commit()
        }
        return linkSet
    }
}