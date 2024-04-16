package de.flyndre.flat.models

import de.flyndre.flat.helper.UUIDSerializer
import io.github.dellisd.spatialk.geojson.MultiPolygon
import io.github.dellisd.spatialk.geojson.Polygon
import kotlinx.serialization.Serializable
import java.util.UUID
@Serializable
data class CollectionArea(
    var area: MultiPolygon,
    @Serializable(with = UUIDSerializer::class)
    var id:UUID = UUID.randomUUID(),
    @Serializable(with = UUIDSerializer::class)
    var clientId:UUID?=null,
    var name:String
) {

}
