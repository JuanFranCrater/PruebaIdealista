package com.juanfbenitez.prueba.idealista.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyDetailDTO(
    @Json(name = "adid") val adid: Int,
    @Json(name = "price") val price: Double,
    @Json(name = "priceInfo") val priceInfo: PriceAmountDTO,
    @Json(name = "operation") val operation: String,
    @Json(name = "propertyType") val propertyType: String,
    @Json(name = "extendedPropertyType") val extendedPropertyType: String?,
    @Json(name = "homeType") val homeType: String?,
    @Json(name = "state") val state: String?,
    @Json(name = "multimedia") val multimedia: MultimediaDTO?,
    @Json(name = "propertyComment") val propertyComment: String?,
    @Json(name = "ubication") val ubication: UbicationDTO?,
    @Json(name = "country") val country: String?,
    @Json(name = "moreCharacteristics") val moreCharacteristics: MoreCharacteristicsDTO?,
    @Json(name = "energyCertification") val energyCertification: EnergyCertificationDTO?
)

@JsonClass(generateAdapter = true)
data class UbicationDTO(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double
)

@JsonClass(generateAdapter = true)
data class MoreCharacteristicsDTO(
    @Json(name = "communityCosts") val communityCosts: Double?,
    @Json(name = "roomNumber") val roomNumber: Int?,
    @Json(name = "bathNumber") val bathNumber: Int?,
    @Json(name = "exterior") val exterior: Boolean?,
    @Json(name = "housingFurnitures") val housingFurnitures: String?,
    @Json(name = "agencyIsABank") val agencyIsABank: Boolean?,
    @Json(name = "energyCertificationType") val energyCertificationType: String?,
    @Json(name = "flatLocation") val flatLocation: String?,
    @Json(name = "modificationDate") val modificationDate: Long?,
    @Json(name = "constructedArea") val constructedArea: Int?,
    @Json(name = "lift") val lift: Boolean?,
    @Json(name = "boxroom") val boxroom: Boolean?,
    @Json(name = "isDuplex") val isDuplex: Boolean?,
    @Json(name = "floor") val floor: String?,
    @Json(name = "status") val status: String?
)

@JsonClass(generateAdapter = true)
data class EnergyCertificationDTO(
    @Json(name = "title") val title: String?,
    @Json(name = "energyConsumption") val energyConsumption: EnergyDetailDTO?,
    @Json(name = "emissions") val emissions: EnergyDetailDTO?
)

@JsonClass(generateAdapter = true)
data class EnergyDetailDTO(
    @Json(name = "type") val type: String?
)
