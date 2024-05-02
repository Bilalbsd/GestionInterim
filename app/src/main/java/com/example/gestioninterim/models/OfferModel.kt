package com.example.gestioninterim.models

import android.os.Parcel
import android.os.Parcelable

data class OfferModel(
    var jobTitle: String? = null,
    var jobDescription: String? = null,
    var jobTarget: String? = null,
    var salary: Double? = null,
    var period: String? = null,
    var location: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    val image: String? = null,
    var offerId: String? = null,
    val creatorId: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jobTitle)
        parcel.writeString(jobDescription)
        parcel.writeString(jobTarget)
        parcel.writeValue(salary)
        parcel.writeString(period)
        parcel.writeString(location)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(image)
        parcel.writeString(offerId)
        parcel.writeString(creatorId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OfferModel> {
        override fun createFromParcel(parcel: Parcel): OfferModel {
            return OfferModel(parcel)
        }

        override fun newArray(size: Int): Array<OfferModel?> {
            return arrayOfNulls(size)
        }
    }
}
