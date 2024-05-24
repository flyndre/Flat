package de.flyndre.flat.interfaces

import java.util.UUID

interface ISettingService {
    fun getClientId(): UUID
    fun resetClientId():UUID

    fun getServerBaseUrl():String
    fun setServerBaseUrl(baseUrl:String)

    fun getDefaultUserName():String
    fun setDefaultUserName(username:String)

    fun getResentJoinLinks():Set<String>
    fun addResentJoinLink(link:String):Set<String>
}