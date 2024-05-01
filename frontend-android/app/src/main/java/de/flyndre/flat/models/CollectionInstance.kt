package de.flyndre.flat.models

import de.flyndre.flat.helper.UUIDSerializer
import io.github.dellisd.spatialk.geojson.MultiPolygon
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CollectionInstance(

    var name:String,
    @Serializable(with = UUIDSerializer::class)
    var clientId:UUID,
    var area: MultiPolygon,
    @Serializable(with = UUIDSerializer::class)
    var id: UUID?=null,
    var divisions: ArrayList<CollectionArea> = arrayListOf(),
    var confirmedUsers: ArrayList<UserModel> = arrayListOf(),
    var requestedAccess: ArrayList<UserModel> = arrayListOf()
) {


}
