package com.example.bridgestore.model
import android.os.Parcel
import android.os.Parcelable


data class Product(
    var id:String?="",
    var name: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var imageUrl: String = "",
    var inStock: Boolean = true,
    var sellerId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",

        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(description)
        parcel.writeString(imageUrl)
        parcel.writeByte(if (inStock) 1 else 0)
        parcel.writeString(sellerId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
