package de.flyndre.flat.models

import de.flyndre.flat.helper.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserModel(
    var username:String,
    @Serializable(with = UUIDSerializer::class)
    var clientId: UUID,
    var accepted: Boolean? = null
) {
}