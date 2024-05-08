package de.flyndre.flat.models

import kotlinx.serialization.Serializable

@Serializable
class RequestAccessResult(
    val accepted: Boolean,
    val collection: CollectionInstance?
) {

}
