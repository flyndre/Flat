package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.Polygon
import java.util.UUID

class CollectionInstance(
    var name:String,
    var id: UUID,
    var area: Polygon,
    var divisions: ArrayList<CollectionArea> = arrayListOf()
) {


}
