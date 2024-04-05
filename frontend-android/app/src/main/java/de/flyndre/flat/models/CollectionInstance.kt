package de.flyndre.flat.models

import io.github.dellisd.spatialk.geojson.Polygon
import java.util.UUID

class CollectionInstance(
    name:String,
    id: UUID,
    area: Polygon,
    divisions: ArrayList<CollectionArea> = arrayListOf()
) {


}
