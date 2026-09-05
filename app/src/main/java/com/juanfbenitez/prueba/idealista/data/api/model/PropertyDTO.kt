package com.juanfbenitez.prueba.idealista.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyDTO(
    @Json(name = "propertyCode") val propertyCode: String,
    @Json(name = "thumbnail") val thumbnail: String?,
    @Json(name = "floor") val floor: String?,
    @Json(name = "price") val price: Double,
    @Json(name = "priceInfo") val priceInfo: PriceInfoDTO,
    @Json(name = "propertyType") val propertyType: String,
    @Json(name = "operation") val operation: String,
    @Json(name = "size") val size: Double,
    @Json(name = "exterior") val exterior: Boolean,
    @Json(name = "rooms") val rooms: Int,
    @Json(name = "bathrooms") val bathrooms: Int,
    @Json(name = "address") val address: String,
    @Json(name = "province") val province: String,
    @Json(name = "municipality") val municipality: String,
    @Json(name = "district") val district: String,
    @Json(name = "country") val country: String,
    @Json(name = "neighborhood") val neighborhood: String?,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "description") val description: String?,
    @Json(name = "multimedia") val multimedia: MultimediaDTO?,
    @Json(name = "features") val features: FeaturesDTO?,
    @Json(name = "parkingSpace") val parkingSpace: ParkingSpaceDTO?
)

@JsonClass(generateAdapter = true)
data class PriceInfoDTO(
    @Json(name = "price") val price: PriceAmountDTO
)

@JsonClass(generateAdapter = true)
data class PriceAmountDTO(
    @Json(name = "amount") val amount: Double,
    @Json(name = "currencySuffix") val currencySuffix: String
)

@JsonClass(generateAdapter = true)
data class MultimediaDTO(
    @Json(name = "images") val images: List<ImageDTO>
)

@JsonClass(generateAdapter = true)
data class ImageDTO(
    @Json(name = "url") val url: String,
    @Json(name = "tag") val tag: String?,
    @Json(name = "localizedName") val localizedName: String? = null,
    @Json(name = "multimediaId") val multimediaId: Long? = null
)

@JsonClass(generateAdapter = true)
data class FeaturesDTO(
    @Json(name = "hasAirConditioning") val hasAirConditioning: Boolean?,
    @Json(name = "hasBoxRoom") val hasBoxRoom: Boolean?,
    @Json(name = "hasSwimmingPool") val hasSwimmingPool: Boolean?,
    @Json(name = "hasTerrace") val hasTerrace: Boolean?,
    @Json(name = "hasGarden") val hasGarden: Boolean?
)

@JsonClass(generateAdapter = true)
data class ParkingSpaceDTO(
    @Json(name = "hasParkingSpace") val hasParkingSpace: Boolean,
    @Json(name = "isParkingSpaceIncludedInPrice") val isParkingSpaceIncludedInPrice: Boolean
)
