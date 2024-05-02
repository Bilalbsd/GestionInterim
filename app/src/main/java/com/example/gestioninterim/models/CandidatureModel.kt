package com.example.gestioninterim.models

import android.os.Parcel
import android.os.Parcelable

data class CandidatureModel(
    val candidatureId: String? = "",
    val candidateId: String? = "",
    val offerId: String? = "",
    val employerId: String? = "",
    val jobTitle: String? = "",
    val jobLocation: String? = "",
    val employerResponse: String? = "",
    val jobSeekerResponse: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(candidatureId)
        dest.writeString(candidateId)
        dest.writeString(offerId)
        dest.writeString(employerId)
        dest.writeString(jobTitle)
        dest.writeString(jobLocation)
        dest.writeString(employerResponse)
        dest.writeString(jobSeekerResponse)
    }

    companion object CREATOR : Parcelable.Creator<CandidatureModel> {
        override fun createFromParcel(parcel: Parcel): CandidatureModel {
            return CandidatureModel(parcel)
        }

        override fun newArray(size: Int): Array<CandidatureModel?> {
            return arrayOfNulls(size)
        }
    }
}
